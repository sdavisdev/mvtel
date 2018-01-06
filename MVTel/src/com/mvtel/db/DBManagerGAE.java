package com.mvtel.db;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import com.google.appengine.api.datastore.Key;

import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;
import com.mvtel.persistence.PMF;
import com.mvtel.structures.Article;
import com.mvtel.structures.EmailAddress;
import com.mvtel.structures.Phone;
import com.mvtel.structures.Result;
import com.mvtel.structures.SaleItem;
import com.mvtel.structures.WebsiteHits;
import com.mvtel.structures.WebsiteLink;
import com.mvtel.util.OutputUtils;

public class DBManagerGAE implements IDBManager
{
	Logger logger = Logger.getLogger(DBManagerGAE.class.getName());
	
    private static DBManagerGAE instance = new DBManagerGAE();

    
    private DBManagerGAE()
    {
    	
    }
    
    public static DBManagerGAE getInstance()
    {
    	return instance;
    }

	@Override
	public List<Phone> getByCategory(String category) throws DatabaseException
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Query query = pm.newQuery("select from " + Phone.class.getName() +
					" where category == categoryParam" +
					" parameters String categoryParam" +
					" order by name ASC");
			
			List<Phone> results = (List<Phone>)query.execute(category);
			
			if(results.size() == 0)
				throw new DatabaseException("Could not look up phones by category " + category);
			
			return new ArrayList<Phone>(results);
		}finally {
			pm.close();
		}
	}

	@Override
	public Phone getPhone(String category, String name)
			throws DatabaseException
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Query query = pm.newQuery("select from " + Phone.class.getName() +
					" where name == nameParam " +
					" && category == categoryParam " +
					"parameters String nameParam, String categoryParam");
			List<Phone> results = (List<Phone>)query.execute(name, category);
			
			Iterator<Phone> it = results.iterator();
			if(it.hasNext())
				return results.iterator().next();
			else
				throw new DatabaseException("Could not look up phone: " + category + "/" + name + ": It does not exist.");
		} finally {
			pm.close();
		}
	}

	@Override
	public Phone getPhone(long id) throws DatabaseException
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Phone phone = pm.getObjectById(Phone.class, id);
			if(phone != null)
				return phone;
			else
				throw new DatabaseException("Could not look up phone by id: " + id + ": It does not exist.");
		} finally {
			pm.close();
		}
	}

	@Override
	public Result savePhone(Phone phone)
	{
		System.out.println("DBManagerGAE::savePhone() Entered:\n" + phone);
		Phone p = null;
		
		// If the phone to save has an ID, look it up in the database
		if(phone.getId() != null && phone.getId() != -1)
		{
			try {
				p = getPhone(phone.getId().longValue());
				System.out.println("DBManagerGAE::savePhone() Found existing phone by id:\n" + p);
			} catch(DatabaseException dbe) {
				OutputUtils.logError(logger, dbe, "Error looking up Phone to save with id: " + phone.getId());
			}
		}
		
		// No phone by Id, attempt to look it up by name and category
		if(p == null)
		{
			String name = phone.getName();
			String category = phone.getCategory();
			try {
				p = getPhone(category, name);
				System.out.println("DBManagerGAE::savePhone() Found existing phone by category/name:\n" + p);
			} catch(DatabaseException dbe) {
				if(dbe.getMessage().indexOf("It does not exist") < 0)
					System.out.println("Error looking up Phone to save: " + category + "/" + name + ": " + dbe.getMessage());
			}
		}
		
		// if null, simply save the phone
		if(p == null)
		{
			System.out.println("DBManagerGAE::savePhone() This is a new phone.");
			p = phone;
		}
		// otherwise, update fields that have been supplied
		else
		{
			boolean changed = false;
			
			// name
			if(phone.getName() != null && !phone.getName().equals(p.getName()))
			{
				System.out.println("DBManagerGAE::savePhone() Name Changed");
				p.setName(phone.getName());
				changed = true;
			}
			// category
			if(phone.getCategory() != null && !phone.getCategory().equals(p.getCategory()))
			{
				System.out.println("DBManagerGAE::savePhone() Category Changed");
				p.setCategory(phone.getCategory());
				changed = true;
			}
			// description
			if(phone.getDescription() != null && !phone.getDescription().equals(p.getDescription()))
			{
				System.out.println("DBManagerGAE::savePhone() Description Changed");
				p.setDescription(phone.getDescription());
				changed = true;
			}
			// image blob keys
			if(phone.getImages().size() > 0)
			{
				System.out.println("DBManagerGAE::savePhone() Images > 0");
				
				deleteNonPresentImages(phone.getImages(), p.getImages());
				
				p.getImages().clear();
				p.getImages().addAll(phone.getImages());
				changed = true;
			}
			// image urls
			if(phone.getImageUrls().size() > 0)
			{
				System.out.println("DBManagerGAE::savePhone() Image Urls > 0");
				
				p.getImageUrls().clear();
				p.getImageUrls().addAll(phone.getImageUrls());
				changed = true;
			}
			
			if(changed)
			{
				System.out.println("DBManagerGAE::savePhone() There are changed fields. Saving...");
			}
			else
			{
				System.out.println("DBManagerGAE::savePhone() There are NO changed fields. Not Saving!");
				return new Result(false, "There are no new changes to save.");
			}
		}
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		phone = pm.makePersistent(p);
		pm.close();
		
		System.out.println("Phone: Saved Phone: " + phone);
		return new Result();
	}

	@Override
	public Result deletePhone(long id)
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		try {
			Phone phone = pm.getObjectById(Phone.class, id);
			
			pm.currentTransaction().begin();
			
			BlobInfoFactory blobInfoFactory = new BlobInfoFactory();
			List<BlobKey> blobKeys = new ArrayList<BlobKey>();
			for(String image : phone.getImages())
			{
				blobKeys.add(new BlobKey(image));
			}
			
			BlobstoreService blobStoreService = BlobstoreServiceFactory.getBlobstoreService();
			blobStoreService.delete(blobKeys.toArray(new BlobKey[0]));
	
			pm.currentTransaction().commit();
			
			pm.deletePersistent(phone);
			return new Result();
		} finally {
			pm.close();
		}
	}
	
	private void deleteNonPresentImages(List<String> newImages, List<String> oldImages)
	{
		BlobstoreService blobStoreService = BlobstoreServiceFactory.getBlobstoreService();
		
		List<BlobKey> keysToDelete = new ArrayList<BlobKey>();
		for(String key : oldImages)
		{
			if(!newImages.contains(key))
			{
				keysToDelete.add(new BlobKey(key));
			}
		}
		
		blobStoreService.delete(keysToDelete.toArray(new BlobKey[0]));
	}

	@Override
	public SaleItem getSaleItem(long id) throws DatabaseException
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			SaleItem item = pm.getObjectById(SaleItem.class, id);
			if(item != null)
				return item;
			else
				throw new DatabaseException("Could not look up Sale Item by id: " + id + ": It does not exist.");
		} finally {
			pm.close();
		}
	}

	@Override
	public List<SaleItem> getSaleItems() throws DatabaseException
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Query query = pm.newQuery("select from " + SaleItem.class.getName() +
					" order by title ASC");
			
			List<SaleItem> results = (List<SaleItem>)query.execute();
			
			if(results.size() == 0)
				throw new DatabaseException("Could not find any sale items.");
			
			return new ArrayList<SaleItem>(results);
		}finally {
			pm.close();
		}
	}

	@Override
	public Result saveSaleItem(SaleItem item)
	{
		System.out.println("DBManagerGAE::saveSaleItem() Entered:\n" + item);
		SaleItem si = null;
		
		// If the sale item to save has an ID, look it up in the database
		if(item.getId() != null && item.getId() != -1)
		{
			try {
				si = getSaleItem(item.getId().longValue());
				System.out.println("DBManagerGAE::saveSaleItem() Found existing sale item by id:\n" + si);
			} catch(DatabaseException dbe) {
				OutputUtils.logError(logger, dbe, "Error looking up sale item to save with id: " + item.getId());
			}
		}
		
		// if null, simply save the item
		if(si == null)
		{
			System.out.println("DBManagerGAE::saveSaleItem() This is a new Sale Item.");
			si = item;
		}
		// otherwise, update fields that have been supplied
		else
		{
			boolean changed = false;
			
			// Title
			if(item.getTitle() != null && !item.getTitle().equals(si.getTitle()))
			{
				System.out.println("DBManagerGAE::saveSaleItem() Title Changed");
				si.setTitle(item.getTitle());
				changed = true;
			}
			// price
			if(item.getPrice() != null && !item.getPrice().equals(si.getPrice()))
			{
				System.out.println("DBManagerGAE::saveSaleItem() Price Changed");
				si.setPrice(item.getPrice());
				changed = true;
			}
			// description
			if(item.getDescription() != null && !item.getDescription().equals(si.getDescription()))
			{
				System.out.println("DBManagerGAE::saveSaleItem() Description Changed");
				si.setDescription(item.getDescription());
				changed = true;
			}
			// image blob keys
			if(item.getImages().size() > 0)
			{
				System.out.println("DBManagerGAE::saveSaleItem() Images > 0");
				deleteNonPresentImages(item.getImages(), si.getImages());
				
				si.getImages().clear();
				si.getImages().addAll(item.getImages());
				changed = true;
			}
			// image urls
			if(item.getImageUrls().size() > 0)
			{
				System.out.println("DBManagerGAE::saveSaleItem() Image Urls > 0");
				
				si.getImageUrls().clear();
				si.getImageUrls().addAll(item.getImageUrls());
				changed = true;
			}
			
			if(changed)
			{
				System.out.println("DBManagerGAE::saveSaleItem() There are changed fields. Saving...");
			}
			else
			{
				System.out.println("DBManagerGAE::saveSaleItem() There are NO changed fields. Not Saving!");
				return new Result(false, "There are no new changes to save.");
			}
		}
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		item = pm.makePersistent(si);
		pm.close();
		
		System.out.println("Phone: Saved Sale Item: " + item);
		return new Result();
	}

	@Override
	public Result deleteSaleItem(long key)
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		try {
			SaleItem item = pm.getObjectById(SaleItem.class, key);
			
			pm.currentTransaction().begin();
			
			BlobInfoFactory blobInfoFactory = new BlobInfoFactory();
			List<BlobKey> blobKeys = new ArrayList<BlobKey>();
			for(String image : item.getImages())
			{
				blobKeys.add(new BlobKey(image));
			}
			
			BlobstoreService blobStoreService = BlobstoreServiceFactory.getBlobstoreService();
			blobStoreService.delete(blobKeys.toArray(new BlobKey[0]));
	
			pm.currentTransaction().commit();
			
			pm.deletePersistent(item);
			return new Result();
		} finally {
			pm.close();
		}
	}

	@Override
	public Result saveArticle(Article article)
	{
		System.out.println("DBManagerGAE::saveArticle() Entered:\n" + article);
		Article art = null;
		
		// If the article to save has an ID, look it up in the database
		if(article.getId() != null && article.getId() != -1)
		{
			try {
				art = getArticle(article.getId().longValue());
				System.out.println("DBManagerGAE::saveArticle() Found existing sale item by id:\n" + art);
			} catch(DatabaseException dbe) {
				OutputUtils.logError(logger, dbe, "Error looking up article to save with id: " + article.getId());
			}
		}
		
		// if null, simply save the article
		if(art == null)
		{
			System.out.println("DBManagerGAE::saveArticle() This is a new Article.");
			art = article;
		}
		// otherwise, update fields that have been supplied
		else
		{
			boolean changed = false;
			
			// Name
			if(article.getName() != null && !article.getName().equals(art.getName()))
			{
				System.out.println("DBManagerGAE::saveArticle() Name Changed");
				art.setName(article.getName());
				changed = true;
			}
			// Publish Date
			if(article.getPublishDate() != null && !article.getPublishDate().equals(art.getPublishDate()))
			{
				System.out.println("DBManagerGAE::saveArticle() Publish Date Changed");
				art.setPublishDate(article.getPublishDate());
				changed = true;
			}
			// Content
			if(article.getContent() != null && !article.getContent().equals(art.getContent()))
			{
				System.out.println("DBManagerGAE::saveArticle() Content Changed");
				art.setContent(article.getContent());
				changed = true;
			}
			// Content
			if(article.getSortOrder() != art.getSortOrder())
			{
				System.out.println("DBManagerGAE::saveArticle() Sort Order Changed");
				art.setSortOrder(article.getSortOrder());
				changed = true;
			}
			
			if(changed)
			{
				System.out.println("DBManagerGAE::saveArticle() There are changed fields. Saving...");
			}
			else
			{
				System.out.println("DBManagerGAE::saveArticle() There are NO changed fields. Not Saving!");
				return new Result(false, "There are no new changes to save.");
			}
		}
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		article = pm.makePersistent(art);
		pm.close();
		
		System.out.println("DBManagerGAE::saveArticle() Saved Article: " + article);
		return new Result();
	}

	@Override
	public Article getArticle(long id) throws DatabaseException
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Article article = pm.getObjectById(Article.class, id);
			if(article != null)
				return article;
			else
				throw new DatabaseException("Could not look up article by id: " + id + ": It does not exist.");
		} finally {
			pm.close();
		}
	}

	@Override
	public List<Article> getArticles() throws DatabaseException
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Query query = pm.newQuery("select from " + Article.class.getName() +
					" order by sortOrder DESC");
			
			List<Article> results = (List<Article>)query.execute();
			
			if(results.size() == 0)
				throw new DatabaseException("Could not find any articles.");
			
			return new ArrayList<Article>(results);
		}finally {
			pm.close();
		}
	}
    
	@Override
	public Result deleteArticle(long key)
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Article article = pm.getObjectById(Article.class, key);
			pm.deletePersistent(article);
			return new Result();
		} finally {
			pm.close();
		}
	}

	private EmailAddress getEmail(String address) throws DatabaseException
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Key key = KeyFactory.createKey(EmailAddress.class.getSimpleName(), address);
			EmailAddress email = pm.getObjectById(EmailAddress.class, key);
			if(email != null)
				return email;
			else
				throw new DatabaseException("Could not look up email address: " + address + ": It does not exist.");
		} catch (JDOException jdoe) {
			System.out.println("JDOException: " + jdoe.getMessage());
			return null;
		}finally {
			pm.close();
		}
	}

	@Override
	public Result saveEmail(EmailAddress email)
	{
		System.out.println("DBManagerGAE::saveEmail() Entered:\n" + email);
		EmailAddress em = null;
		
		// If the phone to save has an ID, look it up in the database
		if(email.getAddress() != null && email.getAddress().length() > 1)
		{
			try {
				em = getEmail(email.getAddress());
				System.out.println("DBManagerGAE::saveEmail() Found existing email for:\n" + em);
			} catch(DatabaseException dbe) {
				OutputUtils.logError(logger, dbe, "Error looking up Email to save with address: " + email.getAddress());
			}
		}
		
		// if null, simply save the email
		if(em == null)
		{
			System.out.println("DBManagerGAE::saveEmail() This is a new Email.");
			em = email;
			Key key = KeyFactory.createKey(EmailAddress.class.getSimpleName(), email.getAddress());
			em.setKey(key);
		}
		// otherwise, update fields that have been supplied
		else
		{
			boolean changed = false;
			
			// Name
			if(email.getName() != null && !email.getName().equals(em.getName()))
			{
				System.out.println("DBManagerGAE::saveEmail() Name Changed");
				em.setName(email.getName());
				changed = true;
			}
			// Address
			if(email.getAddress() != null && !email.getAddress().equals(em.getAddress()))
			{
				System.out.println("DBManagerGAE::saveEmail() Address Changed");
				em.setAddress(email.getAddress());
				changed = true;
			}
			
			if(changed)
			{
				System.out.println("DBManagerGAE::saveEmail() There are changed fields. Saving...");
			}
			else
			{
				System.out.println("DBManagerGAE::saveEmail() There are NO changed fields. Not Saving!");
				return new Result(false, "There are no new changes to save.");
			}
		}
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		email = pm.makePersistent(em);
		pm.close();
		
		System.out.println("DBManagerGAE::saveEmail() Saved Email: " + email);
		return new Result();
	}

	@Override
	public List<EmailAddress> getEmails()
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Query query = pm.newQuery("select from " + EmailAddress.class.getName() +
					" order by address ASC");
			
			List<EmailAddress> results = (List<EmailAddress>)query.execute();
			
			return new ArrayList<EmailAddress>(results);
		}finally {
			pm.close();
		}
	}

	@Override
	public Result deleteEmail(EmailAddress email)
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			EmailAddress em = pm.getObjectById(EmailAddress.class, email.getAddress());
			pm.deletePersistent(em);
			return new Result();
		} finally {
			pm.close();
		}
	}

	private WebsiteLink getLink(long id) throws DatabaseException
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			WebsiteLink link = pm.getObjectById(WebsiteLink.class, id);
			if(link != null)
				return link;
			else
				throw new DatabaseException("Could not look up link by id: " + id + ": It does not exist.");
		} finally {
			pm.close();
		}
	}

	@Override
	public Result saveLink(WebsiteLink link)
	{
		System.out.println("DBManagerGAE::saveLink() Entered:\n" + link);
		WebsiteLink wl = null;
		
		// If the link to save has an ID, look it up in the database
		if(link.getId() != null && link.getId() != -1)
		{
			try {
				wl = getLink(link.getId().longValue());
				System.out.println("DBManagerGAE::saveLink() Found existing link by id:\n" + wl);
			} catch(DatabaseException dbe) {
				OutputUtils.logError(logger, dbe, "Error looking up link to save with id: " + link.getId());
			}
		}
		
		// if null, simply save the link
		if(wl == null)
		{
			System.out.println("DBManagerGAE::saveLink() This is a new WebsiteLink.");
			wl = link;
		}
		// otherwise, update fields that have been supplied
		else
		{
			boolean changed = false;
			
			// Name
			if(link.getName() != null && !link.getName().equals(wl.getName()))
			{
				System.out.println("DBManagerGAE::saveLink() Name Changed");
				wl.setName(link.getName());
				changed = true;
			}
			// Description
			if(link.getDescription() != null && !link.getDescription().equals(wl.getDescription()))
			{
				System.out.println("DBManagerGAE::saveLink() Description Changed");
				wl.setDescription(link.getDescription());
				changed = true;
			}
			// Sort Order
			if(link.getSortOrder() != wl.getSortOrder())
			{
				System.out.println("DBManagerGAE::saveLink() Sort Order Changed");
				wl.setSortOrder(link.getSortOrder());
				changed = true;
			}
			// URL
			if(link.getUrl() != null && !link.getUrl().equals(wl.getUrl()))
			{
				System.out.println("DBManagerGAE::saveLink() Url Changed");
				wl.setUrl(link.getUrl());
				changed = true;
			}
			
			if(changed)
			{
				System.out.println("DBManagerGAE::saveLink() There are changed fields. Saving...");
			}
			else
			{
				System.out.println("DBManagerGAE::saveLink() There are NO changed fields. Not Saving!");
				return new Result(false, "There are no new changes to save.");
			}
		}
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		link = pm.makePersistent(wl);
		pm.close();
		
		System.out.println("DBManagerGAE::saveLink() Saved WebsiteLink: " + link);
		return new Result();
	}

	@Override
	public List<WebsiteLink> getLinks() throws DatabaseException
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Query query = pm.newQuery("select from " + WebsiteLink.class.getName() +
					" order by sortOrder ASC");
			
			List<WebsiteLink> results = (List<WebsiteLink>)query.execute();
			
			if(results.size() == 0)
				throw new DatabaseException("Could not find any links.");
			
			return new ArrayList<WebsiteLink>(results);
		}finally {
			pm.close();
		}
	}

	@Override
	public Result deleteLink(long key)
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			WebsiteLink link = pm.getObjectById(WebsiteLink.class, key);
			pm.deletePersistent(link);
			return new Result();
		} finally {
			pm.close();
		}
	}

	@Override
	public long getHitCount()
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			WebsiteHits hits = pm.getObjectById(WebsiteHits.class, 1);
			
			return hits.getCount();
		} catch(Exception e){
			logger.severe("Error Getting Website Hits. Creating now.");
//			WebsiteHits hits = new WebsiteHits();
//			hits.increment();
//			pm.makePersistent(hits);
//			return hits.getCount();
			return -100;
		} finally {
			pm.close();
		}
	}
	
	@Override
	public long incrementHitCount()
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			WebsiteHits hits = pm.getObjectById(WebsiteHits.class, 1);
			
			return hits.increment();
		} catch(Exception e){
			logger.severe("Error Incrementing Website Hits. Skipping.");
//			WebsiteHits hits = new WebsiteHits();
//			hits.increment();
//			pm.makePersistent(hits);
//			return hits.getCount();
			return -100;
		} finally {
			pm.close();
		}
	}

	public static void main(String[] args) throws Exception
	{
		RemoteApiOptions options = new RemoteApiOptions()
			.server("localhost", 8888)
			.credentials("test@example.com", "");

		RemoteApiInstaller installer = new RemoteApiInstaller();
		installer.install(options);
		
		DBManagerGAE dbm = DBManagerGAE.getInstance();
		try {
			System.out.println(dbm.saveSaleItem(new SaleItem(null, "Test", "$250", "Some Desc")));
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		installer.uninstall();
	}
	
}
