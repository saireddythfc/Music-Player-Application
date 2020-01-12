package com.example.saireddy.music.databases

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.saireddy.music.Songs
import com.example.saireddy.music.databases.EchoDatabase.Staticated.DB_VERSION

class EchoDatabase: SQLiteOpenHelper{
    var _songList= ArrayList<Songs>()



    object Staticated {
        val DB_NAME= "FavoriteDatabase"
        var DB_VERSION = 1
        val TABLE_NAME = "FavoriteTable"
        val COLUMN_ID = "SongID"
        val COLUMN_SONG_TITLE = "SongTitle"
        val COLUMN_SONG_ARTIST = "SongArtist"
        val COLUMN_SONG_PATH = "SongPath"
    }

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + Staticated.TABLE_NAME +  "( " + Staticated.COLUMN_ID + " INTEGER," + Staticated.COLUMN_SONG_ARTIST + " STRING,"
                + Staticated.COLUMN_SONG_TITLE + " STRING," + Staticated.COLUMN_SONG_PATH + " STRING);")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    constructor(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : super(context, name, factory, version)
    constructor(context: Context?) : super(context, Staticated.DB_NAME, null, Staticated.DB_VERSION)

    fun storeAsFavorite(id: Int?, artist: String?, songTitle: String?, path: String?){
        val db= this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(Staticated.COLUMN_ID,id)
        contentValues.put(Staticated.COLUMN_SONG_ARTIST, artist)
        contentValues.put(Staticated.COLUMN_SONG_TITLE, songTitle)
        contentValues.put(Staticated.COLUMN_SONG_PATH, path)
        db.insert(Staticated.TABLE_NAME,null,contentValues)
        db.close()

    }

    fun queryDBList(): ArrayList<Songs>?{
        try{
            val db= this.readableDatabase
            val query_params= "SELECT * FROM" + Staticated.TABLE_NAME
            var cSor = db.rawQuery(query_params, null)
            if(cSor.moveToFirst()){
                do{
                    var _id= cSor.getInt(cSor.getColumnIndexOrThrow(Staticated.COLUMN_ID))
                    var _artist= cSor.getString(cSor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_ARTIST))
                    var _title= cSor.getString(cSor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_TITLE))
                    var  _songPath= cSor.getString(cSor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_PATH))
                    _songList.add(Songs(_id.toLong(), _title, _artist, _songPath, 0))
                }while(cSor.moveToNext())
            }
            else{
                return null
            }
        }
        catch (e: Exception){
            e.printStackTrace()
        }
        return _songList
    }

    fun checkifIdExists(_id: Int): Boolean {
/*Random id which does not exist
* We know that this id can never exist as the song id cannot be less than 0*/
        var storeId = -1090
        val db = this.readableDatabase
/*The query for checking the if id is present or not is
* SELECT * FROM FavoriteTable WHERE SongID = <id_of_our_song>*/
        val query_params = "SELECT * FROM " + Staticated.TABLE_NAME + " WHERE SongID = '$_id'"
        val cSor = db.rawQuery(query_params, null)
        if (cSor.moveToFirst()) {
            do {
/*Storing the song id into the variable storeId*/
                storeId = cSor.getInt(cSor.getColumnIndexOrThrow(Staticated.COLUMN_ID))
            } while (cSor.moveToNext())
        } else {
            return false
        }
        return storeId != -1090
    }
    /*This function is used to delete the songs from the favorite if the user the user
    removes any song from the favorite list*/
    fun deleteFavourite(_id: Int) {
        val db = this.writableDatabase
/*The delete query is used to perform the delete function*/
        db.delete(Staticated.TABLE_NAME, Staticated.COLUMN_ID + " = " + _id, null)
/*Here is also we close the database connection
* Note that we only close the database whenever we open in writable mode*/
        db.close()
    }
    fun checkSize(): Int{
        var counter=0
        val db = this.readableDatabase

        val query_params = "SELECT * FROM " + Staticated.TABLE_NAME
        val cSor = db.rawQuery(query_params, null)
        if (cSor.moveToFirst()) {
            do {
                counter += 1
            } while (cSor.moveToNext())
        } else {
            return 0
        }
        return counter
    }
}
