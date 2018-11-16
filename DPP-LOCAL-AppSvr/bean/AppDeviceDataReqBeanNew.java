package bean;

import java.math.BigInteger;

import net.MsgCtrl;
import util.CmdUtil;
import util.Cmd_Sta;
import util.CommUtil;
public class AppDeviceDataReqBeanNew extends BaseCmdBean {	
	
	private String Dev_Id = "";
	//private String Dev_Name = ""; 
	private String Dev_Attr_Id = "0001";
	//private String Dev_Attr_Name = "";
	private String Dev_CTime = "";
	private String Dev_CData = "";             
	private String Dev_RealData = "";
	private boolean isError = false;
	
	private String Dev_Unit = "";
	public AppDeviceDataReqBeanNew(int action, String seq) {
		super(action, seq);
		setBeanName("AppDeviceDataReqBeanNew");
	}

	@Override
	public void parseReqest(String srcKey, String strRequest, byte[] strData) {
		// TODO Auto-generated method stub
		byte[] byte01 = new byte[1];
		byte01[0] = strData[78];
		byte[] byte02 = new byte[1];
		byte02[0] = strData[79];
		byte[] byte03 = new byte[1];
		byte03[0] = strData[80];
		int addr = Integer.parseInt(CommUtil.BytesToHexString(byte01, 1), 16);
		int gnm = Integer.parseInt(CommUtil.BytesToHexString(byte02, 1), 16);
		int length = Integer.parseInt(CommUtil.BytesToHexString(byte03, 1), 16);
		if(addr != 1 || gnm != 3){
			isError = true;
		}

		//String strCData = CommUtil.BytesToHexString(CommUtil.BSubstring(strRequest, 115, 2).getBytes(), 2);	
		this.setActionSource(srcKey);
		this.setReserve(strRequest.substring(0, 20));
		this.setAction(Integer.parseInt(strRequest.substring(24, 28)));
		Dev_Id        = CommUtil.BSubstring(strRequest, 28, 10).trim();
		Dev_CData     = CommUtil.BSubstring(strRequest, 38, 128).trim();
		Dev_CTime     = CommUtil.getDateTime();
		
		byte[] byteData = new byte[2];
		byteData[0] = strData[81];
		byteData[1] = strData[82];
		if((Dev_Id.substring(0,6) + Dev_Attr_Id).equals(Cmd_Sta.DATA_041101_0001))		//����Һλ������0.005
		{
			float fData = (float) (Integer.parseInt(CommUtil.BytesToHexString(byteData, 2), 16) * 0.005);
			Dev_RealData = String.valueOf(fData);
		}
		else if((Dev_Id.substring(0,6) + Dev_Attr_Id).equals(Cmd_Sta.DATA_041102_0001))		//����Һλ��ˮλ�߶ȣ�����0.0025
		{			
			float fData = (float) (Integer.parseInt(CommUtil.BytesToHexString(byteData, 2), 16) * 0.0025);
			Dev_RealData = String.valueOf(fData);
		}
		else if((Dev_Id.substring(0,6) + Dev_Attr_Id).equals(Cmd_Sta.DATA_041103_0001))		//����Һλ��ˮλ�߶ȣ�����0.003
		{		
			// 2�ֽ�����תfloat
			float fData = (float) (Integer.parseInt(CommUtil.BytesToHexString(byteData, 2), 16) * 0.003);
			Dev_RealData = String.valueOf(fData);
		}
		else if((Dev_Id.substring(0,6) + Dev_Attr_Id).equals(Cmd_Sta.DATA_051101_0001))		//������������
		{	
			/*�ۺϲ�ѯ
			01   ��ַ	  03 ������ 	1C   �ֽ�����
			00 00 00 00       ˮ��ֵ      ��λ��m
			00 00 00 00	    û��     
			00 00 00 00       û��
			00 00 00 00       û��  
			00 00 00 00       û��   
			00 00 00 00       �¶�˲ʱֵ  ��λ����
			00 00 00 00       ����˲ʱֵ  ��λ��m/s
			5C A5  У����*/
			byte[] waterLevByte = new byte[4];	//ˮ��
			byte[] tmpByte = new byte[4];		//�¶�
			byte[] velocityByte = new byte[4];	//����
			waterLevByte[0] = strData[81];
			waterLevByte[1] = strData[82];
			waterLevByte[2] = strData[83];
			waterLevByte[3] = strData[84];

			tmpByte[0] = strData[101];
			tmpByte[1] = strData[102];
			tmpByte[2] = strData[103];
			tmpByte[3] = strData[104];

			velocityByte[0] = strData[105];
			velocityByte[1] = strData[106];
			velocityByte[2] = strData[107];
			velocityByte[3] = strData[108];
			
			/*byte[] bData = new byte[4];
			bData[0] = strData[155];
			bData[1] = strData[156];
			bData[2] = strData[157];
			bData[3] = strData[158];*/
			// 4�ֽڵ�����תfloat
			//float fData = Float.intBitsToFloat(Integer.parseInt(CommUtil.BytesToHexString(bData, 4),16));
			float waterLevData = Float.intBitsToFloat(Integer.parseInt(CommUtil.BytesToHexString(waterLevByte, 4),16));
			float tmpData = Float.intBitsToFloat(Integer.parseInt(CommUtil.BytesToHexString(tmpByte, 4),16));
			BigInteger velocityDataBi = new BigInteger(CommUtil.BytesToHexString(velocityByte, 4),16);
			float velocityData = Float.intBitsToFloat(velocityDataBi.intValue());
			//new java.text.DecimalFormat("#.00").format(3.1415926);
			String strTmp = new java.text.DecimalFormat("#.##").format(tmpData);
			String strWaterlev = new java.text.DecimalFormat("#.####").format(waterLevData);
			String strVeloc = new java.text.DecimalFormat("#.####").format(velocityData);
			String fData = strTmp + " " + strWaterlev + " " +strVeloc;
			CommUtil.LOG("fData[" + fData + "]");
			Dev_CData = fData;
			Dev_RealData = "0.0";
		}
		else
		{
			Dev_RealData = Dev_CData;
		}
		//����ˮλֵ�ж�,ˮλ������Ϊ0-5m
		if(Double.valueOf(Dev_RealData) < 0.0 || Double.valueOf(Dev_RealData) > 5.0)
		{
			Dev_RealData = "0.0";
		}
		
		Dev_Unit      = CommUtil.BSubstring(strRequest, 240, 10).trim();
	}

	@Override
	public int execRequest(MsgCtrl m_MsgCtrl)
	{
		int ret = Cmd_Sta.STA_ERROR;
		if(isError){
			m_MsgCtrl.getM_TcpSvr().dveiceTimedTask.collectDataNowAll(Dev_Id);
			return ret;
		}
		// TODO Auto-generated method stub
		String Sql = "";
		String sendStr = "";
		if(!Dev_CData.equalsIgnoreCase("NULL") && Dev_CData.length() > 0)
		{
			if((Dev_Id.substring(0,6) + Dev_Attr_Id).equals(Cmd_Sta.DATA_041101_0001))		// ����Һλ������0.005
			{
				Sql = "insert into data(cpm_id, id, cname, attr_id, attr_name, ctime, value, unit)" +
						"values('"+ this.getActionSource().trim() +"', " +
			  	  	     "'"+ Dev_Id +"', " +
			  	  	     "'ˮλ��', " +
			  	  	     "'"+ Dev_Attr_Id +"', " +
			  	  	     "'ˮλ', " +
			  	  	     "date_format('"+ Dev_CTime +"', '%Y-%m-%d %H-%i-%S'), " +
			  	  	     "'"+ Dev_RealData +"', " +
			  	  	     "'m')";
			}
			else if((Dev_Id.substring(0,6) + Dev_Attr_Id).equals(Cmd_Sta.DATA_041102_0001))		// ����Һλ������0.0025
			{
				Sql = "insert into data(cpm_id, id, cname, attr_id, attr_name, ctime, value, unit)" +
						"values('"+ this.getActionSource().trim() +"', " +
			  	  	     "'"+ Dev_Id +"', " +
			  	  	     "'ˮλ��', " +
			  	  	     "'"+ Dev_Attr_Id +"', " +
			  	  	     "'ˮλ', " +
			  	  	     "date_format('"+ Dev_CTime +"', '%Y-%m-%d %H-%i-%S'), " +
			  	  	     "'"+ Dev_RealData +"', " +
			  	  	     "'m')";
			}
			else if((Dev_Id.substring(0,6) + Dev_Attr_Id).equals(Cmd_Sta.DATA_041103_0001))		// ����Һλ������0.0025
			{
				Sql = "insert into data(cpm_id, id, cname, attr_id, attr_name, ctime, value, unit)" +
						"values('"+ this.getActionSource().trim() +"', " +
			  	  	     "'"+ Dev_Id +"', " +
			  	  	     "'ˮλ��', " +
			  	  	     "'"+ Dev_Attr_Id +"', " +
			  	  	     "'ˮλ', " +
			  	  	     "date_format('"+ Dev_CTime +"', '%Y-%m-%d %H-%i-%S'), " +
			  	  	     "'"+ Dev_RealData +"', " +
			  	  	     "'m')";
			}
			else if((Dev_Id.substring(0,6) + Dev_Attr_Id).equals(Cmd_Sta.DATA_051101_0001))		// ������
			{
				Sql = "insert into data_dpl(cpm_id, id, cname, attr_id, attr_name, ctime, value, unit)" +
						"values('"+ this.getActionSource().trim() +"', " +
			  	  	     "'"+ Dev_Id +"', " +
			  	  	     "'������', " +
			  	  	     "'1002', " +
			  	  	     "'�¶� ˮλ ���� ����', " +
			  	  	     "date_format('"+ Dev_CTime +"', '%Y-%m-%d %H-%i-%S'), " +
			  	  	     "'"+ Dev_CData +"', " +
			  	  	     "'�� m m/s m/s')";
			}
			else
			{
				Sql = "insert into data(cpm_id, id, cname, attr_id, attr_name, ctime, value, unit)" +
					  	  "values('"+ this.getActionSource().trim() +"', " +
					  	  	     "'"+ Dev_Id +"', " +
					  	  	     "'', " +
					  	  	     "'"+ Dev_Attr_Id +"', " +
					  	  	     "'', " +
					  	  	     "date_format('"+ Dev_CTime +"', '%Y-%m-%d %H-%i-%S'), " +
					  	  	     "'"+ Dev_CData +"', " +
					  	  	     "'"+ Dev_Unit +"')";
			}
			System.out.println("Sql["+Sql+"]");
			if((Dev_Id.substring(0,6) + Dev_Attr_Id).equals(Cmd_Sta.DATA_051101_0001)){
				m_MsgCtrl.getM_DBUtil().doUpdate(Sql);
			}else{
				if(m_MsgCtrl.getM_DBUtil().doUpdate(Sql))
				{
					ret = Cmd_Sta.STA_SUCCESS;
					String Project_IdAndGJ_Id = m_MsgCtrl.getM_DBUtil().GetProject_IdAndGJ_Id(this.getActionSource().trim(),Dev_Id);
					sendStr = CommUtil.StrBRightFillSpace("", 20);									//������
					sendStr += CommUtil.StrBRightFillSpace("0000", 4);								//�����״̬
					sendStr += CommUtil.StrBRightFillSpace(Cmd_Sta.CMD_SUBMIT_2002 + "", 4);		//����ָ��
					sendStr += CommUtil.StrBRightFillSpace(Project_IdAndGJ_Id, 10);					//��Ŀ����ϵͳID
					m_MsgCtrl.getM_TcpSvr().DisPatch(Cmd_Sta.COMM_DELIVER, CommUtil.StrBRightFillSpace(Cmd_Sta.DATA_00000_0001, 20), sendStr);
					
				}
			}
			return ret;
		}
		
		//�ظ�
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