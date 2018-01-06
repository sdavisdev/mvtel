/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mvtel.servlets;

import com.mvtel.db.DBManagerFactory;
import com.mvtel.db.IDBManager;
import com.mvtel.structures.Article;
import com.mvtel.structures.Phone;
import com.mvtel.structures.SaleItem;
import com.mvtel.structures.WebsiteLink;
import com.mvtel.util.OutputUtils;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Steve
 */
//@WebServlet(name = "PhoneServlet", urlPatterns = {"/items/*"}, loadOnStartup = 2)
public class PhoneServlet extends HttpServlet {

	static Logger logger = Logger.getLogger(PhoneServlet.class.getName());

	private static IDBManager dbManager = DBManagerFactory.getDBManager();
	
    public static final String ATTR_ERROR_MESSAGE = "ErrorMessage";
    public static final String ATTR_CATEGORY_LISTING = "category_items";
    public static final String ATTR_CATEGORY_LISTING_BEGIN = "category_items_begin";
    public static final String ATTR_CATEGORY_LISTING_END = "category_items_end";
    public static final String ATTR_CATEGORY_LISTING_TOTAL = "category_items_total";
    public static final String ATTR_CATEGORY_LISTING_CURRENT_PAGE = "current_page";
    public static final String ATTR_CATEGORY_LISTING_TOTAL_PAGES = "total_pages";
    public static final String ATTR_CATEGORY_NAME = "category_name";
    public static final String ATTR_SESSION_TRACKED = "session_tracked";
    public static final String ATTR_SESSION_HIT_COUNT = "session_hit_count";
    public static final String ATTR_KEY_ITEMS_FOR_SALE = "itemsForSale";
    public static final String ATTR_KEY_WEBSITE_LINKS = "websiteLinks";
    public static final String ATTR_KEY_ARTICLES = "articles";
    public static final int NUM_RESULTS_PER_PAGE = 40;
    
    private String nameContextPath;
    
    @Override
    public void init() throws ServletException {
        nameContextPath = getServletContext().getContextPath() + "/items/";
        
        // load the list of sale items into an attribute under the servlet context
        try {
            getServletContext().setAttribute(ATTR_KEY_ITEMS_FOR_SALE, dbManager.getSaleItems());
        } catch(Exception e) {
            logger.severe("ERROR: Could not lookup Sale Items from the DB on PhoneServlet init: " + e.getMessage());
        }
       
        // load the list of sale items into an attribute under the servlet context
        try {
            getServletContext().setAttribute(ATTR_KEY_WEBSITE_LINKS, dbManager.getLinks());
        } catch(Exception e) {
        	logger.severe("ERROR: Could not lookup Website Links from the DB on PhoneServlet init: " + e.getMessage());
        }
       
        // load the list of sale items into an attribute under the servlet context
        try {
            getServletContext().setAttribute(ATTR_KEY_ARTICLES, dbManager.getArticles());
        } catch(Exception e) {
            logger.severe("ERROR: Could not lookup Articles from the DB on PhoneServlet init: " + e.getMessage());
        }
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
        
        try {

            boolean authenticated = AdminServlet.checkAuthentication(request);
            
            // Track information about the user
            trackUserSession(request);
            
            String category = null;
            String product = null;
                
            String requestData = request.getRequestURI().replaceFirst(nameContextPath, "");
            StringTokenizer tk = new StringTokenizer(requestData, "/");

            if(tk.hasMoreTokens())
            {
                category = URLDecoder.decode(tk.nextToken(), "UTF-8");
                
                // Request to get items For Sale
                if("For Sale".equals(category))
                {
                    loadForSaleItems(request, response, authenticated);
                }
                else if("Articles".equals(category))
                {
                    // If more tokens, it's a specific article
                    if(tk.hasMoreTokens())
                    {
                        // Get a specific item under the category
                        product = URLDecoder.decode(tk.nextToken(), "UTF-8");
                        loadArticle(request, response, Long.parseLong(product), authenticated);
                    }
                    // Otherwise, just load all articles
                    else
                    {
                        loadArticles(request, response, authenticated);
                    }
                }
                else if("Links".equals(category))
                {
                	loadLinks(request, response, authenticated);
                	return;
                }
                else if("HitCount".equals(category))
                {
                	request.getSession().setAttribute(ATTR_SESSION_HIT_COUNT, dbManager.getHitCount());
                	return;
                }
                else
                {
                    // If more tokens, it's a specific phone
                    if(tk.hasMoreTokens())
                    {
                        // Get a specific item under the category
                        product = URLDecoder.decode(tk.nextToken(), "UTF-8");
                        loadPhone(request, response, category, product, authenticated);
                    }
                    // Otherwise, just a category of phones
                    else
                    {
                        // Get All phones by Category
                        loadCategory(request, response, category, authenticated);
                    }
                }
            }
            else
            {
            	// no specific item type. Go to the index/home page
                RequestDispatcher view = request.getRequestDispatcher("/index.jsp");
                view.include(request, response);
            }
        } catch(Exception e) {
        	logger.severe(e.getMessage());
        }
        finally {      
        }
    }
    
    /**
     * Gets Phones By Category.
     * Data is forwarded to category.jsp by request dispatcher.
     */
    private void loadCategory(HttpServletRequest request, HttpServletResponse response, String category, boolean authenticated)
    {
        HttpSession session = request.getSession();
        
        boolean isAjax = Boolean.parseBoolean(request.getParameter("ajax"));

        // Log debug info if "debug" parameter was specified
        boolean debug = request.getParameter("debug") != null;
        if(debug)
        	logger.info("In loadCategory() - " + category + ", ajax=" + isAjax);
        
        List<Phone> phones = getContextPhones(request, category, authenticated);
        
        if(phones.size() == 0)
            request.setAttribute(ATTR_ERROR_MESSAGE, "Could not find any phones for category " + category);
        
        // Generate Page Index Information
        int pageNumber = 1;
        try {
            pageNumber = Integer.parseInt(request.getParameter(ATTR_CATEGORY_LISTING_CURRENT_PAGE));
        } catch(Exception e) {}
        
        int beginIndex, endIndex;
        int totalPages = (phones.size() / NUM_RESULTS_PER_PAGE) + (phones.size()%NUM_RESULTS_PER_PAGE == 0 ? 0 : 1);
        
        if(phones.size() <= NUM_RESULTS_PER_PAGE)
        {
            beginIndex = 0;
            endIndex = phones.size() - 1;
        }
        else 
        {
            beginIndex = (pageNumber -1) * NUM_RESULTS_PER_PAGE;
            if(pageNumber < totalPages)
                endIndex = beginIndex + NUM_RESULTS_PER_PAGE - 1;
            else
                endIndex = phones.size() - 1;
        }
        
        if(debug)
        	logger.info(" Page: " + pageNumber + "/" + totalPages
                         	 + "\n Displaying:  " + (beginIndex + 1) + " through " 
                         	 + (endIndex+1) + " of " + phones.size() + " total phones.");
        
        session.setAttribute(ATTR_CATEGORY_LISTING_BEGIN, beginIndex);
        session.setAttribute(ATTR_CATEGORY_LISTING_END, endIndex);
        session.setAttribute(ATTR_CATEGORY_LISTING_TOTAL, phones.size());
        session.setAttribute(ATTR_CATEGORY_LISTING_CURRENT_PAGE, pageNumber);
        session.setAttribute(ATTR_CATEGORY_LISTING_TOTAL_PAGES, totalPages);
        
        session.setAttribute(ATTR_CATEGORY_LISTING, phones);
        session.setAttribute(ATTR_CATEGORY_NAME, category);
        
        String pageToLoad;
        if(isAjax)
        {
            pageToLoad = "/WEB-INF/jsp/category.jsp";
        }
        else
        {
            request.setAttribute("pageToLoad", "/WEB-INF/jsp/category.jsp");
            pageToLoad = "/index.jsp";
        }
        
        try {
            RequestDispatcher view = request.getRequestDispatcher(pageToLoad);
            view.include(request, response);
        
        } catch(Exception e) {
        	OutputUtils.logError(logger, e, "Error Loading Category " + category + ": ");
        }
    }

    /**
     * Gets Phones By Category.
     * Data is forwarded to category.jsp by request dispatcher.
     */
    private void loadPhone(HttpServletRequest request, HttpServletResponse response, String category, String name, boolean authenticated)
    {
        // Log debug info if "debug" parameter was specified
        boolean debug = request.getParameter("debug") != null;
        if(debug)
        	logger.info("In loadPhone() - " + category + '/' + name);
        
        Phone phone = getContextPhone(request, category, name, authenticated);
        
        request.setAttribute("phone", phone);
        request.setAttribute("pageToLoad", "/WEB-INF/jsp/product.jsp");
        
        try {
            RequestDispatcher view = request.getRequestDispatcher("/index.jsp");
            view.forward(request, response);
        
        } catch(Exception e) {
        	OutputUtils.logError(logger, e, "Error Loading Phone " + category + "/" + name + ": ");
        }
    }
    
    /**
     * Gets Phones By Category.
     * Data is forwarded to category.jsp by request dispatcher.
     */
    private void loadForSaleItems(HttpServletRequest request, HttpServletResponse response, boolean authenticated)
    {
        HttpSession session = request.getSession();
        
        boolean isAjax = Boolean.parseBoolean(request.getParameter("ajax"));

        // Log debug info if "debug" parameter was specified
        boolean debug = request.getParameter("debug") != null;
        if(debug)
        	logger.info("In loadForSaleItems(), ajax=" + isAjax);
        
        List<SaleItem> items;
        if(authenticated || getServletContext().getAttribute(PhoneServlet.ATTR_KEY_ITEMS_FOR_SALE) == null)
        {
	        try {
	             items = dbManager.getSaleItems();
	        } catch(Exception e) {
	        	OutputUtils.logError(logger, e, "Error Loading For Sale Items: ");
	            items = new ArrayList<SaleItem>();
	            request.setAttribute(ATTR_ERROR_MESSAGE, e.getMessage());
	        }
	        
	        if(items.size() == 0)
	            request.setAttribute(ATTR_ERROR_MESSAGE, "There are no items for sale at this time.");
	        
	        // restore sale items on the servlet context (GAE is slow sometimes on save)
	        getServletContext().setAttribute(PhoneServlet.ATTR_KEY_ITEMS_FOR_SALE, items);
        }
        else
        {
        	items = (List<SaleItem>)getServletContext().getAttribute(PhoneServlet.ATTR_KEY_ITEMS_FOR_SALE);
        }
        
        // Generate Page Index Information
        int pageNumber = 1;
        try {
            pageNumber = Integer.parseInt(request.getParameter(ATTR_CATEGORY_LISTING_CURRENT_PAGE));
        } catch(Exception e) {}
        
        int beginIndex, endIndex;
        int totalPages = (items.size() / NUM_RESULTS_PER_PAGE) + (items.size()%NUM_RESULTS_PER_PAGE == 0 ? 0 : 1);
        
        if(items.size() <= NUM_RESULTS_PER_PAGE)
        {
            beginIndex = 0;
            endIndex = items.size() - 1;
        }
        else 
        {
            beginIndex = (pageNumber -1) * NUM_RESULTS_PER_PAGE;
            if(pageNumber < totalPages)
                endIndex = beginIndex + NUM_RESULTS_PER_PAGE - 1;
            else
                endIndex = items.size() - 1;
        }
        
        if(debug)
        	logger.info(" Page: " + pageNumber + "/" + totalPages
                         	 + "\n Displaying:  " + (beginIndex + 1) + " through " + (endIndex+1) 
                         	 + " of " + items.size() + " items for sale.");
        
        session.setAttribute(ATTR_CATEGORY_LISTING_BEGIN, beginIndex);
        // TODO: Uncomment next line and delete the following line for pagination
//        session.setAttribute(ATTR_CATEGORY_LISTING_END, endIndex);
        session.setAttribute(ATTR_CATEGORY_LISTING_END, items.size());
        session.setAttribute(ATTR_CATEGORY_LISTING_TOTAL, items.size());
        session.setAttribute(ATTR_CATEGORY_LISTING_CURRENT_PAGE, pageNumber);
        session.setAttribute(ATTR_CATEGORY_LISTING_TOTAL_PAGES, totalPages);
        
        session.setAttribute(ATTR_CATEGORY_LISTING, items);
        session.setAttribute(ATTR_CATEGORY_NAME, "For Sale");
        
        String pageToLoad;
        if(isAjax)
        {
            pageToLoad = "/WEB-INF/jsp/forsale.jsp";
        }
        else
        {
            request.setAttribute("pageToLoad", "/WEB-INF/jsp/forsalelist.jsp");
            pageToLoad = "/index.jsp";
        }
        
        try {
            RequestDispatcher view = request.getRequestDispatcher(pageToLoad);
            view.include(request, response);
        
        } catch(Exception e) {
        	OutputUtils.logError(logger, e, "Error Loading For Sale Items: ");
        }
    }

    /**
     * Loads the article with the specified id.
     * Data is forwarded to article.jsp by request dispatcher.
     */
    private void loadArticle(HttpServletRequest request, HttpServletResponse response, long id, boolean authenticated)
    {
        // Log debug info if "debug" parameter was specified
        boolean debug = request.getParameter("debug") != null;
        if(debug)
        	logger.info("In loadArticle(" + id + ")");

        Article article = null;
        if(!authenticated)
        {
        	Object attribute = getServletContext().getAttribute(ATTR_KEY_ARTICLES);
        	if(attribute != null)
        	{
        		List<Article> articles = (List<Article>)attribute;
    	    	for(Iterator<Article> it = articles.iterator(); it.hasNext();)
    	    	{
    	    		Article art = it.next();
    	    		if(art.getId() == id)
    	    		{
    	    			article = art;
    	    			break;
    	    		}
    	    	}
    	    	
    	    	if(article == null)
    	    		logger.severe("ERROR: Could not lookup article by id: " + id);
        	}
        	
        }

        if(article == null)
        {
	        try {
	            article = dbManager.getArticle(id);
	        } catch(Exception e) {
	        	OutputUtils.logError(logger, e, "Error Loading Article id " + id + ": ");
	            request.setAttribute(ATTR_ERROR_MESSAGE, e.getMessage());
	            article = new Article();
	        }
        }
        
        request.setAttribute("article", article);
        request.setAttribute("pageToLoad", "/WEB-INF/jsp/article.jsp");
        
        try {
            RequestDispatcher view = request.getRequestDispatcher("/index.jsp");
            view.forward(request, response);
        
        } catch(Exception e) {
        	OutputUtils.logError(logger, e, "Error Loading Article id " + id + ": ");
        }
    }
    
    private void loadArticles(HttpServletRequest request, HttpServletResponse response, boolean authenticated)
    {
        // reload articles each time someone hits the articles page
    	List<Article> articles = new ArrayList<Article>();
    	if(authenticated || getServletContext().getAttribute(PhoneServlet.ATTR_KEY_ARTICLES) == null)
        {
	        try {
	        	articles = dbManager.getArticles();
	        } catch (Exception e) {
	        	logger.severe("PhoneServlet Error loading articles: " + e.getMessage());
			} finally {
	        	getServletContext().setAttribute(PhoneServlet.ATTR_KEY_ARTICLES, articles);
			}
        }
    	else
    	{
    		articles = (List<Article>)getServletContext().getAttribute(PhoneServlet.ATTR_KEY_ARTICLES);
    	}
        
        // dispatch to the index jsp with articles jsp as page to load
        request.setAttribute("pageToLoad", "/articles.jsp");
        try {
            RequestDispatcher view = request.getRequestDispatcher("/index.jsp");
            view.forward(request, response);
        
        } catch(Exception e) {
        	OutputUtils.logError(logger, e, "Error Loading Articles: ");
        }
    }
    
    private void loadLinks(HttpServletRequest request, HttpServletResponse response, boolean authenticated)
    {
        // reload links each time someone hits the links page
    	List<WebsiteLink> links = new ArrayList<WebsiteLink>();
    	if(authenticated || getServletContext().getAttribute(PhoneServlet.ATTR_KEY_WEBSITE_LINKS) == null)
        {
	        try {
	        	links = dbManager.getLinks();
	        } catch (Exception e) {
	        	logger.severe("PhoneServlet Error loading links: " + e.getMessage());
			} finally {
	        	getServletContext().setAttribute(PhoneServlet.ATTR_KEY_WEBSITE_LINKS, links);
			}
        }
    	else
    	{
    		links = (List<WebsiteLink>)getServletContext().getAttribute(PhoneServlet.ATTR_KEY_WEBSITE_LINKS);
    	}
        
        // dispatch to the index jsp with links jsp as page to load
        request.setAttribute("pageToLoad", "/links.jsp");
        try {
            RequestDispatcher view = request.getRequestDispatcher("/index.jsp");
            view.forward(request, response);
        
        } catch(Exception e) {
        	OutputUtils.logError(logger, e, "Error Loading Links:");
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
    

    public void trackUserSession(HttpServletRequest request)
    {
    	
    	HttpSession session = request.getSession();
    	if(!Boolean.parseBoolean((String)session.getAttribute(ATTR_SESSION_TRACKED)))
    	{
    		logger.info("New Session: ");

    		String remoteAddress = request.getRemoteAddr();
        	logger.severe("Remote Addr: " + remoteAddress);
        	
        	// Don't increment hit count for google bot process
        	boolean isGoogleBot = false;
        	
        	Enumeration enums = request.getHeaderNames();
        	while(enums.hasMoreElements())
        	{
        		String header = (String)enums.nextElement();
        		String value = request.getHeader(header);
        		logger.severe(header + ": " + value);

        		// Google bot headers look like this:
        		//   From: googlebot(at)googlebot.com
        		//   If-Modified-Since: Sat, 07 Apr 2012 03:43:01 GMT
        		//   User-Agent: Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)
        		if(value != null && value.contains("googlebot"))
        			isGoogleBot = true;
        	}
	    	session.setAttribute(ATTR_SESSION_TRACKED, "true");
	    	
	    	if(isGoogleBot)
	    	{
	    		logger.severe("Detected Google Bot. Will not increment the hit counter.");
	    		return;
	    	}
	    	
	    	long count = dbManager.incrementHitCount();
	    	session.setAttribute(ATTR_SESSION_HIT_COUNT, count);
    	}
    	else
    		logger.info("Session is already being tracked.");
    }
    
    private List<Phone> getContextPhones(HttpServletRequest request, String category, boolean authenticated)
    {
    	List<Phone> phones; 
    	
    	// always lookup the phones when authenticated or when they haven't been set yet
    	Object contextPhones = getServletContext().getAttribute("context_phones_" + category);
    	if(contextPhones == null || authenticated)
    	{
	    	try {
	            phones = dbManager.getByCategory(category);
	            getServletContext().setAttribute("context_phones_" + category, phones);
	        } catch(Exception e) {
	        	OutputUtils.logError(logger, e, "Error Loading Category " + category + ": ");
	            e.printStackTrace();
	            phones = new ArrayList<Phone>();
	            request.setAttribute(ATTR_ERROR_MESSAGE, e.getMessage());
	        }
        }
    	else
    	{
    		phones = (List<Phone>)contextPhones;
    	}
    	
    	return phones;
    }
    
    private Phone getContextPhone(HttpServletRequest request, String category, String name, boolean authenticated)
    {
    	// look up the stored phones if not authenticated
    	if(!authenticated)
    	{
	    	List<Phone> phones = getContextPhones(request, category, authenticated);
	    	for(Iterator<Phone> it = phones.iterator(); it.hasNext();)
	    	{
	    		Phone phone = it.next();
	    		if(name.equals(phone.getName()))
	    			return phone;
	    	}
	    	
	    	logger.severe("ERROR: Could not lookup phone by category/name: " + category + "/" + name +
	    			". Attempting to look it up in the database.");
    	}
    	
    	Phone phone;
        try {
            phone = dbManager.getPhone(category, name);
        } catch(Exception e) {
        	OutputUtils.logError(logger, e, "Error Loading Phone " + category + "/" + name + ": ");
            request.setAttribute(ATTR_ERROR_MESSAGE, e.getMessage());
            phone = new Phone();
        }
        
        return phone;
    }
}
