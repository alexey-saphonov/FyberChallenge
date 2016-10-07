package com.alexeysafonov.fyberchallenge.data

import com.alexeysafonov.fyberchallenge.model.FyberResponse
import com.alexeysafonov.fyberchallenge.sha1.sha1

/**
 * Fyber request.
 */
open class Request(val apiKey: String,
                   val appId: Int,
                   val uid: String,
                   val deviceId: String,
                   val locale: String,
                   val ip: String,
                   val osVersion: String,
                   val timestamp: Long,
                   val pub0: String? = null,
                   val googleAdIsEnabled: Boolean = true,
                   val format: String = "json",
                   val offerTypes: Int = 112) : ResponseListener {

    val line: String by lazy { combine() }


    open fun combine(): String {
        val res = listOf(("appid" to appId.toString()),
                ("device_id" to deviceId),
                ("os_version" to osVersion),
                ("google_ad_id_limited_tracking_enabled" to googleAdIsEnabled.toString()),
                ("format" to format),
                ("timestamp" to timestamp.toString()),
                ("ip" to ip),
                ("locale" to locale),
                ("offer_types" to offerTypes.toString()),
                ("pub0" to (pub0 ?: "")), ("uid" to uid))
                .filter { !it.second.isEmpty() }
                .sortedBy{ it.first }
                .map{ it.first + "=" + it.second }
                .reduce{ first, second -> first + "&" + second } + "&" + apiKey
        return res
    }

    val sha1 by lazy { line.sha1() }

    var listener: ResponseListener? = null

    override fun onSuccess(response: FyberResponse) {
        listener?.onSuccess(response)
    }

    override fun onFailure(exception: Throwable) {
        listener?.onFailure(exception)
    }
}

