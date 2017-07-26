package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class FileTool 
{
	public static synchronized void Append(String fileName, String content)
	{
	   try 
	   {
		    //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
		    FileWriter writer = new FileWriter(fileName, true);
		    writer.write(content);
		    writer.close();
	   } catch (IOException e) {
		   e.printStackTrace();
	   }
	} 	
	public static  synchronized String Read(String fileName) 
	{
		String fileContent = "";
		File file = new File(fileName);
		if (file.exists())
		{
			try
			{
				FileInputStream in = new FileInputStream(file);
				int fileSize = in.available();
				byte[] byteContent = new byte[fileSize];
				in.read(byteContent);
				fileContent=new String(new String(byteContent, "GB2312").getBytes("GB2312"));
				in.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			} 
	     }

	    return fileContent;
	}
	public static  synchronized void delFile(String filePathAndName)
	{
       try
       {
           File myDelFile = new File(filePathAndName);
           myDelFile.delete();
       }
       catch (Exception e)
       {
           e.printStackTrace();
       }
	}
	public static  synchronized void Write_Log(String pData)
	{
		Append("Log.txt", pData);
	}
	public static  synchronized String[] Read_Log()
	{
		String[] ContentList = null;
		String Content = FileTool.Read("LOG.Txt");
		if(Content.length() > 0)
		{
			ContentList = Content.split("\n");
		}
		delFile("Log.txt");
		return ContentList;
	}
}
