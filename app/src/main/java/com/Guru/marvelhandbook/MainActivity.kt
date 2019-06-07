package com.Guru.marvelhandbook

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.tasks.Task
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateManager
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.play.core.install.InstallStateUpdatedListener
import android.support.v4.view.accessibility.AccessibilityEventCompat.setAction
import android.support.design.widget.Snackbar
import android.view.View
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import android.widget.Toast
import android.support.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener


class MainActivity : AppCompatActivity() {

    val MY_REQUEST_CODE = 111;
    lateinit var appUpdateManager:AppUpdateManager
    var i =0;
    var mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
    var isFlexible = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFirebaseRemoteConfig.setDefaults(R.xml.te);
       hello.text = BuildConfig.VERSION_NAME
        update.setOnClickListener{
            checkForUpdate()
        }
        fetchRemoteConfig()
    }

    override fun onResume() {
        super.onResume()
        hello.text = BuildConfig.VERSION_NAME
    }
fun checkForUpdate()
{
    // Creates instance of the manager.
    appUpdateManager = AppUpdateManagerFactory.create(baseContext)

// Returns an intent object that you use to check for an update.
     val appUpdateInfoTask = appUpdateManager.appUpdateInfo

// Checks that the platform will allow the specified type of update.
    appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
        if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            // For a flexible update, use AppUpdateType.FLEXIBLE
            && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
        ) {
            // Request the update.
            appUpdateManager.startUpdateFlowForResult(
                // Pass the intent that is returned by 'getAppUpdateInfo()'.
                appUpdateInfo,
                // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                AppUpdateType.FLEXIBLE,
                // The current activity making the update request.
                this,
                // Include a request code to later monitor this update request.
                MY_REQUEST_CODE);
        }
    }
}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (MY_REQUEST_CODE != RESULT_OK) {
            // If the update is cancelled or fails,
            // you can request to start the update again.
            checkForUpdate()
                // Or 'AppUpdateType.FLEXIBLE' for flexible updates.


        } else {

            popupSnackbarForCompleteUpdate()
        }
    }

        fun popupSnackbarForCompleteUpdate() {
            val snackbar = Snackbar.make(
                findViewById<View>(R.id.root),
                "An update has just been downloaded.",
                Snackbar.LENGTH_INDEFINITE
            )
            snackbar.setAction("RESTART") { view -> appUpdateManager.completeUpdate() }
            snackbar.setActionTextColor(
                resources.getColor(R.color.material_grey_100)
            )
            snackbar.show()
        }

    fun fetchRemoteConfig()
    {

        mFirebaseRemoteConfig.fetch(1000)
            .addOnCompleteListener(this, OnCompleteListener<Void> { task ->
                if (task.isSuccessful) {
                    isFlexible = mFirebaseRemoteConfig.getBoolean("isFlexible");
                    Toast.makeText(
                        this@MainActivity, "Fetch Succeeded",
                        Toast.LENGTH_SHORT
                    ).show()

                    // After config data is successfully fetched, it must be activated before newly fetched
                    // values are returned.
                    mFirebaseRemoteConfig.activateFetched()
                } else {
                    Toast.makeText(
                        this@MainActivity, "Fetch Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })


    }


}
