package net.mybluemix.visualrecognitiontester.backgroundaemons;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.Part;

import com.cloudant.client.api.Database;
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
//TODO punto di grossa attenzione: questo metodo è molto pesante per il
//server.. confido che non venga usato massivamente contemporanemente.. altrimenti
//dovrei studiare alternative scrivendo in una "area di staging" sul disco

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
				handleDelete(datasetJob, oo);
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
		List<BufferedImage> positives = insertJob.getObj().getPositives();
		List<BufferedImage> negatives = insertJob.getObj().getNegatives();

		// Retrieve the first Id go be used
		Long firstId = getFirstId();

		// Create and insert dataset into cloudant
		insertDataset(datasetId, label, firstId, positives.size(), negatives.size());

		// Store all images into object storage
		// an error is still ok: images wont be displayed correctly
		storeImages(positives, firstId);
		storeImages(negatives, firstId + positives.size());


		// --------------------------

	}

	private void handleDelete(Job<DatasetJobInfo> deleteJob, ObjectStorage oo) {

		
		String datasetId = deleteJob.getObj().getDatasetId();
		
		// TODO read dataset images from cloudant
		

		
		// delete images from object storage
//		deleteImages();
		
		
		// at the end, delete from cloudant datasetId

	}

	private Long getFirstId() throws IOException {

		Database db = CloudantClientMgr.getCloudantDB();

		ViewRequestBuilder builder = db.getViewRequestBuilder("maxID", "maxID");

		ViewRequest<String, Long> result = builder.newRequest(Key.Type.STRING, Long.class).build();

		// System.out.println(result.getResponse());
		// System.out.println(result.getSingleValue());

		Long firstId = result.getSingleValue() + 1;
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
		 System.out.println("[DatasetDaemon] Inserted dataset, response: " + responsePost);
	}

	private void storeImages(List<BufferedImage> images, Long firstImageId) throws IOException {

		Long curId = firstImageId;
		ObjectStorage oo = ObjectStorageClientMgr.getObjectStorage();

		
		for(BufferedImage image : images){
			
			// XXX da generalizzare!
			// XXX test veloce: se salvo un png come jpg cosa succede?
			String imgName = Long.toUnsignedString(curId++) + ".jpg";

			// Normalize
			// TODO
			
			// Store this image in object storage
			
			System.out.println("[DatasetDaemon storeImages()] Storing imgName: " + imgName);
			oo.doPut("/" + Configs.OO_DEFAULTCONTAINER, "/" +imgName, image);
		}
	}


	private void normalizeImage(BufferedImage img) {

		// TODO
	}
	
	
	private void deleteImages(){
		
//		oo.doDelete("/" + Configs.OO_DEFAULTCONTAINER, "/" +imgName);

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
