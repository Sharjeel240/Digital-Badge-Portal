<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Skill Wallet</title>
    <!-- Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin="anonymous">
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@700;800&family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        :root {
            --bg-dark: #0a0c10;
            --glass-bg: rgba(255, 255, 255, 0.04);
            --glass-border: rgba(255, 255, 255, 0.08);
            --card-inner-bg: rgba(0, 0, 0, 0.25);
            --text-main: #f8fafc;
            --text-muted: #94a3b8;
            --accent-cyan: #06b6d4;
            --accent-glow: #6366f1;
            --gold: #f59e0b;
            --silver: #94a3b8;
            --bronze: #d97706;
            --radius-lg: 24px;
            --radius-md: 16px;
        }

        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        body {
            font-family: 'Plus Jakarta Sans', sans-serif;
            background-color: var(--bg-dark);
            color: var(--text-main);
            min-height: 100vh;
            padding: 40px 20px;
            background-image: 
                radial-gradient(circle at 15% 20%, rgba(99, 102, 241, 0.12) 0%, transparent 45%),
                radial-gradient(circle at 85% 80%, rgba(6, 182, 212, 0.1) 0%, transparent 45%);
            background-attachment: fixed;
        }

        .container {
            max-width: 900px;
            margin: 0 auto;
        }

        .header {
            text-align: center;
            margin-bottom: 32px;
        }

        .header h1 {
            font-family: 'Playfair Display', serif;
            font-size: 2.5rem;
            background: linear-gradient(135deg, #ffffff 30%, #94a3b8 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            margin-bottom: 8px;
        }

        .header p {
            color: var(--text-muted);
            font-size: 0.95rem;
            text-transform: uppercase;
            letter-spacing: 0.15em;
        }

        .bento-card {
            background: var(--glass-bg);
            backdrop-filter: blur(20px);
            border: 1px solid var(--glass-border);
            border-radius: var(--radius-lg);
            padding: 32px;
            margin-bottom: 24px;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
        }

        .badge-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
            gap: 20px;
            margin-top: 20px;
        }

        .badge-card {
            background: var(--card-inner-bg);
            border: 1px solid var(--glass-border);
            border-radius: var(--radius-md);
            padding: 20px;
            position: relative;
            transition: transform 0.3s ease;
        }

        .badge-card:hover {
            transform: translateY(-4px);
        }

        .badge-card.gold { border-top: 3px solid var(--gold); }
        .badge-card.silver { border-top: 3px solid var(--silver); }
        .badge-card.bronze { border-top: 3px solid var(--bronze); }

        .badge-title {
            font-size: 1.1rem;
            font-weight: 700;
            color: var(--text-main);
            margin-bottom: 8px;
        }

        .badge-meta {
            font-size: 0.85rem;
            color: var(--text-muted);
            margin-bottom: 6px;
        }

        .tier-tag {
            display: inline-block;
            font-size: 0.75rem;
            font-weight: 700;
            padding: 4px 10px;
            border-radius: 99px;
            margin-top: 10px;
        }

        .tag-gold { background: rgba(245, 158, 11, 0.15); color: var(--gold); border: 1px solid rgba(245, 158, 11, 0.3); }
        .tag-silver { background: rgba(148, 163, 184, 0.15); color: var(--silver); border: 1px solid rgba(148, 163, 184, 0.3); }
        .tag-bronze { background: rgba(217, 119, 6, 0.15); color: var(--bronze); border: 1px solid rgba(217, 119, 6, 0.3); }

        .code-box {
            background: rgba(0, 0, 0, 0.4);
            padding: 8px 12px;
            border-radius: 8px;
            font-family: monospace;
            font-size: 0.85rem;
            color: var(--accent-cyan);
            margin-top: 12px;
            word-break: break-all;
            display: block;
        }

        .btn-back {
            display: inline-block;
            padding: 10px 20px;
            border-radius: 99px;
            background: rgba(255, 255, 255, 0.08);
            border: 1px solid var(--glass-border);
            color: var(--text-main);
            text-decoration: none;
            font-size: 0.85rem;
            font-weight: 600;
            transition: all 0.3s ease;
            margin-top: 20px;
        }

        .btn-back:hover {
            background: rgba(255, 255, 255, 0.15);
            border-color: var(--accent-cyan);
        }

        .error-msg {
            color: #ef4444;
            background: rgba(239, 68, 68, 0.1);
            border: 1px solid rgba(239, 68, 68, 0.2);
            padding: 16px;
            border-radius: var(--radius-md);
            margin-top: 16px;
        }
    </style>
</head>
<body>

<div class="container">
    <div class="header">
        <h1>Student Digital Wallet</h1>
        <p>Verified Skill Credentials</p>
    </div>

    <%
        // Database credentials - Change to match your local database settings
        String dbUrl = "jdbc:mysql://localhost:3306/badge_db";
        String dbUser = "root";
        String dbPass = "12345678";

        // Read request parameters / forward attributes
        String studentIdParam = request.getParameter("studentId");
        String studentNameAttr = (String) request.getAttribute("studentName");
        String courseNameAttr = (String) request.getAttribute("courseName");
        String codeAttr = (String) request.getAttribute("verificationCode");
        Object scoreAttrObj = request.getAttribute("score");

        boolean hasForwardData = (studentNameAttr != null && codeAttr != null);
        boolean hasStudentId = (studentIdParam != null && !studentIdParam.trim().isEmpty());

        String displayStudentName = hasForwardData ? studentNameAttr : "Student Wallet";
        if (hasStudentId) {
            displayStudentName = "Student ID #" + studentIdParam;
        }
    %>

    <div class="bento-card">
        <h2>💼 Earned Badges for <%= displayStudentName %></h2>
        <p style="color: var(--text-muted); font-size: 0.9rem; margin-top: 6px;">
            Tamper-evident credentials linked to database verification records.
        </p>

        <div class="badge-grid">
        <%
            // 1. IF FORWARDED DIRECTLY FROM IssueBadgeServlet
            if (hasForwardData) {
                int score = (scoreAttrObj != null) ? (Integer) scoreAttrObj : 0;
                String tierClass = "bronze";
                String tagClass = "tag-bronze";
                String tierName = "🥉 Bronze Tier";

                if (score >= 90) {
                    tierClass = "gold";
                    tagClass = "tag-gold";
                    tierName = "🥇 Gold Tier";
                } else if (score >= 75) {
                    tierClass = "silver";
                    tagClass = "tag-silver";
                    tierName = "🥈 Silver Tier";
                }
        %>
            <div class="badge-card <%= tierClass %>">
                <div class="badge-title"><%= courseNameAttr %></div>
                <div class="badge-meta">Issued To: <strong><%= studentNameAttr %></strong></div>
                <div class="badge-meta">Score: <strong><%= score %>%</strong></div>
                <span class="tier-tag <%= tagClass %>"><%= tierName %></span>
                <span class="code-box">Code: <%= codeAttr %></span>
            </div>
        <%
            } 
            // 2. IF ACCESSED VIA STUDENT ID FORM QUERY
            else if (hasStudentId) {
                Connection conn = null;
                PreparedStatement stmt = null;
                ResultSet rs = null;
                boolean foundBadges = false;

                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    conn = DriverManager.getConnection(dbUrl, dbUser, dbPass);

                    // Robust SQL query selecting explicit columns to avoid missing field errors
                    String sql = "SELECT verification_code, student_name, course_name, score FROM badges WHERE student_id = ? OR student_name LIKE ?";
                    stmt = conn.prepareStatement(sql);
                    
                    int studentId = 0;
                    try { studentId = Integer.parseInt(studentIdParam); } catch (Exception e) {}
                    
                    stmt.setInt(1, studentId);
                    stmt.setString(2, "%" + studentIdParam + "%");
                    
                    rs = stmt.executeQuery();

                    while (rs.next()) {
                        foundBadges = true;
                        String code = rs.getString("verification_code");
                        String name = rs.getString("student_name");
                        String course = rs.getString("course_name");
                        int score = rs.getInt("score");

                        String tierClass = "bronze";
                        String tagClass = "tag-bronze";
                        String tierName = "🥉 Bronze Tier";

                        if (score >= 90) {
                            tierClass = "gold";
                            tagClass = "tag-gold";
                            tierName = "🥇 Gold Tier";
                        } else if (score >= 75) {
                            tierClass = "silver";
                            tagClass = "tag-silver";
                            tierName = "🥈 Silver Tier";
                        }
        %>
            <div class="badge-card <%= tierClass %>">
                <div class="badge-title"><%= course %></div>
                <div class="badge-meta">Issued To: <strong><%= name %></strong></div>
                <div class="badge-meta">Score: <strong><%= score %>%</strong></div>
                <span class="tier-tag <%= tagClass %>"><%= tierName %></span>
                <span class="code-box">Code: <%= code %></span>
            </div>
        <%
                    }

                    if (!foundBadges) {
        %>
            <p style="color: var(--text-muted); grid-column: 1 / -1; margin-top: 12px;">
                No badges found for this student ID or name.
            </p>
        <%
                    }
                } catch (Exception e) {
        %>
            <div class="error-msg" style="grid-column: 1 / -1;">
                <strong>Error loading wallet:</strong> <%= e.getMessage() %>
            </div>
        <%
                } finally {
                    if (rs != null) try { rs.close(); } catch (Exception e) {}
                    if (stmt != null) try { stmt.close(); } catch (Exception e) {}
                    if (conn != null) try { conn.close(); } catch (Exception e) {}
                }
            } else {
        %>
            <p style="color: var(--text-muted); grid-column: 1 / -1;">
                No student specified. Please issue a badge or search via Student ID.
            </p>
        <%
            }
        %>
        </div>

        <a href="index.html" class="btn-back">← Back to Portal Dashboard</a>
    </div>
</div>

</body>
</html>