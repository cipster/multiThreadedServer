<h2>A simple HTTP Server built with the help of Java and it's multithreading capabilities.</h2>

This server lives on the web and spends it's time serving HTML and on specific requests it also grants it's users file upload and download.

You can start it by running <code>java -jar <pathTo.jar> <port> <fileDirectory></code>, 
I like to use <code>java -jar /home/ciprian/work/projects/multiThreadedServer/target/multithreadedserver-1.0-SNAPSHOT.jar 8080 /home/ciprian/serverFiles</code>, but I suggest 
checking out the code and running a <code>mvn clean install</code> before even trying.

It all started when I googled "A multi-threaded (e.g. file-based) web server with thread-pooling implemented in Java".
I found a simple implementation at <a href="https://github.com/ibogomolov/WebServer">https://github.com/ibogomolov/WebServer</a>
  and then worked my way from there.

  Alongside the github repo mentioned, I also make use of
  <ul>
     <li>
        <a href="https://commons.apache.org/proper/commons-io/">apache commons-io</a>
     </li>
     <li>
        <a href="https://github.com/google/guava/wiki">google guava</a>
     </li>
     <li>
        <a href="http://junit.org/junit4/">Junit4</a>
     </li>
     <li>
        <a href="http://mockito.org/">Mockito</a>
     </li>
     <li>
        <a href="http://hamcrest.org/JavaHamcrest/">Hamcrest Matchers</a>
     </li>
  </ul>
Whoop.
