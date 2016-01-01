/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.effects;

import java.util.Iterator;

import org.eclipse.swt.events.TypedEvent;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.editors.tab.Caret;
import org.herac.tuxguitar.gui.undo.undoables.measure.UndoableMeasureGeneric;
import org.herac.tuxguitar.song.models.TGNote;

/**
 * @author luffah
 * (fork of julian's code)
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ChangeGhostChordAction extends Action{
	public static final String NAME = "action.chord.effect.change-ghost";
	
	public ChangeGhostChordAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(TypedEvent e){
		//comienza el undoable
		UndoableMeasureGeneric undoable = UndoableMeasureGeneric.startUndo();
		
		Caret caret = getEditor().getTablature().getCaret();
		
		Iterator it=caret.getSelectedBeat().getVoice(caret.getVoice()).getNotes().iterator();
		while (it.hasNext()){
			getSongManager().getMeasureManager().changeGhostNote(caret.getMeasure(),caret.getPosition(),((TGNote) it.next()).getString());	
		}
		
		TuxGuitar.instance().getFileHistory().setUnsavedFile();
		updateTablature();
		
		
		
		//termia el undoable
		addUndoableEdit(undoable.endUndo());
		
		return 0;
	}
	
	public void updateTablature() {
		fireUpdate(getEditor().getTablature().getCaret().getMeasure().getNumber());
	}
}
