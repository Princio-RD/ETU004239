@echo off
REM ============================================================
REM  run.bat - Build + Deploiement + Tomcat en mode console
REM  Affiche les logs Tomcat directement dans cette fenetre
REM ============================================================

set "JAVA_HOME=C:\Program Files\Java\jdk-26.0.2"
set "TOMCAT_HOME=C:\Program Files\Apache Software Foundation\Tomcat 11.0"
set "MOTEUR_DIR=D:\S5\Sprint\Sprint origine\ETU004239"
set "WEB_DIR=D:\S5\Sprint\Sprint origine\TestOrigine"

REM ---------- 1. Arreter Tomcat s'il tourne ----------
echo === Arret Tomcat ===
call "%TOMCAT_HOME%\bin\shutdown.bat" 2>nul
timeout /t 3 /nobreak >nul

REM ---------- 2. Supprimer l'ancien deploiement ----------
echo === Nettoyage Tomcat ===
rd /s /q "%TOMCAT_HOME%\webapps\TestOrigine" 2>nul
del /q "%TOMCAT_HOME%\webapps\TestOrigine.war" 2>nul

REM ---------- 3. Rebuild le moteur ----------
echo === Build Moteur ===
cd /d "%MOTEUR_DIR%"
call mvn clean install
if errorlevel 1 (echo Echec moteur & pause & exit /b 1)

REM ---------- 4. Rebuild le web ----------
echo === Build Web ===
cd /d "%WEB_DIR%"
call mvn clean package
if errorlevel 1 (echo Echec web & pause & exit /b 1)

REM ---------- 5. Copier le WAR dans Tomcat ----------
echo === Deploiement Tomcat ===
copy /y "%WEB_DIR%\target\TestOrigine.war" "%TOMCAT_HOME%\webapps\" >nul

REM ---------- 6. Lancer Tomcat EN MODE CONSOLE ----------
echo.
echo ============================================================
echo   Tomcat en mode console - Les logs s'affichent ci-dessous
echo   Ctrl+C pour arreter
echo ============================================================
echo.
set "CATALINA_HOME=C:\Program Files\Apache Software Foundation\Tomcat 11.0"
set "CATALINA_BASE=C:\Program Files\Apache Software Foundation\Tomcat 11.0"

call "%TOMCAT_HOME%\bin\catalina.bat" run

pause