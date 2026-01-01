package com.example.fragmenthandler

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fragmenthandler.fragments.FragmentOne

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()
        setContentView(R.layout.activity_main)
    }
}