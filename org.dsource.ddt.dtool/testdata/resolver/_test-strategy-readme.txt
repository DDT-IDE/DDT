Approach for writing resolver tests:

* Base each test file around a particular AST node / element.
A: Write cases to test how such node affects resolving originating in various scopes
B: Write cases to test code completion invoked with an offset (pick location) on the node itself, if applicable.
In both A and B, make sure to test syntax error versions of the node.

 