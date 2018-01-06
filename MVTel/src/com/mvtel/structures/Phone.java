package com.mvtel.structures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.persistence.Entity;

/**
 *
 * @author Steve
 */
//@Entity
@PersistenceCapable
//@PersistenceCapable(identityType=IdentityType.APPLICATION)
//@PersistenceCapable(identityType=IdentityType.DATASTORE)
public class Phone implements Serializable
{
	@NotPersistent
    private long serialVersionUID = 1;
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;
	
	@Persistent
    private String name;
	
	@Persistent
    private String category;
	
	@Persistent
    private String description;
	
	@Persistent
    private List<String> images;
	
	@Persistent
    private List<String> imageUrls;
    
    public Phone() {}

    public Phone(Long id, String name, String description, String category)
    {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.images = new ArrayList<String>();
        this.imageUrls = new ArrayList<String>();
    }
    
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public List<String> getImages() {
    	if(images == null)
    	{
    		images = new ArrayList<String>();
    	}
        return images;
    }

    public List<String> getImageUrls() {
    	if(imageUrls == null)
    	{
    		imageUrls = new ArrayList<String>();
    	}
        return imageUrls;
    }
    
    public String getFirstImageUrl()
    {
    	if(imageUrls.size() > 0)
    		return imageUrls.get(0);
    	else
    	{
    		System.out.println("No image urls for phone:\n" + this);
    		return "http://";
    	}
    }
    
    public String toString() {
    	StringBuilder builder = new StringBuilder("PHONE: ");
    	builder.append(name);
    	builder.append(" (");
    	builder.append(id);
    	builder.append(")\n  Category: ");
    	builder.append(category);
    	builder.append("\n  Description: ");
    	builder.append(description);
    	builder.append("\n  Images:");
    	for(String image: getImages())
    	{
	    	builder.append("\n    ");
	    	builder.append(image);
    	}
    	builder.append("\n  ImageUrls:");
    	for(String url: getImageUrls())
    	{
	    	builder.append("\n    ");
	    	builder.append(url);
    	}
    	builder.append("\n");
        return builder.toString();
    }
}
