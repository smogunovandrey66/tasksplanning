package com.smogunovandrey.tasksplanning.runtask

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.google.android.gms.location.LocationServices

class GeofenceBroadcastReceiver: BroadcastReceiver() {
    private val TAG = "GeofenceBroadcastReceiver"
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive")

        val geoFencingEvent = GeofencingEvent.fromIntent(intent)
        if(geoFencingEvent.hasError()){
            Log.d(TAG, "error in GeofenceBroadcastReceiver=${GeofenceStatusCodes.getStatusCodeString(geoFencingEvent.errorCode)}")
            return
        }

        val geofenceTransaction = geoFencingEvent.geofenceTransition
        if(geofenceTransaction == Geofence.GEOFENCE_TRANSITION_ENTER){
            val triggeringFeofences = geoFencingEvent.triggeringGeofences
            context?.let {
                it.sendBroadcast(Intent(it, RunBroadcastReceiver::class.java))
            }
        }
    }
}