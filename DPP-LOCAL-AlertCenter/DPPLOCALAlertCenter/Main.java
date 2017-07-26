package DPPLOCALAlertCenter;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.log4j.PropertyConfigurator;

import net.*;
import util.*;

public class Main extends Thread 
{
	private static Main objMain = null;
	
	private DBUtil m_DBUtil = null;				//数据库
	private TcpClient m_TcpClient = null;		//客户端链接
	private AlertCtrl m_AlertCtrl = null;		//告警中心
	public static void main(String[] args) 
	{
		objMain = new Main();
		objMain.init();
	}

	public void init() 
	{		
		try 
		{
			PropertyConfigurator.configure("log4j.properties");//加载properties文件
			
			//数据库初始化
			m_DBUtil = new DBUtil();
			if(!m_DBUtil.init())
			{
				System.exit(-1);
			}
			//控制中心初始化
			m_AlertCtrl = new AlertCtrl(m_DBUtil);
			if(!m_AlertCtrl.Initialize())
			{
				System.out.println("m_AlertCtrl Failed======");
				System.exit(0);
			}
			//TCPClient初始化
			m_TcpClient = new TcpClient(m_DBUtil, m_AlertCtrl);
			if(!m_TcpClient.init())
			{
				System.out.println("m_TcpClient Failed======");
				System.exit(-3);
			}

			this.start();
			Runtime.getRuntime().addShutdownHook(new Thread(){
				public void run() {
					System.gc();
				}
			});
		} catch (Exception e) 
		{
			e.printStackTrace();
			Runtime.getRuntime().exit(0);
		}

	}

	public void run() 
	{
		System.out.println("Start..........................................");
		while (!interrupted()) 
		{
			try {
				sleep(1000);	
				if(true)
					continue;
				@SuppressWarnings("unused")
				String inputCmd = new BufferedReader(new InputStreamReader(System.in)).readLine().toLowerCase();
				System.out.println();
				
			} catch (Exception exp) {
				exp.printStackTrace();
			}
		}	
	}
}
