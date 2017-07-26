package util;

import java.io.FileInputStream;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class DBUtil
{	
	public ConnPool objConnPool = null;
	private String m_Url = "";
	private String m_Name = "";
	private String m_Pwd = "";
	private String m_Flag = "";
	public DBUtil() throws RemoteException
	{
	}
	
	public boolean init()
	{
		boolean ret = false;
		try
		{
			Properties prop = new Properties();
			prop.load(new FileInputStream("Config.ini"));
			m_Url = prop.getProperty("DBUrl");
			m_Name = prop.getProperty("DBName");
			m_Pwd = prop.getProperty("DBPass");
			m_Flag = prop.getProperty("Flag");
			
			objConnPool = new ConnPool(m_Url, m_Name, m_Pwd, m_Flag);
			DBTest();
			ret = true;
		}	
		catch (Exception sqlExp)
		{
			sqlExp.printStackTrace();
		}
		return ret;
	}
	
	public String DBTest()
	{
		System.out.println("ÄãºÃ!");
		String rslt = "";	
		String sql = "select * from user_info";
		Connection conn = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		try
		{
			conn = objConnPool.getConnection();
			conn.setAutoCommit(false);
			pStmt = conn.prepareStatement(sql);
			rs = pStmt.executeQuery();
			if(rs.next())
			{ 
				rslt = rs.getString(1);
				CommUtil.PRINT("DBTest:" + rslt);
			}	
		}
		catch(Exception ex)
		{		
			ex.printStackTrace();
		}
		finally
		{
			try
			{
				if(null != rs)
				{
					rs.close();
					rs = null;					
				}
				if(null != pStmt)
				{
					pStmt.close();
					pStmt = null;					
				}
				if(null != conn)
				{
					conn.close();
					conn = null;	
				}
			}catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		return rslt;
	}
	
	public String TPC(String strReauest)
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
				cstat = conn.prepareCall("{? = call TPC(?)}");
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
			return  CommUtil.IntToStringLeftFillSpace(MsgBean.STA_FAILED, 4);
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
			}catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		CommUtil.PRINT("DB_IN["+strReauest+"] DB_OUT[" + rslt + "]");	
		return rslt;
	}
}