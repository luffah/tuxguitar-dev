/*
 * Created on 08-dec-2015
 * 
 * Copy the previous chord if the current chord (under the carret) is empty
 *
 * TODO : action.note.general.delete
 * Delete all note on the current chord (under the carret)
 *
 */
package org.herac.tuxguitar.gui.actions.note;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.swt.events.TypedEvent;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.editors.tab.Caret;
import org.herac.tuxguitar.gui.editors.tab.TGBeatImpl;
import org.herac.tuxguitar.gui.editors.tab.TGMeasureImpl;
import org.herac.tuxguitar.gui.editors.tab.TGTrackImpl;
import org.herac.tuxguitar.gui.undo.undoables.measure.UndoableAddMeasure;
import org.herac.tuxguitar.gui.undo.undoables.measure.UndoableMeasureGeneric;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.song.models.TGVoice;

/**
 * @author luffah
 */
public class ChangeCopyChordAction extends Action {
	public static final String NAME = "action.chord.general.duplicate";

	public ChangeCopyChordAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING
				| KEY_BINDING_AVAILABLE);
	}

	
	protected int execute(TypedEvent e) {
		Caret caret = getEditor().getTablature().getCaret();
		// undoable begin
		if (caret.isSelectionClosed()){
			addCopiedNotesFromSelectedVoices(caret);
		} else {
			UndoableMeasureGeneric undoable = UndoableMeasureGeneric.startUndo();
			addCopiedNotesFromPreviousVoice(caret);
			// undoable end
			addUndoableEdit(undoable.endUndo());
		}
		
		TuxGuitar.instance().getFileHistory().setUnsavedFile();
		updateTablature();
		
		return 0;
	}
	private void addCopiedNotesFromPreviousVoice(Caret caret) {
		TGDuration duration = getSongManager().getFactory().newDuration();
		caret.getDuration().copy(duration);
		TGVoice voice = getPreviousVoice(caret);
		addCopiedNotesFromVoice(caret, duration, voice);
	}
	private void addCopiedNotesFromSelectedVoices(Caret caret) {
		TGBeat b;
		TGDuration duration_sel;
		TGDuration duration;
		TGVoice voice;
		long duration_time;
		long measure_avaiable_time;
		Iterator it=caret.getSelectionBeats().iterator();
		while (it.hasNext()) {
			UndoableMeasureGeneric undoable = UndoableMeasureGeneric.startUndo();
			
			b= (TGBeat) it.next();
			voice=b.getVoice(caret.getVoice());
			duration = getSongManager().getFactory().newDuration();
			duration_sel = voice.getDuration();
			duration_sel.copy(duration);
			measure_avaiable_time=(caret.getMeasure().getLength()-(caret.getSelectedBeat().getStart()-caret.getMeasure().getStart()));
			while (duration.getTime()>measure_avaiable_time){
				duration.setValue(duration.getValue()*2);
				duration.setDotted(false);
				duration.setDoubleDotted(false);
				if (duration.getTime()==measure_avaiable_time-(duration.getTime()/2))
					duration.setDotted(true);
				else if (duration.getTime()==measure_avaiable_time-(duration.getTime()/2)-(duration.getTime()/4))
					duration.setDoubleDotted(true);
			}
			addCopiedNotesFromVoice(caret, duration, voice);
			duration_time=duration.getTime();
			
			measure_avaiable_time=measure_avaiable_time-duration_time;
			if (measure_avaiable_time > 0)
				System.out.println("Warning!!! measure_avaiable_time > 0");
			
			long duration_val=(duration_sel.getTime()-duration_time);
			duration = getSongManager().getFactory().newDuration();
			while(duration_val > 0){
				if(!caret.moveRight()){
					int number = (getSongManager().getSong().countMeasureHeaders() + 1);
					getSongManager().addNewMeasure(number);
					fireUpdate(number);
					caret.moveRight();			
				}
				duration_sel.copy(duration);
				duration_val=((duration_sel.getTime()-duration_time)/TGDuration.QUARTER_TIME);
				System.out.println("ChangeCopyChordAction duration_val "+ duration_val);
				duration.setValue((int) duration_val);
				duration_time=duration.getTime();
				addTiedNotesFromVoice(caret, duration, voice);
			}
			this.updateTablature();
					
			if(!caret.moveRight()){
				int number = (getSongManager().getSong().countMeasureHeaders() + 1);
				getSongManager().addNewMeasure(number);
				fireUpdate(number);
				caret.moveRight();			
			}
			addUndoableEdit(undoable.endUndo());
		}
	}
	private void changeDuration(int value) {
		Caret caret = getEditor().getTablature().getCaret();
		caret.getDuration().setValue(value);
		caret.getDuration().setDotted(false);
		caret.getDuration().setDoubleDotted(false);
		caret.changeDuration(caret.getDuration().clone(getSongManager().getFactory()));
	}
	private TGVoice getPreviousVoice(Caret caret){
		TGMeasure measure = caret.getMeasure();
		
		TGVoice voice = getSongManager().getMeasureManager().getPreviousVoice(
				measure.getBeats(), caret.getSelectedBeat(), caret.getVoice());
		if (measure != null) {
			if (voice != null) {
			} else {
				measure = getSongManager().getTrackManager().getPrevMeasure(
						measure);
				if (measure != null) {
					voice = getSongManager().getMeasureManager().getLastVoice(
							measure.getBeats(), caret.getVoice());
				}
			}
			if ( (voice != null) && (!voice.isRestVoice()) ) {
				return voice;
			}
		}
		return null;
	}
	private void addCopiedNotesFromVoice(Caret caret, TGDuration duration, TGVoice voice){
		if (voice !=null) {// Check if is there any note at same string. 
			Iterator it = voice.getNotes().iterator();
			TGNote note;
			while (it.hasNext()) {
				note = getSongManager().getFactory().newNote();
				note.setVelocity(caret.getVelocity());
				
				TGNote current = (TGNote) it.next();
				note.setValue(current.getValue());
				note.setTiedNote(current.isTiedNote());
				note.setString(current.getString());
				// add note
				getSongManager().getMeasureManager().addNote(
						caret.getSelectedBeat(), note, duration,
						caret.getVoice());
			}
		}
	}
	private void addTiedNotesFromVoice(Caret caret, TGDuration duration, TGVoice voice){
		if (voice !=null) {// Check if is there any note at same string. 
			Iterator it = voice.getNotes().iterator();
			TGNote note;
			while (it.hasNext()) {
				note = getSongManager().getFactory().newNote();
				note.setVelocity(caret.getVelocity());
				
				TGNote current = (TGNote) it.next();
				note.setValue(current.getValue());
				note.setTiedNote(true);
				note.setString(current.getString());
				// add note
				getSongManager().getMeasureManager().addNote(
						caret.getSelectedBeat(), note, duration,
						caret.getVoice());
			}
		}
	}
	public void updateTablature() {
		fireUpdate(getEditor().getTablature().getCaret().getMeasure()
				.getNumber());
	}
}
