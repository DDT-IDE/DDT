▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic case
#PARSE(DECLARATION)   alias foo[] aliasDef; 
#STRUCTURE_EXPECTED:  DefinitionAliasVarDecl(RefTypeDynArray(RefIdentifier)  DefSymbol)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic case
#PARSE(DECLARATION)   alias aliasDef = foo[]; 
#STRUCTURE_EXPECTED:  DefinitionAlias(AliasFragment(DefSymbol RefTypeDynArray(RefIdentifier)))

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@FRAG_PREFIX《
  ►#?STRUCTURE_EXPECTED!【/*No fragment*/● 】●
  ►#?STRUCTURE_EXPECTED!【 aliasDef /*No =*/ #error(EXP_ASSIGN)【】 ,● AliasFragment(DefSymbol) 】●
  ►#?STRUCTURE_EXPECTED!【 aliasDef = #@TYPEREF_NB__LITE ,● AliasFragment(DefSymbol #@TYPEREF_NB__LITE)】●
¤》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   alias #@FRAG_PREFIX #@NO_DEFID #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  DefinitionAlias(#@FRAG_PREFIX AliasFragment(#@NO_DEFID))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   alias #@FRAG_PREFIX aliasDef #error(EXP_ASSIGN)【】 #@SEMICOLON_OR_BREAK 
#STRUCTURE_EXPECTED:  DefinitionAlias(#@FRAG_PREFIX AliasFragment(DefSymbol))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   alias #@FRAG_PREFIX aliasDef = #@TYPEREF_ANY #@SEMICOLON_OR_BREAK 
#STRUCTURE_EXPECTED:  DefinitionAlias(#@FRAG_PREFIX AliasFragment(DefSymbol #@TYPEREF_ANY))

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Decl syntax - var
#@INIT_OR_NO《
  ►#?STRUCTURE_EXPECTED!【/*Noinit*/● 】●
  ►#?STRUCTURE_EXPECTED!【= #@INITIALIZERS__SAMPLE● #@INITIALIZERS__SAMPLE 】●
¤》

#@ATTRIBS_TESTER《
  ►#?STRUCTURE_EXPECTED!【#@ATTRIBS● #@ATTRIBS)】●
  ►#?STRUCTURE_EXPECTED!【public extern(C) pure● DeclarationProtection DeclarationLinkage DeclarationBasicAttrib】●
¤》

#@ALIAS_VAR_DECL《
 ►#?STRUCTURE_EXPECTED!【#@TYPEREF_NBNE_NoSingleDefId #@NO_DEFID #@SEMICOLON_OR_BREAK #?TM_ATTRIB_AMBIG【#:DISCARD_CASE】● 
                        #@TYPEREF_NBNE_NoSingleDefId #@NO_DEFID 】●
 ►#?STRUCTURE_EXPECTED!【#@TYPEREF_BROKEN #@NO_SEMICOLON #@BREAK fooB #?TM_ATTRIB_AMBIG【#:DISCARD_CASE】 ●
                        #@TYPEREF_BROKEN #@NO_DEFID 】●
 ►#?STRUCTURE_EXPECTED!【#@TYPEREF_NBNE fooB #@SEMICOLON_OR_BREAK #?TM_ATTRIB_AMBIG【#:DISCARD_CASE】●
                        #@TYPEREF_NBNE DefSymbol 】●
 ►#?STRUCTURE_EXPECTED!【#@TYPEREF_NBNE__LITE fooA #@NO_SEMICOLON #@BREAK = #@INITIALIZERS__SAMPLE●
                        #@TYPEREF_NBNE__LITE DefSymbol #@BREAK 】●
 ►#?STRUCTURE_EXPECTED!【#@TYPEREF_NBNE__LITE fooA, fooB #@NO_SEMICOLON #@BREAK = #@INITIALIZERS__SAMPLE●
                        #@TYPEREF_NBNE__LITE DefSymbol AliasVarDeclFragment(DefSymbol) 】●                                 
 ►#?STRUCTURE_EXPECTED!【int foo[] #@《 /**/ ● #error(EXP_SEMICOLON) #@BREAK * 》 ;●
                        RefPrimitive DefSymbol RefTypeDynArray(CStyleRootRef) 】●
 ►#?STRUCTURE_EXPECTED!【int foo[ #error(EXP_CLOSE_BRACKET) ;●
                        RefPrimitive DefSymbol RefTypeDynArray(CStyleRootRef) 】●
 ►#?STRUCTURE_EXPECTED!【#@ATTRIBS_TESTER #@TYPEREF_NBNE__LITE fooA ;● 
                        #@ATTRIBS_TESTER #@TYPEREF_NBNE__LITE DefSymbol 】●
 ►#?STRUCTURE_EXPECTED!【align(123 #error(EXP_CLOSE_PARENS)#error(EXP_SEMICOLON)#@BREAK #@TYPEREF_NBNE__LITE fooA ;● 
                        DeclarationAlign #@NO_DEFID $ 】●
 ►#?STRUCTURE_EXPECTED!【pure #@NO_TYPEREF #@SEMICOLON_OR_BREAK● 
                        DeclarationBasicAttrib #@NO_TYPEREF #@NO_DEFID 】●
¤》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  
#PARSE(DECLARATION)   alias #@ALIAS_VAR_DECL  
#STRUCTURE_EXPECTED:  DefinitionAliasVarDecl(#@ALIAS_VAR_DECL)
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Decl syntax - function
#@ALIAS_FN_DECL《
 ►#?STRUCTURE_EXPECTED!【#@TYPEREF_NBNE__LITE #@DEFID ( #@CLOSEPARENS_OR_NO #@SEMICOLON_OR_BREAK●
                        #@TYPEREF_NBNE__LITE #@DEFID】●
 ►#?STRUCTURE_EXPECTED!【#@TYPEREF_NBNE__LITE #@DEFID (#@FN_PARAMS_SAMPLE) #@FN_ATTRIB__SAMPLE #@SEMICOLON_OR_BREAK●
                        #@TYPEREF_NBNE__LITE #@DEFID  #@FN_PARAMS_SAMPLE  #@FN_ATTRIB__SAMPLE】●
 ►#?STRUCTURE_EXPECTED!【#@TYPEREF_NBNE__LITE #@DEFID (#@FN_PARAMS_SAMPLE) #@NO_SEMICOLON #@BREAK , foo ;●
                        #@TYPEREF_NBNE__LITE #@DEFID  #@FN_PARAMS_SAMPLE  #@BREAK】●
 ►#?STRUCTURE_EXPECTED!【#@ATTRIBS_TESTER #@TYPEREF_NBNE__LITE #@DEFID (#@FN_PARAMS_SAMPLE) #@FN_ATTRIB__SAMPLE #@SEMICOLON_OR_BREAK●
                        #@ATTRIBS_TESTER #@TYPEREF_NBNE__LITE #@DEFID  #@FN_PARAMS_SAMPLE  #@FN_ATTRIB__SAMPLE】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  
#PARSE(DECLARATION)   alias #@ALIAS_FN_DECL  
#STRUCTURE_EXPECTED:  DefinitionAliasFunctionDecl(#@ALIAS_FN_DECL)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  
#PARSE(DECLARATION)   alias #@TYPEREF_NBNE__LITE #@DEFID #@NO_SEMICOLON #@BREAK nothrow ;  
#STRUCTURE_EXPECTED:  DefinitionAliasVarDecl(#@TYPEREF_NBNE__LITE #@DEFID #@BREAK)

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ alias this

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   alias memberId this; 
#STRUCTURE_EXPECTED:  DeclarationAliasThis(RefIdentifier)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   alias this = memberId #@SEMICOLON_OR_BREAK  
#STRUCTURE_EXPECTED:  DeclarationAliasThis(RefIdentifier)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   alias this = memberId #error(EXP_SEMICOLON) #@BREAK  []; 
#STRUCTURE_EXPECTED:  DeclarationAliasThis(RefIdentifier)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   alias this = #@NO_ID #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  DeclarationAliasThis(#@NO_ID)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   alias this #error(EXP_ASSIGN)【】 #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  DeclarationAliasThis()
