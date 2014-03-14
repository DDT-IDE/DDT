
## DDT - D Development Tools:
[![sample_basic](screenshots/sample_basic.png)](:)

## DDT features:
 * Build and project configuration/dependencies support using [DUB](http://code.dlang.org/about).
 * Project viewer annotated with DUB elements (under the Project Explorer view).
 
|[![sample_basic](screenshots/thumbs/sample_basic.png)](https://raw.github.com/bruno-medeiros/DDT/latest/documentation/screenshots/sample_basic.png)|
|----|

 * D source editor:
   * Syntax Highlighting (configurable styling) and syntax error reporting.
   * Folding of source blocks, bracket matching.
   * Outline view of source file elements and Quick-Outline. (`Ctrl+O`)
   
|[![sample_quickOutline](screenshots/thumbs/sample_quickOutline.png)](https://raw.github.com/bruno-medeiros/DDT/latest/documentation/screenshots/sample_quickOutline.png)<br/>`Quick outline popup, with name filter "*Aut"`|
|----|

   * DDoc editor hover. (renders DDoc into an annotated visual representation)
   
|[![sample_ddocView](screenshots/thumbs/sample_ddocView.png)](https://raw.github.com/bruno-medeiros/DDT/latest/documentation/screenshots/sample_ddocView.png)|
|----|
   * Find Definition (aka Open Definition, Reference Resolving). (`F3` or `Ctrl+mouse-click`)
   * Content Assist (aka Code Completion, Intellisense). (`Ctrl+Space`) Resolves references to symbols/definitions, has the exact same semantic capabilities as Reference Resolving.
     * Can only resolve non-qualified named references, or qualified named references whose segments are all names (ie: `std.stdio.writeln`).
     * Some qualified references with expression segments, like `member` in `(new Foo()).member` work as well, the rest don't.
     * Can not distinguish between function or template overloads.
     * There are several other semantic limitations. TODO: describe more precisely which semantic constructs are supported and which are not.

| [![sample_ca1](screenshots/thumbs/sample_ca1.png)](https://raw.github.com/bruno-medeiros/DDT/latest/documentation/screenshots/sample_ca1.png) | [![sample_ca2](screenshots/thumbs/sample_ca2.png)](https://raw.github.com/bruno-medeiros/DDT/latest/documentation/screenshots/sample_ca2.png) |
|----|----|
 * Semantic Search - searches for elements (variables, functions, types, references, etc.)
   * Open-Type dialog. Search and open a type (or type-like definition such as templates or aliases) using text pattern. (`Ctrl+Shift+T`)

|[![sample_openType](screenshots/thumbs/sample_openType.png)](https://raw.github.com/bruno-medeiros/DDT/latest/documentation/screenshots/sample_openType.png)|
|----|
   * Search Dialog. Search for definitions based on a text pattern. (`Ctrl+H`)
   * Search Dialog. Search for all references to a given definition. (`Ctrl+Shift+G`)
     * *Experimental*: Search Dialog feature is experimental and can have several bugs and limitations.

|[![sample_searchReferences](screenshots/thumbs/sample_searchReferences.png)](https://raw.github.com/bruno-medeiros/DDT/latest/documentation/screenshots/sample_searchReferences.png)<br/>`Search results after searching for "writeln" references`|
|----|

 * Debugging functionality. Fully featured GDB debugger support (reusing Eclipse CDT's GDB integration)
  * Stop/resume program execution. Listing program threads and stack frame contents.
  * Setting breakpoints, watchpoints (breakpoint on data/variables), tracepoints. Breakpoint conditions.
  * Stack variables inspection view. Expression watch and view. Disassembly view.
  * Non-stop mode (for supported GBDs). Reverse debugging (for supported GDB targets).
  * Most of Eclipse CDT is now a requirement for DDT (for the debugging feature). 

| [![sample_debug1](screenshots/thumbs/sample_debug1.png)](https://raw.github.com/bruno-medeiros/DDT/latest/documentation/screenshots/sample_debug1.png)<br/>`Execution stopped on a conditional breakpoint` | [![sample_debug2](screenshots/thumbs/sample_debug2.png)](https://raw.github.com/bruno-medeiros/DDT/latest/documentation/screenshots/sample_debug2.png)<br/>`Variables for current stack frame` |
|----|----|

