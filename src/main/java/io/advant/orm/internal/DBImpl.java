package io.advant.orm.internal;

import io.advant.orm.DB;
import io.advant.orm.DBConfig;
import io.advant.orm.DBConnection;
import io.advant.orm.exception.ConnectionException;
import io.advant.orm.exception.TableParseException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Marco Romagnolo
 */
public class DBImpl implements DB {

    private static final Logger LOGGER = Logger.getLogger(DBImpl.class.getName());
    private final DBConfig config;
    private final DBConnectionParams params;
    private DBConnection connection;

    public DBImpl(DBConfig config) {
        this.config = config;
        this.params = config.getParams();
        try {
            // Configure Driver Loading for JDBC
            Class.forName(config.getDriver());
            // Configure Entities
            for (String entity : config.getEntities()) {
                Class<?> entityClass = Class.forName(entity);
                EntityReflect.getInstance(entityClass);
            }
        } catch (ClassNotFoundException | TableParseException | NoSuchFieldException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public void connect() throws SQLException {
        Connection conn = DriverManager.getConnection(params.getUri(), params.getProperties());
        connection = new DBConnection(conn, config.getDbType());
    }

    @Override
    public DBConnection getConnection() throws ConnectionException {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            throw new ConnectionException(e);
        }
        return connection;
    }

    @Override
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void disconnect() {
        if (connection!=null) {
            try {
                connection.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }
}