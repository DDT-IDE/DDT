▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic case
#PARSE(DECLARATION)   alias foo[] aliasDef; 
#STRUCTURE_EXPECTED:  DefinitionAliasDecl(RefTypeDynArray(RefIdentifier)  DefSymbol)
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

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Decl syntax
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  
#PARSE(DECLARATION)   alias #@TYPEREF_NBNE_NoSingleDefId #@NO_DEFID  #@SEMICOLON_OR_BREAK 
#STRUCTURE_EXPECTED:  DefinitionAliasDecl(#@TYPEREF_NBNE_NoSingleDefId #@NO_DEFID)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   alias #@TYPEREF_NBNE__LITE  #@DEFID  #@SEMICOLON_OR_BREAK 
#STRUCTURE_EXPECTED:  DefinitionAliasDecl(#@TYPEREF_NBNE__LITE #@DEFID)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  function form
#PARSE(DECLARATION)   alias #@TYPEREF_NBNE__LITE  #@DEFID ( #@CLOSEPARENS_OR_NO #@SEMICOLON_OR_BREAK 
#STRUCTURE_EXPECTED:  DefinitionAliasDecl(#@TYPEREF_NBNE__LITE #@DEFID)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   alias #@TYPEREF_NBNE__LITE  #@DEFID (#@FN_PARAMS_SAMPLE) #@FN_ATTRIB__SAMPLE #@SEMICOLON_OR_BREAK 
#STRUCTURE_EXPECTED:  DefinitionAliasDecl(#@TYPEREF_NBNE__LITE #@DEFID #@FN_PARAMS_SAMPLE #@FN_ATTRIB__SAMPLE)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   alias               #@TYPEREF_NBNE__LITE #@DEFID #@NO_SEMICOLON #@BREAK nothrow ;
#STRUCTURE_EXPECTED:  DefinitionAliasDecl(#@TYPEREF_NBNE__LITE #@DEFID #@BREAK )

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ NOT supported: C style declaration syntax (it's deprecated in D)
#PARSE(DECLARATION)   alias int aliasDef[];

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
