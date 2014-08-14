## DDT release ChangeLog

### DDT 0.10.x
 * Fixed - D application run/debug menu shortcut appearing in non-D editors.
 * Added: error message in status line when doing content assist in invalid source positions.
 * Greater DUB integrations for semantic operations. XXX: clarify 
   * Can now do Open Definition, Content Assist, etc. in D files outside of a DDT Eclipse project.
   * Better prevention of temporary UI freezes when doing semantic operations.
 * Added D Standard Library element to Project Explorer.
 * Added: detection of standard library source locations for Arch Linux DMD/LDC/GDC compiler installations.
   * #/bin/dmd  -> #/include/dlang/dmd
   * #/bin/ldc2 -> #/include/dlang/ldc
   * #/bin/gdc  -> #/include/dlang/gdc
 * Removed D compilers preference page. This was replaced in favor of a more general, non-DDT-specific way of detecting compiler installations. See below: 
   * Added UserGuide.md#compiler-setup section.
 * More lenient with CDT versions. DDT can now be installed with any CDT 8.x.x.
  * However, there is a small change using DDT with a version other than the recommned/tested one will break things. Each DDT release will list a recommented/tested CDT version.
 * Updated/tested DDT to CDT 8.4. 
  * Added Dynamic printf ruler action to DDT editor.
  * For more info on these new CDT 8.4 debug features, see: https://wiki.eclipse.org/CDT/User/NewIn84#Debug
 * Fixed #65 - MixinString parsing as expression.
 * Added: Hovering the mouse (or pressing F2) over an auto keyword (or enum keyword for manifest constants) will display 
 the type that the associated variable declaration resolves to.
 * Fixed: bug when parsing error messages of the D compiler, when non-standard output messages are emmited. 
   
### DDT 0.10.1 (2014-05-02)
 * Build: now adds problem marker to project for DUB build failures. 
 * Build: now adds problem markers to resources with compiler errors.
 * Debugger: DDT now requires CDT 8.3.x .
 * Debugger: The "Details" format is now the default display value for variables in the Variables view, Expressions view, debug hover.
  * This is more in line to what GDB display in the command line, and is usually a more useful display. It replaces the uselesss '{...}' value that was commonly displayed for most complex types.
 * Debugger: fixed #43 - Backend errors when displaying the value of dynamic array variables.
 * Fixed #51 - corrected parsing/splitting of DUB build extra arguments. (also can now use quotes to prevent splitting)
 * Fixed #52 - User Defined Attributes without parantheses marked as syntax errors.     
 * Fixed #53 - Code completion doesn't see variables defined in foreach loop.
 * Removed some unused/invalid preferences from Editor preference page.
 
### DDT 0.10.0 (2014-03-14)
 * Added DUB support. Project import-path now determined by the `dub.json` manifest file and running `dub describe`.
   * On DDT startup, or whenever `dub.json` is modified (and saved), `dub describe` will be run to resolve 
   dependencies and to supply source folders and import path info for the project (as well as some other DUB 
   package information).
 * Added a per-project Eclipse console to monitor the output of issued DUB commands.
 * Added DUB elements support for the Project Explorer view.
   * Added icon for source folders, DUB cache folder, dub.json manifest file.
   * Add elements for DUB dependencies, and DUB error status.
   * <u>The Script Explorer view is now deprecated.</u> Use Project Explorer view instead. 
   * Note that some Script Explorer functionality has not been re-implemented in Project Explorer. For example the 
   packages and modules of dependencies are not shown (#50).
 * Removed all DLTK-based buildpath UI from standard DDT views. This functionality was never fully supported. 
   * Modifying the import-path is now only supported by means of the dub.json file.
 * Added DUB build support. Removed basic builder (the previous build functionality using response files). 
   * Project build configuration page has only one field now: a field with optional extra options to pass to DUB 
   when building.
   * <u>Previously created DDT Eclipse projects are no longer compatible with this new version.</u> You should 
   recreate the project using the new DUB Project wizard.

##### Changes after the Preview Release
 * Implemented #16: Added support for LDC compiler locations.
 * Compiler locations are now automatically searched and added on DDT startup:
   * DDT will look on the PATH environment variable for compilers installations.
 * Cleanup: Removed compilers/interpreters config block from `New DUB Project` wizard.   
 * Cleanup: Removed host option from location group in New DUB Project wizard. (it was unnecessary)
 * Fixed #46: 0.10.0-PR adds spaces between characters to DUB extra options
 * DUB builder: incremental builds are now retried if previous 'dub build' exited with non-zero return code.
 * Fixed exception when dub.json modified and Project Explorer view was closed.
 * Minor improvements and bugfixes to DUB UI tree elements in Project Explorer.
 * Added to Project Explorer context menu a few DUB actions to add/remove the project from local packages.
 * DUB builder: fixed project not being refreshed after build.
 * Documentation: updated the [Prerequisites](UserGuide.md#ddt-prerequisites-and-configuration) and 
 [Project Setup](UserGuide.md#project-setup) UserGuide sections with new DUB info.
 * Fixed NPE when resolving a function call against on a non-function reference.
 * Fixed Project Explorer sorting bug in non-toplevel resources.

Some DUB functionality was not fully implemented/supported in this release:
 * Issue #49 DUB: Add proper support for configurations 
 * Issue #48 DUB: improve UI support for sub-packages


### DDT 0.9.1 (2014-02-07)
 * Added debug hover to D editor. When a CDT debug session is active, this hover presents detailed info for the variable under the cursor, similar to the Variables view.
 * Added D 2.064 support for eponymous template shortcut syntax for alias and enum.
 * Added D 2.064 support for the package.d import rule. (#33)
 * Fixed bug: working directory option of a D launch configuration not honored when run in debug mode.
 * Fixed bug: environment variables non-append option of a D launch configuration not honored.
 * Fixed some potential issues related to debugging occurring unless the user had full CDT feature manually installed.
 * Fixed #20: GDC `incluce/d/*/` library location layout not recognized
 * Fixed #36: Editor/parser doesn't recognize `new` operator's nested class instantiation syntax. 
 * Cleaned up the behavior and labels of D Application launch configuration dialog.
 * UI cleanup: "Interpreter Container" now named "D Standard Library"
 * Updated UserGuide documentation: added info for debugging, Code Templates, Auto-Indent. Restructured sections and updated/removed old info.

### DDT 0.9.0 (2013-11-14)
 * Issue #13: Added integrated debugging support, using CDT's GDB debugger integration. Supports:
   * Stop/resume program execution. Listing program threads and stack frame contents. 
   * Setting breakpoints, watchpoints (breakpoint on data/variables), tracepoints. Breakpoint conditions.
   * Stack variables inspection view. Expression watch and view. Disassembly view.
   * Non-stop mode (for supported GBDs). Reverse debugging (for supported GDB targets).
   * Most of Eclipse CDT is now a requirement for DDT (for the debugging feature).
 * Fixed issue #19: NPE when opening editor with hyperlinks disabled
 * Fixed issue #17: Assertion failure with cast expression and invalid code in UnaryExpression
 * Changed D perspective icon.

### DDT 0.8.1 (2013-09-23)
 * Documentation: added entry in [UserGuide#Eclipse_basics] about Eclipse memory settings optimization.
 * Fixed: Code Completion module list in import declarations now shows the module's DDoc.
 * Implemented: Code Completion now resolves:
   * auto declarations (resolved to the type of the initializer).
   * global type properties (.init, .sizeof, .alignof, etc.)
   * integral properties (.min, .max), float properties(.infinity, .nan, etc.), object properties (.classinfo).
   * static/dynamic array properties (.ptr, .length, .dup, etc.).
   * the type of bool/char/integral/floating/string/array literals (not map array though).
   * parentheses expression.
   * this/super literal expressions.
   * cast expression.
   * new expression.
 * Cleanup: Code Completion now shows no results if invoked after a float token ending in a dot.
 * Fixed 0.7.0 regression: Code Completion now locates all identifiers in multi-identifier variable declaration and multi-identifier alias declaration.
 * Fixed issue #7: Error parsing interface function with contracts but no body.
 * Fixed issue #10: Compiler installation search in preferences page.
 * Fixed issue #9: Control-click opens up new editor everytime
 * Fixed 0.8.0 regression: missing icon for D application launch action/configuration.

### DDT 0.8.0 (2013-08-30)
 * Updated DDT to latest version of DLTK (version 5.0, from Eclipse Kepler).
 * Changed: In DDoc view, the text of undefined macros now remains unmodified, instead of being replaced by empty text.
   * Example: *`My $(NO_SUCH_MACRO undefined) macro`* will render as *`My $(NO_SUCH_MACRO undefined) macro`* instead of *`My  macro`*.
 * Added: In DDoc view, added predefined DDoc macro: *`D`*. Works the same as *`D_CODE`*.
 * Implemented: Code Completion now presents language primitive results (void, int, char, etc.).
 * Fixed unintended behavior regression in 0.7.0 where Code Completion would not show any results when invoked in certain type reference source contexts. Examples (♦ denotes location where CC would be invoked):
```
foo[♦]♦
foo[123]♦
foo!♦(♦)
const(foo)♦
``` 
 * Improved Code Completion to work when invoked next or inside a keyword. Previously it worked on primitive type keywords only (`int`, `char`, etc. ). Examples:   
```
char intro, intro2; in♦ // CC invoked here will offer 【intro, intro2】 as completions
class Foo { char intro } auto x = Foo.in♦ // CC invoked here will offer 【intro】 as completions
```
 * Fixed 0.7.0 regression bug in editor resolving functionality where a reference would not be found if the cursor was at the very end of the reference.
   * Affects: Open Definition, Open Definition hyperlinks, References search, DDoc view hovers.
 * Cleanup: new UI base icons for most D source elements. New icon for D search.
  * The new icon design is meant to better convey visual information for the semantics of the underlying element.
 * Added icon adornment for templated types.
 * Added icon adornments for `const,immutable` attributes in variables.
 * Fixed issue #8: Missing enum members in the outline view.
 * Fixed bug with implementation of issue #3: support Debian Linux DMD layout.
 * Cleanup: changed some of the dialog messages for the Open Definition operation to be more clear and concise.
 * Cleanup: restricted some editor functionality to named references only (Open Definition, Open Definition hyperlinks, DDoc view hovers).

### DDT 0.7.0 - "Midnight Riders" (2013-08-15)
 * Requirements changes: now requires minimum Java VM version 1.7 .
 * Implemented issue #2: Support parsing of latest D syntax (up to D language version 2.063).
   * Completely rewrote D parser, creating a new hand-written parser (IDE no longer depends on descent.compiler).
 * Holding Ctrl and clicking over a reference now always opens a new editor on where the reference target is.
 * Fixed NPE bug when opening D search page with a package reference selected in the editor.
 * Fixed minor parser AFE bug with qualified reference at end of file.
 * Fixed minor bug when resolving modules without module declarations (implicit module name).
 * SITE: Moved project website from Eclipse Labs to normal Google Code: http://code.google.com/p/ddt/
 * -- released Preview Release 0.7.0 --
 * Added mixin template instance to listed model elements in UI (for example, outline)
 * Fixed bug in D Appearance preference page: var and funtions showing up as Structs in preview.
 * Fixed a bug with selective imports presenting all module symbols, not just the selected ones.
 * Code completion icons now consistent with the rest of the IDE UI:
  * (previously some decorators for storage classes or protection attributes were missing, and JDT like icons where not supported in Code Completion popup)
 * The -v2 switch is no longer included in default GDC build response file.
 * Implemented issue #3 : support Debian Linux DMD layout
 * Fixed code completion NPE bug with function declarations with missing syntax.
 * Fixed code completion NPE bug with conditional declarations/statements with missing bodies.
 * Fixed bug with the display text of function and template parameters in code completion popup.  

### DDT 0.6.0 (2012-05-17)
 * Fixed bug causing DDT to never load the pre-defined editor Code Templates.
 * Added support for GDC, as well as a generic compiler install. ([http://code.google.com/a/eclipselabs.org/p/ddt/issues/detail?id=59 issue@ELabs #59])
 * Added support for DMD Unix style compiler installs ("usr/bin/dmd" and similar). ([http://code.google.com/a/eclipselabs.org/p/ddt/issues/detail?id=56 issue@ELabs #56])
 * Fixed several bugs with the D Search Page (incorrect or missing search matches). 
 * Fixed bug of incorrect default text shown in D Search Page when editor cursor/selection was on a symbol definition.
 * Labels for D elements in the outline and quick outline now show type (for variables) and return type (for functions).
 * Icons for D elements in all viewers now show protection attribute info.
   * This can be configured (overlay for all elements, or JDT-style icon overlays) in the new Appearance preference page.
 * Fixed the Open D Type dialog so that it shows the standard D icons as the rest of the IDE.
 * Fixed parser NPE bug when using inout as const wildcard declarator.
 * Fixed parser error when "alias this" appeared as a statement.
 * Fixed bugs where parser and semantic engine ignored the parameters of constructors.
 * Fixed some invalid menu entries in editor context menu (entries for commands that are not implemented).
 * Modules without module declarations will now have their names inferred from their filename. ([http://code.google.com/a/eclipselabs.org/p/ddt/issues/detail?id=26 issue@ELabs #26])
 
### DDT 0.5.0 (2011-08-26)
 * Updated DDT to DTLK 3.0
 * Made icons of D source elements (classes, structs templates, etc.) consistent throughout the UI. 
   * (Previously DLTK views and some popups used simplified icons, and only CA and outline used more detailed icons.)
 * Outline view changes:
   * Now shows nested elements.
   * Clicking on named elements correctly selects their name in the editor (previously it just revealed the element).
   * Added context menu to the outline elements, added some filter actions to the toolbar of the view.
 * Constructor/Desctructors/Allocators/Deallocators now show up in Outline view and quick Outline.
 * Implemented [http://code.google.com/a/eclipselabs.org/p/ddt/issues/detail?id=52 issue@ELabs #52]: function/delegate literals can now be folded. And also anonymous classes.
 * Fix in Documentation hovers: removed redudant text, and now displays concrete archetype (Class, Interface, Struct, Union), instead of "Aggregate".
 * Fixed [http://code.google.com/a/eclipselabs.org/p/ddt/issues/detail?id=47 issue@ELabs #47]: NPE while parsing is expression. Source ranges will still be missing though.
 * Fixed some minor NPE and assertion failure bugs.
 * Fixed [http://code.google.com/a/eclipselabs.org/p/ddt/issues/detail?id=51 issue@ELabs #51], parsing AssertFailedException.
 * Resolved #19 in a definite and proper way (instead of with a workaround hack).
 * Fixed some issues relate to completion of imports, including [http://code.google.com/a/eclipselabs.org/p/ddt/issues/detail?id=53 issue@ELabs #53]. 
 * Fixed [http://code.google.com/a/eclipselabs.org/p/ddt/issues/detail?id=58 issue@ELabs #58]: typing the dot causes selected completion proposal to be applied.

### DDT 0.4.3 (2011-06-02)
 * Resolved [http://code.google.com/a/eclipselabs.org/p/ddt/issues/detail?id=12 issue@ELabs #12], added Content Assist preference page. 
   * Options: Completion insert/override; Single proposals automatic insert; Auto-Activation enable and delay;
   * Added Content Assist proposal auto-insertion trigger characters: ' ', '=', ';', '.'.  
 * Fixed [http://code.google.com/a/eclipselabs.org/p/ddt/issues/detail?id=31 issue@ELabs #31]: Indent auto edit: full indent deletion is incorrectly triggered on certain positions.
 * Fixed some shortcomings related to auto indent in lines that started with comments.
 * Resolved [http://code.google.com/a/eclipselabs.org/p/ddt/issues/detail?id=30 issue@ELabs #30], changed implementation of editor folding to a newer API:
   * Now the editor can fold non-DDoc comments as well (a previous limitation).
   * Now the editor can fold unittests and conditional declarations (debug/version).
   * Added folding preference page, can now configure the initial state (folded or not) of each folding group.
   * The folding groups/kinds are now: comments, DDoc comments, module header comments, aggregate types, functions, unittests, conditional declarations.
 * Fixed minor source range bugs for template references and typeof() references.
 * `is` and `!is` now correctly considered as keywords for the purposes of syntax highlighting.
   * `!in` is fully highlighted, instead of just `in`.
 * Fixed [http://code.google.com/a/eclipselabs.org/p/ddt/issues/detail?id=41 issue@ELabs #41] and some other related parsing bugs with qualified expressions (DotIdExp).
 * Fixed issue where syntax highlighting would break when typing inside a multi-line r"" string.
 
### DDT 0.4.2 (2011-04-29)
 * Fixed [http://code.google.com/a/eclipselabs.org/p/ddt/issues/detail?id=33 issue@ELabs #33], bug with spaces in projects names: made all variables in DMD response file resolve to quote escaped values.
 * Fixed bug where all D comments where considered DDoc comments for documentation hover.
 * Fixed limitation where problem hovers where not more prioritary than documentation hovers.
 * Fixed [http://code.google.com/a/eclipselabs.org/p/ddt/issues/detail?id=37 issue@ELabs #37]: F2 always brings up empty documentation hover.
 * Fixed [http://code.google.com/a/eclipselabs.org/p/ddt/issues/detail?id=38 issue@ELabs #38]: autocomplete crashing / very slow. (Content Assist takes very long to show up when many completion options are available)
 * Added support for editor code templates in Content Assist. 
 * Fixed bug in cast expression, where the cast type reference was ignored by the parser/semantic-engine; 
 * Fixed bug where DDoc comments where not associated with the corresponding symbol definition if that definition had protection, storage, linkage, or certain other kinds of attributes;
 * Implemented [http://code.google.com/a/eclipselabs.org/p/ddt/issues/detail?id=35 issue@ELabs #35]: format immutable keyword and @annotations.
    * Added (nothrow, pure, shared, immutable) keywords to syntax coloring
    * Added @annotations to syntax coloring (spaces after @ not supported, any identifier accepted)
    * Changed syntax coloring example in preferences
 * Fixed minor Content Assist bug where completions would not appear when requested on certain syntax errors.

### DDT 0.4.1 (2011-03-04)
 * Fixed some parser bugs. (mostly relating to Template Instance references and source with invalid syntax)
 * Fixed [http://code.google.com/a/eclipselabs.org/p/ddt/issues/detail?id=25 issue@ELabs #25] and other source highlighting bugs (like nested comments being lexed as non-nested comments). 
   * Also added additional syntax highlighting options for WYSIWYG strings, delimited strings, and character literals.
 * Fixed a bug due to reuse of a single instance of the editor source-highlighting & folding parser. 
   * This bug may have been manifested in obscure, unknown, and/or hard to replicate ways.
 * Fixed [http://code.google.com/a/eclipselabs.org/p/ddt/issues/detail?id=17 issue@ELabs #17], [http://code.google.com/a/eclipselabs.org/p/ddt/issues/detail?id=18 issue@ELabs #18], [http://code.google.com/a/eclipselabs.org/p/ddt/issues/detail?id=20 issue@ELabs #20], all related to editor auto edits. Properly implemented:
   * Editor auto-edits (smart indenting/deindenting according to block structure), added "Typing" preference page.

### DDT 0.4.0 (2010-11-17)
 * Renamed project to DDT
 * Updated IDE to latest DLTK version (2.0).
 * Fixed comment indentation character (was '#' instead of '//') on toggle comment actions 
 * Fixed defaults bug in DeeRootPreferencePage and DeeEditorPreferencePage.
 * Added DEEBUILDER.COMPILEREXEPATH variable to builder, changed builder response file defaults.
 * Fixed parser to be able to parse expressions as the argument of typeid.
 * Added a parser workaround to allow parsing D source with annotations.
 * Fixed several parser bugs.
 * Removed Content Assist Templates preference page.
 
### Mmrnmhrm 0.3.1 (2010-04-12)
 * Updated IDE to latest DLTK version (1.0), fixed bugs related to DLTK version migration.
 * Added Eclipse feature and update-site (so now supports automatic updating).
 * Updated support for new folder layout in newer DMD compiler installations.
 * Added D perspective.

### Mmrnmhrm 0.3.0 (2008-10-09)
 * Updated IDE to latest DLTK version (1.0M2), and also to Eclipse 3.4.
 * Minor builder changes. Added $DEEBUILDER.COMPILERPATH builder flag for compiler executable location.
 * Implemented initial DLTK Type Hierarchies implementation. Supertype hierarchy viewing should work ok, but subtype hierarchy is not, needs further work on part of DLTK. (Type Hierarchy View is available by pressing F4, Type Hierarchy Pop-up is available by pressing Ctrl-T).
 * Minor fixes to the search engine (searching for classes and functions should work better now, as well as Open-Type)
 * Added auto edit strategies: smarter indentation on enter presses, copy&paste, etc. There may still be some kinks.


### Mmrnmhrm 0.2.2 (2007-11-13)
 * Fixed dependency on the org.junit plugin, and consequently on JDT.
 * Fixed SWT debug colors allways on.
 * Fixed Source Coloring preference page "Basic types" entry bug.
 * Removed unused DTLK project script builder (fix only takes effect on new projects).
 * Rethought and implemented the integrated project builder, based on a simple IDE-managed response file. It should be ready for use now.

### Mmrnmhrm 0.2.1 (2007-10-23)
 * Fixed major bug with document syntax highlighting and partitions.
 * Improved name lookup: statement blocks and enum bodies are now supported correctly (they don't see forward definitions anymore).
 * Improved Code completion: duplicates or occluded names are no longer presented.
 * Can now set, view, and remove Descent-compatible breakpoints in Mmrnmhrm's editor (these will work with Descent's debugger).
 * DDoc text and code completion hovers are now processed and presented as HTML instead of raw characters, thanks to Ary Mazana's Descent DDoc parser. ([pic](http://svn.d
