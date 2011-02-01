package mmrnmhrm.core.dltk;

import java.util.Iterator;
import java.util.List;

import org.eclipse.dltk.ast.Modifiers;
import org.eclipse.dltk.compiler.ISourceElementRequestor;
import org.eclipse.dltk.compiler.IElementRequestor.FieldInfo;
import org.eclipse.dltk.compiler.IElementRequestor.TypeInfo;

import descent.internal.compiler.parser.STC;
import dtool.ast.ASTNeoUpTreeVisitor;
import dtool.ast.definitions.BaseClass;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Definition;
import dtool.ast.definitions.DefinitionAggregate;
import dtool.ast.definitions.DefinitionAlias;
import dtool.ast.definitions.DefinitionClass;
import dtool.ast.definitions.DefinitionEnum;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.DefinitionInterface;
import dtool.ast.definitions.DefinitionTemplate;
import dtool.ast.definitions.DefinitionTypedef;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.Module;
import dtool.ast.references.NamedReference;

public final class DeeSourceElementProvider extends ASTNeoUpTreeVisitor {

	protected static final String[] EMPTY_STRING = new String[0];
	protected static final String OBJECT = "Object"; //$NON-NLS-1$
	protected static final String[] OBJECT_SUPER_CLASS_LIST = new String[] {OBJECT};

	protected ISourceElementRequestor requestor;

	public DeeSourceElementProvider(ISourceElementRequestor requestor) {
		this.requestor = requestor;
	}

	public void provide(DeeModuleDeclaration moduleDecl) {
		Module neoModule = moduleDecl.neoModule;
		
		requestor.enterModule();

		if(neoModule != null) {
			neoModule.accept(this);
		}
		
		requestor.exitModule(moduleDecl.dmdModule.getEndPos());
	}
	
	@Override
	public boolean visit(Module node) {
		//requestor.enterModule();
		requestor.enterType(createTypeInfoForModule(node));
		/*DeclarationModule md = node.md;
		String pkgName = "";
		if(md != null) {
			for (int i = 0; i < md.packages.length; i++) {
				RefIdentifier id = md.packages[i];
				if(i == 0)
					pkgName = pkgName + id.toString();
				else
					pkgName = pkgName + "." + id.toString();
			}
			requestor.acceptPackage(md.getStartPos(), md.getEndPos()-1, pkgName.toCharArray());
		} else {
			//requestor.acceptPackage(0, 0-1, "".toCharArray());
		}*/
		return true;
	}

	@Override
	public void endVisit(Module node) {
		requestor.exitType(node.sourceEnd() -1);
		//requestor.exitModule(node.sourceEnd() -1);
	}
	
	@Override
	public boolean visit(DefinitionAggregate elem) {
		requestor.enterType(createTypeInfoForDefinition(elem));
		return true;
	}
	@Override
	public void endVisit(DefinitionAggregate elem) {
		requestor.exitType(elem.sourceEnd() -1);
	}
	
	@Override
	public boolean visit(DefinitionTemplate elem) {
		requestor.enterType(createTypeInfoForDefinition(elem));
		return true;
	}
	@Override
	public void endVisit(DefinitionTemplate elem) {
		requestor.exitType(elem.sourceEnd() -1);
	}		
	
	@Override
	public boolean visit(DefinitionClass elem) {
		requestor.enterType(createTypeInfoForClass(elem));
		return true;
	}
	@Override
	public void endVisit(DefinitionClass elem) {
		requestor.exitType(elem.sourceEnd() -1);
	}	
	
	@Override
	public boolean visit(DefinitionFunction elem) {
		requestor.enterMethod(createMethodInfo(elem));
		return true;
	}
	@Override
	public void endVisit(DefinitionFunction elem) {
		requestor.exitMethod(elem.sourceEnd() -1);
	}	

	/* ---------------------------------- */

	@Override
	public boolean visit(DefinitionVariable elem) {
		requestor.enterField(createFieldInfo(elem));
		return true;
	}
	
	@Override
	public void endVisit(DefinitionVariable elem) {
		requestor.exitField(elem.sourceEnd()-1);
	}	
	
	@Override
	public boolean visit(DefinitionEnum elem) {
		requestor.acceptFieldReference(elem.getName(), elem.sourceStart());
		return true;
	}
	
	@Override
	public boolean visit(DefinitionTypedef elem) {
		requestor.acceptFieldReference(elem.getName(), elem.sourceStart());
		return true;
	}
	
	@Override
	public boolean visit(DefinitionAlias elem) {
		requestor.acceptFieldReference(elem.getName(), elem.sourceStart());
		return true;
	}
	

	@Override
	public boolean visit(NamedReference elem) {
		requestor.acceptTypeReference(elem.toStringAsElement(), elem.sourceStart() /*-1*/);
		return true;
	}
	
	/* ================================== */

	


	protected static void setupDefUnitTypeInfo(DefUnit defAggr,
			ISourceElementRequestor.ElementInfo elemInfo) {
		elemInfo.name = defAggr.getName();
		elemInfo.declarationStart = defAggr.sourceStart();
		elemInfo.nameSourceStart = defAggr.defname.sourceStart();
		elemInfo.nameSourceEnd = defAggr.defname.sourceEnd() - 1;
	}
	
	private void setupDefinitionTypeInfo(Definition elem, ISourceElementRequestor.ElementInfo elemInfo) {
		elemInfo.modifiers = getModifiersFlags(elem);
		elemInfo.modifiers = getProtectionFlags(elem, elemInfo.modifiers);
	}
	


	private static int getModifiersFlags(Definition elem) {
		int modifiers = 0;
		
		if((elem.effectiveModifiers & STC.STCabstract) != 0)
			modifiers |= Modifiers.AccAbstract; 
		if((elem.effectiveModifiers & STC.STCconst) != 0)
			modifiers |= Modifiers.AccConst; 
		if((elem.effectiveModifiers & STC.STCfinal) != 0)
			modifiers |= Modifiers.AccFinal; 
		if((elem.effectiveModifiers & STC.STCstatic) != 0)
			modifiers |= Modifiers.AccStatic; 
/*		
		for (int i = 0; i < elem.modifiers.length; i++) {
			Modifier mod = elem.modifiers[i];
			if(mod.tok.value.equals(TOK.TOKabstract))
				modifiers |= Modifiers.AccAbstract; 
			if(mod.tok.value.equals(TOK.TOKconst))
				modifiers |= Modifiers.AccConst; 
			if(mod.tok.value.equals(TOK.TOKfinal))
				modifiers |= Modifiers.AccFinal; 
			if(mod.tok.value.equals(TOK.TOKstatic))
				modifiers |= Modifiers.AccStatic; 
//			if(mod.tok.value.equals(TOK.TOKabstract))
//				modifiers |= Modifiers.AccAbstract; 
				
		}*/
		return modifiers;
	}
	
	private static int getProtectionFlags(Definition elem, int modifiers) {
		// default:
		
		switch(elem.getEffectiveProtection()) {
		case PROTprivate: modifiers |= Modifiers.AccPrivate; break;
		case PROTpublic: modifiers |= Modifiers.AccPublic; break;
		case PROTprotected: modifiers |= Modifiers.AccProtected; break;
		case PROTpackage: modifiers |= Modifiers.AccDefault; break;
		default: modifiers |= Modifiers.AccPublic;
		}
		return modifiers;
	}
	
	private TypeInfo createTypeInfoForModule(Module elem) {
		ISourceElementRequestor.TypeInfo typeInfo = new ISourceElementRequestor.TypeInfo();
		setupDefUnitTypeInfo(elem, typeInfo);
		typeInfo.modifiers |= Modifiers.AccModule;
		//typeInfo.superclasses = DeeSourceElementProvider.EMPTY_STRING;
		return typeInfo;
	}
	
	private TypeInfo createTypeInfoForDefinition(Definition elem) {
		ISourceElementRequestor.TypeInfo typeInfo = new ISourceElementRequestor.TypeInfo();
		setupDefUnitTypeInfo(elem, typeInfo);
		setupDefinitionTypeInfo(elem, typeInfo);
		if(elem instanceof DefinitionInterface)
			typeInfo.modifiers |= Modifiers.AccInterface;
		typeInfo.superclasses = DeeSourceElementProvider.EMPTY_STRING;
		return typeInfo;
	}

	
	private TypeInfo createTypeInfoForClass(DefinitionClass elem) {
		ISourceElementRequestor.TypeInfo typeInfo = createTypeInfoForDefinition(elem);
		typeInfo.superclasses = DeeSourceElementProvider.processClassNames(elem);
		return typeInfo;
	}
	

	protected static String[] processClassNames(DefinitionClass defClass) {
		if(defClass.getName().equals("Object"))
			return DeeSourceElementProvider.EMPTY_STRING;

		List<BaseClass> coll = defClass.baseClasses;
		if(coll == null || coll.isEmpty()) {
			if(defClass instanceof DefinitionInterface && false)
				return DeeSourceElementProvider.EMPTY_STRING;
			else {
				return DeeSourceElementProvider.OBJECT_SUPER_CLASS_LIST;
			}
		}
		String[] strs = new String[coll.size()];
		Iterator<BaseClass> iter = coll.iterator();
		for (int i = 0; i < strs.length; i++) {
			strs[i] = iter.next().type.toStringAsElement();
		}
		return strs;
	}
	
	private ISourceElementRequestor.MethodInfo createMethodInfo(DefinitionFunction elem) {
		ISourceElementRequestor.MethodInfo methodInfo = new ISourceElementRequestor.MethodInfo();
		setupDefUnitTypeInfo(elem, methodInfo);
		setupDefinitionTypeInfo(elem, methodInfo);
		
		methodInfo.parameterNames = new String[elem.params.size()];
		methodInfo.parameterInitializers = new String[elem.params.size()];
		for (int i = 0; i < methodInfo.parameterNames.length; i++) {
			String name = elem.params.get(i).toStringAsFunctionSimpleSignaturePart();
			if(name == null)
				name = "";
			methodInfo.parameterNames[i] = name;
			String initStr = elem.params.get(i).toStringInitializer();
			methodInfo.parameterInitializers[i] = initStr; 
		}
		return methodInfo;
	}
	
	
	private FieldInfo createFieldInfo(DefinitionVariable elem) {
		ISourceElementRequestor.FieldInfo fieldInfo = new ISourceElementRequestor.FieldInfo();
		setupDefUnitTypeInfo(elem, fieldInfo);
		setupDefinitionTypeInfo(elem, fieldInfo);
		return fieldInfo;
	}
	

}
