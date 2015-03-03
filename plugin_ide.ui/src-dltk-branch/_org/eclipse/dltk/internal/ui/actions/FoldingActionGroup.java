/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package _org.eclipse.dltk.internal.ui.actions;

import java.util.ResourceBundle;

import org.eclipse.dltk.internal.ui.actions.FoldingMessages;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.actions.IScriptEditorActionDefinitionIds;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.source.projection.IProjectionListener;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.editors.text.IFoldingCommandIds;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.IUpdate;
import org.eclipse.ui.texteditor.ResourceAction;
import org.eclipse.ui.texteditor.TextOperationAction;

/**
 * Groups the folding actions.
 * 
 * 
 */
public class FoldingActionGroup extends ActionGroup {
	private static abstract class PreferenceAction extends ResourceAction
			implements IUpdate {
		PreferenceAction(ResourceBundle bundle, String prefix, int style) {
			super(bundle, prefix, style);
		}
	}

	private class FoldingAction extends PreferenceAction {

		FoldingAction(ResourceBundle bundle, String prefix) {
			super(bundle, prefix, IAction.AS_PUSH_BUTTON);
		}

		@Override
		public void update() {
			setEnabled(FoldingActionGroup.this.isEnabled()
					&& fViewer.isProjectionMode());
		}
	}

	private ProjectionViewer fViewer;

	private final PreferenceAction fToggle;
	private final TextOperationAction fExpand;
	private final TextOperationAction fCollapse;
	private final TextOperationAction fExpandAll;
	private final IProjectionListener fProjectionListener;

	private final PreferenceAction fRestoreDefaults;
	private final FoldingAction fCollapseMembers;
	private final FoldingAction fCollapseComments;
	private final TextOperationAction fCollapseAll;

	private IPreferenceStore fStore;

	/**
	 * Creates a new projection action group for <code>editor</code>. If the
	 * supplied viewer is not an instance of <code>ProjectionViewer</code>,
	 * the action group is disabled.
	 * 
	 * @param editor
	 *            the text editor to operate on
	 * @param viewer
	 *            the viewer of the editor
	 */
	public FoldingActionGroup(final ITextEditor editor, ITextViewer viewer,
			IPreferenceStore store) {
		if (!(viewer instanceof ProjectionViewer)) {
			fToggle = null;
			fExpand = null;
			fCollapse = null;
			fExpandAll = null;
			fCollapseAll = null;
			fRestoreDefaults = null;
			fCollapseMembers = null;
			fCollapseComments = null;
			fProjectionListener = null;
			return;
		}

		fViewer = (ProjectionViewer) viewer;
		fStore = store;

		fProjectionListener = new IProjectionListener() {

			@Override
			public void projectionEnabled() {
				update();
			}

			@Override
			public void projectionDisabled() {
				update();
			}
		};

		fViewer.addProjectionListener(fProjectionListener);

		fToggle = new PreferenceAction(FoldingMessages.getResourceBundle(),
				"Projection.Toggle.", IAction.AS_CHECK_BOX) { //$NON-NLS-1$
			@Override
			public void run() {
				boolean current = fStore
						.getBoolean(PreferenceConstants.EDITOR_FOLDING_ENABLED);
				fStore.setValue(PreferenceConstants.EDITOR_FOLDING_ENABLED,
						!current);
			}

			@Override
			public void update() {
				ITextOperationTarget target = (ITextOperationTarget) editor
						.getAdapter(ITextOperationTarget.class);

				boolean isEnabled = (target != null && target
						.canDoOperation(ProjectionViewer.TOGGLE));
				setEnabled(isEnabled);
			}
		};
		fToggle.setChecked(true);
		fToggle.setActionDefinitionId(IFoldingCommandIds.FOLDING_TOGGLE);
		editor.setAction("FoldingToggle", fToggle); //$NON-NLS-1$

		fExpandAll = new TextOperationAction(
				FoldingMessages.getResourceBundle(),
				"Projection.ExpandAll.", editor, ProjectionViewer.EXPAND_ALL, true); //$NON-NLS-1$
		fExpandAll.setActionDefinitionId(IFoldingCommandIds.FOLDING_EXPAND_ALL);
		editor.setAction("FoldingExpandAll", fExpandAll); //$NON-NLS-1$

		fCollapseAll = new TextOperationAction(
				FoldingMessages.getResourceBundle(),
				"Projection.CollapseAll.", editor, ProjectionViewer.COLLAPSE_ALL, true); //$NON-NLS-1$
		fCollapseAll
				.setActionDefinitionId(IFoldingCommandIds.FOLDING_COLLAPSE_ALL);
		editor.setAction("FoldingCollapseAll", fCollapseAll); //$NON-NLS-1$

		fExpand = new TextOperationAction(FoldingMessages.getResourceBundle(),
				"Projection.Expand.", editor, ProjectionViewer.EXPAND, true); //$NON-NLS-1$
		fExpand.setActionDefinitionId(IFoldingCommandIds.FOLDING_EXPAND);
		editor.setAction("FoldingExpand", fExpand); //$NON-NLS-1$

		fCollapse = new TextOperationAction(
				FoldingMessages.getResourceBundle(),
				"Projection.Collapse.", editor, ProjectionViewer.COLLAPSE, true); //$NON-NLS-1$
		fCollapse.setActionDefinitionId(IFoldingCommandIds.FOLDING_COLLAPSE);
		editor.setAction("FoldingCollapse", fCollapse); //$NON-NLS-1$

		fRestoreDefaults = new FoldingAction(FoldingMessages
				.getResourceBundle(), "Projection.Restore.") { //$NON-NLS-1$
			@Override
			public void run() {
				if (editor instanceof ScriptEditor) {
					ScriptEditor javaEditor = (ScriptEditor) editor;
					javaEditor.resetProjection();
				}
			}
		};
		fRestoreDefaults
				.setActionDefinitionId(IFoldingCommandIds.FOLDING_RESTORE);
		editor.setAction("FoldingRestore", fRestoreDefaults); //$NON-NLS-1$

		fCollapseMembers = new FoldingAction(FoldingMessages
				.getResourceBundle(), "Projection.CollapseMembers.") { //$NON-NLS-1$
			@Override
			public void run() {
				if (editor instanceof ScriptEditor) {
					ScriptEditor javaEditor = (ScriptEditor) editor;
					javaEditor.collapseMembers();
				}
			}
		};
		fCollapseMembers
				.setActionDefinitionId(IScriptEditorActionDefinitionIds.FOLDING_COLLAPSE_MEMBERS);
		editor.setAction("FoldingCollapseMembers", fCollapseMembers); //$NON-NLS-1$

		fCollapseComments = new FoldingAction(FoldingMessages
				.getResourceBundle(), "Projection.CollapseComments.") { //$NON-NLS-1$
			@Override
			public void run() {
				if (editor instanceof ScriptEditor) {
					ScriptEditor javaEditor = (ScriptEditor) editor;
					javaEditor.collapseComments();
				}
			}
		};
		fCollapseComments
				.setActionDefinitionId(IScriptEditorActionDefinitionIds.FOLDING_COLLAPSE_COMMENTS);
		editor.setAction("FoldingCollapseComments", fCollapseComments); //$NON-NLS-1$
	}

	/**
	 * Returns <code>true</code> if the group is enabled.
	 * 
	 * <pre>
	 *   Invariant: isEnabled() &lt;=&gt; fViewer and all actions are != null.
	 * </pre>
	 * 
	 * @return <code>true</code> if the group is enabled
	 */
	protected boolean isEnabled() {
		return fViewer != null;
	}

	@Override
	public void dispose() {
		if (isEnabled()) {
			fViewer.removeProjectionListener(fProjectionListener);
			fViewer = null;
		}
		super.dispose();
	}

	/**
	 * Updates the actions.
	 */
	protected void update() {
		if (isEnabled()) {
			fToggle.update();
			fToggle.setChecked(fViewer.isProjectionMode());
			fExpand.update();
			fExpandAll.update();
			fCollapse.update();
			fCollapseAll.update();
			fRestoreDefaults.update();
			fCollapseMembers.update();
			fCollapseComments.update();
		}
	}

	/**
	 * Fills the menu with all folding actions.
	 * 
	 * @param manager
	 *            the menu manager for the folding submenu
	 */
	public void fillMenu(IMenuManager manager) {
		if (isEnabled()) {
			update();
			manager.add(fToggle);
			manager.add(fExpandAll);
			manager.add(fExpand);
			manager.add(fCollapse);
			manager.add(fCollapseAll);
			manager.add(fRestoreDefaults);
			manager.add(fCollapseMembers);
			manager.add(fCollapseComments);
		}
	}

	@Override
	public void updateActionBars() {
		update();
	}
}
