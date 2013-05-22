package org.rapla.migration15_17.ldap.test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.rapla.facade.RaplaComponent;
import org.rapla.framework.RaplaContext;
import org.rapla.framework.logger.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: kuestermann
 * Date: 15.03.13
 * Time: 13:18
 * To change this template use File | Settings | File Templates.
 */
public class LDAPQueryImpl extends RaplaComponent implements LDAPQuery {


    public LDAPQueryImpl(RaplaContext context) {
        super(context);
        Logger logger = getLogger();
    }


    @Override
    public Map<String, Map<String, String>> getLDAPValues(String[] searchTermDepartments, String password) throws Exception {
        final Map<String, Map<String, String>> result = new TreeMap<String, Map<String, String>>();

        DirContext context = new InitialDirContext(getDirectoryContextEnvironment(password));
        // Set up security environment to bind as the user

        Collection<String> personCNs = getPersonCNs(context, searchTermDepartments);

        result.putAll(getPersons(context, personCNs));

        return result;
    }

    private Map<String, Map<String, String>> getPersons(DirContext context, Collection<String> personCNs) throws NamingException {
        final Map<String, Map<String, String>> persons = new TreeMap<String, Map<String, String>>();

        // Set up the search controls
        final SearchControls constraints = new SearchControls();
        constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
        constraints.setReturningAttributes(PERSON_RESULT_ATTRIBUTES);

        for (String personCN : personCNs) {
            final NamingEnumeration<?> results = context.search(SEARCH_USER_BASE, "("+personCN+")", constraints);

            if (results != null) {
                while (results.hasMoreElements()) {
                    final Map<String, String> person = new TreeMap<String, String>();
                    final SearchResult result = (SearchResult) results.next();
                    final Attributes attributes = result.getAttributes();
                    for (String personResultAttribute : PERSON_RESULT_ATTRIBUTES) {
                        if (attributes.get(personResultAttribute)!=null)
                            person.put(personResultAttribute, attributes.get(personResultAttribute).get().toString());
                        else
                            System.out.print("Attribute "+personResultAttribute+" does not exist");
                    }
                    if (attributes.get("sn")!= null && attributes.get("givenName") != null)
                        persons.put(attributes.get("sn").get().toString().toLowerCase()+"."+attributes.get("givenName").get().toString().toLowerCase(), person);
                }

            }

        }

        return persons;

    }

    protected Collection<String> getPersonCNs(DirContext context, String[] searchTermDepartments) throws NamingException {
        final Set<String> personCNs = new HashSet<String>();

        // Set up the search controls
        final SearchControls constraints = new SearchControls();
        constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
        constraints.setReturningAttributes(MEMBER_ATTRIBUTES);

        for (String department : searchTermDepartments) {
            final NamingEnumeration<?> results = context.search(SEARCH_USER_BASE, department, constraints);
            if (results != null) {
                while (results.hasMoreElements()) {
                    final SearchResult result = (SearchResult) results.next();
                    final NameParser parser = context.getNameParser("");
                    final Name member = parser.parse(result.getName());
                    assert member != null;
                    personCNs.add(member.get(1).toString());
                }

            }
        }

        return personCNs;
    }

    /**
     * Create our directory context configuration.
     *
     * @return java.util.Hashtable the configuration for the directory context.
     */
    protected Hashtable<String, Object> getDirectoryContextEnvironment(String password) {

        Hashtable<String, Object> env = new Hashtable<String, Object>();

        // Configure our directory context environment.
        env.put(Context.INITIAL_CONTEXT_FACTORY, CONTEXT_FACTORY);
        env.put(Context.SECURITY_PRINCIPAL, CONNECTION_USERNAME);
        env.put(Context.SECURITY_CREDENTIALS, password);
        env.put(Context.PROVIDER_URL, CONNECTION_URL);
        env.put("java.naming.ldap.derefAliases", "never");
        return env;
    }
}
