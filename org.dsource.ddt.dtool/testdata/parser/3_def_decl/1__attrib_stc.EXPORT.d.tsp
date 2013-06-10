Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂


#@STC_NOTM《
 ►#?STRUCTURE_EXPECTED!【abstract●DeclarationBasicAttrib( 】●
 ►#?STRUCTURE_EXPECTED!【auto●DeclarationBasicAttrib( 】●
 
 ►#?STRUCTURE_EXPECTED!【deprecated●DeclarationBasicAttrib( 】●
 
 ►#?STRUCTURE_EXPECTED!【extern●DeclarationBasicAttrib( 】●
 ►#?STRUCTURE_EXPECTED!【final●DeclarationBasicAttrib( 】●
 ►#?STRUCTURE_EXPECTED!【nothrow●DeclarationBasicAttrib( 】●
 ►#?STRUCTURE_EXPECTED!【override●DeclarationBasicAttrib( 】●
 ►#?STRUCTURE_EXPECTED!【pure●DeclarationBasicAttrib( 】●
 ►#?STRUCTURE_EXPECTED!【__gshared●DeclarationBasicAttrib( 】●
 ►#?STRUCTURE_EXPECTED!【scope●DeclarationBasicAttrib( 】●
 ►#?STRUCTURE_EXPECTED!【static●DeclarationBasicAttrib( 】●
 ►#?STRUCTURE_EXPECTED!【synchronized●DeclarationBasicAttrib( 】●
 
 ►#?STRUCTURE_EXPECTED!【ref●DeclarationBasicAttrib( 】●
¤》
// ref attrib is not documented in spec as an attrib as of 2.062

#@PROT_ATTRIB【#?STRUCTURE_EXPECTED!【#@《private●package●protected●public●export》● DeclarationProtection( 】】
#@AT_ATTRIB【#?STRUCTURE_EXPECTED!【#@《@property●@safe●@trusted●@  system●@disable●@ #error(INVALID_ATTRIB_ID){xxx}》●
  DeclarationAtAttrib(Symbol 】】
  
#@TYPEMOD_ATTRIB《
 ►#?STRUCTURE_EXPECTED!【const●DeclarationBasicAttrib( 】●
 ►#?STRUCTURE_EXPECTED!【immutable●DeclarationBasicAttrib( 】●
 ►#?STRUCTURE_EXPECTED!【inout●DeclarationBasicAttrib( 】●
 ►#?STRUCTURE_EXPECTED!【shared●DeclarationBasicAttrib( 】●
¤》

#@STC《
 ►#@STC_NOTM●
 ►#@TYPEMOD_ATTRIB●
¤》

#@SIMPLE_ATTRIBS_NOTM《
 ►#@STC_NOTM●
 ►#@PROT_ATTRIB●
 ►#@AT_ATTRIB●
¤》
#@SIMPLE_ATTRIBS《
 ►#@SIMPLE_ATTRIBS_NOTM●
 ►#@TYPEMOD_ATTRIB●
¤》


#@SIMPLE_ATTRIBS__SAMPLE《
 ►#?STRUCTURE_EXPECTED!【package●DeclarationProtection( 】●
 ►#?STRUCTURE_EXPECTED!【static●DeclarationBasicAttrib( 】●
 ►#?STRUCTURE_EXPECTED!【@safe●DeclarationAtAttrib(Symbol 】●
¤》

#@SIMPLE_ATTRIBS_ENUM《
 ►#?STRUCTURE_EXPECTED!【enum●DeclarationBasicAttrib( 】●
¤》



#@LINKAGE_TYPE{C,C++,D,Windows,Pascal,System}
#@EXTERN_ATTRIB《
 ►#?STRUCTURE_EXPECTED!【extern(#@LINKAGE_TYPE)● DeclarationLinkage( 】●
 ►#?STRUCTURE_EXPECTED!【extern (Xpto #error(BAD_LINKAGE_ID) )● DeclarationLinkage( 】●
 ►#?STRUCTURE_EXPECTED!【extern(#error(BAD_LINKAGE_ID))● DeclarationLinkage( 】●
¤》

#@ALIGN_ATTRIB《
 ►#?STRUCTURE_EXPECTED!【align● DeclarationAlign( 】●
 ►#?STRUCTURE_EXPECTED!【align(1)● DeclarationAlign( 】●
 ►#?STRUCTURE_EXPECTED!【align(12)● DeclarationAlign( 】●
 ►#?STRUCTURE_EXPECTED!【align(#error(EXP_INTEGER_DECIMAL)) ● DeclarationAlign( 】●
¤》

#@PRAGMA_ATTRIB《
 ►#?STRUCTURE_EXPECTED!【pragma(foo, 123)● DeclarationPragma(Symbol Integer】●
 ►#?STRUCTURE_EXPECTED!【pragma(foo)● DeclarationPragma(Symbol 】●
 ►#?STRUCTURE_EXPECTED!【pragma ( #error(EXP_ID) )● DeclarationPragma(Symbol 】●
¤》

    #@ATTRIBS_NoENUM《#@EXTERN_ATTRIB●#@ALIGN_ATTRIB●#@PRAGMA_ATTRIB#PRAGMA(flag)●#@SIMPLE_ATTRIBS》
#@ATTRIBS_NoTMNoENUM《#@EXTERN_ATTRIB●#@ALIGN_ATTRIB●#@PRAGMA_ATTRIB#PRAGMA(flag)●#@SIMPLE_ATTRIBS_NOTM》
#@ATTRIBS_NoTM《#@SIMPLE_ATTRIBS_ENUM●#@ATTRIBS_NoTMNoENUM》
     #@ATTRIBS《#@SIMPLE_ATTRIBS_ENUM●#@ATTRIBS_NoENUM》
     
#@ATTRIBS__LITE《
 ►#?STRUCTURE_EXPECTED!【extern(D)● DeclarationLinkage( 】●
 ►#?STRUCTURE_EXPECTED!【align(1)● DeclarationAlign( 】●
 ►#?STRUCTURE_EXPECTED!【pragma(foo, 123)● DeclarationPragma(Symbol Integer】●
 ►#@SIMPLE_ATTRIBS__SAMPLE●
 ►#@SIMPLE_ATTRIBS_ENUM●
¤》
