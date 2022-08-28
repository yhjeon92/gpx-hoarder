package com.example.myapplication.ui.login

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityLoginBinding
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.*

class LoginActivity : AppCompatActivity(), LocationListener {
    private lateinit var locationManager: LocationManager
    private lateinit var tvGpsLocation: TextView
    private lateinit var tvAltitude: TextView
    private lateinit var tvTimestamp: TextView

    private val locationPermissionCode = 2
    private lateinit var binding: ActivityLoginBinding

    private lateinit var fileWriter: FileWriter
    private lateinit var bufferedWriter: BufferedWriter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val button: Button = findViewById(R.id.getLocation)
        button.setOnClickListener {
            getLocation()
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), 1
            )
        }

        //val path = filesDir.path + "/appTest.txt"
        //Log.d("Test", "path : $path")
        //fileWriter = FileWriter(File(path), true)

        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        //val path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        fileWriter = FileWriter(File(path.absolutePath, "appTest.txt"), true)
        bufferedWriter = BufferedWriter(fileWriter)
        bufferedWriter.append("<gpx>")
        bufferedWriter.newLine()
        bufferedWriter.append("<trk>")
        bufferedWriter.newLine()
        bufferedWriter.append("<name>MyApplicationTest</name>")
        bufferedWriter.newLine()
        bufferedWriter.append("<type>CYCLING</type>")
        bufferedWriter.newLine()
        bufferedWriter.append("<trkseg>")
        bufferedWriter.newLine()

        val endAct: Button = findViewById(R.id.endActivity)
        endAct.setOnClickListener {
            bufferedWriter.append("</trkseg>")
            bufferedWriter.newLine()
            bufferedWriter.append("</trk>")
            bufferedWriter.newLine()
            bufferedWriter.append("</gpx>")
            bufferedWriter.close()
            fileWriter.close()
        }
    }

    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0.1f, this)
    }

    override fun onLocationChanged(location: Location) {
        tvGpsLocation = findViewById(R.id.textView)
        tvAltitude = findViewById(R.id.altitudeTextView)
        tvTimestamp = findViewById(R.id.timestampTextView)
        tvGpsLocation.text = "Latitude: " + location.latitude + " , Longitude: " + location.longitude
        tvAltitude.text = "Altitude: " + location.altitude
        tvTimestamp.text = "Timestamp: " + location.time

        val latitude: Double = location.latitude
        val longitude: Double = location.longitude
        val altitude: Double = location.altitude
        val timestamp: Long = location.time

        val tsString = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA).format(Date(timestamp))

        bufferedWriter.append("<trkpt lat=\"$latitude\" lon=\"$longitude\">")
        bufferedWriter.newLine()
        bufferedWriter.append("<ele>$altitude</ele>")
        bufferedWriter.newLine()
        bufferedWriter.append("<time>$tsString</time>")
        bufferedWriter.newLine()
        bufferedWriter.append("</trkpt>")
        bufferedWriter.newLine()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
                applicationContext,
                "$welcome $displayName",
                Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}