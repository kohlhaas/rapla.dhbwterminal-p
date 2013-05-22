package org.rapla.migration15_17.migration.test;

import org.rapla.entities.Category;
import org.rapla.entities.domain.Allocatable;
import org.rapla.entities.dynamictype.*;
import org.rapla.facade.ClientFacade;
import org.rapla.migration15_17.MigrationTestCase;

public class MigrationRaumTest extends MigrationTestCase {
    ClientFacade facade;

    public MigrationRaumTest(String name) {
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

    public void testRaum() throws Exception {
        //�berpr�fe ob alle n�tigen Datentypen vorhanden sind
        //und f�ge gegebenenfalls fehlende hinzu


        boolean raumnrExists = false;
        boolean a7Exists = false;
        boolean zusatzinfoExists = false;
        boolean zuletztExists = false;
        boolean bildExists = false;
        boolean studiengangExists = false;
        //neues Raum-Attribut
        boolean a9Exists = false;
        String[] keys = new String[facade.getDynamicType("resource1").getAttributes().length];
        for (int i = 0; i < keys.length; i++) {
            if (facade.getDynamicType("resource1").getAttributes()[i].getKey().equals("raumnr"))
                raumnrExists = true;
            if (facade.getDynamicType("resource1").getAttributes()[i].getKey().equals("a7"))
                a7Exists = true;
            if (facade.getDynamicType("resource1").getAttributes()[i].getKey().equals("zusatzinfo"))
                zusatzinfoExists = true;
            if (facade.getDynamicType("resource1").getAttributes()[i].getKey().equals("last-modified"))
                zuletztExists = true;
            if (facade.getDynamicType("resource1").getAttributes()[i].getKey().equals("bild"))
                bildExists = true;
            if (facade.getDynamicType("resource1").getAttributes()[i].getKey().equals("studiengang"))
                studiengangExists = true;

            if (facade.getDynamicType("resource1").getAttributes()[i].getKey().equals("a9"))
                a9Exists = true;

        }
        Attribute bild = facade.newAttribute(AttributeType.STRING);
        bild.setKey("bild");
        bild.getName().setName("de", "Bild");
        bild.getName().setName("en", "Image");
        final DynamicType editRaum = facade.edit(facade.getDynamicType("resource1"));
        if (!raumnrExists && a7Exists) {
            editRaum.getAttribute("a7").setKey("raumnr");
        }
        if (!zusatzinfoExists) {
            editRaum.getAttribute("name").setKey("zusatzinfo");
        }
        if (zuletztExists) {
            editRaum.removeAttribute(editRaum.getAttribute("last-modified"));
        }
        if (!bildExists) {
            editRaum.addAttribute(bild);
            facade.store(editRaum);
            editRaum.getAttribute("bild").setDefaultValue("dummy");
            facade.store(editRaum);
        }
        if (!studiengangExists) {
            editRaum.getAttribute("a5").setKey("studiengang");
            editRaum.getAttribute("studiengang").getName().setName("de", "Studiengang");
        }
       /* if (!a9Exists) {
            Attribute a9 = facade.newAttribute(AttributeType.CATEGORY);
            a9.setKey("a9");
            a9.getName().setName("de", "Raum");
            a9.getName().setName("en", "Room");
            a9.setConstraint(ConstraintIds.KEY_ROOT_CATEGORY, facade.getSuperCategory().getCategory("c9"));
            editRaum.addAttribute(a9);


        }*/
        facade.store(editRaum);
        if (a7Exists)
            assertNotNull(facade.getDynamicType("resource1").getAttribute("raumnr"));

        assertNotNull(facade.getDynamicType("resource1").getAttribute("zusatzinfo"));
        assertNull(facade.getDynamicType("resource1").getAttribute("last-modified"));
        assertNotNull(facade.getDynamicType("resource1").getAttribute("bild"));
        assertNotNull(facade.getDynamicType("resource1").getAttribute("a9"));
        assertNotNull(facade.getDynamicType("resource1").getAttribute("studiengang"));


        //Umwandeln von Geb�udefl�gel in Raum und gleichzeitiges anlegen von Unterknoten


        for (int j = 1; j <= facade.getSuperCategory().getCategory("c9").getCategories().length; j++) {
            DynamicType resource1 = facade.getDynamicType("resource1");
            assertNotNull(resource1.getAttribute("a9"));
            ClassificationFilter filter = resource1.newClassificationFilter();

            filter.addEqualsRule("a9", facade.getSuperCategory().getCategory("c9").getCategory("c" + j));
            ClassificationFilter[] filters = new ClassificationFilter[]{filter};
            Allocatable[] allocatable = facade.getAllocatables(filters);
            for (int i = 0; i < allocatable.length; i++) {
                if (!facade.getSuperCategory().getCategory("c9").getCategory("c" + j).hasCategory(
                        facade.getSuperCategory().getCategory("c9").getCategory("c" + j).getCategory(allocatable[i]
                                .getClassification().getValue("raumnr").toString()))) {
                    Category cat = facade.newCategory();
                    Category edit;
                    String buchstabe = "";
                    if (j == 1)
                        buchstabe = "A";
                    if (j == 2)
                        buchstabe = "B";
                    if (j == 3)
                        buchstabe = "C";
                    if (j == 4)
                        buchstabe = "D";
                    if (j == 5)
                        buchstabe = "E";
                    if (j == 6)
                        buchstabe = "F";
                    if (j == 7)
                        buchstabe = "G";
                    if (j == 8)
                        buchstabe = "X";
                    cat.setKey(buchstabe + allocatable[i].getClassification().getValue("raumnr").toString());
                    cat.getName().setName("de", allocatable[i].getClassification().getValue("raumnr").toString());
                    edit = facade.edit(facade.getSuperCategory().getCategory("c9").getCategory("c" + j));
                    edit.addCategory(cat);
                    facade.store(edit);
                }
            }
        }
        for (int j = 0; j < facade.getSuperCategory().getCategory("c9").getCategories().length; j++) {
            ClassificationFilter filter = facade.getDynamicType("resource1").newClassificationFilter();
            filter.addEqualsRule("a9", facade.getSuperCategory().getCategory("c9").getCategory("c" + j));
            ClassificationFilter[] filters = new ClassificationFilter[]{filter};
            Allocatable[] allocatable2 = facade.getAllocatables(filters);
            String buchstabe = "";
            if (j == 1)
                buchstabe = "A";
            if (j == 2)
                buchstabe = "B";
            if (j == 3)
                buchstabe = "C";
            if (j == 4)
                buchstabe = "D";
            if (j == 5)
                buchstabe = "E";
            if (j == 6)
                buchstabe = "F";
            if (j == 7)
                buchstabe = "G";
            if (j == 8)
                buchstabe = "X";
            for (int i = 0; i < allocatable2.length; i++) {
                String raumnr = allocatable2[i].getClassification().getValue("raumnr").toString();
                Category raum = facade.getSuperCategory().getCategory("c9").getCategory("c" + j).getCategory(buchstabe + raumnr);
                Allocatable edit = facade.edit(allocatable2[i]);
                edit.getClassification().setValue("a9", raum);
                facade.store(edit);
            }
        }
        DynamicType edit2 = facade.edit(facade.getDynamicType("resource1"));
        assertNotNull(edit2.getAttribute("a9"));
        edit2.getAttribute("a9").setKey("raum");
        edit2.getAttribute("raum").getName().setName("de", "Raum");
        edit2.setAnnotation(DynamicTypeAnnotations.KEY_NAME_FORMAT, "{raum} {a6} {studiengang}");
        Attribute raum = edit2.getAttribute("raumnr");
        edit2.removeAttribute(raum);
        facade.store(edit2);
        Category editFluegel = facade.edit(facade.getSuperCategory().getCategory("c9"));
        editFluegel.getCategory("c1").setKey("A");
        editFluegel.getCategory("c2").setKey("B");
        editFluegel.getCategory("c3").setKey("C");
        editFluegel.getCategory("c4").setKey("D");
        editFluegel.getCategory("c5").setKey("E");
        editFluegel.getCategory("c6").setKey("F");
        editFluegel.getCategory("c7").setKey("G");
        editFluegel.getCategory("c8").setKey("X");
        facade.store(editFluegel);

        //mapping Studiengangskennzeichen --> Studieng�nge


        // Arztassistenten
        ClassificationFilter filter = facade.getDynamicType("resource1").newClassificationFilter();
        filter.addEqualsRule("studiengang", facade.getSuperCategory().getCategory("c3").getCategory("c1").getCategory("c1"));
        ClassificationFilter[] filters = new ClassificationFilter[]{filter};
        Allocatable[] allocatable = facade.getAllocatables(filters);
        Allocatable edit;
        for (int i = 0; i < allocatable.length; i++) {
            edit = facade.edit(allocatable[i]);
            edit.getClassification().setValue("studiengang", facade.getSuperCategory().getCategory("c2").getCategory("c3").getCategory("c1"));
            facade.store(edit);
        }
        // Elektrotechnik
        filter = facade.getDynamicType("resource1").newClassificationFilter();
        filter.addEqualsRule("studiengang", facade.getSuperCategory().getCategory("c3").getCategory("c3").getCategory("c1"));
        filters = new ClassificationFilter[]{filter};
        allocatable = facade.getAllocatables(filters);
        for (int i = 0; i < allocatable.length; i++) {
            edit = facade.edit(allocatable[i]);
            edit.getClassification().setValue("studiengang", facade.getSuperCategory().getCategory("c2").getCategory("c1").getCategory("c1"));
            facade.store(edit);
        }

        // Informatik
        filter = facade.getDynamicType("resource1").newClassificationFilter();
        filter.addEqualsRule("studiengang", facade.getSuperCategory().getCategory("c3").getCategory("c3").getCategory("c2"));
        filters = new ClassificationFilter[]{filter};
        allocatable = facade.getAllocatables(filters);
        for (int i = 0; i < allocatable.length; i++) {
            edit = facade.edit(allocatable[i]);
            edit.getClassification().setValue("studiengang", facade.getSuperCategory().getCategory("c2").getCategory("c1").getCategory("c2"));
            facade.store(edit);
        }

        // Maschinenbau
        filter = facade.getDynamicType("resource1").newClassificationFilter();
        filter.addEqualsRule("studiengang", facade.getSuperCategory().getCategory("c3").getCategory("c3").getCategory("c3"));
        filters = new ClassificationFilter[]{filter};
        allocatable = facade.getAllocatables(filters);
        for (int i = 0; i < allocatable.length; i++) {
            edit = facade.edit(allocatable[i]);
            edit.getClassification().setValue("studiengang", facade.getSuperCategory().getCategory("c2").getCategory("c1").getCategory("c3"));
            facade.store(edit);
        }

        // Mechatronik
        filter = facade.getDynamicType("resource1").newClassificationFilter();
        filter.addEqualsRule("studiengang", facade.getSuperCategory().getCategory("c3").getCategory("c3").getCategory("c4"));
        filters = new ClassificationFilter[]{filter};
        allocatable = facade.getAllocatables(filters);
        for (int i = 0; i < allocatable.length; i++) {
            edit = facade.edit(allocatable[i]);
            edit.getClassification().setValue("studiengang", facade.getSuperCategory().getCategory("c2").getCategory("c1").getCategory("c4"));
            facade.store(edit);
        }

        // Papiertechnik
        filter = facade.getDynamicType("resource1").newClassificationFilter();
        filter.addEqualsRule("studiengang", facade.getSuperCategory().getCategory("c3").getCategory("c3").getCategory("c5"));
        filters = new ClassificationFilter[]{filter};
        allocatable = facade.getAllocatables(filters);
        for (int i = 0; i < allocatable.length; i++) {
            edit = facade.edit(allocatable[i]);
            edit.getClassification().setValue("studiengang", facade.getSuperCategory().getCategory("c2").getCategory("c1").getCategory("c5"));
            facade.store(edit);
        }

        // Sicherheitswesen
        filter = facade.getDynamicType("resource1").newClassificationFilter();
        filter.addEqualsRule("studiengang", facade.getSuperCategory().getCategory("c3").getCategory("c3").getCategory("c6"));
        filters = new ClassificationFilter[]{filter};
        allocatable = facade.getAllocatables(filters);
        for (int i = 0; i < allocatable.length; i++) {
            edit = facade.edit(allocatable[i]);
            edit.getClassification().setValue("studiengang", facade.getSuperCategory().getCategory("c2").getCategory("c1").getCategory("c6"));
            facade.store(edit);
        }

        // Wirtschaftsingenieurwesen
        filter = facade.getDynamicType("resource1").newClassificationFilter();
        filter.addEqualsRule("studiengang", facade.getSuperCategory().getCategory("c3").getCategory("c3").getCategory("c7"));
        filters = new ClassificationFilter[]{filter};
        allocatable = facade.getAllocatables(filters);
        for (int i = 0; i < allocatable.length; i++) {
            edit = facade.edit(allocatable[i]);
            edit.getClassification().setValue("studiengang", facade.getSuperCategory().getCategory("c2").getCategory("c1").getCategory("c7"));
            facade.store(edit);
        }

        // BWL-Bank
        filter = facade.getDynamicType("resource1").newClassificationFilter();
        filter.addEqualsRule("studiengang", facade.getSuperCategory().getCategory("c3").getCategory("c2").getCategory("c3"));
        filters = new ClassificationFilter[]{filter};
        allocatable = facade.getAllocatables(filters);
        for (int i = 0; i < allocatable.length; i++) {
            edit = facade.edit(allocatable[i]);
            edit.getClassification().setValue("studiengang", facade.getSuperCategory().getCategory("c2").getCategory("c4").getCategory("c3"));
            facade.store(edit);
        }

        // BWL-Handel
        filter = facade.getDynamicType("resource1").newClassificationFilter();
        filter.addEqualsRule("studiengang", facade.getSuperCategory().getCategory("c3").getCategory("c2").getCategory("c4"));
        filters = new ClassificationFilter[]{filter};
        allocatable = facade.getAllocatables(filters);
        for (int i = 0; i < allocatable.length; i++) {
            edit = facade.edit(allocatable[i]);
            edit.getClassification().setValue("studiengang", facade.getSuperCategory().getCategory("c2").getCategory("c4").getCategory("c4"));
            facade.store(edit);
        }

        // BWL-International-Business
        filter = facade.getDynamicType("resource1").newClassificationFilter();
        filter.addEqualsRule("studiengang", facade.getSuperCategory().getCategory("c3").getCategory("c2").getCategory("c5"));
        filters = new ClassificationFilter[]{filter};
        allocatable = facade.getAllocatables(filters);
        for (int i = 0; i < allocatable.length; i++) {
            edit = facade.edit(allocatable[i]);
            edit.getClassification().setValue("studiengang", facade.getSuperCategory().getCategory("c2").getCategory("c4").getCategory("c5"));
            facade.store(edit);
        }

        // BWL-Industrie
        filter = facade.getDynamicType("resource1").newClassificationFilter();
        filter.addEqualsRule("studiengang", facade.getSuperCategory().getCategory("c3").getCategory("c2").getCategory("c6"));
        filters = new ClassificationFilter[]{filter};
        allocatable = facade.getAllocatables(filters);
        for (int i = 0; i < allocatable.length; i++) {
            edit = facade.edit(allocatable[i]);
            edit.getClassification().setValue("studiengang", facade.getSuperCategory().getCategory("c2").getCategory("c4").getCategory("c6"));
            facade.store(edit);
        }

        // BWL-Steuern- und Pr�fungswesen
        filter = facade.getDynamicType("resource1").newClassificationFilter();
        filter.addEqualsRule("studiengang", facade.getSuperCategory().getCategory("c3").getCategory("c2").getCategory("c7"));
        filters = new ClassificationFilter[]{filter};
        allocatable = facade.getAllocatables(filters);
        for (int i = 0; i < allocatable.length; i++) {
            edit = facade.edit(allocatable[i]);
            edit.getClassification().setValue("studiengang", facade.getSuperCategory().getCategory("c2").getCategory("c4").getCategory("c7"));
            facade.store(edit);
        }

        // BWL-Versicherung
        filter = facade.getDynamicType("resource1").newClassificationFilter();
        filter.addEqualsRule("studiengang", facade.getSuperCategory().getCategory("c3").getCategory("c2").getCategory("c2"));
        filters = new ClassificationFilter[]{filter};
        allocatable = facade.getAllocatables(filters);
        for (int i = 0; i < allocatable.length; i++) {
            edit = facade.edit(allocatable[i]);
            edit.getClassification().setValue("studiengang", facade.getSuperCategory().getCategory("c2").getCategory("c4").getCategory("c1"));
            facade.store(edit);
        }

        // Wirtschaftsinformatik
        filter = facade.getDynamicType("resource1").newClassificationFilter();
        filter.addEqualsRule("studiengang", facade.getSuperCategory().getCategory("c3").getCategory("c2").getCategory("c1"));
        filters = new ClassificationFilter[]{filter};
        allocatable = facade.getAllocatables(filters);
        for (int i = 0; i < allocatable.length; i++) {
            edit = facade.edit(allocatable[i]);
            edit.getClassification().setValue("studiengang", facade.getSuperCategory().getCategory("c2").getCategory("c4").getCategory("c2"));
            facade.store(edit);
        }

        // BWL-Unternehmertum
        filter = facade.getDynamicType("resource1").newClassificationFilter();
        filter.addEqualsRule("studiengang", facade.getSuperCategory().getCategory("c3").getCategory("c2").getCategory("c8"));
        filters = new ClassificationFilter[]{filter};
        allocatable = facade.getAllocatables(filters);
        for (int i = 0; i < allocatable.length; i++) {
            edit = facade.edit(allocatable[i]);
            edit.getClassification().setValue("studiengang", facade.getSuperCategory().getCategory("c2").getCategory("c4").getCategory("c8"));
            facade.store(edit);
        }

        DynamicType edit3 = facade.edit(facade.getDynamicType("resource1"));
        edit3.getAttribute("studiengang").setConstraint(ConstraintIds.KEY_ROOT_CATEGORY, facade.getSuperCategory().getCategory("c2"));
        facade.store(edit3);
    }
}