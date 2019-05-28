package info.androidhive.loginandregistration.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

	private static final String TAG = SQLiteHandler.class.getSimpleName();

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 20;

	// Database Name
	private static final String DATABASE_NAME = "wastemanagement.db";

	// Login table name
	private static final String TABLE_USER = "tbl_apartment";

	// Login Table Columns names
	private static final String ID = "id";
	private static final String NAME = "name";
	private static final String EMAIL = "email";
	private static final String APARTMENTNAME ="apartmentname";
	private static final String APARTADDRESS ="apart_address";
	private static final String AVGWEIGHT = "avg_weight";
	private static final String LANDMARK = "landmark";
	private static final String NOFLATS = "noflats";
    private static final String FREQUENCY = "frequency";
    //private static final String TIME = "time";
	private static final String UID = "uid";
	private static final String CREATED_AT = "created_at";

	public SQLiteHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
				+ ID + " INTEGER PRIMARY KEY," + NAME + " TEXT,"
				+ APARTMENTNAME + "TEXT," + APARTADDRESS +"TEXT,"
                + FREQUENCY + "INTEGER,"
				+ AVGWEIGHT + "INTEGER," + EMAIL + " TEXT UNIQUE,"
				+ NOFLATS + "TEXT,"
				+ UID + " TEXT," + LANDMARK + "TEXT,"
				+ CREATED_AT + " TEXT" + ")";
		db.execSQL(CREATE_LOGIN_TABLE);

		Log.d(TAG, "Database tables created");
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

		// Create tables again
		onCreate(db);
	}

	/**
	 * Storing user details in database
	 * */
	public void addUser(String name, String apartmentname, String apart_address, String avg_weight,
						String email, String landmark, String noflats, String frequency,
						String uid, String created_at) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(NAME, name); // Name
		values.put(EMAIL, email); // Email
		values.put(UID, uid); // id
		values.put(APARTMENTNAME, apartmentname); //Apartment Name
        values.put(APARTADDRESS, apart_address); //Apartment Address
		values.put(AVGWEIGHT, avg_weight); //Apartment Average Weight
        values.put(LANDMARK, landmark); //Apartment Landmark
		values.put(NOFLATS, noflats); //Number Of Flats
        values.put(FREQUENCY, frequency); //Frequency of Collection of Waste
		//values.put(TIME, time); // Time for Waste Collection
		values.put(CREATED_AT, created_at); // Created At

		// Inserting Row
		long id = db.insert(TABLE_USER, null, values);
		db.close(); // Closing database connection

		Log.d(TAG, "New user inserted into sqlite: " + id);
	}

	/**
	 * Getting user data from database
	 * */
	public HashMap<String, String> getUserDetails() {
		HashMap<String, String> user = new HashMap<String, String>();
		String selectQuery = "SELECT  * FROM " + TABLE_USER;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			user.put("name", cursor.getString(1));
			user.put("email", cursor.getString(2));
			user.put("apartmentname", cursor.getString(3));
			user.put("apart_address", cursor.getString(4));
			user.put("avg_weight", cursor.getString(5));
			user.put("landmark", cursor.getString(6));
			user.put("noflats", cursor.getString(7));
			user.put("frequency", cursor.getString(8));
			user.put("uid", cursor.getString(9));
			//user.put("time", cursor.getString(10));
			user.put("created_at", cursor.getString(11));
		}
		cursor.close();
		db.close();
		// return user
		Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

		return user;
	}

	/**
	 * Re crate database Delete all tables and create them again
	 * */
	public void deleteUsers() {
		SQLiteDatabase db = this.getWritableDatabase();
		// Delete All Rows
		db.delete(TABLE_USER, null, null);
		db.close();

		Log.d(TAG, "Deleted all user info from sqlite");
	}
}
