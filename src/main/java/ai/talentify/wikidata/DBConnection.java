package ai.talentify.wikidata;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

public class DBConnection {
	private final static Connection CONNECTION = buildConnection();

	private DBConnection() {
		super();

	}

	private static Connection buildConnection() {
		System.out.println("-------- PostgreSQL " + "JDBC Connection Testing ------------");

		try {

			Class.forName("org.postgresql.Driver");

		} catch (ClassNotFoundException e) {

			System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
			e.printStackTrace();
			return null;

		}

		System.out.println("PostgreSQL JDBC Driver Registered!");

		try {

			return  DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/knowledgeBank", "postgres", "root");

		} catch (SQLException e) {

			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return null;

		}

	}

	public static void main(String[] args) {

	}

	public static Connection getInstance() {
		// TODO Auto-generated method stub
		return CONNECTION;
	}
}