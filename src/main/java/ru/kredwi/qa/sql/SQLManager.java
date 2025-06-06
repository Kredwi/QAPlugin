package ru.kredwi.qa.sql;

import static ru.kredwi.qa.config.ConfigKeys.DB_DATABASE;
import static ru.kredwi.qa.config.ConfigKeys.DB_ENABLE;
import static ru.kredwi.qa.config.ConfigKeys.DB_HOST;
import static ru.kredwi.qa.config.ConfigKeys.DB_PASSWORD;
import static ru.kredwi.qa.config.ConfigKeys.DB_PORT;
import static ru.kredwi.qa.config.ConfigKeys.DB_USERNAME;
import static ru.kredwi.qa.config.ConfigKeys.DEBUG;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.UUID;

import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.config.ConfigAs;

public class SQLManager implements DatabaseActions {
	
	private static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS `players` ("
			+ "uuid VARCHAR(36) NOT NULL PRIMARY KEY,"
			+ "last_played TIMESTAMP,"
			+ "wins INT"
			+ ");";
	
	private Connection connection;
	private ConfigAs cm;
	
	public SQLManager(ConfigAs cm) {
		this.cm = cm;
	}
	
	public void createTables() throws SQLException, ExceptionInInitializerError {
		if (connectionNonExists()) {
			throw new ExceptionInInitializerError("SQL Connection is not initilized.");
		}
		
		Statement statement = connection.createStatement();
		statement.executeUpdate(CREATE_TABLE_SQL);
	}
	
	public void connect() throws SQLException {

		if (!cm.getAsBoolean(DB_ENABLE)) return;
		
		if (connectionNonExists()) {
			
			String host = cm.getAsString(DB_HOST);
			int port = cm.getAsInt(DB_PORT);
			String databaseName = cm.getAsString(DB_DATABASE);
			String username = cm.getAsString(DB_USERNAME);
			String password = cm.getAsString(DB_PASSWORD);
			
			Connection newConnection = DriverManager.getConnection(
					"jdbc:mysql://"+host+":"+port+"/" + databaseName,
					username,
					password);
			setConnection(newConnection);
		}
	}
	
	public void disconnect() throws SQLException {
		if (connectionExists()) {
			connection.close();
		}
	}
	
	public boolean connectionExists() throws SQLException {
		
		if (!cm.getAsBoolean(DB_ENABLE)) return false;
		
		return connection != null && !connection.isClosed();
	}
	
	public boolean connectionNonExists() throws SQLException {
		
		if (!cm.getAsBoolean(DB_ENABLE)) return false;
		
		return connection == null || connection.isClosed();
	}
	
	public Connection getConnection() {
		if (!cm.getAsBoolean(DB_ENABLE)) return null;
		return connection;
	}

	private void setConnection(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void addPlayerWinCount(UUID uuid) {
		
		if (!cm.getAsBoolean(DB_ENABLE)) return;
		
		String sql = "UPDATE `players` SET `wins` = `wins` + 1 WHERE `uuid` = ?;";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setString(1, uuid.toString());
			
			int rowsUpdated = statement.executeUpdate();
			if (rowsUpdated == 0) {
				if (cm.getAsBoolean(DEBUG)) {
					QAPlugin.getQALogger().warning("Player in addPlayerWinCount is not found");
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void setPlayerLastPlayedNow(UUID uuid) {
		
		if (!cm.getAsBoolean(DB_ENABLE)) return;
		
		String sql = "UPDATE `players` SET `last_played` = ? WHERE `uuid` = ?;";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			statement.setString(2, uuid.toString());
			
			int rowsUpdated = statement.executeUpdate();
			if (rowsUpdated == 0) {
				if (cm.getAsBoolean(DEBUG)) {
					QAPlugin.getQALogger().warning("Player in addPlayerWinCount is not found");
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void addPlayerIfNonExists(UUID uuid) {
		
		if (!cm.getAsBoolean(DB_ENABLE)) return;
		
		String sql = "INSERT IGNORE INTO players (uuid, last_played, wins) VALUES (?, ?, 0);";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setString(1, uuid.toString());
			statement.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
