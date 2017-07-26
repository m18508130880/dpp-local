<%@ page contentType="text/html; charset=gb2312" %>
<%@ page import="java.util.*" %>
<%@ page import="bean.*" %>
<%@ page import="util.*" %>
<%@ page import="java.lang.*" %>
<%@ page import="java.math.*" %>
<%@ page import="java.text.*" %>
<%@ taglib uri="/WEB-INF/limitvalidatetag.tld" prefix="Limit"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>管线查询</title>
<meta http-equiv="x-ua-compatible" content="ie=7"/>
<link type="text/css" href="../skin/css/style.css" rel="stylesheet"/>
<script type='text/javascript' src='../skin/js/zDrag.js'   charset='gb2312'></script>
<script type='text/javascript' src='../skin/js/zDialog.js' charset='gb2312'></script>
</head>
<%
	
	String Sid               = CommUtil.StrToGB2312(request.getParameter("Sid"));				
  CurrStatus currStatus    = (CurrStatus)session.getAttribute("CurrStatus_" + Sid);
  ArrayList  Dev_GX        = (ArrayList)session.getAttribute("Dev_GX_" + Sid); 
  ArrayList  Project_list  = (ArrayList)session.getAttribute("Project_Info_" + Sid); 
  int sn = 0;  
  
%>
<body style=" background:#CADFFF">
<form name="Admin_DevGX_Info"  action="Admin_DevGX_Info.do" method="post" target="mFrame">
<div id="down_bg_2">
	<table width="100%" style='margin:auto;' border=0 cellPadding=0 cellSpacing=0 bordercolor="#3491D6" borderColorDark="#ffffff">		
		<tr height='25px' class='sjtop'>
			<td width='70%' align='left'>
				管线类型:												
				<select  name='Func_Type_Id' style='width:100px;height:21px' onChange="doSelect()" >										
					  <option value="999" <%=currStatus.getFunc_Type_Id().equals("999")?"selected":""%>  >所有管列表</option>				
					  <option value="YG"  <%=currStatus.getFunc_Type_Id().equals("YG")?"selected":""%>   >雨水管列表</option>
					  <option value="WG"  <%=currStatus.getFunc_Type_Id().equals("WG")?"selected":""%>   >污水管列表</option>
				</select>
				
				所属项目:
				<select  name='Func_Corp_Id' style='width:100px;height:21px' onChange="doSelect()" >										
						<option value="888" <%=currStatus.getFunc_Corp_Id().equals("")?"selected":""%>     >所有项目</option>				
						<%
							if(null != Project_list)
						  {
								  Iterator iterator = Project_list.iterator();
									while(iterator.hasNext())
									{
										 ProjectInfoBean statBean = (ProjectInfoBean)iterator.next();
										 if(!"1000".equals(statBean.getId())){
										 
						%>
						<option value='<%=statBean.getId()%>' <%=currStatus.getFunc_Corp_Id().equals(statBean.getId())?"selected":""%>><%=statBean.getCName()%></option>
						<%
								     }
									}
						  }
						%>
				</select>
				</td>
			<td width='20%' align='right'>		
				<img id="img1" src="../skin/images/mini_button_search.gif" onClick='doSelect()' style='cursor:hand;'>
				<img id="img2" src="../skin/images/excel.gif"         onClick='doExport()' >
			</td>
		</tr>		
		<tr height='30' valign='middle'>
			<td width='100%' align='center' colspan=2>
				<table width="100%" border=1 cellPadding=0 cellSpacing=0 bordercolor="#3491D6" borderColorDark="#ffffff">
					<tr height='30'>
						<td width='10%'  align='center' ><strong>编    码</strong></td>
						<td width='10%'  align='center' ><strong>直    径</strong></td>
						<td width='10%'  align='center' ><strong>长    度</strong></td>
						<td width='20%'  align='center' ><strong>起端管井</strong></td>
						<td width='20%'  align='center' ><strong>结端管井</strong></td>
						<td width='10%'  align='center' ><strong>材料类型</strong></td>
						<td width='10%'  align='center' ><strong>所属项目</strong></td>
						<td width='10%'  align='center' ><strong>设备名称</strong></td>
					</tr>
		<%
					if(null != Dev_GX)
					{
						Iterator deviter = Dev_GX.iterator();
						while(deviter.hasNext())
						{
						  sn++;
							DevGXBean dBean = (DevGXBean)deviter.next();
							String Project_Name ="";
			
							String Equip_Name = dBean.getEquip_Name();
							if(Equip_Name == null ){Equip_Name = "无";}
							String Id = dBean.getId();
					%>   		    	
					<tr height='30' <%=((sn%2)==0?"class='table_blue'":"class='table_white_l'")%>>
						<td  align=center style="cursor:hand " onmouseout="this.style.color='#000000';" onmouseover="this.style.color='#FF0000';"  title="点击查看" onClick="doEdit('<%=Id%>')"><U><%=Id%>&nbsp; </U></td>
						<td><%= dBean.getDiameter()%> &nbsp; </td>
						<td><%= dBean.getLength()%> &nbsp; </td>
						<td><%= dBean.getStart_Id()%>&nbsp;  </td>
						<td><%= dBean.getEnd_Id()%> &nbsp; </td>
						<td><%= dBean.getMaterial()%> &nbsp; </td>	
						<td><%= dBean.getProject_Name()%> &nbsp; </td>	
						<td><%= Equip_Name %> &nbsp; </td>	
					</tr>													
					<%
						}
					}
					for(int i=0;i<(MsgBean.CONST_PAGE_SIZE - sn);i++)
					{
						if(sn % 2 != 0)
					  {
					%>				  
				      <tr <%=((i%2)==0?"class='table_blue'":"class='table_white_l'")%>>
				      	<td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>
				      </tr>
					<%
						}
					  else
					  {
					%>				
	            <tr <%=((i%2)==0?"class='table_white_l'":"class='table_blue'")%>>
		            <td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>
		          </tr>	        
					<%
       			}
     			}
					%> 
				<tr <%=((sn%2)==0?"class='table_blue'":"class='table_white_l'")%>>
				<td colspan="9" class="table_deep_blue" >
					 <table width="100%" height="20"  border="0" cellpadding="0" cellspacing="0" >
				    	<tr valign="bottom">
				      	 <td nowrap><%=currStatus.GetPageHtml("Admin_DevGX_Info")%></td>
				    	</tr>			    		
					 </table>
				</td>
		  </tr>					
										
				</table>
			</td>
		</tr>
	</table>
</div>
<input type="hidden" name="Cmd" value="0">
<input type="hidden" name="Sid" value="<%=Sid%>">
<input type="hidden" name="CurrPage" value="<%=currStatus.getCurrPage()%>">
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
	Admin_DevGX_Info.submit();
}

function GoPage(pPage)
{
	if(pPage == "")
	{
		 alert("请输入目标页面的数值!");
		 return;
	}
	if(pPage < 1)
	{
	   	alert("请输入页数大于1");
		  return;	
	}
	if(pPage > <%=currStatus.getTotalPages()%>)
	{
		pPage = <%=currStatus.getTotalPages()%>;
	}
	Admin_DevGX_Info.CurrPage.value = pPage;
	Admin_DevGX_Info.submit();
}

//数据导出
var req = null;
function doExport()
{	
	if(0 == <%=sn%>)
	{
		alert('当前无记录!');
		return;
	}
	
	if(confirm("确定导出?"))
  {
		if(window.XMLHttpRequest)
	  {
			req = new XMLHttpRequest();
		}
		else if(window.ActiveXObject)
		{
			req = new ActiveXObject("Microsoft.XMLHTTP");
		}		
		//设置回调函数
		req.onreadystatechange = callbackForName;
		var url = "Admin_File_GX_Export.do?Sid=<%=Sid%>&Func_Corp_Id="+Admin_DevGX_Info.Func_Corp_Id.value+"&Func_Type_Id="+Admin_DevGX_Info.Func_Type_Id.value;
		req.open("post",url,true);
		req.send(null);
		return true;
	}
}
function callbackForName()
{
	var state = req.readyState;
	if(state==4)
	{
		var resp = req.responseText;			
		var str = "";
		if(resp != null)
		{
			location.href = "../files/excel/" + resp + ".xls";
		}
	}
}

function doEdit(pId)
{  
	location = "Dev_GX_Edit.jsp?Sid=<%=Sid%>&Id="+pId;
}

</SCRIPT>
</html>