package dam.android.AirNotes;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import android.util.Log;

public class AirportDataParserSax {

	private Airport airport;
	private URL url;

	public AirportDataParserSax(String airportCode) {
		String urlString = "http://ws.geonames.org/search?name_equals="
				+ airportCode.toUpperCase() + "&fcode=airp";
		airport = new Airport();
		airport.setAirportCode(airportCode);
		try {
			this.url = new URL(urlString);
		} catch (MalformedURLException e) {
			Log.d("JOSE",
					"Error en la asignación de url dentro de la clase AirportDataParserSax");
		} // try
	} // Creation Method

	public Airport parse() {
		SAXParserFactory factory = SAXParserFactory.newInstance();

		try {
			SAXParser parser = factory.newSAXParser();
			AirportDataHandler handler = new AirportDataHandler();
			parser.parse(this.getInputStream(), handler);
			airport.setCountryCode(handler.getCountryCode());
			airport.setCountryName(handler.getCountryName());
		} catch (Exception e) {
			Log.d("JOSE", "Problema en la lectura del documento XML dento de la clase AirportDataParserSax");
		} //try
		return airport;
	}//parse

	private InputStream getInputStream() {
		try {
			return url.openConnection().getInputStream();
		} catch (IOException e) {
			Log.d("JOSE",
					"Problema al obtener el input stream dentro de la clase AirportDataParserSax");
		} // try
		return null;
	} // getInputStream

} // class AirportDataParserSax

