package util;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnPool
{
	public static final int DATABASE_ORACLE			= 1001;
	public static final int DATABASE_SQLSERVER		= 2001;
	public static final int DATABASE_MYSQL			= 3001;
	public static final int DATABASE_DB2			= 4001;
	
	private String m_Url = null;
	private String m_Name = null;
	private String m_Pwd = null;
	private String m_dbFlag = null;
	public ConnPool(String Url, String Name, String Pwd, String dbFlag)
	{	
		m_Url = Url;
		m_Name = Name;
		m_Pwd = Pwd;
		m_dbFlag = dbFlag;
	}
	public Connection getConnection()
	{	
		Connection conn = null;
		try
		{		   
			switch(Integer.parseInt(m_dbFlag))
			{
			case DATABASE_ORACLE:
				Class.forName("oracle.jdbc.driver.OracleDriver").newInstance(); 
				break;
			case DATABASE_SQLSERVER:
				Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver").newInstance();
				break;
			case DATABASE_MYSQL:
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				break;
			case DATABASE_DB2:
				Class.forName("com.ibm.db2.jdbc.app.DB2Driver ").newInstance(); 
				break;
			default:
				return null;
			}
			conn = DriverManager.getConnection(m_Url, m_Name, m_Pwd);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}	
		return conn;
	}
}