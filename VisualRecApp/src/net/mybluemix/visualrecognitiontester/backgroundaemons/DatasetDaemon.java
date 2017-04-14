package net.mybluemix.visualrecognitiontester.backgroundaemons;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.FindByIndexOptions;
import com.cloudant.client.api.views.Key;
import com.cloudant.client.api.views.ViewRequest;
import com.cloudant.client.api.views.ViewRequestBuilder;
import com.google.gson.Gson;

import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.DatasetJobInfo;
import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.DatasetJobInfo.TYPE;
import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.Job;
import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.JobQueue;
import net.mybluemix.visualrecognitiontester.blmxservices.CloudantClientMgr;
import net.mybluemix.visualrecognitiontester.blmxservices.ObjectStorage;
import net.mybluemix.visualrecognitiontester.blmxservices.ObjectStorageClientMgr;
import net.mybluemix.visualrecognitiontester.datamodel.Instance;

/**
 * This daemon will run in background, blocking and waiting for a job passed by
 * JobQueue object. A job can be insertion of new dataset or deletion of new
 * dataset
 * 
 * @author Marco Dondio
 *
 */
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


		
		// TODO
		// 1 - normalizza tutte le immagini
		// 2 - costruisci il json cloudant
			// Get id
			//Long firstId = getFirstId(oo);
		// 3 - carica json
		// 4 - carica immagini in oo
		

		// if (fileSize == 0 && (fileName == null || fileName.isEmpty())) {
		// System.out.println("[SubmitDataset parseRequest()] Not an
		// image/jpeg file, skip.");
		// continue; // Ignore part, if not a file.
		// }

		// files.add(info);
		// Files.copy(part.getInputStream(), new File(uploads,
		// info.getId().toString()).toPath());
		
		
		
		// processa tutte le immagini, normalizza
		// part -> bufferedimage
		// normalizza
		// scrivi in oo
		
		// nel mentre costruisci json dataset
		// scrivi in cloudant		
	}


	private void handleDelete(Job<DatasetJobInfo> deleteJob, ObjectStorage oo) {


	}

	private Long getFirstId(ObjectStorage oo) throws IOException {
		
		Long firstId =0L;
		
		
		Database db = CloudantClientMgr.getCloudantDB();

		ViewRequestBuilder builder = db.getViewRequestBuilder("maxID", "maxID");

		ViewRequest<String, Long> result = builder.newRequest(Key.Type.STRING, Long.class).build();
		
		
//		System.out.println(result.getResponse());
	//	System.out.println(result.getSingleValue());
		
		firstId = result.getSingleValue() +1;
		return firstId;
	}
private void normalizeImage(BufferedImage img){
	
	
	// TODO
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
