Yaml-Properties-Plugin
===========

This Maven plugin allows you to reference YAML key-value pairs through Maven properties. To use it,
add the following repository to your pom:
```xml
<repositories>
  <repository>
    <id>comphenix-rep</id>
    <name>Comphenix Maven Releases</name>
    <url>http://repo.comphenix.net/content/groups/public</url>
  </repository>
</repositories>
```
Then reference the plugin itself:
```xml
<build>
  <plugins>
    <plugin>
      <groupId>com.comphenix.maven</groupId>
	  <artifactId>yaml-properties-plugin</artifactId>
	  <version>0.0.1-SNAPSHOT</version>
	  <executions>
	    <execution>
		  <goals>
		    <goal>read-yaml-properties</goal>
		  </goals>
		  <configuration>
		    <files>
			  <!-- YAML files that will be loaded. The element name is the file ID. -->
		      <plugin>${basedir}/src/main/resources/plugin.yml</plugin>
		    </files>
          </configuration> 
		</execution>
      </executions>  
    </plugin>
	...
  </plugins>
</build>
```
A possible use of this is to import plugin name and version from a Bukkit plugin.yml file:
```xml
<version>${yaml.plugin.version}</version>
<name>${yaml.plugin.name}</name>
<description>${yaml.plugin.version}</description>
```
The syntax for referencing values in the YAML file is as follows:
```java
${yaml.FILE_ID.KEY}
```
Of course, you can dig deeper into the hierachy as well, along with looking up list elements by index:
```java
${yaml.FILE_ID.KEY.LIST[INDEX].KEY}
```