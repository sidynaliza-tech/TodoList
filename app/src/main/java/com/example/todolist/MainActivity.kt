package com.example.todolist

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.databinding.ActivityMainBinding
import com.example.todolist.todo.Task
import com.example.todolist.todo.TaskDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.lifecycleScope

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var database: TaskDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = TaskDatabase.getDatabase(this)

        setupRecyclerView()
        setupSwipeToDelete()

        loadTasks()

        binding.buttonAdd.setOnClickListener {
            val taskTitle = binding.editTextTask.text.toString().trim()
            if (taskTitle.isNotEmpty()) {
                val newTask = Task(title = taskTitle)

                CoroutineScope(Dispatchers.IO).launch {
                    database.taskDao().insertTask(newTask)
                    loadTasks()

                    withContext(Dispatchers.Main) {
                        binding.editTextTask.text.clear()
                    }
                }
            } else {
                Toast.makeText(this, "Введіть текст!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            onStatusChanged = { updatedTask ->
                CoroutineScope(Dispatchers.IO).launch {
                    database.taskDao().updateTask(updatedTask)
                    loadTasks()
                }
            },
            onDeleteClicked = { taskToDelete ->
                CoroutineScope(Dispatchers.IO).launch {
                    database.taskDao().deleteTask(taskToDelete)
                    loadTasks()
                }
            },
            onLongClick = { taskToPin ->
                // Перемикаємо стан закріплення
                taskToPin.isPinned = !taskToPin.isPinned

                CoroutineScope(Dispatchers.IO).launch {
                    database.taskDao().updateTask(taskToPin)
                    loadTasks()
                }

                val message = if (taskToPin.isPinned) "Закріплено!" else "Відкріплено"
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        )

        binding.recyclerViewTasks.apply {
            adapter = taskAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun setupSwipeToDelete() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val taskToDelete = taskAdapter.currentList[position]

                lifecycleScope.launch(Dispatchers.IO) {
                    database.taskDao().deleteTask(taskToDelete)
                    loadTasks()
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewTasks)
    }

    private fun loadTasks() {
        CoroutineScope(Dispatchers.IO).launch {
            val tasks = database.taskDao().getAllTasks()
            withContext(Dispatchers.Main) {
                taskAdapter.submitList(tasks)
            }
        }
    }
}