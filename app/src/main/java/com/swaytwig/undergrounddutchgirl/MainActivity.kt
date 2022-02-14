package com.swaytwig.undergrounddutchgirl

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private val fragmentHome by lazy { HomeFragment() }
    private val fragmentTexture by lazy { TextureFragment() }

    private val launcherMFile =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val directoryUri = it.data?.data ?: return@registerForActivityResult
                contentResolver.takePersistableUriPermission(
                    directoryUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            }
            checkPermission()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermission()

        initNavigationBar()
        loadFragment(fragmentHome, 0)
    }

    private fun checkPermission() {
        if (!MFile.checkGranted(this)) {
            val str = resources.getString(R.string.GRANT_PERMISSION_REQUIRED)

            @Suppress("DEPRECATION")
            val dialog = AlertDialog.Builder(this)
                .setMessage(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        Html.fromHtml(
                            str,
                            Html.FROM_HTML_MODE_COMPACT or Html.FROM_HTML_OPTION_USE_CSS_COLORS
                        )
                    else
                        Html.fromHtml(str)
                )
                .setTitle(R.string.GRANT_PERMISSION_REQUIRED_TITLE)
                .setPositiveButton(R.string.BUTTON_YES) { _, _ ->
                    launcherMFile.launch(
                        MFile.createRequestIntent()
                    )
                }
                .setNegativeButton(R.string.BUTTON_CLOSE) { _, _ -> finishAndRemoveTask() }
                .setOnCancelListener { finishAndRemoveTask() }
                .show()

            val msg = dialog.findViewById<TextView>(android.R.id.message)
            if (msg != null)
                msg.textSize = 15f
        } else {
            if(!MFile.isExists(this, "com.smartjoy.LastOrigin_C/files")) {
                AlertDialog.Builder(this)
                    .setMessage(R.string.TARGET_NOT_FOUND)
                    .setTitle(R.string.GRANT_PERMISSION_REQUIRED_TITLE)
                    .setNegativeButton(R.string.BUTTON_CLOSE) { _, _ -> finishAndRemoveTask() }
                    .setOnCancelListener { finishAndRemoveTask() }
                    .show()
                return
            }

            enableNavigationBar()
        }
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == EXTERNAL_MANAGE_REQUEST_CODE)
//            this.checkPermission(false)
//    }
//
//    private fun checkPermission(need_request: Boolean) {
//        val context: Context = this@MainActivity
//
//        val dir = Game.gameAssetDir(context)
//        if (dir.isEmpty()) {
//            Toast.makeText(context, R.string.TARGET_NOT_FOUND, Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        if (need_request) {
//            android.util.Log.e("PATH", dir)
//            val intent = MFile.createRequestIntent(dir.substringBefore("/files/") + "/")
//            launcherMFile.launch(intent)
//        }
//
//        /*
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            if (!Environment.isExternalStorageManager()) {
//                Toast.makeText(
//                    this,
//                    R.string.GRANT_PERMISSION_REQUIRED,
//                    Toast.LENGTH_SHORT
//                ).show()
//
//                val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
//                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri)
//                startActivity(intent)
//            }
//        }
//        */
//    }
//
//    private fun requestPermission(permissions: Array<String>) {
//        ActivityCompat.requestPermissions(this, permissions, EXTERNAL_MANAGE_REQUEST_CODE)
//        this.checkPermission(false)
//
//        for (p in permissions) {
//            val r = !ActivityCompat.shouldShowRequestPermissionRationale(this, p)
//            if (!r) {
//                Toast.makeText(this, R.string.GRANT_PERMISSION_REQUIRED, Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

    private fun initNavigationBar() {
        val navigation = findViewById<BottomNavigationView>(R.id.navigation)

        navigation.isEnabled = false
        navigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.action_nav_home -> loadFragment(fragmentHome, 1)
                R.id.action_nav_texture -> loadFragment(fragmentTexture, 2)
            }
            true
        }
    }

    private fun enableNavigationBar() {
        val navigation = findViewById<BottomNavigationView>(R.id.navigation)
        navigation.isEnabled = true
    }

    private var startingPosition: Int = -1
    private fun loadFragment(fragment: Fragment?, newPosition: Int): Boolean {
        if (fragment != null) {
            when {
                newPosition == 0 -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_container, fragment)
                        .commit()
                }
                startingPosition > newPosition -> {
                    supportFragmentManager
                        .beginTransaction()
                        .setCustomAnimations(
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                        )
                        .replace(R.id.frame_container, fragment)
                        .commit()
                }
                startingPosition < newPosition -> {
                    supportFragmentManager
                        .beginTransaction()
                        .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                        .replace(R.id.frame_container, fragment)
                        .commit()
                }
            }
            startingPosition = newPosition
            return true
        }
        return false
    }
}