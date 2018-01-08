/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mvtel.db;

import com.mvtel.structures.Article;
import com.mvtel.structures.EmailAddress;
import com.mvtel.structures.Phone;
import com.mvtel.structures.Result;
import com.mvtel.structures.SaleItem;
import com.mvtel.structures.WebsiteLink;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Steve
 */
public class DBManager implements IDBManager
{
    private static String dbUrl = "jdbc:mysql://localhost:3306/mvtel";
    private static String dbDriver = "com.mysql.jdbc.Driver";
    private static String dbUser = "mvtel";
    private static String dbPassword = "mvtel";
    
    private static DBManager instance = new DBManager();

    private Connection conn;
    
    private DBManager()
    {
    	try {
    		getConnection();
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public static DBManager getInstance()
    {
    	return instance;
    }
    
    private Connection connect() throws ClassNotFoundException, SQLException
    {
        System.out.println("DBManager establishing connection to: " + dbUrl);

        Class.forName(dbDriver);
        conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

        System.out.println("Established connection: " + conn);
        
        return conn;
    }
    
    private synchronized Connection getConnection() throws ClassNotFoundException, SQLException
    {
        if(conn != null)
        {
            try {
                
                if(!conn.isClosed() && conn.isValid(2))
                {
                    return conn;
                }
            } catch(SQLException sqle) {
                System.out.println("Error with existing connection, creating a new connection: ");
                sqle.printStackTrace();
            }
        }
        
        return connect();
    }
    
    public Result savePhone(Phone phone)
    {
        CallableStatement proc = null;
        try {
            Connection conn = getConnection();
            proc = conn.prepareCall("{ call SavePhone(?, ?, ?, ?, ?, ?) }");
            proc.setLong(1, phone.getId());
            proc.setString(2, phone.getName());
            proc.setString(3, phone.getCategory());
            proc.setString(4, phone.getDescription());
//            System.out.println("Phone Dir: " + phone.getDirectory());
//            proc.setString(5, phone.getDirectory());
//            proc.setInt(6, phone.getImageCount());
            proc.execute();
            
            return new Result();
        } catch(Exception e) {
            e.printStackTrace();
            return new Result(false, "Could not save phone: " + e.getMessage());
        } finally {
            if(proc != null) {
                try { proc.close(); } catch (Exception e) {}
            }
        }       
    }
    
    public Result deletePhone(long id)
    {
        CallableStatement proc = null;
        try {
            Connection conn = getConnection();
            proc = conn.prepareCall("{ call DeletePhone(?) }");
            proc.setLong(1, id);
            proc.execute();
            
            return new Result();
        } catch(Exception e) {
            e.printStackTrace();
            return new Result(false, "Could not delete phone: " + e.getMessage());
        } finally {
            if(proc != null) {
                try { proc.close(); } catch (Exception e) {}
            }
        }       
    }
    
    public ArrayList<Phone> getByCategory(String category) throws DatabaseException
    {
        ArrayList<Phone> phones = new ArrayList<Phone>();
        
        CallableStatement proc = null;
        ResultSet rs = null;
        try {
            Connection conn = getConnection();
            proc = conn.prepareCall("{ call GetPhonesByCategory(?) }");
            proc.setString(1, category);
            proc.execute();
            
            rs = proc.getResultSet();
            while(rs.next())
            {
                Phone phone = new Phone();
                phone.setId(rs.getLong(1));
                phone.setName(rs.getString(2));
                phone.setCategory(rs.getString(3));
                phone.setDescription(rs.getString(4));
//                phone.setDirectory(rs.getString(5));
//                phone.setImageCount(rs.getInt(6));
                
                phones.add(phone);
            }

        } catch(Exception e) {
            throw new DatabaseException("Could not look up phones by category " 
                    + category + ": " + e.getMessage(), e);
        } finally {
            if(rs != null) {
                try { rs.close(); } catch (Exception e) {}
            }
            if(proc != null) {
                try { proc.close(); } catch (Exception e) {}
            }
        }
        
        return phones;
    }
    
    public Phone getPhone(String category, String name) throws DatabaseException
    {
        CallableStatement proc = null;
        ResultSet rs = null;
        try {
            Connection conn = getConnection();
            proc = conn.prepareCall("{ call GetPhone(?, ?) }");
            proc.setString(1, category);
            proc.setString(2, name);
            proc.execute();
            
            rs = proc.getResultSet();
            if(rs.next())
            {
                Phone phone = new Phone();
                phone.setId(rs.getLong(1));
                phone.setName(rs.getString(2));
                phone.setCategory(rs.getString(3));
                phone.setDescription(rs.getString(4));
//                phone.setDirectory(rs.getString(5));
//                phone.setImageCount(rs.getInt(6));
                
                return phone;
            }

        } catch(Exception e) {
            throw new DatabaseException("Could not look up phone " + name + ": " + e.getMessage(), e);
        } finally {
            if(rs != null) {
                try { rs.close(); } catch (Exception e) {}
            }
            if(proc != null) {
                try { proc.close(); } catch (Exception e) {}
            }
        }
        
        throw new DatabaseException("The phone " + name + " does not exist.");
    }
    
    public Phone getPhone(long id) throws DatabaseException
    {
        CallableStatement proc = null;
        ResultSet rs = null;
        try {
            Connection conn = getConnection();
            proc = conn.prepareCall("{ call GetPhoneById(?) }");
            proc.setLong(1, id);
            proc.execute();
            
            rs = proc.getResultSet();
            if(rs.next())
            {
                Phone phone = new Phone();
                phone.setName(rs.getString(2));
                phone.setCategory(rs.getString(3));
                phone.setDescription(rs.getString(4));
//                phone.setDirectory(rs.getString(5));
//                phone.setImageCount(rs.getInt(6));
                
                return phone;
            }

        } catch(Exception e) {
            throw new DatabaseException("Could not look up phone by id: " + id + ": " + e.getMessage(), e);
        } finally {
            if(rs != null) {
                try { rs.close(); } catch (Exception e) {}
            }
            if(proc != null) {
                try { proc.close(); } catch (Exception e) {}
            }
        }
        
        throw new DatabaseException("The phone with id: " + id + " does not exist.");
    }
    
    public Result saveSaleItem(SaleItem item)
    {
        CallableStatement proc = null;
        try {
            Connection conn = getConnection();
            proc = conn.prepareCall("{ call SaveSaleItem(?, ?, ?, ?, ?, ?) }");
            
            proc.registerOutParameter(6, Types.INTEGER);
            proc.setLong(1, item.getId());
            proc.setString(2, item.getTitle());
            proc.setString(3, item.getDescription());
            proc.setString(4, item.getPrice());
//            proc.setInt(5, item.getNumImages());
            proc.setInt(6, -1); //output parameter
            
            proc.execute();
            
            int key = proc.getInt(6);
            
            return new Result(true, ""+key);
        } catch(Exception e) {
            e.printStackTrace();
            return new Result(false, "Could not save sale item: " + e.getMessage());
        } finally {
            if(proc != null) {
                try { proc.close(); } catch (Exception e) {}
            }
        }       
    }
    
    public Result deleteSaleItem(long key)
    {
        CallableStatement proc = null;
        try {
            Connection conn = getConnection();
            proc = conn.prepareCall("{ call DeleteSaleItem(?) }");
            proc.setLong(1, key);
            proc.execute();
            
            return new Result();
        } catch(Exception e) {
            e.printStackTrace();
            return new Result(false, "Could not delete sale item: " + e.getMessage());
        } finally {
            if(proc != null) {
                try { proc.close(); } catch (Exception e) {}
            }
        }       
    }
    
    public ArrayList<SaleItem> getSaleItems() throws DatabaseException
    {
        ArrayList<SaleItem> items = new ArrayList<SaleItem>();
        
        CallableStatement proc = null;

        ResultSet rs = null;
        try {
            Connection conn = getConnection();
            proc = conn.prepareCall("{ call GetSaleItems() }");
            proc.execute();
            
            rs = proc.getResultSet();
            while(rs.next())
            {
                SaleItem item = new SaleItem();
                item.setId(rs.getLong(1));
                item.setTitle(rs.getString(2));
                item.setDescription(rs.getString(3));
                item.setPrice(rs.getString(4));
//                item.setNumImages(rs.getInt(5));
                
                items.add(item);
            }
        } catch(Exception e) {
            throw new DatabaseException("Could not look up sale items: " + e.getMessage(), e);
        } finally {
            if(rs != null) {
                try { rs.close(); } catch (Exception e) {}
            }
            if(proc != null) {
                try { proc.close(); } catch (Exception e) {}
            }
        }
        
        return items;
    }
    
    public SaleItem getSaleItem(long id) throws DatabaseException
    {
        CallableStatement proc = null;

        ResultSet rs = null;
        try {
            Connection conn = getConnection();
            proc = conn.prepareCall("{ call GetSaleItem(?) }");
            proc.setLong(1, id);
            proc.execute();
            
            rs = proc.getResultSet();
            if(rs.next())
            {
                SaleItem item = new SaleItem();
                item.setId(rs.getLong(1));
                item.setTitle(rs.getString(2));
                item.setDescription(rs.getString(3));
                item.setPrice(rs.getString(4));
//                item.setNumImages(rs.getInt(5));
                
                return item;
            }
            else
                throw new DatabaseException("Item does not exist.");
        } catch(Exception e) {
            throw new DatabaseException("Could not look up sale item id " + id + ": " + e.getMessage(), e);
        } finally {
            if(rs != null) {
                try { rs.close(); } catch (Exception e) {}
            }
            if(proc != null) {
                try { proc.close(); } catch (Exception e) {}
            }
        }
    }
    
    public Result saveLink(WebsiteLink link)
    {
        CallableStatement proc = null;
        try {
            Connection conn = getConnection();
            proc = conn.prepareCall("{ call SaveLink(?, ?, ?, ?, ?, ?) }");
            
            proc.registerOutParameter(6, Types.INTEGER);
            proc.setLong(1, link.getId());
            proc.setString(2, link.getName());
            proc.setString(3, link.getDescription());
            proc.setString(4, link.getUrl());
            proc.setInt(5, link.getSortOrder());
            proc.setInt(6, -1);
            
            proc.execute();
            
            int key = proc.getInt(6);
            
            return new Result(true, ""+key);
        } catch(Exception e) {
            e.printStackTrace();
            return new Result(false, "Could not save link: " + e.getMessage());
        } finally {
            if(proc != null) {
                try { proc.close(); } catch (Exception e) {}
            }
        }       
    }
    
    public Result deleteLink(long key)
    {
        CallableStatement proc = null;
        try {
            Connection conn = getConnection();
            proc = conn.prepareCall("{ call DeleteLink(?) }");
            proc.setLong(1, key);
            proc.execute();
            
            return new Result();
        } catch(Exception e) {
            e.printStackTrace();
            return new Result(false, "Could not delete link: " + e.getMessage());
        } finally {
            if(proc != null) {
                try { proc.close(); } catch (Exception e) {}
            }
        }       
    }
    
    public ArrayList<WebsiteLink> getLinks() throws DatabaseException
    {
        ArrayList<WebsiteLink> links = new ArrayList<WebsiteLink>();
        
        CallableStatement proc = null;

        ResultSet rs = null;
        try {
            Connection conn = getConnection();
            proc = conn.prepareCall("{ call GetLinks() }");
            proc.execute();
            
            rs = proc.getResultSet();
            while(rs.next())
            {
                WebsiteLink link = new WebsiteLink();
                link.setId(rs.getLong(1));
                link.setName(rs.getString(2));
                link.setDescription(rs.getString(3));
                link.setUrl(rs.getString(4));
                link.setSortOrder(rs.getInt(5));
                
                links.add(link);
            }
        } catch(Exception e) {
            throw new DatabaseException("Could not look up website links: " + e.getMessage(), e);
        } finally {
            if(rs != null) {
                try { rs.close(); } catch (Exception e) {}
            }
            if(proc != null) {
                try { proc.close(); } catch (Exception e) {}
            }
        }
        
        return links;
    }
    
    public Result saveArticle(Article article)
    {
        CallableStatement proc = null;
        try {
            Connection conn = getConnection();
            proc = conn.prepareCall("{ call SaveArticle(?, ?, ?, ?, ?, ?) }");
            
            proc.registerOutParameter(6, Types.INTEGER);
            proc.setLong(1, article.getId());
            proc.setString(2, article.getName());
            proc.setString(3, article.getPublishDate());
            proc.setString(4, article.getContent());
            proc.setInt(5, article.getSortOrder());
            proc.setInt(6, -1);
            
            proc.execute();
            
            int key = proc.getInt(6);
            
            return new Result(true, ""+key);
        } catch(Exception e) {
            e.printStackTrace();
            return new Result(false, "Could not save article: " + e.getMessage());
        } finally {
            if(proc != null) {
                try { proc.close(); } catch (Exception e) {}
            }
        }       
    }
    
    public Result deleteArticle(long key)
    {
        CallableStatement proc = null;
        try {
            Connection conn = getConnection();
            proc = conn.prepareCall("{ call DeleteArticle(?) }");
            proc.setLong(1, key);
            proc.execute();
            
            return new Result();
        } catch(Exception e) {
            e.printStackTrace();
            return new Result(false, "Could not delete article: " + e.getMessage());
        } finally {
            if(proc != null) {
                try { proc.close(); } catch (Exception e) {}
            }
        }       
    }
    
    public ArrayList<Article> getArticles() throws DatabaseException
    {
        ArrayList<Article> articles = new ArrayList<Article>();
        
        CallableStatement proc = null;

        ResultSet rs = null;
        try {
            Connection conn = getConnection();
            proc = conn.prepareCall("{ call GetArticles() }");
            proc.execute();
            
            rs = proc.getResultSet();
            while(rs.next())
            {
                Article article = new Article();
                article.setId(rs.getLong(1));
                article.setName(rs.getString(2));
                article.setPublishDate(rs.getString(3));
                article.setContent(rs.getString(4));
                article.setSortOrder(rs.getInt(5));
                
                articles.add(article);
            }
        } catch(Exception e) {
            throw new DatabaseException("Could not look up articles: " + e.getMessage(), e);
        } finally {
            if(rs != null) {
                try { rs.close(); } catch (Exception e) {}
            }
            if(proc != null) {
                try { proc.close(); } catch (Exception e) {}
            }
        }
        
        return articles;
    }
    
    public Article getArticle(long id) throws DatabaseException
    {
        CallableStatement proc = null;

        ResultSet rs = null;
        try {
            Connection conn = getConnection();
            proc = conn.prepareCall("{ call GetArticle( ? ) }");
            proc.setLong(1, id);
            proc.execute();
            
            rs = proc.getResultSet();
            if(rs.next())
            {
                Article article = new Article();
                article.setId(rs.getLong(1));
                article.setName(rs.getString(2));
                article.setPublishDate(rs.getString(3));
                article.setContent(rs.getString(4));
                article.setSortOrder(rs.getInt(5));
                
                return article;
            }
            else
            {
                throw new DatabaseException("Id " + id + " does not exist");
            }
        } catch(Exception e) {
            throw new DatabaseException("Could not look up article: " + e.getMessage(), e);
        } finally {
            if(rs != null) {
                try { rs.close(); } catch (Exception e) {}
            }
            if(proc != null) {
                try { proc.close(); } catch (Exception e) {}
            }
        }
    }
    
    public Result saveEmail(EmailAddress email)
    {
        CallableStatement proc = null;
        try {
            Connection conn = getConnection();
            proc = conn.prepareCall("{ call SaveEmail(?, ?) }");
            proc.setString(1, email.getAddress());
            proc.setString(2, email.getName());
            proc.execute();
            
            return new Result(true, email.toString());
        } catch(Exception e) {
            e.printStackTrace();
            return new Result(false, "Could not save email: " + e.getMessage());
        } finally {
            if(proc != null) {
                try { proc.close(); } catch (Exception e) {}
            }
        }       
    }
    
    public Result deleteEmail(EmailAddress email)
    {
        CallableStatement proc = null;
        try {
            Connection conn = getConnection();
            proc = conn.prepareCall("{ call DeleteEmail(?) }");
            proc.setString(1, email.getAddress());
            proc.execute();
            
            return new Result(true, email.getAddress());
        } catch(Exception e) {
            e.printStackTrace();
            return new Result(false, "Could not delete " + email.toString() + ": " + e.getMessage());
        } finally {
            if(proc != null) {
                try { proc.close(); } catch (Exception e) {}
            }
        }       
    }
    
    public List<EmailAddress> getEmails()
    {
        List<EmailAddress> emails = new ArrayList<EmailAddress>();
        
        CallableStatement proc = null;
        try {
            Connection conn = getConnection();
            proc = conn.prepareCall("{ call GetEmails() }");
            proc.execute();
            
            ResultSet rs = proc.getResultSet();
            while(rs.next())
            {
                EmailAddress email = new EmailAddress(rs.getString(1), rs.getString(2));
                emails.add(email);
            }
            
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(proc != null) {
                try { proc.close(); } catch (Exception e) {}
            }
        }
        
        return emails;
    }
    
	public long getHitCount()
	{
		System.out.println("DBManager::getHitCount() NOT SUPPORTED!");
		return 0;
	}
	
	public long incrementHitCount()
	{
		System.out.println("DBManager::incrementHitCount() NOT SUPPORTED!");
		return 0;
	}
	
	public List<Phone> getAllPhones()
	{
		System.out.println("DBManager::getAllPhones() NOT SUPPORTED!");
		return new ArrayList<Phone>();
	}
    
    public static void main(String[] args) throws Exception
    {
    	DBManager mgr = DBManager.getInstance();
    	
        boolean testLinks = false;
        boolean testArticles = false;
        boolean testEmails = true;
        
        if(testLinks)
        {
            WebsiteLink link = new WebsiteLink();
            link.setName("Antique Telephone Collectors Association ATCA");
            link.setDescription("The Antique Telephone Collectors Association, or ATCA, is the largest telephone collectors organization in the world. Chartered in 1971 as a non-profit corporation by the state of Kansas, its over 1000 active members are located throughout the United States, Canada, Europe, and Australia. ");
            link.setUrl("http://www.atcaonline.com/");
            link.setSortOrder(1);

            Result rs = mgr.saveLink(link);
            if(rs.isSuccess())
                System.out.println("Saved ATCA link with id: " + rs.getMessage());

            link = new WebsiteLink();
            link.setName("Telephone Collectors International TCI");
            link.setDescription("Telephone Collectors International is an organization of telephone collectors, hobbyists and historians who are helping to preserve the history of the telecommunications industry through the collection of telephones and telephone related material. Our collections represent all aspects of the industry; from the very first wooden prototypes that started the industry to the technological marvels that made the automatic telephone exchange possible. ");
            link.setUrl("http://www.telephonecollectors.org/");
            link.setSortOrder(2);

            rs = mgr.saveLink(link);
            if(rs.isSuccess())
                System.out.println("Saved TCI link with id: " + rs.getMessage());
        }
        
        if(testArticles)
        {
            ArrayList<Article> articles = mgr.getArticles();
            for(Article art : articles)
            {
                System.out.println("Retrieved Article: " + art);
            }
            
            
            Article article = new Article();
            article.setName("Test Article");
            article.setPublishDate("Tonight");
            article.setContent("Article Content");
            article.setSortOrder(0);
            
            Result rs = mgr.saveArticle(article);
            System.out.println("Save Article Result: " + rs.isSuccess() + ": " + rs.getMessage());
            
            if(rs.isSuccess())
            {
                article.setId(Long.parseLong(rs.getMessage()));
                article.setName(article.getName() + " -- MODIFIED");
                
                rs = mgr.saveArticle(article);
                System.out.println("Save Modified Article Result: " + rs.isSuccess() + ": " + rs.getMessage());
            }
        }
        
        if(testEmails)
        {
            EmailAddress email = new EmailAddress("Steve@example.com", "Steve");
            
            Result rs = mgr.deleteEmail(email);
            System.out.println("Delete Result: " + rs.isSuccess() + " - " + rs.getMessage());
            
            
//            rs = saveEmail(email);
//            System.out.println("Save Result: " + rs.isSuccess() + " - " + rs.getMessage());
            
            List<EmailAddress> emails = mgr.getEmails();
            for(EmailAddress e : emails)
            {
                System.out.println("Found " + e);
            }
        }
    }
}
