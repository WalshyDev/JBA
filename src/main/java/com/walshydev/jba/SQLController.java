package com.walshydev.jba;

import com.mysql.cj.jdbc.MysqlDataSource;
import com.walshydev.jba.sql.SQLTask;

import javax.naming.Context;
import java.sql.Connection;
import java.sql.SQLException;

public class SQLController {

    private static final Context context = null;
    private static MysqlDataSource dataSource;

    protected static void setDataSource(MysqlDataSource source){
        dataSource = source;
    }

    private static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void runSqlTask(SQLTask toRun) throws SQLException {
        Connection c = getConnection();
        toRun.execute(c);
        if (!c.isClosed())
            c.close();
    }
}
