package bean;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rmi.Rmi;
import rmi.RmiBean;
import util.CommUtil;
import util.CurrStatus;
import util.MsgBean;

import com.alibaba.fastjson.JSONObject;

public class DataNowBean extends RmiBean 
{
	public final static long serialVersionUID = RmiBean.RMI_DATA_NOW;
	public long getClassId()
	{
		return serialVersionUID;
	}
	
	public DataNowBean()
	{
		super.className = "DataNowBean";
	}
	
	public void ExecCmd(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
		switch(currStatus.getCmd())
		{
			case 0://查询
		    	request.getSession().setAttribute("Data_Now_" + Sid, ((Object)msgBean.getMsg()));
		    	currStatus.setJsp("Data_Now.jsp?Sid=" + Sid);		    
		    	break;
			case 1://查询
				request.getSession().setAttribute("DTU_Now_" + Sid, ((Object)msgBean.getMsg()));
				currStatus.setJsp("dtu.jsp?Sid=" + Sid);		    
				break;
		}
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
	   	response.sendRedirect(currStatus.getJsp());
	}
	
	public void getDTU(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		PrintWriter outprint = response.getWriter();
		String Resp = "9999";

		msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
		if (msgBean.getStatus() == MsgBean.STA_SUCCESS)
		{
			Resp = "0000";
			ArrayList<?> list = (ArrayList<?>) msgBean.getMsg();
			Iterator<?> iterator = list.iterator();
			while (iterator.hasNext())
			{
				DataNowBean bean = (DataNowBean) iterator.next();
				PId = bean.getTId();
				TId = bean.getTId();
				CTime = bean.getCTime();
				Value = bean.getValue();
				Resp += PId + "|" + TId + "|" + CTime + "|" + Value + ";";
			}
		}

		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		outprint.write(Resp);
	}
	
	public void getDataNow(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
		ArrayList<Object> arrayList = new ArrayList<Object>();
		if (msgBean.getStatus() == MsgBean.STA_SUCCESS)
		{
			ArrayList<?> list = (ArrayList<?>) msgBean.getMsg();
			Iterator<?> iterator = list.iterator();
			while (iterator.hasNext())
			{
				DataNowBean bean = (DataNowBean) iterator.next();
				arrayList.add(bean);
			}
		}
		PrintWriter outprint = response.getWriter();
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		String jsonObj = JSONObject.toJSONString(arrayList);
		response.setCharacterEncoding("UTF-8");
		outprint.write(jsonObj);
		outprint.flush();

//		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
//		outprint.write(Resp);
	}
	
	public String getSql(int pCmd)
	{
		String Sql = "";
		switch (pCmd)
		{
//			case 0://查询               
//				Sql = " select t.sn, t.cpm_id, t.id, t.attr_id, t.attr_name, t.ctime, t.value, t.unit, lev, des " +
//					  " from data_now t order by t.sn";
//				break;
//			case 1://查询               
//				Sql = " select t.sn, t.cpm_id, t.id, t.attr_id, t.attr_name, t.ctime, t.value, t.unit, lev, des " +
//						" from data_now t "+
//						" where t.cpm_id like '%"+Cpm_Id+"' order by t.sn";
//				break;
			case 0://查询               
				Sql = " select t.sn, t.gj_id, t.gj_name, t.longitude, t.latitude, t.project_id, t.top_height, t.base_height, equip_height, " + 
					  " t.pid, t.tid, t.addrs, t.code, t.sign, t.cname, t.attr_id, t.attr_name, t.ctime, t.value, t.unit " +
					  " from view_data_now t "+
					  " where t.project_id = '"+Project_Id+"' order by t.sn";
				break;
			case 1://查询               
				Sql = " select t.sn, t.gj_id, t.gj_name, t.longitude, t.latitude, t.project_id, t.top_height, t.base_height, equip_height, " + 
					  " t.pid, t.tid, t.addrs, t.code, t.sign, t.cname, t.attr_id, t.attr_name, t.ctime, t.value, t.unit " +
					  " from view_data_now t "+
					  " where t.pid like '%"+PId+"' " + 
					  " order by t.sn";
				break;
			case 2://查询               
				Sql = " select t.sn, t.gj_id, t.gj_name, t.longitude, t.latitude, t.project_id, t.top_height, t.base_height, equip_height, " + 
						" t.pid, t.tid, t.addrs, t.code, t.sign, t.cname, t.attr_id, t.attr_name, t.ctime, t.value, t.unit " +
						" from view_data_now t "+
						" where t.project_id = '"+Project_Id+"' " + 
						" and t.pid = '" + PId + "' " + 
						" and t.addrs = '" + Addrs + "' " + 
						" and t.code = '" + Code + "' " + 
						" and t.sign = '" + Sign + "' " + 
						" order by t.sn";
				break;
		}
		return Sql;
	}
	
	public boolean getData(ResultSet pRs)
	{
		boolean IsOK = true;
		try
		{
//			setSN(pRs.getString(1));
//			setCpm_Id(pRs.getString(2));
//			setId(pRs.getString(3));
//			setAttr_Id(pRs.getString(4));
//			setAttr_Name(pRs.getString(5));
//			setCTime(pRs.getString(6));
//			setValue(pRs.getString(7));
//			setUnit(pRs.getString(8));
//			setLev(pRs.getString(9));
//			setDes(pRs.getString(10));
			setSN(pRs.getString(1));
			setGJ_Id(pRs.getString(2));
			setGJ_Name(pRs.getString(3));
			setLongitude(pRs.getString(4));
			setLatitude(pRs.getString(5));
			setProject_Id(pRs.getString(6));
			setTop_Height(pRs.getString(7));
			setBase_Height(pRs.getString(8));
			setEquip_Height(pRs.getString(9));
			setPId(pRs.getString(10));
			setTId(pRs.getString(11));
			setAddrs(pRs.getString(12));
			setCode(pRs.getString(13));
			setSign(pRs.getString(14));
			setCName(pRs.getString(15));
			setAttr_Id(pRs.getString(16));
			setAttr_Name(pRs.getString(17));
			setCTime(pRs.getString(18));
			setValue(pRs.getString(19));
			setUnit(pRs.getString(20));
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
//			setSN(CommUtil.StrToGB2312(request.getParameter("SN")));
//			setCpm_Id(CommUtil.StrToGB2312(request.getParameter("Cpm_Id")));
//			setId(CommUtil.StrToGB2312(request.getParameter("Id")));
//			setAttr_Id(CommUtil.StrToGB2312(request.getParameter("Attr_Id")));
//			setAttr_Name(CommUtil.StrToGB2312(request.getParameter("Attr_Name")));
//			setCTime(CommUtil.StrToGB2312(request.getParameter("CTime")));
//			setValue(CommUtil.StrToGB2312(request.getParameter("Value")));
//			setUnit(CommUtil.StrToGB2312(request.getParameter("Unit")));
//			setLev(CommUtil.StrToGB2312(request.getParameter("Lev")));
//			setDes(CommUtil.StrToGB2312(request.getParameter("Des")));
			setSN(CommUtil.StrToGB2312(request.getParameter("SN")));
			setGJ_Id(CommUtil.StrToGB2312(request.getParameter("GJ_Id")));
			setGJ_Name(CommUtil.StrToGB2312(request.getParameter("GJ_Name")));
			setLongitude(CommUtil.StrToGB2312(request.getParameter("Longitude")));
			setLatitude(CommUtil.StrToGB2312(request.getParameter("Latitude")));
			setProject_Id(CommUtil.StrToGB2312(request.getParameter("Project_Id")));
			setTop_Height(CommUtil.StrToGB2312(request.getParameter("Top_Height")));
			setBase_Height(CommUtil.StrToGB2312(request.getParameter("Base_Height")));
			setEquip_Height(CommUtil.StrToGB2312(request.getParameter("Equip_Height")));
			setPId(CommUtil.StrToGB2312(request.getParameter("PId")));
			setTId(CommUtil.StrToGB2312(request.getParameter("TId")));
			setAddrs(CommUtil.StrToGB2312(request.getParameter("Addrs")));
			setCode(CommUtil.StrToGB2312(request.getParameter("Code")));
			setSign(CommUtil.StrToGB2312(request.getParameter("Sign")));
			setCName(CommUtil.StrToGB2312(request.getParameter("CName")));
			setAttr_Id(CommUtil.StrToGB2312(request.getParameter("Attr_Id")));
			setAttr_Name(CommUtil.StrToGB2312(request.getParameter("Attr_Name")));
			setCTime(CommUtil.StrToGB2312(request.getParameter("CTime")));
			setValue(CommUtil.StrToGB2312(request.getParameter("Value")));
			setUnit(CommUtil.StrToGB2312(request.getParameter("Unit")));

			setSid(CommUtil.StrToGB2312(request.getParameter("Sid")));
		}
		catch (Exception Exp)
		{
			Exp.printStackTrace();
		}
		return IsOK;
	}
	
//	private String SN;
//	private String Cpm_Id;
//	private String Id;
//	private String Attr_Id;
//	private String Attr_Name;
//	private String CTime;
//	private String Value;
//	private String Unit;
//	private String Lev;
//	private String Des;
	
	private String Sid;
	
	// " select t.sn, t.gj_id, t.gj_name, t.longitude, t.latitude, t.project_id, t.top_height, t.base_height, equip_height, " + 
	//  " t.pid, t.tid, t.addrs, t.code, t.sign, t.cname, t.attr_id, t.attr_name, t.ctime, t.value, t.unit " +
	
	private String SN;
	private String GJ_Id;
	private String GJ_Name;
	private String Longitude;
	private String Latitude;
	private String Project_Id;
	private String Top_Height;
	private String Base_Height;
	private String Equip_Height;
	private String PId;
	private String TId;
	private String Addrs;
	private String Code;
	private String Sign;
	private String CName;
	private String Attr_Id;
	private String Attr_Name;
	private String CTime;
	private String Value;
	private String Unit;

	public String getSN()
	{
		return SN;
	}

	public void setSN(String sN)
	{
		SN = sN;
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

	public String getLongitude() {
		return Longitude;
	}

	public void setLongitude(String longitude) {
		Longitude = longitude;
	}

	public String getLatitude() {
		return Latitude;
	}

	public void setLatitude(String latitude) {
		Latitude = latitude;
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

	public String getEquip_Height() {
		return Equip_Height;
	}

	public void setEquip_Height(String equip_Height) {
		Equip_Height = equip_Height;
	}

	public String getPId() {
		return PId;
	}

	public void setPId(String pId) {
		PId = pId;
	}

	public String getTId() {
		return TId;
	}

	public void setTId(String tId) {
		TId = tId;
	}

	public String getAddrs() {
		return Addrs;
	}

	public void setAddrs(String addrs) {
		Addrs = addrs;
	}

	public String getCode() {
		return Code;
	}

	public void setCode(String code) {
		Code = code;
	}

	public String getSign() {
		return Sign;
	}

	public void setSign(String sign) {
		Sign = sign;
	}

	public String getCName() {
		return CName;
	}

	public void setCName(String cName) {
		CName = cName;
	}

	public String getAttr_Id() {
		return Attr_Id;
	}

	public void setAttr_Id(String attr_Id) {
		Attr_Id = attr_Id;
	}

	public String getAttr_Name() {
		return Attr_Name;
	}

	public void setAttr_Name(String attr_Name) {
		Attr_Name = attr_Name;
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

	public String getSid()
	{
		return Sid;
	}

	public void setSid(String sid)
	{
		Sid = sid;
	}
	
}