package com.lollipop.applist.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.util.Date
import java.util.concurrent.Executors

sealed class AppInfoDatabase {

    protected var dbDelegate: DatabaseDelegate? = null
        private set

    protected val executor by lazy {
        Executors.newSingleThreadExecutor()
    }

    protected val uiThread by lazy {
        Handler(Looper.getMainLooper())
    }

    fun init(context: Context) {
        Log.e("AppInfoDatabase", "init: $context")
        if (dbDelegate == null) {
            dbDelegate = DatabaseDelegate(context)
        }
    }

    class Writer : AppInfoDatabase() {
        fun onWindowChanged(pkg: String, cls: String, flag: Boolean) {
            Log.e("AppInfoDatabase", "onWindowChanged: $pkg, $cls")
            if (pkg.isEmpty() || cls.isEmpty()) {
                return
            }
            val info = AppActivityInfo(pkg, cls, System.currentTimeMillis(), flag)
            executor.execute {
                try {
                    dbDelegate?.insert(info)
                } catch (e: Throwable) {
                    Log.e("AppInfoDatabase", "onWindowChanged", e)
                }
            }
        }
    }

    class Reader : AppInfoDatabase() {
        fun query(
            appPkg: String,
            pageIndex: Int,
            pageSize: Int = 40,
            callback: (List<AppActivityInfo>) -> Unit
        ) {
            executor.submit {
                val result = dbDelegate?.query(appPkg, pageIndex, pageSize) ?: emptyList()
                uiThread.post {
                    callback(result)
                }
            }
        }

        fun queryAll(
            appPkg: String,
            callback: (List<AppActivityInfo>) -> Unit
        ) {
            executor.submit {
                val result = dbDelegate?.queryAll(appPkg) ?: emptyList()
                uiThread.post {
                    callback(result)
                }
            }
        }
    }

    protected class DatabaseDelegate(
        context: Context
    ) : SQLiteOpenHelper(context, "app_info", null, 1) {

        override fun onCreate(db: SQLiteDatabase?) {
            db?.execSQL(ActivityInfo.CREATE_TABLE)
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        }

        fun insert(appActivityInfo: AppActivityInfo) {
            try {
                ActivityInfo.insert(writableDatabase, appActivityInfo)
            } catch (e: Throwable) {
                Log.e("AppInfoDatabase", "insert", e)
            }
        }

        fun query(appPkg: String, pageIndex: Int, pageSize: Int): List<AppActivityInfo> {
            try {
                return ActivityInfo.query(readableDatabase, appPkg, pageIndex, pageSize)
            } catch (e: Throwable) {
                Log.e("AppInfoDatabase", "query", e)
                return emptyList()
            }
        }

        fun queryAll(appPkg: String): List<AppActivityInfo> {
            try {
                return ActivityInfo.queryAll(readableDatabase, appPkg)
            } catch (e: Throwable) {
                Log.e("AppInfoDatabase", "query", e)
                return emptyList()
            }
        }

        object ActivityInfo {

            const val TABLE = "activity_info"

            const val COLUMNS_PKG_NAME = "package_name"
            const val COLUMNS_ACTIVITY_NAME = "activity_name"
            const val COLUMNS_TIME = "last_time"
            const val COLUMNS_FLAG = "flag"

            const val CREATE_TABLE = "CREATE TABLE IF NOT EXISTS $TABLE (" +
                    "$COLUMNS_PKG_NAME TEXT, " +
                    "$COLUMNS_ACTIVITY_NAME TEXT, " +
                    "$COLUMNS_TIME INTEGER, " +
                    "$COLUMNS_FLAG INTEGER " +
                    " )"

            private val allColumns by lazy {
                arrayOf(
                    COLUMNS_PKG_NAME,
                    COLUMNS_ACTIVITY_NAME,
                    COLUMNS_TIME,
                    COLUMNS_FLAG
                )
            }

            private const val ORDER_BY_DESC = "$COLUMNS_TIME DESC"

            fun insert(db: SQLiteDatabase, appActivityInfo: AppActivityInfo) {
                val values = ContentValues().apply {
                    put(COLUMNS_PKG_NAME, appActivityInfo.packageName)
                    put(COLUMNS_ACTIVITY_NAME, appActivityInfo.activityName)
                    put(COLUMNS_TIME, appActivityInfo.time)
                    put(
                        COLUMNS_FLAG,
                        if (appActivityInfo.flag) {
                            1
                        } else {
                            0
                        }
                    )
                }
                db.insert(TABLE, "", values)
            }

            private fun where(appPkg: String): String? {
                return if (appPkg.isNotEmpty()) {
                    "$COLUMNS_PKG_NAME = ?"
                } else {
                    null
                }
            }

            private fun whereArgs(appPkg: String): Array<String>? {
                return if (appPkg.isNotEmpty()) {
                    arrayOf(appPkg)
                } else {
                    null
                }
            }

            fun query(
                db: SQLiteDatabase,
                appPkg: String,
                pageIndex: Int,
                pageSize: Int = 40
            ): List<AppActivityInfo> {
                val offset = pageIndex * pageSize
                val limit = "$pageSize OFFSET $offset"
                val cursor = db.query(
                    TABLE,
                    allColumns,
                    where(appPkg),
                    whereArgs(appPkg),
                    null,
                    null,
                    ORDER_BY_DESC,
                    limit
                )
                return selectAppInfo(cursor)
            }

            fun queryAll(
                db: SQLiteDatabase,
                appPkg: String,
            ): List<AppActivityInfo> {
                val cursor = db.query(
                    TABLE,
                    allColumns,
                    where(appPkg),
                    whereArgs(appPkg),
                    null,
                    null,
                    ORDER_BY_DESC,
                    null
                )
                return selectAppInfo(cursor)
            }

            private fun selectAppInfo(cursor: Cursor): List<AppActivityInfo> {
                val appActivityInfoList = mutableListOf<AppActivityInfo>()
                while (cursor.moveToNext()) {
                    try {
                        val packageName =
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMNS_PKG_NAME))
                        val activityName =
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMNS_ACTIVITY_NAME))
                        val time = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMNS_TIME))
                        val flag = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMNS_FLAG))
                        val appActivityInfo = AppActivityInfo(
                            packageName = packageName,
                            activityName = activityName,
                            time = time,
                            flag = flag > 0
                        )
                        appActivityInfoList.add(appActivityInfo)
                    } catch (e: Throwable) {
                        Log.e("AppInfoDatabase", "query", e)
                    }
                }
                cursor.close()
                return appActivityInfoList
            }

        }

    }

    class AppActivityInfo(
        val packageName: String,
        val activityName: String,
        val time: Long,
        val flag: Boolean
    ) {

        val timeDate: Date by lazy {
            Date(time)
        }

    }

}