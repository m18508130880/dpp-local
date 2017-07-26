package net;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import util.*;
import bean.*;

public class AlertCtrl extends Thread
{	
	private DBUtil m_DBUtil = null;

	ArrayList<DevGJAlertBean> gjList = null;
	ArrayList<DevGXAlertBean> gxList = null;
	ArrayList<EquipInfoBean> equipInfo = null;
	@SuppressWarnings("rawtypes")
	public Hashtable<String, ArrayList> objActionTable = null;
	
	public AlertCtrl(DBUtil dbUtil)throws Exception
	{	    
		m_DBUtil = dbUtil;
		gjList = new ArrayList<DevGJAlertBean>();
		gxList = new ArrayList<DevGXAlertBean>();
		equipInfo = new ArrayList<EquipInfoBean>();
	} 
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean Initialize()
	{
		boolean Is_OK = false;
		objActionTable = new Hashtable<String, ArrayList>();
		String Sql = "";
		try
		{
			Sql = " SELECT project_id, LEFT(id,5) FROM dev_gj WHERE LENGTH(equip_id) > 1 GROUP BY LEFT(id,5)";
			String[] Project_SysId = this.getM_DBUtil().doSelectStr(Sql, 2).split(";");
			for(int i = 0; i < Project_SysId.length; i ++)
			{
				String project_Id = Project_SysId[i].split(",")[0];
				String SysId = Project_SysId[i].split(",")[1];
				Sql = " SELECT id, in_id, out_id, curr_data, flag FROM view_dev_gj WHERE Project_Id = '"+project_Id+"' AND id LIKE '"+SysId+"%' GROUP BY id";
				gjList = (ArrayList<DevGJAlertBean>) this.getM_DBUtil().doSelect(Sql, 1);
				objActionTable.put(project_Id + SysId, gjList);
				SysId = dealGXID(SysId);
				Sql = " SELECT id, length, end_id FROM view_dev_gx WHERE Project_Id = '"+project_Id+"' AND id LIKE '"+SysId+"%' GROUP BY id";
				gxList = (ArrayList<DevGXAlertBean>) this.getM_DBUtil().doSelect(Sql, 2);
				objActionTable.put(project_Id + SysId, gxList);
			}
			this.start();
			Is_OK = true;
		}
		catch(Exception e)
		{
			Is_OK = false;
			e.printStackTrace();
		}
		return Is_OK;
	}
	
	@SuppressWarnings("unchecked")
	public void run()
	{
		String Sql = "";
		int i = 0;
		while (true)
		{
			
			try
			{
				Sql = " SELECT tid, pid, CNAME, Project_Id, g_id, CTIME,VALUE,Top_Height, Base_Height,equip_height FROM view_equip_info GROUP BY tid";
				equipInfo = (ArrayList<EquipInfoBean>) this.getM_DBUtil().doSelect(Sql, 3);
				if(equipInfo != null)
				{
					Iterator<?> iterator = equipInfo.iterator();
					String Sys_Id = "#";
					while (iterator.hasNext())
					{
						EquipInfoBean bean = (EquipInfoBean) iterator.next();
						if(equipStuats(bean))	// 判断是否为最新数据
						{
							overflow(bean); 	// 溢流分析
							blockingOne(bean);	// 阻塞分析 单个设备
							if(!Sys_Id.contains(bean.getGJ_Id().substring(0, 5)))	// 判断子系统计算过，则不计算
							{
								Sys_Id += blockingAll(bean, equipInfo);	// 阻塞分析 多个设备
							}
						}
					}
				}
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
	
	/*
	 * 设备状态，若超过两个小时不传数据，则设备出现问题，需检查
	 */
	public boolean equipStuats(EquipInfoBean pEquipInfo)
	{
		CommUtil.PRINT("-----故障检测--["+pEquipInfo.getTid()+"]["+pEquipInfo.getGJ_Id()+"]---");
		boolean IsOK = false;
		int hour = 0;
		String Sql = "";
		hour = CommUtil.getHoursBetween(pEquipInfo.getCTime(), CommUtil.getDateTime());
		Sql = " select count(sn) from alert_info " + 
				" where cpm_id = '"+pEquipInfo.getPid() + "' " +
				" and id = '" + pEquipInfo.getTid() + "' " +
				" and attr_id = '0010' " +
				" and gj_id = '" + pEquipInfo.getGJ_Id() + "' " +
				" and status = '0' ";
		String count = this.getM_DBUtil().doSelectStr(Sql, 1);
		if(hour > 2)
		{
			if(Integer.valueOf(count.substring(0, count.indexOf(","))) <= 0) // 判断是否有此条数据
			{
				Sql = " insert INTO alert_info(CPM_ID, ID, CNAME, ATTR_ID, ATTR_NAME, LEVEL, CTIME, CDATA, GJ_ID, STATUS, UNIT, DES)" + 
					  " VALUES('"+pEquipInfo.getPid()+"', '"+pEquipInfo.getTid()+"', '"+pEquipInfo.getCName()+"', '0010', '设备故障', '"+
					  " "+"', '"+pEquipInfo.getCTime()+"', '"+pEquipInfo.getValue()+"', '"+pEquipInfo.getGJ_Id()+"', '0', '单位', '超过两小时未上传数据')";
				if(this.getM_DBUtil().doUpdate(Sql))
				{
					CommUtil.PRINT("设备[" + pEquipInfo.getTid() + "]["+pEquipInfo.getGJ_Id()+"]超过两小时未上传数据！");
				}
			}
		}
		else
		{
			if(Integer.valueOf(count.substring(0, count.indexOf(","))) > 0) // 判断是否有此条数据
			{
				Sql = " update alert_info set status = '1' " + 
						" where cpm_id = '"+pEquipInfo.getPid() + "' " +
						" and id = '" + pEquipInfo.getTid() + "' " +
						" and attr_id = '0010' " +
						" and gj_id = '" + pEquipInfo.getGJ_Id() + "' ";
				if(this.getM_DBUtil().doUpdate(Sql))
				{
					CommUtil.PRINT("设备[" + pEquipInfo.getTid() + "]["+pEquipInfo.getGJ_Id()+"]开始上传数据！");
				}
			}
			IsOK = true;
		}
		return IsOK;
	}
	
	/*
	 * 溢流分析
	 */
	public void overflow(EquipInfoBean pEquipInfo)
	{
		CommUtil.PRINT("-----溢流检测--["+pEquipInfo.getTid()+"]["+pEquipInfo.getGJ_Id()+"]---");
		String Sql = " SELECT equip_height, value FROM view_equip_info WHERE tid = '"+pEquipInfo.getTid()+"' AND pid = '" + pEquipInfo.getPid() + "' GROUP BY tid";
		String data = this.getM_DBUtil().doSelectStr(Sql, 2).split(";")[0];
		float Equip_Height = Float.parseFloat(data.split(",")[0]);
		float Value = Float.parseFloat(data.split(",")[1]);
		float pValue = (float)(Math.round((Equip_Height-Value)*100))/100;
		if(Equip_Height - Value <= 0.3)
		{
			Sql = " insert INTO alert_info(CPM_ID, ID, CNAME, ATTR_ID, ATTR_NAME, LEVEL, CTIME, CDATA, GJ_ID, STATUS, UNIT, DES)" + 
				  " VALUES('"+pEquipInfo.getPid()+"', '"+pEquipInfo.getTid()+"', '"+pEquipInfo.getCName()+"', '0011', '溢流', '"+
				  " "+"', '"+pEquipInfo.getCTime()+"', '"+pEquipInfo.getValue()+"', '"+pEquipInfo.getGJ_Id()+"', '0', '单位', '离地面"+pValue+"米')";
			if(this.getM_DBUtil().doUpdate(Sql))
			{
				CommUtil.PRINT("管井[" + pEquipInfo.getGJ_Id() + "]溢流！当前水深["+Value+"]离地面["+pValue+"米]");
			}
		}
	}
	
	/*
	 * 阻塞分析 一个设备
	 */
	public void blockingOne(EquipInfoBean pEquipInfo)
	{
		CommUtil.PRINT("-----阻塞检测--["+pEquipInfo.getTid()+"]["+pEquipInfo.getGJ_Id()+"]--单个--");
		String Sql = " SELECT AVG(VALUE) FROM DATA WHERE cpm_id = '"+pEquipInfo.getTid()+"' AND id = '"+pEquipInfo.getPid()+"' AND CTIME >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)";
		float avg_30Day = Float.valueOf(this.getM_DBUtil().doSelectStr(Sql, 1).split(",")[0]);
		Sql = " SELECT AVG(VALUE) FROM DATA WHERE cpm_id = '"+pEquipInfo.getTid()+"' AND id = '"+pEquipInfo.getPid()+"' AND CTIME >= DATE_SUB(NOW(), INTERVAL 6 HOUR)";
		float avg_6Hour = Float.valueOf(this.getM_DBUtil().doSelectStr(Sql, 1).split(",")[0]);
		Sql = " SELECT AVG(VALUE) FROM DATA WHERE cpm_id = '"+pEquipInfo.getTid()+"' AND id = '"+pEquipInfo.getPid()+"' AND CTIME >= DATE_SUB(NOW(), INTERVAL 12 HOUR)";
		float avg_12Hour = Float.valueOf(this.getM_DBUtil().doSelectStr(Sql, 1).split(",")[0]);
		if(avg_6Hour - avg_30Day > 0.5 || avg_12Hour - avg_30Day > 0.3)
		{
			Sql = " insert INTO alert_info(CPM_ID, ID, CNAME, ATTR_ID, ATTR_NAME, LEVEL, CTIME, CDATA, GJ_ID, STATUS, UNIT, DES)"+
				  " VALUES('"+pEquipInfo.getPid()+"', '"+pEquipInfo.getTid()+"', '"+pEquipInfo.getCName()+"', '0012', '阻塞', '"+
				  " "+"', '"+pEquipInfo.getCTime()+"', '"+pEquipInfo.getValue()+"', '"+pEquipInfo.getGJ_Id()+"', '0', '单位', '阻塞')";
			if(this.getM_DBUtil().doUpdate(Sql))
			{
				CommUtil.PRINT("管井[" + pEquipInfo.getGJ_Id() + "]水位偏高，可能阻塞！当前水深["+pEquipInfo.getValue()+"]超过常水位["+(Float.valueOf(pEquipInfo.getValue())-avg_30Day)+"]米");
			}
		}
	
	}
	
	/*
	 * 阻塞分析 一个以上设备
	 * return 计算的子系统
	 */
	@SuppressWarnings("unchecked")
	public String blockingAll(EquipInfoBean pEquipInfo, ArrayList<EquipInfoBean> pEquipObj)
	{
		String projectAndSysId = "";
		String newDate = CommUtil.getDateTime();
		String Sys_Id = pEquipInfo.getGJ_Id().substring(0,5);
		ArrayList<EquipInfoBean> equipList = new ArrayList<EquipInfoBean>();
		Iterator<?> iterator = pEquipObj.iterator();
		while (iterator.hasNext())
		{
			EquipInfoBean bean = (EquipInfoBean) iterator.next();
			if(bean.getGJ_Id().contains(Sys_Id) && bean.getProject_Id().equals(pEquipInfo.getProject_Id()))
			{
				equipList.add(bean);
				projectAndSysId = bean.getProject_Id()+bean.getGJ_Id().substring(0,5);
				if(CommUtil.getHoursBetween(pEquipInfo.getCTime(), newDate) > 1) // 所有数据，在一个小时以内上传的才进行计算
				{
					return Sys_Id;
				}
			}
		}
		if(equipList.size() <= 1) 	// 多个设备才计算
		{
			return Sys_Id;
		}
		CommUtil.PRINT("-----阻塞检测--子系统["+projectAndSysId+"]--多个--");
		// 取到子系统管井
		ArrayList<DevGJAlertBean> gjList = objActionTable.get(projectAndSysId);
		// 将管井list转为hashtable
		Hashtable<String, DevGJAlertBean> objGJTable = listToHashtable(gjList, 1);
		// 管井编号变为管线编号
		projectAndSysId = dealGXID(projectAndSysId);
		// 取到子系统管线
		ArrayList<DevGXAlertBean> gxList = objActionTable.get(projectAndSysId);
		// 将管线list转为hashtable
		Hashtable<String, DevGXAlertBean> objGXTable = listToHashtable(gxList, 2);
		// 将设备list转为hashtable
		Hashtable<String, DevGXAlertBean> objEquipTable = listToHashtable(equipList, 3);
		// 开始遍历设备
		iterator = equipList.iterator();
		while (iterator.hasNext())
		{
			int option = 0; 		// 退出条件
			float allLength = 0;	// 两设备间管线总长度
			float equipSlope = 0;	// 水位的坡度
			float gjSlope = 0;		// 管井的坡度
			EquipInfoBean stratEquip = (EquipInfoBean) iterator.next();		// 开始设备井
			EquipInfoBean endEquip = null;									// 结束设备井
			String gj_Id = stratEquip.getGJ_Id();							
			do{
				DevGJAlertBean gjBean = (DevGJAlertBean) HashGet(objGJTable, gj_Id);  	// 取到第一个设备管井
				if(Float.valueOf(gjBean.getValue()) > 0)	// value>0,则证明有设备
				{
					endEquip = (EquipInfoBean) HashGet(objEquipTable, gjBean.getId());
					option = 1;			// 取到下一个设备即跳出循环
				}
				if( gjBean.getFlag().equals("2"))	// 遇到终点，还没有遇到设备，跳出循环
				{
					option = 2;
				}
				DevGXAlertBean gxBean = (DevGXAlertBean) HashGet(objGXTable, gjBean.getOut_Id());	// 根据管井出口找管线
				if(null != gxBean)
				{
					allLength += Float.valueOf(gxBean.getSize());	// 管线长度相加
					gj_Id = gxBean.getEnd_Id();		// 设置下一个管井的编号
				}
				else
				{
					option = 3;
				}
			}while(option == 0);
			if(option == 1 && allLength > 0) 	// 找到设备的正常退出
			{
				equipSlope = (Float.valueOf(stratEquip.getValue()) - Float.valueOf(endEquip.getValue())) / allLength;
				gjSlope = (Float.valueOf(stratEquip.getBase_Height()) - Float.valueOf(endEquip.getBase_Height())) / allLength;
				if(equipSlope > gjSlope*5)
				{
					String Sql = " insert INTO alert_info(CPM_ID, ID, CNAME, ATTR_ID, ATTR_NAME, LEVEL, CTIME, CDATA, GJ_ID, STATUS, UNIT, DES)"+
							     " VALUES('"+pEquipInfo.getPid()+"', '"+pEquipInfo.getTid()+"', '"+pEquipInfo.getCName()+"', '0012', '阻塞', '"+
							     " "+"', '"+pEquipInfo.getCTime()+"', '"+pEquipInfo.getValue()+"', '"+pEquipInfo.getGJ_Id()+"', '0', '单位', '水位坡度偏高，可能阻塞！水位坡度["+equipSlope+"]管井坡度["+gjSlope+"')";
						if(this.getM_DBUtil().doUpdate(Sql))
						{
							CommUtil.PRINT("管井[" + pEquipInfo.getGJ_Id() + "]水位坡度偏高，可能阻塞！水位坡度["+equipSlope+"]管井坡度["+gjSlope+"]");
						}
				}
			}
			else		// 未找到下一个设备 非正常退出
			{
				continue;
			}
		}
		
		return Sys_Id;	// 返回计算子系统
	}
	
	@SuppressWarnings("rawtypes")
	public Hashtable listToHashtable(ArrayList pList, int pBean)
	{
		switch (pBean)
		{
			case 1:
				Hashtable<String, DevGJAlertBean> objGJTable = null;
				objGJTable = new Hashtable<String, DevGJAlertBean>();
				Iterator iterGX = pList.iterator();
				while (iterGX.hasNext())
				{
					DevGJAlertBean gjBean = (DevGJAlertBean) iterGX.next();
					String gjId = gjBean.getId();
					HashPut(objGJTable, gjId, gjBean);
				}
				return objGJTable;
			case 2:
				Hashtable<String, DevGJAlertBean> objGXTable = null;
				objGXTable = new Hashtable<String, DevGJAlertBean>();
				Iterator iterGX1 = pList.iterator();
				while (iterGX1.hasNext())
				{
					DevGXAlertBean gjBean = (DevGXAlertBean) iterGX1.next();
					String gxId = gjBean.getId();
					HashPut(objGXTable, gxId, gjBean);
				}
				return objGXTable;
			case 3:
				Hashtable<String, DevGJAlertBean> objEquipTable = null;
				objEquipTable = new Hashtable<String, DevGJAlertBean>();
				Iterator iterGX11 = pList.iterator();
				while (iterGX11.hasNext())
				{
					EquipInfoBean gjBean = (EquipInfoBean) iterGX11.next();
					String gjId = gjBean.getGJ_Id();
					HashPut(objEquipTable, gjId, gjBean);
				}
				return objEquipTable;
			default:
				return null;
		}
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void HashPut(Hashtable hashTable, String key, Object obj)
	{
		if (hashTable.containsKey(key))
		{
			hashTable.remove(key); // 在哈希表里移除客户端
		}
		hashTable.put(key, obj);
	}

	@SuppressWarnings({ "rawtypes" })
	public Object HashGet(Hashtable hashTable, String key)
	{
		if (!hashTable.isEmpty() && hashTable.containsKey(key))
		{
			return hashTable.get(key);
		}
		return null;
	}
	/**
	 * 管线命名
	 * 
	 * @param GJ_Id
	 * @return
	 */
	public String dealGXID(String GJ_Id)
	{
		String temGJ_Id = ""; // "WJ", "WG" "YJ", "YG"
		if (GJ_Id.contains("WJ"))
		{
			temGJ_Id = GJ_Id.replace("WJ", "WG");
		}
		if (GJ_Id.contains("YJ"))
		{
			temGJ_Id = GJ_Id.replace("YJ", "YG");
		}
		return temGJ_Id;
	}

	public DBUtil getM_DBUtil() {
		return m_DBUtil;
	}
	public void setM_DBUtil(DBUtil m_DBUtil) {
		this.m_DBUtil = m_DBUtil;
	}
}