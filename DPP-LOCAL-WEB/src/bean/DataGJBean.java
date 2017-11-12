package bean;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import rmi.Rmi;
import rmi.RmiBean;
import util.*;

public class DataGJBean extends RmiBean 
{	
	public final static long serialVersionUID = RmiBean.RMI_DATAGJ;
	public long getClassId()
	{
		return serialVersionUID;
	}
	
	public DataGJBean()
	{
		super.className = "DataGJBean";
	}
	
	public void ExecCmd(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		if(2 == currStatus.getCmd())
			msgBean = pRmi.RmiExec(currStatus.getCmd(), this, currStatus.getCurrPage(), 25);
		else
			msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
		
		
		switch(currStatus.getCmd())
		{
		    case 0://实时数据
		    	request.getSession().setAttribute("Env_" + Sid, ((Object)msgBean.getMsg()));
		    	currStatus.setJsp("Env.jsp?Sid=" + Sid);
		    	break;
		   
		    case 2://历史数据
		    	request.getSession().setAttribute("Env_His_" + Sid, ((Object)msgBean.getMsg()));
		    	currStatus.setTotalRecord(msgBean.getCount());
		    	currStatus.setJsp("Env_His.jsp?Sid=" + Sid);
		    	break;
		}
		
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
	   	response.sendRedirect(currStatus.getJsp());
	}
	
	public void GraphData(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
//		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
//		Calendar c = Calendar.getInstance();    
//		switch(currStatus.getCmd())
//		{
//    		case 1:  //日
//    			SqlETime = df.format(c.getTime());
//    			c.add(Calendar.HOUR_OF_DAY, -24);
//    			SqlBTime = df.format(c.getTime());
//				break;			
//    		case 2:  //周
//    			SqlETime = df.format(c.getTime());
//    			c.add(Calendar.WEEK_OF_MONTH, -1);
//    			SqlBTime = df.format(c.getTime());
//				break;    		
//    		case 3:  //月
//    			SqlETime = df.format(c.getTime());
//    			c.add(Calendar.MONTH, -1);
//    			SqlBTime = df.format(c.getTime());
//    	    	break;
//    		case 4:  //年
//    			SqlETime = df.format(c.getTime());
//    			c.add(Calendar.MONTH, -1);
//    			SqlBTime = df.format(c.getTime());
//    	    	break;
//		
//		}   
		switch(currStatus.getCmd())
		{
    		case 1:  //日
    			msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
    	    	request.getSession().setAttribute("User_Graph_Curve_" + Sid, ((Object)msgBean.getMsg()));    	
    			currStatus.setJsp("User_Graph_Curve.jsp?Sid=" + Sid + "&Id=" + GJ_Id);
				break;
    		case 2:  //周
    			// 判断当月1号是星期几，若是星期五，作为第一周第一天，否则归上星期
				String pBTime = "";
				String pETime = "";
				// 若某一月有第零周：表示有跨年
				if (Week.equals("0")) // 表示跨年
				{
					Flag = true;
					Year = (Integer.parseInt(Year) - 1) + "";
					Month = "12";
					Week = "5";
				}
				System.out.println("111111["+Integer.parseInt(CommUtil.getWeekDayString(Year + "-" + CommUtil.StrLeftFillZero(Month, 2) + "-01")));
				switch (Integer.parseInt(CommUtil.getWeekDayString(Year + "-" + CommUtil.StrLeftFillZero(Month, 2) + "-01")))
				{
					case 5:// 星期天 星期五
						pBTime = CommUtil.getDateAfter(Year + "-" + CommUtil.StrLeftFillZero(Month, 2) + "-01 00:00:00", (Integer.parseInt(Week) - 1) * 7);
						pETime = CommUtil.getDateAfter(Year + "-" + CommUtil.StrLeftFillZero(Month, 2) + "-01 00:00:00", (Integer.parseInt(Week)) * 7 - 1);
						break;
					case 6:// 星期一 星期六
						pBTime = CommUtil.getDateAfter(Year + "-" + CommUtil.StrLeftFillZero(Month, 2) + "-07 00:00:00", (Integer.parseInt(Week) - 1) * 7);
						pETime = CommUtil.getDateAfter(Year + "-" + CommUtil.StrLeftFillZero(Month, 2) + "-07 00:00:00", (Integer.parseInt(Week)) * 7 - 1);
						break;
					case 0:// 星期二 星期日
						pBTime = CommUtil.getDateAfter(Year + "-" + CommUtil.StrLeftFillZero(Month, 2) + "-06 00:00:00", (Integer.parseInt(Week) - 1) * 7);
						pETime = CommUtil.getDateAfter(Year + "-" + CommUtil.StrLeftFillZero(Month, 2) + "-06 00:00:00", (Integer.parseInt(Week)) * 7 - 1);
						break;
					case 1:// 星期三 星期一
						pBTime = CommUtil.getDateAfter(Year + "-" + CommUtil.StrLeftFillZero(Month, 2) + "-05 00:00:00", (Integer.parseInt(Week) - 1) * 7);
						pETime = CommUtil.getDateAfter(Year + "-" + CommUtil.StrLeftFillZero(Month, 2) + "-05 00:00:00", (Integer.parseInt(Week)) * 7 - 1);
						break;
					case 2:// 星期四 星期二
						pBTime = CommUtil.getDateAfter(Year + "-" + CommUtil.StrLeftFillZero(Month, 2) + "-04 00:00:00", (Integer.parseInt(Week) - 1) * 7);
						pETime = CommUtil.getDateAfter(Year + "-" + CommUtil.StrLeftFillZero(Month, 2) + "-04 00:00:00", (Integer.parseInt(Week)) * 7 - 1);
						break;
					case 3:// 星期五 星期三
						pBTime = CommUtil.getDateAfter(Year + "-" + CommUtil.StrLeftFillZero(Month, 2) + "-03 00:00:00", (Integer.parseInt(Week) - 1) * 7);
						pETime = CommUtil.getDateAfter(Year + "-" + CommUtil.StrLeftFillZero(Month, 2) + "-03 00:00:00", (Integer.parseInt(Week)) * 7 - 1);
						break;
					case 4:// 星期六 星期四
						pBTime = CommUtil.getDateAfter(Year + "-" + CommUtil.StrLeftFillZero(Month, 2) + "-02 00:00:00", (Integer.parseInt(Week) - 1) * 7);
						pETime = CommUtil.getDateAfter(Year + "-" + CommUtil.StrLeftFillZero(Month, 2) + "-02 00:00:00", (Integer.parseInt(Week)) * 7 - 1);
						break;
				}
				System.out.println(Year + Month + Week);
				pBTime = pBTime.substring(0, 10) + " 00:00:00";
				pETime = pETime.substring(0, 10) + " 23:59:59";
				currStatus.setVecDate(CommUtil.getDate(pBTime, pETime));
				Flag_Year = pETime;

				if (Flag) // 如果跨年
				{
					Year = (Integer.parseInt(Year) + 1) + "";
					Month = "1";
					Week = "0";
				}
				Level = 1;
				msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 0);
				request.getSession().setAttribute("Water_Max_" + Sid, ((Object) msgBean.getMsg()));
				Level = 2;
				msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 0);
				request.getSession().setAttribute("Water_Avg_" + Sid, ((Object) msgBean.getMsg()));
				Level = 3;
				msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 0);
				request.getSession().setAttribute("Water_Min_" + Sid, ((Object) msgBean.getMsg()));
				request.getSession().setAttribute("Year_" + Sid, Year);
				request.getSession().setAttribute("Month_" + Sid, Month);
				request.getSession().setAttribute("Week_" + Sid, Week);
				currStatus.setJsp("User_Graph_Curve_W.jsp?Sid=" + Sid + "&Id=" + GJ_Id);
				break;
    		case 3:  //月
    			pBTime = Year + "-" + Month + "-01 00:00:00";
				pETime = Year + "-" + (Integer.valueOf(Month) + 1) + "-01 00:00:00";
				currStatus.setVecDate(CommUtil.getDate(pBTime, pETime));
				Level = 1;
				msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 0);
				request.getSession().setAttribute("Water_Max_" + Sid, ((Object) msgBean.getMsg()));
				Level = 2;
				msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 0);
				request.getSession().setAttribute("Water_Avg_" + Sid, ((Object) msgBean.getMsg()));
				Level = 3;
				msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 0);
				request.getSession().setAttribute("Water_Min_" + Sid, ((Object) msgBean.getMsg()));
				request.getSession().setAttribute("Year_" + Sid, Year);
				request.getSession().setAttribute("Month_" + Sid, Month);
				request.getSession().setAttribute("Week_" + Sid, Week);
				currStatus.setJsp("User_Graph_Curve_M.jsp?Sid=" + Sid + "&Id=" + GJ_Id);
				break;
    		case 4:  //年
    			Level = 1;
				msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 0);
				request.getSession().setAttribute("Water_Max_" + Sid, ((Object) msgBean.getMsg()));
				Level = 2;
				msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 0);
				request.getSession().setAttribute("Water_Avg_" + Sid, ((Object) msgBean.getMsg()));
				Level = 3;
				msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 0);
				request.getSession().setAttribute("Water_Min_" + Sid, ((Object) msgBean.getMsg()));
				request.getSession().setAttribute("Year_" + Sid, Year);
				request.getSession().setAttribute("Month_" + Sid, Month);
				request.getSession().setAttribute("Week_" + Sid, Week);
				currStatus.setJsp("User_Graph_Curve_Y.jsp?Sid=" + Sid + "&Id=" + GJ_Id);
				break;
    		case 5: //比较
    			SqlBTime = currStatus.getVecDate().get(0).toString();
    			SqlETime = currStatus.getVecDate().get(1).toString();
    			pBTime = SqlBTime + " 00:00:00";
				pETime = SqlBTime + " 23:59:59";
				currStatus.setVecDate(CommUtil.getDate(pBTime, pETime));
    			msgBean = pRmi.RmiExec(1, this, 0, 25);
    	    	request.getSession().setAttribute("Compare_One_" + Sid, ((Object)msgBean.getMsg()));
    	    	
    	    	pBTime = SqlETime + " 00:00:00";
				pETime = SqlETime + " 23:59:59";
				currStatus.setVecDate(CommUtil.getDate(pBTime, pETime));
    			msgBean = pRmi.RmiExec(1, this, 0, 25);
    	    	request.getSession().setAttribute("Compare_Two_" + Sid, ((Object)msgBean.getMsg()));
    	    	
    	    	currStatus.setVecDate(CommUtil.getDate(SqlBTime, SqlETime));
    			currStatus.setJsp("User_Graph_Curve_L.jsp?Sid=" + Sid + "&Id=" + GJ_Id);
    			break;
		
		}
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
	   	response.sendRedirect(currStatus.getJsp());
	}
	
	/**
	 * 模拟计算管井水位折线图
	 * @param request
	 * @param response
	 * @param pRmi
	 * @param pFromZone
	 * @throws ServletException
	 * @throws IOException
	 */
	public void AnalogGraph(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		//SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		//Calendar c = Calendar.getInstance();    
	/*	switch(currStatus.getCmd())
		{
    		case 4:  //最近二十四小时折线图   			
    			SqlETime = df.format(c.getTime());
    			c.add(Calendar.HOUR_OF_DAY, -24);
    			SqlBTime = df.format(c.getTime());
				break;			
    		case 5:  //最近一周折线图   			
    			SqlETime = df.format(c.getTime());
    			c.add(Calendar.WEEK_OF_MONTH, -1);
    			SqlBTime = df.format(c.getTime());
				break;    		
    		case 6:  //最近一月折线图	
    			SqlETime = df.format(c.getTime());
    			c.add(Calendar.MONTH, -1);
    			SqlBTime = df.format(c.getTime());
    	    	break;
		
		}*/

		//msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
		AnalogBean analog = new AnalogBean();
		String WaterAccGJ = "";
		if(GJ_Id.substring(0,2).equals("YJ")){
			WaterAccGJ = analog.AnalogWaterAccGj(currStatus.getFunc_Project_Id() + "_" + GJ_Id, Double.parseDouble(pSimu));
		}else
		{
			WaterAccGJ = analog.AnalogSewageAccGj(currStatus.getFunc_Project_Id() + "_" + GJ_Id, Double.parseDouble(pSimu));
		}
    	request.getSession().setAttribute("Analog_Graph_Curve_" + Sid, WaterAccGJ);	
    	
		currStatus.setJsp("Analog_Graph_Curve.jsp?Sid=" + Sid + "&GJ_Id=" + GJ_Id);				
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
	   	response.sendRedirect(currStatus.getJsp());
	}
	
	public void HistoryData(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		PrintWriter output = null;
	    try
	    {
	    	getHtmlData(request);
	    	currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
			currStatus.getHtmlData(request, pFromZone);
			
	    	System.out.println("PageSize[" + PageSize + "] PageNum[" + PageNum + "]");

	    	List<Object> CData = new ArrayList<Object>();
    		//根据Level查找管辖的站点
			msgBean = pRmi.RmiExec(0, this, Integer.parseInt(PageNum), Integer.parseInt(PageSize));
			if(msgBean.getStatus() == MsgBean.STA_SUCCESS)
			{
				if(null != msgBean.getMsg())
				{
					ArrayList<?> msgList = (ArrayList<?>)msgBean.getMsg();
					if(msgList.size() > 0)
					{						
						Iterator<?> iterator = msgList.iterator();
						while(iterator.hasNext())
						{
							DataGJBean dataBean = (DataGJBean)iterator.next();
							DataGJReal RealJson = new DataGJReal();
							RealJson.setGj_id(dataBean.getGJ_Id());
							RealJson.setTop_height(dataBean.getTop_Height()); 
							RealJson.setBase_height(dataBean.getBase_Height());
							RealJson.setMaterial(dataBean.getMaterial());
							RealJson.setValue(dataBean.getValue());
							CData.add(RealJson);
						}
					}
				}
			}
			Map <String, Object> jsonMap = new HashMap<String, Object>();
			jsonMap.put("total", msgBean.getCount());
			jsonMap.put("rows", CData);			
			
			JSONObject jsonObj = JSONObject.fromObject(jsonMap);
	    	output = response.getWriter();
	    	output.write(jsonObj.toString());
	    	output.flush();	    	
	    	System.out.println("GJNowData[" + jsonObj.toString() + "]");
	    }
	    catch (IOException e)
	    {
	    	e.printStackTrace();
	    }
	    finally
	    {
	    	output.close();
	    }
	}	

	public String getSql(int pCmd)
	{
		String Sql = "";
		switch (pCmd)
		{
			case 0://实时数据 (GIS表格)
				Sql = " select '' AS sn, t.project_id, t.project_name, t.gj_id, t.gj_name, t.attr_name, t.ctime, t.value, t.unit, t.lev, t.des, t.top_height, t.base_height, t.material" +
					  " FROM view_data_now_gj t " +
					  " where t.project_id = "+ currStatus.getFunc_Project_Id() + 
					  " and t.gj_id like '%" + currStatus.getFunc_Sub_Type_Id() + "%' " +
					  " ORDER BY t.project_id, t.gj_id";
				break;
			case 1:
				Sql = " select '' AS sn, t.project_id, t.project_name, t.gj_id, t.gj_name, t.attr_name, t.ctime, round(avg(t.value),2), t.unit, t.lev, t.des, t.top_height, t.base_height, t.material " +
						  " FROM view_data_gj t  " +
						  " where t.gj_id = '"+ GJ_Id +"'" + 
						  "   and t.project_id = '" + currStatus.getFunc_Project_Id() + "'" +
						  "   and t.ctime >= date_format('"+currStatus.getVecDate().get(0).toString()+"', '%Y-%m-%d %H-%i-%S')" +
					  	  "   and t.ctime <= date_format('"+currStatus.getVecDate().get(1).toString()+"', '%Y-%m-%d %H-%i-%S')" +
						  " GROUP BY SUBSTR(ctime,1,13)" +
						  " ORDER BY t.ctime " ;
				break;
			case 2: //周
				switch (Level)
				{
					case 1:
						Sql = " select '' AS sn, t.project_id, t.project_name, t.gj_id, t.gj_name, t.attr_name, t.ctime, round(max(t.value),2), t.unit, t.lev, t.des, t.top_height, t.base_height, t.material " +
							" FROM view_data_gj t  " +
							" where t.gj_id = '"+ GJ_Id +"'" + 
							"   and t.project_id = '" + currStatus.getFunc_Project_Id() + "'" +
							"   and t.ctime >= date_format('"+currStatus.getVecDate().get(0).toString()+"', '%Y-%m-%d %H-%i-%S')" +
							"   and t.ctime <= date_format('"+currStatus.getVecDate().get(1).toString()+"', '%Y-%m-%d %H-%i-%S')" +
							" GROUP BY SUBSTR(ctime,1,10)" +
							" ORDER BY t.ctime " ;
						break;
					case 2:
						Sql = " select '' AS sn, t.project_id, t.project_name, t.gj_id, t.gj_name, t.attr_name, t.ctime, round(avg(t.value),2), t.unit, t.lev, t.des, t.top_height, t.base_height, t.material " +
								" FROM view_data_gj t  " +
								" where t.gj_id = '"+ GJ_Id +"'" + 
								"   and t.project_id = '" + currStatus.getFunc_Project_Id() + "'" +
								"   and t.ctime >= date_format('"+currStatus.getVecDate().get(0).toString()+"', '%Y-%m-%d %H-%i-%S')" +
								"   and t.ctime <= date_format('"+currStatus.getVecDate().get(1).toString()+"', '%Y-%m-%d %H-%i-%S')" +
								" GROUP BY SUBSTR(ctime,1,10)" +
								" ORDER BY t.ctime " ;
						break;
					case 3:
						Sql = " select '' AS sn, t.project_id, t.project_name, t.gj_id, t.gj_name, t.attr_name, t.ctime, round(min(t.value),2), t.unit, t.lev, t.des, t.top_height, t.base_height, t.material " +
								" FROM view_data_gj t  " +
								" where t.gj_id = '"+ GJ_Id +"'" + 
								"   and t.project_id = '" + currStatus.getFunc_Project_Id() + "'" +
								"   and t.ctime >= date_format('"+currStatus.getVecDate().get(0).toString()+"', '%Y-%m-%d %H-%i-%S')" +
								"   and t.ctime <= date_format('"+currStatus.getVecDate().get(1).toString()+"', '%Y-%m-%d %H-%i-%S')" +
								" GROUP BY SUBSTR(ctime,1,10)" +
								" ORDER BY t.ctime " ;
						break;
				}
				break;
			case 3: //月
				switch (Level)
				{
					case 1:
						Sql = " select '' AS sn, t.project_id, t.project_name, t.gj_id, t.gj_name, t.attr_name, t.ctime, round(max(t.value),2), t.unit, t.lev, t.des, t.top_height, t.base_height, t.material " +
								" FROM view_data_gj t  " +
								" where t.gj_id = '"+ GJ_Id +"'" + 
								"   and t.project_id = '" + currStatus.getFunc_Project_Id() + "'" +
								"   and t.ctime >= date_format('"+currStatus.getVecDate().get(0).toString()+"', '%Y-%m-%d %H-%i-%S')" +
								"   and t.ctime <= date_format('"+currStatus.getVecDate().get(1).toString()+"', '%Y-%m-%d %H-%i-%S')" +
								" GROUP BY SUBSTR(ctime,1,10)" +
								" ORDER BY t.ctime " ;
						break;
					case 2:
						Sql = " select '' AS sn, t.project_id, t.project_name, t.gj_id, t.gj_name, t.attr_name, t.ctime, round(avg(t.value),2), t.unit, t.lev, t.des, t.top_height, t.base_height, t.material " +
								" FROM view_data_gj t  " +
								" where t.gj_id = '"+ GJ_Id +"'" + 
								"   and t.project_id = '" + currStatus.getFunc_Project_Id() + "'" +
								"   and t.ctime >= date_format('"+currStatus.getVecDate().get(0).toString()+"', '%Y-%m-%d %H-%i-%S')" +
								"   and t.ctime <= date_format('"+currStatus.getVecDate().get(1).toString()+"', '%Y-%m-%d %H-%i-%S')" +
								" GROUP BY SUBSTR(ctime,1,10)" +
								" ORDER BY t.ctime " ;
						break;
					case 3:
						Sql = " select '' AS sn, t.project_id, t.project_name, t.gj_id, t.gj_name, t.attr_name, t.ctime, round(min(t.value),2), t.unit, t.lev, t.des, t.top_height, t.base_height, t.material " +
								" FROM view_data_gj t  " +
								" where t.gj_id = '"+ GJ_Id +"'" + 
								"   and t.project_id = '" + currStatus.getFunc_Project_Id() + "'" +
								"   and t.ctime >= date_format('"+currStatus.getVecDate().get(0).toString()+"', '%Y-%m-%d %H-%i-%S')" +
								"   and t.ctime <= date_format('"+currStatus.getVecDate().get(1).toString()+"', '%Y-%m-%d %H-%i-%S')" +
								" GROUP BY SUBSTR(ctime,1,10)" +
								" ORDER BY t.ctime " ;
						break;
				}
				break;
			case 4: //年
				switch (Level)
				{
					case 1:
						Sql = " select '' AS sn, t.project_id, t.project_name, t.gj_id, t.gj_name, t.attr_name, t.ctime, round(max(t.value),2), t.unit, t.lev, t.des, t.top_height, t.base_height, t.material " +
								" FROM view_data_gj t  " +
								" where t.gj_id = '"+ GJ_Id +"'" + 
								"   and t.project_id = '" + currStatus.getFunc_Project_Id() + "'" +
								"   and t.ctime >= date_format('"+currStatus.getVecDate().get(0).toString()+"', '%Y-%m-%d %H-%i-%S')" +
								"   and t.ctime <= date_format('"+currStatus.getVecDate().get(1).toString()+"', '%Y-%m-%d %H-%i-%S')" +
								" GROUP BY SUBSTR(ctime,1,7)" +
								" ORDER BY t.ctime " ;
						break;
					case 2:
						Sql = " select '' AS sn, t.project_id, t.project_name, t.gj_id, t.gj_name, t.attr_name, t.ctime, round(avg(t.value),2), t.unit, t.lev, t.des, t.top_height, t.base_height, t.material " +
								" FROM view_data_gj t  " +
								" where t.gj_id = '"+ GJ_Id +"'" + 
								"   and t.project_id = '" + currStatus.getFunc_Project_Id() + "'" +
								"   and t.ctime >= date_format('"+currStatus.getVecDate().get(0).toString()+"', '%Y-%m-%d %H-%i-%S')" +
								"   and t.ctime <= date_format('"+currStatus.getVecDate().get(1).toString()+"', '%Y-%m-%d %H-%i-%S')" +
								" GROUP BY SUBSTR(ctime,1,7)" +
								" ORDER BY t.ctime " ;
						break;
					case 3:
						Sql = " select '' AS sn, t.project_id, t.project_name, t.gj_id, t.gj_name, t.attr_name, t.ctime, round(min(t.value),2), t.unit, t.lev, t.des, t.top_height, t.base_height, t.material " +
								" FROM view_data_gj t  " +
								" where t.gj_id = '"+ GJ_Id +"'" + 
								"   and t.project_id = '" + currStatus.getFunc_Project_Id() + "'" +
								"   and t.ctime >= date_format('"+currStatus.getVecDate().get(0).toString()+"', '%Y-%m-%d %H-%i-%S')" +
								"   and t.ctime <= date_format('"+currStatus.getVecDate().get(1).toString()+"', '%Y-%m-%d %H-%i-%S')" +
								" GROUP BY SUBSTR(ctime,1,7)" +
								" ORDER BY t.ctime " ;
						break;
				}
				break;
				
//			case 4://历史   最近二十四小时均值折线图
//				Sql = " select '' AS sn, t.project_id, t.project_name, t.gj_id, t.gj_name, t.attr_name, t.ctime, round(avg(t.value),2), t.unit, t.lev, t.des, t.top_height, t.base_height, t.material " +
//					  " FROM view_data_gj t  " +
//					  " where t.gj_id = '"+ GJ_Id +"'" + 
//					  "   and t.project_id = '" + currStatus.getFunc_Project_Id() + "'" +
//					  "   and t.ctime >= date_format('" + SqlBTime + "', '%Y-%m-%d %H-%i-%S')" +
//				  	  "   and t.ctime <= date_format('" + SqlETime + "', '%Y-%m-%d %H-%i-%S')" +
//					  " GROUP BY SUBSTR(ctime,1,13)" +
//					  " ORDER BY t.ctime " ;
//				break;
//			case 5://历史   最近一周均值折线图
//				Sql = " select '' AS sn, t.project_id, t.project_name, t.gj_id, t.gj_name, t.attr_name, t.ctime, round(avg(t.value),2), t.unit, t.lev, t.des, t.top_height, t.base_height, t.material " +
//					  " FROM view_data_gj t  " +
//					  " where t.gj_id = '"+ GJ_Id +"'" + 
//					  "   and t.project_id = '" + currStatus.getFunc_Project_Id() + "'" +
//					  "   and t.ctime >= date_format('" + SqlBTime+"', '%Y-%m-%d %H-%i-%S')" +
//				  	  "   and t.ctime <= date_format('" + SqlETime+"', '%Y-%m-%d %H-%i-%S')" +
//					  " GROUP BY SUBSTR(ctime,1,10)" +
//					  " ORDER BY t.ctime " ;
//				break;
//			case 6://历史  最近一月折线图	
//				Sql = " select '' AS sn, t.project_id, t.project_name, t.gj_id, t.gj_name, t.attr_name, t.ctime, round(avg(t.value),2), t.unit, t.lev, t.des, t.top_height, t.base_height, t.material " +
//					  " FROM view_data_gj t  " +
//					  " where t.gj_id = '"+ GJ_Id +"'" + 
//					  "   and t.project_id = '" + currStatus.getFunc_Project_Id() + "'" +
//					  "   and t.ctime >= date_format('" + SqlBTime+"', '%Y-%m-%d %H-%i-%S')" +
//				  	  "   and t.ctime <= date_format('" + SqlETime+"', '%Y-%m-%d %H-%i-%S')" +
//					  " GROUP BY SUBSTR(ctime,1,10)" +
//					  " ORDER BY t.ctime " ;
//				break;
			
				
				
		}
		return Sql;
	}

	public boolean getData(ResultSet pRs) 
	{
		boolean IsOK = true;
		try
		{
			setSN(pRs.getString(1));
			setProject_Id(pRs.getString(2));
			setProject_Name(pRs.getString(3));
			setGJ_Id(pRs.getString(4));
			setGJ_Name(pRs.getString(5));
			setAttr_Name(pRs.getString(6));			
			setCTime(pRs.getString(7));
			setValue(pRs.getString(8));
			setUnit(pRs.getString(9));
			setLev(pRs.getString(10));
			setDes(pRs.getString(11));
			setTop_Height(pRs.getString(12));
			setBase_Height(pRs.getString(13));
			setMaterial(pRs.getString(14));
		} 
		catch (SQLException sqlExp) 
		{
			sqlExp.printStackTrace();
		}		
		return IsOK;
	}
	public boolean getHtmlData(HttpServletRequest request)
	{
		boolean IsOK = true;
		try 
		{
			setSid(CommUtil.StrToGB2312(request.getParameter("Sid")));
			setSN(CommUtil.StrToGB2312(request.getParameter("SN")));
			setProject_Id(CommUtil.StrToGB2312(request.getParameter("Project_Id")));
			setGJ_Id(CommUtil.StrToGB2312(request.getParameter("GJ_Id")));
			setYear(CommUtil.StrToGB2312(request.getParameter("Year")));
			setMonth(CommUtil.StrToGB2312(request.getParameter("Month")));		
			setWeek(CommUtil.StrToGB2312(request.getParameter("Week")));		
			
			setPageSize(CommUtil.StrToGB2312(request.getParameter("rows")));
			setPageNum(CommUtil.StrToGB2312(request.getParameter("page")));
			setpSimu(CommUtil.StrToGB2312(request.getParameter("pSimu")));
			
		}
		catch (Exception Exp) 
		{
			Exp.printStackTrace();
		}
		return IsOK;
	}

	private String SN;
	private String Project_Id;
	private String Project_Name;
	private String GJ_Id;
	private String GJ_Name;
	private String Attr_Name;
	private String CTime;
	private String Value;
	private String Unit;
	private String Lev;
	private String Des;
	private String Top_Height;
	private String Base_Height;
	private String Material;
	
	private String SqlBTime;
	private String SqlETime;

	public String getSqlBTime() {
		return SqlBTime;
	}


	public void setSqlBTime(String sqlBTime) {
		SqlBTime = sqlBTime;
	}


	public String getSqlETime() {
		return SqlETime;
	}


	public void setSqlETime(String sqlETime) {
		SqlETime = sqlETime;
	}

	private String Sid;
	private int Level;
	private String Year;
	private String Month;
	
	private String	Week;
	private boolean	Flag;
	private String	Flag_Year;
	
	private String PageSize;
	private String PageNum;
	private String pSimu;

	public String getFlag_Year()
	{
		return Flag_Year;
	}

	public void setFlag_Year(String flag_Year)
	{
		Flag_Year = flag_Year;
	}

	public String getWeek()
	{
		return Week;
	}

	public void setWeek(String week)
	{
		Week = week;
	}

	public boolean isFlag()
	{
		return Flag;
	}

	public void setFlag(boolean flag)
	{
		Flag = flag;
	}

	public String getpSimu()
	{
		return pSimu;
	}

	public void setpSimu(String pSimu)
	{
		this.pSimu = pSimu;
	}

	public String getPageSize() {
		return PageSize;
	}

	public void setPageSize(String pageSize) {
		PageSize = pageSize;
	}

	public String getPageNum() {
		return PageNum;
	}

	public void setPageNum(String pageNum) {
		PageNum = pageNum;
	}

	public String getSN() {
		return SN;
	}

	public void setSN(String sN) {
		SN = sN;
	}

	public String getProject_Name() {
		return Project_Name;
	}

	public void setProject_Name(String project_Name) {
		Project_Name = project_Name;
	}

	public String getGJ_Id() {
		return GJ_Id;
	}

	public void setGJ_Id(String gJ_Id) {
		GJ_Id = gJ_Id;
	}

	public String getGJ_Name() {
		return GJ_Name;
	}

	public void setGJ_Name(String gJ_Name) {
		GJ_Name = gJ_Name;
	}

	public String getProject_Id() {
		return Project_Id;
	}

	public void setProject_Id(String project_Id) {
		Project_Id = project_Id;
	}

	public String getTop_Height() {
		return Top_Height;
	}

	public void setTop_Height(String top_Height) {
		Top_Height = top_Height;
	}

	public String getBase_Height() {
		return Base_Height;
	}

	public void setBase_Height(String base_Height) {
		Base_Height = base_Height;
	}

	public String getMaterial() {
		return Material;
	}

	public void setMaterial(String material) {
		Material = material;
	}

	public String getAttr_Name() {
		return Attr_Name;
	}

	public void setAttr_Name(String attrName) {
		Attr_Name = attrName;
	}

	public String getCTime() {
		return CTime;
	}

	public void setCTime(String cTime) {
		CTime = cTime;
	}

	public String getValue() {
		return Value;
	}

	public void setValue(String value) {
		Value = value;
	}

	public String getUnit() {
		return Unit;
	}

	public void setUnit(String unit) {
		Unit = unit;
	}

	public String getLev() {
		return Lev;
	}

	public void setLev(String lev) {
		Lev = lev;
	}

	public String getDes() {
		return Des;
	}

	public void setDes(String des) {
		Des = des;
	}
	
	public String getSid() {
		return Sid;
	}

	public void setSid(String sid) {
		Sid = sid;
	}

	public int getLevel()
	{
		return Level;
	}

	public void setLevel(int level)
	{
		Level = level;
	}

	public String getYear() {
		return Year;
	}

	public void setYear(String year) {
		Year = year;
	}

	public String getMonth() {
		return Month;
	}

	public void setMonth(String month) {
		Month = month;
	}
}