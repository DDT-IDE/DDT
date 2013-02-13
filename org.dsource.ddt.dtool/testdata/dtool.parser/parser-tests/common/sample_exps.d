Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 

---TODO

	foo●
	
	RefIdentifier●

#@EXP_UNARY《
  ►#?AST_STRUCTURE_EXPECTED!【this●ExpThis】●
  ►#?AST_STRUCTURE_EXPECTED!【super●ExpSuper】●
  ►#?AST_STRUCTURE_EXPECTED!【null●ExpNull】●
  ►#?AST_STRUCTURE_EXPECTED!【true●ExpLiteralBool】●
  ►#?AST_STRUCTURE_EXPECTED!【false●ExpLiteralBool】●
  ►#?AST_STRUCTURE_EXPECTED!【$●ExpArrayLength】●
  ►#?AST_STRUCTURE_EXPECTED!【'"'●ExpLiteralChar】●
  ►#?AST_STRUCTURE_EXPECTED!【#@X《12●123_45Lu》●ExpLiteralInteger】●
  ►#?AST_STRUCTURE_EXPECTED!【#@X《123.0F●.456E12●0x25_AD_3FP+1》●ExpLiteralFloat】●
  ►#?AST_STRUCTURE_EXPECTED!【#@X《"abc"●r"inline"q{ TOKEN string }`sfds`》●ExpLiteralString】●
  
  ►#?AST_STRUCTURE_EXPECTED!【__FILE__●ExpLiteralString】●
  ►#?AST_STRUCTURE_EXPECTED!【__LINE__●ExpLiteralInteger】●
  
¤》

#@EXP_UNARY_REFS《
  ►#@EXP_UNARY●
  ►#?AST_STRUCTURE_EXPECTED!【foo●ExpReference(RefIdentifier)】●
  
  ►#?AST_STRUCTURE_EXPECTED!【#error(TYPE_AS_EXP_VALUE)《int》●ExpReference(RefPrimitive)】●
  ►#?AST_STRUCTURE_EXPECTED!【#error(TYPE_AS_EXP_VALUE)《int[]》●ExpReference(RefTypeDynArray(RefPrimitive))】●
  ►#?AST_STRUCTURE_EXPECTED!【#error(TYPE_AS_EXP_VALUE)《int[foo]》●ExpReference(RefIndexing(RefPrimitive ?))】●
¤》
  
#@EXP_UNARY_WITHREFS《
  ►#@EXP_UNARY●
  ►#@EXP_UNARY_REFS●
¤》

#@EXP_OROR《
  ►#@EXP_UNARY●
  ►#?AST_STRUCTURE_EXPECTED!【4 / 6●InfixExpression(? ?)】●
  ►#?AST_STRUCTURE_EXPECTED!【1 + 2●InfixExpression(? ?)】●
  ►#?AST_STRUCTURE_EXPECTED!【1 << 16●InfixExpression(? ?)】●
  ►#?AST_STRUCTURE_EXPECTED!【0xFF & 123●InfixExpression(? ?)】●
  ►#?AST_STRUCTURE_EXPECTED!【0xFF | 0xAA●InfixExpression(? ?)】●
  ►#?AST_STRUCTURE_EXPECTED!【1 > "2" && 3●InfixExpression(InfixExpression(? String) ?)】●
  ►#?AST_STRUCTURE_EXPECTED!【2 || "3" < 4●InfixExpression(? InfixExpression(String ?))】●
¤》

#@EXP_CONDITIONAL《
  ►#@EXP_OROR●
  ►#?AST_STRUCTURE_EXPECTED!【false ? 123 : 456●ExpConditional(Bool Integer Integer)】●
¤》

#@EXP_ASSIGN《
  ►#@EXP_CONDITIONAL●
  ►#?AST_STRUCTURE_EXPECTED!【this = super += null●InfixExpression(ExpThis InfixExpression(ExpSuper ExpNull))】●
¤》

#@EXP_COMMA《
  ►#@EXP_ASSIGN●
  ►#?AST_STRUCTURE_EXPECTED!【12,"asd"●InfixExpression(Integer String)】●
¤》

#@EXP_ANY《#@EXP_COMMA》
