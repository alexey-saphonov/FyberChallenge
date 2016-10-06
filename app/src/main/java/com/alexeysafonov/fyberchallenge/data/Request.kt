package com.alexeysafonov.fyberchallenge.data

import android.util.Log
import com.alexeysafonov.fyberchallenge.model.FyberResponse
import com.alexeysafonov.fyberchallenge.sha1.sha1

/**
 * Fyber request.
 */
open class Request (val apiKey: String,
                    val appId: Int,
                    val uid: String,
                    val deviceId: String,
                    val locale: String,
                    val ip: String,
                    val osVersion: String,
                    val pub0: String?,
                    val timestamp: Long,
                    val googleAdIsEnabled: Boolean = true,
                    val format: String = "json",
                    val offerTypes: Int = 112) {

    val line = listOf(("appid" to appId.toString()),
            ("device_id" to deviceId),
            ("os_version" to osVersion),
            ("google_ad_id_limited_tracking_enabled" to googleAdIsEnabled.toString()),
            ("format" to format),
            ("timestamp" to timestamp.toString()),
            ("ip" to ip),
            ("locale" to locale),
            ("offer_types" to offerTypes.toString()),
            ("pub0" to (pub0?:"")), ("uid" to uid))
            .filter { !it.second.isEmpty() }
            .sortedBy { it.first }
            .map { it.first + "=" + it.second }
            .reduce { first, second ->  first + "&" + second} + "&" + apiKey

    val sha1 = line.sha1()

    open fun onSuccess(response: FyberResponse) {
        Log.d("BLAH", response.toString())
    }
    open fun onFailure(exception: Throwable) {
        Log.d("BLAH", exception.message)
    }
}

