package com.swaytwig.undergrounddutchgirl

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private val fragmentHome by lazy { HomeFragment() }
    private val fragmentTexture by lazy { TextureFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermission(true)

        initNavigationBar()
        loadFragment(fragmentHome, 0)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0)
            this.checkPermission(false)
    }

    private fun checkPermission(need_request: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            ).filter { it != "" }.toTypedArray()

            if (need_request) {
                val gr = PackageManager.PERMISSION_GRANTED
                if (permissions.any { p -> ContextCompat.checkSelfPermission(this, p) != gr })
                    this.requestPermission(permissions)
            }
        }

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
    }

    private fun requestPermission(permissions: Array<String>) {
        ActivityCompat.requestPermissions(this, permissions, 0)
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