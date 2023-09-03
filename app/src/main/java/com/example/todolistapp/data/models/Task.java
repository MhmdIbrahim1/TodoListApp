package com.example.todolistapp.data.models;

import java.util.List;
import java.util.Objects;

public class Task {
    private int id,status;;
    private  String title,task;
    private Long date;

    public Task(int id, String title, int status) {
        this.id = id;
        this.title = title;
        this.status = status;
    }

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
        return Objects.equals(title, otherTask.title);
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}
