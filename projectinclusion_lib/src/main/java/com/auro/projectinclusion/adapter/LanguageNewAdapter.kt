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
private const val TAG = "LanguageNewAdapter"

class LanguageNewAdapter(
    private var mContext: Context,
    private var mLangList: ArrayList<LanguageModel>,
    private var mLangId: Int,
    private var MyOnLanguageClickListener: OnNewLangClickListener
) : RecyclerView.Adapter<LanguageNewAdapter.MainFeedViewHolder>() {

    class MainFeedViewHolder(itemView: View, onNewLangClickListener: OnNewLangClickListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private var mOnNewLangClickListener: OnNewLangClickListener
        var langName: TextView
        var langLayout: ConstraintLayout
        override fun onClick(v: View) {
            mOnNewLangClickListener.onItemClick(adapterPosition, langLayout, langName)
        }

        init {
            mOnNewLangClickListener = onNewLangClickListener
            langName = itemView.findViewById(R.id.language_txt)
            langLayout = itemView.findViewById(R.id.lang_layout)
            itemView.setOnClickListener(this)
        }
    }


    interface OnNewLangClickListener {
        fun onItemClick(pos: Int, langLayout: ConstraintLayout, langName: TextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainFeedViewHolder {
        val view =
            LayoutInflater.from(mContext).inflate(R.layout.language_recycler_item, parent, false)
        return MainFeedViewHolder(view, MyOnLanguageClickListener)
    }

    override fun onBindViewHolder(
        holder: MainFeedViewHolder, position: Int,
        payload: MutableList<Any>
    ) {
        try {
            Log.d(TAG, "onBindViewHolder: payload: $payload")
            if (payload.isEmpty()) {
                super.onBindViewHolder(holder, position, payload)
            } else {
                val langModel = mLangList[position]

                holder.langName.text = langModel.translatedName
                val selectedPosition = payload[0] as Int
                if (selectedPosition == position) {
                    holder.langLayout.setBackgroundResource(R.drawable.background_blue)
                    holder.langName.setTextColor(Color.parseColor("#FFFFFFFF"))
                } else {
                    holder.langLayout.setBackgroundResource(R.drawable.language_background)
                    holder.langName.setTextColor(Color.parseColor("#6E6E6E"))
                    Log.d("called", "onBindViewHolder: ")
                }
            }
        } catch (e: Exception) {
        }

    }

    override fun onBindViewHolder(holder: MainFeedViewHolder, position: Int) {
        val langModel = mLangList[position]


        holder.langName.text = langModel.translatedName

        if ((mLangId - 1) == holder.adapterPosition) {

            holder.langLayout.setBackgroundResource(R.drawable.background_blue)
            holder.langName.setTextColor(Color.parseColor("#FFFFFFFF"))
        }


    }


    override fun getItemCount(): Int {
        return mLangList.size
    }
}