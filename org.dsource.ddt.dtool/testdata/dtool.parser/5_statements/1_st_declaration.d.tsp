Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)    int foo;
#STRUCTURE_EXPECTED: DefVariable(RefPrimitive DefSymbol)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)    int foo(void paramA) {  }
#STRUCTURE_EXPECTED: DefFunction(RefPrimitive DefSymbol FunctionParameter(? ?)  BlockStatement)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)    int foo(#@TPL_PARAMS_SAMPLE1) (void paramA) {  }
#STRUCTURE_EXPECTED: DefFunction(RefPrimitive DefSymbol #@TPL_PARAMS_SAMPLE1 FunctionParameter(? ?)  BlockStatement)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic case
#PARSE(STATEMENT)    struct MyStruct #@DECLS_BLOCK__SAMPLE
#STRUCTURE_EXPECTED: DefinitionStruct(DefSymbol #@DECLS_BLOCK__SAMPLE)