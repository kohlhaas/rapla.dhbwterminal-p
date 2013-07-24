package org.rapla.migration15_17.migration.test;

import org.rapla.entities.domain.Allocatable;
import org.rapla.entities.dynamictype.*;
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
		boolean bildExists = false;
        DynamicType personType = facade.getDynamicType("person2");
        String[] keys = new String[personType.getAttributes().length];
		for(int i=0; i<keys.length; i++)
		{
			if (personType.getAttributes()[i].getKey().equals("abteilung"))
				fakultaetExists = true;
			if (personType.getAttributes()[i].getKey().equals("last-modified"))
				zuletztExists = true;
			if (personType.getAttributes()[i].getKey().equals("raum"))
				raumExists = true;
            if (personType.getAttributes()[i].getKey().equals("bild"))
                bildExists = true;

        }
		Attribute abteilung = facade.newAttribute(AttributeType.CATEGORY);
		abteilung.setKey("abteilung");
		abteilung.getName().setName("de", "Abteilung");
		abteilung.getName().setName("en", "Deparment");
		Attribute raum = facade.newAttribute(AttributeType.CATEGORY);
		raum.setKey("raum");
		raum.getName().setName("de", "Raum");
		raum.getName().setName("en", "Room");
        Attribute bild;
        if (!bildExists) {
            bild = facade.newAttribute(AttributeType.STRING);
            bild.setKey("bild");
            bild.getName().setName("de", "Bild");
            bild.getName().setName("en", "Image");
            bild.setDefaultValue("person");
        } else {
            bild = personType.getAttribute("bild");
        }
		final DynamicType editmitarbeiter = facade.edit(personType);
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
        if (!bildExists) {
            editmitarbeiter.addAttribute(bild);
        }
       facade.store(editmitarbeiter);
		assertNotNull(personType.getAttribute("abteilung"));
		assertNull(personType.getAttribute("last-modified"));
		assertNotNull(personType.getAttribute("raum"));

        ClassificationFilter filter = personType.newClassificationFilter();
        ClassificationFilter[] filters = new ClassificationFilter[]{filter};
        Allocatable[] allocatable = facade.getAllocatables(filters);
        Allocatable edit;
        for (int i = 0; i <allocatable.length; i++)
        {
            edit = facade.edit(allocatable[i]);
            edit.getClassification().setValue("bild", "person");
            facade.store(edit);
        }

	}
}

