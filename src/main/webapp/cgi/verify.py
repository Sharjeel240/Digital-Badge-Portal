#!/usr/bin/env python3
import sys
import mysql.connector

# Force UTF-8 encoding for standard output on Windows
if sys.stdout.encoding != 'utf-8':
    sys.stdout.reconfigure(encoding='utf-8')

# Read verification code passed from ProcessBuilder
code = sys.argv[1].strip() if len(sys.argv) > 1 else ""

if not code:
    print("""
    <div style='padding: 16px; border-radius: 12px; background: rgba(239, 68, 68, 0.1); border: 1px solid #ef4444; color: #ef4444; font-weight: 600;'>
        ⚠️ Please enter a valid verification code.
    </div>
    """)
    sys.exit(0)

# Database Connection Credentials
DB_CONFIG = {
    'host': 'localhost',
    'user': 'root',
    'password': '12345678',  # Update with your MySQL password
    'database': 'badge_db',
    'port': 3306
}

try:
    conn = mysql.connector.connect(**DB_CONFIG)
    cursor = conn.cursor(dictionary=True)

    query = "SELECT verification_code, student_name, course_name, score, created_at FROM badges WHERE verification_code = %s"
    cursor.execute(query, (code,))
    row = cursor.fetchone()

    if row:
        score = row['score']
        
        # Dynamic Badge Tiers with adaptive theme contrast
        if score >= 90:
            tier_label = "🥇 Gold Tier"
            tier_style = "background: rgba(245, 158, 11, 0.15); color: #d97706; border: 1px solid rgba(245, 158, 11, 0.4);"
        elif score >= 75:
            tier_label = "🥈 Silver Tier"
            tier_style = "background: rgba(100, 116, 139, 0.15); color: #475569; border: 1px solid rgba(100, 116, 139, 0.4);"
        else:
            tier_label = "🥉 Bronze Tier"
            tier_style = "background: rgba(180, 83, 9, 0.15); color: #b45309; border: 1px solid rgba(180, 83, 9, 0.4);"

        print(f"""
        <style>
            .cgi-badge-container {{
                font-family: 'Plus Jakarta Sans', system-ui, -apple-system, sans-serif;
                padding: 24px;
                border-radius: 16px;
                background: var(--card-bg, rgba(255, 255, 255, 0.05));
                border: 1px solid var(--border-color, rgba(16, 185, 129, 0.3));
                color: var(--text-main, #0f172a);
                max-width: 480px;
                margin: 0 auto;
                box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.05);
            }}
            .cgi-header {{
                display: flex;
                align-items: center;
                justify-content: space-between;
                padding-bottom: 14px;
                border-bottom: 1px solid rgba(148, 163, 184, 0.2);
                margin-bottom: 18px;
            }}
            .cgi-status-title {{
                display: flex;
                align-items: center;
                gap: 8px;
                font-weight: 700;
                font-size: 0.95rem;
                color: #10b981;
                letter-spacing: 0.03em;
            }}
            .cgi-engine-pill {{
                font-size: 0.72rem;
                font-weight: 600;
                padding: 3px 10px;
                border-radius: 99px;
                background: rgba(6, 182, 212, 0.12);
                color: #0891b2;
                border: 1px solid rgba(6, 182, 212, 0.25);
            }}
            .cgi-row {{
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 12px;
                font-size: 0.9rem;
            }}
            .cgi-label {{
                color: var(--text-muted, #64748b);
                font-weight: 500;
            }}
            .cgi-value {{
                font-weight: 700;
                color: var(--text-main, #0f172a);
            }}
            .cgi-code-wrapper {{
                margin-top: 16px;
                padding: 10px 14px;
                border-radius: 10px;
                background: rgba(0, 0, 0, 0.06);
                border: 1px solid rgba(0, 0, 0, 0.08);
                display: flex;
                justify-content: space-between;
                align-items: center;
                font-family: monospace;
                font-size: 0.85rem;
            }}
        </style>

        <div class='cgi-badge-container'>
            <div class='cgi-header'>
                <div class='cgi-status-title'>
                    <span style='background: #10b981; color: #ffffff; width: 20px; height: 20px; border-radius: 50%; display: inline-flex; align-items: center; justify-content: center; font-size: 11px;'>✓</span>
                    VERIFIED CREDENTIAL
                </div>
                <span class='cgi-engine-pill'>Python CGI Engine</span>
            </div>

            <div class='cgi-row'>
                <span class='cgi-label'>Course / Module</span>
                <span class='cgi-value'>{row['course_name']}</span>
            </div>

            <div class='cgi-row'>
                <span class='cgi-label'>Issued To</span>
                <span class='cgi-value'>{row['student_name']}</span>
            </div>

            <div class='cgi-row'>
                <span class='cgi-label'>Score & Tier</span>
                <div>
                    <span class='cgi-value' style='margin-right: 6px;'>{row['score']}%</span>
                    <span style='{tier_style} font-size: 0.72rem; font-weight: 700; padding: 2px 8px; border-radius: 99px;'>{tier_label}</span>
                </div>
            </div>

            <div class='cgi-row'>
                <span class='cgi-label'>Issued Date</span>
                <span class='cgi-value' style='font-size: 0.85rem; font-weight: 600;'>{row['created_at']}</span>
            </div>

            <div class='cgi-code-wrapper'>
                <span style='color: var(--text-muted, #64748b); font-weight: 600;'>VERIFICATION CODE</span>
                <span style='color: #0891b2; font-weight: 700;'>{row['verification_code']}</span>
            </div>
        </div>
        """)
    else:
        print(f"""
        <div style='padding: 20px; border-radius: 14px; background: rgba(239, 68, 68, 0.08); border: 1px solid rgba(239, 68, 68, 0.25); color: #dc2626; max-width: 480px; margin: 0 auto; text-align: left; font-family: "Plus Jakarta Sans", sans-serif;'>
            <h4 style='margin: 0 0 6px 0; font-size: 1rem; display: flex; align-items: center; gap: 6px;'>
                ❌ Invalid Verification Code
            </h4>
            <p style='margin: 0; font-size: 0.85rem; opacity: 0.9;'>
                No database record matching <code>{code}</code> was found.
            </p>
        </div>
        """)

    cursor.close()
    conn.close()

except Exception as e:
    print(f"""
    <div style='padding: 16px; border-radius: 10px; background: rgba(239, 68, 68, 0.1); border: 1px solid #ef4444; color: #dc2626; font-size: 0.85rem; font-family: sans-serif;'>
        <strong>CGI Execution Error:</strong> {str(e)}
    </div>
    """)

sys.stdout.flush()