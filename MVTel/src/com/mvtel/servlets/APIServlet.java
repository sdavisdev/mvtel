package com.mvtel.servlets;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private Map<Long, SaleItem> saleItems;
    private List<WebsiteLink> links;
    private Map<Long, Article> articles;
    private Map<String, Map<String, Phone>> phones;
    
    @Override
    public void init() throws ServletException{
        contextPath = getServletContext().getContextPath() + "/api";
        loadDatabase();
    }
    
    private void loadDatabase()
    {
        synchronized(this)
        {
        	saleItems = new HashMap<>();
	        try {
	        	for(SaleItem item : dbManager.getSaleItems())
	        	{
	        		saleItems.put(item.getId(), item);
	        	}
	        } catch(Exception e) {
	            logger.severe("ERROR: Could not lookup Sale Items from the DB on APIServlet init: " + e.getMessage());
	            saleItems = new HashMap<>();
	        }
	       
	        try {
	            links = dbManager.getLinks();
	        } catch(Exception e) {
	        	logger.severe("ERROR: Could not lookup Website Links from the DB on APIServlet init: " + e.getMessage());
	            links = new ArrayList<>();
	        }

            articles = new HashMap<>();
	        try {
	        	for(Article article : dbManager.getArticles())
	        	{
	        		articles.put(article.getId(), article);
	        	}
	        } catch(Exception e) {
	            logger.severe("ERROR: Could not lookup Articles from the DB on APIServlet init: " + e.getMessage());
	        }
	        
	        phones = new HashMap<>();
	        try {
	        	for(Phone phone : dbManager.getAllPhones())
	        	{
	        		Map<String, Phone> category = phones.get(phone.getCategory());
	        		if(category == null)
	        		{
	        			category = new HashMap<>();
	        			phones.put(phone.getCategory(), category);
	        		}
	        		category.put(phone.getName(), phone);
	        	}
	        } catch(Exception e) {
	            logger.severe("ERROR: Could not lookup Articles from the DB on APIServlet init: " + e.getMessage());
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
            		Long itemId = Long.parseLong(id);
            		SaleItem saleItem = saleItems.get(itemId);
            		if(saleItem == null)
            		{
            			saleItem = dbManager.getSaleItem(itemId);
            			saleItems.put(itemId, saleItem);
            		}
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
            		OutputUtils.toJSON(saleItems.values().toArray(new SaleItem[saleItems.size()])).writeJSONString(response.getWriter());
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
            		Long articleId = Long.parseLong(id);
            		Article article = articles.get(articleId);
            		if(article == null)
            		{
            			article = dbManager.getArticle(articleId);
            			articles.put(articleId, article);
            		}
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
            		OutputUtils.toJSON(articles.values().toArray(new Article[articles.size()]), full).writeJSONString(response.getWriter());
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
            	Map<String, Phone> categoryMap = phones.get(category);
        		Phone phone;
            	if(categoryMap != null && (phone = categoryMap.get(name)) != null)
            	{
            		logger.info("Phone" + phone);
            		OutputUtils.toJSON(phone, true).writeJSONString(response.getWriter());
            	}
            	else
            	{
            		logger.warning("Could not look up phone '" + name + "' under " + category);
            		response.getWriter().println("{}");
            	}
            }
            else if(category != null)
            {
            	logger.info("phones under " + category);
            	Map<String, Phone> categoryMap = phones.get(category);
            	if(categoryMap == null)
            	{
            		categoryMap = new HashMap<>();
            	}
        		System.out.println("Phones" + categoryMap.size());
        		OutputUtils.toJSON(categoryMap.values().toArray(new Phone[categoryMap.size()]), full).writeJSONString(response.getWriter());
            }
            else
            {
            	logger.info("all phones");
            	List<Phone> phones = new ArrayList<>();
            	for(Map<String, Phone> categories : this.phones.values())
            	{
            		phones.addAll(categories.values());
            	}
        		System.out.println("All Phones" + phones.size());
        		OutputUtils.toJSON(phones.toArray(new Phone[phones.size()]), full).writeJSONString(response.getWriter());
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
