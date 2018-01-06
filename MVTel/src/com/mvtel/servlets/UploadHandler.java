/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mvtel.servlets;

import com.mvtel.db.DBManager;
import com.mvtel.image.ImageManager;
import com.mvtel.structures.Phone;
import com.mvtel.structures.Result;
import com.mvtel.structures.SaleItem;
import com.oreilly.servlet.MultipartRequest;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Steve
 */
//@WebServlet(name = "UploadHandler", urlPatterns = {"/Admin/Upload/*"}, loadOnStartup=3)
public class UploadHandler extends HttpServlet 
{
    private File tempUploadPath;

    @Override
    public void init(ServletConfig config) throws ServletException 
    {
        super.init(config);
        
        tempUploadPath = new File(AdminServlet.getBasePath(), "tmp");
        if(!tempUploadPath.exists())
            tempUploadPath.mkdirs();
        
        System.out.println("UploadHandler[NON-GAE]: Initializing with temp upload path: " + tempUploadPath.getAbsolutePath());
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
        int maxPostSizeBytes = 10 * 1024 * 1024;
        
        // initialize the multipart request which will handle file retrieval
        String contentType = request.getContentType();
        if( contentType == null || !contentType.startsWith("multipart/form-data")) 
        {
             throw new RuntimeException( "content type must be 'multipart/form-data'" );
        }
        
        MultipartRequest mr = new MultipartRequest(request, tempUploadPath.getAbsolutePath(), maxPostSizeBytes);
        
        String uploadType = getUploadType(request.getRequestURI()); 
        
        if("Phone".equalsIgnoreCase(uploadType))
        {
            handlePhoneUpload(mr);
        }
        else if("SaleItem".equalsIgnoreCase(uploadType))
        {
            handleSaleItemUpload(mr);
        }
        else
        {
            System.out.println("WARNING: Upload Type Not Supported: " + uploadType);
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
/*            
            phone.setDirectory(phoneDirectory);
            phone.setImageCount(numImages);
*/            
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
