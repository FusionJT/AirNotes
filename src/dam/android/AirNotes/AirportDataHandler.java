package dam.android.AirNotes;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AirportDataHandler extends DefaultHandler {
	private boolean procCountryCode = false;
	private boolean procCountryName = false;
	private String countryCode;
	private String countryName;
	
	public String getCountryCode(){
		return countryCode;
	} //getCountryCoude
	
	public String getCountryName(){
		return countryName;
	} //getCountryName
	
	public void setCountryCode(String countryCode){
		this.countryCode  = countryCode;
	} //setCountryCode
	
	public void setCountryName(String countryName){
		this.countryName = countryName;
	} //setCountryName
	
	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
	} //startDocument

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	} //endDocument
	
	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		super.startElement(namespaceURI, localName, qName, atts);
		if (localName.equals("countryCode")){
			this.procCountryCode = true;
		} else if (localName.equals("countryName")){
			this.procCountryName = true;
		} //if
	} //startElement

	@Override
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		super.endElement(namespaceURI, localName, qName);
		if (localName.equals("countryCode")){
			this.procCountryCode = false;
		} else if (localName.equals("countryName")){	
			this.procCountryName = false;
		} //if
	} //endElement

	@Override
	public void characters(char ch[], int start, int length) throws SAXException {
			super.characters(ch, start, length);
		if (this.procCountryCode){
			setCountryCode(new String(ch, start, length));
		} else if (this.procCountryName){
			setCountryName(new String(ch, start, length));
		} //if
	} //characters
} //classAirportDataHandler
