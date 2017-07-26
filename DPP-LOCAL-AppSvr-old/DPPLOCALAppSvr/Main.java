package DPPLOCALAppSvr;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.log4j.PropertyConfigurator;
import container.ActionContainer;
import net.appsvr.TcpSvrAppGateWay;
import util.*;

public class Main extends Thread 
{
	private static Main objMain = null;
	private TcpSvrAppGateWay m_PlatSvr = null;
	private DBUtil m_DBUtil = null;
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
			//指令回复容器
			if(!ActionContainer.Initialize())
			{
				System.exit(-1);
			}
			
			//数据库组件
			m_DBUtil = new DBUtil();
			if(!m_DBUtil.init())
			{
				System.exit(-1);
			}

			m_PlatSvr = new TcpSvrAppGateWay(m_DBUtil);
			if(!m_PlatSvr.Initialize())
			{
				System.out.println("m_PlatSvr Failed======");
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
		String inputCmd = null;
		boolean test = true;
		while (!interrupted()) 
		{
			try 
			{
				sleep(1000);	
				if(test)
					continue;
				BufferedReader bufReader = new BufferedReader(new InputStreamReader(System.in));
				inputCmd = bufReader.readLine().toLowerCase();
				if (inputCmd.equals("t")) 
				{
					
				}
			} 
			catch (Exception exp) 
			{
				exp.printStackTrace();
			}
		}
	}
}
