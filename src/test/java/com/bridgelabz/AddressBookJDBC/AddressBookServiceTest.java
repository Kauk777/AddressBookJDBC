package com.bridgelabz.AddressBookJDBC;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.bridgelabz.AddressBookJDBC.AddressBookService.IOService;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class AddressBookServiceTest {

	// JDBC UNIT TESTING

	@Test
	public void givenAddressDB_WhenRetreived_ShouldReturnCount() {
		AddressBookService addressBookService = new AddressBookService();
		List<AddressBookData> addressList = addressBookService.readAddressData(IOService.DB_IO);
		Assert.assertEquals(8, addressList.size());
	}

	@Test
	public void givenAddressDB_WhenUpdated_ShouldSyncWithDB() throws SQLException, AddressBookException {
		AddressBookService addressBookService = new AddressBookService();
		List<AddressBookData> addressList = addressBookService.readAddressData(IOService.DB_IO);
		addressBookService.updateAddressBook("Aarav", "Sweet Water 41 downtown", 514263, IOService.DB_IO);
		boolean result = addressBookService.addressBookSyncWithDB("Aarav");
		Assert.assertTrue(result);
	}

	@Test
	public void givenAddressDB_WhenRetreivedByCityOrState_ShouldReturnCount() {
		AddressBookService addressBookService = new AddressBookService();
		List<AddressBookData> addressList = addressBookService.readAddressData(IOService.DB_IO, "Kolkata", "Delhi");
		Assert.assertEquals(4, addressList.size());
	}

	@Test
	public void givenMultipleAdressBook_WhenAdded_ShoulReturnCount() {
		AddressBookData[] addressArray = {
				new AddressBookData(0, "Helda", "Zden", "Jorlon way 254", "Mandara", "Narobi", 47100, 224578962,
						"zeden78@gmail.com", "Family"),
				new AddressBookData(0, "Alara", "Malori", "206 Dew view", "Imphal", "Meghalaya", 10222, 784752698,
						"malori7@gmail.com", "Family") };

		AddressBookService addressBookService = new AddressBookService();
		addressBookService.readAddressData(IOService.DB_IO);
		Instant threadStart = Instant.now();
		addressBookService.addAddressBooks(Arrays.asList(addressArray));
		Instant threadEnd = Instant.now();
		System.out.println("Duration with thread " + Duration.between(threadStart, threadEnd));
		Assert.assertEquals(10, addressBookService.countEntries(IOService.DB_IO));

	}

	// REST ASSURE API TESTING

	@Before
	public void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 3000;
	}

	public AddressBookData[] getAddressList() {
		Response response = RestAssured.get("/addressbook");
		System.out.println("Address Book entry in JSON server:\n " + response.asString());
		AddressBookData[] arrAddress = new Gson().fromJson(response.asString(), AddressBookData[].class);
		return arrAddress;
	}

	private Response addAddressToJsonServer(AddressBookData addressData) {
		String addressJson = new Gson().toJson(addressData);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(addressJson);

		return request.post("/addressbook");
	}

	@Test
	public void getAddressBookDatainJSONServer_WhenRetreived_ReturnCount() {
		AddressBookData[] arrAddress = getAddressList();
		AddressBookService addressBookService;
		addressBookService = new AddressBookService(Arrays.asList(arrAddress));
		long entries = addressBookService.countEntries(IOService.REST_IO);
		Assert.assertEquals(2, entries);
	}

	@Test
	public void givenListOfAddressBook_WhenAdded_ShouldReturnCount() {
		AddressBookData[] arrAddress = getAddressList();
		AddressBookService addressBookService;
		addressBookService = new AddressBookService(Arrays.asList(arrAddress));
		AddressBookData[] arrAddressData = {
				new AddressBookData(0, "Helda", "Zden", "Jorlon way 254", "Mandara", "Narobi", 47100, 224578962,
						"zeden78@gmail.com"),
				new AddressBookData(0, "Alara", "Malori", "206 Dew view", "Imphal", "Meghalaya", 10222, 784752698,
						"malori7@gmail.com") };
		for (AddressBookData addressData : arrAddressData) {
			Response response = addAddressToJsonServer(addressData);
			int statusCode = response.getStatusCode();
			Assert.assertEquals(201, statusCode);

			addressData = new Gson().fromJson(response.asString(), AddressBookData.class);
			addressBookService.addAddressToBook(addressData, IOService.REST_IO);
		}
		long entries = addressBookService.countEntries(IOService.REST_IO);
		Assert.assertEquals(4, entries);
	}

	@Test
	public void WhenAddressBookGiven_UpdatedWithNewAddress_ShouldMatchRequestStatus()
			throws SQLException, AddressBookException {
		AddressBookData[] arrAddress = getAddressList();
		AddressBookService addressBookService;
		addressBookService = new AddressBookService(Arrays.asList(arrAddress));

		addressBookService.updateAddressBook("Cere", "Sweet Water 47 downtown", 514263, IOService.REST_IO);
		AddressBookData addressBookData = addressBookService.getAddressData("Cere");
		String addressJson = new Gson().toJson(addressBookData);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(addressJson);
		Response response = request.put("/addressbook/" + addressBookData.id);
		int statusCode = response.getStatusCode();
		Assert.assertEquals(200, statusCode);
	}

	@Test
	public void WhenAddressBookGiven_DeletedAddressBook_ShouldMatchRequest() {
		AddressBookData[] arrAddress = getAddressList();
		AddressBookService addressBookService;
		addressBookService = new AddressBookService(Arrays.asList(arrAddress));

		AddressBookData addressBookData = addressBookService.getAddressData("Cere");
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		Response response = request.delete("/addressbook/" + addressBookData.id);
		int statusCode = response.getStatusCode();
		Assert.assertEquals(200, statusCode);

		addressBookService.deleteAddressBook(addressBookData.firstName, IOService.REST_IO);
		long entries = addressBookService.countEntries(IOService.REST_IO);
		Assert.assertEquals(3, entries);

	}

}
