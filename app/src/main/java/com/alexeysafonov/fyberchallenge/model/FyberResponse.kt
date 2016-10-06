package com.alexeysafonov.fyberchallenge.model

/**
 * Data class for a Fyber response.
 */
data class FyberResponse(val code: String,
                         val message: String,
                         val count: Int,
                         val pages: Int,
                         val information: FyberInformation,
                         val offers: List<FyberOffer>)

