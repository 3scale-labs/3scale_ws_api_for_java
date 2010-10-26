@echo off

if "%1a" == "a" goto noParams
if "%2a" == "a" goto noParams

setlocal enabledelayedexpansion

set LIB=
for %%f in (..\lib\compile\*.jar) do set LIB=!LIB!;%%f
for %%f in (..\lib\test\*.jar) do set LIB=!LIB!;%%f
for %%f in (..\lib\*.jar) do set LIB=!LIB!;%%f
rem echo libs: %LIB%

set CP=%LIB%;..\jbosscache-core.jar;%CP%
rem echo cp  is %CP%

java -classpath "%CP%" -Dsource=%1 -Ddestination=%2 org.jboss.cache.config.parsing.ConfigFilesConvertor 

goto fileEnd

:noParams
echo usage: "%0 <file_to_transform> <destination_file>"

:fileEnd