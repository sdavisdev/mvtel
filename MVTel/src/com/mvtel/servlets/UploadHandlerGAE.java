/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mvtel.servlets;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.mvtel.persistence.PMF;
import com.mvtel.structures.ImageEntity;
import com.mvtel.structures.Phone;
import com.mvtel.util.OutputUtils;
import com.oreilly.servlet.MultipartRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.persistence.Persistence;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Steve
 */
public class UploadHandlerGAE extends HttpServlet 
{
	static Logger logger = Logger.getLogger(UploadHandlerGAE.class.getName());
	
//    private File tempUploadPath;

    @Override
    public void init(ServletConfig config) throws ServletException 
    {
        super.init(config);
        
//        tempUploadPath = new File(AdminServlet.getBasePath(), "tmp");
//        if(!tempUploadPath.exists())
//            tempUploadPath.mkdirs();
        
//        logger.info("Initializing with temp upload path: " + tempUploadPath.getAbsolutePath());
    }
    
    Blob imageFor(String name, HttpServletResponse res) throws IOException 
    {
        // find desired image
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery("select from " + ImageEntity.class.getName() +
            " where name == nameParam " +
            "parameters String nameParam");
        List<ImageEntity> results = (List<ImageEntity>)query.execute(name);
        Blob image = results.iterator().next().getImage();

        // serve the first image
//        res.setContentType("image/jpeg");
        logger.info("Blob Size: " + image.getBytes().length);
        res.getOutputStream().write(image.getBytes());
        
        return image;
    }

    
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {

        boolean authenticated = AdminServlet.checkAuthentication(request);
        
    	if(request.getParameter("blobstore") != null)
    	{
			String uploadType = getUploadType(request.getRequestURI()); 
			
    		BlobstoreService blobStoreService = BlobstoreServiceFactory.getBlobstoreService();
    		Map<String, List<BlobKey>> bMap = blobStoreService.getUploads(request);
    		
    		for(String key : bMap.keySet())
    		{
    			for(BlobKey blobKey : bMap.get(key))
    			{
    				logger.info(uploadType + " Upload,  BlobKey: " + blobKey.getKeyString());
    				response.setHeader("blob-key", blobKey.getKeyString());
    			}
    		}
    		
    	}
    	else if(request.getParameter("get") != null)
    	{
    		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    		String key = request.getParameter("blob-key");
    		logger.info("Got Get Request for BLob Key: " + key);
 
    		blobstoreService.serve(new BlobKey(key), response);

    		return;
    	}
    	
    	try 
    	{
			String uploadType = getUploadType(request.getRequestURI()); 
            
            if("Phone".equalsIgnoreCase(uploadType))
            {
//            	logger.info("Phone Upload");
//                handlePhoneUpload(mr);
            }
            else if("SaleItem".equalsIgnoreCase(uploadType))
            {
//            	logger.info("Sale Item Upload");
//                handleSaleItemUpload(mr);
            }
            else
            {
            	logger.warning("Upload Type Not Supported: " + uploadType);
            }
            
    	} catch(Exception e) {
    		OutputUtils.logError(logger, e, "File Upload Error");
    	}
    }
    
    private String getUploadType(String requestUrl)
    {
        int lastIndex = -1;
        if(requestUrl != null && (lastIndex = requestUrl.lastIndexOf("/")) != -1)
            return requestUrl.substring(lastIndex+1);
        
        return "";
    }
    
    private void handlePhoneUpload(MultipartRequest mr)
    {
        String phoneTitle = "NONE";
        String phoneDescription = "Add Description";
        String phoneCategory = "NONE";
        int numImages = 0;
        
        // print out the request parameters
        boolean isFirstImage = false;
        Enumeration parameterNames = mr.getParameterNames();
        for( int i = 0; parameterNames.hasMoreElements(); i++ ) 
        {
            String parameterName = (String)parameterNames.nextElement();
            if("phoneTitle".equals(parameterName))
                phoneTitle = mr.getParameter(parameterName);
            else if("phoneDescription".equals(parameterName))
                phoneDescription = mr.getParameter(parameterName);
            else if("phoneCategory".equals(parameterName))
                phoneCategory = mr.getParameter(parameterName);
            else if("numImages".equals(parameterName))
                numImages = Integer.parseInt(mr.getParameter(parameterName));
            else if("fileName".equals(parameterName))
            {
                String fileName = mr.getParameter(parameterName);
                if(fileName.startsWith("1."))
                {
                    isFirstImage = true;
                }
            }
        }
        
        if(isFirstImage)
        {
            String phoneDirectory = phoneCategory + "/" + phoneTitle;
            
            Phone phone = new Phone();
            phone.setName(phoneTitle);
            phone.setCategory(phoneCategory);
            phone.setDescription(phoneDescription);
//            phone.setDirectory(phoneDirectory);
//            phone.setImageCount(numImages);
            
//            DBManager.savePhone(phone);
        }

        // move each phone image from the temp directory to the phone directory
        
        File phoneDir = AdminServlet.getPhonePath(phoneCategory, phoneTitle);
        if(!phoneDir.exists())
        {
            System.out.println("Creating new phone directory: " + phoneDir.getAbsolutePath());
            phoneDir.mkdirs();
        }
        
        Enumeration fileNames = mr.getFileNames();
        for(int i = 0; fileNames.hasMoreElements(); i++) 
        {
            String fileName = (String)fileNames.nextElement();
            File uploaded = mr.getFile(fileName);
            File destination = new File(phoneDir, uploaded.getName());
            
            if(uploaded.renameTo(destination))
                System.out.println("Uploaded File: " + destination.getAbsolutePath());
            else
                System.out.println("ERROR Moving File from: " + uploaded.getAbsolutePath() 
                        + " to: " + destination.getAbsolutePath());
        }
        
        if(phoneDir.list().length == numImages)
        {
            System.out.println("Upload and copying of all images for phone " + phoneTitle + " is complete!");
//            ImageManager.generateImages(phoneDir);
        }
    }
    
    private void handleSaleItemUpload(MultipartRequest mr)
    {
        String dirName = mr.getParameter("dirName");
        System.out.println("DIRNAME: " + dirName);

        
        // move each phone image from the temp directory to the phone directory
        File itemDir = AdminServlet.getForSalePath(dirName);
        if(!itemDir.exists())
        {
            System.out.println("Creating new sale item directory: " + itemDir.getAbsolutePath());
            itemDir.mkdirs();
        }
        
        Enumeration fileNames = mr.getFileNames();
        for(int i = 0; fileNames.hasMoreElements(); i++) 
        {
            String fileName = (String)fileNames.nextElement();
            File uploaded = mr.getFile(fileName);
            File destination = new File(itemDir, uploaded.getName());
            
            if(uploaded.renameTo(destination))
                System.out.println("Uploaded File: " + destination.getAbsolutePath());
            else
                System.out.println("ERROR Moving File from: " + uploaded.getAbsolutePath() 
                        + " to: " + destination.getAbsolutePath());
        }
    }
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
