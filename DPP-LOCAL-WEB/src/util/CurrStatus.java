package util;

import java.io.Serializable;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

public class CurrStatus implements Serializable
{
	private static final long serialVersionUID = 100;
  	private String CheckCode = "";    //验证码
	private int Cmd;                  //查询命令
	private int CurrPage;             //当前页码
	private int TotalRecord;          //总记录数
	private int TotalPages;           //总页数
	private int Func_Id;         
	
	private String Func_Sort_Id;      //排序                1：编码、2：管径、3：材料
	private String Func_Project_Id;   //项目ID      
	private String Func_Type_Id;      //管井/管线       1：井、    2：线  
	private String Func_Sub_Type_Id;  //雨/污              1：雨、    2：污
	
	private Vector<Object> VecDate;
	private String Result = null;    //
	private int Curr_Status;         //当前状态
	private String Jsp;              //保存jsp地址

	
	public String getFunc_Sort_Id() {
		return Func_Sort_Id;
	}
	public void setFunc_Sort_Id(String func_Sort_Id) {
		Func_Sort_Id = func_Sort_Id;
	}
	public String getFunc_Project_Id() {
		return Func_Project_Id;
	}
	public void setFunc_Project_Id(String func_Project_Id) {
		Func_Project_Id = func_Project_Id;
	}
	
	public String getFunc_Type_Id() {
		return Func_Type_Id;
	}
	public void setFunc_Type_Id(String func_Type_Id) {
		Func_Type_Id = func_Type_Id;
	}
	public String getFunc_Sub_Type_Id() {
		return Func_Sub_Type_Id;
	}
	public void setFunc_Sub_Type_Id(String func_Sub_Type_Id) {
		Func_Sub_Type_Id = func_Sub_Type_Id;
	}
	public String getCheckCode() {
		return CheckCode;
	}
	public void setCheckCode(String checkCode) {
		CheckCode = checkCode;
	}
	public int getCmd() {
		return Cmd;
	}
	public void setCmd(int cmd) {
		Cmd = cmd;
	}
	public int getCurrPage() {
		return CurrPage;
	}
	public void setCurrPage(int currPage) {
		CurrPage = currPage;
	}
	public int getTotalRecord() {
		return TotalRecord;
	}
	
	/** 根据总记录数计算总页数
	 * @param totalRecord
	 */
	public void setTotalRecord(int totalRecord) {
		TotalRecord = totalRecord;
		TotalPages = (totalRecord + MsgBean.CONST_PAGE_SIZE - 1)/MsgBean.CONST_PAGE_SIZE;
	}
	
	public int getTotalPages() {
		return TotalPages;
	}
	
	public void setTotalPages(int totalPages) {
		TotalPages = totalPages;
	}
	
	public int getFunc_Id() {
		return Func_Id;
	}
	
	public void setFunc_Id(int func_Id) {
		Func_Id = func_Id;
	}
	
	public Vector<Object> getVecDate() {
		return VecDate;
	}
	public void setVecDate(Vector<Object> vecDate) {
		VecDate = vecDate;
	}
	public String getResult() {
		return Result;
	}
	public void setResult(String result) {
		Result = result;
	}
	public int getCurr_Status() {
		return Curr_Status;
	}
	
	public void setCurr_Status(int curr_Status) {
		Curr_Status = curr_Status;
	}
	
	public String getJsp() {
		return Jsp;
	}
	public void setJsp(String jsp) {
		Jsp = jsp;
	}
	
	/** 获取request中 Cmd,CurrPage,VecDate等数据 封装到 CurrStatus对象中的页面数据
	 * @param request
	 * @param pFromZone
	 *     是一个boolean值, true表示: 从其他页面 到 当前页面 
	 */
	public void getHtmlData(HttpServletRequest request, boolean pFromZone) {	
		try
		{
			if(pFromZone)
			{
				CurrPage = 1;
			}
			else
			{
				Cmd               = CommUtil.StrToInt(request.getParameter("Cmd"));
				CurrPage          = CommUtil.StrToInt(request.getParameter("CurrPage"));
				CurrPage          = CurrPage==0?1:CurrPage;			
				VecDate           = CommUtil.getDate(request.getParameter("BTime"),request.getParameter("ETime"));
				//INT
				Func_Id           = CommUtil.StrToInt(request.getParameter("Func_Id"));
				//String
				Func_Sort_Id      = CommUtil.StrToGB2312(request.getParameter("Func_Sort_Id"));
				Func_Project_Id   = CommUtil.StrToGB2312(request.getParameter("Func_Project_Id"));	
				Func_Type_Id      = CommUtil.StrToGB2312(request.getParameter("Func_Type_Id"));
				Func_Sub_Type_Id  = CommUtil.StrToGB2312(request.getParameter("Func_Sub_Type_Id"));			
				
			}
		}catch(Exception Ex)
		{
			Ex.printStackTrace();
		}
	}
	
	
	/** 获取分页条: 当前页/总页,首页,上一页,下一页 , 跳入页
	 * @param pForm 字符串  表示: JSP页面的表单 的 name值
	 * @return 返回 HTML字符串 输出到浏览器 
	 */
	public String GetPageHtml(String pForm) 
	{
		String s = "";
		int TotalPages;		
		TotalPages = (TotalRecord + MsgBean.CONST_PAGE_SIZE -1)/MsgBean.CONST_PAGE_SIZE;    //算出总页数
		if(0 == TotalRecord)
		{
			CurrPage = 0;    //总页数为0,则当前页码为0;
		}
		s += "页数：<strong>" + CurrPage + "</strong>/<strong>" + TotalPages + "</strong><span>[共<b>" + TotalRecord +"</b>条记录]</span>";
		s += "<a href=# onclick='GoPage(1)'>&nbsp;首页</a> ";		
		s += "<a href=# onclick='GoPage(" + (CurrPage - 1) +")'>&nbsp;上一页</a> ";	 	
		
		int beginPage = CurrPage - 4 < 1 ? 1 : CurrPage - 4;    // CurrPage >= 5 时 beginPage 为  CurrPage - 4 
		if(beginPage <= TotalPages) 
		{
			for(int i=beginPage, j=0; i<=TotalPages&&j<6; i++,j++)
			{
				if(i == CurrPage)
				   s+="<a href=# onclick='GoPage("+i+")'><strong>"+i+"</strong></a> ";
				else
		           s+="<a href=# onclick='GoPage("+i+")'>"+i+"</a> ";
			}
		}
		if(CurrPage == TotalPages)
		{
			s+="<a >&nbsp;下一页</a>";		
			s+="<a >&nbsp;末页</a> ";
		}
		else
		{
			s+="<a href=# onclick='GoPage("+(CurrPage+1)+")'>&nbsp;下一页</a> ";
			s+="<a href=# onclick='GoPage("+TotalPages+")'>&nbsp;末页</a> ";
			
		}
		s+="到第<input name='ToPage' type='text' size='2'>页";
		s+="<input type='button' style='width:40px;height:20px' onClick='GoPage(" + pForm + ".ToPage.value)' value='确定'/>";
		return s; 
	}
}
