package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;


public class Payment {
	// A common method to connect to the DB
		private Connection connect() {
			Connection con = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				// Provide the correct details: DBServer/DBName, username, password
				con = DriverManager.getConnection("jdbc:mysql://localhost:3306/electrogrid_clientside", "root", "");

				// For testing
				System.out.print("Successfully connected");

			} catch (Exception e) {
				e.printStackTrace();
			}

			return con;
		}
		
		public String readPayment() {
			String output = "";

			try {
				Connection con = connect();
				if (con == null) {
					return "Error while connecting to the database for reading.";
				}

				// Prepare the html table to be displayed
				output = "<table border='1'><tr><th>Payment ID</th>" + "<th>Amount</th><th>Payment Card Number</th>"
						+ "<th>Payment Type</th>"+"<tr><th>Payment Date</th>"+"<th>Update</th><th>Remove</th></tr>";

				String query = "select * from payment";
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery(query);

				// iterate through the rows in the result set
				while (rs.next()) {

					String PaymentID = Integer.toString(rs.getInt("PaymentID"));
					String Amount = rs.getString("Amount");
					String PaymentCardNumber = rs.getString("PaymentCardNumber");
					String PaymentType = rs.getString("PaymentType");
					String PaymentDate = Integer.toString(rs.getInt("PaymentDate"));

					// Add into the html table

					output += "<tr><td><input id='hidPaymentIDUpdate' name='hidPaymentIDUpdate' type='hidden' value='"
							+ PaymentID + "'>" + Amount + "</td>";

					output += "<td>" + PaymentCardNumber + "</td>";
					output += "<td>" + PaymentType + "</td>";
					output += "<td>" + PaymentDate+ "</td>";

					// buttons
					output += "<td><input name='btnUpdate' type='button' value='Update' class='btnUpdate btn btn-secondary'></td>"
							+ "<td><input name='btnRemove' type='button' value='Remove' class='btnRemove btn btn-danger' data-PaymentID='"
							+ PaymentID + "'>" + "</td></tr>";

				}

				con.close();

				// Complete the html table
				output += "</table>";
			} catch (Exception e) {
				output = "Error while reading the Payment Details.";
				System.err.println(e.getMessage());
			}
             return output;
		}
		
		// Insert appointment
		public String insertPayment(String Amount, String PaymentCardNumber, String PaymentType, String PaymentDate) {
			String output = "";

			try {
				Connection con = connect();

				if (con == null) {
					return "Error while connecting to the database";
				}

				// create a prepared statement
				String query = " insert into payment (`PaymentID`,Amount`,`PaymentCardNumber`,`PaymentType`,`PaymentDate`)"
						+ " values (?, ?, ?, ?, ?)";

				PreparedStatement preparedStmt = con.prepareStatement(query);
				
				// binding values
				preparedStmt.setInt(1, 0);
				preparedStmt.setString(2, Amount);
				preparedStmt.setString(3, PaymentCardNumber);
				preparedStmt.setString(4, PaymentType);
				preparedStmt.setString(5, PaymentDate);

				// execute the statement
				preparedStmt.execute();
				con.close();

				// Create JSON Object to show successful msg.
				String newPayment = readPayment();
				output = "{\"status\":\"success\", \"data\": \"" + newPayment + "\"}";
			} catch (Exception e) {
				// Create JSON Object to show Error msg.
				output = "{\"status\":\"error\", \"data\": \"Error while Inserting Customer.\"}";
				System.err.println(e.getMessage());
			}

			return output;
		}

		// Update Customer Details
		public String updatePayment(String PaymentID, String Amount, String PaymentCardNumber, String PaymentType,
				String PaymentDate) {
			String output = "";

			try {
				Connection con = connect();

				if (con == null) {
					return "Error while connecting to the database for updating.";
				}
				
				// create a prepared statement
				String query = "UPDATE payment SET Amount=?,PaymentCardNumber=?,PaymentType=?,PaymentDate=? WHERE PaymentID=?";

				PreparedStatement preparedStmt = con.prepareStatement(query);

				// binding values
				preparedStmt.setString(1, Amount);
				preparedStmt.setString(2, PaymentCardNumber);
				preparedStmt.setString(3, PaymentType);
				preparedStmt.setInt(4, Integer.parseInt(PaymentDate));
				preparedStmt.setInt(5, Integer.parseInt(PaymentID));

				// execute the statement
				preparedStmt.execute();
				con.close();
		
				// create JSON object to show successful msg
				String newPayment = readPayment();
				output = "{\"status\":\"success\", \"data\": \"" + newPayment + "\"}";
			} catch (Exception e) {
				output = "{\"status\":\"error\", \"data\": \"Error while Updating Customer Details.\"}";
				System.err.println(e.getMessage());
			}

			return output;
		}
		
		public String deletePayment(String PaymentID) {
			String output = "";

			try {
				Connection con = connect();

				if (con == null) {
					return "Error while connecting to the database for deleting.";
				}

				// create a prepared statement
				String query = "DELETE FROM payment WHERE PaymentID=?";

				PreparedStatement preparedStmt = con.prepareStatement(query);
				
				// binding values
				preparedStmt.setInt(1, Integer.parseInt(PaymentID));
				// execute the statement
				preparedStmt.execute();
				con.close();

				// create JSON Object
				String newPayment = readPayment();
				output = "{\"status\":\"success\", \"data\": \"" + newPayment + "\"}";
			} catch (Exception e) {
				// Create JSON object
				output = "{\"status\":\"error\", \"data\": \"Error while Deleting Customer.\"}";
				System.err.println(e.getMessage());

			}

			return output;
		}

}
