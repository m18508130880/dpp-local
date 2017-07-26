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

public class DataGXBean extends RmiBean 
{	
	public final static long serialVersionUID = RmiBean.RMI_DATAGX;
	public long getClassId()
	{
		return serialVersionUID;
	}
	
	public DataGXBean()
	{
		super.className = "DataGXBean";
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
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		Calendar c = Calendar.getInstance();    
		switch(currStatus.getCmd())
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
		}

		msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
    	request.getSession().setAttribute("User_DataGX_Graph_" + Sid, ((Object)msgBean.getMsg()));	
    	
    	DevGXBean devGXBean = new DevGXBean();
    	devGXBean.setId(GX_Id);
    	msgBean = pRmi.RmiExec(3, devGXBean, 0, 25);
		request.getSession().setAttribute("One_GX_" + Sid, (Object)msgBean.getMsg());				
		
		currStatus.setJsp("User_DataGX_Graph.jsp?Sid=" + Sid+"&GX_Id=" + GX_Id);				
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
							DataGXBean dataBean = (DataGXBean)iterator.next();
							DataGXReal RealJson = new DataGXReal();
							RealJson.setGx_id(dataBean.getGX_Id());
							RealJson.setDiameter(dataBean.getDiameter()); 
							RealJson.setLength(dataBean.getLength());
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
	    	System.out.println("GXNowData[" + jsonObj.toString() + "]");
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
				Sql = " select '' AS sn, t.project_id, t.project_name, t.GX_id, t.GX_name, t.attr_name, t.ctime, t.value, t.unit, t.lev, t.des, t.length, t.diameter, t.material" +
					  " FROM view_data_now_gx t " +
					  " where t.project_id = "+ Project_Id + 
					  " and t.gx_id like '%" + currStatus.getFunc_Sub_Type_Id() + "%' " +
					  " ORDER BY t.project_id, t.GX_id";
				break;
			
			case 4://历史   最近二十四小时均值折线图
				Sql = " select '' AS sn, t.project_id, t.project_name, t.GX_id, t.GX_name, t.attr_name, t.ctime, round(avg(t.value),2), t.unit, t.lev, t.des, t.length, t.diameter, t.material " +
					  " FROM view_data_gx t  " +
					  " where t.gx_id = '"+ GX_Id +"'" + 
					  "   and t.ctime >= date_format('" + SqlBTime + "', '%Y-%m-%d %H-%i-%S')" +
				  	  "   and t.ctime <= date_format('" + SqlETime + "', '%Y-%m-%d %H-%i-%S')" +
					  " GROUP BY SUBSTR(ctime,1,13)" +
					  " ORDER BY t.ctime " ;
				break;
			case 5://历史   最近一周均值折线图
				Sql = " select '' AS sn, t.project_id, t.project_name, t.GX_id, t.GX_name, t.attr_name, t.ctime, round(avg(t.value),2), t.unit, t.lev, t.des, t.length, t.diameter, t.material " +
					  " FROM view_data_gx t  " +
					  " where t.gx_id = '"+ GX_Id +"'" + 
					  "   and t.ctime >= date_format('" + SqlBTime+"', '%Y-%m-%d %H-%i-%S')" +
				  	  "   and t.ctime <= date_format('" + SqlETime+"', '%Y-%m-%d %H-%i-%S')" +
					  " GROUP BY SUBSTR(ctime,1,10)" +
					  " ORDER BY t.ctime " ;
				break;
			case 6://历史  最近一月折线图	
				Sql = " select '' AS sn, t.project_id, t.project_name, t.GX_id, t.GX_name, t.attr_name, t.ctime, round(avg(t.value),2), t.unit, t.lev, t.des, t.length, t.diameter, t.material " +
					  " FROM view_data_gx t  " +
					  " where t.gx_id = '"+ GX_Id +"'" + 
					  "   and t.ctime >= date_format('" + SqlBTime+"', '%Y-%m-%d %H-%i-%S')" +
				  	  "   and t.ctime <= date_format('" + SqlETime+"', '%Y-%m-%d %H-%i-%S')" +
					  " GROUP BY SUBSTR(ctime,1,10)" +
					  " ORDER BY t.ctime " ;
				break;
				
				
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
			setGX_Id(pRs.getString(4));
			setGX_Name(pRs.getString(5));
			setAttr_Name(pRs.getString(6));			
			setCTime(pRs.getString(7));
			setValue(pRs.getString(8));
			setUnit(pRs.getString(9));
			setLev(pRs.getString(10));
			setDes(pRs.getString(11));
			setLength(pRs.getString(12));
			setDiameter(pRs.getString(13));
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
			setGX_Id(CommUtil.StrToGB2312(request.getParameter("GX_Id")));
			setLevel(CommUtil.StrToGB2312(request.getParameter("Level")));
			setYear(CommUtil.StrToGB2312(request.getParameter("Year")));
			setMonth(CommUtil.StrToGB2312(request.getParameter("Month")));		
			
			setPageSize(CommUtil.StrToGB2312(request.getParameter("rows")));
			setPageNum(CommUtil.StrToGB2312(request.getParameter("page")));
			
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
	private String GX_Id;
	private String GX_Name;
	private String Attr_Name;
	private String CTime;
	private String Value;
	private String Unit;
	private String Lev;
	private String Des;
	
	private String Diameter;
	private String Length;
	private String Start_Height;
	private String End_Height;
	private String Material;
	
	private String SqlBTime;
	private String SqlETime;
	

	private String Sid;
	private String Level;
	private String Year;
	private String Month;
	
	private String PageSize;
	private String PageNum;

	
	
	
	public String getDiameter() {
		return Diameter;
	}

	public void setDiameter(String diameter) {
		Diameter = diameter;
	}

	public String getLength() {
		return Length;
	}

	public void setLength(String length) {
		Length = length;
	}

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

	public String getGX_Id() {
		return GX_Id;
	}

	public void setGX_Id(String gX_Id) {
		GX_Id = gX_Id;
	}

	public String getGX_Name() {
		return GX_Name;
	}

	public void setGX_Name(String gX_Name) {
		GX_Name = gX_Name;
	}

	public String getProject_Id() {
		return Project_Id;
	}

	public void setProject_Id(String project_Id) {
		Project_Id = project_Id;
	}

	

	public String getStart_Height() {
		return Start_Height;
	}


	public void setStart_Height(String start_Height) {
		Start_Height = start_Height;
	}


	public String getEnd_Height() {
		return End_Height;
	}


	public void setEnd_Height(String end_Height) {
		End_Height = end_Height;
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

	public String getLevel() {
		return Level;
	}

	public void setLevel(String level) {
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