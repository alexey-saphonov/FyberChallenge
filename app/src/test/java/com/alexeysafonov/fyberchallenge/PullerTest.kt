package com.alexeysafonov.fyberchallenge

import com.alexeysafonov.fyberchallenge.api.FyberApi
import com.alexeysafonov.fyberchallenge.data.Puller
import com.alexeysafonov.fyberchallenge.data.Request
import com.alexeysafonov.fyberchallenge.model.*
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import rx.Observable
import rx.Subscriber
import rx.schedulers.Schedulers

/**
 * This tests that request can succeed if we have successfully performed API call otherwise (fail call or any exception during the call)
 * request fails.
 */
class PullerTest {

    val NOT_IMPORTANT_INT = 0
    val NOT_IMPORTANT_STRING = ""

    val MOCK_INFORMATION = FyberInformation(NOT_IMPORTANT_STRING, NOT_IMPORTANT_INT, NOT_IMPORTANT_STRING, NOT_IMPORTANT_STRING, NOT_IMPORTANT_STRING, NOT_IMPORTANT_STRING)
    val MOCK_OFFERS = FyberOffer(NOT_IMPORTANT_STRING, NOT_IMPORTANT_INT, NOT_IMPORTANT_STRING, NOT_IMPORTANT_STRING, NOT_IMPORTANT_STRING, listOf(FyberOfferType(1, NOT_IMPORTANT_STRING)), FyberThumbnail(NOT_IMPORTANT_STRING, NOT_IMPORTANT_STRING), 1, FyberAmount(1, NOT_IMPORTANT_STRING))
    val MOCK_RESPONSE = FyberResponse(NOT_IMPORTANT_STRING, NOT_IMPORTANT_STRING, NOT_IMPORTANT_INT, NOT_IMPORTANT_INT, MOCK_INFORMATION, listOf(MOCK_OFFERS))

    val mockRequest = mock<Request>()
    val mockApi = mock<FyberApi>()
    val testThread = Schedulers.test()
    val fixture = Puller(mockApi, testThread, testThread)

    @Test
    fun testRequestSuccess() {
        val mockObservable = Observable.create(object : Observable.OnSubscribe<FyberResponse> {
            override fun call(subscriber: Subscriber<in FyberResponse>) {
                subscriber.onNext(MOCK_RESPONSE)
                subscriber.onCompleted()
            }
        })
        // Capture request and force http client to success.
        whenever(mockApi.getOffers(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(mockObservable)
        // Then perform a request.
        fixture.request(mockRequest)
        // Let rx run actions.
        testThread.triggerActions()
        // Check that request behaves as expected.
        verify(mockRequest).onSuccess(any<FyberResponse>())
    }

    @Test
    fun testRequestFailure() {
        val mockObservable = Observable.create(object : Observable.OnSubscribe<FyberResponse> {
            override fun call(subscriber: Subscriber<in FyberResponse>) {
                subscriber.onError(Throwable())
            }
        })
        // Capture request and force http client to failure.
        whenever(mockApi.getOffers(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(mockObservable)
        // Do stuff.
        fixture.request(mockRequest)
        // Let rx run actions.
        testThread.triggerActions()
        // Check that request behaves as expected.
        verify(mockRequest).onFailure(any<Throwable>())
    }

    @Test
    fun testRequestFailureWithException() {
        // Capture request and force http client to failure because of an IO exception.
        whenever(mockApi.getOffers(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenThrow(RuntimeException())
        // Do stuff.
        fixture.request(mockRequest)
        // Check that request behaves as expected.
        verify(mockRequest).onFailure(any<Exception>())
    }
}
