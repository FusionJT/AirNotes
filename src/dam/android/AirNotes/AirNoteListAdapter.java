package dam.android.AirNotes;

import java.util.GregorianCalendar;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class AirNoteListAdapter extends SimpleCursorAdapter {
	
	Cursor cursor;
	Context context;
	
	public AirNoteListAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		this.context = context;
		this.cursor = c;
	} //Creation Method

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		int day = new Integer(cursor.getString(cursor.getColumnIndex(AirNotesDbAdapter.KEY_DAY))).intValue();
		int month = new Integer(cursor.getString(cursor.getColumnIndex(AirNotesDbAdapter.KEY_MONTH))).intValue();
		int year = new Integer(cursor.getString(cursor.getColumnIndex(AirNotesDbAdapter.KEY_YEAR))).intValue();
		GregorianCalendar eventCalendar = new GregorianCalendar();
		GregorianCalendar currentCalendar = new GregorianCalendar();
		eventCalendar.set(year, month, day);
		if (comparadorFechas(eventCalendar, currentCalendar)==(-1)){
			view.setBackgroundColor(Color.rgb(125, 0, 0));
		} else {
			view.setBackgroundColor(Color.BLACK);
		} //if
		TextView monthField = (TextView) view.findViewById(R.id.textMonth);
		month++;
		monthField.setText(String.valueOf(month));
		ImageView countryFlag = (ImageView) view.findViewById(R.id.flagCountry);
		int flagResourceId = 0;
		String countryCode = cursor.getString(cursor.getColumnIndexOrThrow(AirNotesDbAdapter.KEY_COUNTRY_CODE));
		try{
		flagResourceId = context.getResources().getIdentifier(countryCode.toLowerCase(), "drawable", "dam.android.AirNotes");			
		}catch (Exception ex){	
		} //try			
		countryFlag.setImageResource(flagResourceId);
		if (day < 10){
			TextView dayText = (TextView) view.findViewById(R.id.textDay);
			dayText.setText("0"+day);
		} //if
		if (month<10){
			TextView monthText = (TextView) view.findViewById(R.id.textMonth);
			monthText.setText("0"+month);
		} //if
		return view;
	} //getView

	public int comparadorFechas(GregorianCalendar cal1, GregorianCalendar cal2){
		// Sólo se comparan los días meses y años, de ahí que no se use el método de la 
		// clase gregorian Calendar
		// Si cal1 > cal2 return 1
		// Si cal1 = cal2 return 0
		// Si cal1 < cal2 return -1
		if (cal1.get(GregorianCalendar.YEAR)>(cal2.get(GregorianCalendar.YEAR))){
			return 1;
		} else if (cal1.get(GregorianCalendar.YEAR)<(cal2.get(GregorianCalendar.YEAR))){
			return -1;
		} else {
			if (cal1.get(GregorianCalendar.MONTH)>cal2.get(GregorianCalendar.MONTH)){
				return 1;
			} else if (cal1.get(GregorianCalendar.MONTH)<cal2.get(GregorianCalendar.MONTH)){
				return -1;
			} else {
				if (cal1.get(GregorianCalendar.DAY_OF_MONTH)> cal2.get(GregorianCalendar.DAY_OF_MONTH)){
					return 1;
				} else if (cal1.get(GregorianCalendar.DAY_OF_MONTH)<cal2.get(GregorianCalendar.DAY_OF_MONTH)){
					return -1;
				} else {
					return 0;
				}//if
			} //if
		} //if
	} //comparadorFechas
	
	
} //class AirNoteAdapter
