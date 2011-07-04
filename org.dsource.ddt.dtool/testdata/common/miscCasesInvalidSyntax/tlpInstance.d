import std.stdio;

void func(string str = __FILE__){
    
    // at dtool.ast.references.ReferenceConverter.convertDotTemplateIdExp(ReferenceConverter.java:303)
    (BitUtils.extract!(uint, 4, 4)); 
    
	(BitUtils.extract!(uint, 4, 4))();
} 