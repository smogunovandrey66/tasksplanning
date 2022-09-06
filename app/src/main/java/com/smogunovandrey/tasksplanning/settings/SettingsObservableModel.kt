package com.smogunovandrey.tasksplanning.settings

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.smogunovandrey.tasksplanning.BR

class SettingsObservableModel: BaseObservable() {

    private var editedGeofenceRadiusString = SettingsDatastoreViewModel.KEY_GEOFENCE_RADIUS_DATA_STORE.toString()

    @Bindable
    fun getGeofenceRadiusString(): String{
        return editedGeofenceRadiusString
    }

    fun setGeofenceRadiusString(value: String) {
        if(editedGeofenceRadiusString != value){
            editedGeofenceRadiusString = value
            notifyPropertyChanged(BR.geofenceRadiusString)
        }
    }
}