## Installation

#### Requirements: 
 * Eclipse 4.5 (Mars) or later.
 * Java VM version 8 or later.

#### Instructions:
 1. Use your existing Eclipse, or download a new Eclipse package from http://www.eclipse.org/downloads/. 
  * For an Eclipse package without any other IDEs or extras (such a VCS tools), download the ["Platform Runtime Binary"](http://archive.eclipse.org/eclipse/downloads/drops4/R-4.5-201506032000/#PlatformRuntime). 
 1. Start Eclipse, go to `Help -> Install New Software...`
 1. Click the `Add...` button to add a new update site, enter the URL: **http://ddt-ide.github.io/releases/** in the Location field, click OK.
 1. Select the recently added update site in the `Work with:` dropdown. Type `DDT` in the filter box. Now the DDT feature should appear below.
 1. Select the `DDT - D Development Tools` feature, and complete the wizard. 
  * DDT dependencies such as CDT will automatically be added during installation.
 1. Restart Eclipse. 
 1. For initial setup, follow the instructions at the configuration section in the [User Guide](UserGuide.md). It is recommended you read the rest of the guide too.
  

#### Updating:
If you already have DDT installed, and want to update it to a newer release, click `Help -> Check for Updates...`.

#### :cn: *Note for users in China*
Note: if you are behind the Great Firewall of China, you are very likely to encounter problems installing DDT: blocked connections, timeouts, or slow downloads. This is because the update site is hosted in Github, which is blocked or has limited access. These alternative steps might help you perform the installation:

* Download the website from https://github.com/DDT-IDE/ddt-ide.github.io.git/archive/master.zip, unpack the archive and use the `releases` directory as a Local repository instead of the Update Site URL. However, you will need to redownload the archive above whenever you want to update DDT to a newer version.
* Download an Eclipse installation which already contains CDT (C Development Tools), so it doesn't have to be installed at the same time as DDT.
