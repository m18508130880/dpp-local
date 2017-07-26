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

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jspsmart.upload.SmartUpload;

import rmi.Rmi;
import rmi.RmiBean;
import util.*;
import jxl.Sheet;
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


public class DevGJBean extends RmiBean
{	
	public final static long serialVersionUID =RmiBean.RMI_DEVGJ;
	public long getClassId()
	{
		return serialVersionUID;
	}
	
	public DevGJBean()
	{
		super.className = "DevGJBean";
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
		currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);	
		switch(currStatus.getCmd())
		{
			
			case 10://添加
				currStatus.setResult(MsgBean.GetResult(msgBean.getStatus()));
				msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25); // 得到一个封装了结果集的MsgBean对象 
				break;
			case 12://删除
			case 11://编辑
				currStatus.setResult(MsgBean.GetResult(msgBean.getStatus()));
				msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25); // 得到一个封装了结果集的MsgBean对象 
			case 0://查询
				msgBean = pRmi.RmiExec(0, this,  currStatus.getCurrPage(), 25);
				currStatus.setTotalRecord(msgBean.getCount());
				request.getSession().setAttribute("Dev_GJ_" + Sid, (Object)msgBean.getMsg());
				currStatus.setJsp("Dev_GJ.jsp?Sid=" + Sid);
				break;
			case 3://User单个查询
				msgBean = pRmi.RmiExec(3, this, 0, 25);
				request.getSession().setAttribute("User_DevGJ_Info_" + Sid, (DevGJBean)((ArrayList<?>)msgBean.getMsg()).get(0));				
				currStatus.setJsp("User_DevGJ_Info.jsp?Sid=" + Sid + "&Id=" + Id);
				break;
			case 6://Admin查询单个
				msgBean = pRmi.RmiExec(6, this, 0, 25);
				request.getSession().setAttribute("User_DevGJ_Info_" + Sid, (DevGJBean)((ArrayList<?>)msgBean.getMsg()).get(0));				
				currStatus.setJsp("One_GJ.jsp?Sid=" + Sid + "&Id=" + Id);
				break;
		}
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
	   	response.sendRedirect(currStatus.getJsp());
	}
	
	/** 获取状态 RealStatus、doDefence、doRightClick
	 * RealStatus:当前状态     doDefence:左点击查看接口    doRightClick:右点击事件
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
		currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		PrintWriter outprint = response.getWriter();
		String Resp = "9999";
		
		msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
		if(msgBean.getStatus() == MsgBean.STA_SUCCESS)
		{
			Resp = ((String)msgBean.getMsg());
		}
		
		
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		outprint.write(Resp);
	}
	
	/** 地图接口 的 doDragging、doAddMarke、doDel
	 * @param request  
	 * @param response  
	 * @param pRmi   
	 * @param pFromZone  
	 * @throws ServletException  
	 * @throws IOException
	 *
	 *  地图接口的,拖拽功能,添加标记,删除标记
	 */
	public void doDragging(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		PrintWriter outprint = response.getWriter();
		String Resp = "9999";
		
		msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
		if(msgBean.getStatus() == MsgBean.STA_SUCCESS)
		{
			Resp = "0000";
			msgBean = pRmi.RmiExec(0, this, 0, 25);
			request.getSession().setAttribute("Dev_GJ_" + Sid, ((Object)msgBean.getMsg()));
		}
    	
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		outprint.write(Resp);
	}   
	
	/** 导入Excel文档  解析文档中的管井详细数据  
	 * @param request
	 * @param response
	 * @param pRmi
	 * @param pFromZone
	 * @param pConfig
	 * 
	 */
	public void GJExcel(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone, ServletConfig pConfig) 
	{
		try
		{			
			SmartUpload mySmartUpload = new SmartUpload();
//			mySmartUpload.initialize(pConfig, request, response);
			mySmartUpload.setAllowedFilesList("xls,xlsx,XLS,XLSX,");
			mySmartUpload.upload();
								
			Sid = mySmartUpload.getRequest().getParameter("Sid");
			currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
			Project_Id = mySmartUpload.getRequest().getParameter("Project_Id");

			if(mySmartUpload.getFiles().getCount() > 0 && mySmartUpload.getFiles().getFile(0).getFilePathName().trim().length() > 0)
			{
				if(mySmartUpload.getFiles().getFile(0).getSize()/1024 <= 3072)//最大3M
				{		
					String FileSaveRoute = "/www/DPP-LOCAL/DPP-LOCAL-WEB/files/upfiles/";										
					//上传现有文档			
					com.jspsmart.upload.File myFile = mySmartUpload.getFiles().getFile(0);		
					String File_Name = new SimpleDateFormat("yyyyMMdd").format(new Date()) + CommUtil.Randon()+ "." + myFile.getFileExt();			
					myFile.saveAs(FileSaveRoute + File_Name);						
					//录入数据库
					InputStream is = new FileInputStream(FileSaveRoute + File_Name);
					Workbook rwb = Workbook.getWorkbook(is);					
					Sheet rs = rwb.getSheet(0);					
				    int rsRows = rs.getRows();  //excel表格行的数量：依据是否有边框。
				    int succCnt = 0;	
				    int tmpCnt = 0;
				    //业务处理
				    DevGJBean tmpGJBean = null;
				    DevGXBean tmpGXBean = null;
				    
				    String tmpOut_Id = "";
				    //数据起始行
				    int rowStart = 4;
				    //循环开始
				    for(int i=rowStart; i<rsRows; i++)
				    {
				    	if(rs.getCell(1, i).getContents().trim().length() > 0)
				    	{//管道信息第一行,带有所有数据		
				    		if(rowStart != i)
				    		{
					    		tmpGJBean.setOut_Id(tmpOut_Id);	
					    		tmpOut_Id = "";
					    		//插入提交
					    		
					    		msgBean = pRmi.RmiExec(10, tmpGJBean, 0, 25);
					    		msgBean = pRmi.RmiExec(10, tmpGXBean, 0, 25);
						    	if(msgBean.getStatus() == MsgBean.STA_SUCCESS)
								{
						    		succCnt ++;
								}
						    	tmpCnt ++;
				    		}
					    	
				    		tmpGJBean = new DevGJBean();
				    		tmpGXBean = new DevGXBean();
				    		String temGJ_Id = rs.getCell(1, i).getContents().trim(); 
				    		
				    		//管线
				    		tmpGXBean.setId(dealGXID(temGJ_Id));
				    		tmpGXBean.setLength(!CommUtil.isNumeric(rs.getCell(17, i).getContents().trim())?"0":rs.getCell(17, i).getContents().trim());
				    		tmpGXBean.setDiameter(!CommUtil.isNumeric(rs.getCell(6, i).getContents().trim())?"0":rs.getCell(6, i).getContents().trim());
				    		tmpGXBean.setMaterial(rs.getCell(5, i).getContents().trim());
				    		tmpGXBean.setProject_Id(Project_Id);
				    		tmpGXBean.setStart_Id(rs.getCell(4, i).getContents().trim());
				    		tmpGXBean.setEnd_Id(temGJ_Id);
				    		
				    		//管井
				    		tmpGJBean.setId(temGJ_Id);
				    		tmpGJBean.setMaterial(rs.getCell(5, i).getContents().trim());
				    		tmpGJBean.setTop_Height(!CommUtil.isNumeric(rs.getCell(7, i).getContents().trim())?"0":rs.getCell(7, i).getContents().trim());
				    		//(cm)转(m)
				    		Float fBaseHeight = !CommUtil.isNumeric(rs.getCell(8, i).getContents().trim())?0:Float.parseFloat(rs.getCell(8, i).getContents().trim())/100;
				    		tmpGJBean.setBase_Height(fBaseHeight.toString());
				    		tmpGJBean.setProject_Id(Project_Id);
				    		tmpGJBean.setIn_Id(rs.getCell(4, i).getContents().trim());

				    	}
				    	else
				    	{//管道信息余下行,  全是导出管
				    		String tmpGJID = rs.getCell(4, i).getContents().trim();
				    		//导入管\导出管 解析
				    		if(tmpGJID.equals("")){
				    			continue;
				    		}else{
				    			tmpOut_Id += tmpGJID + ",";
				    		}
	
				    	}				    	
				    }	
				    //最后一行插入执行
		    		tmpGJBean.setOut_Id(tmpOut_Id);
		    		
		    		//插入提交
		    		msgBean = pRmi.RmiExec(10, tmpGJBean, 0, 25);
		    		msgBean = pRmi.RmiExec(10, tmpGXBean, 0, 25);
			    	if(msgBean.getStatus() == MsgBean.STA_SUCCESS)
					{
			    		succCnt ++;
					}
			    	tmpCnt ++;
				    currStatus.setResult("成功导入[" + String.valueOf(succCnt) + "/" + String.valueOf(tmpCnt) + "]个");
				}
				else
				{
					currStatus.setResult("文档上传失败！文档过大，必须小于3M!");
				}				
			}
			
			currStatus.setJsp("GJ_Excel.jsp?Sid=" + Sid);
			request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);	
			System.out.println("[CurrStatus_" + Sid + "][" + currStatus + "]");
		   	response.sendRedirect(currStatus.getJsp());
		}
		catch(Exception exp)
		{
			exp.printStackTrace();
		}
	}
	
	/** 管线命名 
	 * @param GJ_Id
	 * @return
	 * : 将 "WJ"替换为 "WG" ;
	 *              "YJ"替换为 "YG"
	 */
	public String dealGXID(String GJ_Id){
		String temGJ_Id = ""; // "WJ", "WG"    "YJ", "YG"
		if(GJ_Id.contains("WJ")){
			temGJ_Id = GJ_Id.replace("WJ", "WG");
		}
		if(GJ_Id.contains("YJ")){
			temGJ_Id = GJ_Id.replace("YJ", "YG");
		}
		return temGJ_Id;
	}
	
	/** 导入Excel文档  解析文档中的管井详细数据  
	 * @param request
	 * @param response
	 * @param pRmi
	 * @param pFromZone
	 * @param pConfig
	 * 
	 */
	public void ImportExcel(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone, ServletConfig pConfig) 
	{
		try
		{			
			SmartUpload mySmartUpload = new SmartUpload();
//			mySmartUpload.initialize(pConfig, request, response);
			mySmartUpload.setAllowedFilesList("xls,xlsx,XLS,XLSX,");
			mySmartUpload.upload();
								
			Sid = mySmartUpload.getRequest().getParameter("Sid");
			currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
			Project_Id = mySmartUpload.getRequest().getParameter("Project_Id");

			if(mySmartUpload.getFiles().getCount() > 0 && mySmartUpload.getFiles().getFile(0).getFilePathName().trim().length() > 0)
			{
				if(mySmartUpload.getFiles().getFile(0).getSize()/1024 <= 3072)//最大3M
				{		
					String FileSaveRoute = "/www/DPP-LOCAL/DPP-LOCAL-WEB/files/upfiles/";										
					//上传现有文档			
					com.jspsmart.upload.File myFile = mySmartUpload.getFiles().getFile(0);		
					String File_Name = new SimpleDateFormat("yyyyMMdd").format(new Date()) + CommUtil.Randon()+ "." + myFile.getFileExt();			
					myFile.saveAs(FileSaveRoute + File_Name);						
					//录入数据库
					InputStream is = new FileInputStream(FileSaveRoute + File_Name);					
					Workbook rwb = Workbook.getWorkbook(is);					
					Sheet rs = rwb.getSheet(0);					
				    int rsRows = rs.getRows();  //excel表格行的数量：依据是否有边框。
				    int succCnt = 0;	
				    int tmpCnt = 0;

				    //数据起始行
				    int rowStart = 1;
				    //循环开始
				    for(int i=rowStart; i<rsRows; i++)
				    {				    	
			    		String id = rs.getCell(1, i).getContents().trim(); 
			    		if(8 > id.length())
			    			continue;
			    		
			    		tmpCnt ++;
			    		String top_Height = rs.getCell(4, i).getContents().trim(); 
			    		String base_Height = rs.getCell(5, i).getContents().trim(); 
			    		String size = rs.getCell(6, i).getContents().trim(); 
			    		String in_Id = ""; 
			    		for(int j=7; j<10; j++)
			    		{
			    			if(rs.getCell(j, i).getContents().trim().length() > 7)	//编码长度为8
			    			{
			    				in_Id += rs.getCell(j, i).getContents().trim() + ","; 
			    			}
			    		}
			    		String out_Id = rs.getCell(10, i).getContents().trim(); 
			    		String material = rs.getCell(11, i).getContents().trim(); 
			    		String flag = "1";
			    		if(in_Id.contains("000"))
			    		{
			    			flag = "0";
			    		}
			    		else if(out_Id.contains("999"))
			    		{
			    			flag = "2";
			    		}
			    		else
			    		{
			    			flag = "1";
			    		}
			    		String data_Lev = rs.getCell(12, i).getContents().trim(); 

			    		this.setId(id.toUpperCase());			    		
			    		this.setTop_Height(!CommUtil.isNumeric(top_Height)?"0":top_Height);
			    		this.setBase_Height(!CommUtil.isNumeric(base_Height)?"0":base_Height);
			    		this.setSize(!CommUtil.isNumeric(size)?"0":size);
			    		this.setIn_Id(in_Id.toUpperCase());
			    		this.setOut_Id(out_Id.toUpperCase());
			    		this.setMaterial(material);
			    		this.setFlag(flag);
			    		this.setData_Lev(data_Lev);
			    		this.setProject_Id(Project_Id);
			    			    		
			    		//插入提交
			    		msgBean = pRmi.RmiExec(10, this, 0, 25);
				    	if(msgBean.getStatus() == MsgBean.STA_SUCCESS)
						{
				    		succCnt ++;
						}				    	
				    }
				    currStatus.setResult("成功导入[" + String.valueOf(succCnt) + "/" + String.valueOf(tmpCnt) + "]个");
				}
				else
				{
					currStatus.setResult("文档上传失败！文档过大，必须小于3M!");
				}				
			}
			
			currStatus.setJsp("Import_Excel.jsp?Sid=" + Sid + "&Project_Id=" + Project_Id);
			request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);	
		   	response.sendRedirect(currStatus.getJsp());
		}
		catch(Exception exp)
		{
			exp.printStackTrace();
		}
	}
	
	/** 导出管井数据列表
	 * @param request
	 * @param response
	 * @param pRmi
	 * @param pFromZone
	 * @param pConfig
	 * 
	 */
	public void XLQRExcel(HttpServletRequest request,HttpServletResponse response, Rmi pRmi, boolean pFromZone) {
		try {
			getHtmlData(request);
			currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
			currStatus.getHtmlData(request, pFromZone);
			SimpleDateFormat SimFormat = new SimpleDateFormat("yyyyMMddHHmmss");
//			String BT = currStatus.getVecDate().get(0).toString().substring(5, 10);
//			String ET = currStatus.getVecDate().get(1).toString().substring(5, 10);
			String SheetName = "管井信息表";
			String UPLOAD_NAME = SimFormat.format(new Date());
			System.out.println("SheetName [" + SheetName + "]" );
			msgBean = pRmi.RmiExec(0, this, 0, 25);
			ArrayList<?> gj_List = (ArrayList<?>) msgBean.getMsg();
			int row_Index = 0;
			Label cell = null;
			if (null != gj_List) {
				WritableWorkbook book = Workbook.createWorkbook(new File(UPLOAD_PATH + UPLOAD_NAME + ".xls"));
				// 生成名为"第一页"的工作表，参数0表示这是第一页
				WritableSheet sheet = book.createSheet(SheetName, 0);
				
				// 字体格式1
				WritableFont wf = new WritableFont(WritableFont.createFont("normal"), 14,WritableFont.BOLD, false);
				WritableCellFormat font1 = new WritableCellFormat(wf);
				// wf.setColour(Colour.BLACK);//字体颜色
				font1.setAlignment(Alignment.CENTRE);// 设置居中
				font1.setVerticalAlignment(VerticalAlignment.CENTRE); //设置为垂直居中
				font1.setBorder(Border.ALL, BorderLineStyle.THIN);//设置边框线
				
				// 字体格式2
				WritableFont wf2 = new WritableFont(WritableFont.createFont("normal"), 10,WritableFont.NO_BOLD, false);
				WritableCellFormat font2 = new WritableCellFormat(wf2);
				wf2.setColour(Colour.BLACK);// 字体颜色
				font2.setAlignment(Alignment.CENTRE);// 设置居中
				font2.setVerticalAlignment(VerticalAlignment.CENTRE); //设置为垂直居中
				font2.setBorder(Border.ALL, BorderLineStyle.THIN);// 设置边框线
				
//				// 字体格式3
//				WritableFont wf3 = new WritableFont(WritableFont.createFont("normal"), 10,WritableFont.BOLD, false);
//				WritableCellFormat font3 = new WritableCellFormat(wf3);
//			    font3.setBorder(Border.ALL, BorderLineStyle.THIN);//设置边框线
//				
//				// 字体格式4
//				WritableFont wf4 = new WritableFont(WritableFont.createFont("normal"), 10,WritableFont.BOLD, false);
//				WritableCellFormat font4 = new WritableCellFormat(wf4);
//				wf4.setColour(Colour.BLACK);// 字体颜色
//				font4.setAlignment(Alignment.CENTRE);// 设置居中
//				font4.setBorder(Border.ALL, BorderLineStyle.THIN);// 设置边框线
//				font4.setBackground(jxl.format.Colour.TURQUOISE);// 设置单元格的背景颜色

				sheet.setRowView(row_Index, 450);
				sheet.setColumnView(row_Index, 25);
				cell=new Label(0,0,"编码",font1);   
			    sheet.addCell(cell);   
			    cell=new Label(1,0,"顶部标高",font1);  
			    sheet.addCell(cell);   
			    cell=new Label(2,0,"底部标高",font1);  
			    sheet.addCell(cell);  
			    cell=new Label(3,0,"尺寸(m)",font1);  
			    sheet.addCell(cell);
			    cell=new Label(4,0,"入管编号",font1);  
			    sheet.addCell(cell);   
			    cell=new Label(5,0,"出管编号",font1);  
			    sheet.addCell(cell);
			    cell=new Label(6,0,"起终点",font1);  
			    sheet.addCell(cell);
			    cell=new Label(7,0,"材料类型",font1);  
			    sheet.addCell(cell);
			    cell=new Label(8,0,"数据等级",font1);  
			    sheet.addCell(cell);
			    cell=new Label(9,0,"所属项目",font1);  
			    sheet.addCell(cell);  
			    cell=new Label(10,0,"设备名称",font1);  
			    sheet.addCell(cell);  
			    
				
				Iterator<?> gj_iterator = gj_List.iterator();

				while (gj_iterator.hasNext()) {
					DevGJBean devGJBean = (DevGJBean) gj_iterator.next();
					Id = devGJBean.getId();
					Top_Height = devGJBean.getTop_Height();
					Base_Height = devGJBean.getBase_Height();
					Size = devGJBean.getSize();
					In_Id = devGJBean.getIn_Id();
					Out_Id = devGJBean.getOut_Id();
					Data_Lev = "";
					try{
						if(devGJBean.getData_Lev() != null && !devGJBean.getData_Lev().trim().equals("")){
						  	switch(Integer.parseInt(devGJBean.getData_Lev())){
					  		case 1:
					  			Data_Lev ="人工插值";
					  			break;
					  		case 2:
						  		Data_Lev ="原始探测";
						  		break;
					  		case 3:
						  		Data_Lev ="竣工图数据";
						  		break;
					  		case 4:
						  		Data_Lev ="人工插值经过现场校验";
						  		break;
					  		case 5:
						  		Data_Lev ="原始探测经过二次校验";
						  		break;
					  		case 6:
						  		Data_Lev ="可疑数据";
						  		break;
					  		default:
						  		Data_Lev ="数据有误，需要更改！";
							  		break;
						  	}
						}
					}catch(Exception e){
					  	Data_Lev ="数据有误，需要更改！";
					}finally{
					  	if(Data_Lev == null){
					  		Data_Lev ="";
					  	}
					}
					Material = devGJBean.getMaterial();
					Flag = "";
					try{
						if(devGJBean.getFlag() != null && !devGJBean.getFlag().trim().equals("")){
						  	switch(Integer.parseInt(devGJBean.getFlag())){
					  		case 0:
					  			Flag ="起  点";
					  			break;
					  		case 1:
					  			Flag ="中间点";
						  		break;
					  		case 2:
					  			Flag ="终  点";
						  		break;
					  		default:
					  			Flag ="数据有误，需要更改！";
							  		break;
						  	}
						}
					}catch(Exception e){
						Flag ="数据有误，需要更改！";
					}finally{
					  	if(Flag == null){
					  		Flag ="";
					  	}
					}
					Project_Name = devGJBean.getProject_Name();
					Equip_Name = devGJBean.getEquip_Name();

					row_Index++;
					sheet.setRowView(row_Index, 400);
					sheet.setColumnView(row_Index, 25); // row_Index 列宽度
					
					cell = new Label(0, row_Index, Id, font2);
					sheet.addCell(cell);
					cell = new Label(1, row_Index, Top_Height, font2);
					sheet.addCell(cell);
					cell = new Label(2, row_Index, Base_Height, font2);
					sheet.addCell(cell);
					cell = new Label(3, row_Index, Size, font2);
					sheet.addCell(cell);
					cell = new Label(4, row_Index, In_Id, font2);
					sheet.addCell(cell);
					cell = new Label(5, row_Index, Out_Id, font2);
					sheet.addCell(cell);
					cell = new Label(6, row_Index, Flag, font2);
					sheet.addCell(cell);
					cell = new Label(7, row_Index, Material, font2);
					sheet.addCell(cell);
					cell = new Label(8, row_Index, Data_Lev, font2);
					sheet.addCell(cell);
					cell = new Label(9, row_Index, Project_Name, font2);
					sheet.addCell(cell);
					cell = new Label(10, row_Index, Equip_Name, font2);
					sheet.addCell(cell);

				}

				book.write();
				book.close();
				try {
					PrintWriter out = response.getWriter();
					out.print(UPLOAD_NAME);
				} catch (Exception exp) {
					exp.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
		
	/** 获取相应sql语句
	 * @see rmi.RmiBean#getSql(int)
	 *  返回的是一个字符串: sql语句
	 */
	public String getSql(int pCmd)
	{  
		String Sql = "";
		switch (pCmd)
		{  
		    case 0://查询（类型&项目）
		    	Sql = " select t.id, t.Longitude, t.latitude, t.top_Height, t.base_height, t.Size, t.in_id, t.out_id, t.Material, t.Flag, t.Data_Lev, round((t.curr_data),2) , t.sign , t.project_id, t.project_name, t.equip_id ,t.equip_name" +
	    	 	   	  " from view_dev_gj t where t.id like '%" + currStatus.getFunc_Sub_Type_Id() + "%' " + 
	    	 	      " and t.project_id = '" + currStatus.getFunc_Project_Id() + "' " +
 	 		          " order by t.id  ";
			   break;
		    case 1://查询（全部）
		    	Sql = " select t.id, t.Longitude, t.latitude, t.top_Height, t.base_height, t.Size, t.in_id, t.out_id, t.Material, t.Flag, t.Data_Lev, round((t.curr_data),2) , t.sign , t.project_id, t.project_name, t.equip_id ,t.equip_name" +
		    		  " from view_dev_gj t " + 
 	 		          " order by t.id  ";
			   break;    

		    case 3://查询（单个）
		    case 6:
		    	Sql = " select t.id, t.Longitude, t.latitude, t.top_Height, t.base_height, t.Size, t.in_id, t.out_id, t.Material, t.Flag, t.Data_Lev, round((t.curr_data),2) , t.sign , t.project_id, t.project_name, t.equip_id ,t.equip_name" +
	    	 	   	  " from view_dev_gj t " +
	    	 	   	  " where t.id = '"+ Id +"'" + 	
		    		  " order by t.id  ";
			   break;		
		    case 4://查询（多个）
		    	Sql = " select t.id, t.Longitude, t.latitude, t.top_Height, t.base_height, t.Size, t.in_id, t.out_id, t.Material, t.Flag, t.Data_Lev, round((t.curr_data),2) , t.sign , t.project_id, t.project_name, t.equip_id ,t.equip_name" +
		    	 	  " from view_dev_gj t " +
		    	 	  " where instr('"+ Id +"', t.id) > 0 " + 	
				      " order by t.id  ";
		    	break; 
		    case 5://查询（项目&子系统）
		    	Sql = " select t.id, t.Longitude, t.latitude, t.top_Height, t.base_height, t.Size, t.in_id, t.out_id, t.Material, t.Flag, t.Data_Lev, round((t.curr_data),2) , t.sign , t.project_id, t.project_name, t.equip_id ,t.equip_name" +
	    	 	   	  " from view_dev_gj t " +
				      " where t.project_id = '" + Project_Id + "'" + 
				      " and substr(t.id, 3, 3) = '"+ Subsys_Id +"'" +
	    	 	   	  " order by t.flag, t.id";
			   break;		
		    case 10://添加
		    	Sql = "insert into dev_gj(id, top_Height, base_height, Size, in_id, out_id, Material, Flag, Data_Lev, project_id) " +
		    			"values('"+ Id +"','"+Top_Height+"','"+Base_Height+"','"+Size+"','"+In_Id+"','"+Out_Id+"','"+Material+"','"+Flag+"','"+Data_Lev+"','"+Project_Id+"')";
		    	break;
		    case 11://编辑
				Sql = " update dev_gj t set t.in_id= '"+ In_Id + "', t.out_id = '"+ Out_Id +"' ,t.top_height= '"+ Top_Height + "', t.base_height = '"+ Base_Height+ "', t.size = '"+ Size + "', t.Flag = '"+ Flag + "', t.Data_Lev = '"+ Data_Lev+"',t.material = '"+ Material + "' " +
					  " where t.id = '"+ Id +"'";
				break;
		   
		    case 12://删除
		    	Sql = " delete from dev_gj where id = '"+ Id +"' ";
		    	break;
		    
		    case 15://地图拖拽同步更新
				Sql = " update dev_gj t set t.longitude = '"+ Longitude +"', t.latitude = '"+ Latitude +"' " +
					  " where t.id = '"+ Id +"' and t.project_id = '"+ Project_Id +"'";
				break;
			case 16://删除标注接口
				Sql = " update dev_gj t set t.sign = '0' " +
				      " where t.id = '"+ Id +"' and t.project_id = '"+ Project_Id +"'";
				break;
			case 17://添加标注接口
				Sql = " update dev_gj t set t.sign = '1', t.longitude = '"+ Longitude +"', t.latitude = '"+ Latitude +"' " +
					  " where t.id = '"+ Id +"' and t.project_id = '"+ Project_Id +"'";
				break;
			case 21://获取已标注管井
				Sql = "{? = call Func_GJ_Get('"+ Id +"')}";
				break;
			case 23://获取未标注管井
				Sql = "{? = call Func_UnMark_GJ_Get('"+ Project_Id +"')}";
				break;
			case 24://GIS实时通知
				Sql = "{? = call Func_News_GJ_Get('"+ Id +"')}";
				break;
			case 40://编辑设备EquipInfo
				Sql = "{call pro_update_dev_gj('" + Equip_Id + "', '" + Equip_Name + "', '" + Id + "', '" + Project_Id + "', '" + After_Project_Id + "')}";
				break;
		}
		return Sql;
	}
	
	/** 将数据库中  结果集的数据  封装到DevGjBean中  
	 * @see rmi.RmiBean#getData(java.sql.ResultSet)
	 * 
	 * 若返回true则表示注入成功
	 */
	public boolean getData(ResultSet pRs)
	{
		boolean IsOK = true;
		try
		{
			setId(pRs.getString(1));
		 	setLongitude(pRs.getString(2));
		 	setLatitude(pRs.getString(3));
		 	setTop_Height(pRs.getString(4));
		 	setBase_Height(pRs.getString(5));
		 	setSize(pRs.getString(6));
		 	setIn_Id(pRs.getString(7));
		 	setOut_Id(pRs.getString(8));
		 	setMaterial(pRs.getString(9));
		 	setFlag(pRs.getString(10));
		 	setData_Lev(pRs.getString(11));
		 	setCurr_Data(pRs.getString(12));
		 	setSign(pRs.getString(13));
		 	setProject_Id(pRs.getString(14));
		 	setProject_Name(pRs.getString(15));
		 	setEquip_Id(pRs.getString(16));
		 	setEquip_Name(pRs.getString(17));

		}
		catch (SQLException sqlExp)
		{
			sqlExp.printStackTrace();
		}
		return IsOK;
	}
	
	/**
	 * 得到页面数据
	 * @param request
	 * @return 返回一个boolean值   true表示注入成功,false表示出现异常
	 *
	 * 将request中数据封装到DevGJBean中  [注入]
	 */
	public boolean getHtmlData(HttpServletRequest request)
	{
		boolean IsOK = true;
		try
		{  
			setSid(CommUtil.StrToGB2312(request.getParameter("Sid")));
			setId(CommUtil.StrToGB2312(request.getParameter("Id")));
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
			setAfter_Project_Id(CommUtil.StrToGB2312(request.getParameter("After_Project_Id")));
			
		}
		catch (Exception Exp)
		{
			Exp.printStackTrace();
		}
		return IsOK;
	}
	
	private String Id;
	private String Longitude;
	private String Latitude;
	private String Top_Height;
	private String Base_Height;
	private String Size;
	private String In_Id;
	private String Out_Id;
	private String Material;
	private String Flag;
	private String Data_Lev;
	
	private String Sign;
	private String Project_Id;
	private String Project_Name;
	private String Equip_Id;
	private String Equip_Name;
	private String Curr_Data;
	private String Subsys_Id;

	private String Sid;
	private String After_Project_Id;
	
	
	public String getAfter_Project_Id() {
		return After_Project_Id;
	}

	public void setAfter_Project_Id(String after_Project_Id) {
		After_Project_Id = after_Project_Id;
	}

	public String getSubsys_Id() {
		return Subsys_Id;
	}

	public void setSubsys_Id(String subsys_Id) {
		Subsys_Id = subsys_Id;
	}

	public String getSize() {
		return Size;
	}

	public void setSize(String size) {
		Size = size;
	}

	public String getData_Lev() {
		return Data_Lev;
	}

	public void setData_Lev(String data_Lev) {
		Data_Lev = data_Lev;
	}

	public String getFlag() {
		return Flag;
	}

	public void setFlag(String flag) {
		Flag = flag;
	}

	public String getEquip_Id() {
		return Equip_Id;
	}

	public void setEquip_Id(String equip_Id) {
		Equip_Id = equip_Id;
	}

	public String getEquip_Name() {
		return Equip_Name;
	}

	public void setEquip_Name(String equip_Name) {
		Equip_Name = equip_Name;
	}

	public String getProject_Name() {
		return Project_Name;
	}
	
	public void setProject_Name(String project_Name) {
		Project_Name = project_Name;
	}
	
	
	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getLongitude() {
		return Longitude;
	}

	public void setLongitude(String longitude) {
		Longitude = longitude;
	}

	public String getLatitude() {
		return Latitude;
	}

	public void setLatitude(String latitude) {
		Latitude = latitude;
	}

	public String getTop_Height() {
		return Top_Height;
	}

	public void setTop_Height(String top_Height) {
		Top_Height = top_Height;
	}

	public String getBase_Height() {
		return Base_Height;
	}

	public void setBase_Height(String base_Height) {
		Base_Height = base_Height;
	}

	public String getIn_Id() {
		return In_Id;
	}

	public void setIn_Id(String in_Id) {
		In_Id = in_Id;
	}

	public String getOut_Id() {
		return Out_Id;
	}

	public void setOut_Id(String out_Id) {
		Out_Id = out_Id;
	}

	public String getMaterial() {
		return Material;
	}

	public void setMaterial(String material) {
		Material = material;
	}

	

	public String getCurr_Data() {
		return Curr_Data;
	}

	public void setCurr_Data(String curr_Data) {
		Curr_Data = curr_Data;
	}

	public String getProject_Id() {
		return Project_Id;
	}

	public void setProject_Id(String project_Id) {
		Project_Id = project_Id;
	}
	
	public String getSid() {
		return Sid;
	}

	public void setSid(String sid) {
		Sid = sid;
	}

	public String getSign() {
		return Sign;
	}

	public void setSign(String sign) {
		Sign = sign;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}