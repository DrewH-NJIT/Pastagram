package com.example.pastagram

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.pastagram.fragments.ComposeFragment
import com.example.pastagram.fragments.FeedFragment
import com.example.pastagram.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentManager: FragmentManager = supportFragmentManager

        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnItemSelectedListener {
            item -> // this call allows you to name the SINGLE param THEY give you called 'it'. not readable, so we name our own.

            var fragmentToShow: Fragment? = null
            when (item.itemId){
                R.id.action_home ->{
                    fragmentToShow = FeedFragment()
                }
                R.id.action_compose ->{
                    fragmentToShow = ComposeFragment()
                }
                R.id.action_profile ->{
                    fragmentToShow = ProfileFragment()
                }
            }
            if(fragmentToShow!=null){
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragmentToShow).commit()
            }
            // return true = we handled this thing. don't call your super.thing()
            true
        }

        findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId = R.id.action_home

//        queryPosts()
    }


}