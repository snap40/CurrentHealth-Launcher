package com.currenthealth.raptorhealthmonitor

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.Intent.CATEGORY_BROWSABLE
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_REQUIRE_NON_BROWSER
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.currenthealth.raptorhealthmonitor.util.TAG
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class MainActivity : AppCompatActivity() {

    private var appLaunchDelay = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpFireBaseRemoteConfig()
        launchRaptorApp()
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG(), "onResume: ")
        launchApps()
        Log.i(TAG(), "onResume: FINISH")
        finish()
    }

    private fun setUpFireBaseRemoteConfig() {
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        remoteConfig.fetchAndActivate().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                runOnUiThread {
                    showToast("Remote config fetched successfully!")
                }
            }
        }

        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                Log.d(TAG(), "Updated keys: " + configUpdate.updatedKeys);

                if (configUpdate.updatedKeys.contains("appLaunchDelay")) {
                    remoteConfig.activate().addOnCompleteListener {
                        appLaunchDelay = remoteConfig.getLong("appLaunchDelay")
                        showToast("App Launch delay updated to $appLaunchDelay")
                    }
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Log.w(TAG(), "Config update error with code: " + error.code, error)
            }
        })

    }

    private fun launchApps() {
        appLaunchDelay = Firebase.remoteConfig.getLong("appLaunchDelay")
        showToast("App Launch delay is $appLaunchDelay")
        launchVivaLinkApp()
        Thread() { //Effort to make it smooth for the PFA app to launch -> Too much for the hybrid app maybe?
            //Observed PFA showing black screen and crash
            runBlocking(Dispatchers.IO) {
                Log.i(TAG(), "onCreate: runBlobking")
                delay(appLaunchDelay)
                launch(Dispatchers.Main) {
                    Log.i(TAG(), "onCreate: launch")
//                    delay(appLaunchDelay)
                    launchRaptorApp()
                }
            }
        }.start()

    }


    private fun launchVivaLinkApp() {
        val raptorIntent = Intent(ACTION_VIEW, Uri.parse("cardiacapp://")).apply {
            addCategory(CATEGORY_BROWSABLE)
            flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_REQUIRE_NON_BROWSER
        }
        try {
            startActivity(raptorIntent)
        } catch (exception: Exception) {
            Log.e(TAG(), "launchVivaLinkApp: FAILED TO LAUNCH MVM", exception)
        }

        showToast("MVM Launched")
        Log.i(TAG(), "launchVivaLinkApp: COMPLETED")
    }

    private fun launchRaptorApp() {
        val raptorIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse("com.currenthealth.velocigoose://")).apply {
                addCategory(CATEGORY_BROWSABLE)
                flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_REQUIRE_NON_BROWSER
            }
        try {
            startActivity(raptorIntent)
        } catch (exception: Exception) {
            Log.e(TAG(), "launchVivaLinkApp: FAILED TO LAUNCH RAPTOR", exception)
        }

        showToast("PFA Launched")
        Log.i(TAG(), "launchRaptorApp: COMPLETED")
    }

    private fun showToast(message: String) {
        val showToastStatus = Firebase.remoteConfig.getBoolean("showToastMessages")
        if (showToastStatus) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

}