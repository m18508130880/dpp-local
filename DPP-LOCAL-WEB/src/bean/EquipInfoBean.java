package bean;

import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rmi.Rmi;
import rmi.RmiBean;
import util.*;

public class EquipInfoBean extends RmiBean 
{
	public final static long serialVersionUID = RmiBean.RMI_EQUIP_INFO;
	public long getClassId()
	{
		return serialVersionUID;
	}
	
	public EquipInfoBean()
	{
		super.className = "EquipInfoBean";
	}
	
	public void ExecCmd(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
		switch(currStatus.getCmd())
		{
			case 40://添加/编辑
				currStatus.setResult(MsgBean.GetResult(msgBean.getStatus()));
				msgBean = pRmi.RmiExec(0, this, 0, 25);
			case 0://查询
		    	request.getSession().setAttribute("Equip_Info_" + Sid, ((Object)msgBean.getMsg()));
		    	
		    	DevGJBean devGJBean = new DevGJBean();
		    	msgBean = pRmi.RmiExec(1, devGJBean, 0, 25);
		    	request.getSession().setAttribute("DevGJ_All_" + Sid, ((Object)msgBean.getMsg()));
		    	
		    	currStatus.setJsp("Equip_Info.jsp?Sid=" + Sid);		    
		    	break;
		}
		//查询DeviceDetail
		DataNowBean dataNowBean = new DataNowBean();
    	msgBean = pRmi.RmiExec(0, dataNowBean, 0, 0);
    	request.getSession().setAttribute("Data_Now_" + Sid, ((Object)msgBean.getMsg()));
    	
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
	   	response.sendRedirect(currStatus.getJsp());
	}
	
	public void IdCheck(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone)
	{
		try 
		{
			getHtmlData(request);
			currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
			currStatus.getHtmlData(request, pFromZone);
			
			PrintWriter outprint = response.getWriter();
			String Resp = "3006";
			
			msgBean = pRmi.RmiExec(2, this, 0, 25);//查找是否有该设备存在
			System.out.println("msgBean.getStatus():" + msgBean.getStatus());
			switch(msgBean.getStatus())
			{
				case 0://已存在
					Resp = "3006";
					break;
				default://可用
					Resp = "0000";
					break;
			}
			
			request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
			outprint.write(Resp);
		}
		catch (Exception Ex)
		{
			Ex.printStackTrace();
		}
	}
	
	//设备重启指令
	public void Restart(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone)
	{
		try
		{
			getHtmlData(request);
			currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
			currStatus.getHtmlData(request, pFromZone);
			
			PrintWriter outprint = response.getWriter();
			String Resp = "3006";
			
			Resp = pRmi.Client(0001,PId,"");
			
			request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
			outprint.write(Resp);
		}
		catch (RemoteException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//设备对时指令
	public void Compare_Time(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone)
	{
		try
		{
			getHtmlData(request);
			currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
			currStatus.getHtmlData(request, pFromZone);
			
			PrintWriter outprint = response.getWriter();
			String Resp = "3006";
			
			Resp = pRmi.Client(0002,PId," ");
			
			request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
			outprint.write(Resp);
		}
		catch (RemoteException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	public String getSql(int pCmd)
	{
		String Sql = "";
		switch (pCmd)
		{
			case 0://查询                
				Sql = " select  t.tid, t.pid, t.cname, t.tel, t.project_Id, t.project_name, t.g_id, t.ctime, t.value " +
					  " from view_equip_info t order by t.ctime";
				break;
			case 1://查询device_deatail                
				Sql = " select  t.tid, t.pid, t.cname, t.demo, t.pwd, t.onoff " +
					  " from device_detail t order by t.tid";
				break;
			case 3://User设备查询                
				Sql = " select  t.tid, t.pid, t.cname, t.tel, t.project_Id, t.project_name, t.g_id, t.ctime, t.value " +
					  " from view_equip_info t where t.project_Id='" + currStatus.getFunc_Project_Id() + "'  order by t.ctime";
				break;	
			case 2://设备ID检测
				Sql = " select  t.tid, t.pid, t.cname, t.tel, t.project_Id, t.project_name, t.g_id, t.ctime, t.value " +
					  " from view_equip_info t " +
					  " where upper(TId) = upper('"+ TId +"') ";
				break;
			case 40://编辑设备EquipInfo
				Sql = "{call pro_update_equip('" + TId + PId + "', '" + CName + "', '" + Pre_Id + "', '" + Pre_Project_Id + "', '" + After_Id + "', '" + After_Project_Id + "', '" + Tel + "')}";
				break;
		}
		return Sql;
	}
	
	public boolean getData(ResultSet pRs)
	{
		boolean IsOK = true;
		try
		{
			setTId(pRs.getString(1));
			setPId(pRs.getString(2));
			setCName(pRs.getString(3));
			setTel(pRs.getString(4));
			setProject_Id(pRs.getString(5));
			setProject_Name(pRs.getString(6));
			setG_Id(pRs.getString(7));
			setCTime(pRs.getString(8));
			setValue(pRs.getString(9));
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
			setTId(CommUtil.StrToGB2312(request.getParameter("TId")));
			setPId(CommUtil.StrToGB2312(request.getParameter("PId")));
			setCName(CommUtil.StrToGB2312(request.getParameter("CName")));
			setTel(CommUtil.StrToGB2312(request.getParameter("Tel")));
			setProject_Id(CommUtil.StrToGB2312(request.getParameter("Project_Id")));
			setProject_Name(CommUtil.StrToGB2312(request.getParameter("Project_Name")));
			setG_Id(CommUtil.StrToGB2312(request.getParameter("G_Id")));
			setCTime(CommUtil.StrToGB2312(request.getParameter("CTime")));
			setValue(CommUtil.StrToGB2312(request.getParameter("Value")));
			setSid(CommUtil.StrToGB2312(request.getParameter("Sid")));
			setPre_Id(CommUtil.StrToGB2312(request.getParameter("Pre_Id")));
			setPre_Project_Id(CommUtil.StrToGB2312(request.getParameter("Pre_Project_Id")));
			setAfter_Id(CommUtil.StrToGB2312(request.getParameter("After_Id")));
			setAfter_Project_Id(CommUtil.StrToGB2312(request.getParameter("After_Project_Id")));
		}
		catch (Exception Exp)
		{
			Exp.printStackTrace();
		}
		return IsOK;
	}
	
	private String TId;
	private String PId;
	private String CName;
	private String Tel;
	private String Project_Id;
	private String Project_Name;
	private String G_Id;
	private String CTime;
	private String Value;
	
	private String Sid;
	private String Pre_Id;
	private String After_Id;
	private String Pre_Project_Id;
	private String After_Project_Id;
    
	public String getPId()
	{
		return PId;
	}

	public void setPId(String pId)
	{
		PId = pId;
	}

	public String getTel()
	{
		return Tel;
	}

	public void setTel(String tel)
	{
		Tel = tel;
	}
	
	public String getPre_Id() {
		return Pre_Id;
	}

	public void setPre_Id(String pre_Id) {
		Pre_Id = pre_Id;
	}

	public String getAfter_Id() {
		return After_Id;
	}

	public void setAfter_Id(String after_Id) {
		After_Id = after_Id;
	}

	public String getPre_Project_Id() {
		return Pre_Project_Id;
	}

	public void setPre_Project_Id(String pre_Project_Id) {
		Pre_Project_Id = pre_Project_Id;
	}

	public String getAfter_Project_Id() {
		return After_Project_Id;
	}

	public void setAfter_Project_Id(String after_Project_Id) {
		After_Project_Id = after_Project_Id;
	}

	public String getG_Id() {
		return G_Id;
	}

	public void setG_Id(String g_Id) {
		G_Id = g_Id;
	}
	
	public String getCTime()
	{
		return CTime;
	}

	public void setCTime(String cTime)
	{
		CTime = cTime;
	}

	public String getProject_Name() {
		return Project_Name;
	}

	public void setProject_Name(String project_Name) {
		Project_Name = project_Name;
	}

	public String getValue()
	{
		return Value;
	}

	public void setValue(String value)
	{
		Value = value;
	}

	public String getTId() {
		return TId;
	}

	public void setTId(String tid) {
		TId = tid;
	}

	public String getCName() {
		return CName;
	}

	public void setCName(String cName) {
		CName = cName;
	}

	public String getProject_Id() {
		return Project_Id;
	}

	public void setProject_Id(String project_Id) {
		Project_Id = project_Id;
	}

	
	public String getSid() {
		return Sid;
	}

	public void setSid(String sid) {
		Sid = sid;
	}
	
	
}