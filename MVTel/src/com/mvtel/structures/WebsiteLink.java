/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mvtel.structures;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 *
 * @author Steve
 */
@PersistenceCapable
public class WebsiteLink implements Serializable
{
	@NotPersistent
    private static final long serialVersionUID = 1;
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;
	
	@Persistent
    private String name;
	
	@Persistent
    private String description;
	
	@Persistent
    private String url;
	
	@Persistent
    private int sortOrder;

    public WebsiteLink() {}

    public WebsiteLink(Long id, String name, String description, String url, int sortOrder) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.url = url;
        this.sortOrder = sortOrder;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    public String toString() {
        return "LINK:\nid=" + id + ", sortOrder: " + sortOrder
            + "\nName:" + name + " (" + url + ")"
            + "\nDescription: " + description;
    }
    
}
