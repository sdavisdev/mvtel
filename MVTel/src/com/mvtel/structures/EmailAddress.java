/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mvtel.structures;

import javax.jdo.annotations.IdGeneratorStrategy;
import com.google.appengine.api.datastore.Key;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 *
 * @author Steve
 */
@PersistenceCapable
public class EmailAddress
{
    private static final long serialVersionUID = 1;
    
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

	@Persistent
    private String address;
	
	@Persistent
    private String name;

    public EmailAddress() {}
    
    public EmailAddress(String address, String name) {
        this.address = address;
        this.name = name;
    }

    public Key getKey()
	{
		return key;
	}

	public void setKey(Key key)
	{
		this.key = key;
	}

	public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String toString()
    {
        return "Email: " + address + " (" + name + ")";
    }
    
}
