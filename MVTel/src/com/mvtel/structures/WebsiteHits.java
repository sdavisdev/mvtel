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
public class WebsiteHits implements Serializable
{
	@NotPersistent
    private static final long serialVersionUID = 1;
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;
	
	@Persistent
    private long count;

    public WebsiteHits() {
    	id = 1L;
    	count = 0;
    }
    
    public long getCount() {
        return count;
    }
    
    public void setCount(long count) {
        this.count = count;
    }

    public long increment() {
        return ++count;
    }

    public String toString() {
        return "Website Hits" + count;
    }
    
}
