package com.walshydev.jba;

import com.mysql.cj.jdbc.MysqlDataSource;
import com.walshydev.jba.sql.SQLTask;

import javax.naming.Context;
import java.sql.Connection;
import java.sql.SQLException;

public class SQLController {

    private static final Context context = null;
    private static final MysqlDataSource dataSource;

    static {
        dataSource = new MysqlDataSource();
        dataSource.setURL(dataSource.getURL() + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&autoReconnect=true&useSSL=false");
        dataSource.setUrl(dataSource.getUrl() + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&autoReconnect=true&useSSL=false");
    }

    protected static MysqlDataSource getDataSource(){
        return dataSource;
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
