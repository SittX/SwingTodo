package org.example;

public class TaskPriority {
    private Priority priority;

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public TaskPriority(Priority priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return priority.toString();
    }
}
