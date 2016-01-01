/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.caret;

import org.eclipse.swt.events.TypedEvent;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.editors.tab.Caret;

/**
 * @author luffah
 *
 */
public class SelectAction extends Action{
	public static final String NAME = "action.caret.select";
	
	public SelectAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | DISABLE_ON_PLAYING | AUTO_UPDATE );
	}
	
	protected int execute(TypedEvent e){
		Caret caret = getEditor().getTablature().getCaret();
		
		if (caret.isSelectionClosed()){
				caret.unsetSelectionMark();
		} else if (caret.isSelectionOpenned()){
				caret.closeSelectionMark();
		} else {
			caret.openSelectionMark();
		}
		
		return 0;
	}
}
