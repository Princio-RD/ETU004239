# demarrage:
mvn clean package

mettre le web.xml dedans: jar uf target\Sprint0.jar -C src\main\webapp WEB-INF/web.xml
verifier : jar tf target\Sprint0.jar | findstr web.xml
lancer :java -jar target\Sprint0.jar