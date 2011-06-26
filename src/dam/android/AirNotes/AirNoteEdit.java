package dam.android.AirNotes;

import java.util.Calendar;

import dam.android.AirNotes.R.drawable;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class AirNoteEdit extends Activity {

	private EditText mCodeText;
	private TextView mCountryText;
	private String mCountryCode;
	private TextView mDateText;
	private EditText mNotesText;
	private int mDay;
	private int mMonth;
	private int mYear;
	private Long mRowId;
	private AirNotesDbAdapter mDbHelper;
	private AlertDialog.Builder alertBuilder;
	private Airport mAirport;
	private ImageView mCountryFlag;
	private int mFlagResourceId;

	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int month, int day) {
			mYear = year;
			mDay = day;
			mMonth = month;
			updateDateText();
		} // onDateSet
	}; // DatePickerDialog.OnDateSetListener

	static final int DATE_DIALOG = 0;
	static final int ALERT_DIALOG_CODE = 1;
	static final int ALERT_DIALOG_ERROR = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setTitle("AirNotes - Vista de Edición");
		alertBuilder = new AlertDialog.Builder(this);

		mDbHelper = new AirNotesDbAdapter(this);
		mDbHelper.open();

		setContentView(R.layout.airnotes_edit);

		mCodeText = (EditText) findViewById(R.id.airport_code);
		mCountryText = (TextView) findViewById(R.id.airport_country);
		mDateText = (TextView) findViewById(R.id.airport_date);
		mNotesText = (EditText) findViewById(R.id.airport_notes);
		mCountryFlag = (ImageView) findViewById(R.id.country_flag);

		Button confirmButton = (Button) findViewById(R.id.confirm);
		Button dateButton = (Button) findViewById(R.id.airport_date_button);

		mRowId = (savedInstanceState == null) ? null
				: (Long) savedInstanceState
						.getSerializable(AirNotesDbAdapter.KEY_ROWID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras
					.getLong(AirNotesDbAdapter.KEY_ROWID) : null;
		} // if

		populateFields();

		mCodeText.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (mCodeText.getText().length() == 3) {
					AirportDataParserSax parserSax = new AirportDataParserSax(
							mCodeText.getText().toString());
					mAirport = parserSax.parse();
					mCountryText.setText(mAirport.getCountryName());
					mCountryCode = mAirport.getCountryCode();
					mFlagResourceId = 0;
					try{
					mFlagResourceId = getResources().getIdentifier(mCountryCode.toLowerCase(), "drawable", "dam.android.AirNotes");			
					}catch (Exception ex){	
					} //try					
				} //if
				if ((mFlagResourceId == 0)||(mCodeText.getText().length()!=3)){
					mCountryFlag.setImageResource(R.drawable.unknown);
					mCountryText.setText("Desconocido");
				} else {
					mCountryFlag.setImageResource(mFlagResourceId);
				} //If
				return false;
			} //onKey
		});
		

		confirmButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				mCodeText.setText(mCodeText.getText().toString().toUpperCase());
				if (mCodeText.getText().length() != 3) {
					showDialog(ALERT_DIALOG_CODE);
				} else if (mFlagResourceId == 0) {
					showDialog(ALERT_DIALOG_ERROR);
				} else {
					setResult(RESULT_OK);
					finish();
				} // if
			} // onClick

		}); // confirmButton.setOnClickListener

		dateButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				showDialog(DATE_DIALOG);
			} // onClick
		}); // dateButton.setOnClickListener

		final Calendar calendar = Calendar.getInstance();
		mYear = calendar.get(Calendar.YEAR);
		mMonth = calendar.get(Calendar.MONTH);
		mDay = calendar.get(Calendar.DAY_OF_MONTH);

		updateDateText();

	} // onCreate

	private void populateFields() {
		if (mRowId != null) {
			Cursor note = mDbHelper.fetchAirNote(mRowId);
			startManagingCursor(note);
			mCodeText.setText(note.getString(note
					.getColumnIndexOrThrow(AirNotesDbAdapter.KEY_CODE)));
			mCountryText.setText(note.getString(note
					.getColumnIndexOrThrow(AirNotesDbAdapter.KEY_COUNTRY)));
			mCountryCode = note.getString(note.getColumnIndexOrThrow(AirNotesDbAdapter.KEY_COUNTRY_CODE));
			mFlagResourceId = getResources().getIdentifier(mCountryCode.toLowerCase(), "drawable", "dam.android.AirNotes");
			mCountryFlag.setImageResource(mFlagResourceId);
			mDay = Integer.valueOf(
					note.getString(note
							.getColumnIndexOrThrow(AirNotesDbAdapter.KEY_DAY)))
					.intValue();
			mMonth = Integer
					.valueOf(
							note.getString(note
									.getColumnIndexOrThrow(AirNotesDbAdapter.KEY_MONTH)))
					.intValue();
			mYear = Integer
					.valueOf(
							note.getString(note
									.getColumnIndexOrThrow(AirNotesDbAdapter.KEY_YEAR)))
					.intValue();
			updateDateText();
			mNotesText.setText(note.getString(note
					.getColumnIndexOrThrow(AirNotesDbAdapter.KEY_NOTES)));
		} // if
	} // populateFields

	private void saveState() {
		String code = mCodeText.getText().toString();
		String country = mCountryText.getText().toString();
		String countryCode = mCountryCode;
		int day = mDay;
		int month = mMonth;
		int year = mYear;
		String notes = mNotesText.getText().toString();

		// If mRowId == null we're creating a new note.
		// If mrowId != null we're editing a previous created note
		if ((code.length() > 0) && (mFlagResourceId != 0)) {
			if (mRowId == null) {
				long id = mDbHelper.createAirNote(code, country, countryCode,
						day, month, year, notes);
				if (id > 0) {
					mRowId = id;
				} // if
			} else {
				mDbHelper.updateAirNote(mRowId, code, country, countryCode, 
						day, month, year, notes);
			} // if
		} // if
	} // saveState

	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	} // onPause

	@Override
	protected void onResume() {
		super.onResume();
		populateFields();
	} // onResume

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putSerializable(AirNotesDbAdapter.KEY_ROWID, mRowId);
	} // onSaveInstanceState

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		case ALERT_DIALOG_CODE:
			alertBuilder.setTitle("ERROR");
			alertBuilder.setMessage("Debes introducir un código de aeropuerto IATA correcto");
			alertBuilder.setCancelable(false);
			alertBuilder.setPositiveButton("OK", null);
			alertBuilder.setIcon(R.drawable.icono_error);
			AlertDialog alertCode = alertBuilder.create();
			return alertCode;
		case ALERT_DIALOG_ERROR:
			alertBuilder.setTitle("ERROR");
			alertBuilder.setMessage("El aeropuerto introducido es desconocido o no existe");
			alertBuilder.setCancelable(false);
			alertBuilder.setPositiveButton("OK", null);
			alertBuilder.setIcon(R.drawable.icono_error);
			AlertDialog alertCountry = alertBuilder.create();
			return alertCountry;
		} // switch
		return null;
	} // onCreateDialog

	protected void updateDateText() {
		mDateText.setText("Fecha: " + Integer.valueOf(mDay) + "/"
				+ (Integer.valueOf(mMonth)+1) + "/" + Integer.valueOf(mYear));
	} // UpdateDateText

} // class NoteEdit
