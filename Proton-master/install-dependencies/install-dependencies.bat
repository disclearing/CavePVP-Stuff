@echo off
call mvn install:install-file -DgroupId=mkremins -DartifactId=fanciful -Dversion=0.4.0-SNAPSHOT -Dfile=fanciful-0.4.0-SNAPSHOT.jar -Dpackaging=jar
call mvn install:install-file -DgroupId=com.comphenix.protocol -DartifactId=ProtocolLib -Dversion=3.6.5-SNAPSHOT -Dfile=ProtocolLib-3.6.5-SNAPSHOT.jar -Dpackaging=jar
pause