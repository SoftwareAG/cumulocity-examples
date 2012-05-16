Cumulocity examples
---------------

This repository contains example applications created using Cumulocity SDK. For more information on [Cumulocity] [1] visit [http://www.cumulocity.com] [1].

Access to [Cumulocity SDK] [1] p2 repository is required to build examples with [Maven] [2]. URL and credentials are available to registered users of [http://www.cumulocity.com] [1] under Guides -> Developer's guide -> Installing the SDK.

Running from Eclipse
---------------

Refer to Developer's guide for detailed instructions.

Building with Maven
---------------

[Maven] [2] build is manifest centric and is using [Tycho] [3]. Please add [Cumulocity] [1] [p2 repository] [4] to Your `settings.xml` like this:

    <settings>
      <activeProfiles>
        <activeProfile>cumulocity</activeProfile>
      </activeProfiles>

      <servers>
        <server>
          <id>cumulocity-sdk-repo</id>
          <username>********</username>
          <password>********</password>
        </server>
      </servers>

      <profiles>
        <profile>
          <id>cumulocity</id>
          <repositories>
            <repository>
              <id>cumulocity-sdk-repo</id>
              <layout>p2</layout>
              <url>**********</url>
            </repository>
          </repositories>
        </profile>
      </profiles>
    </settings>

To built `example-packages` it may also be necessary to put the current version of [Cumulocity] [1] in `example-packages/examples-repository/category.xml`.

  [1]: http://www.cumulocity.com
  [2]: http://maven.apache.org/
  [3]: http://www.eclipse.org/tycho/
  [4]: http://www.eclipse.org/equinox/p2/

