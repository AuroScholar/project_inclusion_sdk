package com.pi.projectinclusion

import android.app.DownloadManager
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.res.Configuration
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.LocaleList
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.pi.projectinclusion.activity.LanguageActivity
import com.pi.projectinclusion.activity.internetError
import com.pi.projectinclusion.model.CertificateModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.NullPointerException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

var dManager: DownloadManager? = null
var downloadID: Long = 0

fun Context.toast(msg: String) {
    if (isNetwork(this))
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    else
        Toast.makeText(this, internetError, Toast.LENGTH_SHORT).show()
}

fun isNetwork(context: Context): Boolean {

    // register activity with the connectivity manager service
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // if the android version is equal to M
    // or greater we need to use the
    // NetworkCapabilities to check what type of
    // network has the internet connection
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

        // Returns a Network object corresponding to
        // the currently active default data network.
        val network = connectivityManager.activeNetwork ?: return false

        // Representation of the capabilities of an active network.
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            // Indicates this network uses a Wi-Fi transport,
            // or WiFi has network connectivity
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

            // Indicates this network uses a Cellular transport. or
            // Cellular has network connectivity
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

            // else return false
            else -> false
        }
    } else {
        // if the android version is below M
        @Suppress("DEPRECATION") val networkInfo =
            connectivityManager.activeNetworkInfo ?: return false
        @Suppress("DEPRECATION")
        return networkInfo.isConnected
    }


}

fun saveList(context: Context, listName: String?, list: ArrayList<String>) {
    val gson = Gson()
    val json = gson.toJson(list)
    val pref = context.getSharedPreferences("PREFRENCE_SAVE", MODE_PRIVATE)
    val editor = pref.edit()//converting list to Json
    editor.putString(listName, json)
    editor.apply()
}

//getting the list from shared preference
fun getList(context: Context, listName: String?): ArrayList<String>? {
    val gson = Gson()
    val pref = context.getSharedPreferences("PREFRENCE_SAVE", MODE_PRIVATE)
    val json = pref.getString(listName, null)
    if (json != null) {
        val type =
            object : TypeToken<ArrayList<String>>() {}.type//converting the json to list
        return gson.fromJson(json, type)//returning the list
    } else {
        return null
    }
}

fun saveHashMap(context: Context, listName: String?, list: HashMap<String, String>) {
    val gson = Gson()
    val json = gson.toJson(list)
    val pref = context.getSharedPreferences("PREFRENCE_SAVE", MODE_PRIVATE)
    val editor = pref.edit()//converting list to Json
    editor.putString(listName, json)
    editor.apply()
}

//getting the list from shared preference
@Throws(NullPointerException::class)
fun getHashMap(context: Context, listName: String?): HashMap<String, String> {
    val gson = Gson()
    val pref = context.getSharedPreferences("PREFRENCE_SAVE", MODE_PRIVATE)
    val json = pref.getString(listName, null) ?: throw NullPointerException()
    val type = object : TypeToken<HashMap<String, String>>() {}.type//converting the json to list
    return gson.fromJson(json, type)//returning the list
}

fun saveData(context: Context, key: String, value: String) {
    val pref = context.getSharedPreferences("PREFRENCE_SAVE", MODE_PRIVATE)
    val editor = pref.edit()
    editor.putString(key, value)
    editor.apply()
}

fun clearData(context: Context, key: String) {
    val pref = context.getSharedPreferences("PREFRENCE_SAVE", MODE_PRIVATE)
    val editor = pref.edit()
    editor.remove(key)
    editor.apply()
}

fun clearAllData(context: Context) {
    val pref = context.getSharedPreferences("PREFRENCE_SAVE", 0)
    val editor = pref.edit()
    editor.clear()
    editor.apply()
}

fun getData(context: Context, key: String): String {
    val pref = context.getSharedPreferences("PREFRENCE_SAVE", MODE_PRIVATE)
    return pref.getString(key, "null").toString()
}

fun setAppLocale(context: Context, language: String) {
//    val locale = Locale(language)
//    Locale.setDefault(locale)
//    val config = context.resources.configuration
//    config.setLocale(locale)
//    context.createConfigurationContext(config)
//
//    context.resources.updateConfiguration(config, context.resources.displayMetrics)
    val activityRes: Resources = context.resources
    val activityConf = activityRes.configuration
    val newLocale = Locale(language)
    activityConf.setLocale(newLocale)
    activityRes.updateConfiguration(activityConf, activityRes.displayMetrics)

    val applicationRes: Resources = context.applicationContext.resources
    val applicationConf = applicationRes.configuration
    applicationConf.setLocale(newLocale)
    applicationRes.updateConfiguration(
        applicationConf,
        applicationRes.displayMetrics
    )
}

fun setLocale(context: Context, language: String?): Context? {
    val locale = Locale(language)
    Locale.setDefault(locale)
    val resources: Resources = context.resources
    val configuration = Configuration(resources.getConfiguration())
    configuration.setLayoutDirection(locale)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        configuration.setLocale(locale)
        val localeList = LocaleList(locale)
        LocaleList.setDefault(localeList)
        configuration.setLocales(localeList)
    } else {
        configuration.locale = locale
        configuration.setLocale(locale)
    }
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
        context.createConfigurationContext(configuration)
    } else {
        resources.updateConfiguration(configuration, resources.getDisplayMetrics())
        context
    }
}

fun checkIfUsernameValid(user: String): Boolean{
    val pattern = Regex("^[A-Za-z0-9_@]+\$")
    return pattern.containsMatchIn(user)
}

fun dwn_certificate(url: String?, name: String) {
    val request1 = DownloadManager.Request(Uri.parse(url))
    // Log.d("download",url);
    //dwnType = 0
    request1.setDescription("Downloading Certificate") //appears the same in Notification bar while downloading
    request1.setTitle("Certificate $name")
    request1.setVisibleInDownloadsUi(true)
    request1.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
    request1.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name)
    downloadID = dManager!!.enqueue(request1)
}

@RequiresApi(Build.VERSION_CODES.O)
fun convertDateInS():String{

    val current = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    val formatted = current.format(formatter)
    Log.e("convertDateInS",formatted.toString())
    return formatted.toString()
}
