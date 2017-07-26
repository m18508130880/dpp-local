package net.appsvr;

import net.TcpSvrBase;
import bean.BaseCmdBean;
import util.CmdUtil;
import util.Cmd_Sta;
import util.CommUtil;
import util.DBUtil;
public class RmiDeviceDataReqBean extends BaseCmdBean {	
	private String BTime = "";
	
	private String Dev_Id = "";
	private String Dev_Status = "";
	private String Dev_Deal = "";
	private String Act_Id = "";
	private String Act_Value = "";
	private String DeliverData = "";
	
	private String Oprator = "";
	
	public RmiDeviceDataReqBean(int action, String seq, DBUtil dbUtil) {
		super(action, seq, dbUtil);
	}

	@Override
	public void parseReqest(String srcKey, String strRequest, byte[] strData) {
		// TODO Auto-generated method stub

		//String strCData = CommUtil.BytesToHexString(CommUtil.BSubstring(strRequest, 48, 2).getBytes(), 2);	
		//System.out.println("strCData[" + strCData + "]");
		
		this.setActionSource(srcKey);
		this.setReserve(strRequest.substring(0, 20));
		Dev_Id        = CommUtil.BSubstring(strRequest, 28, 10).trim();
		Dev_Status    = CommUtil.BSubstring(strRequest, 20, 4).trim();
		Dev_Deal      = CommUtil.BSubstring(strRequest, 24, 4).trim();
		Act_Id  	  = "0001"+Dev_Deal;
		Oprator       = CommUtil.BSubstring(strRequest, 38, 10).trim();	

		if(Integer.parseInt(Dev_Deal) == Cmd_Sta.CMD_SUBMIT_0001)
		{
			Act_Value = "";
		}
		else if(Integer.parseInt(Dev_Deal) == Cmd_Sta.CMD_SUBMIT_0002)
		{
			BTime = CommUtil.getTime();
			Act_Value = BTime;
		}
		//byte[] byteData = new byte[2];
		//byteData[0] = strData[85];
		//byteData[1] = strData[86];
	}

	@Override
	public int execRequest(TcpSvrBase tcpSvr)
	{
		// TODO Auto-generated method stub
		int ret = Cmd_Sta.STA_ERROR;
		if(!Dev_Status.equalsIgnoreCase("NULL") && Dev_Status.length() > 0)
		{
			DeliverData = this.getReserve() +			//保留字
						  Dev_Status +					//命令发送状态
						  Dev_Deal +					//处理指令
						  Dev_Id +						//DTU的ID
						  Act_Id +						//发送的指令
						  Oprator +						//操作用户
						  Act_Value;					//指令内容
			ret = Cmd_Sta.COMM_DELIVER;
		}
		
		System.out.println("DeliverData["+DeliverData+"]");
		
		
		
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

	public String getDev_Status()
	{
		return Dev_Status;
	}

	public void setDev_Status(String dev_Status)
	{
		Dev_Status = dev_Status;
	}

	public String getDev_Deal()
	{
		return Dev_Deal;
	}

	public void setDev_Deal(String dev_Deal)
	{
		Dev_Deal = dev_Deal;
	}

	public String getAct_Id()
	{
		return Act_Id;
	}

	public void setAct_Id(String act_Id)
	{
		Act_Id = act_Id;
	}

	public String getAct_Value()
	{
		return Act_Value;
	}

	public void setAct_Value(String act_Value)
	{
		Act_Value = act_Value;
	}

	public String getDeliverData()
	{
		return DeliverData;
	}

	public void setDeliverData(String deliverData)
	{
		DeliverData = deliverData;
	}

	public String getOprator()
	{
		return Oprator;
	}

	public void setOprator(String oprator)
	{
		Oprator = oprator;
	}

	
}