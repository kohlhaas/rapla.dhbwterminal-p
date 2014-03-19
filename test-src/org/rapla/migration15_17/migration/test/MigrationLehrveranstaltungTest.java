package org.rapla.migration15_17.migration.test;

import java.util.ArrayList;
import java.util.List;

import org.rapla.entities.Category;
import org.rapla.entities.Entity;
import org.rapla.entities.domain.Reservation;
import org.rapla.entities.dynamictype.Attribute;
import org.rapla.entities.dynamictype.ClassificationFilter;
import org.rapla.entities.dynamictype.ConstraintIds;
import org.rapla.entities.dynamictype.DynamicType;
import org.rapla.facade.ClientFacade;
import org.rapla.migration15_17.MigrationTestCase;

public class MigrationLehrveranstaltungTest extends MigrationTestCase {
    ClientFacade facade;

    public MigrationLehrveranstaltungTest(String name) {
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

    public void testLehrveranstaltung() throws Exception {

        Category editCat = facade.edit(facade.getSuperCategory().getCategory("c2"));
        // Arztassistenten
        ClassificationFilter filter = facade.getDynamicType("reservation2").newClassificationFilter();
        filter.addEqualsRule("a5", facade.getSuperCategory().getCategory("c3").getCategory("c1").getCategory("c1"));
        ClassificationFilter[] filters = new ClassificationFilter[]{filter};
        Reservation[] reservation = facade.getReservations(null, null, null, filters);
        Reservation edit;

        List<Entity<?>> edits = new ArrayList<Entity<?>>(reservation.length);

        for (int i = 0; i < reservation.length; i++) {
            edit = facade.edit(reservation[i]);
            edit.getClassification().setValue("a5", facade.getSuperCategory().getCategory("c2").getCategory("c3").getCategory("c1"));
            edits.add(edit);
            //facade.store(edit);
        }
        facade.storeObjects(edits.toArray(new Entity[reservation.length]));
        editCat.getCategory("c3").getCategory("c1").setKey(
                facade.getSuperCategory().getCategory("c3").getCategory("c1").getCategory("c1").getName().getName("de"));
        System.out.println("Arztassistenten fertig");

        // Elektrotechnik
        filter = facade.getDynamicType("reservation2").newClassificationFilter();
        filter.addEqualsRule("a5", facade.getSuperCategory().getCategory("c3").getCategory("c3").getCategory("c1"));
        filters = new ClassificationFilter[]{filter};
        reservation = facade.getReservations(null, null, null, filters);
        edits = new ArrayList<Entity<?>>(reservation.length);

        for (int i = 0; i < reservation.length; i++) {
            edit = facade.edit(reservation[i]);
            edit.getClassification().setValue("a5", facade.getSuperCategory().getCategory("c2").getCategory("c1").getCategory("c1"));
            //		facade.store(edit);
            edits.add(edit);
        }
        facade.storeObjects(edits.toArray(new Entity[reservation.length]));

        editCat.getCategory("c1").getCategory("c1").setKey(
                facade.getSuperCategory().getCategory("c3").getCategory("c3").getCategory("c1").getName().getName("de"));
        System.out.println("Elektortechnik fertig");
        // Informatik
        filter = facade.getDynamicType("reservation2").newClassificationFilter();
        filter.addEqualsRule("a5", facade.getSuperCategory().getCategory("c3").getCategory("c3").getCategory("c2"));
        filters = new ClassificationFilter[]{filter};
        reservation = facade.getReservations(null, null, null, filters);
        edits = new ArrayList<Entity<?>>(reservation.length);

        for (int i = 0; i < reservation.length; i++) {
            edit = facade.edit(reservation[i]);
            edit.getClassification().setValue("a5", facade.getSuperCategory().getCategory("c2").getCategory("c1").getCategory("c2"));
            //facade.store(edit);
            edits.add(edit);
        }
        facade.storeObjects(edits.toArray(new Entity[reservation.length]));

        editCat.getCategory("c1").getCategory("c2").setKey(
                facade.getSuperCategory().getCategory("c3").getCategory("c3").getCategory("c2").getName().getName("de"));
        System.out.println("Informatik fertig");

        // Maschinenbau
        filter = facade.getDynamicType("reservation2").newClassificationFilter();
        filter.addEqualsRule("a5", facade.getSuperCategory().getCategory("c3").getCategory("c3").getCategory("c3"));
        filters = new ClassificationFilter[]{filter};
        reservation = facade.getReservations(null, null, null, filters);
        edits = new ArrayList<Entity<?>>(reservation.length);

        for (int i = 0; i < reservation.length; i++) {
            edit = facade.edit(reservation[i]);
            edit.getClassification().setValue("a5", facade.getSuperCategory().getCategory("c2").getCategory("c1").getCategory("c3"));
            edits.add(edit);
//            facade.store(edit);
        }
        facade.storeObjects(edits.toArray(new Entity[reservation.length]));

        editCat.getCategory("c1").getCategory("c3").setKey(
                facade.getSuperCategory().getCategory("c3").getCategory("c3").getCategory("c3").getName().getName("de"));
        System.out.println("Maschinenbau fertig");
        // Mechatronik
        filter = facade.getDynamicType("reservation2").newClassificationFilter();
        filter.addEqualsRule("a5", facade.getSuperCategory().getCategory("c3").getCategory("c3").getCategory("c4"));
        filters = new ClassificationFilter[]{filter};
        reservation = facade.getReservations(null, null, null, filters);
        edits = new ArrayList<Entity<?>>(reservation.length);

        for (int i = 0; i < reservation.length; i++) {
            edit = facade.edit(reservation[i]);
            edit.getClassification().setValue("a5", facade.getSuperCategory().getCategory("c2").getCategory("c1").getCategory("c4"));
            edits.add(edit);
//            facade.store(edit);
        }
        facade.storeObjects(edits.toArray(new Entity[reservation.length]));

        editCat.getCategory("c1").getCategory("c4").setKey(
                facade.getSuperCategory().getCategory("c3").getCategory("c3").getCategory("c4").getName().getName("de"));
        System.out.println("Mechatronik fertig");

        // Papiertechnik
        filter = facade.getDynamicType("reservation2").newClassificationFilter();
        filter.addEqualsRule("a5", facade.getSuperCategory().getCategory("c3").getCategory("c3").getCategory("c5"));
        filters = new ClassificationFilter[]{filter};
        reservation = facade.getReservations(null, null, null, filters);
        edits = new ArrayList<Entity<?>>(reservation.length);

        for (int i = 0; i < reservation.length; i++) {
            edit = facade.edit(reservation[i]);
            edit.getClassification().setValue("a5", facade.getSuperCategory().getCategory("c2").getCategory("c1").getCategory("c5"));
            edits.add(edit);
//            facade.store(edit);
        }
        facade.storeObjects(edits.toArray(new Entity[reservation.length]));

        editCat.getCategory("c1").getCategory("c5").setKey(
                facade.getSuperCategory().getCategory("c3").getCategory("c3").getCategory("c5").getName().getName("de"));
        System.out.println("Papiertechnik fertig");
        // Sicherheitswesen
        filter = facade.getDynamicType("reservation2").newClassificationFilter();
        filter.addEqualsRule("a5", facade.getSuperCategory().getCategory("c3").getCategory("c3").getCategory("c5"));
        filters = new ClassificationFilter[]{filter};
        reservation = facade.getReservations(null, null, null, filters);
        edits = new ArrayList<Entity<?>>(reservation.length);

        for (int i = 0; i < reservation.length; i++) {
            edit = facade.edit(reservation[i]);
            edit.getClassification().setValue("a5", facade.getSuperCategory().getCategory("c2").getCategory("c1").getCategory("c6"));
            edits.add(edit);
//            facade.store(edit);
        }
        facade.storeObjects(edits.toArray(new Entity[reservation.length]));

        editCat.getCategory("c1").getCategory("c6").setKey(
                facade.getSuperCategory().getCategory("c3").getCategory("c3").getCategory("c6").getName().getName("de"));
        System.out.println("Sicherheitswesen fertig");
        // Wirtschaftsingenieurwesen
        filter = facade.getDynamicType("reservation2").newClassificationFilter();
        filter.addEqualsRule("a5", facade.getSuperCategory().getCategory("c3").getCategory("c3").getCategory("c7"));
        filters = new ClassificationFilter[]{filter};
        reservation = facade.getReservations(null, null, null, filters);
        edits = new ArrayList<Entity<?>>(reservation.length);

        for (int i = 0; i < reservation.length; i++) {
            edit = facade.edit(reservation[i]);
            edit.getClassification().setValue("a5", facade.getSuperCategory().getCategory("c2").getCategory("c1").getCategory("c7"));
            edits.add(edit);
//            facade.store(edit);
        }
        facade.storeObjects(edits.toArray(new Entity[reservation.length]));

        editCat.getCategory("c1").getCategory("c7").setKey(
                facade.getSuperCategory().getCategory("c3").getCategory("c3").getCategory("c7").getName().getName("de"));
        System.out.println("Wirtschaftsingenieurwesen fertig");
        // BWL-Bank
        filter = facade.getDynamicType("reservation2").newClassificationFilter();
        filter.addEqualsRule("a5", facade.getSuperCategory().getCategory("c3").getCategory("c2").getCategory("c3"));
        filters = new ClassificationFilter[]{filter};
        reservation = facade.getReservations(null, null, null, filters);
        edits = new ArrayList<Entity<?>>(reservation.length);

        for (int i = 0; i < reservation.length; i++) {
            edit = facade.edit(reservation[i]);
            edit.getClassification().setValue("a5", facade.getSuperCategory().getCategory("c2").getCategory("c4").getCategory("c3"));
            edits.add(edit);
//            facade.store(edit);
        }
        facade.storeObjects(edits.toArray(new Entity[reservation.length]));

        editCat.getCategory("c4").getCategory("c3").setKey(
                facade.getSuperCategory().getCategory("c3").getCategory("c2").getCategory("c3").getName().getName("de"));
        System.out.println("BWL-Bank fertig");
        // BWL-Handel
        filter = facade.getDynamicType("reservation2").newClassificationFilter();
        filter.addEqualsRule("a5", facade.getSuperCategory().getCategory("c3").getCategory("c2").getCategory("c4"));
        filters = new ClassificationFilter[]{filter};
        reservation = facade.getReservations(null, null, null, filters);
        edits = new ArrayList<Entity<?>>(reservation.length);

        for (int i = 0; i < reservation.length; i++) {
            edit = facade.edit(reservation[i]);
            edit.getClassification().setValue("a5", facade.getSuperCategory().getCategory("c2").getCategory("c4").getCategory("c4"));
            edits.add(edit);
//            facade.store(edit);
        }
        facade.storeObjects(edits.toArray(new Entity[reservation.length]));
        editCat.getCategory("c4").getCategory("c4").setKey(
                facade.getSuperCategory().getCategory("c3").getCategory("c2").getCategory("c4").getName().getName("de"));
        System.out.println("BWL-Handel fertig");
        // BWL-International-Business
        filter = facade.getDynamicType("reservation2").newClassificationFilter();
        filter.addEqualsRule("a5", facade.getSuperCategory().getCategory("c3").getCategory("c2").getCategory("c5"));
        filters = new ClassificationFilter[]{filter};
        reservation = facade.getReservations(null, null, null, filters);
        edits = new ArrayList<Entity<?>>(reservation.length);

        for (int i = 0; i < reservation.length; i++) {
            edit = facade.edit(reservation[i]);
            edit.getClassification().setValue("a5", facade.getSuperCategory().getCategory("c2").getCategory("c4").getCategory("c5"));
            edits.add(edit);
//            facade.store(edit);
        }
        facade.storeObjects(edits.toArray(new Entity[reservation.length]));
        editCat.getCategory("c4").getCategory("c5").setKey(
                facade.getSuperCategory().getCategory("c3").getCategory("c2").getCategory("c5").getName().getName("de"));
        System.out.println("BWL-International-Business fertig");
        // BWL-Industrie
        filter = facade.getDynamicType("reservation2").newClassificationFilter();
        filter.addEqualsRule("a5", facade.getSuperCategory().getCategory("c3").getCategory("c2").getCategory("c6"));
        filters = new ClassificationFilter[]{filter};
        reservation = facade.getReservations(null, null, null, filters);
        edits = new ArrayList<Entity<?>>(reservation.length);

        for (int i = 0; i < reservation.length; i++) {
            edit = facade.edit(reservation[i]);
            edit.getClassification().setValue("a5", facade.getSuperCategory().getCategory("c2").getCategory("c4").getCategory("c6"));
            edits.add(edit);
//            facade.store(edit);
        }
        facade.storeObjects(edits.toArray(new Entity[reservation.length]));
        editCat.getCategory("c4").getCategory("c6").setKey(
                facade.getSuperCategory().getCategory("c3").getCategory("c2").getCategory("c6").getName().getName("de"));
        System.out.println("BWL-Industrie fertig");
        // BWL-Steuern- und Pr�fungswesen
        filter = facade.getDynamicType("reservation2").newClassificationFilter();
        filter.addEqualsRule("a5", facade.getSuperCategory().getCategory("c3").getCategory("c2").getCategory("c7"));
        filters = new ClassificationFilter[]{filter};
        reservation = facade.getReservations(null, null, null, filters);
        edits = new ArrayList<Entity<?>>(reservation.length);

        for (int i = 0; i < reservation.length; i++) {
            edit = facade.edit(reservation[i]);
            edit.getClassification().setValue("a5", facade.getSuperCategory().getCategory("c2").getCategory("c4").getCategory("c7"));
            edits.add(edit);
//            facade.store(edit);
        }
        facade.storeObjects(edits.toArray(new Entity[reservation.length]));
        editCat.getCategory("c4").getCategory("c7").setKey(
                facade.getSuperCategory().getCategory("c3").getCategory("c2").getCategory("c7").getName().getName("de"));
        System.out.println("BWL-Steuern- und Pr�fungswesen fertig");
        // BWL-Versicherung
        filter = facade.getDynamicType("reservation2").newClassificationFilter();
        filter.addEqualsRule("a5", facade.getSuperCategory().getCategory("c3").getCategory("c2").getCategory("c2"));
        filters = new ClassificationFilter[]{filter};
        reservation = facade.getReservations(null, null, null, filters);
        edits = new ArrayList<Entity<?>>(reservation.length);

        for (int i = 0; i < reservation.length; i++) {
            edit = facade.edit(reservation[i]);
            edit.getClassification().setValue("a5", facade.getSuperCategory().getCategory("c2").getCategory("c4").getCategory("c1"));
            edits.add(edit);
//            facade.store(edit);
        }
        facade.storeObjects(edits.toArray(new Entity[reservation.length]));
        editCat.getCategory("c4").getCategory("c1").setKey(
                facade.getSuperCategory().getCategory("c3").getCategory("c2").getCategory("c2").getName().getName("de"));
        System.out.println("BWL-Versicherung fertig");
        // Wirtschaftsinformatik
        filter = facade.getDynamicType("reservation2").newClassificationFilter();
        filter.addEqualsRule("a5", facade.getSuperCategory().getCategory("c3").getCategory("c2").getCategory("c1"));
        filters = new ClassificationFilter[]{filter};
        reservation = facade.getReservations(null, null, null, filters);
        edits = new ArrayList<Entity<?>>(reservation.length);

        for (int i = 0; i < reservation.length; i++) {
            edit = facade.edit(reservation[i]);
            edit.getClassification().setValue("a5", facade.getSuperCategory().getCategory("c2").getCategory("c4").getCategory("c2"));
            edits.add(edit);
//            facade.store(edit);
        }
        facade.storeObjects(edits.toArray(new Entity[reservation.length]));
        editCat.getCategory("c4").getCategory("c2").setKey(
                facade.getSuperCategory().getCategory("c3").getCategory("c2").getCategory("c1").getName().getName("de"));
        System.out.println("Wirtschaftsinformatik fertig");
        // BWL-Unternehmertum
        filter = facade.getDynamicType("reservation2").newClassificationFilter();
        filter.addEqualsRule("a5", facade.getSuperCategory().getCategory("c3").getCategory("c2").getCategory("c8"));
        filters = new ClassificationFilter[]{filter};
        reservation = facade.getReservations(null, null, null, filters);
        edits = new ArrayList<Entity<?>>(reservation.length);

        for (int i = 0; i < reservation.length; i++) {
            edit = facade.edit(reservation[i]);
            edit.getClassification().setValue("a5", facade.getSuperCategory().getCategory("c2").getCategory("c4").getCategory("c8"));
            edits.add(edit);
//            facade.store(edit);
        }
        facade.storeObjects(edits.toArray(new Entity[reservation.length]));
        editCat.getCategory("c4").getCategory("c8").setKey(
                facade.getSuperCategory().getCategory("c3").getCategory("c2").getCategory("c8").getName().getName("de"));
        System.out.println("BWL-Unternehmertum fertig");
        // OberKategorien
        editCat.getCategory("c3").setKey("G");
        editCat.getCategory("c1").setKey("T");
        editCat.getCategory("c4").setKey("W");
        editCat.getCategory("c5").setKey("A");


        DynamicType edit2 = facade.edit(facade.getDynamicType("reservation2"));
        Attribute att = edit2.getAttribute("a5");
        att.setKey("studiengang");
        att.getName().setName("de", "Studiengang");
        Category cat = facade.getSuperCategory().getCategory("c2");
        att.setConstraint(ConstraintIds.KEY_ROOT_CATEGORY, cat);
        facade.store(edit2);
        facade.store(editCat);
    }
}
