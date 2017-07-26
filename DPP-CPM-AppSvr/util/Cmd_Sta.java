package util;
public class Cmd_Sta 
{
//	==================通信========================================================
	public static final int COMM_LOGON 					= 0x00000001;	//请求登陆
	public static final int COMM_LOGOUT 				= 0x00000002;	//终止登陆
	public static final int COMM_ACTIVE_TEST 			= 0x00000003;	//链接测试
	public static final int COMM_SUBMMIT 				= 0x00000004;	//客户端提交
	public static final int COMM_DELIVER 				= 0x00000005;	//服务器派发
	public static final int COMM_RESP 					= 0x80000000; 	//回应标记

//	==================常量定义=====================================================	
	public static final int CONST_MAX_BUFF_SIZE = 2048; //接收缓存的最大数
	public static final int CONST_MSGHDRLEN     = 20;   //包头长度
	public static final int CONST_TEST_START    = 3;    //测试包测试开始次数
	public static final int CONST_TEST_END      = 6;    //测试包测试结束次数
	
//	==================DeCode函数的返回值===========================================	
	public static final byte CODEC_CMD       = 0;
	public static final byte CODEC_RESP      = 1;
	public static final byte CODEC_TRANS     = 2;
	public static final byte CODEC_NEED_DATA = 3;
	public static final byte CODEC_ERR       = 4;

//	==========================AppSvr ~ CPM========================================
	public static final int CMD_SUBMIT_1000	 				= 1000;
	public static final int CMD_SUBMIT_1001	 				= 1001;
	public static final int CMD_SUBMIT_1003	 				= 1003;
	public static final int CMD_SUBMIT_1004	 				= 1004;
	public static final int CMD_SUBMIT_1011	 				= 1011;

//	==========================AppSvr ~ CPM========================================
	public static final String DATA_1011_20                 = "0321020001";
	public static final String DATA_1011_21                 = "0321010001";
	public static final String DATA_1011_22                 = "0321030001";
	public static final String DATA_1011_23                 = "0321040001";
	public static final String DATA_1011_24                 = "0321050001";
	public static final String DATA_1011_25                 = "0321060001";
	
//	==================系统状态=====================================================
	public static final int STA_SUCCESS						= 0000;	//成功
	public static final int STA_ERROR						= 3006;	//失败
	public static final int STA_NET_ERROR					= 9993;	//网络故障
	public static final int STA_SYSTEM_BUSY					= 9994;	//系统忙
	public static final int STA_DATA_FORMAT_ERROR			= 9995;	//格式错误
	public static final int STA_UNKHOWN_DEAL_TYPE			= 9996;	//未知业务类型	
	public static final int STA_DB_ERROR					= 9997; //数据库出错	
	public static final int STA_SYSTEM_ERROR				= 9998;	//系统忙	
	public static final int STA_UNKHOWN_ERROR				= 9999;	//未知错误
	public static final int STA_DEAL_SUBMIT_SUCCESS			= 3000;	//提交成功
	public static final int STA_DEAL_SEND_SUCCESS			= 3001;	//发送成功
	
	public static String GetErrorMsg(String strCode)
	{
		String RetVal = "";
		int Code = Integer.parseInt(strCode);
		switch(Code)
		{
			case STA_SUCCESS: 
			 	RetVal = "成功";	
			 	break;
			case STA_NET_ERROR:
				RetVal = "网络故障";
				break;
			case STA_SYSTEM_BUSY:
				RetVal = "系统忙";
				break;	
			case STA_DATA_FORMAT_ERROR:
				RetVal = "格式错误";
				break;
			case STA_UNKHOWN_DEAL_TYPE:
				RetVal = "未知业务类型";
				break;
			case STA_DB_ERROR:
				RetVal = "数据库出错";
				break;
			case STA_SYSTEM_ERROR:
				RetVal = "系统忙";
				break;
			case STA_UNKHOWN_ERROR:
				RetVal = "未知错误";
				break;
			case STA_DEAL_SUBMIT_SUCCESS:
				RetVal = "提交成功";	
				break;
			default:
				RetVal = "未知错误";
				break;
		}
		return RetVal;
	}
}