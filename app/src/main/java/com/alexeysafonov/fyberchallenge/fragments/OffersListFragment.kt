package com.alexeysafonov.fyberchallenge.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.alexeysafonov.fyberchallenge.R
import com.alexeysafonov.fyberchallenge.model.FyberOffer
import com.bumptech.glide.Glide
import java.util.*

/**
 * This fragment represents a list of offers.
 */
class OffersListFragment: Fragment() {

    var offersList: RecyclerView? = null
    val adapter = OffersAdapter()

    var offers: List<FyberOffer> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_offers_list, container, false)

        offersList = view.findViewById(R.id.offers_list) as RecyclerView
        offersList?.setHasFixedSize(true)
        offersList?.layoutManager = LinearLayoutManager(context)
        if (offers.isNotEmpty()) {
            adapter.offers = offers
        }
        offersList?.adapter = adapter
        return view
    }

    override fun onResume() {
        super.onResume()
    }

    class OffersAdapter: RecyclerView.Adapter<ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent?.context).inflate(R.layout.offer_row, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            holder?.bind(offers[position])
        }

        var offers: List<FyberOffer> = ArrayList()

        override fun getItemCount(): Int {
            return offers.size
        }

    }

    class ViewHolder: RecyclerView.ViewHolder {

        val title: TextView
        val teaser: TextView
        val thumbnail: ImageView
        val payout: TextView

        constructor(view: View): super(view) {
            title = view.findViewById(R.id.offer_title) as TextView
            teaser = view.findViewById(R.id.offer_teaser) as TextView
            thumbnail = view.findViewById(R.id.offer_thumbnail) as ImageView
            payout = view.findViewById(R.id.offer_payout) as TextView
        }

        fun bind(fyberOffer: FyberOffer) {
            title.text = fyberOffer.title
            teaser.text = fyberOffer.teaser
            Glide.with(thumbnail.context.applicationContext)
                    .load(fyberOffer.thumbnail.hires)
                    .placeholder(R.drawable.ic_placeholder)
                    .animate(android.R.anim.fade_in)
                    .into(thumbnail)
            payout.text = fyberOffer.payout.toString()
        }
    }
}