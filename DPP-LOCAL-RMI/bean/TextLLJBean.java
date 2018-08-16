package bean;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import rmi.Rmi;
import rmi.RmiBean;
import util.CommUtil;
import util.CurrStatus;
import util.MsgBean;

public class TextLLJBean extends RmiBean
{
	public final static long	serialVersionUID	= RmiBean.RMI_TEXT_LLJ;

	public long getClassId()
	{
		return serialVersionUID;
	}

	public TextLLJBean()
	{
		super.className = "TextLLJBean";
	}
	
	public TextLLJBean(CurrStatus currStatus)
	{
		super.className = "TextLLJBean";
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
			
		}
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		response.sendRedirect(currStatus.getJsp());
	}
	
	public void getShow(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		String Resp = "9999";
		msgBean = pRmi.RmiExec(1, this, 0, 25);
		
		if(MsgBean.STA_SUCCESS == msgBean.getStatus()){
			Resp = "0000";
			TextLLJBean bean = (TextLLJBean) ((ArrayList<?>) msgBean.getMsg()).get(0);
			Resp += bean.getCTime() + "|" + bean.getValue();
		}
		
		PrintWriter outprint = response.getWriter();
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		outprint.write(Resp);
	}
	
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
			String SheetName = "多普勒流量计";
			String UPLOAD_NAME = SimFormat.format(new Date());
			//System.out.println("SheetName [" + SheetName + "]");
			msgBean = pRmi.RmiExec(0, this, 0, 25);
			ArrayList<?> list = (ArrayList<?>) msgBean.getMsg();
			int row_Index = 0;
			Label cell = null;
			if (null != list)
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

				sheet.setRowView(row_Index, 450);
				sheet.setColumnView(row_Index, 25);
				cell = new Label(0, 0, "序号", font1);
				sheet.addCell(cell);
				cell = new Label(1, 0, "时间", font1);
				sheet.addCell(cell);
				cell = new Label(2, 0, "温度", font1);
				sheet.addCell(cell);
				cell = new Label(3, 0, "水位", font1);
				sheet.addCell(cell);
				cell = new Label(4, 0, "流速", font1);
				sheet.addCell(cell);

				Iterator<?> iterator = list.iterator();
				
				int sn = 1;
				while (iterator.hasNext())
				{
					TextLLJBean devGJBean = (TextLLJBean) iterator.next();
					CTime = devGJBean.getCTime();
					Value = devGJBean.getValue();
					String tmp = "";
					String waterLev = "";
					String velocity = "";
					if(Value.length() > 0){
						String[] str = Value.split(" ");
						tmp = str[0];
						waterLev = str[1];
						velocity = str[3];
					}
					row_Index++;
					sheet.setRowView(row_Index, 400);
					sheet.setColumnView(row_Index, 25); // row_Index 列宽度

					cell = new Label(0, row_Index, String.valueOf(sn), font2);
					sheet.addCell(cell);
					cell = new Label(1, row_Index, CTime, font2);
					sheet.addCell(cell);
					cell = new Label(2, row_Index, tmp, font2);
					sheet.addCell(cell);
					cell = new Label(3, row_Index, waterLev, font2);
					sheet.addCell(cell);
					cell = new Label(4, row_Index, velocity, font2);
					sheet.addCell(cell);
					
					sn ++;
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
	
	/**
	 * 获取相应sql语句
	 * 
	 */
	public String getSql(int pCmd)
	{
		String Sql = "";
		switch (pCmd)
		{
			case 0:// 查询（子系统&项目）
				Sql = " select t.SN, t.CPM_Id, t.Id, t.CName, t.Attr_Id, t.CTime, t.Value, t.Unit, t.Lev, t.Des " + 
					  " from data_dpl t order by t.CName ";
				break;
			case 1:// 查询最新
				Sql = " select t.SN, t.CPM_Id, t.Id, t.CName, t.Attr_Id, t.CTime, t.Value, t.Unit, t.Lev, t.Des " + 
					  " FROM data_dpl ORDER BY sn DESC LIMIT 0, 1 ";
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
			setSN(pRs.getString(1));
			setCPM_Id(pRs.getString(2));
			setId(pRs.getString(3));
			setCName(pRs.getString(4));
			setAttr_Id(pRs.getString(5));
			setCTime(pRs.getString(6));
			setValue(pRs.getString(7));
			setUnit(pRs.getString(8));
			setLev(pRs.getString(9));
			setDes(pRs.getString(10));
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
			setSN(CommUtil.StrToGB2312(request.getParameter("SN")));
			setCPM_Id(CommUtil.StrToGB2312(request.getParameter("CPM_Id")));
			setId(CommUtil.StrToGB2312(request.getParameter("Id")));
			setCName(CommUtil.StrToGB2312(request.getParameter("CName")));
			setAttr_Id(CommUtil.StrToGB2312(request.getParameter("Attr_Id")));
			setCTime(CommUtil.StrToGB2312(request.getParameter("CTime")));
			setValue(CommUtil.StrToGB2312(request.getParameter("Value")));
			setUnit(CommUtil.StrToGB2312(request.getParameter("Unit")));
			setLev(CommUtil.StrToGB2312(request.getParameter("Lev")));
			setDes(CommUtil.StrToGB2312(request.getParameter("Des")));
			
			setSid(CommUtil.StrToGB2312(request.getParameter("Sid")));
			
		}
		catch (Exception Exp)
		{
			Exp.printStackTrace();
		}
		return IsOK;
	}

	private String	SN;
	private String	CPM_Id;
	private String	Id;
	private String	CName;
	private String	Attr_Id;
	private String	CTime;
	private String	Value;
	private String	Unit;
	private String	Lev;
	private String	Des;

	private String	Sid;

	public String getSN() {
		return SN;
	}

	public void setSN(String sN) {
		SN = sN;
	}

	public String getCPM_Id() {
		return CPM_Id;
	}

	public void setCPM_Id(String cPM_Id) {
		CPM_Id = cPM_Id;
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

	public void setAttr_Id(String attr_Id) {
		Attr_Id = attr_Id;
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
	
}