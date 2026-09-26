package com.badgesystem.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/VerifyBadgeServlet")
public class VerifyBadgeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database Connection Credentials - Ensure db name, user, and password match your setup
    private static final String DB_URL = "jdbc:mysql://localhost:3306/badge_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "12345678";

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ServletException("MySQL JDBC Driver not found.", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String code = request.getParameter("code");

        if (code == null || code.trim().isEmpty()) {
            response.sendRedirect("index.html");
            return;
        }

        code = code.trim();
        String selectSQL = "SELECT verification_code, student_name, course_name, score FROM badges WHERE verification_code = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            stmt = conn.prepareStatement(selectSQL);
            stmt.setString(1, code);

            rs = stmt.executeQuery();

            if (rs.next()) {
                // Read record details from DB
                String verificationCode = rs.getString("verification_code");
                String studentName = rs.getString("student_name");
                String courseName = rs.getString("course_name");
                int score = rs.getInt("score");

                // Set attributes so wallet.jsp can render the badge card
                request.setAttribute("verificationCode", verificationCode);
                request.setAttribute("studentName", studentName);
                request.setAttribute("courseName", courseName);
                request.setAttribute("score", score);

                // Forward to wallet.jsp to display the verified badge card
                request.getRequestDispatcher("/wallet.jsp").forward(request, response);
            } else {
                // If code is not found in database
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().println("<!DOCTYPE html><html><head><title>Invalid Badge</title></head>");
                response.getWriter().println("<body style='font-family:sans-serif; background:#0a0c10; color:#fff; text-align:center; padding:50px;'>");
                response.getWriter().println("<h2 style='color:#ef4444;'>❌ Invalid or Unverified Badge</h2>");
                response.getWriter().println("<p>No badge was found matching code: <code>" + code + "</code></p>");
                response.getWriter().println("<br><a href='index.html' style='color:#06b6d4;'>← Back to Dashboard</a>");
                response.getWriter().println("</body></html>");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.setContentType("text/html");
            response.getWriter().println("<h3 style='color:red;'>Database Error: " + e.getMessage() + "</h3>");
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException ignored) {}
            if (stmt != null) try { stmt.close(); } catch (SQLException ignored) {}
            if (conn != null) try { conn.close(); } catch (SQLException ignored) {}
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}