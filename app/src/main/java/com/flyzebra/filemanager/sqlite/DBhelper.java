package com.flyzebra.filemanager.sqlite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBhelper extends SQLiteOpenHelper {
	public DBhelper(Context context) {
		super(context, DB_NAME, null, 1);
	}

	private static final String DB_NAME = "filemanager.db";
	private static final String TABLE_HOME = "homeitem";
	private static final String CREATE_TABLE_HOME = "create table homeitem(NAME text primary key, PATH text, MARK integer, USER text , PASS text)";
	private SQLiteDatabase db;

	@Override
	public void onCreate(SQLiteDatabase db) {
		this.db = db;
		db.execSQL(CREATE_TABLE_HOME);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	// 列出整个表的数据
	public List<HashMap<String, Object>> QueryAll() {
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		SQLiteDatabase db = getWritableDatabase();
		Cursor c = db.query(TABLE_HOME, null, null, null, null, null, null);
		while (c.moveToNext()) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("NAME", c.getString(c.getColumnIndex("NAME")));
			map.put("PATH", c.getString(c.getColumnIndex("PATH")));
			map.put("USER", c.getString(c.getColumnIndex("USER")));
			map.put("PASS", c.getString(c.getColumnIndex("PASS")));
			map.put("MARK", c.getInt(c.getColumnIndex("MARK")));
			list.add(map);
		}
		c.close();
		return list;
	}

	//插入一条记录
	public boolean Insert(ContentValues value) {
		SQLiteDatabase db = getWritableDatabase();
		db.insert(TABLE_HOME, null, value);
		db.close();
		return true;
	}

	// 更新指定记录
	public int Update(ContentValues cv, String Clause, String[] Args) {
		SQLiteDatabase db = getWritableDatabase();
		return db.update(TABLE_HOME, cv, Clause, Args);
	}

	// 删除一条记录
	public int DeleteOne(String path) {
		SQLiteDatabase db = getWritableDatabase();
		return db.delete(TABLE_HOME, "PATH=?", new String[] { path });
	}

	// 关闭数据库
	public void close() {
		if (db != null) {
			db.close();
		}
	}

}
