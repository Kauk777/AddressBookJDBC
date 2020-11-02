package com.bridgelabz.AddressBookJDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddressBookDBService {
	
	private static AddressBookDBService addressDBService;
	
	public AddressBookDBService() {
		
	}
	
	public static AddressBookDBService getInstance() {
		if(addressDBService==null)
			addressDBService=new AddressBookDBService();
		return addressDBService;
	}
	
	private Connection getConnection() throws SQLException {
		String jdbcURL = "jdbc:mysql://localhost:3306/addressbook_service?useSSL=false";
		String user = "root";
		String password = "root";

		Connection connection;
		System.out.println("Connection to database: " + jdbcURL);
		connection = DriverManager.getConnection(jdbcURL, user, password);
		System.out.println("Connection is successful!!!!" + connection);

		return connection;
	}
	
	public List<AddressBookData> read() {
		String sql="SELECT * FROM address_book;";
		return this.readQuery(sql);
	}

	private List<AddressBookData> readQuery(String sql) {
		try(Connection conn=this.getConnection()) {
			PreparedStatement statement=conn.prepareStatement(sql);
			ResultSet result=statement.executeQuery();
			return this.getAddressList(result);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private List<AddressBookData> getAddressList(ResultSet result) {
		List<AddressBookData> addressDBList=new ArrayList<>();
		try {
			while(result.next()) {
				int id=result.getInt("AddressBook_Id");
				String firstName=result.getString("firstName");
				String address=result.getString("address");
				int zip=result.getInt("zip");
				addressDBList.add(new AddressBookData(id,firstName,address,zip));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return addressDBList;
	}

}
