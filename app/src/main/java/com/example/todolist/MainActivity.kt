package com.example.todolist

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolist.databinding.ActivityMainBinding
import com.example.todolist.todo.Task
import com.example.todolist.todo.TaskDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var database: TaskDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Якщо цей рядок все одно горітиме червоним — зроби крок 3!
        database = TaskDatabase.getDatabase(this)

        setupRecyclerView()
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
            }
        )

        binding.recyclerViewTasks.apply {
            adapter = taskAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
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