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
		String result = "";// ���ʷ��ؽ��
		BufferedReader read = null;// ��ȡ���ʽ��
		try
		{
			String url = HTTP + "userId=" + NAME + "&pwd=" + PWD + "&timeRange=[" + bTime + "," + eTime + "]&staIDs=" + S_Id_L + "&elements=" + ELEMENTS;

			// ����url
			URL realurl = new URL(url);
			// ������
			URLConnection connection = realurl.openConnection();
			// ����ͨ�õ���������
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// ��������
			connection.connect();
			
			// ���� BufferedReader����������ȡURL����Ӧ		
			read = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			String line;// ѭ����ȡ
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
			{// �ر���
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
