package com.example.todolistapp.data.models;

import java.util.Objects;

public class Task {
    private int id,status;
    private  String title,task;
    private Long date;

    public Task(String title, String task, int status, Long date) {
        this.title = title;
        this.task = task;
        this.status = status;
        this.date = date;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
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
        return id == otherTask.id &&
                status == otherTask.status &&
                Objects.equals(title, otherTask.title) &&
                Objects.equals(task, otherTask.task) &&
                Objects.equals(date, otherTask.date);
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}
