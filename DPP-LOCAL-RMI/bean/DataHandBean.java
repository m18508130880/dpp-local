package bean;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rmi.Rmi;
import rmi.RmiBean;
import util.CommUtil;
import util.CurrStatus;

public class DataHandBean extends RmiBean 
{	
	public final static long serialVersionUID = RmiBean.RMI_DATAHAND;
	public long getClassId()
	{
		return serialVersionUID;
	}
	
	public DataHandBean()
	{
		super.className = "DataHandBean";
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
	
	public void GraphData(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		String pBTime = "";
		String pETime = "";
		switch(currStatus.getCmd())
		{
		case 1:  //ÔÂ
			pBTime = Year + "-" + Month + "-01 00:00:00";
			pETime = Year + "-" + (Integer.valueOf(Month) + 1) + "-01 00:00:00";
			currStatus.setVecDate(CommUtil.getDate(pBTime, pETime));

			msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 0);
			request.getSession().setAttribute("Water_" + Sid, ((Object) msgBean.getMsg()));
			request.getSession().setAttribute("Year_" + Sid, Year);
			request.getSession().setAttribute("Month_" + Sid, Month);
			currStatus.setJsp("User_DevHand_Curve.jsp?Sid=" + Sid + "&Id=" + Id + "&Project_Id=" + Project_Id + "&Equip_Id=" + Equip_Id);
			break;
		}
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
	   	response.sendRedirect(currStatus.getJsp());
	}
	

	public String getSql(int pCmd)
	{
		String Sql = "";
		switch (pCmd)
		{
		case 1:
			Sql = " select t.id, t.project_id, t.equip_id, t.ctime, t.value, t.unit " +
					" FROM data_hand t  " +
					" where t.id = '"+ Id +"'" + 
					" and t.project_id = '" + Project_Id + "'" +
					" and t.equip_id = '" + Equip_Id + "'" +
					" and t.ctime >= date_format('"+currStatus.getVecDate().get(0).toString()+"', '%Y-%m-%d %H-%i-%S')" +
					" and t.ctime <= date_format('"+currStatus.getVecDate().get(1).toString()+"', '%Y-%m-%d %H-%i-%S')" +
					" GROUP BY SUBSTR(ctime,1,10)" +
					" ORDER BY t.ctime " ;
			break;
		}
		return Sql;
	}

	public boolean getData(ResultSet pRs) 
	{
		boolean IsOK = true;
		try
		{
			setSN(pRs.getString(1));
			setId(pRs.getString(2));
			setProject_Id(pRs.getString(3));
			setEquip_Id(pRs.getString(4));	
			setCTime(pRs.getString(5));
			setValue(pRs.getString(6));
			setUnit(pRs.getString(7));
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
			
			setSN(CommUtil.StrToGB2312(request.getParameter("SN")));
			setId(CommUtil.StrToGB2312(request.getParameter("Id")));
			setProject_Id(CommUtil.StrToGB2312(request.getParameter("Project_Id")));
			setEquip_Id(CommUtil.StrToGB2312(request.getParameter("Equip_Id")));
			setCTime(CommUtil.StrToGB2312(request.getParameter("CTime")));
			setValue(CommUtil.StrToGB2312(request.getParameter("Value")));
			setUnit(CommUtil.StrToGB2312(request.getParameter("Unit")));
			
			setYear(CommUtil.StrToGB2312(request.getParameter("Year")));
			setMonth(CommUtil.StrToGB2312(request.getParameter("Month")));			
		}
		catch (Exception Exp) 
		{
			Exp.printStackTrace();
		}
		return IsOK;
	}

	private String SN;
	private String Id;
	private String Project_Id;
	private String Equip_Id;
	private String CTime;
	private String Value;
	private String Unit;
	
	private String Sid;
	private String Year;
	private String Month;
	public String getSN() {
		return SN;
	}

	public void setSN(String sN) {
		SN = sN;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getProject_Id() {
		return Project_Id;
	}

	public void setProject_Id(String project_Id) {
		Project_Id = project_Id;
	}

	public String getEquip_Id() {
		return Equip_Id;
	}

	public void setEquip_Id(String equip_Id) {
		Equip_Id = equip_Id;
	}

	public String getCTime() {
		return CTime;
	}

	public void setCTime(String cTime) {
		CTime = cTime;
	}

	public String getValue() {
		return Value;
	}

	public void setValue(String value) {
		Value = value;
	}

	public String getUnit() {
		return Unit;
	}

	public void setUnit(String unit) {
		Unit = unit;
	}

	public String getSid() {
		return Sid;
	}

	public void setSid(String sid) {
		Sid = sid;
	}

	public String getYear() {
		return Year;
	}

	public void setYear(String year) {
		Year = year;
	}

	public String getMonth() {
		return Month;
	}

	public void setMonth(String month) {
		Month = month;
	}

}