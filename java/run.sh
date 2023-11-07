# mvn package --quiet
# java -cp target/tkn-1.0-SNAPSHOT.jar com.javatiktoken.App  com.javatiktoken.App
N=100
mvn compile exec:java -Dexec.mainClass="com.javatiktoken.App" -Dexec.args="$N" --quiet 
