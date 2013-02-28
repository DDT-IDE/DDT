Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) 1 , "abc"
#AST_STRUCTURE_EXPECTED:
ExpInfix(ExpLiteralInteger ExpLiteralString)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) 1 , "abc", 123
#AST_STRUCTURE_EXPECTED:
ExpInfix(ExpLiteralInteger ExpInfix(ExpLiteralString ExpLiteralInteger))


Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@INT_OR_MISSING《
  ►#?AST_STRUCTURE_EXPECTED!【123●Integer】●
  ►#?AST_STRUCTURE_EXPECTED!【#error(EXPRULE_exp)●】●
¤》

#@ASSIGN《=●+=●-=●*=●/=●%=●&=●|=●^=●~=●<<=●>>=●>>>=●^^=》
#@COND《 ? #@《"middleExp"》 :》
#@CMP《==●!=●is●!is●in●!in●<●<=●>●>=●<>●<>=●!<>●!<>=●!<●!<=●!>●!>=》
#@SHIFT《<<●>>●>>>》
#@ADD《+●-●~》
#@MUL《*●/●%》

#@OP《
  ►#@ASSIGN  #RIGHT_ASSOC(flag)●
  ►#@COND  #RIGHT_ASSOC(flag)#COND(flag)●
  ►||●
  ►&&●
  ►|●
  ►^●
  ►&●
  ►#@SHIFT●
  ►#@ADD●
  ►#@MUL●
¤》

#@OP_HI《
  ►,●
  ►#@ASSIGN●
  ►#@COND#COND_HI(flag)●
  ►||●
  ►&&●
  ►|●
  ►^●
  ►#@CMP●
  ►#@SHIFT●
  ►#@ADD●
¤》(OP)

#@INFIX_EXP{#?COND《ExpConditional●ExpInfix》}
#@INFIX_EXP_HI{#?COND_HI《ExpConditional●ExpInfix》}

#@MIDDLE_EXP{#?COND{String}}
#@MIDDLE_EXP_HI{#?COND_HI{String}}

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)	 this    #@OP    "abc"  #@OP  #@INT_OR_MISSING
#AST_STRUCTURE_EXPECTED:
#@INFIX_EXP #?RIGHT_ASSOC《
	(ExpThis  #@MIDDLE_EXP  #@INFIX_EXP•(String #@MIDDLE_EXP #@INT_OR_MISSING))●
	(#@INFIX_EXP•(ExpThis #@MIDDLE_EXP String)  #@MIDDLE_EXP  #@INT_OR_MISSING)》
	
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)	 this   #@OP /*OP*/  "abc"  #@OP_HI(OP)   #@INT_OR_MISSING
#AST_STRUCTURE_EXPECTED:
#@INFIX_EXP_HI•(#@INFIX_EXP•(ExpThis #@MIDDLE_EXP String)  #@MIDDLE_EXP_HI  #@INT_OR_MISSING)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) 	 this #@OP_HI(OP) #@EXP_UNARY__NO_PENDING  #@OP  /*OP*/  123
#AST_STRUCTURE_EXPECTED:
#@INFIX_EXP_HI•(ExpThis  #@MIDDLE_EXP_HI  #@INFIX_EXP•(#@EXP_UNARY__NO_PENDING #@MIDDLE_EXP ExpLiteralInteger))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Special case for '==' and other compare ops
#PARSE(EXPRESSION)	 #error(REQPARENS)《this  #@CMP   "abc"》    #@CMP  #@INT_OR_MISSING
#AST_STRUCTURE_EXPECTED:
ExpInfix(ExpInfix(ExpThis  String)  #@INT_OR_MISSING)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)	 #error(REQPARENS_&)《this  #@CMP /*OP*/ "abc"》/*blah*/    &   #@INT_OR_MISSING
#AST_STRUCTURE_EXPECTED:
ExpInfix(ExpInfix(ExpThis String)  #@INT_OR_MISSING)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) 	 this    &   #error(REQPARENS_&)《"abc"  #@CMP /*OP*/  123》
#AST_STRUCTURE_EXPECTED:
ExpInfix(ExpThis  ExpInfix(String Integer))

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test error with == next to |●^●&
#PARSE(EXPRESSION) 
#@〔1 &●〕  #error(REQPARENS)《1 == 2》 #@〔|●^●&〕  #error(REQPARENS)《2 <= 3》#@〔|●^●&〕   this

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(EXPRESSION) 
         #error(REQPARENS_^)《1 == 2》 ^  #error(REQPARENS_&)《2 <= 3》&   this
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(EXPRESSION) 
         #error(REQPARENS_|)《1 == 2》 |  #error(REQPARENS_&)《2 <= 3》  &   this

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test order of errors
#PARSE(EXPRESSION) 
#error(REQPARENS)《1 == 123》 | #@《 #error:ITC{} 123 ● #error(EXPRULE_exp)》  

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) 	this && "abc" == 123  #@《||●&&●,》  super
#AST_STRUCTURE_EXPECTED:
ExpInfix(
  ExpInfix(ExpThis ExpInfix(String Integer)) 
  ExpSuper
)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) 	this && 1 + 2 == 5*5  ||  super
#AST_STRUCTURE_EXPECTED:
ExpInfix(
  ExpInfix(ExpThis ExpInfix(* *)) 
  ExpSuper
)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) 	this && 1 ==  #error(EXPRULE_exp)  ||  super
#AST_STRUCTURE_EXPECTED:
ExpInfix(
  ExpInfix(ExpThis ExpInfix(Integer)) 
  ExpSuper
)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) 	this || "abc" == 123  &&  super
#AST_STRUCTURE_EXPECTED:
ExpInfix(
  ExpThis
  ExpInfix(ExpInfix(String Integer) ExpSuper) 
)

