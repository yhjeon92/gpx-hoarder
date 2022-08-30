package com.yhjeon92.gpxHoarder.ui.main

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Environment
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.*

class LoggingService : Service(), LocationListener {
    private lateinit var locationManager: LocationManager
    private lateinit var fileWriter: FileWriter
    private lateinit var bufferedWriter: BufferedWriter

    private val locationPermissionCode = 2

    override fun onBind(intent: Intent): IBinder {

        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            return START_NOT_STICKY
        }

        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val fileName = intent?.getStringExtra("filename")
        fileWriter = FileWriter(File(path.absolutePath, fileName), true)
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return Service.START_NOT_STICKY
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0.1f, this)

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        bufferedWriter.append("</trkseg>")
        bufferedWriter.newLine()
        bufferedWriter.append("</trk>")
        bufferedWriter.newLine()
        bufferedWriter.append("</gpx>")
        bufferedWriter.close()
        fileWriter.close()

        super.onDestroy()
    }

    override fun onLocationChanged(location: Location) {
        val latitude: Double = location.latitude
        val longitude: Double = location.longitude
        val altitude: Double = location.altitude
        val dateTimeString = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA).format(
            Date(location.time)
        )

        bufferedWriter.append("<trkpt lat=\"$latitude\" lon=\"$longitude\">")
        bufferedWriter.newLine()
        bufferedWriter.append("<ele>$altitude</ele>")
        bufferedWriter.newLine()
        bufferedWriter.append("<time>$dateTimeString</time>")
        bufferedWriter.newLine()
        bufferedWriter.append("</trkpt>")
        bufferedWriter.newLine()
    }
}