package com.pi.projectinclusion.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.pi.projectinclusion.R
import com.pi.projectinclusion.model.LanguageModel


/**
 * * API TO BE IMPLEMENTED:
 * 1. List of certificates
 */
private const val TAG = "LanguageWebinarAdapter"

class LanguageWebinarAdapter(
    private var mContext: Context,
    private var mLangList: ArrayList<LanguageModel>,
    private var mLangId: Int,
    private var MyOnLanguageClickListener: OnNewLangClickListener
) : RecyclerView.Adapter<LanguageWebinarAdapter.MainFeedViewHolder>() {

    class MainFeedViewHolder(itemView: View, onNewLangClickListener: OnNewLangClickListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private var mOnNewLangClickListener: OnNewLangClickListener
        var tvSelected: TextView
        var tvUnselected: TextView
        override fun onClick(v: View) {
            mOnNewLangClickListener.onItemClick(adapterPosition, tvSelected, tvUnselected)
        }

        init {
            mOnNewLangClickListener = onNewLangClickListener
            tvSelected = itemView.findViewById(R.id.tvLangWebSelected)
            tvUnselected = itemView.findViewById(R.id.tvLangWeb)
            itemView.setOnClickListener(this)
        }
    }


    interface OnNewLangClickListener {
        fun onItemClick(pos: Int, tvSelected: TextView, tvUnselected: TextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainFeedViewHolder {
        val view =
            LayoutInflater.from(mContext).inflate(R.layout.item_webinar_lang, parent, false)
        return MainFeedViewHolder(view, MyOnLanguageClickListener)
    }

    override fun onBindViewHolder(
        holder: MainFeedViewHolder, position: Int,
        payload: MutableList<Any>
    ) {
        Log.d(TAG, "onBindViewHolder: payload: $payload")
        if (payload.isEmpty()) {
            super.onBindViewHolder(holder, position, payload)
        } else {
            val langModel = mLangList[position]

            holder.tvSelected.text = langModel.translatedName
            holder.tvUnselected.text = langModel.translatedName
            val selectedPosition = payload[0] as Int
            if (selectedPosition == position) {
                holder.tvSelected.visibility = View.VISIBLE
                holder.tvUnselected.visibility = View.GONE
            } else {
                holder.tvSelected.visibility = View.GONE
                holder.tvUnselected.visibility = View.VISIBLE
            }
        }

    }

    override fun onBindViewHolder(holder: MainFeedViewHolder, position: Int) {
        val langModel = mLangList[position]

        holder.tvSelected.text = langModel.translatedName
        holder.tvUnselected.text = langModel.translatedName
        if ((mLangId - 1) == holder.adapterPosition) {
            holder.tvSelected.visibility = View.VISIBLE
            holder.tvUnselected.visibility = View.GONE
        } else {

            holder.tvUnselected.visibility = View.VISIBLE
            holder.tvSelected.visibility = View.GONE
        }


    }


    override fun getItemCount(): Int {
        return mLangList.size
    }
}