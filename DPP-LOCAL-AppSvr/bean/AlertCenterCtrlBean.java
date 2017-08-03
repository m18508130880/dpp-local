package bean;

import net.MsgCtrl;
import bean.BaseCmdBean;
import util.CmdUtil;
import util.Cmd_Sta;
import util.CommUtil;
public class AlertCenterCtrlBean extends BaseCmdBean {
	
	private String Dev_Id = "";			// DTU的ID
	private String Dev_Status = "";		// 命令发送状态
	private String Dev_Deal = "";		// 处理指令
	
	public AlertCenterCtrlBean(int action, String seq) {
		super(action, seq);
		setBeanName("DTURestartBean");
	}

	@Override
	public void parseReqest(String srcKey, String strRequest, byte[] strData) {
		// TODO Auto-generated method stub
		this.setActionSource(srcKey);
		this.setReserve(strRequest.substring(0, 20));
		Dev_Id        = CommUtil.BSubstring(strRequest, 28, 10).trim();
		Dev_Status    = CommUtil.BSubstring(strRequest, 20, 4).trim();
		Dev_Deal      = CommUtil.BSubstring(strRequest, 24, 4).trim();
		
	}

	@Override
	public int execRequest(MsgCtrl m_MsgCtrl)
	{
		// TODO Auto-generated method stub
		int ret = Cmd_Sta.STA_ERROR;
		if(!Dev_Status.equalsIgnoreCase("NULL") && Dev_Status.length() > 0)
		{

		}
		if(m_MsgCtrl.getM_TcpSvr().DisPatch(Cmd_Sta.COMM_DELIVER, CommUtil.StrBRightFillSpace(Dev_Id, 20), EncodeSendMsg()))
		{
			ret = Cmd_Sta.STA_SUCCESS;
		}
		
		//回复
		//setStatus(CommUtil.IntToStringLeftFillZero(ret, 4));
		//execResponse();
		return ret;
	}
	@SuppressWarnings("unused")
	private String EncodeSendMsg()
	{
		String ret = null;
		ret = CommUtil.StrBRightFillSpace(getReserve(), 20);		//保留字
		ret += CommUtil.StrBRightFillSpace(getDev_Status(), 4);		//命令发送状态
		ret += CommUtil.StrBRightFillSpace(getDev_Deal(), 4);		//处理指令
		ret += CommUtil.StrBRightFillSpace(getDev_Id(), 10);		//DTU的ID
		return ret;
	}
	public void noticeTimeOut()
	{
		
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
}