package dtool.descentadapter;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Collection;
import java.util.Iterator;

import melnorme.utilbox.core.CoreUtil;
import melnorme.utilbox.misc.ArrayUtil;
import descent.internal.compiler.parser.ast.ASTNode;
import descent.internal.compiler.parser.ast.IASTNode;
import dtool.ast.ASTNeoHomogenousVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.definitions.Module;
import dtool.util.ArrayView;

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
	
	
	public static Module convertModule(ASTNode cumodule, String defaultModuleName) {
		ASTConversionContext convCtx = new ASTConversionContext((descent.internal.compiler.parser.Module) cumodule);
		Module module = DefinitionConverter.createModule(convCtx.module, convCtx, defaultModuleName);
		module.accept(new ASTNodeParentChecker());
		return module;
	}
	
	
	public static class ASTNodeParentChecker extends ASTNeoHomogenousVisitor {
		
		private ASTNeoNode parent = null;
		
		@Override
		public boolean preVisit(ASTNeoNode elem) {
			assertTrue(elem.getParent() == parent);
			parent = elem; // Set the current expected parent
			return true;
		}
		
		@Override
		public void postVisit(ASTNeoNode elem) {
			parent = elem.getParent(); // Restore previous parent
		}
		
	}
	
	public static ASTNeoNode convertElem(ASTNode elem, ASTConversionContext convContext) {
		if(elem == null) return null;
		return new DescentASTConverter(convContext).doConverElem(elem);
	}
	
	public ASTNeoNode doConverElem(ASTNode elem) {
		elem.accept(this);
		return this.ret;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends IASTNode> T convertElem(ASTNode elem, @SuppressWarnings("unused") Class<T> elemClass,
			ASTConversionContext convContext) {
		return (T) convertElem(elem, convContext);
	}
	
	public static ArrayView<ASTNeoNode> convertMany(Collection<? extends IASTNode> children, 
			ASTConversionContext convContext) {
		return convertMany(children, ASTNeoNode.class, convContext);
	}
	
	public static <T extends IASTNode> ArrayView<T> convertMany(Collection<? extends IASTNode> children,
			Class<T> elemClass, ASTConversionContext convContext) {
		if(children == null) return null;
		T[] rets = ArrayUtil.create(children.size(), elemClass);
		convertManyIntoArray(children, rets, convContext);
		return ArrayView.create(rets);
	}
	
	public static <T extends IASTNode> ArrayView<T> convertManyNoNulls(Collection<? extends IASTNode> children, 
			Class<T> elemClass, ASTConversionContext convContext) {
		if(children == null) return null;
		ArrayView<T> res = convertMany(children, elemClass, convContext);
		assertTrue(res.contains(null) == false);
		return res;
	}
	
	protected static <T extends IASTNode> T[] convertManyIntoArray(Iterable<? extends IASTNode> children, T[] rets, 
			ASTConversionContext convContext) {
		Iterator<? extends IASTNode> iterator = children.iterator();
		for(int i = 0; iterator.hasNext(); ++i) {
			ASTNode elem = (ASTNode) iterator.next();
			rets[i] = CoreUtil.blindCast(convertElem(elem, convContext));
		}
		return rets;
	}
	
}