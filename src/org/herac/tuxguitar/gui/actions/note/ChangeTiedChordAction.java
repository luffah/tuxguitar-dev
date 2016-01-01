/*
 * Created on 01-jan-2016
 */
package org.herac.tuxguitar.gui.actions.note;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.events.TypedEvent;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.editors.tab.Caret;
import org.herac.tuxguitar.gui.undo.undoables.measure.UndoableMeasureGeneric;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGVoice;

/**
 * @author luffah
 * (fork of julian's code)
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class ChangeTiedChordAction extends Action {
	public static final String NAME = "action.chord.general.tied";

	public ChangeTiedChordAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING
				| KEY_BINDING_AVAILABLE);
	}

	protected int execute(TypedEvent e) {
		Caret caret = getEditor().getTablature().getCaret();
		if (caret.getSelectedNote() != null) {
			UndoableMeasureGeneric undoable = UndoableMeasureGeneric
					.startUndo();

			
			Iterator it=caret.getSelectedBeat().getVoice(caret.getVoice()).getNotes().iterator();
			while (it.hasNext()){
				getSongManager().getMeasureManager().changeTieNote(
						(TGNote) it.next());
			}
			
			addUndoableEdit(undoable.endUndo());
		} else {
			TGDuration duration = getSongManager().getFactory().newDuration();
			caret.getDuration().copy(duration);

			ArrayList notes = getTiedNotes(caret);

			UndoableMeasureGeneric undoable = UndoableMeasureGeneric
					.startUndo();

			Iterator it = notes.iterator();
			while (it.hasNext()) {
				getSongManager().getMeasureManager().addNote(
						caret.getSelectedBeat(), (TGNote) it.next(), duration,
						caret.getVoice());
			}
			addUndoableEdit(undoable.endUndo());
		}
		TuxGuitar.instance().getFileHistory().setUnsavedFile();
		updateTablature();
		return 0;
	}

	private ArrayList getTiedNotes(Caret caret) {
		TGMeasure measure = caret.getMeasure();
		TGVoice voice = getSongManager().getMeasureManager().getPreviousVoice(
				measure.getBeats(), caret.getSelectedBeat(), caret.getVoice());
		ArrayList tiednotes = new ArrayList();

		while (measure != null) {
			while (voice != null) {
				if (!voice.isRestVoice()) {

					// Check if is there any note at same string.
					Iterator it = voice.getNotes().iterator();
					while (it.hasNext()) {
						TGNote current = (TGNote) it.next();
						TGNote note = getSongManager().getFactory().newNote();
						note.setValue(current.getValue());
						note.setVelocity(current.getVelocity());
						note.setString(current.getString());
						note.setTiedNote(true);
						tiednotes.add(note);
					}
				}
				voice = getSongManager().getMeasureManager().getPreviousVoice(
						measure.getBeats(), voice.getBeat(), caret.getVoice());
			}
			measure = getSongManager().getTrackManager()
					.getPrevMeasure(measure);
			if (measure != null) {
				voice = getSongManager().getMeasureManager().getLastVoice(
						measure.getBeats(), caret.getVoice());
			}
		}
		return tiednotes;
	}

	public void updateTablature() {
		fireUpdate(getEditor().getTablature().getCaret().getMeasure()
				.getNumber());
	}
}
