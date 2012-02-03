package mmrnmhrm.ui.views;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import mmrnmhrm.lang.ui.EditorUtil;
import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.DeePluginImages;
import mmrnmhrm.ui.actions.GoToDefinitionHandler;

import org.dsource.ddt.ide.core.model.DeeModuleDeclaration;
import org.dsource.ddt.ide.core.model.DeeModuleParsingUtil;
import org.dsource.ddt.ide.core.model.DeeModuleDeclaration.EModelStatus;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
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
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;

import descent.internal.compiler.parser.ast.IASTNode;
import dtool.ast.ASTNodeFinder;


/**
 * D AST viewer
 */
public class ASTViewer extends ViewPart implements ISelectionListener,
		IDocumentListener, ISelectionChangedListener, IDoubleClickListener {
	
	
	public static final String VIEW_ID = DeePlugin.EXTENSIONS_IDPREFIX+"views.ASTViewer";
	
	private IWorkbenchWindow window;
	
	protected TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
	private Action actionExpand;
	private Action actionCollapse;
	private Action actionToggle;
	
	//protected MultiListener fMultiListener;
	
	protected ITextEditor fEditor;
	protected IDocument fDocument;
	protected ISourceModule fSourceModule;
	protected DeeModuleDeclaration fDeeModule;
	protected boolean fUseOldAst = false;
	protected IASTNode selNode;
	
	
	public ASTViewer() {
	}
	
	
	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		
		window = site.getWorkbenchWindow();
		window.getSelectionService().addPostSelectionListener(this);
		//site.getPage().addPartListener(this);
		
		/*if (fMultiListener == null) {
			fMultiListener = this.new MultiListener();
			
			ISelectionService service= site.getWorkbenchWindow().getSelectionService();
			service.addPostSelectionListener(fMultiListener);
			site.getPage().addPartListener(fMultiListener);
			//FileBuffers.getTextFileBufferManager().addFileBufferListener(fSuperListener);
		}*/
	}
	
	@Override
	public void dispose() {
		if (fEditor != null && fDocument != null) {
			fDocument.removeDocumentListener(this);
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
		selectionChanged(EditorUtil.getActiveEditor(), null);
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
	
	@Override
	public void documentAboutToBeChanged(DocumentEvent event) {
		// Do nothing
	}
	
	@Override
	public void documentChanged(DocumentEvent event) {
		refreshViewer();
	}
	
	public void setInput(ITextEditor editor) {
		if (fEditor != null && fDocument != null) {
			fDocument.removeDocumentListener(this);
		}
		
		fEditor = null;
		fDocument = null;
		
		if (editor == null) {
			setContentDescription("No Editor available");
			viewer.getControl().setVisible(false);
		} else {
			fEditor = editor;
			
			fSourceModule = EditorUtil.getModuleUnit(fEditor);
			if(fSourceModule != null) {
				fDocument = fEditor.getDocumentProvider().getDocument(editor.getEditorInput());
				fDocument.addDocumentListener(this);
			} 
			refreshViewer();
		}
		
	}
	
	
	private void refreshViewer() {
		if(fEditor == null || fSourceModule == null) {
			setContentDescription("No Editor or SourceModule available");
			viewer.getControl().setVisible(false);
			return;
		}
		
		fDeeModule = DeeModuleParsingUtil.getParsedDeeModule(fSourceModule);
		if(fDeeModule == null) {
			setContentDescription("No DeeModuleUnit available");
			viewer.getControl().setVisible(false);
			return;
		} 
		
		makeVisible(viewer.getControl());
		
		int offset = EditorUtil.getSelection(fEditor).getOffset();
		setContentDescription("AST ("+toStringParseStatus(fDeeModule.getParseStatus())+"), sel: " + offset);
		
		//viewer.getControl().setRedraw(false);
		viewer.refresh();
		selNode = ASTNodeFinder.findElementDependingOnASTType(fDeeModule.getEffectiveModuleNode(), offset);
		if(selNode != null) {
			viewer.reveal(selNode);
		}
		//viewer.getControl().setRedraw(true);
	}
	
	public static String toStringParseStatus(int parseStatus) {
		switch(parseStatus) {
		case EModelStatus.PARSER_INTERNAL_ERROR: return "Internal Error";
		case EModelStatus.PARSER_SYNTAX_ERRORS: return "Syntax Errors";
		case EModelStatus.OK: return "OK";
		default: assertFail(); return null;
		}
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
		manager.add(actionToggle);
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
		ASTViewer.setupActionImages(actionExpand, "expandall.gif");
		
		actionCollapse = new Action() {
			@Override
			public void run() {
				viewer.collapseAll();
			}
		};
		actionCollapse.setText("Collapse All");
		actionCollapse.setToolTipText("Collapse All nodes");
		ASTViewer.setupActionImages(actionCollapse, "collapseall.gif");
		
		actionToggle = new Action() {
			@Override
			public void run() {
				fUseOldAst  = !fUseOldAst; refreshViewer();
			}
		};
		actionToggle.setText("Toggle Neo/Old AST");
		actionToggle.setToolTipText("Toggle Neo/Old AST");
		actionToggle.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
	}
	
	
	@Override
	public void doubleClick(DoubleClickEvent event) {
		GoToDefinitionHandler.executeChecked((AbstractTextEditor)fEditor, true);
	}
	
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		if(fEditor == null)
			return;
		EditorUtil.selectNodeInEditor((AbstractTextEditor)fEditor, event);
	}


	public static void setupActionImages(IAction action, String file) {
		ImageDescriptor imgDesc = DeePluginImages.getActionImageDescriptor(file, true); 
		action.setImageDescriptor(imgDesc);
	}
	
}