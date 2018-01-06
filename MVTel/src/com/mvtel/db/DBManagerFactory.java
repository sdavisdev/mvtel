package com.mvtel.db;

public class DBManagerFactory
{
	private static boolean useGAE = true;
	
	public static IDBManager getDBManager()
	{
		if(useGAE)
			return DBManagerGAE.getInstance();
		else
			return DBManager.getInstance();
	}
}
