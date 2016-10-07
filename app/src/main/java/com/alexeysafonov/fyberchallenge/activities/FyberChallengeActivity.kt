package com.alexeysafonov.fyberchallenge.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.alexeysafonov.fyberchallenge.BuildConfig
import com.alexeysafonov.fyberchallenge.R
import com.alexeysafonov.fyberchallenge.api.FyberApi
import com.alexeysafonov.fyberchallenge.data.Puller
import com.alexeysafonov.fyberchallenge.data.Request
import com.alexeysafonov.fyberchallenge.data.ResponseListener
import com.alexeysafonov.fyberchallenge.fragments.OffersListFragment
import com.alexeysafonov.fyberchallenge.fragments.RequestFormFragment
import com.alexeysafonov.fyberchallenge.model.FyberResponse
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.net.NetworkInterface
import java.net.SocketException

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
        return "127.0.0.1" // TODO: It is for sure not right.
    }

    val okclientBuilder: OkHttpClient.Builder by lazy {
        val tmp = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            tmp.interceptors().add(interceptor)
        }
        tmp
    }
    val retrofit2: Retrofit by lazy {
        Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okclientBuilder.build())
                .build()
    }

    val api: FyberApi by lazy {
        retrofit2.create(FyberApi::class.java)
    }

    val puller: Puller by lazy {
        Puller(api, Schedulers.io(), AndroidSchedulers.mainThread())
    }

    val requestForm = RequestFormFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fyber_challenge)

        if (savedInstanceState == null) {
            // First start.
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.content_fragment, requestForm)
                    .commit()
        }
    }

    fun request(request: Request) {
        request.listener = object : ResponseListener {
            override fun onSuccess(response: FyberResponse) {

                if (response.offers.isEmpty()) {
                    // Show No offers.
                    requestForm.showNoOffers()
                } else {
                    // Show offers list.
                    val fragment = OffersListFragment()
                    fragment.offers = response.offers
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.content_fragment, fragment)
                            .addToBackStack(null)
                            .commit()
                    supportFragmentManager.executePendingTransactions()
                }
            }

            override fun onFailure(exception: Throwable) {
                requestForm.showError(exception.message?:getString(R.string.unknown_error))
            }

        }
        puller.request(request)
    }
}
