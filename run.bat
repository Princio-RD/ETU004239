@echo off
chcp 65001 >nul
REM ============================================================
REM  run.bat - Build + Deploiement + Tomcat en mode console
REM ============================================================

REM ---------- CHEMINS A ADAPTER ----------
set "JAVA_HOME=C:\Program Files\Java\jdk-26.0.2"
set "CATALINA_HOME=C:\Program Files\Apache Software Foundation\Tomcat 11.0"
set "CATALINA_BASE=C:\Program Files\Apache Software Foundation\Tomcat 11.0"
set "MOTEUR_DIR=D:\Github\ETU004239\ETU004239"
set "WEB_DIR=D:\Github\ETU004239\TestOrigine"
REM ---------------------------------------

REM ---------- 0. Verifications ----------
if not exist "%CATALINA_HOME%\bin\catalina.bat" (
    echo [ERREUR] Tomcat introuvable dans : %CATALINA_HOME%
    pause
    exit /b 1
)

REM ---------- 1. Arreter Tomcat ----------
echo === Arret Tomcat ===
call "%CATALINA_HOME%\bin\shutdown.bat" 2>nul
timeout /t 3 /nobreak >nul

REM ---------- 2. Nettoyer l'ancien deploiement ----------
echo === Nettoyage Tomcat ===
rd /s /q "%CATALINA_HOME%\webapps\TestOrigine" 2>nul
del /q "%CATALINA_HOME%\webapps\TestOrigine.war" 2>nul

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

REM ---------- 5. Copier le WAR ----------
echo === Deploiement Tomcat ===
copy /y "%WEB_DIR%\target\TestOrigine.war" "%CATALINA_HOME%\webapps\" >nul

REM ---------- 6. Lancer Tomcat ----------
echo.
echo ============================================================
echo   Tomcat en mode console - Ctrl+C pour arreter
echo ============================================================
echo.
echo  URL de test : http://localhost:8084/TestOrigine/
echo.

call "%CATALINA_HOME%\bin\catalina.bat" run

pause