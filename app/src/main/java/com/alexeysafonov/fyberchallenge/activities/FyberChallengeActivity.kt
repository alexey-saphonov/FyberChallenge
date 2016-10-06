package com.alexeysafonov.fyberchallenge.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import com.alexeysafonov.fyberchallenge.BuildConfig
import com.alexeysafonov.fyberchallenge.R
import com.alexeysafonov.fyberchallenge.api.FyberApi
import com.alexeysafonov.fyberchallenge.data.Puller
import com.alexeysafonov.fyberchallenge.data.Request
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.gson.GsonBuilder
import com.jakewharton.rxbinding.view.RxView
import com.jakewharton.rxbinding.widget.RxTextView
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.IOException
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*

class FyberChallengeActivity : AppCompatActivity() {

    val locale: String
    get() {
        return resources.configuration.locale.language;
    }

    val ip: String
    get() {
        val stringBuilder = StringBuilder()
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.getInetAddresses()
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress()) {
                        return inetAddress.getHostAddress().toString()
                    }

                }
            }
        } catch (ex: SocketException) {

        }
        return "127.0.0.1" // TODO: It is for sure not right
    }

    var lastRequest: Request? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fyber_challenge)

        val okclientBuilder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okclientBuilder.interceptors().add(interceptor)
        }

        val retrofit2 = Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okclientBuilder.build())
                .build()
        val api = retrofit2.create(FyberApi::class.java)
        val puller = Puller(api, Schedulers.io(), AndroidSchedulers.mainThread())

        val uid = RxTextView.textChanges(findViewById(R.id.uid) as EditText);
        val apiKey = RxTextView.textChanges(findViewById(R.id.api_key) as EditText);
        val appId = RxTextView.textChanges(findViewById(R.id.app_id) as EditText);
        val pub0 = RxTextView.textChanges(findViewById(R.id.pub0) as EditText);
        val adId = Observable.create<String> {subscriber ->
            var adInfo: AdvertisingIdClient.Info?
            adInfo = null
            try {
                adInfo = AdvertisingIdClient.getAdvertisingIdInfo(this@FyberChallengeActivity.getApplicationContext())
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

        val requestButton = findViewById(R.id.request) as Button
        val requestButtonObservable = RxView.clicks(requestButton)
        val requestObservable = Observable.combineLatest(uid, apiKey, appId, pub0, adId) {
            uid, apiKey, appId, pub0, adId ->
            if (!uid.isEmpty() && !apiKey.isEmpty() && !appId.isEmpty() && !adId.isEmpty()) {
                val pubStr = if (pub0.toString().isEmpty()) null else pub0.toString()
                return@combineLatest Request(apiKey.toString(),
                        appId.toString().toInt(),
                        uid.toString(),
                        adId,
                        this@FyberChallengeActivity.locale,
                        this@FyberChallengeActivity.ip,
                        android.os.Build.VERSION.RELEASE,
                        pubStr,
                        Calendar.getInstance().timeInMillis / 1000)
            }
            return@combineLatest null
        }
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe { request ->
            requestButton.isEnabled = request != null
            requestButtonObservable.subscribe {
                if (request != null) {
                    puller.request(request)
                }
            }
        }
    }
}
