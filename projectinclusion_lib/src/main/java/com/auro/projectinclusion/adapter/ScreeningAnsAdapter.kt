package com.pi.projectinclusion.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.pi.projectinclusion.R


/**
 * * API TO BE IMPLEMENTED:
 * 1. List of certificates
 */
private const val TAG = "ScreeningAnsAdapter"

class ScreeningAnsAdapter(
    private var mContext: Context,
    private var ques: String,
    private var alsAnswerList: ArrayList<String>,
    private var MyOnAnsSelectedListener: OnAnsSelectedListener
) : RecyclerView.Adapter<ScreeningAnsAdapter.MainFeedViewHolder>() {

    class MainFeedViewHolder(itemView: View, onAnsSelectedListener: OnAnsSelectedListener) :
        RecyclerView.ViewHolder(itemView) {
        private var mOnAnsSelectedListener: OnAnsSelectedListener
        private var iv: ImageView
        var tv: TextView
        var cl: ConstraintLayout

        init {
            mOnAnsSelectedListener = onAnsSelectedListener
            iv = itemView.findViewById(R.id.ivRB)
            tv = itemView.findViewById(R.id.tvRB)
            cl = itemView.findViewById(R.id.clRB)
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
            LayoutInflater.from(mContext).inflate(R.layout.item_screening_ans, parent, false)
        return MainFeedViewHolder(view, MyOnAnsSelectedListener)
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
                val answer = alsAnswerList[position]
                val selectedPosition = payload[0] as Int
                holder.tv.text = answer

                if (position == selectedPosition) {
                  //  holder.iv.setImageResource(R.drawable.rounded_radio_selected)

                    holder.cl.setBackgroundResource(R.drawable.rounded_radio_btn_selected)
                    holder.tv.setTextColor(Color.WHITE)
                } else {
                   // holder.iv.setImageResource(R.drawable.rounded_radio_unselected)

                    holder.cl.setBackgroundResource(R.drawable.rounded_radio_btn_unselected)
                    holder.tv.setTextColor(Color.BLACK)
                }

                holder.itemView.setOnClickListener {
                    notifyItemRangeChanged(0, alsAnswerList.size, position)
                    MyOnAnsSelectedListener.onItemClick(
                        holder.adapterPosition,
                        answer,
                        ques
                    )
                }
            }
        } catch (e: Exception) {
            throw e
        }

    }

    override fun onBindViewHolder(holder: MainFeedViewHolder, position: Int) {
        val answer = alsAnswerList[position]
        Log.d(TAG, "onBindViewHolder: answer: $answer")
        holder.tv.text = answer



        holder.itemView.setOnClickListener {
            notifyItemRangeChanged(0, alsAnswerList.size, position)
            MyOnAnsSelectedListener.onItemClick(
                holder.adapterPosition,
                answer,
                ques
            )
        }


    }

    override fun getItemCount(): Int {
        return alsAnswerList.size
    }
}