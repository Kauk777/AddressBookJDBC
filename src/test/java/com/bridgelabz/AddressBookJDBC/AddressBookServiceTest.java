package com.bridgelabz.AddressBookJDBC;

import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;


public class AddressBookServiceTest {
	@Test
	public void givenAddressDB_WhenRetreived_ShouldReturnCount() {
		AddressBookService addressBookService = new AddressBookService();
		List<AddressBookData> addressList = addressBookService.readAddressData(AddressBookService.IOService.DB_IO);
		Assert.assertEquals(8, addressList.size());
	}
	
	@Test
	public void givenAddressDB_WhenUpdated_ShouldSyncWithDB() throws SQLException {
		AddressBookService addressBookService = new AddressBookService();
		List<AddressBookData> addressList = addressBookService.readAddressData(AddressBookService.IOService.DB_IO);
		addressBookService.updateAddressBook("Aarav", "Sweet Water 41 downtown", 514263);
		boolean result=addressBookService.addressBookSyncWithDB("Aarav");
		Assert.assertTrue(result);
	}
	
	@Test
	public void givenAddressDB_WhenRetreivedByCityOrState_ShouldReturnCount() {
		AddressBookService addressBookService = new AddressBookService();
		List<AddressBookData> addressList = addressBookService.readAddressData(AddressBookService.IOService.DB_IO,"Kolkata","Delhi");
		Assert.assertEquals(4, addressList.size());
	}
	
	@Test
	public void givenMultipleAdressBook_WhenAdded_ShoulReturnCount() {
		AddressBookData[] addressArray= { new AddressBookData("Helda","Zden","Jorlon way 254","Mandara","Narobi",47100,224578962,"zeden78@gmail.com","Family"),
				                          new AddressBookData("Alara","Malori","206 Dew view","Imphal","Meghalaya",10222,784752698,"malori7@gmail.com","Family") };
				                          
		AddressBookService addressBookService = new AddressBookService();
		addressBookService.readAddressData(AddressBookService.IOService.DB_IO);
		Instant threadStart=Instant.now();
		addressBookService.addAddressBooks(Arrays.asList(addressArray));
		Instant threadEnd=Instant.now();
		System.out.println("Duration with thread "+Duration.between(threadStart,threadEnd));
		Assert.assertEquals(10 , addressBookService.countEntries(AddressBookService.IOService.DB_IO));
		
	}
}
