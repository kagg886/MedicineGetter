package com.kagg886.medicine_getter

import com.kagg886.medicine_getter.network.getAIResult
import com.kagg886.sylu_eoa.api.v2.network.NetWorkClient
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