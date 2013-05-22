package org.rapla.migration15_17.migration.test;

import org.rapla.entities.User;
import org.rapla.entities.domain.Allocatable;
import org.rapla.entities.domain.Permission;
import org.rapla.entities.dynamictype.ClassificationFilter;
import org.rapla.facade.ClientFacade;
import org.rapla.migration15_17.MigrationTestCase;

public class PermissionsTest extends MigrationTestCase {
	ClientFacade facade;
	
	public PermissionsTest(String name) {
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

	public void testPermissions() throws Exception{
		ClassificationFilter filter = facade.getDynamicType("resource1").newClassificationFilter();
		ClassificationFilter[] filters = new ClassificationFilter[] {filter};
		Allocatable[] allocatable = facade.getAllocatables(filters);
		Permission permission;
		User stele = facade.getUser("stele");
		for(int i = 0; i < allocatable.length; i++)
		{
			Allocatable edit = facade.edit(allocatable[i]);
			permission = edit.newPermission();
            permission.setUser(stele);
            permission.setAccessLevel(Permission.READ);
            edit.addPermission(permission);
            facade.store(edit);
		}
		filter = facade.getDynamicType("resource2").newClassificationFilter();
		filters = new ClassificationFilter[] {filter};
		allocatable = facade.getAllocatables(filters);
		for(int i = 0; i < allocatable.length; i++)
		{
			Allocatable edit = facade.edit(allocatable[i]);
			permission = edit.newPermission();
            permission.setUser(stele);
            permission.setAccessLevel(Permission.READ);
            edit.addPermission(permission);
            facade.store(edit);
		}
		filter = facade.getDynamicType("person1").newClassificationFilter();
		filters = new ClassificationFilter[] {filter};
		allocatable = facade.getAllocatables(filters);
		for(int i = 0; i < allocatable.length; i++)
		{
			Allocatable edit = facade.edit(allocatable[i]);
			permission = edit.newPermission();
            permission.setUser(stele);
            permission.setAccessLevel(Permission.READ);
            edit.addPermission(permission);
            facade.store(edit);
		}
		filter = facade.getDynamicType("defaultPerson").newClassificationFilter();
		filters = new ClassificationFilter[] {filter};
		allocatable = facade.getAllocatables(filters);
		for(int i = 0; i < allocatable.length; i++)
		{
			Allocatable edit = facade.edit(allocatable[i]);
			permission = edit.newPermission();
            permission.setUser(stele);
            permission.setAccessLevel(Permission.READ);
            edit.addPermission(permission);
            facade.store(edit);
		}
	}
}
