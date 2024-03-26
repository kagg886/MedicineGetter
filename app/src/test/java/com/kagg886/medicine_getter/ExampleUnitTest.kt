package com.kagg886.medicine_getter

import com.kagg886.medicine_getter.network.getAIResult
import com.kagg886.medicine_getter.network.NetWorkClient
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testOCRResult() {
        //{
        //                "confidence": 0.9892696142196655,
        //                "text": "慢严舒柠",
        //                "text_box_position": [
        //                    [
        //                        72,
        //                        149
        //                    ],
        //                    [
        //                        274,
        //                        142
        //                    ],
        //                    [
        //                        275,
        //                        186
        //                    ],
        //                    [
        //                        74,
        //                        193
        //                    ]
        //                ]
        //}
        val ocr = "[{\"data\": [{\"confidence\": 0.9892696142196655,\"text\": \"慢严舒柠\",\"text_box_position\": [[72, 149],[274, 142],[275, 186],[74, 193]]},{\"confidence\": 0.9696086049079895,\"text\": \"复方青橄榄利咽含片说明书\",\"text_box_position\": [[76, 197],[904, 207],[904, 284],[76, 273]]},{\"confidence\": 0.9983592629432678,\"text\": \"乙类\",\"text_box_position\": [[1094, 195],[1143, 195],[1143, 218],[1094, 218]]},{\"confidence\": 0.9958167672157288,\"text\": \"请仔细阅读说明书并按说明使用或在药师指导下购买和使用\",\"text_box_position\": [[104, 273],[879, 282],[879, 325],[104, 316]]},{\"confidence\": 0.9768179059028625,\"text\": \"硬脂酸镁。\",\"text_box_position\": [[218, 378],[342, 382],[342, 415],[218, 412]]},{\"confidence\": 0.9849770069122314,\"text\": \"【性状】本品为浅棕色的异型片；气清香，味清凉、微甜。\",\"text_box_position\": [[72, 410],[778, 412],[778, 451],[72, 449]]},{\"confidence\": 0.9762603640556335,\"text\": \"【功能主治】滋阴清热，利咽解毒。适用于咽部灼热，疼痛，咽干不适等。\",\"text_box_position\": [[78, 442],[904, 444],[904, 481],[78, 479]]},{\"confidence\": 0.9983109831809998,\"text\": \"【规\",\"text_box_position\": [[78, 476],[131, 476],[131, 506],[78, 506]]},{\"confidence\": 0.969456672668457,\"text\": \"格】每片重0.5克（相当于饮片0.12克，含薄荷脑1.3毫克）。\",\"text_box_position\": [[165, 474],[850, 476],[850, 510],[165, 508]]},{\"confidence\": 0.9925244450569153,\"text\": \"【用法用量】\",\"text_box_position\": [[78, 504],[245, 504],[245, 536],[78, 536]]},{\"confidence\": 0.9767800569534302,\"text\": \"含服。一次1～2片，每小时一次，一日10~20片。\",\"text_box_position\": [[224, 504],[787, 508],[787, 540],[224, 536]]},{\"confidence\": 0.9994087219238281,\"text\": \"【不良反应】\",\"text_box_position\": [[81, 538],[239, 538],[239, 565],[81, 565]]},{\"confidence\": 0.9978029131889343,\"text\": \"尚不明确。\",\"text_box_position\": [[228, 536],[348, 540],[348, 568],[228, 565]]},{\"confidence\": 0.9864766001701355,\"text\": \"【禁\",\"text_box_position\": [[85, 566],[182, 566],[182, 595],[85, 595]]},{\"confidence\": 0.7796619534492493,\"text\": \"忌】\",\"text_box_position\": [[169, 570],[241, 570],[241, 593],[169, 593]]},{\"confidence\": 0.9949242472648621,\"text\": \"尚不明确。\",\"text_box_position\": [[228, 565],[350, 568],[350, 597],[228, 593]]},{\"confidence\": 0.9994965195655823,\"text\": \"【注意事项】\",\"text_box_position\": [[83, 597],[243, 597],[243, 623],[83, 623]]},{\"confidence\": 0.9140797257423401,\"text\": \"1.忌烟酒、辛辣、鱼醒食物。\",\"text_box_position\": [[226, 595],[548, 597],[548, 629],[226, 627]]},{\"confidence\": 0.9811400175094604,\"text\": \"6.服药3天症状无缓解，应去医院就诊\",\"text_box_position\": [[694, 597],[1119, 595],[1119, 623],[694, 625]]},{\"confidence\": 0.9872641563415527,\"text\": \"2.不宜在服药期间同时服用温补性中药。7.对本品过敏者禁用，过敏体质者慎用。\",\"text_box_position\": [[232, 625],[1132, 623],[1132, 655],[232, 657]]},{\"confidence\": 0.9886167645454407,\"text\": \"3.孕妇慎用。糖尿病患者、\",\"text_box_position\": [[236, 655],[525, 659],[525, 685],[236, 682]]},{\"confidence\": 0.9993157982826233,\"text\": \"8本品性状发生改变时禁止使用。\",\"text_box_position\": [[694, 657],[1056, 657],[1056, 684],[694, 684]]},{\"confidence\": 0.9991533756256104,\"text\": \"儿童应在医师指导下服用。\",\"text_box_position\": [[260, 685],[551, 687],[551, 714],[260, 712]]},{\"confidence\": 0.9934667944908142,\"text\": \"9.儿童必须在成人监护下使用\",\"text_box_position\": [[692, 685],[1027, 685],[1027, 712],[692, 712]]},{\"confidence\": 0.8709688782691956,\"text\": \"4.牌虚大便塘者慎用。\",\"text_box_position\": [[239, 716],[479, 716],[479, 742],[239, 742]]},{\"confidence\": 0.9967017769813538,\"text\": \"10.请将本品放在儿童不能接触的地方。\",\"text_box_position\": [[696, 716],[1117, 716],[1117, 742],[696, 742]]},{\"confidence\": 0.9650434255599976,\"text\": \"5.属风寒感冒咽痛者，症见恶寒发热、\",\"text_box_position\": [[239, 744],[652, 744],[652, 771],[239, 771]]},{\"confidence\": 0.9779883027076721,\"text\": \"11如正在使用其他药品，\",\"text_box_position\": [[694, 744],[964, 744],[964, 771],[694, 771]]},{\"confidence\": 0.949227511882782,\"text\": \"无汗、鼻流清沸者慎用。\",\"text_box_position\": [[260, 769],[525, 773],[525, 801],[260, 797]]},{\"confidence\": 0.9918608069419861,\"text\": \"使用本品前请咨询医师或药师。\",\"text_box_position\": [[730, 773],[1067, 773],[1067, 799],[730, 799]]},{\"confidence\": 0.9995221495628357,\"text\": \"【药物相互作用】如与其他药物同时使用可能会发生药物相互作用，详情请咨询医师或药师。\",\"text_box_position\": [[83, 796],[1105, 799],[1105, 831],[83, 828]]},{\"confidence\": 0.760735273361206,\"text\": \"【购\",\"text_box_position\": [[89, 828],[133, 828],[133, 860],[89, 860]]},{\"confidence\": 0.9773615002632141,\"text\": \"藏】密封。\",\"text_box_position\": [[177, 829],[306, 829],[306, 856],[177, 856]]},{\"confidence\": 0.9596172571182251,\"text\": \"装】铝塑泡罩包装，12片/板×1板/盒。\",\"text_box_position\": [[175, 854],[609, 856],[609, 888],[175, 886]]},{\"confidence\": 0.984332799911499,\"text\": \"铝塑泡罩包装，16片/板×1板/盒。\",\"text_box_position\": [[236, 883],[609, 884],[609, 918],[236, 916]]},{\"confidence\": 0.9881080389022827,\"text\": \"铝塑泡罩包装，12片/板×2板/盒。\",\"text_box_position\": [[234, 911],[609, 913],[609, 947],[234, 945]]},{\"confidence\": 0.9934626817703247,\"text\": \"铝塑泡罩包装，13片/板×2板/盒。\",\"text_box_position\": [[236, 943],[610, 943],[610, 975],[236, 975]]},{\"confidence\": 0.9679169654846191,\"text\": \"铝塑泡罩包装，\",\"text_box_position\": [[236, 973],[403, 973],[403, 1005],[236, 1005]]},{\"confidence\": 0.9723756909370422,\"text\": \"，14片/板×2板/盒。\",\"text_box_position\": [[392, 972],[609, 973],[609, 1002],[392, 1000]]},{\"confidence\": 0.9681292176246643,\"text\": \"铝塑泡罩包装，\",\"text_box_position\": [[232, 1002],[397, 998],[397, 1030],[232, 1034]]},{\"confidence\": 0.939491331577301,\"text\": \"，16片/板×2板/盒。\",\"text_box_position\": [[388, 998],[607, 1002],[607, 1034],[388, 1030]]},{\"confidence\": 0.988897442817688,\"text\": \"铝塑泡罩包装，\",\"text_box_position\": [[228, 1032],[397, 1032],[397, 1064],[228, 1064]]},{\"confidence\": 0.9526174068450928,\"text\": \"12片／板×4板/盒。\",\"text_box_position\": [[409, 1030],[609, 1030],[609, 1062],[409, 1062]]},{\"confidence\": 0.9908437728881836,\"text\": \"铝塑泡罩包装，\",\"text_box_position\": [[226, 1062],[403, 1062],[403, 1094],[226, 1094]]},{\"confidence\": 0.9436518549919128,\"text\": \"，12片/板×5板/盒。\",\"text_box_position\": [[386, 1064],[607, 1064],[607, 1091],[386, 1091]]},{\"confidence\": 0.960479736328125,\"text\": \"【有效期】24个月。\",\"text_box_position\": [[74, 1089],[319, 1091],[319, 1124],[74, 1123]]},{\"confidence\": 0.9952683448791504,\"text\": \"【执行标准】\",\"text_box_position\": [[79, 1124],[236, 1124],[236, 1151],[79, 1151]]},{\"confidence\": 0.9807584881782532,\"text\": \"国家药品监督管理局标准YBZ05262019\",\"text_box_position\": [[218, 1121],[677, 1121],[677, 1153],[218, 1153]]},{\"confidence\": 0.9922796487808228,\"text\": \"【批准文号】国药准字B20050002\",\"text_box_position\": [[74, 1153],[464, 1153],[464, 1185],[74, 1185]]},{\"confidence\": 0.9823325872421265,\"text\": \"【说明书修订日期】2020年12月30日\",\"text_box_position\": [[74, 1185],[492, 1188],[492, 1215],[74, 1211]]},{\"confidence\": 0.9978243112564087,\"text\": \"【生产企业】企业名称：桂龙药业（安徽）有限公司\",\"text_box_position\": [[72, 1254],[649, 1254],[649, 1286],[72, 1286]]},{\"confidence\": 0.9889664649963379,\"text\": \"生产地址：马鞍山市当涂县经济开发区红旗南路与明\",\"text_box_position\": [[70, 1284],[664, 1286],[664, 1325],[70, 1323]]},{\"confidence\": 0.9917135834693909,\"text\": \"珠路交叉口\",\"text_box_position\": [[72, 1316],[201, 1316],[201, 1350],[72, 1350]]},{\"confidence\": 0.9997074604034424,\"text\": \"邮政编码：243100\",\"text_box_position\": [[70, 1345],[285, 1352],[285, 1386],[70, 1379]]},{\"confidence\": 0.9526852965354919,\"text\": \"电话号码：（0555）6758271\",\"text_box_position\": [[411, 1354],[723, 1350],[723, 1384],[411, 1387]]},{\"confidence\": 0.9903055429458618,\"text\": \"传真号码：（0555）6758272\",\"text_box_position\": [[70, 1377],[378, 1387],[378, 1421],[70, 1411]]},{\"confidence\": 0.9989103078842163,\"text\": \"服务电话：4008801842\",\"text_box_position\": [[407, 1387],[698, 1380],[698, 1414],[407, 1421]]},{\"confidence\": 0.9827783107757568,\"text\": \"网址：www.manyanshuning.com.cn\",\"text_box_position\": [[70, 1407],[489, 1421],[487, 1460],[68, 1446]]},{\"confidence\": 0.998565673828125,\"text\": \"南路与明珠路交叉口\",\"text_box_position\": [[70, 1508],[300, 1517],[300, 1544],[70, 1535]]},{\"confidence\": 0.9880183935165405,\"text\": \"如有问题可与生产企业直接联系\",\"text_box_position\": [[66, 1538],[430, 1554],[428, 1592],[64, 1576]]},{\"confidence\": 0.9976420998573303,\"text\": \"3250721\",\"text_box_position\": [[1054, 1538],[1151, 1538],[1151, 1572],[1054, 1572]]}],\"save_path\": \"\"}]"
        val result = Json.parseToJsonElement(ocr).jsonArray[0].jsonObject["data"]?.jsonArray ?: JsonArray(emptyList())

        val results = result.mapNotNull { (it as? JsonObject)?.get("text")?.jsonPrimitive?.contentOrNull }

        val name = results.find { it.contains("说明书") }?.replace("说明书", "") ?: "识别失败说明书"
        val id = results.find { it.contains("国药准字") }?.split("国药准字")?.get(1) ?: "国药准字识别失败"
        val otc = results.find { it.contains("甲") || it.contains("乙") || it.contains("丙") } ?: "识别失败"
        val use = results.find { it.contains("一次") || it.contains("小时") } ?: "识别失败"
        val doctor = results.find { it.contains("功能主治") } ?: "未知"

        val percent: String = when {
            otc.contains("甲") -> "20%"
            otc.contains("乙") -> "25%"
            else -> "未知"
        }
        println("name" to name)
        println("id" to id)
        println("otc" to otc)
        println("use" to use)
        println("doctor" to doctor)
        println("percent" to percent)
    }

    data class OCRBean(
        val confidence: Double,
        val text:String
    )

    @Test
    fun testJSON() {
        val s = """
            {
                "Anxixiang": {
                    "information": "安息香为球形颗粒压结成的团块，大小不等，外面红棕色至灰棕色，嵌有黄白色及灰白色不透明的杏仁样颗粒，表面粗糙不平坦。常温下质坚脆，加热即软化。气芳香、味微辛。安息香有泰国安息香与苏门答腊安息香两种。中国进口商品主要为泰国安息香，分有水安息、旱安息、白胶香等规格。安息香功能、主治：开窍清神，行气活血，止痛。用于中风痰厥，气郁暴厥，中恶昏迷，心腹疼痛，产后血晕，小儿惊风。",
                    "link": "https://baike.baidu.com/item/%E5%AE%89%E6%81%AF%E9%A6%99/687192"
                },
            }
        """.trimIndent()
    }

    @Test
    fun testAISuccess(): Unit = runBlocking {
        println(File("").absolutePath)
        val network = NetWorkClient("http://localhost:8080")

        val a = network.getAIResult(File("../test/success.jpg").inputStream())
        assertNotNull(a)
    }

    @Test
    fun testAiError(): Unit = runBlocking {
        val network = NetWorkClient("http://localhost:8080")

        assertThrows(IllegalStateException::class.java) {
            runBlocking {
                val a = network.getAIResult(File("../test/failed.png").inputStream())
                println(a)
            }
        }
    }
}