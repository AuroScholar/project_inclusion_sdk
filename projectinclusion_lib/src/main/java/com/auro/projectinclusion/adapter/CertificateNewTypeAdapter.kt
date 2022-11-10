package com.pi.projectinclusion.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.pi.projectinclusion.R
import com.pi.projectinclusion.model.CertificateTypeModel

/**
 * * API TO BE IMPLEMENTED:
 * 1. Certificate Type Api
 */
private const val TAG = "CertificateNewType"

class CertificateNewTypeAdapter(
    private var mContext: Context,
    private var mCertTypeList: ArrayList<CertificateTypeModel>,// CHANGE MODEL TO CERTIFICATE TYPE MODEL //TODO Certificate Type Api
    private var certType: String?,
    private var myOnNewCertTypeClickListener: OnNewCertTypeClickListener,
) : RecyclerView.Adapter<CertificateNewTypeAdapter.MainFeedViewHolder>() {

    class MainFeedViewHolder(
        itemView: View,
        onNewCertTypeClickListener: OnNewCertTypeClickListener
    ) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private var mOnNewCertTypeClickListener: OnNewCertTypeClickListener
        var acbCertType: AppCompatButton
        override fun onClick(v: View) {
            mOnNewCertTypeClickListener.onItemViewClick(adapterPosition)
        }

        init {
            mOnNewCertTypeClickListener = onNewCertTypeClickListener
            acbCertType = itemView.findViewById(R.id.acbCertType)
            acbCertType.setOnClickListener(this)
        }
    }


    interface OnNewCertTypeClickListener {
        fun onItemViewClick(pos: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainFeedViewHolder {
        val view =
            LayoutInflater.from(mContext).inflate(R.layout.item_certificate_type, parent, false)
        val lp = view.layoutParams
        lp.width = parent.measuredWidth / 3
        return MainFeedViewHolder(view, myOnNewCertTypeClickListener)
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
                val certTypModel: CertificateTypeModel = payload[0] as CertificateTypeModel
                certType = certTypModel.name
                Log.d(TAG, "onBindViewHolder: name: " + certTypModel.name)
                if (certType == mCertTypeList[position].name) {
                    holder.acbCertType.setBackgroundResource(R.drawable.rounded_tab_background_selected)
                    holder.acbCertType.setTextColor(Color.WHITE)
                } else {
                    holder.acbCertType.setBackgroundResource(R.drawable.rounded_tab_background)
                    holder.acbCertType.setTextColor(Color.BLACK)
                }
            }
        } catch (e: Exception) {
        }

    }

    override fun getItemCount(): Int {
        return mCertTypeList.size
    }

    override fun onBindViewHolder(holder: MainFeedViewHolder, position: Int) {
        try {
            val certTypModel: CertificateTypeModel = mCertTypeList[position]

            Log.d(TAG, "onBindViewHolder: name2 : " + certTypModel.name)
            if (certType == certTypModel.id.toString()) {
                holder.acbCertType.setBackgroundResource(R.drawable.rounded_tab_background_selected)
                holder.acbCertType.setTextColor(Color.WHITE)
            } else {
                holder.acbCertType.setBackgroundResource(R.drawable.rounded_tab_background)
                holder.acbCertType.setTextColor(Color.BLACK)
            }
            holder.acbCertType.text = certTypModel.name
        } catch (e: Exception) {
        }
    }
}