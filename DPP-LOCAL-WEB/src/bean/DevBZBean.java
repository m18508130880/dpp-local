package bean;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rmi.Rmi;
import rmi.RmiBean;
import util.CommUtil;
import util.CurrStatus;
import util.MsgBean;

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
		
		String pId = request.getParameter("pId");
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
				currStatus.setJsp("User_DevBZ_Info.jsp?Sid=" + Sid + "&Id=" + Id + "&pId=" + pId + "&Project_Id=" + currStatus.getFunc_Project_Id());
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
				Sql = " select t.id, t.Longitude, t.latitude, t.top_Height, t.base_height, t.in_id, t.out_id, t.sign , t.project_id, t.project_name, t.Flag, t.equip_id ,t.equip_name ,t.equip_height ,t.equip_tel, t.road, t.front_id, t.front_equip_name, t.front_size, t.front_top, t.front_base, t.front_equip_height, t.front_equip_id, t.front_data, t.front_tel, t.back_id, t.back_equip_name, t.back_size, t.back_top, t.back_base, t.back_equip_height, t.back_equip_id, t.back_data, t.back_tel " + 
					  " from view_dev_bz t " +
					  " where t.id like '%" + currStatus.getFunc_Sub_Type_Id() + "%' " + 
					  " and t.project_id = '" + currStatus.getFunc_Project_Id() + "' " + 
					  " order by t.id  ";
				break;
			case 1:// 查询（单个）
				Sql = " select t.id, t.Longitude, t.latitude, t.top_Height, t.base_height, t.in_id, t.out_id, t.sign , t.project_id, t.project_name, t.Flag, t.equip_id ,t.equip_name ,t.equip_height ,t.equip_tel, t.road, t.front_id, t.front_equip_name, t.front_size, t.front_top, t.front_base, t.front_equip_height, t.front_equip_id, t.front_data, t.front_tel, t.back_id, t.back_equip_name, t.back_size, t.back_top, t.back_base, t.back_equip_height, t.back_equip_id, t.back_data, t.back_tel " + 
					  " from view_dev_bz t " + 
					  " where t.id = '" + Id + "' " +
					  " and t.project_id = '" + currStatus.getFunc_Project_Id() + "'" + 
					  " order by t.id  ";
				break;
			case 2:// 查询（多个）
				Sql = " select t.id, t.Longitude, t.latitude, t.top_Height, t.base_height, t.in_id, t.out_id, t.sign , t.project_id, t.project_name, t.Flag, t.equip_id ,t.equip_name ,t.equip_height ,t.equip_tel, t.road, t.front_id, t.front_equip_name, t.front_size, t.front_top, t.front_base, t.front_equip_height, t.front_equip_id, t.front_data, t.front_tel, t.back_id, t.back_equip_name, t.back_size, t.back_top, t.back_base, t.back_equip_height, t.back_equip_id, t.back_data, t.back_tel " + 
					  " from view_dev_bz t " + 
					  " where instr('" + Id + "', t.id) > 0 " + 
					  " and t.project_id = '" + currStatus.getFunc_Project_Id() + "'" + 
					  " order by t.id  ";
				break;
			case 10:// 添加
				Sql = " insert into dev_bz(id, Flag, project_id, front_gj, back_gj) " + 
					  " values('" + Id + "','" + Flag + "','" + Project_Id + "','"+ Front_Id + "','" + Back_Id + "')";
				break;
			case 11:// 编辑
				Sql = " update dev_bz t set t.Flag = '" + Flag + "', front_gj = '" + Front_Id + "', back_gj = '" + Back_Id + "' where t.id = '" + Id + "' and t.project_id = '" + currStatus.getFunc_Project_Id() + "'";
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
			setRoad(pRs.getString(16));
			
			setFront_Id(pRs.getString(17));
			setFront_Equip_Name(pRs.getString(18));
			setFront_Size(pRs.getString(19));
			setFront_Top(pRs.getString(20));
			setFront_Base(pRs.getString(21));
			setFront_Equip_Height(pRs.getString(22));
			setFront_Equip_Id(pRs.getString(23));
			setFront_Data(pRs.getString(24));
			setFront_Tel(pRs.getString(25));
			
			setBack_Id(pRs.getString(26));
			setBack_Equip_Name(pRs.getString(27));
			setBack_Size(pRs.getString(28));
			setBack_Top(pRs.getString(29));
			setBack_Base(pRs.getString(30));
			setBack_Equip_Height(pRs.getString(31));
			setBack_Equip_Id(pRs.getString(32));
			setBack_Data(pRs.getString(33));
			setBack_Tel(pRs.getString(34));
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
			setFront_Id(CommUtil.StrToGB2312(request.getParameter("Front_Id")));
			setFront_Equip_Name(CommUtil.StrToGB2312(request.getParameter("Front_Equip_Name")));
			setFront_Size(CommUtil.StrToGB2312(request.getParameter("Front_Size")));
			setFront_Top(CommUtil.StrToGB2312(request.getParameter("Front_Top")));
			setFront_Base(CommUtil.StrToGB2312(request.getParameter("Front_Base")));
			setFront_Equip_Height(CommUtil.StrToGB2312(request.getParameter("Front_Equip_Height")));
			setFront_Equip_Id(CommUtil.StrToGB2312(request.getParameter("Front_Equip_Id")));
			setFront_Data(CommUtil.StrToGB2312(request.getParameter("Front_Data")));
			setFront_Tel(CommUtil.StrToGB2312(request.getParameter("Front_Tel")));

			setBack_Id(CommUtil.StrToGB2312(request.getParameter("Back_Id")));
			setBack_Equip_Name(CommUtil.StrToGB2312(request.getParameter("Back_Equip_Name")));
			setBack_Size(CommUtil.StrToGB2312(request.getParameter("Back_Size")));
			setBack_Top(CommUtil.StrToGB2312(request.getParameter("Back_Top")));
			setBack_Base(CommUtil.StrToGB2312(request.getParameter("Back_Base")));
			setBack_Equip_Height(CommUtil.StrToGB2312(request.getParameter("Back_Equip_Height")));
			setBack_Equip_Id(CommUtil.StrToGB2312(request.getParameter("Back_Equip_Id")));
			setBack_Data(CommUtil.StrToGB2312(request.getParameter("Back_Data")));
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
	private String	Front_Id;
	private String	Front_Equip_Name;
	private String	Front_Size;
	private String	Front_Top;
	private String	Front_Base;
	private String	Front_Equip_Height;
	private String	Front_Equip_Id;
	private String	Front_Data;
	private String	Front_Tel;
	private String	Back_Id;
	private String	Back_Equip_Name;
	private String	Back_Size;
	private String	Back_Top;
	private String	Back_Base;
	private String	Back_Equip_Height;
	private String	Back_Equip_Id;
	private String	Back_Data;
	private String	Back_Tel;

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

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getFlag() {
		return Flag;
	}

	public void setFlag(String flag) {
		Flag = flag;
	}

	public String getFront_Equip_Id() {
		return Front_Equip_Id;
	}

	public void setFront_Equip_Id(String front_Equip_Id) {
		Front_Equip_Id = front_Equip_Id;
	}

	public String getBack_Equip_Id() {
		return Back_Equip_Id;
	}

	public void setBack_Equip_Id(String back_Equip_Id) {
		Back_Equip_Id = back_Equip_Id;
	}

	public String getProject_Id() {
		return Project_Id;
	}

	public void setProject_Id(String project_Id) {
		Project_Id = project_Id;
	}

	public String getFront_Id() {
		return Front_Id;
	}

	public void setFront_Id(String front_Id) {
		Front_Id = front_Id;
	}

	public String getFront_Equip_Name() {
		return Front_Equip_Name;
	}

	public void setFront_Equip_Name(String front_Equip_Name) {
		Front_Equip_Name = front_Equip_Name;
	}

	public String getFront_Size() {
		return Front_Size;
	}

	public void setFront_Size(String front_Size) {
		Front_Size = front_Size;
	}

	public String getFront_Top() {
		return Front_Top;
	}

	public void setFront_Top(String front_Top) {
		Front_Top = front_Top;
	}

	public String getFront_Base() {
		return Front_Base;
	}

	public void setFront_Base(String front_Base) {
		Front_Base = front_Base;
	}

	public String getFront_Equip_Height() {
		return Front_Equip_Height;
	}

	public void setFront_Equip_Height(String front_Equip_Height) {
		Front_Equip_Height = front_Equip_Height;
	}

	public String getFront_Data() {
		return Front_Data;
	}

	public void setFront_Data(String front_Data) {
		Front_Data = front_Data;
	}

	public String getFront_Tel() {
		return Front_Tel;
	}

	public void setFront_Tel(String front_Tel) {
		Front_Tel = front_Tel;
	}

	public String getBack_Id() {
		return Back_Id;
	}

	public void setBack_Id(String back_Id) {
		Back_Id = back_Id;
	}

	public String getBack_Equip_Name() {
		return Back_Equip_Name;
	}

	public void setBack_Equip_Name(String back_Equip_Name) {
		Back_Equip_Name = back_Equip_Name;
	}

	public String getBack_Size() {
		return Back_Size;
	}

	public void setBack_Size(String back_Size) {
		Back_Size = back_Size;
	}

	public String getBack_Top() {
		return Back_Top;
	}

	public void setBack_Top(String back_Top) {
		Back_Top = back_Top;
	}

	public String getBack_Base() {
		return Back_Base;
	}

	public void setBack_Base(String back_Base) {
		Back_Base = back_Base;
	}

	public String getBack_Equip_Height() {
		return Back_Equip_Height;
	}

	public void setBack_Equip_Height(String back_Equip_Height) {
		Back_Equip_Height = back_Equip_Height;
	}

	public String getBack_Data() {
		return Back_Data;
	}

	public void setBack_Data(String back_Data) {
		Back_Data = back_Data;
	}

	public String getBack_Tel() {
		return Back_Tel;
	}

	public void setBack_Tel(String back_Tel) {
		Back_Tel = back_Tel;
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

	public String getIn_Id() {
		return In_Id;
	}

	public void setIn_Id(String in_Id) {
		In_Id = in_Id;
	}

	public String getOut_Id() {
		return Out_Id;
	}

	public void setOut_Id(String out_Id) {
		Out_Id = out_Id;
	}

	public String getSign() {
		return Sign;
	}

	public void setSign(String sign) {
		Sign = sign;
	}

	public String getProject_Name() {
		return Project_Name;
	}

	public void setProject_Name(String project_Name) {
		Project_Name = project_Name;
	}

	public String getEquip_Id() {
		return Equip_Id;
	}

	public void setEquip_Id(String equip_Id) {
		Equip_Id = equip_Id;
	}

	public String getEquip_Name() {
		return Equip_Name;
	}

	public void setEquip_Name(String equip_Name) {
		Equip_Name = equip_Name;
	}

	public String getEquip_Height() {
		return Equip_Height;
	}

	public void setEquip_Height(String equip_Height) {
		Equip_Height = equip_Height;
	}

	public String getEquip_Tel() {
		return Equip_Tel;
	}

	public void setEquip_Tel(String equip_Tel) {
		Equip_Tel = equip_Tel;
	}

	public String getCurr_Data() {
		return Curr_Data;
	}

	public void setCurr_Data(String curr_Data) {
		Curr_Data = curr_Data;
	}

	public String getEquip_Time() {
		return Equip_Time;
	}

	public void setEquip_Time(String equip_Time) {
		Equip_Time = equip_Time;
	}

	public String getRoad() {
		return Road;
	}

	public void setRoad(String road) {
		Road = road;
	}

	public String getSid() {
		return Sid;
	}

	public void setSid(String sid) {
		Sid = sid;
	}

	

}