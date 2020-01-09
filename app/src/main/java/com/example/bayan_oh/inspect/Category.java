package com.example.bayan_oh.inspect;

public class Category {

    private int cat_id;
    private String cat_name;
    private String icon_name;

    public Category() {

    }

    public Category(int id, String category_name, String icon_name) {
        this.cat_id = id;
        this.cat_name = category_name;
        this.icon_name = icon_name;

    }

    public void setCID(int id) {
        this.cat_id = id;
    }

    public int getCID() {
        return this.cat_id;
    }

    public void setCName(String category_name) {
        this.cat_name = category_name;
    }

    public String getCName (){
        return this.cat_name;
    }

    public void setIcon(String icon_name) {
        this.icon_name = icon_name;
    }

    public String getIcon(){
        return this.icon_name;
    }


}
