
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

public class Md5 implements Cryptogram
{
	/* (non-Javadoc)
	 * @see com.best.rpm.Cryptogram#encrypt(java.lang.String)
	 */
	public byte[] encrypt(byte []str)
	{
		java.security.MessageDigest alga=null;
		try
		{			
			alga = java.security.MessageDigest.getInstance("MD5");
		    alga.update(str);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		byte[] digesta=alga.digest();
		return digesta;
	}
}
