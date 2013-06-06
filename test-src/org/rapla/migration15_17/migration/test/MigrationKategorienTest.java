package org.rapla.migration15_17.migration.test;

import org.rapla.entities.Category;
import org.rapla.facade.ClientFacade;
import org.rapla.migration15_17.MigrationTestCase;

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
	}

}
