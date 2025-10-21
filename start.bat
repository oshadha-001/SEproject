@echo off
echo Starting BookNest Online Bookstore System...
echo.
echo Building project...
call mvnw.cmd clean compile
if %errorlevel% neq 0 (
    echo Build failed!
    pause
    exit /b 1
)

echo.
echo Starting application...
echo Server will be available at: http://localhost:4567
echo Default admin login: admin / admin123
echo.
echo Press Ctrl+C to stop the server
echo.

call mvnw.cmd exec:java -Dexec.mainClass="com.example.booknest.Main"
pause
