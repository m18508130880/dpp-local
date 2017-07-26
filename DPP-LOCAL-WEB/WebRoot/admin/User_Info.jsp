<%@ page contentType="text/html; charset=gb2312"%>
<%@ page import="java.util.*"%>
<%@ page import="bean.*"%>
<%@ page import="util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>用户信息</title>
<meta http-equiv="x-ua-compatible" content="ie=7"/>
<link type="text/css" href="../skin/css/style.css" rel="stylesheet"/>
<script language=javascript>document.oncontextmenu=function(){window.event.returnValue=false;};</script>
</head>
<%
	
	String       Sid         = CommUtil.StrToGB2312(request.getParameter("Sid"));
	CurrStatus   currStatus  = (CurrStatus)session.getAttribute("CurrStatus_" + Sid);
	CorpInfoBean Corp_Info   = (CorpInfoBean)session.getAttribute("Corp_Info_" + Sid);
 	
  ArrayList    User_Info   = (ArrayList)session.getAttribute("User_Info_" + Sid);
  ArrayList    FP_Role     = (ArrayList)session.getAttribute("FP_Role_" + Sid);
  ArrayList    Manage_Role = (ArrayList)session.getAttribute("Manage_Role_" + Sid);
  
  String Dept = Corp_Info.getDept();
  int sn = 0;
  
%>
<body style="background:#CADFFF">
<form name="User_Info"  action="Admin_User_Info.do" method="post" target="mFrame">
<div id="down_bg_2">
	<div id="cap"><img src="../skin/images/user_info.gif"></div><br><br><br>
	<div id="right_table_center">
		<table width="80%" style='margin:auto;' border=0 cellPadding=0 cellSpacing=0 bordercolor="#3491D6" borderColorDark="#ffffff">
			
			
			<tr height='30'>
				<td width='30%' align='left'>
					<select name='Func_Corp_Id' style='width:120px;height:20px;' onchange="doSelect();">
						<option value='99' <%=currStatus.getFunc_Corp_Id().equals("99")?"selected":""%>>全部</option>
						<%
						if(Dept.trim().length() > 0)
						{
							String[] DeptList = Dept.split(",");
						  String pDept_Id = "";
						  String pDept_Name = "";
						  for(int i=0; i<DeptList.length; i++)
						  {
								pDept_Id = CommUtil.IntToStringLeftFillZero(i+1, 2);
								pDept_Name = DeptList[i];
						%>
						    <option value="<%=pDept_Id%>" <%=currStatus.getFunc_Corp_Id().equals(pDept_Id)?"selected":""%>><%=pDept_Name%></option>
						<%
    					}
						}
						%>
					</select>
				</td>
				<td width='70%' align='right'>
					<img src="../skin/images/mini_button_add.gif" style='cursor:hand;' onClick='doAdd()'>
				</td>
			</tr>
			
			
			<tr height='30'>
				<td width='100%' align='center' colspan=2>
					<table width="100%" border=1 cellPadding=0 cellSpacing=0 bordercolor="#3491D6" borderColorDark="#ffffff">
						<tr>
							<td width="5%"  class="table_deep_blue">序号</td>
							<td width="10%" class="table_deep_blue">帐号</td>
							<td width="10%" class="table_deep_blue">姓名</td>
							<td width="10%" class="table_deep_blue">部门</td>
							<td width="10%" class="table_deep_blue">入职时间</td>
							<td width="10%" class="table_deep_blue">电话</td>
							<td width="10%" class="table_deep_blue">项目名称</td>
							<td width="10%" class="table_deep_blue">状态</td>
						</tr>
					<%
						if(User_Info != null)
						{
							Iterator iterator = User_Info.iterator();
							while(iterator.hasNext())
							{
								UserInfoBean statBean = (UserInfoBean)iterator.next();
								String Id = statBean.getId();
								String CName = statBean.getCName();							
								String Dept_Id = statBean.getDept_Id();	
								String Birthday	= statBean.getBirthday();	
								String Tel	= statBean.getTel();	
								String Project_Name = statBean.getProject_Name();
							  String Status = statBean.getStatus();				  
						    						    								
								String Dept_Name = "无";
							  if(null != Dept_Id && Dept.trim().length() > 0)
							 	{
							 		String[] DeptList = Dept.split(",");
									for(int i=0; i<DeptList.length; i++)
									{
							    	if(Dept_Id.equals(CommUtil.IntToStringLeftFillZero(i+1, 2)))
							    	{
									  	Dept_Name = DeptList[i];
									  }
									}
							 	}
								
								String str_Status = "";
								switch(Integer.parseInt(Status))
								{
									case 0:
											str_Status = "启用";
										break;
									case 1:
											str_Status = "注销";
										break;
								}
											    
								sn ++;
						%>
					
					  <tr <%=((sn%2)==0?"class='table_blue'":"class='table_white_l'")%>>
							<td <%=((!Status.equals("0"))?"class='font_gray'":"")%> align=center style="cursor:hand " onmouseout="this.style.color='#000000';" onmouseover="this.style.color='#FF0000';"  title="点击查看" onClick="doEdit('<%=Id%>')"><U><%=sn%></U></td>
					    <td <%=((!Status.equals("0"))?"class='font_gray'":"")%> align=center><%=Id%></td>
					    <td <%=((!Status.equals("0"))?"class='font_gray'":"")%> align=center><%=CName%></td>
					    <td <%=((!Status.equals("0"))?"class='font_gray'":"")%> align=center><%=Dept_Name%></td>
					    <td <%=((!Status.equals("0"))?"class='font_gray'":"")%> align=center><%=Birthday%></td>
					    <td <%=((!Status.equals("0"))?"class='font_gray'":"")%> align=center><%=Tel%></td>
					    <td <%=((!Status.equals("0"))?"class='font_gray'":"")%> align=center><%=Project_Name%></td>
					    <td <%=((!Status.equals("0"))?"class='font_gray'":"")%> align=center><%=str_Status%></td>      
						</tr>
						<%
							}
						}
						%>
					</table>
				</td>
			</tr>
		</table>
	</div>
</div>
<input name="Cmd" type="hidden" value="1">
<input name="Sid" type="hidden" value="<%=Sid%>">
</form>
</body>
<SCRIPT LANGUAGE=javascript>
if(<%=currStatus.getResult().length()%> > 0)
   alert("<%=currStatus.getResult()%>");
<%
currStatus.setResult("");
session.setAttribute("CurrStatus_" + Sid, currStatus);
%>

function doSelect()
{
	User_Info.submit();
}

function doAdd()
{
	location = "User_Info_Add.jsp?Sid=<%=Sid%>";
}

function doEdit(pId)
{  
	location = "User_Info_Edit.jsp?Sid=<%=Sid%>&Id="+pId;
}
</SCRIPT>
</html>