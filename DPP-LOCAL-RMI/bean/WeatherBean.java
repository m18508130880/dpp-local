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

import com.alibaba.fastjson.JSONObject;

public class WeatherBean extends RmiBean
{
	public final static long	serialVersionUID	= RmiBean.RMI_WEATHER;

	public long getClassId()
	{
		return serialVersionUID;
	}

	public WeatherBean()
	{
		super.className = "WeatherBean";
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
			
		}
		
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		response.sendRedirect(currStatus.getJsp());
	}

	public void getWeatherAll(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		PrintWriter outprint = response.getWriter();
		String Resp = "9999";
		
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
					WeatherBean bean = (WeatherBean) iterator.next();
					
				}
			}
			JSONObject jsonObject = (JSONObject) JSONObject.toJSON(alertNow);
			System.out.println(jsonObject.toString());
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
			case 0:// 查询全部
				Sql = " select t.Project_Id, t.Location, t.CId, t.Parent_City, t.Admin_Area, t.Cnty, t.Tz, t.Loc, t.Utc, t.Fl, t.Tmp, t.Cond_Code, t.Cond_Txt, t.Wind_Deg, t.Wind_Dir, t.Wind_Sc, t.Wind_Spd, t.Hum, t.Pcpn, t.Pres, t.Vis, t.Cloud " + 
					  " from weather_now t where t.project_id = '" + currStatus.getFunc_Project_Id() + "'"+
				  	  " order by t.loc desc ";
				break;
			case 1:// 查询前数个小时
				Sql = " select t.Project_Id, t.Location, t.CId, t.Parent_City, t.Admin_Area, t.Cnty, t.Tz, t.Loc, t.Utc, t.Fl, t.Tmp, t.Cond_Code, t.Cond_Txt, t.Wind_Deg, t.Wind_Dir, t.Wind_Sc, t.Wind_Spd, t.Hum, t.Pcpn, t.Pres, t.Vis, t.Cloud " + 
					  " from weather_now t where t.project_id = '" + currStatus.getFunc_Project_Id() + "'"+
					  " and loc > DATE_SUB(NOW(),INTERVAL '" + Loc + "' HOUR)" +
				  	  " order by t.loc desc ";
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
			setProject_Id(pRs.getString(1));
			setLocation(pRs.getString(2));
			setCId(pRs.getString(3));
			setParent_City(pRs.getString(4));
			setAdmin_Area(pRs.getString(5));
			setCnty(pRs.getString(6));
			setTz(pRs.getString(7));
			setLoc(pRs.getString(8));
			setUtc(pRs.getString(9));
			setFl(pRs.getString(10));
			setTmp(pRs.getString(11));
			setCond_Code(pRs.getString(12));
			setCond_Txt(pRs.getString(13));
			setWind_Deg(pRs.getString(14));
			setWind_Dir(pRs.getString(15));
			setWind_Sc(pRs.getString(16));
			setWind_Spd(pRs.getString(17));
			setHum(pRs.getString(18));
			setPcpn(pRs.getString(19));
			setPres(pRs.getString(20));
			setVis(pRs.getString(21));
			setCloud(pRs.getString(22));
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
			setProject_Id(CommUtil.StrToGB2312(request.getParameter("Project_Id")));
			setLocation(CommUtil.StrToGB2312(request.getParameter("Location")));
			setCId(CommUtil.StrToGB2312(request.getParameter("CId")));
			setParent_City(CommUtil.StrToGB2312(request.getParameter("Parent_City")));
			setAdmin_Area(CommUtil.StrToGB2312(request.getParameter("Admin_Area")));
			setCnty(CommUtil.StrToGB2312(request.getParameter("Cnty")));
			setTz(CommUtil.StrToGB2312(request.getParameter("Tz")));
			setLoc(CommUtil.StrToGB2312(request.getParameter("Loc")));
			setUtc(CommUtil.StrToGB2312(request.getParameter("Utc")));
			setFl(CommUtil.StrToGB2312(request.getParameter("Fl")));
			setTmp(CommUtil.StrToGB2312(request.getParameter("Tmp")));
			setCond_Code(CommUtil.StrToGB2312(request.getParameter("Cond_Code")));
			setCond_Txt(CommUtil.StrToGB2312(request.getParameter("Cond_Txt")));
			setWind_Deg(CommUtil.StrToGB2312(request.getParameter("Wind_Deg")));
			setWind_Dir(CommUtil.StrToGB2312(request.getParameter("Wind_Dir")));
			setWind_Sc(CommUtil.StrToGB2312(request.getParameter("Wind_Sc")));
			setWind_Spd(CommUtil.StrToGB2312(request.getParameter("Wind_Spd")));
			setHum(CommUtil.StrToGB2312(request.getParameter("Hum")));
			setPcpn(CommUtil.StrToGB2312(request.getParameter("Pcpn")));
			setPres(CommUtil.StrToGB2312(request.getParameter("Pres")));
			setVis(CommUtil.StrToGB2312(request.getParameter("Vis")));
			setCloud(CommUtil.StrToGB2312(request.getParameter("Cloud")));
			
			setSid(CommUtil.StrToGB2312(request.getParameter("Sid")));
		}
		catch (Exception Exp)
		{
			Exp.printStackTrace();
		}
		return IsOK;
	}
	
	private String Project_Id;
	private String Location;
	private String CId;
	private String Parent_City;
	private String Admin_Area;
	private String Cnty;
	private String Tz;
	private String Loc;
	private String Utc;
	private String Fl;
	private String Tmp;
	private String Cond_Code;
	private String Cond_Txt;
	private String Wind_Deg;
	private String Wind_Dir;
	private String Wind_Sc;
	private String Wind_Spd;
	private String Hum;
	private String Pcpn;
	private String Pres;
	private String Vis;
	private String Cloud;

	private String Sid;

	public String getProject_Id() {
		return Project_Id;
	}

	public void setProject_Id(String project_Id) {
		Project_Id = project_Id;
	}

	public String getLocation() {
		return Location;
	}

	public void setLocation(String location) {
		Location = location;
	}

	public String getCId() {
		return CId;
	}

	public void setCId(String cId) {
		CId = cId;
	}

	public String getParent_City() {
		return Parent_City;
	}

	public void setParent_City(String parent_City) {
		Parent_City = parent_City;
	}

	public String getAdmin_Area() {
		return Admin_Area;
	}

	public void setAdmin_Area(String admin_Area) {
		Admin_Area = admin_Area;
	}

	public String getCnty() {
		return Cnty;
	}

	public void setCnty(String cnty) {
		Cnty = cnty;
	}

	public String getTz() {
		return Tz;
	}

	public void setTz(String tz) {
		Tz = tz;
	}

	public String getLoc() {
		return Loc;
	}

	public void setLoc(String loc) {
		Loc = loc;
	}

	public String getUtc() {
		return Utc;
	}

	public void setUtc(String utc) {
		Utc = utc;
	}

	public String getFl() {
		return Fl;
	}

	public void setFl(String fl) {
		Fl = fl;
	}

	public String getTmp() {
		return Tmp;
	}

	public void setTmp(String tmp) {
		Tmp = tmp;
	}

	public String getCond_Code() {
		return Cond_Code;
	}

	public void setCond_Code(String cond_Code) {
		Cond_Code = cond_Code;
	}

	public String getCond_Txt() {
		return Cond_Txt;
	}

	public void setCond_Txt(String cond_Txt) {
		Cond_Txt = cond_Txt;
	}

	public String getWind_Deg() {
		return Wind_Deg;
	}

	public void setWind_Deg(String wind_Deg) {
		Wind_Deg = wind_Deg;
	}

	public String getWind_Dir() {
		return Wind_Dir;
	}

	public void setWind_Dir(String wind_Dir) {
		Wind_Dir = wind_Dir;
	}

	public String getWind_Sc() {
		return Wind_Sc;
	}

	public void setWind_Sc(String wind_Sc) {
		Wind_Sc = wind_Sc;
	}

	public String getWind_Spd() {
		return Wind_Spd;
	}

	public void setWind_Spd(String wind_Spd) {
		Wind_Spd = wind_Spd;
	}

	public String getHum() {
		return Hum;
	}

	public void setHum(String hum) {
		Hum = hum;
	}

	public String getPcpn() {
		return Pcpn;
	}

	public void setPcpn(String pcpn) {
		Pcpn = pcpn;
	}

	public String getPres() {
		return Pres;
	}

	public void setPres(String pres) {
		Pres = pres;
	}

	public String getVis() {
		return Vis;
	}

	public void setVis(String vis) {
		Vis = vis;
	}

	public String getCloud() {
		return Cloud;
	}

	public void setCloud(String cloud) {
		Cloud = cloud;
	}

	public String getSid() {
		return Sid;
	}

	public void setSid(String sid) {
		Sid = sid;
	}
}