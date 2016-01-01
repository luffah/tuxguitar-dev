package org.herac.tuxguitar.gui;

public class TGMain {
	
	public static void main(String[] args){
		System.out.println("TuxGuitar");
		TuxGuitar.instance().displayGUI(args);
		System.exit(0);
	}
	
}
