package bean;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rmi.Rmi;
import rmi.RmiBean;
import util.CommUtil;
import util.CurrStatus;
import util.MsgBean;

import com.alibaba.fastjson.JSONObject;

public class DataBean extends RmiBean 
{	
	public final static long serialVersionUID = RmiBean.RMI_DATA;
	public long getClassId()
	{
		return serialVersionUID;
	}
	
	public DataBean()
	{
		super.className = "DataBean";
	}
	
	public void ExecCmd(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		msgBean = pRmi.RmiExec(0, this, 0, 0);
		
		switch(currStatus.getCmd())
		{
		    case 0://水质数据
		    	request.getSession().setAttribute("000007_HQ_" + Sid, ((Object)msgBean.getMsg()));
		    	currStatus.setJsp("000007_HQ_SZ.jsp?Sid=" + Sid);
		    	break;
		    case 2://水位数据
		    	request.getSession().setAttribute("000007_HQ_" + Sid, ((Object)msgBean.getMsg()));
		    	currStatus.setJsp("000007_HQ_SW.jsp?Sid=" + Sid);
		    	break;
		    case 3://温度数据
		    	request.getSession().setAttribute("000007_HQ_" + Sid, ((Object)msgBean.getMsg()));
		    	currStatus.setJsp("000007_HQ_WD.jsp?Sid=" + Sid);
		    	break;
		    case 1://流速数据
		    	request.getSession().setAttribute("TextLLJ_" + Sid, ((Object)msgBean.getMsg()));
		    	currStatus.setJsp("TextLLJ.jsp?Sid=" + Sid);
		    	break;
		    case 4://氨氮数据
		    	request.getSession().setAttribute("000007_HQ_" + Sid, ((Object)msgBean.getMsg()));
		    	currStatus.setJsp("000007_HQ_NH.jsp?Sid=" + Sid);
		    	break;
		}
		
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
	   	response.sendRedirect(currStatus.getJsp());
	}
	
	public void getDataNow(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 0, 25);
		ArrayList<Object> arrayList = new ArrayList<Object>();
		if (msgBean.getStatus() == MsgBean.STA_SUCCESS)
		{
			ArrayList<?> list = (ArrayList<?>) msgBean.getMsg();
			Iterator<?> iterator = list.iterator();
			while (iterator.hasNext())
			{
				DataBean bean = (DataBean) iterator.next();
				arrayList.add(bean);
			}
		}
		PrintWriter outprint = response.getWriter();
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		String jsonObj = JSONObject.toJSONString(arrayList);
		response.setCharacterEncoding("UTF-8");
		outprint.write(jsonObj);
		outprint.flush();

//		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
//		outprint.write(Resp);
	}
	//数据图表
	public void Graph(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) 
	{
		try
		{
			getHtmlData(request);
			currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
			currStatus.getHtmlData(request, pFromZone);
		    
			switch(currStatus.getFunc_Id())
			{
			    case 1://时均值
			    	break;
			    case 2://日均值
			    	request.getSession().setAttribute("Month_" + Sid, Month);
			    	request.getSession().setAttribute("Year_" + Sid, Year);
			    	break;
			}
			
			msgBean = pRmi.RmiExec(currStatus.getCmd(), this, 1, 25);
			switch(currStatus.getCmd())
			{
				case 20://数据图表
			    	request.getSession().setAttribute("Graph_" + Sid, (Object)msgBean.getMsg());
					currStatus.setJsp("Graph.jsp?Sid=" + Sid);
					break;
			}
			
			request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		   	response.sendRedirect(currStatus.getJsp());
		}
		catch(Exception Ex)
		{
			Ex.printStackTrace();
		}
	}
	// 获取涨幅情况
	public void getRising(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus)request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		msgBean = pRmi.RmiExec(9, this, 0, 25);
		String rising = "";
		if (msgBean.getStatus() == MsgBean.STA_SUCCESS)
		{
			ArrayList<?> list = (ArrayList<?>) msgBean.getMsg();
			Iterator<?> iterator = list.iterator();
			int i = 0;
			while (iterator.hasNext())
			{
				DataBean bean = (DataBean) iterator.next();
				if(i == 1){
					bean.getValue();
					if(Double.valueOf(Value) < Double.valueOf(bean.getValue())){
						rising = "rising";
					}else if(Double.valueOf(Value) > Double.valueOf(bean.getValue())){
						rising = "falling";
					}else {
						rising = "keep";
					}
				}
				i ++;
			}
		}
		PrintWriter outprint = response.getWriter();
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		response.setCharacterEncoding("UTF-8");
		outprint.write(rising);
		outprint.flush();
	}
	
	public String getSql(int pCmd)
	{
		String Sql = "";
		switch (pCmd)
		{
			case 0://实时数据
				Sql = " select '' AS sn, t.cpm_id, t.id, t.addrs, t.code, t.sign, t.cname, t.attr_id, t.attr_name, t.ctime, t.value, t.unit, t.lev, t.des " +
					  " FROM data t" +
					  " where cpm_id = '" + Cpm_Id + "'" +
					  " and ctime >= date_format('"+currStatus.getVecDate().get(0).toString()+"', '%Y-%m-%d %H-%i-%S')" + 
					  " and ctime <= date_format('"+currStatus.getVecDate().get(1).toString()+"', '%Y-%m-%d %H-%i-%S')" + 
					  " ORDER BY t.ctime";
				break;
			case 1://实时数据
				Sql = " select '' AS sn, t.cpm_id, t.id, t.addrs, t.code, t.sign, t.cname, t.attr_id, t.attr_name, t.ctime, t.value, t.unit, t.lev, t.des " +
						" FROM data t" +
						" where cpm_id = '" + Cpm_Id + "'" +
						" and Attr_id = '" + Attr_Id + "'" +
						" ORDER BY t.ctime desc LIMIT 25 OFFSET 0";
				break;
			case 9:// 获取历史前两个数据
				Sql = " select '' AS sn, t.cpm_id, t.id, t.addrs, t.code, t.sign, t.cname, t.attr_id, t.attr_name, t.ctime, t.value, t.unit, t.lev, t.des " +
						" FROM data t" +
						" where cpm_id = '" + Cpm_Id + "'" +
						" and Attr_id = '" + Attr_Id + "'" +
						" ORDER BY t.ctime desc LIMIT 2 OFFSET 0";
				break;
			case 20://数据图表
				Sql = " {? = call rmi_graph('"+ Id +"', '"+ currStatus.getFunc_Id() +"', '"+ currStatus.getVecDate().get(0).toString().substring(0,10) +"')}";
				break;
		}
		return Sql;
	}
	
	public boolean getData(ResultSet pRs) 
	{
		boolean IsOK = true;
		try
		{
			setSN(pRs.getString(1));
			setCpm_Id(pRs.getString(2));
			setId(pRs.getString(3));
			setAddrs(pRs.getString(4));
			setCode(pRs.getString(5));
			setSign(pRs.getString(6));
			setCName(pRs.getString(7));		
			setAttr_Id(pRs.getString(8));
			setAttr_Name(pRs.getString(9));			
			setCTime(pRs.getString(10));
			setValue(pRs.getString(11));
			setUnit(pRs.getString(12));
			setLev(pRs.getString(13));
			setDes(pRs.getString(14));
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
			setSN(CommUtil.StrToGB2312(request.getParameter("SN")));
			setCpm_Id(CommUtil.StrToGB2312(request.getParameter("Cpm_Id")));
			setId(CommUtil.StrToGB2312(request.getParameter("Id")));
			setAddrs(CommUtil.StrToGB2312(request.getParameter("Addrs")));
			setCode(CommUtil.StrToGB2312(request.getParameter("Code")));
			setSign(CommUtil.StrToGB2312(request.getParameter("Sign")));
			setCName(CommUtil.StrToGB2312(request.getParameter("CName")));
			setAttr_Id(CommUtil.StrToGB2312(request.getParameter("Attr_Id")));
			setAttr_Name(CommUtil.StrToGB2312(request.getParameter("Attr_Name")));		
			setCTime(CommUtil.StrToGB2312(request.getParameter("CTime")));		
			setValue(CommUtil.StrToGB2312(request.getParameter("Value")));
			setUnit(CommUtil.StrToGB2312(request.getParameter("Unit")));
			setLev(CommUtil.StrToGB2312(request.getParameter("Lev")));
			setDes(CommUtil.StrToGB2312(request.getParameter("Des")));
			
			setSid(CommUtil.StrToGB2312(request.getParameter("Sid")));
			setLevel(CommUtil.StrToGB2312(request.getParameter("Level")));
			setYear(CommUtil.StrToGB2312(request.getParameter("Year")));
			setMonth(CommUtil.StrToGB2312(request.getParameter("Month")));		
		}
		catch (Exception Exp) 
		{
			Exp.printStackTrace();
		}
		return IsOK;
	}
	
	private String SN;
	private String Cpm_Id;
	private String Id;
	private String Addrs;
	private String Code;
	private String Sign;
	private String CName;
	private String Attr_Id;
	private String Attr_Name;
	private String CTime;
	private String Value;
	private String Unit;
	private String Lev;
	private String Des;
	
	private String Sid;
	private String Level;
	private String Year;
	private String Month;
	
	public String getSN() {
		return SN;
	}

	public void setSN(String sN) {
		SN = sN;
	}

	public String getCpm_Id() {
		return Cpm_Id;
	}

	public void setCpm_Id(String cpm_Id) {
		Cpm_Id = cpm_Id;
	}

	public String getAddrs() {
		return Addrs;
	}

	public void setAddrs(String addrs) {
		Addrs = addrs;
	}

	public String getCode() {
		return Code;
	}

	public void setCode(String code) {
		Code = code;
	}

	public String getSign() {
		return Sign;
	}

	public void setSign(String sign) {
		Sign = sign;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getCName() {
		return CName;
	}

	public void setCName(String cName) {
		CName = cName;
	}

	public String getAttr_Id() {
		return Attr_Id;
	}

	public void setAttr_Id(String attrId) {
		Attr_Id = attrId;
	}

	public String getAttr_Name() {
		return Attr_Name;
	}

	public void setAttr_Name(String attrName) {
		Attr_Name = attrName;
	}

	public String getCTime() {
		return CTime;
	}

	public void setCTime(String cTime) {
		CTime = cTime;
	}

	public String getValue() {
		return Value;
	}

	public void setValue(String value) {
		Value = value;
	}

	public String getUnit() {
		return Unit;
	}

	public void setUnit(String unit) {
		Unit = unit;
	}

	public String getLev() {
		return Lev;
	}

	public void setLev(String lev) {
		Lev = lev;
	}

	public String getDes() {
		return Des;
	}

	public void setDes(String des) {
		Des = des;
	}
	
	public String getSid() {
		return Sid;
	}

	public void setSid(String sid) {
		Sid = sid;
	}

	public String getLevel() {
		return Level;
	}

	public void setLevel(String level) {
		Level = level;
	}

	public String getYear() {
		return Year;
	}

	public void setYear(String year) {
		Year = year;
	}

	public String getMonth() {
		return Month;
	}

	public void setMonth(String month) {
		Month = month;
	}
}