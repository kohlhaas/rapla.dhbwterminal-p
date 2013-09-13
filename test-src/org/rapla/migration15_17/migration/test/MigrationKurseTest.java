package org.rapla.migration15_17.migration.test;

import org.rapla.entities.Category;
import org.rapla.entities.Entity;
import org.rapla.entities.MultiLanguageName;
import org.rapla.entities.domain.Allocatable;
import org.rapla.entities.dynamictype.*;
import org.rapla.facade.ClientFacade;
import org.rapla.migration15_17.MigrationTestCase;

import java.util.ArrayList;
import java.util.Collection;

public class MigrationKurseTest extends MigrationTestCase {
	ClientFacade facade;
	
	public MigrationKurseTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp("test.xml");
		facade = (ClientFacade) getContext().lookup(ClientFacade.class);
        if (!facade.login("admin", "".toCharArray()))
            throw new Exception("Login failed");

    }

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testKurse() throws Exception
	{
		boolean raumExists = false;
		boolean jahrgangExists = false;
		boolean zuletztExists = false;
		boolean fachrichtungExists = false;
		boolean studiengangExists = false;
		boolean bildExists = false;
        DynamicType resource2 = facade.getDynamicType("resource2");
        String[] keys = new String[resource2.getAttributes().length];
		for(int i=0; i<keys.length; i++)
		{
			if (resource2.getAttributes()[i].getKey().equals("raum"))
				raumExists = true;
			if (resource2.getAttributes()[i].getKey().equals("jahrgang"))
				jahrgangExists = true;
			if (resource2.getAttributes()[i].getKey().equals("last-modified"))
				zuletztExists = true;
			if (resource2.getAttributes()[i].getKey().equals("a1"))
				fachrichtungExists = true;
			if (resource2.getAttributes()[i].getKey().equals("abteilung"))
				studiengangExists = true;
            if (resource2.getAttributes()[i].getKey().equals("bild"))
                bildExists = true;
		}
		Attribute raum = facade.newAttribute(AttributeType.CATEGORY);
		raum.setKey("raum");
		raum.getName().setName("de", "Raum");
		raum.getName().setName("en", "Room");
		Attribute jahrgang = facade.newAttribute(AttributeType.CATEGORY);
		jahrgang.setKey("jahrgang");
		jahrgang.getName().setName("de", "Jahrgang");
		jahrgang.getName().setName("en", "Year");
		DynamicType editKurs = facade.edit(resource2);

        if(!bildExists)
        {
            Attribute bild = facade.newAttribute(AttributeType.STRING);
            bild.setKey("bild");
            bild.getName().setName("de", "Bild");
            bild.getName().setName("en", "Image");
            bild.setDefaultValue("kurs");
            editKurs.addAttribute(bild);
        }
		if(!raumExists)
		{
			editKurs.addAttribute(raum);
          /*  if (facade.getSuperCategory().getCategory("c9") == null) {
                // raum existiert nicht, also anlegen
                Category raumCat = facade.newCategory();
                raumCat.setKey("c9");
                raumCat.getName().setName("de", "Räume");
                Category edit = facade.edit(facade.getSuperCategory());
                edit.addCategory(raumCat);
                facade.store(edit);
                String [] fluegel = {"A","B","C","D","E","F","G","X"};
                for (int i = 0, fluegelLength = fluegel.length; i < fluegelLength; i++) {
                    String fl = fluegel[i];
                    Category fluegelCat = facade.newCategory();
                    fluegelCat.setKey("c"+(i+1));
                    fluegelCat.getName().setName("de", fl);
                    Category editRaumCat = facade.edit(raumCat);
                    editRaumCat.addCategory(fluegelCat);
                    facade.store(editRaumCat);
                }



            }*/
            assertNotNull(facade.getSuperCategory().getCategory("c9"));
			editKurs.getAttribute("raum").setConstraint(ConstraintIds.KEY_ROOT_CATEGORY, facade.getSuperCategory().getCategory("c9"));
		}
		if(!jahrgangExists)
		{
			editKurs.addAttribute(jahrgang);
			Category cat = facade.newCategory();
			cat.setKey("c10");
			cat.getName().setName("de", "Jahrgang");
			Category j08 = facade.newCategory();
			j08.setKey("j08");
			j08.getName().setName("de", "2008");
			Category j09 = facade.newCategory();
			j09.setKey("j09");
			j09.getName().setName("de", "2009");
			Category j10 = facade.newCategory();
			j10.setKey("j10");
			j10.getName().setName("de", "2010");
			Category j11 = facade.newCategory();
			j11.setKey("j11");
			j11.getName().setName("de", "2011");
			Category j12 = facade.newCategory();
			j12.setKey("j12");
			j12.getName().setName("de", "2012");
			Category j13 = facade.newCategory();
			j13.setKey("j13");
			j13.getName().setName("de", "2013");
			Category edit = facade.edit(facade.getSuperCategory());
			edit.addCategory(cat);
			facade.store(edit);
			edit.getCategory("c10").addCategory(j08);
			edit.getCategory("c10").addCategory(j09);
			edit.getCategory("c10").addCategory(j10);
			edit.getCategory("c10").addCategory(j11);
			edit.getCategory("c10").addCategory(j12);
			edit.getCategory("c10").addCategory(j13);
			facade.store(edit);
			editKurs.getAttribute("jahrgang").setConstraint(ConstraintIds.KEY_ROOT_CATEGORY, facade.getSuperCategory().getCategory("c10"));
		}
		if(zuletztExists)
		{
			editKurs.removeAttribute(editKurs.getAttribute("last-modified"));
		}
		if(fachrichtungExists)
		{
			editKurs.removeAttribute(editKurs.getAttribute("a1"));
		}
		if(!studiengangExists)
		{
			editKurs.getAttribute("a2").setKey("abteilung");
			editKurs.getAttribute("abteilung").getName().setName("de", "Studiengang");
			editKurs.getAttribute("abteilung").setConstraint(ConstraintIds.KEY_ROOT_CATEGORY, facade.getSuperCategory().getCategory("c2"));
		}
		facade.store(editKurs);
		assertNotNull(resource2.getAttribute("raum"));
		assertNotNull(resource2.getAttribute("jahrgang"));
		assertNull(resource2.getAttribute("last-modified"));
		assertNull(resource2.getAttribute("a1"));
		assertNotNull(resource2.getAttribute("abteilung"));
		
		ClassificationFilter filter = resource2.newClassificationFilter();
		ClassificationFilter[] filters = new ClassificationFilter[] {filter};
		Allocatable[] allocatable = facade.getAllocatables(filters);
        Collection<Entity<?>> edits = new ArrayList<Entity<?>>();
		for (int i = 0; i < allocatable.length; i++)
		{
			Allocatable edit = facade.edit(allocatable[i]);


            //default value für bild, jpg extension wird automatisch dran gehängt
            edit.getClassification().setValue("bild","kurs");

            String kursName = edit.getClassification().getValue("name").toString().trim();
            Category jahrKategorie = facade.getSuperCategory().getCategory("c10");

            for (int j = 8; j <= 13; j++) {
                String zehnerJahr = (j < 10 ? "0" : "") + j;
                if (kursName.contains(zehnerJahr)) {
                    edit.getClassification().setValue("jahrgang", jahrKategorie.getCategory("j"+zehnerJahr));
                    if (kursName.lastIndexOf(zehnerJahr) > -1)
                    {
                        String kurs = kursName.substring(kursName.lastIndexOf(zehnerJahr) + zehnerJahr.length(), kursName.length()).trim();
                        if (!kurs.isEmpty()) {
                            edit.getClassification().setValue("name", kurs);
                        }
                    }

                }
            }


/*            if (kursName.contains("08"))
			{
				edit.getClassification().setValue("jahrgang", jahrKategorie.getCategory("j08"));
                edit.getClassification().setValue("name","");
            }
			else if (kursName.contains("09"))
			{
				edit.getClassification().setValue("jahrgang", jahrKategorie.getCategory("j09"));
			}
			else if (kursName.contains("10"))
			{
				edit.getClassification().setValue("jahrgang", jahrKategorie.getCategory("j10"));
			}
			else if (kursName.contains("11"))
			{
				edit.getClassification().setValue("jahrgang", jahrKategorie.getCategory("j11"));
			}
			else if (kursName.contains("12"))
			{
				edit.getClassification().setValue("jahrgang", jahrKategorie.getCategory("j12"));
			}
			else if (kursName.contains("13"))
			{
				edit.getClassification().setValue("jahrgang", jahrKategorie.getCategory("j13"));
			}*/



            edits.add(edit);
			//facade.store(edit);
		}
        facade.storeObjects(edits.toArray(new Entity[allocatable.length]));
	}
}
