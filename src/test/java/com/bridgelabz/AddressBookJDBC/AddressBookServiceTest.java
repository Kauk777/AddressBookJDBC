package com.bridgelabz.AddressBookJDBC;

import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
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
}