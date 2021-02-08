package org.nibbler.zoe.liteplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class MusicDBConnection 
{
	public static final String KEY_ROWID = "musicid";
	public static final String KEY_NAME = "music_name";	
	public static final String KEY_PATH = "music_path";
	public static final String KEY_ARTIST = "music_artist";
	public static final String KEY_ALBUM = "music_album";	
	public static final String KEY_LENGTH = "music_length";	

	private static final String DATABASE_NAME = "music_db";
	private static final String DATABASE_TABLE = "pathsTable";
	private static final int DATABASE_VERSION = 1;	
	
	private DbHelper helper;
	private Context context;
	private SQLiteDatabase database;
	
	public MusicDBConnection(Context context)
	{
		this.context = context;
	}
	
	public MusicDBConnection open()
	{
		this.helper = new DbHelper(this.context);
		this.database = this.helper.getWritableDatabase();
		return this;
	}
	
	public void close()
	{
		this.helper.close();
	}
	
	public void dbOverwrite()
	{
		this.helper.onRun(this.database);
	}
	
	public long createEntry(String id, String name, String path, String artist, String album, String length) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_ROWID, id);
		cv.put(KEY_NAME, name);
		cv.put(KEY_PATH, path);
		cv.put(KEY_ARTIST, artist);
		cv.put(KEY_ALBUM, album);
		cv.put(KEY_LENGTH, length);
		return database.insert(DATABASE_TABLE, null, cv);
	}
	
	private static class DbHelper extends SQLiteOpenHelper
	{

		public DbHelper(Context context)
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" +
					KEY_ROWID + " INTEGER PRIMARY KEY, " +
					KEY_NAME + " TEXT NOT NULL, " +
					KEY_PATH + " TEXT NOT NULL, " +
					KEY_ARTIST + " TEXT NOT NULL, " +
					KEY_ALBUM + " TEXT NOT NULL, " +
					KEY_LENGTH + " INTEGER NOT NULL);"
					);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DELETE FROM " + DATABASE_NAME);
			onCreate(db);
		}
		
		@Override
	    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DELETE FROM " + DATABASE_NAME);
			onCreate(db);
	    }

		public void onRun(SQLiteDatabase db){
			db.execSQL("DELETE FROM " + DATABASE_TABLE);
		}
	}

	public String getData() 
	{
		String[] columns = new String[] { KEY_ROWID, KEY_NAME, KEY_PATH, KEY_ARTIST, KEY_ALBUM, KEY_LENGTH };
		Cursor c = database.query(DATABASE_TABLE, columns, null, null, null, null, null);
		String result = "";
		int iRowID = c.getColumnIndex(KEY_ROWID);
		int iRowPath = c.getColumnIndex(KEY_PATH);
		
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
		{
			result = result + c.getString(iRowID) + " " + c.getString(iRowPath) + "\n";
		}
		
		return result;
	}

	public String getRowByID(String id) {
		String[] columns = new String[] { KEY_ROWID, KEY_NAME, KEY_PATH, KEY_ARTIST, KEY_ALBUM, KEY_LENGTH };
		Cursor c = database.query(DATABASE_TABLE, columns, KEY_ROWID + " = " + id, null, null, null, null);
		String result = "";
		int iRowID = c.getColumnIndex(KEY_ROWID);
		int iRowPath = c.getColumnIndex(KEY_PATH);
		int iRowName = c.getColumnIndex(KEY_NAME);
		
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
		{
			result = result + c.getString(iRowPath);
		}
		
		return result;
	}
	
	public String getMusicName(String id) {
		String[] columns = new String[] { KEY_ROWID, KEY_NAME, KEY_PATH, KEY_ARTIST, KEY_ALBUM, KEY_LENGTH };
		Cursor c = database.query(DATABASE_TABLE, columns, KEY_ROWID + " = " + id, null, null, null, null);
		String result = "";
		int iRowID = c.getColumnIndex(KEY_ROWID);
		int iRowPath = c.getColumnIndex(KEY_PATH);
		int iRowName = c.getColumnIndex(KEY_NAME);
		
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
		{
			result = result + c.getString(iRowName);
		}
		
		return result;
	}

	public int getMusicLength(String id) {
		String[] columns = new String[] { KEY_ROWID, KEY_NAME, KEY_PATH, KEY_ARTIST, KEY_ALBUM, KEY_LENGTH };
		Cursor c = database.query(DATABASE_TABLE, columns, KEY_ROWID + " = " + id, null, null, null, null);
		int result = 0;
		int iRowLength = c.getColumnIndex(KEY_LENGTH);
		
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
		{
			result = result + c.getInt(iRowLength);
		}
		
		return result;
	}
}