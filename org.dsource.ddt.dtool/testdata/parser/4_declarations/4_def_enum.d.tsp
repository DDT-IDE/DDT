▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic case
enum i = 4;
enum MyEnum : basetype { A , B }
enum : int[] { A = 2 , float B }
#STRUCTURE_EXPECTED:
DefinitionEnumVar(DefinitionEnumVarFragment(DefSymbol Integer) )
DefinitionEnum(DefSymbol RefIdentifier EnumBody(EnumMember(DefSymbol) EnumMember(DefSymbol)) )
DeclarationEnum( RefTypeDynArray(*) EnumBody(
  EnumMember(DefSymbol Integer) 
  EnumMember(RefPrimitive DefSymbol)
))
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ definition enum 

#@TPL_ELEMENT_TESTER《
  ►#?STRUCTURE_EXPECTED!【 ● 】●
  ►#?STRUCTURE_EXPECTED!【(#@TPL_PARAMS__SAMPLE) ● #@TPL_PARAMS__SAMPLE】●
¤》


#@FRAG_PREFIX_NE《
  ►#?STRUCTURE_EXPECTED!【 fragDef (#@TPL_PARAMS__SAMPLE) /*No = */ #error(EXP_ASSIGN)【】 ,● DefEnumVarFragment(DefSymbol #@TPL_PARAMS__SAMPLE) 】●
  ►#?STRUCTURE_EXPECTED!【 fragDef #@TPL_ELEMENT_TESTER = #@EXP_ASSIGN__LITE ,● DefEnumVarFragment(DefSymbol #@TPL_ELEMENT_TESTER #@EXP_ASSIGN__LITE)】●
¤》
#@FRAG_PREFIX《
  ►#?STRUCTURE_EXPECTED!【/*No fragment*/● 】●
  ►#@FRAG_PREFIX_NE●
¤》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   enum #@FRAG_PREFIX_NE #@NO_DEFID #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  DefinitionEnumVar(#@FRAG_PREFIX_NE DefEnumVarFragment(#@NO_DEFID))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   enum fragDefT (#@TPL_PARAMS__SAMPLE #@NO_ClosePARENS #@NO_SEMICOLON #@BREAK
#STRUCTURE_EXPECTED:  DefinitionEnumVar(DefEnumVarFragment(DefSymbol #@TPL_PARAMS__SAMPLE))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   enum #@FRAG_PREFIX_NE fragDef2 #error(EXP_ASSIGN)【】 #@SEMICOLON_OR_BREAK 
#STRUCTURE_EXPECTED:  DefinitionEnumVar(#@FRAG_PREFIX_NE DefEnumVarFragment(DefSymbol))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   enum #@FRAG_PREFIX fragDef2 = #@INITIALIZERS__SAMPLE #@SEMICOLON_OR_BREAK 
#STRUCTURE_EXPECTED:  DefinitionEnumVar(#@FRAG_PREFIX DefEnumVarFragment(DefSymbol #@INITIALIZERS__SAMPLE))

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  :duplicate test case, here for clarity 
#PARSE(DECLARATION)   enum long x = 4;
#STRUCTURE_EXPECTED:  DeclarationAttrib(AttribBasic DefinitionVariable(RefPrimitive DefSymbol Integer) )
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#@ENUM_BODY__SAMPLE《
  ►#?STRUCTURE_EXPECTED!【{ A = 3, float B, XXX}●
  EnumBody(EnumMember(DefSymbol Integer) EnumMember(RefPrimitive DefSymbol) EnumMember(DefSymbol) )】●
¤》

#@BASE_TYPE__SAMPLE《
  ►#?STRUCTURE_EXPECTED!【/**/ #NoBaseType(flag)●】●
  ►#?STRUCTURE_EXPECTED!【 : #@TYPEREF_NB__LITE● #@TYPEREF_NB__LITE】●
¤》
#@BASE_TYPE《
  ►#@BASE_TYPE__SAMPLE●
  ►#?STRUCTURE_EXPECTED!【 : #@TYPEREF_NB● #@TYPEREF_NB】●
  ►#?STRUCTURE_EXPECTED!【 : #@NO_TYPEREF● #@NO_TYPEREF】●
  ►#?STRUCTURE_EXPECTED!【 : typeof( foo #@NO_ClosePARENS #@BREAK● RefTypeof(*) #@BREAK】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   enum #@BASE_TYPE #@ENUM_BODY__SAMPLE
#STRUCTURE_EXPECTED:  DeclarationEnum(#@BASE_TYPE #@ENUM_BODY__SAMPLE)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   enum #@DEFID #@BASE_TYPE #@ENUM_BODY__SAMPLE
#STRUCTURE_EXPECTED:  DefinitionEnum(#@DEFID #@BASE_TYPE #@ENUM_BODY__SAMPLE)
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 

#@ENUM_BODY《
  ►#?STRUCTURE_EXPECTED!【{ #@CLOSEBRACE_OR_BREAK ● EnumBody()】●
  ►#@ENUM_BODY__SAMPLE●
  ►#?STRUCTURE_EXPECTED!【 #?EnumDecl【 #?NoBaseType【#:DISCARD_CASE】 #error(EXPRULE_EnumBody) #@BREAK ●】 ; ● #?EnumDecl!【NoEnumBody】】●
  ►#?STRUCTURE_EXPECTED!【 #?NoBaseType【#:DISCARD_CASE】 #error(EXPRULE_EnumBody) #@BREAK    ● #@BREAK】●
  ►#?STRUCTURE_EXPECTED!【{ A, B , C #@CLOSEBRACE_OR_BREAK● EnumBody(EnumMember(?) EnumMember(?) EnumMember(?) )】●
  ►#?STRUCTURE_EXPECTED!【{ A = 3, B , #@CLOSEBRACE_OR_BREAK ● EnumBody(EnumMember(DefSymbol Integer) EnumMember(?) )】●
  ►#?STRUCTURE_EXPECTED!【{ A, B = #@EXP_ASSIGN ,}● EnumBody(EnumMember(DefSymbol) EnumMember(DefSymbol #@EXP_ASSIGN) )】●
  ►#?STRUCTURE_EXPECTED!【{ #@NO_DEFID, #@CLOSEBRACE_OR_BREAK ● EnumBody(EnumMember(#@NO_DEFID))】●
  ►#?STRUCTURE_EXPECTED!【{ #@TYPEREF_NBNE A = "asd", } ● EnumBody(EnumMember(#@TYPEREF_NBNE DefSymbol String) )】●
  ►#?STRUCTURE_EXPECTED!【{int[foo #error(EXP_CLOSE_BRACKET) #@NO_CloseBRACE #@BREAK A = "asd", B} ● EnumBody(EnumMember(RefIndexing(? ?) DefSymbol #@BREAK String) EnumMember(?) )】●
  ►#?STRUCTURE_EXPECTED!【{int[foo] #@NO_DEFID = "asd", } ● EnumBody(EnumMember(RefIndexing(? ?) #@NO_DEFID String) )】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   enum #@BASE_TYPE__SAMPLE #EnumDecl(flag) #@ENUM_BODY
#STRUCTURE_EXPECTED:  DeclarationEnum(#@BASE_TYPE__SAMPLE #@ENUM_BODY)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   enum #@DEFID #@BASE_TYPE__SAMPLE #@ENUM_BODY
#STRUCTURE_EXPECTED:  DefinitionEnum(#@DEFID #@BASE_TYPE__SAMPLE #@ENUM_BODY)


