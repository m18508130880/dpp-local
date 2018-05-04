package bean;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rmi.Rmi;
import rmi.RmiBean;
import util.CommUtil;
import util.CurrStatus;

public class ThreeGXBean extends RmiBean
{	
	public final static long serialVersionUID =RmiBean.RMI_THREE_GX;
	public long getClassId()
	{
		return serialVersionUID;
	}
	
	public ThreeGXBean()
	{
		super.className = "ThreeGXBean";
	}
	
	public ThreeGXBean(CurrStatus currStatus)
	{
		super.className = "ThreeGXBean";
		this.currStatus = currStatus;
	}
	
	public void ExecCmd(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);

		switch(currStatus.getCmd())
		{
			
		}
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
	   	response.sendRedirect(currStatus.getJsp());
	}
	
	//管线统计-详细
	public void InTotal_GX(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
		ArrayList<?> gx_List = (ArrayList<?>) msgBean.getMsg();
		Iterator<?> gx_iterator = gx_List.iterator();
		String Resp = Id;
		String GX_DP = "";
		String PD = "";
		DecimalFormat df = new DecimalFormat("#.####");
		while (gx_iterator.hasNext()) {
			ThreeGXBean devGXBean = (ThreeGXBean) gx_iterator.next();
			Id = devGXBean.getId();
			Diameter = devGXBean.getDiameter();
			Length = devGXBean.getLength();
			Start_Id = devGXBean.getStart_Id();
			End_Id = devGXBean.getEnd_Id();
			Start_Height = devGXBean.getStart_Height();
			End_Height = devGXBean.getEnd_Height();
			Material = devGXBean.getMaterial();
			Buried_Year = devGXBean.getBuried_Year();
			Flag = "";
			PD = df.format((Double.valueOf(Start_Height) - Double.valueOf(End_Height))/Double.valueOf(Length));
			if(Double.valueOf(Start_Height) < Double.valueOf(End_Height))
			{
				GX_DP += Id + "(" + PD + "),";
			}
			else
			{
				Resp += Id + "(" + PD + "),";
			}
		}
		Resp = Resp + ";" +GX_DP;
		request.getSession().setAttribute("User_InTotal_GX_" + Sid, Resp);
		currStatus.setJsp("User_InTotal_GX.jsp?Sid=" + Sid + "&Id=" + Id);
		
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		response.sendRedirect(currStatus.getJsp());
	}
	
		
	public String getSql(int pCmd)
	{  
		String Sql = "";
		switch (pCmd)
		{
		case 0://查询（项目&子系统）
			Sql = " select t.id, t.diameter, t.length, t.start_id, t.end_id, t.start_height, t.end_height, t.material, t.buried_year, t.data_lev, t.project_id, t.project_name, t.equip_id, t.equip_name ,round((t.curr_data),2), t.road, t.flag" +
					" from view_dev_gx t " +	
					" where t.project_id = '" + currStatus.getFunc_Project_Id() + "'" + 
					" and t.id like '%"+ Subsys_Id +"%'" +
					" order by t.id ";
			break;
		case 1://查询（项目）
			Sql = " select t.id, t.diameter, t.length, t.start_id, t.end_id, t.start_height, t.end_height, t.material, t.buried_year, t.data_lev, t.project_id, t.project_name, t.equip_id, t.equip_name ,round((t.curr_data),2), t.road, t.flag" +
					" from view_dev_gx t " +	
					" where t.project_id = '" + currStatus.getFunc_Project_Id() + "'" + 
					" order by t.id ";
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
			setDiameter(pRs.getString(2));
			setLength(pRs.getString(3));
			setStart_Id(pRs.getString(4));
			setEnd_Id(pRs.getString(5));
			setStart_Height(pRs.getString(6));
			setEnd_Height(pRs.getString(7));
			setMaterial(pRs.getString(8));
			setBuried_Year(pRs.getString(9));
			setData_Lev(pRs.getString(10));
			setProject_Id(pRs.getString(11));
			setProject_Name(pRs.getString(12));
			setEquip_Id(pRs.getString(13));
			setEquip_Name(pRs.getString(14));
			setCurr_Data(pRs.getString(15));
			setRoad(pRs.getString(16));
			setFlag(pRs.getString(17));
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
			setSid(CommUtil.StrToGB2312(request.getParameter("Sid")));
			setId(CommUtil.StrToGB2312(request.getParameter("Id")));
			setDiameter(CommUtil.StrToGB2312(request.getParameter("Diameter")));
			setLength(CommUtil.StrToGB2312(request.getParameter("Length")));
			setStart_Id(CommUtil.StrToGB2312(request.getParameter("Start_Id")));
			setEnd_Id(CommUtil.StrToGB2312(request.getParameter("End_Id")));
			setStart_Height(CommUtil.StrToGB2312(request.getParameter("Start_Height")));
			setEnd_Height(CommUtil.StrToGB2312(request.getParameter("End_Height")));
			setMaterial(CommUtil.StrToGB2312(request.getParameter("Material")));
			setBuried_Year(CommUtil.StrToGB2312(request.getParameter("Buried_Year")));
			setData_Lev(CommUtil.StrToGB2312(request.getParameter("Data_Lev")));
			setProject_Id(CommUtil.StrToGB2312(request.getParameter("Project_Id")));
			setProject_Name(CommUtil.StrToGB2312(request.getParameter("Project_Name")));
			setEquip_Id(CommUtil.StrToGB2312(request.getParameter("Equip_Id")));
			setEquip_Name(CommUtil.StrToGB2312(request.getParameter("Equip_Name")));
			setSubsys_Id(CommUtil.StrToGB2312(request.getParameter("Subsys_Id")));
			setAfter_Project_Id(CommUtil.StrToGB2312(request.getParameter("After_Project_Id")));
			setRoad(CommUtil.StrToGB2312(request.getParameter("Road")));
			setFlag(CommUtil.StrToGB2312(request.getParameter("Flag")));
			setpSimu(CommUtil.StrToGB2312(request.getParameter("pSimu")));
			
		}
		catch (Exception Exp)
		{
			Exp.printStackTrace();
		}
		return IsOK;
	}
	
	private String Id;
	private String Diameter;
	private String Length;	
	private String Start_Id;
	private String End_Id;
	private String Start_Height;
	private String End_Height;
	private String Material;
	private String Buried_Year;
	private String Data_Lev;
	private String Road;
	private String Flag;
	
	private String Project_Id;	
	private String Project_Name;	
	private String Equip_Id;	
	private String Equip_Name;
	private String Curr_Data;
	private String Subsys_Id;
	
	private String Sid;	
	private String After_Project_Id;
	private String pSimu;
	
	public String getFlag()
	{
		return Flag;
	}

	public void setFlag(String flag)
	{
		Flag = flag;
	}

	public String getpSimu()
	{
		return pSimu;
	}

	public void setpSimu(String pSimu)
	{
		this.pSimu = pSimu;
	}

	public String getRoad()
	{
		return Road;
	}

	public void setRoad(String road)
	{
		Road = road;
	}

	public String getAfter_Project_Id() {
		return After_Project_Id;
	}

	public void setAfter_Project_Id(String after_Project_Id) {
		After_Project_Id = after_Project_Id;
	}	
	
	
	public String getCurr_Data() {
		return Curr_Data;
	}

	public void setCurr_Data(String curr_Data) {
		Curr_Data = curr_Data;
	}

	
	
	public String getSubsys_Id() {
		return Subsys_Id;
	}

	public void setSubsys_Id(String subsys_Id) {
		Subsys_Id = subsys_Id;
	}

	public String getStart_Height() {
		return Start_Height;
	}

	public void setStart_Height(String start_Height) {
		Start_Height = start_Height;
	}

	public String getEnd_Height() {
		return End_Height;
	}

	public void setEnd_Height(String end_Height) {
		End_Height = end_Height;
	}


	public String getBuried_Year() {
		return Buried_Year;
	}

	public void setBuried_Year(String buried_Year) {
		Buried_Year = buried_Year;
	}

	public String getData_Lev() {
		return Data_Lev;
	}

	public void setData_Lev(String data_Lev) {
		Data_Lev = data_Lev;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getDiameter() {
		return Diameter;
	}

	public void setDiameter(String diameter) {
		Diameter = diameter;
	}

	public String getLength() {
		return Length;
	}

	public void setLength(String length) {
		Length = length;
	}

	public String getStart_Id() {
		return Start_Id;
	}

	public void setStart_Id(String start_Id) {
		Start_Id = start_Id;
	}

	public String getEnd_Id() {
		return End_Id;
	}

	public void setEnd_Id(String end_Id) {
		End_Id = end_Id;
	}

	public String getMaterial() {
		return Material;
	}

	public void setMaterial(String material) {
		Material = material;
	}

	public String getProject_Id() {
		return Project_Id;
	}

	public void setProject_Id(String project_Id) {
		Project_Id = project_Id;
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

	public String getSid() {
		return Sid;
	}

	public void setSid(String sid) {
		Sid = sid;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}


