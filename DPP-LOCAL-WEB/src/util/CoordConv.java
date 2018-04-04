package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import com.alibaba.fastjson.JSONObject;

/**
 * 各地图转换工具类
 */

public class CoordConv
{
	public static void main(String [] args)
	{
		BD_09ToGCJ_02("120.202048", "30.276481", bd09ll, gcj02);
	}
	
	private static String BD_09	= "http://api.map.baidu.com/geoconv/v1/?";
	private static String GCJ_02	= "http://api.map.baidu.com/geoconv/v1/?";
	private static String coord	= "";
	public static int wgs84 = 1;			// 1：GPS设备获取的角度坐标，wgs84坐标;
	public static int sogou = 2;			// 2：GPS获取的米制坐标、sogou地图所用坐标;
	public static int gcj02 = 3;			// 3：google地图、soso地图、aliyun地图、mapabc地图和amap地图所用坐标，国测局（gcj02）坐标;
	public static int gcj02_mc = 4;		// 4：3中列表地图坐标对应的米制坐标;
	public static int bd09ll = 5;			// 5：百度地图采用的经纬度坐标;
	public static int bd09mc = 6;			// 6：百度地图采用的米制坐标;
	public static int mapbar = 7;			// 7：mapbar地图坐标;
	public static int _51 = 8;				// 8：51地图坐标
	private static String ak = "84PQWnNC1Ol9hHyawPSHYjVGwTZGpUVV";
	public static String [] BD_09ToGCJ_02(String Lng, String Lat, int from, int to)
	{
		coord = Lng + "," + Lat;
		String result = "";// 访问返回结果
		String [] coords = new String [2];
		BufferedReader read = null;// 读取访问结果
		try
		{
			// 创建url
			URL realurl = new URL(BD_09 + "coords=" + coord + "&from=" + from + "&to=" + to + "&ak=" + ak);
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
			JSONObject jsonObject = JSONObject.parseObject(result).getJSONArray("result").getJSONObject(0);
			coords[0] = jsonObject.getString("x");
			coords[1] = jsonObject.getString("y");
			//System.out.println(coords[0] + "," + coords[1]);
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
		return coords;
	}
}
