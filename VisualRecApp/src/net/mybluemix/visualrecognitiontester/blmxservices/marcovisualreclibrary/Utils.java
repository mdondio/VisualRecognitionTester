package net.mybluemix.visualrecognitiontester.blmxservices.marcovisualreclibrary;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

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

				System.out.println("[Utils buildCompressedStream()]: error while retrieving " +Long.toUnsignedString(id) + ".jpg. Skip image." );
			continue;
			}
			
			// skip non images
			if(!con.getHeaderField("Content-Type").equals("image/jpeg")){
				
				System.out.println("[Utils buildCompressedStream()]: Content-Type: " + con.getHeaderField("Content-Type"));
				System.out.println("[Utils buildCompressedStream()] con.getResponseCode() = " + con.getResponseCode());
				System.out.println("[Utils buildCompressedStream()]: error while retrieving " + Long.toUnsignedString(id) + ".jpg.");

//				BufferedReader reader = new BufferedReader(
//	                    new InputStreamReader(con.getInputStream()));				
//				
//				String s;
//				while( (s = reader.readLine())!=null)
//					System.out.println(s);
				
				
				continue;

			}
			
// We have a valid image: add as zip entry
			
			System.out.println("[Utils buildCompressedStream()] Adding " +Long.toUnsignedString(id) );
			
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
}
