package com.example.todolistapp.data.models;

public class Subtask {
    private int id;
    private String title;
    private boolean isDone;

    public Subtask(int id, String title, boolean isDone) {
        this.id = id;
        this.title = title;
        this.isDone = isDone;
    }

    public Subtask(String title, boolean isDone) {
        this.title = title;
        this.isDone = isDone;
    }

    public Subtask() {}


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }


    public boolean isDone() {
        return isDone;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
