package com.mvtel.servlets;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.mvtel.db.DBManagerFactory;
import com.mvtel.db.IDBManager;
import com.mvtel.image.ImageManager;
import com.mvtel.structures.Article;
import com.mvtel.structures.Phone;
import com.mvtel.structures.Result;
import com.mvtel.structures.SaleItem;
import com.mvtel.structures.WebsiteLink;
import com.mvtel.util.OutputUtils;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
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
//@WebServlet(name = "AdminServlet", urlPatterns = {"/Admin/*"}, loadOnStartup = 1)
public class AdminServlet extends HttpServlet 
{
	static Logger logger = Logger.getLogger(AdminServlet.class.getName());
//    public static final String SERVLET_URI_PREFIX = "/MVTel/Admin";
    public static File basePath;
    
    private static IDBManager dbManager = DBManagerFactory.getDBManager();

    @Override
    public void init(ServletConfig config) throws ServletException 
    {
        super.init(config);
        
        String path = config.getServletContext().getRealPath("");
        basePath = new File(path);
        
        logger.info("AdminServlet: Initializing with base path: " + basePath.getAbsolutePath());
    }
    
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        // Log debug info if "debug" parameter was specified
        boolean debug = request.getParameter("debug") != null;
        if(debug)
        {
            logger.info("\nRequest Debug:"
                            +  "\n--------------------------------------------"
                            +  "\nRequest: " + request
                            +  "\nSession: " + request.getSession()
                            +  "\n--------------------------------------------");
        }
        
        String action = request.getParameter("action");
        if(action == null)
        {
            logger.info("AdminServlet: Action is NULL!");
            
            RequestDispatcher view = request.getRequestDispatcher("/admin/index.jsp");
            view.forward(request, response);
            return;
        }
        
        try {
            if("auth".equals(action))
            {
                String username = (String)request.getParameter("username");
                String password = (String)request.getParameter("password");
                
                logger.info("Authenticating: " + username);
                logger.severe("\n\n########## Auto Authentication Enabled ##########");
                
                
//                request.getSession().setAttribute("authenticated", true);
//                request.getSession().setAttribute("authenticated_user", username);

//                RequestDispatcher view = request.getRequestDispatcher("/admin/index.jsp");
                RequestDispatcher view = request.getRequestDispatcher("/index.jsp");
                view.forward(request, response);
            }
            else if("logout".equals(action))
            {
            	logger.info("Logging out: " + request.getSession().getAttribute("authenticated_user"));
                    
                request.getSession().setAttribute("authenticated", false);
                request.getSession().removeAttribute("authenticated_user");
                
                RequestDispatcher view = request.getRequestDispatcher("/admin/index.jsp");
                view.forward(request, response);
            }
            else if(request.getSession().getAttribute("authenticated") != null && !(Boolean)request.getSession().getAttribute("authenticated"))
            {
                // not authenticated and not logging in or out: present login page before someone
                // who is not authorized attempts to modify something on the server
            	logger.info("User not authenticated. Redirecting to login page.");
                    
                request.getSession().setAttribute("authenticated", false);
                request.getSession().removeAttribute("authenticated_user");
            
                RequestDispatcher view = request.getRequestDispatcher("/admin/index.jsp");
                view.forward(request, response);
                
            }
            else if("scan".equals(action))
            {
                ArrayList<Phone> phones = scanPhones(out);
                logger.info("Phones Added: " + phones.size());
            }
            
            else if("modifyPhone".equals(action))
            {
                Long id = Long.parseLong(request.getParameter("phoneId"));
                String name = request.getParameter("phoneName");
                String description = request.getParameter("phoneDesc");
                String category = request.getParameter("phoneCategory");

                // only use the id if not -1
                // -1 is used when uploading images remotely 
                if(id == -1){
                	id = null;
                }
                
                Phone p = new Phone(id, name, description, category);
                
                if(request.getParameter("phoneImageCount") != null)
                {
                	ImagesService imagesService = ImagesServiceFactory.getImagesService();
            		
	                int imageCount = Integer.parseInt(request.getParameter("phoneImageCount"));
	                for(int i=0; i < imageCount; i++)
	                {
	                	String blobKey = request.getParameter("phoneImage." + i);
	                	String url = imagesService.getServingUrl(new BlobKey(blobKey));
	                	p.getImages().add(blobKey);
	                	p.getImageUrls().add(url);
	                }
	                
	                logger.info("Array Length: " + imageCount);
                }
                

                // PRE GAE:
                //Phone p = new Phone(id, name, description, category, directory, imgCount);
                
                
                logger.info("User: " + request.getSession().getAttribute("authenticated_user")
                        + " is modifying Phone");
                
                Result result = dbManager.savePhone(p);
                out.print(OutputUtils.toJSON(result));
                
            }
            else if("deletePhone".equals(action))
            {
                long id = Long.parseLong(request.getParameter("phoneId"));
                
                try
                {
                    Phone phone = dbManager.getPhone(id);

                
                    logger.info("User: " + request.getSession().getAttribute("authenticated_user")
                        + " is deleting Phone(id: " + id + "): " + phone);
                    Result result = dbManager.deletePhone(id);
                    if(result.isSuccess())
                    {
/*                    	
                        File phoneDir = getPhonePath(phone.getDirectory());
                        
                        if(phoneDir.exists())
                        {
                            for(File file : phoneDir.listFiles())
                            {
                                if(!file.delete())
                                    System.out.println("Error deleting file: " + file.getAbsolutePath());
                            }
                            if(!phoneDir.delete())
                                System.out.println("Error deleting phone directory: " + phoneDir.getAbsolutePath());
                        }
                        else
                        {
                            System.out.println("Error: Directory does not exist: " + phoneDir);
                        }
*/                        
                    }
                    out.print(OutputUtils.toJSON(result));
                } catch(Exception e) {
                    e.printStackTrace();;
                    out.print(OutputUtils.toJSON(new Result(false, "Error: " + e.getMessage())));
                }
            }
            else if("watermarkPhone".equals(action))
            {
                try
                {
                    Phone phone;
                    String phoneId = request.getParameter("phoneId");
                    if(phoneId != null && ! "".equals(phoneId))
                    {
                        long id = Long.parseLong(phoneId);
                        phone = dbManager.getPhone(id);
                    }
                    else
                    {
                        String phoneCategory = request.getParameter("phoneCategory");
                        String phoneTitle = request.getParameter("phoneTitle");
                        phone = dbManager.getPhone(phoneCategory, phoneTitle);
                    }
                        
                    logger.info("User: " + request.getSession().getAttribute("authenticated_user")
                        + " is watermarking Phone: " + phone);
/*                    
                    int generatedImages = ImageManager.generateImages(getPhonePath(phone.getDirectory()));
                    if(generatedImages != phone.getImageCount())
                    {
                        System.out.println("Phone Image Count Differs. Setting Num Images to " + generatedImages);
                        phone.setImageCount(generatedImages);
                        dbManager.savePhone(phone);
                    }
                    
                    Result result = new Result(true, "Watermarked " + generatedImages + " images.");
                    out.print(OutputUtils.toJSON(result));
*/                    
                } catch(Exception e) {
                    e.printStackTrace();;
                    out.print(OutputUtils.toJSON(new Result(false, "Error: " + e.getMessage())));
                }
            }
            else if("checkPhoneExists".equals(action))
            {
                
                String name = request.getParameter("phoneName");
                String category = request.getParameter("phoneCategory");
                
                Result result;
                if(name == null || category == null)
                {
                    result = new Result(false, "Need to specify category and name!");
                }
                else
                {
                    try {
                    	logger.info("Looking up phone: '" + category + "' '" + name + "'");
                        Phone phone = dbManager.getPhone(category, name);
                        result = new Result(true, "Phone: " + category + "/" + name + " exists."); 
                    } catch (Exception e) {
                        result = new Result(false, "Error looking up phone: " + e.getMessage());
                    }
                }
                
                out.print(OutputUtils.toJSON(result));
            }
                    
            /////////////////////////
            // Sale Item Functions //
            /////////////////////////
            else if("saveSaleItem".equals(action))
            {
                SaleItem item = new SaleItem();
                item.setTitle(request.getParameter("saleTitle"));
                item.setPrice(request.getParameter("salePrice"));
                item.setDescription(request.getParameter("saleDescription"));
                
                // Get id, set to null if -1 (new item)
                Long itemId = -1L;
                if(request.getParameter("saleId") != null)
                {
                    try {
                        itemId = Long.parseLong(request.getParameter("saleId"));
                    } catch(Exception e) {e.printStackTrace();}
                }
                if(itemId.equals(-1L))
                	itemId = null;
                item.setId(itemId);

                if(request.getParameter("saleItemImageCount") != null)
                {
                	ImagesService imagesService = ImagesServiceFactory.getImagesService();
            		
	                int imageCount = Integer.parseInt(request.getParameter("saleItemImageCount"));
	                for(int i=0; i < imageCount; i++)
	                {
	                	String blobKey = request.getParameter("saleItemImage." + i);
	                	String url = imagesService.getServingUrl(new BlobKey(blobKey));
	                	item.getImages().add(blobKey);
	                	item.getImageUrls().add(url);
	                }
	                
	                logger.info("Array Length: " + imageCount);
                }
                
                logger.info(item.toString());
            
                Result result = dbManager.saveSaleItem(item);
                logger.info("Save Result: " + result);
            
                // update the sale items attribute in the application scope
                try {
                    getServletContext().setAttribute(PhoneServlet.ATTR_KEY_ITEMS_FOR_SALE, dbManager.getSaleItems());
                } catch(Exception e) {
                    OutputUtils.logError(logger, e, "ERROR: Could not lookup Sale Items from the DB on save.");
                }
                    
                out.print(OutputUtils.toJSON(result));
            }
            else if("deleteSaleItem".equals(action))
            {
                try {
                    long id = Long.parseLong(request.getParameter("saleItemId"));
                    
                    logger.info("Deleting Sale Item: " + id);
                    
                    Result result = dbManager.deleteSaleItem(id);
                    if(result.isSuccess())
                    {
                        File path = getForSalePath(""+id);
                        if(path.exists())
                        {
                            for(File file : path.listFiles())
                            {
                                if(!file.delete())
                                	logger.info("ERROR Deleting for sale image: " + file.getAbsolutePath());
                            }
                            if(!path.delete())
                            	logger.info("ERROR Deleting for sale path: " + path.getAbsolutePath());
                        }
                    }
                    
                    logger.info("Deletion Result: " + result);
                    
                    // update the sale items attribute in the application scope
                    List<SaleItem> saleItems = new ArrayList<SaleItem>();
                    try {
                    	saleItems = dbManager.getSaleItems();
                    } catch(Exception e) {
                        OutputUtils.logError(logger, e, "ERROR: Could not lookup Sale Items from the DB on deletion.");
                    } finally {
                        getServletContext().setAttribute(PhoneServlet.ATTR_KEY_ITEMS_FOR_SALE, saleItems);
                    }
                    
                    out.print(OutputUtils.toJSON(result));
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
            else if("getSaleItems".equals(action))
            {
                try {
                    List<SaleItem> items = dbManager.getSaleItems();
                    for(SaleItem item : items)
                        System.out.println("Got Sale Item: " + item);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            else if("getSaleItem".equals(action))
            {
                try {
                    long id = Long.parseLong(request.getParameter("saleItemId"));
                    
                    SaleItem item = dbManager.getSaleItem(id);
                    
                    System.out.println("Got Sale Item: " + item);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            else if("watermarkSaleItem".equals(action))
            {
                try
                {
                    String itemId = request.getParameter("saleItemId");
                    if(itemId != null && ! "".equals(itemId))
                    {
                        logger.info("User: " + request.getSession().getAttribute("authenticated_user")
                            + " is watermarking sale item: " + itemId);

                        int generatedImages = ImageManager.generateImages(getForSalePath(itemId));
                        Result result = new Result(true, "Watermarked " + generatedImages + " images.");
                        out.print(OutputUtils.toJSON(result));
                    }
                    else
                    {
                        logger.info("Cannot watermark sale item with id: '" + itemId + "'");
                    }
                } catch(Exception e) {
                    e.printStackTrace();;
                    out.print(OutputUtils.toJSON(new Result(false, "Error: " + e.getMessage())));
                }
            }
                    
            /////////////////////////
            // Site Link Functions //
            /////////////////////////
            else if("saveLink".equals(action))
            {
                WebsiteLink link = new WebsiteLink();
                link.setName(request.getParameter("linkTitle"));
                link.setDescription(request.getParameter("linkDescription"));
                
                String linkUrl = request.getParameter("linkUrl");
                if(!linkUrl.startsWith("http://"))
                	linkUrl = "http://" + linkUrl;
                link.setUrl(linkUrl);
                
                // Get id, set to null if -1 (new item)
                Long linkId = -1L;
                if(request.getParameter("linkId") != null)
                {
                    try {
                    	linkId = Long.parseLong(request.getParameter("linkId"));
                    } catch(Exception e) {e.printStackTrace();}
                }
                if(linkId.floatValue() <= 0L)
                	linkId = null;
                link.setId(linkId);
                
                // sort order
                try {
                    link.setSortOrder(Integer.parseInt(request.getParameter("linkOrder")));
                } catch(Exception e) {}
                

                logger.info("Saving link: " + link);
            
                Result result = dbManager.saveLink(link);
                logger.info("Save Result: " + result);
            
                // update the sale items attribute in the application scope
                try {
                    getServletContext().setAttribute(PhoneServlet.ATTR_KEY_WEBSITE_LINKS, dbManager.getLinks());
                } catch(Exception e) {
                    OutputUtils.logError(logger, e, "ERROR: Could not lookup website links from the DB on link save.");
                }
                    
                out.print(OutputUtils.toJSON(result));
            }
            else if("deleteLink".equals(action))
            {
                try
                {
                    long id = Long.parseLong(request.getParameter("linkId"));
                    Result result = dbManager.deleteLink(id);
            
                    // update the sale items attribute in the application scope
                    List<WebsiteLink> links = new ArrayList<WebsiteLink>();
                    try {
                    	links = dbManager.getLinks();
                    } catch(Exception e) {
                        OutputUtils.logError(logger, e, "ERROR: Could not lookup website links from the DB on delete.");
                    } finally {
                        getServletContext().setAttribute(PhoneServlet.ATTR_KEY_WEBSITE_LINKS, links);
                    }
                    
                    // output delete result
                    out.print(OutputUtils.toJSON(result));
                } catch(Exception e) {
                    OutputUtils.logError(logger, e, "ERROR: Could not delete website link from the DB.");
                    
                    Result result = new Result(false, "Could not delete link: " + e.getMessage());
                    out.print(OutputUtils.toJSON(result));
                }
            }
                    
            /////////////////////////
            //  Article Functions  //
            /////////////////////////
            else if("saveArticle".equals(action))
            {
                Article article = new Article();
                article.setName(request.getParameter("articleTitle"));
                article.setPublishDate(request.getParameter("articleDate"));
                article.setContent(request.getParameter("articleContent"));
                try {
                	Long articleId = Long.parseLong(request.getParameter("articleId"));
                	if(articleId <= 0)
                		articleId = null;
                	
                    article.setId(articleId);
                } catch(Exception e) {}
                try {
                    article.setSortOrder(Integer.parseInt(request.getParameter("sortOrder")));
                } catch(Exception e) {}
                

                logger.info("Saving article: " + article);
            
                Result result = dbManager.saveArticle(article);
                logger.info("Save Result: " + result);
            
                // update the sale items attribute in the application scope
                List<Article> articles = new ArrayList<Article>();
                try {
                	articles = dbManager.getArticles();
                } catch(Exception e) {
                    OutputUtils.logError(logger, e, "ERROR: Could not lookup articles from the DB on article save.");
                } finally {
                    getServletContext().setAttribute(PhoneServlet.ATTR_KEY_ARTICLES, articles);
                }
                    
                out.print(OutputUtils.toJSON(result));
            }
            else if("deleteArticle".equals(action))
            {
                try
                {
                    long id = Long.parseLong(request.getParameter("articleId"));
                    Result result = dbManager.deleteArticle(id);
            
                    // update the articles attribute in the application scope
                    List<Article> articles = new ArrayList<Article>();
                    try {
                    	articles = dbManager.getArticles();
                    } catch(Exception e) {
                        OutputUtils.logError(logger, e, "ERROR: Could not lookup articles from the DB on delete.");
                    } finally {
                        getServletContext().setAttribute(PhoneServlet.ATTR_KEY_ARTICLES, articles);
                    }
                    
                    // output delete result
                    out.print(OutputUtils.toJSON(result));
                } catch(Exception e) {
                	OutputUtils.logError(logger, e, "ERROR: Could not delete article from the DB.");
                    
                    Result result = new Result(false, "Could not delete article: " + e.getMessage());
                    out.print(OutputUtils.toJSON(result));
                }
            }
            
            
        } finally {            
//            out.close();
        }
    }
    
    private ArrayList<Phone> scanPhones(PrintWriter out)
    {
        ArrayList<Phone> phones = new ArrayList<Phone>();
/*        
        File phonesRoot = new File(this.getServletContext().getRealPath("phones"));
        System.out.println("Scanning phones Directory: " + phonesRoot);
        
        out.println("<html><body>");
        out.println("<h1>" + this.getServletContext().getRealPath("phones") + "</h1>");

        for(File category : phonesRoot.listFiles())
        {
            String categoryName = category.getName();
            out.println("<li> " + categoryName + "</li>");
            out.println("<ul>");
            for(File item : category.listFiles())
            {
                String phoneName = item.getName();
                String phoneDirectory = categoryName + "/" + phoneName + "/";

                // Check that we don't already have the phone before creating it
                Phone phone = null;
                
                try {
                    phone = dbManager.getPhone(categoryName, phoneName);
                    System.out.println("Phone " + phoneDirectory + " already exists. Not going to do anything.");
                
                    out.println("<li>" + phoneName + " (Already Exists)</li>");
                    continue;
                } catch(DatabaseException dbe) {
                    if(!dbe.getMessage().contains("does not exist."))
                    {
                        System.out.println("DB Exception while creating phone: " + phoneDirectory);
                        dbe.printStackTrace();

                        out.println("<li style='color: red'>" + phoneName + ": DB Exception:" + dbe.getMessage() + "</li>");
                        // 
                        continue;
                    }
                    else
                        ; // phone does not exist, create it now
                }
                
                out.println("<li>" + phoneName + "</li>");
                
                phone = new Phone();
                phone.setCategory(categoryName);
                phone.setName(phoneName);
                phone.setDescription("No Description");
                
                phone.setDirectory(phoneDirectory);
                phone.setImageCount(ImageManager.generateImages(item));

                // Save it to the database
                dbManager.savePhone(phone);
                System.out.println("Adding Phone: " + phone.getName());
                    
                phones.add(phone);
                
                if(!item.isDirectory() || item.listFiles() == null)
                {
                    out.println("<li style='color: red'>Directory for " + phoneName + " is invalid</li>");
                    System.out.println("WARNING: Directory for " + phoneName + " is null. Ignorig pictures!");
                }
                
                out.println("<ul>");
                for(File img : item.listFiles())
                {
                    out.println("<li>" + img.getName() + "</li>");
                }
                out.println("</ul>");
            }
            out.println("</ul>");
        }
        out.println("</body></html>");
*/
        return phones;
        
    }

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
    }

    public static File getBasePath() 
    {
        return basePath;
    }
    
    public static File getPhonePath()
    {
        return new File(basePath, "phones");
    }
    
    public static File getPhonePath(String category, String phone)
    {
        return new File(getPhonePath(), File.separator + category + File.separator + phone);
    }
    
    public static File getPhonePath(String categoryAndPhone)
    {
        return new File(getPhonePath(), File.separator + categoryAndPhone);
    }
    
    public static File getForSalePath()
    {
        return new File(basePath, "for_sale");
    }
    
    public static File getForSalePath(String dirName)
    {
        return new File(getForSalePath(), dirName);
    }
    
    public static boolean checkAuthentication(HttpServletRequest request)
    {

		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		
		boolean authenticated = request.getSession().getAttribute("authenticated") != null 
								&&(Boolean)request.getSession().getAttribute("authenticated");
		
		if(user != null && userService.isUserAdmin())
		{
			String userEmailAddress = user.getEmail();
			String userNickName = user.getNickname();
            
			// User just authenticated, print message
            if(!authenticated)
            {
	            logger.info("User: '" + userEmailAddress + "' (" + userNickName + ") has logged in.");

	            request.getSession().setAttribute("authenticated", true);
	            if(userNickName == null || "".equals(userNickName))
	            	request.getSession().setAttribute("authenticated_user", userEmailAddress);
	            else
	            	request.getSession().setAttribute("authenticated_user", userNickName);
	            
	            authenticated = true;
            }
		}
		else
		{
			if(authenticated)
			{
				logger.info("User: '" + request.getSession().getAttribute("authenticated_user") + "' is no longer authenticated.");
	            request.getSession().setAttribute("authenticated", false);
	            request.getSession().setAttribute("authenticated_user", "");
	            
	            authenticated = false;
			}
		}
		
		return authenticated;
    }
}
