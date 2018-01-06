package com.mvtel.db;

import java.util.List;

import com.mvtel.structures.Article;
import com.mvtel.structures.EmailAddress;
import com.mvtel.structures.Phone;
import com.mvtel.structures.Result;
import com.mvtel.structures.SaleItem;
import com.mvtel.structures.WebsiteLink;

public interface IDBManager
{
	Result savePhone(Phone phone);
	Result deletePhone(long id);
	List<Phone> getByCategory(String category) throws DatabaseException;
	Phone getPhone(String category, String name) throws DatabaseException;
	Phone getPhone(long id) throws DatabaseException;
	
	Result saveSaleItem(SaleItem item);
	Result deleteSaleItem(long key);
	List<SaleItem> getSaleItems() throws DatabaseException;
	SaleItem getSaleItem(long id) throws DatabaseException;
	
	Result saveLink(WebsiteLink link);
	Result deleteLink(long key);
	List<WebsiteLink> getLinks() throws DatabaseException;
	
	Result saveArticle(Article article);
	Result deleteArticle(long key);
	List<Article> getArticles() throws DatabaseException;
	Article getArticle(long id) throws DatabaseException;
	
	Result saveEmail(EmailAddress email);
	Result deleteEmail(EmailAddress email);
	List<EmailAddress> getEmails();
	
	long getHitCount();
	long incrementHitCount();
}
