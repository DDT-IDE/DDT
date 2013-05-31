Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(STATEMENT)    try #@ST__NB #error(EXPRULE_CatchFinally)
#STRUCTURE_EXPECTED: StatementTry( #@ST__NB  )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(STATEMENT)    try #@ST__BROKEN 
#STRUCTURE_EXPECTED: StatementTry( #@ST__BROKEN  )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(STATEMENT)    try #@ST__NB  finally  #@ST__SAMPLE
#STRUCTURE_EXPECTED: StatementTry( #@ST__NB  #@ST__SAMPLE )
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@TYPEorVARDECL《
 ►#?STRUCTURE_EXPECTED!【#@NO_TYPEREF   /*NO_DEFID   */● SimpleVarDef(#@NO_TYPEREF #@NO_DEFID )】●
 ►#?STRUCTURE_EXPECTED!【#@TYPEREF_NBNE /*NO_DEFID-ok*/● SimpleVarDef(#@TYPEREF_NBNE #@NO_DEFID )】●
 ►#?STRUCTURE_EXPECTED!【#@TYPEREF_BROKEN              ● SimpleVarDef(#@TYPEREF_BROKEN #@NO_DEFID)】●
 ►#?STRUCTURE_EXPECTED!【#@TYPEREF_NBNE #@DEFID        ● SimpleVarDef(#@TYPEREF_NBNE #@DEFID)】●
¤》
#@TYPEorVARDECL__SAMPLE《
 ►#?STRUCTURE_EXPECTED!【foo[bar] defid ● SimpleVarDef( RefIndexing(RefIdentifier RefIdentifier) #@DEFID ) 】●
¤》


#@CATCH_FINALLY《
 ►#?STRUCTURE_EXPECTED!【 catch #@ST__ANY ●CatchClause( #@ST__ANY ) 】●
 ►#?STRUCTURE_EXPECTED!【 catch(#@TYPEorVARDECL #@NO_ClosePARENS #@BREAK ●CatchClause(#@TYPEorVARDECL )】●
 ►#?STRUCTURE_EXPECTED!【 catch(#@TYPEorVARDECL#@DISCARD_OnPARENS ) #@ST__SAMPLE ●CatchClause( #@TYPEorVARDECL #@ST__SAMPLE ) 】●
 ►#?STRUCTURE_EXPECTED!【 catch(#@TYPEorVARDECL__SAMPLE ) #@ST__ANY ●CatchClause(#@TYPEorVARDECL__SAMPLE #@ST__ANY ) 】●
 ►#?STRUCTURE_EXPECTED!【 #@CATCH__SAMPLE #@CATCH__SAMPLE● #@CATCH__SAMPLE #@CATCH__SAMPLE 】●
 ►#?STRUCTURE_EXPECTED!【 #@CATCH__SAMPLE #error(LAST_CATCH) catch #@ST__NB #@CATCH__SAMPLE●#@CATCH__SAMPLE CatchClause(#@ST__NB) #@CATCH__SAMPLE 】●
 ►#?STRUCTURE_EXPECTED!【 #@CATCH__SAMPLE #error(LAST_CATCH) catch #@ST__NB catch #@ST__ANY●#@CATCH__SAMPLE CatchClause(#@ST__NB) CatchClause(#@ST__ANY) 】●

 ►#?STRUCTURE_EXPECTED!【 finally #@ST__ANY ● #@ST__ANY 】●
 ►#?STRUCTURE_EXPECTED!【 #@CATCH__SAMPLE finally #@ST__ANY● #@CATCH__SAMPLE #@ST__ANY 】●
 ►#?STRUCTURE_EXPECTED!【 #@CATCH__SAMPLE catch #@ST__NB finally #@ST__SAMPLE● #@CATCH__SAMPLE CatchClause( #@ST__NB ) #@ST__SAMPLE 】●
¤》

#@CATCH__SAMPLE《
 ►#?STRUCTURE_EXPECTED!【catch(#@TYPEorVARDECL__SAMPLE) #@ST__SAMPLE ●CatchClause(#@TYPEorVARDECL__SAMPLE #@ST__SAMPLE )】●
¤》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(STATEMENT)    try #@ST__SAMPLE  #@CATCH_FINALLY
#STRUCTURE_EXPECTED: StatementTry( #@ST__SAMPLE  #@CATCH_FINALLY )
