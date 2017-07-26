package container;

import java.net.InetAddress;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import util.*;
import bean.*;

public class ActionContainer
{
	public static Hashtable<String, BaseCmdBean> objActionTable = null;//登陆客户端列表
	private static Byte markActionTable = new Byte((byte)1);	
	
	private static TimeCheckThrd checkThrd = null;

	InetAddress addr = InetAddress.getLocalHost();
	public String m_LocalIp = addr.getHostAddress().toString();//获得本机IP
	
	public ActionContainer()throws Exception
	{	  
		
	}
	
	public static boolean Initialize()
	{
		boolean ret = false;
		try
		{
			objActionTable = new Hashtable<String, BaseCmdBean>(); 
			checkThrd = new TimeCheckThrd(30);
			checkThrd.start();
			ret = true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return ret;
	}
	
	/*
	 * 获取
	 */
	public static BaseCmdBean GetAction(String pKey)
	{
		BaseCmdBean bean = null;
		try
		{			
			synchronized(markActionTable)
			{
				if(!objActionTable.isEmpty() && objActionTable.containsKey(pKey))
				{
					bean = (BaseCmdBean) objActionTable.get(pKey);
					objActionTable.remove(pKey);		//在哈希表里移除
				}
			}
		}
		catch(Exception exp)
		{		
			exp.printStackTrace();	
		}
		return bean;
	}
	
	/*
	 * 增加
	 */
	public static void InsertAction(String pKey, BaseCmdBean bean)
	{
		try
		{			
			synchronized(markActionTable)					
			{
				if(objActionTable.containsKey(pKey))
				{
					CommUtil.PRINT("Key[" + pKey + "] Already Exist!");
					objActionTable.remove(pKey);		//在哈希表里移除客户端
				}		
				objActionTable.put(pKey , bean);
			}
		}
		catch(Exception exp)
		{		
			exp.printStackTrace();	
		}
	}
	/*
	 * 删除
	 */
	public static void RemoveAction(String pKey)
	{
		try
		{			
			synchronized(markActionTable)
			{
				if(!objActionTable.isEmpty() && objActionTable.containsKey(pKey))
				{
						objActionTable.remove(pKey);		//在哈希表里移除客户端
					
				}
			}
		}
		catch(Exception exp)
		{		
			exp.printStackTrace();	
		}
	}
	public static LinkedList<String> GetTimeOutList(int mTimeOut)
	{
		LinkedList<String> checkList = new LinkedList<String>();		 //接收数据列表,用于客户端数据交换
	
		try
		{
			synchronized(markActionTable)
			{
				Enumeration<BaseCmdBean> en = objActionTable.elements();  
				while(en.hasMoreElements())
				{    
					BaseCmdBean client = en.nextElement();
				
					int TestTime = (int)(new java.util.Date().getTime()/1000);
					if(TestTime > client.getTestTime() + mTimeOut)
					{
						checkList.addLast(CommUtil.StrBRightFillSpace(client.getSeq(), 20));
					}
				}
			}
			while(!checkList.isEmpty())
			{
				String data = checkList.removeFirst();
				if(null ==  data)
				{
					break;
				}						
				BaseCmdBean bean = GetAction(data);
				if(null != bean)
					bean.noticeTimeOut();
				CommUtil.LOG(data + " 回应超时 111");
			}
		}catch(Exception e)
		{}		
		return checkList;
	}	
}//ActionContainer