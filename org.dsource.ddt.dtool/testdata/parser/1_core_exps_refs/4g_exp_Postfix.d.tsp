▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) 123++
#STRUCTURE_EXPECTED:
ExpPostfix(Integer)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) 123--
#STRUCTURE_EXPECTED:
ExpPostfix(Integer)

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@INDEXEE《
  ►#?STRUCTURE_EXPECTED!【foo ● #@ExpIdentifier】● 
  ►#?STRUCTURE_EXPECTED!【foo++ ● ExpPostfix(#@ExpIdentifier)】 ●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    #@INDEXEE --
#STRUCTURE_EXPECTED:  ExpPostfix( #@INDEXEE )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    +foo++
#STRUCTURE_EXPECTED:  ExpPrefix( ExpPostfix( #@ExpIdentifier ) )