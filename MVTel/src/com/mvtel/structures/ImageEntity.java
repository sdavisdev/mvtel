package com.mvtel.structures;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.persistence.Entity;


import com.google.appengine.api.datastore.Blob;

@PersistenceCapable
//@Entity
//@PersistenceCapable(identityType=IdentityType.APPLICATION)
public class ImageEntity implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	
	@Persistent
	private String name;
	
	@Persistent
	Blob image;
	
	public ImageEntity() {}
	
	public ImageEntity(String name, Blob image) 
	{
		this.name = name;
		this.image = image;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Blob getImage()
	{
		return image;
	}

	public void setImage(Blob image)
	{
		this.image = image;
	}
	
	
	
}
