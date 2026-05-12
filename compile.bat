@echo off
echo ============================================
echo   Hotel Reservation System - Compile
echo ============================================
if not exist out mkdir out

javac -d out -sourcepath src src\com\hotel\HotelApp.java

if %ERRORLEVEL% == 0 (
    echo.
    echo  Compilation successful!
    echo  Run with:  run.bat
) else (
    echo.
    echo  Compilation FAILED. Check errors above.
)
echo.
