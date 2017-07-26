<%@ page contentType="text/html; charset=gb2312" %>  
<%@ page import="java.util.*" %>
<%@ page import="bean.*" %>
<%@ page import="util.*" %>
<%@ taglib uri="/WEB-INF/limitvalidatetag.tld" prefix="Limit"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>百事达排水管网信息管理平台</title>
<meta http-equiv="x-ua-compatible" content="ie=7"/>
<link type="text/css" href="../skin/css/style.css" rel="stylesheet"/>
<script type="text/javascript" src="../skin/js/util.js"></script>

<script type="text/javascript" src="../skin/js/day.js"></script>

<%

	String Sid = CommUtil.StrToGB2312(request.getParameter("Sid"));
	ArrayList User_FP_Role = (ArrayList)session.getAttribute("User_FP_Role_" + Sid);
	UserInfoBean UserInfo  = (UserInfoBean)session.getAttribute("UserInfo_" + Sid);
	CorpInfoBean Corp_Info = (CorpInfoBean)session.getAttribute("Corp_Info_" + Sid);
	String Demo = "";
	if(Corp_Info != null)
	{
		Demo = Corp_Info.getDemo();
  	if(Demo == null){Demo = "";}
 	}
%>
</head>

<body TEXT-ALIGN:center >
<div id="PARENT" >	
	<ul id="nav" >
		<% 
						
						if(UserInfo.getId().equals("dzc"))
						{
	%>
		<li><a href="#" onClick="doDiaphic()"    >图表分析</a></li>
		<li><a href="#" onClick="doDiap()"    >图表测试</a></li>
	<%					
			
		}else{
	 %>
		<li id="li01"><a href="#" onClick="doGIS()">GIS监控</a></li>	    	 
	</ul>
</div>
<div id="PARENT" style="background-color:'gray'">	
	<ul id="nav">
		<li id="li02"><a href="#" >气象预告</a></li>	    	 
	</ul>
</div>

<div>
	<iframe name="weather_inc" src="http://i.tianqi.com/index.php?c=code&id=4" width="180" height="206" frameborder="0" marginwidth="0" marginheight="0" scrolling="no" ></iframe>
</div>

<div id="PARENT" >	
	<ul id="nav">
		<li id="li03"><a href="#" >事件公告</a></li>	    	 
	</ul>
</div>

<div align=center>	
	<br/>
  <%=Demo%>
	
</div>
<%}%>
</body>

<script language='javascript'>
//初始化
	window.parent.frames.mFrame.location = 'MapMain_Map.jsp?Sid=<%=Sid%>';
	
//菜单Menu
var LastLeftID = "";
function DoMenu(emid)
{
	 var obj = document.getElementById(emid); 
	 obj.className = (obj.className.toLowerCase() == "expanded"?"collapsed":"expanded");
	 if((LastLeftID!="")&&(emid!=LastLeftID)) //关闭上一个Menu
	 {
	  	document.getElementById(LastLeftID).className = "collapsed";
	 }
	 LastLeftID = emid;
}
//菜单颜色变化
var LastsubID = "";
function DoDisplay(emid)
{
	 var obj = document.getElementById(emid); 
	 obj.className = (obj.className.toLowerCase() == "expanded"?"collapsed":"expanded");
	 if((LastsubID!="")&&(emid!=LastsubID)) //关闭上一个
	 {
	  	document.getElementById(LastsubID).className = "collapsed";
	 }
	 LastsubID = emid;
}

function doGIS()
{
	window.parent.frames.mFrame.location = 'MapMain_Map.jsp?Sid=<%=Sid%>';
}
function doDiaphic()
{	
	window.parent.frames.mFrame.location = "Diapgic.do?Sid=<%=Sid%>&Cmd=4&Func_Corp_Id=HCBWJ002,HCBWJ003,HCBWJ004,HCBWJ005&Id=HCBWG003,HCBWG004,HCBWG005";
}
function doDiap()
{
	window.parent.frames.mFrame.location = "CesText.jsp";
}
</script>
</html>