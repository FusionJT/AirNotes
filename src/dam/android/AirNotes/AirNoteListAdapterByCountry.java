package dam.android.AirNotes;

import java.util.GregorianCalendar;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class AirNoteListAdapterByCountry extends SimpleCursorAdapter {
	
	Cursor cursor;
	Context context;
	
	public AirNoteListAdapterByCountry(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		this.context = context;
		this.cursor = c;
	} //Creation Method

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		ImageView countryFlag = (ImageView) view.findViewById(R.id.flagCountry);
		int flagResourceId = 0;
		String countryCode = cursor.getString(cursor.getColumnIndexOrThrow(AirNotesDbAdapter.KEY_COUNTRY_CODE));
		try{
		flagResourceId = context.getResources().getIdentifier(countryCode.toLowerCase(), "drawable", "dam.android.AirNotes");			
		}catch (Exception ex){	
		} //try			
		countryFlag.setImageResource(flagResourceId);
		return view;
	} //getView

} //class AirNoteAdapter
