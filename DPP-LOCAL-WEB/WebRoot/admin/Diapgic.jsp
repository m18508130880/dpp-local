<%@ page contentType="text/html; charset=gb2312"%>
<%@ page import="java.util.*"%>
<%@ page import="bean.*"%>
<%@ page import="util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>中海油LNG加气站公司级信息化管理平台</title>
<link type="text/css" href="../skin/css/style.css" rel="stylesheet"/>
<script type="text/javascript" src="../skin/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="../skin/js/util.js"></script>
<script type="text/javascript" src="http://cdn.hcharts.cn/jquery/jquery-1.8.3.min.js"></script>
<script type="text/javascript" src="http://cdn.hcharts.cn/highcharts/highcharts.js"></script>
<script type="text/javascript" src="http://cdn.hcharts.cn/highcharts/exporting.js"></script>
</head>
<%
	
	String Sid = CommUtil.StrToGB2312(request.getParameter("Sid"));
  CurrStatus currStatus = (CurrStatus)session.getAttribute("CurrStatus_" + Sid);
  ArrayList    Project_Info   = (ArrayList)session.getAttribute("Project_Info_" + Sid);
	ArrayList    MoreDev_GX   = (ArrayList)session.getAttribute("MoreDev_GX_" + Sid);
	ArrayList    MoreDev_GJ   = (ArrayList)session.getAttribute("MoreDev_GJ_" + Sid);
	String Dev_GX_Diameter = "";
	String data1 = "";
	String data2 = "";
	String data3 = "";
	String data4 = "";		
	String data5 = "";	
%>
<body  style=" background:#CADFFF">
<form name="Gj_File" action="File_GJ.do" method="post" target="mFrame" enctype="multipart/form-data">
	<table width="100%" style='margin:auto;' border=0 cellPadding=0 cellSpacing=0 bordercolor="#3491D6" borderColorDark="#ffffff">
		<tr height='100%' valign='middle'>
			<td align=center>
				<font color='red'><Strong>选择管段</Strong></font>
				<select  name='Func_Name_Id' style='width:100px;height:21px' onChange="doSelect()" >										
						 <option value="999"   >管线1</option>									 						
				</select>
			</td>
		</tr>				
	 </table> 
  <div  id="container" style="min-width:700px;height:450px"></div>
</form>
</body>
<script LANGUAGE="javascript">
$(function () {
    $('#container').highcharts({

        chart: {
            type: 'column'
        },

        title: {
            text: 'Water Elevation Profile: Node GJ123-134'
        },

        xAxis: {
        	tickmarkPlacement:'on',
        	title:	{
        		text: 'Distance(m)'
        		},
             categories: [
             10,
 <%
 		if(null != MoreDev_GX)
 		{
 			 Iterator iterator = MoreDev_GX.iterator();
			 while(iterator.hasNext())
			  {
						DevGXBean xBean = (DevGXBean)iterator.next();
						Dev_GX_Diameter = xBean.getLength();
	%>
			<%=  Dev_GX_Diameter%>
	<%					
						
			}
 		} 
 	
 %>             
            ]
        },
<%if(null != MoreDev_GJ)
 		{
 			 Iterator iter = MoreDev_GJ.iterator();
			 while(iter.hasNext())
			  {
						DevGJBean jBean = (DevGJBean)iter.next();
						
						data1 += jBean.getTop_Height()+",";		
						
						data2 += jBean.getBase_Height()+",";	
						
					
			}
 		}%>	
        yAxis: {
            allowDecimals: true,
            min: 0,
            title: {
                text: 'Elevation(m)'
            }
        },

        tooltip: {
            formatter: function() {
                return '<b>'+ this.x +'</b><br/>'+
                    this.series.name +': '+ this.y +'<br/>'+
                    'Total: '+ this.point.stackTotal;
            }
        },

        plotOptions: {
            column: {
                stacking: 'normal',
                pointPadding: 0,
       					borderWidth: 0,
        				pointWidth: 10
            }
        },				
        series: [
        //柱状图               
        {//实际显示图  
        		type:'column',
            name: '顶高',
            colorByPoint:false,
           	color:"green",
           	shadow: true,
            data: [<%=data1%>],
            stack: 'male'
        }, 
        {//掩盖图
            name: '底高+水位',
            colorByPoint:false,
            color:"#A6FFFF",
            shadow: true,
            data: [<%=data3%>],
            stack: 'male'
        },
        {//掩盖图
            name: '底高',
            colorByPoint:false,
            color:"white",
            data: [<%=data2%>],
            stack: 'male'
        },
        //线
        {
          	type:'line',
            name: '顶线',
            dashStyle: 'longdash',
            data: [<%=data1%>]
        },
        //区域图
        {//显示区域
          	type:'area',
            name: 'Tokyo',
            colorByPoint:false,
            color:"#A6FFFF",
            stack: 'tatl',
            data: [<%=data3%>]
        },
        {//掩盖区域
          	type:'area',
            name: 'Tokyo',
            colorByPoint:false,
            color:"#FDFFFF",
            stack: 'tatl',
            data: [<%=data2%>]
        }]      
    });  
});

</script>
</html>