Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 

#@EXP_DISCARD_CASE《#?STRUCTURE_EXPECTED!【666/*NoCase*/●Integer】》

#@TYPE_AS_EXP《#?STRUCTURE_EXPECTED!【#error(TYPE_AS_EXP_VALUE)【int】●ExpReference(RefPrimitive)】》
 #@INT_AS_EXP《#?STRUCTURE_EXPECTED!【#error(TYPE_AS_EXP_VALUE)【int】●ExpReference(RefPrimitive)】》

#@EXP_PRIMARY_SIMPLE《
  ►#?STRUCTURE_EXPECTED!【this●ExpThis】●
  ►#?STRUCTURE_EXPECTED!【false●ExpLiteralBool】●
  ►#?STRUCTURE_EXPECTED!【$●ExpArrayLength】●
  ►#?STRUCTURE_EXPECTED!【'"'●ExpLiteralChar】●
  ►#?STRUCTURE_EXPECTED!【12●Integer】●
  ►#?STRUCTURE_EXPECTED!【0x25_AD_3FP+1●Float】●
  ►#?STRUCTURE_EXPECTED!【123.0F●Float】●
  ►#?STRUCTURE_EXPECTED!【r"inline"`sfds`q{ TOKEN string }●String】●
  
  ►#?STRUCTURE_EXPECTED!【__FILE__●ExpLiteralString】●
  ►#?STRUCTURE_EXPECTED!【__LINE__●ExpLiteralInteger】●
¤》

#@EXP_POSTFIX《
  ►#@EXP_PRIMARY_SIMPLE●
  
  ►#?STRUCTURE_EXPECTED!【 [123] ●ExpLiteralArray(Integer)】●
  ►#?STRUCTURE_EXPECTED!【 [1, .456E12, 0x25_AD_3FP+1] ●ExpLiteralArray(Integer Float Float)】●
  ►#?STRUCTURE_EXPECTED!【 [12345 : true, 66.6 : false, "asdfd" : "false"]●
  ExpLiteralMapArray(MapEntry(Integer Bool) MapEntry(Float Bool) MapEntry(String String))】●
  
  ►#?STRUCTURE_EXPECTED!【(123 ,"asd")●ExpParentheses(ExpInfix(Integer String))】●
  
  ►#?STRUCTURE_EXPECTED!【assert(2 + 2 == true, "error")●ExpAssert(* String)】●
  ►#?STRUCTURE_EXPECTED!【mixin("2 + " ~ "2")●ExpMixinString(*)】●
  ►#?STRUCTURE_EXPECTED!【import("testdata/samples.txt")●ExpImportString(String)】●
  
  ►#?STRUCTURE_EXPECTED!【foo(1, 3)●             ExpCall(#@ExpIdentifier Integer Integer)】●
  ►#?STRUCTURE_EXPECTED!【foo[12 .. [123]]●      ExpSlice(#@ExpIdentifier Integer ExpLiteralArray(?))】●
  ►#?STRUCTURE_EXPECTED!【foo++●                 ExpPostfix(#@ExpIdentifier)】●
  
  ►#?STRUCTURE_EXPECTED!【 (int[]).init●ExpReference(RefQualified(ExpParentheses(RefTypeDynArray(*)) ?))】●
  ►#?STRUCTURE_EXPECTED!【 (.foo).bar.xxx●ExpReference(RefQualified(?(RefQualified(ExpParentheses(*) ?)) ?))】●
  
  
  ►#?EXP__NO_AMBIGS【#@EXP_DISCARD_CASE●
    ►#?STRUCTURE_EXPECTED!【foo[]●                 ExpSlice(#@ExpIdentifier)】 】●
  ►#?EXP__NO_AMBIGS【#@EXP_DISCARD_CASE●
    ►#?STRUCTURE_EXPECTED!【foo[[123]]●            ExpIndex(#@ExpIdentifier ExpLiteralArray(?))】】●
  
  ►#?STRUCTURE_EXPECTED!【 foo[].init●ExpReference(RefQualified(ExpSlice(#@ExpIdentifier) RefIdentifier))】●
¤》
 #@EXP_POSTFIX__NO_UNAMBIG_REFS《 #EXP__NO_UNAMBIG_REFS(flag) #@EXP_POSTFIX 》
#@EXP_POSTFIX__NO_POSSIBLE_REFS《 #EXP__NO_UNAMBIG_REFS(flag)#EXP__NO_AMBIGS(flag) #@EXP_POSTFIX 》


EXP__NO_PENDING_MISSING : exclude cases that could have been parsed further if an EXP was appended
EXP__NO_UNAMBIG_REFS : exclude cases that can only be cleanly parsed as refs

#@EXP_UNARY_REFS《
   ►#?EXP__NO_AMBIGS【#@EXP_DISCARD_CASE●
  ►#?STRUCTURE_EXPECTED!【  foo● ExpReference(RefIdentifier)】 】●
   ►#?EXP__NO_UNAMBIG_REFS【#@EXP_DISCARD_CASE●
  ►#?STRUCTURE_EXPECTED!【  #@INT_AS_EXP● #@INT_AS_EXP 】 】●
   ►#?EXP__NO_AMBIGS【#@EXP_DISCARD_CASE●

  ►#?STRUCTURE_EXPECTED!【  .foo● ExpReference(RefModuleQualified(RefIdentifier))】 】●
   ►#?EXP__NO_AMBIGS【#@EXP_DISCARD_CASE●
  ►#?STRUCTURE_EXPECTED!【  .foo.bar● ExpReference(RefQualified(RefModuleQualified(?) RefIdentifier))】 】●
  
   ►#?EXP__NO_AMBIGS【#@EXP_DISCARD_CASE●
  ►#?STRUCTURE_EXPECTED!【 foo!(int)●ExpReference(RefTemplateInstance(RefIdentifier RefPrimitive) )】 】●
  
   ►#?EXP__NO_AMBIGS【#@EXP_DISCARD_CASE●
  ►#?STRUCTURE_EXPECTED!【foo[]● ExpSlice(#@ExpIdentifier)】】●
  
   ►#?EXP__NO_UNAMBIG_REFS【#@EXP_DISCARD_CASE●
  ►#?STRUCTURE_EXPECTED!【#error(TYPE_AS_EXP_VALUE)【int []】● ExpReference(RefTypeDynArray(RefPrimitive))】】●
   ►#?EXP__NO_UNAMBIG_REFS【#@EXP_DISCARD_CASE●
  ►#?STRUCTURE_EXPECTED!【#error(TYPE_AS_EXP_VALUE)【foo[int]】●ExpReference(RefIndexing(RefIdentifier RefPrimitive))】】●
   ►#?EXP__NO_UNAMBIG_REFS【#@EXP_DISCARD_CASE●
  ►#?STRUCTURE_EXPECTED!【#error(TYPE_AS_EXP_VALUE)【int[foo]】●ExpReference(RefIndexing(RefPrimitive RefIdentifier))】】●
¤》

#@EXP_NON_UNARY_REFS《
   ►#?EXP__NO_BREAKING_EXPS【#@EXP_DISCARD_CASE●
   ►#?EXP__NO_AMBIGS【#@EXP_DISCARD_CASE● 
  ►#?STRUCTURE_EXPECTED!【foo * #@NO_EXP #EXP_RULE_BROKEN(flag)● ExpInfix(ExpReference(RefIdentifier) #@NO_EXP)】】】●

   ►#?EXP__NO_BREAKING_EXPS【#@EXP_DISCARD_CASE●
   ►#?EXP__NO_UNAMBIG_REFS【#@EXP_DISCARD_CASE● 
  ►#?STRUCTURE_EXPECTED!【#error(TYPE_AS_EXP_VALUE)【void *】 #EXP_RULE_BROKEN(flag)● ExpReference(RefTypePointer(RefPrimitive))】】】●
  
   ►#?EXP__NO_BREAKING_EXPS【#@EXP_DISCARD_CASE●
   ►#?EXP__NO_UNAMBIG_REFS【#@EXP_DISCARD_CASE●
  ►#?STRUCTURE_EXPECTED!【 #error(TYPE_AS_EXP_VALUE)【foo**[bar* /++/] *】  #EXP_RULE_BROKEN(flag)●
  ExpReference(
   RefTypePointer(RefIndexing(RefTypePointer(RefTypePointer(RefIdentifier)) RefTypePointer(RefIdentifier) ))
  )】】】●
¤》

#@EXP_REFS《
  ►#@EXP_UNARY_REFS●
   ►#?EXP__NO_PENDING_MISSING【#@EXP_DISCARD_CASE●
  ►#@EXP_NON_UNARY_REFS】●
¤》

#@EXP_UNARY《
  ►#@EXP_POSTFIX●
  ►#@EXP_UNARY_REFS●
  
  ►#?STRUCTURE_EXPECTED!【123 ^^ exp●            ExpInfix(Integer #@ExpIdentifier)】●
  ►#?STRUCTURE_EXPECTED!【new(123, foo) .bar.Foo(+1, foo[123])●ExpNew(? #@ExpIdentifier RefQualified(* *) * *)】●
  ►#?STRUCTURE_EXPECTED!【cast(Foo) foo●         ExpCast(RefIdentifier #@ExpIdentifier)】●

  ►#?STRUCTURE_EXPECTED!【*foo[]●      ExpPrefix(ExpSlice(#@ExpIdentifier))】●
  ►#?STRUCTURE_EXPECTED!【!foo[[123]]● ExpPrefix(ExpIndex(#@ExpIdentifier ExpLiteralArray(?)))】●
  
¤》
#@EXP_UNARY__LITE《
  ►#?STRUCTURE_EXPECTED!【!foo[[123]]● ExpPrefix(ExpIndex(#@ExpIdentifier ExpLiteralArray(?)))】●
¤》
 #@EXP_UNARY__NO_UNAMBIG_REFS《 #EXP__NO_UNAMBIG_REFS(flag) #@EXP_UNARY 》
#@EXP_UNARY__NO_POSSIBLE_REFS《 #EXP__NO_UNAMBIG_REFS(flag)#EXP__NO_AMBIGS(flag) #@EXP_UNARY 》
#@EXP_UNARY__NO_PENDING《#EXP__NO_PENDING_MISSING(flag) #?STRUCTURE_EXPECTED!【/*UNARY_NO_PENDING*/】#@EXP_UNARY 》


#@EXP_OROR《
  ►#@EXP_UNARY●
   ►#?EXP__NO_PENDING_MISSING【#@EXP_DISCARD_CASE●
  ►#@EXP_NON_UNARY_REFS】●
  
  ►#?STRUCTURE_EXPECTED!【4 / 6●ExpInfix(? ?)】●
  ►#?STRUCTURE_EXPECTED!【1 + 2●ExpInfix(? ?)】●
  ►#?STRUCTURE_EXPECTED!【1 << 16●ExpInfix(? ?)】●
  ►#?STRUCTURE_EXPECTED!【0xFF & 123●ExpInfix(? ?)】●
  ►#?STRUCTURE_EXPECTED!【0xFF | 0xAA●ExpInfix(? ?)】●
  ►#?STRUCTURE_EXPECTED!【1 > "2" && 3●ExpInfix(ExpInfix(? String) ?)】●
  ►#?STRUCTURE_EXPECTED!【2 || "3" < 4●ExpInfix(? ExpInfix(String ?))】●
  ►#?STRUCTURE_EXPECTED!【foo *** ptr ●ExpInfix(ExpReference(?) ExpPrefix(ExpPrefix(ExpReference(RefIdentifier))) )】●
  
  ►#@EXP_OROR__LITE●
¤》
#@EXP_OROR__LITE《
  ►#?STRUCTURE_EXPECTED!【2 ~ [1, 0xFF, "3"] == null ~ [123 : "entry"] || assert(2 < "four" + length)●
ExpInfix(
  ExpInfix(  ExpInfix(Integer ExpLiteralArray(* * *))   ExpInfix(ExpNull ExpLiteralMapArray(*))  )
  ExpAssert(ExpInfix(Integer ExpInfix(String ExpReference(RefIdentifier))))
)】●
¤》

#@EXP_CONDITIONAL《#@EXP_OROR●#@EXP_CONDITIONAL__LITE》
#@EXP_CONDITIONAL__LITE《
  ►#?STRUCTURE_EXPECTED!【false ? 123 : 456●ExpConditional(Bool Integer Integer)】●
¤》
#@EXP_COND《#@EXP_CONDITIONAL》
#@EXP_COND__LITE《#@EXP_CONDITIONAL__LITE》

#@EXP_ASSIGN《#@EXP_CONDITIONAL●#@EXP_ASSIGN__LITE》
#@EXP_ASSIGN__LITE《
  ►#?STRUCTURE_EXPECTED!【this = super += null●ExpInfix(ExpThis ExpInfix(ExpSuper ExpNull))】●
¤》

#@EXP_COMMA《#@EXP_ASSIGN●#@EXP_CONDITIONAL__LITE》
#@EXP_COMMA__LITE《
  ►#?STRUCTURE_EXPECTED!【12,"asd"●ExpInfix(Integer String)】●
¤》


#@EXP_ANY《#@EXP_COMMA》
#@EXP_ANY__LITE《#@EXP_OROR__LITE》

#@EXP_NO_PENDING《 #EXP__NO_PENDING_MISSING(flag) #@EXP_ANY 》

  #@EXP_COND__NO_POSSIBLE_REFS《 #EXP__NO_AMBIGS(flag)#EXP__NO_UNAMBIG_REFS(flag) #@EXP_COND 》
#@EXP_ASSIGN__NO_POSSIBLE_REFS《 #EXP__NO_AMBIGS(flag)#EXP__NO_UNAMBIG_REFS(flag) #@EXP_ASSIGN 》
      #@EXPS__NO_POSSIBLE_REFS《 #EXP__NO_UNAMBIG_REFS(flag)#EXP__NO_AMBIGS(flag) #@EXP_ANY 》
  #@EXP_COND__NO_UNAMBIG_REFS《 #EXP__NO_UNAMBIG_REFS(flag) #@EXP_COND 》
#@EXP_ASSIGN__NO_UNAMBIG_REFS《 #EXP__NO_UNAMBIG_REFS(flag) #@EXP_ASSIGN 》
      #@EXPS__NO_UNAMBIG_REFS《 #EXP__NO_UNAMBIG_REFS(flag) #@EXP_ANY 》

#@EXP_ASSIGN__NO_PENDING《 #EXP__NO_PENDING_MISSING(flag) #@EXP_ASSIGN 》   
