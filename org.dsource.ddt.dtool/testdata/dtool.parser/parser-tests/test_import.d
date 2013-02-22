▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
import foo;

#AST_STRUCTURE_EXPECTED:
DeclarationImport(ImportContent(RefModule))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
import pack.foo;
import pack.bar.foo;
static import pack.bar.foo;
import foo, pack.foo, pack.bar.foo;
#AST_STRUCTURE_EXPECTED:
DeclarationImport(ImportContent(RefModule))
DeclarationImport(ImportContent(RefModule))
DeclarationImport(ImportContent(RefModule))
DeclarationImport(ImportContent(RefModule) ImportContent(RefModule) ImportContent(RefModule))

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
import#error(EXP_ID)#error(EXP_SEMICOLON)#AST_SOURCE_EXPECTED:
import ;
#AST_STRUCTURE_EXPECTED:
DeclarationImport(ImportContent(RefModule))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
import #error(EXP_ID)#error(EXP_SEMICOLON) import foo;
#AST_SOURCE_EXPECTED:
import ; import foo;

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
import bar1 = foo, bar2 = pack.foo;

#AST_STRUCTURE_EXPECTED:
DeclarationImport(ImportAlias(DefSymbol RefModule) ImportAlias(DefSymbol RefModule))
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Import Content and Import Alias
#@SEMICOLON_OR_NO《#error(EXP_SEMICOLON)●;》

#@ID_OR_NO《
  ►#?AST_STRUCTURE_EXPECTED!【foo●ImportContent(RefModule)】●
  ►#?AST_STRUCTURE_EXPECTED!【#error(EXP_ID)●ImportContent(RefModule)】●
  ►#?AST_STRUCTURE_EXPECTED!【pack.foo●ImportContent(RefModule)】●
  ►#?AST_STRUCTURE_EXPECTED!【pack.#error(EXP_ID)●ImportContent(RefModule)】●
  ►#?AST_STRUCTURE_EXPECTED!【pack.foo.bar●ImportContent(RefModule)】●
  ►#?AST_STRUCTURE_EXPECTED!【pack.foo.bar.#error(EXP_ID)●ImportContent(RefModule)】●

  ►#?AST_STRUCTURE_EXPECTED!【aldef = foo●ImportAlias(DefSymbol RefModule)】●
  ►#?AST_STRUCTURE_EXPECTED!【#error(EXP_ID) = foo●ImportAlias(DefSymbol RefModule)】●
  ►#?AST_STRUCTURE_EXPECTED!【aldef = #error(EXP_ID)●ImportAlias(DefSymbol RefModule)】●
  ►#?AST_STRUCTURE_EXPECTED!【aldef = pack.foo●ImportAlias(DefSymbol RefModule)】●
  ►#?AST_STRUCTURE_EXPECTED!【aldef = pack.#error(EXP_ID)●ImportAlias(DefSymbol RefModule)】●

¤》

#@EXTRA_ARG_OR_NO《
  ►#?AST_STRUCTURE_EXPECTED!【/*NoStartArg*/ ● 】●
  ►#?AST_STRUCTURE_EXPECTED!【dai ,●ImportContent(RefModule)】●
  ►#?AST_STRUCTURE_EXPECTED!【#error(EXP_ID) ,●ImportContent(RefModule)】●
  ►#?AST_STRUCTURE_EXPECTED!【pack.dai ,●ImportContent(RefModule)】●

  ►#?AST_STRUCTURE_EXPECTED!【aldef = pack.dai ,●ImportAlias(DefSymbol RefModule)】●
¤》

#@IMPORT_END《
  ►#?AST_STRUCTURE_EXPECTED!【;● 】●
  ►#?AST_STRUCTURE_EXPECTED!【#error(EXP_SEMICOLON)● 】●
  ►#?AST_STRUCTURE_EXPECTED!【, #@^ID_OR_NO(END_ID) ;● #@^ID_OR_NO(END_ID)】●
¤》

#@END_IGNORE!《#error(EXP_SEMICOLON) #parser(IgnoreRest)》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
import #@EXTRA_ARG_OR_NO #@ID_OR_NO #@IMPORT_END  
#AST_STRUCTURE_EXPECTED:
DeclarationImport(#@EXTRA_ARG_OR_NO #@ID_OR_NO #@IMPORT_END)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DeclarationImport)  import #@EXTRA_ARG_OR_NO #@END_REP_TEST《
  ►#?AST_STRUCTURE_EXPECTED!【foo #@END_IGNORE dummy;● ImportContent(RefModule)】●
  ►#?AST_STRUCTURE_EXPECTED!【ali = foo #@END_IGNORE = dummy;●ImportAlias(DefSymbol RefModule)】●
¤》
#AST_STRUCTURE_EXPECTED:
DeclarationImport(#@EXTRA_ARG_OR_NO #@END_REP_TEST )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
/* ----------------  Import Content and Import Alias  ---------------- */
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  

static import ali = foo : elem1, ren = elem2, elem3;

#AST_STRUCTURE_EXPECTED:
DeclarationImport(ImportSelective(ImportAlias(DefSymbol RefModule) 
  RefImportSelection ImportSelectiveAlias(DefSymbol RefImportSelection) RefImportSelection
))

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Import Content and Import Alias



#@SELID_OR_NO《
  ►#?AST_STRUCTURE_EXPECTED!【sel●RefImportSelection】●
  ►#?AST_STRUCTURE_EXPECTED!【#error(EXP_ID)●RefImportSelection】●

  ►#?AST_STRUCTURE_EXPECTED!【aldef = sel●ImportSelectiveAlias(DefSymbol RefImportSelection)】●
  ►#?AST_STRUCTURE_EXPECTED!【#error(EXP_ID) = sel●ImportSelectiveAlias(DefSymbol RefImportSelection)】●
  ►#?AST_STRUCTURE_EXPECTED!【aldef = #error(EXP_ID)●ImportSelectiveAlias(DefSymbol RefImportSelection)】●
¤》

#@SELID_EXTRA_OR_NO《
  ►#?AST_STRUCTURE_EXPECTED!【/*NoExtra*/ ● 】●
  ►#?AST_STRUCTURE_EXPECTED!【dai ,● RefImportSelection】●
  ►#?AST_STRUCTURE_EXPECTED!【#error(EXP_ID) ,●RefImportSelection】●

  ►#?AST_STRUCTURE_EXPECTED!【aldef = mod ,●ImportSelectiveAlias(DefSymbol RefImportSelection)】●
¤》

#@SELIMPORT_END《
  ►#?AST_STRUCTURE_EXPECTED!【;● 】●
  ►#?AST_STRUCTURE_EXPECTED!【#error(EXP_SEMICOLON)● 】●
  ►#?AST_STRUCTURE_EXPECTED!【, #@^SELID_OR_NO(END_ID) ; ● #@^SELID_OR_NO(END_ID)】●
¤》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
import #@ID_OR_NO : #@SELID_EXTRA_OR_NO #@SELID_OR_NO #@SELIMPORT_END
#AST_STRUCTURE_EXPECTED:
DeclarationImport(ImportSelective( #@ID_OR_NO #@SELID_EXTRA_OR_NO #@SELID_OR_NO #@SELIMPORT_END ))

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DeclarationImport)  import foo : #@SELID_EXTRA_OR_NO #@END_REP_TEST《
  ►#?AST_STRUCTURE_EXPECTED!【foo       #@END_IGNORE dummy;  ● RefImportSelection】●
  ►#?AST_STRUCTURE_EXPECTED!【foo       #@END_IGNORE : dummy;● RefImportSelection】●
  ►#?AST_STRUCTURE_EXPECTED!【ali = foo #@END_IGNORE = dummy;● ImportSelectiveAlias(DefSymbol RefImportSelection)】●
¤》
#AST_STRUCTURE_EXPECTED:
DeclarationImport(ImportSelective(ImportContent(RefModule) #@SELID_EXTRA_OR_NO #@END_REP_TEST ))
