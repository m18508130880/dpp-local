package bean;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jspsmart.upload.SmartUpload;

import rmi.Rmi;
import rmi.RmiBean;
import util.*;
import jxl.Sheet;
import jxl.Workbook;

public class DevMapBean extends RmiBean
{
	public final static long	serialVersionUID	= RmiBean.RMI_DEVMAP;

	public long getClassId()
	{
		return serialVersionUID;
	}

	public DevMapBean()
	{
		super.className = "DevMapBean";
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
	public void getHLData(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		PrintWriter outprint = response.getWriter();
		String Resp = "9999";
		
		ArrayList<?> DevHL = new ArrayList();
		msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
		if (msgBean.getStatus() == MsgBean.STA_SUCCESS)
		{
			Resp = "0000";
			DevHL = (ArrayList<?>) msgBean.getMsg();
			Iterator<?> hl_iterator = DevHL.iterator();
			while (hl_iterator.hasNext())
			{
				DevMapBean hlBean = (DevMapBean)hl_iterator.next();
				Id = hlBean.getId();
				LngAndLat = hlBean.getLngAndLat();
				Des = hlBean.getDes();
				Resp += Id + "|" + LngAndLat + "|" + Des + "#";
			}
		}

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
			case 0:// admin查询（类型&项目）
				Sql = " select t.id,t.project_id, t.project_name, t.type, t.cname, t.lngandlat, t.des" +
					  " from view_dev_hl t where t.project_id = '" + currStatus.getFunc_Project_Id() + "'" + " order by t.id  ";
				break;
			case 10:// 添加
				Sql = " insert into dev_hl(id, project_Id, type, CName, LngAndLat, des) " + 
					  " values('" + Id + "','" + Project_Id + "','" + Type + "','"+ CName + "','"+ LngAndLat + "','"+ Des + "')";
				break;
			case 11:// 编辑
				Sql = " update dev_hl t set t.project_Id = '" + Project_Id + "', type = '" + Type + "', CName = '" + CName + "', LngAndLat = '"+LngAndLat+"', des = '" + Des + "' where t.id = '" + Id + "' and t.project_id = '" + currStatus.getFunc_Project_Id() + "'";
				break;
			case 12:// 删除
				Sql = " delete from dev_hl where id = '" + Id + "' and project_id = '" + currStatus.getFunc_Project_Id() + "'";
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
			setProject_Name(pRs.getString(3));
			setType(pRs.getString(4));
			setCName(pRs.getString(5));
			setLngAndLat(pRs.getString(6));
			setDes(pRs.getString(7));
			
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
			setProject_Name(CommUtil.StrToGB2312(request.getParameter("Project_Name")));
			setType(CommUtil.StrToGB2312(request.getParameter("Type")));
			setCName(CommUtil.StrToGB2312(request.getParameter("CName")));
			setLngAndLat(CommUtil.StrToGB2312(request.getParameter("LngAndLat")));
			setDes(CommUtil.StrToGB2312(request.getParameter("Des")));
			
		}
		catch (Exception Exp)
		{
			Exp.printStackTrace();
		}
		return IsOK;
	}

	private String	Id;
	private String	Project_Id;
	private String	Project_Name;
	private String	Type;
	private String	CName;
	private String	LngAndLat;
	private String	Des;
	
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

	public String getType()
	{
		return Type;
	}

	public void setType(String type)
	{
		Type = type;
	}

	public String getCName()
	{
		return CName;
	}

	public void setCName(String cName)
	{
		CName = cName;
	}

	public String getLngAndLat()
	{
		return LngAndLat;
	}

	public void setLngAndLat(String lngAndLat)
	{
		LngAndLat = lngAndLat;
	}

	public String getDes()
	{
		return Des;
	}

	public void setDes(String des)
	{
		Des = des;
	}

	public String getProject_Name()
	{
		return Project_Name;
	}

	public void setProject_Name(String project_Name)
	{
		Project_Name = project_Name;
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