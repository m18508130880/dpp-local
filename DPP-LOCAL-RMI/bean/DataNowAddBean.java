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

public class DataNowAddBean extends RmiBean 
{
	public final static long serialVersionUID = RmiBean.RMI_DATA_ADD;
	public long getClassId()
	{
		return serialVersionUID;
	}
	
	public DataNowAddBean()
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
				DataNowAddBean bean = (DataNowAddBean) iterator.next();
				Cpm_Id = bean.getCpm_Id();
				Id = bean.getId();
				CTime = bean.getCTime();
				Value = bean.getValue();
				Resp += Cpm_Id + "|" + Id + "|" + CTime + "|" + Value + ";";
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
				DataNowAddBean bean = (DataNowAddBean) iterator.next();
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
			case 0://查询               
				Sql = " select t.sn, t.cpm_id, t.id, t.attr_id, t.attr_name, t.ctime, t.value, t.unit, lev, des " +
					  " from data_now t order by t.sn";
				break;
			case 1://查询               
				Sql = " select t.sn, t.cpm_id, t.id, t.attr_id, t.attr_name, t.ctime, t.value, t.unit, lev, des " +
						" from data_now t "+
						" where t.cpm_id like '%"+Cpm_Id+"' order by t.sn";
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
			setCpm_Id(pRs.getString(2));
			setId(pRs.getString(3));
			setAttr_Id(pRs.getString(4));
			setAttr_Name(pRs.getString(5));
			setCTime(pRs.getString(6));
			setValue(pRs.getString(7));
			setUnit(pRs.getString(8));
			setLev(pRs.getString(9));
			setDes(pRs.getString(10));
//			
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
			setSN(CommUtil.StrToGB2312(request.getParameter("SN")));
			setCpm_Id(CommUtil.StrToGB2312(request.getParameter("Cpm_Id")));
			setId(CommUtil.StrToGB2312(request.getParameter("Id")));
			setAttr_Id(CommUtil.StrToGB2312(request.getParameter("Attr_Id")));
			setAttr_Name(CommUtil.StrToGB2312(request.getParameter("Attr_Name")));
			setCTime(CommUtil.StrToGB2312(request.getParameter("CTime")));
			setValue(CommUtil.StrToGB2312(request.getParameter("Value")));
			setUnit(CommUtil.StrToGB2312(request.getParameter("Unit")));
			setLev(CommUtil.StrToGB2312(request.getParameter("Lev")));
			setDes(CommUtil.StrToGB2312(request.getParameter("Des")));
			
			setSid(CommUtil.StrToGB2312(request.getParameter("Sid")));
		}
		catch (Exception Exp)
		{
			Exp.printStackTrace();
		}
		return IsOK;
	}
	
	private String SN;
	private String Cpm_Id;
	private String Id;
	private String Attr_Id;
	private String Attr_Name;
	private String CTime;
	private String Value;
	private String Unit;
	private String Lev;
	private String Des;
	
	private String Sid;
	
	

	public String getCpm_Id() {
		return Cpm_Id;
	}

	public void setCpm_Id(String cpm_Id) {
		Cpm_Id = cpm_Id;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
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

	public String getSN()
	{
		return SN;
	}

	public void setSN(String sN)
	{
		SN = sN;
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