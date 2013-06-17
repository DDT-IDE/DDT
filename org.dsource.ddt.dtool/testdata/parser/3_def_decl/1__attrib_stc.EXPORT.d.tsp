Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@PROT_ATTRIBS《
 ►#?STRUCTURE_EXPECTED!【private●DeclarationProtection( 】●
 ►#?STRUCTURE_EXPECTED!【package●DeclarationProtection( 】●
 ►#?STRUCTURE_EXPECTED!【protected●DeclarationProtection( 】●
 ►#?STRUCTURE_EXPECTED!【public●DeclarationProtection( 】●
 ►#?STRUCTURE_EXPECTED!【export●DeclarationProtection( 】●
¤》


// ref attrib is not documented in spec as an attrib as of 2.062

#@BASIC_ATTRIBS《
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
 ►#?STRUCTURE_EXPECTED!【synchronized #SYNCHRONIZED_ATTRIB(flag)●DeclarationBasicAttrib( 】●
 
 ►#?STRUCTURE_EXPECTED!【ref●DeclarationBasicAttrib( 】●
 
 ►#?STRUCTURE_EXPECTED!【__thread /*This attrib is not documented in spec?*/●DeclarationBasicAttrib( 】●
¤》

#@BASIC_ATTRIB_ENUM《
 ►#?STRUCTURE_EXPECTED!【enum #ENUM_ATTRIB(flag)●DeclarationBasicAttrib( 】●
¤》

#@TYPEMOD_ATTRIBS《
 ►#?STRUCTURE_EXPECTED!【const●DeclarationBasicAttrib( 】●
 ►#?STRUCTURE_EXPECTED!【immutable●DeclarationBasicAttrib( 】●
 ►#?STRUCTURE_EXPECTED!【inout●DeclarationBasicAttrib( 】●
 ►#?STRUCTURE_EXPECTED!【shared●DeclarationBasicAttrib( 】●
¤》

#@AT_ATTRIBS【#?STRUCTURE_EXPECTED!【#@《@property●@safe●@trusted●@  system●@disable●@ #error(INVALID_ATTRIB_ID){xxx}》●
  DeclarationAtAttrib(Symbol 】】


// These attribs enable auto declarations
#@ATTRIBS_AUTODecl《
 ►#@PROT_ATTRIBS●
 ►#@BASIC_ATTRIBS●
 ►#@BASIC_ATTRIB_ENUM●
 ►#@TYPEMOD_ATTRIBS●
 ►#@AT_ATTRIBS●
¤》

#@ATTRIBS_AUTODecl__SAMPLE《
 ►#?STRUCTURE_EXPECTED!【package●DeclarationProtection( 】●
 ►#?STRUCTURE_EXPECTED!【static●DeclarationBasicAttrib( 】●
 ►#@BASIC_ATTRIB_ENUM●
 ►#?STRUCTURE_EXPECTED!【const●DeclarationBasicAttrib( 】●
 ►#?STRUCTURE_EXPECTED!【@safe●DeclarationAtAttrib(Symbol 】●
¤》


/----------------  Composite attributes  ----------------/

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


#@COMPLEX_ATTRIBS《
 ►#@EXTERN_ATTRIB●
 ►#@ALIGN_ATTRIB●
 ►#@PRAGMA_ATTRIB #PRAGMA(flag)●
¤》


#@ATTRIBS《#@ATTRIBS_AUTODecl●#@COMPLEX_ATTRIBS》

#@ATTRIBS__LITE《
 ►#?STRUCTURE_EXPECTED!【extern(D)● DeclarationLinkage( 】●
 ►#?STRUCTURE_EXPECTED!【align(1)● DeclarationAlign( 】●
 ►#?STRUCTURE_EXPECTED!【pragma(foo, 123) #PRAGMA(flag)● DeclarationPragma(Symbol Integer】●
 ►#@ATTRIBS_AUTODecl__SAMPLE●
¤》

#@ATTRIBS_NoENUM《#@ATTRIBS #?ENUM_ATTRIB{#:DISCARD_CASE}》

