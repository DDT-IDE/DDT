package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.Modifier;
import descent.internal.compiler.parser.PROT;
import dtool.ast.declarations.DeclarationProtection.Protection;
import dtool.descentadapter.DescentASTConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

/**
 * Abstract class for all declaration-based DefUnits. 
 */
public abstract class Definition extends DefUnit {
	
	private static final Modifier[] NOMODIFIERS = new Modifier[0];
	
	public final Modifier[] modifiers;
	public /*final*/ PROT protection; // fixme, should be node
	public /*final*/ int effectiveModifiers;
	
	public Definition(DefUnitTuple defunit, PROT prot) {
		super(defunit);
		this.protection = prot;
		this.modifiers = NOMODIFIERS;
		this.effectiveModifiers = 0;
	}
	
	public static Definition convert(Dsymbol elem, ASTConversionContext convContext) {
		return (Definition) DescentASTConverter.convertElem(elem, convContext);
	}
	
	public PROT getEffectiveProtection() {
		if(protection == null || protection == PROT.PROTundefined) {
			// BM: What about is PROTnone, what is it??
			return PROT.PROTpublic;
		}
		return protection;
	}
	
	public static PROT fromProtection(Protection protection) {
		if(protection == null) 
			return PROT.PROTundefined;
		switch(protection) {
		case PRIVATE: return PROT.PROTprivate;
		case PACKAGE: return PROT.PROTpackage;
		case PROTECTED: return PROT.PROTprotected;
		case PUBLIC: return PROT.PROTpublic;
		case EXPORT: return PROT.PROTexport;
		}
		throw assertUnreachable();
	}
	
}