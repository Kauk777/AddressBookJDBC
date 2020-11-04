package com.bridgelabz.AddressBookJDBC;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

	public List<AddressBookData> readAddressData(IOService ioService, String city, String state) {
		if (ioService.equals(IOService.DB_IO))
			this.addressList = addressDBService.readByStateOrCity(city, state);
		return this.addressList;
	}

	public void updateAddressBook(String firstName, String address, int zip) throws SQLException {
		int result = addressDBService.updateData(firstName, address, zip);
		if (result == 0) {
			System.out.println("No Updation");
			return;
		}
		AddressBookData addressData = this.getAddressData(firstName);
		if (addressData != null)
			addressData.address = address;
	}

	private AddressBookData getAddressData(String firstName) {
		return this.addressList.stream().filter(addressDataItem -> addressDataItem.firstName.equals(firstName))
				.findFirst().orElse(null);
	}

	public boolean addressBookSyncWithDB(String name) {
		List<AddressBookData> addressList = addressDBService.getAddressList(name);
		return addressList.get(0).equals(getAddressData(name));
	}

	public void addAddressBooks(List<AddressBookData> addressList) {
		Map<Integer, Boolean> addressAdditionStatus = new HashMap<>();
		addressList.forEach(addressData -> {
			Runnable task = () -> {
				try {
					addressAdditionStatus.put(addressData.hashCode(), false);
					System.out.println("Address being added: " + Thread.currentThread().getName());
					this.addAddressData(addressData.firstName,addressData.lastName,addressData.address,addressData.city,addressData.state,addressData.zip,addressData.phone,addressData.email,addressData.type);
					addressAdditionStatus.put(addressData.hashCode(), true);
					System.out.println("Address added: " + Thread.currentThread().getName());
					
				} catch(Exception e) {
					e.printStackTrace();
				}
			};
			Thread thread = new Thread(task, addressData.firstName);
			thread.start();
		});
		while (addressAdditionStatus.containsValue(false)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void addAddressData(String firstName, String lastName, String address, String city, String state, int zip, int phone, String email, String type) {
		addressList.add(addressDBService.addData(firstName,lastName,address,city,state,zip,phone,email,type));
	}

	public long countEntries(IOService ioService) {
		return addressList.size();
	}

}
