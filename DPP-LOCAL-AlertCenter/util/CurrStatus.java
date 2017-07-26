package util;

public class CurrStatus {
	//==============================主功能===============================================
	public static final int FUNC_SP_INFO 				= 1001;  //网点信息
	public static final int FUNC_BANK_INFO 				= 1002;  //银行信息
	public static final int FUNC_RECHARGE_INFO			= 1003;  //充值信息
	public static final int FUNC_REMIT_INFO 			= 1004;  //来往帐目	
	public static final int FUNC_SIM_INFO				= 1005;  //SIM信息
	public static final int FUNC_ISSUE_INFO 			= 1006;  //投诉信息
	public static final int FUNC_SMS_INFO 				= 1007;  //投诉信息
	public static final int FUNC_EMPLOYEE_INFO          = 1008;  //管理员信息
	public static final int FUNC_INFORMATION            = 1009;  //页面通知
	
	public static final int FUNC_SIM_MANAGE 			= 2001;  //SIM管理
	public static final int FUNC_TEL_MANAGE				= 2002;  //电话导入
	public static final int FUNC_SIM_DISPATCH			= 2003;  //SIM派发
	public static final int FUNC_SIM_REFUND				= 2004;  //SIM退货	
	public static final int FUNC_PKG_MANAGE	 			= 2005;  //套餐信息	
	public static final int FUNC_TEL_READY 				= 3001;  //开通号码
	public static final int FUNC_REMIT_INFO_EDIT		= 3002;  //往来帐目
	public static final int FUNC_RATIO_INFO_EDIT        = 3003;  //费率管理
	public static final int FUNC_SENDMESSAGE            = 3004;  //短信群发
	public static final int FUNC_CORRECT_INFO_EDIT      = 3005;  //联通冲正
	public static final int FUNC_LEDGER_DAY 			= 4001;  //日总帐信息
	public static final int FUNC_LEDGER_MON	 			= 4002;  //月总帐信息
	public static final int FUNC_LEDGER_INTERVAL		= 4003;  //不定期总帐信息
//	==============================子功能===============================================
	public static final int FUNC_SUB_SP_INFO_ALL		= 0;  //全部网点
	public static final int FUNC_SUB_SP_INFO_OK			= 1;  //正常网点
	public static final int FUNC_SUB_SP_INFO_CANCEL		= 2;  //正常网点
	
	
	private int Cmd;
	private String Zone_Id;
	private int Func_Id;	
	private int Func_Sub_Id;
	private String Jsp;
	
	public int getFunc_Id() {
		return Func_Id;
	}
	public void setFunc_Id(int func_Id) {
		Func_Id = func_Id;
	}
	public int getFunc_Sub_Id() {
		return Func_Sub_Id;
	}
	public void setFunc_Sub_Id(int func_Sub_Id) {
		Func_Sub_Id = func_Sub_Id;
	}
	public String getZone_Id() {
		return Zone_Id;
	}
	public void setZone_Id(String zone_Id) {
		Zone_Id = zone_Id;
	}
	public int getCmd() {
		return Cmd;
	}
	public void setCmd(int cmd) {
		Cmd = cmd;
	}
	public String getJsp() {
		return Jsp;
	}
	public void setJsp(String jsp) {
		Jsp = jsp;
	}


	

}
