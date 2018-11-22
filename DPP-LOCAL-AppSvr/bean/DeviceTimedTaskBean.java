package bean;

import java.util.Hashtable;

import net.TcpSvr;
import util.Cmd_Sta;
import util.CommUtil;
import util.DBUtil;

public class DeviceTimedTaskBean {
	private String SN;
	private String Id;
	private String Cycle;
	private String Order;
	
	private DBUtil m_DBUtil;
	private TcpSvr m_TcpSvr;
	
	public Hashtable<String, Thread> objThrdTable = null;//定时任务线程列表
	
	private TimedThrd timedThrd;	// 定时任务线程
		
	public DeviceTimedTaskBean(DBUtil m_DBUtil, TcpSvr m_TcpSvr){
		this.m_DBUtil = m_DBUtil;
		this.m_TcpSvr = m_TcpSvr;
		objThrdTable = new Hashtable<String, Thread>();
	}
	
	/**
	 * 立即采集数据
	 */
	public void collectDataNow(String SN){
		String taskList = m_DBUtil.Func("Func_Send_GetOne", SN); // 取出当前thread的所有定时任务
		String [] task = taskList.split("\\|");

		Id = task[1];
		//Cycle = task[2];
		Order = task[3];
		
		// 发送采集内容
		if(m_TcpSvr.DisPatch(Cmd_Sta.COMM_DELIVER, CommUtil.StrBRightFillSpace(Id, 20), Order)){
			CommUtil.PRINT("Thread [" + Id + "] Send Now [" + Order + "] SUCCESS");
		}else{
			CommUtil.PRINT("Thread [" + Id + "] Send Now [" + Order + "] ERROR");
		}
	}
	
	/**
	 * 立即采集全部数据
	 */
	public void collectDataNowAll(String pClientKey){
		String taskList = m_DBUtil.Func("Func_Send_GetAll_TId", pClientKey); // 取出当前clien的所有定时任务
		String [] task = taskList.split(";");
		for(int i = 0; i < task.length; i ++){
			String [] obj = task[i].split("\\|");
			Id = obj[1];
			//Cycle = obj[2];
			Order = obj[3];
			// 发送采集内容
			if(m_TcpSvr.DisPatch(Cmd_Sta.COMM_DELIVER, CommUtil.StrBRightFillSpace(Id, 20), Order)){
				CommUtil.PRINT("Thread [" + Id + "] Send Now [" + Order + "] SUCCESS");
			}else{
				CommUtil.PRINT("Thread [" + Id + "] Send Now [" + Order + "] ERROR");
			}
		}
	}
	
	/**
	 * 打开当前cline的定时任务
	 * @param pClientKey
	 */
	public void openTimedTask(String pClientKey){
		String taskList = m_DBUtil.Func("Func_Send_GetAll", pClientKey); // 取出当前clien的所有定时任务
		String [] task = taskList.split(";");
		for(int i = 0; i < task.length; i ++){
			String [] obj = task[i].split("\\|");
			SN = obj[0];
			if(objThrdTable.containsKey(SN)){	// 如果此任务已存在，到下一个
				continue;
			}
			Id = obj[1];
			Cycle = obj[2];
			Order = obj[3];
			CommUtil.PRINT("SN["+SN+"]Id["+Id+"]Cycle["+Cycle+"]SN["+Order+"]");
			timedThrd = new TimedThrd(Id, Cycle, Order);	// 新建线程
			timedThrd.start();								// 开启线程

			objThrdTable.put(SN, timedThrd);
			CommUtil.PRINT("open Thread["+SN+"]");
		}
	}
	
	/**
	 * 关闭当前cline的定时任务
	 * @param pClientKey
	 */
	public void closeTimedTask(String pClientKey){
		String taskList = m_DBUtil.Func("Func_Send_GetSN", pClientKey); // 取出当前clien的所有定时任务
		String [] task = taskList.split("\\|");
		for(int i = 0; i < task.length; i ++){
			SN = task[i];
			System.out.println("SN["+SN+"]");
			if(SN != null && SN.length() > 0){
				// 取到线程
				timedThrd = (TimedThrd) objThrdTable.get(SN);
				// 线程interrupted，使线程停止
				timedThrd.interrupt();
				// 线程tab里移除关闭的线程
				objThrdTable.remove(SN);
				CommUtil.PRINT("close Thread["+SN+"]");
			}
		}
	}
	
	/**
	 * 打开单个定时任务
	 * @param SN
	 */
	public void openOneTimedTask(String SN){
		String taskList = m_DBUtil.Func("Func_Send_GetOne", SN);
		String [] task = taskList.split("\\|");
		SN = task[0];
		if(objThrdTable.containsKey(SN)){
			return;
		}
		Id = task[1];
		Cycle = task[2];
		Order = task[3];
		
		timedThrd = new TimedThrd(Id, Cycle, Order);
		timedThrd.start();

		objThrdTable.put(SN, timedThrd);
		CommUtil.PRINT("open Thread["+SN+"]");
	}
	
	/**
	 * 关闭单个定时任务
	 * @param SN
	 */
	public void closeOneTimedTask(String SN){
		// 取到线程
		timedThrd = (TimedThrd) objThrdTable.get(SN);
		// 线程interrupted，使线程停止
		timedThrd.interrupt();
		// 线程tab里移除关闭的线程
		objThrdTable.remove(SN);
		CommUtil.PRINT("close Thread["+SN+"]");
	}
	
	private class TimedThrd extends Thread
	{
		private String taskId;
		private long taskCycle;
		private String taskOrder;
		public TimedThrd(String taskId, String taskCycle, String taskOrder){
			this.taskId = taskId;
			this.taskCycle = Long.parseLong(taskCycle);
			this.taskOrder = taskOrder;
		}
		
		public void run()
		{
			while(!isInterrupted()){
				try{
					// 发送采集内容
					if(m_TcpSvr.DisPatch(Cmd_Sta.COMM_COLLECT, CommUtil.StrBRightFillSpace(taskId, 20), taskOrder)){
						CommUtil.PRINT("Thread [" + taskId + "] Send [" + taskOrder + "] SUCCESS");
					}else{
						CommUtil.PRINT("Thread [" + taskId + "] Send [" + taskOrder + "] ERROR");
					}
					
					sleep(taskCycle * 1000);//taskCycle 秒数
				}catch(InterruptedException e){
	                e.printStackTrace();
	                break;//捕获到异常之后，执行break跳出循环。
	            }catch(Exception ex){
					ex.printStackTrace();
				}
				
			}
		}	
	}
	
}