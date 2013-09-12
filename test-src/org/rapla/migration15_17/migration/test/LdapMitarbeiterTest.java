package org.rapla.migration15_17.migration.test;

import org.rapla.RaplaMainContainer;
import org.rapla.RaplaStartupEnvironment;
import org.rapla.RaplaTestCase;
import org.rapla.ServletTestBase;
import org.rapla.components.util.IOUtil;
import org.rapla.entities.Category;
import org.rapla.entities.domain.Allocatable;
import org.rapla.entities.dynamictype.Attribute;
import org.rapla.entities.dynamictype.AttributeType;
import org.rapla.entities.dynamictype.ClassificationFilter;
import org.rapla.entities.dynamictype.DynamicType;
import org.rapla.facade.ClientFacade;
import org.rapla.framework.Container;
import org.rapla.framework.logger.ConsoleLogger;
import org.rapla.framework.logger.Logger;
import org.rapla.migration15_17.MigrationTestCase;
import org.rapla.migration15_17.ldap.test.LDAPQuery;
import org.rapla.migration15_17.ldap.test.LDAPQueryImpl;
import org.rapla.server.ServerService;
import org.rapla.server.ServerServiceContainer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class LdapMitarbeiterTest extends MigrationTestCase {
    //ServerService raplaServer;
    	Logger logger = new ConsoleLogger(ConsoleLogger.LEVEL_WARN).getChildLogger("test");
    ClientFacade facade;
//    Locale locale;
//    protected RaplaStartupEnvironment env = new RaplaStartupEnvironment();
//    protected Container raplaContainer;
    public static String TEST_FOLDER_NAME="temp/test";
	public LdapMitarbeiterTest(String name) {
		super(name);
		try {
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
		
	}

	protected void setUp() throws Exception {
        super.setUp("test.xml");
        facade = getContext().lookup(ClientFacade.class);
        facade.login("admin", "".toCharArray());

/*
        super.setUp();
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
            IOUtil.copy( "temp/test" + "/test.xml", "test-src" + "/test.xml" );
       } catch (IOException ex) {
           throw new IOException("Failed to copy TestFile '" + "/test.xml" + "': " + ex.getMessage());
       }
	}
	
	public void testLDAPConnection () throws Exception {
        LDAPQuery ldapQuery = new LDAPQueryImpl(getContext());
        String password =  LDAPQuery.PASSWORD;
        Map<String,Map<String,String>> ldapValues = ldapQuery.getLDAPValues(
                LDAPQuery.SEARCH_TERM_ALL, password
        );

        DynamicType personType = facade.getDynamicType("person2");
        if (personType.getAttribute("telefon") == null)
        {
            Attribute attribute = facade.newAttribute(AttributeType.STRING);
            attribute.setKey("telefon");
            attribute.getName().setName("de","Telefon");
            DynamicType editPerson2 = facade.edit(personType);
            editPerson2.addAttribute(attribute);
            facade.store(editPerson2);
            personType = facade.getDynamicType("person2");
        }


        ClassificationFilter filter = personType.newClassificationFilter();

        ClassificationFilter[] filters = new ClassificationFilter[] {filter};
        Allocatable[] mitarbeiter = facade.getAllocatables(filters);
        
        for (Map.Entry<String, Map<String, String>> stringMapEntry : ldapValues.entrySet()) {
            //System.out.println(ldapValues.get(stringMapEntry.getKey()).get("sn"));
            filter = personType.newClassificationFilter();
            filter.addEqualsRule("forename", ldapValues.get(stringMapEntry.getKey()).get("givenName"));
            filter.addEqualsRule("surname", ldapValues.get(stringMapEntry.getKey()).get("sn"));
            filters = new ClassificationFilter[] {filter};
            mitarbeiter = facade.getAllocatables(filters);
            Allocatable mitarbeiterEdit;
            for (int i = 0; i < mitarbeiter.length; i++)
            {
            mitarbeiterEdit = facade.edit(mitarbeiter[i]);
            //Telefon
            mitarbeiterEdit.getClassification().setValue("telefon", ldapValues.get(stringMapEntry.getKey()).get("telephoneNumber"));
            //Email
            mitarbeiterEdit.getClassification().setValue("email", ldapValues.get(stringMapEntry.getKey()).get("mail"));
            //Raum
            //Bestehender Raum
            Category[] cats = facade.getSuperCategory().getCategory("c9").getCategory(
            		ldapValues.get(stringMapEntry.getKey()).get("physicalDeliveryOfficeName").substring(0, 1)).getCategories();
            ArrayList<String> catsStr = new ArrayList<String>();
            for (int j = 0; j < cats.length; j++)
            {
            	catsStr.add(cats[j].getKey());
            }
            if(catsStr.contains(
            				ldapValues.get(stringMapEntry.getKey()).get("physicalDeliveryOfficeName")))
            {
            mitarbeiterEdit.getClassification().setValue("raum", facade.getSuperCategory().getCategory("c9").getCategory(
            		ldapValues.get(stringMapEntry.getKey()).get("physicalDeliveryOfficeName").substring(0, 1)).getCategory(
            				ldapValues.get(stringMapEntry.getKey()).get("physicalDeliveryOfficeName")));
            }
            //Neuen Raum anlegen
            else
            {
            	Category editCat = facade.edit(facade.getSuperCategory());
            	Category newCat = facade.newCategory();
            	newCat.setKey(makeValidKey(ldapValues.get(stringMapEntry.getKey()).get("physicalDeliveryOfficeName")));
            	newCat.getName().setName("de", ldapValues.get(stringMapEntry.getKey()).get("physicalDeliveryOfficeName").substring(1, 
            			ldapValues.get(stringMapEntry.getKey()).get("physicalDeliveryOfficeName").length()));
            	editCat.getCategory("c9").getCategory(ldapValues.get(stringMapEntry.getKey()).get("physicalDeliveryOfficeName").substring(0, 1)).
            	addCategory(newCat);
            	facade.store(editCat);
            	mitarbeiterEdit.getClassification().setValue("raum", facade.getSuperCategory().getCategory("c9").getCategory(
                		ldapValues.get(stringMapEntry.getKey()).get("physicalDeliveryOfficeName").substring(0, 1)).getCategory(
                				ldapValues.get(stringMapEntry.getKey()).get("physicalDeliveryOfficeName")));
            }
            //Studiengang
            if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Fakultät Wirtschaft"))
            {
            	if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Bank"))
            	{
            		mitarbeiterEdit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("W").getCategory("BK"));
            	}
            	else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Handel"))
            	{
            		mitarbeiterEdit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("W").getCategory("HD"));
            	}
            	else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Business"))
            	{
            		mitarbeiterEdit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("W").getCategory("IB"));
            	}
            	else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Industrie"))
            	{
            		mitarbeiterEdit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("W").getCategory("IN"));
            	}
            	else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("RSW"))
            	{
            		mitarbeiterEdit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("W").getCategory("SP"));
            	}
            	else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Unternehmertum"))
            	{
            		mitarbeiterEdit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("W").getCategory("UN"));
            	}
            	else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Versicherung"))
            	{
            		mitarbeiterEdit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("W").getCategory("VS"));
            	}
            	else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Wirtschaftsinformatik"))
            	{
            		mitarbeiterEdit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("W").getCategory("WI"));
            	}
            	else
            	{
            		mitarbeiterEdit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("W"));
            	}
            }
            else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Fakultät Technik"))
            {
            	if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Elektrotechnik"))
            	{
            		mitarbeiterEdit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("T").getCategory("EL"));
            	}
            	else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Informatik"))
            	{
            		mitarbeiterEdit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("T").getCategory("INF"));
            	}
            	else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Maschinenbau"))
            	{
            		mitarbeiterEdit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("T").getCategory("MB"));
            	}
            	else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Mechatronik"))
            	{
            		mitarbeiterEdit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("T").getCategory("MT"));
            	}
            	else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Papiertechnik"))
            	{
            		mitarbeiterEdit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("T").getCategory("PT"));
            	}
            	else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Sicherheitswesen"))
            	{
            		mitarbeiterEdit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("T").getCategory("SHE"));
            	}
            	else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Wirtschaftsingenieur"))
            	{
            		mitarbeiterEdit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("T").getCategory("WIW"));
            	}
            	else
            	{
            		mitarbeiterEdit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("T"));
            	}
            }
            else if (ldapValues.get(stringMapEntry.getKey()).get("department").contains("Arztassistent"))
            {
            	mitarbeiterEdit.getClassification().setValue("abteilung", facade.getSuperCategory().getCategory("c2").getCategory("G").getCategory("PA"));
            }
            facade.store(mitarbeiterEdit);
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
