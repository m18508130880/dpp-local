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
//		String result = "";// ���ʷ��ؽ��
//		BufferedReader read = null;// ��ȡ���ʽ��
//		try
//		{
//			// ����url
//			URL realurl = new URL(ALL + "location=" + coord + "&key=" + KEY);
//			// ������
//			URLConnection connection = realurl.openConnection();
//			// ����ͨ�õ���������
//			connection.setRequestProperty("accept", "*/*");
//			connection.setRequestProperty("connection", "Keep-Alive");
//			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
//			// ��������
//			connection.connect();
//			
//			// ���� BufferedReader����������ȡURL����Ӧ		
//			read = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
//			String line;// ѭ����ȡ
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
//			{// �ر���
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
	 * ��ȡ��Ӧsql���
	 * 
	 */
	public String getSql(int pCmd)
	{
		String Sql = "";
		switch (pCmd)
		{
			case 0:// ��ѯÿ����ʷ�����������
				Sql = " select t.ctime, max(t.pre_1h) as pre_1h, t.wep_now " + 
					  " from weather t " + 
					  " where t.Station_Id_C = '" + Station_Id_C + "'"+
				  	  " GROUP BY SUBSTR(ctime,1,10) ORDER BY ctime DESC ";
				break;			
			case 1:// ��ѯÿ����ʷ�����������
				Sql = " SELECT ctime, pre_1h, WEP_Now " + 
					  " FROM weather " +
					  " WHERE pre_1h IN (SELECT MAX(pre_1h) FROM weather WHERE pre_1h > 0 AND Station_Id_C = '" + Station_Id_C + "' GROUP BY DATE_FORMAT(ctime, '%Y-%m-%d')) "+
					  " AND Station_Id_C = '" + Station_Id_C + "'" + 
					  " GROUP BY DATE_FORMAT(ctime, '%Y-%m-%d')" +
					  " ORDER BY ctime DESC";
				break;
			case 2:// ��ѯ��ʷ
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
	 * �����ݿ��� ����������� ��װ��DevGjBean��
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
	 * �õ�ҳ������
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
	
	private String Station_Name;	//	��վ��/�۲�ƽ̨���� (�ַ�)	
	private String CTime;			//	ʱ��	
	
	private String Station_Id_C;	//	��վ��/�۲�ƽ̨��ʶ (�ַ�)	
	private String Year;			//	��	��
	private String Mon;				//	��	��
	private String Day;				//	��	��
	private String Hour;			//	ʱ��	ʱ
	private String PRS;				//	��ѹ	����
//	private String PRS_Sea;			//	��ƽ����ѹ			����
//	private String PRS_Max;			//	�����ѹ			����
//	private String PRS_Min;			//	�����ѹ			����
	private String TEM;				//	�¶�/����			���϶�(��)
//	private String TEM_Max;			//	�������			���϶�(��)
//	private String TEM_Min;			//	�������			���϶�(��)
	private String RHU;				//	���ʪ��			�ٷ���
//	private String RHU_Min;			//	��С���ʪ��		�ٷ���
//	private String VAP;				//	ˮ��ѹ			����
	private String PRE_1h;			//	��ˮ��			����
	private String WIN_D_INST_Max;	//	������ٵķ���(�Ƕ�)	�ַ�
	private String WIN_S_Max;		//	������			��/��
	private String WIN_D_S_Max;		//	�����ٵķ���(�Ƕ�)	��
//	private String WIN_S_Avg_2mi;	//	2����ƽ������		��/��
//	private String WIN_D_Avg_2mi;	//	2����ƽ������(�Ƕ�)	��
	private String WEP_Now;			//	��������	
	private String WEP_Des;			//	������������	
	private String WIN_S_Inst_Max;	//	�������			��/��
//	private String tigan;			//	����¶�			���϶�(��)
	private String windpower;		//	����	
//	private String VIS;				//	ˮƽ�ܼ���(�˹�)		��
//	private String CLO_Cov;			//	������			�ٷ���
//	private String CLO_Cov_Low;		//	������			�ٷ���
//	private String CLO_COV_LM;		//	����(���ƻ�����)		�ٷ���

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