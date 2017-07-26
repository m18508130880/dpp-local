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
		String result = "";// ���ʷ��ؽ��
		BufferedReader read = null;// ��ȡ���ʽ��

		try
		{
			// ����url
			URL realurl = new URL(BD2GCJ + "?Lat=" + Lat + "&Lng=" + Lng);
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

		//return result;
	}
}
