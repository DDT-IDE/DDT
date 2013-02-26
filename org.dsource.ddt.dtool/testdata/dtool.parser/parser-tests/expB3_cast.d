▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) cast(Foo) foo
#AST_STRUCTURE_EXPECTED:
ExpCast(RefIdentifier #@ExpIdentifier)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) cast() foo
#AST_STRUCTURE_EXPECTED:
ExpCast( #@MISSING_REF #@ExpIdentifier )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) cast(const shared) foo
#AST_STRUCTURE_EXPECTED:
ExpCastQual( #@ExpIdentifier )

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@UNEXP_OR_NO《#@EXP_UNARY●#@NO_EXP》

#@CAST_QUAL《const●inout●immutable●shared●const shared●shared const●inout shared●shared inout》
#@CAST_START《
  ►#?AST_STRUCTURE_EXPECTED!【cast( #@TYPE_REFS )● ExpCast( #@TYPE_REFS 】● 
  ►#?AST_STRUCTURE_EXPECTED!【cast( )●             ExpCast( #@MISSING_REF 】● 
  ►#?AST_STRUCTURE_EXPECTED!【cast( #@CAST_QUAL )● ExpCastQual(】●
¤》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)       #@CAST_START  #@UNEXP_OR_NO
#AST_STRUCTURE_EXPECTED: #@CAST_START  #@UNEXP_OR_NO )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)       cast   ( #@TYPE_REFS #error(EXP_CLOSE_PARENS) #parser(IgnoreRest) foo  
#AST_STRUCTURE_EXPECTED: ExpCast( #@TYPE_REFS )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)       cast   (  #error(EXP_CLOSE_PARENS) / 7  
#AST_STRUCTURE_EXPECTED: ExpInfix(ExpCast( #@MISSING_REF ) Integer) 
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)       cast   #error(EXP_OPEN_PARENS) / 7
#AST_STRUCTURE_EXPECTED: ExpInfix(ExpCast( )               Integer) 
#AST_SOURCE_EXPECTED:    cast / 7
