package io.advant.orm.test.testsuite.derby;

import io.advant.orm.DB;
import io.advant.orm.DBLocalParams;
import io.advant.orm.exception.ConnectionException;
import io.advant.orm.exception.OrmException;
import io.advant.orm.test.testcase.DefaultParams;
import io.advant.orm.test.testcase.PrintUtil;
import io.advant.orm.test.testcase.TestDropDB;
import io.advant.orm.type.DBLocalType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Marco Romagnolo
 */
public class DerbyDropDB {

    private static TestDropDB test;

    @BeforeClass
    public static void connect() throws ConnectionException {
        PrintUtil.suite(DerbyDropDB.class.getName());
        Connection connection = DB.newInstance(new DerbyLocalParams(), DefaultParams.getEntities()).getConnection();
        test = new TestDropDB(connection);
    }

    @Test
    public void drop() throws ConnectionException, OrmException {
        test.drop("DERBY");
    }

    @AfterClass
    public static void disconnect() throws ConnectionException {
        DB.getInstance().disconnect();
    }
}
