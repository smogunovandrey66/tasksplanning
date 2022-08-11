package com.smogunovandrey.tasksplanning.taskstemplate

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.smogunovandrey.tasksplanning.ACCESS_BACKGROUND_LOCATION
import com.smogunovandrey.tasksplanning.ACCESS_COARSE_LOCATION
import com.smogunovandrey.tasksplanning.ACCESS_FINE_LOCATION
import com.smogunovandrey.tasksplanning.MainActivity
import com.smogunovandrey.tasksplanning.databinding.FragmentTasksTemplateBinding
import com.smogunovandrey.tasksplanning.runtask.ManagerActiveTask
import com.smogunovandrey.tasksplanning.runtask.RunTaskViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class
TasksTemplateFragment : Fragment(), OnRunTaskItemClick {

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

    private fun infoPermission(permission: String) {
        val permissionInt = ActivityCompat.checkSelfPermission(requireActivity(), permission)
        Log.d("registerForActivityResult", "$permission=$permissionInt")
    }

    private fun startTask(task: Task){
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
    override fun onRunTaskItemClick(task: Task) {
        infoPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        infoPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        infoPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        Log.d("TasksTemplateFragment", "onRunTaskItemClick, task=$task")

        val activityResultLauncher = (requireActivity() as MainActivity).activityResultLauncher
        activityResultLauncher.launch(arrayOf(
//            ACCESS_BACKGROUND_LOCATION,
            ACCESS_COARSE_LOCATION,
            ACCESS_FINE_LOCATION
        ))
        if(requireActivity().checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //Check background
        }

        return

    }
}