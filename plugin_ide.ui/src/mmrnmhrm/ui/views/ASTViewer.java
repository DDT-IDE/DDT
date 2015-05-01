package mmrnmhrm.ui.views;

import java.nio.file.Path;

import melnorme.lang.ide.ui.editor.EditorUtils;
import melnorme.lang.ide.ui.editor.EditorUtils.OpenNewEditorMode;
import melnorme.lang.ide.ui.utils.WorkbenchUtils;
import melnorme.lang.tooling.ast.ASTNodeFinder;
import melnorme.lang.tooling.ast.IASTNode;
import mmrnmhrm.core.engine_client.DToolClient;
import mmrnmhrm.ui.DeeImages;
import mmrnmhrm.ui.DeeUIPlugin;
import mmrnmhrm.ui.actions.DeeOpenDefinitionOperation;
import mmrnmhrm.ui.editor.EditorUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;

import dtool.parser.DeeParserResult;


/**
 * D AST viewer
 */
@Deprecated
public class ASTViewer extends ViewPart implements ISelectionListener,
		ISelectionChangedListener, IDoubleClickListener {
	
	
	public static final String VIEW_ID = DeeUIPlugin.PLUGIN_ID + ".views.ASTViewer";
	
	private IWorkbenchWindow window;
	
	protected TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
	private Action actionExpand;
	private Action actionCollapse;
//	private Action actionToggle;
	
	//protected MultiListener fMultiListener;
	
	protected ITextEditor fEditor;
	protected IDocument fDocument;
	protected Path inputFilePath;
	protected DeeParserResult fDeeModule;
	protected IASTNode selNode;
	
	
	public ASTViewer() {
	}
	
	
	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		
		window = site.getWorkbenchWindow();
		window.getSelectionService().addPostSelectionListener(this);
		//site.getPage().addPartListener(this);
		
	}
	
	@Override
	public void dispose() {
		if (fDocument != null) {
			fDocument.removeDocumentListener(documentListener);
		}
		window.getSelectionService().removePostSelectionListener(this);
		super.dispose();
	}
	
	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(viewer);
		viewer.setContentProvider(new ASTViewerContentProvider(this));
		viewer.setLabelProvider(new ASTViewerLabelProvider(this));
		viewer.addSelectionChangedListener(this);
		viewer.addDoubleClickListener(this);
		
		makeActions();
		contributeToActionBars();
		hookContextMenu();
		
		viewer.setInput(this);
		selectionChanged(WorkbenchUtils.getActiveEditor(), null);
	}
	
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		IWorkbenchPage page = getSite().getWorkbenchWindow().getActivePage();
		if(page == null)
			return;
		
		if(part instanceof ITextEditor) {
			setInput((ITextEditor)part);
		} else if(part == null || page.getActiveEditor() == null){ 
			setInput(null);
		}
	}
	
	
	public void setInput(ITextEditor editor) {
		if(editor == fEditor) {
			return;
		}
		
		if (fEditor != null && fDocument != null) {
			fDocument.removeDocumentListener(documentListener);
		}
		
		fEditor = null;
		fDocument = null;
		
		if (editor == null) {
			setContentDescription("No Editor available");
			viewer.getControl().setVisible(false);
		} else {
			fEditor = editor;
			
			inputFilePath = EditorUtils.getFilePathFromEditorInput(fEditor.getEditorInput());
			
			if(inputFilePath != null) {
				fDocument = fEditor.getDocumentProvider().getDocument(editor.getEditorInput());
				if(fDocument != null) {
					fDocument.addDocumentListener(documentListener);
				}
			} 
			refreshViewer();
		}
		
	}
	
	protected final IDocumentListener documentListener = new IDocumentListener() {
		
		@Override
		public void documentChanged(DocumentEvent event) {
		}
		
		@Override
		public void documentAboutToBeChanged(DocumentEvent event) {
			viewerUpdateJob.schedule(500);
		}
	};
	
	protected final UIJob viewerUpdateJob = new UIJob("ASTViewer.refresh") {
		{ setSystem(true); }
		
		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {
			if(!viewer.getTree().isDisposed()) {
				refreshViewer();
			}
			
			return Status.OK_STATUS;
		}
	};

	protected void refreshViewer() {
		if(fEditor == null || inputFilePath == null || fEditor.getDocumentProvider() == null) {
			setContentDescription("No Editor or SourceModule available");
			viewer.getControl().setVisible(false);
			return;
		}
		
		fDeeModule = DToolClient.getDefault().getClientModuleCache().getExistingParsedModule(inputFilePath);
		if(fDeeModule == null) {
			setContentDescription("No DeeModuleUnit available");
			viewer.getControl().setVisible(false);
			return;
		} 
		
		makeVisible(viewer.getControl());
		
		int offset = EditorUtils.getSelection(fEditor).getOffset();
		setContentDescription("AST, sel: " + offset);
		
		//viewer.getControl().setRedraw(false);
		viewer.refresh();
		if(offset <= fDeeModule.source.length()) {
			selNode = ASTNodeFinder.findElement(fDeeModule.getModuleNode(), offset);
		}
		if(selNode != null) {
			viewer.reveal(selNode);
		}
		//viewer.getControl().setRedraw(true);
	}
	
	public static void makeVisible(Control control) {
		if(!control.isVisible()) {
			control.setVisible(true);
		}
	}
	
	
	
	/** Passing the focus request to the viewer's control. */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	/* ================== Action construction ==================== */
	
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#DeeASTViewerContext");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				ASTViewer.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu("#DeeASTViewerContext", menuMgr, viewer);
	}
	
	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(actionCollapse);
		manager.add(actionExpand);
//		manager.add(actionToggle);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
	}
	
	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(actionCollapse);
		manager.add(actionExpand);
		manager.add(new Separator());
	}
	
	private void fillContextMenu(IMenuManager manager) {
		manager.add(actionCollapse);
		manager.add(actionExpand);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	
	private void makeActions() {
		actionExpand = new Action() {
			@Override
			public void run() {
				viewer.expandAll();
			}
		};
		actionExpand.setText("Expand All");
		actionExpand.setToolTipText("Expand all nodes");
		actionExpand.setImageDescriptor(DeeImages.EXPAND_ALL);
		
		actionCollapse = new Action() {
			@Override
			public void run() {
				viewer.collapseAll();
			}
		};
		actionCollapse.setText("Collapse All");
		actionCollapse.setToolTipText("Collapse All nodes");
		actionCollapse.setImageDescriptor(DeeImages.COLLAPSE_ALL);
		
//		actionToggle = new Action() {
//			@Override
//			public void run() {
//				fUseOldAst  = !fUseOldAst; refreshViewer();
//			}
//		};
//		actionToggle.setText("Toggle Neo/Old AST");
//		actionToggle.setToolTipText("Toggle Neo/Old AST");
//		actionToggle.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
//				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
	}
	
	
	@Override
	public void doubleClick(DoubleClickEvent event) {
		new DeeOpenDefinitionOperation(fEditor, OpenNewEditorMode.NEVER).executeAndHandleResult();
	}
	
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		if(fEditor == null)
			return;
		EditorUtil.selectNodeInEditor((AbstractTextEditor)fEditor, event);
	}
	
}