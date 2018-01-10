package com.mvtel.servlets;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.mvtel.db.DBManagerFactory;
import com.mvtel.db.IDBManager;
import com.mvtel.structures.Article;
import com.mvtel.structures.Phone;
import com.mvtel.structures.SaleItem;
import com.mvtel.structures.WebsiteLink;
import com.mvtel.util.OutputUtils;

/**
 * Servlet implementation class APIServlet
 */
public class APIServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(PhoneServlet.class.getName());

	private static IDBManager dbManager = DBManagerFactory.getDBManager();
    
    private String contextPath;
    private List<SaleItem> saleItems;
    private List<WebsiteLink> links;
    private List<Article> articles;
    
    @Override
    public void init() throws ServletException{
        contextPath = getServletContext().getContextPath() + "/api";
    }
    
    private void loadDatabase()
    {
        synchronized(this)
        {
	        try {
	            saleItems = dbManager.getSaleItems();
	        } catch(Exception e) {
	            logger.severe("ERROR: Could not lookup Sale Items from the DB on APIServlet init: " + e.getMessage());
	            saleItems = new ArrayList<>();
	        }
	       
	        try {
	            links = dbManager.getLinks();
	        } catch(Exception e) {
	        	logger.severe("ERROR: Could not lookup Website Links from the DB on APIServlet init: " + e.getMessage());
	            links = new ArrayList<>();
	        }
	       
	        try {
	            articles = dbManager.getArticles();
	        } catch(Exception e) {
	            logger.severe("ERROR: Could not lookup Articles from the DB on APIServlet init: " + e.getMessage());
	            articles = new ArrayList<>();
	        }
        }
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}
	
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String requestData = request.getRequestURI().replaceFirst(contextPath, "");
        StringTokenizer tk = new StringTokenizer(requestData, "/");

        String type = tk.hasMoreTokens() ? URLDecoder.decode(tk.nextToken(), "UTF-8") : null;
        if(type == null)
        {
        	response.sendError(404);
        	return;
        }

    	logger.info("category " + type);
        
        // Request to get items For Sale
        if("saleitems".equals(type))
        {
            // If more tokens, it's a specific item
            if(tk.hasMoreTokens())
            {
                String id = URLDecoder.decode(tk.nextToken(), "UTF-8");

            	logger.info("Sale item: " + id);
            	try
            	{
            		SaleItem saleItem = dbManager.getSaleItem(Long.parseLong(id));
            		logger.info("SALE ITEM " + saleItem);
            		OutputUtils.toJSON(saleItem).writeJSONString(response.getWriter());
            	}
            	catch(Exception e)
            	{
            		logger.warning(e.getMessage());
            		response.getWriter().println("{}");
            	}
            }
            else
            {
            	logger.info("sale items");
            	try
            	{
            		System.out.println("SALE ITEMS " + saleItems.size());
            		OutputUtils.toJSON(saleItems.toArray(new SaleItem[saleItems.size()])).writeJSONString(response.getWriter());
            	}
            	catch(Exception e)
            	{
            		logger.warning(e.getMessage());
            		response.getWriter().println("[]");
            	}
            }
        }
        else if("articles".equals(type))
        {
            if(tk.hasMoreTokens())
            {
                String id = URLDecoder.decode(tk.nextToken(), "UTF-8");
            	logger.info("article: " + id);
            	try
            	{
            		Article article = dbManager.getArticle(Long.parseLong(id));
            		logger.info("Article " + article);
            		OutputUtils.toJSON(article, true).writeJSONString(response.getWriter());
            	}
            	catch(Exception e)
            	{
            		logger.warning(e.getMessage());
            		response.getWriter().println("{}");
            	}
            }
            else
            {
            	logger.info("articles");
            	boolean full = Boolean.parseBoolean(request.getParameter("full"));
            	try
            	{
            		System.out.println("Articles " + articles.size());
            		OutputUtils.toJSON(articles.toArray(new Article[articles.size()]), full).writeJSONString(response.getWriter());
            	}
            	catch(Exception e)
            	{
            		logger.warning(e.getMessage());
            		response.getWriter().println("[]");
            	}
            }
        }
        else if("links".equals(type))
        {
        	logger.info("links");
        	try
        	{
        		System.out.println("Links " + links.size());
        		OutputUtils.toJSON(links.toArray(new WebsiteLink[links.size()])).writeJSONString(response.getWriter());
        	}
        	catch(Exception e)
        	{
        		logger.warning(e.getMessage());
        		response.getWriter().println("[]");
        	}
        }
        else if("hits".equals(type))
        {
        	long hits = dbManager.getHitCount();
        	logger.info("Hits " + hits);
        	JSONObject json = new JSONObject();
        	json.put("hits", Long.toString(hits));
        	json.writeJSONString(response.getWriter());
        }
        else if("phones".equals(type))
        {
            String category = tk.hasMoreTokens() ? URLDecoder.decode(tk.nextToken(), "UTF-8") : null;
            String name = tk.hasMoreTokens() ? URLDecoder.decode(tk.nextToken(), "UTF-8") : null;
        	boolean full = Boolean.parseBoolean(request.getParameter("full"));

            if(name != null)
            {
            	logger.info("phone " + name);
            	try
            	{
            		Phone phone = dbManager.getPhone(category, name);
            		logger.info("Phone" + phone);
            		OutputUtils.toJSON(phone, true).writeJSONString(response.getWriter());
            	}
            	catch(Exception e)
            	{
            		logger.warning(e.getMessage());
            		response.getWriter().println("{}");
            	}
            }
            else if(category != null)
            {
            	logger.info("phones under " + category);
            	try
            	{
                	List<Phone> phones = dbManager.getByCategory(category);
            		System.out.println("Phones" + phones.size());
            		OutputUtils.toJSON(phones.toArray(new Phone[phones.size()]), full).writeJSONString(response.getWriter());
            	}
            	catch(Exception e)
            	{
            		logger.warning(e.getMessage());
            		response.getWriter().println("[]");
            	}
            }
            else
            {
            	logger.info("all phones");
            	try
            	{
                	List<Phone> phones = dbManager.getAllPhones();
            		System.out.println("All Phones" + phones.size());
            		OutputUtils.toJSON(phones.toArray(new Phone[phones.size()]), full).writeJSONString(response.getWriter());
            	}
            	catch(Exception e)
            	{
            		logger.warning(e.getMessage());
            		response.getWriter().println("[]");
            	}
            }
        }
        else if("invalidate".equals(type))
        {
    		boolean authenticated = request.getSession().getAttribute("authenticated") != null 
					&&(Boolean)request.getSession().getAttribute("authenticated");
    		if(authenticated)
    		{
    			loadDatabase();
    		}
        }
	}

}
