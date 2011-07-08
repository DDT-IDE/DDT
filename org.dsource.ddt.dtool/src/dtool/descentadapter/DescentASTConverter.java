package dtool.descentadapter;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Collection;

import melnorme.utilbox.misc.ArrayUtil;
import descent.internal.compiler.parser.ast.ASTNode;
import descent.internal.compiler.parser.ast.IASTNode;
import dtool.ast.ASTNeoNode;
import dtool.ast.definitions.ArrayView;
import dtool.ast.definitions.Module;

public class DescentASTConverter extends StatementConverterVisitor {

	public DescentASTConverter(ASTConversionContext convContext) {
		this.convContext = convContext;
	}
	
	public static class ASTConversionContext {
		
		public ASTConversionContext(descent.internal.compiler.parser.Module module) {
			this.module = module;
		}

		public final descent.internal.compiler.parser.Module module;
		
		public boolean hasSyntaxErrors() {
			return module.hasSyntaxErrors();
		}
	}
	
	
	public static Module convertModule(ASTNode cumodule) {
		ASTConversionContext convCtx = new ASTConversionContext((descent.internal.compiler.parser.Module) cumodule);
		Module module = DefinitionConverter.createModule(convCtx.module, convCtx);
		module.accept(new PostConvertionAdapter());
		return module;
	}
	
	public static ASTNeoNode convertElem(ASTNode elem, ASTConversionContext convContext) {
		if(elem == null) return null;
		DescentASTConverter conv = new DescentASTConverter(convContext);
		elem.accept(conv);
		return conv.ret;
	}
	
	public static ASTNeoNode[] convertMany(Collection<? extends IASTNode> children, ASTConversionContext convContext) {
		if(children == null) return null;
		ASTNeoNode[] rets = new ASTNeoNode[children.size()];
		convertMany(children.toArray(), rets, convContext);
		return rets;
	}
	
	public static <T extends IASTNode> T[] convertMany(Collection<? extends IASTNode> children, Class<T> klass,
			ASTConversionContext convContext) {
		if(children == null) return null;
		T[] rets = ArrayUtil.create(children.size(), klass);
		convertMany(children.toArray(), rets, convContext);
		return rets;
	}
	
	public static <T extends IASTNode> ArrayView<T> convertManyToView(Collection<? extends IASTNode> children,
			Class<T> klass, ASTConversionContext convContext) {
		if(children == null) return null;
		return ArrayView.create(convertMany(children, klass, convContext));
	}
	
	public static ArrayView<ASTNeoNode> convertManyNoNulls(Collection<? extends IASTNode> children, 
			ASTConversionContext convContext) {
		if(children == null) return null;
		ArrayView<ASTNeoNode> res = convertManyToView(children, ASTNeoNode.class, convContext);
		if(true) {
			assertTrue(ArrayUtil.contains(res.getInternalArray(), null) == false);
		}
		return res;
	}
	
	public static <T extends IASTNode> ArrayView<T> convertManyToView(Object[] children, Class<T> klass, 
			ASTConversionContext convContext) {
		if(children == null) return null;
		return ArrayView.create(convertMany(children, klass, convContext));
	}
	
	public static <T extends IASTNode> T[] convertMany(Object[] children, Class<T> klass,
			ASTConversionContext convContext) {
		if(children == null) return null;
		T[] rets = ArrayUtil.create(children.length, klass);
		convertMany(children, rets, convContext);
		return rets;
	}
	
	@SuppressWarnings("unchecked")
	protected static <T extends IASTNode> T[] convertMany(Object[] children, T[] rets, 
			ASTConversionContext convContext) {
		for(int i = 0; i < children.length; ++i) {
			ASTNode elem = (ASTNode) children[i];
			rets[i] = (T) convertElem(elem, convContext);
		}
		return rets;
	}
	
}
