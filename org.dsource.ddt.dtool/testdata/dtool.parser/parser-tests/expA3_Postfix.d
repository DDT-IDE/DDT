▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) 123++
#AST_STRUCTURE_EXPECTED:
ExpPostfix(Integer)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) 123--
#AST_STRUCTURE_EXPECTED:
ExpPostfix(Integer)

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@INDEXEE《
  ►#?AST_STRUCTURE_EXPECTED!【foo ● #@ExpIdentifier】● 
  ►#?AST_STRUCTURE_EXPECTED!【foo++ ● ExpPostfix(#@ExpIdentifier)】 ●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) #@INDEXEE --
#AST_STRUCTURE_EXPECTED:
ExpPostfix( #@INDEXEE )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) +foo++
#AST_STRUCTURE_EXPECTED:
ExpPrefix( ExpPostfix( #@ExpIdentifier ) )