package com.bridgelabz.AddressBookJDBC;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AddressBookDBService {

	private static AddressBookDBService addressDBService;
	private PreparedStatement addressDataStatement;

	public AddressBookDBService() {

	}

	public static AddressBookDBService getInstance() {
		if (addressDBService == null)
			addressDBService = new AddressBookDBService();
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
		String sql = "SELECT * FROM address_book;";
		return this.readQuery(sql);
	}

	public List<AddressBookData> readByStateOrCity(String city, String state) {
		String sql = "SELECT * FROM address_book where city=? or state=?;";
		return this.readQuery(sql, city, state);
	}

	private List<AddressBookData> readQuery(String sql, String city, String state) {
		try (Connection conn = this.getConnection()) {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, city);
			statement.setString(2, state);
			ResultSet result = statement.executeQuery();
			return this.getAddressList(result);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private List<AddressBookData> readQuery(String sql) {
		try (Connection conn = this.getConnection()) {
			PreparedStatement statement = conn.prepareStatement(sql);
			ResultSet result = statement.executeQuery();
			return this.getAddressList(result);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private List<AddressBookData> getAddressList(ResultSet result) {
		List<AddressBookData> addressDBList = new ArrayList<>();
		try {
			while (result.next()) {
				int id = result.getInt("AddressBook_Id");
				String firstName = result.getString("firstName");
				String address = result.getString("address");
				int zip = result.getInt("zip");
				addressDBList.add(new AddressBookData(id, firstName, address, zip));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return addressDBList;
	}

	public int updateData(String firstName, String address, int zip) throws SQLException {
		return this.updateAddressData(firstName, address);
	}

	private int updateAddressData(String firstName, String address) throws SQLException {
		String sql = String.format("UPDATE address_book SET address= '%s' WHERE firstName='%s';", address, firstName);
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			return statement.executeUpdate(sql);
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

	public AddressBookData addData(String firstName, String lastName, String address, String city, String state,
			int zip, int phone, String email, String type) {
		int addressId = -1;
		Connection connection = null;
		AddressBookData addressData = null;
		try {
			connection = this.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try (Statement statement = connection.createStatement()) {
			String sql = String.format(
					"INSERT INTO address_book (firstName,lastName,address,city,state,zip,phone,email,type) "
							+ "VALUES ('%s', %s, '%s','%s','%s',%s,%s,'%s','%s')",
					firstName, lastName, address, city, state, zip, phone, email, type);
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet result = statement.getGeneratedKeys();
				if (result.next())
					addressId = result.getInt(1);
				addressData = new AddressBookData(addressId,firstName, lastName, address, city, state, zip, phone, email, type);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return addressData;
	}

}
