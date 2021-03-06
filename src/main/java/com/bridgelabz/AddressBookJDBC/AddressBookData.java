package com.bridgelabz.AddressBookJDBC;

import java.util.Objects;

public class AddressBookData {

	public int id;
	public String firstName;
	public String lastName;
	public String address;
	public String city;
	public String state;
	public int zip;
	public int phone;
	public String email;
	public String type;

	public AddressBookData(int addressId,String firstName, String lastName, String address, String city, String state,
			int zip, int phone, String email, String type) {
		this(addressId,firstName,lastName,address,city,state,zip,phone,email);
		this.type = type;
	}
	
	public AddressBookData(int addressId,String firstName, String lastName, String address, String city, String state,
			int zip, int phone, String email) {
		this.id=addressId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.phone = phone;
		this.email = email;
		
		
	}

	public AddressBookData(int addressId, String firstName, String address, int zip) {
		this.id = addressId;
		this.firstName = firstName;
		this.address = address;
		this.zip = zip;
	}
	
	
	@Override 
	public int hashCode() {
		return Objects.hash(firstName,address,zip);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AddressBookData other = (AddressBookData) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (id != other.id)
			return false;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (phone != other.phone)
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (zip != other.zip)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AddressBookData [addressId=" + id + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", address=" + address + ", city=" + city + ", state=" + state + ", zip=" + zip + ", phone=" + phone
				+ ", email=" + email + ", type=" + type + "]";
	}

}
