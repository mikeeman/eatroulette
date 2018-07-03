package com.squarepi.eatroulette;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mmatkiws on 5/7/2017.
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME                    = "EatRouletteData.db";
    public static final String SETTINGS_TABLE_NAME              = "settings";
    public static final String SETTINGS_COLUMN_ID               = "id";
    public static final String SETTINGS_COLUMN_NAME             = "name";
    public static final String SETTINGS_COLUMN_VALUE            = "value";
    public static final String SETTINGS_NAME_TOKEN              = "token";
    public static final String SETTINGS_NAME_TOKENEXPIRES       = "tokenexpires";

    public static final String SEARCH_TABLE_NAME                = "search";
    public static final String SEARCH_COLUMN_ID                 = "id";
    public static final String SEARCH_COLUMN_NAME               = "name";
    public static final String SEARCH_COLUMN_PHONE              = "phone";
    public static final String SEARCH_COLUMN_PRICE              = "price";
    public static final String SEARCH_COLUMN_PHOTO              = "image_url";
    public static final String SEARCH_COLUMN_RATING             = "rating";
    public static final String SEARCH_COLUMN_LATITUDE           = "lat";
    public static final String SEARCH_COLUMN_LONGITUDE          = "lon";
    public static final String SEARCH_COLUMN_ADDRESS_COUNTRY    = "country";
    public static final String SEARCH_COLUMN_ADDRESS_STATE      = "state";
    public static final String SEARCH_COLUMN_ADDRESS_CITY       = "city";
    public static final String SEARCH_COLUMN_ADDRESS_ZIP_CODE   = "zip_code";
    public static final String SEARCH_COLUMN_ADDRESS_STREET     = "address1";
    public static final String SEARCH_COLUMN_WEBSITE            = "url";
    public static final String SEARCH_COLUMN_TYPE               = "type";

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + SETTINGS_TABLE_NAME
                + "("
                + SETTINGS_COLUMN_ID + " integer primary key, "
                + SETTINGS_COLUMN_NAME + " text, "
                + SETTINGS_COLUMN_VALUE + " text"
                + ");"
        );

        db.execSQL("create table " + SEARCH_TABLE_NAME
                + "("
                + SEARCH_COLUMN_ID + " integer primary key, "
                + SEARCH_COLUMN_NAME + " text, "
                + SEARCH_COLUMN_PHONE + " text, "
                + SEARCH_COLUMN_PRICE + " text, "
                + SEARCH_COLUMN_PHOTO + " text, "
                + SEARCH_COLUMN_RATING + " text, "
                + SEARCH_COLUMN_LATITUDE + " text, "
                + SEARCH_COLUMN_LONGITUDE + " text, "
                + SEARCH_COLUMN_ADDRESS_COUNTRY + " text, "
                + SEARCH_COLUMN_ADDRESS_STATE + " text, "
                + SEARCH_COLUMN_ADDRESS_CITY + " text, "
                + SEARCH_COLUMN_ADDRESS_ZIP_CODE + " text, "
                + SEARCH_COLUMN_ADDRESS_STREET + " text, "
                + SEARCH_COLUMN_WEBSITE + " text, "
                + SEARCH_COLUMN_TYPE + " text"
                + ");"
        );

        //insertSetting(SETTINGS_NAME_TOKEN, "");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SETTINGS_TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + SEARCH_TABLE_NAME + ";");
        onCreate(db);
    }

    public boolean insertSetting  (String name, String value)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();

        ContentValues contentValues = new ContentValues();
        contentValues.put(SETTINGS_COLUMN_NAME, name);
        contentValues.put(SETTINGS_COLUMN_VALUE, value);

        db.insert(SETTINGS_TABLE_NAME, null, contentValues);

        db.setTransactionSuccessful();

        db.endTransaction();
        return true;
    }

    public boolean insertSearch (JSONObject business){
        SQLiteDatabase db = this.getWritableDatabase();

        String name         = new String();
        String phone        = new String();
        String price        = new String();
        String photo        = new String();
        String rating       = new String();
        String latitude     = new String();
        String longitude    = new String();
        //String country      = new String();
        String state        = new String();
        String city         = new String();
        //String zip_code     = new String();
        String street       = new String();
        String website      = new String();
        String type         = new String();

        try {
            name = business.getString(YelpHelper.RESPONSE_NAME);
            photo = business.getString(YelpHelper.RESPONSE_PHOTO);
            rating = business.getString(YelpHelper.RESPONSE_RATING);
            //phone = business.getString(YelpHelper.RESPONSE_PHONE);
            price = business.getString(YelpHelper.RESPONSE_PRICE);
            website = business.getString(YelpHelper.RESPONSE_WEBSITE);

            JSONObject location = new JSONObject(business.getString(YelpHelper.RESPONSE_LOCATION));
            //country = location.getString(YelpHelper.RESPONSE_ADDRESS_COUNTRY);
            state = location.getString(YelpHelper.RESPONSE_ADDRESS_STATE);
            city = location.getString(YelpHelper.RESPONSE_ADDRESS_CITY);
            //zip_code = location.getString(YelpHelper.RESPONSE_ADDRESS_ZIP_CODE);
            street = location.getString(YelpHelper.RESPONSE_ADDRESS_STREET);

            JSONArray categories = new JSONArray(business.getString(YelpHelper.RESPONSE_TYPE));
            JSONObject category = categories.getJSONObject(0);
            type = category.getString(YelpHelper.RESPONSE_TYPE_NAME);

            JSONObject coordinates = new JSONObject(business.getString(YelpHelper.RESPONSE_COORDINATES));
            latitude = coordinates.getString(YelpHelper.RESPONSE_LATITUDE);
            longitude = coordinates.getString(YelpHelper.RESPONSE_LONGITUDE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        db.beginTransaction();

        ContentValues contentValues = new ContentValues();
        contentValues.put(SEARCH_COLUMN_NAME, name);
        contentValues.put(SEARCH_COLUMN_PHONE, phone);
        contentValues.put(SEARCH_COLUMN_PRICE, price);
        contentValues.put(SEARCH_COLUMN_PHOTO, photo);
        contentValues.put(SEARCH_COLUMN_RATING, rating);
        //contentValues.put(SEARCH_COLUMN_ADDRESS_COUNTRY, country);
        contentValues.put(SEARCH_COLUMN_ADDRESS_STATE, state);
        contentValues.put(SEARCH_COLUMN_ADDRESS_CITY, city);
        //contentValues.put(SEARCH_COLUMN_ADDRESS_ZIP_CODE, zip_code);
        contentValues.put(SEARCH_COLUMN_ADDRESS_STREET, street);
        contentValues.put(SEARCH_COLUMN_WEBSITE, website);
        contentValues.put(SEARCH_COLUMN_TYPE, type);
        contentValues.put(SEARCH_COLUMN_LATITUDE, latitude);
        contentValues.put(SEARCH_COLUMN_LONGITUDE, longitude);

        db.insert(SEARCH_TABLE_NAME, null, contentValues);

        db.setTransactionSuccessful();

        db.endTransaction();
        return true;
    }

    public Cursor getSearchById(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + SEARCH_TABLE_NAME + " where " + SEARCH_COLUMN_ID + "=" + id + ";", null );
        return res;
    }

    public String getToken() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select * from " + SETTINGS_TABLE_NAME + " where " + SETTINGS_COLUMN_NAME + "=\"" + SETTINGS_NAME_TOKEN + "\";", null );

        String token = new String();

        if (res != null) {
            if (res.moveToFirst()) {
                do {
                    token = res.getString(res.getColumnIndex(SETTINGS_COLUMN_VALUE));
                } while (res.moveToNext());
            }
        }

        return token;
    }

    public Boolean isValidToken() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res1 =  db.rawQuery( "select * from " + SETTINGS_TABLE_NAME + " where " + SETTINGS_COLUMN_NAME + "=\"" + SETTINGS_NAME_TOKEN + "\";", null );

        String token = new String();
        String expiry = new String();

        int tokens = 0;
        int expiryDates = 0;
        int expiresIn = 0;

        if (res1 != null) {
            if (res1.moveToFirst()) {
                do {
                    token = res1.getString(res1.getColumnIndex(SETTINGS_COLUMN_VALUE));
                    tokens++;
                } while (res1.moveToNext());
            }
        }
        else {
            return false;
        }

        Cursor res2 =  db.rawQuery( "select * from " + SETTINGS_TABLE_NAME + " where " + SETTINGS_COLUMN_NAME + "=\"" + SETTINGS_NAME_TOKENEXPIRES + "\";", null );

        if (res2 != null) {
            if (res2.moveToFirst()) {
                do {
                    expiry = res2.getString(res2.getColumnIndex(SETTINGS_COLUMN_VALUE));
                    if (expiry.length() > 0) {
                        expiresIn = Integer.parseInt(expiry);
                    }
                    expiryDates++;
                } while (res2.moveToNext());
            }
        }
        else {
            return false;
        }

        if (tokens != 1 || expiryDates != 1) {
            return false;
        }

        if (token.length() == 0 || expiry.length() == 0 || expiresIn <= 0 || (System.currentTimeMillis()/1000) > expiresIn) {
            return false;
        }

        return true;
    }

    public int numberOfRows(String table){
        SQLiteDatabase db = this.getReadableDatabase();

        int numRows = (int) DatabaseUtils.queryNumEntries(db, table);
        return numRows;
    }

    public boolean updateSetting (Integer id, String name, String value)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("pragma journal_mode=memory;");

        ContentValues contentValues = new ContentValues();
        contentValues.put(SETTINGS_COLUMN_NAME, name);
        contentValues.put(SETTINGS_COLUMN_VALUE, value);

        db.update(SETTINGS_TABLE_NAME, contentValues, SETTINGS_COLUMN_ID + " = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteSetting (String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(SETTINGS_TABLE_NAME, SETTINGS_COLUMN_NAME + "=\"" + name + "\"", null);
    }

    public ArrayList<String> getAllSettings()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select * from " + SETTINGS_TABLE_NAME + ";", null );
        res.moveToFirst();

        while(res.isAfterLast() == false) {
            array_list.add(res.getString(res.getColumnIndex(SETTINGS_COLUMN_NAME)));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> getAllSearch(){
        ArrayList<String> array_list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select * from " + SEARCH_TABLE_NAME + ";", null );
        res.moveToFirst();

        while(res.isAfterLast() == false) {
            array_list.add(res.getString(res.getColumnIndex(SEARCH_COLUMN_NAME)));
            res.moveToNext();
        }
        return array_list;
    }

    public static void clearSearchData(SQLiteDatabase db){
        db.execSQL("DELETE FROM " + SEARCH_TABLE_NAME + ";");
    }

    public static void dropSearchTable(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS " + SEARCH_TABLE_NAME + ";");
    }
}

