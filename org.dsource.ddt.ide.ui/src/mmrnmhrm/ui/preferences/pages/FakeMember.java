package mmrnmhrm.ui.preferences.pages;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import java.io.InputStream;
import java.util.ArrayList;

import melnorme.utilbox.misc.ArrayUtil;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.core.CompletionRequestor;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelElementVisitor;
import org.eclipse.dltk.core.INamespace;
import org.eclipse.dltk.core.IOpenable;
import org.eclipse.dltk.core.IParameter;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptModel;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ITypeHierarchy;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.WorkingCopyOwner;
import org.eclipse.dltk.internal.core.SourceNamespace_Copy;

/**
 * Fake modelElements, used only for label preview purposes.
 */
public abstract class FakeMember implements IMember {
	
	protected IModelElement parent;
	protected int elementType;
	protected String elementName;
	protected int flags;
	protected INamespace nameSpace;
	protected final ArrayList<FakeMember> children = new ArrayList<FakeMember>();
	
	public FakeMember(IModelElement parent, int elementType, String elementName, int flags, String[] namespace) {
		this.parent = parent;
		this.elementType = elementType;
		this.elementName = elementName;
		this.flags = flags;
		this.nameSpace = new SourceNamespace_Copy(namespace);
		
		if(parent instanceof FakeMember) {
			((FakeMember) parent).children.add(this);
		}
	}
	
	@Override
	public int getElementType() {
		return elementType;
	}
	
	@Override
	public String getElementName() {
		return elementName;
	}
	
	@Override
	public IModelElement getParent() {
		return parent;
	}
	
	@Override
	public IScriptModel getModel() {
		return getScriptProject().getModel();
	}
	
	@Override
	public IScriptProject getScriptProject() {
		return (IScriptProject) getAncestor(IModelElement.SCRIPT_PROJECT);
	}
	
	@Override
	public boolean isReadOnly() {
		return true;
	}
	
	@Override
	public IModelElement getAncestor(int ancestorType) {
		IModelElement element = this;
		while (element != null) {
			if (element.getElementType() == ancestorType)
				return element;
			element = element.getParent();
		}
		return null;
	}
	
	@Override
	public IResource getResource() {
		return null;
	}
	
	@Override
	public IPath getPath() {
		throw assertFail();
	}
	
	@Override
	public boolean exists() {
		return true;
	}
	
	@Override
	public IOpenable getOpenable() {
		throw assertFail();
	}
	
	
	@Override
	public IResource getUnderlyingResource() throws ModelException {
		throw assertFail();
	}
	
	@Override
	public IResource getCorrespondingResource() throws ModelException {
		throw assertFail();
	}
	
	@Override
	public IModelElement getPrimaryElement() {
		throw assertFail();
	}
	
	@Override
	public String getHandleIdentifier() {
		throw assertFail();
	}
	
	@Override
	public boolean isStructureKnown() throws ModelException {
		return true;
	}
	
	@Override
	public void accept(IModelElementVisitor visitor) throws ModelException {
		visitor.visit(this);
	}
	
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		throw assertFail();
	}
	
	@Override
	public ISourceRange getSourceRange() throws ModelException {
		throw assertFail();
	}
	
	@Override
	public String getSource() throws ModelException {
		throw assertFail();
	}
	
	@Override
	public IModelElement[] getChildren() {
		return ArrayUtil.createFrom(children, IModelElement.class);
	}
	
	@Override
	public boolean hasChildren() {
		return !children.isEmpty();
	}
	
	@Override
	public int getFlags() throws ModelException {
		return flags;
	}
	
	@Override
	public INamespace getNamespace() throws ModelException {
		return nameSpace;
	}
	
	@Override
	public IType getDeclaringType() {
		IModelElement parentElement = getParent();
		if (parentElement.getElementType() == TYPE) {
			return (IType) parentElement;
		}
		return null;
	}
	
	@Override
	public ISourceModule getSourceModule() {
		return (ISourceModule) getAncestor(IModelElement.SOURCE_MODULE);
	}
	
	@Override
	public ISourceRange getNameRange() throws ModelException {
		throw assertFail();
	}
	
	@Override
	public IType getType(String name, int occurrenceCount) {
		throw assertFail();
	}
	
	public static class FakeType extends FakeMember implements IType {
		
		public FakeType(IModelElement parent, String elementName, int flags, String[] namespace) {
			super(parent, IModelElement.TYPE, elementName, flags, namespace);
		}
		
		@Override
		public String[] getSuperClasses() throws ModelException {
			throw assertFail();
		}
		
		@Override
		public IField getField(String name) {
			throw assertFail();
		}
		
		@Override
		public IField[] getFields() throws ModelException {
			throw assertFail();
		}
		
		@Override
		public IType getType(String name) {
			throw assertFail();
		}
		
		@Override
		public IType[] getTypes() throws ModelException {
			throw assertFail();
		}
		
		@Override
		public IMethod getMethod(String name) {
			throw assertFail();
		}
		
		@Override
		public IMethod[] getMethods() throws ModelException {
			throw assertFail();
		}
		
		@Override
		public String getFullyQualifiedName(String enclosingTypeSeparator) {
			throw assertFail();
		}
		
		@Override
		public String getFullyQualifiedName() {
			throw assertFail();
		}
		
		@Override
		public void codeComplete(char[] snippet, int insertion, int position, char[][] localVariableTypeNames,
				char[][] localVariableNames, int[] localVariableModifiers, boolean isStatic,
				CompletionRequestor requestor) throws ModelException {
			throw assertFail();
		}
		
		@Override
		public void codeComplete(char[] snippet, int insertion, int position, char[][] localVariableTypeNames,
				char[][] localVariableNames, int[] localVariableModifiers, boolean isStatic,
				CompletionRequestor requestor, WorkingCopyOwner owner) throws ModelException {
			throw assertFail();
		}
		
		@Override
		public IScriptFolder getScriptFolder() {
			throw assertFail();
		}
		
		@Override
		public String getTypeQualifiedName() {
			throw assertFail();
		}
		
		@Override
		public String getTypeQualifiedName(String enclosingTypeSeparator) {
			throw assertFail();
		}
		
		@Override
		public IMethod[] findMethods(IMethod method) {
			throw assertFail();
		}
		
		@Override
		public ITypeHierarchy loadTypeHierachy(InputStream input, IProgressMonitor monitor) throws ModelException {
			throw assertFail();
		}
		
		@Override
		public ITypeHierarchy newSupertypeHierarchy(IProgressMonitor monitor) throws ModelException {
			throw assertFail();
		}
		
		@Override
		public ITypeHierarchy newSupertypeHierarchy(ISourceModule[] workingCopies, IProgressMonitor monitor)
				throws ModelException {
			throw assertFail();
		}
		
		@Override
		public ITypeHierarchy newSupertypeHierarchy(WorkingCopyOwner owner, IProgressMonitor monitor)
				throws ModelException {
			throw assertFail();
		}
		
		@Override
		public ITypeHierarchy newTypeHierarchy(IScriptProject project, IProgressMonitor monitor) 
				throws ModelException {
			throw assertFail();
		}
		
		@Override
		public ITypeHierarchy newTypeHierarchy(IScriptProject project, WorkingCopyOwner owner, 
				IProgressMonitor monitor) throws ModelException {
			throw assertFail();
		}
		
		@Override
		public ITypeHierarchy newTypeHierarchy(IProgressMonitor monitor) throws ModelException {
			throw assertFail();
		}
		
		@Override
		public ITypeHierarchy newTypeHierarchy(ISourceModule[] workingCopies, IProgressMonitor monitor)
				throws ModelException {
			throw assertFail();
		}
		
		@Override
		public ITypeHierarchy newTypeHierarchy(WorkingCopyOwner owner, IProgressMonitor monitor) 
				throws ModelException {
			throw assertFail();
		}
		
	}
	
	public static class SourceMethodUtils {
		
		public static final IParameter[] NO_PARAMETERS = new IParameter[0];
		
		/**
		 * @param parameters
		 * @return
		 */
		public static String[] getParameterNames(IParameter[] parameters) {
			if (parameters.length == 0) {
				return CharOperation.NO_STRINGS;
			}
			final String[] names = new String[parameters.length];
			for (int i = 0, len = parameters.length; i < len; ++i) {
				names[i] = parameters[i].getName();
			}
			return names;
		}
		
	}
	
	public static class FakeMethod extends FakeMember implements IMethod {
		
		protected IParameter[] parameters;
		protected boolean isConstructor;
		protected String returnType;
		
		public FakeMethod(IModelElement parent, String elementName, int flags, String[] namespace,
				IParameter[] parameters, boolean isConstructor, String returnType) {
			super(parent, IModelElement.METHOD, elementName, flags, namespace);
			this.parameters = parameters;
			this.isConstructor = isConstructor;
			this.returnType = returnType;
		}
		
		@Override
		public IParameter[] getParameters() throws ModelException {
			return parameters;
		}
		
		@Override
		public String[] getParameterNames() throws ModelException {
			return SourceMethodUtils.getParameterNames(parameters);
		}
		
		@Override
		public boolean isConstructor() throws ModelException {
			return isConstructor;
		}
		
		@Override
		public String getType() throws ModelException {
			return returnType;
		}
		
		@Override
		public String getFullyQualifiedName(String enclosingTypeSeparator) {
			throw assertFail();
		}
		
		@Override
		public String getFullyQualifiedName() {
			throw assertFail();
		}
		
		@Override
		public String getTypeQualifiedName(String enclosingTypeSeparator, boolean showParameters) 
				throws ModelException {
			throw assertFail();
		}
		
	}
	
	public static class FakeField extends FakeMember implements IField {
		
		protected String type;
		
		public FakeField(IModelElement parent, String elementName, int flags, String[] namespace, String type) {
			super(parent, IModelElement.FIELD, elementName, flags, namespace);
			this.type = type;
		}
		
		@Override
		public String getType() throws ModelException {
			return type;
		}
		
		
		@Override
		public String getFullyQualifiedName(String enclosingTypeSeparator) {
			throw assertFail();
		}
		
		@Override
		public String getFullyQualifiedName() {
			throw assertFail();
		}
		
		@Override
		public String getTypeQualifiedName(String enclosingTypeSeparator, boolean showParameters) 
				throws ModelException {
			throw assertFail();
		}
		
	}
	
}
