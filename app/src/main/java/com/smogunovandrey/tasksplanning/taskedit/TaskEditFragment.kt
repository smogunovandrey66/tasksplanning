package com.smogunovandrey.tasksplanning.taskedit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.smogunovandrey.tasksplanning.databinding.FragmentTaskEditBinding
import com.smogunovandrey.tasksplanning.taskstemplate.Point
import com.smogunovandrey.tasksplanning.taskstemplate.TaskTemplateViewModel
import com.smogunovandrey.tasksplanning.utils.swap
import kotlinx.coroutines.launch

class TaskEditFragment: Fragment() {

    private val binding by lazy {
        FragmentTaskEditBinding.inflate(layoutInflater)
    }

    private val args by navArgs<TaskEditFragmentArgs>()
    private val model: TaskTemplateViewModel by viewModels()
    private val adapter: AdapterEditPoints = AdapterEditPoints()
    private var pointsBefore: List<Point> = emptyList()

    private fun checkSave(){
        val ena = !(adapter.points.containsAll(pointsBefore) && pointsBefore.containsAll(adapter.points))
        Log.d("TaskEditFragment", "ena = $ena")
        binding.btnSave.isEnabled = ena
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //viewLifecycleOwner vs this - see documentation(documentation in detached)
        binding.lifecycleOwner = viewLifecycleOwner

        //Set adapter
        binding.rvPoints.adapter = adapter

        //Collect data about task by way of Flow
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                model.taskById(args.idTask).collect{
                    it?.let {
                        binding.taskItem = it
                    }
                }
            }
        }

        //Collect data about points by way of Flow
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                model.pointsTemplate(args.idTask).collect{
                    adapter.points = it.toMutableList()
                    pointsBefore = it.toMutableList()
                    adapter.notifyDataSetChanged()
                }
            }
        }

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

        return binding.root
    }
}