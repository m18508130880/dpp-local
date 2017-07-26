package net.appsvr;

import bean.BaseCmdBean;
import util.CmdUtil;
import util.Cmd_Sta;
import util.CommUtil;
import util.DBUtil;
public class AppDeviceDataReqBean extends BaseCmdBean {	
	
	private String Dev_Id = "";
	private String Dev_Name = "";
	private String Dev_Attr_Id = "";
	private String Dev_Attr_Name = "";
	private String Dev_CTime = "";
	private String Dev_CData = "";
	private String Dev_Unit = "";
	public AppDeviceDataReqBean(int action, String seq, DBUtil dbUtil) {
		super(action, seq, dbUtil);
	}

	@Override
	public void parseReqest(String srcKey, String strRequest, byte[] strData) {
		// TODO Auto-generated method stub
		this.setActionSource(srcKey);
		this.setReserve(strRequest.substring(0, 20));
		this.setAction(Integer.parseInt(strRequest.substring(24, 28)));
		Dev_Id        = CommUtil.BSubstring(strRequest, 70, 10).trim();
//		Dev_Name      = CommUtil.BSubstring(strRequest, 38, 30).trim();
		Dev_Attr_Id   = CommUtil.BSubstring(strRequest, 80, 4).trim();
//		Dev_Attr_Name = CommUtil.BSubstring(strRequest, 72, 20).trim();
//		Dev_CTime     = CommUtil.BSubstring(strRequest, 92, 20).trim();
		String strCData = CommUtil.BytesToHexString(CommUtil.BSubstring(strRequest, 91, 2).getBytes(), 2);
		float fData = (float) (Integer.parseInt(strCData, 16) * 0.005);
		Dev_CData = String.valueOf(fData);
//		Dev_Unit      = CommUtil.BSubstring(strRequest, 240, 10).trim();
	}

	@Override
	public int execRequest()
	{
		// TODO Auto-generated method stub
		int ret = Cmd_Sta.STA_ERROR;

		if(!Dev_CData.equalsIgnoreCase("NULL") && Dev_CData.length() > 0)
		{
			String Sql = "insert into data(cpm_id, id, cname, attr_id, attr_name, ctime, value, unit)" +
						"SELECT '"+ this.getActionSource().trim() +"', id, '水位仪', '" +
			  	  	     Dev_Attr_Id +"', '水位', date_format(now(), '%Y-%m-%d %H-%i-%S'), '"+ 
			  	  	     Dev_CData +"', 'm' FROM dev_gj WHERE equip_id='" + 
			  	  	     Dev_Id + "'";
			
			System.out.println(Sql);
			if(m_DbUtil.doUpdate(Sql))
			{
				ret = Cmd_Sta.STA_SUCCESS;
			}	
			
			Sql = "update device_detail t set t.onoff = '1' where t.id = '"+ this.getActionSource().trim() +"'";
			m_DbUtil.doUpdate(Sql);
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
	public void execResponse() {
		// TODO Auto-generated method stub
		String sendStr = EncodeRespMsg();
		if(null != sendStr)
		{
			String key = getReserve();
			TcpSvrAppGateWay.DisPatch(CmdUtil.COMM_DELIVER, this.getActionSource(), key + sendStr);
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
}