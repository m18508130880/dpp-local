package net;

import java.util.Random;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;
import util.*;

public class APCClient extends Thread
{
	private String m_AppSvrIp = null;
	private int m_AppSvrPort = 0;
	private int m_AppSvrTimeOut = 0;
	private int m_Seq = 0;
	private Socket objSocket = null;
	
	public APCClient() throws Exception
	{
		FileInputStream file = new FileInputStream("/www/LNG-LOCAL/LNG-LOCAL-WEB/files/Config.ini");
		try
		{
			Properties prop = new Properties();
			prop.load(file);
			m_AppSvrIp = prop.getProperty("AppSvrIp");
			m_AppSvrPort = Integer.parseInt(prop.getProperty("AppSvrPort"));
			m_AppSvrTimeOut = Integer.parseInt(prop.getProperty("AppSvrTimeOut"));
			
			file.close();
			file = null;
		}
		catch(Exception ex)
		{ 
			m_AppSvrIp = "192.168.0.225";
			m_AppSvrPort = 50000;
			m_AppSvrTimeOut = 60;
		}
		finally
		{
			if(file != null)
			{
				file.close();
			}
		}
	}
	
	//生成随机数
    public String Randon() 
    {
    	String Resp = "";
	    Random rnd = new Random();
	    for(int i=1; i<5; i++) 
	    {
	        int p = rnd.nextInt(9);
	        Resp += p;
	    }	    
	    return Resp;
    }
	
	//生成序列号
	private int GetSeq()
	{
		if(m_Seq++ == 0xffffff)
			m_Seq = 0;
		return m_Seq;
	}
	
	private boolean Login()
	{
		boolean RetVal = false;
		try
		{
			objSocket = new Socket(m_AppSvrIp, m_AppSvrPort);
			if(objSocket == null)
			{
				CommUtil.PRINT("Create Client Failed!");
				return RetVal;
			}
			
			String PId = "BESTZNWL" + (new SimpleDateFormat("yyyyMMddHHmmssss")).format(new Date()).substring(8) + Randon();
			String CTime = (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date());
			String Pwd = "BEST8530";
			String Md5_IN = CommUtil.StrRightFillSpace(PId, 20) + CTime + Pwd;
			String Md5_OUT = (CommUtil.BytesToHexString(new Md5().encrypt((Md5_IN).getBytes()), 16)).toUpperCase();	
			String LoginData = "0000" + CommUtil.StrRightFillSpace(PId, 20) + CTime + Md5_OUT;
			
			ByteArrayOutputStream boutStream = new ByteArrayOutputStream();
			DataOutputStream doutStream = new DataOutputStream(boutStream);
			doutStream.writeInt(CommUtil.converseInt(Cmd_Sta.CONST_MSGHDRLEN + LoginData.length()));
			doutStream.writeInt(CommUtil.converseInt(Cmd_Sta.COMM_LOGON));//连接
			doutStream.writeInt(CommUtil.converseInt(0));
			doutStream.writeInt(CommUtil.converseInt(GetSeq()));
			doutStream.writeInt(CommUtil.converseInt(0));		
			doutStream.writeBytes(LoginData);
			
			byte[]byteData = boutStream.toByteArray();
			DataOutputStream SendChannel = null;
			SendChannel = new DataOutputStream(objSocket.getOutputStream());
			SendChannel.write(byteData);
			SendChannel.flush();
			
	    	DataInputStream RecvChannlLogin = new DataInputStream(objSocket.getInputStream());
	    	objSocket.setSoTimeout(20000);
			int iRecvLen = RecvChannlLogin.read(byteData);
			if(iRecvLen >= 20)
			{
				String RespStatus = new String(byteData).substring(20,24);
				if(RespStatus.equals("0000"))
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
	
	
	public int ExecCmd(String pData) throws Exception
	{
		int RetVal = Cmd_Sta.STA_UNKHOWN_ERROR;
		try 
		{
			if(Login())
			{
				RetVal = Send(pData);
				System.out.println("RetVal:"+RetVal);
			}
		}
		catch(Exception Ex)
		{
			Ex.printStackTrace();
			RetVal = Cmd_Sta.STA_NET_ERROR;
		}
		finally
		{
			if(null != objSocket)
			{
				objSocket.close();
				objSocket = null;
			}
		}
		
		return RetVal;
	}
	
	public int Send(String pData) throws Exception
	{
		int RetVal = Cmd_Sta.STA_UNKHOWN_ERROR;
		Vector<Object> data = new Vector<Object>();
		int nRecvLen = 0;
		int nRcvPos = 0;
		int nCursor = 0;
		byte ctRslt = 0;
		boolean bContParse = true;
		byte[] cBuff = new byte[2048];
		boolean Flag = true;
		try 
		{    
			DataOutputStream SendChannel = new DataOutputStream(objSocket.getOutputStream());
			DataInputStream RecvChannel = new DataInputStream(objSocket.getInputStream());
			
			System.out.println("WEBSvr -> AppSvr ["+ pData +"]");
			SendChannel.write(EnCode(pData.getBytes()));
			SendChannel.flush();

			objSocket.setSoTimeout(m_AppSvrTimeOut*1000);		
			while(Flag)
			{
				try
				{				
					if(null == objSocket || objSocket.isClosed())
					{
						objSocket.close();
						break;
					}
					
					nRecvLen = RecvChannel.read(cBuff, 0, cBuff.length);
					if(nRecvLen <= 0)
					{ 
						objSocket.close();
						break;
					}
					
					nRcvPos += nRecvLen;
					nRecvLen = 0;
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
						data.clear();
						data.insertElementAt(new Integer(nLen),0);
						data.insertElementAt(new Integer(nCursor),1);
						
						ctRslt = DeCode(cBuff, data);
						nLen = ((Integer)data.get(0)).intValue();
						switch(ctRslt)
						{
							case Cmd_Sta.CODEC_CMD:
								System.out.println("AppSvr -> WEBSvr ["+ new String(cBuff,20,28) +"]");
								String Resp = new String(cBuff,20,28).substring(20, 24);
								RetVal = Cmd_Sta.STA_SUBMIT_SUCCESS;
								/*
								if(Resp.equals("0000"))
								{
									RetVal = Cmd_Sta.STA_SUCCESS;
								}
								else if(Resp.equals("3000"))
								{
									RetVal = Cmd_Sta.STA_SUBMIT_SUCCESS;
								}
								else
								{
									RetVal = Cmd_Sta.STA_UNKHOWN_ERROR;
								}
								*/
								bContParse = false;
								Flag = false;
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
				}catch(Exception ex)
				{
					ex.printStackTrace();
					objSocket.close();
				}
			}
		}
		catch(Exception Ex)
		{
			Ex.printStackTrace();
			objSocket.close();
			RetVal = Cmd_Sta.STA_NET_ERROR;
		}
		
		return RetVal;
	}
	
	private byte[] EnCode(byte[] pData)
	{
		byte[] byteData = null;
		ByteArrayOutputStream boutStream = new ByteArrayOutputStream();
		DataOutputStream doutStream = new DataOutputStream(boutStream);
		try
		{
			doutStream.writeInt(CommUtil.converseInt(Cmd_Sta.CONST_MSGHDRLEN + pData.length));
			doutStream.writeInt(CommUtil.converseInt(Cmd_Sta.COMM_DELIVER));	
			doutStream.writeInt(CommUtil.converseInt(0));
			doutStream.writeInt(CommUtil.converseInt(GetSeq()));
			doutStream.writeInt(CommUtil.converseInt(0));
			
			doutStream.write(pData);
			byteData = boutStream.toByteArray();
			boutStream.close();
			doutStream.close();
		}
		catch(Exception ex)
		{
			System.out.println("Error[" + pData + "]");
		}
		
		return byteData;
	}
	
	private byte DeCode(byte[] pMsg, Vector<Object> data)
	{
		byte RetVal = Cmd_Sta.CODEC_ERR;
		int nUsed = ((Integer)data.get(0)).intValue();//现有的数据长度
		int nCursor = ((Integer)data.get(1)).intValue();//从什么地方开始
		try
		{
			DataInputStream DinStream= new DataInputStream(new ByteArrayInputStream(pMsg));
			if(nUsed < (int)Cmd_Sta.CONST_MSGHDRLEN)
			{
				return Cmd_Sta.CODEC_NEED_DATA;
			}
			DinStream.skip(nCursor); 

			int unMsgLen = CommUtil.converseInt(DinStream.readInt());
			int unMsgCode = CommUtil.converseInt(DinStream.readInt());
			int unStatus = CommUtil.converseInt(DinStream.readInt());
			int unMsgSeq = CommUtil.converseInt(DinStream.readInt());
			int unReserve = CommUtil.converseInt(DinStream.readInt());
			
			byte[] BodyArray = new byte[unMsgLen - 16];
			
			if(unMsgLen < Cmd_Sta.CONST_MSGHDRLEN || unMsgLen > Cmd_Sta.CONST_MAX_BUFF_SIZE)
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
			doutStream.writeInt(CommUtil.converseInt(unReserve));//
			data.insertElementAt(boutStream.toByteArray(), 1);		
			DinStream.close();
			boutStream.close();
			doutStream.close();
			data.insertElementAt(null,2);
			RetVal = Cmd_Sta.CODEC_CMD;	
			switch(unMsgCode)
			{
			    case Cmd_Sta.COMM_SUBMMIT:
				case Cmd_Sta.COMM_DELIVER:
					System.arraycopy(pMsg, nCursor + Cmd_Sta.CONST_MSGHDRLEN, BodyArray, 0, unMsgLen - Cmd_Sta.CONST_MSGHDRLEN);
					data.insertElementAt(BodyArray, 2);
					break;
				case Cmd_Sta.COMM_ACTIVE_TEST:
					break;	
			  	default:
			  		RetVal = Cmd_Sta.CODEC_ERR;				
			}  	
		}
		catch (Exception exp)
		{
			exp.printStackTrace();
		}		
		
		return (byte)Cmd_Sta.CODEC_CMD;
	}
}
