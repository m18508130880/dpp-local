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
		System.out.println("Sid["+Sid+"]");
		currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		DevGJBean devGJBean = new DevGJBean();
		devGJBean.setId(Id);
		devGJBean.setLongitude(Longitude);
		devGJBean.setLatitude(Latitude);
		devGJBean.setTop_Height(Top_Height);
		devGJBean.setBase_Height(Base_Height);
		devGJBean.setIn_Id(In_Id);
		devGJBean.setOut_Id(Out_Id);
		devGJBean.setSign(Sign);
		devGJBean.setProject_Id(currStatus.getFunc_Project_Id());
		devGJBean.setEquip_Id(Equip_Id);
		devGJBean.setEquip_Name(Equip_Name);
		devGJBean.setEquip_Tel(Equip_Tel);
		devGJBean.setEquip_Height(Equip_Height);
		devGJBean.setRoad(Road);
		devGJBean.setSid(Sid);
		
		devGJBean.setSize("");
		devGJBean.setMaterial("");
		devGJBean.setFlag("");
		devGJBean.setData_Lev("");
		switch (currStatus.getCmd())
		{
			case 12:// 删除
			case 11:// 编辑
				devGJBean.setProject_Id(Project_Id);
				currStatus.setResult(MsgBean.GetResult(msgBean.getStatus()));
				msgBean = pRmi.RmiExec(19, devGJBean, 0, 0);
				msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 0);
				pRmi.Client(2001,"0000000001","");
				msgBean = pRmi.RmiExec(1, this, currStatus.getCurrPage(), 25);
				request.getSession().setAttribute("Admin_DevBZ_Info_" + Sid, (ArrayList<?>) msgBean.getMsg());
				currStatus.setJsp("Dev_BZ.jsp?Sid=" + Sid);
				break;
			case 0:// admin根据项目查询
				msgBean = pRmi.RmiExec(0, this, currStatus.getCurrPage(), 25);
				currStatus.setTotalRecord(msgBean.getCount());
				request.getSession().setAttribute("Admin_DevBZ_Info_" + Sid, (ArrayList<?>) msgBean.getMsg());
				currStatus.setJsp("Dev_BZ.jsp?Sid=" + Sid);
				break;
			case 10:// 添加
				devGJBean.setProject_Id(Project_Id);
				currStatus.setResult(MsgBean.GetResult(msgBean.getStatus()));
				msgBean = pRmi.RmiExec(currStatus.getCmd(), devGJBean, 0, 0);
				msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 0);
				pRmi.Client(2001,"0000000001","");
			case 1:// admin单个查询
				msgBean = pRmi.RmiExec(1, this, 0, 0);
				request.getSession().setAttribute("Admin_DevBZ_Info_" + Sid, (ArrayList<?>) msgBean.getMsg());
				currStatus.setJsp("Dev_BZ.jsp?Sid=" + Sid);
				break;
			case 2:// user单个查询
				msgBean = pRmi.RmiExec(1, this, 0, 0);
				request.getSession().setAttribute("User_DevBZ_Info_" + Sid, (DevBZBean)((ArrayList<?>) msgBean.getMsg()).get(0));
				currStatus.setJsp("User_DevBZ_Info.jsp?Sid=" + Sid + "&Id=" + Id + "&Project_Id=" + currStatus.getFunc_Project_Id());
				break;
		}
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		response.sendRedirect(currStatus.getJsp());
	}
	public void CutData(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		switch (currStatus.getCmd())
		{
			case 1://剖面图
				msgBean = pRmi.RmiExec(1, this, 0, 0);
				request.getSession().setAttribute("User_DevBZ_Cut_" + Sid, (DevBZBean)((ArrayList<?>) msgBean.getMsg()).get(0));
				currStatus.setJsp("User_DevBZ_Cut.jsp?Sid=" + Sid + "&Id=" + Id);
				break;
		}
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		response.sendRedirect(currStatus.getJsp());
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
				Sql = " select t.id, t.Longitude, t.latitude, t.top_Height, t.base_height, t.in_id, t.out_id, t.sign , t.project_id, t.project_name, t.Flag, t.equip_id ,t.equip_name ,t.equip_height ,t.equip_tel, t.curr_data, t.equip_time, t.road, t.front_name, t.front_top, t.front_base, t.front_diameter, t.front_size, t.front_start, t.front_end, t.front_equip, t.front_height, t.front_tel, t.front_data, t.back_name, t.back_top, t.back_base, t.back_diameter, t.back_size, t.back_start, t.back_end, t.back_equip, t.back_height, t.back_tel, t.back_data " + 
					  " from view_dev_bz t where t.id like '%" + currStatus.getFunc_Sub_Type_Id() + "%' " + " and t.project_id = '" + currStatus.getFunc_Project_Id() + "' " + " order by t.id  ";
				break;
			case 1:// 查询（单个）
				Sql = " select t.id, t.Longitude, t.latitude, t.top_Height, t.base_height, t.in_id, t.out_id, t.sign , t.project_id, t.project_name, t.Flag, t.equip_id ,t.equip_name ,t.equip_height ,t.equip_tel, t.curr_data, t.equip_time, t.road, t.front_name, t.front_top, t.front_base, t.front_diameter, t.front_size, t.front_start, t.front_end, t.front_equip, t.front_height, t.front_tel, t.front_data, t.back_name, t.back_top, t.back_base, t.back_diameter, t.back_size, t.back_start, t.back_end, t.back_equip, t.back_height, t.back_tel, t.back_data " + 
					  " from view_dev_bz t " + " where t.id = '" + Id + "' and t.project_id = '" + currStatus.getFunc_Project_Id() + "'" + " order by t.id  ";
				break;
			case 2:// 查询（多个）
				Sql = " select t.id, t.Longitude, t.latitude, t.top_Height, t.base_height, t.in_id, t.out_id, t.sign , t.project_id, t.project_name, t.Flag, t.equip_id ,t.equip_name ,t.equip_height ,t.equip_tel, t.curr_data, t.equip_time, t.road, t.front_name, t.front_top, t.front_base, t.front_diameter, t.front_size, t.front_start, t.front_end, t.front_equip, t.front_height, t.front_tel, t.front_data, t.back_name, t.back_top, t.back_base, t.back_diameter, t.back_size, t.back_start, t.back_end, t.back_equip, t.back_height, t.back_tel, t.back_data " + 
					  " from view_dev_bz t " + " where instr('" + Id + "', t.id) > 0 and t.project_id = '" + currStatus.getFunc_Project_Id() + "'" + " order by t.id  ";
				break;
			case 10:// 添加
				Sql = " insert into dev_bz(id, Flag, project_id, front_name, front_top, front_base, front_diameter, front_size, front_start, front_end, front_equip, front_height, front_tel, back_name, back_top, back_base, back_diameter, back_size, back_start, back_end, back_equip, back_height, back_tel) " + 
					  " values('" + Id + "','" + Flag + "','" + Project_Id + "','"+ Front_Name + "','"+ Front_Top + "','"+ Front_Base + "','"+ Front_Diameter + "','"+ Front_Size + "','"+ Front_Start + "','"+ Front_End + "','"+ Front_Equip + "','"+ Front_Height + "','"+ Front_Tel + "','"+ Back_Name + "','"+ Back_Top + "','"+ Back_Base + "','"+ Back_Diameter + "','"+ Back_Size + "','"+ Back_Start + "','"+ Back_End + "','"+ Back_Equip + "','"+ Back_Height + "','"+ Back_Tel + "')";
				break;
			case 11:// 编辑
				Sql = " update dev_bz t set t.Flag = '" + Flag + "', front_name = '" + Front_Name + "', front_top = '" + Front_Top + "', front_base = '"+Front_Base+"', front_diameter = '" + Front_Diameter + "', front_size = '" + Front_Size + "', front_start = '" + Front_Start + "', front_end = '" + Front_End + "', front_equip = '" + Front_Equip + "', front_height = '" + Front_Height + "', front_tel = '" + Front_Tel + "', back_name = '" + Back_Name + "', back_top = '" + Back_Top + "', back_base = '" + Back_Base + "', back_diameter = '" + Back_Diameter + "', back_size = '" + Back_Size + "', back_start = '" + Back_Start + "', back_end = '" + Back_End + "', back_equip = '" + Back_Equip + "', back_height = '" + Back_Height + "', back_tel = '" + Back_Tel + "' where t.id = '" + Id + "' and t.project_id = '" + currStatus.getFunc_Project_Id() + "'";
				break;
			case 12:// 删除
				Sql = " delete from dev_bz where id = '" + Id + "' and project_id = '" + Project_Id + "'";
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
			setOut_Id(pRs.getString(7));
			setSign(pRs.getString(8));
			setProject_Id(pRs.getString(9));
			setProject_Name(pRs.getString(10));
			setFlag(pRs.getString(11));
			setEquip_Id(pRs.getString(12));
			setEquip_Name(pRs.getString(13));
			setEquip_Height(pRs.getString(14));
			setEquip_Tel(pRs.getString(15));
			setCurr_Data(pRs.getString(16));
			setEquip_Time(pRs.getString(17));
			setRoad(pRs.getString(18));
			setFront_Name(pRs.getString(19));
			setFront_Top(pRs.getString(20));
			setFront_Base(pRs.getString(21));
			setFront_Diameter(pRs.getString(22));
			setFront_Size(pRs.getString(23));
			setFront_Start(pRs.getString(24));
			setFront_End(pRs.getString(25));
			setFront_Equip(pRs.getString(26));
			setFront_Height(pRs.getString(27));
			setFront_Tel(pRs.getString(28));
			setFront_Data(pRs.getString(29));
			setBack_Name(pRs.getString(30));
			setBack_Top(pRs.getString(31));
			setBack_Base(pRs.getString(32));
			setBack_Diameter(pRs.getString(33));
			setBack_Size(pRs.getString(34));
			setBack_Start(pRs.getString(35));
			setBack_End(pRs.getString(36));
			setBack_Equip(pRs.getString(37));
			setBack_Height(pRs.getString(38));
			setBack_Tel(pRs.getString(39));
			setBack_Data(pRs.getString(40));
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
			setFlag(CommUtil.StrToGB2312(request.getParameter("Flag")));
			setProject_Id(CommUtil.StrToGB2312(request.getParameter("Project_Id")));
			setFront_Name(CommUtil.StrToGB2312(request.getParameter("Front_Name")));
			setFront_Top(CommUtil.StrToGB2312(request.getParameter("Front_Top")));
			setFront_Base(CommUtil.StrToGB2312(request.getParameter("Front_Base")));
			setFront_Diameter(CommUtil.StrToGB2312(request.getParameter("Front_Diameter")));
			setFront_Size(CommUtil.StrToGB2312(request.getParameter("Front_Size")));
			setFront_Start(CommUtil.StrToGB2312(request.getParameter("Front_Start")));
			setFront_End(CommUtil.StrToGB2312(request.getParameter("Front_End")));
			setFront_Equip(CommUtil.StrToGB2312(request.getParameter("Front_Equip")));
			setFront_Height(CommUtil.StrToGB2312(request.getParameter("Front_Height")));
			setFront_Tel(CommUtil.StrToGB2312(request.getParameter("Front_Tel")));
			setBack_Name(CommUtil.StrToGB2312(request.getParameter("Back_Name")));
			setBack_Top(CommUtil.StrToGB2312(request.getParameter("Back_Top")));
			setBack_Base(CommUtil.StrToGB2312(request.getParameter("Back_Base")));
			setBack_Diameter(CommUtil.StrToGB2312(request.getParameter("Back_Diameter")));
			setBack_Size(CommUtil.StrToGB2312(request.getParameter("Back_Size")));
			setBack_Start(CommUtil.StrToGB2312(request.getParameter("Back_Start")));
			setBack_End(CommUtil.StrToGB2312(request.getParameter("Back_End")));
			setBack_Equip(CommUtil.StrToGB2312(request.getParameter("Back_Equip")));
			setBack_Height(CommUtil.StrToGB2312(request.getParameter("Back_Height")));
			setBack_Tel(CommUtil.StrToGB2312(request.getParameter("Back_Tel")));
			
			setLongitude(CommUtil.StrToGB2312(request.getParameter("Longitude")));
			setLatitude(CommUtil.StrToGB2312(request.getParameter("Latitude")));
			setTop_Height(CommUtil.StrToGB2312(request.getParameter("Top_Height")));
			setBase_Height(CommUtil.StrToGB2312(request.getParameter("Base_Height")));
			setIn_Id(CommUtil.StrToGB2312(request.getParameter("In_Id")));
			setOut_Id(CommUtil.StrToGB2312(request.getParameter("Out_Id")));
			setSign(CommUtil.StrToGB2312(request.getParameter("Sign")));
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
	private String	Flag;
	private String	Project_Id;
	private String	Front_Name;
	private String	Front_Top;
	private String	Front_Base;
	private String	Front_Diameter;
	private String	Front_Size;
	private String	Front_Start;
	private String	Front_End;
	private String	Front_Equip;
	private String	Front_Height;
	private String	Front_Tel;
	private String	Front_Data;
	private String	Back_Name;
	private String	Back_Top;
	private String	Back_Base;
	private String	Back_Diameter;
	private String	Back_Size;
	private String	Back_Start;
	private String	Back_End;
	private String	Back_Equip;
	private String	Back_Height;
	private String	Back_Tel;
	private String	Back_Data;

	private String	Longitude;
	private String	Latitude;
	private String	Top_Height;
	private String	Base_Height;
	private String	In_Id;
	private String	Out_Id;
	private String	Sign;
	private String	Project_Name;
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

	public String getFront_Data()
	{
		return Front_Data;
	}

	public void setFront_Data(String front_Data)
	{
		Front_Data = front_Data;
	}

	public String getBack_Data()
	{
		return Back_Data;
	}

	public void setBack_Data(String back_Data)
	{
		Back_Data = back_Data;
	}

	public String getFront_Name()
	{
		return Front_Name;
	}

	public void setFront_Name(String front_Name)
	{
		Front_Name = front_Name;
	}

	public String getFront_Top()
	{
		return Front_Top;
	}

	public void setFront_Top(String front_Top)
	{
		Front_Top = front_Top;
	}

	public String getFront_Base()
	{
		return Front_Base;
	}

	public void setFront_Base(String front_Base)
	{
		Front_Base = front_Base;
	}

	public String getFront_Diameter()
	{
		return Front_Diameter;
	}

	public void setFront_Diameter(String front_Diameter)
	{
		Front_Diameter = front_Diameter;
	}

	public String getFront_Size()
	{
		return Front_Size;
	}

	public void setFront_Size(String front_Size)
	{
		Front_Size = front_Size;
	}

	public String getFront_Start()
	{
		return Front_Start;
	}

	public void setFront_Start(String front_Start)
	{
		Front_Start = front_Start;
	}

	public String getFront_End()
	{
		return Front_End;
	}

	public void setFront_End(String front_End)
	{
		Front_End = front_End;
	}

	public String getFront_Equip()
	{
		return Front_Equip;
	}

	public void setFront_Equip(String front_Equip)
	{
		Front_Equip = front_Equip;
	}

	public String getFront_Height()
	{
		return Front_Height;
	}

	public void setFront_Height(String front_Height)
	{
		Front_Height = front_Height;
	}

	public String getFront_Tel()
	{
		return Front_Tel;
	}

	public void setFront_Tel(String front_Tel)
	{
		Front_Tel = front_Tel;
	}

	public String getBack_Name()
	{
		return Back_Name;
	}

	public void setBack_Name(String back_Name)
	{
		Back_Name = back_Name;
	}

	public String getBack_Top()
	{
		return Back_Top;
	}

	public void setBack_Top(String back_Top)
	{
		Back_Top = back_Top;
	}

	public String getBack_Base()
	{
		return Back_Base;
	}

	public void setBack_Base(String back_Base)
	{
		Back_Base = back_Base;
	}

	public String getBack_Diameter()
	{
		return Back_Diameter;
	}

	public void setBack_Diameter(String back_Diameter)
	{
		Back_Diameter = back_Diameter;
	}

	public String getBack_Size()
	{
		return Back_Size;
	}

	public void setBack_Size(String back_Size)
	{
		Back_Size = back_Size;
	}

	public String getBack_Start()
	{
		return Back_Start;
	}

	public void setBack_Start(String back_Start)
	{
		Back_Start = back_Start;
	}

	public String getBack_End()
	{
		return Back_End;
	}

	public void setBack_End(String back_End)
	{
		Back_End = back_End;
	}

	public String getBack_Equip()
	{
		return Back_Equip;
	}

	public void setBack_Equip(String back_Equip)
	{
		Back_Equip = back_Equip;
	}

	public String getBack_Height()
	{
		return Back_Height;
	}

	public void setBack_Height(String back_Height)
	{
		Back_Height = back_Height;
	}

	public String getBack_Tel()
	{
		return Back_Tel;
	}

	public void setBack_Tel(String back_Tel)
	{
		Back_Tel = back_Tel;
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

	public String getOut_Id()
	{
		return Out_Id;
	}

	public void setOut_Id(String out_Id)
	{
		Out_Id = out_Id;
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