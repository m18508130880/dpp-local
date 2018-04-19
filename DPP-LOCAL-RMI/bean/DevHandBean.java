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

public class DevHandBean extends RmiBean
{
	public final static long	serialVersionUID	= RmiBean.RMI_DEVHAND;

	public long getClassId()
	{
		return serialVersionUID;
	}

	public DevHandBean()
	{
		super.className = "DevHandBean";
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
		
		switch (currStatus.getCmd())
		{
			case 10:// 添加
			case 11:// 编辑
			case 12:// 删除
				
				break;
			case 0:// 根据项目查询
				
				break;
			case 1:// 单个查询
				
				break;
		}
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		response.sendRedirect(currStatus.getJsp());
	}
	public void getHand(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		PrintWriter outprint = response.getWriter();
		String Resp = "9999";
		
		ArrayList<?> DevHand = new ArrayList();
		msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
		if (msgBean.getStatus() == MsgBean.STA_SUCCESS)
		{
			Resp = "0000";
			DevHand = (ArrayList<?>) msgBean.getMsg();
			Iterator<?> iterator = DevHand.iterator();
			while (iterator.hasNext())
			{
				DevHandBean hlBean = (DevHandBean)iterator.next();
				Id = hlBean.getId();
				Longitude = hlBean.getLongitude();
				Latitude = hlBean.getLatitude();
				Value = hlBean.getValue();
				Des = hlBean.getDes();
				Resp += Id + "|" + Longitude + "|" + Longitude + "|" + Value + ";";
			}
		}
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		outprint.write(Resp);
	}
//	public void updateHLData(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
//	{
//		getHtmlData(request);
//		currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
//		currStatus.getHtmlData(request, pFromZone);
//		PrintWriter outprint = response.getWriter();
//		String Resp = "9999";
//		
//		msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
//		if (msgBean.getStatus() == MsgBean.STA_SUCCESS)
//		{
//			Resp = "0000";
//		}
//		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
//		outprint.write(Resp);
//	}

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
				Sql = " select t.id,t.project_id, t.Longitude, t.Latitude, t.Equip_Id, t.Value, t.CTime, t.des" +
					  " from view_dev_hand t " + 
					  " where t.project_id = '" + currStatus.getFunc_Project_Id() + "'" + 
					  " order by t.id  ";
				break;
			case 1:// 查询单个
				Sql = " select t.id,t.project_id, t.Longitude, t.Latitude, t.Equip_Id, t.Value, t.CTime, t.des" +
					  " from view_dev_hand t " +
					  " where t.project_id = '" + currStatus.getFunc_Project_Id() + "'" + 
					  " and t.id = '" + Id + "'" +
					  " order by t.id  ";
				break;
			case 10:// 添加
				Sql = " insert into dev_hand(id, project_id, Equip_Id) " + 
					  " values('" + Id + "','" + Project_Id + "','" + Equip_Id + "')";
				break;
			case 11:// 编辑 alert_hand 表
				Sql = " update alert_hand t set " +
					  " t.status = '1', "+ 
					  " t.ctime = '" + CTime + "', "+ 
					  " where t.id = '" + Id + "' " + 
					  " and t.project_id = '" + currStatus.getFunc_Project_Id() + "'" +
					  " and t.equip_id = '" + Equip_Id + "'";
				break;
			case 12:// 删除
				Sql = " delete from dev_hand where id = '" + Id + "' and project_id = '" + currStatus.getFunc_Project_Id() + "' and equip_id = '" + Equip_Id + "'";
				break;
			case 13:// 编辑 data_hand 表
				Sql = " insert into data_hand(id, project_id, equip_id, ctime, value)" +
					  " values('" + Id + "', '" + Project_Id + "', '" + Equip_Id + "', '" + CTime + "', '" + Value + "')";
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
			setId(pRs.getString(1));
			setProject_Id(pRs.getString(2));
			setLongitude(pRs.getString(3));
			setLatitude(pRs.getString(4));
			setEquip_Id(pRs.getString(5));
			setValue(pRs.getString(6));
			setCTime(pRs.getString(7));
			setDes(pRs.getString(8));
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
			
			setId(CommUtil.StrToGB2312(request.getParameter("Id")));
			setProject_Id(CommUtil.StrToGB2312(request.getParameter("Project_Id")));
			setLongitude(CommUtil.StrToGB2312(request.getParameter("Longitude")));
			setLatitude(CommUtil.StrToGB2312(request.getParameter("Latitude")));
			setEquip_Id(CommUtil.StrToGB2312(request.getParameter("Equip_Id")));
			setValue(CommUtil.StrToGB2312(request.getParameter("Value")));
			setCTime(CommUtil.StrToGB2312(request.getParameter("CTime")));
			setDes(CommUtil.StrToGB2312(request.getParameter("Des")));

			setStatus(CommUtil.StrToGB2312(request.getParameter("Status")));
			
		}
		catch (Exception Exp)
		{
			Exp.printStackTrace();
		}
		return IsOK;
	}

	private String	Id;
	private String	Project_Id;
	private String	Longitude;
	private String	Latitude;
	private String	Equip_Id;
	private String	Value;
	private String	CTime;
	private String	Des;

	private String	Status;
	
	private String	Sid;

	public String getId()
	{
		return Id;
	}

	public void setId(String id)
	{
		Id = id;
	}

	public String getProject_Id()
	{
		return Project_Id;
	}

	public void setProject_Id(String project_Id)
	{
		Project_Id = project_Id;
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

	public String getEquip_Id() {
		return Equip_Id;
	}

	public void setEquip_Id(String equip_Id) {
		Equip_Id = equip_Id;
	}

	public String getValue() {
		return Value;
	}

	public void setValue(String value) {
		Value = value;
	}

	public String getCTime() {
		return CTime;
	}

	public void setCTime(String cTime) {
		CTime = cTime;
	}

	public String getDes()
	{
		return Des;
	}

	public void setDes(String des)
	{
		Des = des;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
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