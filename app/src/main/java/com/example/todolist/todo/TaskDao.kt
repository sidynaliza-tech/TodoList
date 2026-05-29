package com.example.todolist.todo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {

    // Сортуємо спочатку за закріпленням (isPinned DESC), потім за id
    @Query("SELECT * FROM tasks_table ORDER BY isPinned DESC, id DESC")
    fun getAllTasks(): List<Task>

    @Insert
    fun insertTask(task: Task)

    @Update
    fun updateTask(task: Task)

    @Delete
    fun deleteTask(task: Task)
}