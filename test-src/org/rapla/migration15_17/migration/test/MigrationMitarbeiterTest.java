package org.rapla.migration15_17.migration.test;

import org.rapla.entities.dynamictype.Attribute;
import org.rapla.entities.dynamictype.AttributeType;
import org.rapla.entities.dynamictype.ConstraintIds;
import org.rapla.entities.dynamictype.DynamicType;
import org.rapla.facade.ClientFacade;
import org.rapla.migration15_17.MigrationTestCase;

public class MigrationMitarbeiterTest extends MigrationTestCase {
ClientFacade facade;
	
	public MigrationMitarbeiterTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp("test.xml");
		facade = (ClientFacade) getContext().lookup(ClientFacade.class);
		facade.login("admin", "".toCharArray());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testMitarbeiter() throws Exception {
		boolean fakultaetExists = false;
		boolean zuletztExists = false;
		boolean raumExists = false;
		String[] keys = new String[facade.getDynamicType("person2").getAttributes().length];
		for(int i=0; i<keys.length; i++)
		{
			if (facade.getDynamicType("person2").getAttributes()[i].getKey().equals("abteilung"))
				fakultaetExists = true;
			if (facade.getDynamicType("person2").getAttributes()[i].getKey().equals("last-modified"))
				zuletztExists = true;
			if (facade.getDynamicType("person2").getAttributes()[i].getKey().equals("raum"))
				raumExists = true;
		}
		Attribute abteilung = facade.newAttribute(AttributeType.CATEGORY);
		abteilung.setKey("abteilung");
		abteilung.getName().setName("de", "Abteilung");
		abteilung.getName().setName("en", "Deparment");
		Attribute raum = facade.newAttribute(AttributeType.CATEGORY);
		raum.setKey("raum");
		raum.getName().setName("de", "Raum");
		raum.getName().setName("en", "Room");
		final DynamicType editmitarbeiter = facade.edit(facade.getDynamicType("person2"));
		if(!fakultaetExists)
		{
			editmitarbeiter.addAttribute(abteilung);
			editmitarbeiter.getAttribute("abteilung").getName().setName("de", "Studiengang");
			facade.store(editmitarbeiter);
			editmitarbeiter.getAttribute("abteilung").setConstraint(ConstraintIds.KEY_ROOT_CATEGORY, facade.getSuperCategory().getCategory("c2"));
			facade.store(editmitarbeiter);
		}
		if(zuletztExists)
		{
			editmitarbeiter.removeAttribute(editmitarbeiter.getAttribute("last-modified"));
		}
		if(!raumExists)
		{
			editmitarbeiter.addAttribute(raum);
			facade.store(editmitarbeiter);
			editmitarbeiter.getAttribute("raum").setConstraint(ConstraintIds.KEY_ROOT_CATEGORY, facade.getSuperCategory().getCategory("c9"));
			facade.store(editmitarbeiter);
		}
		facade.store(editmitarbeiter);
		assertNotNull(facade.getDynamicType("person2").getAttribute("abteilung"));
		assertNull(facade.getDynamicType("person2").getAttribute("last-modified"));
		assertNotNull(facade.getDynamicType("person2").getAttribute("raum"));
	}
}

