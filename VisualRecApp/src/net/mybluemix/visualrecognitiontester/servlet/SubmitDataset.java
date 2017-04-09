package net.mybluemix.visualrecognitiontester.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Deque;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;



/**
 * This endpoint is used to submit a dataset, analyze it and gives a feedback to
 * user. Actual job will be done asynchronously in background
 * 
 * @author Marco Dondio
 */
// https://css-tricks.com/snippets/html/multiple-file-input/
// http://docs.oracle.com/javaee/6/tutorial/doc/glraq.html

@WebServlet("/SubmitDataset")
@MultipartConfig
public class SubmitDataset extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SubmitDataset() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		return;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// TODO
		// 0 - recupera tutte le immagini posotive e negative
		parseRequest(request);
		
		// capire cosa conviene costruire... images?
		// accetto image/jpeg only?
		
		
		// 1 - processa tutte le img positive e negative
		// 2 - controllo di vario tipo, filtro
		// 3 - normalizza
		// 4 - produce gli zip

		// 5 - add to queue!
		// Now we add this classifier to the zombie queue...
//		ServletContext ctx = getServletContext();
//		@SuppressWarnings("unchecked")
//		JobQueue<Job<Classifier>> datasetQueue = (JobQueue<Job<Classifier>>) ctx.getAttribute("datasetQueue");
//		datasetQueue.addJob(new Job<Classifier>(classifierJson));

	}
	
	// https://ursaj.com/upload-files-in-java-with-servlet-api
	private void parseRequest(HttpServletRequest req) throws IOException, ServletException{
	//	Deque<FileInfo> files = new LinkedList<>();
//		int i = 0;
	    for (Part part : req.getParts()) {
	        long fileSize = part.getSize();
	        String name = part.getName();
	        	String contentDisposition = part.getHeader("content-disposition");
	        String contentType = part.getContentType();
	        
        	System.out.println("[SubmitDataset parseRequest()] Received: " + name + " - (" + contentDisposition + ") - " + contentType + " - " + fileSize);

//        	buildImage(i++, part.getInputStream());

        	// punto aperto: conviene usare un byte[] brutale e salvarlo nel db
        	// oppure trasformarlo in image
        	// http://stackoverflow.com/questions/3211156/how-to-convert-image-to-byte-array-in-java
        	
        	
        	// preprocessing
        	// poi salva in oo
	        
	        
//	        if (fileSize == 0 && (fileName == null || fileName.isEmpty())) {
//	        	System.out.println("[SubmitDataset parseRequest()] Not an image/jpeg file, skip.");
//	            continue; // Ignore part, if not a file.
//	        }

//	        files.add(info);
//	        Files.copy(part.getInputStream(), new File(uploads, info.getId().toString()).toPath());
	    }

//	    req.getSession().setAttribute("uploadedFiles", files);
//	    resp.sendRedirect(applicationUrl + "/upload");

		
		
	}
	
	// http://stackoverflow.com/questions/25669874/opening-an-image-file-from-java-inputstream
//	private void buildImage(int i, InputStream imageStream){
//		
//    	System.out.println("[SubmitDataset buildImage()] start");
//
//		    try (FileOutputStream out = new FileOutputStream(new File(getServletContext().getRealPath("/")+"test_"+i+".jpg"))) {
//		        byte[] buffer = new byte[1024]; 
//		        int len; 
//		        while ((len = imageStream.read(buffer)) != -1) { 
//
//		            out.write(buffer, 0, len); 
//		        	System.out.println(buffer);
//		            
//		        }
//	        	System.out.println("[SubmitDataset buildImage()] close");
//		        
//		        out.close();
//		    } catch (Exception e) {
//		    	e.printStackTrace();
//		    }
//	}

}
