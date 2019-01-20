package ru.slayter.stock.advisor.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

import ru.slayter.stock.advisor.tasks.Task;
import ru.slayter.stock.commons.Emitent;
import ru.slayter.stock.strategies.Strategy;

public class Database {
	private String url;
	private Connection connection;
	private PreparedStatement statement;
	private ResultSet resultSet;
	private String lastErrorMessage;

	public Database(String url) {
		super();
		this.url = url;
		this.connection = null;
		this.statement = null;
		this.resultSet = null;
	};

	public String getLastErrorMessage() {
		return lastErrorMessage;
	}

	private void setLastErrorMessage(String prefix, String errorMessage) {
		StringBuilder errm = new StringBuilder();
		if ((prefix != null) && (!prefix.isEmpty())) {
			errm.append(prefix);
		}
		if ((errorMessage != null) && (!errorMessage.isEmpty())) {
			if (errm.toString().isEmpty()) {
				errm.append(errorMessage);
			} else {
				errm.append(" ").append(errorMessage);
			}
		}
		this.lastErrorMessage = errm.toString();
	}

	private boolean connect() throws ClassNotFoundException, SQLException {
		this.connection = null;
		Class.forName("org.sqlite.JDBC");
		this.connection = DriverManager.getConnection(this.url);
		return (this.connection != null);
	}

	public CopyOnWriteArrayList<Task> getTasksList() {
		CopyOnWriteArrayList<Task> taskList = null;
		try {
			if (connect()) {
				this.statement = this.connection.prepareStatement(
						"SELECT t.id\r\n" + 
						"      ,t.description\r\n" + 
						"      ,t.id_emitent\r\n" + 
						"      ,e.ticker\r\n" + 
						"      ,e.name\r\n" + 
						"      ,e.engine\r\n" + 
						"      ,e.market\r\n" + 
						"      ,e.board\r\n" + 
						"      ,e.security\r\n" + 
						"      ,e.IS_SHORT_ALLOWED\r\n" + 
						"      ,e.LOT_SIZE\r\n" + 
						"      ,t.id_strategy\r\n" + 
						"      ,s.SIGN\r\n" + 
						"      ,s.FILE\r\n" + 
						"  FROM tasks t\r\n" + 
						"       INNER JOIN emitents e ON e.id = t.id_emitent\r\n" + 
						"       INNER JOIN strategies s ON s.id = t.id_strategy\r\n" + 
						" WHERE t.active = 1\r\n" + 
						"   AND e.active = 1");
				resultSet = this.statement.executeQuery();

				taskList = new CopyOnWriteArrayList<Task>();
				while (resultSet.next()) {
					// task
					Task task = new Task();
					task.setId(resultSet.getInt("id"));
					task.setDescription(resultSet.getString("DESCRIPTION"));
					// emitent
					Emitent emitent = new Emitent();
					emitent.setId(resultSet.getInt("ID_EMITENT"));
					emitent.setTicker(resultSet.getString("TICKER").trim());
					emitent.setName(resultSet.getString("NAME").trim());
					emitent.setEngine(resultSet.getString("ENGINE").trim());
					emitent.setMarket(resultSet.getString("MARKET").trim());
					emitent.setBoard(resultSet.getString("BOARD").trim());
					emitent.setSecurity(resultSet.getString("SECURITY").trim());
					emitent.setShortAllowed(resultSet.getBoolean("IS_SHORT_ALLOWED"));
					emitent.setLotSize(resultSet.getInt("LOT_SIZE"));
					task.setEmitent(emitent);
					// strategy
					Strategy strategy = new Strategy();
					strategy.setId(resultSet.getInt("ID_STRATEGY"));
					strategy.setSign(resultSet.getString("SIGN"));
					strategy.setFileName(resultSet.getString("FILE"));
					task.setStrategy(strategy);
					taskList.add(task);
				}
			}
		} catch (ClassNotFoundException e) {
			this.setLastErrorMessage("Error in getTaskList", e.getMessage());
			taskList = null;
		} catch (SQLException e) {
			this.setLastErrorMessage("Error in getTaskList", e.getMessage());
			taskList = null;
		} finally {
			this.disconnect();
		}
		return taskList;
	}

	public void disconnect() {
		try {
			if (!this.resultSet.isClosed()) {
				this.resultSet.close();
			}

			if (!this.statement.isClosed()) {
				this.statement.close();
			}

			if (!this.connection.isClosed()) {
				this.connection.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			// обрабатывать не станем - нет реальной нужды
		}
	}

	public Properties getTaskProperties(int id) {
		Properties properties = new Properties();
		try {
			if (connect()) {
				this.statement = this.connection.prepareStatement("select key, value from tasks_properties where id_task = ?");
				this.statement.setInt(1, id);
				this.resultSet = this.statement.executeQuery();
				while (resultSet.next()) {
					properties.setProperty(resultSet.getString("KEY"), resultSet.getString("VALUE"));
				}
			}
		} catch (ClassNotFoundException e) {
			this.setLastErrorMessage("Error in getTaskProperties", e.getMessage());
		} catch (SQLException e) {
			this.setLastErrorMessage("Error in getTaskProperties", e.getMessage());
		} finally {
			this.disconnect();
		}
		return properties;
	}

	public Properties getStrategyProperties(int id) {
		Properties properties = new Properties();
		try {
			if (connect()) {
				this.statement = this.connection.prepareStatement("select key, value from strategy_properties where id_strategy = ?");
				this.statement.setInt(1, id);
				this.resultSet = this.statement.executeQuery();
				while (resultSet.next()) {
					properties.setProperty(resultSet.getString("KEY"), resultSet.getString("VALUE"));
				}				
			}			
		} catch (ClassNotFoundException e) {
			this.setLastErrorMessage("Error in getTaskProperties", e.getMessage());
		} catch (SQLException e) {
			this.setLastErrorMessage("Error in getTaskProperties", e.getMessage());
		} finally {
			this.disconnect();
		}
		return properties;
	}
}
