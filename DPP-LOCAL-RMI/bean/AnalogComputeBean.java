package bean;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rmi.Rmi;
import rmi.RmiBean;
import util.CommUtil;
import util.CurrStatus;
import util.MsgBean;

public class AnalogComputeBean extends RmiBean
{
	public final static long	serialVersionUID	= RmiBean.RMI_COMPUTE;

	public long getClassId()
	{
		return serialVersionUID;
	}

	public AnalogComputeBean()
	{
		super.className = "AnalogComputeBean";
	}
	
	public AnalogComputeBean(CurrStatus currStatus)
	{
		super.className = "AnalogComputeBean";
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
	public void Compute(HttpServletRequest request, HttpServletResponse response, Rmi pRmi, boolean pFromZone) throws ServletException, IOException
	{
		getHtmlData(request);
		currStatus = (CurrStatus) request.getSession().getAttribute("CurrStatus_" + Sid);
		currStatus.getHtmlData(request, pFromZone);
		
		String Resp = "9999";
		String Defeat = "";
		String Success = "";
		
		String SysIdList = CommUtil.StrToGB2312(request.getParameter("gjList"));
		if(SysIdList != null && SysIdList.length() > 0){
			Resp = "0000";
			String [] list = SysIdList.split(",");
			AnalogBean analog = new AnalogBean();
			for(int i = 0; i < list.length; i ++){
				Id = list[i];
				String FileName = currStatus.getFunc_Project_Id() + "_" + Id;
				WaterAcc = analog.AnalogWaterAcc(FileName, Double.parseDouble(Simu));
				if(!WaterAcc.contains("|")){
					Defeat += Id + ",";
					continue;
				}
//				WaterLev = analog.getWaterLevList();
//				WaterAccGj = analog.getWaterAccGj();
//				WaterFlowLoad = analog.getWaterFlowLoad();
//				WaterActualFlow = analog.getWaterActualFlow();
//				WaterFlowRate = analog.getWaterFlowRate();
				msgBean = pRmi.RmiExec(12, this, 0, 25);
				msgBean = pRmi.RmiExec(10, this, 0, 25);
				if(msgBean.getStatus() == MsgBean.STA_SUCCESS){
					Success += Id + ",";
				}
			}
			Resp += "成功[" + Success + "]失败[" + Defeat + "]";
		}
		
		PrintWriter outprint = response.getWriter();
		request.getSession().setAttribute("CurrStatus_" + Sid, currStatus);
		outprint.write(Resp);
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
				Sql = " select t.id, t.project_id, t.simu, t.waterAcc, t.waterLev, t.waterAccGj, t.waterFlowLoad, t.waterActualFlow, t.waterFlowRate" + 
					  " from analog_compute t where t.id = '" + Id + "%' " + 
					  " and t.project_id = '" + currStatus.getFunc_Project_Id() + "' " + 
					  " order by t.id";
				break;
			case 10:// 添加
				Sql = " insert into analog_compute(id, project_id, simu, waterAcc, waterLev, waterAccGj, waterFlowLoad, waterActualFlow, waterFlowRate) " + 
					  " values('" + Id + "','" + Project_Id + "','" + Simu + "','" + WaterAcc + "','" + WaterLev + "','" + WaterAccGj + "','" + WaterFlowLoad + "','" + WaterActualFlow + "','" + WaterFlowRate + "')";
				break;
			case 11:// 编辑
				Sql = " update analog_compute set simu= '" + Simu + "', waterAcc = '" + WaterAcc + "' ,waterLev= '" + WaterLev + "', waterAccGj = '" + WaterAccGj + "', waterFlowLoad = '" + WaterFlowLoad + "', waterActualFlow = '" + WaterActualFlow + "', waterFlowRate = '" + WaterFlowRate + "' " + 
					  " where id = '" + Id + "' and project_id = '" + currStatus.getFunc_Project_Id() + "'";
				break;
			case 12:// 删除
				Sql = " delete from analog_compute " +
					  " where id = '" + Id + "' and project_id = '" + currStatus.getFunc_Project_Id() + "'";
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
			setProject_Id(pRs.getString(2));
			setSimu(pRs.getString(3));
			setWaterAcc(pRs.getString(4));
			setWaterLev(pRs.getString(5));
			setWaterAccGj(pRs.getString(6));
			setWaterFlowLoad(pRs.getString(7));
			setWaterActualFlow(pRs.getString(8));
			setWaterFlowRate(pRs.getString(9));
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
			setProject_Id(CommUtil.StrToGB2312(request.getParameter("Project_Id")));
			setSimu(CommUtil.StrToGB2312(request.getParameter("Simu")));
			setWaterAcc(CommUtil.StrToGB2312(request.getParameter("WaterAcc")));
			setWaterLev(CommUtil.StrToGB2312(request.getParameter("WaterLev")));
			setWaterAccGj(CommUtil.StrToGB2312(request.getParameter("WaterAccGj")));
			setWaterFlowLoad(CommUtil.StrToGB2312(request.getParameter("WaterFlowLoad")));
			setWaterActualFlow(CommUtil.StrToGB2312(request.getParameter("WaterActualFlow")));
			setWaterFlowRate(CommUtil.StrToGB2312(request.getParameter("WaterFlowRate")));
		}
		catch (Exception Exp)
		{
			Exp.printStackTrace();
		}
		return IsOK;
	}

	private String	Id;
	private String	Project_Id;
	private String	Simu;
	private String	WaterAcc;
	private String	WaterLev;
	private String	WaterAccGj;
	private String	WaterFlowLoad;
	private String	WaterActualFlow;
	private String	WaterFlowRate;
	
	private String Sid;

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getProject_Id() {
		return Project_Id;
	}

	public void setProject_Id(String project_Id) {
		Project_Id = project_Id;
	}

	public String getSimu() {
		return Simu;
	}

	public void setSimu(String simu) {
		Simu = simu;
	}

	public String getWaterAcc() {
		return WaterAcc;
	}

	public void setWaterAcc(String waterAcc) {
		WaterAcc = waterAcc;
	}

	public String getWaterLev() {
		return WaterLev;
	}

	public void setWaterLev(String waterLev) {
		WaterLev = waterLev;
	}

	public String getWaterAccGj() {
		return WaterAccGj;
	}

	public void setWaterAccGj(String waterAccGj) {
		WaterAccGj = waterAccGj;
	}

	public String getWaterFlowLoad() {
		return WaterFlowLoad;
	}

	public void setWaterFlowLoad(String waterFlowLoad) {
		WaterFlowLoad = waterFlowLoad;
	}

	public String getWaterActualFlow() {
		return WaterActualFlow;
	}

	public void setWaterActualFlow(String waterActualFlow) {
		WaterActualFlow = waterActualFlow;
	}

	public String getWaterFlowRate() {
		return WaterFlowRate;
	}

	public void setWaterFlowRate(String waterFlowRate) {
		WaterFlowRate = waterFlowRate;
	}

	public String getSid() {
		return Sid;
	}

	public void setSid(String sid) {
		Sid = sid;
	}
	
}