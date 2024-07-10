package com.duelisto

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.duelisto.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(HomeFragment())
        binding.bottomNavigationView.background = null


        // Set custom ripple effect for menu items
        applyCustomRippleEffect(binding.bottomNavigationView)
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.Home -> replaceFragment(HomeFragment())
                R.id.Message -> replaceFragment(MessageFragment())
                R.id.Notification -> replaceFragment(NotificationFragment())
                R.id.Profile -> replaceFragment(ProfileFragment())
            }
            true
        }

    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTranslation = fragmentManager.beginTransaction()
        fragmentTranslation.replace(R.id.frame_layout, fragment)
        fragmentTranslation.commit()
    }

    @SuppressLint("RestrictedApi")
    private fun applyCustomRippleEffect(bottomNavigationView: BottomNavigationView) {
        val menuView = bottomNavigationView.getChildAt(0) as BottomNavigationMenuView
        for (i in 0 until menuView.childCount) {
            val item = menuView.getChildAt(i) as BottomNavigationItemView
            item.setBackgroundResource(R.drawable.ripple_effect)
        }
    }
}
