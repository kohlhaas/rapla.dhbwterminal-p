package org.rapla.migration15_17.migration.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.rapla.components.util.IOUtil;
import org.rapla.entities.Category;
import org.rapla.entities.domain.Allocatable;
import org.rapla.entities.dynamictype.ClassificationFilter;
import org.rapla.facade.ClientFacade;
import org.rapla.framework.logger.ConsoleLogger;
import org.rapla.framework.logger.Logger;
import org.rapla.migration15_17.MigrationTestCase;
import org.rapla.migration15_17.ldap.test.LDAPQuery;
import org.rapla.migration15_17.ldap.test.LDAPQueryImpl;

public class LdapProfessorTest extends MigrationTestCase {
    //ServerService raplaServer;
    Logger logger = new ConsoleLogger(ConsoleLogger.LEVEL_WARN).getChildLogger("test");
    ClientFacade facade;
    //Locale locale;
    //protected RaplaStartupEnvironment env = new RaplaStartupEnvironment();
    //protected Container raplaContainer;
    public static String TEST_FOLDER_NAME = "temp/test";

    public LdapProfessorTest(String name) {
        super(name);
        /*try {
            new File("temp").mkdir();
            File testFolder =new File(TEST_FOLDER_NAME);
            System.setProperty("jetty.home",testFolder.getPath());
            testFolder.mkdir();
            IOUtil.copy( "test-src/test.xconf", TEST_FOLDER_NAME + "/test.xconf" );
            //IOUtil.copy( "test-src/test.xlog", TEST_FOLDER_NAME + "/test.xlog" );
        } catch (IOException ex) {
            throw new RuntimeException("Can't initialize config-files: " + ex.getMessage());
        }
        try
        {
        	Class<?> forName = RaplaTestCase.class.getClassLoader().loadClass("org.slf4j.bridge.SLF4JBridgeHandler");
        	forName.getMethod("removeHandlersForRootLogger", new Class[] {}).invoke(null, new Object[] {});
        	forName.getMethod("install", new Class[] {}).invoke(null, new Object[] {});
        }
        catch (Exception ex)
        {
        	getLogger().warn("Can't install logging bridge  " + ex.getMessage());
        	// Todo bootstrap log
        }
		*/
    }

    protected void setUp() throws Exception {
        super.setUp("test.xml");
        facade = getContext().lookup(ClientFacade.class);
        facade.login("admin", "".toCharArray());

/*
        Container container = getContainer();
        ServerServiceContainer raplaServerContainer = container.lookup(ServerServiceContainer.class,getStorageName());
        raplaServer = raplaServerContainer.getContext().lookup( ServerService.class);


        URL configURL = new URL("file:./" + TEST_FOLDER_NAME + "/test.xconf");
        env.setConfigURL( configURL);
        try {
            IOUtil.copy( "test-src/" + "test.xml", TEST_FOLDER_NAME + "/test.xml" );
       } catch (IOException ex) {
           throw new IOException("Failed to copy TestFile '" + "test-src/" + "test.xml" + "': " + ex.getMessage());
       }
        raplaContainer = new RaplaMainContainer( env );

        facade = (ClientFacade) raplaContainer.getContext().lookup(ClientFacade.class);
        facade.login("admin", "".toCharArray());
        
        locale = Locale.getDefault();
*/
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        if (raplaContainer != null)
            raplaContainer.dispose();
        try {
            IOUtil.copy("temp/test" + "/test.xml", "test-src" + "/test.xml");
        } catch (IOException ex) {
            throw new IOException("Failed to copy TestFile '" + "/test.xml" + "': " + ex.getMessage());
        }
    }

    public void testLDAPConnection() throws Exception {
        LDAPQuery ldapQuery = new LDAPQueryImpl(getContext());
        String password = LDAPQuery.PASSWORD;
        Map<String, Map<String, String>> ldapValues = ldapQuery.getLDAPValues(
                LDAPQuery.SEARCH_TERM_ABTEILUNGEN, password
        );

        String personKey = "defaultPerson"; //"defaultPerson";
        ClassificationFilter filter = facade.getDynamicType(personKey).newClassificationFilter();
        ClassificationFilter[] filters = new ClassificationFilter[]{filter};
        Allocatable[] professor = facade.getAllocatables(filters);

        for (Map.Entry<String, Map<String, String>> stringMapEntry : ldapValues.entrySet()) {
            //System.out.println(ldapValues.get(stringMapEntry.getKey()).get("sn"));
            String physicalDeliveryOfficeNameValue = ldapValues.get(stringMapEntry.getKey()).get("physicalDeliveryOfficeName");
            String physicalDeliveryOfficeNameKey = makeValidKey(physicalDeliveryOfficeNameValue);
            String physicalDeliveryOfficeNameFluegelKey = physicalDeliveryOfficeNameValue.substring(0, 1);

            filter = facade.getDynamicType(personKey).newClassificationFilter();
            filter.addEqualsRule("forename", ldapValues.get(stringMapEntry.getKey()).get("givenName"));
            filter.addEqualsRule("surname", ldapValues.get(stringMapEntry.getKey()).get("sn"));
            filters = new ClassificationFilter[]{filter};
            professor = facade.getAllocatables(filters);
            Allocatable profEdit;
            int counter;
            if (professor.length > 0)
                counter = professor.length;
            else
                counter = 1;
            for (int i = 0; i < counter; i++) {
                if (professor.length >= 1) {
                    profEdit = facade.edit(professor[i]);
                    //Telefon
                    profEdit.getClassification().setValue("telefon", "+49 (0)721 9735 - " + ldapValues.get(stringMapEntry.getKey()).get("telephoneNumber"));
                    //Email
                    profEdit.getClassification().setValue("email", ldapValues.get(stringMapEntry.getKey()).get("mail"));
                    //Raum
                    //Bestehender Raum
                    Category[] cats = facade.getSuperCategory().getCategory("c9").getCategory(
                            physicalDeliveryOfficeNameFluegelKey).getCategories();
                    ArrayList<String> catsStr = new ArrayList<String>();
                    for (Category cat : cats) {
                        catsStr.add(cat.getKey());
                    }
                    if (catsStr.contains(physicalDeliveryOfficeNameKey)) {
                        profEdit.getClassification().setValue("raum", facade.getSuperCategory().getCategory("c9").getCategory(
                                physicalDeliveryOfficeNameFluegelKey).getCategory(
                                physicalDeliveryOfficeNameKey));
                    }
                    //Neuen Raum anlegen
                    else {
                        Category editCat = facade.edit(facade.getSuperCategory());
                        Category newCat = facade.newCategory();
                        newCat.setKey(physicalDeliveryOfficeNameKey);
                        newCat.getName().setName("de", physicalDeliveryOfficeNameValue.substring(1,
                                physicalDeliveryOfficeNameValue.length()));
                        editCat.getCategory("c9").getCategory(physicalDeliveryOfficeNameFluegelKey).
                                addCategory(newCat);
                        facade.store(editCat);
                        profEdit.getClassification().setValue("raum", facade.getSuperCategory().getCategory("c9").getCategory(
                                physicalDeliveryOfficeNameFluegelKey).getCategory(physicalDeliveryOfficeNameKey));
                    }
                    //Studiengang
                    if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Fakultät Wirtschaft")) {
                        if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Bank")) {
                            profEdit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("W").getCategory("BK"));
                        } else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Handel")) {
                            profEdit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("W").getCategory("HD"));
                        } else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Business")) {
                            profEdit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("W").getCategory("IB"));
                        } else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Industrie")) {
                            profEdit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("W").getCategory("IN"));
                        } else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("RSW")) {
                            profEdit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("W").getCategory("SP"));
                        } else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Unternehmertum")) {
                            profEdit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("W").getCategory("UN"));
                        } else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Versicherung")) {
                            profEdit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("W").getCategory("VS"));
                        } else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Wirtschaftsinformatik")) {
                            profEdit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("W").getCategory("WI"));
                        } else {
                            profEdit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("W"));
                        }
                    } else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Fakultät Technik")) {
                        if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Elektrotechnik")) {
                            profEdit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("T").getCategory("EL"));
                        } else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Informatik")) {
                            profEdit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("T").getCategory("INF"));
                        } else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Maschinenbau")) {
                            profEdit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("T").getCategory("MB"));
                        } else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Mechatronik")) {
                            profEdit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("T").getCategory("MT"));
                        } else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Papiertechnik")) {
                            profEdit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("T").getCategory("PT"));
                        } else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Sicherheitswesen")) {
                            profEdit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("T").getCategory("SHE"));
                        } else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Wirtschaftsingenieur")) {
                            profEdit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("T").getCategory("WIW"));
                        } else {
                            profEdit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("T"));
                        }
                    } else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Arztassistent")) {
                        profEdit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("G").getCategory("PA"));
                    }
                    facade.store(profEdit);
                } else {
                    Allocatable person = facade.newPerson();
                    person.setClassification(facade.getDynamicType(personKey).newClassification());
                    person.getClassification().setValue("forename", ldapValues.get(stringMapEntry.getKey()).get("givenName"));
                    person.getClassification().setValue("surname", ldapValues.get(stringMapEntry.getKey()).get("sn"));
                    person.getClassification().setValue("telefon", "+49 (0)721 9735 - " + ldapValues.get(stringMapEntry.getKey()).get("telephoneNumber"));
                    person.getClassification().setValue("email", ldapValues.get(stringMapEntry.getKey()).get("mail"));
                    //Raum
                    //Bestehender Raum
                    Category[] cats = facade.getSuperCategory().getCategory("c9").getCategory(
                            physicalDeliveryOfficeNameFluegelKey).getCategories();
                    ArrayList<String> catsStr = new ArrayList<String>();
                    for (int j = 0; j < cats.length; j++) {
                        catsStr.add(cats[j].getKey());
                    }
                    if (catsStr.contains(physicalDeliveryOfficeNameKey)) {
                            //ldapValues.get(stringMapEntry.getKey()).get("physicalDeliveryOfficeName"))) {
                        person.getClassification().setValue("raum", facade.getSuperCategory().getCategory("c9").getCategory(
                                physicalDeliveryOfficeNameFluegelKey).getCategory(physicalDeliveryOfficeNameKey));
                                //ldapValues.get(stringMapEntry.getKey()).get("physicalDeliveryOfficeName")));
                    }
                    //Neuen Raum anlegen
                    else {
                        Category editCat = facade.edit(facade.getSuperCategory());
                        Category newCat = facade.newCategory();
                        newCat.setKey(physicalDeliveryOfficeNameKey);
                        newCat.getName().setName("de", physicalDeliveryOfficeNameValue.substring(1,
                                physicalDeliveryOfficeNameValue.length()));
                        editCat.getCategory("c9").getCategory(physicalDeliveryOfficeNameFluegelKey).
                                addCategory(newCat);
                        facade.store(editCat);
                        person.getClassification().setValue("raum", facade.getSuperCategory().getCategory("c9").getCategory(
                                physicalDeliveryOfficeNameFluegelKey).getCategory(physicalDeliveryOfficeNameKey));
                                //ldapValues.get(stringMapEntry.getKey()).get("physicalDeliveryOfficeName")));
                    }
                    //Studiengang
                    if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Fakultät Wirtschaft")) {
                        if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Bank")) {
                            person.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("W").getCategory("BK"));
                        } else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Handel")) {
                            person.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("W").getCategory("HD"));
                        } else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Business")) {
                            person.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("W").getCategory("IB"));
                        } else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Industrie")) {
                            person.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("W").getCategory("IN"));
                        } else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("RSW")) {
                            person.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("W").getCategory("SP"));
                        } else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Unternehmertum")) {
                            person.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("W").getCategory("UN"));
                        } else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Versicherung")) {
                            person.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("W").getCategory("VS"));
                        } else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Wirtschaftsinformatik")) {
                            person.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("W").getCategory("WI"));
                        } else {
                            person.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("W"));
                        }
                    } else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Fakultät Technik")) {
                        if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Elektrotechnik")) {
                            person.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("T").getCategory("EL"));
                        } else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Informatik")) {
                            person.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("T").getCategory("INF"));
                        } else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Maschinenbau")) {
                            person.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("T").getCategory("MB"));
                        } else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Mechatronik")) {
                            person.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("T").getCategory("MT"));
                        } else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Papiertechnik")) {
                            person.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("T").getCategory("PT"));
                        } else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Sicherheitswesen")) {
                            person.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("T").getCategory("SHE"));
                        } else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Wirtschaftsingenieur")) {
                            person.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("T").getCategory("WIW"));
                        } else {
                            person.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("T"));
                        }
                    } else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Arztassistent")) {
                        person.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("G").getCategory("PA"));
                    }
                    facade.store(person);
                }
            }
        }
        //System.out.println(ldapValues.size());

        //System.out.println(ldapValues.get("k�stermann.roland").get("department"));
/*
        
        
        for (int i = 0; i <allocatable.length; i++)
        {
        	System.out.println(allocatable[i].toString());
        }
*/
    }

    protected Logger getLogger() {
        return logger;
    }

}
