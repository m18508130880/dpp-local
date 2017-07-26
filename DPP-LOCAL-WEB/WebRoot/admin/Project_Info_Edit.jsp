<%@ page contentType="text/html; charset=gb2312"%>
<%@ page import="java.util.*"%>
<%@ page import="bean.*"%>
<%@ page import="util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>编辑项目信息</title>
<meta http-equiv="x-ua-compatible" content="ie=7"/>
<link type="text/css" href="../skin/css/style.css" rel="stylesheet"/>
<script type="text/javascript" src="../skin/js/util.js"></script>
<script type="text/javascript" src="../skin/js/My97DatePicker/WdatePicker.js"></script>
<script language=javascript>document.oncontextmenu=function(){window.event.returnValue=false;};</script>
</head>
<%
	
	String Sid = CommUtil.StrToGB2312(request.getParameter("Sid"));
	CurrStatus currStatus = (CurrStatus)session.getAttribute("CurrStatus_" + Sid);
  ArrayList    Project_Info   = (ArrayList)session.getAttribute("Project_Info_" + Sid);

 	String Id = request.getParameter("Id");
  String CName = "";
  String Longitude = "";
	String Latitude = "";
	String MapLev = "";
	String MapAngle = "";
	String Demo = "";
	if(Project_Info != null)
	{
		Iterator iterator = Project_Info.iterator();
		while(iterator.hasNext())
		{
			ProjectInfoBean statBean = (ProjectInfoBean)iterator.next();
			if(statBean.getId().equals(Id))
			{
					CName = statBean.getCName();
					Longitude = statBean.getLongitude();
					Latitude = statBean.getLatitude();
					MapLev = statBean.getMapLev();
					MapAngle = statBean.getMapAngle();
					Demo = statBean.getDemo();
					
			}
		}
 	} 
%>
<body style="background:#CADFFF">
<form name="Project_Info_Edit"  action="Admin_Project_Info.do" method="post" target="mFrame" enctype="multipart/form-data">
<div id="down_bg_2">
	<div id="cap"><img src="../skin/images/cap_user_info.gif"></div><br><br><br>
	<div id="right_table_center">
		<table width="60%" style='margin:auto;' border=0 cellPadding=0 cellSpacing=0 bordercolor="#3491D6" borderColorDark="#ffffff">
			<tr height='30'>
				<td width='100%' align='right'>
				  <img src="../skin/images/mini_button_submit.gif"    style='cursor:hand;' onClick='doEdit()'>
					<img src="../skin/images/button10.gif"              style='cursor:hand;' onclick='doNO()'>
				</td>
			</tr>
			<tr height='30'>
				<td width='100%' align='center'>
					<table width="100%" border=1 cellPadding=0 cellSpacing=0 bordercolor="#3491D6" borderColorDark="#ffffff">
						<tr height='30'>
							<td width='20%' align='center'>ID&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;号</td>
							<td width='30%' align='left'>
								<%=Id%>
							</td>
							<td width='20%' align='center'>项目名称</td>
							<td width='30%' align='left'>
								<input type='text' name='CName' style='width:96%;height:20px;' value='<%=CName%>' maxlength='6'>
							</td>
						</tr>
						<tr height='30'>
							<td width='20%' align='center'>经&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;度</td>
							<td width='30%' align='left'>
								<input type='text' name='Longitude' style='width:96%;height:20px;' value='<%=Longitude%>' maxlength='11'>
							</td>
							<td width='20%' align='center'>纬&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;度</td>
							<td width='30%' align='left'>
								<input type='text' name='Latitude' style='width:96%;height:20px;' value='<%=Latitude%>' maxlength='11'>
							</td>
						</tr>		
						
						<tr height='30'>
							<td width='20%' align='center'>地图倍数</td>
							<td width='30%' align='left'>
								<input type='text' name='MapLev' style='width:96%;height:20px;' value='<%=MapLev%>' maxlength='11'>
							</td>
							<td width='20%' align='center'>展示角度</td>
							<td width='30%' align='left'>
								<input type='text' name='MapAngle' style='width:96%;height:20px;' value='<%=MapAngle%>' maxlength='11'>
							</td>
						</tr>	
						<tr height='30'>
							<td width='20%' align='center'>项目描述</td>
							<td width='30%' align='left'>
								<input type='text' name='Demo' style='width:96%;height:20px;' value='<%=Demo%>' maxlength='11'>
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

function doNO()
{
	location = "Project_Info.jsp?Sid=<%=Sid%>";
}

function doEdit()
{

  if(Project_Info_Edit.CName.value.Trim().length < 1)
  {
    alert("请输入姓名!");
    return;
  }

  if(confirm("信息无误,确定编辑?"))
  {
  	location = "Project_Info.do?Cmd=11&Id=<%=Id%>&Sid=<%=Sid%>&CName="
  	         + Project_Info_Edit.CName.value
  	         + "&Longitude=" + Project_Info_Edit.Longitude.value
  	         + "&Latitude=" + Project_Info_Edit.Latitude.value
  	         + "&MapLev=" + Project_Info_Edit.MapLev.value
  	         + "&MapAngle=" + Project_Info_Edit.MapAngle.value
  	         + "&Demo=" + Project_Info_Edit.Demo.value;
  }
}

</SCRIPT>
</html>