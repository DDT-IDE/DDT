DDT
===

DDT is an Eclipse-based IDE for the D programming language.

## Develpment Workspace setup and usage

#### Setting up the development environment:
 * Clone the DDT repository.
 * In Eclipse, import from the Git repo all the `org.dsource.ddt*` projects. Java Compiler settings should automatically be configured, since each project has project-specific settings stored in source control.
 * Finally you'll need to setup the target platform: Open the target platform file: `org.dsource.ddt-build\target-platform\DDT.target` and set it as your target platform.

 
#### Running the tests in Eclipse:


 * The tests are divided into 3 suites for each of the 3 main plugins: DTool, IDE Core, IDE UI.
 * There are common Eclipse launch configurations for each of these test suites, they should already be available in your workspace, and properly configured to run out of the box. Some of the default VM arguments in the launch configuration (already configured) should be:
  * `-DDToolTestResources.workingDir=${workspace_loc}/_runtime-tests` (workspace for tests to use while running)
  * `-DDToolTestResources.baseDir=${workspace_loc:/org.dsource.ddt.dtool/testdata}` (where to get certain DTool test resources. This allows DTool tests to run outside of Eclipse runtime.)
  * Some of the suites (DTool at the moment) can be run in Lite Mode, skipping some of the heavyweight, long-running tests. There is also a launch configuration for this.

#### Automated Building and Testing:
Using Maven Tycho, it is possible to automatically build DDT, create an update site, and run all the tests. Download [Maven](http://maven.apache.org/) (minimum version 3.0), and open a shell on the root folder of the source repository:
 * Run `mvn package` to build the main DDT artifacts. The built p2 repository should rest at `bin-maven/ddt.repository/repository`
 * Run `mvn integration-test` to build all the DDT artifacts and run the test suites. You can do `mvn integration-test -P TestsLiteMode` to run the test suites in Lite Mode (skip heavyweight tests).
   * Run `mvn package -P build-ide-product` to build a prepackaged Eclipse installation with DDT already installed. This is not actively maintained as it is not a release artifact, so it may be broken.

#### Uploading a new release:
 Releases are made on the p2 update site. The DDT update site is the `updates` Git repository, accessed through plain HTTP: http://updates.ddt.googlecode.com/git/ . Therefore, a new DDT release is created by building a the p2 repository locally as described above (run `mvn integration-test`), then placing the p2 repository in the `https://code.google.com/p/ddt.updates/` Git repository (and pushing to origin of course):
 * The DDT update site is a composite p2 repository, containing the DDT feature repository, and a link to DDT repository dependencies (such as Kepler). This structure should be maintained when updating the repository.
 * There is an Ant script that can help with this task: repo-release-script.xml


### Project info, other notes

Old source history of the DDT project can still be found at the [Descent SVN repository](http://svn.dsource.org/projects/descent/!svn/bc/1700/trunk/)

#### Code idioms and techniques
 
 * What is this:

```java
@Test
public void testXXX() throws Exception { testXXX$(); }
public void testXXX$() throws Exception {
```
code idiom that is seen so often?


TODO expand this section
