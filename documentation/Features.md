## DDT - D Development Tools:
[![sample_basic](screenshots/sample_basic.png)](screenshots/sample_basic.png?raw=true)

## DDT features:
 * Build and project configuration/dependencies support using [DUB](http://code.dlang.org/about).
 * Project viewer annotated with DUB elements (under the Project Explorer view).
 
|[![sample_basic](screenshots/ProjectExplorer.png)](screenshots/ProjectExplorer.png?raw=true)|
|----|

 * D source editor:
   * Syntax Highlighting (configurable styling) and syntax error reporting.
   * Folding of source blocks, bracket matching.
   * Outline view of source file elements and Quick-Outline. (`Ctrl+O`)
   
|[![sample_quickOutline](screenshots/thumbs/sample_quickOutline.png)](screenshots/sample_quickOutline.png?raw=true)<br/>`Quick outline popup, with name filter "*Aut"`|
|----|

   * DDoc editor hover. (renders DDoc into an annotated visual representation)
   
|[![sample_ddocView](screenshots/thumbs/sample_ddocView.png)](screenshots/sample_ddocView.png?raw=true)|
|----|
   * Find Definition (aka Open Definition, Reference Resolving). (`F3` or `Ctrl+mouse-click`)
   * Content Assist (aka Code Completion, Intellisense). (`Ctrl+Space`) Resolves references to symbols/definitions, has the exact same capabilities as Reference Resolving.
   * Content Assist code snippets (configurable). 

| [![sample_ca1](screenshots/thumbs/sample_ca1.png)](screenshots/sample_ca1.png?raw=true) | [![sample_ca2](screenshots/thumbs/sample_ca2.png)](screenshots/sample_ca2.png?raw=true) |
|----|----|


#### Debugging functionality. 
Fully featured GDB debugger support (uses Eclipse CDT's GDB integration)
  * Stop/resume program execution. Listing program threads and stack frame contents.
  * Setting breakpoints, watchpoints (breakpoint on data/variables), tracepoints. Breakpoint conditions.
  * Stack variables inspection view. Expression watch and view. Disassembly view.
  * Non-stop mode (for supported GBDs). Reverse debugging (for supported GDB targets).

| [![sample_debug1](screenshots/thumbs/sample_debug1.png)](screenshots/sample_debug1.png?raw=true)<br/>`Execution stopped on a conditional breakpoint` | [![sample_debug2](screenshots/thumbs/sample_debug2.png)](screenshots/sample_debug2.png?raw=true)<br/>`Variables for current stack frame` |
|----|----|
