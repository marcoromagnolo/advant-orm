package io.advant.orm.internal;

/*
 * Slightly modified version of the com.ibatis.common.jdbc.ScriptRunner class
 * from the iBATIS Apache project. Only removed dependency on Resource class
 * and a constructor
 * GPSHansl, 06.08.2015: regex for delimiter, rearrange comment/delimiter detection, remove some ide warnings.
 */
/*
 *  Copyright 2004 Clinton Begin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Tool to run database scripts
 */
public class SqlScript {

    private final Connection connection;
    private final boolean stopOnError;
    private final boolean autoCommit;
    private static final String delimiter = ";";

    /**
     * Default constructor
     */
    public SqlScript(Connection connection, boolean autoCommit, boolean stopOnError) {
        this.connection = connection;
        this.autoCommit = autoCommit;
        this.stopOnError = stopOnError;
    }

    /**
     * Runs an SQL script (read in using the Reader parameter)
     *
     * @param reader - the source of the script
     */
    public void run(Reader reader) throws IOException, SQLException {
        try {
            boolean originalAutoCommit = connection.getAutoCommit();
            try {
                if (originalAutoCommit != this.autoCommit) {
                    connection.setAutoCommit(this.autoCommit);
                }
                run(connection, reader);
            } finally {
                connection.setAutoCommit(originalAutoCommit);
            }
        } catch (IOException | SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error running script.  Cause: " + e, e);
        }
    }

    /**
     * Runs an SQL script (read in using the Reader parameter) using the
     * connection passed in
     *
     * @param conn - the connection to use for the script
     * @param reader - the source of the script
     * @throws SQLException if any SQL errors occur
     * @throws IOException if there is an error reading from the Reader
     */
    private void run(Connection conn, Reader reader) throws IOException, SQLException {
        StringBuffer command = null;
        try {
            LineNumberReader lineReader = new LineNumberReader(reader);
            String line;
            while ((line = lineReader.readLine()) != null) {
                if (command == null) {
                    command = new StringBuffer();
                }
                String trimmedLine = line.trim();
                if (trimmedLine.length() >= 1 && !trimmedLine.startsWith("//") && !trimmedLine.startsWith("--")) {
                    if (!trimmedLine.isEmpty() && trimmedLine.endsWith(delimiter)) {
                        command.append(line.substring(0, line.lastIndexOf(delimiter)));
                        command.append(" ");
                        execCommand(conn, command, lineReader);
                        command = null;
                    } else {
                        command.append(line);
                    }
                }
            }
            if (!autoCommit) {
                conn.commit();
            }
        } catch (Exception e) {
            conn.rollback();
            throw new IOException(String.format("Error executing '%s': %s", command, e.getMessage()), e);
        }
    }

    private void execCommand(Connection conn, StringBuffer command, LineNumberReader lineReader) throws SQLException {
        Statement statement = conn.createStatement();
        try {
            statement.execute(command.toString());
        } catch (SQLException e) {
            final String errText = String.format("Error executing '%s' (line %d): %s", command, lineReader.getLineNumber(), e.getMessage());
            if (stopOnError) {
                throw new SQLException(errText, e);
            }
        } finally {
            if (statement!=null) {
                statement.close();
            }
        }
    }

}
