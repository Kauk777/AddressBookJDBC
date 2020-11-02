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
	private PreparedStatement addressDataStatement;
	
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
	
	public List<AddressBookData> readByStateOrCity(String city, String state) {
		String sql="SELECT * FROM address_book where city=? or state=?;";
		return this.readQuery(sql,city,state);
	}

	private List<AddressBookData> readQuery(String sql, String city, String state) {
		try(Connection conn=this.getConnection()) {
			PreparedStatement statement=conn.prepareStatement(sql);
			statement.setString(1, city);
			statement.setString(2, state);
			ResultSet result=statement.executeQuery();
			return this.getAddressList(result);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
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

	public int updateData(String firstName, String address, int zip) {
		try(Connection conn=this.getConnection()) {
			String sql="UPDATE address_book SET address=? WHERE firstName=? ;";
			PreparedStatement statement=conn.prepareStatement(sql);
			statement.setString(1, address);
			statement.setString(2, firstName);
			int result=statement.executeUpdate();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public List<AddressBookData> getAddressList(String name) {
		List<AddressBookData> addressList = null;
		if (this.addressDataStatement == null)
			this.readPrepared();
		try {
			addressDataStatement.setString(1, name);
			ResultSet result = addressDataStatement.executeQuery();
			addressList = this.getAddressList(result);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return addressList;
	}

	private void readPrepared() {
		try {
			Connection connection = this.getConnection();
			String sql = "SELECT * FROM address_book WHERE firstName=? ";
			addressDataStatement = connection.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}


}
