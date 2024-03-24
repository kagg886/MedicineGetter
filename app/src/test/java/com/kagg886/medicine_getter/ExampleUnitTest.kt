package com.kagg886.medicine_getter

import com.kagg886.medicine_getter.network.getAIResult
import com.kagg886.medicine_getter.network.NetWorkClient
import kotlinx.coroutines.runBlocking
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