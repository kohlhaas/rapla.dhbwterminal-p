package org.rapla.migration15_17.migration.test;

import org.rapla.entities.Category;
import org.rapla.entities.NamedComparator;
import org.rapla.facade.ClientFacade;
import org.rapla.migration15_17.MigrationTestCase;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class MigrationKategorienTest extends MigrationTestCase {
ClientFacade facade;
	
	public MigrationKategorienTest(String name) {
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
	
	public void testDeleteKennzeichen() throws Exception {
		Category root = facade.edit(facade.getSuperCategory());
		if (root.getCategory("c3") != null)
		root.removeCategory(facade.getSuperCategory().getCategory("c3"));
		root.getCategory("c9").getName().setName("de", "Räume");
		facade.store(root);

        //sortieren der Räume
        Category[] fluegelCats = facade.getSuperCategory().getCategory("c9").getCategories();
        for (Category fluegel : fluegelCats) {
            System.out.println ("Sorting fluegel " + fluegel);
            Category[] rooms = fluegel.getCategories();
            Arrays.sort(rooms, new NamedComparator<Category>(Locale.GERMAN));
            Category edit = facade.edit(fluegel);
            for (Category room : fluegel.getCategories()) {
                edit.removeCategory(room);
            }
            for (Category room : rooms) {
                edit.addCategory(room);
            }
            facade.store(edit);
        }

    }
}
