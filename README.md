TrCkanClient 
====

Java client for dati.trentino.it ckan instance. Targets Ckan 2.2+ . It _may_ work with other ckan installations also.

Forked from [andmar8 Ckan-Java-Client](https://github.com/andmar8/CKAN-Java-Client). We added
  * ckan 2.2+ compliance
  * dependency handling with Maven
  * 

License: AGPL v3

#### Usage

To use it with Maven, add this in your pom.xml:


```
    <repositories>
        <repository>
            <id>tr-ckan-client-repo</id>
            <url>https://raw.github.com/opendatatrentino/TrCkanClient/master/mvn-repo</url>
        </repository>
                
    </repositories>

```    

and, in the pom.xml dependencies section,  add this:


```
        <dependency>	
            <groupId>eu.trentorise.opendata</groupId>
            <artifactId>trckanclient</artifactId>
            <version>1.0</version>            
        </dependency>

```




Credits:

* Alberto Zanella - Trento Rise - a.zanella@trentorise.eu 

* Forked from [andmar8 Ckan-Java-Client](https://github.com/andmar8/CKAN-Java-Client) which was forked [Open knowledge foundation ckan client]
(https://github.com/okfn/CKANClient-J). 




