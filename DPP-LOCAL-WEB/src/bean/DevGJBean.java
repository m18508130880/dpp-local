package bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jsonBean.DevGJJsonBean;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;

import rmi.Rmi;
import rmi.RmiBean;
import util.CommUtil;
import util.CoordConv;
import util.CurrStatus;
import util.MsgBean;

import com.alibaba.fastjson.JSONObject;
import com.jspsmart.upload.SmartUpload;

public class DevGJBean extends RmiBean
{
	public final static long	serialVersionUID	= RmiBean.RMI_DEVGJ;

	public long getClassId()
	{
		return serialVersionUID;
	}

	public DevGJBean()
	{
		super.className = "DevGJBean";
	}
	
	public DevGJBean(CurrStatus currStatus)
	{
		super.className = "DevGJBean";
		this.currStatus = currStatus;
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
		
		switch (currStatus.getCmd())
		{
			case 10:// 添加
				currStatus.setResult(MsgBean.GetResult(msgBean.getStatus()));
				msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25); // 得到一个封装了结果集的MsgBean对象
				pRmi.Client(2001,"0000000001","");
				break;
			case 12:// 删除
				String[] Ids = currStatus.getFunc_Sub_Type_Id().split(",");
				for(int i = 0; i < Ids.length; i ++){
					Id = Ids[i];
					msgBean = pRmi.RmiExec(currStatus.getCmd(), this, currStatus.getCurrPage(), 25);
				}
				pRmi.Client(2001,"0000000001","");
				currStatus.setResult(MsgBean.GetResult(msgBean.getStatus()));
				msgBean = pRmi.RmiExec(0, this, currStatus.getCurrPage(), 25);
				currStatus.setTotalRecord(msgBean.getCount());
				request.getSession().setAttribute("User_DevGJ_Info_" + Sid, (Object) msgBean.getMsg());
				currStatus.setJsp("Dev_GJ.jsp?Sid=" + Sid);
				break;
			case 11:// 编辑
				currStatus.setResult(MsgBean.GetResult(msgBean.getStatus()));
				msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25); // 得到一个封装了结果集的MsgBean对象
				pRmi.Client(2001,"0000000001","");
			case 0:// Admin查询
				msgBean = pRmi.RmiExec(0, this, currStatus.getCurrPage(), 25);
				currStatus.setTotalRecord(msgBean.getCount());
				request.getSession().setAttribute("Admin_DevGJ_Info_" + Sid, (Object) msgBean.getMsg());
				currStatus.setJsp("Dev_GJ.jsp?Sid=" + Sid);
				break;
			case 8:// User查询(道路)
				msgBean = pRmi.RmiExec(8, this, currStatus.getCurrPage(), 25);
				currStatus.setTotalRecord(msgBean.getCount());
				request.getSession().setAttribute("User_DevGJ_Info_" + Sid, (Object) msgBean.getMsg());
				currStatus.setJsp("Dev_GJ.jsp?Sid=" + Sid);
				break;
			case 1:// User查询
				msgBean = pRmi.RmiExec(0, this, currStatus.getCurrPage(), 25);
				currStatus.setTotalRecord(msgBean.getCount());
				request.getSession().setAttribute("User_DevGJ_Info_" + Sid, (Object) msgBean.getMsg());
				currStatus.setJsp("Dev_GJ.jsp?Sid=" + Sid);
				break;
			case 3:// User单个查询
				msgBean = pRmi.RmiExec(3, this, 0, 25);
				request.getSession().setAttribute("User_DevGJ_Info_" + Sid, (DevGJBean) ((ArrayList<?>) msgBean.getMsg()).get(0));
				currStatus.setJsp("User_DevGJ_Info.jsp?Sid=" + Sid + "&Id=" + Id);
				break;
			case 4:// User单个查询
				msgBean = pRmi.RmiExec(3, this, 0, 25);
				request.getSession().setAttribute("User_DevGJ_Info_" + Sid, (DevGJBean) ((ArrayList<?>) msgBean.getMsg()).get(0));
				String Join = request.getParameter("Join");
				currStatus.setJsp("One_GJ.jsp?Sid=" + Sid + "&Id=" + Id + "&Join=" + Join);
				break;
			case 5:// User单个查询
				msgBean = pRmi.RmiExec(3, this, 0, 25);
				request.getSession().setAttribute("User_DevGJ_Info_" + Sid, (DevGJBean) ((ArrayList<?>) msgBean.getMsg()).get(0));
				currStatus.setJsp("User_One_GJ.jsp?Sid=" + Sid + "&Id=" + Id);
				break;
			case 6:// Admin查询单个
				msgBean = pRmi.RmiExec(6, this, 0, 25);
				request.getSession().setAttribute("Admin_DevGJ_Info_" + Sid, (DevGJBean) ((ArrayList<?>) msgBean.getMsg()).get(0));
				currStatus.setJsp("Admin_DevGJ_Info.jsp?Sid=" + Sid);
				break;
		}
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		response.sendRedirect(currStatus.getJsp());
	}

	/**
	 * 模拟计算时段水位深度
	 * 
	 * @param request
	 * @param response
	 * @param pRmi
	 * @param pFromZone
	 * @throws ServletException
	 * @throws IOException
	 */
	public void AnalogExecCmd(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);

		int timePeriod = CommUtil.StrToInt(request.getParameter("TimePeriod"));

		switch (currStatus.getCmd())
		{
			case 3:// User单个查询
				msgBean = pRmi.RmiExec(3, this, 0, 25);
				request.getSession().setAttribute("User_DevGJ_Info_" + Sid, (DevGJBean) ((ArrayList<?>) msgBean.getMsg()).get(0));
				currStatus.setJsp("Analog_DevGJ_Info.jsp?Sid=" + Sid + "&TimePeriod=" + timePeriod + "&Id=" + Id +"&pSimu=" + pSimu);
				break;
		}
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		response.sendRedirect(currStatus.getJsp());
	}

	/**
	 * 获取状态 RealStatus、doDefence、doRightClick RealStatus:当前状态 doDefence:左点击查看接口 doRightClick:右点击事件
	 * 
	 * @param request
	 * @param response
	 * @param pRmi
	 * @param pFromZone
	 * @throws ServletException
	 * @throws IOException
	 */
	public void ToPo(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);

		PrintWriter outprint = response.getWriter();
		String Resp = "9999";

		msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
		if (msgBean.getStatus() == MsgBean.STA_SUCCESS)
		{
			Resp = ((String) msgBean.getMsg());
		}

		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		outprint.write(Resp);
	}
	
	/**
	 * 删除模拟数据表格
	 */
	public void DeleteData(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		AnalogBean Analog = new AnalogBean();
		if(Analog.DeleteData(Project_Id + "_" + Id))
		{
			currStatus.setResult("删除数据成功！");
		}
		else
		{
			currStatus.setResult("删除数据失败！");
		}
		currStatus.setJsp("AnalogDataM.jsp?Sid=" + Sid + "&Project_Id=" + Project_Id + "&AnalogType=" + Id.substring(0, 2));
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		response.sendRedirect(currStatus.getJsp());
	}
	
	/**
	 * 计算出所有子系统，所有时段的积水量，新
	 */
	public void WaterAcc(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		String path = "/www/DPP-LOCAL/DPP-LOCAL-WEB/files/analogData/";
		File file = new File(path);
		File[] array = file.listFiles();
		ArrayList<Object> WaterAcc = new ArrayList<Object>();
		
		if(currStatus.getFunc_Type_Id().equals("YJ"))
		{
			currStatus.setJsp("Analog_Water_Map.jsp?Sid=" + Sid + "&Project_Id="+Project_Id + "&pSimu=" + pSimu);
			if(pSimu != null && pSimu.length() > 0)
			{
				for (int i = 0; i < array.length; i++)
				{
					//雨井
					if (array[i].getName().substring(0, 6).equals(Project_Id)
							&& array[i].getName().substring(7, 9).equals("YJ"))
					{
						String WaterAccList = "";
						String FileName = array[i].getName();
						WaterAccBean waterAccBean = new WaterAccBean();
						WaterAccList = waterAccBean.analog_Y(FileName.substring(0,12), pSimu);
						if(WaterAccList.contains("|"))
						{
							String[] Water = WaterAccList.split(";");
							for (int j = 0; j < Water.length; j++)
							{
								WaterAccBean waterBean = new WaterAccBean();
								waterBean.setSysId(Water[j].substring(0, 5));
								waterBean.setTimePeriod(Integer.toString(j));
								waterBean.setWater(Water[j].substring(5));
								waterBean.setStatus("0");
								WaterAcc.add(waterBean);
							}
						}else
						{
							WaterAccBean waterBean = new WaterAccBean();
							waterBean.setSysId(WaterAccList.substring(7));
							waterBean.setStatus("1");
							WaterAcc.add(waterBean);
						}
					}
				}
			}
		}
		//污井
		else if(currStatus.getFunc_Type_Id().equals("WJ"))
		{
			currStatus.setJsp("Analog_Sewage_Map.jsp?Sid=" + Sid + "&Project_Id="+Project_Id + "&pSimu=" + pSimu);
			if(pSimu != null && pSimu.length() > 0)
			{
				for (int i = 0; i < array.length; i++)
				{
					if (array[i].getName().substring(0, 6).equals(Project_Id)
							&& array[i].getName().substring(7, 9).equals("WJ"))
					{
						String SewageAccList = "";
						String FileName = array[i].getName();
						WaterAccBean waterAccBean = new WaterAccBean();
						SewageAccList = waterAccBean.analog_W(FileName.substring(0,12), pSimu);
						if(SewageAccList.contains("|"))
						{
							String[] Water = SewageAccList.split(";");
							for (int j = 0; j < Water.length; j++)
							{
								WaterAccBean waterBean = new WaterAccBean();
								waterBean.setSysId(Water[j].substring(0, 5));
								waterBean.setTimePeriod(Integer.toString(j));
								waterBean.setWater(Water[j].substring(5));
								waterBean.setStatus("0");
								WaterAcc.add(waterBean);
							}
						}else
						{
							WaterAccBean waterBean = new WaterAccBean();
							waterBean.setSysId(SewageAccList.substring(7));
							waterBean.setStatus("1");
							WaterAcc.add(waterBean);
						}
					}
				}
			}
		}
//		PrintWriter outprint = response.getWriter();
//		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
//		String jsonObj = JSONObject.toJSONString(WaterAcc);
//		response.setCharacterEncoding("UTF-8");
//		outprint.write(jsonObj);
//		outprint.flush();
		
		request.getSession().setAttribute("WaterAcc_" + Sid, WaterAcc);
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		response.sendRedirect(currStatus.getJsp());
	}

	/**
	 * 动态返回模拟计算时段积水量
	 * 
	 * @param request
	 * @param response
	 * @param pRmi
	 * @param pFromZone
	 * @throws ServletException
	 * @throws IOException
	 */
	public void AnalogToPo(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);

		PrintWriter outprint = response.getWriter();
		String Resp = "9999";
		
		msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
		if (msgBean.getStatus() == MsgBean.STA_SUCCESS)
		{
			String yjResp = ((String) msgBean.getMsg()).substring(4);
			Resp = "0000" + yjResp;
		}

		//System.out.println("Resp["+Resp+"]");
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		outprint.write(Resp);
	}

	/**
	 * 返回子系统号
	 * 
	 * @param request
	 * @param response
	 * @param pRmi
	 * @param pFromZone
	 * @throws ServletException
	 * @throws IOException
	 */
	public void FileToPo(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);

		PrintWriter outprint = response.getWriter();
		String Resp = "9999";

		String FileName = "";
		if (msgBean.getStatus() == MsgBean.STA_SUCCESS)
		{
			String path = "/www/DPP-LOCAL/DPP-LOCAL-WEB/files/analogData/";
			File file = new File(path);

			File[] array = file.listFiles();

			for (int i = 0; i < array.length; i++)
			{
				if (array[i].isFile() && array[i].getName().substring(0, 6).equals(Id))
				{
					FileName += array[i].getName().substring(7, 12) + ",";

				}
			}
			Resp = "0000" + FileName;
		}
		// System.out.println(Resp);
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		outprint.write(Resp);
	}

	/**
	 * 地图接口 的 doDragging、doAddMarke、doDel
	 * 
	 * @param request
	 * @param response
	 * @param pRmi
	 * @param pFromZone
	 * @throws ServletException
	 * @throws IOException
	 */
	public void doDragging(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);

		PrintWriter outprint = response.getWriter();
		String Resp = "9999";

		msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
		if (msgBean.getStatus() == MsgBean.STA_SUCCESS)
		{
			Resp = "0000";
			msgBean = pRmi.RmiExec(0, this, 0, 25);
			request.getSession().setAttribute("Dev_GJ_" + Sid, ((Object) msgBean.getMsg()));
		}

		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		outprint.write(Resp);
	}
	/**
	 * 地图覆盖物旋转 Rotation
	 */
	public void doRotation(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		PrintWriter outprint = response.getWriter();
		String Resp = "9999";
		
		msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
		if (msgBean.getStatus() == MsgBean.STA_SUCCESS)
		{
			Resp = "0000";
		}
		
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		outprint.write(Resp);
	}
	
	/**
	 * 百度坐标转GCJ02
	 * @param request
	 * @param response
	 * @param pRmi
	 * @param pFromZone
	 * @throws ServletException
	 * @throws IOException
	 */
	public void bd09ToGcj02(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		PrintWriter outprint = response.getWriter();
		String Resp = "9999";
		String Lng = request.getParameter("pLng");
		String Lat = request.getParameter("pLat");

		String [] coord;
		
		msgBean = pRmi.RmiExec(2, this, 0, 25);
		if (msgBean.getStatus() == MsgBean.STA_SUCCESS)
		{
			Resp = "0000";
			int succCnt = 0;
			int tmpCnt = 0;
			ArrayList<?> gj_List = (ArrayList<?>) msgBean.getMsg();
			Iterator<?> gj_iterator = gj_List.iterator();
			while (gj_iterator.hasNext())
			{
				DevGJBean devGJBean = (DevGJBean) gj_iterator.next();
				Id = devGJBean.getId();
				Project_Id = devGJBean.getProject_Id();
				Longitude = devGJBean.getLongitude();
				Latitude = devGJBean.getLatitude();
				coord = CoordConv.convLngAndLat(Longitude, Latitude, CoordConv.bd09ll, CoordConv.gcj02);
				WX_Lng = coord[0];
				WX_Lat = coord[1];
				msgBean = pRmi.RmiExec(51, this, 0, 25);
				if (msgBean.getStatus() == MsgBean.STA_SUCCESS)
				{
					succCnt ++;
				}
				tmpCnt ++;
			}
			Resp += "成功转换[" + String.valueOf(succCnt) + "/" + String.valueOf(tmpCnt) + "]个\\n";
			ProjectInfoBean projectInfoBean = new ProjectInfoBean();
			coord = CoordConv.convLngAndLat(Lng, Lat, CoordConv.bd09ll, CoordConv.gcj02);
			projectInfoBean.setId(currStatus.getFunc_Project_Id());
			projectInfoBean.setWX_Lng(coord[0]);
			projectInfoBean.setWX_Lat(coord[1]);			
			msgBean = pRmi.RmiExec(12, projectInfoBean, 0, 25);
		}
		
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		outprint.write(Resp);
	}
	
	public void getSysId(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		PrintWriter outprint = response.getWriter();
		String Resp = "9999";
		
		msgBean = pRmi.RmiExec(24, this, 0, 25);
		if (msgBean.getStatus() == MsgBean.STA_SUCCESS)
		{
			Resp = (String) msgBean.getMsg();
		}
		System.out.println(Resp);
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		outprint.write(Resp);
	}

	/**
	 * 管线命名
	 * 
	 * @param GJ_Id
	 * @return
	 */
	public String dealGXID(String GJ_Id)
	{
		String temGJ_Id = ""; // "WJ", "WG" "YJ", "YG"
		if (GJ_Id.contains("WJ"))
		{
			temGJ_Id = GJ_Id.replace("WJ", "WG");
		}
		if (GJ_Id.contains("YJ"))
		{
			temGJ_Id = GJ_Id.replace("YJ", "YG");
		}
		return temGJ_Id;
	}

	/**
	 * 导入Excel文档 解析文档中的管井详细数据
	 * 
	 * @param request
	 * @param response
	 * @param pRmi
	 * @param pFromZone
	 * @param pConfig
	 * 
	 */
	public void ImportExcel(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone, ServletConfig pConfig)
	{
		System.out.println("base_Height["+1+"]");
		try
		{
			SmartUpload mySmartUpload = new SmartUpload();
			mySmartUpload.initialize(pConfig, request, response);
			mySmartUpload.setAllowedFilesList("xls,xlsx,XLS,XLSX,");
			mySmartUpload.upload();

			Sid = mySmartUpload.getRequest().getParameter("Sid");
			currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
			currStatus.getHtmlData(request, pFromZone);
			Project_Id = mySmartUpload.getRequest().getParameter("Project_Id");
			String Timeout = mySmartUpload.getRequest().getParameter("Timeout");

			if (mySmartUpload.getFiles().getCount() > 0 && mySmartUpload.getFiles().getCount() <= 5)
			{
				String Resp = "";
				for(int n = 0; n < mySmartUpload.getFiles().getCount(); n ++)
				{
					String fileName = mySmartUpload.getFiles().getFile(n).getFilePathName().trim();
					if (mySmartUpload.getFiles().getFile(n).getSize() / 1024 <= 3072)// 最大3M
					{
						String FileSaveRoute = "/www/DPP-LOCAL/DPP-LOCAL-WEB/files/upfiles/";
						// 上传现有文档
						com.jspsmart.upload.File myFile = mySmartUpload.getFiles().getFile(n);
						String File_Name = new SimpleDateFormat("yyyyMMdd").format(new Date()) + CommUtil.Randon() + "." + myFile.getFileExt();
						myFile.saveAs(FileSaveRoute + File_Name);
						// 录入数据库
						InputStream is = new FileInputStream(FileSaveRoute + File_Name);
						HSSFWorkbook wb = new HSSFWorkbook(is);
						HSSFSheet sheet = wb.getSheetAt(0);
						HSSFRow row;
						int succCnt = 0;
						int rowCount = 0;
						for(int i = 1; i < sheet.getPhysicalNumberOfRows() ; i++ ){
							row = sheet.getRow(i);
							if(row == null){
								// 当读取行为空时
								if(i >= sheet.getPhysicalNumberOfRows()){//判断是否是最后一行  
									break;
								}
								continue;
							}else{
								String id = getString(row.getCell(1));
								System.out.println("id["+id+"]");
								if(id.trim().length() <= 0){continue;}
								
								String wgs84_Lng = getString(row.getCell(2));
								String wgs84_Lat = getString(row.getCell(3));
								
								String longitude = getString(row.getCell(4));
								String latitude = getString(row.getCell(5));

								String top_Height = getString(row.getCell(6));
								String base_Height = getString(row.getCell(7));
								System.out.println("base_Height["+base_Height+"]");
								String size = getString(row.getCell(8));
								String in_Id = "";
								for (int j = 9; j < 13; j++)
								{
									if (row.getCell(j) != null && getString(row.getCell(j)).length() > 7) // 编码长度为8
									{
										in_Id += getString(row.getCell(j)) + ",";
									}
								}
								System.out.println("in_Id["+in_Id+"]");
								String out_Id = getString(row.getCell(13));
								String material = getString(row.getCell(14));
								String flag = "1";
								if(in_Id.length() > 5 && out_Id.length() > 5 ){
									if (in_Id.substring(5).contains("000"))
									{
										flag = "0";
									}
									else if (out_Id.substring(5).contains("999"))
									{
										flag = "2";
									}
									else
									{
										flag = "1";
									}
								}
								String data_Lev = getString(row.getCell(15));
								if (data_Lev.length() <= 0)
								{
									data_Lev = "2,2,2";
								}
								String road = getString(row.getCell(16));
								String sign = "0";
								// wgs84坐标转百度坐标
								if(longitude.length() > 0 && latitude.length() > 0){
									String [] lngAndLat = CoordConv.convLngAndLat(longitude, latitude, 1, 5);
									longitude = lngAndLat[0];
									latitude = lngAndLat[1];
									sign = "1";
								}
								
								this.setId(id.toUpperCase());
								this.setWgs84_Lng(!CommUtil.isNumeric(wgs84_Lng) ? "0" : wgs84_Lng);
								this.setWgs84_Lat(!CommUtil.isNumeric(wgs84_Lat) ? "0" : wgs84_Lat);
								this.setLongitude(!CommUtil.isNumeric(longitude) ? "0" : longitude);
								this.setLatitude(!CommUtil.isNumeric(latitude) ? "0" : latitude);
								this.setTop_Height(!CommUtil.isNumeric(top_Height) ? "0" : top_Height);
								this.setBase_Height(!CommUtil.isNumeric(base_Height) ? "0" : base_Height);
								this.setSize(size);
								this.setIn_Id(in_Id.toUpperCase());
								this.setOut_Id(out_Id.toUpperCase());
								this.setMaterial(material);
								this.setFlag(flag);
								this.setData_Lev(data_Lev);
								this.setProject_Id(Project_Id);
								this.setRoad(road);
								this.setSign(sign);

								// 插入提交
								if(sign.equals("1")){
									msgBean = pRmi.RmiExec(10, this, 0, 25);
								}
								else{
									msgBean = pRmi.RmiExec(53, this, 0, 25);
								}
								if (msgBean.getStatus() == MsgBean.STA_SUCCESS)
								{
									succCnt++;
								}
								rowCount ++;
							}
							wb.close();
						}
						Resp += "文件[" + fileName + "]成功导入[" + String.valueOf(succCnt) + "/" + String.valueOf(rowCount) + "]个\\n";
					}
					else
					{
						Resp += "文件[" + fileName + "]上传失败！文档过大，必须小于3M!\\n";
					}
				}
				currStatus.setResult(Resp);
				pRmi.Client(2001,"0000000001","");
			}
			else
			{
				currStatus.setResult("上传失败！每次上传最多5个文件!");
			}
			currStatus.setJsp("Import_Excel.jsp?Sid=" + Sid + "&Project_Id=" + Project_Id + "&Timeout=" + Timeout);
			request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
			response.sendRedirect(currStatus.getJsp());
		}
		catch (Exception exp)
		{
			exp.printStackTrace();
		}
	}

	public void UpdateExcel(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone, ServletConfig pConfig)
	{
		try
		{
			SmartUpload mySmartUpload = new SmartUpload();
			mySmartUpload.initialize(pConfig, request, response);
			mySmartUpload.setAllowedFilesList("xls,xlsx,XLS,XLSX,");
			mySmartUpload.upload();

			Sid = mySmartUpload.getRequest().getParameter("Sid");
			currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
			currStatus.getHtmlData(request, pFromZone);
			Project_Id = mySmartUpload.getRequest().getParameter("Project_Id");
			String Timeout = mySmartUpload.getRequest().getParameter("Timeout");

			if (mySmartUpload.getFiles().getCount() > 0 && mySmartUpload.getFiles().getCount() <=5)
			{
				String Resp = "";
				for(int n = 0; n < mySmartUpload.getFiles().getCount(); n ++)
				{
					String fileName = mySmartUpload.getFiles().getFile(n).getFilePathName().trim();
					if (mySmartUpload.getFiles().getFile(n).getSize() / 1024 <= 3072)// 最大3M
					{
						String FileSaveRoute = "/www/DPP-LOCAL/DPP-LOCAL-WEB/files/upfiles/";
						// 上传现有文档
						com.jspsmart.upload.File myFile = mySmartUpload.getFiles().getFile(n);
						String File_Name = new SimpleDateFormat("yyyyMMdd").format(new Date()) + CommUtil.Randon() + "." + myFile.getFileExt();
						myFile.saveAs(FileSaveRoute + File_Name);
						// 录入数据库
						InputStream is = new FileInputStream(FileSaveRoute + File_Name);
						HSSFWorkbook wb = new HSSFWorkbook(is);
						HSSFSheet sheet = wb.getSheetAt(0);
			            HSSFRow row;
			            int succCnt = 0;
			            int rowCount = 0;
			            String Ids = "";
			            for(int i = 1; i < sheet.getPhysicalNumberOfRows() ; i++ ){
			                row = sheet.getRow(i);
			                if(row == null){
			                    // 当读取行为空时
			                    if(i >= sheet.getPhysicalNumberOfRows()){//判断是否是最后一行  
			                    	break;
			                    }
			                    continue;
			                }else{
			                	String id = getString(row.getCell(1));
								if(id.trim().length() <= 0){continue;}
								
			                	String wgs84_Lng = getString(row.getCell(2));
								String wgs84_Lat = getString(row.getCell(3));
								
			                	String longitude = getString(row.getCell(4));
								String latitude = getString(row.getCell(5));
								
								String top_Height = getString(row.getCell(6));
								String base_Height = getString(row.getCell(7));
								String size = getString(row.getCell(8));
								String in_Id = "";
								for (int j = 9; j < 13; j++)
								{
									if (row.getCell(j) != null && getString(row.getCell(j)).length() > 7) // 编码长度为8
									{
										in_Id += getString(row.getCell(j)) + ",";
									}
								}
								String out_Id = getString(row.getCell(13));
								String material = getString(row.getCell(14));
								String flag = "1";
								if(in_Id.length() > 5 && out_Id.length() > 5 ){
									if (in_Id.substring(5).contains("000"))
									{
										flag = "0";
									}
									else if (out_Id.substring(5).contains("999"))
									{
										flag = "2";
									}
									else
									{
										flag = "1";
									}
								}
								String data_Lev = getString(row.getCell(15));
								if (data_Lev.length() <= 0)
								{
									data_Lev = "2,2,2";
								}
								String road = getString(row.getCell(16));
								String sign = "0";
								// wgs84坐标转百度坐标
								if(longitude.length() > 0 && latitude.length() > 0){
									String [] lngAndLat = CoordConv.convLngAndLat(longitude, latitude, 1, 5);
									longitude = lngAndLat[0];
									latitude = lngAndLat[1];
									sign = "1";
								}
								
								this.setId(id.toUpperCase());
								this.setWgs84_Lng(!CommUtil.isNumeric(wgs84_Lng) ? "0" : wgs84_Lng);
								this.setWgs84_Lat(!CommUtil.isNumeric(wgs84_Lat) ? "0" : wgs84_Lat);
								this.setLongitude(!CommUtil.isNumeric(longitude) ? "0" : longitude);
								this.setLatitude(!CommUtil.isNumeric(latitude) ? "0" : latitude);
								this.setTop_Height(!CommUtil.isNumeric(top_Height) ? "0" : top_Height);
								this.setBase_Height(!CommUtil.isNumeric(base_Height) ? "0" : base_Height);
								this.setSize(size);
								this.setIn_Id(in_Id.toUpperCase());
								this.setOut_Id(out_Id.toUpperCase());
								this.setMaterial(material);
								this.setFlag(flag);
								this.setData_Lev(data_Lev);
								this.setProject_Id(Project_Id);
								this.setRoad(road);
								this.setSign(sign);
		
								// 插入提交
								if(sign.equals("1")){
									msgBean = pRmi.RmiExec(13, this, 0, 25);
								}
								else{
									msgBean = pRmi.RmiExec(52, this, 0, 25);
								}
								if (msgBean.getStatus() == MsgBean.STA_SUCCESS)
								{
									succCnt++;
								}else{Ids += id.toUpperCase() + ",";}
			                    rowCount++;  
			                }
			            }
						wb.close();
						Resp += "文件[" + fileName + "]成功更新[" + String.valueOf(succCnt) + "/" + String.valueOf(rowCount) + "]个\\n失败["+Ids+"]";
					}
					else
					{
						Resp += "文件[" + fileName + "]上传失败！文档过大，必须小于3M!\\n";
					}
				}
				currStatus.setResult(Resp);
				//pRmi.Client(2001,"0000000001","");
			}
			else
			{
				currStatus.setResult("上传失败！每次上传最多5个文件!");
			}

			currStatus.setJsp("Import_Excel.jsp?Sid=" + Sid + "&Project_Id=" + Project_Id + "&Timeout=" + Timeout);
			request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
			response.sendRedirect(currStatus.getJsp());
		}
		catch (Exception exp)
		{
			exp.printStackTrace();
		}
	}
	/**
	 * 窨井图片上传
	 * @param request
	 * @param response
	 * @param pRmi
	 * @param pFromZone
	 * @param pConfig
	 * @throws ServletException
	 * @throws IOException
	 */
	public void DetailSenceUp(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone, ServletConfig pConfig) throws ServletException, IOException
	{
		try
		{
			SmartUpload mySmartUpload = new SmartUpload();
			mySmartUpload.initialize(pConfig, request, response);
			mySmartUpload.setAllowedFilesList("jpg,bmp,JPG,BMP,gif,GIF");
			mySmartUpload.upload();

			Sid = mySmartUpload.getRequest().getParameter("Sid");
			currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
			Id = mySmartUpload.getRequest().getParameter("GJ_Id");
			Project_Id = mySmartUpload.getRequest().getParameter("Project_Id");
			int Cmd = CommUtil.StrToInt(mySmartUpload.getRequest().getParameter("Cmd"));
			//System.out.println("Cmd[" + Cmd + "]");
			if (mySmartUpload.getFiles().getCount() > 0 && mySmartUpload.getFiles().getFile(0).getFilePathName().trim().length() > 0)
			{
				if (mySmartUpload.getFiles().getFile(0).getSize() / 1024 <= 3072)// 最大3M
				{
					String FileSaveRoute = "/www/DPP-LOCAL/DPP-LOCAL-WEB/skin/images/GJ_Img/";
					com.jspsmart.upload.File myFile = mySmartUpload.getFiles().getFile(0);
					String Img_Url = "";
					switch(Cmd){
						case 14:
							In_Img = Project_Id + "_" + Id + "_In"+ "." + myFile.getFileExt();
							Img_Url = FileSaveRoute + In_Img;
							break;
						case 18:
							Out_Img = Project_Id + "_" + Id + "_Out" + "." + myFile.getFileExt();
							Img_Url = FileSaveRoute + Out_Img;
							break;
					}
					myFile.saveAs(Img_Url);
				}
				else
				{
					currStatus.setResult("文档上传失败！文档过大，必须小于3M!");
				}
			}
			msgBean = pRmi.RmiExec(Cmd, this, 0, 25);
			msgBean = pRmi.RmiExec(6, this, 0, 25);
			currStatus.setJsp("Admin_DevGJ_Info.jsp?Sid=" + Sid);
			request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
			request.getSession().setAttribute("Admin_DevGJ_Info_" + Sid, (DevGJBean) ((ArrayList<?>) msgBean.getMsg()).get(0));
			response.sendRedirect(currStatus.getJsp());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 导出管井数据列表
	 * 
	 * @param request
	 * @param response
	 * @param pRmi
	 * @param pFromZone
	 * @param pConfig
	 * 
	 */
	public void XLQRExcel(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone)
	{
		try
		{
			getHtmlData(request);
			currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
			currStatus.getHtmlData(request, pFromZone);
			SimpleDateFormat SimFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			// String BT =
			// currStatus.getVecDate().get(0).toString().substring(5, 10);
			// String ET =
			// currStatus.getVecDate().get(1).toString().substring(5, 10);
			String SheetName = "管井信息表";
			String UPLOAD_NAME = SimFormat.format(new Date());
			//System.out.println("SheetName [" + SheetName + "]");
			msgBean = pRmi.RmiExec(0, this, 0, 25);
			ArrayList<?> gj_List = (ArrayList<?>) msgBean.getMsg();
			int row_Index = 0;
			Label cell = null;
			if (null != gj_List)
			{
				WritableWorkbook book = Workbook.createWorkbook(new File(UPLOAD_PATH + UPLOAD_NAME + ".xls"));
				// 生成名为"第一页"的工作表，参数0表示这是第一页
				WritableSheet sheet = book.createSheet(SheetName, 0);

				// 字体格式1
				WritableFont wf = new WritableFont(WritableFont.createFont("normal"), 14, WritableFont.BOLD, false);
				WritableCellFormat font1 = new WritableCellFormat(wf);
				// wf.setColour(Colour.BLACK);//字体颜色
				font1.setAlignment(Alignment.CENTRE);// 设置居中
				font1.setVerticalAlignment(VerticalAlignment.CENTRE); // 设置为垂直居中
				font1.setBorder(Border.ALL, BorderLineStyle.THIN);// 设置边框线

				// 字体格式2
				WritableFont wf2 = new WritableFont(WritableFont.createFont("normal"), 10, WritableFont.NO_BOLD, false);
				WritableCellFormat font2 = new WritableCellFormat(wf2);
				wf2.setColour(Colour.BLACK);// 字体颜色
				font2.setAlignment(Alignment.CENTRE);// 设置居中
				font2.setVerticalAlignment(VerticalAlignment.CENTRE); // 设置为垂直居中
				font2.setBorder(Border.ALL, BorderLineStyle.THIN);// 设置边框线

				// // 字体格式3
				// WritableFont wf3 = new
				// WritableFont(WritableFont.createFont("normal"),
				// 10,WritableFont.BOLD, false);
				// WritableCellFormat font3 = new WritableCellFormat(wf3);
				// font3.setBorder(Border.ALL, BorderLineStyle.THIN);//设置边框线
				//
				// // 字体格式4
				// WritableFont wf4 = new
				// WritableFont(WritableFont.createFont("normal"),
				// 10,WritableFont.BOLD, false);
				// WritableCellFormat font4 = new WritableCellFormat(wf4);
				// wf4.setColour(Colour.BLACK);// 字体颜色
				// font4.setAlignment(Alignment.CENTRE);// 设置居中
				// font4.setBorder(Border.ALL, BorderLineStyle.THIN);// 设置边框线
				// font4.setBackground(jxl.format.Colour.TURQUOISE);//
				// 设置单元格的背景颜色

				sheet.setRowView(row_Index, 450);
				sheet.setColumnView(row_Index, 25);
				cell = new Label(0, 0, "编码", font1);
				sheet.addCell(cell);
				cell = new Label(1, 0, "道路", font1);
				sheet.addCell(cell);
				cell = new Label(2, 0, "顶部标高", font1);
				sheet.addCell(cell);
				cell = new Label(3, 0, "底部标高", font1);
				sheet.addCell(cell);
				cell = new Label(4, 0, "尺寸(m)", font1);
				sheet.addCell(cell);
				cell = new Label(5, 0, "入管编号", font1);
				sheet.addCell(cell);
				cell = new Label(6, 0, "出管编号", font1);
				sheet.addCell(cell);
				cell = new Label(7, 0, "起终点", font1);
				sheet.addCell(cell);
				cell = new Label(8, 0, "材料类型", font1);
				sheet.addCell(cell);
				cell = new Label(9, 0, "数据等级", font1);
				sheet.addCell(cell);
				cell = new Label(10, 0, "所属项目", font1);
				sheet.addCell(cell);
				cell = new Label(11, 0, "设备名称", font1);
				sheet.addCell(cell);
				cell = new Label(12, 0, "设备深度", font1);
				sheet.addCell(cell);
				cell = new Label(13, 0, "设备号码", font1);
				sheet.addCell(cell);

				Iterator<?> gj_iterator = gj_List.iterator();

				while (gj_iterator.hasNext())
				{
					DevGJBean devGJBean = (DevGJBean) gj_iterator.next();
					Id = devGJBean.getId();
					Top_Height = devGJBean.getTop_Height();
					Base_Height = devGJBean.getBase_Height();
					Size = devGJBean.getSize();
					In_Id = devGJBean.getIn_Id();
					Out_Id = devGJBean.getOut_Id();
					Data_Lev = "";
					try
					{
						if (devGJBean.getData_Lev() != null && !devGJBean.getData_Lev().trim().equals(""))
						{
							switch (Integer.parseInt(devGJBean.getData_Lev()))
							{
								case 1:
									Data_Lev = "人工插值";
									break;
								case 2:
									Data_Lev = "原始探测";
									break;
								case 3:
									Data_Lev = "竣工图数据";
									break;
								case 4:
									Data_Lev = "人工插值经过现场校验";
									break;
								case 5:
									Data_Lev = "原始探测经过二次校验";
									break;
								case 6:
									Data_Lev = "可疑数据";
									break;
								default:
									Data_Lev = "数据有误，需要更改！";
									break;
							}
						}
					}
					catch (Exception e)
					{
						Data_Lev = "数据有误，需要更改！";
					}
					finally
					{
						if (Data_Lev == null)
						{
							Data_Lev = "";
						}
					}
					Material = devGJBean.getMaterial();
					Flag = "";
					try
					{
						if (devGJBean.getFlag() != null && !devGJBean.getFlag().trim().equals(""))
						{
							switch (Integer.parseInt(devGJBean.getFlag()))
							{
								case 0:
									Flag = "起  点";
									break;
								case 1:
									Flag = "中间点";
									break;
								case 2:
									Flag = "终  点";
									break;
								case 3:
									Flag = "水位站";
									break;
								case 4:
									Flag = "泵站";
									break;
								case 5:
									Flag = "排出口";
									break;
								default:
									Flag = "数据有误，需要更改！";
									break;
							}
						}
					}
					catch (Exception e)
					{
						Flag = "数据有误，需要更改！";
					}
					finally
					{
						if (Flag == null)
						{
							Flag = "";
						}
					}
					Project_Name = devGJBean.getProject_Name();
					Equip_Name = devGJBean.getEquip_Name();
					Equip_Height = "";
					if(!devGJBean.getEquip_Height().equals("0"))
					{
						Equip_Height = devGJBean.getEquip_Height();
					}
					Equip_Tel = "";
					if(devGJBean.getEquip_Tel().length() > 0)
					{
						Equip_Tel = devGJBean.getEquip_Tel();
					}
					Road = "";
					if(devGJBean.getEquip_Tel().length() > 0)
					{
						Road = devGJBean.getEquip_Tel();
					}
					row_Index++;
					sheet.setRowView(row_Index, 400);
					sheet.setColumnView(row_Index, 25); // row_Index 列宽度

					cell = new Label(0, row_Index, Id, font2);
					sheet.addCell(cell);
					cell = new Label(1, row_Index, Road, font2);
					sheet.addCell(cell);
					cell = new Label(2, row_Index, Top_Height, font2);
					sheet.addCell(cell);
					cell = new Label(3, row_Index, Base_Height, font2);
					sheet.addCell(cell);
					cell = new Label(4, row_Index, Size, font2);
					sheet.addCell(cell);
					cell = new Label(5, row_Index, In_Id, font2);
					sheet.addCell(cell);
					cell = new Label(6, row_Index, Out_Id, font2);
					sheet.addCell(cell);
					cell = new Label(7, row_Index, Flag, font2);
					sheet.addCell(cell);
					cell = new Label(8, row_Index, Material, font2);
					sheet.addCell(cell);
					cell = new Label(9, row_Index, Data_Lev, font2);
					sheet.addCell(cell);
					cell = new Label(10, row_Index, Project_Name, font2);
					sheet.addCell(cell);
					cell = new Label(11, row_Index, Equip_Name, font2);
					sheet.addCell(cell);
					cell = new Label(12, row_Index, Equip_Height, font2);
					sheet.addCell(cell);
					cell = new Label(13, row_Index, Equip_Tel, font2);
					sheet.addCell(cell);

				}

				book.write();
				book.close();
				try
				{
					PrintWriter out = response.getWriter();
					out.print(UPLOAD_NAME);
				}
				catch (Exception exp)
				{
					exp.printStackTrace();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public static String getString(HSSFCell cell){
		String str = "";
		if(cell != null){
			switch(cell.getCellType()){  
			case XSSFCell.CELL_TYPE_STRING:      
				str = cell.getStringCellValue().trim();    
				break;    
			case XSSFCell.CELL_TYPE_NUMERIC:    
				str = String.valueOf(cell.getNumericCellValue()).trim();  
				break;    
			case XSSFCell.CELL_TYPE_BOOLEAN:     
				str = String.valueOf(cell.getBooleanCellValue()).trim();  
				break;    
			case XSSFCell.CELL_TYPE_BLANK:     
				str = "";    
				break;
			default:
				str = cell.toString().trim();    
			}
		}
		return str;
	}
	
	//获取当前项目下的所有管井
	public void getGJAll(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		PrintWriter outprint = response.getWriter();
		
		msgBean = pRmi.RmiExec(2, this, 0, 0);
		
		List<Object> CData = new ArrayList<Object>();
		if (msgBean.getStatus() == MsgBean.STA_SUCCESS)
		{
			ArrayList<?> gjList = (ArrayList<?>) msgBean.getMsg();
			Iterator<?> gjListIterator = gjList.iterator();
			while (gjListIterator.hasNext()) {
				DevGJBean bean = (DevGJBean)gjListIterator.next();
				DevGJJsonBean jsonBean = new DevGJJsonBean();
				jsonBean.setId(bean.getId());
				jsonBean.setLongitude(bean.getLongitude());
				jsonBean.setLatitude(bean.getLatitude());
				jsonBean.setTop_Height(bean.getTop_Height());
				jsonBean.setBase_Height(bean.getBase_Height());
				jsonBean.setSize(bean.getSize());
				jsonBean.setIn_Id(bean.getIn_Id());
				jsonBean.setOut_Id(bean.getOut_Id());
				jsonBean.setMaterial(bean.getMaterial());
				jsonBean.setFlag(bean.getFlag());
				jsonBean.setData_Lev(bean.getData_Lev());
				jsonBean.setSign(bean.getSign());
				jsonBean.setProject_Id(bean.getProject_Id());
				jsonBean.setProject_Name(bean.getProject_Name());
				jsonBean.setEquip_Id(bean.getEquip_Id());
				jsonBean.setEquip_Name(bean.getEquip_Height());
				jsonBean.setEquip_Height(bean.getEquip_Height());
				jsonBean.setEquip_Tel(bean.getEquip_Tel());
				jsonBean.setEquip_Time(bean.getEquip_Time());
				jsonBean.setRoad(bean.getRoad());
				jsonBean.setUser_Id(bean.getUser_Id());
				jsonBean.setDes(bean.getDes());
				jsonBean.setStatus(bean.getStatus());
				CData.add(jsonBean);
			}
		}
		//System.out.println(Resp);
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		String jsonObj = JSONObject.toJSONString(CData);
		response.setCharacterEncoding("UTF-8");
		outprint = response.getWriter();
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
			case 0:// 查询（类型&项目）
				Sql = " select t.id, t.wgs84_lng, t.wgs84_lat, t.Longitude, t.latitude, t.top_Height, t.base_height, t.Size, t.in_id, t.out_id, t.Material, t.Flag, t.Data_Lev, round((t.curr_data),2) , t.sign , t.project_id, t.project_name, t.equip_id ,t.equip_name ,t.equip_height ,t.equip_tel, t.In_Img, t.Out_Img, t.equip_time, t.road, t.rotation" + " from view_dev_gj t where t.id like '%" + currStatus.getFunc_Sub_Type_Id() + "%' " + " and t.project_id = '" + currStatus.getFunc_Project_Id() + "' " + " order by t.id  ";
				break;
			case 1:// 查询（全部）
				Sql = " select t.id, t.wgs84_lng, t.wgs84_lat, t.Longitude, t.latitude, t.top_Height, t.base_height, t.Size, t.in_id, t.out_id, t.Material, t.Flag, t.Data_Lev, round((t.curr_data),2) , t.sign , t.project_id, t.project_name, t.equip_id ,t.equip_name ,t.equip_height ,t.equip_tel, t.In_Img, t.Out_Img, t.equip_time, t.road, t.rotation" + " from view_dev_gj t where t.project_id = '" + Project_Id + "' "  + " order by t.id  ";
				break;
			case 2:// 查询（类型&项目）
				Sql = " select t.id, t.wgs84_lng, t.wgs84_lat, t.Longitude, t.latitude, t.top_Height, t.base_height, t.Size, t.in_id, t.out_id, t.Material, t.Flag, t.Data_Lev, round((t.curr_data),2) , t.sign , t.project_id, t.project_name, t.equip_id ,t.equip_name ,t.equip_height ,t.equip_tel, t.In_Img, t.Out_Img, t.equip_time, t.road, t.rotation" + " from view_dev_gj t where t.project_id = '" + currStatus.getFunc_Project_Id() + "' " + " order by t.id  ";
				break;

			case 3:// 查询（单个）
			case 6:
				Sql = " select t.id, t.wgs84_lng, t.wgs84_lat, t.Longitude, t.latitude, t.top_Height, t.base_height, t.Size, t.in_id, t.out_id, t.Material, t.Flag, t.Data_Lev, round((t.curr_data),2) , t.sign , t.project_id, t.project_name, t.equip_id ,t.equip_name ,t.equip_height ,t.equip_tel, t.In_Img, t.Out_Img, t.equip_time, t.road, t.rotation" + " from view_dev_gj t " + " where t.id = '" + Id + "' and t.project_id = '" + currStatus.getFunc_Project_Id() + "'" + " order by t.id  ";
				break;
			case 4:// 查询（多个）
				Sql = " select t.id, t.wgs84_lng, t.wgs84_lat, t.Longitude, t.latitude, t.top_Height, t.base_height, t.Size, t.in_id, t.out_id, t.Material, t.Flag, t.Data_Lev, round((t.curr_data),2) , t.sign , t.project_id, t.project_name, t.equip_id ,t.equip_name ,t.equip_height ,t.equip_tel, t.In_Img, t.Out_Img, t.equip_time, t.road, t.rotation" + " from view_dev_gj t " + " where instr('" + Id + "', t.id) > 0 and t.project_id = '" + currStatus.getFunc_Project_Id() + "'" + " order by t.id  ";
				break;
			case 5:// 查询（项目&子系统）
				Sql = " select t.id, t.wgs84_lng, t.wgs84_lat, t.Longitude, t.latitude, t.top_Height, t.base_height, t.Size, t.in_id, t.out_id, t.Material, t.Flag, t.Data_Lev, round((t.curr_data),2) , t.sign , t.project_id, t.project_name, t.equip_id ,t.equip_name ,t.equip_height ,t.equip_tel, t.In_Img, t.Out_Img, t.equip_time, t.road, t.rotation" + " from view_dev_gj t " + " where t.project_id = '" + Project_Id + "'" + " and substr(t.id, 3, 3) = '" + Subsys_Id + "'" + " order by t.id";
				break;
			case 7:// 查询（下载地图）
				Sql = " select t.id, t.wgs84_lng, t.wgs84_lat, t.Longitude, t.latitude, t.top_Height, t.base_height, t.Size, t.in_id, t.out_id, t.Material, t.Flag, t.Data_Lev, round((t.curr_data),2) , t.sign , t.project_id, t.project_name, t.equip_id ,t.equip_name ,t.equip_height ,t.equip_tel, t.In_Img, t.Out_Img, t.equip_time, t.road, t.rotation" + " from view_dev_gj t " + " where t.id like '" + Id + "%' and t.project_id = '" + Project_Id + "'" + " order by t.id  ";
				break;
			case 8:// 查询（道路）
				Sql = " select t.id, t.wgs84_lng, t.wgs84_lat, t.Longitude, t.latitude, t.top_Height, t.base_height, t.Size, t.in_id, t.out_id, t.Material, t.Flag, t.Data_Lev, round((t.curr_data),2) , t.sign , t.project_id, t.project_name, t.equip_id ,t.equip_name ,t.equip_height ,t.equip_tel, t.In_Img, t.Out_Img, t.equip_time, t.road, t.rotation" + " from view_dev_gj t " + " where t.road like '" + Road + "%' and t.project_id = '" + Project_Id + "'" + " order by t.id  ";
				break;
			case 10:// 添加
				Sql = "insert into dev_gj(id, wgs84_lng, wgs84_lat, Longitude, latitude, top_Height, base_height, Size, in_id, out_id, Material, Flag, Data_Lev, project_id, road, sign) " + "values('" + Id + "','" + Wgs84_Lng + "','" + Wgs84_Lat + "','" + Longitude + "','" + Latitude + "','" + Top_Height + "','" + Base_Height + "','" + Size + "','" + In_Id + "','" + Out_Id + "','" + Material + "','" + Flag + "','" + Data_Lev + "','" + Project_Id + "','" + Road + "', " + Sign + ")";
				break;
			case 53:// 添加-没有经纬度
				Sql = "insert into dev_gj(id, top_Height, base_height, Size, in_id, out_id, Material, Flag, Data_Lev, project_id, road) " + "values('" + Id + "','" + Top_Height + "','" + Base_Height + "','" + Size + "','" + In_Id + "','" + Out_Id + "','" + Material + "','" + Flag + "','" + Data_Lev + "','" + Project_Id + "','" + Road + ")";
				break;
			case 11:// 编辑
				Sql = " update dev_gj t set t.in_id= '" + In_Id + "', t.out_id = '" + Out_Id + "' ,t.top_height= '" + Top_Height + "', t.base_height = '" + Base_Height + "', t.size = '" + Size + "', t.Flag = '" + Flag + "', t.Data_Lev = '" + Data_Lev + "',t.material = '" + Material + "', t.gj_name = '" + Equip_Name + "',t.equip_height = '" + Equip_Height + "',t.equip_tel = '" + Equip_Tel + "',t.road = '" + Road + "' " + " where t.id = '" + Id + "' and t.project_id = '" + currStatus.getFunc_Project_Id() + "'";
				break;
			case 19:// 编辑
				Sql = " update dev_gj t set t.in_id= '" + In_Id + "', t.out_id = '" + Out_Id + "' ,t.top_height= '" + Top_Height + "', t.base_height = '" + Base_Height + "', t.size = '" + Size + "', t.Flag = '" + Flag + "', t.Data_Lev = '" + Data_Lev + "',t.material = '" + Material + "', t.gj_name = '" + Equip_Name + "',t.equip_height = '" + Equip_Height + "',t.equip_tel = '" + Equip_Tel + "',t.road = '" + Road + "' " + " where t.id = '" + Id + "' and t.project_id = '" + Project_Id + "'";
				break;
			case 12:// 删除
				Sql = " delete from dev_gj where id = '" + Id + "' and project_id = '" + currStatus.getFunc_Project_Id() + "'";
				break;

			case 13:// 管井更新
				Sql = " update dev_gj t set t.wgs84_lng = '" + Wgs84_Lng + "',t.Wgs84_Lat= '" + Wgs84_Lat + "',t.Longitude= '" + Longitude + "',t.latitude= '" + Latitude + "',t.in_id= '" + In_Id + "', t.out_id = '" + Out_Id + "' ,t.top_height= '" + Top_Height + "', t.base_height = '" + Base_Height + "', t.size = '" + Size + "', t.Flag = '" + Flag + "', t.Data_Lev = '" + Data_Lev + "',t.material = '" + Material + "',t.road = '" + Road + "',t.sign = '" + Sign + "' " + " where t.id = '" + Id + "' and t.project_id = '" + Project_Id + "'";
				break;
			case 52:// 管井更新-不更新经纬度
				Sql = " update dev_gj t set t.in_id= '" + In_Id + "', t.out_id = '" + Out_Id + "' ,t.top_height= '" + Top_Height + "', t.base_height = '" + Base_Height + "', t.size = '" + Size + "', t.Flag = '" + Flag + "', t.Data_Lev = '" + Data_Lev + "',t.material = '" + Material + "',t.road = '" + Road + "' " + " where t.id = '" + Id + "' and t.project_id = '" + Project_Id + "'";
				break;

			case 14:// 窨井内图更新
				Sql = " update dev_gj t set t.in_Img = '" + In_Img + "' where t.id = '" + Id + "' and t.project_id = '" + Project_Id + "'";
				break;
			case 18:// 窨井外图更新
				Sql = " update dev_gj t set t.out_Img = '" + Out_Img + "' where t.id = '" + Id + "' and t.project_id = '" + Project_Id + "'";
				break;
			case 15:// 地图拖拽同步更新
				Sql = " update dev_gj t set t.longitude = '" + Longitude + "', t.latitude = '" + Latitude + "' " + " where t.id = '" + Id + "' and t.project_id = '" + Project_Id + "'";
				break;
			case 16:// 删除标注接口
				Sql = " update dev_gj t set t.sign = '0' " + " where t.id = '" + Id + "' and t.project_id = '" + Project_Id + "'";
				break;
			case 17:// 添加标注接口
				Sql = " update dev_gj t set t.sign = '1', t.longitude = '" + Longitude + "', t.latitude = '" + Latitude + "' " + " where t.id = '" + Id + "' and t.project_id = '" + Project_Id + "'";
				break;
			case 21:// 获取已标注管井
				Sql = "{? = call Func_GJ_Get('" + Id + "', '" + Road + "')}";
				break;
			case 22:// 获取已标注管井
				Sql = "{? = call Func_GJ_Analog('" + Id + "','" + currStatus.getFunc_Sub_Type_Id() + "')}";
				break;
			case 23:// 获取未标注管井
				Sql = "{? = call Func_UnMark_GJ_Get('" + Project_Id + "')}";
				break;
			case 24: // 查询本项目的所有子系统
				Sql = "{? = call Func_SysId_Get('" + Project_Id + "')}";
				break;
			case 50:// 地图拖拽更新旋转角度
				Sql = " update dev_gj t set t.rotation = '" + Rotation + "' " + " where t.id = '" + Id + "' and t.project_id = '" + Project_Id + "'";
				break;
			case 51:// 百度坐标系转腾讯坐标系
				Sql = " update dev_gj t set t.wx_lng = '" + WX_Lng + "',t.wx_lat = '" + WX_Lat + "' where t.id = '" + Id + "' and t.project_id = '" + Project_Id + "'";
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
			setId(pRs.getString(1));
			setWgs84_Lng(pRs.getString(2));
			setWgs84_Lat(pRs.getString(3));
			setLongitude(pRs.getString(4));
			setLatitude(pRs.getString(5));
			setTop_Height(pRs.getString(6));
			setBase_Height(pRs.getString(7));
			setSize(pRs.getString(8));
			setIn_Id(pRs.getString(9));
			setOut_Id(pRs.getString(10));
			setMaterial(pRs.getString(11));
			setFlag(pRs.getString(12));
			setData_Lev(pRs.getString(13));
			setCurr_Data(pRs.getString(14));
			setSign(pRs.getString(15));
			setProject_Id(pRs.getString(16));
			setProject_Name(pRs.getString(17));
			setEquip_Id(pRs.getString(18));
			setEquip_Name(pRs.getString(19));
			setEquip_Height(pRs.getString(20));
			setEquip_Tel(pRs.getString(21));
			setIn_Img(pRs.getString(22));
			setOut_Img(pRs.getString(23));
			setEquip_Time(pRs.getString(24));
			setRoad(pRs.getString(25));
			setRotation(pRs.getString(26));
			setUser_Id(pRs.getString(27));
			setDes(pRs.getString(28));
			setStatus(pRs.getString(29));
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
			setSid(CommUtil.StrToGB2312(request.getParameter("Sid")));
			setId(CommUtil.StrToGB2312(request.getParameter("Id")));
			setWgs84_Lng(CommUtil.StrToGB2312(request.getParameter("Wgs84_Lng")));
			setWgs84_Lat(CommUtil.StrToGB2312(request.getParameter("Wgs84_Lat")));
			setLongitude(CommUtil.StrToGB2312(request.getParameter("Longitude")));
			setLatitude(CommUtil.StrToGB2312(request.getParameter("Latitude")));
			setTop_Height(CommUtil.StrToGB2312(request.getParameter("Top_Height")));
			setBase_Height(CommUtil.StrToGB2312(request.getParameter("Base_Height")));
			setSize(CommUtil.StrToGB2312(request.getParameter("Size")));
			setIn_Id(CommUtil.StrToGB2312(request.getParameter("In_Id")));
			setOut_Id(CommUtil.StrToGB2312(request.getParameter("Out_Id")));
			setMaterial(CommUtil.StrToGB2312(request.getParameter("Material")));
			setFlag(CommUtil.StrToGB2312(request.getParameter("Flag")));
			setData_Lev(CommUtil.StrToGB2312(request.getParameter("Data_Lev")));
			setCurr_Data(CommUtil.StrToGB2312(request.getParameter("Curr_Data")));
			setSign(CommUtil.StrToGB2312(request.getParameter("Sign")));
			setProject_Id(CommUtil.StrToGB2312(request.getParameter("Project_Id")));
			setEquip_Id(CommUtil.StrToGB2312(request.getParameter("Equip_Id")));
			setEquip_Name(CommUtil.StrToGB2312(request.getParameter("Equip_Name")));
			setEquip_Height(CommUtil.StrToGB2312(request.getParameter("Equip_Height")));
			setEquip_Tel(CommUtil.StrToGB2312(request.getParameter("Equip_Tel")));
			setIn_Img(CommUtil.StrToGB2312(request.getParameter("In_Img")));
			setOut_Img(CommUtil.StrToGB2312(request.getParameter("Out_Img")));
			setEquip_Time(CommUtil.StrToGB2312(request.getParameter("Equip_Time")));
			setRoad(CommUtil.StrToGB2312(request.getParameter("Road")));
			setRotation(CommUtil.StrToGB2312(request.getParameter("Rotation")));
			setUser_Id(CommUtil.StrToGB2312(request.getParameter("User_Id")));
			setDes(CommUtil.StrToGB2312(request.getParameter("Des")));
			setStatus(CommUtil.StrToGB2312(request.getParameter("Status")));
			setpSimu(CommUtil.StrToGB2312(request.getParameter("pSimu")));
		}
		catch (Exception Exp)
		{
			Exp.printStackTrace();
		}
		return IsOK;
	}

	private String	Id;
	
	private String Wgs84_Lng;
	private String Wgs84_Lat;
	
	private String	Longitude;
	private String	Latitude;
	private String	Top_Height;
	private String	Base_Height;
	private String	Size;
	private String	In_Id;
	private String	Out_Id;
	private String	Material;
	private String	Flag;
	private String	Data_Lev;

	private String	Sign;
	private String	Project_Id;
	private String	Project_Name;
	private String	Equip_Id;
	private String	Equip_Name;
	private String	Equip_Height;
	private String	Equip_Tel;
	private String	Equip_Time;
	private String	Road;
	private String	Rotation;

	private String	User_Id;
	private String	Des;
	private String	Status;
	
	private String	WX_Lng;
	private String	WX_Lat;
	
	private String	pSimu; //降雨强度

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public String getUser_Id() {
		return User_Id;
	}

	public void setUser_Id(String user_Id) {
		User_Id = user_Id;
	}

	public String getDes() {
		return Des;
	}

	public void setDes(String des) {
		Des = des;
	}

	public String getWX_Lng() {
		return WX_Lng;
	}

	public void setWX_Lng(String wX_Lng) {
		WX_Lng = wX_Lng;
	}

	public String getWX_Lat() {
		return WX_Lat;
	}

	public void setWX_Lat(String wX_Lat) {
		WX_Lat = wX_Lat;
	}

	public String getWgs84_Lng() {
		return Wgs84_Lng;
	}

	public void setWgs84_Lng(String wgs84_Lng) {
		Wgs84_Lng = wgs84_Lng;
	}

	public String getWgs84_Lat() {
		return Wgs84_Lat;
	}

	public void setWgs84_Lat(String wgs84_Lat) {
		Wgs84_Lat = wgs84_Lat;
	}

	public String getpSimu()
	{
		return pSimu;
	}

	public void setpSimu(String pSimu)
	{
		this.pSimu = pSimu;
	}

	public String getRotation()
	{
		return Rotation;
	}

	public void setRotation(String rotation)
	{
		Rotation = rotation;
	}

	public String getRoad()
	{
		return Road;
	}

	public void setRoad(String road)
	{
		Road = road;
	}

	public String getEquip_Time()
	{
		return Equip_Time;
	}

	public void setEquip_Time(String equip_Time)
	{
		Equip_Time = equip_Time;
	}

	public String getEquip_Tel()
	{
		return Equip_Tel;
	}

	public void setEquip_Tel(String equip_Tel)
	{
		Equip_Tel = equip_Tel;
	}

	private String	Curr_Data;
	private String	Subsys_Id;
	private String	In_Img;
	private String	Out_Img;

	private String	Sid;

	public String getSubsys_Id()
	{
		return Subsys_Id;
	}

	public void setSubsys_Id(String subsys_Id)
	{
		Subsys_Id = subsys_Id;
	}

	public String getSize()
	{
		return Size;
	}

	public void setSize(String size)
	{
		Size = size;
	}

	public String getData_Lev()
	{
		return Data_Lev;
	}

	public void setData_Lev(String data_Lev)
	{
		Data_Lev = data_Lev;
	}

	public String getFlag()
	{
		return Flag;
	}

	public void setFlag(String flag)
	{
		Flag = flag;
	}

	public String getEquip_Id()
	{
		return Equip_Id;
	}

	public void setEquip_Id(String equip_Id)
	{
		Equip_Id = equip_Id;
	}

	public String getEquip_Name()
	{
		return Equip_Name;
	}

	public void setEquip_Name(String equip_Name)
	{
		Equip_Name = equip_Name;
	}

	public String getProject_Name()
	{
		return Project_Name;
	}

	public String getEquip_Height()
	{
		return Equip_Height;
	}

	public void setEquip_Height(String equip_Height)
	{
		Equip_Height = equip_Height;
	}

	public void setProject_Name(String project_Name)
	{
		Project_Name = project_Name;
	}

	public String getId()
	{
		return Id;
	}

	public void setId(String id)
	{
		Id = id;
	}

	public String getLongitude()
	{
		return Longitude;
	}

	public void setLongitude(String longitude)
	{
		Longitude = longitude;
	}

	public String getLatitude()
	{
		return Latitude;
	}

	public void setLatitude(String latitude)
	{
		Latitude = latitude;
	}

	public String getTop_Height()
	{
		return Top_Height;
	}

	public void setTop_Height(String top_Height)
	{
		Top_Height = top_Height;
	}

	public String getBase_Height()
	{
		return Base_Height;
	}

	public void setBase_Height(String base_Height)
	{
		Base_Height = base_Height;
	}

	public String getIn_Id()
	{
		return In_Id;
	}

	public void setIn_Id(String in_Id)
	{
		In_Id = in_Id;
	}

	public String getOut_Id()
	{
		return Out_Id;
	}

	public void setOut_Id(String out_Id)
	{
		Out_Id = out_Id;
	}

	public String getMaterial()
	{
		return Material;
	}

	public void setMaterial(String material)
	{
		Material = material;
	}

	public String getCurr_Data()
	{
		return Curr_Data;
	}

	public void setCurr_Data(String curr_Data)
	{
		Curr_Data = curr_Data;
	}

	public String getProject_Id()
	{
		return Project_Id;
	}

	public void setProject_Id(String project_Id)
	{
		Project_Id = project_Id;
	}

	public String getSid()
	{
		return Sid;
	}

	public void setSid(String sid)
	{
		Sid = sid;
	}

	public String getSign()
	{
		return Sign;
	}

	public void setSign(String sign)
	{
		Sign = sign;
	}

	public String getIn_Img()
	{
		return In_Img;
	}

	public void setIn_Img(String in_Img)
	{
		In_Img = in_Img;
	}

	public String getOut_Img()
	{
		return Out_Img;
	}

	public void setOut_Img(String out_Img)
	{
		Out_Img = out_Img;
	}

	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}

}