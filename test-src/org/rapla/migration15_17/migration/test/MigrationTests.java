package org.rapla.migration15_17.migration.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class MigrationTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(MigrationTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(MigrationKurseTest.class);
		suite.addTestSuite(MigrationHonorarkraftTest.class);
		suite.addTestSuite(MigrationRaumTest.class);
		suite.addTestSuite(MigrationLehrveranstaltungTest.class);
		suite.addTestSuite(MigrationProfessorTest.class);
		suite.addTestSuite(LdapProfessorTest.class);
		suite.addTestSuite(MigrationMitarbeiterTest.class);
		suite.addTestSuite(LdapMitarbeiterTest.class);
		suite.addTestSuite(SteleUserTest.class);
		suite.addTestSuite(PermissionsTest.class);
		suite.addTestSuite(MigrationKategorienTest.class); 
		//$JUnit-END$
		return suite;
	}

}
