package bean;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import rmi.Rmi;
import rmi.RmiBean;
import util.*;

public class ProjectInfoBean extends RmiBean 
{
	public final static long serialVersionUID = RmiBean.RMI_PROJECT_INFO;
	public long getClassId()
	{
		return serialVersionUID;
	}
	
	public ProjectInfoBean()
	{
		super.className = "ProjectInfoBean";
	}
	
	public void ExecCmd(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
		switch(currStatus.getCmd())
		{
			case 10://ÃÌº”
			case 11://±‡º≠
				currStatus.setResult(MsgBean.GetResult(msgBean.getStatus()));
				msgBean = pRmi.RmiExec(0, this, 0, 25);
			case 0://≤È—Ø
		    	request.getSession().setAttribute("Project_Info_" + Sid, ((Object)msgBean.getMsg()));
		    	currStatus.setJsp("Project_Info.jsp?Sid=" + Sid);		    
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
			case 0://≤È—Ø
				Sql = " select  t.id, t.cname, t.Longitude, t.Latitude, t.MapLev , t.MapAngle , t.Demo "
						+ " from project_info t order by t.id";
				break;
			case 10://ÃÌº”
				Sql = " insert into project_info( id, cname, Longitude, Latitude, MapLev, MapAngle, Demo)" +
					  " values('"+ Id +"', '"+ CName +"', '"+ Longitude +"', '"+ Latitude +"', '"+ MapLev +"', '"+ MapAngle +"', '"+ Demo +"')";
				break;
			case 11://±‡º≠
				Sql = " update project_info t set t.cname= '"+ CName +"', t.Longitude= '"+ Longitude +"', t.Latitude= '"+ Latitude +"', t.MapLev= '"+ MapLev +"' , t.MapAngle= '"+ MapAngle +"' , t.Demo= '"+ Demo +"' " +
					  " where t.id = '"+ Id +"'";
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
			setLongitude(pRs.getString(3));
			setLatitude(pRs.getString(4));
			setMapLev(pRs.getString(5));
			setMapAngle(pRs.getString(6));
			setDemo(pRs.getString(7));
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
			setLongitude(CommUtil.StrToGB2312(request.getParameter("Longitude")));
			setLatitude(CommUtil.StrToGB2312(request.getParameter("Latitude")));
			setMapLev(CommUtil.StrToGB2312(request.getParameter("MapLev")));
			setMapAngle(CommUtil.StrToGB2312(request.getParameter("MapAngle")));
			setDemo(CommUtil.StrToGB2312(request.getParameter("Demo")));
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
	private String Longitude;
	private String Latitude;
	private String MapLev;
	private String MapAngle;
	private String Demo;
	private String Sid;
	
	
	public String getMapAngle() {
		return MapAngle;
	}

	public void setMapAngle(String mapAngle) {
		MapAngle = mapAngle;
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

	public String getMapLev() {
		return MapLev;
	}

	public void setMapLev(String mapLev) {
		MapLev = mapLev;
	}

	public String getDemo() {
		return Demo;
	}

	public void setDemo(String demo) {
		Demo = demo;
	}

	public String getSid() {
		return Sid;
	}

	public void setSid(String sid) {
		Sid = sid;
	}
	
	
}