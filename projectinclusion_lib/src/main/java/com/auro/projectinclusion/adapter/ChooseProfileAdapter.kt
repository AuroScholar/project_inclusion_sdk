package com.pi.projectinclusion.adapter

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.pi.projectinclusion.ClickProfileId
import com.pi.projectinclusion.model.ProfileModel
import com.pi.projectinclusion.R


class ChooseProfileAdapter(
    private var mContext: Context,
    private var mProfileList: ArrayList<ProfileModel>,
    private var profileId: ClickProfileId
) : RecyclerView.Adapter<ChooseProfileAdapter.ChooseProfileHolder>() {
    private var selectedPosition = -1

    class ChooseProfileHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profileName: TextView = itemView.findViewById(R.id.profile_name)
        var profileLayout: ConstraintLayout = itemView.findViewById(R.id.profile_layout)
        var vBarrier: View = itemView.findViewById(R.id.vBarrier)
        var profileImage: ImageView = itemView.findViewById(R.id.profile_img)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseProfileHolder {
        val view =
            LayoutInflater.from(mContext).inflate(R.layout.profile_recycler_item, parent, false)
        return ChooseProfileHolder(view)
    }

    override fun onBindViewHolder(holder: ChooseProfileHolder, position: Int) {
        val profileModel = mProfileList[position]

        holder.vBarrier.visibility = View.GONE

        holder.profileName.text = profileModel.name
        if (profileModel.id?.toInt() == 3) {
            holder.profileImage.setImageResource(R.drawable.ic_teacher)

            holder.profileImage.setPadding(0,0,0,0)
        }else if(profileModel.id?.toInt() == 4){
            holder.profileImage.setImageResource(R.drawable.ic_parents_2)
            holder.profileImage.setPadding(0,-10,0 ,0)
        }
        holder.profileLayout.setOnClickListener {
            notifyItemChanged(selectedPosition)
            selectedPosition = holder.adapterPosition
            profileId.clickToGetProfileId(profileModel.id!!)
            holder.profileLayout.setBackgroundResource(R.drawable.select_circle_background)
        }
        if (selectedPosition == holder.adapterPosition) {
            holder.profileLayout.setBackgroundResource(R.drawable.background_blue)
        } else {
            holder.profileLayout.setBackgroundResource(R.drawable.language_background)

            Log.d("called", "onBindViewHolder: ")
        }
        holder.profileImage.loadImageFromUrl(profileModel.icon!!)


    }

    private fun ImageView.loadImageFromUrl(imageUrl: String) {
        val imageLoader = ImageLoader.Builder(this.context)
            .componentRegistry {
                add(SvgDecoder(this@loadImageFromUrl.context))
            }
            .build()

        val imageRequest = ImageRequest.Builder(this.context)
            .crossfade(true)
            .crossfade(300)
            .data(imageUrl)
            .target(
                onStart = {
                    //set up an image loader or whatever you need
                },
                onSuccess = { result ->
                    val bitmap = (result as BitmapDrawable).bitmap
                    this.setImageBitmap(bitmap)
                    //dismiss the loader if any
                },
                onError = {
                    /**
                     * TODO: set an error drawable
                     */
                }
            )
            .build()

        imageLoader.enqueue(imageRequest)
    }


    override fun getItemCount(): Int {
        return mProfileList.size
    }
}