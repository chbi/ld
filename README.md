ld
==

LinkedData PS Exercise

This protject will result in a linked data crawler for a project at university.

people in charge:

Christian (chbi@gmx.at)
Wolfgang (wolfgang DOT rohregger AT gmail DOT com)

Use wit Eclipse IDE for Java EE Developers Eclipse IDE for Java EE Developers from https://www.eclipse.org/downloads/ as there are all needed plugins included.


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

