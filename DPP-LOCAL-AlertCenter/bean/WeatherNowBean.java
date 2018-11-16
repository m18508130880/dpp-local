package bean;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import net.AlertCtrl;
import util.CommUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class WeatherNowBean {

	private static String HTTP		= "http://api.data.cma.cn:8090/api?dataFormat=json&interfaceId=getSurfEleByTimeRangeAndStaID&dataCode=SURF_CHN_MUL_HOR&";
//	private static String NAME	= "540535968105IcFB2";
	private static String NAME		= "541398038270Qsj8Q";
//	private static String PWD		= "Y0mSwiu";
	private static String PWD		= "fUP2nnD";
	private static String S_Id_L	= "58457,58452";
	private static String ELEMENTS	= "Station_Id_C,Year,Mon,Day,Hour,TEM,PRS,RHU,windpower,WIN_D_S_Max,WIN_S_Max,WIN_D_INST_Max,WIN_S_Inst_Max,PRE_1h,WEP_Now";
	
	public void doWeatherNow(AlertCtrl alertCtrl)
	{
		String nTime = CommUtil.getTime();
		String bTime = CommUtil.getDateHourAfter(nTime, -10);
//		String bTime = "20181101000000";
		String eTime = CommUtil.getDateHourAfter(nTime, -8);
		String status = "";
		String result = "";// 访问返回结果
		BufferedReader read = null;// 读取访问结果
		try
		{
			String url = HTTP + "userId=" + NAME + "&pwd=" + PWD + "&timeRange=[" + bTime + "," + eTime + "]&staIDs=" + S_Id_L + "&elements=" + ELEMENTS;

			// 创建url
			URL realurl = new URL(url);
			// 打开连接
			URLConnection connection = realurl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立连接
			connection.connect();
			
			// 定义 BufferedReader输入流来读取URL的响应		
			read = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			String line;// 循环读取
			while ((line = read.readLine()) != null)
			{
				result += line;
			}
			//CommUtil.PRINT(result);
			JSONObject jsonObject = JSONObject.parseObject(result);
			JSONArray jsonArray = jsonObject.getJSONArray("DS");
			String Sql = "";
			for(int i = 0; i < jsonArray.size(); i ++){
				JSONObject json = jsonArray.getJSONObject(i);
				Station_Id_C = json.getString("Station_Id_C");
				Station_Name = WeatherElementBean.getStation(Station_Id_C);
				Year = json.getString("Year");
				Mon = json.getString("Mon");
				Day = json.getString("Day");
				Hour = json.getString("Hour");
				CTime = Year+"-"+Mon+"-"+Day+" " + Hour + ":00:00";
				CTime = CommUtil.getDateHourAfter_(CTime, 8);
//				TEM,PRS,RHU,windpower,WIN_D_S_Max,WIN_S_Max,WIN_D_INST_Max,WIN_S_Inst_Max,PRE_1h,WEP_Now
				TEM = json.getString("TEM");
				PRS = json.getString("PRS");
				RHU = json.getString("RHU");
				windpower = json.getString("windpower");
				WIN_D_S_Max = json.getString("WIN_D_S_Max");
				WIN_S_Max = json.getString("WIN_S_Max");
				WIN_D_INST_Max = json.getString("WIN_D_INST_Max");
				WIN_S_Inst_Max = json.getString("WIN_S_Inst_Max");
				PRE_1h = json.getString("PRE_1h");
				WEP_Now = json.getString("WEP_Now");
				WEP_Des = WeatherElementBean.getElement(WEP_Now);
				
				Sql = "insert into weather"
						+ "(Station_Id_C,Station_Name,CTime,TEM,PRS,RHU,windpower,WIN_D_S_Max,WIN_S_Max,WIN_D_INST_Max,WIN_S_Inst_Max,PRE_1h,WEP_Now) "
						+ "values('"+Station_Id_C+"','"+Station_Name+"','"+CTime+"','"+TEM+"','"+PRS+"','"+RHU+"','"+windpower+"','"+WIN_D_S_Max+"','"+WIN_S_Max+"','"+WIN_D_INST_Max+"','"+WIN_S_Inst_Max+"','"+PRE_1h+"','"+WEP_Des+"')";
				//System.out.println("Sql["+Sql+"]");
				if(alertCtrl.getM_DBUtil().doUpdate(Sql)){
					status = "SUCCESS";
				}else{
					status = "ERROR";
				}
				CommUtil.PRINT(Station_Name + "["+CTime+"]["+status+"]");
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (read != null)
			{// 关闭流
				try
				{
					read.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
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
	
	public static String getHTTP() {
		return HTTP;
	}
	public static void setHTTP(String hTTP) {
		HTTP = hTTP;
	}
	public static String getNAME() {
		return NAME;
	}
	public static void setNAME(String nAME) {
		NAME = nAME;
	}
	public static String getPWD() {
		return PWD;
	}
	public static void setPWD(String pWD) {
		PWD = pWD;
	}
	public static String getS_Id_L() {
		return S_Id_L;
	}
	public static void setS_Id_L(String s_Id_L) {
		S_Id_L = s_Id_L;
	}
	public static String getELEMENTS() {
		return ELEMENTS;
	}
	public static void setELEMENTS(String eLEMENTS) {
		ELEMENTS = eLEMENTS;
	}
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

	
}
