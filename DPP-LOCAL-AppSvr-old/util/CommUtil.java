package util;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;
public class CommUtil
{
	private static boolean DEBUGFLAG = true;
	public static final Logger logger = Logger.getLogger("");
	public static  void LOG(String strData)
	{
		try
		{
			logger.info(strData);
		}
		catch(Exception ex)
		{}
	}
	public static  boolean IsDateString(String strData)
	{
		boolean RetVal = true;
		String s1 = strData;
		int len = strData.length();
		for(int i = 0; i< (14 - len); i++)
		{
			s1 += "0";
		}
		try
		{			
			SimpleDateFormat SimFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			RetVal = s1.equals(SimFormat.format(SimFormat.parse(s1)));   
		}
		catch(Exception Ex)
		{
			RetVal = false;
		}
		
		return RetVal;
	}
	
	public static String BytesToHexString(byte[] src, int len)
	{
		String strRet = "";
		for(int i=0; i<len; i++)
		{
			String s1 = Integer.toHexString(src[i]);
			if(s1.length() == 1)
			{
				strRet += "0" + s1;
			}
			else
			{
				strRet += s1.subSequence(s1.length() - 2,s1.length());
			}
		}
		return strRet;
	}
	
	public static byte[] HexStringToBytes(String src)
	{
		int len = src.length()/2;
		byte[] RetVal = new byte[len];
		for(int i=0; i<len; i++)
		{
			String str = src.substring(i*2, i*2+2);
			RetVal[i] = (byte)Integer.parseInt(str, 16);
		}
		return RetVal;
	}
//************************字符串转换成其它数据类型************************
	public static String IntToStringRightFillSpace(int data, int len)
	{
		String strRet = Integer.toString(data);
		int FillLen = len - strRet.length();
		for(int i=0; i<FillLen; i++)
		{
			strRet += " ";
		}
		return strRet;
	}
	
	public static String IntToStringLeftFillSpace(int data, int len)
	{
		String strRet = Integer.toString(data);
		int FillLen = len - strRet.length();
		for(int i=0; i<FillLen; i++)
		{
			strRet = " " + strRet;
		}
		return strRet;
	}
	public static String IntToStringLeftFillZero(int data, int len)
	{
		String strRet = Integer.toString(data);
		int FillLen = len - strRet.length();
		for(int i=0; i<FillLen; i++)
		{
			strRet = "0" + strRet;
		}
		return strRet;
	}
	public static String LongToStringLeftFillZero(long data, int len)
	{
		String strRet = Long.toString(data);
		int FillLen = len - strRet.length();
		for(int i=0; i<FillLen; i++)
		{
			strRet = "0" + strRet;
		}
		return strRet;
	}
	public static String StrRightFillBytes(String strData, int vByte, int len)
	{
		String strRet = null;
		ByteArrayOutputStream boutStream = new ByteArrayOutputStream();
		DataOutputStream doutStream = new DataOutputStream(boutStream);
		try
		{
			doutStream.writeBytes(strData);
			int FillLen = len - strData.trim().length();
			for(int i=0; i<FillLen; i++)
			{
				doutStream.writeByte(vByte);
			}

			byte[] byteData = boutStream.toByteArray();
			doutStream.close();	
			boutStream.close();
			
			strRet = new String(byteData);
		}catch(Exception ex)
		{
			strRet = null;
			ex.printStackTrace();
		}
		return strRet;
	}
	
	
	public static String StrRightFillSpace(String strData, int len)
	{
		String strRet = strData.trim();
		int FillLen = len - strData.trim().length();
		for(int i=0; i<FillLen; i++)
		{
			strRet += " ";
		}
		return strRet;
	}
	public static String StrRightFillZero(String strData, int len)
	{
		String strRet = strData.trim();
		int FillLen = len - strData.trim().length();
		for(int i=0; i<FillLen; i++)
		{
			strRet += "0";
		}
		return strRet;
	}
	
	public static String StrLeftFillZero(String strData, int len)
	{
		String strRet = "";
		int FillLen = len - strData.trim().length();
		for(int i=0; i<FillLen; i++)
		{
			strRet = "0" + strRet;
		}
		strRet += strData.trim();
		return strRet;
	}
	public static String StrLeftFillSpace(String strData, int len)
	{
		String strRet = "";
		int FillLen = len - strData.trim().length();
		for(int i=0; i<FillLen; i++)
		{
			strRet += " ";
		}
		strRet += strData.trim();
		return strRet;
	}
	
	public static int StrToInt(String pStr) {
		int Rslt = 0;
		int IStr = 0;
		if (pStr != null && pStr != "") {
			IStr = Integer.parseInt(pStr);
			Rslt = IStr;
		}
		return Rslt;
	}
	public static float StrToFloat(String pStr) {
		float Rslt = 0;
		if (pStr != null && pStr != "") {
			Rslt = Float.parseFloat(pStr);
		}
		return Rslt;
	}
//	字符串转换成GB2312编码
	public static String StrToGB2312(String str)
	{
		if (str == null)
			str ="";
		try{
			str = new String(str.getBytes("ISO-8859-1"),"GB2312");
		}
		catch(Exception ex){
			return "";
		}
		return str;
	}	
	//字符串转换成字节流
	public static byte[] StrToBytes(String pSrc) 
	{
		
		char[] Temp = pSrc.toCharArray();
		byte [] Dst = new byte[Temp.length];
		try {
			for (int i = 0; i < Temp.length; i++) {
				Dst[i] = (byte) Temp[i];
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return Dst;
	}
	//双字节变成单字节
	public static byte[] StrToHex(String pSrc) 
	{
		int len = pSrc.length();
		byte[] Buff = new byte[(len) / 2];
		int ch = 0;
		int ch1 = 0;
		for (int i = 0; i < (len / 2); i++) 
		{
			ch = (int) (pSrc.charAt(i * 2));
			if (ch >= 0x30 && ch <= 0x39)
				ch = ch - 0x30;
			else
				ch = ch - 0x41 + 10;
			ch = (ch << 4) & 0x0000000f0;
			ch1 = (int) (pSrc.charAt(i * 2 + 1));
			if (ch1 >= 0x30 && ch1 <= 0x39)
				ch1 = ch1 - 0x30;
			else
				ch1 = ch1 - 0x41 + 10;
			ch1 = ch1 & 0x00000000f;

			Buff[i] = (byte) (((int) ((ch | ch1) & 0x0000000ff)));
		}
		return Buff;
	}
//	双字节变成单字节
	public static byte[] Byte2ToByte1(byte[] pSrc, int len) 
	{
		byte[] Buff = new byte[(len) / 2];
		int ch = 0;
		int ch1 = 0;
		for (int i = 0; i < (len / 2); i++) 
		{
			ch = (int) (pSrc[i * 2]);
			if (ch >= 0x30 && ch <= 0x39)
				ch = ch - 0x30;
			else
				ch = ch - 0x41 + 10;
			ch = (ch << 4) & 0x0000000f0;
			ch1 = (int) (pSrc[i * 2 + 1]);
			if (ch1 >= 0x30 && ch1 <= 0x39)
				ch1 = ch1 - 0x30;
			else
				ch1 = ch1 - 0x41 + 10;
			ch1 = ch1 & 0x00000000f;

			Buff[i] = (byte) (((int) ((ch | ch1) & 0x0000000ff)));
		}
		return Buff;
	}
//*******************时间类型************************************************
	public static Vector<?> getDate(String pStartDate, String pEndDate)
	{
		String strToday ="";
		String strTomorrow = "";
		Vector<Object> data =  new Vector<Object>();
		if(pStartDate == null || pStartDate.equals(""))
		{
			SimpleDateFormat SimFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	    	Date Today = new Date();
	        Date Tomorrow = new Date();	        
	        long myTime = (Today.getTime()/1000)-60*60*24;

	        Today.setTime(myTime*1000);
//	        Tomorrow.setTime(myTime*1000);
	        strToday = SimFormat.format(Today);
	        strTomorrow = SimFormat.format(Tomorrow);
		}
		else
		{
			strToday = pStartDate;
			strTomorrow = pEndDate;
		}
		data.insertElementAt(strToday,0);
		data.insertElementAt(strTomorrow,1);		
		return data;
	}
	public static String getNewDate(String pStrDate, int pInterval)
	{
		String retVal = "";
		try
		{
			SimpleDateFormat SimFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
			Date Temp = SimFormat.parse(pStrDate);
			long lTemp = Temp.getTime()+ pInterval;
			Temp.setTime(lTemp);
			retVal = SimFormat.format(Temp);
		}
		catch(Exception ex)
		{
			 ex.printStackTrace();
		} 
		return retVal;
	}
	public static String getTime()
	{
		String retVal = "";
		try
		{
			SimpleDateFormat SimFormat = new SimpleDateFormat("yyyyMMddHHmmss"); 
			retVal = SimFormat.format(new Date());
		}
		catch(Exception ex)
		{
			 ex.printStackTrace();
		} 
		return retVal;
	}
//*******************整型转换成其它类型********************************
	/*
	 * 整数转换成Hex字符串， 其中length为转换后的Hex宽度
	 */
	public static String IntToHex(int a, int length) {
		String s = "000000000000000000";
		s = s.substring(0, length);
		if (s.length() <= Integer.toHexString(a).length()) {
			return Integer.toHexString(a).toUpperCase();
		}
		return s.substring(Integer.toHexString(a).length())
				+ Integer.toHexString(a).toUpperCase();
	}
	//数据反向
	public static int converseInt(int i) {
		int j = 0;
		j |= (i << 24) & 0xFF000000;
		j |= (i << 8) & 0x00FF0000;
		j |= (i >> 8) & 0x0000FF00;
		j |= (i >> 24) & 0x000000FF;
		return j;
	}
	public static short converseShort(short i) {
		short j = 0;
		j |= (i << 8) & 0xFF00;
		j |= (i >> 8) & 0x00FF;
		return j;
	}
	public static long converseLong(long i) {
		long j = 0;
		j |= (i << 56) & 0xFF00000000000000L;
		j |= (j << 40) & 0x00FF000000000000L;
		j |= (j << 24) & 0x0000FF0000000000L;
		j |= (j << 8) & 0x000000FF00000000L;
		j |= (j >> 8) & 0x00000000FF000000L;
		j |= (j >> 24) & 0x0000000000FF0000L;
		j |= (j >> 40) & 0x000000000000FF00L;
		j |= (j >> 56) & 0x00000000000000FFL;
		return j;
	}
	public static byte[] converseTime(int time) {
		byte c1 = (byte) ((time) & 0x000000FF);
		byte c2 = (byte) ((time >> 8) & 0x000000FF);
		byte c3 = (byte) ((time >> 16) & 0x000000FF);
		byte c4 = (byte) ((time >> 24) & 0x000000FF);
		byte c[] = new byte[4];
		c[0] = c4;
		c[1] = c3;
		c[2] = c2;
		c[3] = c1;
		return c;
	}

//*****************************字节打印*************************************
	public static void printMsg(byte[] ba, int loopcount) {
		int temp;
		printer.println();
		for (int i = 0; i < loopcount; i++) {
			temp = (int) ba[i];
			temp &= 0x000000ff;
			if (temp < 0x10)
				printer.print("0");
			printer.print(Integer.toHexString(temp));
			if ((i + 1) % LineByteCnt == 0) {
				printer.println();
				continue;
			} else if (((i + 1) % WordCnt) == 0) {
				printer.print("  ");
			}
			// for(int j=0; j<BlankBtByte; j++)
			printer.print(" ");
		}
		printer.println();
		printer.println();
		printer.flush();
	}

	public void finalize() {
		printer.close();
	}

	private static PrintWriter printer = new PrintWriter(
			new OutputStreamWriter(System.out));

	private static int LineByteCnt = 20;

	private static int WordCnt = 4;

	// private static int BlankBtByte = 1;
	// private static int BlankBtWord = 2;

	public static void setPrinter(String filepath) throws IOException {
		Writer w = new FileWriter(filepath, true);
		printer = new PrintWriter(w);
	}

	/*
	 * public static void setPrinter(){printer = new PrintWriter(new
	 * OutputStreamWriter(System.out));} public static void setFormat(int
	 * howmanybytesinaline,int howmanyblanksinaline){
	 * if(howmanyblanksinaline>0)ubytePrintInHex.howmanyblanksinaline =
	 * howmanyblanksinaline;
	 * if(howmanybytesinaline>0)ubytePrintInHex.howmanybytesinaline =
	 * howmanybytesinaline; }
	 */

	
	// 将中英文字串分割
	public static String EnChDivide(String str) {
		byte[] bt = str.getBytes();
		int j = 0;
		for (int i = 0; i < bt.length; i++) {
			if (bt[i] < 0) {// 是汉字字符

			} else {// 是英文字符
				j++;
			}
		}
		return str.substring(0, j);
	}
	
//**********************网络字符转换*************************************************	

	// 将中英文字串转换成纯英文字串
	public static String toTureAsciiStr(String str) {
		StringBuffer sb = new StringBuffer();
		byte[] bt = str.getBytes();
		for (int i = 0; i < bt.length; i++) {
			if (bt[i] < 0) {// 是汉字去高位1
				sb.append((char) (bt[i] & 0x7f));
			} else {// 是英文字符 补0作记录
				sb.append((char) 0);
				sb.append((char) bt[i]);
			}
		}
		return sb.toString();
	}
	public static String unToTrueAsciiStr(byte[] pSrc, int pStart, int pLen) 
	{		
		int i, l = 0, length = pLen, j = 0;
		for (i = pStart; i < pStart + length; i++) 
		{
			if (pSrc[i] == 0) 
			{
				l++;
			}
		}
		byte[] bt2 = new byte[length - l];
		for (i = pStart; i < pStart + length; i++) 
		{			
			if (pSrc[i] == 0) {
				i++;
				bt2[j] = pSrc[i];
			} else {
				bt2[j] = (byte) (pSrc[i] | 0x80);
			}
			j++;
		}
		String tt = new String(bt2);
		return tt;
	}
	// 将经转换的字串还原
	public static String unToTrueAsciiStr(String str) {
		byte[] bt = str.getBytes();
		int i, l = 0, length = bt.length, j = 0;
		for (i = 0; i < length; i++) {
			if (bt[i] == 0) {
				l++;
			}
		}
		byte[] bt2 = new byte[length - l];
		for (i = 0; i < length; i++) {
			if (bt[i] == 0) {
				i++;
				bt2[j] = bt[i];
			} else {
				bt2[j] = (byte) (bt[i] | 0x80);
			}
			j++;
		}
		String tt = new String(bt2);
		return tt;
	}
//***********************其它*******************************
	public static String UcsToBytes(byte[] bs) {
		int new_len = bs.length + 2;
		boolean lenAdd = false;
		if ((new_len % 2) == 1) {
			new_len++;
			lenAdd = true;
		}
		if (new_len <= 2)
			return "";

		byte[] new_bs = new byte[new_len];
		new_bs[0] = (byte) 0xff;
		new_bs[1] = (byte) 0xfe;
		for (int i = 2; i < new_len; i += 2) {
			if (i != new_len - 2 || !lenAdd)
				new_bs[i] = bs[i - 1];
			new_bs[i + 1] = bs[i - 2];
		}
		String r = "";
		try {
			r = new String(new_bs, "Unicode");
		} catch (Exception e) {
		}
		return r;
	}
	public static void PRINT(String msg)
	{
		if(DEBUGFLAG)
			System.out.println(msg);
	}
	public static void PRINT_ERROR(String msg)
	{
		if(DEBUGFLAG)
		{
			System.out.println("ERROR:" + msg);
		}
	}
	public static boolean setConfigValue(String item,String value)
	{ 
		boolean ret = false;
		try
		{
			Properties prop = new Properties();
			prop.load(new FileInputStream("Client.ini"));
			OutputStream fos = new FileOutputStream("Client.ini");
	        prop.setProperty(item, StrToGB2312(value));
	        prop.store(fos,"mobile interface");
	        System.out.println("end");
	        fos.close();
	        ret = true;
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return ret;
	}
	
	public static String BSubstring(String formatStr, int from, int len)
	{
		if(formatStr.getBytes().length >= from + len)
		{
			byte[] fromBytes = formatStr.getBytes();
			byte[] toBytes = new byte[len];
			for(int i=from; i<from+len; i++)
			{
				toBytes[i-from] = fromBytes[i];
			}
			return new String(toBytes);
		}
		else
		{
			return "";
		}
	}
	
	public static String StrBRightFillSpace(String strData, int len)
	{
		String strRet = strData.trim();
		int FillLen = len - strData.trim().getBytes().length;
		for(int i=0; i<FillLen; i++)
		{
			strRet += " ";
		}
		return strRet;
	}
}
