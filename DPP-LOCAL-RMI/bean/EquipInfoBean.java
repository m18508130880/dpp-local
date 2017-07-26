package bean;

import java.io.IOException;
import java.io.PrintWriter;
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
		
		switch(currStatus.getCmd())
		{
			case 10://添加
				currStatus.setResult(MsgBean.GetResult(msgBean.getStatus()));
				if(G_Id.contains("WJ") || G_Id.contains("YJ")){
					DevGJBean devGJBean = new DevGJBean();
					devGJBean.setId(G_Id);           //GJ_id
					devGJBean.setEquip_Id(Id);       //equip_id
					devGJBean.setEquip_Name(CName);  //equip_name
					msgBean = pRmi.RmiExec(40, devGJBean, 0, 25);
				}else{
					DevGXBean devGXBean = new DevGXBean();
					devGXBean.setId(G_Id);           //GX_id
					devGXBean.setEquip_Id(Id);       //equip_id
					devGXBean.setEquip_Name(CName);  //equip_name
					msgBean = pRmi.RmiExec(40, devGXBean, 0, 25);
				}
				msgBean = pRmi.RmiExec(0, this, 0, 25);
				request.getSession().setAttribute("Equip_Info_" + Sid, ((Object)msgBean.getMsg()));
		    	currStatus.setJsp("Equip_Info.jsp?Sid=" + Sid);	
				break;
			case 11://编辑
				currStatus.setResult(MsgBean.GetResult(msgBean.getStatus()));
				if(G_Id.contains("WJ") || G_Id.contains("YJ")){
					DevGJBean devGJBean = new DevGJBean();
					devGJBean.setId(G_Id);           //GJ_id
					devGJBean.setEquip_Id(Id);       //equip_id
					devGJBean.setEquip_Name(CName);  //equip_name
					msgBean = pRmi.RmiExec(40, devGJBean, 0, 25);
				}else{
					DevGXBean devGXBean = new DevGXBean();
					devGXBean.setId(G_Id);           //GX_id
					devGXBean.setEquip_Id(Id);       //equip_id
					devGXBean.setEquip_Name(CName);  //equip_name
					msgBean = pRmi.RmiExec(40, devGXBean, 0, 25);
				}
				msgBean = pRmi.RmiExec(0, this, 0, 25);
			case 0://查询
				msgBean = pRmi.RmiExec(0, this, 0, 25);
		    	request.getSession().setAttribute("Equip_Info_" + Sid, ((Object)msgBean.getMsg()));
		    	currStatus.setJsp("Equip_Info.jsp?Sid=" + Sid);		    
		    	break;
		}
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
		
	public String getSql(int pCmd)
	{
		String Sql = "";
		switch (pCmd)
		{
			case 0://查询                
				Sql = " select  t.id, t.cname, t.project_Id, t.project_name, t.g_id " +
					  " from view_equip_info t order by t.id";
				break;
			case 2://设备ID检测
				Sql = " select  t.id, t.cname, t.project_Id, t.project_name, t.g_id " +
					  " from view_equip_info t " +
					  " where upper(Id) = upper('"+ Id +"') ";
				break;
		}
		return Sql;
	}
	
	public boolean getData(ResultSet pRs)
	{
		boolean IsOK = true;
		try
		{
			setId(pRs.getString(1));
			setCName(pRs.getString(2));
			setProject_Id(pRs.getString(3));
			setProject_Name(pRs.getString(4));
			setG_Id(pRs.getString(5));
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
			setId(CommUtil.StrToGB2312(request.getParameter("Id")));
			setCName(CommUtil.StrToGB2312(request.getParameter("CName")));
			setProject_Id(CommUtil.StrToGB2312(request.getParameter("Project_Id")));
			setProject_Name(CommUtil.StrToGB2312(request.getParameter("Project_Name")));
			setG_Id(CommUtil.StrToGB2312(request.getParameter("G_Id")));
			setSid(CommUtil.StrToGB2312(request.getParameter("Sid")));
		}
		catch (Exception Exp)
		{
			Exp.printStackTrace();
		}
		return IsOK;
	}
	
	private String Id;
	private String CName;
	private String Project_Id;
	private String Project_Name;
	private String G_Id;
	
	private String Sid;

	
	public String getG_Id() {
		return G_Id;
	}

	public void setG_Id(String g_Id) {
		G_Id = g_Id;
	}

	public String getProject_Name() {
		return Project_Name;
	}

	public void setProject_Name(String project_Name) {
		Project_Name = project_Name;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
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