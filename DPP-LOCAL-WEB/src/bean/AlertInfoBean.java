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
import util.*;

public class AlertInfoBean extends RmiBean
{
	public final static long	serialVersionUID	= RmiBean.RMI_ALERT;

	public long getClassId()
	{
		return serialVersionUID;
	}

	public AlertInfoBean()
	{
		super.className = "AlertInfoBean";
	}

	/**
	 * @param request
	 * @param response
	 * @param pRmi
	 * @param pFromZone
	 * @throws ServletException
	 * @throws IOException
	 */
	public void ExecCmd(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		msgBean = pRmi.RmiExec(currStatus.getCmd(), this, currStatus.getCurrPage(), 25);
		switch (currStatus.getCmd())
		{
			case 0:// 查询
				currStatus.setTotalRecord(msgBean.getCount());
				request.getSession().setAttribute("Alert_Info_" + Sid, (Object) msgBean.getMsg());
				currStatus.setJsp("Alert_Info.jsp?Sid=" + Sid);
				break;
			case 10:
				currStatus.setResult("处理成功!");
				msgBean = pRmi.RmiExec(2, this, currStatus.getCurrPage(), 25);
			case 2:// 条件查询
				currStatus.setTotalRecord(msgBean.getCount());
				request.getSession().setAttribute("Alert_Info_" + Sid, (Object) msgBean.getMsg());
				currStatus.setJsp("Alert_Info.jsp?Sid=" + Sid);
				break;
		}
		// 查询告警类型
		msgBean = pRmi.RmiExec(3, this, 0, 0);
		request.getSession().setAttribute("Alert_Type_" + Sid, (Object) msgBean.getMsg());
		
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		response.sendRedirect(currStatus.getJsp());
	}

	public void AlertNow(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		PrintWriter outprint = response.getWriter();
		String Resp = "9999";
		// Cmd = 1
		msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 0);
		if (msgBean.getStatus() == MsgBean.STA_SUCCESS)
		{
			ArrayList<?> alertNow = (ArrayList<?>) msgBean.getMsg();
			if (null != alertNow)
			{
				Resp = "0000";
				Iterator<?> iterator = alertNow.iterator();
				while (iterator.hasNext())
				{
					AlertInfoBean bean = (AlertInfoBean) iterator.next();
					Resp += bean.getGJ_Id()+","+bean.getCTime()+"," + bean.getAttr_Id() + "," + bean.getAttr_Name()+","+bean.getDes()+","+bean.getLongitude()+","+bean.getLatitude()+";";
				}
			}
		}
		System.out.println(Resp);
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		outprint.write(Resp);
	}
	/**
	 * 获取相应sql语句
	 * 
	 */
	public String getSql(int pCmd)
	{
		String Sql = "";
		switch (pCmd)
		{
			case 0:// 查询全部
				Sql = " select t.cpm_id, t.equip_id, t.cname, t.attr_id, t.attr_name, t.level, t.ctime, t.cdata, t.gj_id, t.project_id, t.status, t.unit, t.des, t.longitude, t.latitude " + 
					  " from view_alert_info t where t.project_id = '" + currStatus.getFunc_Project_Id() + "'"+
				  	  " order by t.ctime desc ";
				break;
			case 1:// 查询最新
				Sql = " select t.cpm_id, t.equip_id, t.cname, t.attr_id, t.attr_name, t.level, t.ctime, t.cdata, t.gj_id, t.project_id, t.status, t.unit, t.des, t.longitude, t.latitude " + 
					  " from view_alert_now t where t.project_id = '" + currStatus.getFunc_Project_Id() + "' and t.status = '0' order by t.ctime ";
				break;
			case 2:// 条件查询
				Sql = " select t.cpm_id, t.equip_id, t.cname, t.attr_id, t.attr_name, t.level, t.ctime, t.cdata, t.gj_id, t.project_id, t.status, t.unit, t.des, t.longitude, t.latitude " + 
					  " from view_alert_info t where t.project_id = '" + currStatus.getFunc_Project_Id() + "'"+
					  //" and t.ctime >= date_format('" + currStatus.getVecDate().get(0).toString() + "', '%Y-%m-%d %H-%i-%S')" +
					  //" and t.ctime <= date_format('" + currStatus.getVecDate().get(1).toString() + "', '%Y-%m-%d %H-%i-%S')" + 
					  " and t.attr_id like '%" + currStatus.getFunc_Sub_Type_Id() + "%' " +
					  " and t.gj_id like '%" + GJ_Id + "%' " +
					  " order by t.ctime desc ";
				break;
			case 3:// 查询告警类型
				Sql = " select t.cpm_id, t.equip_id, t.cname, t.attr_id, t.attr_name, t.level, t.ctime, t.cdata, t.gj_id, t.project_id, t.status, t.unit, t.des, t.longitude, t.latitude " + 
					  " from view_alert_info t group by t.attr_id";
				break;
			case 10:// 修改状态
				Sql = " update alert_info set status = '1' where cpm_id = '" + Cpm_Id + "'" +
					  " and id = '" + Equip_Id + "'" +
					  " and attr_id = '" + currStatus.getFunc_Sub_Type_Id() + "'" +
					  " and gj_id = '" + GJ_Id + "'";
				break;
		}
		return Sql;
	}

	/**
	 * 将数据库中 结果集的数据 封装到DevGjBean中
	 * 
	 */
	public boolean getData(ResultSet pRs)
	{
		boolean IsOK = true;
		try
		{
			setCpm_Id(pRs.getString(1));
			setEquip_Id(pRs.getString(2));
			setCName(pRs.getString(3));
			setAttr_Id(pRs.getString(4));
			setAttr_Name(pRs.getString(5));
			setLevel(pRs.getString(6));
			setCTime(pRs.getString(7));
			setCData(pRs.getString(8));
			setGJ_Id(pRs.getString(9));
			setProject_Id(pRs.getString(10));
			setStatus(pRs.getString(11));
			setUnit(pRs.getString(12));
			setDes(pRs.getString(13));
			setLongitude(pRs.getString(14));
			setLatitude(pRs.getString(15));
		}
		catch (SQLException sqlExp)
		{
			sqlExp.printStackTrace();
		}
		return IsOK;
	}

	/**
	 * 得到页面数据
	 * 
	 * @param request
	 * @return 
	 */
	public boolean getHtmlData(HttpServletRequest request)
	{
		boolean IsOK = true;
		try
		{
			setSid(CommUtil.StrToGB2312(request.getParameter("Sid")));
			setCpm_Id(CommUtil.StrToGB2312(request.getParameter("Cpm_Id")));
			setEquip_Id(CommUtil.StrToGB2312(request.getParameter("Equip_Id")));
			setCName(CommUtil.StrToGB2312(request.getParameter("CName")));
			setAttr_Id(CommUtil.StrToGB2312(request.getParameter("Attr_Id")));
			setAttr_Name(CommUtil.StrToGB2312(request.getParameter("Attr_Name")));
			setLevel(CommUtil.StrToGB2312(request.getParameter("Level")));
			setCTime(CommUtil.StrToGB2312(request.getParameter("CTime")));
			setCData(CommUtil.StrToGB2312(request.getParameter("CData")));
			setGJ_Id(CommUtil.StrToGB2312(request.getParameter("GJ_Id")));
			setProject_Id(CommUtil.StrToGB2312(request.getParameter("Project_Id")));
			setStatus(CommUtil.StrToGB2312(request.getParameter("Status")));
			setUnit(CommUtil.StrToGB2312(request.getParameter("Unit")));
			setDes(CommUtil.StrToGB2312(request.getParameter("Des")));
			setLongitude(CommUtil.StrToGB2312(request.getParameter("Longitude")));
			setLatitude(CommUtil.StrToGB2312(request.getParameter("Latitude")));
		}
		catch (Exception Exp)
		{
			Exp.printStackTrace();
		}
		return IsOK;
	}
	
	private String Cpm_Id;
	private String Equip_Id;
	private String CName;
	private String Attr_Id;
	private String Attr_Name;
	private String Level;
	private String CTime;
	private String CData;
	private String GJ_Id;
	private String Project_Id;
	private String Status;
	private String Unit;
	private String Des;

	private String Longitude;
	private String Latitude;

	private String Sid;

	public String getLongitude()
	{
		return Longitude;
	}

	public void setLongitude(String longitude)
	{
		Longitude = longitude;
	}

	public String getLatitude()
	{
		return Latitude;
	}

	public void setLatitude(String latitude)
	{
		Latitude = latitude;
	}

	public String getCpm_Id()
	{
		return Cpm_Id;
	}

	public void setCpm_Id(String cpm_Id)
	{
		Cpm_Id = cpm_Id;
	}

	public String getProject_Id()
	{
		return Project_Id;
	}

	public void setProject_Id(String project_Id)
	{
		Project_Id = project_Id;
	}

	public String getEquip_Id()
	{
		return Equip_Id;
	}

	public void setEquip_Id(String equip_Id)
	{
		Equip_Id = equip_Id;
	}

	public String getCName()
	{
		return CName;
	}

	public void setCName(String cName)
	{
		CName = cName;
	}

	public String getAttr_Id()
	{
		return Attr_Id;
	}

	public void setAttr_Id(String attr_Id)
	{
		Attr_Id = attr_Id;
	}

	public String getAttr_Name()
	{
		return Attr_Name;
	}

	public void setAttr_Name(String attr_Name)
	{
		Attr_Name = attr_Name;
	}

	public String getLevel()
	{
		return Level;
	}

	public void setLevel(String level)
	{
		Level = level;
	}

	public String getCTime()
	{
		return CTime;
	}

	public void setCTime(String cTime)
	{
		CTime = cTime;
	}

	public String getCData()
	{
		return CData;
	}

	public void setCData(String cData)
	{
		CData = cData;
	}

	public String getGJ_Id()
	{
		return GJ_Id;
	}

	public void setGJ_Id(String gJ_Id)
	{
		GJ_Id = gJ_Id;
	}

	public String getStatus()
	{
		return Status;
	}

	public void setStatus(String status)
	{
		Status = status;
	}

	public String getUnit()
	{
		return Unit;
	}

	public void setUnit(String unit)
	{
		Unit = unit;
	}

	public String getDes()
	{
		return Des;
	}

	public void setDes(String des)
	{
		Des = des;
	}

	public String getSid()
	{
		return Sid;
	}

	public void setSid(String sid)
	{
		Sid = sid;
	}

	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}
	
}