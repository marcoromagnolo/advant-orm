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
import io.advant.orm.type.DBType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Marco Romagnolo
 */
public class DB {

    private static final Logger LOGGER = Logger.getLogger(DB.class.getName());
    private final Params params;
    private final DBType dbType;
    private static DB instance;
    private DBConnection connection;

    private DB(Params params, Set<String> entities) {
        this.params = params;
        this.dbType = params.getDBType();
        try {
            if (entities!=null) {
                for (String entity : entities) {
                    Class<?> entityClass = Class.forName(entity);
                    EntityReflect.getInstance(entityClass);
                }
            }
            Class.forName(dbType.getDriver());
        } catch (ClassNotFoundException | TableParseException | NoSuchFieldException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public DBType getDbType() {
        return dbType;
    }

    public static DB newInstance(Params params, Set<String> entities) throws ConnectionException {
        instance = new DB(params, entities);
        return instance;
    }

    public static DB getInstance() throws ConnectionException {
        return instance;
    }

    public DBConnection getConnection() throws ConnectionException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = connect();
            }
        } catch (SQLException e) {
            throw new ConnectionException(e);
        }
        return connection;
    }

    public static boolean isConnected() {
        try {
            return instance.connection != null && !instance.connection.isClosed();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    private DBConnection connect() throws SQLException {
        Connection conn = DriverManager.getConnection(params.getUri(), params.getProperties());
        connection = new DBConnection(conn, dbType);
        return connection;
    }

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