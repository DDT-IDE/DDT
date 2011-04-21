package dtool.descentadapter;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.Argument;
import descent.internal.compiler.parser.AttribDeclaration;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.TemplateInstanceWrapper;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.ast.ASTNode;
import dtool.ast.definitions.NamelessParameter;
import dtool.ast.expressions.Expression;
import dtool.ast.references.ReferenceConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

public class DefinitionConverter extends BaseDmdConverter {
	
	public static NamelessParameter convertNamelessParameter(Type type, ASTConversionContext convContext) {
		return new NamelessParameter(ReferenceConverter.convertType(type, convContext), 0, null, sourceRange(type));
	}

	public static NamelessParameter convertNamelessParameter(Argument elem, IdentifierExp ident,
			@SuppressWarnings("unused") ASTConversionContext convContext) {
		assertTrue(!(ident instanceof TemplateInstanceWrapper));
		return new NamelessParameter(ReferenceConverter.convertToRefIdentifier(ident), 0, null, sourceRange(elem));
	}

	public static NamelessParameter convertNamelessParameter(Argument elem, ASTConversionContext convContext) {
		return new NamelessParameter(ReferenceConverter.convertType(elem.type, convContext), elem.storageClass, 
				Expression.convert(elem.defaultArg, convContext), sourceRange(elem));
	}
	
	public static boolean isSingleSymbolDeclaration(ASTDmdNode parent) {
		if(!(parent instanceof AttribDeclaration)) {
			return false;
		}
		int length = 0;
		for(ASTNode child : parent.getChildren()) {
			if(child instanceof Dsymbol) {
				length++;
				if(length > 1) {
					return false;
				}
			}
		}
		return length == 1;
	}
	
	
}
