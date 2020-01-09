package com.example.bayan_oh.inspect;


public class Product {
    private int p_id;
    private String p_name;
    private String exp_date;
    private String not_date;
    private String diff_date;
    private int c_id;
    private byte[] image;


    public Product() {

    }

    public Product(int pid, String name, String exparation, String notification, String diff_date, int cid, byte[] image) {
        this.p_id = pid;
        this.p_name = name;
        this.exp_date = exparation;
        this.not_date = notification;
        this.c_id = cid;
        this.image = image;
        this.diff_date = diff_date;
    }


    public void setPID(int pid) {
        this.p_id = pid;
    }

    public int getPID() {
        return this.p_id;
    }

    public void setPName(String name) {
        this.p_name = name;
    }

    public String getPName() {
        return this.p_name;
    }

    public void setXPDate(String exparation) {
        this.exp_date = exparation;
    }

    public String getXPDate() {
        return this.exp_date;
    }

    public void setNTDate(String notification) {
        this.not_date = notification;
    }

    public String getNTDate() {
        return this.not_date;
    }

    public void setDiffDate(String diff_date) {
        this.diff_date = diff_date;
    }

    public String getDiffDate() {
        return this.diff_date;
    }

    public void setCID(int cid) {
        this.c_id = cid;
    }

    public int getCID() {
        return this.c_id;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public byte[] getImage() {
        return this.image;
    }


}
