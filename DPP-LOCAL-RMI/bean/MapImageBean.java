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

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		
		String Id 	= request.getParameter("Id");
		String Project_Id 	= request.getParameter("Project_Id");
		
		double centerLng = Double.parseDouble(Center.split(",")[0]);
		double centerLat = Double.parseDouble(Center.split(",")[1]);
		double offsetLng = 0.00027;
		double offsetLat = 0.00016;
		
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
		
		String markers01 = "";
		String markers02 = "";
		String markers03 = "";
		String markers04 = "";
		String markerStyles = "-1,http://121.41.52.236/dpp/skin/images/m.png";
		String paths01 = "";
		String paths02 = "";
		String paths03 = "";
		String paths04 = "";
		String pathStyles = "0x000fff,2.7,0.9";
		String labelsStyle01 = "";
		String labelsStyle02 = "";
		String labelsStyle03 = "";
		String labelsStyle04 = "";
		String Url01 = "";
		String Url02 = "";
		String Url03 = "";
		String Url04 = "";
		String path = "/www/DPP-LOCAL/DPP-LOCAL-WEB/files/downloadGIS/";
		try
		{
			Hashtable<String, DevGJBean> objGJTable = new Hashtable<String, DevGJBean>();
			Hashtable<String, DevGXBean> objGXTable = new Hashtable<String, DevGXBean>();
			
			Hashtable<String, DevGJBean> pathsTable01 = new Hashtable<String, DevGJBean>();
			Hashtable<String, DevGJBean> pathsTable02 = new Hashtable<String, DevGJBean>();
			Hashtable<String, DevGJBean> pathsTable03 = new Hashtable<String, DevGJBean>();
			Hashtable<String, DevGJBean> pathsTable04 = new Hashtable<String, DevGJBean>();
			AnalogBean analogBean = new AnalogBean();
			
				Iterator<?> iterGJbean = gjObj.iterator();
				while(iterGJbean.hasNext())
				{
					DevGJBean gjBean = (DevGJBean)iterGJbean.next();
					String gjId = gjBean.getId();
					analogBean.HashPut(objGJTable, gjId, gjBean);
				}
				
				Iterator<?> iterGX = gxObj.iterator();
				while(iterGX.hasNext())
				{
					DevGXBean gxBean = (DevGXBean)iterGX.next();
					String gxId = gxBean.getId();
					analogBean.HashPut(objGXTable, gxId, gxBean);
				}
				Iterator<?> iterGJ = gjObj.iterator();
				while(iterGJ.hasNext())
				{
					DevGJBean gjBean = (DevGJBean)iterGJ.next();
					String gjId = gjBean.getId();
					double Lng = Double.parseDouble(gjBean.getLongitude());
					double Lat = Double.parseDouble(gjBean.getLatitude());
					if(Lng < centerLng && Lat > centerLat)	//区域1
					{
						markers01 += Lng + "," + Lat + "|";
						labelsStyle01 += gjId.substring(5) + ",1,12,0x000000,0xffffff,1|";
						analogBean.HashPut(pathsTable01, gjId, gjBean);
						this.spellGX(gjBean, objGXTable, objGJTable, pathsTable01);
					}
					if(Lng > centerLng - offsetLng && Lat > centerLat)	//区域2
					{
						markers02 += Lng + "," + Lat + "|";
						labelsStyle02 += gjId.substring(5) + ",1,12,0x000000,0xffffff,1|";
						analogBean.HashPut(pathsTable02, gjId, gjBean);
						this.spellGX(gjBean, objGXTable, objGJTable, pathsTable02);
					}
					if(Lng < centerLng && Lat < centerLat + offsetLat)	//区域3
					{
						markers03 += Lng + "," + Lat + "|";
						labelsStyle03 += gjId.substring(5) + ",1,12,0x000000,0xffffff,1|";
						analogBean.HashPut(pathsTable03, gjId, gjBean);
						this.spellGX(gjBean, objGXTable, objGJTable, pathsTable03);
					}
					if(Lng > centerLng - offsetLng && Lat < centerLat + offsetLat)	//区域4
					{
						markers04 += Lng + "," + Lat + "|";
						labelsStyle04 += gjId.substring(5) + ",1,12,0x000000,0xffffff,1|";
						analogBean.HashPut(pathsTable04, gjId, gjBean);
						this.spellGX(gjBean, objGXTable, objGJTable, pathsTable04);
					}
				}
				paths01 = this.spellUrl(objGXTable, pathsTable01);
				paths02 = this.spellUrl(objGXTable, pathsTable02);
				paths03 = this.spellUrl(objGXTable, pathsTable03);
				paths04 = this.spellUrl(objGXTable, pathsTable04);
				
				String Url = "http://api.map.baidu.com/staticimage/v2" +
						"?ak=SftGBVkAlOW02AZKa3FdOMMLRg8kCY3r" +
						"&center=" + Center +
						"&width=1024" +
						"&height=1024" +
						"&zoom=" + Zoom;
				Url01 = Url +
						"&paths=" + paths01 +
						"&pathStyles=" + pathStyles +
						"&markers=" + markers01 +
						"&markerStyles=" + markerStyles;
				Url02 = Url +
						"&paths=" + paths02 +
						"&pathStyles=" + pathStyles +
						"&markers=" + markers02 +
						"&markerStyles=" + markerStyles;
				Url03 = Url +
						"&paths=" + paths03 +
						"&pathStyles=" + pathStyles +
						"&markers=" + markers03 +
						"&markerStyles=" + markerStyles;
				Url04 = Url +
						"&paths=" + paths04 +
						"&pathStyles=" + pathStyles +
						"&markers=" + markers04 +
						"&markerStyles=" + markerStyles;
				if(GJIdFalg.equals("1"))
				{
					Url01 += "&labels=" + markers01 + "&labelStyles=" + labelsStyle01;
					Url02 += "&labels=" + markers02 + "&labelStyles=" + labelsStyle02;
					Url03 += "&labels=" + markers03 + "&labelStyles=" + labelsStyle03;
					Url04 += "&labels=" + markers04 + "&labelStyles=" + labelsStyle04;
				}
				System.out.println("Url01["+Url01.length()+"]");
				System.out.println("Url02["+Url02.length()+"]");
				System.out.println("Url03["+Url03.length()+"]");
				System.out.println("Url04["+Url04.length()+"]");
				if(Url01.length() > 8000)
				{
					Resp = "1111";
				}
				else if(Url02.length() > 8000)
				{
					Resp = "2222";
				}
				else if(Url03.length() > 8000)
				{
					Resp = "3333";
				}
				else if(Url04.length() > 8000)
				{
					Resp = "4444";
				}
				else
				{
					this.downloadImage(Url01,"1.png");
					this.downloadImage(Url02,"2.png");
					this.downloadImage(Url03,"3.png");
					this.downloadImage(Url04,"4.png");
					
					this.cutImage(path + "1.png", "1-1", "png", 0, 0, 512, 512);
					this.cutImage(path + "2.png", "2-2", "png", 512, 0, 512, 512);
					this.cutImage(path + "3.png", "3-3", "png", 0, 512, 512, 512);
					this.cutImage(path + "4.png", "4-4", "png", 512, 512, 512, 512);
					this.spellImage4(new File(path + "1-1.png"), 
							new File(path + "2-2.png"),
							new File(path + "3-3.png"),
							new File(path + "4-4.png"),
							"png","v2", path);
				}
			
		}
		catch(Exception e)
		{
			Resp = "3006";
			e.printStackTrace();
		}
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		outprint.write(Resp);
	}

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
	 * 整理管线
	 * @param gjBean
	 * @param objGXTable
	 * @param objGJTable
	 * @param pathsTable01
	 */
	public void spellGX(DevGJBean gjBean, Hashtable<String, DevGXBean> objGXTable, Hashtable<String, DevGJBean> objGJTable, Hashtable<String, DevGJBean> pathsTable01)
	{
		AnalogBean analogBean = new AnalogBean();
		String outGX = gjBean.getOut_Id();
		String[] StartGX = gjBean.getIn_Id().split(",");
		//遍历起点管线，加入起点管线
		for(int i = 0; i < StartGX.length; i ++)
		{
			if(StartGX[i].length() > 0 && objGXTable.containsKey(StartGX[i]))
			{
				DevGXBean gxBean = objGXTable.get(StartGX[i]);
				String StartGJ = gxBean.getStart_Id();
				if(!pathsTable01.containsKey(StartGJ))
				{
					DevGJBean devGJBean = objGJTable.get(StartGJ);
					analogBean.HashPut(pathsTable01, StartGJ, devGJBean);
				}
			}
		}
		//加入出口管线
		if(!gjBean.getFlag().equals("2") && outGX.length() > 0 && objGXTable.containsKey(outGX))
		{
			
			DevGXBean gxBean = objGXTable.get(outGX);
			String outGJ = gxBean.getEnd_Id();
			if(!pathsTable01.containsKey(outGJ))
			{
				DevGJBean devGJBean = objGJTable.get(outGJ);
				analogBean.HashPut(pathsTable01, outGJ, devGJBean);
			}
		}
	}
	/**
	 * 整理URL
	 * @param objGXTable
	 * @param pathsTable
	 * @return
	 */
	public String spellUrl(Hashtable<String, DevGXBean> objGXTable, Hashtable<String, DevGJBean> pathsTable)
	{
		String paths = "";
		Enumeration<?> e = pathsTable.elements();
		while (e.hasMoreElements())
		{
			DevGJBean gjBean01 = (DevGJBean)e.nextElement();
			if(objGXTable.containsKey(gjBean01.getOut_Id()))
			{
				DevGXBean gxBean = objGXTable.get(gjBean01.getOut_Id());
				if(pathsTable.containsKey(gxBean.getEnd_Id()))
				{
					DevGJBean gjBean02 = (DevGJBean)pathsTable.get(gxBean.getEnd_Id());
					paths += gjBean01.getLongitude() + "," + gjBean01.getLatitude() + ";" 
							+ gjBean02.getLongitude() + "," + gjBean02.getLatitude() + "|";
				}
					
			}
		}
		return paths;
	}
	
	/**
	 * 返回拼接图片 拼接4张
	 * @param img1
	 * @param img2
	 * @param img3
	 * @param img4
	 * @param type
	 * @param fileName
	 * @return
	 */
	public void spellImage4(File img1, File img2, File img3, File img4, String type, String fileName, String path)
	{
		BufferedImage redImage = null;
		Graphics graphics = null;
		fileName = fileName + "." + type;
		try
		{
			BufferedImage image1 = ImageIO.read(img1);  
	        BufferedImage image2 = ImageIO.read(img2);  
	        BufferedImage image3 = ImageIO.read(img3);  
	        BufferedImage image4 = ImageIO.read(img4);  
	  
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
	        
	        ImageIO.write(redImage, type, new File(path, fileName));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 返回拼接图片 拼接9张
	 * @param img1
	 * @param img2
	 * @param img3
	 * @param img4
	 * @param type
	 * @param fileName
	 * @return
	 */
	public void spellImage9(File img1, File img2, File img3, File img4, File img5, File img6, File img7, File img8, File img9, String type, String fileName, String path)
	{
		BufferedImage redImage = null;
		Graphics graphics = null;
		fileName = fileName + "." + type;
		try
		{
			BufferedImage image1 = ImageIO.read(img1);  
	        BufferedImage image2 = ImageIO.read(img2);  
	        BufferedImage image3 = ImageIO.read(img3);  
	        BufferedImage image4 = ImageIO.read(img4);  
	        BufferedImage image5 = ImageIO.read(img5);  
	        BufferedImage image6 = ImageIO.read(img6);  
	        BufferedImage image7 = ImageIO.read(img7);  
	        BufferedImage image8 = ImageIO.read(img8);  
	        BufferedImage image9 = ImageIO.read(img9);  
	  
	        //1-2-3
	        redImage = new BufferedImage(image2.getWidth() * 2, image2.getHeight(), BufferedImage.TYPE_INT_RGB);
	        graphics = redImage.getGraphics(); 
	        graphics.drawImage(image2, 0, 0, null);
	        graphics.drawImage(image3, image2.getWidth(), 0, null);
	        BufferedImage image2_3 = redImage;
	        redImage = new BufferedImage(image1.getWidth() * 3, image1.getHeight(), BufferedImage.TYPE_INT_RGB);  
	        graphics = redImage.getGraphics();  
	        graphics.drawImage(image1, 0, 0, null);  
	        graphics.drawImage(image2_3, image1.getWidth(), 0, null);
	        BufferedImage image1_2_3 = redImage;
	        
	        //4-5-6
	        redImage = new BufferedImage(image5.getWidth() * 2, image5.getHeight(), BufferedImage.TYPE_INT_RGB);
	        graphics = redImage.getGraphics();  
	        graphics.drawImage(image5, 0, 0, null);
	        graphics.drawImage(image6, image5.getWidth(), 0, null);
	        BufferedImage image5_6 = redImage;
	        redImage = new BufferedImage(image4.getWidth() * 3, image4.getHeight(), BufferedImage.TYPE_INT_RGB);  
	        graphics = redImage.getGraphics();  
	        graphics.drawImage(image4, 0, 0, null);  
	        graphics.drawImage(image5_6, image4.getWidth(), 0, null);
	        BufferedImage image4_5_6 = redImage;
	        
	        //7-8-9
	        redImage = new BufferedImage(image8.getWidth() * 2, image8.getHeight(), BufferedImage.TYPE_INT_RGB);
	        graphics = redImage.getGraphics();  
	        graphics.drawImage(image8, 0, 0, null);
	        graphics.drawImage(image9, image8.getWidth(), 0, null);
	        BufferedImage image8_9 = redImage;
	        redImage = new BufferedImage(image7.getWidth() * 3, image7.getHeight(), BufferedImage.TYPE_INT_RGB);  
	        graphics = redImage.getGraphics();  
	        graphics.drawImage(image7, 0, 0, null);  
	        graphics.drawImage(image8_9, image7.getWidth(), 0, null);
	        BufferedImage image7_8_9 = redImage;
	        
	        //4-5-6-7-8-9
	        redImage = new BufferedImage(image4_5_6.getWidth(), image4_5_6.getHeight() * 2, BufferedImage.TYPE_INT_RGB);
	        graphics = redImage.getGraphics();  
	        graphics.drawImage(image4_5_6, 0, 0, null);
	        graphics.drawImage(image7_8_9, 0, image4_5_6.getHeight(), null);
	        BufferedImage image4_5_6_7_8_9 = redImage;
	        
	        //1-2-3-4-5-6-7-8-9
	        redImage = new BufferedImage(image1_2_3.getWidth(), image1_2_3.getHeight() * 3, BufferedImage.TYPE_INT_RGB);  
	        graphics = redImage.getGraphics();  
	        graphics.drawImage(image1_2_3, 0, 0, null);  
	        graphics.drawImage(image4_5_6_7_8_9, 0, image7_8_9.getHeight(), null);
	        
	        ImageIO.write(redImage, type, new File(path, fileName));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 返回被截取的图片
	 * @param urlString 初始图片路径
	 * @param fileName  输出名称
	 * @param x	输出图片在原图上的起点X位置
	 * @param y 输出图片在原图上的起点Y位置
	 * @param width	输出图片宽度
	 * @param height 输出图片高度
	 * @return
	 */
	public void cutImage(String urlString, String fileName, String type, int x, int y, int width, int height)
	{
		OutputStream outputImage = null;
		InputStream inputImage = null;
		ImageInputStream imageStream = null;
		fileName = fileName + "." + type;
		try
		{
			inputImage = new FileInputStream(urlString);
			outputImage = new FileOutputStream("/www/DPP-LOCAL/DPP-LOCAL-WEB/files/downloadGIS/" + fileName);
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
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(imageStream != null)
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
	 * @param urlString
	 * @param fileName
	 */
	public void downloadImage(String urlString, String fileName)
	{
		String savePath = "/www/DPP-LOCAL/DPP-LOCAL-WEB/files/downloadGIS/";
		URL url = null;
		URLConnection urlCon = null;
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try{
			url = new URL(urlString);					// 构造URL
			urlCon = url.openConnection();				// 打开连接
			urlCon.setConnectTimeout(5 * 1000);			// 设置请求超时为5s
			inputStream = urlCon.getInputStream();		// 输入流
			byte[] bytes = new byte[1024];				// 1K的数据缓冲
			int len;									// 读取到的数据长度
			File file = new File(savePath);				// 输出的文件流
			if (!file.exists())
			{
				file.mkdirs();
			}
			outputStream = new FileOutputStream(savePath + fileName);
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
		}finally
		{
			try
			{
				if(outputStream != null)
				{
					outputStream.close();
				}
				if(inputStream != null)
				{
					inputStream.close();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
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
			setUrl(CommUtil.StrToGB2312(request.getParameter("Url")));
		}
		catch (Exception Exp)
		{
			Exp.printStackTrace();
		}
		return IsOK;
	}
	
	private String Sid;
	
	private String Center;
	private String Width;
	private String Height;
	private String Zoom;
	private String Markers;
	private String MarkerStyle;
	private String Paths;
	private String PathStyle;
	private String Labels;
	private String LabelStyle;
	private String GJIdFalg;
	private String Url;
	
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
	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}
}
