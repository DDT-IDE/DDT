package mmrnmhrm.core.search;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;
import java.util.LinkedList;

import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.StringUtil;

import org.dsource.ddt.ide.core.model.DeeModuleParsingUtil;
import org.dsource.ddt.ide.core.model.DeeModuleDeclaration;
import org.dsource.ddt.ide.core.model.engine.DeeModelEngine;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IParent;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;

import dtool.ast.ASTNeoHomogenousVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.definitions.Module;

public class DeeSearchEngineTestUtils {
	
	public static String getSourceModuleFQName(ISourceModule sourceModule) {
		String moduleFileName = sourceModule.getElementName();
		int fileExtensionIx = moduleFileName.indexOf('.');
		if(fileExtensionIx != -1) {
			moduleFileName = moduleFileName.substring(0, fileExtensionIx);
		}
		String packageName = DeeModelEngine.getPackageName(sourceModule);
		return packageName.isEmpty() ? moduleFileName : packageName + "." + moduleFileName;
	}
	
	public static String[] getModelElementFQNameArray(ISourceModule sourceModule) {
		String fqName = getSourceModuleFQName(sourceModule);
		if(fqName.isEmpty()) {
			return DeeModelEngine.EMPTY_STRINGS;
		} else {
			return fqName.split("\\.");
		}
	}
	
	public static String getModelElementFQName(IMember element) {
		switch (element.getElementType()) {
		case IModelElement.FIELD:
		case IModelElement.METHOD:
		case IModelElement.TYPE:
		case IModelElement.LOCAL_VARIABLE:
			
			String parentFQName;
			if(element.getParent() instanceof IMember) {
				parentFQName = getModelElementFQName((IMember) element.getParent());
			} else {
				assertTrue(element.getParent() == element.getSourceModule());
				parentFQName = DeeModelEngine.getPackageName(element.getSourceModule());
			}
			
			String qualification = "";
			if(!parentFQName.isEmpty()) {
				qualification = parentFQName + ".";
			}
			return qualification + element.getElementName();
		default:
			throw assertFail();
		}
	}
	
	public static ISourceModule getSourceModule(IModelElement element) {
		return (ISourceModule) element.getAncestor(IModelElement.SOURCE_MODULE);
	}
	
	public static ArrayList<Integer> getNodeTreePath(ASTNeoNode node) {

		ASTNeoNode parent = node.getParent();
		if(parent == null) {
			return new ArrayList<Integer>();
		}
		
		ArrayList<Integer> parentPath = getNodeTreePath(parent);
		
		ASTNeoNode[] children = parent.getChildren();
		for (int ix = 0; ix < children.length; ix++) {
			ASTNeoNode child = children[ix];
			if(node == child) {
				parentPath.add(ix);
				assertTrue(getNodeFromPath(node.getModuleNode(), parentPath) == node);
				return parentPath;
			}
		}
		throw assertFail();
	}
	
	public static ASTNeoNode getNodeFromPath(ASTNeoNode node, ArrayList<Integer> nodeTreePath) {
		return getNodeFromPath(node, nodeTreePath, 0);
	}
	
	private static ASTNeoNode getNodeFromPath(ASTNeoNode node, ArrayList<Integer> nodeTreePath, int treePathIx) {
		if(nodeTreePath.size() == treePathIx) {
			return node;
		}
		int ix = nodeTreePath.get(treePathIx);
		return getNodeFromPath(node.getChildren()[ix], nodeTreePath, treePathIx+1);
	}
	
	public static class ElementsAndDefUnitVisitor { 
		
		public void visitElementsAndNodes(IModelElement element, int depth) throws ModelException, CoreException {
			if(element instanceof ISourceModule) {
				final ISourceModule sourceModule = (ISourceModule) element;
				DeeModuleDeclaration moduleDec = DeeModuleParsingUtil.getParsedDeeModule(sourceModule);
				moduleDec.neoModule.accept(new ASTNeoHomogenousVisitor() {
					@Override
					public boolean preVisit(ASTNeoNode node) {
						visitNode(node, sourceModule);
						return true;
					}
				});
			}
			
			if(element instanceof IMember) {
				visitMember((IMember) element);
			}
			
			if(depth > 0 && element instanceof IParent) {
				if(element instanceof IProjectFragment && ((IProjectFragment) element).isExternal()) {
					return; // We do this to ignore standard library entry
				}
				IModelElement[] children = ((IParent) element).getChildren();
				for (IModelElement child : children) {
					visitElementsAndNodes(child, depth - 1);
				}
			}
		}
		
		@SuppressWarnings("unused")
		protected void visitNode(ASTNeoNode node, ISourceModule sourceModule) {
		}
		
		@SuppressWarnings("unused")
		protected void visitMember(IMember element) throws CoreException {
		}
	}
	
	public static String[] getModuleFQName(Module moduleNode) {
		LinkedList<String> qualification = DeeModelEngine.getQualificationList(moduleNode);
		qualification.add(moduleNode.getName());
		return ArrayUtil.createFrom(qualification, String.class);
	}
	
}
