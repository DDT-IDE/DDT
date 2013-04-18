▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic case
#PARSE(DECLARATION)   alias foo[] aliasDef; 
#STRUCTURE_EXPECTED:  DefinitionAliasDecl(RefTypeDynArray(RefIdentifier)  DefSymbol)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic case
#PARSE(DECLARATION)   alias aliasDef = foo[]; 
#STRUCTURE_EXPECTED:  DefinitionAlias(AliasFragment(DefSymbol RefTypeDynArray(RefIdentifier)))

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@TYPE_REFS__NB__NO_SINGLE_ID《#@TYPEREFS_NB #?TYPE_REF_CAN_PARSE_AS_ID【#DISCARD_TEST_CASE】》

#@FRAG_PREFIX《
  ►#?STRUCTURE_EXPECTED!【/*No fragment*/● 】●
  ►#?STRUCTURE_EXPECTED!【 aliasDef /*No =*/ #error(EXP_ASSIGN)【】 ,● AliasFragment(DefSymbol) 】●
  ►#?STRUCTURE_EXPECTED!【 aliasDef = #@TYPEREFS_NB_ANY__LITE ,● AliasFragment(DefSymbol #@TYPEREFS_NB_ANY__LITE)】●
¤》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   alias #@FRAG_PREFIX #@NO_DEFID #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  DefinitionAlias(#@FRAG_PREFIX AliasFragment(#@NO_DEFID))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   alias #@FRAG_PREFIX aliasDef #error(EXP_ASSIGN)【】 #@SEMICOLON_OR_BREAK 
#STRUCTURE_EXPECTED:  DefinitionAlias(#@FRAG_PREFIX AliasFragment(DefSymbol))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   alias #@FRAG_PREFIX aliasDef = #@TYPEREFS_ANY #@SEMICOLON_OR_BREAK 
#STRUCTURE_EXPECTED:  DefinitionAlias(#@FRAG_PREFIX AliasFragment(DefSymbol #@TYPEREFS_ANY))

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  Decl syntax
#PARSE(DECLARATION)   alias #@TYPE_REFS__NB__NO_SINGLE_ID #@NO_DEFID  #@SEMICOLON_OR_BREAK 
#STRUCTURE_EXPECTED:  DefinitionAliasDecl(#@TYPE_REFS__NB__NO_SINGLE_ID #@NO_DEFID)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   alias #@TYPEREFS_NB__LITE  foo  #@SEMICOLON_OR_BREAK 
#STRUCTURE_EXPECTED:  DefinitionAliasDecl(#@TYPEREFS_NB__LITE DefSymbol)


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
