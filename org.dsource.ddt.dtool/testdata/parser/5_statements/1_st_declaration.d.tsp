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
#STRUCTURE_EXPECTED: DefinitionAutoVariable(AttribBasic DefSymbol Integer)

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

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ template
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

#@ATTRIBS_ST《#@ATTRIBS #?SYNCHRONIZED_ATTRIB{#:DISCARD_CASE}》
#@ATTRIBS_AUTODecl_ST《#@ATTRIBS_AUTODecl #?SYNCHRONIZED_ATTRIB{#:DISCARD_CASE}》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)     #@ATTRIBS_ST int foo;
#STRUCTURE_EXPECTED:  DefVariable(#@ATTRIBS_ST RefPrimitive DefSymbol)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(STATEMENT)     #@ATTRIBS_AUTODecl_ST fooB = #@EXP_ASSIGN__LITE;
#STRUCTURE_EXPECTED:  DefAutoVariable(#@ATTRIBS_AUTODecl_ST DefSymbol #@EXP_ASSIGN__LITE )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ ommited return type
#PARSE(STATEMENT)     #@ATTRIBS_AUTODecl_ST func(#@FN_PARAMS_SAMPLE) #@ST_BLOCK__SAMPLE
#STRUCTURE_EXPECTED:  DefFunction(#@ATTRIBS_AUTODecl_ST DefSymbol #@FN_PARAMS_SAMPLE #@ST_BLOCK__SAMPLE)

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ this test is COMMENTED OUT since prot attribs are allowed structurally
#PARSE(STATEMENT)  #@NULL public void foo ;

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#@INVALID_ATTRIB_ST_FOLLOW《
  ►invariant() { }●
  ►this(int foo) { }●
  ►  #@ST_BLOCK__SAMPLE #?ENUM_ATTRIB{#:DISCARD_CASE}●
  ►: #@ST_BLOCK__SAMPLE #?ENUM_ATTRIB{#:DISCARD_CASE}●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ check that only declarators are allowed after  non-pragma ATTRIBS
#PARSE(STATEMENT)    #@ATTRIBS_ST #@NO_DECLARATOR #@IGNORE_REST #@INVALID_ATTRIB_ST_FOLLOW #?PRAGMA_ATTR{#:DISCARD_CASE}
#STRUCTURE_EXPECTED: DeclarationAttrib( #@ATTRIBS_ST #@NO_DECLARATOR )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(STATEMENT)    pragma(foo) #@ST__ANY
#STRUCTURE_EXPECTED: DeclarationAttrib( AttribPragma(Symbol) #BLOCK_ST_UNSCOPED(flag) #@ST__ANY)
