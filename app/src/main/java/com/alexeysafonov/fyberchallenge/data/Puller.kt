package com.alexeysafonov.fyberchallenge.data

import android.util.Log
import com.alexeysafonov.fyberchallenge.api.FyberApi
import rx.Scheduler
import java.util.*

/**
 *
 */
open class Puller (val api: FyberApi,
                   val workingThread: Scheduler,
                   val communicationThread: Scheduler) {

    open fun request(request: Request) {
        try {
            Log.d("BLAH", "line is " + request.line)
            Log.d("BLAH", "hash is " + request.sha1)
            Log.d("BLAH", "time stamp now is " + Calendar.getInstance().timeInMillis)
            val observable = if (request.pub0 == null)
                api.getOffers(request.format,
                        request.appId,
                        request.uid,
                        request.deviceId,
                        request.locale,
                        request.ip,
                        request.osVersion,
                        request.googleAdIsEnabled,
                        request.timestamp,
                        request.offerTypes,
                        request.sha1)
            else
                api.getOffersPub0(request.format,
                        request.appId,
                        request.uid,
                        request.deviceId,
                        request.locale,
                        request.ip,
                        request.osVersion,
                        request.googleAdIsEnabled,
                        request.timestamp,
                        request.offerTypes,
                        request.pub0,
                        request.sha1)
            val response = observable
                    .subscribeOn(workingThread)
                    .observeOn(communicationThread)
                    .subscribe({request.onSuccess(it)},
                            {if (it != null) request.onFailure(it)
                            else request.onFailure(Exception())})
        } catch (e: Exception) {
            request.onFailure(e)
        }
    }
}