package mmrnmhrm.core.model_elements;

import mmrnmhrm.tests.BaseDeeTest;

import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;

public abstract class SampleModelElementsVisitor extends BaseDeeTest {
	
	protected final ISourceModule srcModule;
	protected final IType topLevelElement;

	public SampleModelElementsVisitor(ISourceModule srcModule) {
		this.srcModule = srcModule;
		this.topLevelElement = srcModule.getType("sampledefs");
	}
	
	public void visitAll() {
		IType moduleType = topLevelElement;
		
		IField variable = topLevelElement.getField("Variable");
		IField variable2 = topLevelElement.getField("Variable2");
		IField varExtended = topLevelElement.getField("VarExtended");
		IField varExtended2 = topLevelElement.getField("VarExtended2");
		IField autoVar = topLevelElement.getField("AutoVar");
		IField autoVar2 = topLevelElement.getField("AutoVar2");
		
		IMethod function_ = topLevelElement.getMethod("Function");
		IMethod autoFunction = topLevelElement.getMethod("AutoFunction");
		
		IType struct_ = topLevelElement.getType("Struct");
		IType union_ = topLevelElement.getType("Union");
		IType class_ = topLevelElement.getType("Class");
		IType interface_ = topLevelElement.getType("Interface");
		IType template = topLevelElement.getType("Template");
		IType enum_ = topLevelElement.getType("Enum");
		IType mixin = topLevelElement.getType("Mixin");
		IType aliasVarDecl = topLevelElement.getType("AliasVarDecl");
		IType aliasFunctionDecl = topLevelElement.getType("AliasFunctionDecl");
		IType aliasFrag = topLevelElement.getType("AliasFrag");
		IType aliasFrag2 = topLevelElement.getType("AliasFrag2");
		
		// Nested elements:
		IField otherClass_fieldA = topLevelElement.getType("OtherClass").getField("fieldA");
		IMethod otherClass_methodB = topLevelElement.getType("OtherClass").getMethod("methodB");
		IMethod otherClass_this = topLevelElement.getType("OtherClass").getMethod("this");
		final IType otherTemplate = topLevelElement.getType("OtherTemplate");
		IType tplNestedClass = otherTemplate.getType("TplNestedClass");
		IMethod tplFunc = otherTemplate.getType("TplNestedClass").getMethod("tplFunc");
		
		
		visitAllModelElements(moduleType, variable, variable2, varExtended, varExtended2, autoVar, autoVar2,
			function_, autoFunction, struct_, union_, class_, interface_, template, enum_, mixin, aliasVarDecl,
			aliasFunctionDecl, aliasFrag, aliasFrag2, otherClass_fieldA, otherClass_methodB, otherClass_this,
			tplNestedClass, tplFunc);
	}
	
	public abstract void visitAllModelElements(
		IType _Module,
		IField _Variable,
		IField _Variable2,
		IField _VarExtended,
		IField _VarExtended2,
		IField _AutoVar,
		IField _AutoVar2,
		
		IMethod _Function,
		IMethod _AutoFunction,
		
		IType _Struct,
		IType _Union,
		IType _Class,
		IType _Interface,
		IType _Template,
		IType _Enum,
		IType _Mixin,
		IType _AliasVarDecl,
		IType _AliasFunctionDecl,
		IType _AliasFrag,
		IType _AliasFrag2,
		IField _OtherClass_fieldA,
		IMethod _OtherClass_methodB,
		IMethod _OtherClass_this,
		IType _OtherTemplate_TplNestedClass,
		IMethod tplFunc
	);
	
}