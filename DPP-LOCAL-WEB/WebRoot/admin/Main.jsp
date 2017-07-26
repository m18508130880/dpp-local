<%@ page contentType="text/html; charset=gb2312" %>
<%@ page import="java.util.*" %>
<%@ page import="bean.*" %>
<%@ page import="rmi.*" %>
<%@ page import="util.*" %>
<%@page import="java.awt.*, java.awt.image.*, java.io.*, com.sun.image.codec.jpeg.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>百事达排水管网信息管理平台</title>
<meta http-equiv="x-ua-compatible" content="ie=7"/>
<link type="text/css" href="../skin/css/style.css" rel="stylesheet"/>
<script language=javascript>document.oncontextmenu=function(){window.event.returnValue=false;};</script>
<%
	
	String Sid = CommUtil.StrToGB2312(request.getParameter("Sid"));
	
%>
</head>

<frameset rows="111,*" cols="*" frameborder="NO" border="0" framespacing="0px">
	
 	 		<frame src="main_top.jsp?Sid=<%=Sid%>" name="tFrame" scrolling="NO" noresize >
 	 
		  <frameset id="glb_frm" rows="*" cols="184,11,*" framespacing="0px" frameborder="NO" border="0">
		  			  	
				<frame src="Main_left.jsp?Sid=<%=Sid%>" name="lFrame"  frameborder="no" scrolling="no"   noResize>
				
				<frame src="open.jsp"                   name="ctr_frm" id="ctr_frm"     scrolling="no"   noResize>
				
				<frame src="Corp_Info.jsp?Sid=<%=Sid%>" name="mFrame"  frameborder="no" scrolling="auto" noResize>
				
				
		  </frameset>
		  
</frameset>

</html>