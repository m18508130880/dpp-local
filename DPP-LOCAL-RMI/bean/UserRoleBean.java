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

public class UserRoleBean extends RmiBean 
{	
	public final static long serialVersionUID = RmiBean.RMI_USER_ROLE;
	public long getClassId()
	{
		return serialVersionUID;
	}
	
	public UserRoleBean()
	{
		super.className = "UserRoleBean";
	}
	
	//查询
	public void ExecCmd(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
		switch(currStatus.getCmd())
		{
			case 0://功能权限
		    	request.getSession().setAttribute("FP_Role_" + Sid, ((Object)msgBean.getMsg()));
		    	currStatus.setJsp("FP_Role.jsp?Sid=" + Sid);
		    	
		    	//功能点
		    	msgBean = pRmi.RmiExec(2, this, 0, 25);
		    	request.getSession().setAttribute("FP_Info_" + Sid, ((Object)msgBean.getMsg()));
		    	break;
		    case 1://管理权限
		    	request.getSession().setAttribute("Manage_Role_" + Sid, ((Object)msgBean.getMsg()));
		    	currStatus.setJsp("Manage_Role.jsp?Sid=" + Sid);    
		    	break;
		}
		
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
	   	response.sendRedirect(currStatus.getJsp());
	}
	
	//操作
	public void RoleOP(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		PrintWriter outprint = response.getWriter();
		String Resp = "9999";
		
		if(13 == currStatus.getCmd())
		{
			msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
			if(RoleList.trim().length() > 0)
			{
				String[] List = RoleList.split("@");
				for(int i=0; i<List.length && List[i].length()>0; i++)
				{
					String[] subList = List[i].split(";");
					Id = subList[0].trim();
					CName = subList[1].trim();
					Point = subList[2].trim();
					if(null == CName){CName = "无";}
					if(null == Point){Point = "";}
					msgBean = pRmi.RmiExec(14, this, 0, 25);
					if(msgBean.getStatus() == MsgBean.STA_SUCCESS)
					{
						Resp = "0000";
					}
				}
			}
		}
		else
		{
			msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
			switch(currStatus.getCmd())
			{
				case 12://功能权限删除
				case 11://功能权限修改
				case 10://功能权限添加
					if(msgBean.getStatus() == MsgBean.STA_SUCCESS)
						Resp = "0000";
					break;
			}
		}
		
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		outprint.write(Resp);
	}
	
	public String getSql(int pCmd)
	{
		String Sql = "";
		switch (pCmd)
		{
			case 0://功能权限
				Sql = " select t.id, t.cname, t.point from role t where length(t.id) = 3 order by t.id";
				break;
			case 1://管理权限
				Sql = " select t.id, t.cname, t.point from role t where substr(t.id,1,2) = '50' and (length(t.id) = 4 or length(t.id) = 6 or length(t.id) = 8) order by t.id";
				break;
			case 2://功能点
				Sql = " select t.id, t.cname, '' as point from fp_info t where t.status = '0' order by t.id";
				break;
			case 10://功能权限添加
				Sql = " insert into role(id, cname)values('"+ Id +"', '"+ CName +"')";
				break;
			case 11://功能权限修改
				Sql = " update role t set t.cname = '"+ CName +"', t.point = '"+ Point +"' where t.id = '"+ Id +"'";
				break;
			case 12://功能权限删除
				Sql = " delete from role where id = '"+ Id +"'";
				break;
			case 13://管理权限删除
				Sql = " delete from role where substr(id,1,2) = '"+ Id +"'";
				break;
			case 14://管理权限添加
				Sql = " insert into role(id, cname, point)values('"+ Id +"', '"+ CName +"', '"+ Point +"')";
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
			setPoint(pRs.getString(3));
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
			setPoint(CommUtil.StrToGB2312(request.getParameter("Point")));
			setRoleList(CommUtil.StrToGB2312(request.getParameter("RoleList")));
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
	private String Point;
	private String RoleList;
	private String Sid;
	
	public String getRoleList() {
		return RoleList;
	}
	public void setRoleList(String roleList) {
		RoleList = roleList;
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
	public String getPoint() {
		return Point;
	}
	public void setPoint(String point) {
		Point = point;
	}

	public String getSid() {
		return Sid;
	}

	public void setSid(String sid) {
		Sid = sid;
	}
}
