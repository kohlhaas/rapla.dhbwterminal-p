package org.rapla.migration15_17.ldap.test;

import java.util.Map;

public abstract interface LDAPQuery {
    String CONNECTION_USERNAME = "CN=shibtest,OU=Benutzer-fürGruppen, DC=dhbw-karlsruhe, DC=aa";
    String CONNECTION_URL = "ldap://10.203.15.7:389";
    String CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
    String SEARCH_USER_BASE = "DC=dhbw-karlsruhe,DC=aa";
    String PASSWORD = "XXX";

    String[] SEARCH_TERM_ABTEILUNGEN = {
            "(memberOf=CN=us-interneDozierendeTechnik,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
            "(memberOf=CN=us-StudiengangsleitungTechnik,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
            "(memberOf=CN=us-interneDozierendeWirtschft,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
            "(memberOf=CN=us-StudiengangsleitungWirtschaft,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)"};

    String[] SEARCH_TERM_ALL = //{"(memberOf=CN=us-Alle,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)"};
            {
                    "(memberOf=CN=us-AkademischesAuslandsamt,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-Alle,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-alleProjekte,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-alleRektorat,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-alleTechnik,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-alleVerwaltung,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-alleWirtschaft,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-Bibliothek,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-FakultaetsratTechnik,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-hausdienst,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-Hochschulkommunikation,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-interneDozierende,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-interneDozierendeTechnik,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-interneDozierendeWirtschft,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-justiziariat,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-Labore,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-Leitung,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-mlz,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-Personalrat,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-Poststelle,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-Professorenschaft,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-ProfessorenschaftTechnik,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-ProfessorenschaftWirtschaft,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-ProjektOptes,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-ProjektOptesLeitung,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-ProjektRapla,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-Prorektorat-Technik,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-Prorektorat-Wirtschft,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-Raumplanung,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-Rechenzentrum,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-rektor-prorektoren,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-Rektorat,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-Sekretariate,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-SekretariateStudiengaenge,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-SekretariateTechnik,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-SekretariateVerwaltung-Rektorat,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-SekretariateWirtschaft,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-StudiengangsassistenzTechnik,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-studiengangsassistenzWirtschaft,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-Studiengangsleitung,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-StudiengangsleitungTechnik,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-StudiengangsleitungWirtschaft,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-unterstuetzung-technik,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-unterstuetzung-wirtschaft,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
                    "(memberOf=CN=us-Verwaltung,OU=Abteilungen,OU=Dateiserver,DC=dhbw-karlsruhe,DC=aa)",
            };

    String[] PERSON_RESULT_ATTRIBUTES = {"cn", "sn", "givenName", "mail", "physicalDeliveryOfficeName", "telephoneNumber", "department"};

    String[] MEMBER_ATTRIBUTES = {"objectGUID"};


    /**
     * get all persons as a map who belong to the given departments (ldap attribute memberOf)
     *
     * @param searchTermDepartments array of search terms (example above)
     * @return unique map of sn.givenName of persons
     * @throws Exception
     */
    public Map<String, Map<String, String>> getLDAPValues(String[] searchTermDepartments, String password) throws Exception;
}
