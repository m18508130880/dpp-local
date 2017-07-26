package bean;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import rmi.Rmi;
import rmi.RmiBean;
import util.CommUtil;
import util.CurrStatus;

public class MapImageBean extends RmiBean
{
	public final static long	serialVersionUID	= RmiBean.RMI_MAP_IMAGE;

	public long getClassId()
	{
		return serialVersionUID;
	}

	public MapImageBean()
	{
		super.className = "GISMapBean";
	}

	/**
	 * 下载地图到服务器上
	 * 
	 * @param request
	 * @param response
	 * @param pRmi
	 * @param pFromZone
	 * @throws IOException
	 * @throws RemoteException
	 */
	public void downloadGIS(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);

		PrintWriter outprint = response.getWriter();
		String Resp = "0000";

		String Id = request.getParameter("Id");
		String Project_Id = request.getParameter("Project_Id");
		
		UUID randomId = UUID.randomUUID();
		String path = "/www/DPP-LOCAL/DPP-LOCAL-WEB/files/downloadGIS/" + randomId + "/";
		File dir = new File(path);
		if(dir.mkdirs())
		{
			DevGJBean devGJBean = new DevGJBean();
			devGJBean.setId(Id);
			devGJBean.setProject_Id(Project_Id);
			msgBean = pRmi.RmiExec(7, devGJBean, 0, 25);
			ArrayList<?> gjObj = (ArrayList<?>) msgBean.getMsg();
	
			DevGXBean devGXBean = new DevGXBean();
			devGXBean.setId(Id);
			devGXBean.setProject_Id(Project_Id);
			msgBean = pRmi.RmiExec(7, devGXBean, 0, 25);
			ArrayList<?> gxObj = (ArrayList<?>) msgBean.getMsg();
			
			String imagePath = downloadImage(Center, "L", path, Id.substring(0, 2), gxObj, gjObj);
			String newPath = "/www/DPP-LOCAL/DPP-LOCAL-WEB/files/downloadGIS/"+randomId+".png";
			if(copyFile(imagePath, newPath))
			{
				Resp += newPath;
			}
			else
			{
				Resp = "9999";
			}
			deleteDirectory(path);
		}
		else
		{
			Resp = "9999";
		}
		
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		outprint.write(Resp);
	}

	/**
	 * 整理URL，下载、整理图片
	 */
	public String downloadImage(String center, String lev, String path, String gjType, ArrayList<?> gxObj, ArrayList<?> gjObj)
	{
		Size =  (int) Math.pow(2,(11-lev.length()));
		double centerLng = Double.parseDouble(center.split(",")[0]);
		double centerLat = Double.parseDouble(center.split(",")[1]);
		//中心点转化为米制坐标
		String pxCenter = pointToPixel(center);
		double pxCenterLng = Double.parseDouble(pxCenter.split(",")[0]);
		double pxCenterLat = Double.parseDouble(pxCenter.split(",")[1]);
		
		//根据中心点算出四个角的米制坐标
		String px01 = (pxCenterLng - Math.floor((Size/2) * Math.pow(2, 18 - Integer.parseInt(Zoom)))) + "," + (pxCenterLat + Math.floor((Size/2) * Math.pow(2, 18 - Integer.parseInt(Zoom))));
		String px02 = (pxCenterLng + Math.floor((Size/2) * Math.pow(2, 18 - Integer.parseInt(Zoom)))) + "," + (pxCenterLat + Math.floor((Size/2) * Math.pow(2, 18 - Integer.parseInt(Zoom))));
		String px03 = (pxCenterLng - Math.floor((Size/2) * Math.pow(2, 18 - Integer.parseInt(Zoom)))) + "," + (pxCenterLat - Math.floor((Size/2) * Math.pow(2, 18 - Integer.parseInt(Zoom))));
		String px04 = (pxCenterLng + Math.floor((Size/2) * Math.pow(2, 18 - Integer.parseInt(Zoom)))) + "," + (pxCenterLat - Math.floor((Size/2) * Math.pow(2, 18 - Integer.parseInt(Zoom))));
		
		//将四个米制坐标转化为经纬度
		String[] points = pixelToPoint(px01, px02, px03, px04);
		double p01Lng = Double.parseDouble(points[0].split(",")[0]);
		double p01Lat = Double.parseDouble(points[0].split(",")[1]);
		double p02Lng = Double.parseDouble(points[1].split(",")[0]);
		double p02Lat = Double.parseDouble(points[1].split(",")[1]);
		double p03Lng = Double.parseDouble(points[2].split(",")[0]);
		double p03Lat = Double.parseDouble(points[2].split(",")[1]);
		double p04Lng = Double.parseDouble(points[3].split(",")[0]);
		double p04Lat = Double.parseDouble(points[3].split(",")[1]);
		
		double offsetLng = 0.00027;
		double offsetLat = 0.00016;
		String markers = "";
		String markerStyles = "-1,http://121.41.52.236/dpp/skin/images/yj_m.png";
		String paths = "";
		String pathStyles = "0x0000FF,2.7,0.9";
		String labelsStyle = "";
		if(gjType.equals("WJ"))
		{
			markerStyles = "-1,http://121.41.52.236/dpp/skin/images/wj_m.png";
			pathStyles = "0xFF0000,2.7,0.9";
		}
		try
		{
			Hashtable<String, DevGJBean> objGJTable = new Hashtable<String, DevGJBean>();
			Hashtable<String, DevGXBean> objGXTable = new Hashtable<String, DevGXBean>();
			Hashtable<String, DevGJBean> pathsTable = new Hashtable<String, DevGJBean>();
			AnalogBean analogBean = new AnalogBean();
			Iterator<?> iterGJbean = gjObj.iterator();
			while (iterGJbean.hasNext())
			{
				DevGJBean gjBean = (DevGJBean) iterGJbean.next();
				String gjId = gjBean.getId();
				analogBean.HashPut(objGJTable, gjId, gjBean);
			}

			Iterator<?> iterGX = gxObj.iterator();
			while (iterGX.hasNext())
			{
				DevGXBean gxBean = (DevGXBean) iterGX.next();
				String gxId = gxBean.getId();
				analogBean.HashPut(objGXTable, gxId, gxBean);
			}
			Iterator<?> iterGJ = gjObj.iterator();
			while (iterGJ.hasNext())
			{
				DevGJBean gjBean = (DevGJBean) iterGJ.next();
				String gjId = gjBean.getId();
				double Lng = Double.parseDouble(gjBean.getLongitude());
				double Lat = Double.parseDouble(gjBean.getLatitude());
				if (Lng > p01Lng - offsetLng && Lng < p02Lng + offsetLng && Lat > p03Lat - offsetLat && Lat < p01Lat + offsetLat)
				{
					markers += Lng + "," + Lat + "|";
					labelsStyle += gjId.substring(5) + ",1,12,0x000000,0xffffff,1|";
					analogBean.HashPut(pathsTable, gjId, gjBean);
					spellGX(gjBean, objGXTable, objGJTable, pathsTable);
				}
			}
			paths = spellPathsUrl(objGXTable, pathsTable);
			String Url = "http://api.map.baidu.com/staticimage/v2" + "?ak=SftGBVkAlOW02AZKa3FdOMMLRg8kCY3r&copyright=1" + "&center=" + center + "&width=" + Size + "&height=" + Size + "&zoom=" + Zoom;
			Url += "&paths=" + paths + "&pathStyles=" + pathStyles + "&markers=" + markers + "&markerStyles=" + markerStyles;
			if (GJIdFalg.equals("1"))
			{
				Url += "&labels=" + markers + "&labelStyles=" + labelsStyle;
			}
			if(pathsTable.size() < 50 && Url.length() < 8000)
			{
				return downloadImage(Url, path, lev+".png");
			}
			else
			{
				String centerNew01 = String.valueOf((centerLng + p01Lng)/2 + "," + (centerLat + p01Lat)/2);
				String path01 = downloadImage(centerNew01, lev+"1", path, gjType, gxObj, gjObj);
				
				String centerNew02 = String.valueOf((centerLng + p02Lng)/2 + "," + (centerLat + p02Lat)/2);
				String path02 = downloadImage(centerNew02, lev+"2", path, gjType, gxObj, gjObj);
				
				String centerNew03 = String.valueOf((centerLng + p03Lng)/2 + "," + (centerLat + p03Lat)/2);
				String path03 = downloadImage(centerNew03, lev+"3", path, gjType, gxObj, gjObj);
				
				String centerNew04 = String.valueOf((centerLng + p04Lng)/2 + "," + (centerLat + p04Lat)/2);
				String path04 = downloadImage(centerNew04, lev+"4", path, gjType, gxObj, gjObj);
				
				return spellImage4(path01, path02, path03, path04, lev, path);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return "";
	}

	
	/**
	 * 复制文件
	 * @param oldPath
	 * @param newPath
	 */
	public boolean copyFile(String oldPath, String newPath)
	{
		boolean flag = true;
		try
		{
			//int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists())
			{ // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1)
				{
					//bytesum += byteread; // 字节数 文件大小
					fs.write(buffer, 0, byteread);
				}
				//System.out.println("bytesum["+bytesum+"]");
				inStream.close();
				fs.close();
			}
			else
			{
				flag = false;
			}
		}
		catch (Exception e)
		{
			System.out.println("复制["+oldPath+"]文件操作出错");
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * 删除文件夹及文件夹内所有文件夹和文件
	 * @param dirPath
	 * @return
	 */
	public boolean deleteDirectory(String dirPath)
	{
		// 如果sPath不以文件分隔符结尾，自动添加文件分隔符
		if (!dirPath.endsWith(File.separator))
		{
			dirPath = dirPath + File.separator;
		}
		File dirFile = new File(dirPath);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory())
		{
			return false;
		}
		boolean flag = true;
		File[] files = dirFile.listFiles();// 获得传入路径下的所有文件
		for (int i = 0; i < files.length; i++)
		{// 循环遍历删除文件夹下的所有文件(包括子目录)
			if (files[i].isFile())
			{// 删除子文件
				if (files[i].isFile() && files[i].exists())
				{// 路径为文件且不为空则进行删除
					files[i].delete();// 文件删除
					flag = true;
				}
				//System.out.println(files[i].getAbsolutePath() + " 删除成功");
				if (!flag) 
				{		
					//System.out.println(files[i].getAbsolutePath() + " 删除失败");
					break;// 如果删除失败，则跳出
				}
			}
			else
			{// 运用递归，删除子目录
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag) break;// 如果删除失败，则跳出
			}
		}
		if (!flag) return false;
		if (dirFile.delete())
		{// 删除当前目录
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * 平面米制坐标转换为经纬度  画框
	 */
	public void drawFrame(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);

		String coords = request.getParameter("coords");
		String url = "http://api.map.baidu.com/geoconv/v1/?from=6&to=5&ak=SftGBVkAlOW02AZKa3FdOMMLRg8kCY3r&coords=" + coords;
		PrintWriter outprint = response.getWriter();
		String Resp = "0000";
		BufferedReader read = null;
		try
		{
			URL realurl = new URL(url);
			URLConnection connection = realurl.openConnection();
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			connection.connect();
			read = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			String line;// 循环读取
			while ((line = read.readLine()) != null)
			{
				Resp += line;
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
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		outprint.write(Resp);
	}
	/**
	 * 经纬度转换为平面米制坐标
	 */
	public String pointToPixel(String point)
	{
		String url = "http://api.map.baidu.com/geoconv/v1/?from=5&to=6&ak=84PQWnNC1Ol9hHyawPSHYjVGwTZGpUVV&coords=" + point;
		String buff = "";
		BufferedReader read = null;
		try
		{
			URL realurl = new URL(url);
			URLConnection connection = realurl.openConnection();
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			connection.connect();
			read = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			String line;// 循环读取
			while ((line = read.readLine()) != null)
			{
				buff += line;
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
		JSONObject bufJson = JSON.parseObject(buff);
		JSONArray resultArray = bufJson.getJSONArray("result");
		String results = "";
		for (int i = 0; i < resultArray.size(); i++) 
		{
			results = resultArray.getJSONObject(i).getDouble("x") + "," + resultArray.getJSONObject(i).getDouble("y");
		}
		return results;
	}
	/**
	 * 平面米制坐标转换为经纬度
	 */
	public String[] pixelToPoint(String px01, String px02, String px03, String px04)
	{
		String coords = px01 + ";" + px02 + ";" + px03 + ";" + px04;
		String url = "http://api.map.baidu.com/geoconv/v1/?from=6&to=5&ak=84PQWnNC1Ol9hHyawPSHYjVGwTZGpUVV&coords=" + coords;
		String buff = "";
		BufferedReader read = null;
		try
		{
			URL realurl = new URL(url);
			URLConnection connection = realurl.openConnection();
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			connection.connect();
			read = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			String line;// 循环读取
			while ((line = read.readLine()) != null)
			{
				buff += line;
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
		JSONObject json = JSON.parseObject(buff);
		JSONArray jsonArray = json.getJSONArray("result");
		String[] results = new String[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) 
		{
			results[i] = jsonArray.getJSONObject(i).get("x") + "," + jsonArray.getJSONObject(i).get("y");
		}
		return results;
	}

	/**
	 * 整理管线
	 * 
	 * @param gjBean
	 * @param objGXTable
	 * @param objGJTable
	 * @param pathsTable01
	 */
	public void spellGX(DevGJBean gjBean, Hashtable<String, DevGXBean> objGXTable, Hashtable<String, DevGJBean> objGJTable, Hashtable<String, DevGJBean> pathsTable)
	{
		AnalogBean analogBean = new AnalogBean();
		String outGX = gjBean.getOut_Id();
		String[] StartGX = gjBean.getIn_Id().split(",");
		// 遍历起点管线，加入起点管线
		for (int i = 0; i < StartGX.length; i++)
		{
			if (StartGX[i].length() > 0 && objGXTable.containsKey(StartGX[i]))
			{
				DevGXBean gxBean = objGXTable.get(StartGX[i]);
				String StartGJ = gxBean.getStart_Id();
				if (objGJTable.containsKey(StartGJ) && !pathsTable.containsKey(StartGJ))
				{
					DevGJBean devGJBean = objGJTable.get(StartGJ);
					analogBean.HashPut(pathsTable, StartGJ, devGJBean);
				}
			}
		}
		// 加入出口管线
		if (!gjBean.getFlag().equals("2") && outGX.length() > 0 && objGXTable.containsKey(outGX))
		{

			DevGXBean gxBean = objGXTable.get(outGX);
			String outGJ = gxBean.getEnd_Id();
			if (objGJTable.containsKey(outGJ) && !pathsTable.containsKey(outGJ))
			{
				DevGJBean devGJBean = objGJTable.get(outGJ);
				analogBean.HashPut(pathsTable, outGJ, devGJBean);
			}
		}
	}

	/**
	 * 整理管线URL
	 * 
	 * @param objGXTable
	 * @param pathsTable
	 * @return
	 */
	public String spellPathsUrl(Hashtable<String, DevGXBean> objGXTable, Hashtable<String, DevGJBean> pathsTable)
	{
		String paths = "";
		Enumeration<?> e = pathsTable.elements();
		while (e.hasMoreElements())
		{
			DevGJBean gjBean01 = (DevGJBean) e.nextElement();
			if (objGXTable.containsKey(gjBean01.getOut_Id()))
			{
				DevGXBean gxBean = objGXTable.get(gjBean01.getOut_Id());
				if (pathsTable.containsKey(gxBean.getEnd_Id()))
				{
					DevGJBean gjBean02 = (DevGJBean) pathsTable.get(gxBean.getEnd_Id());
					paths += gjBean01.getLongitude() + "," + gjBean01.getLatitude() + ";" + gjBean02.getLongitude() + "," + gjBean02.getLatitude() + "|";
				}

			}
		}
		return paths;
	}

	/**
	 * 返回拼接图片 拼接4张
	 * 
	 * @param img1
	 * @param img2
	 * @param img3
	 * @param img4
	 * @param type
	 * @param fileName
	 * @return
	 */
	public String spellImage4(String img1, String img2, String img3, String img4, String lev, String path)
	{
		BufferedImage redImage = null;
		Graphics graphics = null;
		String fileName = lev + ".png";
		try
		{
			BufferedImage image1 = ImageIO.read(new File(img1));
			BufferedImage image2 = ImageIO.read(new File(img2));
			BufferedImage image3 = ImageIO.read(new File(img3));
			BufferedImage image4 = ImageIO.read(new File(img4));

			redImage = new BufferedImage(image1.getWidth() * 2, image1.getHeight(), BufferedImage.TYPE_INT_RGB);
			graphics = redImage.getGraphics();
			graphics.drawImage(image1, 0, 0, null);
			graphics.drawImage(image2, image1.getWidth(), 0, null);
			BufferedImage image01 = redImage;

			redImage = new BufferedImage(image3.getWidth() * 2, image3.getHeight(), BufferedImage.TYPE_INT_RGB);
			graphics = redImage.getGraphics();
			graphics.drawImage(image3, 0, 0, null);
			graphics.drawImage(image4, image3.getWidth(), 0, null);
			BufferedImage image02 = redImage;

			redImage = new BufferedImage(image01.getWidth(), image01.getHeight() * 2, BufferedImage.TYPE_INT_RGB);
			graphics = redImage.getGraphics();
			graphics.drawImage(image01, 0, 0, null);
			graphics.drawImage(image02, 0, image01.getHeight(), null);

			ImageIO.write(redImage, "png", new File(path, fileName));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return path+lev+".png";
	}

	

	/**
	 * 返回被截取的图片
	 * @param urlString 初始图片路径
	 * @param fileName 输出名称
	 * @param x 输出图片在原图上的起点X位置
	 * @param y 输出图片在原图上的起点Y位置
	 * @param width 输出图片宽度
	 * @param height 输出图片高度
	 * @return
	 */
	public void cutImage(String urlString, String fileName, String type, String path, int x, int y, int width, int height)
	{
		OutputStream outputImage = null;
		InputStream inputImage = null;
		ImageInputStream imageStream = null;
		fileName = fileName + "." + type;
		
		try
		{
			inputImage = new FileInputStream(urlString);
			outputImage = new FileOutputStream(path + fileName);
			Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName(type);
			ImageReader reader = readers.next();
			imageStream = ImageIO.createImageInputStream(inputImage);
			reader.setInput(imageStream, true);
			ImageReadParam param = reader.getDefaultReadParam();

			Rectangle rect = new Rectangle(x, y, width, height);
			param.setSourceRegion(rect);
			BufferedImage bi = reader.read(0, param);
			ImageIO.write(bi, type, outputImage);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (imageStream != null)
				{
					imageStream.close();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * 根据传入的Url，下载图片，并保存为传入的名字
	 * 
	 * @param urlString
	 * @param fileName
	 */
	public String downloadImage(String urlString,String path, String fileName)
	{
		URL url = null;
		URLConnection urlCon = null;
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try
		{
			url = new URL(urlString); // 构造URL
			urlCon = url.openConnection(); // 打开连接
			urlCon.setConnectTimeout(5 * 1000); // 设置请求超时为5s
			inputStream = urlCon.getInputStream(); // 输入流
			byte[] bytes = new byte[1024]; // 1K的数据缓冲
			int len; // 读取到的数据长度
			File file = new File(path); // 输出的文件流
			if (!file.exists())
			{
				file.mkdirs();
			}
			outputStream = new FileOutputStream(path + fileName);
			// 开始读取
			while ((len = inputStream.read(bytes)) != -1)
			{
				outputStream.write(bytes, 0, len);
			}
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (outputStream != null)
				{
					outputStream.close();
				}
				if (inputStream != null)
				{
					inputStream.close();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return path + fileName;
	}

	@Override
	public String getSql(int pCmd)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getData(ResultSet pRs)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean getHtmlData(HttpServletRequest request)
	{
		boolean IsOK = true;
		try
		{
			setSid(CommUtil.StrToGB2312(request.getParameter("Sid")));
			setCenter(CommUtil.StrToGB2312(request.getParameter("Center")));
			setWidth(CommUtil.StrToGB2312(request.getParameter("Width")));
			setHeight(CommUtil.StrToGB2312(request.getParameter("Height")));
			setZoom(CommUtil.StrToGB2312(request.getParameter("Zoom")));
			setMarkers(CommUtil.StrToGB2312(request.getParameter("Markers")));
			setMarkers(CommUtil.StrToGB2312(request.getParameter("Markers")));
			setMarkerStyle(CommUtil.StrToGB2312(request.getParameter("MarkerStyle")));
			setPaths(CommUtil.StrToGB2312(request.getParameter("Paths")));
			setPathStyle(CommUtil.StrToGB2312(request.getParameter("PathStyle")));
			setLabels(CommUtil.StrToGB2312(request.getParameter("Labels")));
			setLabelStyle(CommUtil.StrToGB2312(request.getParameter("LabelStyle")));
			setGJIdFalg(CommUtil.StrToGB2312(request.getParameter("GJIdFalg")));
			setCurr_Data(CommUtil.StrToGB2312(request.getParameter("Curr_Data")));
			setUrl(CommUtil.StrToGB2312(request.getParameter("Url")));
			
			setCenter01(CommUtil.StrToGB2312(request.getParameter("Center01")));
			setCenter02(CommUtil.StrToGB2312(request.getParameter("Center02")));
			setCenter03(CommUtil.StrToGB2312(request.getParameter("Center03")));
			setCenter04(CommUtil.StrToGB2312(request.getParameter("Center04")));
			setPoint01(CommUtil.StrToGB2312(request.getParameter("Point01")));
			setPoint02(CommUtil.StrToGB2312(request.getParameter("Point02")));
			setPoint03(CommUtil.StrToGB2312(request.getParameter("Point03")));
			setPoint04(CommUtil.StrToGB2312(request.getParameter("Point04")));
			setPoint05(CommUtil.StrToGB2312(request.getParameter("Point05")));
			setPoint06(CommUtil.StrToGB2312(request.getParameter("Point06")));
			setPoint07(CommUtil.StrToGB2312(request.getParameter("Point07")));
			setPoint08(CommUtil.StrToGB2312(request.getParameter("Point08")));
		}
		catch (Exception Exp)
		{
			Exp.printStackTrace();
		}
		return IsOK;
	}

	private String	Sid;

	private String	Center;
	private String	Width;
	private String	Height;
	private String	Zoom;
	private String	Markers;
	private String	MarkerStyle;
	private String	Paths;
	private String	PathStyle;
	private String	Labels;
	private String	LabelStyle;
	private String	GJIdFalg;
	private String	Curr_Data;
	private String	Url;
	private int	Lev;
	private int	Size;

	private String	Center01;
	private String	Center02;
	private String	Center03;
	private String	Center04;
	private String	Point01;
	private String	Point02;
	private String	Point03;
	private String	Point04;
	private String	Point05;
	private String	Point06;
	private String	Point07;
	private String	Point08;


	public int getSize()
	{
		return Size;
	}
	public void setSize(int size)
	{
		Size = size;
	}
	public int getLev()
	{
		return Lev;
	}
	public void setLev(int lev)
	{
		Lev = lev;
	}
	public String getSid()
	{
		return Sid;
	}
	public void setSid(String sid)
	{
		Sid = sid;
	}

	public String getCenter()
	{
		return Center;
	}

	public void setCenter(String center)
	{
		Center = center;
	}

	public String getCenter01()
	{
		return Center01;
	}

	public void setCenter01(String center01)
	{
		Center01 = center01;
	}

	public String getCenter02()
	{
		return Center02;
	}

	public void setCenter02(String center02)
	{
		Center02 = center02;
	}

	public String getCenter03()
	{
		return Center03;
	}

	public void setCenter03(String center03)
	{
		Center03 = center03;
	}

	public String getCenter04()
	{
		return Center04;
	}

	public void setCenter04(String center04)
	{
		Center04 = center04;
	}

	public String getPoint01()
	{
		return Point01;
	}

	public void setPoint01(String point01)
	{
		Point01 = point01;
	}

	public String getPoint02()
	{
		return Point02;
	}

	public void setPoint02(String point02)
	{
		Point02 = point02;
	}

	public String getPoint03()
	{
		return Point03;
	}

	public void setPoint03(String point03)
	{
		Point03 = point03;
	}

	public String getPoint04()
	{
		return Point04;
	}

	public void setPoint04(String point04)
	{
		Point04 = point04;
	}

	public String getPoint05()
	{
		return Point05;
	}

	public void setPoint05(String point05)
	{
		Point05 = point05;
	}

	public String getPoint06()
	{
		return Point06;
	}

	public void setPoint06(String point06)
	{
		Point06 = point06;
	}

	public String getPoint07()
	{
		return Point07;
	}

	public void setPoint07(String point07)
	{
		Point07 = point07;
	}

	public String getPoint08()
	{
		return Point08;
	}

	public void setPoint08(String point08)
	{
		Point08 = point08;
	}

	public String getWidth()
	{
		return Width;
	}

	public void setWidth(String width)
	{
		Width = width;
	}

	public String getHeight()
	{
		return Height;
	}

	public void setHeight(String height)
	{
		Height = height;
	}

	public String getZoom()
	{
		return Zoom;
	}

	public void setZoom(String zoom)
	{
		Zoom = zoom;
	}

	public String getMarkers()
	{
		return Markers;
	}

	public void setMarkers(String markers)
	{
		Markers = markers;
	}

	public String getMarkerStyle()
	{
		return MarkerStyle;
	}

	public void setMarkerStyle(String markerStyle)
	{
		MarkerStyle = markerStyle;
	}

	public String getPathStyle()
	{
		return PathStyle;
	}

	public void setPathStyle(String pathStyle)
	{
		PathStyle = pathStyle;
	}

	public String getLabelStyle()
	{
		return LabelStyle;
	}

	public void setLabelStyle(String labelStyle)
	{
		LabelStyle = labelStyle;
	}

	public String getPaths()
	{
		return Paths;
	}

	public void setPaths(String paths)
	{
		Paths = paths;
	}

	public String getLabels()
	{
		return Labels;
	}

	public void setLabels(String labels)
	{
		Labels = labels;
	}

	public String getGJIdFalg()
	{
		return GJIdFalg;
	}

	public void setGJIdFalg(String gJIdFalg)
	{
		GJIdFalg = gJIdFalg;
	}

	public String getUrl()
	{
		return Url;
	}

	public void setUrl(String url)
	{
		Url = url;
	}

	public String getCurr_Data()
	{
		return Curr_Data;
	}

	public void setCurr_Data(String curr_Data)
	{
		Curr_Data = curr_Data;
	}

	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}
}
