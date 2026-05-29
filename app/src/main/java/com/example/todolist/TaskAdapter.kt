package com.example.todolist

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.databinding.ItemTaskBinding
import com.example.todolist.todo.Task

class TaskAdapter(
    private val onStatusChanged: (Task) -> Unit,
    private val onDeleteClicked: (Task) -> Unit,
    private val onLongClick: (Task) -> Unit // Передаємо довгий клік
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(private val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            binding.checkBoxTask.text = task.title

            binding.checkBoxTask.setOnCheckedChangeListener(null)
            binding.checkBoxTask.isChecked = task.isCompleted

            // Логіка відображення стилів тексту
            if (task.isCompleted) {
                binding.checkBoxTask.paintFlags = binding.checkBoxTask.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                binding.checkBoxTask.setTextColor(Color.GRAY)
            } else {
                binding.checkBoxTask.paintFlags = binding.checkBoxTask.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()

                val titleLower = task.title.lowercase()
                if (titleLower.contains("!") || titleLower.contains("терміново") || titleLower.contains("важливо")) {
                    binding.checkBoxTask.setTextColor(Color.parseColor("#D32F2F")) // Червоний пріоритет
                } else {
                    binding.checkBoxTask.setTextColor(Color.BLACK)
                }
            }

            binding.checkBoxTask.invalidate() // Примусове перемалювання тексту

            binding.checkBoxTask.setOnCheckedChangeListener { _, isChecked ->
                task.isCompleted = isChecked
                onStatusChanged(task)
            }

            binding.buttonDeleteTask.setOnClickListener {
                onDeleteClicked(task)
            }

            // Довгий клік на всю картку таски для закріплення
            binding.root.setOnLongClickListener {
                onLongClick(task)
                true
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}