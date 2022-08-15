package com.smogunovandrey.tasksplanning.taskstemplate

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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

    override fun onRunTaskItemClick(task: Task) {
        lifecycleScope.launch {
            val activeRunTask = managerActiveTask.activeRunTaskWithPointsFlow.value
            if (activeRunTask == null) {
                val accessBackground = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                    ActivityCompat.checkSelfPermission(
                        requireContext(),
                        ACCESS_BACKGROUND_LOCATION
                    ) == PERMISSION_DENIED
                } else {
                    ActivityCompat.checkSelfPermission(
                        requireContext(),
                        ACCESS_FINE_LOCATION
                    ) == PERMISSION_GRANTED
                }

                if (accessBackground) {
                    withContext(Dispatchers.Default) {
                        managerActiveTask.startTask(task.id)
                    }
                    findNavController().navigate(TasksTemplateFragmentDirections.actionTasksTemplateFragmentToRunTaskActiveFragment())
                } else {
                    val intent = Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.parse("package:" + requireContext().packageName)
                    }
                    requireActivity().startActivity(intent)
                }
            } else {
                //already run task
                if (activeRunTask.runTask.idTask == task.id) findNavController().navigate(
                    TasksTemplateFragmentDirections.actionTasksTemplateFragmentToRunTaskActiveFragment()
                )
            }
        }
    }
}