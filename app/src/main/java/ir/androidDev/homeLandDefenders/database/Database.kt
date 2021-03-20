package ir.androidDev.homeLandDefenders.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class Database(context: Context?) : SQLiteOpenHelper(context, "hld.db", null, 1) {
	
	/**
	 * the class fields
	 */
	companion object {
		const val TBL_BIOGRAPHY = "biography"
		const val TBL_TESTAMENT = "testament"
		const val TBL_OPERATION = "operation"
		const val TBL_SCROLL = "scroll"
		const val TBL_SETTINGS = "settings"
		
		const val WAR_HISTORY = "war_history"
	}
	
	/**
	 * create database and tables
	 */
	override fun onCreate(db: SQLiteDatabase?) {
		/* create 3 main tables */
		db!!.execSQL("CREATE TABLE $TBL_BIOGRAPHY (id INTEGER PRIMARY KEY, name VARCHAR(50), birth VARCHAR(4), death VARCHAR(4), rank VARCHAR(30), age VARCHAR(2), img_url VARCHAR(500), inner_img_url VARCHAR(500), text TEXT)")
		db.execSQL("CREATE TABLE $TBL_TESTAMENT (id INTEGER PRIMARY KEY, name VARCHAR(50),img_url VARCHAR(500), inner_img_url VARCHAR(500), text TEXT)")
		db.execSQL("CREATE TABLE $TBL_OPERATION (id INTEGER PRIMARY KEY, name VARCHAR(50),img_url VARCHAR(500), inner_img_url VARCHAR(500), text TEXT)")
		
		/* create scroll position saves table */
		db.execSQL("CREATE TABLE $TBL_SCROLL (name VARCHAR(30) PRIMARY KEY, ext_id INTEGER, scroll INTEGER)")
		
		/* create app settings table */
		db.execSQL("CREATE TABLE $TBL_SETTINGS (name VARCHAR(30) PRIMARY KEY, value VARCHAR(30))")
		
		/* create some table rows */
		createPreDefinedRows(db)
	}
	
	/**
	 * upgrades database
	 */
	override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
		db!!.execSQL("DROP TABLE IF EXISTS $TBL_BIOGRAPHY")
		db.execSQL("DROP TABLE IF EXISTS $TBL_TESTAMENT")
		db.execSQL("DROP TABLE IF EXISTS $TBL_OPERATION")
		db.execSQL("DROP TABLE IF EXISTS $TBL_SCROLL")
		db.execSQL("DROP TABLE IF EXISTS $TBL_SETTINGS")
		onCreate(db)
	}
	
	/**
	 * create rows for pre defined data
	 */
	private fun createPreDefinedRows(db: SQLiteDatabase?) {
		/* scroll saves */
		val cv = ContentValues()
		
		val names = arrayOf(TBL_BIOGRAPHY, TBL_TESTAMENT, TBL_OPERATION, WAR_HISTORY)
		
		names.forEach {
			cv.put("name", it)
			cv.put("ext_id", null as Int?)
			cv.put("scroll", null as Int?)
			
			db!!.insert(TBL_SCROLL, null, cv)
		}
		
		/* settings saves */
		cv.clear()
		
		val settingsRows = arrayOf("nick_name", "font_family", "font_size")
		
		settingsRows.forEach {
			cv.put("name", it)
			cv.put("value", null as String?)
			
			db!!.insert(TBL_SETTINGS, null, cv)
		}
		
		cv.clear()
		cv.put("name", "auto_download")
		cv.put("value", "1")
		db!!.insert(TBL_SETTINGS, null, cv)
	}
	
	/**
	 * insert data to database
	 *
	 * @param tName the name of the table
	 * @param cv the values to insert same as table columns
	 */
	fun insert(tName: String, cv: ContentValues) {
		val db: SQLiteDatabase = this.writableDatabase
		db.insert(tName, null, cv)
	}
	
	/**
	 * updates table by parameters given
	 *
	 * @param tName the table name
	 * @param cv the column names and new values
	 * @param where defines where to apply changes
	 * @param equalTo defines the value of [where]
	 */
	fun updateBy(tName: String, cv: ContentValues, where: String, equalTo: Any) {
		val db: SQLiteDatabase = this.writableDatabase
		db.update(tName, cv, "$where='$equalTo'", null)
	}
	
	/**
	 * deletes all records in the selected table
	 *
	 * @param tName the name of the table
	 */
	fun clearTable(tName: String) {
		val db: SQLiteDatabase = writableDatabase
		db.execSQL("DELETE FROM $tName")
	}
	
	/**
	 * resets all the records of all rows for the column "value" and sets it to null
	 * *** except the war history scroll position value ***
	 */
	fun resetColumnValues(tName: String, column: String) {
		val db: SQLiteDatabase = writableDatabase
		
		val cv = ContentValues()
		cv.put(column, null as String?)
		
		db.update(tName, cv, "NOT name='${WAR_HISTORY}'", null)
	}
	
	/**
	 * returns all the rows in the selected table
	 *
	 * @param tName the name of the table
	 */
	fun getData(tName: String): Cursor {
		val db: SQLiteDatabase = this.readableDatabase
		return db.rawQuery("SELECT * FROM $tName", null)
	}
	
	/**
	 * returns the number of rows in the selected table
	 *
	 * @param tName the name of the table
	 */
	fun getItemsCount(tName: String): Int {
		val db: SQLiteDatabase = this.readableDatabase
		val cursor = db.rawQuery("SELECT COUNT(*) FROM $tName", null)
		cursor.moveToNext()
		val count = cursor.getInt(0)
		cursor.close()
		return count
	}
	
	/**
	 * returns array of ids that exist in the selected table
	 *
	 * @param tName the name of the table
	 */
	fun getArrayOfIds(tName: String): MutableList<Int> {
		val db: SQLiteDatabase = readableDatabase
		
		val cursor = db.rawQuery("SELECT id FROM $tName", null)
		
		val arr: MutableList<Int> = ArrayList()
		
		while (cursor.moveToNext()) {
			arr.add(cursor.getInt(0))
		}
		cursor.close()
		
		return arr
	}
	
	/**
	 * returns the row in the selected table that matches the id given
	 *
	 * @param tName the table name
	 * @param id the id of row in the table
	 */
	fun getRowById(tName: String, id: Int): Cursor {
		val db: SQLiteDatabase = this.readableDatabase
		val cursor = db.rawQuery("SELECT * FROM $tName WHERE id=$id", null)
		cursor.moveToNext()
		return cursor
	}
	
	/**
	 * returns the row in the database by given [where] clause
	 *
	 * @param tName the table name
	 * @param where where to get row by
	 * @param equalTo the value of [where]
	 */
	fun getRowBy(tName: String, where: String, equalTo: Any): Cursor {
		val db = this.readableDatabase
		val cursor = db.rawQuery("SELECT * FROM $tName WHERE $where='$equalTo'", null)
		cursor.moveToNext()
		return cursor
	}
}