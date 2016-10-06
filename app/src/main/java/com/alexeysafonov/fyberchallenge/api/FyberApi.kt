package com.alexeysafonov.fyberchallenge.api

import com.alexeysafonov.fyberchallenge.model.FyberResponse
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

/**
 * An API interface to Fyber offers.
 */
interface FyberApi {
    @GET("offers.json")
    fun getOffers(@Query("format") format:String,
                  @Query("appid") appId: Int,
                  @Query("uid") uid: String,
                  @Query("device_id") deviceId: String,
                  @Query("locale") locale: String,
                  @Query("ip") ip: String,
                  @Query("os_version") osVersion: String,
                  @Query("google_ad_id_limited_tracking_enabled") googleAdIsEnabled: Boolean,
                  @Query("timestamp") timeStamp: Long,
                  @Query("offer_types")offerTypes: Int,
                  @Query("hashkey") hash: String): Observable<FyberResponse>


    @GET("offers.json")
    fun getOffersPub0(@Query("format") format:String,
                      @Query("appid") appId: Int,
                      @Query("uid") uid: String,
                      @Query("device_id") deviceId: String,
                      @Query("locale") locale: String,
                      @Query("ip") ip: String,
                      @Query("os_version") osVersion: String,
                      @Query("google_ad_id_limited_tracking_enabled") googleAdIsEnabled: Boolean,
                      @Query("timestamp") timeStamp: Long,
                      @Query("offer_types")offerTypes: Int,
                      @Query("pub0") pub0: String,
                      @Query("hashkey") hash: String): Observable<FyberResponse>
}