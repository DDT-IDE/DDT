/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package _org.eclipse.dltk.ui.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.ISourceReference;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.core.SourceRange;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.IUpdate;

import _org.eclipse.dltk.internal.ui.editor.ScriptEditor2;


public class GoToNextPreviousMemberAction extends Action implements IUpdate {

	public static final String NEXT_MEMBER= "GoToNextMember"; //$NON-NLS-1$
	public static final String PREVIOUS_MEMBER= "GoToPreviousMember"; //$NON-NLS-1$
	private ScriptEditor2 fEditor;
	private boolean fIsGotoNext;

	public static GoToNextPreviousMemberAction newGoToNextMemberAction(ScriptEditor2 editor) {
		String text= "N&ext Member";
		return new GoToNextPreviousMemberAction(editor, text, true);
	}

	public static GoToNextPreviousMemberAction newGoToPreviousMemberAction(ScriptEditor2 editor) {
		String text= "Previ&ous Member";
		return new GoToNextPreviousMemberAction(editor, text, false);
	}

	private GoToNextPreviousMemberAction(ScriptEditor2 editor, String text, boolean isGotoNext) {
		super(text);
		fEditor= editor;
		fIsGotoNext= isGotoNext;
		update();
//		if (isGotoNext)
//			PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.GOTO_NEXT_MEMBER_ACTION);
//		else
//			PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.GOTO_PREVIOUS_MEMBER_ACTION);
	}

	/*
	 * This constructor is for testing purpose only.
	 */
	public GoToNextPreviousMemberAction(boolean isSelectNext) {
		super(""); //$NON-NLS-1$
		fIsGotoNext= isSelectNext;
	}

	@Override
	public void update() {
		boolean enabled= false;
		ISourceReference ref= getSourceReference();
		if (ref != null) {
			ISourceRange range;
			try {
				range= ref.getSourceRange();
				enabled= range != null && range.getLength() > 0;
			} catch (ModelException e) {
				// enabled= false;
			}
		}
		setEnabled(enabled);
	}

	/* (non-JavaDoc)
	 * Method declared in IAction.
	 */
	@Override
	public final  void run() {
		ITextSelection selection= getTextSelection();
		ISourceRange newRange= getNewSelectionRange(createSourceRange(selection), null);
		// Check if new selection differs from current selection
		if (selection.getOffset() == newRange.getOffset() && selection.getLength() == newRange.getLength())
			return;
		fEditor.selectAndReveal(newRange.getOffset(), newRange.getLength());
	}

	private IType[] getTypes() throws ModelException {
		IEditorInput input= fEditor.getEditorInput();		
		return DLTKUIPlugin.getDefault().getWorkingCopyManager().getWorkingCopy(input).getTypes();
	}

	private ISourceReference getSourceReference() {
		IEditorInput input= fEditor.getEditorInput();		
		return DLTKUIPlugin.getDefault().getWorkingCopyManager().getWorkingCopy(input);		
	}

	private ITextSelection getTextSelection() {
		return (ITextSelection)fEditor.getSelectionProvider().getSelection();
	}

	public ISourceRange getNewSelectionRange(ISourceRange oldSourceRange, IType[] types) {
		try{
			if (types == null)
				types= getTypes();
			Integer[] offsetArray= createOffsetArray(types);
			if (offsetArray.length == 0)
				return oldSourceRange;
			Arrays.sort(offsetArray);
			Integer oldOffset= new Integer(oldSourceRange.getOffset());
			int index= Arrays.binarySearch(offsetArray, oldOffset);

			if (fIsGotoNext)
				return createNewSourceRange(getNextOffset(index, offsetArray, oldOffset));
			else
				return createNewSourceRange(getPreviousOffset(index, offsetArray, oldOffset));

	 	}	catch (ModelException e){
	 		DLTKUIPlugin.log(e); //dialog would be too heavy here
	 		return oldSourceRange;
	 	}
	}

	private static Integer getPreviousOffset(int index, Integer[] offsetArray, Integer oldOffset) {
		if (index == -1)
			return oldOffset;
		if (index == 0)
			return offsetArray[0];
		if (index > 0)
			return offsetArray[index - 1];
		Assert.isTrue(index < -1);
		int absIndex= Math.abs(index);
		return offsetArray[absIndex - 2];
	}

	private static Integer getNextOffset(int index, Integer[] offsetArray, Integer oldOffset) {
		if (index == -1)
			return offsetArray[0];

		if (index == 0){
			if (offsetArray.length != 1)
				return offsetArray[1];
			else
				return offsetArray[0];
		}
		if (index > 0){
			if (index == offsetArray.length - 1)
				return oldOffset;
			return offsetArray[index + 1];
		}
		Assert.isTrue(index < -1);
		int absIndex= Math.abs(index);
		if (absIndex > offsetArray.length)
			return oldOffset;
		else
			return offsetArray[absIndex - 1];
	}

	private static ISourceRange createNewSourceRange(Integer offset){
		return new SourceRange(offset.intValue(), 0);
	}

	private static Integer[] createOffsetArray(IType[] types) throws ModelException {
		List result= new ArrayList();
		for (int i= 0; i < types.length; i++) {
			IType iType= types[i];
			addOffset(result, iType.getNameRange().getOffset());
			addOffset(result, iType.getSourceRange().getOffset() + iType.getSourceRange().getLength());
			addMemberOffsetList(result, iType.getMethods());
			addMemberOffsetList(result, iType.getFields());
			//addMemberOffsetList(result, iType.getInitializers());
		}
		return (Integer[]) result.toArray(new Integer[result.size()]);
	}

	private static void addMemberOffsetList(List result, IMember[] members) throws ModelException {
		for (int i= 0; i < members.length; i++) {
			addOffset(result, getOffset(members[i]));
		}
	}

	private static int getOffset(IMember iMember) throws ModelException {
		//special case
//		if (iMember.getElementType() == IModelElement.INITIALIZER)
//			return firstOpeningBraceOffset((IInitializer)iMember);

		if (iMember.getNameRange() != null && iMember.getNameRange().getOffset() >= 0)
			return iMember.getNameRange().getOffset();
		return iMember.getSourceRange().getOffset();
	}

	//-- private helper methods

	private static ISourceRange createSourceRange(ITextSelection ts) {
		return new SourceRange(ts.getOffset(), ts.getLength());
	}

	private static void addOffset(List result, int offset) {
		if (offset >= 0)
			result.add(new Integer(offset));
	}
}
