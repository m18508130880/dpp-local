package net;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import util.*;
import bean.*;

public class AlertCtrl extends Thread
{	
	private DBUtil m_DBUtil = null;
	private BaseCmdBean m_CmdBean = null;
	private EquipInfoBean m_EquipInfo = null;
	
	private int m_Seq = (int)new Date().getTime();

	ArrayList<EquipInfoBean> equipInfo = null;
	@SuppressWarnings("rawtypes")
	public Hashtable<String, ArrayList> objGJGXTable = null;
	
	public AlertCtrl(DBUtil dbUtil)throws Exception
	{	    
		m_DBUtil = dbUtil;
	}
	
	public boolean Initialize()
	{
		boolean Is_OK = false;
		try
		{
			m_CmdBean = new UpdateDataBean(Cmd_Sta.CMD_SUBMIT_2001, getSeq());
			equipInfo = new ArrayList<EquipInfoBean>();
			m_EquipInfo = new EquipInfoBean();

			m_CmdBean.execRequest(this);
			//this.start();
			Is_OK = true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return Is_OK;
	}
	
	@SuppressWarnings({"rawtypes" })
	public void run()
	{
		int i = 0;
		while (true)
		{
			try
			{
				objGJGXTable = m_CmdBean.getObjGJGXTable();
				//遍历key
				Enumeration e = objGJGXTable.keys();
				while(e.hasMoreElements())
				{
					String projectAndSysId = String.valueOf(e.nextElement());
					if(projectAndSysId.contains("YJ"))
					{
						m_EquipInfo.doAlert(this, projectAndSysId, objGJGXTable);
					}
				}
//				for(Entry<String, ArrayList> entry : objGJGXTable.entrySet())
//				{
//					m_EquipInfo.doAlert(this, entry.getKey(), entry.getValue());
//			        //System.out.println(entry.getKey() + entry.getValue());  
//			    }  
				CommUtil.PRINT("循环次数["+(i ++)+"] Time["+CommUtil.getDateTime()+"]");
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				continue;
			}
			try
			{
				sleep(300000);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//while
	}
	
	public void doAlert(String projectAndSysId)
	{
		objGJGXTable = m_CmdBean.getObjGJGXTable();
		m_EquipInfo.doAlert(this, projectAndSysId, objGJGXTable);
	}
	
	public synchronized String getSeq()
	{
		if(m_Seq == 0xffffffff)
			m_Seq = 0;
		else
			m_Seq++;
		return CommUtil.IntToStringLeftFillZero(m_Seq, 20);
	}
	public DBUtil getM_DBUtil() {
		return m_DBUtil;
	}
	public void setM_DBUtil(DBUtil m_DBUtil) {
		this.m_DBUtil = m_DBUtil;
	}

	public BaseCmdBean getM_CmdBean()
	{
		return m_CmdBean;
	}

	public void setM_CmdBean(BaseCmdBean m_CmdBean)
	{
		this.m_CmdBean = m_CmdBean;
	}
}