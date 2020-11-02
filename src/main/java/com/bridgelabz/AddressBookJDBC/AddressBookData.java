package com.bridgelabz.AddressBookJDBC;

public class AddressBookData {
	
	private int addressId;
	private String firstName;
	private String lastName;
	private String address;
	private String city;
	private String state;
	private int zip;
	private int phone;
	private String email;
	private String type;
	
	public AddressBookData(int addressId, String firstName, String lastName, String address, String city, String state,
			int zip, int phone, String email, String type) {
		this.addressId = addressId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.phone = phone;
		this.email = email;
		this.type = type;
	}
	
	public AddressBookData(int addressId, String firstName,String address, int zip) {
		this.addressId=addressId;
		this.firstName = firstName;
		this.address = address;
		this.zip = zip;
	}

	@Override
	public String toString() {
		return "AddressBookData [addressId=" + addressId + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", address=" + address + ", city=" + city + ", state=" + state + ", zip=" + zip + ", phone=" + phone
				+ ", email=" + email + ", type=" + type + "]";
	}
	
	
	
	

}
