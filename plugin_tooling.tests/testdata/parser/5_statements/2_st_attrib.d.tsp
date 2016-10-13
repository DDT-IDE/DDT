Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 

#@ATTRIBS_ST《#@ATTRIBS #?SYNCHRONIZED_ATTRIB{#:DISCARD_CASE}》
#@ATTRIBS_AUTODecl_ST《#@ATTRIBS_AUTODecl #?SYNCHRONIZED_ATTRIB{#:DISCARD_CASE}》

#@ATTRIB_BODY_ST《
  ►#?STRUCTURE_EXPECTED!【int foo;●DefVariable(RefPrimitive DefSymbol)】●
  
  ►#?STRUCTURE_EXPECTED!【struct defunit { } ●DefinitionStruct(* *)】●
  ►#?STRUCTURE_EXPECTED!【class defunit { } ●DefinitionClass(* *)】●
  
  ►#?STRUCTURE_EXPECTED!【template defunit(T) { } ●DefinitionTemplate(* * *)】●
  ►#?STRUCTURE_EXPECTED!【mixin tpl!(foo) defunit; ●DefinitionMixinInstance(* *)】●
  ►#?STRUCTURE_EXPECTED!【enum defunit {A, B, C} ●DefinitionEnum(* *)】●
  
  ►#?STRUCTURE_EXPECTED!【alias int defunit; ●DefinitionAliasVarDecl(* *) 】●
  ►#?STRUCTURE_EXPECTED!【alias int defunit(int foo); ●DefinitionAliasFunctionDecl(* * *)】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)     #@ATTRIBS_ST #@ATTRIB_BODY_ST 
#STRUCTURE_EXPECTED:  
    DeclarationAttrib(#@ATTRIBS_ST #@ATTRIB_BODY_ST )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(STATEMENT)     #@ATTRIBS_AUTODecl_ST fooB = #@EXP_ASSIGN__LITE;
#STRUCTURE_EXPECTED:  
    DeclarationAttrib(#@ATTRIBS_AUTODecl_ST DefAutoVariable(DefSymbol #@EXP_ASSIGN__LITE) )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ ommited return type
#PARSE(STATEMENT)     #@ATTRIBS_AUTODecl_ST func(#@FN_PARAMS_SAMPLE) #@ST_BLOCK__SAMPLE
#STRUCTURE_EXPECTED:  
    DeclarationAttrib(#@ATTRIBS_AUTODecl_ST DefFunction(DefSymbol #@FN_PARAMS_SAMPLE #@ST_BLOCK__SAMPLE) )

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
