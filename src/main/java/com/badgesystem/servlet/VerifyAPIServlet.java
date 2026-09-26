package com.badgesystem.servlet;

import com.badgesystem.util.DBConnection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/api/verify")
public class VerifyAPIServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String code = request.getParameter("code");

        if (code == null || code.trim().isEmpty()) {
            out.print("{\"valid\": false, \"message\": \"No code provided\"}");
            return;
        }

        String sql = "SELECT b.badge_id, s.name AS student_name, m.title AS module_title, b.issued_at " +
                     "FROM Badges b JOIN Students s ON b.student_id = s.student_id " +
                     "JOIN Modules m ON b.module_id = m.module_id WHERE b.verification_code = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    out.print(String.format(
                        "{\"valid\": true, \"student\": \"%s\", \"module\": \"%s\", \"issued\": \"%s\"}",
                        rs.getString("student_name"),
                        rs.getString("module_title"),
                        rs.getDate("issued_at").toString()
                    ));
                } else {
                    out.print("{\"valid\": false, \"message\": \"Code not found\"}");
                }
            }
        } catch (Exception e) {
            out.print("{\"valid\": false, \"message\": \"" + e.getMessage() + "\"}");
        }
    }
}