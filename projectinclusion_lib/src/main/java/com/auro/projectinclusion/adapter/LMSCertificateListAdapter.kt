package com.pi.projectinclusion.adapter

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.pi.projectinclusion.R
import com.pi.projectinclusion.model.CertificateDetailModel
import com.pi.projectinclusion.repository.AuroRepository
import com.pi.projectinclusion.toast
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


/**
 * * API TO BE IMPLEMENTED:
 * 1. List of certificates
 */
private const val TAG = "CertificateListAdapter"

class LMSCertificateListAdapter(
    private var mContext: Context,
    private var mCertificatesList: ArrayList<CertificateDetailModel>,
    private var hmLangValue: HashMap<String, String>,
    // private var MyOnCertificateClickListener: OnCertClickListener,
    private var certType: String?,
    private var activity: Activity,
) : RecyclerView.Adapter<LMSCertificateListAdapter.MainFeedViewHolder>() {

    class MainFeedViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var tvScore: TextView
        var tvTitle: TextView
        var ivCert: ImageView
        private var ivDummy: ImageView
        var acbView: AppCompatButton
        var acbDownload: AppCompatButton

        init {
            ivCert = itemView.findViewById(R.id.ivCert)
            ivDummy = itemView.findViewById(R.id.ivDummy)
            tvTitle = itemView.findViewById(R.id.tvCertTitle)
            tvScore = itemView.findViewById(R.id.tvScore)
            acbView = itemView.findViewById(R.id.acbView)
            acbDownload = itemView.findViewById(R.id.acbDownload)
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainFeedViewHolder {
        val view =
            LayoutInflater.from(mContext).inflate(R.layout.item_certificates_list, parent, false)
        return MainFeedViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainFeedViewHolder, position: Int) {

        holder.acbView.text = hmLangValue["btn_view"]
        holder.acbDownload.text = hmLangValue["btn_download"]
        val certModel = mCertificatesList[position]
        when (certType) {
            "2" -> {
                // PUT EXCELLENCE MODEL HERE  // TODO List of certificates

                holder.tvScore.visibility = View.VISIBLE
                holder.tvScore.text = certModel.score.toString()
            }
            else -> {
                // PUT MODULE CERT MODEL HERE // TODO List of certificates
                holder.tvScore.visibility = View.GONE
            }

        }
        holder.tvTitle.text = certModel.name
        Log.d(TAG, "onBindViewHolder: imgUrl: " + certModel.certificatePath)
        if (certModel.certificatePath == "") {
            holder.ivCert.setImageResource(R.drawable.ic_baseline_search)
        } else {
            AuroRepository().loadImageWithGlide(
                mContext,
                certModel.certificatePath.toString(),
                holder.ivCert,
                null
            )
        }

        holder.acbDownload.setOnClickListener {
            if (askPermission()) {

                if (certModel.certificatePath != "") {

                    Log.d(TAG, "onBindViewHolder: imgUrl 2: " + certModel.certificatePath)

                    try {

                        AuroRepository().loadBitmapWithGlide(
                            mContext,
                            certModel.certificatePath.toString(),
                            certModel.name.toString(),
                            null, ::mSaveMediaToStorage
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    mContext.toast(hmLangValue["img_unavailable_txt"].toString())
                }


            }

        }

        val alertDialog: Dialog?

        alertDialog =
            showNewNameDialog(certModel.certificatePath.toString(), certModel.name.toString())


        holder.acbView.setOnClickListener {
            alertDialog.show()
        }


        // NEEDS TO BE ADJUSTED TODO List of certificates


    }


    // Function to save image on the device.
// Refer: https://www.geeksforgeeks.org/circular-crop-an-image-and-save-it-to-the-file-in-android/
    private fun mSaveMediaToStorage(bitmap: Bitmap?, nameFile: String) {
        //Log.d(TAG, "mSaveMediaToStorage: nameFile: $nameFile")
        //Log.d(TAG, "mSaveMediaToStorage: bitmap: $bitmap")
        CoroutineScope(Dispatchers.IO).launch {
            val filename = "${nameFile}.png"
            var fos: OutputStream? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                activity.contentResolver?.also { resolver ->
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    }
                    val imageUri: Uri? =
                        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    withContext(Dispatchers.IO) {
                        fos = imageUri?.let { resolver.openOutputStream(it) }
                    }
                }
            } else {
                val imagesDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val image = File(imagesDir, filename)
                withContext(Dispatchers.Main) {
                    fos = FileOutputStream(image)
                }
            }
            fos?.use {
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it)
                withContext(Dispatchers.Main) {
                    mContext.toast(hmLangValue["msg_certificate_saved"].toString())
                }
            }
        }
    }

    private fun showNewNameDialog(imgPath: String, filename: String): Dialog {
        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.dialog_view_certificate)
        val ivCertificate =
            dialog.findViewById<SubsamplingScaleImageView>(R.id.ivViewCertificate) // CHANGE AS PER REQUIREMENT TODO List of certificates

        val ivClose =
            dialog.findViewById<ImageView>(R.id.ivCloseDialog) // CHANGE AS PER REQUIREMENT TODO List of certificates

        AuroRepository().loadBitmapWithGlide(
            mContext,
            imgPath,
            filename,
            ivCertificate,
            null
        )
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        ivClose.setOnClickListener { dialog.cancel() }
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(width - 100, height - 100)

        return dialog

    }


    private fun askPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                mContext,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) !== PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                mContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            mContext.toast(hmLangValue["perm_gallery"].toString())
            requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                100
            )
            return false
        } else {
            return true
        }
    }

    override fun getItemCount(): Int {
        return mCertificatesList.size
    }
}