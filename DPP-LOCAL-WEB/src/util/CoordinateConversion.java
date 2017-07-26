package util;

import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import net.sf.json.JSONObject;

public class CoordinateConversion
{
	public static void main(String [] args)
	{
		BD2_GCJ("120.201326", "30.27709");
	}
	
	public static String BD2GCJ	= "http://api.zdoz.net/bd2gcj.aspx";
	
	public static void BD2_GCJ(String Lat, String Lng)
	{
		System.out.println("Lat[" + Lat + "],Lng[" + Lng + "]");
		String result = "";// 访问返回结果
		BufferedReader read = null;// 读取访问结果

		try
		{
			// 创建url
			URL realurl = new URL(BD2GCJ + "?Lat=" + Lat + "&Lng=" + Lng);
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
			System.out.println("result[" + result + "]");
			JSONObject jsonObject = JSONObject.fromObject("{'Lng':30.270582374657849,'Lat':120.1953427509313}");
			String Lat1 = jsonObject.getString("Lat");
			String Lng1 = jsonObject.getString("Lng");
			System.out.println("Lat[" + Lat1 + "],Lng[" + Lng + "]");
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

		//return result;
	}
}
