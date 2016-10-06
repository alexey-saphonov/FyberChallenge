package com.alexeysafonov.fyberchallenge.model

/**
 * This data structure represents information from fyber payload.
 */
data class FyberInformation(val app_name: String,
                            val appid: Int,
                            val virtual_currency: String,
                            val country: String,
                            val language: String,
                            val support_url: String)