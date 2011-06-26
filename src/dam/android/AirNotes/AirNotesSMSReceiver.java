package dam.android.AirNotes;

import java.util.GregorianCalendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class AirNotesSMSReceiver extends BroadcastReceiver {

	protected static final String LOG_TAG = "AirNotesSMSReceiver";
	private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";

	private String mSmsBody;
	private String mCode;
	private String mCountry;
	private int mDay = 0;
	private int mMonth = 0;
	private int mYear = 0;
	private String mNotes;
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		Log.i(LOG_TAG, "-> AirNotesSMSReceiver.onReceive()");

		if (intent.getAction().equals(ACTION)) {

			StringBuilder sb = new StringBuilder();
			Bundle bundle = intent.getExtras();

			if (bundle != null) {
				Object[] pdus = (Object[]) bundle.get("pdus");
				SmsMessage[] mensajes = new SmsMessage[pdus.length];
				for (int i = 0; i < pdus.length; i++) {
					mensajes[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				} //for

				Log.i(LOG_TAG, "SMS bundle: " + bundle.toString());

				for (SmsMessage m : mensajes) {
					mSmsBody = m.getDisplayMessageBody().trim();
					if ((mSmsBody.startsWith("#aeropuerto:"))&&(mSmsBody.endsWith(";"))){
						mSmsBody = detectarNotasNulas(mSmsBody);
						Log.d("CadenaNULA", mSmsBody);
						boolean valido = true;
						mSmsBody = mSmsBody.substring(mSmsBody.indexOf(':')+1);
						String[] auxBody = mSmsBody.split(";");
						String[] auxDate = null;
						if (auxBody.length != 4){
							valido = false;
						} else {
							auxDate = auxBody[2].split("/");
							if (auxDate.length != 3){
								valido = false;
							} else {
								if ((!isNumeric(auxDate[0]))||(!isNumeric(auxDate[1]))||(!isNumeric(auxDate[2]))){
									valido = false;
								} //if								
							} //if
						} //if
						
						if (valido == true){
							mCode = auxBody[0];
							mCountry = auxBody[1];
							mDay = new Integer(auxDate[0]).intValue();
							mMonth = (new Integer(auxDate[1]).intValue())-1;
							mYear = new Integer(auxDate[2]).intValue();
							mNotes = auxBody[3];
							
							//Validar Fecha
							GregorianCalendar calendar = new GregorianCalendar(mYear, mMonth, mDay);
							if ((mYear!=calendar.get(GregorianCalendar.YEAR))||
								(mMonth)!=calendar.get(GregorianCalendar.MONTH)||
								(mDay != calendar.get(GregorianCalendar.DAY_OF_MONTH))){
								valido = false;
							} //if validarFecha
							
							//Validar código y pais
							if ((mCode.length()!=3)||(mCountry.length()==0)){
								valido = false;
							} 
						} //if
						
						if (valido == true){
							Airport airport;
							AirportDataParserSax parserSax = new AirportDataParserSax(
									mCode);
							airport = parserSax.parse();
							String countryCode = airport.getCountryCode();						
							
							AirNotesDbAdapter mDbHelper = new AirNotesDbAdapter(context);
							mDbHelper.open();
							mDbHelper.createAirNote(mCode.toUpperCase(), airport.getCountryName(), countryCode, mDay, mMonth, mYear, mNotes);
							mDbHelper.close();
							
							
							sb.append("AirNote importado desde SMS");
							Toast.makeText(context, sb.toString(), Toast.LENGTH_LONG).show();
							// Start the Main-Activity
							Intent i = new Intent(context, AirNotes.class);
							i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(i);
						} //if
					} //if
				} //for
			}
			Log.i(LOG_TAG, "Mensaje: " + sb);

		} //if
	} //onReceive
	
	private boolean isNumeric(String cadena){
		for (int i = 0; i < cadena.length(); i++){
			if ((cadena.charAt(i)<'0')||(cadena.charAt(i)>'9')){
				return false;
			} //if
		} //for
		return true;
	} //isNumeric
	
	private String detectarNotasNulas(String cadena){
		//Si las notas son nulas metemos un carácter en blanco
		if (cadena.charAt(cadena.length()-1) == ';'){
			cadena = cadena.substring(0, cadena.length()-1);
			cadena = cadena.concat(" ;");
		} //if
		return cadena;
	} //detectarCadenasNulas

} //AirNotesSMSReceiver
