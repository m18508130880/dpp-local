<%@ page contentType="text/html; charset=gb2312"%>
<%@ page import="java.util.*"%>
<%@ page import="bean.*"%>
<%@ page import="util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>编辑用户信息</title>
<meta http-equiv="x-ua-compatible" content="ie=7"/>
<link type="text/css" href="../skin/css/style.css" rel="stylesheet"/>
<script type="text/javascript" src="../skin/js/util.js"></script>
<script type="text/javascript" src="../skin/js/My97DatePicker/WdatePicker.js"></script>
<script language=javascript>document.oncontextmenu=function(){window.event.returnValue=false;};</script>
</head>
<%
	
	String Sid = CommUtil.StrToGB2312(request.getParameter("Sid"));
	CurrStatus currStatus = (CurrStatus)session.getAttribute("CurrStatus_" + Sid);
	CorpInfoBean Corp_Info = (CorpInfoBean)session.getAttribute("Corp_Info_" + Sid);
	ArrayList    Project_Info   = (ArrayList)session.getAttribute("Project_Info_" + Sid);
  String Dept = "";
  if(Corp_Info != null)
	{
		Dept = Corp_Info.getDept();
    if(Dept == null)
    {
    	Dept = "";
    }
 	}
 	
 	ArrayList FP_Role = (ArrayList)session.getAttribute("FP_Role_" + Sid);
  ArrayList Manage_Role = (ArrayList)session.getAttribute("Manage_Role_" + Sid);
 	ArrayList User_Info = (ArrayList)session.getAttribute("User_Info_" + Sid);
 	String Id = request.getParameter("Id");
  String CName = "";
  String Dept_Id = "";
	String Birthday = "";
	String Tel = "";
	String Status = "";
	String Project_Id = "";
	String Pwd = "";
	
	if(User_Info != null)
	{
		Iterator iterator = User_Info.iterator();
		while(iterator.hasNext())
		{
			UserInfoBean statBean = (UserInfoBean)iterator.next();
			if(statBean.getId().equals(Id))
			{
					CName = statBean.getCName();
					Dept_Id = statBean.getDept_Id();
					Birthday = statBean.getBirthday();
					Tel = statBean.getTel();
					Status = statBean.getStatus();
					Project_Id = statBean.getProject_Id();
					Pwd = statBean.getPwd();
					
			}
		}
 	}
	
  
%>
<body style="background:#CADFFF">
<form name="User_Info_Edit"  action="Admin_User_Info.do" method="post" target="mFrame" enctype="multipart/form-data">
<div id="down_bg_2">
	<div id="cap"><img src="../skin/images/cap_user_info.gif"></div><br><br><br>
	<div id="right_table_center">
		<table width="60%" style='margin:auto;' border=0 cellPadding=0 cellSpacing=0 bordercolor="#3491D6" borderColorDark="#ffffff">
			<tr height='30'>
				<td width='100%' align='right'>
					<img src="../skin/images/mini_button_submit.gif"    style='cursor:hand;' onClick='doEdit()'>
					<img src="../skin/images/mini_button_pwd_reset.gif" style='cursor:hand;' onClick='doPwdEdit()'>
					<img src="../skin/images/button10.gif"              style='cursor:hand;' onclick='doNO()'>
				</td>
			</tr>
			<tr height='30'>
				<td width='100%' align='center'>
					<table width="100%" border=1 cellPadding=0 cellSpacing=0 bordercolor="#3491D6" borderColorDark="#ffffff">
						<tr height='30'>
							<td width='20%' align='center'>帐&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;号</td>
							<td width='30%' align='left'>
								<%=Id%>
							</td>
							<td width='20%' align='center'>部&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;门</td>
							<td width='30%' align='left'>
								<select name="Dept_Id" style="width:97%;height:20px"> 
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
								    <option value="<%=pDept_Id%>" <%=pDept_Id.equals(Dept_Id)?"selected":""%>><%=pDept_Name%></option>
								<%
		    					}
								}
								%>
								</select>
							</td>
						</tr>
						
						<tr height='30'>
							<td width='20%' align='center'>姓&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;名</td>
							<td width='30%' align='left'>
								<input type='text' name='CName' style='width:96%;height:20px;' value='<%=CName%>' maxlength='6'>
							</td>
							<td width='20%' align='center'>电&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;话</td>
							<td width='30%' align='left'>
								<input type='text' name='Tel' style='width:96%;height:20px;' value='<%=Tel%>' maxlength='11'>
							</td>
						
						</tr>
						
						<tr height='30'>
							<td width='20%' align='center'>入职时间</td>
							<td width='30%' align='left'>
								<input type="text" name="Birthday" onClick="WdatePicker({readOnly:true})" class="Wdate" maxlength="10" style='width:97%;' value='<%=Birthday%>'>
							</td>			
							<td width='20%' align='center'>项&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;目</td>
							<td width='30%' align='left'>

								<select name="Project_Id" style="width:97%;height:20px"> 
								<%
 	                if(Project_Info != null){
		  								Iterator iterator = Project_Info.iterator();
											while(iterator.hasNext()){
											ProjectInfoBean statBean = (ProjectInfoBean)iterator.next();
											String Pro_Id = statBean.getId();
											String Pro_Name = statBean.getCName();				
								%>
								    <option value="<%=Pro_Id%>" <%=Project_Id.equals(Pro_Id)?"selected":""%> > <%=Pro_Name%></option>
								<%
		    						 }
									}
								%>
								</select>
	
							</td>
						</tr>
						<tr height='30'>	
							<td width='20%' align='center'>状&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;态</td>
							<td width='30%' align='left'>
								<select name='Status' style='width:97%;height:20px'>
									<option value='0' <%=Status.equals("0")?"selected":""%>>启用</option>
									<option value='1' <%=Status.equals("1")?"selected":""%>>注销</option>
								</select>
							</td>
						</tr>
						
						
					</table>
				</td>
			</tr>
		</table>
	</div>
</div>
<input name="Id" type="hidden" value="<%=Id%>">
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



function doEdit()
{
  if(User_Info_Edit.Dept_Id.value.Trim().length < 1)
  {
    alert("请选择部门!");
    return;
  }

  if(User_Info_Edit.CName.value.Trim().length < 1)
  {
    alert("请输入姓名!");
    return;
  }

  if(User_Info_Edit.Tel.value.Trim().length < 1)
  {
    alert("请输入联系电话!");
    return;
  }

  if(User_Info_Edit.Birthday.value.Trim().length < 1)
  {
    alert("请输入入职时间!");
    return;
  }


  if(confirm("信息无误,确定编辑?"))
  {
  	location = "User_Info.do?Cmd=11&Id=<%=Id%>&Sid=<%=Sid%>&Func_Corp_Id=<%=currStatus.getFunc_Corp_Id()%>&Dept_Id="
  	         + User_Info_Edit.Dept_Id.value
  	         + "&CName=" + User_Info_Edit.CName.value
  	         + "&Tel=" + User_Info_Edit.Tel.value
  	         + "&Birthday=" + User_Info_Edit.Birthday.value
  	         + "&Project_Id=" + User_Info_Edit.Project_Id.value
  	         + "&Status=" + User_Info_Edit.Status.value;
  }
}

function doPwdEdit()
{
	if(confirm("确认将密码重置为111111?"))
	{
		m_PwdEdit = createXHR();
		if(m_PwdEdit)
		{
			m_PwdEdit.onreadystatechange = callbackForPwdEdit;
			var url = "PwdEdit.do?Cmd=24&Sid=<%=Sid%>&Id=<%=Id%>&Pwd=<%=Pwd%>&NewPwd=111111&Func_Corp_Id=<%=currStatus.getFunc_Corp_Id()%>&currtime="+new Date();
			m_PwdEdit.open("get", url);
			m_PwdEdit.send(null);
		}
		else
		{
			alert("浏览器不支持，请更换浏览器！");
		}
	}
}

function callbackForPwdEdit()
{
	if(m_PwdEdit.readyState == 4)
	{
		if(m_PwdEdit.status == 200)
		{
			var returnValue = m_PwdEdit.responseText;
			if(returnValue != null && returnValue == '0000')
      {    	
      		alert('重置成功');     
      }
      else if(returnValue != null && returnValue == '1001')
      {
      		alert('失败,密码错误');
      }
      else
      {
          alert("失败,请重新操作");
      }
		}
		else
		{
			  alert("失败,请重新操作");
		}
	}
}

function doNO()
{
	location = "User_Info.jsp?Sid=<%=Sid%>";
}
</SCRIPT>
</html>