package com.swaytwig.undergrounddutchgirl

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    companion object {
        private const val EXTERNAL_MANAGE_REQUEST_CODE = 3464
        private const val SAF_REQUEST_CODE = 3465
    }

    private val fragmentHome by lazy { HomeFragment() }
    private val fragmentTexture by lazy { TextureFragment() }

    private val launcherSAF = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == SAF_REQUEST_CODE)
            checkPermission(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermission(true)

        initNavigationBar()
        loadFragment(fragmentHome, 0)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == EXTERNAL_MANAGE_REQUEST_CODE)
            this.checkPermission(false)
    }

    private fun checkPermission(need_request: Boolean) {
        val context: Context = this@MainActivity

        val dir = Game.gameAssetDir(context)
        if(dir.isEmpty()) {
            Toast.makeText(context, R.string.TARGET_NOT_FOUND, Toast.LENGTH_SHORT).show()
            return
        }

        if(need_request) {
            android.util.Log.e("PATH", dir)
            val intent = SAF.createRequestIntent(context, dir.substringBefore("/files/") + "/")
            launcherSAF.launch(intent)
        }
        /*

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Toast.makeText(
                    this,
                    R.string.GRANT_PERMISSION_REQUIRED,
                    Toast.LENGTH_SHORT
                ).show()

                val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri)
                startActivity(intent)
            }
        }
        */
    }

    private fun requestPermission(permissions: Array<String>) {
        ActivityCompat.requestPermissions(this, permissions, EXTERNAL_MANAGE_REQUEST_CODE)
        this.checkPermission(false)

        for (p in permissions) {
            val r = !ActivityCompat.shouldShowRequestPermissionRationale(this, p)
            if (!r) {
                Toast.makeText(this, R.string.GRANT_PERMISSION_REQUIRED, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initNavigationBar() {
        val navigation = findViewById<BottomNavigationView>(R.id.navigation)

        navigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.action_nav_home -> loadFragment(fragmentHome, 1)
                R.id.action_nav_texture -> loadFragment(fragmentTexture, 2)
            }
            true
        }
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