package com.smogunovandrey.tasksplanning.taskstemplate

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.smogunovandrey.tasksplanning.*
import com.smogunovandrey.tasksplanning.databinding.FragmentTasksTemplateBinding
import com.smogunovandrey.tasksplanning.runtask.ManagerActiveTask
import com.smogunovandrey.tasksplanning.runtask.RunTaskViewModel
import com.smogunovandrey.tasksplanning.utils.showDialogWithSettings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class TasksTemplateFragment : Fragment(), OnRunTaskItemClick {

    private val binding: FragmentTasksTemplateBinding by lazy {
        FragmentTasksTemplateBinding.inflate(layoutInflater)
    }

    private val adapter: AdapterTasksTemplate by lazy {
        AdapterTasksTemplate().apply {
            onRunTaskItemClick = this@TasksTemplateFragment
        }
    }
    private val managerActiveTask by lazy {
        ManagerActiveTask.getInstance(requireContext().applicationContext)
    }

    private val model: TaskTemplateViewModel by activityViewModels()
    private val modelRunTask: RunTaskViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.rvTasks.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                withContext(Dispatchers.Default) {
                    Log.d("TasksTemplateFragment", "before reloadActiviTask")
                    managerActiveTask.reloadActiviTask()
                }
                managerActiveTask.activeRunTaskWithPointsFlow.collect {
                    Log.d("TasksTemplateFragment", "collect activeRunTaskWithPointsFlow $it")
                    adapter.activeRunTaskWithPoints = it
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.tasksTemplate.collect { listTasks ->
                    Log.d("TasksTemplateFragment", "collect listTasks $listTasks")
                    adapter.submitList(null)
                    adapter.submitList(listTasks)
                }
            }
        }

        binding.btnAdd.setOnClickListener {
            model.editedTaskWithPoints.clear()
            val action =
                TasksTemplateFragmentDirections.actionTasksTemplateFragmentToTaskEditFragment(0)
            findNavController().navigate(action)
        }
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.d(
            "TasksTemplateFragment",
            "onRequestPermissionsResult permissions=${permissions.toList()}," +
                    "grantResults=${grantResults.toList()},requestCode=$requestCode"
        )
    }

    private fun startTask(task: Task) {
        lifecycleScope.launch {
            val activeRunTask = managerActiveTask.activeRunTaskWithPointsFlow.value
            if (activeRunTask == null) {
                withContext(Dispatchers.Default) {
                    managerActiveTask.startTask(task.id)
                }
                findNavController().navigate(TasksTemplateFragmentDirections.actionTasksTemplateFragmentToRunTaskActiveFragment())
            } else {
                //id mus equals
                if (activeRunTask.runTask.idTask == task.id) {
                    findNavController().navigate(TasksTemplateFragmentDirections.actionTasksTemplateFragmentToRunTaskActiveFragment())
                }
            }
        }
    }

    fun canWorkBackground(): Boolean{
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
            return requireActivity().checkSelfPermission(ACCESS_FINE_LOCATION) == PERMISSION_GRANTED

        return requireActivity().checkSelfPermission(ACCESS_BACKGROUND_LOCATION) == PERMISSION_GRANTED
    }


    @Inject
    lateinit var dataStore: DataStore<Preferences>
    //@Inject lateinit var dataStore: DataStore<Preferences>

    override fun onRunTaskItemClick(task: Task) {
        //Test preference jetpack library
//        val strKey = stringPreferencesKey("strKKK")
//        lifecycleScope.launch {
//            dataStore.edit {
//                it[strKey] = UUID.randomUUID().toString()
//            }
//        }
//        return
        if(!canWorkBackground()){
            requireActivity().showDialogWithSettings()
            return
        }

        lifecycleScope.launch {
            val activeRunTask = managerActiveTask.activeRunTaskWithPointsFlow.value
            if (activeRunTask == null) {
                withContext(Dispatchers.Default) {
                    managerActiveTask.startTask(task.id)
                }
                findNavController().navigate(TasksTemplateFragmentDirections.actionTasksTemplateFragmentToRunTaskActiveFragment())
            } else {
                //already run task
                if (activeRunTask.runTask.idTask == task.id) findNavController().navigate(
                    TasksTemplateFragmentDirections.actionTasksTemplateFragmentToRunTaskActiveFragment()
                )
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.settings, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.settings -> {
                findNavController().navigate(TasksTemplateFragmentDirections.actionTasksTemplateFragmentToSettingsFragment())
            }
        }
        return false
    }
}