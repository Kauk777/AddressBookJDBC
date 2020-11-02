package com.bridgelabz.AddressBookJDBC;

import java.util.List;

public class AddressBookService {
	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	}

	private List<AddressBookData> addressList;
	private AddressBookDBService addressDBService;

	public AddressBookService() {
		addressDBService = AddressBookDBService.getInstance();
	}

	public List<AddressBookData> readAddressData(IOService ioService) {
		if (ioService.equals(IOService.DB_IO))
			this.addressList = addressDBService.read();
		return this.addressList;
	}

}
