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

public class DevBZBean extends RmiBean
{
	public final static long	serialVersionUID	= RmiBean.RMI_DEVBZ;

	public long getClassId()
	{
		return serialVersionUID;
	}

	public DevBZBean()
	{
		super.className = "DevBZBean";
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
				currStatus.setResult(MsgBean.GetResult(msgBean.getStatus()));
				msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25); // 得到一个封装了结果集的MsgBean对象
				pRmi.Client(2001,"0000000001","");
				break;
			case 12:// 删除
			case 11:// 编辑
				currStatus.setResult(MsgBean.GetResult(msgBean.getStatus()));
				msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25); // 得到一个封装了结果集的MsgBean对象
				pRmi.Client(2001,"0000000001","");
			case 0:// Admin查询
				msgBean = pRmi.RmiExec(0, this, currStatus.getCurrPage(), 25);
				currStatus.setTotalRecord(msgBean.getCount());
				request.getSession().setAttribute("Admin_DevGJ_Info_" + Sid, (Object) msgBean.getMsg());
				currStatus.setJsp("Dev_GJ.jsp?Sid=" + Sid);
				break;
			case 1:// User查询
				msgBean = pRmi.RmiExec(0, this, currStatus.getCurrPage(), 25);
				currStatus.setTotalRecord(msgBean.getCount());
				request.getSession().setAttribute("User_DevGJ_Info_" + Sid, (Object) msgBean.getMsg());
				currStatus.setJsp("Dev_GJ.jsp?Sid=" + Sid);
				break;
			case 3:// User单个查询
				msgBean = pRmi.RmiExec(3, this, 0, 25);
				request.getSession().setAttribute("User_DevGJ_Info_" + Sid, (DevBZBean) ((ArrayList<?>) msgBean.getMsg()).get(0));
				currStatus.setJsp("User_DevGJ_Info.jsp?Sid=" + Sid + "&Id=" + Id);
				break;
			case 4:// User单个查询
				msgBean = pRmi.RmiExec(3, this, 0, 25);
				request.getSession().setAttribute("User_DevGJ_Info_" + Sid, (DevBZBean) ((ArrayList<?>) msgBean.getMsg()).get(0));
				currStatus.setJsp("User_One_GJ.jsp?Sid=" + Sid + "&Id=" + Id);
				break;
			case 6:// Admin查询单个
				msgBean = pRmi.RmiExec(6, this, 0, 25);
				request.getSession().setAttribute("Admin_DevGJ_Info_" + Sid, (DevBZBean) ((ArrayList<?>) msgBean.getMsg()).get(0));
				currStatus.setJsp("Admin_DevGJ_Info.jsp?Sid=" + Sid);
				break;
		}
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		response.sendRedirect(currStatus.getJsp());
	}
	/**
	 * 地图接口 的 doDragging、doAddMarke、doDel
	 * 
	 * @param request
	 * @param response
	 * @param pRmi
	 * @param pFromZone
	 * @throws ServletException
	 * @throws IOException
	 */
	public void doDragging(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);

		PrintWriter outprint = response.getWriter();
		String Resp = "9999";

		msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
		if (msgBean.getStatus() == MsgBean.STA_SUCCESS)
		{
			Resp = "0000";
			msgBean = pRmi.RmiExec(0, this, 0, 25);
			request.getSession().setAttribute("Dev_GJ_" + Sid, ((Object) msgBean.getMsg()));
		}

		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		outprint.write(Resp);
	}

	/**
	 * 管线命名
	 * 
	 * @param GJ_Id
	 * @return
	 */
	public String dealGXID(String GJ_Id)
	{
		String temGJ_Id = ""; // "WJ", "WG" "YJ", "YG"
		if (GJ_Id.contains("WJ"))
		{
			temGJ_Id = GJ_Id.replace("WJ", "WG");
		}
		if (GJ_Id.contains("YJ"))
		{
			temGJ_Id = GJ_Id.replace("YJ", "YG");
		}
		return temGJ_Id;
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
			case 0:// 查询（类型&项目）
				Sql = " select t.id, t.Longitude, t.latitude, t.top_Height, t.base_height, t.in_id, t.in_name, t.out_id, t.out_name, t.sign , t.project_id, t.project_name, t.Flag, t.equip_id ,t.equip_name ,t.equip_height ,t.equip_tel, t.curr_data, t.equip_time, t.road" + 
					  " from view_dev_bz t where t.id like '%" + currStatus.getFunc_Sub_Type_Id() + "%' " + " and t.project_id = '" + currStatus.getFunc_Project_Id() + "' " + " order by t.id  ";
				break;
			case 1:// 查询（单个）
				Sql = " select t.id, t.Longitude, t.latitude, t.top_Height, t.base_height, t.in_id, t.in_name, t.out_id, t.out_name, t.sign , t.project_id, t.project_name, t.Flag, t.equip_id ,t.equip_name ,t.equip_height ,t.equip_tel, t.curr_data, t.equip_time, t.road" + 
					  " from view_dev_bz t " + " where t.id = '" + Id + "' and t.project_id = '" + currStatus.getFunc_Project_Id() + "'" + " order by t.id  ";
				break;
			case 2:// 查询（多个）
				Sql = " select t.id, t.Longitude, t.latitude, t.top_Height, t.base_height, t.in_id, t.in_name, t.out_id, t.out_name, t.sign , t.project_id, t.project_name, t.Flag, t.equip_id ,t.equip_name ,t.equip_height ,t.equip_tel, t.curr_data, t.equip_time, t.road" + 
					  " from view_dev_bz t " + " where instr('" + Id + "', t.id) > 0 and t.project_id = '" + currStatus.getFunc_Project_Id() + "'" + " order by t.id  ";
				break;
			case 10:// 添加
				Sql = " insert into dev_bz(id, top_Height, base_height, in_id, in_name, out_id, out_name, Flag, project_id, road) " + 
					  " values('" + Id + "','" + Top_Height + "','" + Base_Height + "','" + In_Id + "','" + In_Name + "','" + Out_Id + "','" + Out_Name + "','" + Flag + "','" + Project_Id + "','" + Road + "')";
				break;
			case 11:// 编辑
				Sql = " update dev_bz t set t.in_id= '" + In_Id + "', t.in_name = '" + In_Name + "', t.out_id = '" + Out_Id + "', t.out_Name = '" + Out_Name + "' ,t.top_height= '" + Top_Height + "', t.base_height = '" + Base_Height + "', t.Flag = '" + Flag + "', t.equip_name = '" + Equip_Name + "',t.equip_height = '" + Equip_Height + "',t.equip_tel = '" + Equip_Tel + "',t.road = '" + Road + "' " + " where t.id = '" + Id + "' and t.project_id = '" + currStatus.getFunc_Project_Id() + "'";
				break;
			case 12:// 删除
				Sql = " delete from dev_bz where id = '" + Id + "' and project_id = '" + currStatus.getFunc_Project_Id() + "'";
				break;

			case 13:// 管井更新
				Sql = " update dev_bz t set t.in_id= '" + In_Id + "', t.out_id = '" + Out_Id + "' ,t.top_height= '" + Top_Height + "', t.base_height = '" + Base_Height + "', t.Flag = '" + Flag + "',t.road = '" + Road + "' " + " where t.id = '" + Id + "' and t.project_id = '" + Project_Id + "'";
				break;
			case 15:// 地图拖拽同步更新
				Sql = " update dev_bz t set t.longitude = '" + Longitude + "', t.latitude = '" + Latitude + "' " + " where t.id = '" + Id + "' and t.project_id = '" + Project_Id + "'";
				break;
			case 16:// 删除标注接口
				Sql = " update dev_bz t set t.sign = '0' " + " where t.id = '" + Id + "' and t.project_id = '" + Project_Id + "'";
				break;
			case 17:// 添加标注接口
				Sql = " update dev_bz t set t.sign = '1', t.longitude = '" + Longitude + "', t.latitude = '" + Latitude + "' " + " where t.id = '" + Id + "' and t.project_id = '" + Project_Id + "'";
				break;
			case 21:// 获取已标注管井
				Sql = "{? = call Func_GJ_Get('" + Id + "', '" + Road + "')}";
				break;
			case 22:// 获取已标注管井
				Sql = "{? = call Func_GJ_Analog('" + Id + "','" + currStatus.getFunc_Sub_Type_Id() + "')}";
				break;
			case 23:// 获取未标注管井
				Sql = "{? = call Func_UnMark_GJ_Get('" + Project_Id + "')}";
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
			setLongitude(pRs.getString(2));
			setLatitude(pRs.getString(3));
			setTop_Height(pRs.getString(4));
			setBase_Height(pRs.getString(5));
			setIn_Id(pRs.getString(6));
			setIn_Name(pRs.getString(7));
			setOut_Id(pRs.getString(8));
			setOut_Name(pRs.getString(9));
			setSign(pRs.getString(10));
			setProject_Id(pRs.getString(11));
			setProject_Name(pRs.getString(12));
			setFlag(pRs.getString(13));
			setEquip_Id(pRs.getString(14));
			setEquip_Name(pRs.getString(15));
			setEquip_Height(pRs.getString(16));
			setEquip_Tel(pRs.getString(17));
			setEquip_Time(pRs.getString(18));
			setCurr_Data(pRs.getString(19));
			setRoad(pRs.getString(20));
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
			setLongitude(CommUtil.StrToGB2312(request.getParameter("Longitude")));
			setLatitude(CommUtil.StrToGB2312(request.getParameter("Latitude")));
			setTop_Height(CommUtil.StrToGB2312(request.getParameter("Top_Height")));
			setBase_Height(CommUtil.StrToGB2312(request.getParameter("Base_Height")));
			setIn_Id(CommUtil.StrToGB2312(request.getParameter("In_Id")));
			setIn_Name(CommUtil.StrToGB2312(request.getParameter("In_Name")));
			setOut_Id(CommUtil.StrToGB2312(request.getParameter("Out_Id")));
			setOut_Name(CommUtil.StrToGB2312(request.getParameter("Out_Name")));
			setSign(CommUtil.StrToGB2312(request.getParameter("Sign")));
			setProject_Id(CommUtil.StrToGB2312(request.getParameter("Project_Id")));
			setFlag(CommUtil.StrToGB2312(request.getParameter("Flag")));
			setEquip_Id(CommUtil.StrToGB2312(request.getParameter("Equip_Id")));
			setEquip_Name(CommUtil.StrToGB2312(request.getParameter("Equip_Name")));
			setEquip_Height(CommUtil.StrToGB2312(request.getParameter("Equip_Height")));
			setEquip_Tel(CommUtil.StrToGB2312(request.getParameter("Equip_Tel")));
			setCurr_Data(CommUtil.StrToGB2312(request.getParameter("Curr_Data")));
			setEquip_Time(CommUtil.StrToGB2312(request.getParameter("Equip_Time")));
			setRoad(CommUtil.StrToGB2312(request.getParameter("Road")));
		}
		catch (Exception Exp)
		{
			Exp.printStackTrace();
		}
		return IsOK;
	}

	private String	Id;
	private String	Longitude;
	private String	Latitude;
	private String	Top_Height;
	private String	Base_Height;
	private String	In_Id;
	private String	In_Name;
	private String	Out_Id;
	private String	Out_Name;
	private String	Sign;
	private String	Project_Id;
	private String	Project_Name;
	private String	Flag;
	private String	Equip_Id;
	private String	Equip_Name;
	private String	Equip_Height;
	private String	Equip_Tel;
	private String	Curr_Data;
	private String	Equip_Time;
	private String	Road;
	
	private String	Sid;

	public String getId()
	{
		return Id;
	}

	public void setId(String id)
	{
		Id = id;
	}

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

	public String getTop_Height()
	{
		return Top_Height;
	}

	public void setTop_Height(String top_Height)
	{
		Top_Height = top_Height;
	}

	public String getBase_Height()
	{
		return Base_Height;
	}

	public void setBase_Height(String base_Height)
	{
		Base_Height = base_Height;
	}

	public String getIn_Id()
	{
		return In_Id;
	}

	public void setIn_Id(String in_Id)
	{
		In_Id = in_Id;
	}

	public String getIn_Name()
	{
		return In_Name;
	}

	public void setIn_Name(String in_Name)
	{
		In_Name = in_Name;
	}

	public String getOut_Id()
	{
		return Out_Id;
	}

	public void setOut_Id(String out_Id)
	{
		Out_Id = out_Id;
	}

	public String getOut_Name()
	{
		return Out_Name;
	}

	public void setOut_Name(String out_Name)
	{
		Out_Name = out_Name;
	}

	public String getSign()
	{
		return Sign;
	}

	public void setSign(String sign)
	{
		Sign = sign;
	}

	public String getProject_Id()
	{
		return Project_Id;
	}

	public void setProject_Id(String project_Id)
	{
		Project_Id = project_Id;
	}

	public String getProject_Name()
	{
		return Project_Name;
	}

	public void setProject_Name(String project_Name)
	{
		Project_Name = project_Name;
	}

	public String getFlag()
	{
		return Flag;
	}

	public void setFlag(String flag)
	{
		Flag = flag;
	}

	public String getEquip_Id()
	{
		return Equip_Id;
	}

	public void setEquip_Id(String equip_Id)
	{
		Equip_Id = equip_Id;
	}

	public String getEquip_Name()
	{
		return Equip_Name;
	}

	public void setEquip_Name(String equip_Name)
	{
		Equip_Name = equip_Name;
	}

	public String getEquip_Height()
	{
		return Equip_Height;
	}

	public void setEquip_Height(String equip_Height)
	{
		Equip_Height = equip_Height;
	}

	public String getEquip_Tel()
	{
		return Equip_Tel;
	}

	public void setEquip_Tel(String equip_Tel)
	{
		Equip_Tel = equip_Tel;
	}

	public String getCurr_Data()
	{
		return Curr_Data;
	}

	public void setCurr_Data(String curr_Data)
	{
		Curr_Data = curr_Data;
	}

	public String getEquip_Time()
	{
		return Equip_Time;
	}

	public void setEquip_Time(String equip_Time)
	{
		Equip_Time = equip_Time;
	}

	public String getRoad()
	{
		return Road;
	}

	public void setRoad(String road)
	{
		Road = road;
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