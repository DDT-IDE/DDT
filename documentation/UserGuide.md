## User Guide

*Note:* For an overview of DDT features, see [Features](Features.md#ddt-features). This also serves to document 
what overall functionalities are available.

### Eclipse basics

If you are new to Eclipse, you can learn some of the basics of the Eclipse IDE with this short intro article: 
[An introduction to Eclipse for Visual Studio users
](http://www.ibm.com/developerworks/opensource/library/os-eclipse-visualstudio/)

Also, to improve Eclipse performance and startup time, it is recommended you tweak the JVM parameters. There is a tool called Eclipse Optimizer that can do that automatically, it is recommended you use it. Read more about it [here](http://www.infoq.com/news/2015/03/eclipse-optimizer). (Installing/enabling the JRebel optimization is not necessary as that only applies to Java developers)

### Configuration

A [D installation](http://dlang.org/download.html) is required for most IDE functionality, as well as the [DUB tool](http://code.dlang.org/about).

 * The D compiler from the D installation should be found in the `PATH` environment variable. This is so the standard library source modules can be found and used (for code completion, etc.).

 * The path to the `dub` executable should be configured in the `DDT` preference page, which can be accessed from the menu `Window / Preferences`. The path can be an absolute path, or just the executable name, in which case the executable will be searched in the PATH environment variable.
 
 * To enable source formatting, you must download and build the [`dfmt` tool](https://github.com/Hackerpilot/dfmt). Then configure the path to the `dfmt` executable in the `DDT` preference page.


### Project setup

##### Project creation
A new D project is created with the New Project Wizard: from the Project Explorer context menu, select `New / Project...` and then `D / DUB Project`. The same wizard can be used to add a pre-existing DUB package: simply use the location field to select a pre-existing directory.

##### Project configuration
Most project settings (such as source folders, or build configurations) are specified in the DUB package manifest file (typically `dub.json`). You will need to be familiar with the format of this file, see [here](http://code.dlang.org/package-format).
DDT will detect any changes to the file automatically, and subsequently run `dub describe` to resolve DUB dependencies, and obtain other DUB package information. If an error occurs during this operation, you can view the output of the command in the `D Build` console page in the Console view.   

> **Note:** DUB's `dub.sdl` manifest format is not fully supported: 
 * Build Targets won't be correctly supported because DDT won't be able to understand the defined DUB build configurations.
 * Changing a project from DUB's JSON format to SDL (and vice-versa) during an Eclipse session is not supported. You will need to restart Eclipse for changes to be recognized/refreshed.

##### D Standard Library setup
Every time `dub describe` is invoked, DDT will also search for a compiler in the `DUB_COMPILERS_PATH` and `PATH` environment variables. (`DUB_COMPILERS_PATH` is examined in the same way as the `PATH` variable). Most compiler standard-library directory layouts, relative to the compiler executable, should be recognized (be it DMD, GDC, or LDC). 

> If DDT does not find the standard library locations, it is not possible to manually configure them at the moment. As a workaround, download and unpack the official DMD release archive (to use as a mock compiler installation), and put the *binaries directory* in the `DUB_COMPILERS_PATH` environment variable. This way DDT will find the standard library locations, although the compiler used for actual compilation may be a differnt one. (You can also replace the standard library source folders of this compiler installation with symbolic links to you actual, up-to-date compiler installation)

##### DUB Package Search Paths

In a project's context menu, there is DUB submenu with a few DUB commands, in particular some to add or remove a project's location to the list of DUB package paths:

<div align="center">
<a href="screenshots/UserGuide_DubCtxMenu.png?raw=true"><img src="screenshots/UserGuide_DubCtxMenu.png" /><a/> 
</div> 

This may be necessary since D projects will not be visible as dependencies to other DUB packages unless they have been placed on the local packages path list.


##### Building:
A project has a set of Build Targets, each being a command invocation that builds the source code into one or more artifacts, and reports back possible compilation errors to the IDE. Build Targets can be configured directly from the Project Explorer. 

Build Targets can be enabled for a regular project build, or for auto-check. Auto-check is invoked when an editor is saved and no syntax errors are present in the source code. Normally it does not produce any artifacts, it just checks for compilation errors. **Note that auto-check is a different setting than the Eclipse workspace "Project / Build Automatically" option**. DDT ignores the later option by default. Auto-check is also not invoked if a file is saved automatically due to a regular build being requested. 

From the context menu of a Build Target, you can also directly create a Run or Debug launch configuration for one the generated executables. 

D projects are built using DUB, and the Build Targets are derived from DUB configurations.


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

### Editor and Navigation

##### Editor newline auto-indentation:
The editor will auto-indent new lines after an Enter is pressed. Pressing Backspace with the cursor after the indent characters in the start of the line will delete the indent and preceding newline, thus joining the rest of the line with the previous line. Pressing Delete before a newline will have an identical effect.
This is unlike most source editors - if instead you want to just remove one level of indent (or delete the preceding Tab), press Shift-Tab. 

##### Content Assist / Open Definition:
Content Assist (also know as Code Completion, Auto Complete) is invoked with `Ctrl-Space`. 

The Open Definition functionality is invoked by pressing F3 in the source editor. 
Open Definition is also available in the editor context menu and by means of editor *hyper-linking* 
(hold Ctrl and click on a reference with the mouse cursor). 

### Launch and Debug:
To run a D project that builds to an executable, you will need to create a launch configuration. Locate the main menu, open 'Run' / 'Run Configurations...'. Then double click 'D Application" to create a new D launch, and configure it accordingly. You can run these launches from the 'Run Configurations...', or for quicker access, from the Launch button in the Eclipse toolbar.

Alternatively, to automatically create and run a launch configuration (if a matching one doesn't exist already), you can select a D project in the workspace explorer, open the context menu, and do 'Run As...' / 'D Application'. (or 'Debug As...' for debugging instead). If a matching configuration exists already, that one will be run.

Whenever a launch is requested, a build will be performed beforehand. This behavior can be configured under general Eclipse settings, or in the launch configuration.

##### Debugging
a
sdafsdf


| **Windows note:** _Using Cygwin GDB doesn't work very well, if at all. The recommended way to debug in Windows is to use the GDB of [mingw-w64](http://mingw-w64.org/), or the one of [TDM-GCC](http://tdm-gcc.tdragon.net/)._ |
|----|

| **OS X note:** _The GDB that is included with OS X doesn't work properly. You'll need to install the latest GDB from Homebrew. See [this article](http://ntraft.com/installing-gdb-on-os-x-mavericks/) for details. Or alternatively, [this SO link](https://stackoverflow.com/questions/33162757/how-to-install-gdb-debugger-in-mac-osx-el-capitan), which might me more up-to-date. _ |
|----|


You can debug a D program by running a launch in debug mode. You will need a GDB debugger. To configure debug options (in particular, the path to the debugger to use), open the launch under 'Run' / 'Debug Configurations...', and then navigate to the 'Debugger' tab in the desired launch configuration:

<div align="center">
<a href="screenshots/UserGuide_DebuggerLaunchConfiguration.png?raw=true"><img src="screenshots/UserGuide_DebuggerLaunchConfiguration.png" /><a/> 
</div>

GDB debugger integration is achieved by using the CDT plugins. To configure global debugger options, go the 'C/C++'/'Debug'/'GDB' preference page.

**Note that for debugging to work**, the program must be compiled with debug symbols information, and those debug symbols must be on a format that GDB understands. Otherwise you will get GDB error messages such "(no debugging symbols found)" or "file format not recognized". See http://wiki.dlang.org/Debugging for more information on what debug symbols format each compiler produces.
