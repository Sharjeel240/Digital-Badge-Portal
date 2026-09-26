package com.badgesystem.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/IssueBadgeServlet")
public class IssueBadgeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database Connection Credentials - Update to match your local MySQL settings
    private static final String DB_URL = "jdbc:mysql://localhost:3306/badge_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "12345678";

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            // Explicitly load the MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ServletException("MySQL JDBC Driver not found in project dependencies.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set request character encoding for special characters
        request.setCharacterEncoding("UTF-8");

        // 1. Extract dynamic user parameters from the HTML form
        String studentName = request.getParameter("studentName");
        String courseName = request.getParameter("courseName");
        String scoreStr = request.getParameter("score");

        // Fallback for null or empty string entries
        if (studentName == null || studentName.trim().isEmpty()) {
            studentName = "Anonymous Student";
        } else {
            studentName = studentName.trim();
        }

        if (courseName == null || courseName.trim().isEmpty()) {
            courseName = "General Skill Assessment";
        } else {
            courseName = courseName.trim();
        }

        // Safe integer parsing for scores (defaults to 0 if non-numeric or empty)
        int score = 0;
        if (scoreStr != null && !scoreStr.trim().isEmpty()) {
            try {
                score = Integer.parseInt(scoreStr.trim());
                // Clamp score between 0 and 100
                if (score < 0) score = 0;
                if (score > 100) score = 100;
            } catch (NumberFormatException e) {
                score = 0;
            }
        }

        // 2. Generate an 8-character unique verification code (e.g., BADGE-A1B2C3D4)
        String verificationCode = "BADGE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Optional default Student ID if not provided in the form
        int studentId = 101; 

        // 3. Robust Parameterized SQL query explicitly specifying table columns
        String insertSQL = "INSERT INTO badges (verification_code, student_id, student_name, course_name, score) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            stmt = conn.prepareStatement(insertSQL);

            stmt.setString(1, verificationCode);
            stmt.setInt(2, studentId);
            stmt.setString(3, studentName);
            stmt.setString(4, courseName);
            stmt.setInt(5, score);

            stmt.executeUpdate();

            // 4. Attach request attributes to pass to wallet.jsp
            request.setAttribute("verificationCode", verificationCode);
            request.setAttribute("studentName", studentName);
            request.setAttribute("courseName", courseName);
            request.setAttribute("score", score);

            // Forward execution to wallet.jsp
            request.getRequestDispatcher("/wallet.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            response.setContentType("text/html");
            response.getWriter().println("<h3 style='color:red;'>Database Error: " + e.getMessage() + "</h3>");
            response.getWriter().println("<p>Please verify your SQL database table contains columns: <code>verification_code</code>, <code>student_id</code>, <code>student_name</code>, <code>course_name</code>, <code>score</code>.</p>");
        } finally {
            // Clean up DB resources
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException ignored) {}
            }
            if (conn != null) {
                try { conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect direct GET access attempts back to the home portal page
        response.sendRedirect("index.html");
    }
}