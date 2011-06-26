package dam.android.AirNotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AirNotesDbAdapter {

	public static final String KEY_CODE = "code";
	public static final String KEY_COUNTRY = "country";
	public static final String KEY_COUNTRY_CODE = "country_code";
	public static final String KEY_DAY = "day";
	public static final String KEY_MONTH = "month";
	public static final String KEY_YEAR = "year";
	public static final String KEY_NOTES = "notes";
	public static final String KEY_ROWID = "_id";

	private static final String TAG = "AirNotesDbAdapter";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	private static final String DATABASE_CREATE = 
			"create table airnotes (_id integer primary key autoincrement, "
			+ "code text not null, country text not null, country_code text not null,"
			+ "day integer not null, month integer not null, year integer not null, notes text not null);";
	
	
	private static final String DATABASE_NAME = "data";
	private static final String DATABASE_TABLE = "airnotes";
	private static final int DATABASE_VERSION = 2;

	private final Context mCtx;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		

		
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);		
		} // creationMethod DatabaseHelper

		@Override
		public void onCreate(SQLiteDatabase db) {
			
			db.execSQL(DATABASE_CREATE);
		} // onCreate

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS notes");
			onCreate(db);
		} // onUpgrade
	} // class DataBaseHelper

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx
	 *            the Context within which to work
	 */
	public AirNotesDbAdapter(Context ctx) {
		this.mCtx = ctx;
	} // creationMethod

	/**
	 * Open the airnotes database. If it cannot be opened, try to create a new
	 * instance of the database. If it cannot be created, throw an exception to
	 * signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException
	 *             if the database could be neither opened or created
	 */
	public AirNotesDbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	/**
	 * Create a new AirNote using the code, the country, the date and the notes
	 * about the airport. If the note is successfully created return the new
	 * rowId for that note, otherwise return a -1 to indicate failure.
	 * 
	 * @param code
	 *            the code of the airport
	 * @param country
	 *            the country of the airport
	 * @param day
	 *            the day of the visit
	 * @param month
	 *            the month of the visit
	 * @param year
	 *            the year of the visit
	 * @param notes
	 *            the information about the visit
	 * @return rowId or -1 if failed
	 */
	public long createAirNote(String code, String country, 
			String countryCode, int day,
			int month, int year, String notes) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_CODE, code);
		initialValues.put(KEY_COUNTRY, country);
		initialValues.put(KEY_COUNTRY_CODE, countryCode);
		initialValues.put(KEY_DAY, day);
		initialValues.put(KEY_MONTH, month);
		initialValues.put(KEY_YEAR, year);
		initialValues.put(KEY_NOTES, notes);

		return mDb.insert(DATABASE_TABLE, null, initialValues);
	}

	/**
	 * Delete the AirNote with the given rowId
	 * 
	 * @param rowId
	 *            id of AirNote to delete
	 * @return true if deleted, false otherwise
	 */
	public boolean deleteAirNote(long rowId) {

		return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}

	/**
	 * Return a Cursor over the list of all AirNotes in the database
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor fetchAllNotes() {
		return mDb.query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_CODE,
				KEY_COUNTRY, KEY_COUNTRY_CODE, KEY_DAY, KEY_MONTH, KEY_YEAR, KEY_NOTES }, null, 
				null, null, null, KEY_YEAR + " DESC, "
				+ KEY_MONTH + " DESC, " +  KEY_DAY + " DESC," + KEY_CODE);
	} //fetchAllNotes
	
	public Cursor fetchAllNotesByCountry(){
		return mDb.query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_COUNTRY_CODE, "count()"}, null,
				null, KEY_COUNTRY_CODE, null, "count() DESC");
	} //fetchAllNotesByCountry

	/**
	 * Return a Cursor positioned at the AirNote that matches the given rowId
	 * 
	 * @param rowId
	 *            id of AirNote to retrieve
	 * @return Cursor positioned to matching AirNote, if found
	 * @throws SQLException
	 *             if AirNote could not be found/retrieved
	 */
	public Cursor fetchAirNote(long rowId) throws SQLException {

		Cursor mCursor =

		mDb.query(true, DATABASE_TABLE, new String[] { KEY_ROWID, KEY_CODE,
				KEY_COUNTRY,KEY_COUNTRY_CODE, KEY_DAY, KEY_MONTH, KEY_YEAR, KEY_NOTES },
				KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	/**
	 * Update the AirNote using the details provided. The AirNote to be updated
	 * is specified using the rowId, and it is altered to use the code, the
	 * country the date of the visit and the notes about the airport values
	 * passed in
	 * 
	 * @param rowId
	 *            id of note to update
	 * @param code
	 *            value to set AirNote code to
	 * @param country
	 *            value to set AirNote country to
	 * @param day
	 *            value to set AirNote day to
	 * @param month
	 *            value to set AirNote month to
	 * @param year
	 *            value to set AirNote year to
	 * @param notes
	 *            value to set AirNotes notes to
	 * @return true if the AirNote was successfully updated, false otherwise
	 */
	public boolean updateAirNote(long rowId, String code, String country,
			String countryCode, int day, int month, int year, String notes) {
		ContentValues args = new ContentValues();
		args.put(KEY_CODE, code);
		args.put(KEY_COUNTRY, country);
		args.put(KEY_COUNTRY_CODE, countryCode);
		args.put(KEY_DAY, day);
		args.put(KEY_MONTH, month);
		args.put(KEY_YEAR, year);
		args.put(KEY_NOTES, notes);

		return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}
}
