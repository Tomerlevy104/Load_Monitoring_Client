package com.finalproject.load_monitoring

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.finalproject.load_monitoring.ui.search.TrainSearchFragment
import com.finalproject.load_monitoring.ui.trainslist.TrainsListFragment

class MainActivity : AppCompatActivity() {
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        // Check if its the first time the app is opened
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, TrainDetailsFragment())
//                .replace(R.id.fragment_container, TrainsListFragment()).commit()
                .replace(R.id.fragment_container, TrainSearchFragment()).commit()

        }
    }
}