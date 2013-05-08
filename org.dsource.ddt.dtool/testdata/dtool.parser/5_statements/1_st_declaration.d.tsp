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
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic case
#PARSE(STATEMENT)    class MyClass : basetype #@DECLS_BLOCK__SAMPLE
#STRUCTURE_EXPECTED: DefinitionClass(DefSymbol RefIdentifier #@DECLS_BLOCK__SAMPLE )
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic case
#PARSE(STATEMENT)    enum MyEnum : basetype { A , B }
#STRUCTURE_EXPECTED: DefinitionEnum(DefSymbol RefIdentifier EnumBody(EnumMember(DefSymbol) EnumMember(DefSymbol)) )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic case
#PARSE(STATEMENT)    enum : int[] { A = 2 , float B }
#STRUCTURE_EXPECTED: DeclarationEnum( RefTypeDynArray(*) EnumBody(EnumMember(DefSymbol Integer) EnumMember(RefPrimitive DefSymbol)) )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)    enum i = 4;
#STRUCTURE_EXPECTED: DeclarationBasicAttrib(DefinitionAutoVariable(DefSymbol InitializerExp(?)))

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic case
#PARSE(DECLARATION)   alias foo[] aliasDef; 
#STRUCTURE_EXPECTED:  DefinitionAliasDecl(RefTypeDynArray(RefIdentifier)  DefSymbol)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   alias memberId this; 
#STRUCTURE_EXPECTED:  DeclarationAliasThis(RefIdentifier)

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(STATEMENT)  #@NULL public { void foo ; }
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(STATEMENT)    pragma(foo) void foo;
#STRUCTURE_EXPECTED: DeclarationPragma(Symbol DefinitionVariable(RefPrimitive DefSymbol))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(STATEMENT)  #@NULL extern(C) { void foo ; }
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ DeclarationBasicAttrib
#PARSE(STATEMENT) const { void foo ; }

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)    static import pack.bar.foo;
#STRUCTURE_EXPECTED: DeclarationImport(ImportContent(RefModule))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)    static assert ( #@EXP_ASSIGN__LITE) ;
#STRUCTURE_EXPECTED: DeclarationStaticAssert(#@EXP_ASSIGN__LITE)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  TODO static if, conditionals
#PARSE(STATEMENT)   static #error(EXPRULE_DeclOrBlock) #@BREAK if ( #@EXP_ASSIGN__LITE)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  TODO static if, conditionals
#PARSE(STATEMENT)  #@NULL  version(unix) { } else { } 

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(STATEMENT)     #@MIXIN_PRF《#?STRUCTURE_EXPECTED!【mixin ●】 ●/++/》 template tpl() #@DECLS_BLOCK__SAMPLE
#STRUCTURE_EXPECTED:  DefinitionTemplate(DefSymbol #@DECLS_BLOCK__SAMPLE )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)     mixin("int foo");
#STRUCTURE_EXPECTED:  DeclarationMixinString(ExpLiteralString)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)     mixin #@TYPE_REFS__LITE ;
#STRUCTURE_EXPECTED:  DeclarationMixin(#@TYPE_REFS__LITE)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)     mixin #@TYPEREFS_NB__LITE defid ;
#STRUCTURE_EXPECTED:  DefinitionNamedMixin(#@TYPEREFS_NB__LITE DefSymbol)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)  #@NULL invariant () #@ST_BLOCK__SAMPLE
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)  #@NULL unittest #@ST_BLOCK__SAMPLE

