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
//		getHtmlData(request);
//		currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
//		currStatus.getHtmlData(request, pFromZone);
//
//		response.setCharacterEncoding("UTF-8");
//		PrintWriter outprint = response.getWriter();
//		String Resp = "9999";
//		
//		String ALL = "https://free-api.heweather.com/s6/weather?";
//		String KEY	= "2bc1018d899f4f0e966cfe2c6d8f99ae";
//		String coord = Longitude + "," + Latitude;
//		String result = "";// 访问返回结果
//		BufferedReader read = null;// 读取访问结果
//		try
//		{
//			// 创建url
//			URL realurl = new URL(ALL + "location=" + coord + "&key=" + KEY);
//			// 打开连接
//			URLConnection connection = realurl.openConnection();
//			// 设置通用的请求属性
//			connection.setRequestProperty("accept", "*/*");
//			connection.setRequestProperty("connection", "Keep-Alive");
//			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
//			// 建立连接
//			connection.connect();
//			
//			// 定义 BufferedReader输入流来读取URL的响应		
//			read = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
//			String line;// 循环读取
//			while ((line = read.readLine()) != null)
//			{
//				result += line;
//			}
//			JSONObject jsonObject = JSONObject.parseObject(result).getJSONArray("HeWeather6").getJSONObject(0);
//			Resp = "0000" + jsonObject.toString();
//		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//		}
//		finally
//		{
//			if (read != null)
//			{// 关闭流
//				try
//				{
//					read.close();
//				}
//				catch (IOException e)
//				{
//					e.printStackTrace();
//				}
//			}
//		}
//		
//		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
//		outprint.write(Resp);
	}
	
	public void getWeather(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 0);
		ArrayList<Object> arrayList = new ArrayList<Object>();
		if (msgBean.getStatus() == MsgBean.STA_SUCCESS)
		{
			ArrayList<?> weather = (ArrayList<?>) msgBean.getMsg();
			Iterator<?> iterator = weather.iterator();
			while (iterator.hasNext())
			{
				WeatherBean bean = (WeatherBean) iterator.next();
				arrayList.add(bean);
			}
		}
		
		PrintWriter outprint = response.getWriter();
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		String jsonObj = JSONObject.toJSONString(arrayList);
		response.setCharacterEncoding("UTF-8");
		outprint.write(jsonObj);
		outprint.flush();
	}
	
	public void getWeatherHistory(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 0);
		ArrayList<Object> arrayList = new ArrayList<Object>();
		if (msgBean.getStatus() == MsgBean.STA_SUCCESS)
		{
			ArrayList<?> weather = (ArrayList<?>) msgBean.getMsg();
			Iterator<?> iterator = weather.iterator();
			while (iterator.hasNext())
			{
				WeatherBean bean = (WeatherBean) iterator.next();
				arrayList.add(bean);
			}
		}
		
		PrintWriter outprint = response.getWriter();
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		String jsonObj = JSONObject.toJSONString(arrayList);
		response.setCharacterEncoding("UTF-8");
		outprint.write(jsonObj);
		outprint.flush();
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
			case 0:// 查询每天历史最大天气数据
				Sql = " select t.ctime, max(t.pre_1h) as pre_1h, t.wep_now " + 
					  " from weather t " + 
					  " where t.Station_Id_C = '" + Station_Id_C + "'"+
				  	  " GROUP BY SUBSTR(ctime,1,10) ORDER BY ctime DESC ";
				break;			
			case 1:// 查询每天历史最大天气数据
				Sql = " SELECT ctime, pre_1h, WEP_Now " + 
					  " FROM weather " +
					  " WHERE pre_1h IN (SELECT MAX(pre_1h) FROM weather WHERE pre_1h > 0 AND Station_Id_C = '" + Station_Id_C + "' GROUP BY DATE_FORMAT(ctime, '%Y-%m-%d')) "+
					  " AND Station_Id_C = '" + Station_Id_C + "'" + 
					  " GROUP BY DATE_FORMAT(ctime, '%Y-%m-%d')" +
					  " ORDER BY ctime DESC";
				break;
			case 2:// 查询历史
				Sql = " select t.ctime, t.pre_1h, t.wep_now " + 
					  " from weather t " + 
					  " where t.Station_Id_C = '" + Station_Id_C + "'"+
				  	  " and t.ctime >= date_format('"+currStatus.getVecDate().get(0).toString()+"', '%Y-%m-%d %H-%i-%S')" +
					  " ORDER BY t.ctime DESC ";
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
			setCTime(pRs.getString(1));
			setPRE_1h(pRs.getString(2));
			setWEP_Now(pRs.getString(3));
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
			setStation_Id_C(CommUtil.StrToGB2312(request.getParameter("Station_Id_C")));
			setSid(CommUtil.StrToGB2312(request.getParameter("Sid")));
		}
		catch (Exception Exp)
		{
			Exp.printStackTrace();
		}
		return IsOK;
	}
	
	private String Station_Name;	//	区站号/观测平台名称 (字符)	
	private String CTime;			//	时间	
	
	private String Station_Id_C;	//	区站号/观测平台标识 (字符)	
	private String Year;			//	年	年
	private String Mon;				//	月	月
	private String Day;				//	日	日
	private String Hour;			//	时次	时
	private String PRS;				//	气压	百帕
//	private String PRS_Sea;			//	海平面气压			百帕
//	private String PRS_Max;			//	最高气压			百帕
//	private String PRS_Min;			//	最低气压			百帕
	private String TEM;				//	温度/气温			摄氏度(℃)
//	private String TEM_Max;			//	最高气温			摄氏度(℃)
//	private String TEM_Min;			//	最低气温			摄氏度(℃)
	private String RHU;				//	相对湿度			百分率
//	private String RHU_Min;			//	最小相对湿度		百分率
//	private String VAP;				//	水汽压			百帕
	private String PRE_1h;			//	降水量			毫米
	private String WIN_D_INST_Max;	//	极大风速的风向(角度)	字符
	private String WIN_S_Max;		//	最大风速			米/秒
	private String WIN_D_S_Max;		//	最大风速的风向(角度)	度
//	private String WIN_S_Avg_2mi;	//	2分钟平均风速		米/秒
//	private String WIN_D_Avg_2mi;	//	2分钟平均风向(角度)	度
	private String WEP_Now;			//	现在天气	
	private String WEP_Des;			//	现在天气描述	
	private String WIN_S_Inst_Max;	//	极大风速			米/秒
//	private String tigan;			//	体感温度			摄氏度(℃)
	private String windpower;		//	风力	
//	private String VIS;				//	水平能见度(人工)		米
//	private String CLO_Cov;			//	总云量			百分率
//	private String CLO_Cov_Low;		//	低云量			百分率
//	private String CLO_COV_LM;		//	云量(低云或中云)		百分率

	private String Sid;

	public String getStation_Name() {
		return Station_Name;
	}

	public void setStation_Name(String station_Name) {
		Station_Name = station_Name;
	}

	public String getCTime() {
		return CTime;
	}

	public void setCTime(String cTime) {
		CTime = cTime;
	}

	public String getStation_Id_C() {
		return Station_Id_C;
	}

	public void setStation_Id_C(String station_Id_C) {
		Station_Id_C = station_Id_C;
	}

	public String getYear() {
		return Year;
	}

	public void setYear(String year) {
		Year = year;
	}

	public String getMon() {
		return Mon;
	}

	public void setMon(String mon) {
		Mon = mon;
	}

	public String getDay() {
		return Day;
	}

	public void setDay(String day) {
		Day = day;
	}

	public String getHour() {
		return Hour;
	}

	public void setHour(String hour) {
		Hour = hour;
	}

	public String getPRS() {
		return PRS;
	}

	public void setPRS(String pRS) {
		PRS = pRS;
	}

	public String getTEM() {
		return TEM;
	}

	public void setTEM(String tEM) {
		TEM = tEM;
	}

	public String getRHU() {
		return RHU;
	}

	public void setRHU(String rHU) {
		RHU = rHU;
	}

	public String getPRE_1h() {
		return PRE_1h;
	}

	public void setPRE_1h(String pRE_1h) {
		PRE_1h = pRE_1h;
	}

	public String getWIN_D_INST_Max() {
		return WIN_D_INST_Max;
	}

	public void setWIN_D_INST_Max(String wIN_D_INST_Max) {
		WIN_D_INST_Max = wIN_D_INST_Max;
	}

	public String getWIN_S_Max() {
		return WIN_S_Max;
	}

	public void setWIN_S_Max(String wIN_S_Max) {
		WIN_S_Max = wIN_S_Max;
	}

	public String getWIN_D_S_Max() {
		return WIN_D_S_Max;
	}

	public void setWIN_D_S_Max(String wIN_D_S_Max) {
		WIN_D_S_Max = wIN_D_S_Max;
	}

	public String getWEP_Now() {
		return WEP_Now;
	}

	public void setWEP_Now(String wEP_Now) {
		WEP_Now = wEP_Now;
	}

	public String getWEP_Des() {
		return WEP_Des;
	}

	public void setWEP_Des(String wEP_Des) {
		WEP_Des = wEP_Des;
	}

	public String getWIN_S_Inst_Max() {
		return WIN_S_Inst_Max;
	}

	public void setWIN_S_Inst_Max(String wIN_S_Inst_Max) {
		WIN_S_Inst_Max = wIN_S_Inst_Max;
	}

	public String getWindpower() {
		return windpower;
	}

	public void setWindpower(String windpower) {
		this.windpower = windpower;
	}

	public String getSid() {
		return Sid;
	}

	public void setSid(String sid) {
		Sid = sid;
	}
}