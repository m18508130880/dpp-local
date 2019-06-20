package bean;

import java.util.ArrayList;
import java.util.Hashtable;

import net.AlertCtrl;
import bean.BaseCmdBean;
import util.Cmd_Sta;
import util.CommUtil;
public class UpdateDataBean extends BaseCmdBean {	
	
	private String Dev_Id = "";			// DTU的ID
	private String Dev_Status = "";		// 命令发送状态
	private String Dev_Deal = "";		// 处理指令
	ArrayList<DevGJAlertBean> gjList = null;
	ArrayList<DevGXAlertBean> gxList = null;
	@SuppressWarnings("rawtypes")
	public Hashtable<String, ArrayList> objGJGXTable = null;
	
	public UpdateDataBean(int action, String seq) {
		super(action, seq);
		setBeanName("UpdateDataBean");
		setDev_Deal("2001");
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
		
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int execRequest(AlertCtrl alertCtrl)
	{
		// TODO Auto-generated method stub
		System.out.println("\n加载管井管线数据....");
		int ret = Cmd_Sta.STA_ERROR;
		if(Integer.valueOf(Dev_Deal) == Cmd_Sta.CMD_SUBMIT_2001)
		{
			objGJGXTable = new Hashtable<String, ArrayList>();
			String Sql = "";
			try
			{
				Sql = " SELECT project_id, LEFT(id,5) FROM dev_gj WHERE LENGTH(equip_id) > 1 GROUP BY Project_Id, LEFT(id,5)";
				String[] Project_SysId = alertCtrl.getM_DBUtil().doSelectStr(Sql, 2).split(";");
				for(int i = 0; i < Project_SysId.length; i ++)
				{
					String project_Id = Project_SysId[i].split(",")[0];
					String SysId = Project_SysId[i].split(",")[1];
					Sql = " SELECT id, in_id, out_id, curr_data, flag, top_height, base_height, equip_height FROM view_dev_gj WHERE Project_Id = '"+project_Id+"' AND id LIKE '"+SysId+"%' GROUP BY id";
					gjList = (ArrayList<DevGJAlertBean>) alertCtrl.getM_DBUtil().doSelect(Sql, 1);
					objGJGXTable.put(project_Id + SysId, gjList);
					SysId = dealGXID(SysId);
					Sql = " SELECT id, length, end_id, start_height, end_height, diameter FROM dev_gx WHERE Project_Id = '"+project_Id+"' AND id LIKE '"+SysId+"%' GROUP BY id";
					gxList = (ArrayList<DevGXAlertBean>) alertCtrl.getM_DBUtil().doSelect(Sql, 2);
					objGJGXTable.put(project_Id + SysId, gxList);
				}
				ret = Cmd_Sta.STA_SUCCESS;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		System.out.println("加载成功!\n");
		return ret;
	}
	
	/**
	 * 管线命名
	 * 
	 * @param GJ_Id
	 * @return
	 */
	public String dealGXID(String GJ_Id)
	{
		String temGJ_Id = ""; // "WJ", "WG" "YJ", "YG"
		if (GJ_Id.contains("WJ"))
		{
			temGJ_Id = GJ_Id.replace("WJ", "WG");
		}
		if (GJ_Id.contains("YJ"))
		{
			temGJ_Id = GJ_Id.replace("YJ", "YG");
		}
		return temGJ_Id;
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
		}
	}	

	private String EncodeRespMsg()
	{
		String ret = null;
		ret = getStatus() + getAction();
		return ret;
	}

	public String getDev_Id()
	{
		return Dev_Id;
	}

	public void setDev_Id(String dev_Id)
	{
		Dev_Id = dev_Id;
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

	public Hashtable<String, ArrayList> getObjGJGXTable()
	{
		return objGJGXTable;
	}

	public void setObjGJGXTable(Hashtable<String, ArrayList> objGJGXTable)
	{
		this.objGJGXTable = objGJGXTable;
	}

}