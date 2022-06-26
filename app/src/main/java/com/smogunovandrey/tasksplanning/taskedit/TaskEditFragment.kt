package com.smogunovandrey.tasksplanning.taskedit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.smogunovandrey.tasksplanning.databinding.FragmentTaskEditBinding
import com.smogunovandrey.tasksplanning.taskstemplate.Point
import com.smogunovandrey.tasksplanning.taskstemplate.Task
import com.smogunovandrey.tasksplanning.taskstemplate.TaskTemplateViewModel
import com.smogunovandrey.tasksplanning.taskstemplate.TaskWithPoints
import com.smogunovandrey.tasksplanning.utils.swap
import kotlinx.coroutines.launch

class TaskEditFragment: Fragment(), AdapterEditPoints.OnClickPoint {

    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var simpleCallback: ItemTouchHelper.SimpleCallback
    private val binding by lazy {
        FragmentTaskEditBinding.inflate(layoutInflater)
    }

    private val args by navArgs<TaskEditFragmentArgs>()
    private val model: TaskTemplateViewModel by activityViewModels()
    private val adapter: AdapterEditPoints by lazy{
        AdapterEditPoints(editedPoints, this)
    }

    //Edited Data
    private val editedTaskWithPoints: TaskWithPoints by lazy {
        model.editedTaskWithPoints
    }
    private val editedTask by lazy {
        editedTaskWithPoints.task
    }
    private val editedPoints by lazy{
        editedTaskWithPoints.points
    }

    //DB Data(saved)
    private lateinit var dbTaskWithPoints: TaskWithPoints
    private val dbTask: Task
        get() {
            return dbTaskWithPoints.task
        }
    private val dbPoints: List<Point>
        get(){
            return dbTaskWithPoints.points
        }


    private fun checkSave(){
        if(!this::dbTaskWithPoints.isInitialized)
            return

        var ena = editedTask != dbTask || editedPoints.size != dbPoints.size
        if(!ena){
            for(i in editedPoints.indices){
                ena = editedPoints[i] != dbPoints[i]
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
        //viewLifecycleOwner vs this - see documentation(documentation in detached)
        binding.lifecycleOwner = viewLifecycleOwner

        //Set adapter
        binding.rvPoints.adapter = adapter

        binding.taskItem = editedTaskWithPoints.task

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                model.taskWithPoints(editedTaskWithPoints.task.id).collect{
                    dbTaskWithPoints = it
                    checkSave()
                }
            }
        }

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

        simpleCallback = object :ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun isItemViewSwipeEnabled(): Boolean {
                return super.isItemViewSwipeEnabled()
            }

            override fun isLongPressDragEnabled(): Boolean {
                return super.isLongPressDragEnabled()
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPos = viewHolder.adapterPosition
                val toPos = target.adapterPosition

                editedPoints.swap(fromPos, toPos)
                adapter.notifyItemMoved(fromPos, toPos)

                editedPoints[fromPos].num = fromPos + 1L
                editedPoints[toPos].num = toPos + 1L

                checkSave()

                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val delPos = viewHolder.adapterPosition
                editedPoints.removeAt(delPos)
                adapter.notifyItemRemoved(delPos)
                for(i in delPos + 1 until editedPoints.size){
                    editedPoints[i].num = i.toLong() - 1L
                }
                checkSave()
            }

        }

        itemTouchHelper = ItemTouchHelper(simpleCallback)

        binding.edtName.addTextChangedListener {
            it?.let {
                editedTask.name = it.toString()
            }
            checkSave()
        }

        binding.btnCancel.setOnClickListener {
            val navController = findNavController()
            navController.popBackStack()
        }

        binding.btnSave.setOnClickListener {
            lifecycleScope.launch {
                model.updateTaskWithPoints(editedTaskWithPoints)
            }
        }

        binding.btnAddPoint.setOnClickListener{
            model.flagEditPoint = false
            model.editedPoint.clear()
            model.editedPoint.idTask = editedTask.id
            DialogEditPointFragment().show(childFragmentManager, DialogEditPointFragment.TAG)
        }

        updateTouchHelper()
        binding.btnSwitchEditClickPoint.isChecked = model.editClickPoint

        binding.btnSwitchEditClickPoint.setOnCheckedChangeListener { _, isChecked ->
            model.editClickPoint = isChecked
            updateTouchHelper()
        }


        return binding.root
    }

    /**
     * Update behavior touch event recycler view
     */
    fun updateTouchHelper(){
        if(model.editClickPoint)
            //Mode click item
            itemTouchHelper.attachToRecyclerView(null)
        else
            //Mode move/swipe item
            itemTouchHelper.attachToRecyclerView(binding.rvPoints)
    }

    override fun onClick(point: Point) {
        //Only mode click
        if(!model.editClickPoint)
            return

        model.editedPoint = point
        model.flagEditPoint = true
        DialogEditPointFragment().show(childFragmentManager, DialogEditPointFragment.TAG)
    }
}