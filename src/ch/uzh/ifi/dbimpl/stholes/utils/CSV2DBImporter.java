package ch.uzh.ifi.dbimpl.stholes.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class CSV2DBImporter {

	private static final String USERNAME = "SA";
	private static final String PASSWORD = "";
	private static String DB_FILE = "db/import";

	private static String TABLE_NAME_PREFIX = "dataset_";

	private static List<File> filesToImport;
	private static Connection connection;
	private static String currentTableName;
	private static char currentTableSuffix = 'a';

	public static void main(String[] args) {
		prepareFileImport(args);
		prepareDatabase();
		importDataFromFiles();
		closeDatabaseConnection();
		System.out.println("Done.");
	}

	protected static void closeDatabaseConnection() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void importDataFromFiles() {
		for (File file : filesToImport) {
			importFile(file);
		}
	}

	private static void importFile(File file) {
		BufferedReader bufferedReader = null;
		try {
			currentTableName = nextTableName();
			System.out.println("Prepare table " + currentTableName + "...");
			createTable();
			connection.commit();

			bufferedReader = new BufferedReader(new FileReader(file));
			PreparedStatement insertStatement = connection.prepareStatement("insert into " + currentTableName
					+ " (x, y) values (?,?)");

			System.out.println("Insert values...");
			addValues(bufferedReader, insertStatement);
			insertStatement.executeBatch();
			connection.commit();

			System.out.println("Create indices on (x, y)...");
			createIndices();
			connection.commit();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {
			}
		}
	}

	protected static void addValues(BufferedReader bufferedReader, PreparedStatement insertStatement)
			throws IOException, SQLException {
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			String[] values = line.split(",");
			if (values.length == 2) {
				insertStatement.setDouble(1, Double.valueOf(values[0]));
				insertStatement.setDouble(2, Double.valueOf(values[1]));
				insertStatement.addBatch();
			} else {
				System.err.println("Non-conforming values: " + line);
			}
		}
	}

	private static void prepareDatabase() {
		// try {
		// connection = DriverManager.getConnection("jdbc:hsqldb:file:"
		// + dbFile + ";ifexists=true", USERNAME, PASSWORD);
		try {
			connection = DriverManager.getConnection("jdbc:hsqldb:file:" +
					DB_FILE, USERNAME, PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//
		// System.out.println("Create table...");
		// currentTableName = nextTableName();
		// createTable();
		// connection.commit();
		// System.out.println("Insert random values...");
		// insertRandomValues();
		// connection.commit();
		// System.out.println("Create indices on (x, y)...");
		// createIndices();
		// connection.commit();
		// connection.close();
		// System.out.println("Done. (" + (end - start) + "ms)");
		// } catch (SQLException e) {
		// System.err.println("SQLException: " + e.getMessage());
		// }
	}

	private static void createTable() throws SQLException {
		Statement createStatement = connection.createStatement();
		createStatement.execute("create table " + currentTableName + " (id integer identity, x double, y double);");
	}

	private static String nextTableName() {
		String nextTableName = TABLE_NAME_PREFIX + currentTableSuffix;
		currentTableSuffix = (char) (currentTableSuffix + 1);
		return nextTableName;
	}

	private static void createIndices() throws SQLException {
		PreparedStatement createIndexStatement = connection.prepareStatement("create index idx_" + currentTableName
				+ " on "
				+ currentTableName + " (x, y)");
		createIndexStatement.execute();
	}

	protected static void prepareFileImport(String[] args) {
		filesToImport = new LinkedList<File>();
		for (String fileName : args) {
			File file = new File(fileName);
			if (file.exists()) {
				filesToImport.add(file);
			} else {
				System.err.println("Could not import file '" + fileName + "'.");
			}
		}
	}

}
