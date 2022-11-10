package com.pi.projectinclusion.adapter

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.pi.projectinclusion.R
import com.pi.projectinclusion.model.SlotIdModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter


/**
 * * API TO BE IMPLEMENTED:
 * 1. List of certificates
 */
private const val TAG = "SlotListAdapter"

class SlotListAdapter(
    private var mContext: Context,
    private var alsSlots: ArrayList<SlotIdModel>,
    private var MyOnSlotSelected: OnSlotSelected
) : RecyclerView.Adapter<SlotListAdapter.MainFeedViewHolder>() {
    var lastChecked = -1


    class MainFeedViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener
    {
        var tvSlot: TextView
        var radioButn: AppCompatCheckBox

        var cstLyt : ConstraintLayout
        /*var tvSubjectName: TextView
        var tvDate: TextView
        var tvHostedBy: TextView
        var tvWebinarName: TextView
        var tvSlotTitle: TextView
        var tvSlotNumber: TextView
        var ivWebinarImage: ImageView
        var civHostImage: CircularImageView*/

        init {
            tvSlot = itemView.findViewById(R.id.tvSlotName)
            radioButn = itemView.findViewById(R.id.ivRadioBtn)
            cstLyt = itemView.findViewById(R.id.clSLot)
            /*tvSubjectName = itemView.findViewById(R.id.tvSubjectName)
            tvDate = itemView.findViewById(R.id.tvDate)
            tvHostedBy = itemView.findViewById(R.id.tvHostedBy)
            tvWebinarName = itemView.findViewById(R.id.tvWebinarName)
            tvSlotTitle = itemView.findViewById(R.id.tvSlotTitle)
            tvSlotNumber = itemView.findViewById(R.id.tvSlotNumber)
            ivWebinarImage = itemView.findViewById(R.id.ivWebinarImage)
            civHostImage = itemView.findViewById(R.id.civHostImage)*/
            //itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            Log.e("click","-----------------------Click registered----------------------------------")
            //mOnSlotSelected.onSelected(adapterPosition)
        }


    }


    interface OnSlotSelected {
        fun onSelected(
            mStartTime:String,
            mEndTime : String,
            slotID: Int
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainFeedViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_slots, parent, false)
        return MainFeedViewHolder(view)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MainFeedViewHolder, position: Int) {
        val slotIdModel = alsSlots[position]
        Log.e("onBindViewHolder","......-------------------------------------------"+slotIdModel.slotId!!+ "---------------")
        Log.e("Slot","Start time >>" + slotIdModel.startTime.toString() + " --  --  " + "End Time >>" + slotIdModel.endTime
        )
        val startTime = slotIdModel.startTime.toString()
        val endTime = slotIdModel.endTime.toString()
        holder.tvSlot.text = "$startTime -- $endTime"

        if (lastChecked == position)
        {
            Log.e("Test 1","If......")
            holder.radioButn.isChecked=true
            //MyOnSlotSelected.onSelected(startTime, endTime)
        }
        else{
            Log.e("Test 1","else......")
            holder.radioButn.isChecked = false
            //MyOnSlotSelected.onSelected("", "")
        }

        holder.cstLyt.setOnClickListener {
            lastChecked = holder.adapterPosition
            MyOnSlotSelected.onSelected(startTime, endTime, slotIdModel.slotId!!)
            //MyOnSlotSelected.onSelected(startTime, endTime)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return alsSlots.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun buildDate(date: String): String {
        Log.e("test",">>>>--------------  "+date)
        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val formattedDate = LocalDate.parse(date, dateFormat)
        val result =
            formattedDate.dayOfWeek.toString() + ", " + formattedDate.dayOfMonth + "/" + formattedDate.monthValue + "/" + formattedDate.year
        return result

    }
}