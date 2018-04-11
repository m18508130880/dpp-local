package bean;

import net.MsgCtrl;
import util.CmdUtil;
import util.Cmd_Sta;
import util.CommUtil;
public class AppDeviceDataReqBean extends BaseCmdBean {	
	
	private String Dev_Id = "";
	private String Dev_Name = ""; 
	private String Dev_Attr_Id = "";
	private String Dev_Attr_Name = "";
	private String Dev_CTime = "";
	private String Dev_CData = "";
	private String Dev_RealData = "";
	
	private String Dev_Unit = "";
	public AppDeviceDataReqBean(int action, String seq) {
		super(action, seq);
		setBeanName("AppDeviceDataReqBean");
	}

	@Override
	public void parseReqest(String srcKey, String strRequest, byte[] strData) {
		// TODO Auto-generated method stub

		String strCData = CommUtil.BytesToHexString(CommUtil.BSubstring(strRequest, 115, 2).getBytes(), 2);	
		System.out.println("strCData[" + strCData + "]");
		
		this.setActionSource(srcKey);
		this.setReserve(strRequest.substring(0, 20));
		this.setAction(Integer.parseInt(strRequest.substring(24, 28)));
		Dev_Id        = CommUtil.BSubstring(strRequest, 28, 10).trim();
		Dev_Name      = CommUtil.BSubstring(strRequest, 38, 30).trim();
		Dev_Attr_Id   = CommUtil.BSubstring(strRequest, 68, 4).trim();
		Dev_Attr_Name = CommUtil.BSubstring(strRequest, 72, 20).trim();
		Dev_CTime     = CommUtil.BSubstring(strRequest, 92, 20).trim();	
		Dev_CData     = CommUtil.BSubstring(strRequest, 112, 128).trim();

		byte[] byteData = new byte[2];
		byteData[0] = strData[155];
		byteData[1] = strData[156];
		if(Dev_CTime.substring(0,4).contains("1970") || Dev_CTime.substring(0,4).contains("2000"))
		{
			Dev_CTime = CommUtil.getDateTime();
		}
		if((Dev_Id.substring(0,6) + Dev_Attr_Id).equals(Cmd_Sta.DATA_041101_0001))		//星仪液位，精度0.005
		{
			float fData = (float) (Integer.parseInt(CommUtil.BytesToHexString(byteData, 2), 16) * 0.005);
			Dev_RealData = String.valueOf(fData);
		}
		else if((Dev_Id.substring(0,6) + Dev_Attr_Id).equals(Cmd_Sta.DATA_041102_0001))		//星仪液位，水位高度，精度0.0025
		{			
			float fData = (float) (Integer.parseInt(CommUtil.BytesToHexString(byteData, 2), 16) * 0.0025);
			Dev_RealData = String.valueOf(fData);
		}
		else if((Dev_Id.substring(0,6) + Dev_Attr_Id).equals(Cmd_Sta.DATA_041103_0001))		//星仪液位，水位高度，精度0.003
		{			
			float fData = (float) (Integer.parseInt(CommUtil.BytesToHexString(byteData, 2), 16) * 0.003);
			Dev_RealData = String.valueOf(fData);
		}
		else
		{
			Dev_RealData = Dev_CData;
		}
		//增加水位值判断,水位计量程为0-5m
		if(Double.valueOf(Dev_RealData) < 0.0 || Double.valueOf(Dev_RealData) > 5.0)
		{
			Dev_RealData = "0.0";
		}
		
		Dev_Unit      = CommUtil.BSubstring(strRequest, 240, 10).trim();
	}

	@Override
	public int execRequest(MsgCtrl m_MsgCtrl)
	{
		// TODO Auto-generated method stub
		int ret = Cmd_Sta.STA_ERROR;
		String Sql = "";
		String sendStr = "";
		if(!Dev_CData.equalsIgnoreCase("NULL") && Dev_CData.length() > 0)
		{
			if((Dev_Id.substring(0,6) + Dev_Attr_Id).equals(Cmd_Sta.DATA_041101_0001))		//星仪液位，精度0.005
			{
//				Sql = "insert into data(cpm_id, project_id, id, cname, attr_id, attr_name, ctime, value, unit)" +
//						"SELECT '"+ this.getActionSource().trim() +"', project_id, id, '水位仪', '" +
//			  	  	     Dev_Attr_Id +"', '水位', date_format('"+ Dev_CTime +"', '%Y-%m-%d %H-%i-%S'), '"+ 
//			  	  	     Dev_RealData +"', 'm' FROM dev_gj WHERE equip_id='" + 
//			  	  	     Dev_Id + "'";
				Sql = "insert into data(cpm_id, id, cname, attr_id, attr_name, ctime, value, unit)" +
						"values('"+ this.getActionSource().trim() +"', " +
			  	  	     "'"+ Dev_Id +"', " +
			  	  	     "'水位仪', " +
			  	  	     "'"+ Dev_Attr_Id +"', " +
			  	  	     "'水位', " +
			  	  	     "date_format('"+ Dev_CTime +"', '%Y-%m-%d %H-%i-%S'), " +
			  	  	     "'"+ Dev_RealData +"', " +
			  	  	     "'m')";
			}
			else if((Dev_Id.substring(0,6) + Dev_Attr_Id).equals(Cmd_Sta.DATA_041102_0001))		//星仪液位，精度0.0025
			{
//				Sql = "insert into data(cpm_id, project_id, id, cname, attr_id, attr_name, ctime, value, unit)" +
//						"SELECT '"+ this.getActionSource().trim() +"', project_id, id, '水位仪', '" +
//			  	  	     Dev_Attr_Id +"', '水位', date_format('"+ Dev_CTime +"', '%Y-%m-%d %H-%i-%S'), '"+ 
//			  	  	     Dev_RealData +"', 'm' FROM dev_gj WHERE equip_id='" + 
//			  	  	     Dev_Id + "'";
				Sql = "insert into data(cpm_id, id, cname, attr_id, attr_name, ctime, value, unit)" +
						"values('"+ this.getActionSource().trim() +"', " +
			  	  	     "'"+ Dev_Id +"', " +
			  	  	     "'水位仪', " +
			  	  	     "'"+ Dev_Attr_Id +"', " +
			  	  	     "'水位', " +
			  	  	     "date_format('"+ Dev_CTime +"', '%Y-%m-%d %H-%i-%S'), " +
			  	  	     "'"+ Dev_RealData +"', " +
			  	  	     "'m')";
			}
			else if((Dev_Id.substring(0,6) + Dev_Attr_Id).equals(Cmd_Sta.DATA_041103_0001))		//星仪液位，精度0.0025
			{
//				Sql = "insert into data(cpm_id, project_id, id, cname, attr_id, attr_name, ctime, value, unit)" +
//						"SELECT '"+ this.getActionSource().trim() +"', project_id, id, '水位仪', '" +
//			  	  	     Dev_Attr_Id +"', '水位', date_format('"+ Dev_CTime +"', '%Y-%m-%d %H-%i-%S'), '"+ 
//			  	  	     Dev_RealData +"', 'm' FROM dev_gj WHERE equip_id='" + 
//			  	  	     Dev_Id + "'";
				Sql = "insert into data(cpm_id, id, cname, attr_id, attr_name, ctime, value, unit)" +
						"values('"+ this.getActionSource().trim() +"', " +
			  	  	     "'"+ Dev_Id +"', " +
			  	  	     "'水位仪', " +
			  	  	     "'"+ Dev_Attr_Id +"', " +
			  	  	     "'水位', " +
			  	  	     "date_format('"+ Dev_CTime +"', '%Y-%m-%d %H-%i-%S'), " +
			  	  	     "'"+ Dev_RealData +"', " +
			  	  	     "'m')";
			}
			else
			{
				Sql = "insert into data(cpm_id, id, cname, attr_id, attr_name, ctime, value, unit)" +
					  	  "values('"+ this.getActionSource().trim() +"', " +
					  	  	     "'"+ Dev_Id +"', " +
					  	  	     "'"+ Dev_Name +"', " +
					  	  	     "'"+ Dev_Attr_Id +"', " +
					  	  	     "'"+ Dev_Attr_Name +"', " +
					  	  	     "date_format('"+ Dev_CTime +"', '%Y-%m-%d %H-%i-%S'), " +
					  	  	     "'"+ Dev_CData +"', " +
					  	  	     "'"+ Dev_Unit +"')";
			}
			if(m_MsgCtrl.getM_DBUtil().doUpdate(Sql))
			{
				ret = Cmd_Sta.STA_SUCCESS;
				String Project_IdAndGJ_Id = m_MsgCtrl.getM_DBUtil().GetProject_IdAndGJ_Id(this.getActionSource().trim(),Dev_Id);
				sendStr = CommUtil.StrBRightFillSpace("", 20);									//保留字
				sendStr += CommUtil.StrBRightFillSpace("0000", 4);								//命令发送状态
				sendStr += CommUtil.StrBRightFillSpace(Cmd_Sta.CMD_SUBMIT_2002 + "", 4);		//处理指令
				sendStr += CommUtil.StrBRightFillSpace(Project_IdAndGJ_Id, 10);					//项目和子系统ID
				m_MsgCtrl.getM_TcpSvr().DisPatch(Cmd_Sta.COMM_DELIVER, CommUtil.StrBRightFillSpace(Cmd_Sta.DATA_00000_0001, 20), sendStr);
				
			}
		}
		
		//回复
		//setStatus(CommUtil.IntToStringLeftFillZero(ret, 4));
		//execResponse();
		return ret;
	}
	
	public void noticeTimeOut()
	{
		
	}
	@SuppressWarnings("unused")
	private String EncodeSendMsg()
	{
		String ret = null;
		return ret;
	}
	
	@Override
	public void parseReponse(String strResponse){
		// TODO Auto-generated method stub
		this.setStatus(strResponse.substring(20, 24));
	}

	@Override
	public void execResponse(MsgCtrl m_MsgCtrl) {
		// TODO Auto-generated method stub
		String sendStr = EncodeRespMsg();
		if(null != sendStr)
		{
			String key = getReserve();
			m_MsgCtrl.getM_TcpSvr().DisPatch(CmdUtil.COMM_DELIVER, this.getActionSource(), key + sendStr);
		}
	}	

	private String EncodeRespMsg()
	{
		String ret = null;
		ret = getStatus() + getAction();
		return ret;
	}

	public String getDev_Id() {
		return Dev_Id;
	}

	public void setDev_Id(String devId) {
		Dev_Id = devId;
	}

	public String getDev_Name() {
		return Dev_Name;
	}

	public void setDev_Name(String devName) {
		Dev_Name = devName;
	}

	public String getDev_Attr_Id() {
		return Dev_Attr_Id;
	}

	public void setDev_Attr_Id(String devAttrId) {
		Dev_Attr_Id = devAttrId;
	}

	public String getDev_Attr_Name() {
		return Dev_Attr_Name;
	}

	public void setDev_Attr_Name(String devAttrName) {
		Dev_Attr_Name = devAttrName;
	}

	public String getDev_CTime() {
		return Dev_CTime;
	}

	public void setDev_CTime(String devCTime) {
		Dev_CTime = devCTime;
	}

	public String getDev_CData() {
		return Dev_CData;
	}

	public void setDev_CData(String devCData) {
		Dev_CData = devCData;
	}

	public String getDev_Unit() {
		return Dev_Unit;
	}
	
	public void setDev_Unit(String devUnit) {
		Dev_Unit = devUnit;
	}

	public String getDev_RealData() {
		return Dev_RealData;
	}

	public void setDev_RealData(String dev_RealData) {
		Dev_RealData = dev_RealData;
	}
}