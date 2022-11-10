package com.pi.projectinclusion.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pi.projectinclusion.R


/**
 * * API TO BE IMPLEMENTED:
 * 1. List of certificates
 */
private const val TAG = "ScreeningQuesAdapter"

class ScreeningQuesAdapter(
    private var mContext: Context,
    private var alsQuesList: ArrayList<ArrayList<String>>,
    private var mAnsAdapter: ScreeningAnsAdapter?,
    private var MyOnSubmitClickListener: OnSubmitClickListener,
    private var mOnAnsSelectedListener: ScreeningAnsAdapter.OnAnsSelectedListener,
) : RecyclerView.Adapter<ScreeningQuesAdapter.MainFeedViewHolder>() {

    class MainFeedViewHolder(itemView: View, onSubmitClickListener: OnSubmitClickListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var mOnSubmitClickListener: OnSubmitClickListener
        var tvQues: TextView
        var rvAnswers: RecyclerView

        init {
            mOnSubmitClickListener = onSubmitClickListener
            tvQues = itemView.findViewById(R.id.tvQues)
            rvAnswers = itemView.findViewById(R.id.rvAnswers)
            //acbSubmit.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            mOnSubmitClickListener.onItemClick(adapterPosition)
        }
    }


    interface OnSubmitClickListener {
        fun onItemClick(
            pos: Int
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainFeedViewHolder {
        val view =
            LayoutInflater.from(mContext).inflate(R.layout.item_screening_ques, parent, false)
        return MainFeedViewHolder(view, MyOnSubmitClickListener)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MainFeedViewHolder, position: Int) {
        val quesModel = alsQuesList.get(position)
        holder.tvQues.text = "Ques " + (position + 1) + ") " + quesModel[0]
        val ques = quesModel[0]
        quesModel.removeFirst()

        Log.d(TAG, "onBindViewHolder: quesModel: $quesModel")

        holder.rvAnswers.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        mAnsAdapter = ScreeningAnsAdapter(mContext, ques, quesModel, mOnAnsSelectedListener)
        holder.rvAnswers.adapter = mAnsAdapter //TODO VALIDATE FOR NULL VALUE OF RESPONSE


    }

    override fun getItemCount(): Int {
        return alsQuesList.size
    }
}