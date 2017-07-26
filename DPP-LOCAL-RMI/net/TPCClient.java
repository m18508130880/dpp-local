package net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Vector;

import util.*;

public class TPCClient extends Thread
{
	private String m_IP = null;
	private int m_Port = 0;
	private int m_TimeOut = 0;
	private int m_TestSta = 0;
	private String m_ID = "";
	private String m_PWD = "";
	
	private Socket objSocket = null;
	private RecvThrd objRecvThrd = null;
	private Byte markRecv = new Byte((byte)1);
	private LinkedList<Object> recvMsgList = null;
	
	private SendThrd objSendThrd = null;
	private Byte markSend = new Byte((byte)1);
	private LinkedList<Object> sendMsgList = null;
	
	private int m_Seq = 0;
	private String m_XmlPath = "Config.ini";
	public TPCClient(String xmlPath) throws Exception
	{
		m_XmlPath = xmlPath;
	}
	
	/**
	 * 初始化
	 * @return
	 */
	public boolean init()
	{
		boolean RetVal = false;
		try
		{
			Properties prop = new Properties();
			prop.load(new FileInputStream(m_XmlPath));
			m_IP = prop.getProperty("RMIIP");
			m_Port = Integer.parseInt(prop.getProperty("RMIPort"));
			m_ID = prop.getProperty("RMILoginId");
			m_PWD = prop.getProperty("RMILoginPwd");
			m_TimeOut = Integer.parseInt(prop.getProperty("RMITimeout"));
			System.out.println("m_IP[" + m_IP + "] m_Port[" + m_Port + "]");
			
			if(null == recvMsgList)
				recvMsgList = new LinkedList<Object>();
			if(null == sendMsgList)
				sendMsgList = new LinkedList<Object>();
			if(Reconnect())
			{
				objRecvThrd = new RecvThrd(objSocket);
				objRecvThrd.start();			
				objSendThrd = new SendThrd(objSocket);
				objSendThrd.start();
				RetVal = true;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}		
		if(m_TimeOut != 0 )
		{
			if(!this.isAlive())
			{
				this.start();
			}
		}
		return RetVal;
	}
	
	/**
	 * 重连 Reconnect
	 * @return boolean
	 */
	private boolean Reconnect()
	{
		boolean RetVal = false;
		try
		{
			if(null != objSocket)
			{
				objSocket.close();
				objSocket = null;
			}
			objSocket = new Socket(m_IP, m_Port);
			if(null != objSocket )
			{
				RetVal = Login();
				if(RetVal)
				{
					System.out.println("Connect " + m_IP + " Success");
				}
			}			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return RetVal;
	}
	
	/**
	 * 登陆 Login
	 * @return
	 */
	private boolean Login()
	{
		boolean RetVal = false;

		try
		{
	    	String strToday = (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date());
	        String strData = CommUtil.StrBRightFillSpace(m_ID, 20) + strToday + m_PWD;	        
			byte []md5_output = new Md5().encrypt(strData.getBytes());
			strData = CommUtil.StrBRightFillSpace(m_ID, 20) + CommUtil.StrBRightFillSpace(strToday, 14) + CommUtil.BytesToHexString(md5_output, 16).toUpperCase();
			ByteArrayOutputStream boutStream = new ByteArrayOutputStream();
			DataOutputStream doutStream = new DataOutputStream(boutStream);
			doutStream.writeInt(CommUtil.converseInt(70));
			doutStream.writeInt(CommUtil.converseInt(Cmd_Sta.COMM_LOGON));//连接
			doutStream.writeInt(CommUtil.converseInt(0));
			doutStream.writeInt(CommUtil.converseInt(0));
			doutStream.writeInt(CommUtil.converseInt(0));
			doutStream.writeBytes("0000");
			doutStream.writeBytes(strData);
			byte[]byteData = boutStream.toByteArray();
			DataOutputStream SendChannel = null;
			SendChannel = new DataOutputStream(objSocket.getOutputStream());
			SendChannel.write(byteData);			
			SendChannel.flush();

			CommUtil.PRINT(strData);
			
	    	DataInputStream RecvChannlLogin = new DataInputStream(objSocket.getInputStream());
	    	objSocket.setSoTimeout(20000);
			int iRecvLen = RecvChannlLogin.read(byteData);  
			if(iRecvLen >= 16 )
			{
				DataInputStream DinStream = new DataInputStream(new ByteArrayInputStream(byteData));
				DinStream.skipBytes(8);
				int unMsgStatus = DinStream.readInt();
				if(0 == unMsgStatus)
				{
					RetVal = true;
				}
				objSocket.setSoTimeout(0);				
			}
		}
		catch (Exception exp)
		{
			System.out.println("Logon failed");
			exp.printStackTrace();
		}
		return RetVal;
	}
	
	public void run()
	{
		int testTime= (int)(new java.util.Date().getTime()/1000);
		int nowTime = testTime;
		int dTime = 0;
		
		while(true)
		{
			try
			{					
				sleep(1000);
				if(objSocket == null || objSocket.isClosed())
				{
					if(init())
					{
						System.out.println(m_IP+"Reconnect sucess.............\n");
					}
					else
					{
						if(null != objSocket)
						{
							objSocket.close();
							objSocket = null;
						}
						System.out.println(m_IP + "Reconnect Failed.............\n");
					}
					continue;
				}
				
				nowTime = (int)(new java.util.Date().getTime()/1000);
				dTime = nowTime - testTime;
				if(dTime > m_TimeOut)
				{
					//连接超时的一个处理
					m_TestSta++;
					if(m_TestSta > Cmd_Sta.COMM_ACTIVE_TEST)
					{
						m_TestSta = 0;
						objSocket.close();
						objSocket = null;
						System.out.println("Active Test...Close the socket");
					}
					else
					{
						//如果正常的话，就发出测试包
//						if(m_TestSta >= Cmd_Sta.COMM_ACTIVE_TEST)
						{							
							ByteArrayOutputStream boutStream = new ByteArrayOutputStream();
							DataOutputStream doutStream = new DataOutputStream(boutStream);
							doutStream.writeInt(CommUtil.converseInt(Cmd_Sta.CONST_MSGHDRLEN));
							doutStream.writeInt(CommUtil.converseInt(Cmd_Sta.COMM_ACTIVE_TEST));
							doutStream.writeInt(0);
							doutStream.writeInt(CommUtil.converseInt(GetSeq()));
							doutStream.writeInt(CommUtil.converseInt(0));
							byte[] msg = boutStream.toByteArray();
							SetSendMsgList(msg);
							CommUtil.PRINT("Send Active Package to UAG!");
							
						}
					}
					testTime = nowTime;
				}				
			}
			catch(SocketException exp)
			{
				try
				{
					exp.printStackTrace();
					if(null != objSocket)
					{
						objSocket.close();
						objSocket = null;
					}
				}catch(Exception e)
				{}
				continue;
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				continue;
			}
		}
	}
	//生成序列号
	public int GetSeq()
	{
		if(m_Seq++ == 0xffffff)
			m_Seq = 0;
		return m_Seq;
	}	
	//添加发送数据
	public boolean SetSendMsg(String pData, int pType)
	{
		boolean ret = false;
		if(SetSendMsgList(EnCode(pData, pType)))
		{
			System.out.println("m_IP[" + m_IP + "] pData[" + pData + "]");
			ret = true;
		}
		return ret;
	}
	
	public byte[] GetRecvMsgList()
	{		
		byte[] data = null;
		synchronized(markRecv)
		{
			if(!recvMsgList.isEmpty())
			{	
				data = (byte[])recvMsgList.removeFirst();
			}
		}
		return data;
	}
	public boolean SetSendMsgList(Object  object)
	{
		synchronized(markSend)
		{
			sendMsgList.addLast(object);
		}
		return true;
	}
	private byte[] getSendMsgList()
	{
		byte[] byteData = null;		
		synchronized(markSend)
		{
			if(null !=sendMsgList && !sendMsgList.isEmpty())
			{	
				byteData = (byte[]) sendMsgList.removeFirst();
			}
		}
		return byteData;
	}

	public byte[] EnCode(String pData, int pType)
	{
		byte[] byteData = null;
		int msgLen = Cmd_Sta.CONST_MSGHDRLEN + pData.getBytes().length;
		ByteArrayOutputStream boutStream = new ByteArrayOutputStream();
		DataOutputStream doutStream = new DataOutputStream(boutStream);
		try
		{
			doutStream.writeInt(CommUtil.converseInt(msgLen));
			switch(pType)
			{	
				case 1:
					doutStream.writeInt(CommUtil.converseInt(Cmd_Sta.COMM_SUBMMIT));
					break;
				case 2:
					doutStream.writeInt(CommUtil.converseInt(Cmd_Sta.COMM_DELIVER));
					break;
				default:
					doutStream.writeInt(CommUtil.converseInt(Cmd_Sta.COMM_SUBMMIT));
					break;				
			}		
			doutStream.writeInt(CommUtil.converseInt(0));//Sta
			doutStream.writeInt(CommUtil.converseInt(GetSeq()));//seq
			doutStream.writeInt(CommUtil.converseInt(0));//reserve
			doutStream.write(pData.getBytes());
			byteData = boutStream.toByteArray();
			boutStream.close();
			doutStream.close();	 
		}
		catch(Exception exp)
		{
			System.out.println("EnCode Exp:"+exp.getMessage());
			exp.printStackTrace();
		}
		System.out.println("byteData["+byteData+"]");
		return byteData;
	}

private class RecvThrd extends Thread
{
	private DataInputStream RecvChannel = null;
	public RecvThrd(Socket pSocket)throws Exception
	{
		RecvChannel = new DataInputStream(pSocket.getInputStream());
	}
	public void run()
	{
		byte[] data = null;
		Vector<Object> vectData= new Vector<Object>();
		int nRcvLen = 0;
		int nRcvPos = 0;
		int nCursor = 0;
		byte ctRslt = 0;
		boolean bContParse = true;
		byte[] cBuff = new byte[Cmd_Sta.CONST_MAX_BUFF_SIZE];
		while(true)
		{
			try
			{
				if(null == objSocket || objSocket.isClosed())
				{
					System.out.println("objSocket error");
					objSocket = null;
					break;
				}
				
				nRcvLen = RecvChannel.read(cBuff, nRcvPos, (Cmd_Sta.CONST_MAX_BUFF_SIZE - nRcvPos));
				if(nRcvLen <= 0)
				{
					objSocket.close();
					objSocket = null;
					System.out.println("UC Server Closed the socket");
					break;
				}
				//CommUtil.printMsg(cBuff, nRcvLen+nRcvPos);
				m_TestSta = 0;
				nRcvPos += nRcvLen;
				nRcvLen = 0;
				nCursor = 0;
				int nLen = 0;
				bContParse = true;
				while(bContParse)
				{
					nLen = nRcvPos - nCursor;
					if(0 >= nLen)
					{
						break;
					}					
					vectData.clear();
					vectData.insertElementAt(new Integer(nLen),0);
					vectData.insertElementAt(new Integer(nCursor),1);
					ctRslt = DeCode(cBuff, vectData);
					nLen = ((Integer)vectData.get(0)).intValue();					
					switch(ctRslt)
					{
						case Cmd_Sta.CODEC_CMD:
							byte [] Resp = ((byte[])vectData.get(1));
							if(null != Resp && Resp.length > 0)
							{
								SetSendMsgList(Resp);
							}
							data = (byte[])vectData.get(2);
							if(null != data && data.length > Cmd_Sta.CONST_MSGHDRLEN)
							{
								//SetRecvMsgList(data);
								//接收
					//			m_DBUtil.TPC(new String(data, 20, 28) + new String(data, 136, data.length-136));
								//回应             
								if(SetSendMsg(new String(data, 20, 28), 2))
								{
									
								}
							}
							nCursor += nLen;
							break;
						case Cmd_Sta.CODEC_RESP:
							nCursor += nLen;
							break;
						case Cmd_Sta.CODEC_NEED_DATA:
							bContParse = false;
							break;					
						case Cmd_Sta.CODEC_ERR:
							nRcvPos = 0;
							bContParse = false;
							break;
						default:
							break;
					}
				}//bContParse
				if(0 != nRcvPos)
				{
					System.arraycopy(cBuff, nCursor, cBuff, 0, nRcvPos - nCursor);
					nRcvPos -= nCursor;
				}
			}
			catch(SocketException ex)
			{
				System.out.println("RevThread SocketException Exp:"+ex.getMessage());
				ex.printStackTrace();
				try
				{
					if(null != objSocket)
					{
						objSocket.close();
						objSocket = null;
					}
				}catch(Exception e)
				{}
				break;
			}
			catch(Exception exp)
			{
				exp.printStackTrace();
			}		
		}//while
		
	}

	private byte DeCode(byte[] pMsg, Vector<Object> data)
	{
		byte RetVal = Cmd_Sta.CODEC_ERR;
		int nUsed = ((Integer)data.get(0)).intValue();//现有的数据长度
		int nCursor = ((Integer)data.get(1)).intValue();//从什么地方开始
		try
		{
			DataInputStream DinStream= new DataInputStream(new ByteArrayInputStream(pMsg));
			if(nUsed < (int)Cmd_Sta.CONST_MSGHDRLEN )
			{
				return Cmd_Sta.CODEC_NEED_DATA;
			}
			DinStream.skip(nCursor); 

			int unMsgLen = CommUtil.converseInt(DinStream.readInt());
			int unMsgCode = CommUtil.converseInt(DinStream.readInt());
			int unStatus = CommUtil.converseInt(DinStream.readInt());
			int unMsgSeq = CommUtil.converseInt(DinStream.readInt());
			int unReserve = CommUtil.converseInt(DinStream.readInt());

			if (unMsgLen < Cmd_Sta.CONST_MSGHDRLEN || unMsgLen > Cmd_Sta.CONST_MAX_BUFF_SIZE)
			{				
				return Cmd_Sta.CODEC_ERR;
			}
			if(nUsed < unMsgLen)
			{
				return Cmd_Sta.CODEC_NEED_DATA;
			}
	
			data.insertElementAt(new Integer(unMsgLen),0);//nUsed = unMsgLen;
			
			if((unMsgCode & Cmd_Sta.COMM_RESP) != 0)//是应答包
			{
				RetVal = Cmd_Sta.CODEC_RESP;
				return RetVal;
			}
			
			ByteArrayOutputStream boutStream = new ByteArrayOutputStream();
			DataOutputStream doutStream = new DataOutputStream(boutStream);
			// 置应答包
			doutStream.writeInt(CommUtil.converseInt(Cmd_Sta.CONST_MSGHDRLEN));
			doutStream.writeInt(CommUtil.converseInt(unMsgCode|Cmd_Sta.COMM_RESP));
			doutStream.writeInt(CommUtil.converseInt(unStatus));//Sta
			doutStream.writeInt(CommUtil.converseInt(unMsgSeq));//seq
			doutStream.writeInt(CommUtil.converseInt(unReserve));//seq
			byte hdrMsg[] = boutStream.toByteArray();
			data.insertElementAt(hdrMsg, 1);
			DinStream.close();
			boutStream.close();
			doutStream.close();	 
    	
			switch(unMsgCode)
			{
				case Cmd_Sta.COMM_ACTIVE_TEST:
				{
					data.insertElementAt(null,2);
					RetVal = Cmd_Sta.CODEC_CMD;
					break;
				}
				case Cmd_Sta.COMM_SUBMMIT:
				{
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					DataOutputStream dout = new DataOutputStream(bout);
					dout.write(pMsg, nCursor, unMsgLen);
					data.insertElementAt(bout.toByteArray(), 2);
					dout.close();
					bout.close();	
					RetVal = Cmd_Sta.CODEC_CMD;
					break;
				}
				case Cmd_Sta.COMM_DELIVER:
				{
					data.insertElementAt(null, 2);
					RetVal = Cmd_Sta.CODEC_CMD;
					break;
				}
			  	default:
			  	{
			  		RetVal = Cmd_Sta.CODEC_ERR;	
			  		break;
			  	}
			}  	
		}
		catch (Exception exp)
		{
			exp.printStackTrace();
		}		
		
		return RetVal;
	}
}
	/**
	 * 发送线程
	 * 
	 */
private class SendThrd extends Thread
{
	private DataOutputStream SendChannel = null;
	public SendThrd(Socket pSocket)throws Exception
	{
		SendChannel = new DataOutputStream(pSocket.getOutputStream());
	}
	public void run()
	{			
		while(true)
		{		
			try
			{
				if((objSocket == null ) || objSocket.isClosed() )
				{		
					objSocket = null;
					break;
				}						
				byte[] byteData = (byte[])getSendMsgList();
				if(byteData == null)
				{
					sleep(10);
					continue;
				}
				//System.out.println("NM -> CPM:"+new String(byteData));
				SendChannel.write(byteData);
				SendChannel.flush();
			}
			catch(SocketException ex)
			{
				System.out.println("SendThread SocketException Exp:"+ex.getMessage());
				ex.printStackTrace();
				try
				{
					if(null != objSocket)
					{
						objSocket.close();
						objSocket = null;
					}
				}catch(Exception e)
				{}
				break;
			}
			catch(Exception ex)
			{
				System.out.println("SendThread Exception Exp:"+ex.getMessage());
				ex.printStackTrace();
			}					
		}				
	} //SendThrdRun		
	}//SendThrd	
}
