Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@PRIMITIVE_TYPE_REF{
  bool,byte,ubyte,short,ushort,int,uint,long,ulong,char,wchar,dchar,float,double,real,void,
  ifloat,idouble,ireal,cfloat,cdouble,creal
}
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@(PRIMITIVE_TYPE_REF) dummy;
#@(PRIMITIVE_TYPE_REF) dummy2;
#AST_STRUCTURE_EXPECTED:
DefVariable(RefPrimitive DefSymbol)
DefVariable(RefPrimitive DefSymbol)

#AST_SOURCE_EXPECTED:
///Not much more to test

#@(PRIMITIVE_TYPE_REF) dummy;
#@(PRIMITIVE_TYPE_REF) dummy2;
