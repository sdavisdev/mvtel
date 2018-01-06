package com.mvtel.structures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Represents an item for sale.
 */
@PersistenceCapable
public class SaleItem implements Serializable
{
	@NotPersistent
    private long serialVersionUID = 1;
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;
	
	@Persistent
    private String title;
	
	@Persistent
    private String price;
	
	@Persistent
    private String description;
	
	@Persistent
    private List<String> images;
	
	@Persistent
    private List<String> imageUrls;

    public SaleItem(){}

    public SaleItem(String title, String price, String description) {
        this.title = title;
        this.price = price;
        this.description = description;
        this.images = new ArrayList<String>();
        this.imageUrls = new ArrayList<String>();
    }

    public SaleItem(Long id, String title, String price, String description) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.description = description;
        this.images = new ArrayList<String>();
        this.imageUrls = new ArrayList<String>();
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getFirstImageUrl()
    {
    	if(imageUrls.size() > 0)
    		return imageUrls.get(0);
    	else
    	{
    		System.out.println("No images for sale item:\n" + this);
    		return "http://";
    	}
    }
    
    public String toString() {
    	StringBuilder builder = new StringBuilder("SALE ITEM: ");
    	builder.append(title);
    	builder.append(" (");
    	builder.append(id);
    	builder.append(")\n  Price: ");
    	builder.append(price);
    	builder.append("\n  Description: ");
    	builder.append(description);
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
