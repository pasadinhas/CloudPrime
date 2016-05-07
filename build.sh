##Script

####Compilar o ficheiro *MyICount.java* com Java 4 e com a biblioteca BIT no classpath
javac -cp .:./CNV-DB/target/CNV-DB-1.0-SNAPSHOT-jar-with-dependencies.jar -source 1.4 MyICount.java
####Compilar os ficheiros *WebServer.java* e *IntFactorization.java* com java 7
javac WebServer.java IntFactorization.java

####Instrumentar a class *IntFactorization.class* executando MyICount. Por omissão faz override da class instrumentada, na mesma pasta.
java -XX:-UseSplitVerifier MyICount

####Criar o ficheiro *log.txt* na directoria do servidor
touch log.txt 

####Correr o *WebServer*
java -XX:-UseSplitVerifier -cp .:./CNV-DB/target/CNV-DB-1.0-SNAPSHOT-jar-with-dependencies.jar WebServer