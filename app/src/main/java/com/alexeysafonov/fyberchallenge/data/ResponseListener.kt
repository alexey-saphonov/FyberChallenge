package com.alexeysafonov.fyberchallenge.data

import com.alexeysafonov.fyberchallenge.model.FyberResponse

/**
 * Response callback interface.
 */
interface ResponseListener {
    fun onSuccess(response: FyberResponse)
    fun onFailure(exception: Throwable)
}