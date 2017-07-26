<%@ page contentType="text/html; charset=gb2312" %>
<%@ page import="java.util.*" %>
<%@ page import="bean.*" %>
<%@ page import="util.*" %>
<%@ page import="java.lang.*" %>
<%@ page import="java.math.*" %>
<%@ page import="java.text.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>中海油LNG加气站公司级信息化管理平台</title>
<meta http-equiv="x-ua-compatible" content="ie=7"/>
<link type="text/css" href="../skin/css/style.css" rel="stylesheet"/>
<script type="text/javascript" src="../skin/js/excanvas.min.js"></script>
<script type="text/javascript" src="../skin/js/jquery-1.4.2.js"></script>
<script type="text/javascript" src="../skin/js/jquery.flot.js"></script>
<script type="text/javascript" src="../skin/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="../skin/js/util.js"></script>
<script language=javascript>document.oncontextmenu=function(){window.event.returnValue=false;};</script>
</head>
<%
	
	String Sid = CommUtil.StrToGB2312(request.getParameter("Sid"));
  CurrStatus currStatus = (CurrStatus)session.getAttribute("CurrStatus_" + Sid);
  String BeginD = currStatus.getVecDate().get(0).toString().substring(0,10);
  UserInfoBean UserInfo = (UserInfoBean)session.getAttribute("UserInfo_" + Sid);
	ArrayList User_Manage_Role   = (ArrayList)session.getAttribute("User_Manage_Role_" + Sid);
	ArrayList User_Device_Detail = (ArrayList)session.getAttribute("User_Device_Detail_" + Sid);
	String ManageId = UserInfo.getManage_Role();
  int Year = Integer.parseInt(CommUtil.getDate().substring(0,4));
  int Month = Integer.parseInt(CommUtil.getDate().substring(5,7));
  if(null != (String)session.getAttribute("Year_" + Sid) && ((String)session.getAttribute("Year_" + Sid)).trim().length() > 0){Year = CommUtil.StrToInt((String)session.getAttribute("Year_" + Sid));}
  if(null != (String)session.getAttribute("Month_" + Sid) && ((String)session.getAttribute("Month_" + Sid)).trim().length() > 0){Month = CommUtil.StrToInt((String)session.getAttribute("Month_" + Sid));}
  
  String Graph = (String)session.getAttribute("Graph_" + Sid);
  	String Manage_List = "";
								if(ManageId.length() > 0 && null != User_Manage_Role)
								{
									Iterator iterator = User_Manage_Role.iterator();
									while(iterator.hasNext())
									{
										UserRoleBean statBean = (UserRoleBean)iterator.next();
										if(statBean.getId().substring(0,4).equals(ManageId) && statBean.getId().length() == 8)
										{
											String R_Point = statBean.getPoint();
											if(null == R_Point){R_Point = "";}
											Manage_List += R_Point;
										}
									}
								}
								String Dept_Id = UserInfo.getDept_Id();
								if(Dept_Id.length()>3){Manage_List = Dept_Id; }
%>
<body style="background:#CADFFF" onload="doChangeSelect('<%=currStatus.getFunc_Sub_Id()%>')">
<form name="Graph"  action="Graph.do" method="post" target="mFrame">
<div id='down_bg_2'>
	<table style='margin:auto' border=0 cellPadding=0 cellSpacing=0 bordercolor="#3491D6" borderColorDark="#ffffff" width='100%'>  
	  <tr height='25px' class='sjtop'>
	    <td width="70%" align="left"> 
	    	加气站点:
				<select  name='Func_Cpm_Id' style='width:100px;height:20px' onChange="doSelect()" >					
						<%
												if(Manage_List.length() > 0 && null != User_Device_Detail)
																	{
																		Iterator iterator = User_Device_Detail.iterator();
																		while(iterator.hasNext())
																		{
																			EquipInfoBean statBean = (EquipInfoBean)iterator.next();
																			if(Manage_List.contains(statBean.getId()))
																			{
											%>
											<option value='<%=statBean.getId()%>' <%=currStatus.getFunc_Cpm_Id().equals(statBean.getId())?"selected":""%>><%=statBean.getBrief()%></option>
								<%
										}
									}
								}
								%>
				</select>
				时日均值:    
	      <select name="Func_Sub_Id"  style="width:90px;height:20px" onChange="doChangeSelect(this.value)">
	        <option value="1" <%=(currStatus.getFunc_Sub_Id() == 1 ?"SELECTED":"")%>>时均值</option>
	        <option value="2" <%=(currStatus.getFunc_Sub_Id() == 2 ?"SELECTED":"")%>>日均值</option>
	      </select>
	      <input id='BeginD' name='BeginD' type='text' style='width:90px;height:18px;display:none;' value='<%=BeginD%>' onClick='WdatePicker({readOnly:true})' class='Wdate' size='10' maxlength='10'>
	      <select id="Year" name="Year" style="width:60px;height:20px;display:none;">
        <%
        for(int j=2012; j<=2030; j++)
        {
        %>
          <option value="<%=j%>" <%=(Year == j?"selected":"")%>><%=j%>年</option>
        <%
        }
        %>
        </select>
        <select id="Month" name="Month" style="width:50px;height:20px;display:none;">
        <%
        for(int k=1; k<13; k++)
        {
       	%>
       		<option value="<%=k%>" <%=(Month == k?"selected":"")%>><%=k%>月</option>
       	<%
       	}
       	%>
        </select>
	    </td>
      <td width="30%" align="right">
        <img style="cursor:hand" onClick="doSelect()" src="../skin/images/graph.gif">
      </td>         
    </tr>
  </table><br>
 	<table style='margin:auto' cellpadding='0' cellspacing='0' border='0' width='100%'>
  	<tr>
  		<td width='15%' align='center'>&nbsp</td>
  		<td width='85%' align='left' colspan=2><p id='choices' style='margin-left:25px'></p></td> 		         
    </tr>
    <tr>
    	<td width='15%' align='center'>&nbsp</td>
    	<td width='55%' align='left'><div id='placeholder' style='width:100%;height:350px;'></div></td>
    	<td width='30%' align='left' valign='bottom'>&nbsp;
    		<font color=gray>
    			<%
    			switch(currStatus.getFunc_Sub_Id())
    			{
    				case 1:
    			%>
    						(小时)
    			<%
    					break;
    				case 2:
    			%>
    						(日期)
    			<%
    					break;
    			}
    			%>
    		</font>
    	</td>
    </tr>   
  </table>
	<input name="Cmd"    type="hidden"  value="20">
	<input name="Sid"    type="hidden"  value="<%=Sid%>">
	<input name="Id"     type="hidden"  value="">
	<input name="Level"  type="hidden"  value="">
	<input name="BTime"  type="hidden"  value="">
	<input type="button" id="CurrButton"  onClick="doSelect()" style="display:none">
</div>
</form>
</body>
<SCRIPT LANGUAGE=javascript>
function doChangeSelect(pFunc_Sub_Id)
{
	switch(parseInt(pFunc_Sub_Id))
	{
		case 1:
	  	document.all.BeginD.style.display = '';
	    document.all.Year.style.display = 'none';
	    document.all.Month.style.display = 'none';
	    break;
	 	case 2:
	    document.all.BeginD.style.display = 'none';
	    document.all.Year.style.display = '';
	    document.all.Month.style.display = '';
	    break;
	}
}

function doSelect()
{
  switch(parseInt(Graph.Func_Sub_Id.value))
  {
    case 1://时均值
        Graph.BTime.value = Graph.BeginD.value;
      break;
    case 2://日均值
        Graph.BTime.value = Graph.Year.value + '-' + StrLeftFillZero(Graph.Month.value, 2) +'-01';
      break;
  }
  Graph.Id.value = Graph.Func_Cpm_Id.value;
  Graph.Level.value = '4';
  if('4' != Graph.Level.value)
  {
  	Graph.Id.value = '';
  }
  Graph.submit();
}

var dataset = [];
var data = [];
var data_min;
var data_max;
var max_index;
var min_index;
var highlight = [];
var TUnit = '';
var choiceContainer = $("#choices");
var previousPoint = null;
<%
if(null != Graph && Graph.length() > 4)
{
	Graph = Graph.substring(4);
	String[] temp1 = Graph.split("@");
	for(int i=0; i< temp1.length; i++)
	{
		String[] temp2 = temp1[i].split("&");
	  if(temp2.length > 4)//有数据
	  {
	  	String Brief     = temp2[0];
	    String CName     = temp2[1];       
	    String Attr_Name = temp2[2];
	    String Unit      = temp2[3];
	    if(null == Brief){Brief = "";}
	    if(null == CName){CName = "";}
	    if(null == Attr_Name){Attr_Name = "";}
	    if(null == Unit){Unit = "";}
%>	        
      switch(<%=currStatus.getFunc_Sub_Id()%>)
			{
				 case 1://时均值
				      TUnit = "时";
				 		break;
				 case 2://日均值
				      TUnit = "号";
				 		break;
			}
<%
      for(int j=4; j<temp2.length; j++)
			{
				String[] temp3 = temp2[j].split("\\*");
				String CTime = temp3[0];
				String AvgCData = temp3[1];			
				if(j == 4)
				{
%>
					data_min = '<%=AvgCData%>';
					min_index = 0;
					data_max = '<%=AvgCData%>';
					max_index = 0;
<%
				}
				else
				{
%>
					if('<%=AvgCData%>' < data_min)
					{
						data_min = '<%=AvgCData%>';
						min_index = '<%=j%>';
					}
					if('<%=AvgCData%>' > data_max)
					{
						data_max = '<%=AvgCData%>';
						max_index = '<%=j%>';
					}
<%			
				}
%>
				data.push([<%=CTime%>, <%=AvgCData%>]);			  
<%   	
		  }
%>  	
		  if(data.length > 0)
			{		
				highlight.push([min_index, max_index]);
				dataset.push({'label': '[' + '<%=Attr_Name%>' + '][' + '<%=Unit%>' + ']', 'data': data});
			}
			data = []; 
<%
		}
	}
}
%>

if(0 == dataset.length)
{
	highlight.push([20, 50]);
	dataset.push({'label':'无相关数据', 'data':data});
}
var i = 0;
$.each(dataset, function(key, val)
{
	if(1 == i)
		i++;
	val.color = i;
	++i;
});
showPIG();

$('#placeholder').bind('plothover', function (event, pos, item)
{
	$('#x').text(pos.x.toFixed(2));
	$('#y').text(pos.y.toFixed(2));
	if(item)
	{
		if(previousPoint != item.datapoint)
		{
			previousPoint = item.datapoint;
			$('#tooltip').remove();
			var x = item.datapoint[0].toFixed(2),
			y = item.datapoint[1].toFixed(2);
			var content = '时间:' + parseInt(x) + TUnit + ';均值:' + y;
			showTooltip(item.pageX, item.pageY, content);
		}
	}
	else
	{
		$('#tooltip').remove();
		previousPoint = null;
	}
});

function showPIG() 
{
	var i = 0;
 	$.each(dataset, function(key, val)
 	{
 		if(i%3 == 0)
 		{
 			choiceContainer.append('<br>');
 		}
 		choiceContainer.append('<input type="checkbox" name="' + key +
                                           '" checked="checked" id="id' + key + '">' +
                                           '<label for="id' + key + '">'
                                           + val.label + '</label>');                                         
 		i++;
 	});
 	choiceContainer.find('input').click(plotAccordingToChoices);
 	plotAccordingToChoices();
}

function plotAccordingToChoices() 
{
  var data = [];	
  var hLight = [];
  choiceContainer.find("input:checked").each(function() 
	{
		var key = $(this).attr("name");    
	  if(key && dataset[key])
	  {
	   	 data.push(dataset[key]);
	   	 hLight.push([highlight[key][0], highlight[key][1]]);
	  }
	});
	
  if(data.length > 0)
  {
  	var plot = $.plot($('#placeholder'), data, {	
  	<%
  	if(1 == currStatus.getFunc_Sub_Id())
  	{
  	%>
  		xaxis: {min:0, max:24},
  	<%
  	}
  	else if(2 == currStatus.getFunc_Sub_Id())
  	{
  	%>
  		xaxis: {min:1, max:31},
  	<%
  	}
  	%>
  	yaxis: { tickDecimals: 2},
  	series:{
  	lines: { show: true },
  	points: { show:true, radius:3,  symbol: 'circle'}},
  	grid: { hoverable: true },
  	legend: {noColumns: 1, margin: [-180, 0]}
  	});	
    $.each(hLight, function(key, val)
    {
    	plot.highlight(key, val[0]);
    	plot.highlight(key, val[1]);
    });
  }
}

function showTooltip(x, y, contents) 
{
  $('<div id="tooltip">' + contents + '</div>').css
   ({
   		position: 'absolute',
      display: 'none',
      top: y + 5,
      left: x + 5,
      border: '1px solid #fdd',
      padding: '2px',
      'background-color': '#fee',
      opacity: 0.80
	}).appendTo("body").fadeIn(200);
}
</script>
</html>