﻿
//2、伤残赔偿金计算方法：

//(1)60周岁以下，城镇居民人均可支配收入/农村居民人均纯收入×赔偿指数×20年;

//(2)60周岁至75周岁，每增加一岁，减少一年;

//(3)75周岁以上，城镇居民人均可支配收入/农村居民人均纯收入×赔偿指数×5年
//省名称，代号，说明跳转地址，上年度月平均工资
//p1 全体居民人均可支配收入 p2 全体居民人均消费支出
//p3 城镇居民人均可支配收入 p4 城镇居民人均消费支出
//p5 农村居民人均可支配收入 p6 农村居民人均消费支出
var ajType = [
    {
        "name": "浙江", "value": "A1", "Url": "zjjt.html", "prices": "7267",
        "p1": "42046", "p2": "27079", "p3": "51261", "p4": "31924", "p5": "24956", "p6": "18093"
    },
    {
        "name": "上海", "value": "A4", "Url": "shjt.html", "prices": "8825",
        "p1": "58988", "p2": "39792", "p3": "62596", "p4": "42304", "p5": "27825", "p6": "18090"
    },
    {
        "name": "北京", "value": "A5", "Url": "bjjt.html", "prices": "8717",
        "p1": "57230", "p2": "37425", "p3": "62406", "p4": "40346", "p5": "24240", "p6": "18810"
    },
    {
        "name": "安徽", "value": "A2", "Url": "ahjt.html", "prices": "4595",
        "p1": "21863", "p2": "15752", "p3": "31640", "p4": "20740", "p5": "12758", "p6": "11106"
    },
    {
        "name": "广东", "value": "A3", "Url": "gdjt.html", "prices": "7178",
        "p1": "33003", "p2": "24820", "p3": "40975", "p4": "30198", "p5": "15780", "p6": "13200"
    },
    {
        "name": "福建", "value": "A6", "Url": "fjjt.html", "prices": "4111",
        "p1": "30048", "p2": "21249", "p3": "39001", "p4": "25980", "p5": "16335", "p6": "14003"
    },
    {
        "name": "河南", "value": "A7", "Url": "hnjt.html", "prices": "3556",
        "p1": "20170", "p2": "13730", "p3": "29558", "p4": "19422", "p5": "12719", "p6": "9212"
    },
    {
        "name": "河北", "value": "A8", "Url": "hbjt.html", "prices": "4243",
        "p1": "21484", "p2": "15437", "p3": "30548", "p4": "20600", "p5": "12881", "p6": "10536"
    },
    {
        "name": "江苏", "value": "A9", "Url": "jsjt.html", "prices": "6487",
        "p1": "35024", "p2": "23469", "p3": "43622", "p4": "27726", "p5": "19158", "p6": "15612"

    },
    {
        "name": "甘肃", "value": "A10", "Url": "gsjt.html", "prices": "3892",
        "p1": "16011", "p2": "13120", "p3": "27763", "p4": "20659", "p5": "8076", "p6": "8030"

    },
    {
        "name": "广西", "value": "A11", "Url": "gxjt.html", "prices": "3621",
        "p1": "19905", "p2": "13424", "p3": "30502", "p4": "18349", "p5": "11325", "p6": "9437"

    },
    {
        "name": "贵州", "value": "A12", "Url": "gzjt.html", "prices": "3955",
        "p1": "16704", "p2": "12970", "p3": "29080", "p4": "20348.20", "p5": "8869", "p6": "8299"

    },
    {
        "name": "湖南", "value": "A15", "Url": "hunjt.html", "prices": "4491",
        "p1": "23103", "p2": "17160", "p3": "33948", "p4": "23163", "p5": "12936", "p6": "11534"

    },
    {
        "name": "湖北", "value": "A14", "Url": "hubjt.html", "prices": "6050",
        "p1": "23757", "p2": "16938", "p3": "31889", "p4": "", "p5": "13812", "p6": ""

    },
    {
        "name": "黑龙江", "value": "A13", "Url": "hljjt.html", "prices": "3920",
        "p1": "21206", "p2": "15578", "p3": "27446", "p4": "19270", "p5": "12665", "p6": "10524"

    },
    {
        "name": "吉林", "value": "A16", "Url": "jljt.html", "prices": "4296",
        "p1": "21368", "p2": "15632", "p3": "28319", "p4": "20051", "p5": "12950", "p6": "10279"
    },
    {

        "name": "江西", "value": "A17", "Url": "jxjt.html", "prices": "4245",
        "p1": "22031", "p2": "14459", "p3": "31198", "p4": "19244", "p5": "13242", "p6": "9870"
    },
    {
        "name": "辽宁", "value": "A18", "Url": "lnjt.html", "prices": "4455",
        "p1": "27835", "p2": "20463", "p3": "34993", "p4": "", "p5": "13747", "p6": ""
    },
    {
        "name": "内蒙古", "value": "A19", "Url": "nmgjt.html", "prices": "4538",
        "p1": "26212", "p2": "18946", "p3": "35670", "p4": "23638", "p5": "12584", "p6": "12184"
    },
    {
        "name": "宁夏", "value": "A20", "Url": "nxjt.html", "prices": "5031",
        "p1": "20562", "p2": "15350", "p3": "29472", "p4": "20219", "p5": "10738", "p6": "9982"

    },
    {
        "name": "青海", "value": "A21", "Url": "qhjt.html", "prices": "4817",
        "p1": "19001", "p2": "15503", "p3": "21473", "p4": "29169", "p5": "9903", "p6": "9462"

    },
    {

        "name": "山东", "value": "A22", "Url": "sdjt.html", "prices": "4880",
        "p1": "26930", "p2": "17281", "p3": "36789", "p4": "23072", "p5": "15118", "p6": "10342"

    },
    {
        "name": "山西", "value": "A23", "Url": "sxjt.html", "prices": "4317",
        "p1": "20420", "p2": "13664", "p3": "29132", "p4": "18404", "p5": "10788", "p6": "8424"

    },
    {
        "name": "陕西", "value": "A24", "Url": "shanxjt.html", "prices": "4742",
        "p1": "20635", "p2": "14900", "p3": "30810", "p4": "20388", "p5": "10265", "p6": "9306"

    },
    {
        "name": "四川", "value": "A25", "Url": "scjt.html", "prices": "6151",
        "p1": "20580", "p2": "16180", "p3": "30727", "p4": "21991", "p5": "12227", "p6": "11397"

    },
    {
        "name": "天津", "value": "A26", "Url": "tjjt.html", "prices": "5500",
        "p1": "37022", "p2": "27841", "p3": "", "p4": "", "p5": "", "p6": ""
    },
     //{ "name": "西安", "value": "A27", "Url": "xa.html", "prices": "3709", "minPrice": "5939", "Spilt": "0", "Bf": "0.3", "DBf": "0.4", "WQ": "0.5" },
  {
      "name": "西藏", "value": "A28", "Url": "xzjt.html", "prices": "5550",
      "p1": "15457", "p2": "10320", "p3": "30671", "p4": "21088", "p5": "10330", "p6": "6691"

  },
{
    "name": "新疆", "value": "A29", "Url": "xjjt.html", "prices": "4417",
    "p1": "19975", "p2": "15087", "p3": "30775", "p4": "22797", "p5": "11045", "p6": "8713"

},
//{ "name": "银川", "value": "A30", "Url": "yc.html", "prices": "3709", "minPrice": "5939", "Spilt": "0", "Bf": "0.3", "DBf": "0.4", "WQ": "0.5" },
{
    "name": "重庆", "value": "A32", "Url": "cqjt.html", "prices": "6362",
    "p1": "24153", "p2": "17898", "p3": "32193", "p4": "22759", "p5": "12638", "p6": "10936"
},
{
    "name": "云南", "value": "A31", "Url": "ynjt.html", "prices": "4962",
    "p1": "18348", "p2": "12658", "p3": "30996", "p4": "19560", "p5": "9862", "p6": "8027"
}
,
  {
      "name": "海南", "value": "A33", "Url": "hain.html", "prices": "6324",
      "p1": "24579", "p2": "17528", "p3": "33349", "p4": "22971", "p5": "13989", "p6": "10956"
  }

];

//p1 全体居民人均可支配收入 p2 全体居民人均消费支出
//p3 城镇居民人均可支配收入 p4 城镇居民人均消费支出
//p5 农村居民人均可支配收入 p6 农村居民人均消费支出
var moneyType = [
    {
        "name": "A1", //浙江 
        "value": [
            {
                "0": "20,0,5",//残疾费
                "1": "按需据实",//交通费
                "2": "10-30/天，不超过3000元",//营养费
                "3": "150/天",//住宿费
                "4": "30/天", //住院伙食补助费
                "5": "30786", //丧葬费
                "6": "法院根据实际情况判决", //精神损害抚慰金
                "7": "当地劳务报酬标准*护理天数",  //护理费
                "8": "诊疗费+医药费以发票为准",  //医疗费
                "9": "以发票为准"  //鉴定费

            }
        ]
    }, {
        "name": "A4", //上海 
        "value": [
            {
                "0": "20,0,5",//残疾费
                "1": "按需据实",//交通费
                "2": "20—40/天",//营养费
                "3": "60/天",//住宿费
                "4": "20/天", //住院伙食补助费
                "5": "32706", //丧葬费
                "6": "法院根据实际情况判决", //精神损害抚慰金
                "7": "40元/天",  //护理费
                "8": "诊疗费+医药费以发票为准",  //医疗费
                "9": "以发票为准"  //鉴定费
            }
        ]
    }, {
        "name": "A5", //北京 
        "value": [
            {
                "0": "20,0,5",//残疾费
                "1": "按需据实",//交通费
                "2": "参照医疗机构意见",//营养费
                "3": "150/天",//住宿费
                "4": "50/天", //住院伙食补助费
                "5": "38778", //丧葬费
                "6": "残疾：1-2万元，死亡：5-10万元", //精神损害抚慰金
                "7": "当地劳务报酬标准*护理天数",  //护理费
                "8": "诊疗费+医药费以发票为准",  //医疗费
                "9": "以发票为准"  //鉴定费
            }
        ]
    }, {
        "name": "A2", //安徽 
        "value": [
            {
                "0": "20,0,5",//残疾费
                "1": "按需据实",//交通费
                "2": "30/天",//营养费
                "3": "按需据实",//住宿费
                "4": "100/天", //住院伙食补助费
                "5": "25447", //丧葬费
                "6": "不超过8万元", //精神损害抚慰金
                "7": "104元/天",  //护理费
                "8": "诊疗费+医药费以发票为准",  //医疗费
                "9": "以发票为准"  //鉴定费
            }
        ]
    }, {
        "name": "A3", //广东 
        "value": [
            {
                "0": "20,0,5",//残疾费
                "1": "500-2000",//交通费
                "2": "500-1000",//营养费
                "3": "按需据实",//住宿费
                "4": "100/天", //住院伙食补助费
                "5": "32395", //丧葬费
                "6": "60000-80000*伤残系数", //精神损害抚慰金
                "7": "104元/天",  //护理费
                "8": "诊疗费+医药费以发票为准",  //医疗费
                "9": "以发票为准"  //鉴定费
            }
        ]
    }, {
        "name": "A6", //福建 
        "value": [
            {
                "0": "20,0,5",//残疾费
                "1": "按需据实",//交通费
                "2": "参照医疗机构",//营养费
                "3": "按需据实",//住宿费
                "4": "按需据实", //住院伙食补助费
                "5": "26731", //丧葬费
                "6": "60000-80000*伤残系数", //精神损害抚慰金
                "7": "当地劳务报酬标准*护理天数",  //护理费
                "8": "诊疗费+医药费以发票为准",  //医疗费
                "9": "以发票为准"  //鉴定费
            }
        ]
    }, {
        "name": "A7", //河南 
        "value": [
            {
                "0": "20,0,5",//残疾费
                "1": "按需据实",//交通费
                "2": "参照医疗机构",//营养费
                "3": "按需据实",//住宿费
                "4": "国家机关一般工作人员出差标准(元/天)*天数", //住院伙食补助费
                "5": "19402", //丧葬费
                "6": "法院根据实际情况判断", //精神损害抚慰金
                "7": "当地劳务报酬标准*护理天数",  //护理费
                "8": "诊疗费+医药费以发票为准",  //医疗费
                "9": "以发票为准"  //鉴定费
            }
        ]
    }, {
        "name": "A7", //河南 
        "value": [
            {
                "0": "20,0,5",//残疾费
                "1": "按需据实",//交通费
                "2": "参照医疗机构",//营养费
                "3": "按需据实",//住宿费
                "4": "国家机关一般工作人员出差标准(元/天)*天数", //住院伙食补助费
                "5": "19402", //丧葬费
                "6": "法院根据实际情况判断", //精神损害抚慰金
                "7": "当地劳务报酬标准*护理天数",  //护理费
                "8": "诊疗费+医药费以发票为准",  //医疗费
                "9": "以发票为准"  //鉴定费
            }
        ]
    }, {
        "name": "A8", //河北 
        "value": [
            {
                "0": "20,0,5",//残疾费
                "1": "按需据实",//交通费
                "2": "参照医疗机构",//营养费
                "3": "按需据实",//住宿费
                "4": "国家机关一般工作人员出差标准(元/天)*天数", //住院伙食补助费
                "5": "23119", //丧葬费
                "6": "法院根据实际情况判断", //精神损害抚慰金
                "7": "当地劳务报酬标准*护理天数",  //护理费
                "8": "诊疗费+医药费以发票为准",  //医疗费
                "9": "以发票为准"  //鉴定费
            }
        ]
    }, {
        "name": "A9", //江苏 
        "value": [
            {
                "0": "20,0,5",//残疾费
                "1": "按需据实",//交通费
                "2": "参照医疗机构",//营养费
                "3": "按需据实",//住宿费
                "4": "国家机关一般工作人员出差标准(元/天)*天数", //住院伙食补助费
                "5": "30433", //丧葬费
                "6": "法院根据实际情况判断", //精神损害抚慰金
                "7": "当地劳务报酬标准*护理天数",  //护理费
                "8": "诊疗费+医药费以发票为准",  //医疗费
                "9": "以发票为准"  //鉴定费
            }
        ]
    }, {
        "name": "A10", //甘肃 
        "value": [
            {
                "0": "20,0,5",//残疾费
                "1": "按需据实",//交通费
                "2": "参照医疗机构",//营养费
                "3": "按需据实",//住宿费
                "4": "省外50元/天，省内40元/天", //住院伙食补助费
                "5": "23480", //丧葬费
                "6": "法院根据实际情况判断", //精神损害抚慰金
                "7": "当地劳务报酬标准*护理天数",  //护理费
                "8": "诊疗费+医药费以发票为准",  //医疗费
                "9": "以发票为准"  //鉴定费
            }
        ]
    }, {
        "name": "A11", //广西 
        "value": [
            {
                "0": "20,0,5",//残疾费
                "1": "按需据实",//交通费
                "2": "参照医疗机构",//营养费
                "3": "330元*天数",//住宿费
                "4": "100元/天", //住院伙食补助费
                "5": "23424", //丧葬费
                "6": "死亡：20000-50000，伤残：(20000-50000)*伤害等级", //精神损害抚慰金
                "7": "当地劳务报酬标准*护理天数",  //护理费
                "8": "诊疗费+医药费以发票为准",  //医疗费
                "9": "以发票为准"  //鉴定费
            }
        ]
    }, {
        "name": "A12", //贵州 
        "value": [
            {
                "0": "20,0,5",//残疾费
                "1": "按需据实",//交通费
                "2": "参照医疗机构",//营养费
                "3": "按需据实",//住宿费
                "4": "100元/天", //住院伙食补助费
                "5": "21407", //丧葬费
                "6": "法院根据实际情况判断", //精神损害抚慰金
                "7": "当地劳务报酬标准*护理天数",  //护理费
                "8": "诊疗费+医药费以发票为准",  //医疗费
                "9": "以发票为准"  //鉴定费
            }
        ]
    }, {
        "name": "A15", //湖南 
        "value": [
            {
                "0": "20,0,5",//残疾费
                "1": "按需据实",//交通费
                "2": "参照医疗机构",//营养费
                "3": "按需据实",//住宿费
                "4": "100元/天", //住院伙食补助费
                "5": "21946", //丧葬费
                "6": "法院根据实际情况判断", //精神损害抚慰金
                "7": "当地劳务报酬标准*护理天数",  //护理费
                "8": "诊疗费+医药费以发票为准",  //医疗费
                "9": "以发票为准"  //鉴定费
            }
        ]
    }, {
        "name": "A14", //湖北
        "value": [
            {
                "0": "20,0,5",//残疾费
                "1": "按需据实",//交通费
                "2": "参照医疗机构",//营养费
                "3": "按需据实",//住宿费
                "4": "国家机关一般工作人员出差标准(元/天)*天数", //住院伙食补助费
                "5": "21608", //丧葬费
                "6": "法院根据实际情况判断", //精神损害抚慰金
                "7": "当地劳务报酬标准*护理天数",  //护理费
                "8": "诊疗费+医药费以发票为准",  //医疗费
                "9": "以发票为准"  //鉴定费
            }
        ]
    }, {
        "name": "A13", //黑龙江
        "value": [
            {
                "0": "20,0,5",//残疾费
                "1": "按需据实",//交通费
                "2": "参照医疗机构",//营养费
                "3": "按需据实",//住宿费
                "4": "国家机关一般工作人员出差标准(元/天)*天数", //住院伙食补助费
                "5": "22018", //丧葬费
                "6": "自然人：1000-10000元，法人等：5000-10000", //精神损害抚慰金
                "7": "当地劳务报酬标准*护理天数",  //护理费
                "8": "诊疗费+医药费以发票为准",  //医疗费
                "9": "以发票为准"  //鉴定费
            }
        ]
    }, {
        "name": "A16", //吉林
        "value": [
            {
                "0": "20,0,5",//残疾费
                "1": "按需据实",//交通费
                "2": "参照医疗机构",//营养费
                "3": "按需据实",//住宿费
                "4": "国家机关一般工作人员出差标准(元/天)*天数", //住院伙食补助费
                "5": "22018", //丧葬费
                "6": "自然人：1000-10000元，法人等：5000-10000", //精神损害抚慰金
                "7": "当地劳务报酬标准*护理天数",  //护理费
                "8": "诊疗费+医药费以发票为准",  //医疗费
                "9": "以发票为准"  //鉴定费
            }
        ]
    }, {
        "name": "A17", //江西
        "value": [
            {
                "0": "20,0,5",//残疾费
                "1": "按需据实",//交通费
                "2": "10-20元/天",//营养费
                "3": "100元/天",//住宿费
                "4": "30-50元/天", //住院伙食补助费
                "5": "23649", //丧葬费
                "6": "3000-5000 ，30000-50000", //精神损害抚慰金
                "7": "60-100元/天",  //护理费
                "8": "诊疗费+医药费以发票为准",  //医疗费
                "9": "以发票为准"  //鉴定费
            }
        ]
    }, {
        "name": "A18", //辽宁
        "value": [
            {
                "0": "20,0,5",//残疾费
                "1": "按需据实",//交通费
                "2": "参照医疗机构",//营养费
                "3": "100元/天",//住宿费
                "4": "国家机关一般工作人员出差标准(元/天)*天数", //住院伙食补助费
                "5": "24555", //丧葬费
                "6": "法院根据实际情况判断", //精神损害抚慰金
                "7": "当地劳务报酬标准*护理天数",  //护理费
                "8": "诊疗费+医药费以发票为准",  //医疗费
                "9": "以发票为准"  //鉴定费
            }
        ]
    }, {
        "name": "A19", //内蒙古
        "value": [
            {
                "0": "20,0,5",//残疾费
                "1": "按需据实",//交通费
                "2": "参照医疗机构",//营养费
                "3": "按需据实",//住宿费
                "4": "国家机关一般工作人员出差标准(元/天)*天数", //住院伙食补助费
                "5": "28169", //丧葬费
                "6": "法院根据实际情况判断", //精神损害抚慰金
                "7": "当地劳务报酬标准*护理天数",  //护理费
                "8": "诊疗费+医药费以发票为准",  //医疗费
                "9": "以发票为准"  //鉴定费
            }
        ]
    }, {
        "name": "A20", //宁夏
        "value": [
            {
                "0": "20,0,5",//残疾费
                "1": "按需据实",//交通费
                "2": "参照医疗机构",//营养费
                "3": "按需据实",//住宿费
                "4": "国家机关一般工作人员出差标准(元/天)*天数", //住院伙食补助费
                "5": "34241", //丧葬费
                "6": "法院根据实际情况判断", //精神损害抚慰金
                "7": "当地劳务报酬标准*护理天数",  //护理费
                "8": "诊疗费+医药费以发票为准",  //医疗费
                "9": "以发票为准"  //鉴定费
            }
        ]
    }, {
        "name": "A21", //青海
        "value": [
            {
                "0": "20,0,5",//残疾费
                "1": "按需据实",//交通费
                "2": "参照医疗机构",//营养费
                "3": "按需据实",//住宿费
                "4": "国家机关一般工作人员出差标准(元/天)*天数", //住院伙食补助费
                "5": "28902", //丧葬费
                "6": "法院根据实际情况判断", //精神损害抚慰金
                "7": "当地劳务报酬标准*护理天数",  //护理费
                "8": "诊疗费+医药费以发票为准",  //医疗费
                "9": "以发票为准"  //鉴定费
            }
        ]
    }, {
        "name": "A22", //山东
        "value": [
            {
                "0": "20,0,5",//残疾费
                "1": "按需据实",//交通费
                "2": "参照医疗机构",//营养费
                "3": "按需据实",//住宿费
                "4": "国家机关一般工作人员出差标准(元/天)*天数", //住院伙食补助费
                "5": "26480", //丧葬费
                "6": "法院根据实际情况判断", //精神损害抚慰金
                "7": "当地劳务报酬标准*护理天数",  //护理费
                "8": "诊疗费+医药费以发票为准",  //医疗费
                "9": "以发票为准"  //鉴定费
            }
        ]
    }, {
        "name": "A23", //山西
        "value": [
            {
                "0": "20,0,5",//残疾费
                "1": "按需据实",//交通费
                "2": "参照医疗机构",//营养费
                "3": "按需据实",//住宿费
                "4": "国家机关一般工作人员出差标准(元/天)*天数", //住院伙食补助费
                "5": "26480", //丧葬费
                "6": "法院根据实际情况判断", //精神损害抚慰金
                "7": "当地劳务报酬标准*护理天数",  //护理费
                "8": "诊疗费+医药费以发票为准",  //医疗费
                "9": "以发票为准"  //鉴定费
            }
        ]
    }, {
        "name": "A24", //陕西
        "value": [
            {
                "0": "20,0,5",//残疾费
                "1": "按需据实",//交通费
                "2": "参照医疗机构",//营养费
                "3": "按需据实",//住宿费
                "4": "30-50元/天", //住院伙食补助费
                "5": "26059", //丧葬费
                "6": "法院根据实际情况判断", //精神损害抚慰金
                "7": "当地劳务报酬标准*护理天数",  //护理费
                "8": "诊疗费+医药费以发票为准",  //医疗费
                "9": "以发票为准"  //鉴定费
            }
        ]
    }, {
        "name": "A25", //四川
        "value": [
            {
                "0": "20,0,5",//残疾费
                "1": "按需据实",//交通费
                "2": "参照医疗机构",//营养费
                "3": "按需据实",//住宿费
                "4": "20-50元/天", //住院伙食补助费
                "5": "20897", //丧葬费
                "6": "法院根据实际情况判断", //精神损害抚慰金
                "7": "50元/天",  //护理费
                "8": "诊疗费+医药费以发票为准",  //医疗费
                "9": "以发票为准"  //鉴定费
            }
        ]
    }, {
        "name": "A26", //天津
        "value": [
            {
                "0": "20,0,5",//残疾费
                "1": "按需据实",//交通费
                "2": "参照医疗机构",//营养费
                "3": "150元/天",//住宿费
                "4": "50元/天", //住院伙食补助费
                "5": "33000", //丧葬费
                "6": "自然人：1000-10000元，法人等：5000-10000", //精神损害抚慰金
                "7": "50元/天",  //护理费
                "8": "诊疗费+医药费以发票为准",  //医疗费
                "9": "以发票为准"  //鉴定费
            }
        ]
    }, {
        "name": "A28", //西藏
        "value": [
            {
                "0": "20,0,5",//残疾费
                "1": "按需据实",//交通费
                "2": "参照医疗机构",//营养费
                "3": "150元/天",//住宿费
                "4": "7天以内120元/天，超过7天，80元/天", //住院伙食补助费
                "5": "32204", //丧葬费
                "6": "法院根据实际情况判决", //精神损害抚慰金
                "7": "50元/天",  //护理费
                "8": "诊疗费+医药费以发票为准",  //医疗费
                "9": "以发票为准"  //鉴定费
            }
        ]
    }, {
        "name": "A29", //新疆
        "value": [
            {
                "0": "20,0,5",//残疾费
                "1": "按需据实",//交通费
                "2": "参照医疗机构",//营养费
                "3": "按需据实",//住宿费
                "4": "国家机关一般工作人员出差标准(元/天)*天数", //住院伙食补助费
                "5": "32204", //丧葬费
                "6": "法院根据实际情况判决", //精神损害抚慰金
                "7": "当地劳务报酬标准*护理天数",  //护理费
                "8": "诊疗费+医药费以发票为准",  //医疗费
                "9": "以发票为准"  //鉴定费
            }
        ]
    }, {
        "name": "A29", //新疆
        "value": [
            {
                "0": "20,0,5",//残疾费
                "1": "按需据实",//交通费
                "2": "参照医疗机构",//营养费
                "3": "按需据实",//住宿费
                "4": "国家机关一般工作人员出差标准(元/天)*天数", //住院伙食补助费
                "5": "32204", //丧葬费
                "6": "法院根据实际情况判决", //精神损害抚慰金
                "7": "当地劳务报酬标准*护理天数",  //护理费
                "8": "诊疗费+医药费以发票为准",  //医疗费
                "9": "以发票为准"  //鉴定费
            }
        ]
    }, {
        "name": "A32", //重庆
        "value": [
            {
                "0": "20,0,5",//残疾费
                "1": "按需据实",//交通费
                "2": "参照医疗机构",//营养费
                "3": "按需据实",//住宿费
                "4": "国家机关一般工作人员出差标准(元/天)*天数", //住院伙食补助费
                "5": "28428", //丧葬费
                "6": "法院根据实际情况判决", //精神损害抚慰金
                "7": "当地劳务报酬标准*护理天数",  //护理费
                "8": "诊疗费+医药费以发票为准",  //医疗费
                "9": "以发票为准"  //鉴定费
            }
        ]
    }, {
        "name": "A31", //云南
        "value": [
            {
                "0": "20,0,5",//残疾费
                "1": "80元/天",//交通费
                "2": "参照医疗机构",//营养费
                "3": "按需据实",//住宿费
                "4": "100元/天", //住院伙食补助费
                "5": "27512", //丧葬费
                "6": "法院根据实际情况判决", //精神损害抚慰金
                "7": "当地劳务报酬标准*护理天数",  //护理费
                "8": "诊疗费+医药费以发票为准",  //医疗费
                "9": "以发票为准"  //鉴定费
            }
        ]
    }
    , {
        "name": "A33", //海南
        "value": [
            {
                "0": "20,0,5",//残疾费
                "1": "实际发生的费用计算",//交通费
                "2": "根据受害人伤残情况参照医疗机构的意见确定",//营养费
                "3": "按需据实",//住宿费
                "4": "参照当地国家机关一般工作人员的出差伙食补助标准", //住院伙食补助费
                "5": "37944", //丧葬费
                "6": "法院根据实际情况判决", //精神损害抚慰金
                "7": "当地劳务报酬标准*护理天数",  //护理费
                "8": "根据医疗证明或鉴定结论予以赔偿",  //医疗费
                "9": "以发票为准"  //鉴定费
            }
        ]
    }
];

function setJS() {
    $("#wgfSpan").hide();
    $("#fyfSpan").hide();


    if (PostCheck()) {
        var type = $("#t1").val(); //伤残等级
        var cityType = $("#t2").val(); //0：城市、1：农村
        var city = $("#t3").val(); //0：城市、1：农村

        var age = parseInt($("#age").val()); //年龄

        var p3 = 0;
        var p5 = 0;

        $.each(ajType, function (idxc, itemc) {
            if (itemc.value === city) {
                p3 = parseFloat(itemc.p3);
                p5 = parseFloat(itemc.p5);
            }
        });
        var value = "";
        $.each(moneyType, function (idx, item) {
            if (item.name === city) {
                $.each(item.value, function (i) {
                    var list = item.value[i];
                    var prices1 = 0;
                    for (var key in list) {
                        if (list.hasOwnProperty(key)) {
                            var a = list["0"]; //残疾费
                            var b = list["1"]; //交通费
                            var c = list["2"]; //营养费
                            var d = list["3"]; //住宿费
                            var e = list["4"]; //住院伙食补助费
                            var f = list["5"]; //丧葬费
                            var g = list["6"]; //精神损害抚慰金
                            var l = list["7"]; //护理费
                            var m = list["8"]; //医疗费
                            var n = list["9"]; //鉴定费

                            $("#yyf").html(c);
                            $("#zsf").html(d);
                            $("#hsf").html(e);
                            $("#yyf").html(c);
                            $("#jtf").html(b);
                            $("#jsfwj").html(g);
                            $("#hlf").html(l);
                            $("#jdf").html(n);
                            $("#ylf").html(m);

                            if (type === "0") {//死亡
                                if (age < 60) {
                                    if (cityType === "0") {
                                        prices1 = p3 * 20;
                                    } else if (cityType === "1") {
                                        prices1 = p5 * 20;
                                    }
                                } else if (age >= 60 && age <= 74) {
                                    if (cityType === "0") {
                                        prices1 = p3 * (20 - (age - 60));
                                    } else if (cityType === "1") {
                                        prices1 = p5 * (20 - (age - 60));
                                    }
                                } else if (age >= 75) {
                                    if (cityType === "0") {
                                        prices1 = p3 * 5;
                                    } else if (cityType === "1") {
                                        prices1 = p5 * 5;
                                    }
                                }
                                $("#table").show();
                                $("#pzjPrices").html("死亡赔偿金：" + prices1.toFixed(2) + " 元");
                                $("#szfLi").show();
                                $("#szf").html(f);
                            } else {
                                if (age < 60) {
                                    if (cityType === "0") {
                                        prices1 = type * p3 * 20;
                                    } else if (cityType === "1") {
                                        prices1 = type * p5 * 20;
                                    }
                                } else if (age >= 60 && age <= 74) {
                                    if (cityType === "0") {
                                        prices1 = type * p3 * (20 - (age - 60));
                                    } else if (cityType === "1") {
                                        prices1 = type * p5 * (20 - (age - 60));
                                    }
                                } else if (age >= 75) {
                                    if (cityType === "0") {
                                        prices1 = type * p3 * 5;
                                    } else if (cityType === "1") {
                                        prices1 = type * p5 * 5;
                                    }
                                }
                                $("#table").show();
                                $("#szfLi").hide();
                                $("#pzjPrices").html("伤残赔偿金：" + prices1.toFixed(2) + " 元");
                            }
                        }
                    }
                });
                $.each(ajType, function (idx, item) {
                    if (item.value === city) {
                        $("#bzDiv").html("注：依据<a target='_balnk' href=\"elvshitools://html/jt/" + item.Url + "\">《" + item.name + "交通事故赔偿标准》</a>文件计算，结果仅供参考");
                    }
                });
            }
        });
    }
}

function setDays() {
    var money = $("#wgMoney").val();
    var day = $("#wgDay").val();
    var value = money * day;
    $("#wgfSpan").html(value + "元");
    $("#topDiv").hide();
    $("#main").show();
}


function setHide2(site) {
    $("#wgfSpan").show();
    $("#fyfSpan").show();
    if (site === 1) {
        $("#topDiv2").hide();
        $("#main").show();
    } else {
        $("#topDiv2").show();
        $("#main").hide();
    }
}


function setHide(site) {
    $("#wgfSpan").show();
    $("#fyfSpan").show();
    if (site === 1) {
        $("#topDiv").hide();
        $("#main").show();
    } else {
        $("#topDiv").show();
        $("#main").hide();
    }
}

function setFyMoney() {
    var validaCode = /^\d+$/;
    var type = $("#t1").val(); //伤残等级
    var cityType = $("#t2").val(); //0：城市、1：农村
    var city = $("#t3").val(); //0：城市、1：农村

    var prices = 0;
    var v1 = $("input[name='fyAge']");
    var v2 = $("input[name='fyNum']");
    var value = "";

    if (type === "0") {
        type ="1";
    }

    for (var l = 0; l < v1.length; l++) {
        var ages = v1[l].value;

        var nums = v2[l].value;

        if (ages !== "" && nums !== "") {
            if (!validaCode.test(ages)) {
                alert("年龄'" + ages + "'处请填写整数!");
                return false;
            } if (!validaCode.test(nums)) {
                alert("人数'" + ages + "'处请填写整数!");
                return false;
            }
        }
        var age = 0;
        var num = 0;
        age = parseInt(v1[l].value);
        num = parseInt(v2[l].value);


        var p3 = 0;
        var p5 = 0;

        $.each(ajType, function (idxc, itemc) {
            if (itemc.value === city) {
                p3 = parseFloat(itemc.p4);
                p5 = parseFloat(itemc.p6);
            }
        });

        if (age < 18) {
            if (cityType === "0") {

                prices = (p3 * (18 - age)) / num * type;
            } else {

                prices = (p5 * (18 - age)) / num * type;

            }
        } else if (age >= 18 && age <= 60) {
            if (cityType === "0") {
                prices = p3 * 20 / num * type;
            } else {
                prices = p5 * 20 / num * type;

            }
        } else if (age > 60 && age <= 74) {
            if (cityType === "0") {
                prices = p3 * (20 - (age - 60)) / num * type;
            } else {
                prices = p5 * (20 - (age - 60)) / num * type;

            }
        } else {
            if (cityType === "0") {
                prices = p3 * 5 / num * type;
            } else {
                prices = p5 * 5 / num * type;
            }
        }

        value += age + "岁/被抚养人数" + num + "。总抚养费为：" + prices.toFixed(2) + "元" + "<br /><br />";
    }
   
    $("#fyfSpan").html("");
    $("#fyfSpan").append(value);
    $("#topDiv2").hide();
    $("#main").show();
}


function setUL(type, value) {
    if (type === 0) {
        var htmls = $("#ul1").html();
        var str = "<ul class=\"ul1\" style='margin-top:30px;' >";
        str +=htmls + "</ul>";

        $("#pubs").before(str);
    } else {
        var v1 = $("input[name='fyAge']");
        if (v1.length > 1) {
           
            if ($(value).parent().parent('.ul1').attr("id") !== "ul1") {
                $(value).parent().parent('.ul1').remove();
            }
        }
    }
}


$(document).ready(function () {
    var values = "";
    $.each(ajType, function (idx, item) {
        values += " <option value=\"" + item.value + "\">" + item.name + "</option>";
    });
    $("#t3").append(values);
});