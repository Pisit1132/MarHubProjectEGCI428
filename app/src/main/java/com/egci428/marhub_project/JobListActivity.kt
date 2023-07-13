package com.egci428.marhub_project

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class JobListActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var sensorManager: SensorManager
    private lateinit var shakeDetector: ShakeDetector
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_list)
        lateinit var postJobButton: Button
        lateinit var profileButton: Button
        lateinit var messageButton: Button
        listView = findViewById(R.id.list_view)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        shakeDetector = ShakeDetector()
        database = FirebaseDatabase.getInstance().getReference("jobs")
        postJobButton = findViewById(R.id.post_job_button)
        profileButton = findViewById(R.id.profile_button)

        shakeDetector.setOnShakeListener(object: ShakeDetector.OnShakeListener {
            override fun onShake() {
                val intent = Intent(this@JobListActivity, PostJobActivity::class.java)
                startActivity(intent)
            }
        })

        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(shakeDetector, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        listView.setOnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position) as String
            // Do something with the selected item
            Toast.makeText(this, "Selected: $item", Toast.LENGTH_SHORT).show()
        }

        postJobButton.setOnClickListener {
            val intent = Intent(this, PostJobActivity::class.java)
            startActivity(intent)
        }

        profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }


        loadJobs()
    }

    override fun onResume() {
        super.onResume()
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(shakeDetector, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(shakeDetector)  // Don't forget to unregister the listener!
    }

    private fun loadJobs() {
        Log.d("JobListActivity", "Loading jobs...")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val jobs = mutableListOf<String>()
                for (postSnapshot in dataSnapshot.children) {
                    val job = postSnapshot.getValue(Job::class.java)
                    if (job != null) {
                        jobs.add("Posted by ${job.username}\n${job.description}\nBudget: ${job.budget}\nContact: ${job.contact}")
                    }
                }
                val adapter = ArrayAdapter(this@JobListActivity, android.R.layout.simple_list_item_1, jobs)
                listView.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("JobListActivity", "loadJobs:onCancelled", databaseError.toException())
            }
        })
    }
}