package bean;

import net.MsgCtrl;
import util.CmdUtil;
import util.Cmd_Sta;
import util.CommUtil;
public class DTUActionBean extends BaseCmdBean {
	private String ClientKey = "";		// DTU��id
	private String SN = "";				// ���ʹ���
	private String Function = "";		// ����
	
	public DTUActionBean(int action, String seq) {
		super(action, seq);
		setBeanName("DTUActionBean");
	}

	@Override
	public void parseReqest(String srcKey, String strRequest, byte[] strData, MsgCtrl msgCtrl) {
		// TODO Auto-generated method stub
		this.setActionSource(srcKey);
		this.setReserve(strRequest.substring(0, 20));
		SN = CommUtil.BSubstring(strRequest, 28, 8).trim();
		ClientKey = CommUtil.BSubstring(strRequest, 36, 10).trim();
		Function = CommUtil.BSubstring(strRequest, 46, 2).trim();
		System.out.println("SN["+SN+"]");
		System.out.println("ClientKey["+ClientKey+"]");
		System.out.println("Function["+Function+"]");
	}

	@Override
	public int execRequest(MsgCtrl m_MsgCtrl)
	{
		// TODO Auto-generated method stub
		System.out.println("Function["+Function+"]");
		int ret = Cmd_Sta.STA_SUCCESS;
		if(Function.equals("01")){	// �������͵���
			m_MsgCtrl.getM_TcpSvr().dveiceTimedTask.collectDataNow(SN);
		}else if(Function.equals("02")){	// �������͵�ǰDTU��ȫ�� 
			m_MsgCtrl.getM_TcpSvr().dveiceTimedTask.collectDataNowAll(ClientKey);
		}else if(Function.equals("03")){	// �򿪵�����ʱ����
			m_MsgCtrl.getM_TcpSvr().dveiceTimedTask.openOneTimedTask(SN);
		}else if(Function.equals("04")){	// �رյ�����ʱ����
			m_MsgCtrl.getM_TcpSvr().dveiceTimedTask.closeOneTimedTask(SN);
		}else if(Function.equals("05")){	// ��DTU��ȫ����ʱ����
			m_MsgCtrl.getM_TcpSvr().dveiceTimedTask.openTimedTask(ClientKey);
		}else if(Function.equals("06")){	// �ر�DTU��ȫ����ʱ����
			m_MsgCtrl.getM_TcpSvr().dveiceTimedTask.closeTimedTask(ClientKey);
		}else if(Function.equals("10")){	// ���½���client
			m_MsgCtrl.getM_TcpSvr().ClientClose(ClientKey);
		}else{
			ret = Cmd_Sta.STA_ERROR;
		}
		
		return ret;
	}

	private String EncodeSendMsg()
	{
		String ret = null;
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

	public String getClientKey() {
		return ClientKey;
	}

	public void setClientKey(String clientKey) {
		ClientKey = clientKey;
	}

	public String getSN() {
		return SN;
	}

	public void setSN(String sN) {
		SN = sN;
	}

	public String getFunction() {
		return Function;
	}

	public void setFunction(String function) {
		Function = function;
	}

}