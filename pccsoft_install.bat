::install as windows service by apache procrun (prunsrv.exe rename to pccsoft.exe)
@echo off
setlocal enabledelayedexpansion

::---------------------------------------------
:: ตั้งค่าชื่อ service และ path
::---------------------------------------------
set SERVICE_NAME=pccsoft
set SERVICE_DISPLAY=pccsoft system
set SERVICE_DESC=pccsoft system
set EXE_PATH=%~dp0pccsoft.exe
set JAR_PATH=%~dp0startApp-0.0.1.jar
set LIB_PATH=%~dp0lib\*
set STARTCLASS=com.pcc.start.MainApp
set STARTMETHOD=startService
set STOPCLASS=com.pcc.start.MainApp
set STOPMETHOD=stopService
set LOG_PATH=%~dp0logs
set JAVA_HOME=%~dp0jre21
set JVM_DLL=%JAVA_HOME%\bin\server\jvm.dll

::---------------------------------------------
:: สร้าง logs folder ถ้ายังไม่มี
::---------------------------------------------
if not exist "%LOG_PATH%" mkdir "%LOG_PATH%"

::---------------------------------------------
:: สร้าง classpath รวม jar ทั้งหมดใน lib
::---------------------------------------------
set CLASSPATH="%JAR_PATH%;%LIB_PATH%"

echo Installing %SERVICE_NAME% ...
"%EXE_PATH%" //IS//%SERVICE_NAME% ^
  --DisplayName="%SERVICE_DISPLAY%" ^
  --Description="%SERVICE_DESC%" ^
  --Install="%EXE_PATH%" ^
  --Jvm="%JVM_DLL%" ^
  --Classpath=%CLASSPATH% ^
  --StartMode=jvm ^
  --StartClass="%STARTCLASS%" ^
  --StartMethod=%STARTMETHOD% ^
  --StopMode=jvm ^
  --StopClass="%STOPCLASS%" ^
  --StopMethod=%STOPMETHOD% ^
  --StopTimeout=1000 ^
  --LogPath="%LOG_PATH%" ^
  --LogPrefix=%SERVICE_NAME% ^
  --StdOutput=auto ^
  --StdError=auto ^
  --JvmOptions=-Xms256m;-Xmx512m ^
  --PidFile=pccsoft_id

echo.
echo Service installation complete.
pause
