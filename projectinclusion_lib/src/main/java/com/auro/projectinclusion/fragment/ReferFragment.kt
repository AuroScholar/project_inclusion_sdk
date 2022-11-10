package com.pi.projectinclusion.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.pi.projectinclusion.*

private const val TAG = "ReferFragment"

class ReferFragment : Fragment() {
    private lateinit var cvRefer: CardView
    private lateinit var v: View
    private lateinit var mContext: Context
    private lateinit var hmLangValue: HashMap<String, String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        Log.d(TAG, "onCreateView: ")
        mContext = requireActivity()
        v = inflater.inflate(R.layout.fragment_refer, container, false)
        cvRefer = v.findViewById(R.id.cvRefer)
        setLanguage()

        cvRefer.setOnClickListener {
            emailIntent()
        }


        return v
    }

    override fun onResume() {
        super.onResume()
        setLanguage()
    }

    private fun setLanguage() {

        if (getData(mContext, mContext.getString(R.string.key_lang_id)) == "1") {
            setAppLocale(mContext, "en")
        } else {
            setAppLocale(mContext, "hi")
        }
        hmLangValue = getHashMap(mContext, mContext.getString(R.string.key_lang_list))

        v.findViewById<TextView>(R.id.imageView2).text = hmLangValue["txt_refer"]
        v.findViewById<TextView>(R.id.refer_text).text = hmLangValue["txt_refer_message"]
        v.findViewById<TextView>(R.id.crt_acnt_txt).text = hmLangValue["txt_refer"]
    }

    private fun emailIntent() {
        val intentShare = Intent(Intent.ACTION_SEND)
        intentShare.type = "text/plain"
        intentShare.putExtra(Intent.EXTRA_SUBJECT, mContext.getString(R.string.refer_heading_txt))
        intentShare.putExtra(
            Intent.EXTRA_TEXT,
            mContext.getString(R.string.refer_content_txt)
        )

        startActivity(
            Intent.createChooser(
                intentShare,
                mContext.getString(R.string.refer_heading_txt)
            )
        )
    }


}