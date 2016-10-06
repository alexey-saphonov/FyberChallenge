package com.alexeysafonov.fyberchallenge.model

/**
 * This data structure represents a single offer from Fyber payload.
 */
data class FyberOffer(val title: String,
                      val offer_id: Int,
                      val teaser: String,
                      val required_actions: String,
                      val link: String,
                      val offer_types: List<FyberOfferType>,
                      val thumbnail: FyberThumbnail,
                      val payout: Int,
                      val time_to_payout: FyberAmount)