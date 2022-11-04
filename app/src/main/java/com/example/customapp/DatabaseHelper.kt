package com.example.customapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE " + TABLE_NAME + " ("+
                NAME_COl + " TEXT, " +
                SPECIES_COL + " INTEGER, " +
                PURCHASEDATE_COL + " TEXT, " +
                WATERINGDATE_COL + " TEXT, "
                +ID_COL + " INTEGER PRIMARY KEY" + ")")
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        // this method is to check if table already exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    fun addPlant(name: String, species: Int, purchaseDate: String, wateringDate: String): Int {
        val values = ContentValues()

        values.put(NAME_COl, name)
        values.put(SPECIES_COL, species)
        values.put(PURCHASEDATE_COL, purchaseDate)
        values.put(WATERINGDATE_COL, wateringDate)

        Log.i("INFO", name.toString()+species.toString()+purchaseDate+wateringDate)

        val db = this.writableDatabase
        val result = db.insert(TABLE_NAME, null, values).toInt()
        db.close()
        return result
    }

    fun deletePlant(position: Int){
        val db = this.writableDatabase
        //Log.i("INFO", position.toString())
        //Log.i("INFO", (position+1).toString())
        db.execSQL("DELETE FROM " +TABLE_NAME+ " WHERE "+ ID_COL+"="+(position+1)+";")


        db.execSQL("PRAGMA foreign_keys=off;")
        db.execSQL("BEGIN TRANSACTION;")
        db.execSQL("ALTER TABLE "+TABLE_NAME+ " RENAME TO "+ TABLE_NAME_OLD+";")
        val query1 = ("CREATE TABLE " + TABLE_NAME + " ("
                + NAME_COl + " TEXT, " +
                SPECIES_COL + " INTEGER, " +
                PURCHASEDATE_COL + " TEXT, " +
                WATERINGDATE_COL + " TEXT" + ")")
        db.execSQL(query1)
        db.execSQL("INSERT INTO " +TABLE_NAME+" SELECT "+ NAME_COl+", "+ SPECIES_COL+", "+ PURCHASEDATE_COL+", "+ WATERINGDATE_COL+" FROM "+TABLE_NAME_OLD+";")
        db.execSQL("COMMIT;")
        db.execSQL("PRAGMA foreign_keys=on;")
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_OLD)

        //db.execSQL("ALTER TABLE "+ TABLE_NAME+" ADD "+ ID_COL+" INTEGER PRIMARY KEY AUTOINCREMENT;")

        db.execSQL("PRAGMA foreign_keys=off;")
        db.execSQL("BEGIN TRANSACTION;")
        db.execSQL("ALTER TABLE "+TABLE_NAME+ " RENAME TO "+ TABLE_NAME_OLD+";")
        val query2 = ("CREATE TABLE " + TABLE_NAME + " (" +
                ID_COL + " INTEGER PRIMARY KEY, " +
                NAME_COl + " TEXT, " +
                SPECIES_COL + " INTEGER, " +
                PURCHASEDATE_COL + " TEXT, " +
                WATERINGDATE_COL + " TEXT)")
        db.execSQL(query2)
        db.execSQL("INSERT INTO " +TABLE_NAME+" ("+ NAME_COl+", "+ SPECIES_COL+" , "+ PURCHASEDATE_COL+", "+ WATERINGDATE_COL+") SELECT "+NAME_COl+", "+ SPECIES_COL+" , "+ PURCHASEDATE_COL+", "+ WATERINGDATE_COL+ " FROM "+TABLE_NAME_OLD+"")
        db.execSQL("COMMIT;")
        db.execSQL("PRAGMA foreign_keys=on;")
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_OLD)


        db.close()
    }

    fun getName(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null)

    }

    companion object {
        private val DATABASE_NAME = "PlantLibrary.db"
        private val DATABASE_VERSION = 1
        val TABLE_NAME = "myLibrary"
        val TABLE_NAME_OLD = "myLibraryOld"

        val ID_COL = "_id"
        val NAME_COl = "_name"
        val SPECIES_COL = "_species"
        val PURCHASEDATE_COL = "_purchasedate"
        val WATERINGDATE_COL = "_wateringdate"
    }
}
