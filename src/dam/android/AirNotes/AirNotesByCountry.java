
package dam.android.AirNotes;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class AirNotesByCountry extends ListActivity {

	private static final int BACK_ID = Menu.FIRST;

    private AirNotesDbAdapter mDbHelper;
    private Context mCtx;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	mCtx = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.airnotes_list);
        setTitle("AirNotes - Vista por Países");
        mDbHelper = new AirNotesDbAdapter(mCtx);
        fillData();
        registerForContextMenu(getListView());
    } //onCreate

    private void fillData() {
        // Get all of the rows from the database and create the item list
    	mDbHelper.open();
    	Cursor airNotesCursor;
    	
        airNotesCursor = mDbHelper.fetchAllNotesByCountry();
        startManagingCursor(airNotesCursor);

        String[] from = new String[]{AirNotesDbAdapter.KEY_COUNTRY_CODE, "count()"};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.flagCountry, R.id.countryCount};

        // Now create a simple cursor adapter and set it to display
        AirNoteListAdapterByCountry airNotes = 
        	new AirNoteListAdapterByCountry(this, R.layout.airnotes_row_by_country, airNotesCursor, from, to);
        
        setListAdapter(airNotes);
        mDbHelper.close();

    } //fillData

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, BACK_ID, 0, R.string.menu_single_view);
        return true;
    } //onCreateOptionsMenu

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case BACK_ID:
                finish();
                setResult(RESULT_OK);
                return true;
        } //switch
        return super.onMenuItemSelected(featureId, item);
    } //onMenuItemSelected
} //class AirNotesByCountry
