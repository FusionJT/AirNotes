package dam.android.AirNotes;

public class Airport {
		
	private String airportCode;
	private String countryName;
	private String countryCode;
	
	
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	} //setCountryCode

	public String getCountryCode() {
		return countryCode;
	} //getCountryCode

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	} //setCountry

	public String getCountryName() {
		return countryName;
	} //getCountry

	public void setAirportCode(String code) {
		this.airportCode = code;
	} //setCode

	public String getCode() {
		return airportCode;
	} //getCode
	
} //class Airport
