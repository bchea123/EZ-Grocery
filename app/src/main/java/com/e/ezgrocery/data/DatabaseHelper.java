package com.e.ezgrocery.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.e.ezgrocery.R;
import com.e.ezgrocery.model.GroceryItem;
import com.e.ezgrocery.util.Util;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, Util.DATABASE_NAME, null, Util.VERSION_NUMBER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create database table
        String CREATE_TABLE = "CREATE TABLE " + Util.TABLE_NAME + " ("
                + Util.KEY_ID + " INTEGER PRIMARY KEY, "
                + Util.KEY_ITEM + " TEXT, "
                + Util.KEY_QUANTITY + " INTEGER, "
                + Util.KEY_DESCRIPTION + " TEXT)";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTable = String.valueOf(R.string.drop_table);
        db.execSQL(dropTable, new String[]{Util.TABLE_NAME});

        onCreate(db);
    }

    public void createGroceryItem(GroceryItem groceryItem){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Util.KEY_ITEM, groceryItem.getItemName());
        values.put(Util.KEY_QUANTITY, groceryItem.getItemQuantity());
        values.put(Util.KEY_DESCRIPTION, groceryItem.getItemDescription());

        db.insert(Util.TABLE_NAME, null, values);
        db.close();
    }

    public int updateGroceryItem(GroceryItem groceryItem){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Util.KEY_ITEM, groceryItem.getItemName());
        values.put(Util.KEY_QUANTITY, groceryItem.getItemQuantity());
        values.put(Util.KEY_DESCRIPTION, groceryItem.getItemDescription());

        return db.update(Util.TABLE_NAME, values, Util.KEY_ID + "=?",
                new String[]{String.valueOf(groceryItem.getId())});
    }

    public void deleteGroceryItem(int id){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(Util.TABLE_NAME, Util.KEY_ID + "=?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    public GroceryItem getGroceryItem(String table, int id){
        SQLiteDatabase db = this.getReadableDatabase();

        //Create cursor to point to table
        Cursor cursor = db.query(table, new String[]{Util.KEY_ID, Util.KEY_ITEM, Util.KEY_QUANTITY, Util.KEY_DESCRIPTION},
                Util.KEY_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);

        //Move cursor to first item in table
        if (cursor != null)
            cursor.moveToFirst();

        //Create return grocery item object
        GroceryItem groceryItem = new GroceryItem();
        groceryItem.setId(cursor.getInt(0));
        groceryItem.setItemName(cursor.getString(1));
        groceryItem.setItemQuantity(cursor.getInt(2));
        groceryItem.setItemDescription(cursor.getString(3));

        cursor.close();

        return groceryItem;
    }

    public List<GroceryItem> getAllGroceries(){
        List<GroceryItem> groceryItemList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String selectAll = "SELECT * FROM " + Util.TABLE_NAME;
        Cursor cursor = db.rawQuery(selectAll, null);

        //Move to first item in table
        if (cursor.moveToFirst()){
            do {
                //Create grocery item
                GroceryItem groceryItem = new GroceryItem();
                groceryItem.setId(cursor.getInt(0));
                groceryItem.setItemName(cursor.getString(1));
                groceryItem.setItemQuantity(cursor.getInt(2));
                groceryItem.setItemDescription(cursor.getString(3));

                //Add grocery item to list
                groceryItemList.add(groceryItem);
            } while(cursor.moveToNext());
        }

        cursor.close();
        return groceryItemList;
    }

    public int getCount(){
        SQLiteDatabase db = this.getReadableDatabase();

        //Create cursor, point to table, return value of getCount() method from Cursor library
        String countQuery = "SELECT * FROM " + Util.TABLE_NAME;
        Cursor cursor = db.rawQuery(countQuery, null);
        return cursor.getCount();
    }
}
