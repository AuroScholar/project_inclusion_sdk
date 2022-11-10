package com.pi.projectinclusion.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.media.Image
import android.os.Build
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.pi.projectinclusion.R
import com.pi.projectinclusion.model.WebinarModel
import com.pi.projectinclusion.repository.AuroRepository
import com.instabug.library.ui.custom.CircularImageView
import java.time.LocalDate
import java.time.format.DateTimeFormatter


/**
 * * API TO BE IMPLEMENTED:
 * 1. List of certificates
 */
private const val TAG = "WebinarListAdapter"

class WebinarListAdapter(
    private var mContext: Context,
    private var alsWebinars: ArrayList<WebinarModel>,
    private var MyOnJoinClickListener: OnJoinClickListener
) : RecyclerView.Adapter<WebinarListAdapter.MainFeedViewHolder>() {

    class MainFeedViewHolder(itemView: View, onJoinClickListener: OnJoinClickListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var mOnJoinClickListener: OnJoinClickListener
        var tvSubjectName: TextView
        var tvDate: TextView
        var tvHostedBy: TextView
        var tvWebinarName: TextView
        var tvSlotTitle: TextView
        var tvSlotNumber: TextView
        var ivWebinarImage: ImageView
        var civHostImage: CircularImageView

        init {
            mOnJoinClickListener = onJoinClickListener
            tvSubjectName = itemView.findViewById(R.id.tvSubjectName)
            tvDate = itemView.findViewById(R.id.tvDate)
            tvHostedBy = itemView.findViewById(R.id.tvHostedBy)
            tvWebinarName = itemView.findViewById(R.id.tvWebinarName)
            tvSlotTitle = itemView.findViewById(R.id.tvSlotTitle)
            tvSlotNumber = itemView.findViewById(R.id.tvSlotNumber)
            ivWebinarImage = itemView.findViewById(R.id.ivWebinarImage)
            civHostImage = itemView.findViewById(R.id.civHostImage)
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            mOnJoinClickListener.onJoinClick(adapterPosition)
        }


    }


    interface OnJoinClickListener {
        fun onJoinClick(
            position: Int
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainFeedViewHolder {
        val view =
            LayoutInflater.from(mContext).inflate(R.layout.item_webinar, parent, false)
        return MainFeedViewHolder(view, MyOnJoinClickListener)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MainFeedViewHolder, position: Int) {
        val webinarModel = alsWebinars[position]


        holder.tvDate.text = buildDate(webinarModel.webinarDate)
        holder.tvWebinarName.text = webinarModel.name
        if (webinarModel.iconURL != null) {
            AuroRepository().loadImageWithGlide(
                mContext,
                webinarModel.iconURL.toString(),
                holder.ivWebinarImage,
                null
            )
        }
        if (webinarModel.hostProfilePicUrl != null) {
            AuroRepository().loadImageWithGlide(
                mContext,
                webinarModel.hostProfilePicUrl.toString(),
                holder.civHostImage,
                null
            )
        }
        holder.tvSlotNumber.text = webinarModel.totalslot.toString()
        holder.tvHostedBy.text = "Hosted By " + webinarModel.hostName
        holder.tvSubjectName.text = webinarModel.subject
    }

    override fun getItemCount(): Int {
        return alsWebinars.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun buildDate(date: String): String {
        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val formattedDate = LocalDate.parse(date, dateFormat)
        val result =
            formattedDate.dayOfWeek.toString() + ", " + formattedDate.dayOfMonth + "/" + formattedDate.monthValue + "/" + formattedDate.year
        return result

    }
}