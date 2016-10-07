package com.alexeysafonov.fyberchallenge.fragments

import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.alexeysafonov.fyberchallenge.R
import com.alexeysafonov.fyberchallenge.activities.FyberChallengeActivity
import com.alexeysafonov.fyberchallenge.data.Request
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.jakewharton.rxbinding.view.RxView
import com.jakewharton.rxbinding.widget.RxTextView
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.IOException
import java.util.*

/**
 * This fragment provides UI for the request form.
 */
class RequestFormFragment: Fragment() {

    val noOffersView: TextView
        get() {
            return view?.findViewById(R.id.no_offers) as TextView
        }

    val errorView: TextView
        get() {
            return view?.findViewById(R.id.error_message) as TextView
        }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_request_form, container, false)

        val uid = RxTextView.textChanges(view.findViewById(R.id.uid) as EditText);
        val apiKey = RxTextView.textChanges(view.findViewById(R.id.api_key) as EditText);
        val appId = RxTextView.textChanges(view.findViewById(R.id.app_id) as EditText);
        val pub0 = RxTextView.textChanges(view.findViewById(R.id.pub0) as EditText);
        val adId = Observable.create<String> { subscriber ->
            var adInfo: AdvertisingIdClient.Info?
            adInfo = null
            try {
                adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context.applicationContext)
                if (!adInfo!!.isLimitAdTrackingEnabled()) {
                    subscriber.onNext(adInfo!!.getId())
                    subscriber.onCompleted()
                }
            } catch (e: IOException) {
                subscriber.onError(e)
            } catch (e: GooglePlayServicesNotAvailableException) {
                subscriber.onError(e)
            } catch (e: GooglePlayServicesRepairableException) {
                subscriber.onError(e)
            }
        }.subscribeOn(Schedulers.newThread())

        val requestButton = view.findViewById(R.id.request) as Button
        val requestButtonObservable = RxView.clicks(requestButton)
        val requestObservable = Observable.combineLatest(uid, apiKey, appId, pub0, adId) {
            uid, apiKey, appId, pub0, adId ->
            val protocol = activity as FyberChallengeActivity
            if (protocol != null && uid.isNotEmpty() && apiKey.isNotEmpty() && appId.isNotEmpty() && adId.isNotEmpty()) {
                val pubStr = if (pub0.toString().isEmpty()) null else pub0.toString()
                return@combineLatest Request(apiKey.toString(),
                        appId.toString().toInt(),
                        uid.toString(),
                        adId,
                        protocol.locale,
                        protocol.ip,
                        Build.VERSION.RELEASE,
                        Calendar.getInstance().timeInMillis / 1000,
                        pubStr)
            }
            return@combineLatest null
        }
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe { request ->
            // Make request button available if there is a request.
            requestButton.isEnabled = request != null
            // We have new request data so we have to hide no offers.
            noOffersView.visibility = View.INVISIBLE
            errorView.visibility = View.INVISIBLE
            requestButtonObservable.subscribe {
                val protocol = activity as FyberChallengeActivity
                if (protocol != null && request != null) {
                    protocol.request(request)
                    // this data already is requested.
                    requestButton.isEnabled = false
                }
            }
        }
        return view
    }

    fun showNoOffers() {
        noOffersView.visibility = View.VISIBLE
    }


    fun showError(message: String) {
        errorView.text = message
        errorView.visibility = View.VISIBLE
    }
}