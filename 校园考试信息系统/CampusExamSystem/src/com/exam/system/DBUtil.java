package com.exam.system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBUtil {
    // 数据库配置信息
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=CampusExamSystem;encrypt=false";
    private static final String USER = "sa";
    private static final String PASSWORD = "123";

    // 加载驱动
    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 获取数据库连接
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            System.out.println("数据库连接失败！请检查账号密码或服务是否开启。");
            e.printStackTrace();
            return null;
        }
    }


    public static void executeUpdate(String sql, Object... params) throws Exception {
        Connection conn = null;
        java.sql.PreparedStatement ps = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            ps.executeUpdate();
        } finally {
            close(conn, ps, null);
        }
    }



    // 释放资源
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}