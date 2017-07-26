package net.appsvr;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import net.Md5;
import net.MsgHeadBean;
import net.TcpClient;
import net.TcpSvrBase;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import container.ActionContainer;
import bean.BaseCmdBean;
import util.*;

public class TcpSvrAppGateWay extends TcpSvrBase
{
	private int m_iPort    = 0;
	private int m_iTimeOut = 0;
	private int m_iStatus  = 0;
	DBUtil m_DbUtil = null;
	TcpClient m_TcpClient = null;
	
	//登陆客户端列表
	private static Hashtable<String, ClientSocket> objClientTable = null;
	private static Byte markClientTable = new Byte((byte)1);
	
	//读取配置文件内容
	public TcpSvrAppGateWay(DBUtil dbUtil)throws Exception
	{
		SAXReader reader  = new SAXReader();
		Document document = reader.read(new FileInputStream("Config.xml"));
		Element root = document.getRootElement();
		m_iPort      = Integer.parseInt(root.element("app_server").element("server_prot").getText());
		m_iTimeOut   = Integer.parseInt(root.element("app_server").element("server_timeout").getText());
		m_iStatus    = Integer.parseInt(root.element("app_client").element("client_sta").getText());
		m_DbUtil     = dbUtil;
		
		if(1 == m_iStatus)
		{
			m_TcpClient = new TcpClient(m_DbUtil);
			m_TcpClient.init();
		}
	}
	
	//初始化
	public boolean Initialize()
	{
		if(!init(m_iPort, m_iTimeOut))
			return false;
		objClientTable = new Hashtable<String, ClientSocket>();
		MsgCtrl msgCtrl = new MsgCtrl();
		msgCtrl.start();
		return true;
	}
	
	public String CheckClient(byte[] Buffer, Socket objClient)
	{
		String ret = null;
		try
		{
//			DataInputStream DinStream = new DataInputStream(new ByteArrayInputStream(Buffer));
//			DinStream.readInt();
//			int Cmd = CommUtil.converseInt(DinStream.readInt());
//			if(Cmd_Sta.COMM_LOGON != Cmd)
//			{
//				return null;
//			}
//			
//			//登入验证
//			String Status = new String(Buffer, 20, 4);
//			String PId = new String(Buffer, 24, 20);
//			String TimeStamp = new String(Buffer, 44, 14);
//			String strMd5 = new String(Buffer, 58, 32);
//			String checkResult = checkClient(Status, PId, TimeStamp, strMd5);
//			if(!checkResult.substring(0, 4).equalsIgnoreCase("0000"))
//			{
//				return null;
//			}
//			ret = PId;
//			
//			//验证是否已存在
//			if(objClientTable.containsKey(PId))
//			{
//				CommUtil.PRINT("Id Already Exist!" + PId);
//				ClientClose(PId);
//			}
			
			String strClientKey =  String.valueOf(m_iClientKey ++);
			//新建通道
			ClientSocket objChannel= new ClientSocket();	
			if(!objChannel.init(objClient, strClientKey))
			{
				CommUtil.LOG("ClientId [" + strClientKey + "] ClientSocket init failed!");
			}
			synchronized(markClientTable)
			{
				objClientTable.put(strClientKey , objChannel);
			}
			
			//更新通道IP
//			CommUtil.LOG("CPM_IP:" + objClient.getInetAddress().toString());
//			String pSql = "update device_detail t set t.link_url = '"+ objClient.getInetAddress().toString().substring(1) +"' where t.id = '"+ PId.trim() +"'";
//			m_DbUtil.doUpdate(pSql);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return ret;
	}
	
	public byte[] GetActiveTestBuf()
	{
		byte[] byteData = null;
		try
		{
			ByteArrayOutputStream boutStream = new ByteArrayOutputStream();
			DataOutputStream doutStream = new DataOutputStream(boutStream);
			doutStream.writeInt(CommUtil.converseInt(CmdUtil.MSGHDRLEN));
			doutStream.writeInt(CommUtil.converseInt(CmdUtil.COMM_ACTIVE_TEST));
			doutStream.writeInt(0);
			doutStream.writeInt(CommUtil.converseInt(GetSeq()));
			doutStream.writeInt(0);
			byteData = boutStream.toByteArray();
			doutStream.close();
			boutStream.close();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
			byteData = null;
		}
		return byteData;
	}

	public String checkClient(String strStatus, String strId, String strTimestamp, String strOriginalMd5)
	{
		String ret = "3006";
		String password = m_DbUtil.APC(CommUtil.StrRightFillSpace(strId, 40)+ strStatus + "0001");
		String strData = strId + strTimestamp + password;
		System.out.println("strData[" + strData + "]");
		String Temp = CommUtil.BytesToHexString(new Md5().encrypt(strData.getBytes()), 16);
		System.out.println("Temp[" + Temp + "]");
		CommUtil.LOG("Client[" + strId + "] TimeStamp[" + strTimestamp + "] OldMd5[" + strOriginalMd5 + "] NewMd5[" + Temp + "] DbMsg[" + password + "]");
		
		if(Temp.equalsIgnoreCase(strOriginalMd5))
		{
			ret = "0000";
		}
		return ret;
	}

	public void ClientStatusNotify(String strClientKey, int iStatus)
	{		
		switch(iStatus)
		{
			case STATUS_CLIENT_ONLINE:
			{
				//CPM网关恢复在线
				String OffStr = "";
				OffStr = CommUtil.StrBRightFillSpace("", 20)
					   + "0000"
					   + "1004"
					   + CommUtil.StrBRightFillSpace("", 10)
					   + CommUtil.StrBRightFillSpace("", 30)
					   + CommUtil.StrBRightFillSpace("", 4)
					   + CommUtil.StrBRightFillSpace("", 20)
					   + "7"
					   + CommUtil.StrBRightFillSpace((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()), 20)
					   + CommUtil.StrBRightFillSpace("网关恢复在线", 128);
				SetRecvMsgList((strClientKey + new String(EnCode(Cmd_Sta.COMM_SUBMMIT, OffStr))).getBytes());
				break;
			}
			case STATUS_CLIENT_OFFLINE:
			{
				//CPM网关离线
				String OffStr = "";
				OffStr = CommUtil.StrBRightFillSpace("", 20)
					   + "0000"
					   + "1004"
					   + CommUtil.StrBRightFillSpace("", 10)
					   + CommUtil.StrBRightFillSpace("", 30)
					   + CommUtil.StrBRightFillSpace("", 4)
					   + CommUtil.StrBRightFillSpace("", 20)
					   + "6"
					   + CommUtil.StrBRightFillSpace((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()), 20)
					   + CommUtil.StrBRightFillSpace("网关离线", 128);
				SetRecvMsgList((strClientKey + new String(EnCode(Cmd_Sta.COMM_SUBMMIT, OffStr))).getBytes());
				break;
			}
		}
	}
	
	public byte[] EnCode(int msgCode, String pData)
	{
		byte[] byteData = null;
		try
		{
			ByteArrayOutputStream boutStream = new ByteArrayOutputStream();
			DataOutputStream doutStream = new DataOutputStream(boutStream);
			{
				doutStream.writeInt(CommUtil.converseInt(CmdUtil.MSGHDRLEN + pData.getBytes().length));//长度
				doutStream.writeInt(CommUtil.converseInt(msgCode));
				doutStream.writeInt(CommUtil.converseInt(0));
				doutStream.writeInt(CommUtil.converseInt(GetSeq()));
				doutStream.writeInt(CommUtil.converseInt(0));
				doutStream.write(pData.getBytes());
			}
			byteData = boutStream.toByteArray();
			boutStream.close();
			doutStream.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return byteData;
	}
	
	public static boolean DisPatch(int msgCode, String clientKey, String pData)
	{
		boolean ret = false;
		try
		{
			synchronized(markClientTable)
			{
				if(!objClientTable.isEmpty() && objClientTable.containsKey(clientKey))
				{
					CommUtil.LOG("Succee DisPatch Client[" + clientKey + "] Data[" + pData + "]");
					ClientSocket objChannel = (ClientSocket) objClientTable.get(clientKey);	
					objChannel.SendMsg(msgCode, pData);
					ret = true;
				}
				else
				{
					CommUtil.LOG("Failed DisPatch Client[" + clientKey + "] Data[" + pData + "]");
				}
			}
		}
		catch(Exception e)
		{
		}
		return ret;
	}
	
	public synchronized void ClientClose(String pClientKey)
	{
		try
		{			
			synchronized(markClientTable)
			{
				if(!objClientTable.isEmpty() && objClientTable.containsKey(pClientKey))
				{
					ClientSocket objChannel = (ClientSocket) objClientTable.get(pClientKey);
					if(null != objChannel.objSocket && !objChannel.objSocket.isClosed())
					{
						//关掉SOCKET连接
						objChannel.objSocket.close();
						objChannel.objSocket = null;
						ClientStatusNotify(pClientKey, STATUS_CLIENT_OFFLINE);
					}
					//在哈希表里移除客户端
					objClientTable.remove(pClientKey);			
				}
			}
		}
		catch(Exception exp)
		{
			exp.printStackTrace();
		}
	}
	
	private class MsgCtrl extends Thread
	{
		public void run()
		{
			String dealData = "";
			while (true)
			{
				try
				{
					byte[] data = (byte[])GetRecvMsgList();
					if(null ==  data || data.length < Cmd_Sta.CONST_MSGHDRLEN)
					{
						sleep(10);
						continue;
					}
					
					DataInputStream DinStream = new DataInputStream(new ByteArrayInputStream(data));
					MsgHeadBean msgHead = new MsgHeadBean();
					msgHead.setUnMsgLen(CommUtil.converseInt(DinStream.readInt()));
					msgHead.setUnMsgCode(CommUtil.converseInt(DinStream.readInt()));
					msgHead.setUnStatus(CommUtil.converseInt(DinStream.readInt()));
					msgHead.setUnMsgSeq(CommUtil.converseInt(DinStream.readInt()));
					msgHead.setUnReserve(CommUtil.converseInt(DinStream.readInt()));
					DinStream.close();

					dealData = new String(data, 20, data.length - 20);
					
					String dealReserve = dealData.substring(0,  20);
					String dealCmd = dealData.substring(20, 24);
					String strClientKey=dealData.substring(28,  38);
					switch(msgHead.getUnMsgCode())
					{
						case Cmd_Sta.COMM_SUBMMIT://提交
						{
							CommUtil.LOG("PlatForm Submit [" + strClientKey + "] " + "[" + dealCmd + "]");
							BaseCmdBean cmdBean = BaseCmdBean.getBean(Integer.parseInt(dealCmd), m_DbUtil);	
							if(null != cmdBean)
							{
								cmdBean.parseReqest(strClientKey, dealData, data);
								cmdBean.execRequest();
								
								if(1 == m_iStatus)
								{
									//上传
									m_TcpClient.SetSendMsg(strClientKey + dealData, 1);
								}
							}
							break;
						}
						case Cmd_Sta.COMM_DELIVER://回应
						{
							CommUtil.LOG("PlatForm Deliver [" + strClientKey + "] " + "[" + dealData + "]");
							BaseCmdBean cmdBean = ActionContainer.GetAction(dealReserve);
							if(null != cmdBean)
							{
								cmdBean.parseReponse(dealData);
								cmdBean.execResponse();
							}
							break;
						}
					}
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					CommUtil.PRINT("TcpSvrAppGateWay Exception [" + dealData+"]");
					continue;
				}
			}//while
		}
	}
}//TcpSvrCls
