<%@ page contentType="text/html; charset=gb2312"%>
<%@ page import="java.util.*"%>
<%@ page import="bean.*"%>
<%@ page import="util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>添加项目信息</title>
<meta http-equiv="x-ua-compatible" content="ie=7"/>
<link type="text/css" href="../skin/css/style.css" rel="stylesheet"/>
<script type="text/javascript" src="../skin/js/util.js"></script>
<script type="text/javascript" src="../skin/js/My97DatePicker/WdatePicker.js"></script>
<script language=javascript>document.oncontextmenu=function(){window.event.returnValue=false;};</script>
</head>
<%
	String       Sid            = CommUtil.StrToGB2312(request.getParameter("Sid"));
	CurrStatus   currStatus     = (CurrStatus)session.getAttribute("CurrStatus_" + Sid);
  ArrayList    Equipment_Info = (ArrayList)session.getAttribute("Equipment_Info_" + Sid);
  ArrayList    Project_Info   = (ArrayList)session.getAttribute("Project_Info_" + Sid);
  ArrayList    Select_GJ      = (ArrayList)session.getAttribute("Map_GJ_" + Sid); 
  
%>
<body style="background:#CADFFF">
<form name="Equipment_Info_Add"  action="Admin_Equipment_Info.do" method="post" target="mFrame">
<div id="down_bg_2">
	<div id="cap"><img src="../skin/images/cap_user_info.gif"></div><br><br><br>
	<div id="right_table_center">
		<table width="60%" style='margin:auto;' border=0 cellPadding=0 cellSpacing=0 bordercolor="#3491D6" borderColorDark="#ffffff">
			<tr height='30'>
				<td width='100%' align='right'>
					<img src="../skin/images/mini_button_submit.gif" style='cursor:hand;' onclick='doAdd()'>
					<img src="../skin/images/button10.gif"           style='cursor:hand;' onclick='doNO()'>
				</td>
			</tr>
			<tr height='30'>
				<td width='100%' align='center'>
					<table width="100%" border=1 cellPadding=0 cellSpacing=0 bordercolor="#3491D6" borderColorDark="#ffffff">
						<tr height='30'>
							<td width='20%' align='center'>ID&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;号</td>
							<td width='30%' align='left'>
								<table width='98%'>
									<tr>
										<td width='60%' align='left'><input type='text' name='Id' style='width:98%;height:18px;' value='' maxlength='20' onkeyup="doCheck(this.value)"><td>
										<td width='40%' align='left' id='ErrorMsg' style="color:red;">&nbsp;</td>
									</tr>
								</table>
							</td>
							<td width='20%' align='center'>设备名称</td>
							<td width='30%' align='left'>
									<input type='text' name='CName' style='width:96%;height:20px;' value='' maxlength='6'>							        
							</td>
						</tr>					
						<tr height='30'>
							<td width='20%' align='center'>所属项目</td>
							<td width='30%' align='left'>
								<select name="Project_Id" style="width:97%;height:20px" onChange="doGJselect(this.value)">
									 <option value="8989"  >请选择项目</option>
								<%
 	                if(Project_Info != null){
		  								Iterator iterator = Project_Info.iterator();
											while(iterator.hasNext()){
											ProjectInfoBean statBean = (ProjectInfoBean)iterator.next();
											String Pro_Id = statBean.getId();
											String Pro_Name = statBean.getCName();					
								%>
								    <option value="<%=Pro_Id%>"  > <%=Pro_Name%></option>
								<%
		    						 }
									}
								%>
								</select>
							</td>	
							<td width='20%' align='center'>管井编码</td>
							<td width='30%' align='left'>						
								<select id="GJ_Select" style="width:97%;height:20px" > 
									
								</select>
							</td>	
						</tr>		
							<td width='20%' align='center'>设备简介</td>
							<td width='30%' align='left'>
								<input type='text' name='Demo' style='width:96%;height:20px;' value='' maxlength='11'>
							</td>
						</tr>		
												
					</table>
				</td>
			</tr>
		</table>
	</div>
</div>
<input name="Cmd" type="hidden" value="10">
<input name="Sid" type="hidden" value="<%=Sid%>">
<input name="GJ_Id"  type="hidden" value="">
</form>
</body>

<SCRIPT LANGUAGE=javascript>
var Flag = 0;
//自定义帐号检测
function doCheck(Id)
{
	if(Id.Trim().length == 0)if(null != Select_GJ)
		{
			Iterator deviter = Select_GJ.iterator();
			while(deviter.hasNext())
			{
				DevGJ3Bean devBean  = (DevGJ3Bean)deviter.next();
				String Project_Id  = devBean.getProject_Id();
				String Id          = devBean.getId();dow.XMLHttpRequest)
  {
			req = new XMLHttpRequest();
  }
  else if(window.ActiveXObject)
  {
			req = new ActiveXObject("Microsoft.XMLHTTP");
  }		
	//设置回调函数
	req.onreadystatechange = callbackCheckName;
	var url = "Admin_IdCheck.do?Id="+Id+"&Sid=<%=Sid%>";
	req.open("post",url,true);
	req.send(null);
	return true;
}
function callbackCheckName()
{
		var state = req.readyState;
		if(state==4)
		{
			var resp = req.responseText;			
			var str = "";
			if(resp != null && resp == '0000')
			{
				 document.getElementById("ErrorMsg").innerText = " √ 可用!";
				 Flag = 1;
				 return;
			}
			else if(resp != null && resp == '3006')
			{
				 document.getElementById("ErrorMsg").innerText = " X 已存在!";
				 Flag = 0;
				 return;
			}
		}
}

function doAdd()
{
	if(Flag == 0)
  {
  	alert("项目ID名称重复或有误，请重新输入！");
  	return;
  }
  if(Equipment_Info_Add.CName.value.Trim().length < 1)
  {
    alert("请输入项目名称!");
    return;
  }
  
  
  if(confirm("信息无误,确定添加?"))
  {
  	Equipment_Info_Add.GJ_Id.value=Equipment_Info_Add.GJ_Select.value;
  	Equipment_Info_Add.submit();
  	
  }
}

function doNO()
{
	location = "Equipment_Info.jsp?Sid=<%=Sid%>";
}


function doGJselect(pProId)
{
	//先删除
	var length = document.getElementById('GJ_Select').length;
	for(var i=0; i<length; i++)
	{
		document.getElementById('GJ_Select').remove(0);
	}
	//再添加
		<%
		if(null != Select_GJ)
		{
			Iterator deviter = Select_GJ.iterator();
			while(deviter.hasNext())
			{
				DevGJBean devBean  = (DevGJBean)deviter.next();
				String Project_Id  = devBean.getProject_Id();
				String Id          = devBean.getId();
		%>
				if('<%=Project_Id%>' == pProId)
				{
					var objOption = document.createElement('OPTION');
					objOption.value = '<%=Id%>';
					objOption.text  = '<%=Id%>';
					document.getElementById('GJ_Select').add(objOption);
				}
		<%
			}
		}
		%>
}
</SCRIPT>
</html>