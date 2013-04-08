▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test possible refs under TypeOrExp parse
#PARSE(TypeOrExp)         #@CAN_BE_REF《#@TYPEREFS_AMBIG●#@TYPEREFS_UNAMBIG》
#AST_STRUCTURE_EXPECTED:  #@CAN_BE_REF
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  special cases
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION_ToE)    [123]  // regression test for canBeRef at exp Start
#AST_STRUCTURE_EXPECTED:  ExpLiteralArray(Integer)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION_ToE)    []  // regression test for canBeRef at exp Start
#AST_STRUCTURE_EXPECTED:  ExpLiteralArray()
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION_ToE)    #error(TYPE_AS_EXP_VALUE)【foo * [int]】 ++
#AST_STRUCTURE_EXPECTED:  ExpPostfix(ExpReference(RefIndexing(RefTypePointer(RefIdentifier) RefPrimitive)))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION_ToE)    #error(TYPE_AS_EXP_VALUE)【foo * [int]】 ++ [ #error(EXP_CLOSE_BRACKET) #@BREAK = 2 
#AST_STRUCTURE_EXPECTED: ExpSlice(*)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(EXPRESSION_ToE)    * #@NO_EXP // special negative case test #@BREAK
#AST_STRUCTURE_EXPECTED:  ExpPrefix(#@NO_EXP)

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ ambig cases that can parse as EXP with missing exp in the end
#@SUFFIX_OR_NO《  ●  = 2 ● ^^ 52 》  Suffix doesnt gets parsed. 
      #@ALT_PREFIX《 ● ►#?AST_STRUCTURE_EXPECTED!【7 +●ExpInfix(Integer 】》
#@ALT_PREFIX_END《 #@《 ●)》(ALT_PREFIX) 》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(EXPRESSION)       #@ALT_PREFIX foo *   #@NO_EXP  #@BREAK #@SUFFIX_OR_NO
#AST_STRUCTURE_EXPECTED: #@ALT_PREFIX ExpInfix(#@ExpIdentifier #@NO_EXP) $
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(EXPRESSION)       #@ALT_PREFIX foo * * #@NO_EXP  #@BREAK #@SUFFIX_OR_NO
#AST_STRUCTURE_EXPECTED: #@ALT_PREFIX ExpInfix(#@ExpIdentifier ExpPrefix(#@NO_EXP)) $
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(EXPRESSION)       #EXP__NO_PENDING_MISSING(flag)#@EXP_UNARY__NO_UNAMBIG_REFS * #@NO_EXP #@BREAK
#AST_STRUCTURE_EXPECTED: ExpInfix(#@EXP_UNARY__NO_UNAMBIG_REFS #@NO_EXP)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(EXPRESSION)       #error(TYPE_AS_EXP_VALUE)【int *】   #@NULL_EXP  #@BREAK #@SUFFIX_OR_NO
#AST_STRUCTURE_EXPECTED: ExpReference(RefTypePointer(RefPrimitive))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(EXPRESSION)       #error(TYPE_AS_EXP_VALUE)【int * *】 #@NULL_EXP #@BREAK #@SUFFIX_OR_NO
#AST_STRUCTURE_EXPECTED: ExpReference(RefTypePointer(RefTypePointer(RefPrimitive)))

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test unambiguous refs being turned into expressions

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test unambiguous refs being turned into expressions


#@EXPS_WITH_CHILD_NEEDING_UPDATE《  * [foo] ● [foo]》
#@EXP_CAUSING_BREAK_TO_TOP《  [1 ..2] ● [3, $]● (3) ● ++ 》
#@EXP_CAUSING_BREAK_TO_TOP_X《    ^^ 3》
#@UNAMBIG_BEGIN《
  ►#error(TYPE_AS_EXP_VALUE)【int】●
  ►#error(TYPE_AS_EXP_VALUE)【int*[foo]】●
  ►#error(TYPE_AS_EXP_VALUE)【foo*[int]】●
  ►#error(TYPE_AS_EXP_VALUE)【foo*[foo*]】●
  ►#@UNAMBIG_NO_PENDING●
¤》
#@UNAMBIG_NO_PENDING《
  ►#?AST_STRUCTURE_EXPECTED!【#error(TYPE_AS_EXP_VALUE)【#@TYPEREFS_UNAMBIG__NO_PENDING】●#@TYPEREFS_UNAMBIG__NO_PENDING】》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(EXPRESSION_ToE)   #@ALT_PREFIX #@UNAMBIG_BEGIN /++/ #@EXP_CAUSING_BREAK_TO_TOP   #@EXPS_WITH_CHILD_NEEDING_UPDATE
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(EXPRESSION_ToE)   #@ALT_PREFIX #@UNAMBIG_BEGIN /++/ #@EXP_CAUSING_BREAK_TO_TOP_X #@EXPS_WITH_CHILD_NEEDING_UPDATE
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ // Next is a special case because of pending * operator 
#@UNAMBIG_WITH_PENDING!《
  ►#error(TYPE_AS_EXP_VALUE)【int*】●
  ►#error(TYPE_AS_EXP_VALUE)【foo*[int]*】●
¤》#PARSE(EXPRESSION_ToE) #@ALT_PREFIX #@UNAMBIG_WITH_PENDING/++/ #@BREAK #@EXP_CAUSING_BREAK_TO_TOP_X  #@EXPS_WITH_CHILD_NEEDING_UPDATE

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  extensive coverage from TYPEREFS_UNAMBIG is important
#PARSE(EXPRESSION_ToE)   #@ALT_PREFIX #@UNAMBIG_NO_PENDING
#AST_STRUCTURE_EXPECTED: #@ALT_PREFIX ExpReference(#@UNAMBIG_NO_PENDING) #@ALT_PREFIX_END

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test unambig to exp, with peding leftref Conversion

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(EXPRESSION_ToE)   #@ALT_PREFIX #@UNAMBIG_NO_PENDING * .init 
#AST_STRUCTURE_EXPECTED: #@ALT_PREFIX ExpInfix(ExpReference(#@UNAMBIG_NO_PENDING) ExpReference(RefModuleQualified(?))) #@ALT_PREFIX_END
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(EXPRESSION_ToE)   #@ALT_PREFIX #@UNAMBIG_NO_PENDING * *.init 
#AST_STRUCTURE_EXPECTED: #@ALT_PREFIX ExpInfix(ExpReference(#@UNAMBIG_NO_PENDING) ExpPrefix(ExpReference(RefModuleQualified(?)))) #@ALT_PREFIX_END 
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(EXPRESSION_ToE)   #@ALT_PREFIX #error(TYPE_AS_EXP_VALUE)【#@TYPEREFS_UNAMBIG__NO_PENDING * [foo]】 [2,3] 
#AST_STRUCTURE_EXPECTED: #@ALT_PREFIX ExpIndex( ExpReference(
  RefIndexing(RefTypePointer(#@UNAMBIG_NO_PENDING) RefIdentifier)) Integer Integer) #@ALT_PREFIX_END 

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Test ambiguous to exp
 #@IGNORE_BREAK《 #IGNORE_BREAK_TEST(test)》
 
#@AMBIG_TO_EXP《
  ►#?AST_STRUCTURE_EXPECTED!【#@TYPEREFS_AMBIG *foo●ExpInfix(* #?INFIX【ExpPrefix(*)●#@ExpIdentifier】)】●
  ►#?AST_STRUCTURE_EXPECTED!【#@TYPEREFS_AMBIG * #@TYPE_AS_EXP●ExpInfix(* #?INFIX【ExpPrefix(*)●ExpReference(RefPrimitive)】)】●
  
  ►#?AST_STRUCTURE_EXPECTED!【#@TYPEREFS_AMBIG [foo , 2] * []●ExpInfix(* ExpLiteralArray)】●
  ►#?AST_STRUCTURE_EXPECTED!【#@TYPEREFS_AMBIG [#@NO_EXP ,2] * #@NO_EXP●ExpInfix(* #@NO_EXP)】●
  ►#?AST_STRUCTURE_EXPECTED!【#@TYPEREFS_AMBIG *[foo: #@NO_EXP] * [foo]●ExpInfix(* ExpLiteralArray(#@ExpIdentifier))】●

  ►#?AST_STRUCTURE_EXPECTED!【bar[#@INT_AS_EXP , 123] ●ExpIndex(#@ExpIdentifier #@INT_AS_EXP Integer)】●
  ►#?AST_STRUCTURE_EXPECTED!【bar[#@INT_AS_EXP .. 546]●ExpSlice(#@ExpIdentifier #@INT_AS_EXP Integer)】●
  ►#?AST_STRUCTURE_EXPECTED!【bar*[#@INT_AS_EXP , 123]●ExpInfix(#@ExpIdentifier ExpLiteralArray(#@INT_AS_EXP Integer))】●
  ►#?AST_STRUCTURE_EXPECTED!【bar*[#@INT_AS_EXP : 123]●ExpInfix(#@ExpIdentifier ExpLiteralMapArray(
      MapArrayLiteralKeyValue( #@INT_AS_EXP Integer) ))】●
  
  ►#?AST_STRUCTURE_EXPECTED!【#@TYPEREFS_AMBIG #?INFIX【[7/*INFIX FIX*/]●】  (1, 2) * []●ExpInfix(* ExpLiteralArray)】●
  ►#?AST_STRUCTURE_EXPECTED!【#@TYPEREFS_AMBIG #?INFIX【[7/*INFIX FIX*/]●】  [foo .. 2] * []●ExpInfix(* ExpLiteralArray)】●
  
  ►#?AST_STRUCTURE_EXPECTED!【#@TYPEREFS_AMBIG [] ++ ● * 】●
  ►#?AST_STRUCTURE_EXPECTED!【#@TYPEREFS_AMBIG [] ^^ 123 ● * 】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test ROE to expression, decided in middle or end
#PARSE(EXPRESSION_ToE)    #@AMBIG_TO_EXP #@IGNORE_BREAK
#AST_STRUCTURE_EXPECTED:  #@AMBIG_TO_EXP

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test op category mismatch
#PARSE(EXPRESSION_ToE)    foo / * #@NO_EXP  #@BREAK
#AST_STRUCTURE_EXPECTED:  ExpInfix(ExpReference(?) ExpPrefix(#@NO_EXP) )
