package com.example.customapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull


class DatabaseHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {
    val context = context.applicationContext

    override fun onCreate(db: SQLiteDatabase) {
        //TABLE PLANTS
        val query = ("CREATE TABLE " + TABLE_NAME + " ("+
                NAME_COl + " TEXT, " +
                SPECIES_COL + " INTEGER, " +
                PURCHASEDATE_COL + " TEXT, " +
                WATERINGDATE_COL + " TEXT, "
                +ID_COL + " INTEGER PRIMARY KEY" + ")")
        db.execSQL(query)

        //TABLE SPECIES
        val query2 = ("CREATE TABLE " + TABLE_SPECIES + " ('_species' TEXT, '_infoText' TEXT, '_deltaWater' INTEGER);")
        db.execSQL(query2)

        val tableName = TABLE_SPECIES
        val columns = "_species, _infoText, _deltaWater"
        val str1 = "INSERT INTO $tableName ($columns) values("
        val str2 = ");"

        val reader = context.resources.openRawResource(R.raw.speciesinfo).bufferedReader()
        reader.readLine()
        reader.forEachLine {
            val temp = it.split(";")
            val sb = StringBuilder(str1)
            for (i in 0..2){
                sb.append("'" + temp[i] + "', ")
            }
            sb.deleteCharAt(sb.length-1)
            sb.deleteCharAt(sb.length-1)
            sb.append(str2)
            db.execSQL(sb.toString())
        }
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

        val db = this.writableDatabase
        val result = db.insert(TABLE_NAME, null, values).toInt()
        db.close()
        return result
    }

    fun updatePlant(position: Int, name: String, species: Int, purchaseDate: String, wateringDate: String): Int {
        val values = ContentValues()

        values.put(NAME_COl, name)
        values.put(SPECIES_COL, species)
        values.put(PURCHASEDATE_COL, purchaseDate)
        values.put(WATERINGDATE_COL, wateringDate)

        val whereclause = "$ID_COL=?"
        val whereargs = arrayOf((position+1).toString())
        return this.writableDatabase.update(TABLE_NAME, values, whereclause, whereargs)

    }

    fun deletePlant(position: Int){
        val db = this.writableDatabase
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

    fun getNextWateringDate(species:String): Int? {
        val db = this.readableDatabase
        val args = listOf<String>(species.toString()).toTypedArray()
        val query = "SELECT * FROM " + TABLE_SPECIES + " WHERE _species = ? ;"
        db.rawQuery(query, args).use {
            if (it.moveToFirst()) {
                val result = it.getIntOrNull(it.getColumnIndex("_deltaWater"))
                return result
            }
        }
        return null
    }

    fun getInfo(species:String): String? {
        val db = this.readableDatabase
        val args = listOf<String>(species.toString()).toTypedArray()
        val query = "SELECT * FROM " + TABLE_SPECIES + " WHERE _species = ? ;"
        db.rawQuery(query, args).use {
            if (it.moveToFirst()) {
                val result = it.getStringOrNull(it.getColumnIndex("_infoText"))
                return result
            }
        }
        return null
    }

    companion object {
        private val DATABASE_NAME = "PlantLibrary.db"
        private val DATABASE_VERSION = 1
        val TABLE_NAME = "myLibrary"
        val TABLE_NAME_OLD = "myLibraryOld"
        val TABLE_SPECIES = "mySpeciesLibrary"

        val ID_COL = "_id"
        val NAME_COl = "_name"
        val SPECIES_COL = "_species"
        val PURCHASEDATE_COL = "_purchasedate"
        val WATERINGDATE_COL = "_wateringdate"
    }
}
