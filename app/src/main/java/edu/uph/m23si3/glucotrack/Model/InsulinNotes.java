package edu.uph.m23si3.glucotrack.Model;

import java.time.LocalDate;
import java.time.LocalTime;

public class InsulinNotes {
    private int numberOfUnitOfInsulin;
    private String notes;
    private LocalDate date;
    private LocalTime time;

    public InsulinNotes(int numberOfUnitOfInsulin, String notes, LocalDate date, LocalTime time) {
        this.numberOfUnitOfInsulin = numberOfUnitOfInsulin;
        this.notes = notes;
        this.date = date;
        this.time = time;
    }

    public int getNumberOfUnitOfInsulin() {
        return numberOfUnitOfInsulin;
    }

    public void setNumberOfUnitOfInsulin(int numberOfUnitOfInsulin) {
        this.numberOfUnitOfInsulin = numberOfUnitOfInsulin;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }
}
