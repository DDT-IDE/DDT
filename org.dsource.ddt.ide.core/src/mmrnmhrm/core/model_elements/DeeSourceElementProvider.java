/*******************************************************************************
 * Copyright (c) 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.model_elements;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import mmrnmhrm.core.parser.DeeModuleDeclaration;

import org.eclipse.dltk.ast.Modifiers;
import org.eclipse.dltk.compiler.IElementRequestor.FieldInfo;
import org.eclipse.dltk.compiler.IElementRequestor.TypeInfo;
import org.eclipse.dltk.compiler.ISourceElementRequestor;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.ModelException;

import dtool.ast.ASTNode;
import dtool.ast.ASTSwitchVisitor;
import dtool.ast.declarations.AttribBasic.AttributeKinds;
import dtool.ast.definitions.CommonDefinition;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.DefVarFragment;
import dtool.ast.definitions.DefinitionAlias.DefinitionAliasFragment;
import dtool.ast.definitions.DefinitionAliasFunctionDecl;
import dtool.ast.definitions.DefinitionAliasVarDecl;
import dtool.ast.definitions.DefinitionClass;
import dtool.ast.definitions.DefinitionConstructor;
import dtool.ast.definitions.DefinitionEnum;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.DefinitionInterface;
import dtool.ast.definitions.DefinitionMixinInstance;
import dtool.ast.definitions.DefinitionStruct;
import dtool.ast.definitions.DefinitionTemplate;
import dtool.ast.definitions.DefinitionUnion;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.EnumMember;
import dtool.ast.definitions.ICallableElement;
import dtool.ast.definitions.IFunctionParameter;
import dtool.ast.definitions.Module;
import dtool.ast.expressions.ExpCall;
import dtool.ast.expressions.ExpReference;
import dtool.ast.references.NamedReference;
import dtool.ast.references.Reference;
import dtool.util.ArrayView;

public final class DeeSourceElementProvider extends ASTSwitchVisitor {
	
	protected ISourceElementRequestor requestor;
	
	public DeeSourceElementProvider(ISourceElementRequestor requestor) {
		this.requestor = requestor;
	}
	
	public void provide(DeeModuleDeclaration moduleDecl) {
		requestor.enterModule();
		
		Module module = moduleDecl.getModule();
		if(module.md != null) {
			requestor.enterNamespace(module.md.packages);
		} else {
			requestor.enterNamespace(EMPTY_STRING);
		}
		
		module.accept(this);
		
		requestor.exitNamespace();
		
		requestor.exitModule(module.getEndPos());
	}
	
	@Override
	public boolean visit(Module node) {
		requestor.enterType(createTypeInfoForModule(node));
		return true;
	}
	
	@Override
	public void endVisit(Module node) {
		requestor.exitType(getDeclarationEndforNode(node));
	}
	
	protected static int getDeclarationEndforNode(ASTNode node) {
		return node.getEndPos() - 1;
	}
	
	/* ---------------------------------- */
	
	@Override
	public boolean visit(DefinitionVariable node) {
		requestor.enterField(createFieldInfo(node));
		requestor.exitField(getDeclarationEndforNode(node));
		return true;
	}
	@Override
	public void endVisit(DefinitionVariable node) {
	}
	
	@Override
	public boolean visit(DefVarFragment node) {
		requestor.enterField(createFieldInfo(node));
		requestor.exitField(getDeclarationEndforNode(node));
		return true;
	}
	@Override
	public void endVisit(DefVarFragment node) {
	}
	
	protected static FieldInfo createFieldInfo(DefinitionVariable defVar) {
		ISourceElementRequestor.FieldInfo fieldInfo = new ISourceElementRequestor.FieldInfo();
		setupDefUnitTypeInfo(defVar, fieldInfo, DeeModelConstants.FLAG_KIND_VARIABLE);
		setupDefinitionTypeInfo(defVar, fieldInfo);
		fieldInfo.type = getTypeRefString(defVar.type);
		
		return fieldInfo;
	}
	
	protected static FieldInfo createFieldInfo(DefVarFragment defVarFragment) {
		ISourceElementRequestor.FieldInfo fieldInfo = new ISourceElementRequestor.FieldInfo();
		setupDefUnitTypeInfo(defVarFragment, fieldInfo, DeeModelConstants.FLAG_KIND_VARIABLE);
		setupModifiersInfo(defVarFragment.getDefinitionVariableParent(), fieldInfo);
		fieldInfo.type = getTypeRefString(defVarFragment.getDefinitionVariableParent().type);
		
		return fieldInfo;
	}
	
	public static String getTypeRefString(Reference typeReference) {
		if(typeReference == null) {
			return "auto"; // Perhaps we could just return null instead
		}
		return typeReference.toStringAsCode();
	}
	
	/* ---------------------------------- */
	
	@Override
	public boolean visit(DefinitionFunction node) {
		requestor.enterMethod(createMethodInfo(node));
		return true; //TODO: make /*BUG here set to false*/
	}
	@Override
	public void endVisit(DefinitionFunction node) {
		requestor.exitMethod(getDeclarationEndforNode(node));
	}
	
	@Override
	public boolean visit(DefinitionConstructor node) {
		requestor.enterMethod(createConstructorInfo(node));
		return true; //TODO: make /*BUG here set to false*/
	}
	@Override
	public void endVisit(DefinitionConstructor node) {
		requestor.exitMethod(getDeclarationEndforNode(node));
	}
	
	/* ---------------------------------- */
	
	@Override
	public boolean visit(DefinitionStruct node) {
		requestor.enterType(createTypeInfoForDefinition(node, DeeModelConstants.FLAG_KIND_STRUCT));
		return true;
	}
	@Override
	public void endVisit(DefinitionStruct node) {
		requestor.exitType(getDeclarationEndforNode(node));
	}
	
	@Override
	public boolean visit(DefinitionUnion node) {
		requestor.enterType(createTypeInfoForDefinition(node, DeeModelConstants.FLAG_KIND_UNION));
		return true;
	}
	@Override
	public void endVisit(DefinitionUnion node) {
		requestor.exitType(getDeclarationEndforNode(node));
	}
	
	@Override
	public boolean visit(DefinitionInterface node) {
		requestor.enterType(createTypeInfoForInterface(node));
		return true;
	}
	@Override
	public void endVisit(DefinitionInterface node) {
		requestor.exitType(getDeclarationEndforNode(node));
	}
	
	@Override
	public boolean visit(DefinitionClass node) {
		assertTrue(node.getClass() == DefinitionClass.class);
		requestor.enterType(createTypeInfoForClass(node));
		return true;
	}
	@Override
	public void endVisit(DefinitionClass node) {
		requestor.exitType(getDeclarationEndforNode(node));
	}
	
	
	@Override
	public boolean visit(DefinitionTemplate node) {
		requestor.enterType(createTypeInfoForDefinition(node, DeeModelConstants.FLAG_KIND_TEMPLATE));
		return true;
	}
	@Override
	public void endVisit(DefinitionTemplate node) {
		requestor.exitType(getDeclarationEndforNode(node));
	}
	
	@Override
	public boolean visit(DefinitionMixinInstance node) {
		requestor.enterType(createTypeInfoForDefinition(node, DeeModelConstants.FLAG_KIND_MIXIN));
		return true;
	}
	@Override
	public void endVisit(DefinitionMixinInstance node) {
		requestor.exitType(getDeclarationEndforNode(node));
	}

	@Override
	public boolean visit(DefinitionEnum node) {
		requestor.enterType(createTypeInfoForDefinition(node, DeeModelConstants.FLAG_KIND_ENUM));
		return true;
	}
	@Override
	public void endVisit(DefinitionEnum node) {
		requestor.exitType(getDeclarationEndforNode(node));
	}
	
	@Override
	public boolean visit(EnumMember node) {
		// Don't report as model element
		return false;
	}
	@Override
	public void endVisit(EnumMember node) {
	}
	
	
	/* ---------------------------------- */
	
	protected static final String[] EMPTY_STRING = new String[0];
	
	protected static void setupDefUnitTypeInfo(DefUnit defUnit, ISourceElementRequestor.ElementInfo elemInfo,
			int archetypeMask) {
		elemInfo.name = defUnit.getName();
		elemInfo.declarationStart = defUnit.getStartPos();
		elemInfo.nameSourceStart = defUnit.defname.getStartPos();
		elemInfo.nameSourceEnd = defUnit.defname.getEndPos() - 1;
		
		if(defUnit instanceof Module) {
			elemInfo.modifiers |= Modifiers.AccModule;
		} else if(defUnit instanceof DefinitionInterface) {
			elemInfo.modifiers |= Modifiers.AccInterface; // This one might be redundant as archetype is also set
		}
		
		assertTrue((archetypeMask & DeeModelConstants.FLAGMASK_KIND) == archetypeMask);
		elemInfo.modifiers |= archetypeMask;
	}
	
	protected static void setupDefinitionTypeInfo(CommonDefinition commonDef, 
		ISourceElementRequestor.ElementInfo elemInfo) {
		setupModifiersInfo(commonDef, elemInfo);
		elemInfo.declarationStart = commonDef.getExtendedStartPos();
	}
	
	public static void setupModifiersInfo(CommonDefinition commonDef, ISourceElementRequestor.ElementInfo elemInfo) {
		elemInfo.modifiers |= getCommonDefinitionModifiersInfo(commonDef);
	}
	
	public static int getCommonDefinitionModifiersInfo(CommonDefinition commonDef) {
		return getDeclarationModifiersFlags(commonDef) | getProtectionFlags(commonDef);
	}
	
	protected static int getDeclarationModifiersFlags(CommonDefinition elem) {
		int modifiers = 0;
		
		modifiers = addBitFlag(elem, AttributeKinds.ABSTRACT, modifiers, Modifiers.AccAbstract);
		modifiers = addBitFlag(elem, AttributeKinds.CONST, modifiers, Modifiers.AccConst);
		modifiers = addBitFlag(elem, AttributeKinds.FINAL, modifiers, Modifiers.AccFinal);
		modifiers = addBitFlag(elem, AttributeKinds.STATIC, modifiers, Modifiers.AccStatic);
		
		return modifiers;
	}
	
	protected static int addBitFlag(CommonDefinition def, AttributeKinds attrib, int modifiers, int modifierFlag) {
		if(def.hasAttribute(attrib)) {
			modifiers |= modifierFlag;
		}
		return modifiers;
	}
	
	protected static int getProtectionFlags(CommonDefinition elem) {
		int flags = 0;
		
		switch(elem.getEffectiveProtection()) {
		case PRIVATE: flags |= Modifiers.AccPrivate; break;
		case PUBLIC: flags |= Modifiers.AccPublic; break;
		case PROTECTED: flags |= Modifiers.AccProtected; break;
		case PACKAGE: flags |= DeeModelConstants.FLAG_PROTECTION_PACKAGE; break;
		case EXPORT: flags |= DeeModelConstants.FLAG_PROTECTION_EXPORT; break;
		
		default: flags |= Modifiers.AccPublic;
		}
		return flags;
	}
	
	protected static TypeInfo createTypeInfoForModule(Module elem) {
		ISourceElementRequestor.TypeInfo typeInfo = new ISourceElementRequestor.TypeInfo();
		setupDefUnitTypeInfo(elem, typeInfo, 0);
		//typeInfo.superclasses = DeeSourceElementProvider.EMPTY_STRING;
		return typeInfo;
	}
	
	protected static TypeInfo createTypeInfoForDefUnit(DefUnit node, int archetypeMask) {
		ISourceElementRequestor.TypeInfo typeInfo = new ISourceElementRequestor.TypeInfo();
		setupDefUnitTypeInfo(node, typeInfo, archetypeMask);
		typeInfo.superclasses = DeeSourceElementProvider.EMPTY_STRING;
		return typeInfo;
	}
	
	protected static TypeInfo createTypeInfoForDefinition(CommonDefinition node, int archetypeMask) {
		TypeInfo typeInfo = createTypeInfoForDefUnit(node, archetypeMask);
		setupDefinitionTypeInfo(node, typeInfo);
		return typeInfo;
	}
	
	
	protected static TypeInfo createTypeInfoForClass(DefinitionClass node) {
		int archeType = DeeModelConstants.FLAG_KIND_CLASS;
		ISourceElementRequestor.TypeInfo typeInfo = createTypeInfoForDefinition(node, archeType);
		typeInfo.superclasses = DeeSourceElementProvider.processSuperClassNames(node, false);
		return typeInfo;
	}
	
	protected static TypeInfo createTypeInfoForInterface(DefinitionInterface node) {
		int archetype = DeeModelConstants.FLAG_KIND_INTERFACE;
		ISourceElementRequestor.TypeInfo typeInfo = createTypeInfoForDefinition(node, archetype);
		typeInfo.superclasses = DeeSourceElementProvider.processSuperClassNames(node, true);
		return typeInfo;
	}
	
	protected static final String OBJECT = "Object";
	protected static final String[] OBJECT_SUPER_CLASS_LIST = new String[] { OBJECT };
	
	protected static String[] processSuperClassNames(DefinitionClass defClass, boolean isInterface) {
		if(defClass.getName().equals("Object"))
			return DeeSourceElementProvider.EMPTY_STRING;
		
		ArrayView<Reference> baseClasses = defClass.baseClasses;
		if(baseClasses == null || baseClasses.size() == 0) {
			if(isInterface) {
				return DeeSourceElementProvider.EMPTY_STRING;
			} else {
				return DeeSourceElementProvider.OBJECT_SUPER_CLASS_LIST;
			}
		}
		String[] baseClassesStr = new String[baseClasses.size()];
		for (int i = 0; i < baseClasses.size(); i++) {
			// There is no way this can work without a FQN, but I don't know what DLTK wants
			baseClassesStr[i] = baseClasses.get(i).toStringAsCode(); 
		}
		return baseClassesStr;
	}
	
	protected static ISourceElementRequestor.MethodInfo createMethodInfo(DefinitionFunction elem) {
		ISourceElementRequestor.MethodInfo methodInfo = new ISourceElementRequestor.MethodInfo();
		setupDefUnitTypeInfo(elem, methodInfo, DeeModelConstants.FLAG_KIND_FUNCTION);
		setupDefinitionTypeInfo(elem, methodInfo);
		
		setupParametersInfo(elem, methodInfo);
		methodInfo.returnType = getTypeRefString(elem.retType);
		return methodInfo;
	}
	
	protected static ISourceElementRequestor.MethodInfo createConstructorInfo(DefinitionConstructor elem) {
		ISourceElementRequestor.MethodInfo elemInfo = new ISourceElementRequestor.MethodInfo();
		setupDefUnitTypeInfo(elem, elemInfo, DeeModelConstants.FLAG_KIND_CONSTRUCTOR);
		setupDefinitionTypeInfo(elem, elemInfo);
		elemInfo.isConstructor = true;
		
		setupParametersInfo(elem, elemInfo);
		return elemInfo;
	}
	
	protected static void setupParametersInfo(ICallableElement elem, ISourceElementRequestor.MethodInfo methodInfo) {
		ArrayView<IFunctionParameter> params = elem.getParameters();
		
		methodInfo.parameterNames = new String[params.size()];
		methodInfo.parameterTypes = new String[params.size()];
		methodInfo.parameterInitializers = new String[params.size()];
		for (int i = 0; i < methodInfo.parameterNames.length; i++) {
			IFunctionParameter param = params.get(i);
			
			methodInfo.parameterNames[i] = param instanceof DefUnit ? ((DefUnit) param).getName() : "";
			methodInfo.parameterTypes[i] = param.getTypeStringRepresentation();
			methodInfo.parameterInitializers[i] = param.getInitializerStringRepresentation(); 
		}
	}
	
	@Override
	public boolean visit(DefinitionAliasVarDecl node) {
		requestor.enterType(createTypeInfoForDefinition(node, DeeModelConstants.FLAG_KIND_ALIAS));
		return true;
	}
	@Override
	public void endVisit(DefinitionAliasVarDecl node) {
		requestor.exitType(getDeclarationEndforNode(node));
	}
	
	@Override
	public boolean visit(DefinitionAliasFunctionDecl node) {
		requestor.enterType(createTypeInfoForDefinition(node, DeeModelConstants.FLAG_KIND_ALIAS));
		return true;
	}
	@Override
	public void endVisit(DefinitionAliasFunctionDecl node) {
		requestor.exitType(getDeclarationEndforNode(node));
	}
	
	@Override
	public boolean visit(DefinitionAliasFragment node) {
		requestor.enterType(createTypeInfoForFragment(node));
		return true;
	}
	@Override
	public void endVisit(DefinitionAliasFragment node) {
		requestor.exitType(getDeclarationEndforNode(node));
	}
	
	public static TypeInfo createTypeInfoForFragment(DefinitionAliasFragment node) {
		TypeInfo typeInfo = createTypeInfoForDefUnit(node, DeeModelConstants.FLAG_KIND_ALIAS);
		if(node.getDefinitionAliasParent().aliasFragments.get(0) == node) {
			typeInfo.declarationStart = node.getDefinitionAliasParent().getStartPos();
			// TODO: test case for extended start pos (definition alias with extended start)
		}
		return typeInfo;
	}
	
	public static DefElementDescriptor toElementDescriptor(IMember member) throws ModelException {
		int modifierFlags = member.getFlags();
		return new DefElementDescriptor(modifierFlags);
	}	
	
	/* ================================== */
	
	@Override
	public boolean visit(NamedReference elem) {
		Reference topReference = elem;
		
		ASTNode parent = topReference.getParent();
		if(parent instanceof ExpReference) {
			parent = parent.getParent();
		}
		if(parent instanceof ExpCall) {
			ExpCall expCall = (ExpCall) parent;
			int length = expCall.args == null ? 0 : expCall.args.size();
			String methodName = elem.getCoreReferenceName(); // Dont use qualified name
			if(methodName != null) {
				requestor.acceptMethodReference(methodName, length, elem.getStartPos(), elem.getEndPos()-1);
			}
		}
		// Should we report qualified refs as well?
		requestor.acceptTypeReference(elem.toStringAsCode(), elem.getStartPos());
		return true;
	}
	
}