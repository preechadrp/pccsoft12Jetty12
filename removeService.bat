@echo off
setlocal enabledelayedexpansion

::---------------------------------------------
:: ตั้งค่าชื่อ service และ path
::---------------------------------------------
set SERVICE_NAME=pccsoft
set EXE_PATH=%~dp0pccsoft.exe

echo remove service %SERVICE_NAME% ...
"%EXE_PATH%" //DS//%SERVICE_NAME%

echo.
echo Service installation complete.
pause
