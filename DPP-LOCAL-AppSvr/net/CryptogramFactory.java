/*
 * Created on 2004-4-21
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

package net;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class CryptogramFactory
{
    	private static Cryptogram cryptogram = null;
    	
	    
	    public static Cryptogram createCry(String str)
		{
	    	if(str.trim().equals("MD5"))
	    	{
	    		
	    			cryptogram = new Md5();
	    		return cryptogram;
	    	}
	    	else
	    		return null;
	    	
	    }
	    
}
