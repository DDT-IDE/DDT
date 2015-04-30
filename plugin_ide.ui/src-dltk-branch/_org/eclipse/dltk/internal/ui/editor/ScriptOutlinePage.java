/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package _org.eclipse.dltk.internal.ui.editor;

import java.util.ArrayList;
import java.util.List;

import melnorme.lang.ide.ui.editor.structure.LangOutlinePage;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ElementChangedEvent;
import org.eclipse.dltk.core.IElementChangedListener;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelElementDelta;
import org.eclipse.dltk.core.IParent;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.ProblemsLabelDecorator.ProblemsLabelChangedEvent;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;

/**
 * The content outline page of the Java editor. The viewer implements a
 * proprietary update mechanism based on Java model deltas. It does not react on
 * domain changes. It is specified to show the content of ICompilationUnits and
 * IClassFiles. Publishes its context menu under
 * <code>JavaPlugin.getDefault().getPluginId() + ".outline"</code>.
 */
public class ScriptOutlinePage extends LangOutlinePage {
	
	/**
	 * Content provider for the children of an ICompilationUnit or an IClassFile
	 * 
	 * @see ITreeContentProvider
	 */
	protected class ChildrenProvider implements ITreeContentProvider {

		// private Object[] NO_CLASS = new Object[] { new NoClassElement() };
		private ElementChangedListener fListener;

		@Override
		public void dispose() {
			if (fListener != null) {
				DLTKCore.removeElementChangedListener(fListener);
				fListener = null;
			}
		}

		protected IModelElement[] filter(IModelElement[] children) {
			boolean initializers = false;
			for (int i = 0; i < children.length; i++) {
				if (matches(children[i])) {
					initializers = true;
					break;
				}
			}

			if (!initializers) {
				return children;
			}

			List<IModelElement> v = new ArrayList<IModelElement>();
			for (int i = 0; i < children.length; i++) {
				if (matches(children[i])) {
					continue;
				}
				v.add(children[i]);
			}
			return v.toArray(new IModelElement[v.size()]);
		}

		@Override
		public Object[] getChildren(Object parent) {
			if (parent instanceof IParent) {
				IParent c = (IParent) parent;
				try {
					return filter(c.getChildren());
				} catch (ModelException x) {
					// https://bugs.eclipse.org/bugs/show_bug.cgi?id=38341
					// don't log NotExist exceptions as this is a valid case
					// since we might have been posted and the element
					// removed in the meantime.
					if (DLTKCore.DEBUG || !x.isDoesNotExist()) {
						DLTKUIPlugin.log(x);
					}
				}
			}
			return ScriptOutlinePage.NO_CHILDREN;
		}

		@Override
		public Object[] getElements(Object parent) {
			return getChildren(parent);
		}

		@Override
		public Object getParent(Object child) {
			if (child instanceof IModelElement) {
				IModelElement e = (IModelElement) child;
				return e.getParent();
			}
			return null;
		}

		@Override
		public boolean hasChildren(Object parent) {
			if (parent instanceof IParent) {
				IParent c = (IParent) parent;
				try {
					IModelElement[] children = filter(c.getChildren());
					return (children != null && children.length > 0);
				} catch (ModelException x) {
					// https://bugs.eclipse.org/bugs/show_bug.cgi?id=38341
					// don't log NotExist exceptions as this is a valid case
					// since we might have been posted and the element
					// removed in the meantime.
					if (DLTKUIPlugin.isDebug() || !x.isDoesNotExist()) {
						DLTKUIPlugin.log(x);
					}
				}
			}
			return false;
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			boolean isCU = (newInput instanceof ISourceModule);

			if (isCU && fListener == null) {
				fListener = new ElementChangedListener();
				DLTKCore.addElementChangedListener(fListener);
			} else if (!isCU && fListener != null) {
				DLTKCore.removeElementChangedListener(fListener);
				fListener = null;
			}
		}

		protected boolean matches(IModelElement element) {
			if (element.getElementType() == IModelElement.METHOD) {
				String name = element.getElementName();
				return (name != null && name.indexOf('<') >= 0);
			}
			return false;
		}
	}

	/**
	 * The element change listener of the java outline viewer.
	 * 
	 * @see IElementChangedListener
	 */
	protected class ElementChangedListener implements IElementChangedListener {

		@Override
		public void elementChanged(final ElementChangedEvent e) {

			if (getControl() == null) {
				return;
			}

			Display d = getControl().getDisplay();
			if (d != null) {
				d.asyncExec(new Runnable() {
					@Override
					public void run() {
						ISourceModule cu = (ISourceModule) fInput;
						IModelElement base = cu;

						IModelElementDelta delta = findElement(base, e.getDelta());
						if (delta != null && getTreeViewer() != null) {
							getTreeViewer().refresh(true);
						}
					}
				});
			}
		}

		protected IModelElementDelta findElement(IModelElement unit,
				IModelElementDelta delta) {

			if (delta == null || unit == null) {
				return null;
			}

			IModelElement element = delta.getElement();

			if (unit.equals(element)) {
				if (isPossibleStructuralChange(delta)) {
					return delta;
				}
				return null;
			}

			if (element.getElementType() > IModelElement.SOURCE_MODULE) {
				return null;
			}

			IModelElementDelta[] children = delta.getAffectedChildren();
			if (children == null || children.length == 0) {
				return null;
			}

			for (int i = 0; i < children.length; i++) {
				IModelElementDelta d = findElement(unit, children[i]);
				if (d != null) {
					return d;
				}
			}

			return null;
		}

		private boolean isPossibleStructuralChange(IModelElementDelta cuDelta) {
			if (cuDelta.getKind() != IModelElementDelta.CHANGED) {
				return true; // add or remove
			}
			int flags = cuDelta.getFlags();
			if ((flags & IModelElementDelta.F_CHILDREN) != 0) {
				return true;
			}
			return (flags & (IModelElementDelta.F_CONTENT | IModelElementDelta.F_FINE_GRAINED)) == IModelElementDelta.F_CONTENT;
		}
	}

	/**
	 * The tree viewer used for displaying the outline.
	 * 
	 * @see TreeViewer
	 */
	protected class ScriptOutlineViewer extends TreeViewer {

		public ScriptOutlineViewer(Composite parent) {
			super(parent, SWT.MULTI);
			setAutoExpandLevel(ALL_LEVELS);
			setUseHashlookup(true);
		}

		private IResource getUnderlyingResource() {
			Object input = getInput();
			if (input instanceof ISourceModule) {
				ISourceModule cu = (ISourceModule) input;
				cu = cu.getPrimary();
				return cu.getResource();
			} /*
			 * else if (input instanceof IClassFile) { return ((IClassFile)
			 * input).getResource(); }
			 */
			return null;
		}

		@Override
		protected void handleLabelProviderChanged(LabelProviderChangedEvent event) {
			Object input = getInput();
			if (event instanceof ProblemsLabelChangedEvent) {
				ProblemsLabelChangedEvent e = (ProblemsLabelChangedEvent) event;
				if (e.isMarkerChange() && input instanceof ISourceModule) {
					return; // marker changes can be ignored
				}
			}
			// look if the underlying resource changed
			Object[] changed = event.getElements();
			if (changed != null) {
				IResource resource = getUnderlyingResource();
				if (resource != null) {
					for (int i = 0; i < changed.length; i++) {
						if (changed[i] != null && changed[i].equals(resource)) {
							// change event to a full refresh
							event = new LabelProviderChangedEvent((IBaseLabelProvider) event.getSource());
							break;
						}
					}
				}
			}
			super.handleLabelProviderChanged(event);
		}

	}
	
	static final Object[] NO_CHILDREN = new Object[0];

	protected final  ScriptEditor fEditor;
	
	private IModelElement fInput;
	protected IPreferenceStore fStore;


	/**
	 * @since 2.0
	 */
	public ScriptOutlinePage(ScriptEditor editor, IPreferenceStore store) {
		super(editor);

		Assert.isNotNull(editor);

		// fContextMenuID = "#CompilationUnitOutlinerContext";// contextMenuID;
		fEditor = editor;
		fStore = store;
	}
	
	@Override
	protected TreeViewer createTreeViewer(Composite parent) {
		return new ScriptOutlineViewer(parent);
	}
	
	@Override
	protected void customizeCreateControl() {
		// we must create the groups after we have set the selection provider to the site

		// register global actions
		IActionBars actionBars = getSite().getActionBars();
		registerSpecialToolbarActions(actionBars);
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}

	/* -----------------  ----------------- */
	
	@Override
	protected void treeViewerPostSelectionChanged(SelectionChangedEvent event) {
		fEditor.doSelectionChanged(event);
		
		super.treeViewerPostSelectionChanged(event);
	}
	
	protected void registerSpecialToolbarActions(@SuppressWarnings("unused") IActionBars actionBars) {
		// derived classes could implement it
	}

}
