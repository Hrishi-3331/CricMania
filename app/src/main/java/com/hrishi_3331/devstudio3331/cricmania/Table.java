package com.hrishi_3331.devstudio3331.cricmania;

public class Table {

    private String table_id;
    private String host;
    private int CR;
    private int Players;

    public Table() {

    }

    public Table(String table_id, int CR, int players, String host) {
        this.table_id = table_id;
        this.CR = CR;
        Players = players;
        this.host = host;
    }

    public String getTable_id() {
        return table_id;
    }

    public void setTable_id(String table_id) {
        this.table_id = table_id;
    }

    public int getCR() {
        return CR;
    }

    public void setCR(int CR) {
        this.CR = CR;
    }

    public int getPlayers() {
        return Players;
    }

    public void setPlayers(int players) {
        Players = players;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
