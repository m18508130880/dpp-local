package bean;

import java.util.ArrayList;

import net.MsgCtrl;
import util.CmdUtil;
import util.Cmd_Sta;
import util.CommUtil;
public class AppDeviceDataReqBeanNew extends BaseCmdBean {	
	
	private String Dev_Id = "";
	private String Dev_Name = "";
	private String Dev_Attr_Id = "";
	private String Dev_Attr_Name = "";
	private String Dev_CTime = "";
	private String Dev_CData = "";             
	private String Dev_RealData = "";
	ArrayList<MacAnalysisBean> analysisList = null;
	ArrayList<MacReadTaskBean> readTaskList = null;
	ArrayList<MacReadBean> readList = null;
	private boolean isError = false;
	private boolean isSZ = false;
	
	private String Dev_Unit = "";
	private String Addrs = "";
	private String Code = "";
	private String Sign = "";
	public AppDeviceDataReqBeanNew(int action, String seq) {
		super(action, seq);
		setBeanName("AppDeviceDataReqBeanNew");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void parseReqest(String srcKey, String strRequest, byte[] strData, MsgCtrl m_MsgCtrl) {
		// TODO Auto-generated method stub
		//String strCData = CommUtil.BytesToHexString(CommUtil.BSubstring(strRequest, 115, 2).getBytes(), 2);	
		analysisList = new ArrayList<MacAnalysisBean>();
		String data = String.valueOf(CommUtil.BytesToHexString(strData, strData.length)).substring(80);
		this.setActionSource(srcKey);
		this.setReserve(data.substring(0, 20*2));
		this.setAction(Integer.parseInt(data.substring(24*2, 28*2)));
		Dev_Id        = CommUtil.hexToString(CommUtil.BSubstring(data, 28*2, 10*2).trim());
		Dev_CData     = CommUtil.BSubstring(data, 38*2, 128*2).trim();
		Dev_CTime     = CommUtil.getDateTime();

		Addrs = Dev_CData.substring(0, 2).toUpperCase();
		Code = Dev_CData.substring(2, 4).toUpperCase();
		Sign = Dev_CData.substring(4, 6).toUpperCase();
		
		// 查找到PId对应的接收任务
		MacReadTaskBean readTaskBean = new MacReadTaskBean();
		readTaskBean.setPId(Dev_Id);
		readTaskList = (ArrayList<MacReadTaskBean>) m_MsgCtrl.getM_DBUtil().
				doSelect(readTaskBean.getSql(0), 3);
		// 查找到的接收任务至少有一个
		if(readTaskList != null && readTaskList.size() > 0){
			for(int i = 0; i < readTaskList.size(); i ++){
				MacReadTaskBean taskBean = readTaskList.get(i);
				MacReadBean readBean = new MacReadBean();
				readBean.setSN(taskBean.getRead());
				readList = (ArrayList<MacReadBean>) m_MsgCtrl.getM_DBUtil().
						doSelect(readBean.getSql(0), 2);
				if(readList.size() > 0){
					// 根据接收任务的Addrs、Code、Sign，查找到对应的分析
					for(int k = 0; k < readList.size(); k ++){
						MacReadBean read = readList.get(k);
						if(read.getAddrs().toUpperCase().equals(Addrs) 
								&& read.getCode().toUpperCase().equals(Code) 
								&& read.getSign().toUpperCase().equals(Sign)){						
							Dev_Name = read.getCName();
							String [] Analysis = read.getAnalysis().split(",");
							for(int j = 0; j < Analysis.length; j ++){
								MacAnalysisBean analysisBean = new MacAnalysisBean();
								analysisBean.setSN(Analysis[j]);
								MacAnalysisBean aBean =  (MacAnalysisBean) m_MsgCtrl.getM_DBUtil().
										doSelect(analysisBean.getSql(0), 1).get(0);
								analysisList.add(aBean);
							}
						}
					}
				}
			}
		}
		if(analysisList != null && analysisList.size() > 0){
			for(int i = 0; i < analysisList.size(); i ++){
				MacAnalysisBean analysisBean = analysisList.get(i);
				int Addrs_S = Integer.valueOf(analysisBean.getAddrs_S());
				int Addrs_E = Integer.valueOf(analysisBean.getAddrs_E());
				String val = Dev_CData.substring(Addrs_S * 2, Addrs_E * 2);
				val = val.replace(" ", "");
				// 大端 顺序读取
				float value = 0;
				if(analysisBean.getType().equals("1")){
					Long l = CommUtil.parseLong(val, 16);
					if(analysisBean.getFlow().equals("1")){
						value = l;
					}else if(analysisBean.getFlow().equals("2")){
						value = Float.intBitsToFloat(l.intValue());
					}else if(analysisBean.getFlow().equals("3")){
						//value = Float.intBitsToFloat(l.intValue());
					}
					// 小端 倒序读取
				}else if(analysisBean.getType().equals("2")){
					String val_ = "";
					for(int j = Addrs_E - Addrs_S; j > 0; j --){
						val_ += val.substring(2*(j-1), 2*j);
					}
					Long l = CommUtil.parseLong(val_, 16);
					if(analysisBean.getFlow().equals("1")){
						value = l;
					}else if(analysisBean.getFlow().equals("2")){
						value = Float.intBitsToFloat(l.intValue());
					}else if(analysisBean.getFlow().equals("3")){
						//value = Float.intBitsToFloat(l.intValue());
					}
				}
				double Amend = Double.valueOf(analysisBean.getAmend());
				Dev_RealData += new java.text.DecimalFormat("#.####").format(Amend*value) + " ";
				Dev_Attr_Name += analysisBean.getAttr_Name() + " ";
				Dev_Attr_Id += analysisBean.getAttr_Id() + " ";
				Dev_Unit += analysisBean.getUnit() + " ";
			}
		}else{
			//01 10 31 00 00 00 CE F5
			isError = true;
			if(!(Addrs.equals("01") && Code.equals("10") && Sign.equals("31"))){				
				isSZ = true;
			}
		}
	}

	@Override
	public int execRequest(MsgCtrl m_MsgCtrl)
	{
		int ret = Cmd_Sta.STA_ERROR;
		if(isError){
			sleep(2000);
			if(!isSZ){
				m_MsgCtrl.getM_TcpSvr().dveiceTimedTask.collectDataNowAll(Dev_Id);
			}
			return ret;
		}
		// TODO Auto-generated method stub
		String Sql = "";
		if(!Dev_CData.equalsIgnoreCase("NULL") && Dev_CData.length() > 0)
		{
			Sql = "insert into data(cpm_id, id, addrs, code, sign, cname, attr_id, attr_name, ctime, value, unit)" +
					"values('"+ this.getActionSource().trim() +"', " +
		  	  	     "'"+ Dev_Id +"', " +
		  	  	     "'"+ Addrs +"', " +
		  	  	     "'"+ Code +"', " +
		  	  	     "'"+ Sign +"', " +
		  	  	     "'" + Dev_Name + "', " +
		  	  	     "'"+ Dev_Attr_Id +"', " +
		  	  	     "'" + Dev_Attr_Name + "', " +
		  	  	     "date_format('"+ Dev_CTime +"', '%Y-%m-%d %H-%i-%S'), " +
		  	  	     "'"+ Dev_RealData +"', " +
		  	  	     "'" + Dev_Unit + "')";
//			System.out.println("Sql["+Sql+"]");

			String sendStr = "";
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
			return ret;
		}
		
		//回复
		//setStatus(CommUtil.IntToStringLeftFillZero(ret, 4));
		//execResponse();
		return ret;
	}
	
	private void sleep(int i) {
		// TODO Auto-generated method stub
		
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
	
	public String getDev_Attr_Id() {
		return Dev_Attr_Id;
	}

	public void setDev_Attr_Id(String devAttrId) {
		Dev_Attr_Id = devAttrId;
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