package com.swaytwig.undergrounddutchgirl

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private val fragmentHome by lazy { HomeFragment() }
    private val fragmentTexture by lazy { TextureFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initNavigationBar()
        loadFragment(fragmentHome, 0)
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
            if (newPosition == 0) {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_container, fragment)
                    .commit()
            } else if (startingPosition > newPosition) {
                supportFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                    )
                    .replace(R.id.frame_container, fragment)
                    .commit()
            } else if (startingPosition < newPosition) {
                supportFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                    )
                    .replace(R.id.frame_container, fragment)
                    .commit()
            }
            startingPosition = newPosition
            return true
        }
        return false
    }
}