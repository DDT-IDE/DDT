package dtool.descentadapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import descent.internal.compiler.parser.ast.ASTNode;
import descent.internal.compiler.parser.ast.IASTNode;
import dtool.ast.ASTNeoNode;
import dtool.ast.definitions.Module;

public class DescentASTConverter extends StatementConverter {

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
		ASTConversionContext convContext = new ASTConversionContext((descent.internal.compiler.parser.Module) cumodule);
		Module module = Module.createModule(convContext.module, convContext);
		module.accept(new PostConvertionAdapter());
		return module;
	}
	
	public static ASTNeoNode convertElem(ASTNode elem, ASTConversionContext convContext) {
		if(elem == null) return null;
		DescentASTConverter conv = new DescentASTConverter(convContext);
		elem.accept(conv);
		return conv.ret;
	}
	
	public static ASTNeoNode[] convertMany(Collection<? extends IASTNode> children
			, ASTConversionContext convContext) {
		if(children == null) return null;
		ASTNeoNode[] rets = new ASTNeoNode[children.size()];
		convertMany(children.toArray(), rets, convContext);
		return rets;
	}
	
	public static void convertMany(List<? extends IASTNode> children, ASTNeoNode[] rets
			, ASTConversionContext convContext) {
		if(children == null) return;
		convertMany(children.toArray(), rets, convContext);
		return;
	}
	
	
	@SuppressWarnings("unchecked")
	public static <T extends IASTNode> T[] convertMany(Object[] children, T[] rets, ASTConversionContext convContext) {
		DescentASTConverter conv = new DescentASTConverter(convContext);
		for(int i = 0; i < children.length; ++i) {
			ASTNode elem = (ASTNode) children[i];
			if(elem == null) {
				rets[i] = null;
			} else {
				elem.accept(conv);
				rets[i] = (T) conv.ret;
			}
		}
		return rets;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends IASTNode> ArrayList<T> convertManyL(List<? extends ASTNode> children, 
			@SuppressWarnings("unused")	List<T> dummy, ASTConversionContext convContext) {
		DescentASTConverter conv = new DescentASTConverter(convContext);
		if(children == null)
			return null;
		ArrayList<T> rets = new ArrayList<T>(children.size());
		for (int i = 0; i < children.size(); ++i) {
			ASTNode elem = children.get(i);
			if(elem == null) {
				rets.add(null);
			} else {
				elem.accept(conv);
				rets.add((T) conv.ret);
			}
		}
		return rets;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends IASTNode> List<T> convertManyL(ASTNode[] children, 
			@SuppressWarnings("unused") List<T> dummy, ASTConversionContext convContext) {
		DescentASTConverter conv = new DescentASTConverter(convContext);
		List<T> rets = new ArrayList<T>(children.length);
		for (int i = 0; i < children.length; ++i) {
			ASTNode elem = children[i];
			if(elem == null) {
				rets.add(null);
			} else {
				elem.accept(conv);
				rets.add((T) conv.ret);
			}
		}
		return rets;
	}

}
