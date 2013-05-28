Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@VERSION_VALUE《
  ►#?STRUCTURE_EXPECTED!【 123● VersionSymbol】●
  ►#?STRUCTURE_EXPECTED!【 id ● VersionSymbol】●
  ►#?STRUCTURE_EXPECTED!【  #error(EXPRULE_VersionArg) ● VersionSymbol】●
¤》
#@DEBUG_VALUE《
  ►#?STRUCTURE_EXPECTED!【 123● VersionSymbol】●
  ►#?STRUCTURE_EXPECTED!【 id ● VersionSymbol】●
  ►#?STRUCTURE_EXPECTED!【  #error(EXPRULE_DebugArg) ● VersionSymbol】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   version #error(EXP_OPEN_PARENS)【】  #@BREAK
#STRUCTURE_EXPECTED:  DeclarationDebugVersion( )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   debug #@NO_DECLorBLOCK #@BREAK
#STRUCTURE_EXPECTED:  DeclarationDebugVersion( #@NO_DECLorBLOCK )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   version = #@VERSION_VALUE #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  DeclarationDebugVersionSpec( #@VERSION_VALUE )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   debug = #@DEBUG_VALUE #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  DeclarationDebugVersionSpec( #@DEBUG_VALUE )
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@VERSION_ARG《
  ►#@VERSION_VALUE●
  ►#?STRUCTURE_EXPECTED!【 unittest ● VersionSymbol】●
  ►#?STRUCTURE_EXPECTED!【 assert ● VersionSymbol】●
¤》

#@CONDITION《
  ►#?STRUCTURE_EXPECTED!【version   #error(EXP_OPEN_PARENS)【】 #@BREAK ●DeclarationDebugVersion( #@BREAK】●
  ►#?STRUCTURE_EXPECTED!【version( #@VERSION_ARG #@CLOSEPARENS_OR_BREAK●DeclarationDebugVersion(#@VERSION_ARG #@CLOSEPARENS_OR_BREAK】●

  ►#?STRUCTURE_EXPECTED!【debug ● DeclarationDebugVersion(】●
  ►#?STRUCTURE_EXPECTED!【debug(  #@DEBUG_VALUE  #@CLOSEPARENS_OR_BREAK●DeclarationDebugVersion(#@DEBUG_VALUE #@CLOSEPARENS_OR_BREAK】●

  ►#?STRUCTURE_EXPECTED!【static if #error(EXP_OPEN_PARENS)【】 #@BREAK ●DeclarationStaticIf( #@BREAK】●
  ►#?STRUCTURE_EXPECTED!【static if(#EXP__NO_PENDING_MISSING(flag) #@EXP_ASSIGN #@CLOSEPARENS_OR_BREAK●DeclarationStaticIf(#@EXP_ASSIGN #@CLOSEPARENS_OR_BREAK】●
  ►#?STRUCTURE_EXPECTED!【static if(#@NO_EXP)●DeclarationStaticIf(#@NO_EXP 】●
¤》


#@COND_DECLS_NB《
  ►#?STRUCTURE_EXPECTED!【#@NO_DECLorBLOCK #@BREAK ● #@NO_DECLorBLOCK #@BREAK 】●
  ►#?STRUCTURE_EXPECTED!【{ } ● DeclBlock( )】●
  ►#?STRUCTURE_EXPECTED!【#@DECLS_BLOCK__SAMPLE●#@DECLS_BLOCK__SAMPLE】●
  ►#?STRUCTURE_EXPECTED!【#@SINGLE_DECL_SAMPLE●#@SINGLE_DECL_SAMPLE】●
¤》
#@ELSE_DECLS《
  ►#?STRUCTURE_EXPECTED!【#@NO_DECLorBLOCK #@BREAK ● #@NO_DECLorBLOCK #@BREAK 】●
  ►#@DECLS_BLOCK__NO_MISSING●
¤》

#@COND_BODY《
  ►#?STRUCTURE_EXPECTED!【#@COND_DECLS_NB    ● #@COND_DECLS_NB 】●
  ►#?STRUCTURE_EXPECTED!【#@DECLS_BLOCK__BROKEN ● #@DECLS_BLOCK__BROKEN 】●
  ►#?STRUCTURE_EXPECTED!【#@COND_DECLS_NB else #@ELSE_DECLS       ● #@COND_DECLS_NB #@ELSE_DECLS】●
  
  ►#?STRUCTURE_EXPECTED!【: ● DeclList】●
  ►#?STRUCTURE_EXPECTED!【: #@DECL_LIST_SAMPLE ● DeclList( #@DECL_LIST_SAMPLE )】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   #@CONDITION #@COND_BODY
#STRUCTURE_EXPECTED:  #@CONDITION #@COND_BODY )

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Statement variation

#@COND_BODY_ST《
  ►#?STRUCTURE_EXPECTED!【#@ST__NB     ● #BLOCK_ST_UNSCOPED(flag) #@ST__NB 】●
  ►#?STRUCTURE_EXPECTED!【#@ST__BROKEN ● #BLOCK_ST_UNSCOPED(flag) #@ST__BROKEN 】●
  ►#?STRUCTURE_EXPECTED!【#@ST__NB else #@ST__ANY ● #BLOCK_ST_UNSCOPED(flag) #@ST__NB #@ST__ANY】●
  
  ►#?STRUCTURE_EXPECTED!【#@NO_STOrBLOCK #@IGNORE_REST : ● #BLOCK_ST_UNSCOPED(flag) #@NO_STOrBLOCK 】●
  ►#?STRUCTURE_EXPECTED!【#@NO_STOrBLOCK #@IGNORE_REST invariant() /*Test that non-statements are not parsed*/ ● #BLOCK_ST_UNSCOPED(flag)#@NO_STOrBLOCK 】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)     version #error(EXP_OPEN_PARENS)【】  #@BREAK
#STRUCTURE_EXPECTED:  DeclarationDebugVersion( )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)     debug  #@NO_STOrBLOCK 
#STRUCTURE_EXPECTED:  DeclarationDebugVersion( #BLOCK_ST_UNSCOPED(flag) #@NO_STOrBLOCK )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)     #@CONDITION #@COND_BODY_ST
#STRUCTURE_EXPECTED:  #@CONDITION #@COND_BODY_ST )
