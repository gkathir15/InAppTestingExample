package com.Guru.marvelhandbook

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateManager
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.material.snackbar.Snackbar
import android.view.View
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest


class MainActivity : AppCompatActivity() {

    val MY_REQUEST_CODE = 111;
    lateinit var appUpdateManager:AppUpdateManager
    var i =0;
    var mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
    var isFlexible = true
    private lateinit var splitInstallManager: SplitInstallManager
    private lateinit var camRequest: SplitInstallRequest
    private lateinit var liteRequest: SplitInstallRequest
  //  val CamActivity ="com.guru.camera.CamActivity"
    val liteActivity ="com.guru.lite.LiteActivity"
    val RECORD_REQUEST_CODE =500
    val camModule = "camera"
    val liteModule = "lite"





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFirebaseRemoteConfig.setDefaults(R.xml.te);
       hello.text = BuildConfig.VERSION_NAME
        appUpdateManager = AppUpdateManagerFactory.create(baseContext)

//       camRequest =
//        SplitInstallRequest
//            .newBuilder()
//            // You can download multiple on demand modules per
//            // camRequest by invoking the following method for each
//            // module you want to install.
//            .addModule("camera")
//            .build()
         liteRequest = SplitInstallRequest
            .newBuilder()
            // You can download multiple on demand modules per
            // camRequest by invoking the following method for each
            // module you want to install.
            .addModule(liteModule)
            .build()

        splitInstallManager = SplitInstallManagerFactory.create(this)

        update.setOnClickListener{ checkForUpdateFLEXIBLE() }
        update2.setOnClickListener {   checkForUpdateIMMEDIATE() }
      //  camera.setOnClickListener { openCamera() }
        camera.visibility = View.INVISIBLE
        assetAct.setOnClickListener { openlite() }
        fetchRemoteConfig()
      //  setupPermissions()

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)


    }


    override fun onResume() {
        super.onResume()
        hello.text = BuildConfig.VERSION_NAME

    }

    private fun setupPermissions() {
        val permission = checkSelfPermission(this,
            Manifest.permission.CAMERA)
        val perm1 = checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED&&perm1!=PackageManager.PERMISSION_GRANTED) {
           // Log.i(TAG, "Permission to record denied")
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE),
            RECORD_REQUEST_CODE)
    }

//    private fun openCamera()
//    {
//
//
//        if(!splitInstallManager.installedModules.contains(camModule))
//        {
//            splitInstallManager.startInstall(camRequest)
//                .addOnCompleteListener { popupSnackbarForCompleteUpdate("module installed","ok") }
//                .addOnFailureListener { popupSnackbarForCompleteUpdate("module install failed ","ok") }
//                .addOnSuccessListener { popupSnackbarForCompleteUpdate("module installed successfully","ok")
//                    invokeModule(CamActivity)}
//
//
//        }
//        else
//        {
//            invokeModule(CamActivity)
//        }
//
//    }

    private fun openlite()
    {


        if(!splitInstallManager.installedModules.contains(liteModule))
        {
            splitInstallManager.startInstall(liteRequest)
                .addOnCompleteListener { popupSnackbarForCompleteUpdate("module installed","ok") }
                .addOnFailureListener { popupSnackbarForCompleteUpdate("module install failed ","ok") }
                .addOnSuccessListener { popupSnackbarForCompleteUpdate("module installed successfully","ok")
                    invokeModule(liteActivity)}


        }
        else
        {
            invokeModule(liteActivity)
        }

    }

    private fun invokeModule(classname:String)
    {
        Intent().setClassName(BuildConfig.APPLICATION_ID, classname)
            .also {
                startActivity(it)
            }
    }
private fun checkForUpdateFLEXIBLE()
{
    // Creates instance of the manager.


// Returns an intent object that you use to check for an update.
     val appUpdateInfoTask = appUpdateManager.appUpdateInfo

// Checks that the platform will allow the specified type of update.
    appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
        if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            // For a flexible update, use AppUpdateType.FLEXIBLE
            && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
        ) {
            // Request the update.
            appUpdateManager.startUpdateFlowForResult(
                // Pass the intent that is returned by 'getAppUpdateInfo()'.
                appUpdateInfo,
                // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                AppUpdateType.FLEXIBLE,
                // The current activity making the update camRequest.
                this,
                // Include a camRequest code to later monitor this update camRequest.
                MY_REQUEST_CODE)
        }
    }
}

    private fun checkForUpdateIMMEDIATE()
    {
        // Creates instance of the manager.


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
                    AppUpdateType.IMMEDIATE,
                    // The current activity making the update camRequest.
                    this,
                    // Include a camRequest code to later monitor this update camRequest.
                    MY_REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) {
            // If the update is cancelled or fails,
            // you can camRequest to start the update again.
            //checkForUpdateFLEXIBLE()
                // Or 'AppUpdateType.FLEXIBLE' for flexible updates.


        } else {

            popupSnackbarForCompleteUpdate()
        }
    }

        private fun popupSnackbarForCompleteUpdate() {
            val snackbar = Snackbar.make(
                findViewById<View>(R.id.root),
                "An update has just been downloaded.",
                Snackbar.LENGTH_INDEFINITE
            )
            snackbar.setAction("RESTART") { appUpdateManager.completeUpdate() }
            snackbar.setActionTextColor(
                resources.getColor(R.color.material_grey_100,this.theme)

            )
            snackbar.show()
        }

    private fun popupSnackbarForCompleteUpdate(txt:String, action:String) {
        val snackbar = Snackbar.make(
            findViewById<View>(R.id.root),
            txt,
            Snackbar.LENGTH_INDEFINITE
        )
        snackbar.setAction(action) { snackbar.dismiss() }
        snackbar.setActionTextColor(
            resources.getColor(R.color.material_grey_100,this.theme)

        )
        snackbar.show()
    }

    private fun fetchRemoteConfig()
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
