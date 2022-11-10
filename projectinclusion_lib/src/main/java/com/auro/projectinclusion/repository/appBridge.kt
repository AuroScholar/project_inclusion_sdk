package com.pi.projectinclusion.repository

import android.annotation.SuppressLint
import android.content.*
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.webkit.JavascriptInterface
import com.pi.projectinclusion.*
import com.pi.projectinclusion.model.appBridge
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject

private const val TAG = "appBridge"

class appBridge(context: Context, factory: SQLiteDatabase.CursorFactory?) : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION)
{
    // below is the method for creating a database by a sqlite query
    override fun onCreate(db: SQLiteDatabase) {
        // below is a sqlite query, where column names
        // along with their data types is given
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + cid + " TEXT, " +
                suspend_data + " TEXT," +
                lesson_state + " TEXT," +
                sync + " INTEGER" + ")")

        // we are calling sqlite
        // method for executing our query
        db.execSQL(query)
        //        DownloadManager dm = (DownloadManager)getActivity().getSystemService(getActivity().DOWNLOAD_SERVICE);
       /* dManager = this@appBridge.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        dManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        LocalBroadcastManager.getInstance(this@appBridge).registerReceiver(downloadReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))*/

        //registerReceiver(downloadReceiver,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        // this method is to check if table already exists
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // This method is for adding data in our database
    fun saveLmsData(json: String, context: Context) {

        // below we are creating
        // a content values variable
        val values = ContentValues()

        val jsonObject = JSONObject(json)
        val appBridge = appBridge(0, "", "", "")

        appBridge.cid = jsonObject.getString("cid")
        appBridge.suspend_data = jsonObject.getString("suspend_data")
        appBridge.lesson_status = jsonObject.getString("lesson_status")
        appBridge.sync = jsonObject.getInt("sync")


        val db = this.writableDatabase
        values.put(cid, appBridge.cid)
        values.put(suspend_data, appBridge.suspend_data)
        values.put(lesson_state, appBridge.lesson_status)
        values.put("sync", appBridge.sync)

        var courseIdList: ArrayList<String>? =
            getList(context, context.getString(R.string.key_courseId_list))
        Log.d(TAG, "saveLmsData: courseIdList: " + courseIdList)
        var isInsert: Boolean
        if (courseIdList != null && courseIdList.contains(appBridge.cid)) {
            isInsert = false
        } else if (courseIdList != null && !courseIdList.contains(appBridge.cid)) {
            isInsert = true
            courseIdList.add(appBridge.cid)
            saveList(context, context.getString(R.string.key_courseId_list), courseIdList)
        } else {
            isInsert = true
            courseIdList = ArrayList()
            courseIdList.add(appBridge.cid)
            saveList(context, context.getString(R.string.key_courseId_list), courseIdList)
        }

        Log.d(TAG, "saveLmsData: courseId: " + isInsert)
        var QUERY = ""
        if (isInsert) {
            QUERY =
                ("INSERT INTO " + TABLE_NAME + " (" + cid + ", " + suspend_data + ", " + lesson_state + ", " + sync + ") Values (" + "\"" + appBridge.cid + "\"" + ", " + "\"" + appBridge.suspend_data + "\"" + ", " + "\"" + appBridge.lesson_status + "\"" + ", " + appBridge.sync + ") ")
            // saveData(context, context.getString(R.string.key_is_courseId_exists), "true")
        } else {
            QUERY =
                ("UPDATE " + TABLE_NAME + " SET "
                        + suspend_data + " = " + "\"" + appBridge.suspend_data + "\"" + ", "
                        + lesson_state + " = " + "\"" + appBridge.lesson_status + "\"" + ", "
                        + sync + " = " + appBridge.sync
                        + " WHERE " + cid + " = " + "\"" + appBridge.cid + "\"")
        }
        Log.d(TAG, "saveLmsData: query: " + QUERY)
        // all values are inserted into database
        db.execSQL(QUERY)
        // we are inserting our values
        // in the form of key-value pair

        // here we are creating a
        // writable variable of
        // our database as we want to
        // insert value in our database
        // at last we are
        // closing our database
        db.close()
    }

    // below method is to get
    // all data from our database
    @SuppressLint("Range")
    fun getLMSData(selectedCousreId: String): String {

        // here we are creating a readable
        // variable of our database
        // as we want to read value from it
        val db = this.readableDatabase

        // below code returns a cursor to
        // read data from the database
        val appBridge = appBridge(0, "", "", "")
        db.rawQuery(
            "SELECT * FROM " + TABLE_NAME + " WHERE " + cid + " = " + selectedCousreId,
            null
        )
            .use {
                if (it.moveToFirst()) {
                    appBridge.cid = it.getString(it.getColumnIndex(cid))
                    appBridge.suspend_data = it.getString(it.getColumnIndex(suspend_data))
                    appBridge.lesson_status = it.getString(it.getColumnIndex(lesson_state))
                    appBridge.sync = it.getInt(it.getColumnIndex("sync"))
                }
            }
        val gson = Gson()
        val jsonString = gson.toJson(appBridge)
        var request = JSONObject()
        try {
            request = JSONObject(jsonString)
        } catch (e: JSONException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        Log.d(TAG, "getLMSData: request: " + request.toString())
        return request.toString()
    }

    @SuppressLint("Range")
    fun getLMSData(): ArrayList<appBridge> {

        // here we are creating a readable
        // variable of our database
        // as we want to read value from it
        val db = this.readableDatabase

        // below code returns a cursor to
        // read data from the database
        val allResults: ArrayList<appBridge> = ArrayList()
        db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + sync + " = " + "0", null)
            .use {
                if (it.moveToFirst()) {
                    do {
                        val appBridge = appBridge(0, "", "", "")
                        appBridge.cid = it.getString(it.getColumnIndex(cid))
                        appBridge.suspend_data = it.getString(it.getColumnIndex(suspend_data))
                        appBridge.lesson_status = it.getString(it.getColumnIndex(lesson_state))
                        appBridge.sync = it.getInt(it.getColumnIndex(sync))

                        allResults.add(appBridge)
                    } while (it.moveToNext())
                }
            }
        return allResults
    }


    /*
    @JavascriptInterface
    fun downloadCertificate(str: String?, name: String?) {
        if (name != null) {
            dwn_certificate(str, name)
        }
    }

    fun dwn_certificate(url: String?, name: String) {
        val request1 = DownloadManager.Request(Uri.parse(url))
        // Log.d("download",url);
        dwnType = 0
        request1.setDescription("Downloading Certificate") //appears the same in Notification bar while downloading
        request1.setTitle("Certificate $name")
        request1.setVisibleInDownloadsUi(true)
        request1.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request1.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name)
        downloadID = dManager!!.enqueue(request1)
    }*/


    companion object {
        // here we have defined variables for our database

        // below is variable for database name
        private val DATABASE_NAME = "LmsAppBridge"

        // below is the variable for database version
        private val DATABASE_VERSION = 1

        // below is the variable for table name
        val TABLE_NAME = "appBridge"

        // below is the variable for courseID column
        val cid = "cid"

        // below is the variable for suspendDate column
        val suspend_data = "suspend_data"

        // below is the variable for lessonState column
        val lesson_state = "lesson_status"

        // below is the variable for sync column
        val sync = "sync"
    }
}