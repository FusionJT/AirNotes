
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

public class AirNotes extends ListActivity {
    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    public static final int ACTIVITY_IMPORT = 2;
    public static final int ACTIVITY_COUNTRY_VIEW = 3;

    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int COUNTRY_VIEW_ID = Menu.FIRST + 2;

    private AirNotesDbAdapter mDbHelper;
    private Context mCtx;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	mCtx = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.airnotes_list);
        setTitle("AirNotes - Vista por AirNotes");
        mDbHelper = new AirNotesDbAdapter(mCtx);
        fillData();
        registerForContextMenu(getListView());
    } //onCreate

    private void fillData() {
        // Get all of the rows from the database and create the item list
    	mDbHelper.open();
    	Cursor airNotesCursor;
    	
        airNotesCursor = mDbHelper.fetchAllNotes();
        startManagingCursor(airNotesCursor);

        String[] from = new String[]{AirNotesDbAdapter.KEY_CODE, AirNotesDbAdapter.KEY_COUNTRY_CODE,
        		AirNotesDbAdapter.KEY_DAY, AirNotesDbAdapter.KEY_MONTH, AirNotesDbAdapter.KEY_YEAR};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.textCode, R.id.flagCountry, R.id.textDay, R.id.textMonth, R.id.textYear};

        // Now create a simple cursor adapter and set it to display
        AirNoteListAdapter airNotes = 
        	new AirNoteListAdapter(this, R.layout.airnotes_row, airNotesCursor, from, to);
        
        setListAdapter(airNotes);
        mDbHelper.close();

    } //fillData

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_insert);
        menu.add(0, COUNTRY_VIEW_ID, 1, R.string.menu_country_view);
        return true;
    } //onCreateOptionsMenu

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case INSERT_ID:
                createNote();
                return true;
            case COUNTRY_VIEW_ID:
            	countryView();
            	return true;
        } //switch
        return super.onMenuItemSelected(featureId, item);
    } //onMenuItemSelected

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    } //onCreateContextMenu

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_ID:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.open();
                mDbHelper.deleteAirNote(info.id);
                mDbHelper.close();
                fillData();
                return true;
        } //switch
        return super.onContextItemSelected(item);
    } //onContextItemSelected

    private void createNote() {
        Intent i = new Intent(this, AirNoteEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    } //createNote
    
    private void countryView(){
    	Intent i = new Intent(this, AirNotesByCountry.class);
    	startActivityForResult(i, ACTIVITY_COUNTRY_VIEW);
    } //countryView

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, AirNoteEdit.class);
        i.putExtra(AirNotesDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    } //onListItemClick

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    } //onActivityResult


    
    
    
} //class Notepadv3
