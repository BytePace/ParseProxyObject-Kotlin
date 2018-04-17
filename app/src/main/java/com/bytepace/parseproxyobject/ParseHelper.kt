package com.bytepace.parseproxyobject

import com.parse.ParseGeoPoint
import com.parse.ParseObject
import org.json.JSONObject
import java.io.Serializable

/**
 * Created by Viktor Matskevich on 16.04.2018.
 * Company: Bytepace
 * EMAIL: viktor.matskevich@bytepace.com
 */
class ParseHelper {
    companion object {
        fun from(obj: Any?): Proxy {
            obj ?: throw NullPointerException("The 'obj' must been not null")
            return Proxy.create(obj)
        }
    }
}

abstract class Proxy(var data: JSONObject) : Serializable {
    companion object {

        @Suppress("UNCHECKED_CAST")
        fun <T> create(obj: Any): T {
            return when (obj) {
                is ParseObject -> ParseProxyObject(obj.className, ParseProxyObject.getData(obj)) as T
                is ParseGeoPoint -> ParseProxyGeoPoint(ParseProxyGeoPoint.getData(obj)) as T
                else -> throw IllegalArgumentException("Invalid parameter type '${obj::class.java}'")
            }
        }
    }

    abstract fun <T> restore(): T
}

class ParseProxyObject(var className: String, data: JSONObject) : Proxy(data) {
    companion object {
        internal fun getData(obj: ParseObject): JSONObject {
            val data = JSONObject()
            obj.keySet().forEach {
                var value = obj[it]
                if (value is ParseObject || value is ParseGeoPoint) {
                    value = ParseHelper.from(value)
                }
                data.put(it, value)
            }
            return data
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> restore(): T {
        val parseObject = ParseObject.create(className)
        data.keys().forEach {
            val value = data[it]
            parseObject.put(it, when (value) {
                is ParseProxyObject -> value.restore()
                is ParseProxyGeoPoint -> value.restore<ParseGeoPoint>()
                else -> value
            })
        }
        return parseObject as T
    }
}

class ParseProxyGeoPoint(data: JSONObject) : Proxy(data) {
    companion object {
        private const val KEY_GEO_POINT_LAT = "lat"
        private const val KEY_GEO_POINT_LNG = "lng"

        fun getData(obj: ParseGeoPoint): JSONObject {
            val data = JSONObject()
            data.put(KEY_GEO_POINT_LAT, obj.latitude)
            data.put(KEY_GEO_POINT_LNG, obj.longitude)
            return data
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> restore(): T = ParseGeoPoint(
            data.getDouble(KEY_GEO_POINT_LAT), data.getDouble(KEY_GEO_POINT_LNG)) as T
}
