
//利息快速计算方法

/*----------------------------
money 本金
rate 贷款利率
stdate 起始日期
enddate 截止日期
----------------------------*/
function showRateInfo(money, rate, stdate, enddate) {
   // var money = 0;
    //var rate = 0;
    var zrate = 0;
    var intdate = 0;
    //money = document.getElementById("txt1").value;
    if (!IsNullOrEmpty(money)) {
        alert("本金金额不能为空！");
        return "";
    }
    //var stdate = document.getElementById("txt2").value;
    //var enddate = document.getElementById("txt3").value;
    intdate = getDateDiff(stdate, enddate);
    if (IsNullOrEmpty(rate)) {
        rate = parseFloat((rate) * 0.01);
    } else {
        alert('您的输入的利率有误，请重新输入！');
        return "";
    }
    zrate = parseFloat((money * intdate * rate) / 360);
    return (Math.round(zrate * 100)) / 100;
}

function clearInfo() {
    $("#txt1").val("");
    $("#txt2").val("");
    $("#txt3").val("");
    $("#txt4").val("");
    $("#txt5").val("");
}
function getDateDiff(sDate1, sDate2) {
    var oDate1, oDate2, iDays;
    oDate1 = new Date(sDate1);
    oDate2 = new Date(sDate2);
    iDays = parseInt((Math.abs(oDate1.getTime() - oDate2.getTime())) / 86400000);
    return iDays;
}
function IsNullOrEmpty(obj) {
    if (obj != "" && obj != null && obj != "undefined") {
        return true;
    }
    else {
        return false;
    }
}