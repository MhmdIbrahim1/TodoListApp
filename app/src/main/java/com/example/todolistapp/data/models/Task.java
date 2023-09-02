package com.example.todolistapp.data.models;

import java.util.List;
import java.util.Objects;

public class Task {
    private int id;
    private  String title;
    private  List<Subtask> subtasks;

    public Task(int id, String title, List<Subtask> subtasks) {
        this.id = id;
        this.title = title;
        this.subtasks = subtasks;
    }
    public Task(String title, List<Subtask> subtasks) {
        this.title = title;
        this.subtasks = subtasks;
    }
    public Task() {}

    public void setId(int id) {
        this.id = id;
    }


    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    // Implement equals() method to compare tasks
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Task otherTask = (Task) obj;
        return Objects.equals(title, otherTask.title);
    }
}
