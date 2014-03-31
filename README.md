ld
==

LinkedData PS Exercise

This project is the result of a linked data crawler project at university.

The crawler itself may be invoked by using the "Main" class. It simply runs a breadth-first search for a default of 2 hops. The default stating website is "http://www.w3.org/People/Berners-Lee/card.rdf". Settings like the search depth etc. are hard-coded.

Behaviour of the crawler:
* Fetch all already known non-fetched URIs and parse them
* Put new URIs into a list for the next hop
* Skip all documents that cause an error while downloading or parsing
* Print all discoverd triples to the console

There is extensive logging enabled.

The downloading and parsing is done by using threads for better handling bad situations, e.g. skip the document if downloading/parsing takes too long.

For downloading Apache HTTPClient is used: http://hc.apache.org/
For parsing RDF data NXParser library is used: https://code.google.com/p/nxparser/

Following stuff is not handled/not at optimum:

* Too much logging of parsing failures
* No honoring of robots.txt
* Something in the triple handling/parsing is strange - sometimes triples contain many 'x' characters


people in charge:

Christian (chbi@gmx.at)
Wolfgang (wolfgang DOT rohregger AT gmail DOT com)

Use with Eclipse IDE for Java EE Developers Eclipse IDE for Java EE Developers from https://www.eclipse.org/downloads/ as there are all needed plugins included.


ATTENTION:

You need enabled maven2 repository for nxparser. Edit your ~/.m2/settings.xml
```xml
<settings>
	<profiles>
		<profile>
			<id>myprofile</id>
			<repositories>
				<repository>
					<id>nxparser-repo</id>
					<url>
						http://nxparser.googlecode.com/svn/repository
					</url>
				</repository>
				<repository>
					<id>nxparser-snapshots</id>
					<url>
						http://nxparser.googlecode.com/svn/snapshots
					</url>
				</repository>
			</repositories>
		</profile>
	</profiles>

	<activeProfiles>
		<activeProfile>myprofile</activeProfile>
	</activeProfiles>

</settings>
```


Afterwards: Maven > Update Project

