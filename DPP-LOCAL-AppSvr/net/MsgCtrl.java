package net;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


import util.*;
import bean.*;

public class MsgCtrl extends Thread
{	
	private TcpSvr m_TcpSvr = null;//TCP 服务器
	private DBUtil m_DBUtil = null;

	private int m_Seq = (int)new Date().getTime();

	public Hashtable<String, BaseCmdBean> objActionTable = null;//登陆客户端列表
	private Byte markActionTable = new Byte((byte)1);	
	
	TimeCheckThrd checkThrd = null;

	InetAddress addr = InetAddress.getLocalHost();
	public String m_LocalIp = addr.getHostAddress().toString();//获得本机IP
	
	public MsgCtrl(TcpSvr pTcpSvr, DBUtil dbUtil)throws Exception
	{	    
		m_TcpSvr = pTcpSvr;
		m_DBUtil = dbUtil;
	} 
	public boolean Initialize()
	{
		try
		{
			objActionTable = new Hashtable<String, BaseCmdBean>(); 
			SAXReader reader = new SAXReader();
			Document document = reader.read(new FileInputStream("Config.xml"));
			Element root = document.getRootElement();//取得根节点

			int timeout =  Integer.parseInt(root.element("msgctrl").element("timeout").getText());
			checkThrd = new TimeCheckThrd(timeout);
			checkThrd.start();
			this.start();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
	//监听TcpSvr服务器端数据处理
	public void run()
	{	
		String dealData = "";
		while (true)
		{  			
			try
			{
				byte[] data = (byte[])m_TcpSvr.GetRecvMsgList();
				if(null ==  data || data.length < Cmd_Sta.CONST_MSGHDRLEN)
				{
					sleep(10);
					continue;
				}				
				String strClientKey = new String(data, 0, 20);	
				DataInputStream DinStream = new DataInputStream(new ByteArrayInputStream(data));
				DinStream.skip(20);
				MsgHeadBean msgHead = new MsgHeadBean();			
				msgHead.setUnMsgLen(CommUtil.converseInt(DinStream.readInt()));
				msgHead.setUnMsgCode(CommUtil.converseInt(DinStream.readInt()));
				msgHead.setUnStatus(CommUtil.converseInt(DinStream.readInt()));
				msgHead.setUnMsgSeq(CommUtil.converseInt(DinStream.readInt()));
				msgHead.setUnReserve(CommUtil.converseInt(DinStream.readInt()));
				DinStream.close();
				
				dealData = new String(data, 40, data.length - 40);
				String dealReserve = dealData.substring(0,  20);
				String dealCmd = dealData.substring(24, 28);	
				switch(msgHead.getUnMsgCode())
				{
					case Cmd_Sta.COMM_SUBMMIT://提交
					{
						CommUtil.LOG("Submit [" + strClientKey + "] " + "[" + dealData + "]");
						BaseCmdBean cmdBean = BaseCmdBean.getBean(Integer.parseInt(dealCmd), getSeq());	
						if(null != cmdBean)
						{
							CommUtil.PRINT("Bean[" + cmdBean.getBeanName() + "]");
							cmdBean.parseReqest(strClientKey, dealData, data);
							cmdBean.execRequest(this);
						}
						else
						{
							System.out.println("bean = null");
						}
							
						break;
					}
					case Cmd_Sta.COMM_DELIVER://回应
					{
						CommUtil.LOG("Deliver [ " + strClientKey + "] " + "[" + dealData + "]");
						BaseCmdBean cmdBean = GetAction(dealReserve);
						if(null != cmdBean)
						{
							CommUtil.PRINT("Bean[" + cmdBean.getBeanName() + "]");
							cmdBean.parseReponse(dealData);
							cmdBean.execResponse(this);
						}
						else
						{
							CommUtil.LOG("找不到actiion");
						}
						break;
					}
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				CommUtil.PRINT("MsgCtrl Exception " + dealData);
				continue;
			}
		}//while
	}
	public synchronized String getSeq()
	{
		if(m_Seq == 0xffffffff)
			m_Seq = 0;
		else
			m_Seq++;
		return CommUtil.IntToStringLeftFillZero(m_Seq, 20);
	}
	public boolean DisPatch(int msgCode, String clientKey, String pData)
	{
		boolean ret = false;
		try
		{
			ret = m_TcpSvr.DisPatch(msgCode, clientKey, pData);
		}
		catch(Exception e)
		{	
		}
		return ret;
	}
	
	public TcpSvr getM_TcpSvr() {
		return m_TcpSvr;
	}
	public void setM_TcpSvr(TcpSvr m_TcpSvr) {
		this.m_TcpSvr = m_TcpSvr;
	}
	public DBUtil getM_DBUtil() {
		return m_DBUtil;
	}
	public void setM_DBUtil(DBUtil m_DBUtil) {
		this.m_DBUtil = m_DBUtil;
	}	

	/*
	 * 获取
	 */
	public BaseCmdBean GetAction(String pKey)
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
	public void InsertAction(String pKey, BaseCmdBean bean)
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
				bean.setTestTime((int) (new Date().getTime()/1000));
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
	public void RemoveAction(String pKey)
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
	

/************************************发送线程*****************************************/
private class TimeCheckThrd extends Thread
{
	private int m_TimeOut = 60;
//	private MsgCtrl m_MsgCtrl = null;
	public TimeCheckThrd(int timeout)throws Exception
	{
		m_TimeOut = timeout;
//		m_MsgCtrl = msgCtrl;
	}
	public void run()
	{
		LinkedList<String> checkList = new LinkedList<String>();		 //接收数据列表,用于客户端数据交换
		while(true)
		{
			try
			{
				synchronized(markActionTable)
				{
					Enumeration<BaseCmdBean> en = objActionTable.elements();  
					while(en.hasMoreElements())
					{    
						BaseCmdBean client = en.nextElement();
					
						int TestTime = (int)(new java.util.Date().getTime()/1000);
						if(TestTime > client.getTestTime() + m_TimeOut)
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
					switch(bean.getAction())
					{
						default:
							break;
					}
					CommUtil.LOG(data + " 回应超时");
				}
				sleep(1000*10);
			}catch(Exception e)
			{}
		}				
	}	
}//SendThrd
}//MsgCtrl