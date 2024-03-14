package com.currenthealth.raptorhealthmonitor

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.Intent.CATEGORY_BROWSABLE
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_REQUIRE_NON_BROWSER
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.currenthealth.raptorhealthmonitor.util.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG(), "onResume: ")
        launchApps()
        Log.i(TAG(), "onResume: FINISH")
        finish()
    }


    private fun launchApps(){
        launchVivaLinkApp()
        runBlocking {
            Log.i(TAG(), "onCreate: runBlobking")
            launch {
                Log.i(TAG(), "onCreate: launch")
                delay(5000)
                launchRaptorApp()
            }
        }
    }


    private fun launchVivaLinkApp() {
        val raptorIntent = Intent(ACTION_VIEW, Uri.parse("cardiacapp://")).apply {
            addCategory(CATEGORY_BROWSABLE)
            flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_REQUIRE_NON_BROWSER
        }
        startActivity(raptorIntent)
        Log.i(TAG(), "launchVivaLinkApp: COMPLETED")
    }

    private fun launchRaptorApp() {
        val raptorIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse("com.currenthealth.velocigoose://")).apply {

                addCategory(CATEGORY_BROWSABLE)
                flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_REQUIRE_NON_BROWSER
            }

        startActivity(raptorIntent)
        Log.i(TAG(), "launchRaptorApp: COMPLETED")
    }
}