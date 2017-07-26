package util;
import java.io.Serializable;

/** 封装了查询数据库的返回值到一个对象中去, 和封装了返回结果类型 的MagBean ,
 *  MsgBean implements Serializable
 * @author Cui
 * 消息bean
 */
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
	
	public static final int CONST_PAGE_SIZE				    = 25;  
	 
	//====================系统状态=================================	
	public static final int STA_SUCCESS						= 0000;	//成功
	public static final int STA_DEAL_SUBMIT_SUCCESS			= 3000;	//提交成功
	public static final int STA_NET_ERROR					= 9993;	//网络故障
	public static final int STA_FAILED						= 9999;	//失败	
	public static final int STA_ACCOUNT_NOT_EXIST			= 1001; //用户名不存在
	public static final int STA_ACCOUNT_PWD_ERROR			= 1002;	//密码错误
	public static final int STA_CHECK_CODE_ERROR			= 1003;	//验证码错误
	public static final int STA_ACCOUNT_OTP_ERROR			= 1004;	//动态密码错误

	
	/**根据传入的 int pStatus 获取相应的中文状态名称    
	 * @param pStatus
	 *      一个四位数字  表示  状态  0000:成功 , 3000:提交成功 , 9999:失败 等等.
	 * @return 一个中文字符串: 系统状态名称
	 */
	public static String GetResult(int pStatus)
	{
		String RetVal = "";
		switch(pStatus)
		{
			//系统状态	
			case STA_SUCCESS: 
			 	RetVal = "成功";
			 	break;
			case STA_FAILED: 
			 	RetVal = "失败";	
			 	break;
			case STA_ACCOUNT_NOT_EXIST:
				RetVal = "用户名不存在";
				break;
			case STA_ACCOUNT_PWD_ERROR:
				RetVal = "密码错误";
				break;				
			case STA_CHECK_CODE_ERROR:
				RetVal = "验证码错误";
			    break;
			case STA_ACCOUNT_OTP_ERROR:
				RetVal = "动态密码错误";
				break;
			case STA_DEAL_SUBMIT_SUCCESS:
				RetVal = "提交成功";
				break;
			default:
				RetVal = "系统错误";
				break;
		}
		return RetVal;
	}
	

	/**根据传入的 int pStatus 获取相应的中文状态名称    
	 * @param pStatus
	 *      一个四位数字  表示  状态  0000:成功 , 3000:提交成功 , 9999:失败 等等.
	 * @return 一个中文字符串: 系统状态名称
	 */
	public static String GetNetResult(String pStatus)
	{
		String RetVal = "";
		switch(Integer.parseInt(pStatus))
		{
			//系统状态	
			case STA_SUCCESS: 
			 	RetVal = "成功";
			 	break;
			case STA_FAILED: 
			 	RetVal = "失败";	
			 	break;
			case STA_ACCOUNT_NOT_EXIST:
				RetVal = "用户名不存在";
				break;
			case STA_ACCOUNT_PWD_ERROR:
				RetVal = "密码错误";
				break;				
			case STA_CHECK_CODE_ERROR:
				RetVal = "验证码错误";
			    break;
			case STA_ACCOUNT_OTP_ERROR:
				RetVal = "动态密码错误";
				break;
			default:
				RetVal = "系统错误";
				break;
		}
		return RetVal;
	}
	
	private int status = STA_SUCCESS;
	private Object msg;
	private int count = 0;	
	
	/**
	 * 空参构造器
	 */
	public MsgBean() {
		
	}
	
	/** 带有三个参数的构造器
	 * @param pStatus
	 *    一个四位数字  表示  状态  0000:成功 , 3000:提交成功 , 9999:失败 等等.
	 * @param pMsg
	 *    一个Object 对象 
	 * @param pCount
	 *    一个记录数值(数据库????)
	 */
	public MsgBean(int pStatus, Object pMsg, int pCount) {
		status = pStatus;
		msg    = pMsg;
		count  = pCount;
	}
	
	
	public Object getMsg() {
		return msg;
	}
	
	public int getStatus() {
		return status;
	}
	
	public int getCount() {
		return count;
	}
}