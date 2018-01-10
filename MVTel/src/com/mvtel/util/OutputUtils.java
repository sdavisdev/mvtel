/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mvtel.util;

import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Text;
import com.mvtel.structures.Article;
import com.mvtel.structures.EmailAddress;
import com.mvtel.structures.Phone;
import com.mvtel.structures.Result;
import com.mvtel.structures.SaleItem;
import com.mvtel.structures.WebsiteLink;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Steve
 */
public class OutputUtils 
{
    public static JSONObject toJSON(Result result)
    {
        JSONObject object = new JSONObject();
        object.put("success", result.isSuccess());
        object.put("message", result.getMessage());
        
        return object;
    }

    public static JSONArray toJSON(Phone[] phones, boolean full)
    {
    	JSONArray array = new JSONArray();
    	for(Phone phone : phones)
    	{
    		array.add(toJSON(phone, full));
    	}
    	return array;
    }
    
    public static JSONObject toJSON(Phone phone, boolean full)
    {
    	JSONObject object = new JSONObject();
    	object.put("id", Long.toString(phone.getId()));
    	object.put("name", phone.getName());
    	object.put("cat", phone.getCategory());
    	object.put("img", phone.getFirstImageUrl());
    	if(full)
    	{
    		object.put("desc", phone.getDescription());
        	JSONArray imageUrls = new JSONArray();
        	for(String url: phone.getImageUrls())
        	{
        		imageUrls.add(url);
        	}
        	object.put("imgUrls", imageUrls);
        	JSONArray images = new JSONArray();
        	for(String image: phone.getImages())
        	{
        		images.add(image);
        	}
        	object.put("imgs", images);
    	}
    	return object;
    }

    public static JSONArray toJSON(SaleItem[] items)
    {
    	JSONArray array = new JSONArray();
    	for(SaleItem item : items)
    	{
    		array.add(toJSON(item));
    	}
    	return array;
    }
    
    public static JSONObject toJSON(SaleItem item)
    {
    	JSONObject object = new JSONObject();
    	object.put("id", Long.toString(item.getId()));
    	object.put("title", item.getTitle());
    	object.put("desc", item.getDescription());
    	object.put("price", item.getPrice());
    	JSONArray imageUrls = new JSONArray();
    	for(String url: item.getImageUrls())
    	{
    		imageUrls.add(url);
    	}
    	object.put("imgUrls", imageUrls);
    	JSONArray images = new JSONArray();
    	for(String image: item.getImages())
    	{
    		images.add(image);
    	}
    	object.put("imgs", images);
    	return object;
    }

    public static JSONArray toJSON(EmailAddress[] emails)
    {
    	JSONArray array = new JSONArray();
    	for(EmailAddress email : emails)
    	{
    		array.add(toJSON(email));
    	}
    	return array;
    }

    public static JSONObject toJSON(EmailAddress email)
    {
    	JSONObject object = new JSONObject();
    	object.put("addr", email.getAddress());
    	object.put("name", email.getName());
    	return object;
    }

    public static JSONArray toJSON(Article[] articles, boolean full)
    {
    	JSONArray array = new JSONArray();
    	for(Article article : articles)
    	{
    		array.add(toJSON(article, full));
    	}
    	return array;
    }

    public static JSONObject toJSON(Article article, boolean full)
    {
    	JSONObject object = new JSONObject();
    	object.put("id", Long.toString(article.getId()));
    	object.put("name", article.getName());
    	object.put("date", article.getPublishDate());
    	object.put("sort", Integer.toString(article.getSortOrder()));
    	if(full)
    	{
        	object.put("content", article.getContent());
    	}
    	return object;
    }

    public static JSONArray toJSON(WebsiteLink[] links)
    {
    	JSONArray array = new JSONArray();
    	for(WebsiteLink link : links)
    	{
    		array.add(toJSON(link));
    	}
    	return array;
    }

    public static JSONObject toJSON(WebsiteLink link)
    {
    	JSONObject object = new JSONObject();
    	object.put("id", Long.toString(link.getId()));
    	object.put("url", link.getUrl());
    	object.put("name", link.getName());
    	object.put("desc", link.getDescription());
    	object.put("sort", Integer.toString(link.getSortOrder()));
    	return object;
    }
    
    public static void logError(Logger logger, Throwable t, String message)
    {
    	StringBuilder sb = new StringBuilder(message);
    	sb.append('\n');
    	sb.append(t.getMessage());
    	sb.append('\n');
    	
        for(StackTraceElement ste : t.getStackTrace())
        {
        	sb.append("   at ");
        	sb.append(ste);
        	sb.append('\n');
        }

        logger.severe(sb.toString());
    }
    
    public static void main(String[] args)
    {
    	Phone phone = new Phone(10L, "Name", "Description", "Category");
    	phone.getImages().add("img1");
    	phone.getImages().add("img2");
    	phone.getImages().add("img3");
    	phone.getImageUrls().add("http://img.1");
    	phone.getImageUrls().add("http://img.2");
    	phone.getImageUrls().add("http://img.3");
    	System.out.println(toJSON(phone, true).toJSONString());
    	
    	EmailAddress email = new EmailAddress("test@example.com", "Test User");
    	System.out.println(toJSON(email).toJSONString());
    	
    	Article article = new Article(123L, "Article Name", "Jan 2018", new Text("Article Contents"), 5);
    	System.out.println(toJSON(article, true).toJSONString());
    	
    	WebsiteLink link = new WebsiteLink(456L, "Mikes Vintage Telephones", "My Site...", "mvtelonline.com", 2);
    	System.out.println(toJSON(link).toJSONString());

    	SaleItem item = new SaleItem(10L, "For sale", "$100", "This item is for sale");
    	item.getImages().add("item1");
    	item.getImages().add("item2");
    	item.getImages().add("item3");
    	item.getImageUrls().add("http://item.1");
    	item.getImageUrls().add("http://item.2");
    	item.getImageUrls().add("http://item.3");
    	System.out.println(toJSON(item).toJSONString());
    }
}
