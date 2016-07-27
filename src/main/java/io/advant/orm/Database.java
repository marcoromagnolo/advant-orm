/**
 * Copyright 2016 Advant I/O
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.advant.orm;

import io.advant.orm.exception.ConnectionException;
import io.advant.orm.exception.TableParseException;
import io.advant.orm.internal.EntityReflect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class Database {

    private static final Logger LOGGER = Logger.getLogger(Database.class.getName());
    private final Params params;
    private static Database instance;
    private Connection connection;

    private Database(Params params) {
        this.params = params;
        try {
            if (params.getEntities()!=null) {
                for (String entity : params.getEntities()) {
                    Class<?> entityClass = Class.forName(entity);
                    EntityReflect.getInstance(entityClass);
                }
            }
            if (params.getProperties() == null) {
                params.setProperties(new Properties());
            }
            if (params.getUser() != null) {
                params.getProperties().put("user", params.getUser());
            }
            if (params.getPassword() != null) {
                params.getProperties().put("password", params.getPassword());
            }
            if (params.getDbType() != null) {
                Class.forName(params.getDbType().getDriver());
            } else if (params.getDriver() != null) {
                Class.forName(params.getDriver());
            }
        } catch (ClassNotFoundException | TableParseException | NoSuchFieldException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public static Database newInstance(Params params) throws ConnectionException {
        instance = new Database(params);
        return instance;
    }

    public static Database getInstance() throws ConnectionException {
        return instance;
    }

    public Connection getConnection() throws ConnectionException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = connect();
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new ConnectionException(e);
        }
        return connection;
    }

    private Connection connect() throws ClassNotFoundException, SQLException {
        Connection connection = null;
        switch (params.getDbType()) {
            case MYSQL:
                connection = DriverManager.getConnection("jdbc:mysql://" + params.getHost() + ":" + params.getPort() + "/" + params.getDatabase(), params.getProperties());
                break;
            case POSTGRESQL:
                connection = DriverManager.getConnection("jdbc:postgresql://" + params.getHost() + ":" + params.getPort() + "/" + params.getDatabase(), params.getProperties());
                break;
            case IBMDB2:
                connection = DriverManager.getConnection("jdbc:db2://" + params.getHost() + ":" + params.getPort() + "/" + params.getDatabase(), params.getProperties());
                break;
            case MSSQL:
                connection = DriverManager.getConnection("jdbc:microsoft:sqlserver://" + params.getHost() + ":" + params.getPort() + "/" + params.getDatabase(), params.getProperties());
                break;
            case ORACLE:
                connection = DriverManager.getConnection("jdbc:oracle:thin:@" + params.getHost() + ":" + params.getPort() + "/" + params.getDatabase(), params.getProperties());
                break;
            case DERBY:
                connection = DriverManager.getConnection("jdbc:derby:" + params.getDatabase(), params.getProperties());
                break;
            case H2:
                connection = DriverManager.getConnection("jdbc:h2:" + params.getDatabase(), params.getProperties());
                break;
            case HSQLDB:
                connection = DriverManager.getConnection("jdbc:hsqldb:" + params.getDatabase(), params.getProperties());
                break;
        }
        return connection;
    }

    public void disconnect() throws SQLException {
        if (connection!=null) {
            connection.close();
        }
    }

    public void begin() throws SQLException {
        connection.setAutoCommit(false);
    }

    public void commit() throws SQLException {
        connection.commit();
    }

    public void rollback() throws SQLException {
        connection.rollback();
    }
}
