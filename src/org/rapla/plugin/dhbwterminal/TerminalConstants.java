package org.rapla.plugin.dhbwterminal;

public interface TerminalConstants 
{
	static final String LINK_TITEL_PERSON = "Termine";
	static final String LINK_TITEL_KURS = "Veranstaltungen";
	static final String LINK_TITEL_RAUM = "Belegung";
	static final String LINK_TITEL_DEFAULT = "Info";
    static final String KURS_KEY = "kurs";
	static final String STELE_USER = "stele";
	static final String ROOM_KEY = "raum";
	static final String[]  exportTypeNames = new String[] {ROOM_KEY,"professor", "honorarkraefte", "mitarbeiter",KURS_KEY, "sonstiges"};
	static final String[]  exportEventTypeNames = new String[] {"lehrveranstaltung","pruefung"};
//	static final String CUSTOM_CSS_FILE_PATH = "kursuebersicht_dhbw.css";
	static final String KURS_UEBERSCHRIFT = "Kurse mit aktuellen Veranstaltungen";
	static final String NO_COURSES = "Heute sind keine weiteren Veranstaltungen geplant.";
	 
}
