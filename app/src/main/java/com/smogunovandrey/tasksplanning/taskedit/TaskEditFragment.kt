package com.smogunovandrey.tasksplanning.taskedit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.smogunovandrey.tasksplanning.databinding.FragmentTaskEditBinding
import com.smogunovandrey.tasksplanning.taskstemplate.Point
import com.smogunovandrey.tasksplanning.taskstemplate.Task
import com.smogunovandrey.tasksplanning.taskstemplate.TaskTemplateViewModel
import com.smogunovandrey.tasksplanning.utils.swap
import kotlinx.coroutines.launch

class TaskEditFragment: Fragment() {

    private val binding by lazy {
        FragmentTaskEditBinding.inflate(layoutInflater)
    }

    private val args by navArgs<TaskEditFragmentArgs>()
    private val model: TaskTemplateViewModel by activityViewModels()
    private val adapter: AdapterEditPoints by lazy{
        AdapterEditPoints(model.editedTaskWithPoints.points)
    }
    private var pointsBefore: List<Point> = emptyList()
    private var taskBefor: Task = Task()

    private fun checkSave(){
        var ena = taskBefor == binding.taskItem && pointsBefore.size != adapter.points.size
        if(!ena){
            for(i in pointsBefore.indices){
                ena = pointsBefore[i] != adapter.points[i]
                if(ena)
                    break
            }
        }
        binding.btnSave.isEnabled = ena
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("TaskEditFragment", "onCreateView")
        //viewLifecycleOwner vs this - see documentation(documentation in detached)
        binding.lifecycleOwner = viewLifecycleOwner

        //Set adapter
        binding.rvPoints.adapter = adapter

        binding.taskItem = model.editedTaskWithPoints.task
//        adapter.points = model.editedTaskWithPoints.points

        //Collect data about task by way of Flow
//        lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
//                model.taskById(args.idTask).collect{
//                    it?.let {
//                        binding.taskItem = it
//                        taskBefor = it.copy()
//                    }
//                }
//            }
//        }

        //Collect data about points by way of Flow
//        lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
//                model.pointsTemplate(args.idTask).collect{
//                    adapter.points = it.toMutableList()
//                    pointsBefore = it.toMutableList()
//                    adapter.notifyDataSetChanged()
//                }
//            }
//        }

        val simpleCallback = object :ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPos = viewHolder.adapterPosition
                val toPos = target.adapterPosition

                adapter.points.swap(fromPos, toPos)
                adapter.notifyItemMoved(fromPos, toPos)

                checkSave()

                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val delPos = viewHolder.adapterPosition
                adapter.points.removeAt(delPos)
                adapter.notifyItemRemoved(delPos)
                checkSave()
            }

        }

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvPoints)

        binding.edtName.addTextChangedListener {
            checkSave()
        }

        return binding.root
    }
}