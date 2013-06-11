Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)    int foo;
#STRUCTURE_EXPECTED: DefVariable(RefPrimitive DefSymbol)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ duplicate case
#PARSE(STATEMENT)    foo[#error(TYPE_AS_EXP_VALUE)【int】];
#STRUCTURE_EXPECTED: StatementExpression(*)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)    int foo(Object) {  } #@IGNORE_REST ;
#STRUCTURE_EXPECTED: DefFunction(RefPrimitive DefSymbol NamelessParameter(?)  BlockStatement)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)    int foo(#@TPLPARAMS__SIMPLE_TESTER) (Object) {  }
#STRUCTURE_EXPECTED: DefFunction(RefPrimitive DefSymbol #@TPLPARAMS__SIMPLE_TESTER NamelessParameter(?)  BlockStatement)
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
#STRUCTURE_EXPECTED: DeclarationBasicAttrib(DefinitionAutoVariable(DefSymbol Integer))

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic case
#PARSE(DECLARATION)   alias foo[] aliasDef; 
#STRUCTURE_EXPECTED:  DefinitionAliasVarDecl(RefTypeDynArray(RefIdentifier)  DefSymbol)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   alias memberId this; 
#STRUCTURE_EXPECTED:  DeclarationAliasThis(RefIdentifier)

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)    static import pack.bar.foo;
#STRUCTURE_EXPECTED: DeclarationImport(ImportContent(RefModule))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)    static assert ( #@EXP_ASSIGN__LITE) ;
#STRUCTURE_EXPECTED: DeclarationStaticAssert(#@EXP_ASSIGN__LITE)

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
#PARSE(STATEMENT)     mixin #@TYPEREF_NBNE__LITE defid ;
#STRUCTURE_EXPECTED:  DefinitionNamedMixin(#@TYPEREF_NBNE__LITE DefSymbol)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)  #@NULL invariant () #@ST_BLOCK__SAMPLE
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)  #@NULL unittest #@ST_BLOCK__SAMPLE
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)    #@ATTRIBS_STCLike int foo; #?SYNCHRONIZED_ATTRIB{#:DISCARD_CASE}
#STRUCTURE_EXPECTED: #@ATTRIBS_STCLike DefVariable(RefPrimitive DefSymbol) )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(STATEMENT)  #@NULL public { void foo ; }
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(STATEMENT)    pragma(foo) void foo;
#STRUCTURE_EXPECTED: DeclarationPragma(Symbol DefinitionVariable(RefPrimitive DefSymbol))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(STATEMENT)  extern(C) { void foo ; }
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ DeclarationBasicAttrib TODO
#PARSE(STATEMENT) const { void foo ; }

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ DeclarationBasicAttrib TODO: basic attrib , this is wrong
#PARSE(STATEMENT) const invariant() { }
