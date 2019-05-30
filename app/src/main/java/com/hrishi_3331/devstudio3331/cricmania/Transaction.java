package com.hrishi_3331.devstudio3331.cricmania;

public class Transaction {

    private String date;
    private String note;
    private int amount;
    private int mode;

    public Transaction() {
    }

    public Transaction(String date, String note, int amount, int mode) {
        this.date = date;
        this.note = note;
        this.amount = amount;
        this.mode = mode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
