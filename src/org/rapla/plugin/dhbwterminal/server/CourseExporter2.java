package org.rapla.plugin.dhbwterminal.server;

import org.rapla.components.util.DateTools;
import org.rapla.components.util.xml.XMLWriter;
import org.rapla.entities.Category;
import org.rapla.entities.Named;
import org.rapla.entities.NamedComparator;
import org.rapla.entities.User;
import org.rapla.entities.domain.*;
import org.rapla.entities.dynamictype.Attribute;
import org.rapla.entities.dynamictype.Classification;
import org.rapla.entities.dynamictype.ClassificationFilter;
import org.rapla.entities.dynamictype.DynamicType;
import org.rapla.facade.ClientFacade;
import org.rapla.framework.Configuration;
import org.rapla.framework.RaplaException;
import org.rapla.framework.RaplaLocale;
import org.rapla.plugin.dhbwterminal.TerminalConstants;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;


public class CourseExporter2 extends CourseExporter {
    public CourseExporter2(Configuration config, RaplaLocale raplaLocale, ClientFacade facade) throws RaplaException {
        super(config, raplaLocale, facade);
    }


    /**
     * prints all courses for a current day with the next reservation. Prints one course in one row.
     */
    public void printKurseAmTag(BufferedWriter buf, String noAllocatables) throws RaplaException, IOException {
        setWriter(buf);
        setIndentLevel(1);
        openTag("div");
        att("class","infobox");
        closeTag();
        boolean hasAllocatablesPrinted = false;
        User stele = facade.getUser(terminalUser);
        {
            List<Allocatable> allocatables = new ArrayList<Allocatable>();
            for (DynamicType typeKey : courseType) {
                ClassificationFilter filter = typeKey.newClassificationFilter();
                allocatables.addAll(Arrays.asList(facade.getAllocatables(filter.toArray())));
            }
            Collections.sort(allocatables, new NamedComparator<Allocatable>(locale));
            Object old = null;
            for (Allocatable alloc : allocatables) {
                if (alloc.canRead(stele)) {
                    final Named value = (Named) alloc.getClassification().getValue("abteilung");
                    boolean newAllocatable = old == null || !old.equals(value);

                    if (newAllocatable && value != null) {
                        if (old != null) {
                           // closeElement("marquee");
                            closeElement("div");
                            closeElement("div");
                        }
                        openTag("div");
                        att("id", value.getName(raplaLocale.getLocale()));
                        //att("class", "studiengang");
                        closeTag();
                        openElement("h2");
                        openTag("a");
                        att("href","#"+value.getName(raplaLocale.getLocale()));
                        closeTagOnLine();
                        printEncode(value.getName(raplaLocale.getLocale()));
                        closeElementOnLine("a");
                        closeElement("h2");
                       // openElement("marquee scrollamount=\"1\" scrolldelay=\"1\" direction=\"up\"");
                        // closeTagOnLine();
                        old = value;
                        openTag("div");
                        att("class","abteilung");
                        closeTagOnLine();
                        println();
                    }


                    boolean hasPrinted = true;
                    //boolean hasPrinted = printAllocatableForUebersicht(alloc);
                    println("Testtext<br/>");
                    if (hasPrinted) {
                        hasAllocatablesPrinted = true;
                    }
                }
            }
        }
        closeElement("div");
        closeElement("div");
        if (!hasAllocatablesPrinted) {
            buf.write(noAllocatables);
        }
        closeElement("div");
    }

    protected void printKursRow(Date beginn, Date end, String title, String resources, Allocatable alloc) throws IOException {
        openTag("div");
        att("class", "kurs-row");
        closeTag();

        openTag("div");
        att("class", "kurs-column");
        closeTagOnLine();
        printScaleImage();
        printEncode(alloc.getName(locale));
        closeElementOnLine("div");
        println();
        openTag("div");
        att("class", "time-column");
        closeTagOnLine();
        printScaleImage();
        openElementOnLine("span");
        String beginnString = raplaLocale.formatTime(beginn);
        print(beginnString);
        closeElementOnLine("span");
        if (end != null) {
            print(" - ");
            openElementOnLine("span");
            String endString = raplaLocale.formatTime(end);
            print(endString);
            closeElementOnLine("span");
        }
        closeElementOnLine("div");
        println();

        openTag("div");
        att("class", "name-column");
        closeTagOnLine();
        printScaleImage();
        printEncode(title);
        closeElementOnLine("div");

        println();
        openTag("div");
        att("class", "resource-column");
        closeTagOnLine();
        printScaleImage();
            printEncode(resources);
        closeElementOnLine("div");
        println();
        closeElement("div");
    }



}