package com.finalproject.load_monitoring

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.finalproject.load_monitoring.ui.traindetails.TrainDetailsFragment
import com.finalproject.load_monitoring.ui.trainslist.TrainsListFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // בדוק אם זה פעם ראשונה שה-Activity נוצר
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, TrainDetailsFragment())
                .replace(R.id.fragment_container, TrainsListFragment()).commit()
        }
    }
}