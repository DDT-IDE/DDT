Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 


#@SP_EXP《►
	#@LIT_KEYWORDS《this●super●null●true●false●$●__FILE__●__LINE__》●
	'"'●
	12●123_45Lu●
	123.0F●.456E12●0x25_AD_3FP+1●
	
	"abc"●r"inline"q{ TOKEN string }`sfds`●
》

#@LIT_KEYWORDS_EXP《
ExpThis●ExpSuper●ExpNull●ExpLiteralBool●ExpLiteralBool●ExpArrayLength●ExpLiteralString●ExpLiteralInteger》

#@SPSE_EXP!《►
	#@LIT_KEYWORDS_EXP(LIT_KEYWORDS)●
	ExpLiteralChar●
	ExpLiteralInteger●ExpLiteralInteger●
	ExpLiteralFloat●ExpLiteralFloat●ExpLiteralFloat●
	ExpLiteralString●ExpLiteralString●
	
》

---TODO

	foo●
	
	RefIdentifier●

#@EXP_OROR《►
#?AST_STRUCTURE_EXPECTED!【this●ExpThis】●
#?AST_STRUCTURE_EXPECTED!【super●ExpSuper】●
#?AST_STRUCTURE_EXPECTED!【null●ExpNull】●
#?AST_STRUCTURE_EXPECTED!【true●ExpLiteralBool】●
#?AST_STRUCTURE_EXPECTED!【false●ExpLiteralBool】●
#?AST_STRUCTURE_EXPECTED!【$●ExpArrayLength】●
#?AST_STRUCTURE_EXPECTED!【'"'●ExpLiteralChar】●
#?AST_STRUCTURE_EXPECTED!【#@X《12●123_45Lu》●ExpLiteralInteger】●
#?AST_STRUCTURE_EXPECTED!【#@X《123.0F●.456E12●0x25_AD_3FP+1》●ExpLiteralFloat】●
#?AST_STRUCTURE_EXPECTED!【#@X《"abc"●r"inline"q{ TOKEN string }`sfds`》●ExpLiteralString】●

#?AST_STRUCTURE_EXPECTED!【__FILE__●ExpLiteralString】●
#?AST_STRUCTURE_EXPECTED!【__LINE__●ExpLiteralInteger】●

》

#@EXP_《►
#@EXP_NEW●
》

#@EXP_CONDITIONAL《►
#@EXP_OROR●
#?AST_STRUCTURE_EXPECTED!【false ? 123 : 456●ExpConditional(Bool Integer Integer)】●
》

#@EXP_ASSIGN《►
#@EXP_CONDITIONAL●
#?AST_STRUCTURE_EXPECTED!【this = super += null●InfixExpression(ExpThis InfixExpression(ExpSuper ExpNull))】●
》

#@EXP_COMMA《►
#@EXP_ASSIGN●
#?AST_STRUCTURE_EXPECTED!【12,"asd"●InfixExpression(Integer String)】●
》

#@EXP_ANY《►
#@EXP_COMMA●
》


