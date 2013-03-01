Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 

#@EXP_DISCARD_CASE《#?AST_STRUCTURE_EXPECTED!【666/*NoCase*/●Integer】》

#@EXP_PRIMARY_SIMPLE《
  ►#?AST_STRUCTURE_EXPECTED!【this●ExpThis】●
  ►#?AST_STRUCTURE_EXPECTED!【super●ExpSuper】●
  ►#?AST_STRUCTURE_EXPECTED!【null●ExpNull】●
  ►#?AST_STRUCTURE_EXPECTED!【true●ExpLiteralBool】●
  ►#?AST_STRUCTURE_EXPECTED!【false●ExpLiteralBool】●
  ►#?AST_STRUCTURE_EXPECTED!【$●ExpArrayLength】●
  ►#?AST_STRUCTURE_EXPECTED!【'"'●ExpLiteralChar】●
  ►#?AST_STRUCTURE_EXPECTED!【#@X《12●123_45Lu》●Integer】●
  ►#?AST_STRUCTURE_EXPECTED!【#@X《123.0F●.456E12●0x25_AD_3FP+1》●Float】●
  ►#?AST_STRUCTURE_EXPECTED!【#@X《"abc"●r"inline"q{ TOKEN string }`sfds`》●String】●
  
  ►#?AST_STRUCTURE_EXPECTED!【__FILE__●ExpLiteralString】●
  ►#?AST_STRUCTURE_EXPECTED!【__LINE__●ExpLiteralInteger】●
¤》

#@EXP_PRIMARY_APPENDABLE《
  ►#@EXP_PRIMARY_SIMPLE●
  
  ►#?AST_STRUCTURE_EXPECTED!【 [1, .456E12, 0x25_AD_3FP+1] ●ExpLiteralArray(Integer Float Float)】●
  ►#?AST_STRUCTURE_EXPECTED!【 [12345 : true, 66.6 : false, "asdfd" : "false"]●
  ExpLiteralMapArray(MapEntry(Integer Bool) MapEntry(Float Bool) MapEntry(String String))】●
  
  ►#?AST_STRUCTURE_EXPECTED!【(123 ,"asd")●ExpParentheses(ExpInfix(Integer String))】●
  
  ►#?AST_STRUCTURE_EXPECTED!【assert(2 + 2 == true, "error")●ExpAssert(* String)】●
  ►#?AST_STRUCTURE_EXPECTED!【mixin("2 + " ~ "2")●ExpMixinString(*)】●
  ►#?AST_STRUCTURE_EXPECTED!【import("testdata/samples.txt")●ExpImportString(String)】●
  
  ►#?AST_STRUCTURE_EXPECTED!【foo(1, 3)●             ExpCall(#@ExpIdentifier Integer Integer)】●
  ►#?AST_STRUCTURE_EXPECTED!【foo[12 .. [123]]●      ExpSlice(#@ExpIdentifier Integer ExpLiteralArray(?))】●
  ►#?AST_STRUCTURE_EXPECTED!【foo++●                 ExpPostfix(#@ExpIdentifier)】●
  
  
  ►#?EXP__NO_AMBIGS【#@EXP_DISCARD_CASE●
    ►#?AST_STRUCTURE_EXPECTED!【foo[]●                 ExpSlice(#@ExpIdentifier)】 】●
  ►#?EXP__NO_AMBIGS【#@EXP_DISCARD_CASE●
    ►#?AST_STRUCTURE_EXPECTED!【foo[[123]]●            ExpIndex(#@ExpIdentifier ExpLiteralArray(?))】】●
  
¤》

#@TYPE_AS_EXP《#error(TYPE_AS_EXP_VALUE){int}》

#@EXP_UNARY_REFS《
  ►#?EXP__NO_AMBIGS【#@EXP_DISCARD_CASE●
   ►#?AST_STRUCTURE_EXPECTED!【  foo● ExpReference(RefIdentifier)】 】●
  ►#?EXP__NO_REFS【#@EXP_DISCARD_CASE●
   ►#?AST_STRUCTURE_EXPECTED!【  #@TYPE_AS_EXP● ExpReference(RefPrimitive)】 】●
  ►#?EXP__NO_AMBIGS【#@EXP_DISCARD_CASE●
    ►#?AST_STRUCTURE_EXPECTED!【  .foo● ExpReference(RefModuleQualified(RefIdentifier))】 】●
  ►#?EXP__NO_AMBIGS【#@EXP_DISCARD_CASE●
    ►#?AST_STRUCTURE_EXPECTED!【  .foo.bar● ExpReference(RefQualified(RefModuleQualified(?) RefIdentifier))】 】●
  
  ►#?AST_STRUCTURE_EXPECTED!【 (int[]).init●ExpReference(RefQualified(ExpParentheses(RefTypeDynArray(*)) ?))】●
  ►#?AST_STRUCTURE_EXPECTED!【 (.foo).bar.xxx●ExpReference(RefQualified(RefQualified(ExpParentheses(*) ?) ?))】●
  
  ►#?AST_STRUCTURE_EXPECTED!【  #@TYPE_AS_EXP* #@NULL_EXP● ExpInfix(ExpReference(RefPrimitive) #@NULL_EXP)】●
  ►#?AST_STRUCTURE_EXPECTED!【  foo * #@NULL_EXP● ExpInfix(ExpReference(RefIdentifier) #@NULL_EXP)】●

  ►#?AST_STRUCTURE_EXPECTED!【  foo[]● ExpSlice(ExpReference(RefIdentifier))】●
  ►#?AST_STRUCTURE_EXPECTED!【  #@TYPE_AS_EXP []● ExpSlice(ExpReference(RefPrimitive))】●
  ►#?AST_STRUCTURE_EXPECTED!【  foo[ #@TYPE_AS_EXP ]● ExpIndex(ExpReference(RefIdentifier) ExpReference(RefPrimitive))】●
  ►#?AST_STRUCTURE_EXPECTED!【  #@TYPE_AS_EXP [foo]● ExpIndex(ExpReference(RefPrimitive) ExpReference(RefIdentifier))】●

  ►#?AST_STRUCTURE_EXPECTED!【foo**[bar*#@NULL_EXP]* #@NULL_EXP●
ExpInfix(
  ExpInfix(  #@ExpIdentifier  ExpPrefix(ExpLiteralArray(ExpInfix(#@ExpIdentifier #@NULL_EXP)))  )
  #@NULL_EXP 
)】●
¤》

#@EXP_UNARY《
  ►#@EXP_PRIMARY_APPENDABLE●
  ►#?AST_STRUCTURE_EXPECTED!【123 ^^ exp●            ExpInfix(Integer #@ExpIdentifier)】●
  ►#?AST_STRUCTURE_EXPECTED!【new(123, foo) .bar.Foo(+1, foo[123])●ExpNew(? #@ExpIdentifier RefQualified(* *) * *)】●
  ►#?AST_STRUCTURE_EXPECTED!【cast(Foo) foo●         ExpCast(RefIdentifier #@ExpIdentifier)】●

  ►#?AST_STRUCTURE_EXPECTED!【*foo[]●      ExpPrefix(ExpSlice(#@ExpIdentifier))】●
  ►#?AST_STRUCTURE_EXPECTED!【!foo[[123]]● ExpPrefix(ExpIndex(#@ExpIdentifier ExpLiteralArray(?)))】●
  
  ►#?EXP__NO_REFS【#@EXP_DISCARD_CASE●#@EXP_UNARY_REFS】●
  
  ►#@EXP_UNARY__LITE●
¤》
#@EXP_UNARY__LITE《
  ►#?AST_STRUCTURE_EXPECTED!【42●Integer】●
  ►#?EXP__NO_REFS《#@EXP_DISCARD_CASE●
    ►#?AST_STRUCTURE_EXPECTED!【  #@TYPE_AS_EXP [foo]● ExpIndex(ExpReference(RefPrimitive) ExpReference(RefIdentifier))】》●
¤》

#@EXP_UNARY__NO_REFS《 #EXP__NO_REFS(flag) #@EXP_UNARY 》


#@EXP_OROR《
  ►#@EXP_UNARY●
  ►#?AST_STRUCTURE_EXPECTED!【4 / 6●ExpInfix(? ?)】●
  ►#?AST_STRUCTURE_EXPECTED!【1 + 2●ExpInfix(? ?)】●
  ►#?AST_STRUCTURE_EXPECTED!【1 << 16●ExpInfix(? ?)】●
  ►#?AST_STRUCTURE_EXPECTED!【0xFF & 123●ExpInfix(? ?)】●
  ►#?AST_STRUCTURE_EXPECTED!【0xFF | 0xAA●ExpInfix(? ?)】●
  ►#?AST_STRUCTURE_EXPECTED!【1 > "2" && 3●ExpInfix(ExpInfix(? String) ?)】●
  ►#?AST_STRUCTURE_EXPECTED!【2 || "3" < 4●ExpInfix(? ExpInfix(String ?))】●
  ►#?AST_STRUCTURE_EXPECTED!【foo *** ptr ●ExpInfix(ExpReference(?) ExpPrefix(ExpPrefix(ExpReference(RefIdentifier))) )】●
  
  ►#@EXP_OROR__LITE●
¤》
#@EXP_OROR__LITE《
  ►#?AST_STRUCTURE_EXPECTED!【2 ~ [1, 0xFF, "3"] == null ~ [123 : "entry"] || assert(2 < "four" + length)●
ExpInfix(
  ExpInfix(  ExpInfix(Integer ExpLiteralArray(* * *))   ExpInfix(ExpNull ExpLiteralMapArray(*))  )
  ExpAssert(ExpInfix(Integer ExpInfix(String ExpReference(RefIdentifier))))
)】●
¤》

#@EXP_CONDITIONAL《#@EXP_OROR●#@EXP_CONDITIONAL__LITE》
#@EXP_CONDITIONAL__LITE《
  ►#?AST_STRUCTURE_EXPECTED!【false ? 123 : 456●ExpConditional(Bool Integer Integer)】●
¤》

#@EXP_ASSIGN《#@EXP_CONDITIONAL●#@EXP_ASSIGN__LITE》
#@EXP_ASSIGN__LITE《
  ►#?AST_STRUCTURE_EXPECTED!【this = super += null●ExpInfix(ExpThis ExpInfix(ExpSuper ExpNull))】●
¤》

#@EXP_COMMA《#@EXP_ASSIGN●#@EXP_CONDITIONAL__LITE》
#@EXP_COMMA__LITE《
  ►#?AST_STRUCTURE_EXPECTED!【12,"asd"●ExpInfix(Integer String)】●
¤》


#@EXP_ANY《#@EXP_COMMA》

#@EXP_ASSIGN__NO_REFS__NO_AMBIGS《 #EXP__NO_AMBIGS(flag)#EXP__NO_REFS(flag) #@EXP_ASSIGN 》
#@EXP_ASSIGN__NO_REFS《 #EXP__NO_REFS(flag) #@EXP_ASSIGN 》
#@EXPS__NO_REFS__NO_AMBIGS《 #EXP__NO_REFS(flag)#EXP__NO_AMBIGS(flag) #@EXP_ANY 》
#@EXPS__NO_REFS《 #EXP__NO_REFS(flag) #@EXP_ANY 》

// TODO This is not as precise, we would only want to exclude snippets ending in *, 
// such that with a suffix they could be parsed as exp. 
#@EXP_ASSIGN__NO_PENDING《 #EXP__NO_REFS(flag) #@EXP_ASSIGN 》   
#@EXP_UNARY__NO_PENDING《  ►#?AST_STRUCTURE_EXPECTED!【/*UNARY_NO_PENDING*/】#EXP__NO_REFS(flag)#@EXP_UNARY 》

