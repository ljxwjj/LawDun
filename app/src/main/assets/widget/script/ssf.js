
var ajType = [
    { "name": "财产案件", "value": "A1" },
        { "name": "非财产案件", "value": "A2" },
        { "name": "离婚案件", "value": "A3" },
        { "name": "人格权案件", "value": "A4" },
        { "name": "知识产权民事案件", "value": "A5" },
       { "name": "劳动争议案件", "value": "A6" },
       { "name": "商标专利海事行政案件", "value": "A7" },
       { "name": "其他行政案件", "value": "A8" },
       { "name": "管辖权异议不成立案件", "value": "A9" }



];
//有执行金额或价额
var zhixing = [
    {
        "Min": "0", //标的额区间 区间最小值
        "Max": "10000", //标的额区间 区间最大值
        "BL": "0", //计算比例
        "IsJs": "0", //是否需要计算
        "Number": "50" //固定额度
    }, {
        "Min": "10000", //标的额区间 区间最小值
        "Max": "500000", //标的额区间 区间最大值
        "BL": "0.015", //计算比例
        "IsJs": "1", //是否需要计算
        "Number": "-100" //固定额度
    }, {
        "Min": "500000", //标的额区间 区间最小值
        "Max": "5000000", //标的额区间 区间最大值
        "BL": "0.01", //计算比例
        "IsJs": "1", //是否需要计算
        "Number": "2400" //固定额度
    }, {
        "Min": "5000000", //标的额区间 区间最小值
        "Max": "10000000", //标的额区间 区间最大值
        "BL": "0.005", //计算比例
        "IsJs": "1", //是否需要计算
        "Number": "27400" //固定额度
    }, {
        "Min": "10000000", //标的额区间 区间最小值
        "Max": "9999999999999", //标的额区间 区间最大值
        "BL": "0.001", //计算比例
        "IsJs": "1", //是否需要计算
        "Number": "67400" //固定额度
    }
];

//保全额
var baoquan = [
    {
        "Min": "0", //标的额区间 区间最小值
        "Max": "1000", //标的额区间 区间最大值
        "BL": "0", //计算比例
        "IsJs": "0", //是否需要计算
        "Number": "30", //固定额度
        "Number2": "30" //不涉及财产时

    }, {
        "Min": "1000", //标的额区间 区间最小值
        "Max": "100000", //标的额区间 区间最大值
        "BL": "0.01", //计算比例
        "IsJs": "1", //是否需要计算
        "Number": "20", //固定额度
        "Number2": "30" //不涉及财产时
    }, {
        "Min": "100000", //标的额区间 区间最小值
        "Max": "9999999999999", //标的额区间 区间最大值
        "BL": "0.005", //计算比例
        "IsJs": "1", //是否需要计算
        "Number": "520", //固定额度,
        "Number2": "30" //不涉及财产时
    }
];




//区间对应的比率和固定额
var endPrices = [
{
    //计算公式为 诉讼费＝标的额×比率＋速算数
    "name": "A1",//财产案件
    "value": [
        {
            "Min": "0", //标的额区间 区间最小值
            "Max": "10000", //标的额区间 区间最大值
            "BL": "0", //计算比例
            "IsJs": "0", //是否需要计算
            "Number": "50", //固定额度
            "Number2": "0" //固定额度2(适用于区间费用)
        },
        {
            "Min": "10000", //标的额区间 区间最小值
            "Max": "100000", //标的额区间 区间最大值
            "IsJs": "1", //是否需要计算
            "BL": "0.025", //计算比例
            "Number": "-200", //固定额度
            "Number2": "0" //固定额度2
        }, {
            "Min": "100000", //标的额区间 区间最小值
            "Max": "200000", //标的额区间 区间最大值
            "IsJs": "1", //是否需要计算
            "BL": "0.02", //计算比例
            "Number": "300", //固定额度
            "Number2": "0" //固定额度2
        }, {
            "Min": "200000", //标的额区间 区间最小值
            "Max": "500000", //标的额区间 区间最大值
            "IsJs": "1", //是否需要计算
            "BL": "0.015", //计算比例
            "Number": "1300", //固定额度
            "Number2": "0" //固定额度2
        },
        {
            "Min": "500000", //标的额区间 区间最小值
            "Max": "1000000", //标的额区间 区间最大值
            "IsJs": "1", //是否需要计算
            "BL": "0.01", //计算比例
            "Number": "3800", //固定额度
            "Number2": "0" //固定额度2
        },
        {
            "Min": "1000000", //标的额区间 区间最小值
            "Max": "2000000", //标的额区间 区间最大值
            "IsJs": "1", //是否需要计算
            "BL": "0.009", //计算比例
            "Number": "4800", //固定额度
            "Number2": "0" //固定额度2
        },
        {
            "Min": "2000000", //标的额区间 区间最小值
            "Max": "5000000", //标的额区间 区间最大值
            "IsJs": "1", //是否需要计算
            "BL": "0.008", //计算比例
            "Number": "6800", //固定额度
            "Number2": "0" //固定额度2
        },
        {
            "Min": "5000000", //标的额区间 区间最小值
            "Max": "10000000", //标的额区间 区间最大值
            "IsJs": "1", //是否需要计算
            "BL": "0.007", //计算比例
            "Number": "11800", //固定额度
            "Number2": "0" //固定额度2
        }, {
            "Min": "10000000", //标的额区间 区间最小值
            "Max": "20000000", //标的额区间 区间最大值
            "IsJs": "1", //是否需要计算
            "BL": "0.006", //计算比例
            "Number": "21800", //固定额度
            "Number2": "0" //固定额度2
        },
        {
            "Min": "20000000", //标的额区间 区间最小值
            "Max": "0", //标的额区间 区间最大值
            "IsJs": "1", //是否需要计算
            "BL": "0.005", //计算比例
            "Number": "41800", //固定额度
            "Number2": "0" //固定额度2
        }
    ]
},
    {
        "name": "A2",//非财产案件
        "value": [
            {
                "Min": "0", //标的额区间 区间最小值
                "Max": "0", //标的额区间 区间最大值
                "BL": "0", //计算比例
                "IsJs": "0", //是否需要计算
                "Number": "50", //固定额度
                "Number2": "100", //固定额度2(适用于区间费用)
                "Number3": "50", //固定额度
                "Number4": "100", //固定额度2(适用于区间费用)
            }
        ]
    }
    ,
    {
        "name": "A3",//离婚
        "value": [
            {
                "Min": "0", //标的额区间 区间最小值
                "Max": "200000", //标的额区间 区间最大值
                "BL": "0", //计算比例
                "IsJs": "0", //是否需要计算
                "Number": "50", //固定额度
                "Number2": "300", //固定额度2(适用于区间费用)
                "Number3": "50", //固定额度
                "Number4": "300", //固定额度2(适用于区间费用)
            }, {
                "Min": "200000", //标的额区间 区间最小值
                "Max": "0", //标的额区间 区间最大值
                "BL": "0.005", //计算比例
                "IsJs": "1", //是否需要计算
                "Number": "-950", //固定额度
                "Number2": "-700", //固定额度2(适用于区间费用)
                "Number3": "50", //固定额度
                "Number4": "300", //固定额度2(适用于区间费用)

            }
        ]
    },
    {
        "name": "A4",//人格权案件
        "value": [
            {
                "Min": "0", //标的额区间 区间最小值
                "Max": "50000", //标的额区间 区间最大值
                "BL": "0", //计算比例
                "IsJs": "0", //是否需要计算
                "Number": "100", //固定额度
                "Number2": "500", //固定额度2(适用于区间费用)
                "Number3": "100", //固定额度
                "Number4": "500", //固定额度2(适用于区间费用)
            }, {
                "Min": "50000", //标的额区间 区间最小值
                "Max": "100000", //标的额区间 区间最大值
                "BL": "0.01", //计算比例
                "IsJs": "1", //是否需要计算
                "Number": "-400", //固定额度
                "Number2": "-700", //固定额度2(适用于区间费用)
                "Number3": "100", //固定额度
                "Number4": "500", //固定额度2(适用于区间费用)
            }
            , {
                "Min": "100000", //标的额区间 区间最小值
                "Max": "0", //标的额区间 区间最大值
                "BL": "0.005", //计算比例
                "IsJs": "1", //是否需要计算
                "Number": "100", //固定额度
                "Number2": "500", //固定额度2(适用于区间费用)
                "Number3": "100", //固定额度
                "Number4": "500", //固定额度2(适用于区间费用)
            }
        ]
    },
    {
        "name": "A5",//知识产权民事案件
        "value": [
            {
                "Min": "0", //标的额区间 区间最小值
                "Max": "0", //标的额区间 区间最大值
                "BL": "0", //计算比例
                "IsJs": "0", //是否需要计算
                "Number": "50", //固定额度
                "Number2": "100", //固定额度2(适用于区间费用)
                "Number3": "500", //固定额度
                "Number4": "1000", //固定额度2(适用于区间费用)
            }
        ]
    }
    ,
    {
        "name": "A6",//劳动争议案件
        "value": [
            {
                "Min": "0", //标的额区间 区间最小值
                "Max": "0", //标的额区间 区间最大值
                "BL": "0", //计算比例
                "IsJs": "0", //是否需要计算
                "Number": "10", //固定额度
                "Number2": "0", //固定额度2(适用于区间费用)
                "Number3": "10", //固定额度
                "Number4": "0", //固定额度2(适用于区间费用)
            }
        ]
    }
    ,
    {
        "name": "A7",//商标专利海事行政案件
        "value": [
            {
                "Min": "0", //标的额区间 区间最小值
                "Max": "0", //标的额区间 区间最大值
                "BL": "0", //计算比例
                "IsJs": "0", //是否需要计算
                "Number": "100", //固定额度
                "Number2": "0", //固定额度2(适用于区间费用)
                "Number3": "100", //固定额度
                "Number4": "0", //固定额度2(适用于区间费用)
            }
        ]
    }
    ,
    {
        "name": "A8",//其他行政案件
        "value": [
            {
                "Min": "0", //标的额区间 区间最小值
                "Max": "0", //标的额区间 区间最大值
                "BL": "0", //计算比例
                "IsJs": "0", //是否需要计算
                "Number": "100", //固定额度
                "Number2": "0",  //固定额度2(适用于区间费用)
                "Number3": "100", //固定额度
                "Number4": "0", //固定额度2(适用于区间费用)
            }
        ]
    }
    ,
    {
        "name": "A9",//管辖权异议不成立案件
        "value": [
            {
                "Min": "0", //标的额区间 区间最小值
                "Max": "0", //标的额区间 区间最大值
                "BL": "0", //计算比例
                "IsJs": "0", //是否需要计算
                "Number": "50", //固定额度
                "Number2": "100",  //固定额度2(适用于区间费用)
                "Number3": "50", //固定额度
                "Number4": "100", //固定额度2(适用于区间费用)
            }
        ]
    }
];

$(document).ready(function () {
    var values = "";
    var obj = eval(ajType);
    $.each(ajType, function (idx, item) {
        values += " <option value=\"" + item.value + "\">" + item.name + "</option>";
    });
    $("#t1").append(values);
    setT2($("#t1"));

});

function setT2(obj) {
    $("#t2").removeAttr("disabled");

    var type = $(obj).val();
    var arr = ["A2", "A6", "A7", "A8", "A9"];
    var arr2 = ["A1"];
    var isIn = $.inArray(type, arr);
    var isIn2 = $.inArray(type, arr2);
    if (isIn > -1) {
        $("#t2").val("0");
        $("#t2").attr("disabled", "disabled");
    } else if (isIn2 > -1) {
        $("#t2").val("1");
        $("#t2").attr("disabled", "disabled");
    } else {
        $("#t2").removeAttr("disabled");
    }
    $("#t2").change();
}

function setPrice(obj) {
    if ($(obj).val() == "0") {
        $("#prices").val("0");
        $("#prices").attr("disabled", "disabled");
    } else {
        $("#prices").removeAttr("disabled");
    }
}

function setJS() {
    if (PostCheck()) {
        var slf = 0.00;
        var slf2 = 0.00;
        var zxf = 0.00;
        var zxf2 = 0.00;
        var bqf = 0.00;
        var bqf2 = 0.00;
        var type = $("#t1").val(); //案件分类
        var isMoney = $("#t2").val(); //是否涉及财产
        var priceValue = $("#prices").val(); //标的金额

        if (type === "A5" && isMoney === "1") {
            type = "A1";
        }
        $.each(endPrices, function (idx, item) {
            if (item.name === type) {
                $.each(item.value, function (idx2, item2) {
                    var min = parseFloat(item2.Min); //最小区间
                    var max = parseFloat(item2.Max); //最大区间
                    var bL = parseFloat(item2.BL); //计算比例
                    var isJs = item2.IsJs; //是否需要计算
                    var number = parseFloat(item2.Number); //固定额度
                    var number2 = parseFloat(item2.Number2); //固定额度2(适用于区间费用)
                    var number3 = parseFloat(item2.Number3);
                    var number4 = parseFloat(item2.Number4);
                    var price = parseFloat(priceValue);
                    //不涉及财产，按最低额度
                    if (isMoney === "0") {
                        slf = number3;
                        slf2 = number4;
                    } else if ((min < price && price <= max) || (min < price && max === 0)) {
                        if (isJs === "0") {
                            slf = number;
                        } else {
                            slf = price * bL + number;
                        }
                        if (number2 !== 0) {
                            slf2 = price * bL + number2;
                        }
                    }
                });
            }
        });
        $.each(zhixing, function (idx, item) {
            var min = parseFloat(item.Min); //最小区间
            var max = parseFloat(item.Max); //最大区间
            var isJs = item.IsJs; //是否需要计算
            var number = parseFloat(item.Number); //固定额度
            var price = parseFloat(priceValue);
            var bL = parseFloat(item.BL); //计算比例

            if (isMoney == "0") {
                zxf = 50; zxf2 = 500;
            } else {
                //判断是否在区间内
                if ((min < price && price <= max) || (min < price && max === 0)) {
                    if (isJs === "0") {
                        zxf = number;
                    } else {
                        zxf = price * bL + number;
                    }
                }
            }



        });
        $.each(baoquan, function (idx, item) {
            var min = parseFloat(item.Min); //最小区间
            var max = parseFloat(item.Max); //最大区间
            var isJs = item.IsJs; //是否需要计算
            var number = parseFloat(item.Number); //固定额度
            var number2 = parseFloat(item.Number2); //不涉及财产时

            var price = parseFloat(priceValue);
            var bL = parseFloat(item.BL); //计算比例

            if (isMoney == "0") {
                bqf = number2;
            } else {
                //判断是否在区间内
                if ((min < price && price <= max) || (min < price && max === 0)) {
                    if (isJs === "0") {
                        bqf = number;
                    } else {
                        bqf = price * bL + number;
                    }
                }
                if (bqf > 5000) {
                    bqf = 5000;
                }
            }
        });
        $("#table").show();
        $("#Price").html("受理费：<span style='color:red'>" + setQj(slf,slf2) + "</span> 元" +
                             "<br/>执行费：<span style='color:red'>" + setQj(zxf, zxf2) + "</span> 元" +
                             "<br/>保全费：<span style='color:red'>" + setQj(bqf, bqf2) + "</span> 元");
         
    }
}

function setQj(p1, p2) {
    if (p2 != 0) {
        return p1.toFixed(2) + "至" + p2.toFixed(2);
    }
    return p1.toFixed(2);
}