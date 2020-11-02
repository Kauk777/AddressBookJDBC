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
	
	public void updateAddressBook(String firstName, String address, int zip) {
		int result=addressDBService.updateData(firstName,address,zip);
		if (result == 0) {
			System.out.println("No Updation");
			return;
		}
		AddressBookData addressData=this.getAddressData(firstName);
		if(addressData!=null)
			addressData.address=address;
	}

	private AddressBookData getAddressData(String firstName) {
		return this.addressList.stream()
				.filter(addressDataItem -> addressDataItem.firstName.equals(firstName))
				.findFirst()
				.orElse(null);
	}
	
	public boolean addressBookSyncWithDB(String name) {
		List<AddressBookData> addressList=addressDBService.getAddressList(name);
		return addressList.get(0).equals(getAddressData(name));
	}

}
