# mc-log4j-patcher

Replaces old (vulnerable - CVE-2021-44228) Log4j2 version with the latest one (2.15.0) that contains the JNDI RCE fix.\
Tested on Spigot 1.12.2, PaperSpigot 1.8.8, PaperSpigot 1.17.1

This is intended to fix servers that are currently unsupported, such as PaperSpigot 1.8.8\
Please check if your current server software has an official release fixing the vulnerability before using this tool.

Usage:\
`java -jar patcher.jar <old-server.jar> <fixed-server.jar>`

[Download patcher.jar](https://github.com/OopsieWoopsie/mc-log4j-patcher/raw/main/patcher.jar)

If you use PaperSpigot, then the "old-server.jar" file should be the one located at "cache/patched.jar" and not the paperclip.jar wrapper.\
After the process finishes, you need to replace your old server jar with the "fixed-server.jar" one.

### Example usage with PaperSpigot 1.17.1
```
server$ java -jar patcher.jar cache/patched_1.17.1.jar server.jar
Input file  : cache/patched_1.17.1.jar
Output file : server.jar
Processing original Jar file...
Adding nolookups to log4j2.xml ... Done
Adding Log4j2 2.15.0 (core+api+iostreams)... 
(You can likely ignore this) : duplicate entry: META-INF/MANIFEST.MF
(You can likely ignore this) : duplicate entry: META-INF/NOTICE
(You can likely ignore this) : duplicate entry: META-INF/maven/org.apache.logging.log4j/log4j-core/pom.xml
(You can likely ignore this) : duplicate entry: META-INF/services/javax.annotation.processing.Processor
(You can likely ignore this) : duplicate entry: META-INF/LICENSE
(You can likely ignore this) : duplicate entry: META-INF/DEPENDENCIES
(You can likely ignore this) : duplicate entry: META-INF/maven/org.apache.logging.log4j/log4j-core/pom.properties
(You can likely ignore this) : duplicate entry: META-INF/maven/org.apache.logging.log4j/log4j-api/pom.properties
(You can likely ignore this) : duplicate entry: META-INF/maven/org.apache.logging.log4j/log4j-api/pom.xml
Finished
```

And then to run the server:
```
server$ java -jar server.jar nogui
System Info: Java 16 (Eclipse OpenJ9 VM openj9-0.27.0) Host: Linux 5.4.0-81-generic (amd64)
Loading libraries, please wait...

...(server loading log)...
```
