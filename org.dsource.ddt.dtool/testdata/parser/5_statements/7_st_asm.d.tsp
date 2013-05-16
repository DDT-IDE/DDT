Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@ASM_BODY《
 ►#?STRUCTURE_EXPECTED!【 #error(EXP_OPEN_BRACE)¤【】  ●  】●
 ►#?STRUCTURE_EXPECTED!【 { while(true) {} #@CLOSEBRACE_OR_BREAK ● #@CLOSEBRACE_OR_BREAK】●
 ►#?STRUCTURE_EXPECTED!【 { {}{ while(true) {} } #@CLOSEBRACE_OR_BREAK ● #@CLOSEBRACE_OR_BREAK】●
 ►#?STRUCTURE_EXPECTED!【 { { { while(true)  #@NO_CloseBRACE #@BREAK ● #@BREAK】●
 ►#?STRUCTURE_EXPECTED!【 {  mov EAX,x; #@CLOSEBRACE_OR_BREAK ● #@CLOSEBRACE_OR_BREAK 】●
 ►#?STRUCTURE_EXPECTED!【 { success  blah #error:ITC{} 1234214 #@CLOSEBRACE_OR_BREAK ● #@CLOSEBRACE_OR_BREAK】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)    asm #@ASM_BODY  
#STRUCTURE_EXPECTED: StatementAsm( #@ASM_BODY  )
