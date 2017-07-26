package DPPLOCALRMI;
 
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.rmi.registry.LocateRegistry;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import net.TPCClient;
import rmi.*;
import util.*;

public class Main extends Thread {
	private static Main m_Main = null;
	private DBUtil m_DBUtil = null;
	private String m_OnBack = "";
	private RmiImpl m_RmiImpl = null;
	
	public static void main(String[] args) 
	{
		m_Main = new Main();
		m_Main.init();
	}

	public void init() 
	{
		try 
		{
			//RMI
			Hashtable<Object, Object> env= new Hashtable<Object, Object>();
			env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");
			Properties prop = new Properties();
			prop.load(new FileInputStream("Config.ini"));
			String PROVIDER_URL = prop.getProperty("PROVIDER_URL");			
			m_OnBack = 	prop.getProperty("ON_BACK");
			env.put(Context.PROVIDER_URL,PROVIDER_URL);
			int RMIPORT = Integer.parseInt(prop.getProperty("RMIPORT"));
			Context ctx= new InitialContext(env);
			LocateRegistry.createRegistry(RMIPORT);
			m_RmiImpl = new RmiImpl();
			ctx.rebind("DPPLOCAL", m_RmiImpl);
			
			//启动TPCClient
			TPCClient pTPCClient = new TPCClient("Config.ini");
			if(!pTPCClient.init())
			{
				System.out.println("RMI_TPCClient init failed!");
			}
				
			//起点数据库
			m_DBUtil = new DBUtil();
			if(!m_DBUtil.init())
			{
				System.out.println("m_DBUtil init failed!");
			}
			
			m_RmiImpl.Init(m_DBUtil , pTPCClient);	
			
			this.start();
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run(){
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
		System.out.println("Start......");
		String inputCmd = null;
		
			while (!interrupted()) 
			{
				try 
				{
					sleep(1000);	
					if(m_OnBack.equalsIgnoreCase("true"))
						continue;
						
					BufferedReader bufReader = new BufferedReader(new InputStreamReader(System.in));
					
					inputCmd = bufReader.readLine().toLowerCase();
					if (inputCmd.equals("q")) 
					{
						System.exit(-1);
					} 
					else if (inputCmd.equals("t")) 
					{
              //m_DBUtil.SelectTest();
					} 
					} catch (Exception exp) {
	      				exp.printStackTrace();
	      			}
			}
			System.out.println(" has Started.....");
		
	}


}
