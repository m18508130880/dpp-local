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
	java.text.DecimalFormat   df   =new   java.text.DecimalFormat("#.00");  
	String Dev_GX_Diameter = "";
	String data1 = "";
	String data2 = "";
	String data3 = "";
	String data4 = "";		
	String data5 = "";	
	String data6 = "";	
	List<String> dList = new ArrayList<String>();
	String data8 = "";
%>
<body  style=" background:#CADFFF">
<form name="Gj_File" action="File_GJ.do" method="post" target="mFrame" enctype="multipart/form-data">
	<table width="100%" style='margin:auto;' border=0 cellPadding=0 cellSpacing=0 bordercolor="#3491D6" borderColorDark="#ffffff">
		<tr height='100%' valign='middle'>
			<td align=center>
				<font color='red'><Strong>选择路线</Strong></font>
				<select  name='Func_Name_Id' style='width:100px;height:21px'  >										
						 <option value="999"   >花池北路1段</option>	
						 <option value="999"   >花池北路2段</option>	
						 <option value="999"   >花池东路1段</option>	
						 <option value="999"   >花池东路2段</option>									 						
				</select>
			</td>
		</tr>				
	 </table> 
  <div  id="container" style="min-width:700px;min-height:450px"></div>
</form>
</body>
<script LANGUAGE="javascript">
$(function () {
    $('#container').highcharts({

        chart: {
            type: 'column',
            margin: [ 50, 50, 100, 80]
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
             //起点管井
             'HCBWG001',
 <%
 		if(null != MoreDev_GX)
 		{
 			 Iterator iterator = MoreDev_GX.iterator();
			 while(iterator.hasNext())
			  {
						DevGXBean xBean = (DevGXBean)iterator.next();
						Dev_GX_Diameter = xBean.getDiameter(); 
						data8 = xBean.getLength()+"/"+xBean.getId();
						if(!dList.contains(xBean.getStart_Id()))
						{
							dList.add(xBean.getStart_Id());
							dList.add(xBean.getStart_Id());
						}						
	%>
			'<%= data8%>',
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
 					if(null != MoreDev_GX)
 					{
 			 			Iterator it = MoreDev_GX.iterator();
			 			while(it.hasNext())
			 			 {
								DevGXBean gxBean = (DevGXBean)it.next();
								if(dList.contains(jBean.getId()))
								{					
									dList.remove(jBean.getId());
									data1 += jBean.getBase_Height()+",";							
									data2 += jBean.getWaterLev()+",";											
									data3 += Float.parseFloat(gxBean.getDiameter())/1000+",";							
									data4 += jBean.getTop_Height() +",";
								if(!data2.equals("0"))
								{
									data5 += df.format(Float.parseFloat(jBean.getBase_Height())+Float.parseFloat(jBean.getWaterLev())+Float.parseFloat(gxBean.getDiameter())/1000+Float.parseFloat(jBean.getTop_Height()))+",";
								}else
									{
										data5 += df.format(Float.parseFloat(jBean.getBase_Height())+Float.parseFloat(gxBean.getDiameter())+Float.parseFloat(jBean.getTop_Height()))+",";
									}		
										data6 += df.format(Float.parseFloat(jBean.getBase_Height())+Float.parseFloat(jBean.getWaterLev()))+",";	
									}else
										{
										
										}
								}
 		   				} 		
						}
 					}%>
        yAxis: {
            allowDecimals: true,
            min: 0,
            title: {
                text: 'Elevation(m)'
            }
        },
				legend: {
            		enabled: false
       			 },
        tooltip: {
            formatter: function() {
                return '<b>'+ this.x +'</b><br/>'+
                    this.series.name +': '+ this.y +'<br/>';
            }
            
        },

        plotOptions: {
            column: {
                stacking: 'normal',
                pointPadding: 0,
       					borderWidth: 0,
        				pointWidth: 5,   
        				dataLabels: {
                  enabled: true},    				               
            },  
            line:
            {
            	dataLabels: {
                  enabled: true},
            	}     
        },				
        series: [
        //柱状图 
        {//实际显示图  
        		type:'column',
            name: '顶高',

           	color:"#EED5B7",
           	shadow: true,
            data: [<%=data4%>],           
            stack: 'male'
        },               
        
        {//实际显示图  
        		type:'column',
            name: '管径',

           	color:"#FFFFFF ",
           	shadow: true,
            data: [<%=data3%>],
            stack: 'male'
        }, 
        {//掩盖图
            name: '水位',
            color:"red",
            shadow: true,
            data: [<%=data2%>],
            stack: 'male'
        },
        {//掩盖图
            name: '底高',
            color:"green",
            data: [<%=data1%>],
            stack: 'male'
        },
        //线
        {
          	type:'line',
            name: '顶线',
            dashStyle: 'longdash',
            data: [<%=data5%>]
        },
        {//显示区域
          	type:'area',
            name: '水高',

            color:"#A6FFFF",
            stack: 'tatl',
            data: [<%=data6%>]
        },
        {//掩盖区域
          	type:'area',
            name: '底高',
            colorByPoint:false,
            color:"white",
            stack: 'tatl',
            data: [<%=data1%>]
        }
        ]                 
    });  
});
alert("<%=dList%>");
</script>
</html>