# ETU004239
Projet Mr Naina Sprint0 2026


# creation du projet avec maven:
directe :
mvn archetype:generate "-DgroupId=com.passerelle" "-DartifactId=MonProjetFramework" "-DarchetypeArtifactId=maven-archetype-quickstart" "-DinteractiveMode=false"

manuelle :
mvn archetype:generate "-DarchetypeArtifactId=maven-archetype-quickstart"
mvn archetype:generate
groupId: com.passerelle
artifactId: ETU004239
version: 1.0-SNAPSHOT
package: com.passerelle

autre cas:java -jar target/ETU004239-1.0-SNAPSHOT.jar

installation du jar : mvn install:install-file "-Dfile=P:\Github\ETU004239\target\ETU004239.jar" "-DgroupId=com.passerelle" "-DartifactId=ETU004239" "-Dversion=1.0-SNAPSHOT" "-Dpackaging=jar"


-changement de classe
- demarrage de l'application web
- premier appelle du servlet
- contextlistener ou init (parcourir tout les classes dans le classepath de l'application web)
- annotation controller (mg.ituxxx) alefa any anatiny .jar
- verifier chaque annotation controller