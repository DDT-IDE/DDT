## Installation

Requirements: You will need a **1.7 Java VM** or greater, **Eclipse 4.3** (Kepler) or greater. 

#### Instructions:
 1. Go to http://download.eclipse.org/eclipse/downloads/drops4/R-4.3.1-201309111000/#PlatformRuntime, and download the 
 "Platform Runtime Binary" for your platform. 
  * You can of course download other Eclipse packages with more features and plugins (version control, other IDEs, etc.), the Platform Runtime is just the minimum. 
  * However, if you do get a different Eclipse installation, it can't contain a DLTK 
   version other than the required one. (currently DLTK 5.0, the latest from Kepler)
 1. Start Eclipse, go to `Help -> Install New Software...`
 1. Click the `Add...` button on the right to add a new update site, enter the http://updates.ddt.googlecode.com/git/ URL 
 in the Location field, click OK.
 1. Select the recently added DDT URL in the `Work with:` dropdown. Type `DDT` in the filter box. Now the DDT feature should appear below.
 1. Select the DDT feature, click Next in the wizard. A few dependencies should show up (such as DLTK). Click Next and 
 complete the installation wizard. 
 1. Restart Eclipse. After that take at the DDT configuration guide in the [User Guide](UserGuide.md#user-guide).

#### Updating:
If you followed the installation steps above, you can update DDT to the latest releases by clicking
`Help -> Check for Updates...` on the Eclipse IDE.
