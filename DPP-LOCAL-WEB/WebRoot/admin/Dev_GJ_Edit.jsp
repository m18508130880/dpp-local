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
  ArrayList  Dev_GJ         = (ArrayList)session.getAttribute("Dev_GJ_" + Sid); 
 	String Id = request.getParameter("Id"); 
 	String Project_Name = "";
 	String Top_Height= "";
 	String Base_Height= "";
  String In_Id = "";
  String Out_Id = "";
  String Material = "";
  String Equip_Name = "";
	if(Dev_GJ != null)
	{
		Iterator iterator = Dev_GJ.iterator();
		while(iterator.hasNext())
		{
	DevGJBean devGJBean = (DevGJBean)iterator.next();
	if(devGJBean.getId().equals(Id))
	{
		 In_Id = devGJBean.getIn_Id();
		 Out_Id = devGJBean.getOut_Id();	
		 Project_Name = devGJBean.getProject_Name();
	 	 Top_Height= devGJBean.getTop_Height();	
	 	 Base_Height= devGJBean.getBase_Height();	
	   Material = devGJBean.getMaterial();	
	   Equip_Name = devGJBean.getEquip_Name();
	   if(Equip_Name == null ){ Equip_Name = "无";}
	   			
	}
		}
 	}
%>
<body style="background:#CADFFF" >
<form name="Dev_GJ_Edit"  method="post" target="mFrame" enctype="multipart/form-data">
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
							<td width='20%' align='center'>顶部标高</td>
							<td width='30%' align='left'>
								<input type='text' name='Top_Height' style='width:96%;height:20px;' value='<%=Top_Height%>' maxlength='6'>
							</td>
							<td width='20%' align='center'>底部标高</td>
							<td width='30%' align='left'>
								<input type='text' name='Base_Height' style='width:96%;height:20px;' value='<%=Base_Height%>' maxlength='6'>
							</td>
						</tr>	
						<tr height='30'>
							<td width='20%' align='center'>入管编号</td>
							<td width='30%' align='left'>
								<input type='text' name='In_Id' style='width:96%;height:20px;' value='<%=In_Id%>' maxlength='6'>
							</td>
							<td width='20%' align='center'>出管编号</td>
							<td width='30%' align='left'>
								<input type='text' name='Out_Id' style='width:96%;height:20px;' value='<%=Out_Id%>' maxlength='6'>
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
  	location = "Admin_DevGJ_Info.do?Cmd=11&Id=<%=Id%>&Sid=<%=Sid%>&Top_Height="
  	         + Dev_GJ_Edit.Top_Height.value
  	         + "&Base_Height=" + Dev_GJ_Edit.Base_Height.value
  	         + "&In_Id=" + Dev_GJ_Edit.In_Id.value
  	         + "&Out_Id=" + Dev_GJ_Edit.Out_Id.value
  	         + "&Material=" + Dev_GJ_Edit.Material.value
  }
}

function doNO()
{
	location = "Dev_GJ.jsp?Sid=<%=Sid%>";
}

</SCRIPT>
</html>