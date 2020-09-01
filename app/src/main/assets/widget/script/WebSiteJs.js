

//清空获取数据的前后空格
function STrim(str) {
	return str.replace(/^\s+/, '').replace(/\s+$/, '');
}

//选择器
function $$(a) {
	return document.getElementById(a);
}



//处理input中输入的数字，使用方法：onblur="SetInputDouble（this，2）" 保留两位小数
function SetInputDouble(Vid, VLenth) {
	Vid.value = Vid.value.replace(/[^\d.]/g, '');
	if (Vid.value != "") {
		if (Vid.value.indexOf(".") == -1)
			Vid.value = (parseFloat(Vid.value).toFixed(0)).toString();
		else
			Vid.value = (parseFloat(Vid.value).toFixed(VLenth)).toString();
	}
}

//处理input中输入的数字，使用方法：onblur="SetInputDouble（this，2）" 保留整数
function SetInputInt(Vid) {
	Vid.value = Vid.value.replace(/[^\d.]/g, '');
	if (Vid.value != "")
		Vid.value = (parseFloat(Vid.value).toFixed(0)).toString();
}

///JS判断   Title格式:Price;price|收费金额；0|0|收费金额
//指定格式判断：mobile:手机号码;phone：电话号码;idcardno：身份证号;date：日期；price：金额（可正负数，保留两位小数）
function PostCheck() {
	var IsTrue = true;
	var FormList = $("input,select,textarea");
	var InputCount = FormList.length;
	for (var i = 0; i < InputCount; i++) {
		var VInput = FormList.eq(i);
		var titles = VInput.attr("title");
		if (titles != null) {
			var VTitle = STrim(VInput.attr("title"));
			var VValue = STrim(VInput.val());
			if (VTitle != "") {
				if (VTitle.toLowerCase().indexOf("mobile") != -1)//手机号码
				{
					var MessStr = "手机号码";
					if (VTitle.split('|').length > 1)
						MessStr = VTitle.split('|')[1].toString();
					var ValidaCode = /^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1}))+\d{8})$/;
					if (!(VValue.length == 11 && ValidaCode.test(VValue))) {
						MessAge = MessStr + "格式不正确";
						IsTrue = false;
					}
				} else if (VTitle.toLowerCase().indexOf("phone") != -1)//手机号码
				{
					var MessStr = "电话号码";
					if (VTitle.split('|').length > 1)
						MessStr = VTitle.split('|')[1].toString();
					var ValidaCode1 = /^[a-zA-Z0-9\u4e00-\u9fa5-_]+$/;
					var ValidaCode2 = /^(\d{3,4}-?)?\d{7,9}(-?\d{3,4})$/g;
					if (!(ValidaCode1.test(VValue) && ValidaCode2.test(VValue))) {
						MessAge = MessStr + "格式不正确";
						IsTrue = false;
					}
				} else if (VTitle.toLowerCase().indexOf("idcardno") != -1)//手机号码
				{
					var MessStr = "身份证号码";
					if (VTitle.split('|').length > 1)
						MessStr = VTitle.split('|')[1].toString();
					var ValidaCode = /^(\d{6})()?(\d{4})(\d{2})(\d{2})(\d{3})(\w)$/;
					if (!ValidaCode.test(VValue)) {
						MessAge = MessStr + "格式不正确";
						IsTrue = false;
					}

				} else if (VTitle.toLowerCase().indexOf("date") != -1)//日期
				{
					var MessStr = "日期";
					if (VTitle.split('|').length > 1)
						MessStr = VTitle.split('|')[1].toString();
					var ValidaCode = /((^((1[8-9]\d{2})|([2-9]\d{3}))([-\\/\._])(10|12|0?[13578])([-\\/\._])(3[01]|[12][0-9]|0?[1-9])$)|(^((1[8-9]\d{2})|([2-9]\d{3}))([-\\/\._])(11|0?[469])([-\\/\._])(30|[12][0-9]|0?[1-9])$)|(^((1[8-9]\d{2})|([2-9]\d{3}))([-\\/\._])(0?2)([-\\/\._])(2[0-8]|1[0-9]|0?[1-9])$)|(^([2468][048]00)([-\\/\._])(0?2)([-\\/\._])(29)$)|(^([3579][26]00)([-\\/\._])(0?2)([-\\/\._])(29)$)|(^([1][89][0][48])([-\\/\._])(0?2)([-\\/\._])(29)$)|(^([2-9][0-9][0][48])([-\\/\._])(0?2)([-\\/\._])(29)$)|(^([1][89][2468][048])([-\\/\._])(0?2)([-\\/\._])(29)$)|(^([2-9][0-9][2468][048])([-\\/\._])(0?2)([-\\/\._])(29)$)|(^([1][89][13579][26])([-\\/\._])(0?2)([-\\/\._])(29)$)|(^([2-9][0-9][13579][26])([-\\/\._])(0?2)([-\\/\._])(29)$))/;
					if (!ValidaCode.test(VValue)) {
						MessAge = MessStr + "格式不正确";
						IsTrue = false;
					}
				} else if (VTitle.toLowerCase().indexOf("price") != -1)//金额 可正数负数
				{
					var MessStr = "金额";
					if (VTitle.split('|').length > 1)
						MessStr = VTitle.split('|')[1].toString();
					var ValidaCode = /^(-|)(([1-9]\d{0,9})|0)(\.\d{1,2})?$/;
					if (!ValidaCode.test(VValue)) {
					   
						MessAge = MessStr + "格式不正确";
						IsTrue = false;
					}
				} else if (VTitle.toLowerCase().indexOf("int") != -1)//金额 可正数负数
				{
				    var MessStr = "金额";
				    if (VTitle.split('|').length > 1)
				        MessStr = VTitle.split('|')[1].toString();
				    var ValidaCode = /^\d+$/;
				    if (!ValidaCode.test(VValue)) {

				        MessAge = MessStr + "格式不正确";
				        IsTrue = false;
				    }
				} else {
					var MinLength = parseInt(VTitle.split('|')[0].toString());
					var MaxLength = parseInt(VTitle.split('|')[1].toString());
					var MessAge = VTitle.split('|')[2].toString();
					if (MinLength > 0 && MaxLength > 0) {
						if (VValue.length < MinLength || VValue.length > MaxLength) {
							if (MinLength == MaxLength)
								MessAge = "请填写" + MinLength + "位的" + MessAge;
							else
								MessAge = "请填写" + MinLength + "-" + MaxLength + "位的" + MessAge;
							IsTrue = false;
						}
					} else {
						if (VValue == "") {
							MessAge = "请填写" + MessAge;
							IsTrue = false;
						}
					}
				}
				if (!IsTrue) {
					alert(MessAge);
					break;
				}
			}
		}

	}
	return IsTrue;
}

