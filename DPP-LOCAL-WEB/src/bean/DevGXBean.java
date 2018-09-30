package bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jsonBean.DevGXJsonBean;
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
import net.sf.json.JSONArray;
import rmi.Rmi;
import rmi.RmiBean;
import util.CommUtil;
import util.CurrStatus;
import util.MsgBean;

import com.alibaba.fastjson.JSONObject;
import com.jspsmart.upload.SmartUpload;

public class DevGXBean extends RmiBean
{	
	public final static long serialVersionUID =RmiBean.RMI_DEVGX;
	public long getClassId()
	{
		return serialVersionUID;
	}
	
	public DevGXBean()
	{
		super.className = "DevGXBean";
	}
	
	public DevGXBean(CurrStatus currStatus)
	{
		super.className = "DevGXBean";
		this.currStatus = currStatus;
	}
	
	public void ExecCmd(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);

		switch(currStatus.getCmd())
		{
			case 12://删除
				String[] Ids = currStatus.getFunc_Sub_Type_Id().split(",");
				for(int i = 0; i < Ids.length; i ++){
					Id = Ids[i];
					msgBean = pRmi.RmiExec(currStatus.getCmd(), this, currStatus.getCurrPage(), 25);
				}
				pRmi.Client(2001,"0000000001","");
				currStatus.setResult(MsgBean.GetResult(msgBean.getStatus()));
				msgBean = pRmi.RmiExec(1, this,  currStatus.getCurrPage(), 25);
				currStatus.setTotalRecord(msgBean.getCount());
				request.getSession().setAttribute("User_DevGX_Info_" + Sid, (Object)msgBean.getMsg());
				currStatus.setJsp("Dev_GX.jsp?Sid=" + Sid);
				break;
			case 11://编辑	
				msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
				currStatus.setResult(MsgBean.GetResult(msgBean.getStatus()));
				pRmi.Client(2001,"0000000001","");
			case 0://admin管线分页查询
				msgBean = pRmi.RmiExec(0, this,  currStatus.getCurrPage(), 25);
				currStatus.setTotalRecord(msgBean.getCount());
				request.getSession().setAttribute("Admin_DevGX_Info_" + Sid, (Object)msgBean.getMsg());
				currStatus.setJsp("Dev_GX.jsp?Sid=" + Sid);
				break;
			case 1://User排序分页查询
				msgBean = pRmi.RmiExec(1, this,  currStatus.getCurrPage(), 25);
				currStatus.setTotalRecord(msgBean.getCount());
				request.getSession().setAttribute("User_DevGX_Info_" + Sid, (Object)msgBean.getMsg());
				currStatus.setJsp("Dev_GX.jsp?Sid=" + Sid);
				break;
			
			case 3://查询单个
				msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
				request.getSession().setAttribute("One_GX_" + Sid, (DevGXBean)((ArrayList<?>)msgBean.getMsg()).get(0));				
				currStatus.setJsp("One_GX.jsp?Sid=" + Sid);
				break;
			case 5://admin 查询(编辑)
				msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
				request.getSession().setAttribute("Dev_GX_Edit_" + Sid, (DevGXBean)((ArrayList<?>)msgBean.getMsg()).get(0));				
				currStatus.setJsp("Dev_GX_Edit.jsp?Sid=" + Sid);
				break;
			case 4://剖面图
				msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
				request.getSession().setAttribute("User_Graph_Cut_GX_" + Sid, (Object)msgBean.getMsg());				
				@SuppressWarnings("rawtypes")
				ArrayList gxObj = (ArrayList)(Object)msgBean.getMsg();
				
				DevGJBean tmpGJBean = new DevGJBean();
				tmpGJBean.setProject_Id(currStatus.getFunc_Project_Id());
				tmpGJBean.setSubsys_Id(Id.substring(2, 5));
				
				msgBean = pRmi.RmiExec(5, tmpGJBean, 0, 25);
				@SuppressWarnings("rawtypes")
				ArrayList gjObj = (ArrayList)(Object)msgBean.getMsg();
				
				AnalogBean analog = new AnalogBean();
				@SuppressWarnings("rawtypes")
				ArrayList gjList = analog.AnalogGJList(gjObj, gxObj, Id);
				request.getSession().setAttribute("User_Graph_Cut_GJ_" + Sid, gjList);
				currStatus.setJsp("User_Graph_Cut.jsp?Sid=" + Sid + "&Id=" + Id);
				break;
		}
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
	   	response.sendRedirect(currStatus.getJsp());
	}
	
	// 画剖面图之前计算排出口数量
	public void getSumOutGJ(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
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
	
	// 画剖面图之前多个排出口，取得管井编号list
	public void getOutGJId(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		PrintWriter outprint = response.getWriter();
		String Resp = "9999";
		
		msgBean = pRmi.RmiExec(4, this, 0, 25);
		//request.getSession().setAttribute("User_Graph_Cut_GX_" + Sid, (Object)msgBean.getMsg());				
		@SuppressWarnings("rawtypes")
		ArrayList gxObj = (ArrayList)(Object)msgBean.getMsg();
		
		DevGJBean tmpGJBean = new DevGJBean();
		tmpGJBean.setProject_Id(currStatus.getFunc_Project_Id());
		tmpGJBean.setSubsys_Id(Id.substring(2, 5));
		
		msgBean = pRmi.RmiExec(5, tmpGJBean, 0, 25);
		@SuppressWarnings("rawtypes")
		ArrayList gjObj = (ArrayList)(Object)msgBean.getMsg();
		
		Resp = "0000" + this.getSIdAndEId(gjObj, gxObj, Id, currStatus.getFunc_Sort_Id());

		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		outprint.write(Resp);
	}
	
	/**
	 * 取到一段管段开始管井和结束管井
	 * @param gjObj
	 * @param gxObj
	 * @param StartGJ 起点井
	 * @param joinGJ 交汇井
	 * @return
	 */
	public String getSIdAndEId(ArrayList<?> gjObj, ArrayList<?> gxObj, String StartGJ, String joinGJ)
	{
		//gjObj ArrayList转Hash
		AnalogBean analogBean = new AnalogBean();
		Hashtable<String, DevGJBean> objGJTable = null;
		objGJTable = new Hashtable<String, DevGJBean>(); 
		Iterator<?> iterGJ = gjObj.iterator();
		while(iterGJ.hasNext())
		{
			DevGJBean gjBean = (DevGJBean)iterGJ.next();
			String gjId = gjBean.getId();
			analogBean.HashPut(objGJTable, gjId, gjBean);
		}
		//gxObj ArrayList转Hash
		Hashtable<String, DevGXBean> objGXTable = null;
		objGXTable = new Hashtable<String, DevGXBean>(); 
		Iterator<?> iterGX = gxObj.iterator();
		while(iterGX.hasNext())
		{
			DevGXBean gxBean = (DevGXBean)iterGX.next();
			String gxId = gxBean.getId();
			analogBean.HashPut(objGXTable, gxId, gxBean);
		}
		
		DevGJBean nextGJ = (DevGJBean)analogBean.HashGet(objGJTable, StartGJ);
		DevGJBean preGJ = new DevGJBean();
		DevGXBean nextGX = new DevGXBean();
		boolean option = true;
		String IdList = "";
		do
		{
			preGJ = nextGJ;
			nextGX = (DevGXBean)analogBean.HashGet(objGXTable, nextGJ.getOut_Id());
			nextGJ = (DevGJBean)analogBean.HashGet(objGJTable, nextGX.getEnd_Id());
			if(nextGJ.getFlag().equals("2") || nextGJ.getFlag().equals("6")){
				IdList = "0," + nextGJ.getId();
				option = false;
			}else{
				System.out.println(joinGJ);
				if(nextGJ.getId().equals(joinGJ)){ // 相反的流向
					joinGJ = nextGJ.getId(); // 把当前的管井记录为上一个井
					String In_Id = preGJ.getIn_Id();
					if(In_Id.length() > 10) { // 进口管线大于1个,交汇井
						String [] In_IdList = In_Id.split(","); // 交汇井的进口
						for(int i = 0; i < In_IdList.length; i ++){
							DevGXBean inGX = (DevGXBean)analogBean.HashGet(objGXTable, In_IdList[i]); // 取到进口
							if(inGX.getFlow().equals("2")){ // 进口管线是双流向
								DevGJBean inGJ = (DevGJBean)analogBean.HashGet(objGJTable, inGX.getStart_Id()); // 取到双流向的起始井
								if(inGJ.getId() != nextGJ.getId() && !IdList.contains(inGJ.getId())){
									IdList += inGJ.getId() + "," + this.getQua(nextGJ, inGJ) + ";";
									option = false;
								}
							}
							if(!option){
								DevGXBean outGX = (DevGXBean)analogBean.HashGet(objGXTable, nextGJ.getOut_Id()); // 交汇井的出口
								DevGJBean outGJ = (DevGJBean)analogBean.HashGet(objGJTable, outGX.getEnd_Id());  // 交汇井的出口井
								if(!outGJ.getId().contains(nextGJ.getId())){
									IdList += outGJ.getId() + "," + this.getQua(nextGJ, outGJ) + ";";
								}
								IdList = nextGJ.getId() + "," + nextGJ.getLongitude() + "," + nextGJ.getLatitude() + ";" + IdList;
							}
						}
					}
				}else{
					joinGJ = nextGJ.getId(); // 把当前的管井记录为上一个井
					String In_Id = nextGJ.getIn_Id();
					if(In_Id.length() > 10) { // 进口管线大于1个,交汇井
						String [] In_IdList = In_Id.split(","); // 交汇井的进口
						for(int i = 0; i < In_IdList.length; i ++){
							DevGXBean inGX = (DevGXBean)analogBean.HashGet(objGXTable, In_IdList[i]); // 取到进口
							if(inGX.getFlow().equals("2")){ // 进口管线是双流向
								DevGJBean inGJ = (DevGJBean)analogBean.HashGet(objGJTable, inGX.getStart_Id()); // 取到双流向的起始井
								if(inGJ.getId() != nextGJ.getId() && !IdList.contains(inGJ.getId())){
									IdList += inGJ.getId() + "," + this.getQua(nextGJ, inGJ) + ";";
									option = false;
								}
							}
						}
						if(!option){
							DevGXBean outGX = (DevGXBean)analogBean.HashGet(objGXTable, nextGJ.getOut_Id()); // 交汇井的出口
							DevGJBean outGJ = (DevGJBean)analogBean.HashGet(objGJTable, outGX.getEnd_Id());  // 交汇井的出口井
							if(!outGJ.getId().contains(nextGJ.getId())){
								IdList += outGJ.getId() + "," + this.getQua(nextGJ, outGJ) + ";";
							}
							IdList = nextGJ.getId() + "," + nextGJ.getLongitude() + "," + nextGJ.getLatitude() + ";" + IdList;
						}
					}
				}
			}
		}
		while(option);
		return IdList;
	}
	
	public void getGJListAndGXList(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);

		msgBean = pRmi.RmiExec(4, this, 0, 25);			
		@SuppressWarnings("rawtypes")
		ArrayList gxObj = (ArrayList)(Object)msgBean.getMsg();
		
		DevGJBean tmpGJBean = new DevGJBean();
		tmpGJBean.setProject_Id(currStatus.getFunc_Project_Id());
		tmpGJBean.setSubsys_Id(Id.substring(2, 5));
		
		msgBean = pRmi.RmiExec(5, tmpGJBean, 0, 25);
		@SuppressWarnings("rawtypes")
		ArrayList gjObj = (ArrayList)(Object)msgBean.getMsg();
		String IdList = request.getParameter("IdList");
		currStatus.setJsp("User_Graph_Cut.jsp?Sid=" + Sid + "&Id=" + Id);
		
		//gjObj ArrayList转Hash
		AnalogBean analogBean = new AnalogBean();
		Hashtable<String, DevGJBean> objGJTable = null;
		objGJTable = new Hashtable<String, DevGJBean>(); 
		Iterator<?> iterGJ = gjObj.iterator();
		while(iterGJ.hasNext())
		{
			DevGJBean gjBean = (DevGJBean)iterGJ.next();
			String gjId = gjBean.getId();
			analogBean.HashPut(objGJTable, gjId, gjBean);
		}
		//gxObj ArrayList转Hash
		Hashtable<String, DevGXBean> objGXTable = null;
		objGXTable = new Hashtable<String, DevGXBean>(); 
		Iterator<?> iterGX = gxObj.iterator();
		while(iterGX.hasNext())
		{
			DevGXBean gxBean = (DevGXBean)iterGX.next();
			String gxId = gxBean.getId();
			analogBean.HashPut(objGXTable, gxId, gxBean);
		}
		ArrayList<DevGJBean> gjList = new ArrayList<DevGJBean>();
		ArrayList<DevGXBean> gxList = new ArrayList<DevGXBean>();
		DevGJBean gjBean = new DevGJBean();
		DevGXBean gxBean = new DevGXBean();
		String [] IdArray = IdList.split(";");
		gjBean = (DevGJBean) analogBean.HashGet(objGJTable, IdArray[0].split(",")[0]);
		gjBean.setCurr_Data(gjBean.getBase_Height());
		gjList.add(gjBean);
		// 取到第一段
		while(true) {
			gxBean = (DevGXBean) analogBean.HashGet(objGXTable,gjBean.getOut_Id());
			if(null != gxBean)
			{
				gxList.add(gxBean);
				gjBean = (DevGJBean) analogBean.HashGet(objGJTable, gxBean.getEnd_Id());
				gjBean.setCurr_Data(gjBean.getBase_Height());
				gjList.add(gjBean);
				if(gjBean.getId().equals(IdArray[0].split(",")[1])){
					break;
				}
			}
		}
		for(int i = 1; i < IdArray.length; i ++){
			String [] IdYD = IdArray[i].split(",");
			gjBean = (DevGJBean) analogBean.HashGet(objGJTable, IdYD[1]);
			gjBean.setCurr_Data(gjBean.getBase_Height());
			gjList.add(gjBean);
			String preGj = IdYD[0];
			gxBean = (DevGXBean) analogBean.HashGet(objGXTable,gjBean.getOut_Id());
			System.out.println(gxBean.getEnd_Id() + ":" + preGj);
			System.out.println(gxBean.getEnd_Id().equals(preGj));
			if(gxBean.getEnd_Id().equals(preGj)){
				gxBean = (DevGXBean) analogBean.HashGet(objGXTable,gjBean.getOut_Id());
				String change = gxBean.getEnd_Id();
				gxBean.setEnd_Id(gxBean.getStart_Id());
				gxBean.setStart_Id(change);
				change = gxBean.getEnd_Height();
				gxBean.setEnd_Height(gxBean.getStart_Height());
				gxBean.setStart_Height(change);
				gxList.add(gxBean);
			}else{
				gxBean = (DevGXBean) analogBean.HashGet(objGXTable,gjBean.getIn_Id().substring(0,8));
				if(gxBean.getId().equals("YG011076")){
					String change = gxBean.getEnd_Id();
					gxBean.setEnd_Id(gxBean.getStart_Id());
					gxBean.setStart_Id(change);
					change = gxBean.getEnd_Height();
					gxBean.setEnd_Height(gxBean.getStart_Height());
					gxBean.setStart_Height(change);
				}
				gxList.add(gxBean);
				
			}
			while(true) {
				gxBean = (DevGXBean) analogBean.HashGet(objGXTable,gjBean.getOut_Id());
				if(gxBean.getEnd_Id().equals(preGj)){
					preGj = gjBean.getId();
					gxBean = (DevGXBean) analogBean.HashGet(objGXTable,gjBean.getIn_Id().substring(0,8));
					String change = gxBean.getEnd_Id();
					gxBean.setEnd_Id(gxBean.getStart_Id());
					gxBean.setStart_Id(change);
					change = gxBean.getEnd_Height();
					gxBean.setEnd_Height(gxBean.getStart_Height());
					gxBean.setStart_Height(change);
					gxList.add(gxBean);
					gjBean = (DevGJBean) analogBean.HashGet(objGJTable, gxBean.getStart_Id());
				}else{
					preGj = gjBean.getId();
					gxList.add(gxBean);
					gjBean = (DevGJBean) analogBean.HashGet(objGJTable, gxBean.getEnd_Id());
				}
				gjBean.setCurr_Data(gjBean.getBase_Height());
				gjList.add(gjBean);
				if(gjBean.getId().equals(IdYD[2])){
					break;
				}
			}
		}
		request.getSession().setAttribute("User_Graph_Cut_GX_" + Sid, gxList);
		request.getSession().setAttribute("User_Graph_Cut_GJ_" + Sid, gjList);
		for(int i = 0; i < gjList.size(); i ++){
			System.out.println((gjList.get(i)).getId());
		}
		for(int i = 0; i < gxList.size(); i ++){
			System.out.println((gxList.get(i)).getId());
		}
		
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
	   	response.sendRedirect(currStatus.getJsp());
	}
	
	public String getQua(DevGJBean cenBean, DevGJBean orBean){
		double pi = Math.PI;
		double p_1 = Math.PI/8;
		double p_2 = 3*Math.PI/8;
		double p_3 = 5*Math.PI/8;
		double p_4 = 7*Math.PI/8;
		double p_5 = -7*Math.PI/8;
		double p_6 = -5*Math.PI/8;
		double p_7 = -3*Math.PI/8;
		double p_8 = -Math.PI/8;
		double pX = Double.valueOf(orBean.getLongitude()) - Double.valueOf(cenBean.getLongitude());
		double pY = Double.valueOf(orBean.getLatitude()) - Double.valueOf(cenBean.getLatitude());
		String qua = "";
		double an = 0;
		if(pX != 0){
			an = Math.atan(Math.abs(pY/pX));
		}
		if(pY >= 0 && pX > 0){
			if(an >= 0 && an < p_1){
				qua = "1";
			}else if(an >= p_1 && an < p_2){
				qua = "2";
			}else if(an >= p_2 && an < pi/2){
				qua = "3";
			}
		}else if(pY > 0 && pX <= 0){
			an = pi - an;
			if(an >= pi/2 && an < p_3){
				qua = "3";
			}else if(an >= p_3 && an < p_4){
				qua = "4";
			}else if(an >= p_4 && an < pi){
				qua = "5";
			}
			if(pX == 0){qua = "5";}
		}else if(pY <= 0 && pX < 0){
			an = an - pi;
			if(an >= -pi && an < p_5){
				qua = "5";
			}else if(an >= p_5 && an < p_6){
				qua = "6";
			}else if(an >= p_6 && an < -pi/2){
				qua = "7";
			}
		}else if(pY < 0 && pX >= 0){
			an = -an;
			if(an >= -pi/2 && an < p_7){
				qua = "7";
			}else if(an >= p_7 && an < p_8){
				qua = "8";
			}else if(an >= p_8 && an < 0){
				qua = "1";
			}
			if(pX == 0){
				qua = "1";
			}
		}
		return qua;
	}
	
	//管井管线统计
	public void InTotal(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		String Resp = "9999";
		
		msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
		if(msgBean.getStatus() == MsgBean.STA_SUCCESS)
		{
			Resp = ((String)msgBean.getMsg());
		}
		request.getSession().setAttribute("User_InTotal_" + Sid, Resp);
		currStatus.setJsp("User_InTotal.jsp?Sid=" + Sid + "&Id=" + Id);
		
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		response.sendRedirect(currStatus.getJsp());
	}
	
	//管线统计-详细
	public void InTotal_GX(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
		ArrayList<?> gx_List = (ArrayList<?>) msgBean.getMsg();
		Iterator<?> gx_iterator = gx_List.iterator();
		String Resp = Id;
		String GX_DP = "";
		String PD = "";
		DecimalFormat df = new DecimalFormat("#.####");
		while (gx_iterator.hasNext()) {
			DevGXBean devGXBean = (DevGXBean) gx_iterator.next();
			Id = devGXBean.getId();
			Diameter = devGXBean.getDiameter();
			Length = devGXBean.getLength();
			Start_Id = devGXBean.getStart_Id();
			End_Id = devGXBean.getEnd_Id();
			Start_Height = devGXBean.getStart_Height();
			End_Height = devGXBean.getEnd_Height();
			Material = devGXBean.getMaterial();
			Buried_Year = devGXBean.getBuried_Year();
			Flag = "";
			PD = df.format((Double.valueOf(Start_Height) - Double.valueOf(End_Height))/Double.valueOf(Length));
			if(Double.valueOf(Start_Height) < Double.valueOf(End_Height))
			{
				GX_DP += Id + "(" + PD + "),";
			}
			else
			{
				Resp += Id + "(" + PD + "),";
			}
		}
		Resp = Resp + ";" +GX_DP;
		request.getSession().setAttribute("User_InTotal_GX_" + Sid, Resp);
		currStatus.setJsp("User_InTotal_GX.jsp?Sid=" + Sid + "&Id=" + Id);
		
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		response.sendRedirect(currStatus.getJsp());
	}
	
	/**
	 * 时段水位深度剖面图
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
		currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);

		int timePeriod = CommUtil.StrToInt(request.getParameter("TimePeriod"));
		
		switch(currStatus.getCmd())
		{
			case 4://剖面图
				msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
				request.getSession().setAttribute("Analog_Graph_Cut_GX_" + Sid, (Object)msgBean.getMsg());				
				DevGJBean tmpGJBean = new DevGJBean();
				tmpGJBean.setProject_Id(currStatus.getFunc_Project_Id());
				tmpGJBean.setSubsys_Id(Id.substring(2, 5));
				msgBean = pRmi.RmiExec(5, tmpGJBean, 0, 25);
				request.getSession().setAttribute("Analog_Graph_Cut_GJ_" + Sid, (Object)msgBean.getMsg());
				
				AnalogBean analogBean = new AnalogBean();
				String WaterLev = "";
				if(Id.substring(0,2).equals("YJ"))
				{
					//System.out.println("Func_Project_Id["+currStatus.getFunc_Project_Id()+"]Id["+Id+"]pSimu["+pSimu+"]");
					WaterLev = analogBean.AnalogWaterLev(currStatus.getFunc_Project_Id() + "_" + Id.substring(0,5), timePeriod, Double.parseDouble(pSimu));
				}else
				{
					WaterLev = analogBean.AnalogSewageLev(currStatus.getFunc_Project_Id() + "_" + Id.substring(0,5), timePeriod, Double.parseDouble(pSimu));
				}
				//System.out.println("WaterLev"+WaterLev);
				request.getSession().setAttribute("Analog_WaterLev_" + Sid, WaterLev);				
				currStatus.setJsp("Analog_Graph_Cut.jsp?Sid=" + Sid 
						+ "&TimePeriod=" + timePeriod 
						+ "&Id=" + Id);
				break;
		}
		
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
	   	response.sendRedirect(currStatus.getJsp());
	}
	
	public void AnalogFlow(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		String gjId = CommUtil.StrToGB2312(request.getParameter("gjId"));
		String gxId = CommUtil.StrToGB2312(request.getParameter("gxId"));
		
		Id = gxId;
		//System.out.println("pSimu["+pSimu+"]");
		AnalogBean analogBean = new AnalogBean();
		switch(currStatus.getCmd())
		{
			case 0://查询单个gj信息
				msgBean = pRmi.RmiExec(3, this, 0, 0);
				request.getSession().setAttribute("Analog_DevGX_Info_" + Sid, (DevGXBean)((ArrayList<?>)msgBean.getMsg()).get(0));				
				currStatus.setJsp("Analog_DevGX_Info.jsp?Sid=" + Sid + "&gjId="+gjId+"&gxId="+gxId+"&Project_Id="+currStatus.getFunc_Project_Id() + "&pSimu=" + pSimu);
				break;
			case 1://流量负荷
				String WaterFlowLoad = "";
				if(gjId.substring(0,2).equals("YJ"))
				{
					WaterFlowLoad = analogBean.AnalogFlowLoad(currStatus.getFunc_Project_Id()+"_"+gjId, gxId, Double.parseDouble(pSimu));
				}else
				{
					WaterFlowLoad = analogBean.SewageFlowLoad(currStatus.getFunc_Project_Id()+"_"+gjId, gxId, Double.parseDouble(pSimu));//污水
				}
				request.getSession().setAttribute("Analog_Graph_FlowLoad_" + Sid, WaterFlowLoad);				
				currStatus.setJsp("Analog_Graph_FlowLoad.jsp?Sid=" + Sid);
				break;
			case 2://实际流量
				String WaterActualFlow = "";
				if(gjId.substring(0,2).equals("YJ"))
				{
					WaterActualFlow = analogBean.AnalogActualFlow(currStatus.getFunc_Project_Id()+"_"+gjId, gxId, Double.parseDouble(pSimu));
				}else
				{
					WaterActualFlow = analogBean.SewageActualFlow(currStatus.getFunc_Project_Id()+"_"+gjId, gxId, Double.parseDouble(pSimu));//污水
				}
				request.getSession().setAttribute("Analog_Graph_ActualFlow_" + Sid, WaterActualFlow);				
				currStatus.setJsp("Analog_Graph_ActualFlow.jsp?Sid=" + Sid);
				break;
			case 3://流量负荷
				String WaterFlowRate = "";
				if(gjId.substring(0,2).equals("YJ"))
				{
					WaterFlowRate = analogBean.AnalogFlowRate(currStatus.getFunc_Project_Id()+"_"+gjId, gxId, Double.parseDouble(pSimu));
				}else
				{
					WaterFlowRate = analogBean.SewageFlowRate(currStatus.getFunc_Project_Id()+"_"+gjId, gxId, Double.parseDouble(pSimu));//污水
				}
				request.getSession().setAttribute("Analog_Graph_FlowRate_" + Sid, WaterFlowRate);				
				currStatus.setJsp("Analog_Graph_FlowRate.jsp?Sid=" + Sid);
				break;
		}
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
	   	response.sendRedirect(currStatus.getJsp());
	}
	
	public void GXSuggest(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		PrintWriter output = null;
	    try
	    {
	    	getHtmlData(request);
	    	currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
			currStatus.getHtmlData(request, pFromZone);
	    	
	    	List<Object> CData = new ArrayList<Object>();
    		//根据Level查找管辖的站点
			msgBean = pRmi.RmiExec(0, this, 0, 25);
			if(msgBean.getStatus() == MsgBean.STA_SUCCESS)
			{
				if(null != msgBean.getMsg())
				{
					ArrayList<?> msgList = (ArrayList<?>)msgBean.getMsg();
					if(msgList.size() > 0)
					{						
						Iterator<?> iterator = msgList.iterator();
						while(iterator.hasNext())
						{
							DevGXBean devGXBean = (DevGXBean)iterator.next();
							JsonGXBean RealJson = new JsonGXBean();
							RealJson.setId(devGXBean.getId());
							RealJson.setText(devGXBean.getId());
							CData.add(RealJson);
						}
					}
				}
			}	
			
			JSONArray jsonObj = JSONArray.fromObject(CData);
	    	output = response.getWriter();
	    	output.write(jsonObj.toString());
	    	output.flush();	    	
	    	//System.out.println(jsonObj.toString());
	    }
	    catch (IOException e)
	    {
	    	e.printStackTrace();
	    }
	    finally
	    {
	    	if(null != output){
	    		output.close();
	    	}
	    }
	}	
	
	//获取状态RealStatus、doDefence、doRightClick
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
	/**
	 * 管线拉直技术
	 * @param request
	 * @param response
	 * @param pRmi
	 * @param pFromZone
	 * @throws ServletException
	 * @throws IOException
	 */
	public void straightenGX(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		PrintWriter outprint = response.getWriter();
		String Resp = "0000";
		String Bearing = String.valueOf(request.getParameter("Bearing"));
		String StartGJ = String.valueOf(request.getParameter("StartGJ"));
		String EndGJ = String.valueOf(request.getParameter("EndGJ"));
		
		DecimalFormat df = new DecimalFormat("####.######");

		Id = StartGJ;
		msgBean = pRmi.RmiExec(4, this, 0, 0);
		ArrayList<?> gxObj = (ArrayList<?>) msgBean.getMsg();

		DevGJBean gjBean = new DevGJBean();
		gjBean.setProject_Id(currStatus.getFunc_Project_Id());
		gjBean.setId(StartGJ.substring(0, 5));
		msgBean = pRmi.RmiExec(7, gjBean, 0, 0);
		ArrayList<?> gjObj = (ArrayList<?>) msgBean.getMsg();

		ArrayList<Object> gjList = this.pickUpGJGX(gjObj, gxObj, StartGJ, EndGJ);
		if(null != gjList && gjList.size() > 0)
		{
			double StartGJLng = 0.0;	//起点经度 X
			double StartGJLat = 0.0;	//起点维度 Y
			double EndGJLng = 0.0;		//终点经度 X
			double EndGJLat = 0.0;		//终点维度 Y
			double k = 0.0;
			double b = 0.0;
			
			double NewGJLng = 0.0;		//直线中新点经度 X
			double NewGJLat = 0.0;		//直线中新点维度 Y
			String reqReal = "";		//记录此次修改的数据，用于回退
			
			String GJLng = "";			//直线中原来点经度 X
			String GJLat = "";			//直线中原来点维度 Y
			String GJId = "";			
			StartGJLat = Double.parseDouble(((DevGJBean) gjList.get(0)).getLatitude());
			StartGJLng = Double.parseDouble(((DevGJBean) gjList.get(0)).getLongitude());
			EndGJLat = Double.parseDouble(((DevGJBean) gjList.get(gjList.size() - 1)).getLatitude());
			EndGJLng = Double.parseDouble(((DevGJBean) gjList.get(gjList.size() - 1)).getLongitude());
			if(Bearing.equals("1"))			//南北向	维度（Y轴）不变，只变化经度（X轴）
			{
				k = (EndGJLng - StartGJLng) / (EndGJLat - StartGJLat);
				b = StartGJLng - (StartGJLat * (EndGJLng - StartGJLng)) / (EndGJLat - StartGJLat);
				for (int i = 0; i < gjList.size(); i++)
				{
					DevGJBean Bean = (DevGJBean) gjList.get(i);
					GJId = Bean.getId();
					GJLng = Bean.getLongitude();
					GJLat = Bean.getLatitude();
					NewGJLng = k * Double.parseDouble(GJLat) + b;
					reqReal += GJId + "," + GJLng + "," + GJLat + ";";
					Bean.setLongitude(df.format(NewGJLng));
					msgBean = pRmi.RmiExec(15, Bean, 0, 0);
					if (msgBean.getStatus() != MsgBean.STA_SUCCESS)
					{
						Resp = "9999";
						break;
					}
				}
				Resp += reqReal;
			}
			else if(Bearing.equals("2"))	//东西向	经度（X轴）不变，只变化维度（Y轴）
			{
				k = (EndGJLat - StartGJLat) / (EndGJLng - StartGJLng);
				b = StartGJLat - (StartGJLng * (EndGJLat - StartGJLat)) / (EndGJLng - StartGJLng);
				for (int i = 0; i < gjList.size(); i++)
				{
					DevGJBean Bean = (DevGJBean) gjList.get(i);
					GJId = Bean.getId();
					GJLng = Bean.getLongitude();
					GJLat = Bean.getLatitude();
					NewGJLat = k * Double.parseDouble(GJLng) + b;
					reqReal += GJId + "," + GJLng + "," + GJLat + ";";
					Bean.setLatitude(df.format(NewGJLat));
					msgBean = pRmi.RmiExec(15, Bean, 0, 0);
					if (msgBean.getStatus() != MsgBean.STA_SUCCESS)
					{
						Resp = "9999";
						break;
					}
				}
				Resp += reqReal;
			}
			else							//斜向
			{
				k = (EndGJLat - StartGJLat) / (EndGJLng - StartGJLng);
				for (int i = 0; i < gjList.size(); i++)
				{
					DevGJBean Bean = (DevGJBean) gjList.get(i);
					GJId = Bean.getId();
					GJLng = Bean.getLongitude();
					GJLat = Bean.getLatitude();
					reqReal += GJId + "," + GJLng + "," + GJLat + ";";
					NewGJLng = ((Double.parseDouble(GJLng) + StartGJLng) / 2)
							+ ((Double.parseDouble(GJLat) - StartGJLat) / (2*k));
					NewGJLat = ((k*(Double.parseDouble(GJLng) - StartGJLng)) / 2)
							+ ((Double.parseDouble(GJLat) + StartGJLat) / 2);
					Bean.setLongitude(df.format(NewGJLng));
					Bean.setLatitude(df.format(NewGJLat));
					msgBean = pRmi.RmiExec(15, Bean, 0, 0);
					if (msgBean.getStatus() != MsgBean.STA_SUCCESS)
					{
						Resp = "9999";
						break;
					}
				}
				Resp += reqReal;
			}
		}
		else
		{
			Resp = "1111";
		}
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		outprint.write(Resp);
	}
	
	/**
	 * 管线拉直 回退
	 * @param request
	 * @param response
	 * @param pRmi
	 * @param pFromZone
	 * @throws ServletException
	 * @throws IOException
	 */
	public void unStraightenGX(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		PrintWriter outprint = response.getWriter();
		String Resp = "0000";
		String reqReal = String.valueOf(request.getParameter("reqReal"));
		
		DevGJBean gjBean = new DevGJBean();
		String[] gjArray = reqReal.split(";");
		String[] gjObj = new String[3];
		for(int i = 0; i < gjArray.length; i ++)
		{
			gjObj = gjArray[i].split(",");
			gjBean.setId(gjObj[0]);
			gjBean.setProject_Id(currStatus.getFunc_Project_Id());
			gjBean.setLongitude(gjObj[1]);
			gjBean.setLatitude(gjObj[2]);
			msgBean = pRmi.RmiExec(15, gjBean, 0, 0);
			if (msgBean.getStatus() != MsgBean.STA_SUCCESS)
			{
				Resp = "9999";
				break;
			}
		}
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		outprint.write(Resp);
	}
	/**
	 * 整理管井数据
	 * @param gjObj
	 * @param gxObj
	 * @param StartGJ
	 * @param EndGJ
	 * @return
	 */
	public ArrayList<Object> pickUpGJGX(ArrayList<?> gjObj, ArrayList<?> gxObj, String StartGJ, String EndGJ)
	{
		//gjObj ArrayList转Hash
		AnalogBean analogBean = new AnalogBean();
		Hashtable<String, DevGJBean> objGJTable = null;
		objGJTable = new Hashtable<String, DevGJBean>(); 
		Iterator<?> iterGJ = gjObj.iterator();
		while(iterGJ.hasNext())
		{
			DevGJBean gjBean = (DevGJBean)iterGJ.next();
			String gjId = gjBean.getId();
			analogBean.HashPut(objGJTable, gjId, gjBean);
		}
		//gxObj ArrayList转Hash
		Hashtable<String, DevGXBean> objGXTable = null;
		objGXTable = new Hashtable<String, DevGXBean>(); 
		Iterator<?> iterGX = gxObj.iterator();
		while(iterGX.hasNext())
		{
			DevGXBean gxBean = (DevGXBean)iterGX.next();
			String gxId = gxBean.getId();
			analogBean.HashPut(objGXTable, gxId, gxBean);
		}
		DevGJBean nextGJ = (DevGJBean)analogBean.HashGet(objGJTable, StartGJ);			
		ArrayList<Object> gjList = new ArrayList<Object>();		//管井ArrayList
		gjList.add(nextGJ);
		DevGXBean nextGX = new DevGXBean();
		int option = 0;
		do
		{
			String outGXId = nextGJ.getOut_Id();
			nextGX = (DevGXBean)analogBean.HashGet(objGXTable, outGXId);
			String outGJId = nextGX.getEnd_Id();
			nextGJ = (DevGJBean)analogBean.HashGet(objGJTable, outGJId);
			gjList.add(nextGJ);
			if(nextGJ.getId().equals(EndGJ) || nextGJ.getFlag().equals("2"))
			{
				option = 1;
				if(!nextGJ.getId().equals(EndGJ))
				{
					gjList.clear();
				}
			}
		}
		while(option == 0);
		return gjList;
	}
	
	/**
	 * 获得当前系统的分析数据
	 * @param request
	 * @param response
	 * @param pRmi
	 * @param pFromZone
	 * @throws ServletException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void getSysIdNow(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		PrintWriter outprint = response.getWriter();
		String Resp = "0000";
		
		DevGJBean tmpGJBean = new DevGJBean();
		tmpGJBean.setProject_Id(currStatus.getFunc_Project_Id());
		tmpGJBean.setId(Id.substring(0, 5));

		msgBean = pRmi.RmiExec(7, tmpGJBean, 0, 25);
		@SuppressWarnings("rawtypes")
		ArrayList gjObj = (ArrayList)(Object)msgBean.getMsg();
		
		Id = dealGXID(Id).substring(0, 5);
		msgBean = pRmi.RmiExec(6, this, 0, 25);				
		@SuppressWarnings("rawtypes")
		ArrayList gxObj = (ArrayList)(Object)msgBean.getMsg();

		AnalysisBean bean = new AnalysisBean();
		bean.subSystem(gjObj, gxObj);
		ArrayList<String> gjHeightExp = bean.getGjHeightExp();
		ArrayList<String> gxSlopeExp = bean.getGxSlopeExp();
		ArrayList<String> gxDiameterExp = bean.getGxDiameterExp();
		ArrayList<String> gjTopSort = bean.getGjTopSort();
		ArrayList<String> gjBaseSort = bean.getGjBaseSort();
		
		Resp += JSONObject.toJSONString(gjHeightExp) + ";";
		Resp += JSONObject.toJSONString(gxSlopeExp) + ";";
		Resp += JSONObject.toJSONString(gxDiameterExp) + ";";
		Resp += JSONObject.toJSONString(gjTopSort) + ";";
		Resp += JSONObject.toJSONString(gjBaseSort) + ";";
		
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		outprint.write(Resp);
	}
	/**
	 * 当前管井分析
	 * @param gjObj
	 * @param gxObj
	 * @param Id
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public String sysIdNowGJ(ArrayList gjObj, ArrayList gxObj, String Id)
	{
		AnalogBean analogBean = new AnalogBean();
		// gjObj ArrayList转Hash
		Hashtable<String, DevGJBean> objGJTable = null;
		objGJTable = new Hashtable<String, DevGJBean>();
		Iterator iterGJ = gjObj.iterator();
		while (iterGJ.hasNext())
		{
			DevGJBean gjBean = (DevGJBean) iterGJ.next();
			String gjId = gjBean.getId();
			analogBean.HashPut(objGJTable, gjId, gjBean);
		}
		// gxObj ArrayList转Hash
		Hashtable<String, DevGXBean> objGXTable = null;
		objGXTable = new Hashtable<String, DevGXBean>();
		Iterator iterGX = gxObj.iterator();
		while (iterGX.hasNext())
		{
			DevGXBean gxBean = (DevGXBean) iterGX.next();
			String gxId = gxBean.getId();
			analogBean.HashPut(objGXTable, gxId, gxBean);
		}
		
		// 遍历，梳理先后关系,转成ArrayList
		DevGJBean nextGJ = (DevGJBean) analogBean.HashGet(objGJTable, Id);
		ArrayList<Object> gjList = new ArrayList<Object>(); // 管井ArrayList
		gjList.add(nextGJ); // 加入第一个管井
		DevGXBean nextGX = new DevGXBean();
		
		int option = 0;
		do
		{
			if (nextGJ.getFlag().equals("2") || nextGJ.getFlag().equals("6"))
			{
				option = 1;
			}
			String outGXId = "";
			outGXId = nextGJ.getOut_Id();
			nextGX = (DevGXBean) analogBean.HashGet(objGXTable, outGXId);
			if(null != nextGX)
			{
				String outGJId = nextGX.getEnd_Id();
				String startGJId = nextGX.getStart_Id();
				if(outGJId.substring(2,5).equals(startGJId.substring(2,5)))
				{
					nextGJ = (DevGJBean) analogBean.HashGet(objGJTable, outGJId);
					gjList.add(nextGJ);
				}
				else
				{
					option = 1;
				}
			}
		}while (option == 0);
		
		// 倒序分析
		double minHeight = 100;
		double maxHeight = -100;
		String minGJ = "";
		String maxGJ = "";
		int minGJI = 0;
		int maxGJI = gjList.size() - 1;
		String pondingGJ = "";
		String dpGX = "";
		DevGJBean outGJ = (DevGJBean) gjList.get(gjList.size() - 1);
		double outGJBase = Double.valueOf(outGJ.getBase_Height());
		for(int i = gjList.size() - 1; i >= 0; i --){
			DevGJBean bean = (DevGJBean) gjList.get(i);
			double GJBase = Double.valueOf(bean.getBase_Height());
			if(outGJBase > GJBase) {
				pondingGJ += bean.getId() + "," + bean.getLongitude() + "," + bean.getLatitude() + "#";
			}
			if(Double.valueOf(bean.getBase_Height()) < minHeight) {
				minHeight = Double.valueOf(bean.getBase_Height());
				minGJ = bean.getId() + "[" + minHeight + "]," + bean.getLongitude() + "," + bean.getLatitude();
				minGJI = i;
			}
			if(Double.valueOf(bean.getBase_Height()) > maxHeight) {
				maxHeight = Double.valueOf(bean.getBase_Height());
				maxGJ = bean.getId() + "[" + maxHeight + "]," + bean.getLongitude() + "," + bean.getLatitude();
				maxGJI = i;
			}
			String outGXId = bean.getOut_Id();
			DevGXBean gxBean = (DevGXBean) analogBean.HashGet(objGXTable, outGXId);
			if(gxBean == null){continue;}
			double dp = Double.valueOf(gxBean.getStart_Height()) - Double.valueOf(gxBean.getEnd_Height());
			if(dp < 0) {
				DevGJBean startGJ = (DevGJBean) analogBean.HashGet(objGJTable, gxBean.getStart_Id());
				DevGJBean endGJ = (DevGJBean) analogBean.HashGet(objGJTable, gxBean.getEnd_Id());
				dpGX += gxBean.getId() + "," + startGJ.getLongitude() + "," + startGJ.getLatitude()
						 + "," + endGJ.getLongitude() + "," + endGJ.getLatitude() + "#";
			}
			
		}
		String analyze = "";
		if (minGJI == gjList.size() - 1 && maxGJI == 0) {
			analyze = "经系统分析，"+Id+"至排出口，排水正常";
		} else if (maxGJI > 0 && maxGJI < gjList.size() - 1 && minGJI == gjList.size() - 1) {
			analyze = "经系统分析，"+Id+"至排出口，存在积水";
		} else if (maxGJI > 0 && maxGJI < gjList.size() - 1 && minGJI < gjList.size() - 1 && minGJI > 0) {
			analyze = "经系统分析，"+Id+"至排出口，存在多处积水";
		} else if (maxGJI == 0 && minGJI < gjList.size() - 1 && minGJI > 0) {
			analyze = "经系统分析，"+Id+"至排出口，存在积水";
		}
		String Resp = Id+"|" + minGJ + "|" + maxGJ + "|"+analyze+"|"+pondingGJ+"|"+dpGX;
		return Resp;
	}
	/**
	 * 当前管线分析
	 * @param gjObj
	 * @param gxObj
	 * @param Id
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public String sysIdNowGX(ArrayList gxObj)
	{
		Iterator<?> gx_iterator = gxObj.iterator();
		String Resp = "";
		String PD = "";
		DecimalFormat df = new DecimalFormat("#.####");
		while (gx_iterator.hasNext()) {
			DevGXBean devGXBean = (DevGXBean) gx_iterator.next();
			Id = devGXBean.getId();
			Diameter = devGXBean.getDiameter();
			Length = devGXBean.getLength();
			Start_Id = devGXBean.getStart_Id();
			End_Id = devGXBean.getEnd_Id();
			Start_Height = devGXBean.getStart_Height();
			End_Height = devGXBean.getEnd_Height();
			Material = devGXBean.getMaterial();
			Buried_Year = devGXBean.getBuried_Year();
			Flag = "";
			PD = df.format((Double.valueOf(Start_Height) - Double.valueOf(End_Height))/Double.valueOf(Length));
			if(Double.valueOf(Start_Height) < Double.valueOf(End_Height))
			{
				Resp += Id + ",";
			}
		}
		return Resp;
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
	 * 自动获取模拟的数据表格
	 * @param request
	 * @param response
	 * @param pRmi
	 * @param pFromZone
	 * @param pConfig
	 */
	public void getAnalogExcel(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone, ServletConfig pConfig) 
	{

		try {
			getHtmlData(request);
			currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
			currStatus.getHtmlData(request, pFromZone);
			
			SimpleDateFormat SimFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			
			String SheetName = "模拟数据表格";
			String UPLOAD_NAME = SimFormat.format(new Date());
			
			String Sys_Id = Id.substring(0, 5);//子系统
			String NN = "";//节点数
			String NP = "";//管段数
			String NStart = "";//起点数
			String Npline = "";//最大管段数
			String Nr_node = "";//最大节点数
			String Nend = "";//出口号
			String NT = "";//模拟时段
			String Ncol = "";//节点最大流入数+3
			String Hw_end = "";//出口标高
			// 管井数据
			DevGJBean dbean = new DevGJBean();
			dbean.setId(Id.substring(0, 5));
			dbean.setProject_Id(currStatus.getFunc_Project_Id());
			msgBean = pRmi.RmiExec(7, dbean, 0, 0);
			ArrayList<?> gj_List = (ArrayList<?>) msgBean.getMsg();
			
			Id = dealGXID(Id).substring(0, 5);
			msgBean = pRmi.RmiExec(6, this, 0, 0);
			ArrayList<?> gx_List = (ArrayList<?>) msgBean.getMsg();
			
			int row_Index = 0;
			Label cell = null;
			if (null != gj_List && null != gx_List) {
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
				
				NN = String.valueOf(gj_List.size());//节点数
				
				row_Index = 6;
				
				AnalogBean analogBean = new AnalogBean();
				
				Hashtable<String, DevGXBean> objGXTable = new Hashtable<String, DevGXBean>();
				Iterator<?> iterGX = gx_List.iterator();
				String gxNo = "";
				String gxIo = "";
				String gxJo = "";
				String gxLp = "";
				String gxDp = "";
				String gxRunoff = "";
				String gxStart_Height = "";
				String gxEnd_Height = "";
				
				String End_ = "";
				ArrayList<String> End_I = new ArrayList<String>();
				int gxNo_ = 1;
				while (iterGX.hasNext())
				{
					gxNo = String.valueOf(gxNo_);
					DevGXBean gxBean = (DevGXBean) iterGX.next();
					String gxId = gxBean.getId();
					analogBean.HashPut(objGXTable, gxId, gxBean);
					
					gxIo = String.valueOf(Integer.valueOf(gxBean.getStart_Id().substring(5)) - 1);
					gxJo = String.valueOf(Integer.valueOf(gxBean.getEnd_Id().substring(5)) - 1);
					gxLp = gxBean.getLength();
					gxDp = String.valueOf(Float.valueOf(gxBean.getDiameter()) / 1000);
					gxRunoff = "0.014";
					gxStart_Height = gxBean.getStart_Height();
					gxEnd_Height = gxBean.getEnd_Height();
					
					if(End_.contains(gxBean.getEnd_Id())){
						End_I.add(gxBean.getEnd_Id());
					}else{
						End_ += gxBean.getEnd_Id();
					}
					
					sheet.setRowView(row_Index, 400);
					sheet.setColumnView(row_Index, 25); // row_Index 列宽度
					cell = new Label(0, row_Index, gxNo, font2);
					sheet.addCell(cell);
					cell = new Label(1, row_Index, gxIo, font2);
					sheet.addCell(cell);
					cell = new Label(2, row_Index, gxJo, font2);
					sheet.addCell(cell);
					cell = new Label(3, row_Index, gxLp, font2);
					sheet.addCell(cell);
					cell = new Label(4, row_Index, gxDp, font2);
					sheet.addCell(cell);
					cell = new Label(5, row_Index, gxRunoff, font2);
					sheet.addCell(cell);
					cell = new Label(6, row_Index, gxStart_Height, font2);
					sheet.addCell(cell);
					cell = new Label(7, row_Index, gxEnd_Height, font2);
					sheet.addCell(cell);
					
					gxNo_ ++;
					row_Index ++;
				}
				// 计算节点最大流入管段数+3
				int Ncol_ = 1;
				ArrayList<String> End_I_ = End_I;
				do{
					End_I.clear();
					String End_II = "";
					for(int i = 0; i < End_I_.size(); i ++){
						if(End_II.contains(End_I_.get(i))){
							End_I.add(End_I_.get(i));
						}else{
							End_II += End_I_.get(i);
						}
					}
					Ncol_ ++;
				}while(End_I.size() > 0);
			    Ncol = String.valueOf(Ncol_+3);
			    
			    sheet.setRowView(4, 450);
				sheet.setColumnView(4, 25); // row_Index 列宽度
				cell = new Label(0, row_Index + 1, "管井数据表格", font1);
				sheet.addCell(cell);
				sheet.setRowView(4, 400);
				sheet.setColumnView(4, 25); // row_Index 列宽度
				cell = new Label(0, row_Index + 2, "No", font2);
				sheet.addCell(cell);
				cell = new Label(1, row_Index + 2, "汇水面积ha", font2);
				sheet.addCell(cell);
				cell = new Label(2, row_Index + 2, "径流系数", font2);
				sheet.addCell(cell);
				cell = new Label(3, row_Index + 2, "地面标高", font2);
				sheet.addCell(cell);
				
				String gjNo = "";
				String gjArea = "0.28";
				String gjRunoff = "0.62";
				String gjTop_Height = "";
				row_Index += 3;
				Hashtable<String, DevGJBean> objGJTable = new Hashtable<String, DevGJBean>();
				ArrayList<DevGJBean> StartGJList = new ArrayList<DevGJBean>(); // 起点数
				Iterator<?> iterGJ = gj_List.iterator();
				int gjNo_ = 0;
				while (iterGJ.hasNext())
				{
					DevGJBean gjBean = (DevGJBean) iterGJ.next();
					String gjId = gjBean.getId();
					int gjId_ = Integer.valueOf(gjId.substring(5)) - 1;
					gjNo = String.valueOf(gjNo_);
					while(gjNo_ < gjId_){
						System.out.println("gjNo_["+gjNo_+"] < gjId_["+gjId_+"]");
						sheet.setRowView(row_Index, 400);
						sheet.setColumnView(row_Index, 25); // row_Index 列宽度
						cell = new Label(0, row_Index, gjNo, font2);
						sheet.addCell(cell);
						cell = new Label(1, row_Index, "0", font2);
						sheet.addCell(cell);
						cell = new Label(2, row_Index, "0", font2);
						sheet.addCell(cell);
						cell = new Label(3, row_Index, "0", font2);
						sheet.addCell(cell);
						gjNo_ ++;
						row_Index ++;
					}
					gjNo = String.valueOf(gjNo_);
					String flag = gjBean.getFlag();
					if(flag.equals("0")){
						StartGJList.add(gjBean);
					}
					if(flag.equals("2") || flag.equals("6")){
						Nend = String.valueOf(Integer.valueOf(gjId.substring(5)) - 1); // 计算出口管井
						Hw_end = gjBean.getBase_Height(); // 出口标高
					}
					analogBean.HashPut(objGJTable, gjId, gjBean);
					
					gjTop_Height = gjBean.getTop_Height();
				
					sheet.setRowView(row_Index, 400);
					sheet.setColumnView(row_Index, 25); // row_Index 列宽度
					cell = new Label(0, row_Index, gjNo, font2);
					sheet.addCell(cell);
					cell = new Label(1, row_Index, gjArea, font2);
					sheet.addCell(cell);
					cell = new Label(2, row_Index, gjRunoff, font2);
					sheet.addCell(cell);
					cell = new Label(3, row_Index, gjTop_Height, font2);
					sheet.addCell(cell);
					
					gjNo_ ++;
					row_Index ++;
				}
				NStart = String.valueOf(StartGJList.size()); // 起点数
				NP = String.valueOf(gx_List.size());//管段数
				
				// 计算最大路径管段数和节点数
				int Npline_ = 0;
				int Nr_node_ = 0;
				for(int i = 0; i < StartGJList.size(); i ++){
					int _Nr_node_ = 0;
					DevGJBean nextGJ = StartGJList.get(i);
					boolean ok = true;
					do{
						_Nr_node_ ++;
						if(nextGJ.getFlag().equals("2") || nextGJ.getFlag().equals("6")){
							if(_Nr_node_ > Nr_node_){
								Nr_node_ = _Nr_node_;
								Npline_ = Nr_node_ - 1;
							}
							ok = false;
						}else {
							DevGXBean nextGX = (DevGXBean) analogBean.HashGet(objGXTable, nextGJ.getOut_Id());
							nextGJ = (DevGJBean) analogBean.HashGet(objGJTable, nextGX.getEnd_Id());
						}
					}while(ok);
				}
				Npline = String.valueOf(Npline_);
				Nr_node = String.valueOf(Nr_node_);
				
				sheet.setRowView(4, 450);
				sheet.setColumnView(4, 25); // row_Index 列宽度
				cell = new Label(0, 0, "基础数据表格", font1);
				sheet.addCell(cell);
				sheet.setRowView(row_Index, 450);
				sheet.setColumnView(row_Index, 25);
				cell=new Label(0,1,"子系统号",font2);   
			    sheet.addCell(cell);   
			    cell=new Label(1,1,"节点数(NN)",font2);   
			    sheet.addCell(cell);   
			    cell=new Label(2,1,"管段数(NP)",font2);  
			    sheet.addCell(cell);   
			    cell=new Label(3,1,"起点数(NStart)",font2);  
			    sheet.addCell(cell);   
			    cell=new Label(4,1,"最大路径管段数",font2);  
			    sheet.addCell(cell);   
			    cell=new Label(5,1,"最大路径节点数",font2);  
			    sheet.addCell(cell);
			    cell=new Label(6,1,"终点出口号",font2);  
			    sheet.addCell(cell);
			    cell=new Label(7,1,"模拟时段",font2);  
			    sheet.addCell(cell);
			    cell=new Label(8,1,"节点最大流入管段数+3",font2);  
			    sheet.addCell(cell); 
			    cell=new Label(9,1,"出口标高",font2);  
			    sheet.addCell(cell);
			    
			    NT = "60";
			    sheet.setRowView(2, 400);
				sheet.setColumnView(2, 25); // row_Index 列宽度
				cell = new Label(0, 2, Sys_Id, font2);
				sheet.addCell(cell);
				cell = new Label(1, 2, NN, font2);
				sheet.addCell(cell);
				cell = new Label(2, 2, NP, font2);
				sheet.addCell(cell);
				cell = new Label(3, 2, NStart, font2);
				sheet.addCell(cell);
				cell = new Label(4, 2, Npline, font2);
				sheet.addCell(cell);
				cell = new Label(5, 2, Nr_node, font2);
				sheet.addCell(cell);
				cell = new Label(6, 2, Nend, font2);
				sheet.addCell(cell);
				cell = new Label(7, 2, NT, font2);
				sheet.addCell(cell);
				cell = new Label(8, 2, Ncol, font2);
				sheet.addCell(cell);
				cell = new Label(9, 2, Hw_end, font2);
				sheet.addCell(cell);
			    
				sheet.setRowView(4, 450);
				sheet.setColumnView(4, 25); // row_Index 列宽度
				cell = new Label(0, 4, "管段数据表格", font1);
				sheet.addCell(cell);
				sheet.setRowView(4, 400);
				sheet.setColumnView(4, 25); // row_Index 列宽度
				cell = new Label(0, 5, "No", font2);
				sheet.addCell(cell);
				cell = new Label(1, 5, "起点号I0", font2);
				sheet.addCell(cell);
				cell = new Label(2, 5, "终点号J0", font2);
				sheet.addCell(cell);
				cell = new Label(3, 5, "长度LP", font2);
				sheet.addCell(cell);
				cell = new Label(4, 5, "直径DP", font2);
				sheet.addCell(cell);
				cell = new Label(5, 5, "摩阻系数", font2);
				sheet.addCell(cell);
				cell = new Label(6, 5, "起端标高", font2);
				sheet.addCell(cell);
				cell = new Label(7, 5, "终端标高", font2);
				sheet.addCell(cell);

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
	
	/** 导入Excel文档  解析文档中的管线详细数据  
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
			mySmartUpload.initialize(pConfig, request, response);
			mySmartUpload.setAllowedFilesList("xls,xlsx,XLS,XLSX,");
			mySmartUpload.upload();
								
			Sid = mySmartUpload.getRequest().getParameter("Sid");
			currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
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
						//上传现有文档			
						com.jspsmart.upload.File myFile = mySmartUpload.getFiles().getFile(n);		
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
				    		String diameter = rs.getCell(2, i).getContents().trim(); 
				    		String length = rs.getCell(3, i).getContents().trim(); 
				    		String startId = rs.getCell(4, i).getContents().trim(); 
				    		String endId = rs.getCell(5, i).getContents().trim(); 
				    		String startHeight = rs.getCell(6, i).getContents().trim(); 
				    		String endHeight = rs.getCell(7, i).getContents().trim();
				    		String material = rs.getCell(8, i).getContents().trim(); 
				    		String buriedYear = rs.getCell(9, i).getContents().trim(); 
				    		String data_Lev = rs.getCell(10, i).getContents().trim(); 
				    		String road = rs.getCell(11, i).getContents().trim(); 
				    		String flag = rs.getCell(12, i).getContents().trim(); 
				    		String flow = rs.getCell(13, i).getContents().trim(); 
	
				    		this.setId(id.toUpperCase());			    		
				    		this.setDiameter(!CommUtil.isNumeric(diameter)?"0":diameter);
				    		this.setLength(!CommUtil.isNumeric(length)?"0":length);
				    		this.setStart_Id(startId.toUpperCase());
				    		this.setEnd_Id(endId.toUpperCase());
				    		this.setStart_Height(!CommUtil.isNumeric(startHeight)?"0":startHeight);
				    		this.setEnd_Height(!CommUtil.isNumeric(endHeight)?"0":endHeight);
				    		this.setMaterial(material);
				    		this.setBuried_Year(buriedYear);
				    		this.setData_Lev(data_Lev);
				    		
				    		this.setProject_Id(Project_Id);
				    		this.setRoad(road);
				    		this.setFlag(flag);
				    		this.setFlow(flow);
				    			    		
				    		//插入提交
				    		msgBean = pRmi.RmiExec(10, this, 0, 25);
					    	if(msgBean.getStatus() == MsgBean.STA_SUCCESS)
							{
					    		succCnt ++;
							}				    	
					    }
						Resp += "文件[" + fileName + "]成功导入[" + String.valueOf(succCnt) + "/" + String.valueOf(tmpCnt) + "]个\\n";
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
		catch(Exception exp)
		{
			System.out.println(exp);
			exp.printStackTrace();
		}
	}
	/** 更新管线详细数据  
	 * @param request
	 * @param response
	 * @param pRmi
	 * @param pFromZone
	 * @param pConfig
	 * 
	 */
	public void UpdateExcel(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone, ServletConfig pConfig) 
	{
		try
		{			
			SmartUpload mySmartUpload = new SmartUpload();
			mySmartUpload.initialize(pConfig, request, response);
			mySmartUpload.setAllowedFilesList("xls,xlsx,XLS,XLSX,");
			mySmartUpload.upload();
								
			Sid = mySmartUpload.getRequest().getParameter("Sid");
			currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
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
						//上传现有文档			
						com.jspsmart.upload.File myFile = mySmartUpload.getFiles().getFile(n);		
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
				    		String diameter = rs.getCell(2, i).getContents().trim(); 
				    		String length = rs.getCell(3, i).getContents().trim(); 
				    		String startId = rs.getCell(4, i).getContents().trim(); 
				    		String endId = rs.getCell(5, i).getContents().trim(); 
				    		String startHeight = rs.getCell(6, i).getContents().trim(); 
				    		String endHeight = rs.getCell(7, i).getContents().trim();
				    		String material = rs.getCell(8, i).getContents().trim();
				    		String buriedYear = rs.getCell(9, i).getContents().trim();
				    		String data_Lev = rs.getCell(10, i).getContents().trim();
				    		String road = rs.getCell(11, i).getContents().trim();
				    		String flag = rs.getCell(12, i).getContents().trim();
				    		String flow = rs.getCell(13, i).getContents().trim();
	
				    		this.setId(id.toUpperCase());			    		
				    		this.setDiameter(!CommUtil.isNumeric(diameter)?"0":diameter);
				    		this.setLength(!CommUtil.isNumeric(length)?"0":length);
				    		this.setStart_Id(startId.toUpperCase());
				    		this.setEnd_Id(endId.toUpperCase());
				    		this.setStart_Height(!CommUtil.isNumeric(startHeight)?"0":startHeight);
				    		this.setEnd_Height(!CommUtil.isNumeric(endHeight)?"0":endHeight);
				    		this.setMaterial(material);
				    		this.setBuried_Year(buriedYear);
				    		this.setData_Lev(data_Lev);
				    		
				    		this.setProject_Id(Project_Id);
				    		this.setRoad(road);
				    		this.setFlag(flag);
				    		this.setFlow(flow);
				    			    		
				    		//插入提交
				    		msgBean = pRmi.RmiExec(13, this, 0, 25);
					    	if(msgBean.getStatus() == MsgBean.STA_SUCCESS)
							{
					    		succCnt ++;
							}				    	
					    }
						Resp += "文件[" + fileName + "]成功导入[" + String.valueOf(succCnt) + "/" + String.valueOf(tmpCnt) + "]个\\n";
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
		catch(Exception exp)
		{
			exp.printStackTrace();
		}
	}
	
	/** 导出管段数据列表
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

			String SheetName = "管线信息表";
			String UPLOAD_NAME = SimFormat.format(new Date());
			//System.out.println("SheetName [" + SheetName + "]" );
			msgBean = pRmi.RmiExec(0, this, 0, 25);
			ArrayList<?> gx_List = (ArrayList<?>) msgBean.getMsg();
			int row_Index = 0;
			Label cell = null;
			if (null != gx_List) {
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
//				font3.setBorder(Border.ALL, BorderLineStyle.THIN);//设置边框线
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
			    cell=new Label(1,0,"道路",font1);   
			    sheet.addCell(cell);   
			    cell=new Label(2,0,"直径",font1);  
			    sheet.addCell(cell);   
			    cell=new Label(3,0,"长度",font1);  
			    sheet.addCell(cell);   
			    cell=new Label(4,0,"起端管井",font1);  
			    sheet.addCell(cell);   
			    cell=new Label(5,0,"终端管井",font1);  
			    sheet.addCell(cell);
			    cell=new Label(6,0,"起端底标高",font1);  
			    sheet.addCell(cell);
			    cell=new Label(7,0,"终端底标高",font1);  
			    sheet.addCell(cell);
			    cell=new Label(8,0,"材料类型",font1);  
			    sheet.addCell(cell); 
			    cell=new Label(9,0,"埋设年份",font1);  
			    sheet.addCell(cell);
			    cell=new Label(10,0,"主/支管",font1);  
			    sheet.addCell(cell);
			    cell=new Label(11,0,"数据等级",font1);  
			    sheet.addCell(cell);
			    cell=new Label(12,0,"所属项目",font1);  
			    sheet.addCell(cell);  
			    cell=new Label(13,0,"设备名称",font1);  
			    sheet.addCell(cell);  
			    
				
				Iterator<?> gx_iterator = gx_List.iterator();

				while (gx_iterator.hasNext()) {
					DevGXBean devGXBean = (DevGXBean) gx_iterator.next();
					Id = devGXBean.getId();
					Diameter = devGXBean.getDiameter();
					Length = devGXBean.getLength();
					Start_Id = devGXBean.getStart_Id();
					End_Id = devGXBean.getEnd_Id();
					Start_Height = devGXBean.getStart_Height();
					End_Height = devGXBean.getEnd_Height();
					Material = devGXBean.getMaterial();
					Buried_Year = devGXBean.getBuried_Year();
					Flag = "";
					try{
						if(devGXBean.getFlag() != null && !devGXBean.getFlag().trim().equals("")){
						  	switch(Integer.parseInt(devGXBean.getFlag())){
					  		case 1:
					  			Flag ="主管";
					  			break;
					  		case 2:
					  			Flag ="支管";
						  		break;
					  		case 3:
					  			Flag ="出口管";
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
					Data_Lev = "";
					try{
						if(devGXBean.getData_Lev() != null && !devGXBean.getData_Lev().trim().equals("")){
						  	switch(Integer.parseInt(devGXBean.getData_Lev())){
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
					Curr_Data = devGXBean.getCurr_Data();
					Project_Name = devGXBean.getProject_Name();
					Equip_Name = devGXBean.getEquip_Name();
					Road = devGXBean.getRoad();

					row_Index++;
					sheet.setRowView(row_Index, 400);
					sheet.setColumnView(row_Index, 25); // row_Index 列宽度
					cell = new Label(0, row_Index, Id, font2);
					sheet.addCell(cell);
					cell = new Label(1, row_Index, Road, font2);
					sheet.addCell(cell);
					cell = new Label(2, row_Index, Diameter, font2);
					sheet.addCell(cell);
					cell = new Label(3, row_Index, Length, font2);
					sheet.addCell(cell);
					cell = new Label(4, row_Index, Start_Id, font2);
					sheet.addCell(cell);
					cell = new Label(5, row_Index, End_Id, font2);
					sheet.addCell(cell);
					cell = new Label(6, row_Index, Start_Height, font2);
					sheet.addCell(cell);
					cell = new Label(7, row_Index, End_Height, font2);
					sheet.addCell(cell);
					cell = new Label(8, row_Index, Material, font2);
					sheet.addCell(cell);
					cell = new Label(9, row_Index, Buried_Year, font2);
					sheet.addCell(cell);
					cell = new Label(10, row_Index, Flag, font2);
					sheet.addCell(cell);
					cell = new Label(11, row_Index, Data_Lev, font2);
					sheet.addCell(cell);
					cell = new Label(12, row_Index, Project_Name, font2);
					sheet.addCell(cell);
					cell = new Label(13, row_Index, Equip_Name, font2);
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
	
	//获取当前项目下的所有管线
	public void getGXAll(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		PrintWriter outprint = response.getWriter();
		
		msgBean = pRmi.RmiExec(2, this, 0, 0);
		
		List<Object> CData = new ArrayList<Object>();
		if (msgBean.getStatus() == MsgBean.STA_SUCCESS)
		{
			ArrayList<?> gxList = (ArrayList<?>) msgBean.getMsg();
			Iterator<?> gxListIterator = gxList.iterator();
			while (gxListIterator.hasNext()) {
				DevGXBean bean = (DevGXBean)gxListIterator.next();
				DevGXJsonBean jsonBean = new DevGXJsonBean();
				jsonBean.setId(bean.getId());
				jsonBean.setDiameter(bean.getDiameter());
				jsonBean.setLength(bean.getLength());
				jsonBean.setStart_Id(bean.getStart_Id());
				jsonBean.setEnd_Id(bean.getEnd_Id());
				jsonBean.setStart_Height(bean.getStart_Height());
				jsonBean.setEnd_Height(bean.getEnd_Height());
				jsonBean.setMaterial(bean.getMaterial());
				jsonBean.setBuried_Year(bean.getBuried_Year());
				jsonBean.setData_Lev(bean.getData_Lev());
				jsonBean.setRoad(bean.getRoad());
				jsonBean.setFlag(bean.getFlag());
				jsonBean.setFlow(bean.getFlow());
				jsonBean.setPort(bean.getPort());
				jsonBean.setStatus(bean.getStatus());
				jsonBean.setDes(bean.getDes());
				jsonBean.setEquip_Id(bean.getEquip_Id());
				jsonBean.setEquip_Name(bean.getEquip_Name());
				jsonBean.setCurr_Data(bean.getCurr_Data());
				CData.add(jsonBean);
			}
		}
		//System.out.println(Resp);
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		String jsonObj = JSONObject.toJSONString(CData);
		System.out.println(jsonObj);
		response.setCharacterEncoding("UTF-8");
		outprint = response.getWriter();
		outprint.write(jsonObj);
		outprint.flush();
	}
		
	public String getSql(int pCmd)
	{  
		String Sql = "";
		switch (pCmd)
		{  
			case 0://查询（类型&项目）
				Sql = " select t.id, t.diameter, t.length, t.start_id, t.end_id, t.start_height, t.end_height, t.material, t.buried_year, t.data_lev, t.project_id, t.project_name, t.equip_id, t.equip_name ,round((t.curr_data),2), t.road, t.flag, t.flow, t.port, t.status, t.des " +
		    	 	   	  " from view_dev_gx t " +		
		    	 	   	  " where t.id like  '%"+ currStatus.getFunc_Sub_Type_Id() +"%'" +  
		    	 	   	  " and t.project_id = '" + currStatus.getFunc_Project_Id() + "' " +
	 	 		          " order by t.id ";
				break;
		    case 1://查询（多个）
		    	switch(Integer.parseInt(currStatus.getFunc_Sort_Id()))
				{
					case 1://按照ID排序
						Sql = " select t.id, t.diameter, t.length, t.start_id, t.end_id, t.start_height, t.end_height, t.material, t.buried_year, t.data_lev, t.project_id, t.project_name, t.equip_id, t.equip_name ,round((t.curr_data),2), t.road, t.flag, t.flow, t.port, t.status, t.des " +
				    	 	   	  " from view_dev_gx t " +		
				    	 	   	  " where t.id like  '%"+ currStatus.getFunc_Sub_Type_Id() +"%'" +  
				    	 	   	  " and t.project_id like '" + currStatus.getFunc_Project_Id() + "%' " +
			 	 		          " order by t.id ";
						break;
					case 2://按照直径排序
						Sql = " select t.id, t.diameter, t.length, t.start_id, t.end_id, t.start_height, t.end_height, t.material, t.buried_year, t.data_lev, t.project_id, t.project_name, t.equip_id, t.equip_name ,round((t.curr_data),2), t.road, t.flag, t.flow, t.port, t.status, t.des " +
			    	 	   	  " from view_dev_gx t " +		
			    	 	   	  " where t.id like  '%"+ currStatus.getFunc_Sub_Type_Id() +"%'" +  
			    	 	   	  " and t.project_id like '" + currStatus.getFunc_Project_Id() + "%' " +
		 	 		          " order by t.diameter ";
				       break;
				    case 3://按照材料排序
						Sql = " select t.id, t.diameter, t.length, t.start_id, t.end_id, t.start_height, t.end_height, t.material, t.buried_year, t.data_lev, t.project_id, t.project_name, t.equip_id, t.equip_name ,round((t.curr_data),2), t.road, t.flag, t.flow, t.port, t.status, t.des " +
			    	 	   	  " from view_dev_gx t " +		
			    	 	   	  " where t.id like '%"+ currStatus.getFunc_Sub_Type_Id() +"%'" +
			    	 	   	  " and t.project_id like '" + currStatus.getFunc_Project_Id() + "%' " +
		 	 		          " order by FIELD(t.material, 'PE', '混凝土') desc";
					   break;
				}
				break;

		    case 2:
				Sql = " select t.id, t.diameter, t.length, t.start_id, t.end_id, t.start_height, t.end_height, t.material, t.buried_year, t.data_lev, t.project_id, t.project_name, t.equip_id, t.equip_name ,round((t.curr_data),2), t.road, t.flag, t.flow, t.port, t.status, t.des " +
	    	 	   	  " from view_dev_gx t " +		
	    	 	   	  " where t.project_id='" + currStatus.getFunc_Project_Id() + "'";	 		         
			   break;
		    case 3://查询(单个)
		    case 5:
				Sql = " select t.id, t.diameter, t.length, t.start_id, t.end_id, t.start_height, t.end_height, t.material, t.buried_year, t.data_lev, t.project_id, t.project_name, t.equip_id, t.equip_name ,round((t.curr_data),2), t.road, t.flag, t.flow, t.port, t.status, t.des " +
	    	 	   	  " from view_dev_gx t " +		
	    	 	   	  " where t.id = '"+ Id +"' and t.project_id='" + currStatus.getFunc_Project_Id() + "'";	 		         
			   break;
		    case 4://查询（项目&子系统）
				Sql = " select t.id, t.diameter, t.length, t.start_id, t.end_id, t.start_height, t.end_height, t.material, t.buried_year, t.data_lev, t.project_id, t.project_name, t.equip_id, t.equip_name ,round((t.curr_data),2), t.road, t.flag, t.flow, t.port, t.status, t.des " +
				      " from view_dev_gx t " +	
				      " where t.project_id = '" + currStatus.getFunc_Project_Id() + "'" + 
				      " and substr(t.id, 3, 3) = '"+ Id.substring(2,5) +"'" +
				      " order by t.id ";
			   break;
		    case 6://查询（项目&子系统）
		    	Sql = " select t.id, t.diameter, t.length, t.start_id, t.end_id, t.start_height, t.end_height, t.material, t.buried_year, t.data_lev, t.project_id, t.project_name, t.equip_id, t.equip_name ,round((t.curr_data),2), t.road, t.flag, t.flow, t.port, t.status, t.des " +
		    			" from view_dev_gx t " +	
		    			" where t.project_id = '" + currStatus.getFunc_Project_Id() + "'" + 
		    			" and t.id like '"+ Id +"%'" +
		    			" order by t.id ";
		    	break;
		    case 7://查询（下载地图）
		    	Sql = " select t.id, t.diameter, t.length, t.start_id, t.end_id, t.start_height, t.end_height, t.material, t.buried_year, t.data_lev, t.project_id, t.project_name, t.equip_id, t.equip_name ,round((t.curr_data),2), t.road, t.flag, t.flow, t.port, t.status, t.des " +
		    			" from view_dev_gx t " +	
		    			" where t.project_id = '" + Project_Id + "'" + 
		    			" and substr(t.id, 3, 3) = '"+ Id.substring(2,5) +"'" +
		    			" order by t.id ";
		    	break;
		    case 10://添加
		    	Sql = " insert into dev_gx(id, diameter, length, start_id, end_id, start_height, end_height, material, buried_year, data_lev, project_id, road, flag, flow)" +
		    			"values('"+ Id +"', '"+ Diameter +"', '"+ Length +"', '"+ Start_Id +"', '"+ End_Id +"',  '"+ Start_Height +"', '"+ End_Height +"',  '"+Material +"', '"+Buried_Year +"', '"+Data_Lev +"', '"+ Project_Id +"', '"+ Road +"', '"+ Flag +"', '" + Flow + "')";
		    	break;	
		    case 13://管线更新
				Sql = " update dev_gx t set t.start_id= '"+ Start_Id + "', t.end_id = '"+ End_Id  + "', t.start_height = '"+ Start_Height + "', t.end_height = '"+ End_Height +"' ,t.diameter= '"+ Diameter + "', t.length = '"+ Length + "', t.buried_year = '"+ Buried_Year + "', t.data_lev = '"+ Data_Lev +"',t.material = '"+ Material +"',t.road = '"+ Road +"',t.flag = '"+ Flag + "',t.flow = '" + Flow + "' " +
					  " where t.id = '"+ Id +"' and t.project_id = '" + Project_Id + "'";
				break;
		    case 11://编辑
				Sql = " update dev_gx t set t.start_id= '"+ Start_Id + "', t.end_id = '"+ End_Id  + "', t.start_height = '"+ Start_Height + "', t.end_height = '"+ End_Height +"' ,t.diameter= '"+ Diameter + "', t.length = '"+ Length + "', t.buried_year = '"+ Buried_Year + "', t.data_lev = '"+ Data_Lev +"',t.material = '"+ Material  +"',t.road = '"+ Road +"',t.flag = '"+ Flag + "',t.flow = '" + Flow + "',t.port = '" + Port + "',t.status = '" + Status + "',t.des = '" + Des + "' " +
					  " where t.id = '"+ Id +"' and t.project_id = '" + currStatus.getFunc_Project_Id() + "'";
				break;
		    case 12://删除
		    	Sql = " delete from dev_gx where id = '"+ Id +"' and project_id = '" + currStatus.getFunc_Project_Id() + "'";
		    	break;
	    /*case 14://终点拖拽
				Sql = " update dev_gx t set t.end_longi = '"+ End_Longi +"', t.end_lati = '"+ End_Lati +"' " +
					  " where t.id = '"+ Id +"'";
				break;
				
			case 15://删除起点标注接口
				Sql = " update dev_gx t set t.start_sign = '0' " +
				      " where t.id = '"+ Id +"'";
				break;
				
			case 16://删除终点标注接口
				Sql = " update dev_gx t set t.end_sign = '0' " +
				      " where t.id = '"+ Id +"'";
				break;
			
			case 17://添加起点标注接口
				Sql = " update dev_gx t set t.start_sign = '1', t.start_longi = '"+ Start_Longi +"', t.start_lati = '"+ Start_Lati +"' " +
					  " where t.id = '"+ Id +"'";
				break;
			case 18://添加起点标注接口
				Sql = " update dev_gx t set t.end_sign = '1', t.end_longi = '"+ End_Longi +"', t.end_lati = '"+ End_Lati +"' " +
					  " where t.id = '"+ Id +"'";
				break;	*/
				
				
			case 21://获取状态
				Sql = "{? = call Func_GX_Get('"+ Id + "', '" + Road +"')}";
				break;

			case 22://统计
				Sql = "{? = call Func_InTotal_GJGX('"+ Id + "', '" + currStatus.getFunc_Project_Id() +"')}";
				break;
			case 23://获取未标注企业
				Sql = "{? = call Func_UnMark_GX_Get('')}";
				break;
			case 24://获取子系统的排出口个数
				Sql = "{? = call Func_Sum_OutGJ('"+Id+"', '"+currStatus.getFunc_Project_Id()+"')}";
				break;
			case 40://编辑设备EquipInfo
				Sql = "{call pro_update_dev_gx('" + Equip_Id + "', '" + Equip_Name + "', '" + Id + "' , '" + Project_Id + "', '" + After_Project_Id + "')}";
				break;
		}
		return Sql;
	}
	
	public boolean getData(ResultSet pRs)
	{
		boolean IsOK = true;
		try
		{
			setId(pRs.getString(1));
			setDiameter(pRs.getString(2));
			setLength(pRs.getString(3));
			setStart_Id(pRs.getString(4));
			setEnd_Id(pRs.getString(5));
			setStart_Height(pRs.getString(6));
			setEnd_Height(pRs.getString(7));
			setMaterial(pRs.getString(8));
			setBuried_Year(pRs.getString(9));
			setData_Lev(pRs.getString(10));
			setProject_Id(pRs.getString(11));
			setProject_Name(pRs.getString(12));
			setEquip_Id(pRs.getString(13));
			setEquip_Name(pRs.getString(14));
			setCurr_Data(pRs.getString(15));
			setRoad(pRs.getString(16));
			setFlag(pRs.getString(17));
			setFlow(pRs.getString(18));
			setPort(pRs.getString(19));
			setStatus(pRs.getString(20));
			setDes(pRs.getString(21));
		}
		catch (SQLException sqlExp)
		{
			sqlExp.printStackTrace();
		}
		return IsOK;
	}
	
	public boolean getHtmlData(HttpServletRequest request)
	{
		boolean IsOK = true;
		try
		{
			setSid(CommUtil.StrToGB2312(request.getParameter("Sid")));
			setId(CommUtil.StrToGB2312(request.getParameter("Id")));
			setDiameter(CommUtil.StrToGB2312(request.getParameter("Diameter")));
			setLength(CommUtil.StrToGB2312(request.getParameter("Length")));
			setStart_Id(CommUtil.StrToGB2312(request.getParameter("Start_Id")));
			setEnd_Id(CommUtil.StrToGB2312(request.getParameter("End_Id")));
			setStart_Height(CommUtil.StrToGB2312(request.getParameter("Start_Height")));
			setEnd_Height(CommUtil.StrToGB2312(request.getParameter("End_Height")));
			setMaterial(CommUtil.StrToGB2312(request.getParameter("Material")));
			setBuried_Year(CommUtil.StrToGB2312(request.getParameter("Buried_Year")));
			setData_Lev(CommUtil.StrToGB2312(request.getParameter("Data_Lev")));
			setProject_Id(CommUtil.StrToGB2312(request.getParameter("Project_Id")));
			setProject_Name(CommUtil.StrToGB2312(request.getParameter("Project_Name")));
			setEquip_Id(CommUtil.StrToGB2312(request.getParameter("Equip_Id")));
			setEquip_Name(CommUtil.StrToGB2312(request.getParameter("Equip_Name")));
			setSubsys_Id(CommUtil.StrToGB2312(request.getParameter("Subsys_Id")));
			setAfter_Project_Id(CommUtil.StrToGB2312(request.getParameter("After_Project_Id")));
			setRoad(CommUtil.StrToGB2312(request.getParameter("Road")));
			setFlag(CommUtil.StrToGB2312(request.getParameter("Flag")));
			setFlow(CommUtil.StrToGB2312(request.getParameter("Flow")));
			setPort(CommUtil.StrToGB2312(request.getParameter("Port")));
			setStatus(CommUtil.StrToGB2312(request.getParameter("Status")));
			setDes(CommUtil.StrToGB2312(request.getParameter("Des")));
			setpSimu(CommUtil.StrToGB2312(request.getParameter("pSimu")));
			
		}
		catch (Exception Exp)
		{
			Exp.printStackTrace();
		}
		return IsOK;
	}
	
	private String Id;
	private String Diameter;
	private String Length;	
	private String Start_Id;
	private String End_Id;
	private String Start_Height;
	private String End_Height;
	private String Material;
	private String Buried_Year;
	private String Data_Lev;
	private String Road;
	private String Flag;
	private String Flow;
	private String Port;
	private String Status;
	private String Des;
	
	private String Project_Id;	
	private String Project_Name;	
	private String Equip_Id;	
	private String Equip_Name;
	private String Curr_Data;
	private String Subsys_Id;
	
	private String Sid;	
	private String After_Project_Id;
	private String pSimu;
	
	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public String getDes() {
		return Des;
	}

	public void setDes(String des) {
		Des = des;
	}

	public String getPort() {
		return Port;
	}

	public void setPort(String port) {
		Port = port;
	}

	public String getFlag()
	{
		return Flag;
	}

	public void setFlag(String flag)
	{
		Flag = flag;
	}

	public String getpSimu()
	{
		return pSimu;
	}

	public void setpSimu(String pSimu)
	{
		this.pSimu = pSimu;
	}

	public String getRoad()
	{
		return Road;
	}

	public void setRoad(String road)
	{
		Road = road;
	}

	public String getAfter_Project_Id() {
		return After_Project_Id;
	}

	public void setAfter_Project_Id(String after_Project_Id) {
		After_Project_Id = after_Project_Id;
	}	
	
	
	public String getCurr_Data() {
		return Curr_Data;
	}

	public void setCurr_Data(String curr_Data) {
		Curr_Data = curr_Data;
	}

	
	
	public String getFlow() {
		return Flow;
	}

	public void setFlow(String flow) {
		Flow = flow;
	}

	public String getSubsys_Id() {
		return Subsys_Id;
	}

	public void setSubsys_Id(String subsys_Id) {
		Subsys_Id = subsys_Id;
	}

	public String getStart_Height() {
		return Start_Height;
	}

	public void setStart_Height(String start_Height) {
		Start_Height = start_Height;
	}

	public String getEnd_Height() {
		return End_Height;
	}

	public void setEnd_Height(String end_Height) {
		End_Height = end_Height;
	}


	public String getBuried_Year() {
		return Buried_Year;
	}

	public void setBuried_Year(String buried_Year) {
		Buried_Year = buried_Year;
	}

	public String getData_Lev() {
		return Data_Lev;
	}

	public void setData_Lev(String data_Lev) {
		Data_Lev = data_Lev;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getDiameter() {
		return Diameter;
	}

	public void setDiameter(String diameter) {
		Diameter = diameter;
	}

	public String getLength() {
		return Length;
	}

	public void setLength(String length) {
		Length = length;
	}

	public String getStart_Id() {
		return Start_Id;
	}

	public void setStart_Id(String start_Id) {
		Start_Id = start_Id;
	}

	public String getEnd_Id() {
		return End_Id;
	}

	public void setEnd_Id(String end_Id) {
		End_Id = end_Id;
	}

	public String getMaterial() {
		return Material;
	}

	public void setMaterial(String material) {
		Material = material;
	}

	public String getProject_Id() {
		return Project_Id;
	}

	public void setProject_Id(String project_Id) {
		Project_Id = project_Id;
	}

	public String getProject_Name() {
		return Project_Name;
	}

	public void setProject_Name(String project_Name) {
		Project_Name = project_Name;
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

	public String getSid() {
		return Sid;
	}

	public void setSid(String sid) {
		Sid = sid;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}


