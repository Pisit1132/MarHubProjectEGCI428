package com.egci428.marhub_project

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GestureDetectorCompat
import java.util.Random

class ProfileActivity : AppCompatActivity(), GestureDetector.OnDoubleTapListener {
    private lateinit var imageView: ImageView
    private lateinit var usernameTextView: TextView
    private lateinit var mDetector: GestureDetectorCompat

    // List of drawable resources
    private val images = listOf(
        R.drawable.image4,
        R.drawable.image5,
        R.drawable.image3,
        // Add more images here
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        imageView = findViewById(R.id.profile_image)
        usernameTextView = findViewById(R.id.username_text_view)

        val prefs = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        val username = prefs.getString("username", "")
        usernameTextView.text = username

        // Initialize the gesture detector
        mDetector = GestureDetectorCompat(this, GestureDetector.SimpleOnGestureListener())
        mDetector.setOnDoubleTapListener(this)

        // Set onTouchListener to the imageView
        imageView.setOnTouchListener { _, event ->
            mDetector.onTouchEvent(event)
            true
        }
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        // When a double tap is detected, change the image source to a random image
        val randomImage = images[Random().nextInt(images.size)]
        imageView.setImageResource(randomImage)
        return true
    }

    override fun onDoubleTapEvent(e: MotionEvent): Boolean {
        return false
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        return false
    }
}
