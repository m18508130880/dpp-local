//一天含 86,400,000 毫秒(24* 60 * 60*1000)
//今天
function showToDay()
{
	var Nowdate = new Date();
	M = Number(Nowdate.getMonth()) + 1;
	return Nowdate.getFullYear() + "-" + M + "-" + Nowdate.getDate();
}

//本周第一天
function showWeekFirstDay()
{
	var Nowdate = new Date();
	var WeekFirstDay = new Date(Nowdate-(Nowdate.getDay()-1)*86400000);
	return WeekFirstDay;
}

//本周最后一天
function showWeekLastDay()
{
	var Nowdate = new Date();
	var WeekFirstDay = new Date(Nowdate-(Nowdate.getDay()-1)*86400000);
	var WeekLastDay = new Date((WeekFirstDay/1000+6*86400)*1000);
	return WeekLastDay;
}

//本月第一天
function showMonthFirstDay()
{
	var Nowdate = new Date();
	var MonthFirstDay = new Date(Nowdate.getFullYear(),Nowdate.getMonth(),1);
	return MonthFirstDay;
}

//本月最后一天
function showMonthLastDay()
{
	var Nowdate = new Date();
	var MonthNextFirstDay = new Date(Nowdate.getFullYear(),Nowdate.getMonth()+1,1);
	var MonthLastDay = new Date(MonthNextFirstDay-86400000);
	return MonthLastDay;
}

//上月第一天
function showPreviousFirstDay()
{
	var MonthFirstDay = showMonthFirstDay();
	return new Date(MonthFirstDay.getFullYear(),MonthFirstDay.getMonth()-1,1);
}

//上月最后一天
function showPreviousLastDay()
{
	var MonthFirstDay = showMonthFirstDay();   
	return new Date(MonthFirstDay-86400000);   
}   

//上周第一天   
function showPreviousFirstWeekDay()   
{   
	var WeekFirstDay = showWeekFirstDay();
	return new Date(WeekFirstDay-86400000*7);
}   

//上周最后一天   
function showPreviousLastWeekDay()   
{   
	var WeekFirstDay = showWeekFirstDay();
	return new Date(WeekFirstDay-86400000);
}   

//上一天   
function showPreviousDay()   
{   
	var MonthFirstDay = new Date();   
	return new Date(MonthFirstDay-86400000);
}   

//下一天   
function showNextDay()   
{   
	var MonthFirstDay = new Date();
	return new Date((MonthFirstDay/1000+86400)*1000);   
}   

//下周第一天   
function showNextFirstWeekDay()   
{   
	var MonthFirstDay = showWeekLastDay();
	return new Date((MonthFirstDay/1000+86400)*1000);
}

//下周最后一天
function showNextLastWeekDay()   
{   
	var MonthFirstDay = showWeekLastDay();
	return new Date((MonthFirstDay/1000+7*86400)*1000);
}

//下月第一天
function showNextFirstDay()   
{   
	var MonthFirstDay = showMonthFirstDay();
	return new Date(MonthFirstDay.getFullYear(),MonthFirstDay.getMonth()+1,1);
}

//下月最后一天
function showNextLastDay()
{   
	var MonthFirstDay=showMonthFirstDay();
	return new Date(new Date(MonthFirstDay.getFullYear(),MonthFirstDay.getMonth()+2,1)-86400000);   
}

//获取当月最后一天日期
function getFirstAndLastMonthDay(year, month)
{
 	var firstdate = year + '-' + month + '-01';
 	var day = new Date(year, month, 0);
 	var lastdate = year + '-' + month + '-' + day.getDate();
 	return lastdate;
}

Date.prototype.toString = function() 
{
	return this.getFullYear()+"-"+(this.getMonth()+1)+"-"+this.getDate();
}