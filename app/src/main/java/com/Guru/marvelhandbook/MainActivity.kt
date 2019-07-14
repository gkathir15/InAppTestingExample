package com.Guru.marvelhandbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
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
import androidx.core.view.accessibility.AccessibilityEventCompat.setAction
import com.google.android.material.snackbar.Snackbar
import android.view.View
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat.startActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.play.core.splitinstall.*
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import java.util.*


class MainActivity : AppCompatActivity() {

    val MY_REQUEST_CODE = 111;
    lateinit var appUpdateManager:AppUpdateManager
    var i =0;
    var mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
    var isFlexible = true
    var isReinstall = false
    var dynamicMofule = "musicModule"

    private lateinit var manager : SplitInstallManager
    private lateinit var listener : SplitInstallStateUpdatedListener

    private  val packageNames =  "com.guru.musicmodule"
    private  val administrativoClassname =  "com.guru.musicmodule.MusicActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFirebaseRemoteConfig.setDefaults(R.xml.te);
        manager =  SplitInstallManagerFactory .create ( this )

       hello.text = BuildConfig.VERSION_NAME
        update.setOnClickListener{
            checkForUpdate()
            //startActivity(Intent(this@MainActivity,DummyActivity::class.java))

        }
        fetchRemoteConfig()
        dynamic.setOnClickListener { loadAndLaunchModule(dynamicMofule) }
        uninstall.setOnClickListener { unInstallModule() }
        // listener to know the status of the module

        listener = SplitInstallStateUpdatedListener { state ->

            when (state.status ()) {
                SplitInstallSessionStatus.DOWNLOADING  -> {
                    displayLoadingState (state, " Downloading the module $ {state.moduleNames () [ 0 ]} " )
                }
                        SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION  -> {
                    startIntentSender (state.resolutionIntent () ?. intentSender, null , 0 , 0 , 0 )
                }
                        SplitInstallSessionStatus.INSTALLED -> {
                    launchActivity (administrativoClassname)
                            if(isReinstall)
                            {
                                isReinstall = false
                                popupSnackbarForReinstallModule()
                            }
                }

                        SplitInstallSessionStatus . INSTALLING -> displayLoadingState (state, " Installing the Module $ {state.moduleNames () [ 0 ]} " )

                    SplitInstallSessionStatus . FAILED -> {
                        hello.text =  " Error: $ {state.errorCode ()} for module $ {state.moduleNames ()} "
                    }

            }
        }







    }


    private fun unInstallModule()
    {
        if(manager.installedModules.contains(dynamicMofule))
        {
            val installedModules = manager.installedModules.toList()
            manager.deferredUninstall(installedModules).addOnSuccessListener {
                Toast.makeText(this,"Uninstalling $installedModules",Toast.LENGTH_LONG).show()
            }.addOnFailureListener {
                Toast.makeText(this,"Failed installation of $installedModules",Toast.LENGTH_LONG).show()
            }
        }
        else
            Toast.makeText(this,"module not installed",Toast.LENGTH_LONG).show()


    }

    private fun reInstallModule()
    {
        if(manager.installedModules.contains(dynamicMofule)) {

                val installedModules = manager.installedModules.toList()
                manager.deferredUninstall(installedModules).addOnSuccessListener {
                    hello.text = "Uninstalling $installedModules"
                }.addOnFailureListener {
                    hello.text ="Failed installation of $installedModules"         }
            }


            val request =  SplitInstallRequest .newBuilder ()
                .addModule (dynamicMofule)
                .build ()
            manager.startInstall(request)


    }


    private  fun  displayLogin () {
        progress.visibility =  View . GONE
    }


    override  fun  onPause () {
        manager.unregisterListener (listener)
        super .onPause ()
    }


    private  fun  loadAndLaunchModule ( name :  String ) {
        updateProgressMessage ( " Loading the Module $ name " )
        // Skip loading if the module is already installed. Perform success action directly.
        if (manager.installedModules.contains (name)) {
            updateProgressMessage ( " It is already installed " )
            launchActivity (administrativoClassname)
            displayLogin ()
            return
        }

        // Create request to install a feature module by name.
        val request =  SplitInstallRequest .newBuilder ()
            .addModule (name)
            .build ()

        // Load and install the requested feature module.
        manager.startInstall (request)

        updateProgressMessage ( " Loading! " )
        progress.visibility = View.VISIBLE
    }

    private  fun  displayLoadingState (state : SplitInstallSessionState, message :  String ) {

        progress.visibility = View.VISIBLE
        progress.max = state.totalBytesToDownload (). toInt ()
        progress.progress = state.bytesDownloaded (). toInt ()

        updateProgressMessage (message)
    }



    private  fun  updateProgressMessage ( message :  String ) {
        if (progress.visibility != View.VISIBLE)
            hello.text = message
    }


    private  fun  launchActivity ( className :  String ) {
        Intent () .setClassName (applicationContext, className)
            . also {
                startActivity (it)
            }
    }

    override fun onResume() {
        super.onResume()
        hello.text = BuildConfig.VERSION_NAME
        manager.registerListener (listener)
        super .onResume ()
    }
private fun checkForUpdate()
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

    fun popupSnackbarForReinstallModule() {
        val snackbar = Snackbar.make(
            findViewById<View>(R.id.root),
            "Module updated",
            Snackbar.LENGTH_INDEFINITE
        )
        snackbar.setAction("Explore") { view -> launchActivity(administrativoClassname) }
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
                    isReinstall = mFirebaseRemoteConfig.getBoolean("isReinstall")

                    if(isReinstall)
                        reInstallModule()

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
