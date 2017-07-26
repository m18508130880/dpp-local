package util;
public class CmdUtil
{
//	==================通信========================================================
	public static final int COMM_LOGON 					= 0x00000001;	//请求登陆
	public static final int COMM_LOGOUT 				= 0x00000002;	//终止登陆
	public static final int COMM_ACTIVE_TEST 			= 0x00000003;	//链接测试
	public static final int COMM_SUBMMIT 				= 0x00000004;	//客户端提交
	public static final int COMM_DELIVER 				= 0x00000005;	//服务器派发
	public static final int COMM_RESP 					= 0x80000000; 	//回应标记

	
//	==================常量定义=====================================================	
	public static final int RECV_BUFFER_SIZE  = 2048; //接收缓存的最大数
	public static final int MSGHDRLEN         = 20;   //包头长度
	public static final int ACTIVE_TEST_START = 3;    //测试包测试开始次数
	public static final int ACTIVE_TEST_END   = 6;    //测试包测试结束次数
	
//	==================DeCode函数的返回值===========================================	
	public static final byte CODEC_CMD       = 0;
	public static final byte CODEC_RESP      = 1;
	public static final byte CODEC_TRANS     = 2;
	public static final byte CODEC_NEED_DATA = 3;
	public static final byte CODEC_ERR       = 4;
}
