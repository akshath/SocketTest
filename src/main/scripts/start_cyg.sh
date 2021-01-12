#!/bin/bash
export JARPATH=`find ../ -name '*.jar' | tr '\n' ';' | tr '\/' '\\'`
export CLASSPATH=".;$JARPATH;socket-test-1.0.jar;"
echo $CLASSPATH

"$JAVA_HOME/bin/java" -cp "$CLASSPATH" net.sf.sockettest.SocketTest
