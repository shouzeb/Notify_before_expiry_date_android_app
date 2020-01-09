package com.example.bayan_oh.inspect;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class DBHandler  extends SQLiteOpenHelper {

    // Define Database Parameters
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "inspectDB.db";
    private static final String TABLE_PRODUCTS = "products";
    private static final String TABLE_CATEGORY = "categories";

    // Define Column Names For Product Table
    public static final String COLUMN_PID = "p_id";
    public static final String COLUMN_PNAME = "p_name";
    public static final String COLUMN_XPDATE = "exp_date";
    public static final String COLUMN_NTDATE = "not_date";
    public static final String COLUMN_PCID = "c_id";
    public static final String COLUMN_IMAGE = "p_image";
    public static final String COLUMN_DDIFF = "date_difference";

    // Define Column Names For Category Table
    public static final String COLUMN_CID = "c_id";
    public static final String COLUMN_CNAME = "c_name";
    public static final String COLUMN_ICON = "icon_name";

    public DBHandler(Context context, String name,
                     SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Category Table
        String CREATE_CATEGORY_TABLE = "CREATE TABLE " +
                TABLE_CATEGORY + "("
                + COLUMN_CID + " INTEGER PRIMARY KEY," + COLUMN_CNAME
                + " TEXT," + COLUMN_ICON
                + " TEXT"+ ")";
        db.execSQL(CREATE_CATEGORY_TABLE);

        // Create Product Table
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " +
                TABLE_PRODUCTS + "("
                + COLUMN_PID + " INTEGER PRIMARY KEY," + COLUMN_PNAME
                + " TEXT," + COLUMN_XPDATE
                + " TEXT," + COLUMN_NTDATE
                + " TEXT," + COLUMN_PCID
                + " TEXT," + COLUMN_IMAGE
                + " BLOB," + COLUMN_DDIFF
                + " TEXT" + ")";
        db.execSQL(CREATE_PRODUCTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);

        onCreate(db);
    }

    public void addProduct(Product product) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_PID, product.getPID());
        values.put(COLUMN_PNAME, product.getPName());
        values.put(COLUMN_XPDATE , product.getXPDate());
        values.put(COLUMN_NTDATE, product.getNTDate());
        values.put(COLUMN_PCID, product.getCID());
        values.put(COLUMN_IMAGE, product.getImage());
        values.put(COLUMN_DDIFF, product.getDiffDate());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_PRODUCTS, null, values);
        db.close();
    }

    public void addCategory(Category category) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_CID, category.getCID());
        values.put(COLUMN_CNAME, category.getCName());
        values.put(COLUMN_ICON, category.getIcon());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_CATEGORY, null, values);
        db.close();
    }

    public int getNewProductID(){

        String query = "Select * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_PID + " =  (SELECT max(" + COLUMN_PID + ") FROM " + TABLE_PRODUCTS + ")";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        int max=0;

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            max = Integer.parseInt(cursor.getString(0));
        }
        db.close();
        return max+1;

    }

    public int getNewCategoryID(){

        String query = "Select * FROM " + TABLE_CATEGORY + " WHERE " + COLUMN_CID + " =  (SELECT max(" + COLUMN_CID + ") FROM " + TABLE_CATEGORY + ")";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        int max=0;

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            max = Integer.parseInt(cursor.getString(0));
        }
        db.close();
        return max+1;

    }

    public List<Product> findAllProducts(int id) {
        List<Product> products = new ArrayList<Product>();
        String selectQuery = "SELECT  * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_PCID + " =  \"" + id + "\"";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

                // Get Expiration Date
                Calendar cal1 = Calendar.getInstance();
                try {
                    cal1.setTime(dateFormat.parse(c.getString(2)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // Get yesterday's date
                Calendar cal2 = Calendar.getInstance();
                cal2.add(Calendar.DAY_OF_YEAR, -1);
                Date yesterday = cal2.getTime();

                // Delete Product If Expiration Date is Before Current Day
                if (cal1.getTime().compareTo(yesterday) < 0)
                {
                    deleteProduct(Integer.parseInt(c.getString(0)));
                }
                else {
                    Product product = new Product();
                    product.setPID(c.getInt((c.getColumnIndex(COLUMN_PID))));
                    product.setPName((c.getString(c.getColumnIndex(COLUMN_PNAME))));
                    product.setXPDate(c.getString(c.getColumnIndex(COLUMN_XPDATE)));
                    product.setNTDate(c.getString(c.getColumnIndex(COLUMN_NTDATE)));
                    product.setCID(c.getInt((c.getColumnIndex(COLUMN_PCID))));
                    product.setImage(c.getBlob((c.getColumnIndex(COLUMN_IMAGE))));
                    product.setDiffDate(c.getString(c.getColumnIndex(COLUMN_DDIFF)));

                    // adding to products list
                    products.add(product);
                }
            } while (c.moveToNext());
        }

        return products;
    }

    public List<Category> findAllCategories() {
        List<Category> categories = new ArrayList<Category>();
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORY;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Category category = new Category();
                category.setCID(c.getInt((c.getColumnIndex(COLUMN_CID))));
                category.setCName((c.getString(c.getColumnIndex(COLUMN_CNAME))));
                category.setIcon((c.getString(c.getColumnIndex(COLUMN_ICON))));

                // adding to categories list
                categories.add(category);
            } while (c.moveToNext());
        }

        return categories;
    }

    public Product findProduct(int id) {
        String query = "Select * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_PID + " =  \"" + id + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Product product = new Product();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
                product.setPID(Integer.parseInt(cursor.getString(0)));
                product.setPName(cursor.getString(1));
                product.setXPDate(cursor.getString(2));
                product.setNTDate(cursor.getString(3));
                product.setCID(Integer.parseInt(cursor.getString(4)));
                product.setImage(cursor.getBlob(5));
                product.setDiffDate(cursor.getString(6));
            cursor.close();
        } else {
            product = null;
        }
        db.close();
        return product;
    }

    public Category findCategory(int id) {
        String query = "Select * FROM " + TABLE_CATEGORY + " WHERE " + COLUMN_CID + " =  \"" + id + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Category category = new Category();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            category.setCID(Integer.parseInt(cursor.getString(0)));
            category.setCName(cursor.getString(1));
            category.setIcon(cursor.getString(2));
            cursor.close();
        } else {
            category = null;
        }
        db.close();
        return category;
    }

    public void deleteProduct(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("delete from  " + TABLE_PRODUCTS +
                " where " + COLUMN_PID + " =\'" + id +
                "\'" );
        db.close();

    }

    public void deleteCategory(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("delete from  " + TABLE_CATEGORY +
                " where " + COLUMN_CID + " =\'" + id +
                "\'" );

        db.execSQL("delete from  " + TABLE_PRODUCTS +
                " where " + COLUMN_PCID + " =\'" + id +
                "\'" );
        db.close();

    }

    public boolean editCategory(Category category) {

        boolean result = false;

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CID, category.getCID());
        values.put(COLUMN_CNAME, category.getCName());
        values.put(COLUMN_ICON, category.getIcon());

        int rows = db.update(TABLE_CATEGORY, values, COLUMN_CID + " = ?",
                new String[] { String.valueOf(category.getCID()) });

        if (rows != 0) {
            result = true;
        }

        return result;
    }

    public boolean editProduct(Product product) {

        boolean result = false;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PID, product.getPID());
        values.put(COLUMN_PNAME, product.getPName());
        values.put(COLUMN_XPDATE , product.getXPDate());
        values.put(COLUMN_NTDATE, product.getNTDate());
        values.put(COLUMN_PCID, product.getCID());
        values.put(COLUMN_IMAGE, product.getImage());
        values.put(COLUMN_DDIFF , product.getDiffDate());

        int rows = db.update(TABLE_PRODUCTS, values, COLUMN_PID + " = ?",
                new String[] { String.valueOf(product.getPID()) });

        if (rows != 0) {
            result = true;
        }

        return result;
    }


}


