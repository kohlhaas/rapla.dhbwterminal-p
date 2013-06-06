package org.rapla.migration15_17.migration.test;

import org.rapla.entities.Category;
import org.rapla.entities.domain.Allocatable;
import org.rapla.entities.dynamictype.*;
import org.rapla.facade.ClientFacade;
import org.rapla.migration15_17.MigrationTestCase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MigrationHonorarkraftTest extends MigrationTestCase {
    ClientFacade facade;

    public MigrationHonorarkraftTest(String name) {
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

    public void testTest() throws Exception {
        ClassificationFilter filter = facade.getDynamicType("person1").newClassificationFilter();
        ClassificationFilter[] filters = new ClassificationFilter[]{filter};
        Allocatable[] allocatables = facade.getAllocatables(filters);
        List<String> list = new ArrayList();
        List<String> list2 = new ArrayList();
        List<String> list3 = new ArrayList();
        for (int i = 0; i < allocatables.length; i++) {
            if (facade.getDynamicType("person1").getAttribute("a1") != null) {
                Object a1 = allocatables[i].getClassification().getValue("a1");
                if (a1 != null) {
                    if (!list.contains(a1.toString())) {
                        Object forename = allocatables[i].getClassification().getValue("forename");
                        list2.add(forename == null ? "" : forename.toString());
                        Object surname = allocatables[i].getClassification().getValue("surname");
                        list3.add(surname == null ? "" : surname.toString());
                        list.add(a1 == null ? "" : a1.toString());
                    }
                }
            }
        }
        Object[] listarray = list.toArray();
        Object[] listarray2 = list2.toArray();
        Object[] listarray3 = list3.toArray();
        /*	for (int i = 0; i < listarray.length; i++)
          {
              System.out.print(listarray2[i].toString() + " " + listarray3[i].toString() + " , ");
              System.out.println(listarray[i].toString());
          }
      */
        //TODO Add missing categories


        //Delete Attributes last-modified, a1 and add attribute bild, change studiengang to abteilung
        boolean zuletztExists = false;
        boolean fachrichtungExists = false;
        boolean bildExists = false;
        boolean abteilungExists = false;
        String[] keys = new String[facade.getDynamicType("person1").getAttributes().length];
        for (int i = 0; i < keys.length; i++) {
            if (facade.getDynamicType("person1").getAttributes()[i].getKey().equals("last-modified"))
                zuletztExists = true;
            if (facade.getDynamicType("person1").getAttributes()[i].getKey().equals("a1"))
                fachrichtungExists = true;
            if (facade.getDynamicType("person1").getAttributes()[i].getKey().equals("bild"))
                bildExists = true;
            if (facade.getDynamicType("person1").getAttributes()[i].getKey().equals("abteilung"))
                abteilungExists = true;
        }
        Attribute bild = facade.newAttribute(AttributeType.STRING);
        bild.setKey("bild");
        bild.getName().setName("de", "Bild");
        bild.getName().setName("en", "Image");
        final DynamicType editHKraft = facade.edit(facade.getDynamicType("person1"));
        if (zuletztExists) {
            editHKraft.removeAttribute(editHKraft.getAttribute("last-modified"));
        }
        if (fachrichtungExists) {
            editHKraft.getAttribute("a1").setAnnotation(AttributeAnnotations.KEY_EDIT_VIEW, AttributeAnnotations.VALUE_EDIT_VIEW_NO_VIEW);
        }
        if (!bildExists) {
            editHKraft.addAttribute(bild);
            facade.store(editHKraft);
            editHKraft.getAttribute("bild").setDefaultValue("dummy");
            facade.store(editHKraft);
        }
        if (!abteilungExists) {
            editHKraft.getAttribute("a4").setKey("abteilung");
            editHKraft.getAttribute("abteilung").getName().setName("de", "Abteilung");
            editHKraft.getAttribute("abteilung").setAnnotation(AttributeAnnotations.KEY_MULTI_SELECT, "true");
            editHKraft.getAttribute("abteilung").setConstraint(ConstraintIds.KEY_ROOT_CATEGORY, facade.getSuperCategory().getCategory("c2"));
        }
        facade.store(editHKraft);
        assertNull(facade.getDynamicType("person1").getAttribute("last-modified"));
        assertTrue(facade.getDynamicType("person1").getAttribute("a1").getAnnotation(AttributeAnnotations.KEY_EDIT_VIEW).equals(AttributeAnnotations.VALUE_EDIT_VIEW_NO_VIEW));
        assertNotNull(facade.getDynamicType("person1").getAttribute("bild"));
        assertNotNull(facade.getDynamicType("person1").getAttribute("abteilung"));

        Allocatable edit;
        for (int i = 0; i < listarray.length; i++) {
            boolean treffer = false;
            ClassificationFilter filter2 = facade.getDynamicType("person1").newClassificationFilter();
            filter2.addEqualsRule("forename", listarray2[i].toString());
            filter2.addEqualsRule("surname", listarray3[i].toString());
            ClassificationFilter[] filters2 = {filter2};
            Allocatable[] multiselect = facade.getAllocatables(filters2);
            Collection<Category> categories = new ArrayList<Category>();
            if (multiselect.length > 0) {
                edit = facade.edit(multiselect[0]);
                categories.clear();
                if (listarray[i].toString().contains("Bank") || listarray[i].toString().contains("BWL-BK")) {
                    categories.add(facade.getSuperCategory().getCategory("c2").getCategory("c4").getCategory("c3"));
                    treffer = true;
                }
                if (listarray[i].toString().contains("Steuer") || listarray[i].toString().contains("BWL-SP")) {
                    categories.add(facade.getSuperCategory().getCategory("c2").getCategory("c4").getCategory("c7"));
                    treffer = true;
                }
                if (listarray[i].toString().contains("Handel") || listarray[i].toString().contains("BWL-HD")) {
                    categories.add(facade.getSuperCategory().getCategory("c2").getCategory("c4").getCategory("c4"));
                    treffer = true;
                }
                if (listarray[i].toString().contains("Industrie") || listarray[i].toString().contains("BWL-IN")) {
                    categories.add(facade.getSuperCategory().getCategory("c2").getCategory("c4").getCategory("c6"));
                    treffer = true;
                }
                if (listarray[i].toString().contains("Business") || listarray[i].toString().contains("BWL-IB")) {
                    categories.add(facade.getSuperCategory().getCategory("c2").getCategory("c4").getCategory("c5"));
                    treffer = true;
                }
                if (listarray[i].toString().contains("Unternehmer") || listarray[i].toString().contains("BWL-UN")) {
                    categories.add(facade.getSuperCategory().getCategory("c2").getCategory("c4").getCategory("c8"));
                    treffer = true;
                }
                if (listarray[i].toString().contains("Versicherung") || listarray[i].toString().contains("BWL-VS")) {
                    categories.add(facade.getSuperCategory().getCategory("c2").getCategory("c4").getCategory("c1"));
                    treffer = true;
                }
                if (listarray[i].toString().contains("Wirtschaftsinformatik") || listarray[i].toString().contains("WWI")) {
                    categories.add(facade.getSuperCategory().getCategory("c2").getCategory("c4").getCategory("c2"));
                    treffer = true;
                }
                if (listarray[i].toString().contains("Elektrotechnik") || listarray[i].toString().contains("EL")) {
                    categories.add(facade.getSuperCategory().getCategory("c2").getCategory("c1").getCategory("c1"));
                    treffer = true;
                }
                if (listarray[i].toString().contains("Informatik") || listarray[i].toString().contains("INF")) {
                    categories.add(facade.getSuperCategory().getCategory("c2").getCategory("c1").getCategory("c2"));
                    treffer = true;
                }
                if (listarray[i].toString().contains("Maschinenbau") || listarray[i].toString().contains("MB")) {
                    categories.add(facade.getSuperCategory().getCategory("c2").getCategory("c1").getCategory("c3"));
                    treffer = true;
                }
                if (listarray[i].toString().contains("Mechatronik") || listarray[i].toString().contains("MT")) {
                    categories.add(facade.getSuperCategory().getCategory("c2").getCategory("c1").getCategory("c4"));
                    treffer = true;
                }
                if (listarray[i].toString().contains("Papiertechnik") || listarray[i].toString().contains("PT")) {
                    categories.add(facade.getSuperCategory().getCategory("c2").getCategory("c1").getCategory("c5"));
                    treffer = true;
                }
                if (listarray[i].toString().contains("Sicherheitswesen") || listarray[i].toString().contains("SHE")) {
                    categories.add(facade.getSuperCategory().getCategory("c2").getCategory("c1").getCategory("c6"));
                    treffer = true;
                }
                if (listarray[i].toString().contains("Wirtschaftsingenieur") || listarray[i].toString().contains("WIW")) {
                    categories.add(facade.getSuperCategory().getCategory("c2").getCategory("c1").getCategory("c7"));
                    treffer = true;
                }
                if (listarray[i].toString().contains("Arzt") || listarray[i].toString().contains("PA")) {
                    categories.add(facade.getSuperCategory().getCategory("c2").getCategory("c3").getCategory("c1"));
                    treffer = true;
                }
                if (!treffer) {
                    System.out.println("Keine Zuordnung für: [" + listarray2[i].toString() + " " + listarray3[i].toString() + "]" + " [" + listarray[i].toString() + "]");
                }
                edit.getClassification().setValues(facade.getDynamicType("person1").getAttribute("abteilung"), categories);
                facade.store(edit);
            }

        }
    }
}
