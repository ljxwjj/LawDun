var xmlDoc = null;


//违约金计算
function setJS() {
    if (PostCheck()) {
        var dateSt = $("#d1").val();
        var dateEd = $("#d2").val();
        var lilv = $("#lilv").val();
        var type = $("#t1").val();
        var prices = parseFloat($("#prices").val());

        var begin = new Date(new Date(dateSt).toDateString());

        var end = new Date(new Date(dateEd).toDateString());
        var daytime = 24 * 60 * 60 * 1000; //一天的毫秒总数
        var days = (end - begin) / daytime; //两个时间段相隔的总天数

        if (type === "1") {
            lilv = ((lilv / 30) /100);
        } else if (type === "2") {
            lilv = ((lilv / 360) / 100);
        } else {
            lilv = (lilv / 100);
        }
        var value = (prices * lilv* days).toFixed(2);
        $("#table").show();
        $("#Price").html("逾期期限：<span style='color:red'>" + days + "</span> 天" + "<br/>" +
            "　　利率：<span style='color:red'>" + lilv.toFixed(7) + "</span> 每天" + "<br/>" +
            "　违约金：<span style='color:red'>" + value + "</span> 元");
    }
}



//日期时间间隔计算工具
function setData1(dateST, dateEd, type, jsonStr) {
  
    var days = getWorkDays( new Date(dateST),  new Date(dateEd), type, jsonStr);
    return days;
}

//日期时间间隔计算工具
function setData2(datas, year, day, type, jsonStr) {
    var rValue = "";
    if (type == "0") {
        rValue += getDate(new Date(datas), year, day, type, jsonStr);
    } else {
      
        rValue += getdays(datas, year, day, type, jsonStr);
    }
    return rValue;
}

//获取当前时间所在周的第一天
function getFristDate(date) {
    var days = 0;
    days = date.getDay();
    return new Date(date.getTime() - (days * 24 * 60 * 60 * 1000));
}

//计算两段段时间内的天数；
function getWorkDays(beginDay, endDay, type, jsonStr) {
   
    var begin = new Date(beginDay.toDateString());
  
    var end = new Date(endDay.toDateString());
    var daytime = 24 * 60 * 60 * 1000;    //一天的毫秒总数
    var days = (end - begin) / daytime; //两个时间段相隔的总天数
  
    if (type == "0") {
        return days;
    }
    else {
        var beginWeekFirstDay = getFristDate(beginDay).getTime(); //时间段起始时间所在周的第一天
        var endWeekOverDay = getFristDate(endDay).getTime() + 6 * daytime; //时间段结束时间所在周的最后天
        var weekCount = ((endWeekOverDay - beginWeekFirstDay) / daytime + 1) / 7 * 2; //两段时间所在周的时间周末的天数
        if (endDay.getDay() < 6) {
            weekCount -= 1;
        }
        if (beginDay.getDay() > 0) {
            weekCount -= 1;
        }
        weekCount = showWorkDays(weekCount, begin, end, jsonStr); //排除周末上班时间
        weekCount = showDays(weekCount, begin, end, jsonStr); //排除节假日上班时间
        return days - weekCount; //最终工作日
    }
}
//取得顺延后的除周末节假日外的日期
var getDate = function (GgStart, Ggmonth, Ggdays, type, jsonStr) {
    var rValue = "";
    //var GgStart = $("#txt_Date").val(); //选择的日期
    //var Ggmonth = $("#txt_month").val(); //填写的月数
    //var Ggdays = $("#txt_day").val(); //填写的天数
    if (IsNullOrEmpty(GgStart)) {
        var stDate = new Date(GgStart);
        if (IsNullOrEmpty(Ggmonth)) {
            stDate.setMonth(stDate.getMonth() + parseInt(Ggmonth));
        }
        if (IsNullOrEmpty(Ggdays)) {
            stDate.setDate(stDate.getDate() + parseInt(Ggdays));
        }
        rValue = getStringDate(stDate);
        return rValue;
    }
}
function getdays(date, mouth, days, type, jsonStr) {
    var rValue = "";
  
    //var date = $.trim($("#txt_Date").val()); //选择的日期
    //var days = parseInt($("#txt_day").val()); //填写的天数
    var stdate = new Date(date);
    var stDays = stdate.getDay();//得到当前日期的是星期几
    var newDate = getStringDate(stdate);
    if (IsNullOrEmpty(date)) {
        for (var i = 0; i < days;) {
            var thisTime = getStringDate(stdate);
            stdate = new Date(stdate);
            stDays = stdate.getDay();
            if (stDays == 0 || stDays == 6) {//周末，6:周六，0:周日
                var isWorkDay = false;
                for (j = 0; j < jsonStr.length; j++) {
                    if (jsonStr[j].年份 == stdate.getYear() && jsonStr[j].属性 == '0' && jsonStr[j].日期 == thisTime) {
                        isWorkDay = true;
                    }
                }
                
                if (isWorkDay) {
                    i++;
                    // days = parseInt(days) - 1;
                }
                else {
                    //days = parseInt(days) + 1;
                }
            }
            else {
                var s = "";
                    for (j = 0; j < jsonStr.length; j++) {
                        if (jsonStr[j].年份 == stdate.getYear() && jsonStr[j].属性 == '0' && jsonStr[j].日期 == thisTime) {
                            var enddate = new Date(jsonStr[j].上班);
                            stdate.setTime(enddate.getTime());
                            s = "1";
                            continue;
                        }
                    }
                   if (s!="1") {
                        i++;
                    }
                
            }
            stdate.setDate(stdate.getDate() + 1);
        }
        var mString = new String(stdate.getMonth() + 1);
        var dString = new String(stdate.getDate());
        if (mString.length == 1) {
            mString = "0" + mString;
        }
        if (dString.length == 1) {
            dString = "0" + dString;
        }
        var endDate = stdate.getFullYear() + "/" + mString + "/" + dString;
        rValue = endDate;
     
        var s1 = showFestivalWorkDays(stdate, rValue, jsonStr);
      
        if (s1 != "") {
            rValue = s1;
        }
        var s2 = showFestivalDays(stdate, rValue, jsonStr);
        if (s2 != "") {
            rValue = s2;
        }
    }
   
    return rValue;

}
//判断是否为法定节假日
function showDays(days, begin, end, jsonStr) {

    if (begin.getYear() == end.getYear()) {//同年份日期时间段
        for (i = 0; i < jsonStr.length; i++) {
            if (jsonStr[i].年份 == begin.getYear() && jsonStr[i].属性 == '1') {
                var itemDay = new Date(jsonStr[i].日期);
                if (itemDay.getTime() >= begin.getTime() && itemDay.getTime() <= end.getTime() && itemDay.getDay() > 0 && itemDay.getDay() < 6)
                    days = parseInt(days) + 1;
            }
        }
    }
    else {//不同年份日期时间段
        for (j = 0; j < jsonStr.length; j++) {
            if (jsonStr[j].年份 == begin.getYear() && jsonStr[j].属性 == '1') {
                var itemDay = new Date(jsonStr[j].日期);
                if (itemDay.getTime() >= begin.getTime() && itemDay.getTime() <= end.getTime() && itemDay.getDay() > 0 && itemDay.getDay() < 6)
                    days = parseInt(days) + 1;
            }
        }
        for (w = 0; w < jsonStr.length; w++) {
            if (jsonStr[w].年份 == end.getYear() && jsonStr[w].属性 == '1') {
                var itemDay = new Date(jsonStr[w].日期);
                if (itemDay.getTime() >= begin.getTime() && itemDay.getTime() <= end.getTime() && itemDay.getDay() > 0 && itemDay.getDay() < 6)
                    days = parseInt(days) + 1;
            }
        }
    }
    //alert("排除节假日后是：" + days + "天");
    return days;
}
//判断是否法定周末工作日
function showWorkDays(days, begin, end, jsonStr) {
    for (i = 0; i < jsonStr.length; i++) {
        if (jsonStr[i].年份 == begin.getYear() && jsonStr[i].属性 == '0') {
            if (begin.getYear() == end.getYear()) { //同年份日期时间段
                for (j = 0; j < jsonStr.length; j++) {
                    if (jsonStr[j].年份 == begin.getYear() && jsonStr[j].属性 == '0') {
                        var itemDay = jsonStr[j].日期;
                        if (itemDay.getTime() >= begin.getTime() && itemDay.getTime() <= end.getTime() && (itemDay.getDay() == 0 || itemDay.getDay() == 6)) { //判断该日期是否在所选日期范围内，并且满足是周末
                            days = parseInt(days) - 1;
                        }
                    }
                }
            } else { //不同年份日期时间段
                for (f = 0; f < jsonStr.length; f++) {
                    if (jsonStr[f].年份 == begin.getYear() && jsonStr[f].属性 == '0') {
                        var itemDay = new Date(jsonStr[f].日期);
                        if (itemDay.getTime() >= begin.getTime() && itemDay.getTime() <= end.getTime() && (itemDay.getDay() == 0 || itemDay.getDay() == 6)) { //判断该日期是否在所选日期范围内，并且满足是周末
                            days = parseInt(days) - 1;
                        }
                    }
                }
                for (w = 0; w < jsonStr.length; w++) {
                    if (jsonStr[w].年份 == end.getYear() && jsonStr[w].属性 == '0') {
                        var itemDay = new Date(jsonStr[w].日期);
                        if (itemDay.getTime() >= begin.getTime() && itemDay.getTime() <= end.getTime() && (itemDay.getDay() == 0 || itemDay.getDay() == 6)) { //判断该日期是否在所选日期范围内，并且满足是周末
                            days = parseInt(days) - 1;
                        }
                    }
                }
            }
        }
    }
    //alert("排除周末上班时间后是：" + days + "天");
    return days;

}
//判断是否为法定节假日
function showFestivalDays(objTime, obj, jsonStr) {
    var s = "";
    for (w = 0; w < jsonStr.length; w++) {
        if (jsonStr[w].年份 == objTime.getYear() && jsonStr[w].属性 == '1' && jsonStr[w].日期 == $(obj).val().trim()) {
            s = jsonStr[w].上班;
        }
    }
    return s;
    //var nodes = xmlDoc.selectNodes("/法定节假日调整表/年份[@年份='" + objTime.getYear() + "']/item[@日期='" + $(obj).val().trim() + "'][@属性='1']");
    //    if (nodes.length > 0) {
    //        $(obj).val(nodes[0].selectSingleNode("./@上班").value);
    //    }
}
//判断是否法定周末工作日
function showFestivalWorkDays(objData, txtObj, jsonStr) {
    var rValue = "";
    var isHava = "";
        for (w = 0; w < jsonStr.length; w++) {
            if (jsonStr[w].年份 == objData.getYear() && jsonStr[w].属性 == '0' && jsonStr[w].日期 == txtObj) {
                isHava = jsonStr[w].日期;
            }
        }
                var isTrueOrFalse = false;
                if (objData.getDay() == 0) {
                    if (isHava!="") {
                        rValue = isHava;
                        isTrueOrFalse = true;
                    }
                    if (isTrueOrFalse == false) {
                        var secTime = objData.getTime() + (1 * 24 * 60 * 60 * 1000);
                        objData.setTime(secTime);
                        rValue = getStringDate(objData);
                    }
                } else if (objData.getDay() == 6) {
                    if (isHava != "") {
                        rValue = isHava;
                        isTrueOrFalse = true;
                    }
                    if (isTrueOrFalse == false) {
                        var sepTime = objData.getTime() + (1 * 24 * 60 * 60 * 1000);
                        objData.setTime(sepTime);
                        rValue = getStringDate(objData);

                        var isHave = "";
                        for (w = 0; w < jsonStr.length; w++) {
                            if (jsonStr[w].年份 == objData.getYear() && jsonStr[w].属性 == '0' && jsonStr[w].日期 == txtObj) {
                                isHave = jsonStr[w].日期;
                            }
                        }
                        if (isHave!="") {
                            rValue = isHave;
                            isTrueOrFalse = true;
                        }
                        if (isTrueOrFalse == false) {
                            var secTime = objData.getTime() + (1 * 24 * 60 * 60 * 1000);
                            objData.setTime(secTime);
                            rValue = getStringDate(objData);
                        }
                    }
                } else {
                    rValue =getStringDate(objData);
            
        }
    return rValue;
}
//把日期转化为标准的字符串日期
var getStringDate = function (date) {
    var stringDate = "";
    var mStrin = new String(date.getMonth() + 1);
    var dStrin = new String(date.getDate());
    if (mStrin.length == 1) {
        mStrin = "0" + mStrin;
    }
    if (dStrin.length == 1) {
        dStrin = "0" + dStrin;
    }
    stringDate = date.getFullYear() + "/" + mStrin + "/" + dStrin;
    return stringDate;
}
//是否为空
function IsNullOrEmpty(obj) {
    if (obj != "" && obj != null && obj != "undefined") {
        return true;
    }
    else {
        return false;
    }
}
String.prototype.trim = function () {
    return this.replace(/^ */gi, "").replace(/ *$/gi, "");
} //去除前面的空格