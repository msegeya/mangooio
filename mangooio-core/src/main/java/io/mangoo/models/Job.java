package io.mangoo.models;

import java.util.Date;

public class Job {
    private boolean active;
    private String name;
    private String description;
    private Date nextFireTime;
    private Date previousFireTime;

    public Job(boolean active, String name, String description, Date nextFireTime, Date previousFireTime) {
        this.active = active;
        this.name = name;
        this.nextFireTime = (Date) nextFireTime.clone();
        this.previousFireTime = (Date) previousFireTime.clone();
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public Date getNextFireTime() {
        return (Date) nextFireTime.clone();
    }

    public Date getPreviousFireTime() {
        return (Date) previousFireTime.clone();
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }
}