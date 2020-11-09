package com.bridgelabz.AddressBookJDBC;

@SuppressWarnings("serial")
public class AddressBookException extends Exception {

	enum ExceptionType {
		ADDRESSBOOK_DB_PROBLEM, UNABLE_TO_UPDATE
	}

	ExceptionType type;

	public AddressBookException(String message, ExceptionType type) {
		super(message);
		this.type = type;

	}

}
