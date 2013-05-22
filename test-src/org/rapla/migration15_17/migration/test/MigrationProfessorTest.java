package org.rapla.migration15_17.migration.test;

import org.rapla.entities.domain.Allocatable;
import org.rapla.entities.dynamictype.*;
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
		String[] keys = new String[facade.getDynamicType("defaultPerson").getAttributes().length];
		for(int i=0; i<keys.length; i++)
		{
			if (facade.getDynamicType("defaultPerson").getAttributes()[i].getKey().equals("abteilung"))
				fakultaetExists = true;
			if (facade.getDynamicType("defaultPerson").getAttributes()[i].getKey().equals("telefon"))
				telefonExists = true;
			if (facade.getDynamicType("defaultPerson").getAttributes()[i].getKey().equals("last-modified"))
				zuletztExists = true;
			if (facade.getDynamicType("defaultPerson").getAttributes()[i].getKey().equals("raum"))
				raumExists = true;
			if (facade.getDynamicType("defaultPerson").getAttributes()[i].getKey().equals("bild"))
				bildExists = true;
		}
		Attribute telefon = facade.newAttribute(AttributeType.STRING);
		telefon.setKey("telefon");
		telefon.getName().setName("de", "Telefon");
		telefon.getName().setName("en", "Telephone");
		Attribute raum = facade.newAttribute(AttributeType.CATEGORY);
		raum.setKey("raum");
		raum.getName().setName("de", "Raum");
		raum.getName().setName("en", "Room");
		Attribute bild = facade.newAttribute(AttributeType.STRING);
		bild.setKey("bild");
		bild.getName().setName("de", "Bild");
		bild.getName().setName("en", "Image");
		final DynamicType editProfessor = facade.edit(facade.getDynamicType("defaultPerson"));
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
		if(!bildExists)
		{
			editProfessor.addAttribute(bild);
			facade.store(editProfessor);
			editProfessor.getAttribute("bild").setDefaultValue("dummy");
			facade.store(editProfessor);
		}
		facade.store(editProfessor);
		assertNotNull(facade.getDynamicType("defaultPerson").getAttribute("abteilung"));
		assertNotNull(facade.getDynamicType("defaultPerson").getAttribute("telefon"));
		assertNull(facade.getDynamicType("defaultPerson").getAttribute("last-modified"));
		assertNotNull(facade.getDynamicType("defaultPerson").getAttribute("raum"));
		assertNotNull(facade.getDynamicType("defaultPerson").getAttribute("bild"));
		
		
		//Mapping Fakult�t --> Studiengang
		// Gesundheitswesen
		ClassificationFilter filter = facade.getDynamicType("defaultPerson").newClassificationFilter();
		filter.addEqualsRule("abteilung", facade.getSuperCategory().getCategory("c6").getCategory("c3"));
		ClassificationFilter[] filters = new ClassificationFilter[] {filter};
		Allocatable[] allocatable = facade.getAllocatables(filters);
		Allocatable edit;
		for (int i = 0; i <allocatable.length; i++)
		{
			edit = facade.edit(allocatable[i]);
			edit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("c3"));
			facade.store(edit);
		}
		// Technik
		filter = facade.getDynamicType("defaultPerson").newClassificationFilter();
		filter.addEqualsRule("abteilung", facade.getSuperCategory().getCategory("c6").getCategory("c1"));
		filters = new ClassificationFilter[] {filter};
		allocatable = facade.getAllocatables(filters);
		for (int i = 0; i <allocatable.length; i++)
		{
			edit = facade.edit(allocatable[i]);
			edit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("c1"));
			facade.store(edit);
		}
		// Wirschaft
		filter = facade.getDynamicType("defaultPerson").newClassificationFilter();
		filter.addEqualsRule("abteilung", facade.getSuperCategory().getCategory("c6").getCategory("c2"));
		filters = new ClassificationFilter[] {filter};
		allocatable = facade.getAllocatables(filters);
		for (int i = 0; i <allocatable.length; i++)
		{
			edit = facade.edit(allocatable[i]);
			edit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("c4"));
			facade.store(edit);
		}
		editProfessor.getAttribute("abteilung").setConstraint(ConstraintIds.KEY_ROOT_CATEGORY, facade.getSuperCategory().getCategory("c2"));
		facade.store(editProfessor);
		
	}
}
