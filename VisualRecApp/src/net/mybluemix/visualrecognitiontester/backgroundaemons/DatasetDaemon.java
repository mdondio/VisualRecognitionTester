package net.mybluemix.visualrecognitiontester.backgroundaemons;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.FindByIndexOptions;
import com.cloudant.client.api.model.Response;
import com.cloudant.client.api.views.Key;
import com.cloudant.client.api.views.ViewRequest;
import com.cloudant.client.api.views.ViewRequestBuilder;

import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.DatasetJobInfo;
import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.Job;
import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.JobQueue;
import net.mybluemix.visualrecognitiontester.blmxservices.CloudantClientMgr;
import net.mybluemix.visualrecognitiontester.blmxservices.Configs;
import net.mybluemix.visualrecognitiontester.blmxservices.ObjectStorage;
import net.mybluemix.visualrecognitiontester.blmxservices.ObjectStorageClientMgr;
import net.mybluemix.visualrecognitiontester.datamodel.Dataset;
import net.mybluemix.visualrecognitiontester.datamodel.Images;

/**
 * This daemon will run in background, blocking and waiting for a job passed by
 * JobQueue object. A job can be insertion of new dataset or deletion of new
 * dataset
 * 
 * @author Marco Dondio
 *
 */
// TODO punto di grossa attenzione: questo metodo è molto pesante per il
// server.. confido che non venga usato massivamente contemporanemente..
// altrimenti
// dovrei studiare alternative scrivendo in una "area di staging" sul disco

public class DatasetDaemon implements Runnable {

	// Setup context info
	private ServletContext ctx;
	private JobQueue<Job<DatasetJobInfo>> datasetQueue;

	public DatasetDaemon(ServletContext ctx) {

		System.out.println("[DatasetDaemon] Constructor");
		this.ctx = ctx;

	}

	@SuppressWarnings("unchecked")
	public void initialize() {
		datasetQueue = (JobQueue<Job<DatasetJobInfo>>) ctx.getAttribute("datasetQueue");
	}

	public void run() {

		System.out.println("[DatasetDaemon] Run in execution (thread: " + Thread.currentThread().getName() + ")...");
		initialize();

		while (true) {

			// wait for a new job
			Job<DatasetJobInfo> datasetJob = null;
			try {
				datasetJob = datasetQueue.getJob();
			} catch (InterruptedException e) {
			}

			System.out.println("[DatasetDaemon] Received = " + datasetJob + ". Processing...");

			// 1 - Retrieve image from object storage and build zips
			ObjectStorage oo = null;
			try {
				oo = ObjectStorageClientMgr.getObjectStorage();
			} catch (IOException e) {
				// XXX idealmente non è da skippare, ma da riprovare dopo un po'
				System.out.println("[DatasetDaemon] Error: failed to connect to object storage. Skip job.");
				continue;

			}

			switch (datasetJob.getObj().getType()) {
			case INSERT:
				System.out.println("[DatasetDaemon] Insert Job...");
				try {
					handleInsert(datasetJob, oo);
				} catch (IOException e) {
					System.out.println("[DatasetDaemon] handleInsert error. Skip job.");
				}
				break;
			case DELETE:
				System.out.println("[DatasetDaemon] Delete Job...");
				try {
					handleDelete(datasetJob, oo);
				} catch (IOException e) {
					System.out.println("[DatasetDaemon] handleDelete error. Skip job.");
				}
				break;
			default:
				System.out.println("[DatasetDaemon] No Job recognized");
				break;
			}

			System.out.println("[DatasetDaemon] Job completed!");
		}
	}

	private void handleInsert(Job<DatasetJobInfo> insertJob, ObjectStorage oo) throws IOException {


		String datasetId = insertJob.getObj().getDatasetId();

		String label = insertJob.getObj().getLabel();

		// List<BufferedImage> positives = insertJob.getObj().getPositives();
		// List<BufferedImage> negatives = insertJob.getObj().getNegatives();
		File datasetStagingArea = insertJob.getObj().getStagingArea();

		// Retrieve the first Id go be used
		Long firstPosId = getFirstId();

		System.out.println("[DatasetDaemon handleInsert()] firstId: " + firstPosId);

		// Create and insert dataset into cloudant
		Database db = CloudantClientMgr.getCloudantDB();

		if (db.contains(datasetId)) {
			System.out.println("[DatasetDaemon handleInsert()] Id already used, skip job. DatasetId: " + datasetId);
			return;
		}

		insertDataset(datasetId, label, firstPosId, insertJob.getObj().getPosCounter(),
				insertJob.getObj().getNegCounter());

		// Store all images into object storage
		// an error is still ok: images wont be displayed correctly
		storeImages(datasetStagingArea, firstPosId, (firstPosId + insertJob.getObj().getPosCounter()));

		// And finally delete staging area!
		deleteStagingArea(datasetStagingArea);

	}

	private void deleteStagingArea(File datasetStagingArea) {
		
		System.out.println("[DatasetDaemon deleteStagingArea()] Deleting " + datasetStagingArea);

		for (File images : datasetStagingArea.listFiles())
			images.delete();
		datasetStagingArea.delete();
	}

	private void handleDelete(Job<DatasetJobInfo> deleteJob, ObjectStorage oo) throws IOException {

		String datasetId = deleteJob.getObj().getDatasetId();

		// TODO read dataset images from cloudant

		Dataset d = retrieveDataset(datasetId);

		// System.out.println("[DatasetDaemon handleDelete()] Deleting images
		// from object storage...");

		if (d != null)
			deleteImages(d);

		// Delete from cloudant
		System.out.println("[DatasetDaemon handleDelete()] Deleting dataset " + datasetId + " from cloudant");
		deleteDataset(datasetId);
	}

	private Long getFirstId() throws IOException {

		Database db = CloudantClientMgr.getCloudantDB();

		ViewRequestBuilder builder = db.getViewRequestBuilder("maxID", "maxID");

		ViewRequest<String, Long> result = builder.newRequest(Key.Type.STRING, Long.class).build();

//		 System.out.println(result.getResponse());
//		 System.out.println(result.getSingleValue());

		 Long firstId = new Long(1);
		 if(result.getSingleValue()!=null) firstId = result.getSingleValue() + 1;
		 
		return firstId;
	}

	private void insertDataset(String datasetId, String label, Long firstId, int positiveSize, int negativeSize) {

		// First, build dataset
		Dataset d = new Dataset();
		d.setId(datasetId);
		d.setType("dataset");
		// skip subtype
		d.setLabel(label);

		// Build images
		Long curId = firstId;
		List<Long> positives = new ArrayList<Long>();
		List<Long> negatives = new ArrayList<Long>();

		for (int i = 0; i < positiveSize; i++)
			positives.add(curId++);

		for (int i = 0; i < negativeSize; i++)
			negatives.add(curId++);

		Images i = new Images();
		i.setPositives(positives);
		i.setNegatives(negatives);
		d.setImages(i);

		// debug
		// System.out.println(CloudantClientMgr.getGsonBuilder().create().toJson(d));

		// now store in cloudant
		// get db connection
		Database db = CloudantClientMgr.getCloudantDB();

		// Insert dataset
		Response responsePost = db.post(d);
		System.out.println("[DatasetDaemon insertDataset()] Inserted dataset, response: " + responsePost);
	}

	private void storeImages(File datasetStagingArea, Long firstPosId, Long firstNegId) throws IOException {

		// reads all images

		ObjectStorage oo = ObjectStorageClientMgr.getObjectStorage();

		Long curPosId = firstPosId;
		Long curNegId = firstNegId;

		// reads all images

		for (File img : datasetStagingArea.listFiles()) {

			// build image
			// TODO preprocessing, normalization
			BufferedImage image = ImageIO.read(img);

			// build image name
			String imgName = Long.toUnsignedString((img.getName().startsWith("pos_") ? curPosId++ : curNegId++))
					+ ".jpg";

			System.out.println("[DatasetDaemon storeImages()] Storing imgName: " + imgName);
			
			oo.doPut("/" + Configs.OO_DEFAULTCONTAINER, "/" + imgName, image);

		}
	}

	private void normalizeImage(BufferedImage img) {

		// TODO
		// dimensione standard per esempio
	}

	private void deleteImages(Dataset d) throws IOException {
		ObjectStorage oo = ObjectStorageClientMgr.getObjectStorage();

		for (Long imgId : d.getImages().getPositives()) {
			String imgName = Long.toUnsignedString(imgId) + ".jpg";
			System.out.println("[DatasetDaemon deleteImages()] Deleting imgName: " + imgName);
			oo.doDelete("/" + Configs.OO_DEFAULTCONTAINER, "/" + imgName);
		}

		for (Long imgId : d.getImages().getNegatives()) {
			String imgName = Long.toUnsignedString(imgId) + ".jpg";
			System.out.println("[DatasetDaemon deleteImages()] Deleting imgName: " + imgName);
			oo.doDelete("/" + Configs.OO_DEFAULTCONTAINER, "/" + imgName);
		}
	}

	private Dataset retrieveDataset(String datasetId) {

		Database db = CloudantClientMgr.getCloudantDB();

		// Condizione
		String selector = "{\"selector\": {\"_id\" : \"" + datasetId + "\"}}";

		// Limita i campi
		FindByIndexOptions opt = new FindByIndexOptions().fields("_id").fields("label").fields("images");

		// execute query
		List<Dataset> datasets = db.findByIndex(selector, Dataset.class, opt);

		return datasets == null ? null : datasets.get(0);
	}

	// delete dataset from cloudant
	private void deleteDataset(String datasetId) {

		// get db connection
		Database db = CloudantClientMgr.getCloudantDB();

		// Get the instance from db
		Dataset d = db.find(Dataset.class, datasetId);

		Response responseDelete = db.remove(d);

		System.out.println("[DatasetDaemon deleteDataset()] Deleted dataset, response: " + responseDelete);
	}

	// http://stackoverflow.com/questions/25669874/opening-an-image-file-from-java-inputstream
	// private void buildImage(int i, InputStream imageStream){
	//
	// System.out.println("[SubmitDataset buildImage()] start");
	//
	// try (FileOutputStream out = new FileOutputStream(new
	// File(getServletContext().getRealPath("/")+"test_"+i+".jpg"))) {
	// byte[] buffer = new byte[1024];
	// int len;
	// while ((len = imageStream.read(buffer)) != -1) {
	//
	// out.write(buffer, 0, len);
	// System.out.println(buffer);
	//
	// }
	// System.out.println("[SubmitDataset buildImage()] close");
	//
	// out.close();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

}
