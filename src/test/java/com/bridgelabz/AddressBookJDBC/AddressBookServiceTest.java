package com.bridgelabz.AddressBookJDBC;

import static org.junit.Assert.assertTrue;

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
	
	@Before
	public void setup() {
		RestAssured.baseURI= "http://localhost";
		RestAssured.port = 3000;
	}
	
	
	@Test
	public void givenAddressDB_WhenRetreived_ShouldReturnCount() {
		AddressBookService addressBookService = new AddressBookService();
		List<AddressBookData> addressList = addressBookService.readAddressData(IOService.DB_IO);
		Assert.assertEquals(8, addressList.size());
	}
	
	@Test
	public void givenAddressDB_WhenUpdated_ShouldSyncWithDB() throws SQLException {
		AddressBookService addressBookService = new AddressBookService();
		List<AddressBookData> addressList = addressBookService.readAddressData(IOService.DB_IO);
		addressBookService.updateAddressBook("Aarav", "Sweet Water 41 downtown", 514263);
		boolean result=addressBookService.addressBookSyncWithDB("Aarav");
		Assert.assertTrue(result);
	}
	
	@Test
	public void givenAddressDB_WhenRetreivedByCityOrState_ShouldReturnCount() {
		AddressBookService addressBookService = new AddressBookService();
		List<AddressBookData> addressList = addressBookService.readAddressData(IOService.DB_IO,"Kolkata","Delhi");
		Assert.assertEquals(4, addressList.size());
	}
	
	@Test
	public void givenMultipleAdressBook_WhenAdded_ShoulReturnCount() {
		AddressBookData[] addressArray= { new AddressBookData(0,"Helda","Zden","Jorlon way 254","Mandara","Narobi",47100,224578962,"zeden78@gmail.com","Family"),
				                          new AddressBookData(0,"Alara","Malori","206 Dew view","Imphal","Meghalaya",10222,784752698,"malori7@gmail.com","Family") };
				                          
		AddressBookService addressBookService = new AddressBookService();
		addressBookService.readAddressData(IOService.DB_IO);
		Instant threadStart=Instant.now();
		addressBookService.addAddressBooks(Arrays.asList(addressArray));
		Instant threadEnd=Instant.now();
		System.out.println("Duration with thread "+Duration.between(threadStart,threadEnd));
		Assert.assertEquals(10 , addressBookService.countEntries(IOService.DB_IO));
		
	}
	
	public AddressBookData[] getAddressList() {
		Response response=RestAssured.get("/addressbook");
		System.out.println("Address Book entry in JSON server:\n "+ response.asString());
		AddressBookData[] arrAddress=new Gson().fromJson(response.asString(),AddressBookData[].class);
		return arrAddress;
	}
	
	private Response addAddressToJsonServer(AddressBookData addressData) {
		String addressJson=new Gson().toJson(addressData);
		RequestSpecification request=RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(addressJson);
		
		return request.post("/addressbook");
	}
	
	@Test
	public void getAddressBookDatainJSONServer_WhenRetreived_ReturnCount() {
		AddressBookData[] arrAddress=getAddressList();
		AddressBookService addressBookService;
		addressBookService=new AddressBookService(Arrays.asList(arrAddress));
		long entries=addressBookService.countEntries(IOService.REST_IO);
		Assert.assertEquals(2,entries);
	}
	
	@Test
	public void givenListOfAddressBook_WhenAdded_ShouldReturnCount() {
		AddressBookData[] arrAddress=getAddressList();
		AddressBookService addressBookService;
		addressBookService=new AddressBookService(Arrays.asList(arrAddress));
		AddressBookData[] arrAddressData= { new AddressBookData(3,"Helda","Zden","Jorlon way 254","Mandara","Narobi",47100,224578962,"zeden78@gmail.com"),
											new AddressBookData(4,"Alara","Malori","206 Dew view","Imphal","Meghalaya",10222,784752698,"malori7@gmail.com") };
		for(AddressBookData addressData:arrAddressData) {
			Response response=addAddressToJsonServer(addressData);
			int statusCode=response.getStatusCode();
			Assert.assertEquals(500,statusCode);
			
			addressData=new Gson().fromJson(response.asString(),AddressBookData.class);
			addressBookService.addAddressToBook(addressData,IOService.REST_IO);	
		}
		long entries=addressBookService.countEntries(IOService.REST_IO);
		Assert.assertEquals(4,entries);		
	}


	
	
	
}
