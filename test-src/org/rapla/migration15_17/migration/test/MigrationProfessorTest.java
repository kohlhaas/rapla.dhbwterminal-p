package org.rapla.migration15_17.migration.test;

import org.rapla.entities.domain.Allocatable;
import org.rapla.entities.dynamictype.Attribute;
import org.rapla.entities.dynamictype.AttributeType;
import org.rapla.entities.dynamictype.ClassificationFilter;
import org.rapla.entities.dynamictype.ConstraintIds;
import org.rapla.entities.dynamictype.DynamicType;
import org.rapla.facade.ClientFacade;
import org.rapla.migration15_17.MigrationTestCase;

public class MigrationProfessorTest extends MigrationTestCase {
ClientFacade facade;

	public MigrationProfessorTest(String name) {
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

	public void testProfessor() throws Exception {
		boolean fakultaetExists = false;
		boolean telefonExists = false;
		boolean zuletztExists = false;
		boolean raumExists = false;
		boolean bildExists = false;
        DynamicType defaultPerson = facade.getDynamicType("defaultPerson");
        String[] keys = new String[defaultPerson.getAttributes().length];
		for(int i=0; i<keys.length; i++)
		{
			if (defaultPerson.getAttributes()[i].getKey().equals("abteilung"))
				fakultaetExists = true;
			if (defaultPerson.getAttributes()[i].getKey().equals("telefon"))
				telefonExists = true;
			if (defaultPerson.getAttributes()[i].getKey().equals("last-modified"))
				zuletztExists = true;
			if (defaultPerson.getAttributes()[i].getKey().equals("raum"))
				raumExists = true;
			if (defaultPerson.getAttributes()[i].getKey().equals("bild"))
				bildExists = true;
		}
        Attribute telefon;
        if (!telefonExists) {
            telefon = facade.newAttribute(AttributeType.STRING);
		    telefon.setKey("telefon");
            telefon.getName().setName("de", "Telefon");
            telefon.getName().setName("en", "Telephone");
        } else {
            telefon = defaultPerson.getAttribute("telefon");
        }
        Attribute raum;
        if (!raumExists) {
            raum = facade.newAttribute(AttributeType.CATEGORY);
            raum.setKey("raum");
            raum.getName().setName("de", "Raum");
            raum.getName().setName("en", "Room");
        } else {
            raum = defaultPerson.getAttribute("raum");
        }
        Attribute bild;
        if (!bildExists) {
    		bild = facade.newAttribute(AttributeType.STRING);
    		bild.setKey("bild");
	    	bild.getName().setName("de", "Bild");
		    bild.getName().setName("en", "Image");
            bild.setDefaultValue("person");
        } else {
            bild = defaultPerson.getAttribute("bild");
        }
        // jpg wird automatisch dran gehängt

		final DynamicType editProfessor = facade.edit(defaultPerson);
		if(!fakultaetExists)
		{
			editProfessor.getAttribute("a2").setKey("abteilung");
			editProfessor.getAttribute("abteilung").getName().setName("de", "Studiengang");
			facade.store(editProfessor);

		}
		if(!telefonExists)
		{
			editProfessor.addAttribute(telefon);
		}
		if(zuletztExists)
		{
			editProfessor.removeAttribute(editProfessor.getAttribute("last-modified"));
			
		}
		if(!raumExists)
		{
			editProfessor.addAttribute(raum);
			facade.store(editProfessor);
			editProfessor.getAttribute("raum").setConstraint(ConstraintIds.KEY_ROOT_CATEGORY, facade.getSuperCategory().getCategory("c9"));
			facade.store(editProfessor);
		}
        if (!bildExists)
        {
            editProfessor.addAttribute(bild);

        }
		facade.store(editProfessor);
		assertNotNull(defaultPerson.getAttribute("abteilung"));
		assertNotNull(defaultPerson.getAttribute("telefon"));
		assertNull(defaultPerson.getAttribute("last-modified"));
		assertNotNull(defaultPerson.getAttribute("raum"));
		assertNotNull(defaultPerson.getAttribute("bild"));
		
		
		//Mapping Fakult�t --> Studiengang
		// Gesundheitswesen
		ClassificationFilter filter = defaultPerson.newClassificationFilter();
		filter.addEqualsRule("abteilung", facade.getSuperCategory().getCategory("c6").getCategory("c3"));
		ClassificationFilter[] filters = new ClassificationFilter[] {filter};
		Allocatable[] allocatable = facade.getAllocatables(filters);
		Allocatable edit;
		for (int i = 0; i <allocatable.length; i++)
		{
			edit = facade.edit(allocatable[i]);
			edit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("c3"));
            edit.getClassification().setValue("bild", "person");
			facade.store(edit);
		}
		// Technik
		filter = defaultPerson.newClassificationFilter();
		filter.addEqualsRule("abteilung", facade.getSuperCategory().getCategory("c6").getCategory("c1"));
		filters = new ClassificationFilter[] {filter};
		allocatable = facade.getAllocatables(filters);
		for (int i = 0; i <allocatable.length; i++)
		{
			edit = facade.edit(allocatable[i]);
			edit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("c1"));
			edit.getClassification().setValue("bild", "person");
			facade.store(edit);
		}
		// Wirschaft
		filter = defaultPerson.newClassificationFilter();
		filter.addEqualsRule("abteilung", facade.getSuperCategory().getCategory("c6").getCategory("c2"));
		filters = new ClassificationFilter[] {filter};
		allocatable = facade.getAllocatables(filters);
		for (int i = 0; i <allocatable.length; i++)
		{
			edit = facade.edit(allocatable[i]);
			edit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("c4"));
            edit.getClassification().setValue("bild", "person");
			facade.store(edit);
		}
		editProfessor.getAttribute("abteilung").setConstraint(ConstraintIds.KEY_ROOT_CATEGORY, facade.getSuperCategory().getCategory("c2"));
		facade.store(editProfessor);
		
	}
}
