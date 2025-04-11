package com.secure.commons.helpers

import android.content.Context
import android.content.Intent
import android.util.Log
import com.secure.commons.activities.CrashActivity
import java.io.IOException
import kotlin.system.exitProcess

/** Inspired by https://stackoverflow.com/questions/3643395/how-to-get-android-crash-logs/22527672#22527672
 * https://medium.com/@boyrazgiray/how-to-catch-handle-exceptions-globally-in-android-d3447064df14
 * with some modifications...
 */
class CrashHandler(context: Context) : Thread.UncaughtExceptionHandler {
    //private val defaultUEH: Thread.UncaughtExceptionHandler? = Thread.getDefaultUncaughtExceptionHandler()
    //private var app: Activity? = null
    private var crashContext: Context = context

    /*init {
        this.app = app
    } */

    override fun uncaughtException(thread: Thread, exception: Throwable) {
        var arr = exception.stackTrace
        var report = "====== APPLICATION CRASHED ======\n\n"
        report += exception.toString() + "\n\n"
        report += "==============================\n"
        report += "--------- Stack trace ---------\n\n"
        for (i in arr.indices) {
            report += "  " + arr[i].toString() + "\n"
        }
        report += "-------------------------------\n\n"

        // If the exception was thrown in a background thread inside
        // AsyncTask, then the actual exception can be found with getCause
        report += "--------- Cause ---------\n\n"
        val cause = exception.cause
        if (cause != null) {
            report += cause.toString() + "\n\n"
            arr = cause.stackTrace
            for (i in arr.indices) {
                report += "  " + arr[i].toString() + "\n"
            }
        }
        report += "-------------------------------\n\n"
        try {
            Log.d("Error", report)
            /*val trace = crashContext.openFileOutput(
                "stack.trace",
                Context.MODE_WORLD_READABLE
            )
            trace.write(report.toByteArray())
            trace.close()*/
        } catch (ioe: IOException) {
            ioe.printStackTrace()
        }

        val intent = Intent(crashContext, CrashActivity::class.java).apply {
            putExtra("report", report)
        }

        crashContext.startActivity(intent)
        android.os.Process.killProcess(android.os.Process.myPid())
        exitProcess(2)
    }
}
