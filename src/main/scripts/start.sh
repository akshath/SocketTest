JARPATH=`find ../lib/ -name "*\.jar" | sed ':a;N;$!ba;s/\n/:/g'`
ST_JARPATH=`find . -name "*\.jar" | sed ':a;N;$!ba;s/\n/:/g'`
CLASSPATH="$CLASSPATH:$JARPATH:$ST_JARPATH"
echo $CLASSPATH
java -cp "$CLASSPATH" net.sf.sockettest.SocketTest

