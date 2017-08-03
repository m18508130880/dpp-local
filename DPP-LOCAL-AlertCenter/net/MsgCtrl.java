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
	private TcpClient m_TcpClient = null;//TCP ������
	private AlertCtrl m_AlertCtrl = null;

	private int m_Seq = (int)new Date().getTime();

	public Hashtable<String, BaseCmdBean> objActionTable = null;//��½�ͻ����б�
	private Byte markActionTable = new Byte((byte)1);	
	
	TimeCheckThrd checkThrd = null;

	InetAddress addr = InetAddress.getLocalHost();
	public String m_LocalIp = addr.getHostAddress().toString();//��ñ���IP
	
	public MsgCtrl(TcpClient TcpClient, AlertCtrl alertCtrl)throws Exception
	{	    
		m_TcpClient = TcpClient;
		m_AlertCtrl = alertCtrl;
	} 
	public boolean Initialize()
	{
		try
		{
			objActionTable = new Hashtable<String, BaseCmdBean>(); 
			SAXReader reader = new SAXReader();
			Document document = reader.read(new FileInputStream("Config.xml"));
			Element root = document.getRootElement();//ȡ�ø��ڵ�

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
	//����TcpSvr�����������ݴ���
	public void run()
	{	
		String dealData = "";
		while (true)
		{  			
			try
			{
				byte[] data = (byte[])m_TcpClient.GetRecvMsgList();
				if(null ==  data || data.length < Cmd_Sta.CONST_MSGHDRLEN)
				{
					sleep(10);
					continue;
				}
				String strClientKey = new String(data, 0, 20);
				
				dealData = new String(data, 20, data.length - 20);
				//String dealReserve = dealData.substring(0,  20);
				String dealCmd = dealData.substring(24, 28);
				switch(Integer.valueOf(dealCmd))
				{
					case Cmd_Sta.CMD_SUBMIT_2001://��������
					{
						//CommUtil.LOG("Submit [" + strClientKey + "] " + "[" + dealData + "]");
						BaseCmdBean cmdBean = BaseCmdBean.getBean(Integer.parseInt(dealCmd), getSeq());	
						if(null != cmdBean)
						{
							CommUtil.PRINT("Bean[" + cmdBean.getBeanName() + "]");
							cmdBean.parseReqest(strClientKey, dealData, data);
							cmdBean.execRequest(m_AlertCtrl);
						}
						else
						{
							System.out.println("bean = null");
						}
							
						break;
					}
					case Cmd_Sta.CMD_SUBMIT_2002://�澯����
						String Project_IdAndSysId = dealData.substring(28, 39);
						m_AlertCtrl.doAlert(Project_IdAndSysId);
						break;
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
	
	public TcpClient getM_TcpClient() {
		return m_TcpClient;
	}
	public void setM_TcpSvr(TcpClient m_TcpClient) {
		this.m_TcpClient = m_TcpClient;
	}

	/*
	 * ��ȡ
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
					objActionTable.remove(pKey);		//�ڹ�ϣ�����Ƴ�
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
	 * ����
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
					objActionTable.remove(pKey);		//�ڹ�ϣ�����Ƴ��ͻ���
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
	 * ɾ��
	 */
	public void RemoveAction(String pKey)
	{
		try
		{			
			synchronized(markActionTable)
			{
				if(!objActionTable.isEmpty() && objActionTable.containsKey(pKey))
				{
						objActionTable.remove(pKey);		//�ڹ�ϣ�����Ƴ��ͻ���
					
				}
			}
		}
		catch(Exception exp)
		{		
			exp.printStackTrace();	
		}
	}
	

/************************************�����߳�*****************************************/
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
		LinkedList<String> checkList = new LinkedList<String>();		 //���������б�,���ڿͻ������ݽ���
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
					CommUtil.LOG(data + " ��Ӧ��ʱ");
				}
				sleep(1000*10);
			}catch(Exception e)
			{}
		}				
	}	
}//SendThrd
}//MsgCtrl