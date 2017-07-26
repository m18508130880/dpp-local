package util;

import java.io.FileInputStream;
import java.rmi.RemoteException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;


import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


public class DBUtil
{
	private ConnPool objConnPool = null;
	private ConnPool svrConnPool = null;
	
	private String m_Url  = "";
	private String m_Name = "";
	private String m_Pwd  = "";
	private String m_Flag = "";
	
	private String s_Url  = "";
	private String s_Name = "";
	private String s_Pwd  = "";
	private String s_Flag = "";
	
	public String _Status = "0";
	
	public DBUtil() throws RemoteException
	{
	}

	public boolean init()
	{
		boolean ret = false;
		try
		{
			SAXReader reader = new SAXReader();
			Document document = reader.read(new FileInputStream("Config.xml"));
			Element root = document.getRootElement();//取得根节点
			
			m_Url  = root.element("db").element("url").getText();
			m_Name = root.element("db").element("id").getText();
			m_Pwd  = root.element("db").element("pwd").getText();
			m_Flag = root.element("db").element("flag").getText();
			
			s_Url  = root.element("sqlser").element("url").getText();
			s_Name = root.element("sqlser").element("id").getText();
			s_Pwd  = root.element("sqlser").element("pwd").getText();
			s_Flag = root.element("sqlser").element("flag").getText();
			_Status= root.element("sqlser").element("status").getText();
			
			//MySql
			objConnPool = new ConnPool(m_Url, m_Name, m_Pwd, m_Flag);
			
			//SqlSvr
			if(_Status.equals("1"))
				svrConnPool = new ConnPool(s_Url, s_Name, s_Pwd, s_Flag);
			
			ret = true;
		}	
		catch (Exception sqlExp)
		{
			sqlExp.printStackTrace();
		}
		return ret;
	}
	
	public String APC(String strReauest)
	{
		String rslt = null;
		Connection conn = null;
		CallableStatement cstat = null;
		try
		{
			conn = objConnPool.getConnection();
			if(null != conn)
			{
				conn.setAutoCommit(false);
				cstat = conn.prepareCall("{? = call APC(?)}");
				cstat.setString(2, strReauest);
				cstat.registerOutParameter(1, java.sql.Types.VARCHAR);
				cstat.execute();
				rslt = cstat.getString(1);
				conn.commit();	
			}
		}
		catch(SQLException ex)
		{
			ex.printStackTrace();
			return  CommUtil.IntToStringLeftFillSpace(Cmd_Sta.STA_UNKHOWN_ERROR, 4);
		}
		finally
		{
			try
			{
				if(null != cstat)
				{
					cstat.close();
					cstat = null;					
				}
				if(null != conn)
				{
					conn.close();
					conn = null;	
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		CommUtil.PRINT("DB_IN["+strReauest+"] DB_OUT[" + rslt + "]");	
		return rslt;
	}
	
	public  boolean doUpdate(String pSql)
	{
		boolean IsOK = false;
		Connection conn = null;
		PreparedStatement pStmt = null;
		try 
		{
			conn = objConnPool.getConnection();
			conn.setAutoCommit(false);
			pStmt = conn.prepareStatement(pSql);
			if (pStmt.executeUpdate() > 0) 
			{
				IsOK = true;
				conn.commit();
			} 
		} 
		catch (SQLException sqlExp) 
		{
			sqlExp.printStackTrace();
		}
		finally
		{
			try
			{
				if(pStmt != null)
				{				
					pStmt.close();
					pStmt = null;
				}
				if(conn != null)
				{
					conn.close();	
					conn = null;
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		return IsOK;
	}
	
	
	public String getM_Url() {
		return m_Url;
	}

	public void setM_Url(String mUrl) {
		m_Url = mUrl;
	}

	public String getM_Name() {
		return m_Name;
	}

	public void setM_Name(String mName) {
		m_Name = mName;
	}

	public String getM_Pwd() {
		return m_Pwd;
	}

	public void setM_Pwd(String mPwd) {
		m_Pwd = mPwd;
	}

	public String getM_Flag() {
		return m_Flag;
	}

	public void setM_Flag(String mFlag) {
		m_Flag = mFlag;
	}

	public String getS_Url() {
		return s_Url;
	}

	public void setS_Url(String sUrl) {
		s_Url = sUrl;
	}

	public String getS_Name() {
		return s_Name;
	}

	public void setS_Name(String sName) {
		s_Name = sName;
	}

	public String getS_Pwd() {
		return s_Pwd;
	}

	public void setS_Pwd(String sPwd) {
		s_Pwd = sPwd;
	}

	public String getS_Flag() {
		return s_Flag;
	}

	public void setS_Flag(String sFlag) {
		s_Flag = sFlag;
	}
}