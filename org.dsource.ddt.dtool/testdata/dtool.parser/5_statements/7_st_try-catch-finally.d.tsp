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

#@CATCH_FINALLY《
 ►#?STRUCTURE_EXPECTED!【 catch #@ST__ANY ●TryCatchClause( #@ST__ANY ) 】●
 ►#?STRUCTURE_EXPECTED!【 catch(#@SIMPLE_VAR_DEF #@NO_ClosePARENS #@BREAK ●TryCatchClause(#@SIMPLE_VAR_DEF )】●
 ►#?STRUCTURE_EXPECTED!【 catch(#@SIMPLE_VAR_DEF#@DISCARD_OnPARENS ) #@ST__SAMPLE ●TryCatchClause( #@SIMPLE_VAR_DEF #@ST__SAMPLE ) 】●
 ►#?STRUCTURE_EXPECTED!【 catch(#@SIMPLE_VAR_DEF__SAMPLE ) #@ST__ANY ●TryCatchClause(#@SIMPLE_VAR_DEF__SAMPLE #@ST__ANY ) 】●
 ►#?STRUCTURE_EXPECTED!【 #@CATCH__SAMPLE #@CATCH__SAMPLE● #@CATCH__SAMPLE #@CATCH__SAMPLE 】●
 ►#?STRUCTURE_EXPECTED!【 #@CATCH__SAMPLE #error(LAST_CATCH) catch #@ST__NB #@CATCH__SAMPLE●#@CATCH__SAMPLE TryCatchClause(#@ST__NB) #@CATCH__SAMPLE 】●
 ►#?STRUCTURE_EXPECTED!【 #@CATCH__SAMPLE #error(LAST_CATCH) catch #@ST__NB catch #@ST__ANY●#@CATCH__SAMPLE TryCatchClause(#@ST__NB) TryCatchClause(#@ST__ANY) 】●

 ►#?STRUCTURE_EXPECTED!【 finally #@ST__ANY ● #@ST__ANY 】●
 ►#?STRUCTURE_EXPECTED!【 #@CATCH__SAMPLE finally #@ST__ANY● #@CATCH__SAMPLE #@ST__ANY 】●
 ►#?STRUCTURE_EXPECTED!【 #@CATCH__SAMPLE catch #@ST__NB finally #@ST__SAMPLE● #@CATCH__SAMPLE TryCatchClause( #@ST__NB ) #@ST__SAMPLE 】●
¤》

#@CATCH__SAMPLE《
 ►#?STRUCTURE_EXPECTED!【catch(#@SIMPLE_VAR_DEF__SAMPLE) #@ST__SAMPLE ●TryCatchClause(#@SIMPLE_VAR_DEF__SAMPLE #@ST__SAMPLE )】●
¤》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(STATEMENT)    try #@ST__SAMPLE  #@CATCH_FINALLY
#STRUCTURE_EXPECTED: StatementTry( #@ST__SAMPLE  #@CATCH_FINALLY )
