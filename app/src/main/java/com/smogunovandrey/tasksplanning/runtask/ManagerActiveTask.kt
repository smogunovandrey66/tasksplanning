package com.smogunovandrey.tasksplanning.runtask

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Looper
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.smogunovandrey.tasksplanning.R
import com.smogunovandrey.tasksplanning.db.*
import com.smogunovandrey.tasksplanning.taskstemplate.RunPoint
import com.smogunovandrey.tasksplanning.taskstemplate.RunTask
import com.smogunovandrey.tasksplanning.taskstemplate.RunTaskWithPoints
import com.smogunovandrey.tasksplanning.utils.toRunPointDB
import com.smogunovandrey.tasksplanning.utils.toRunTaskDB
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*
import java.util.concurrent.TimeUnit

class ManagerActiveTask private constructor(val context: Context) {
    private val dao by lazy {
        AppDatabase.getInstance(context).mainDao()
    }

    var idRunTask = 0L

    private val _activeRunTaskWithPointsFlow = MutableStateFlow<RunTaskWithPoints?>(null)
    val activeRunTaskWithPointsFlow: StateFlow<RunTaskWithPoints?> = _activeRunTaskWithPointsFlow

    private val notificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private val geofenceClient by lazy {
        LocationServices.getGeofencingClient(context)
    }

    private val usedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    private var pendingIntentGeofence: PendingIntent? = null

    private suspend fun curGspPoint(): PointGpsDB? {
        val idPointGpsDB = _activeRunTaskWithPointsFlow.value?.curPoint()?.idPoint ?: return null
        return dao.gpsPointSuspend(idPointGpsDB)
    }

    private suspend fun checkAndStartGeofence() {
        curGspPoint()?.let { gpsPointDB ->
            pendingIntentGeofence?.let {
                geofenceClient.removeGeofences(it)
            }

            pendingIntentGeofence = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_BROAD_CAST_GEOFENCE,
                Intent(context, GeofenceBroadcastReceiver::class.java)
                    .apply {
                        action = "ManagerActiveTask.treasureHunt.action.ACTION_GEOFENCE_EVENT"
                    },
                PendingIntent.FLAG_UPDATE_CURRENT or
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                            PendingIntent.FLAG_MUTABLE
                        else
                            0
            )
            Log.d(
                "ManagerActiveTask",
                "checkAndStartGeofence pendingIntentGeofence=$pendingIntentGeofence"
            )
            geofenceClient.addGeofences(
                GeofencingRequest.Builder()
                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                    .addGeofences(
                        listOf(
                            Geofence.Builder()
                                .setRequestId(REQUEST_ID_GEOFENCE)
                                .setCircularRegion(
                                    gpsPointDB.latitude,
                                    gpsPointDB.longitude,
                                    GEOFENCE_RADIUS_IN_METERS
                                )
                                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                                .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                                .build()
                        )
                    )
                    .build(),
                pendingIntentGeofence
            ).run {
                addOnFailureListener { exception ->
                    Log.d("ManagerActiveTask", "addGeofences exception=$exception")
                }
                addOnSuccessListener {
                    Log.d("ManagerActiveTask", "addGeofences succes")
                }
            }

            // Start update location
            usedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallBack,
                Looper.getMainLooper()
            )

        } ?: run {
            //Logic remove gps point(if add before)
            pendingIntentGeofence?.let {
                geofenceClient.removeGeofences(it)
            }
            usedLocationClient.removeLocationUpdates(locationCallBack)
        }
    }

    fun notificationBuilder(commandId: Int) = NotificationCompat.Builder(context, CHANNEL_ID)
        .setCustomContentView(
            RemoteViews(context.packageName, R.layout.notification_run_task)
                .apply {
                    val intentNext = Intent(context, RunBroadcastReceiver::class.java)
                    intentNext.putExtra(COMMAND_ID, commandId)

                    val pendingIntentNext = PendingIntent.getBroadcast(
                        context,
                        1,
                        intentNext,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )

                    setOnClickPendingIntent(R.id.btn_next, pendingIntentNext)
                    _activeRunTaskWithPointsFlow.value?.let { runTaskWithPoints ->
                        setTextViewText(R.id.txt_name, runTaskWithPoints.runTask.name)

                        val curPoint = runTaskWithPoints.curPoint()
                        if (curPoint == null)
                            setImageViewResource(R.id.btn_next, R.drawable.baseline_play_arrow_24)
                        else
                            setTextViewText(R.id.txt_number, curPoint.num.toString())
                    }

                    val intentCancel = Intent(context, RunBroadcastReceiver::class.java)
                    intentCancel.putExtra(COMMAND_ID, COMMAND_CANCEL)

                    val pendingIntentCancel = PendingIntent.getBroadcast(
                        context,
                        1,
                        intentCancel,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )

                    setOnClickPendingIntent(R.id.btn_cancel, pendingIntentCancel)
                }
        )
        .setStyle(NotificationCompat.DecoratedCustomViewStyle())
        .setSmallIcon(R.drawable.baseline_run_circle_24)


    suspend fun reloadActiviTask() {
        val activeRunTaskWithPointDB = dao.activeRunTaskWithPointsSuspend()
        var activeRunTaskWithPoints: RunTaskWithPoints? = null
        activeRunTaskWithPointDB?.let {
            activeRunTaskWithPoints = getRunTask(activeRunTaskWithPointDB.runTask.idRunTask)
        }
        _activeRunTaskWithPointsFlow.emit(activeRunTaskWithPoints)
    }

    suspend fun getRunTask(idRunTask: Long): RunTaskWithPoints {
        Log.d("ManagerActiveTask", "getRunTask idRunTask=$idRunTask")
        val runTaskWithPointsDB = dao.runTaskWithPointsByIdSuspend(idRunTask)
        Log.d("ManagerActiveTask", "getRunTask runTaskWithPointsDB=$runTaskWithPointsDB")

        val runTaskDB = runTaskWithPointsDB.runTask
        val taskDB = dao.taskByIdSuspend(runTaskDB.idTask)
        val runTaskWithPoints = RunTaskWithPoints(
            RunTask(
                runTaskDB.idRunTask,
                runTaskDB.idTask,
                taskDB.name,
                runTaskDB.dateCreate,
                runTaskDB.active
            ),
            runTaskWithPointsDB.runPoints.map {
                var dateMark = 0L
                it.dateMark?.let { date ->
                    //First point
                    if (it.num == 1)
                        dateMark = date.time - runTaskDB.dateCreate.time
                    else {
                        //Other points
                        val prevDate = runTaskWithPointsDB.runPoints[it.num - 2].dateMark
                        if (prevDate != null)
                            dateMark = date.time - prevDate.time
                    }
                }

                val pointDB = dao.pointByIdSuspend(it.idPoint)

                RunPoint(
                    it.idRunPoint,
                    taskDB.idTask,
                    it.idPoint,
                    it.num,
                    pointDB.name,
                    pointDB.triggerType,
                    dateMark,
                    it.dateMark
                )
            }.toMutableList()
        )

        return runTaskWithPoints
    }

    suspend fun startTask(idTask: Long) {
        //1 Insert in DB
        val taskWithPointsDB: TaskWithPointDB = dao.taskWithPointsSuspend(idTask)
        val idRunTask = dao.insertRunTask(RunTaskDB(0, idTask))
        val points = taskWithPointsDB.points.sortedWith { p1, p2 ->
            p1.num - p2.num
        }
        for (pointDB in points) {
            dao.insertRunPoint(
                RunPointDB(
                    0,
                    idRunTask,
                    pointDB.idPoint,
                    pointDB.num
                )
            )
        }
        reloadActiviTask()
        checkAndStartGeofence()

        //2 Start Foreground Service
        ContextCompat.startForegroundService(context,
            Intent(context, RunService::class.java).apply {
                putExtra(COMMAND_ID, COMMAND_START)
            }
        )
    }

    suspend fun markPoint() { //or name as 'nextPoint'
        Log.d("ManagerActiveTask", "makrPoint")
        //Update DB
        val runTaskWithPoints = _activeRunTaskWithPointsFlow.value
        runTaskWithPoints?.let {
            var curIdx = 0
            val lastIdx = it.points.size - 1
            for (point in it.points) {
                if (point.dateMark == null) {
                    point.dateMark = Date()
                    val idRunTask = runTaskWithPoints.runTask.idRunTask
                    dao.updateRunPoint(point.toRunPointDB(idRunTask))

                    //Set active to false for RunTask if last point
                    if (curIdx == lastIdx) {
                        //Deactivate task
                        runTaskWithPoints.runTask.active = false
                        dao.updateRunTask(runTaskWithPoints.runTask.toRunTaskDB())

                        //Stop service
                        context.stopService(Intent(context, RunService::class.java))

                        stopLocationCheck()
                    }

                    _activeRunTaskWithPointsFlow.emit(getRunTask(idRunTask))

                    //Update notification
                    if (curIdx < lastIdx)
                        notificationManager.notify(
                            NOTIFICATION_ID,
                            notificationBuilder(COMMAND_NEXT).build()
                        )

                    pendingIntentGeofence?.let {
                        geofenceClient.removeGeofences(pendingIntentGeofence)
                        pendingIntentGeofence = null
                    }
                    break
                }
                curIdx++
            }

            reloadActiviTask()

            //Check and start (if need) geofence
            checkAndStartGeofence()
        }
    }

    suspend fun cancelTask() {
        val runTaskWithPoints = _activeRunTaskWithPointsFlow.value
        Log.d("ManagerActiveTask", "cancel task $runTaskWithPoints")
        //Delete from DB
        runTaskWithPoints?.let {
            for (point in it.points) {
                val idRunTask = runTaskWithPoints.runTask.idRunTask
                val delRunPointDB = point.toRunPointDB(idRunTask)
                Log.d("ManagerActiveTask", "delRunPointDB=$delRunPointDB")
                val delId = dao.deleteRunPoint(delRunPointDB)
                Log.d("ManagerActiveTask", "delId=$delId")
            }

            val delRunTaskDB = runTaskWithPoints.runTask.toRunTaskDB()
            Log.d("ManagerActiveTask", "delRunTaskDB=$delRunTaskDB")
            dao.deleteRunTask(delRunTaskDB)
            //Stop service
            context.stopService(Intent(context, RunService::class.java))

            _activeRunTaskWithPointsFlow.value = null

            pendingIntentGeofence?.let { notNullPendingIntent ->
                geofenceClient.removeGeofences(notNullPendingIntent)
            }
        }

        stopLocationCheck()
    }

    private val locationCallBack = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            Log.d("ManagerActiveTask", "locationResult=$locationResult")
        }

        override fun onLocationAvailability(locationAvailability: LocationAvailability?) {
            Log.d("ManagerActiveTask", "locationAvailability=$locationAvailability")
        }
    }

    private val locationRequest = LocationRequest()
        .setInterval(3000)
        .setFastestInterval(1500)
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

    private var stateUpdateLocation = false

    private fun startLocationCheck() {
        usedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallBack,
            Looper.getMainLooper()
        )
        stateUpdateLocation = true
    }

    private fun stopLocationCheck() {
        usedLocationClient.removeLocationUpdates(locationCallBack)
        stateUpdateLocation = false
    }

    companion object {
        const val NOTIFICATION_ID = 1

        const val CHANNEL_ID = "channel id for run task"
        const val CHANNEL_NAME = "channel name for run task"
        const val COMMAND_ID = "command for run task"

        const val COMMAND_START = 1
        const val COMMAND_NEXT = COMMAND_START + 1
        const val COMMAND_CANCEL = COMMAND_START + 2

        const val REQUEST_ID_GEOFENCE = "id key geofence"
        const val REQUEST_CODE_BROAD_CAST_GEOFENCE = 1

        const val GEOFENCE_RADIUS_IN_METERS = 100f
        val GEOFENCE_EXPIRATION_IN_MILLISECONDS = TimeUnit.HOURS.toMillis(1L)

        @Volatile
        private var instance: ManagerActiveTask? = null

        /**
         * Singleton
         */
        fun getInstance(context: Context): ManagerActiveTask = instance ?: synchronized(this) {
            val res = instance ?: ManagerActiveTask(context)
            Log.d("ManagerActiveTask", "getInstance $res")
            instance = res
            res
        }
    }
}
