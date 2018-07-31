package bean;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import net.AlertCtrl;
import util.CommUtil;

import com.alibaba.fastjson.JSONObject;

public class WeatherNowBean {

	public void doWeatherNow(AlertCtrl alertCtrl){
		String Sql = "{? = call Func_Project_Get()}";
		String [] idList = alertCtrl.getM_DBUtil().doFunc(Sql).split(";");
		for(int i = 0; i < idList.length; i ++){
			String [] obj = idList[i].split(",");
			getWeatherNow(obj[0], obj[1], obj[2]);
			Sql = "insert into weather_now"
					+ "(Project_Id,Location,CId,Parent_City,Admin_Area,Cnty,Tz,"
					+ "Loc,Utc,"
					+ "Fl,Tmp,Cond_Code,Cond_Txt,Wind_Deg,Wind_Dir,Wind_Sc,Wind_Spd,Hum,Pcpn,Pres,Vis,Cloud)"
					+ "values('"+obj[0]+"','"+Location+"','"+CId+"','"+Parent_City+"','"+Admin_Area+"','"+Cnty+"','"+Tz+"','"
					+ Loc+"','"+Utc+"','"
					+ Fl+"','"+Tmp+"','"+Cond_Code+"','"+Cond_Txt+"','"+Wind_Deg+"','"+Wind_Dir+"','"+Wind_Sc+"','"+Wind_Spd+"','"+Hum+"','"+Pcpn+"','"+Pres+"','"+Vis+"','"+Cloud+"')";
			if(alertCtrl.getM_DBUtil().doUpdate(Sql)){
				CommUtil.PRINT("项目[" + Project_Id + "]["+Loc+"]更新天气！");
			}
		}
	}
	
	private static String NOW	= "https://free-api.heweather.com/s6/weather/now?";
	private static String KEY	= "2bc1018d899f4f0e966cfe2c6d8f99ae";
	
	public void getWeatherNow(String Project_Id, String Lng, String Lat)
	{
		String coord = Lng + "," + Lat;
		String result = "";// 访问返回结果
		BufferedReader read = null;// 读取访问结果
		try
		{
			// 创建url
			URL realurl = new URL(NOW + "location=" + coord + "&key=" + KEY);
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
			CommUtil.PRINT(result);
			JSONObject jsonObject = JSONObject.parseObject(result).getJSONArray("HeWeather6").getJSONObject(0);
			JSONObject update = jsonObject.getJSONObject("update");
			JSONObject now = jsonObject.getJSONObject("now");
			JSONObject basic = jsonObject.getJSONObject("basic");
			
			Location = basic.getString("location");
			CId = basic.getString("cid");
			Parent_City = basic.getString("parent_city");
			Admin_Area = basic.getString("admin_area");
			Cnty = basic.getString("cnty");
			Tz = basic.getString("tz");
			Loc = update.getString("loc");
			Utc = update.getString("utc");
			Fl = now.getString("fl");
			Tmp = now.getString("tmp");
			Cond_Code = now.getString("cond_code");
			Cond_Txt = now.getString("cond_txt");
			Wind_Deg = now.getString("wind_deg");
			Wind_Dir = now.getString("wind_dir");
			Wind_Sc = now.getString("wind_sc");
			Wind_Spd = now.getString("wind_spd");
			Hum = now.getString("hum");
			Pcpn = now.getString("pcpn");
			Pres = now.getString("pres");
			Vis = now.getString("vis");
			Cloud = now.getString("cloud");
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
	
	private String SN;
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
	public String getSN() {
		return SN;
	}
	public void setSN(String sN) {
		SN = sN;
	}
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
	public String getPres() {
		return Pres;
	}
	public void setPres(String pres) {
		Pres = pres;
	}
	public String getPcpn() {
		return Pcpn;
	}
	public void setPcpn(String pcpn) {
		Pcpn = pcpn;
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
	
	
}
