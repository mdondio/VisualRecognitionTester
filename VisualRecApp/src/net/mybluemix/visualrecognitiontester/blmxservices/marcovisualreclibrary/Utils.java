package net.mybluemix.visualrecognitiontester.blmxservices.marcovisualreclibrary;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.mybluemix.visualrecognitiontester.blmxservices.ObjectStorage;

/**
 * This class contains useful methods.
 * @author marcodondio
 *
 */
public class Utils {

	public static final int CLASSIFYMAXIMAGES = 20; // max number of images per
													// call

	public static final double WATSONMINSCORE = 0.0; // minimum score for watson

	
	
	
/**
 *  This method builds a zip file by retrieving all images from Object Storage
 * @param oo - the object storage instance
 * @param containerName - the container containing images
 * @param ids - list of imageIDs
 * @return
 * @throws IOException
 */
	public static byte[] buildCompressedStream(ObjectStorage oo, String containerName, List<Long> ids) throws IOException
	{
		byte data[] = new byte[2048];
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(bos);

		for (Long id  : ids) {
				
			HttpURLConnection con;
			try {
				con = oo.doGet("/"+containerName+"", "/"+Long.toUnsignedString(id)+".jpg");
			} catch (IOException e) {

				System.out.println("buildCompressedStream: error while retrieving " +Long.toUnsignedString(id) + ".jpg. Skip image." );
			continue;
			}
			
			// skip non images
			if(!con.getHeaderField("Content-Type").equals("image/jpeg"))
				continue;
			
// We have a valid image: add as zip entry
			
			BufferedInputStream entryStream = new BufferedInputStream(con.getInputStream(), 2048);
			ZipEntry entry = new ZipEntry(Long.toUnsignedString(id) + ".jpg");
			zos.putNextEntry(entry);
			int count;
			while ((count = entryStream.read(data, 0, 2048)) > 0 ) {
				zos.write(data, 0, count);
			}
			entryStream.close();
			zos.closeEntry();
		}

		zos.close();
		return bos.toByteArray();
	}
	
	
	// Questo costruisce lo lista zip per il test
	public static List<byte[]> buildCompressedStreamBlocks(ObjectStorage oo, String containerName, List<Long> ids) throws IOException{

	// max images per call

		// List of zips, each one containing CLASSIFYMAXIMAGES images
		List<byte[]> results = new LinkedList<byte[]>();

		// Number of blocks we split our sets
		int numBlocks = (int) Math.ceil((double) ids.size() / CLASSIFYMAXIMAGES);
		
		// For every block...
		for (int i = 0; i < numBlocks; i++) {

			// Extract sublist
			List<Long> subIds = ids.subList(i * CLASSIFYMAXIMAGES,
					Math.min(i * CLASSIFYMAXIMAGES + CLASSIFYMAXIMAGES, ids.size()));

			System.out.println("da " + i * CLASSIFYMAXIMAGES + " a "
					+ Math.min(i * CLASSIFYMAXIMAGES + CLASSIFYMAXIMAGES, ids.size()));


			// Add to zip files
			results.add(buildCompressedStream(oo, containerName, subIds));
		}

		return results;
	}
	
	
	
	
	

	/**
	 * The area under the curve (AUC). When using normalized units, the area under
	 * the curve is equal to the probability that a classifier will rank a
	 * randomly chosen positive instance higher than a randomly chosen negative
	 * one (assuming 'positive' ranks higher than 'negative').
	 * <p>
	 * In statistics, a receiver operating characteristic (ROC), or ROC curve,
	 * is a graphical plot that illustrates the performance of a binary classifier
	 * system as its discrimination threshold is varied. The curve is created by
	 * plotting the true positive rate (TPR) against the false positive rate (FPR)
	 * at various threshold settings.
	 * <p>
	 * AUC is quite noisy as a classification measure and has some other
	 * significant problems in model comparison.
	 * <p>
	 * We calculate AUC based on Mann-Whitney U test
	 * (https://en.wikipedia.org/wiki/Mann-Whitney_U_test).
	 *
	 * @author Haifeng Li
	 */



	    /**
	     * Caulculate AUC for binary classifier.
	     * @param truth The sample labels
	     * @param probability The posterior probability of positive class.
	     * @return AUC
	     */
	    public static double measure(int[] truth, double[] probability) {
	        if (truth.length != probability.length) {
	            throw new IllegalArgumentException(String.format("The vector sizes don't match: %d != %d.", truth.length, probability.length));
	        }

	        // for large sample size, overflow may happen for pos * neg.
	        // switch to double to prevent it.
	        double pos = 0;
	        double neg = 0;

	        for (int i = 0; i < truth.length; i++) {
	            if (truth[i] == 0) {
	                neg++;
	            } else if (truth[i] == 1) {
	                pos++;
	            } else {
	                throw new IllegalArgumentException("AUC is only for binary classification. Invalid label: " + truth[i]);
	            }
	        }

	        int[] label = truth.clone();
	        double[] prediction = probability.clone();

	        // TODO
	        double todo_farequisotto;
	        
	        double[] rank = new double[label.length];
	        for (int i = 0; i < prediction.length; i++) {
	            if (i == prediction.length - 1 || prediction[i] != prediction[i+1]) {
	                rank[i] = i + 1;
	            } else {
	                int j = i + 1;
	                for (; j < prediction.length && prediction[j] == prediction[i]; j++);
	                double r = (i + 1 + j) / 2.0;
	                for (int k = i; k < j; k++) rank[k] = r;
	                i = j - 1;
	            }
	        }

	        double auc = 0.0;
	        for (int i = 0; i < label.length; i++) {
	            if (label[i] == 1)
	                auc += rank[i];
	        }

	        auc = (auc - (pos * (pos+1) / 2.0)) / (pos * neg);
	        return auc;
	    }
	
}
