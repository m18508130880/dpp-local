package container;

import java.util.LinkedList;

import util.CommUtil;
import bean.BaseCmdBean;


/************************************发送线程*****************************************/
public class TimeCheckThrd extends Thread
{
	private int m_TimeOut = 60;
	public TimeCheckThrd(int timeout)throws Exception
	{
		m_TimeOut = timeout;
	}
	public void run()
	{
		LinkedList<String> checkList = null;		 //接收数据列表,用于客户端数据交换
		while(true)
		{
			try
			{
				checkList = ActionContainer.GetTimeOutList(m_TimeOut);
				
				while(!checkList.isEmpty())
				{
					String data = checkList.removeFirst();
					if(null ==  data)
					{
						break;
					}						
					BaseCmdBean bean = ActionContainer.GetAction(data);
					if(null != bean)
						bean.noticeTimeOut();
					CommUtil.LOG(data + " 回应超时 222");
				}
				sleep(1000*10);
			}catch(Exception e)
			{}
		}				
	}	
}