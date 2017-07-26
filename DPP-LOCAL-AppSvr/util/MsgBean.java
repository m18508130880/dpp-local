package util;
import java.io.Serializable;

public class MsgBean implements Serializable 
{
	public final static long serialVersionUID = 10;
	public static String ClassName = "MsgBean";
	public long getClassId()
	{
		return serialVersionUID;
	}
	public String getClassName()
	{
		return ClassName;
	}
	public static final int CONST_PAGE_SIZE				= 20;
	
//	====================系统状态	
	public static final int STA_SUCCESS						= 0000;	//成功	
	public static final int STA_DB_NO_DATA					= 0002; //数据库出错
	public static final int STA_DB_ERROR					= 0003; //数据库出错
	public static final int STA_SUBMIT_SUCCESS				= 1000;	//提交成功
	
	public static final int STA_FAILED						= 3006;	//失败
//====================其它	
	public static final int STA_MD5_CODE_ERROR				= 6001;	//校验码错误	
	public static final int STA_OLD_PASSWORD_ERROR			= 6002;	//原密码错误		
	public static final int STA_NET_ERROR					= 6003;	//网络故障	
	public static final int STA_UNKHOWN_ERROR				= 9999;	//网未知错误故障
	
	public static String GetErrorMsg(String strCode)
	{
		String RetVal = "";
		int Code = Integer.parseInt(strCode);
		switch(Code)
		{
			case STA_SUBMIT_SUCCESS:
				RetVal = "提交成功";
				break;
			case STA_SUCCESS:
				RetVal = "成功";
				break;
			case STA_FAILED:
				RetVal = "失败";
				break;
			case STA_MD5_CODE_ERROR:
				RetVal = "校验码错误";
				break;
			case STA_OLD_PASSWORD_ERROR:
				RetVal = "原密码错误"; 
				break;
			case STA_NET_ERROR:
				RetVal = "网络故障";
				break;		
			case STA_DB_ERROR:
				RetVal = "数据库操作错误";
                break;
			case STA_UNKHOWN_ERROR:
			default:
				RetVal = "未知错误";
				break;
		}
		return RetVal;
	}
	
	private int status = STA_SUCCESS;
	private Object msg;
	private int count = 0;	
	
	public MsgBean(int pStatus, Object pMsg) 
	{
		status = pStatus;
		msg = pMsg;
	}
	public MsgBean() 
	{
	}
	public Object getMsg() 
	{
		return msg;
	}
	public int getStatus() {
		return status;
	}
	public int getCount() {
		return count;
	}
}