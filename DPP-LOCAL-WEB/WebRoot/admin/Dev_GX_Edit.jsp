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
	
	String     Sid            = CommUtil.StrToGB2312(request.getParameter("Sid"));
	CurrStatus currStatus     = (CurrStatus)session.getAttribute("CurrStatus_" + Sid);
  ArrayList  Dev_GX        = (ArrayList)session.getAttribute("Dev_GX_" + Sid); 
  
 	 String Id = request.getParameter("Id"); 
 	 String Project_Name = ""; 
	 String Diameter = "";
	 String Length = "";
	 String Start_Id = "";
	 String End_Id = "";
	 String Material = "";
	 String Equip_Name = "";
	 
	if(Dev_GX != null)
	{
		Iterator iterator = Dev_GX.iterator();
		while(iterator.hasNext())
		{
			DevGXBean devGXBean = (DevGXBean)iterator.next();
			if(devGXBean.getId().equals(Id))
			{
				 Project_Name = devGXBean.getProject_Name();
				 Diameter = devGXBean.getDiameter();	
			 	 Length= devGXBean.getLength();	
			 	 Start_Id= devGXBean.getStart_Id();	
			   End_Id = devGXBean.getEnd_Id();
			   Material = devGXBean.getMaterial();	
			   Equip_Name = devGXBean.getEquip_Name();
			   if(Equip_Name == null ){ Equip_Name = "无";}
			   			
			}
		}
 	} 
%>
<body style="background:#CADFFF" >
<form name="Dev_GX_Edit"  method="post" target="mFrame" enctype="multipart/form-data">
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
							<td width='20%' align='center'>管井编码</td>
							<td width='30%' align='left'>
								<%=Id%>
							</td>
							<td width='20%' align='center'>所属项目</td>
							<td width='30%' align='left'>
								 <%=Project_Name%>
							</td>
						</tr>
						<tr height='30'>
							<td width='20%' align='center'>直径</td>
							<td width='30%' align='left'>
								<input type='text' name='Diameter' style='width:96%;height:20px;' value='<%=Diameter%>' maxlength='6'>
							</td>
							<td width='20%' align='center'>长度</td>
							<td width='30%' align='left'>
								<input type='text' name='Length' style='width:96%;height:20px;' value='<%=Length%>' maxlength='6'>
							</td>
						</tr>	
						<tr height='30'>
							<td width='20%' align='center'>起端管井</td>
							<td width='30%' align='left'>
								<input type='text' name='Start_Id' style='width:96%;height:20px;' value='<%=Start_Id%>' maxlength='6'>
							</td>
							<td width='20%' align='center'>终端管井</td>
							<td width='30%' align='left'>
								<input type='text' name='End_Id' style='width:96%;height:20px;' value='<%=End_Id%>' maxlength='6'>
							</td>
						</tr>
						<tr height='30'>
							<td width='20%' align='center'>材料类型</td>
							<td width='30%' align='left'  >
								<input type='text' name='Material' style='width:96%;height:20px;' value='<%=Material%>' maxlength='11'>
							</td>
							<td width='20%' align='center'>设备名称</td>
							<td width='30%' align='left'  >
								<%=Equip_Name%>
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

function doEdit()
{
  if(confirm("信息无误,确定编辑?"))
  {
  	location = "Admin_DevGX_Info.do?Cmd=11&Id=<%=Id%>&Sid=<%=Sid%>&Diameter="
  	         + Dev_GX_Edit.Diameter.value
  	         + "&Length=" + Dev_GX_Edit.Length.value
  	         + "&Start_Id=" + Dev_GX_Edit.Start_Id.value
  	         + "&End_Id=" + Dev_GX_Edit.End_Id.value
  	         + "&Material=" + Dev_GX_Edit.Material.value
  }
}

function doNO()
{
	location = "Dev_GX.jsp?Sid=<%=Sid%>";
}

</SCRIPT>
</html>