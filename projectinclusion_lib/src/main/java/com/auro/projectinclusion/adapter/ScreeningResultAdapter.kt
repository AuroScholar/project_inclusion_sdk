package com.pi.projectinclusion.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
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
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.pi.projectinclusion.R


/**
 * * API TO BE IMPLEMENTED:
 * 1. List of certificates
 */
private const val TAG = "ScreeningAnsAdapter"
private const val PERMISSION_REQUEST_CODE = 100
private lateinit var imgPath: String

class ScreeningResultAdapter(
    private var mContext: Context,
    private var heading1: String,
    private var heading2: String,
    private var noResult: String,
    private var alsSectionList: ArrayList<String>,
    private var hmMessage: HashMap<String, String>,
    private var hmAnsCount: LinkedHashMap<String, String>,
    private var hmIntervention: HashMap<String, String>,
    private var MyOnAnsSelectedListener: OnAnsSelectedListener
) : RecyclerView.Adapter<ScreeningResultAdapter.MainFeedViewHolder>() {

    class MainFeedViewHolder(itemView: View, onAnsSelectedListener: OnAnsSelectedListener) :
        RecyclerView.ViewHolder(itemView) {
        var mOnAnsSelectedListener: OnAnsSelectedListener
        var tvTitle: TextView
        var tvMessage: TextView
        var tvAnsCount: TextView
        var tvIntervention: TextView

        init {
            mOnAnsSelectedListener = onAnsSelectedListener
            tvTitle = itemView.findViewById(R.id.tvSectionResult)
            tvAnsCount = itemView.findViewById(R.id.tvAnsCount)
            tvMessage = itemView.findViewById(R.id.tvSRMessage)
            tvIntervention = itemView.findViewById(R.id.tvSRIntervention)
        }


    }


    interface OnAnsSelectedListener {
        fun onItemClick(
            pos: Int,
            answer: String,
            ques: String,
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainFeedViewHolder {
        val view =
            LayoutInflater.from(mContext).inflate(R.layout.item_screening_result, parent, false)
        return MainFeedViewHolder(view, MyOnAnsSelectedListener)
    }


    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MainFeedViewHolder, position: Int) {
        val section = alsSectionList.get(position)
        Log.d(TAG, "onBindViewHolder: answer: " + section)
        holder.tvTitle.text = section
        holder.tvAnsCount.text = hmAnsCount.get(section)
        if (!hmMessage.containsKey(section)) {
            holder.tvMessage.text = noResult
            holder.tvIntervention.visibility = View.GONE
        } else {

            holder.tvIntervention.visibility = View.VISIBLE

            val spanHeading = setSpanStyle(
                heading1,
                mContext.resources.getDimensionPixelSize(R.dimen.heading_text_size_result),
                Typeface.BOLD
            )
            val spanContent = setSpanStyle(
                hmMessage.get(
                    section
                ).toString(),
                mContext.resources.getDimensionPixelSize(R.dimen.content_text_size_result),
                Typeface.NORMAL
            )
            holder.tvMessage.text = TextUtils.concat(spanHeading, " ", spanContent)

            val spanHeading1 = setSpanStyle(
                heading2,
                mContext.resources.getDimensionPixelSize(R.dimen.heading_text_size_result),
                Typeface.BOLD
            )
            val spanContent1 = setSpanStyle(
                hmIntervention.get(
                    section
                ).toString(),
                mContext.resources.getDimensionPixelSize(R.dimen.content_text_size_result),
                Typeface.NORMAL
            )
            holder.tvIntervention.text = TextUtils.concat(spanHeading1, " ", spanContent1)
            /* val conc =
                 Html.fromHtml(
                     "<B>" + mContext.getString(R.string.conclusion_txt) + "</B>" + ": " + "<small>" + hmMessage.get(
                         section
                     ) + "</small>",
                     Html.FROM_HTML_MODE_LEGACY
                 )*/

//            val interven =
//                Html.fromHtml(
//                    "<B>" + mContext.getString(R.string.intervention_txt) + "</B>" + ": " + hmIntervention.get(
//                        section
//                    ),
//                    Html.FROM_HTML_MODE_LEGACY
//                )
//            // holder.tvMessage.text = conc
//            holder.tvIntervention.text = interven
        }


    }

    private fun setSpanStyle(
        text: String,
        size: Int,
        typeFace: Int/*, color: Int*/
    ): SpannableString {
        val span1 = SpannableString(text)
        span1.setSpan(
            AbsoluteSizeSpan(
                size
            ), 0, text.length, SPAN_INCLUSIVE_INCLUSIVE
        )
        span1.setSpan(StyleSpan(typeFace), 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        //  span1.setSpan( ForegroundColorSpan(color), 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return span1
    }

    override fun getItemCount(): Int {
        return alsSectionList.size
    }
}