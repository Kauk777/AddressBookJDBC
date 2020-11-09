package com.bridgelabz.AddressBookJDBC;

import java.sql.SQLException;
import java.util.ArrayList;
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

	public AddressBookService(List<AddressBookData> addressList) {
		this();
		this.addressList = new ArrayList<>(addressList);
	}

	// Reading AddressBook Data
	public List<AddressBookData> readAddressData(IOService ioService) {
		if (ioService.equals(IOService.DB_IO))
			this.addressList = addressDBService.read();
		return this.addressList;
	}

	// Reading AddressBookData By city or State
	public List<AddressBookData> readAddressData(IOService ioService, String city, String state) {
		if (ioService.equals(IOService.DB_IO))
			this.addressList = addressDBService.readByStateOrCity(city, state);
		return this.addressList;
	}

	// Updating Address in AddressBook For Given Name
	public void updateAddressBook(String firstName, String address, int zip, IOService ioService)
			throws SQLException, AddressBookException {
		if (ioService.equals(IOService.DB_IO)) {
			int result = addressDBService.updateData(firstName, address, zip);
			if (result == 0)
				throw new AddressBookException("No Updation Performed",
						AddressBookException.ExceptionType.UNABLE_TO_UPDATE);
		}
		AddressBookData addressData = this.getAddressData(firstName);
		if (addressData != null)
			addressData.address = address;
	}

	// Method to get AddressBook Object for given Name
	public AddressBookData getAddressData(String firstName) {
		return this.addressList.stream().filter(addressDataItem -> addressDataItem.firstName.equals(firstName))
				.findFirst().orElse(null);
	}

	// Method to check syncing of addressbook with DB
	public boolean addressBookSyncWithDB(String name) {
		List<AddressBookData> addressList = addressDBService.getAddressList(name);
		return addressList.get(0).equals(getAddressData(name));
	}

	// Adding addressBook Using Threads
	public void addAddressBooks(List<AddressBookData> addressList) {
		Map<Integer, Boolean> addressAdditionStatus = new HashMap<>();
		addressList.forEach(addressData -> {
			Runnable task = () -> {
				try {
					addressAdditionStatus.put(addressData.hashCode(), false);
					System.out.println("Address being added: " + Thread.currentThread().getName());
					this.addAddressData(addressData.firstName, addressData.lastName, addressData.address,
							addressData.city, addressData.state, addressData.zip, addressData.phone, addressData.email,
							addressData.type);
					addressAdditionStatus.put(addressData.hashCode(), true);
					System.out.println("Address added: " + Thread.currentThread().getName());

				} catch (Exception e) {
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

	// Adding Data to addressBook
	public void addAddressData(String firstName, String lastName, String address, String city, String state, int zip,
			int phone, String email, String type) throws AddressBookException {
		this.addressList
				.add(addressDBService.addData(firstName, lastName, address, city, state, zip, phone, email, type));
	}

	// Adding Data to addressBook Using REST API Testing
	public void addAddressToBook(AddressBookData addressData, IOService ioService) {
		if (ioService.equals(IOService.REST_IO))
			addressList.add(addressData);

	}

	// Counting number of entries for given Data
	public long countEntries(IOService ioService) {
		return this.addressList.size();
	}

	// Deleting Address Data from the addressBook
	public void deleteAddressBook(String firstName, IOService ioService) {
		if (ioService.equals(IOService.REST_IO)) {
			AddressBookData addressData = this.getAddressData(firstName);
			addressList.remove(addressData);
		}

	}

}
