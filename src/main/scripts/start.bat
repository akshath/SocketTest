@echo off
set JARPATH=.;
for %%x in (..\lib\*.jar) do call :concat %%x
for %%x in (*.jar) do call :concat %%x

echo %JARPATH%

"%JAVA_HOME%\bin\java" -cp %JARPATH% net.sf.sockettest.SocketTest
goto :eof

:concat
set JARPATH=%JARPATH%;%1
goto :eof
