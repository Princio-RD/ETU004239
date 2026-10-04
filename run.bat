@echo off
REM ==== Configuration ====
set "TOMCAT_HOME=C:\Program Files\Apache Software Foundation\Tomcat 11.0"

echo === Build Moteur ===
cd ETU004239
call mvn clean install
if errorlevel 1 (echo Echec moteur & pause & exit /b 1)
cd ..

echo === Build Web ===
cd TestOrigine
call mvn clean package
if errorlevel 1 (echo Echec web & pause & exit /b 1)
cd ..

echo === Deploiement Tomcat ===
del /q "%TOMCAT_HOME%\webapps\TestOrigine.war" 2>nul
rmdir /s /q "%TOMCAT_HOME%\webapps\TestOrigine" 2>nul
copy /y "TestOrigine\target\TestOrigine.war" "%TOMCAT_HOME%\webapps\" >nul

echo === Demarrage Tomcat ===
call "%TOMCAT_HOME%\bin\startup.bat"

echo.
echo === OK ===
echo http://localhost:8084/TestOrigine/
pause