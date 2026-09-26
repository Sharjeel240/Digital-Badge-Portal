package com.badgesystem.servlet;

import com.badgesystem.util.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet("/DeleteBadgeServlet")
public class DeleteBadgeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String badgeId = request.getParameter("badgeId");
        String studentId = request.getParameter("studentId");

        if (badgeId != null && !badgeId.isEmpty()) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM Badges WHERE badge_id = ?")) {
                
                ps.setString(1, badgeId);
                ps.executeUpdate();
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Redirect back to the student's wallet so the removed badge disappears instantly
        response.sendRedirect("wallet.jsp?studentId=" + (studentId != null ? studentId : "101"));
    }
}