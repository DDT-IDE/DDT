▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
import foo;

#STRUCTURE_EXPECTED:
DeclarationImport(ImportContent(RefModule))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
import pack.foo;
import pack.bar.foo;
static import pack.bar.foo;
public static import foo, pack.foo, pack.bar.foo;
#STRUCTURE_EXPECTED:
DeclarationImport(ImportContent(RefModule))
DeclarationImport(ImportContent(RefModule))
DeclarationImport(ImportContent(RefModule))
DeclarationAttrib(AttribProtection 
  DeclarationImport(ImportContent(RefModule) ImportContent(RefModule) ImportContent(RefModule)) 
)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
import#error(EXP_ID)#error(EXP_SEMICOLON)#comment(EOF):

#STRUCTURE_EXPECTED:
DeclarationImport(ImportContent(RefModule))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
import #error(EXP_ID)#error(EXP_SEMICOLON) import foo;

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
import bar1 = foo, bar2 = pack.foo;

#STRUCTURE_EXPECTED:
DeclarationImport(ImportAlias(DefSymbol RefModule) ImportAlias(DefSymbol RefModule))
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Import Content and Import Alias

#@ID_OR_NO《
  ►#?STRUCTURE_EXPECTED!【foo●ImportContent(RefModule)】●
  ►#?STRUCTURE_EXPECTED!【#error(EXP_ID)●ImportContent(RefModule)】●
  ►#?STRUCTURE_EXPECTED!【pack.foo●ImportContent(RefModule)】●
  ►#?STRUCTURE_EXPECTED!【pack.#error(EXP_ID)●ImportContent(RefModule)】●
  ►#?STRUCTURE_EXPECTED!【pack.foo.bar●ImportContent(RefModule)】●
  ►#?STRUCTURE_EXPECTED!【pack.foo.bar.#error(EXP_ID)●ImportContent(RefModule)】●

  ►#?STRUCTURE_EXPECTED!【aldef = foo●ImportAlias(DefSymbol RefModule)】●
  ►#?STRUCTURE_EXPECTED!【#error(EXP_ID) = foo●ImportAlias(DefSymbol RefModule)】●
  ►#?STRUCTURE_EXPECTED!【aldef = #error(EXP_ID)●ImportAlias(DefSymbol RefModule)】●
  ►#?STRUCTURE_EXPECTED!【aldef = pack.foo●ImportAlias(DefSymbol RefModule)】●
  ►#?STRUCTURE_EXPECTED!【aldef = pack.#error(EXP_ID)●ImportAlias(DefSymbol RefModule)】●
¤》
#@ID_OR_NO__LITE《
  ►#?STRUCTURE_EXPECTED!【foo●ImportContent(RefModule)】●
  ►#?STRUCTURE_EXPECTED!【aldef = foo●ImportAlias(DefSymbol RefModule)】●
¤》

#@EXTRA_ARG_OR_NO《
  ►#?STRUCTURE_EXPECTED!【/*NoStartArg*/ ● 】●
  ►#?STRUCTURE_EXPECTED!【dai ,●ImportContent(RefModule)】●
  ►#?STRUCTURE_EXPECTED!【#error(EXP_ID) ,●ImportContent(RefModule)】●
  ►#?STRUCTURE_EXPECTED!【pack.dai ,●ImportContent(RefModule)】●

  ►#?STRUCTURE_EXPECTED!【aldef = pack.dai ,●ImportAlias(DefSymbol RefModule)】●
¤》

#@IMPORT_END《
  ►#@SEMICOLON_OR_BREAK●
  ►#?STRUCTURE_EXPECTED!【, #@ID_OR_NO ;● #@ID_OR_NO】●
¤》

#@END_IGNORE!《#error(EXP_SEMICOLON) #@BREAK》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   import #@EXTRA_ARG_OR_NO #@ID_OR_NO #@IMPORT_END  
#STRUCTURE_EXPECTED:
DeclarationImport(#@EXTRA_ARG_OR_NO #@ID_OR_NO #@IMPORT_END)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   import #@EXTRA_ARG_OR_NO #@END_REP_TEST《
  ►#?STRUCTURE_EXPECTED!【foo #@END_IGNORE dummy;● ImportContent(RefModule)】●
  ►#?STRUCTURE_EXPECTED!【ali = foo #@END_IGNORE = dummy;●ImportAlias(DefSymbol RefModule)】●
¤》
#STRUCTURE_EXPECTED:
DeclarationImport(#@EXTRA_ARG_OR_NO #@END_REP_TEST )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
/* ----------------  Import Content and Import Alias  ---------------- */
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  

static import ali = foo : elem1, ren = elem2, elem3;

#STRUCTURE_EXPECTED:
DeclarationImport(ImportSelective(ImportAlias(DefSymbol RefModule) 
  RefImportSelection ImportSelectiveAlias(DefSymbol RefImportSelection) RefImportSelection
))

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Import Content and Import Alias



#@SELID_OR_NO《
  ►#?STRUCTURE_EXPECTED!【sel●RefImportSelection】●
  ►#?STRUCTURE_EXPECTED!【#error(EXP_ID)●RefImportSelection】●

  ►#?STRUCTURE_EXPECTED!【aldef = sel●ImportSelectiveAlias(DefSymbol RefImportSelection)】●
  ►#?STRUCTURE_EXPECTED!【#error(EXP_ID) = sel●ImportSelectiveAlias(DefSymbol RefImportSelection)】●
  ►#?STRUCTURE_EXPECTED!【aldef = #error(EXP_ID)●ImportSelectiveAlias(DefSymbol RefImportSelection)】●
¤》

#@SELID_EXTRA_OR_NO《
  ►#?STRUCTURE_EXPECTED!【/*NoExtra*/ ● 】●
  ►#?STRUCTURE_EXPECTED!【dai ,● RefImportSelection】●
  ►#?STRUCTURE_EXPECTED!【#error(EXP_ID) ,●RefImportSelection】●

  ►#?STRUCTURE_EXPECTED!【aldef = mod ,●ImportSelectiveAlias(DefSymbol RefImportSelection)】●
¤》

#@SELIMPORT_END《
  ►#@SELIMPORT_END__LITE●
  ►#?STRUCTURE_EXPECTED!【, #@SELID_OR_NO ; ● #@SELID_OR_NO】●
¤》
#@SELIMPORT_END__LITE《
  ►#?STRUCTURE_EXPECTED!【;● 】●
  ►#?STRUCTURE_EXPECTED!【#error(EXP_SEMICOLON)● 】●
¤》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
import #@ID_OR_NO : #@SELID_EXTRA_OR_NO #@SELID_OR_NO #@SELIMPORT_END
#STRUCTURE_EXPECTED:
DeclarationImport(ImportSelective( #@ID_OR_NO #@SELID_EXTRA_OR_NO #@SELID_OR_NO #@SELIMPORT_END ))

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   import foo : #@SELID_EXTRA_OR_NO #@END_REP_TEST《
  ►#?STRUCTURE_EXPECTED!【foo       #@END_IGNORE dummy;  ● RefImportSelection】●
  ►#?STRUCTURE_EXPECTED!【foo       #@END_IGNORE : dummy;● RefImportSelection】●
  ►#?STRUCTURE_EXPECTED!【ali = foo #@END_IGNORE = dummy;● ImportSelectiveAlias(DefSymbol RefImportSelection)】●
¤》
#STRUCTURE_EXPECTED:
DeclarationImport(ImportSelective(ImportContent(RefModule) #@SELID_EXTRA_OR_NO #@END_REP_TEST ))
