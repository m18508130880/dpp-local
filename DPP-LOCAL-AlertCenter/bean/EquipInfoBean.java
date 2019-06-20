package bean;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import util.CommUtil;
import net.AlertCtrl;

public class EquipInfoBean
{
	private String Tid;
	private String Pid;
	private String CName;
	private String Project_Id;
	private String GJ_Id;
	private String CTime;
	private String Attr_Id;
	private String Value;
	private String Top_Height;
	private String Base_Height;
	private String Equip_Height;
	
	ArrayList<EquipInfoBean> equipInfo = null;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void doAlert(AlertCtrl alertCtrl, String projectAndSysId, Hashtable GJGXTable)
	{
		String Sql = " SELECT tid, pid, CNAME, Project_Id, g_id, CTIME, attr_id, VALUE, Top_Height, Base_Height, equip_height " + 
					 " FROM view_equip_info " + 
					 " where project_id = '" + projectAndSysId.substring(0, 6) + "'" +
					 " and g_id like '" + projectAndSysId.substring(6) + "%'" +
					 " GROUP BY tid";
		equipInfo = (ArrayList<EquipInfoBean>) alertCtrl.getM_DBUtil().doSelect(Sql, 3);
		if(equipInfo != null)
		{
			String Sys_Id = "#";
			HashMap overFlowMap = new HashMap<String, EquipInfoBean>();
			Iterator<?> iterator = equipInfo.iterator();
			while (iterator.hasNext())
			{
				EquipInfoBean bean = (EquipInfoBean) iterator.next();
				if(equipStuats(bean, alertCtrl) && bean.getGJ_Id().contains("J"))	// 判断是否为最新数据
				{
					overflow(bean, alertCtrl); 	// 溢流分析
					blockingOne(bean, alertCtrl);	// 阻塞分析 单个设备
					if(!Sys_Id.contains(bean.getGJ_Id().substring(0, 5)))	// 判断子系统计算过，则不计算
					{
						Sys_Id += blockingAll_1(bean, equipInfo, alertCtrl, GJGXTable);	// 阻塞分析 多个设备
					}
					// 取到每个子系统里面最高监测水位的设备管井
					if(!overFlowMap.containsKey(bean.getGJ_Id().substring(0, 5))){
						overFlowMap.put(bean.getGJ_Id().substring(0, 5), bean);
					}else {
						EquipInfoBean equipBean = (EquipInfoBean) overFlowMap.get(bean.getGJ_Id().substring(0, 5));
						double water1 = Double.valueOf(equipBean.getTop_Height()) - Double.valueOf(equipBean.getEquip_Height()) + Double.valueOf(equipBean.getValue());
						double water2 = Double.valueOf(bean.getTop_Height()) - Double.valueOf(bean.getEquip_Height()) + Double.valueOf(bean.getValue());
						if(water1 < water2){
							overFlowMap.put(bean.getGJ_Id().substring(0, 5), bean);
						}
					}
				}
			}
			overflowAll(overFlowMap, alertCtrl, GJGXTable); // 根据每个子系统的最高监测数据，来计算子系统里面可能发生溢流的非监测管井
			avgWaterAll(overFlowMap, alertCtrl, GJGXTable); // 根据每个子系统的监测水位数据的平均值，来计算子系统里面管井的水位，管线的充满度
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void doEquipStuats(AlertCtrl alertCtrl, String projectAndSysId, Hashtable GJGXTable)
	{
		String Sql = " SELECT tid, pid, CNAME, Project_Id, g_id, CTIME,attr_id,VALUE,Top_Height, Base_Height,equip_height " + 
				" FROM view_equip_info " + 
				" where project_id = '" + projectAndSysId.substring(0, 6) + "'" +
				" and g_id like '" + projectAndSysId.substring(6) + "%'" +
				" GROUP BY tid";
		equipInfo = (ArrayList<EquipInfoBean>) alertCtrl.getM_DBUtil().doSelect(Sql, 3);
		if(equipInfo != null)
		{
			Iterator<?> iterator = equipInfo.iterator();
			while (iterator.hasNext())
			{
				EquipInfoBean bean = (EquipInfoBean) iterator.next();
				equipStuats(bean, alertCtrl);	// 判断是否为最新数据
			}
		}
	}
	
	/*
	 * 设备状态，若超过两个小时不传数据，则设备出现问题，需检查
	 */
	public boolean equipStuats(EquipInfoBean pEquipInfo, AlertCtrl alertCtrl)
	{
		CommUtil.PRINT("--equipStuats---故障检测--["+pEquipInfo.getTid()+"]["+pEquipInfo.getGJ_Id()+"]---");
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
		String count = alertCtrl.getM_DBUtil().doSelectStr(Sql, 1);
		if(hour > 2)
		{
			if(Integer.valueOf(count.substring(0, count.indexOf(","))) <= 0) // 判断是否有此条数据
			{
				Sql = " insert INTO alert_info(CPM_ID, ID, CNAME, ATTR_ID, ATTR_NAME, LEVEL, CTIME, CDATA, GJ_ID, STATUS, UNIT, DES)" + 
					  " VALUES('"+pEquipInfo.getPid()+"', '"+pEquipInfo.getTid()+"', '"+pEquipInfo.getCName()+"', '0010', '设备故障', '"+
					  " "+"', '"+CommUtil.getDateTime()+"', '"+pEquipInfo.getValue()+"', '"+pEquipInfo.getGJ_Id()+"', '0', '单位', '超过两小时未上传数据')";
				if(alertCtrl.getM_DBUtil().doUpdate(Sql))
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
				if(alertCtrl.getM_DBUtil().doUpdate(Sql))
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
	public void overflow(EquipInfoBean pEquipInfo, AlertCtrl alertCtrl)
	{
		CommUtil.PRINT("--overflow---溢流检测--["+pEquipInfo.getTid()+"]["+pEquipInfo.getGJ_Id()+"]---");
		String Sql = " SELECT equip_height, value FROM view_equip_info WHERE tid = '"+pEquipInfo.getTid()+"' AND pid = '" + pEquipInfo.getPid() + "' GROUP BY tid";
		String data = alertCtrl.getM_DBUtil().doSelectStr(Sql, 2).split(";")[0];
		float Equip_Height = Float.parseFloat(data.split(",")[0]);
		float Value = Float.parseFloat(data.split(",")[1]);
		float pValue = (float)(Math.round((Equip_Height-Value)*100))/100;
		if(Equip_Height - Value <= 0.3)
		{
			Sql = " insert INTO alert_info(CPM_ID, ID, CNAME, ATTR_ID, ATTR_NAME, LEVEL, CTIME, CDATA, GJ_ID, STATUS, UNIT, DES)" + 
				  " VALUES('"+pEquipInfo.getPid()+"', '"+pEquipInfo.getTid()+"', '"+pEquipInfo.getCName()+"', '0011', '溢流', '"+
				  " "+"', '"+CommUtil.getDateTime()+"', '"+pEquipInfo.getValue()+"', '"+pEquipInfo.getGJ_Id()+"', '0', '单位', '离地面"+pValue+"米')";
			if(alertCtrl.getM_DBUtil().doUpdate(Sql))
			{
				CommUtil.PRINT("管井[" + pEquipInfo.getGJ_Id() + "]溢流！当前水深["+Value+"]离地面["+pValue+"米]");
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void overflowAll(HashMap<String, EquipInfoBean> overFlowMap, AlertCtrl alertCtrl, Hashtable<?, ?> objGJGXTable)
	{
		CommUtil.PRINT("--overflowAll---溢流检测,根据每个子系统的最高监测数据,来计算子系统里面可能发生溢流的非监测管井---");
		Iterator<?> iter = overFlowMap.entrySet().iterator();
		String Sql = "";
		while (iter.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry entry = (Map.Entry) iter.next();
			String sysId = (String) entry.getKey();
			EquipInfoBean pEquipInfo = (EquipInfoBean) entry.getValue();
			CommUtil.PRINT("-----溢流检测，子系统["+sysId+"]");
			// 取到子系统管井
			ArrayList<DevGJAlertBean> gjList = (ArrayList<DevGJAlertBean>) objGJGXTable.get(pEquipInfo.getProject_Id()+sysId);
			for(int i = 0; i < gjList.size(); i ++){
				DevGJAlertBean devGJAlertBean = gjList.get(i);
				double water1 = Double.valueOf(pEquipInfo.getTop_Height()) - Double.valueOf(pEquipInfo.getEquip_Height()) + Double.valueOf(pEquipInfo.getValue());
				double water2 = Double.valueOf(devGJAlertBean.getTop_Height());
				if(water2 - water1 < 0.3){
					Sql = " insert INTO alert_info(CPM_ID, ID, CNAME, ATTR_ID, ATTR_NAME, LEVEL, CTIME, CDATA, GJ_ID, STATUS, UNIT, DES)" + 
							  " VALUES('"+pEquipInfo.getPid()+"', '"+pEquipInfo.getTid()+"', '"+pEquipInfo.getCName()+"', '0011', '溢流', '"+
							  " "+"', '"+CommUtil.getDateTime()+"', '"+pEquipInfo.getValue()+"', '"+devGJAlertBean.getId()+"', '0', '单位', '计算得出离地面"+(water2 - water1)+"米')";

					if(alertCtrl.getM_DBUtil().doUpdate(Sql))
					{
						CommUtil.PRINT("管井[" + pEquipInfo.getGJ_Id() + "]溢流！计算得出离地面["+ (water2 - water1)+"米]");
					}
				}
			}
		}
	}
	
	/*
	 * 计算整个系统管井水位和管线充满度
	 */
	@SuppressWarnings("unchecked")
	public void avgWaterAll(HashMap<String, Double> avgWaterMap, AlertCtrl alertCtrl, Hashtable<?, ?> objGJGXTable)
	{
		CommUtil.PRINT("--avgWaterAll---根据子系统内所有设备的返回水位的平均值来计算出整个系统管井水位和管线充满度---");
		Iterator<?> iter = avgWaterMap.entrySet().iterator();
		String Sql = "";
		DecimalFormat df = new DecimalFormat( "#.#### "); 
		while (iter.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry entry = (Map.Entry) iter.next();
			String sysId = (String) entry.getKey();
			EquipInfoBean pEquipInfo = (EquipInfoBean) entry.getValue();
			String projectAndSysId = pEquipInfo.getProject_Id()+pEquipInfo.getGJ_Id().substring(0,5);
			CommUtil.PRINT("-----水位赋值子系统["+sysId+"]");
			String GJvalue = df.format(Double.valueOf(pEquipInfo.getValue()) + Double.valueOf(pEquipInfo.getTop_Height()) - Double.valueOf(pEquipInfo.getEquip_Height()));
			Sql = "update dev_gj set water_lev='"+GJvalue+","+pEquipInfo.getCTime()+"' where project_id='"+pEquipInfo.getProject_Id()+"' and id like '"+pEquipInfo.getGJ_Id().substring(0,5)+"%'";
			if(alertCtrl.getM_DBUtil().doUpdate(Sql))
			{
				CommUtil.PRINT("子系统[" + pEquipInfo.getGJ_Id().substring(0,5) + "]水位["+GJvalue+","+pEquipInfo.getCTime()+"]");
			}
			// 管井编号变为管线编号
			projectAndSysId = dealGXID(projectAndSysId);
			// 取到子系统管线
			ArrayList<DevGXAlertBean> gxList = (ArrayList<DevGXAlertBean>) objGJGXTable.get(projectAndSysId);
			for(int i = 0; i < gxList.size(); i ++){
				DevGXAlertBean devGXAlertBean = gxList.get(i);
				double waterGX = Double.valueOf(GJvalue) - Double.valueOf(devGXAlertBean.getEnd_Height());
				double depth = 0;
				if(waterGX > 0){
					depth = (waterGX*1000)/Double.valueOf(devGXAlertBean.getDiameter());
				}
				//保留两位小数且不用科学计数法，并使用千分位 
				String value = df.format(depth);
				devGXAlertBean.setDepth(value+","+pEquipInfo.getCTime());
				Sql = "update dev_gx set depth='"+value+","+pEquipInfo.getCTime()+"' where project_id='"+pEquipInfo.getProject_Id()+"' and id='"+devGXAlertBean.getId()+"'";
				if(alertCtrl.getM_DBUtil().doUpdate(Sql))
				{
					CommUtil.PRINT("管线[" + devGXAlertBean.getId() + "]充满度["+value+","+pEquipInfo.getCTime()+"]");
				}
			}
		}
	}
	
	/*
	 * 阻塞分析 一个设备
	 */
	public void blockingOne(EquipInfoBean pEquipInfo, AlertCtrl alertCtrl)
	{
		CommUtil.PRINT("--blockingOne---阻塞检测--["+pEquipInfo.getTid()+"]["+pEquipInfo.getGJ_Id()+"]--单个--");
		String Sql = " SELECT AVG(VALUE) FROM DATA WHERE cpm_id = '"+pEquipInfo.getTid()+"' AND id = '"+pEquipInfo.getPid()+"'";
		float avg_All = Float.valueOf(alertCtrl.getM_DBUtil().doSelectStr(Sql, 1).split(",")[0]);
		Sql = " SELECT AVG(VALUE) FROM DATA WHERE cpm_id = '"+pEquipInfo.getTid()+"' AND id = '"+pEquipInfo.getPid()+"' AND CTIME >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)";
		float avg_30Day = Float.valueOf(alertCtrl.getM_DBUtil().doSelectStr(Sql, 1).split(",")[0]);
		Sql = " SELECT AVG(VALUE) FROM DATA WHERE cpm_id = '"+pEquipInfo.getTid()+"' AND id = '"+pEquipInfo.getPid()+"' AND CTIME >= DATE_SUB(NOW(), INTERVAL 6 HOUR)";
		//float avg_6Hour = Float.valueOf(alertCtrl.getM_DBUtil().doSelectStr(Sql, 1).split(",")[0]);
		Sql = " SELECT AVG(VALUE) FROM DATA WHERE cpm_id = '"+pEquipInfo.getTid()+"' AND id = '"+pEquipInfo.getPid()+"' AND CTIME >= DATE_SUB(NOW(), INTERVAL 12 HOUR)";
		float avg_12Hour = Float.valueOf(alertCtrl.getM_DBUtil().doSelectStr(Sql, 1).split(",")[0]);
//		if(avg_6Hour - avg_30Day > 0.5 || avg_12Hour - avg_30Day > 0.3)
//		{
		if(avg_12Hour - avg_All > 1)
		{
			Sql = " insert INTO alert_info(CPM_ID, ID, CNAME, ATTR_ID, ATTR_NAME, LEVEL, CTIME, CDATA, GJ_ID, STATUS, UNIT, DES)"+
				  " VALUES('"+pEquipInfo.getPid()+"', '"+pEquipInfo.getTid()+"', '"+pEquipInfo.getCName()+"', '0012', '阻塞', '"+
				  " "+"', '"+CommUtil.getDateTime()+"', '"+pEquipInfo.getValue()+"', '"+pEquipInfo.getGJ_Id()+"', '0', '单位', '超过常水位可能阻塞')";
			if(alertCtrl.getM_DBUtil().doUpdate(Sql))
			{
				CommUtil.PRINT("管井[" + pEquipInfo.getGJ_Id() + "]水位偏高，可能阻塞！当前水深["+pEquipInfo.getValue()+"]超过常水位["+(Float.valueOf(pEquipInfo.getValue())-avg_30Day)+"]米");
			}
		}
	
	}
	
	/*
	 * 阻塞分析 一个以上设备 2017-7-31更改
	 * return 计算的子系统
	 */
	@SuppressWarnings("unchecked")
	public String blockingAll_1(EquipInfoBean pEquipInfo, ArrayList<EquipInfoBean> pEquipObj, AlertCtrl alertCtrl, Hashtable<?, ?> objGJGXTable)
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
		CommUtil.PRINT("--blockingAll_1---阻塞检测--子系统["+projectAndSysId+"]--多个--");
		// 取到子系统管井
		ArrayList<DevGJAlertBean> gjList = (ArrayList<DevGJAlertBean>) objGJGXTable.get(projectAndSysId);
		// 将管井list转为hashtable
		Hashtable<String, DevGJAlertBean> objGJTable = listToHashtable(gjList, 1);
		// 管井编号变为管线编号
		projectAndSysId = dealGXID(projectAndSysId);
		// 取到子系统管线
		ArrayList<DevGXAlertBean> gxList = (ArrayList<DevGXAlertBean>) objGJGXTable.get(projectAndSysId);
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
				//gjSlope = (Float.valueOf(stratEquip.getBase_Height()) - Float.valueOf(endEquip.getBase_Height())) / allLength;
				if(Math.abs(equipSlope) > 0.001)
				{
					String Sql = " insert INTO alert_info(CPM_ID, ID, CNAME, ATTR_ID, ATTR_NAME, LEVEL, CTIME, CDATA, GJ_ID, STATUS, UNIT, DES)"+
							     " VALUES('"+pEquipInfo.getPid()+"', '"+pEquipInfo.getTid()+"', '"+pEquipInfo.getCName()+"', '0012', '阻塞', '"+
							     " "+"', '"+CommUtil.getDateTime()+"', '"+pEquipInfo.getValue()+"', '"+pEquipInfo.getGJ_Id()+"', '0', '单位', '水位坡度偏高可能阻塞！水位坡度["+equipSlope+"]管井坡度["+gjSlope+"')";
						if(alertCtrl.getM_DBUtil().doUpdate(Sql))
						{
							CommUtil.PRINT("管线[" + stratEquip.getGJ_Id() + "->"+endEquip.getGJ_Id()+"]水位坡度偏高，可能阻塞！水位坡度["+equipSlope+"]");
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
	
	/*
	 * 阻塞分析 一个以上设备
	 * return 计算的子系统
	 */
	@SuppressWarnings("unchecked")
	public String blockingAll(EquipInfoBean pEquipInfo, ArrayList<EquipInfoBean> pEquipObj, AlertCtrl alertCtrl, Hashtable<?, ?> objGJGXTable)
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
		CommUtil.PRINT("--blockingAll---阻塞检测--子系统["+projectAndSysId+"]--多个--");
		// 取到子系统管井
		ArrayList<DevGJAlertBean> gjList = (ArrayList<DevGJAlertBean>) objGJGXTable.get(projectAndSysId);
		// 将管井list转为hashtable
		Hashtable<String, DevGJAlertBean> objGJTable = listToHashtable(gjList, 1);
		// 管井编号变为管线编号
		projectAndSysId = dealGXID(projectAndSysId);
		// 取到子系统管线
		ArrayList<DevGXAlertBean> gxList = (ArrayList<DevGXAlertBean>) objGJGXTable.get(projectAndSysId);
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
							     " "+"', '"+CommUtil.getDateTime()+"', '"+pEquipInfo.getValue()+"', '"+pEquipInfo.getGJ_Id()+"', '0', '单位', '水位坡度偏高可能阻塞！水位坡度["+equipSlope+"]管井坡度["+gjSlope+"')";
						if(alertCtrl.getM_DBUtil().doUpdate(Sql))
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
	
	public boolean getData(ResultSet pRs)
	{
		boolean IsOK = true;
		try
		{
			setTid(pRs.getString(1));
			setPid(pRs.getString(2));
			setCName(pRs.getString(3));
			setProject_Id(pRs.getString(4));
			setGJ_Id(pRs.getString(5));
			setCTime(pRs.getString(6));
			setAttr_Id(pRs.getString(7));
			setValue(pRs.getString(8));
			setTop_Height(pRs.getString(9));
			setBase_Height(pRs.getString(10));
			setEquip_Height(pRs.getString(11));
		}
		catch (SQLException sqlExp)
		{
			sqlExp.printStackTrace();
		}
		return IsOK;
	}
	
	public String getTid()
	{
		return Tid;
	}
	public void setTid(String tid)
	{
		Tid = tid;
	}
	public String getPid()
	{
		return Pid;
	}
	public void setPid(String pid)
	{
		Pid = pid;
	}
	public String getCName()
	{
		return CName;
	}
	public void setCName(String cName)
	{
		CName = cName;
	}
	public String getProject_Id()
	{
		return Project_Id;
	}
	public void setProject_Id(String project_Id)
	{
		Project_Id = project_Id;
	}
	public String getGJ_Id()
	{
		return GJ_Id;
	}
	public void setGJ_Id(String gJ_Id)
	{
		GJ_Id = gJ_Id;
	}
	public String getCTime()
	{
		return CTime;
	}

	public void setCTime(String cTime)
	{
		CTime = cTime;
	}

	public String getValue()
	{
		return Value;
	}

	public void setValue(String value)
	{
		Value = value;
	}

	public String getTop_Height()
	{
		return Top_Height;
	}
	public void setTop_Height(String top_Height)
	{
		Top_Height = top_Height;
	}
	public String getBase_Height()
	{
		return Base_Height;
	}
	public void setBase_Height(String base_Height)
	{
		Base_Height = base_Height;
	}
	public String getEquip_Height()
	{
		return Equip_Height;
	}
	public void setEquip_Height(String equip_Height)
	{
		Equip_Height = equip_Height;
	}

	public String getAttr_Id() {
		return Attr_Id;
	}

	public void setAttr_Id(String attr_Id) {
		Attr_Id = attr_Id;
	}
	

}
