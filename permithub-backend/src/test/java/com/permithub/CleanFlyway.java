package com.permithub;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class CleanFlyway {
    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/permithub_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC", "root", "")) {
            try (Statement stmt = conn.createStatement()) {
                int count = stmt.executeUpdate("DELETE FROM schema_history WHERE version='7' AND success=0");
                System.out.println("Deleted " + count + " failed migrations.");
                count = stmt.executeUpdate("ALTER ALGORITHM=UNDEFINED VIEW v_current_semester_students AS SELECT 1 AS id");
                stmt.executeUpdate("DROP VIEW IF EXISTS v_current_semester_students");
                stmt.executeUpdate("DROP VIEW IF EXISTS v_hod_pending_approvals");
                System.out.println("Dropped views from previous failed migration.");
            } catch (Exception e) {
                System.out.println("SQL execution error (could be no views to drop): " + e.getMessage());
            }
        }
    }
}
