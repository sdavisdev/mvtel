package com.mvtel.structures;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;

/**
 *
 * @author Steve
 */
@PersistenceCapable
public class Article implements Serializable
{
	@NotPersistent
    private static final long serialVersionUID = 1;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;
	
	@Persistent
    private String name;
	
	@Persistent
    private String publishDate;
	
	@Persistent
    private Text content;
	
	@Persistent
    private int sortOrder;

    public Article() {}

    public Article(Long id, String name, String publishDate, Text content, int sortOrder) 
    {
        this.id = id;
        this.name = name;
        this.publishDate = publishDate;
        this.content = content;
        this.sortOrder = sortOrder;
    }

    public String getContent() {
        return content.getValue();
    }

    public void setContent(String content) {
        this.content = new Text(content);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public String toString() {
        return "ARTICLE: \nid=" + id + ", sortOrder: " + sortOrder
            + "\nName: " + name + "\nPublished on: " + publishDate
            + "\nContent:\n" + content;
    }
}
