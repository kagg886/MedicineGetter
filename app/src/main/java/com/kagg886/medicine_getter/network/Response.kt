package com.kagg886.medicine_getter.network

import android.R.attr.bitmap
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import com.kagg886.sylu_eoa.api.v2.network.NetWorkClient
import com.kagg886.sylu_eoa.api.v2.network.asJSONOrigin
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import java.util.*
import kotlin.math.min


//"name":"\u732a\u7259\u7682","rate":1.0,"spell":"Zhuyazao"
@Serializable
data class AIResult(val name:String,val rate:Float,val spell:String)

suspend fun NetWorkClient.getImage(spell:String):Bitmap = execute("/ai/image?spell=$spell").body!!.byteStream().use {
    val origin = BitmapFactory.decodeStream(it)
    val s = Bitmap.createBitmap(origin, 0, 0, origin.getWidth(), origin.getHeight(), Matrix().apply {
        postScale(200f/origin.width.toFloat(),200f/origin.height.toFloat())
    }, false)
    origin.recycle()
    return s
}


suspend fun NetWorkClient.getAIResult(f: InputStream): JsonElement {
    val k = execute("/ai/getResultForImage") {
        this.post(
            MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "aaa.png", f.readAllOnLowerAndroid().toRequestBody())
                .build()
        )
    }.asJSONOrigin().jsonObject

    if (k["code"]!!.jsonPrimitive.int != 200) {
        throw IllegalStateException("Server Error:${k["msg"]!!.jsonPrimitive.content}")
    }
    return Json.decodeFromJsonElement(k["data"]!!)
}

fun InputStream.readAllOnLowerAndroid(): ByteArray {
    var bufs: MutableList<ByteArray>? = null
    var result: ByteArray? = null
    var total = 0
    var remaining: Int = Integer.MAX_VALUE
    var n: Int
    do {
        var buf = ByteArray(min(remaining, 8192))
        var nread = 0

        // read to EOF which may read more or less than buffer size
        while ((read(
                buf, nread,
                min((buf.size - nread).toDouble(), remaining.toDouble()).toInt()
            ).also { n = it }) > 0
        ) {
            nread += n
            remaining -= n
        }

        if (nread > 0) {
            if ((Integer.MAX_VALUE - 8) - total < nread) {
                throw OutOfMemoryError("Required array size too large")
            }
            if (nread < buf.size) {
                buf = Arrays.copyOfRange(buf, 0, nread)
            }
            total += nread
            if (result == null) {
                result = buf
            } else {
                if (bufs == null) {
                    bufs = ArrayList()
                    bufs.add(result)
                }
                bufs.add(buf)
            }
        }
        // if the last call to read returned -1 or the number of bytes
        // requested have been read then break
    } while (n >= 0 && remaining > 0)

    if (bufs == null) {
        if (result == null) {
            return ByteArray(0)
        }
        return if (result.size == total) result else result.copyOf(total)
    }

    result = ByteArray(total)
    var offset = 0
    remaining = total
    for (b in bufs) {
        val count = min(b.size.toDouble(), remaining.toDouble()).toInt()
        System.arraycopy(b, 0, result, offset, count)
        offset += count
        remaining -= count
    }

    return result
}