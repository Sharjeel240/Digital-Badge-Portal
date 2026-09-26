import time
import requests

# Endpoints
SERVLET_URL = "http://localhost:8080/BadgeVerifySystem/VerifyBadgeServlet?code=SF-A82K91X"
CGI_URL = "http://localhost:8080/BadgeVerifySystem/cgi-bin/verify?code=SF-A82K91X"

TEST_ROUNDS = [10, 50, 100, 500]

def run_benchmark(url, count):
    start_time = time.time()
    successes = 0
    failures = 0
   
    for _ in range(count):
        try:
            res = requests.get(url, timeout=5)
            if res.status_code == 200:
                successes += 1
            else:
                failures += 1
        except Exception:
            failures += 1
           
    total_time = (time.time() - start_time) * 1000  # in ms
    avg_time = total_time / count
    return round(avg_time, 2), successes, failures

if __name__ == "__main__":
    print("\n=================== BENCHMARK EXECUTION ===================")
    print(f"{'Requests':<10} | {'CGI Avg (ms)':<15} | {'Servlet Avg (ms)':<15}")
    print("-" * 50)
    
    for count in TEST_ROUNDS:
        cgi_avg, _, _ = run_benchmark(CGI_URL, count)
        servlet_avg, _, _ = run_benchmark(SERVLET_URL, count)
        print(f"{count:<10} | {cgi_avg:<15} | {servlet_avg:<15}")
        
    print("===========================================================\n")