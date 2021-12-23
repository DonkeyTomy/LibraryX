package com.zzx.utils.json

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import org.json.JSONObject

/**@author Tomy
 * Created by Tomy on 5/1/2021.
 */
object GSonUtil {
    val mGSon: Gson by lazy {
        GsonBuilder().setLenient().registerTypeAdapterFactory(NullStringToEmptyAdapterFactory()).create()
    }

    /**
     * 将Json字符串转换为数据对象
     * @param jsonStr String
     * @return T
     */
    inline fun <reified T> fromJSon(jsonStr: String): T {
        return mGSon.fromJSon(jsonStr)
    }

    /**
     * 将JSONObject转换为数据对象
     * @param json JSONObject
     * @return T
     */
    inline fun <reified T> fromJSon(json: JSONObject): T {
        return mGSon.fromJSon(json)
    }

    /**
     * 将 JsonObject 转换为数据对象
     * @param json JSONObject
     * @return T
     */
    inline fun <reified T> fromJSon(json: JsonObject): T {
        return mGSon.fromJSon(json)
    }

    /**
     * 将数据对象转换为Json字符串
     * @param json T
     * @return String
     */
    inline fun <reified T> toJSonStr(json: T): String {
        return mGSon.toJSonStr(json)
    }

    /**
     * 将字符串转换为JsonObject对象
     * @param jsonStr String
     * @return JsonObject
     */
    fun toJsonObject(jsonStr: String): JsonObject {
        return JsonParser.parseString(jsonStr).asJsonObject
    }

    /**
     * 将数据对象转换为JSONObject.
     * @param jsonObj T
     * @return JSONObject
     */
    inline fun <reified T> toJSONObject(jsonObj: T): JSONObject {
        return JSONObject(toJSonStr(jsonObj))
    }

}

inline fun <reified T> Gson.fromJSon(jsonStr: String): T {
    return this.fromJson(jsonStr, T::class.java)
}

inline fun <reified T> Gson.fromJSon(json: JSONObject): T {
    return this.fromJson(json.toString(), T::class.java)
}

inline fun <reified T> Gson.fromJSon(json: JsonObject): T {
    return this.fromJson(json, T::class.java)
}

inline fun <reified T> Gson.toJSonStr(jsonObj: T): String {
    return this.toJson(jsonObj)
}

class NullStringToEmptyAdapterFactory: TypeAdapterFactory {

    override fun <T : Any?> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        val rawType = type.rawType as Class<T>
        if (rawType != String::class.java) {
            return null
        }
        return StringNullAdapter() as TypeAdapter<T>
    }

}

class StringNullAdapter: TypeAdapter<String>() {
    override fun write(out: JsonWriter, value: String?) {
        out.value(value ?: "")
    }

    override fun read(reader: JsonReader): String {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull()
            return ""
        }
        return reader.nextString()
    }

}

