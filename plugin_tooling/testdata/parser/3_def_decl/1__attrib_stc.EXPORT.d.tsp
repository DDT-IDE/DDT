Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@PROT_ATTRIBS《
 ►#?STRUCTURE_EXPECTED!【private●AttribProtection】●
 ►#?STRUCTURE_EXPECTED!【package●AttribProtection】●
 ►#?STRUCTURE_EXPECTED!【protected●AttribProtection】●
 ►#?STRUCTURE_EXPECTED!【public●AttribProtection】●
 ►#?STRUCTURE_EXPECTED!【export●AttribProtection】●
¤》

#@BASIC_ATTRIBS《
 ►#?STRUCTURE_EXPECTED!【abstract●AttribBasic】●
 ►#?STRUCTURE_EXPECTED!【auto●AttribBasic】●
 
 ►#?STRUCTURE_EXPECTED!【deprecated●AttribBasic】●
 
 ►#?STRUCTURE_EXPECTED!【extern●AttribBasic】●
 ►#?STRUCTURE_EXPECTED!【final●AttribBasic】●
 ►#?STRUCTURE_EXPECTED!【nothrow●AttribBasic】●
 ►#?STRUCTURE_EXPECTED!【override●AttribBasic】●
 ►#?STRUCTURE_EXPECTED!【pure●AttribBasic】●
 ►#?STRUCTURE_EXPECTED!【__gshared●AttribBasic】●
 ►#?STRUCTURE_EXPECTED!【scope●AttribBasic】●
 ►#?STRUCTURE_EXPECTED!【static●AttribBasic】●
 ►#?STRUCTURE_EXPECTED!【synchronized #SYNCHRONIZED_ATTRIB(flag)●AttribBasic】●
 
 ►#?STRUCTURE_EXPECTED!【ref●AttribBasic】●
 
 ►#?STRUCTURE_EXPECTED!【__thread /*This attrib is not documented in spec?*/●AttribBasic】●
¤》

#@BASIC_ATTRIB_ENUM《
 ►#?STRUCTURE_EXPECTED!【enum #ENUM_ATTRIB(flag)●AttribBasic】●
¤》

#@TYPEMOD_ATTRIBS《
 ►#?STRUCTURE_EXPECTED!【const●AttribBasic】●
 ►#?STRUCTURE_EXPECTED!【immutable●AttribBasic】●
 ►#?STRUCTURE_EXPECTED!【inout●AttribBasic】●
 ►#?STRUCTURE_EXPECTED!【shared●AttribBasic】●
¤》

#@AT_ATTRIBS【#?STRUCTURE_EXPECTED!【#@《@property●@safe●@trusted●@  system●@disable》●
  AttribAtKeyword(Symbol)】】



/----------------  Composite attributes  ----------------/

#@LINKAGE_TYPE{C,D,Windows,Pascal,System}
#@EXTERN_ATTRIB《
 ►#?STRUCTURE_EXPECTED!【extern(#@LINKAGE_TYPE)● AttribLinkage】●
 ►#?STRUCTURE_EXPECTED!【extern (Xpto #error(BAD_LINKAGE_ID) )● AttribLinkage】●
 ►#?STRUCTURE_EXPECTED!【extern(#error(BAD_LINKAGE_ID))● AttribLinkage】●
 
 ►#?STRUCTURE_EXPECTED!【extern(C++, foo.bar)● AttribCppLinkage(RefQualified(RefIdentifier RefIdentifier))】●
 ►#?STRUCTURE_EXPECTED!【extern(C++, foo.#@NO_ID)● AttribCppLinkage(RefQualified(RefIdentifier #@NO_ID))】●
 ►#?STRUCTURE_EXPECTED!【extern(C++, #@NO_TYPEREF)● AttribCppLinkage(#@NO_TYPEREF)】●
 
¤》

#@ALIGN_ATTRIB《
 ►#?STRUCTURE_EXPECTED!【align● AttribAlign】●
 ►#?STRUCTURE_EXPECTED!【align(1)● AttribAlign】●
 ►#?STRUCTURE_EXPECTED!【align(12)● AttribAlign】●
 ►#?STRUCTURE_EXPECTED!【align(#error(EXP_INTEGER_DECIMAL)) ● AttribAlign】●
¤》

#@PRAGMA_ATTRIB《
 ►#?STRUCTURE_EXPECTED!【pragma(foo, 123)● AttribPragma(Symbol Integer)】●
 ►#?STRUCTURE_EXPECTED!【pragma(foo)● AttribPragma(Symbol)】●
 ►#?STRUCTURE_EXPECTED!【pragma( #error(EXP_ID)) ● AttribPragma(Symbol)】●
¤》

#@CUSTOM_ATTRIB《
 ►#?STRUCTURE_EXPECTED!【@foo()● AttribCustom(RefIdentifier)】●
 ►#?STRUCTURE_EXPECTED!【@(1, "foo")● AttribCustom(Integer String)】●
 ►#?STRUCTURE_EXPECTED!【@foo(bar) ● AttribCustom(RefIdentifier #@ExpIdentifier)】●
¤》


// These attribs enable auto declarations
#@ATTRIBS_AUTODecl《
 ►#@PROT_ATTRIBS●
 ►#@BASIC_ATTRIBS●
 ►#@TYPEMOD_ATTRIBS●
 ►#@AT_ATTRIBS●
 ►#?STRUCTURE_EXPECTED!【final @safe const●AttribBasic AttribAtKeyword(Symbol) AttribBasic】●
 ►#?STRUCTURE_EXPECTED!【align(1) pure●AttribAlign AttribBasic 】●
¤》

#@ATTRIBS_AUTODecl__SAMPLE《
 ►#?STRUCTURE_EXPECTED!【package●AttribProtection】●
 ►#?STRUCTURE_EXPECTED!【static●AttribBasic】●
 ►#@BASIC_ATTRIB_ENUM●
 ►#?STRUCTURE_EXPECTED!【final @safe const●AttribBasic AttribAtKeyword(Symbol) AttribBasic】●
 ►#?STRUCTURE_EXPECTED!【align(1) pure●AttribAlign AttribBasic 】●
¤》

#@COMPLEX_ATTRIBS《
 ►#@EXTERN_ATTRIB●
 ►#@ALIGN_ATTRIB●
 ►#@PRAGMA_ATTRIB #PRAGMA_ATTR(flag)●
 ►#@CUSTOM_ATTRIB●
 ►#?STRUCTURE_EXPECTED!【pragma(foo, 123) align(1)● AttribPragma(Symbol Integer) AttribAlign】●
 ►#?STRUCTURE_EXPECTED!【pure const extern(#error(BAD_LINKAGE_ID)) ● AttribBasic AttribBasic AttribLinkage 】●
¤》


    #@ATTRIBS《#@ATTRIBS_AUTODecl●#@COMPLEX_ATTRIBS》
#@ATTRIBS_Opt《#@ATTRIBS●#?STRUCTURE_EXPECTED!【●】》

#@ATTRIBS__LITE《
 ►#?STRUCTURE_EXPECTED!【extern(D)● AttribLinkage】●
 ►#?STRUCTURE_EXPECTED!【pragma(foo, 123) align(1)● AttribPragma(Symbol Integer) AttribAlign】●
 ►#@ATTRIBS_AUTODecl__SAMPLE●
¤》

#@ATTRIBS_NoENUM《#@ATTRIBS #?ENUM_ATTRIB{#:DISCARD_CASE}》

