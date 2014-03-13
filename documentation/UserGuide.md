## User Guide

*Note:* For an overview of DDT features, see [Features](Features.md#ddt-features). This also serves to document 
the major functionalities available.

### Eclipse basics

If you are new to Eclipse, you can learn some of the basics of the Eclipse IDE with this short intro article: 

[An introduction to Eclipse for Visual Studio users
](http://www.ibm.com/developerworks/opensource/library/os-eclipse-visualstudio/)

Also, to improve Eclipse performance on modern machines, it is recommended you increase the memory available to 
the JVM. You can do so by modifying the _`eclipse.ini`_ file in your Eclipse installation. The two VM parameters 
in _`eclipse.ini`_ to note are _-Xms_ (initial Java heap size) and _-Xmx_ (maximum Java heap size). For a machine
with 4Gb of RAM or more, the following is recommended as minimum values:

```
-vmargs
-Xms256m
-Xmx1024m
```

### DDT Prerequisites and Configuration

The [DUB tool](http://code.dlang.org/about) is required to fully enable all DDT functionality. DDT will automatically 
find DUB if it is on the `PATH` environment variable. If it's not, the DUB path can be configured in the DDT `DUB` 
preference page.

A D compiler is also required. This is so the system library modules can be found and used (for code completion, etc.). 
All 3 major compilers are supported: DMD, GDC, LDC. On each startup, DDT will automatically search for all compilers 
it can find in the `PATH` environment variable, and add them to the DDT configuration. Most compiler layouts of the 
library locations will be recognized, but if they are not, the library locations for the found compilers can be 
configured in the `DDT / Compilers` preference page:

<div align="center">
<a><img src="screenshots/UserGuide_CompilersPage.png" /><a/> 
<br/> <sup>the compilers preference page</sup>
</div> 

Other compiler locations that are not present in the `PATH` can also be added in this preference page, although at the
moment this is of limited use since DUB may not be able to find them when building.


### Project setup

##### Project creation:
A new D project can be created in the Project Explorer view. Open `New / Project...` and then `D / DUB Project`. The D perspective should open after creation, if it's not open already.

##### Import Path:
A project has an import path: a list of folder locations that are D import path roots. The import path is derived from the import roots of the project itself (usually the same as the _source folders_), and the import paths of the dependency DUB packages. Only the modules contained in the import path will be visible to semantic features (such as code completion).

The configuration of the import path and source folders, as well as dependencies and other settings is done in the `dub.json` manifest file. Edit this file in Eclipse and save it after applying the desired changes. On startup, or whenever DDT detects the `dub.json` file has been modified, `dub describe` will be run to resolve dependencies
and to supply the fully resolved import path for the project (as well as some other DUB package information). The output of this DUB command (as well as any other DUB command) will be displayed in a DUB console in the Console view.

If an error occurs during this process, an error will placed in the project. For more details on what caused the error, view the DUB console contents.

##### Build configuration:

The project is built using DUB, which will be run whenever an Eclipse workspace build is requested. Note that if the `Project / Build Automatically` option in the main menu is enabled (the default), a workspace build will be requested whenever any file is saved. Turn this on or off as desired.

The build is performed by running `dub build`, the output of which will also be presented in the DUB console. Additional command-line options to this process can be configured in the `DUB Options` project property page. Also, it is possible to configure arbitrary external processes to run before of after the DUB build, in the `Builders` property page. (if desired, the DUB builder itself can also be disabled).

### Editor and Navigation

##### Editor newline auto-indentation:
The editor will auto-indent new lines after an Enter is pressed. Pressing Backspace with the cursor after the indent characters in the start of the line will delete the indent and preceding newline, thus joining the rest of the line with the previous line. Pressing Delete before a newline will have an identical effect.
This is unlike most source editors - if instead you want to just remove one level of indent (or delete the preceding Tab), press Shift-Tab. 

##### Open Definition:
The Open Definition functionality is invoked by pressing F3 in the DDT source editor, or by clicking the Open Definition button placed in the toolbar. When using the toolbar button, Open Definition will work in any text editor, however it won't be able to follow imports across modules if the file is not on the build path of a DDT project. Open Definition is also available in the editor context menu and by means of editor *hyper-linking* (hold Ctrl and select a reference with the mouse).

Open Definition functionality should find any definition under basic reference contexts, but references under complex expressions might resolve inaccurately, or not at all.
Particularly: function call overloading, template overloads, template instantiation, IFTI, operator overloading are not currently understood by the semantic engine.

##### Code-Completion/Auto-Complete:
Invoked with Ctrl-Space. This functionality is generally called Content Assist in Eclipse. For DDT, it has the same semantic power as Open Definition to determine completions. Can be used in import statements to list available modules to import.

Content Assist can also present Code Templates. These are predefined parameterized blocks of code that can be automatically inserted in the current source. These can be configured in the preferences, under 'DDT/Editor/Code Templates'.

##### Text Hover:
Text hover shows a text popup over the reference or definition under the mouse cursor. The hover will display the signature of the definition, as well as DDoc, if available. DDoc will be rendered in a graphical way, similar to a standard HTML presentation.

##### Open-Type dialog:
Invoked with Ctrl-Shift-T. This is a dialog that allows one to search for any definitions (types or meta-types) and open an editor on the source of the selected definition. Search works the same as JDT, a simple text filter can be used, or camel-case matching can be used to match the desired element (for example: the `FEx` text will match `FiberException`, `FileException`, `FormatException`, etc.). Wildcards can also be used in the filter text.
 
##### Hierarchy View:
These are not currently supported/implemented, even though they are present in the UI.

##### Semantic Search:
The search dialog allows searching for definitions based on a text pattern. Available in the main menu, under 'Search' / 'D...':

<div align="center">
<a><img src="screenshots/UserGuide_SearchDialog.png" /><a/> 
</div>

It is also possible to search for all references to a given definition. In the editor, select the name of a definition, and use the editor context menu to search for references (shortcut: Ctrl-Shift-G). This can also be invoked on references, invoking a search for all references to the same definition the selected reference resolves to.


### Launch and Debug:
To run a D project that builds to an executable, you will need to create a launch configuration. Locate the main menu, open 'Run' / 'Run Configurations...'. Then double click 'D Application" to create a new D launch, and configure it accordingly. You can run these launches from the 'Run Configurations...', or for quicker access, from the Launch button in the Eclipse toolbar.

Alternatively, to automatically create and run a launch configuration (if a matching one doesn't exist already), you can select a D project in the workspace explorer, open the context menu, and do 'Run As...' / 'D Application'. (or 'Debug As...' for debugging instead). If a matching configuration exists already, that one will be run.

Whenever a launch is requested, a build will be performed beforehand. This behavior can be configured under general Eclipse settings, or in the launch configuration.

D launches can be run in debug mode. You will need a GDB debugger. To configure debug options (in particular, the path to the debugger to use), open the launch under 'Run' / 'Debug Configurations...', and then navigate to the 'Debugger' tab in the desired launch configuration:

<div align="center">
<a><img src="screenshots/UserGuide_DebuggerLaunchConfiguration.png" /><a/> 
</div>

GDB debugger integration is achieved by using the CDT plugins. To configure global debugger options, go the 'C/C++'/'Debug'/'GDB' preference page.


