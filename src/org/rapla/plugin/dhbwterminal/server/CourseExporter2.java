package org.rapla.plugin.dhbwterminal.server;

import org.rapla.components.util.DateTools;
import org.rapla.components.util.xml.XMLWriter;
import org.rapla.entities.Category;
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
    private Object old = null;
    public CourseExporter2(Configuration config, RaplaLocale raplaLocale, ClientFacade facade) throws RaplaException {
        super(config, raplaLocale, facade);
    }


    protected boolean printAllocatableForUebersicht(Allocatable allocatable) throws RaplaException, IOException {
        Attribute studiengang = allocatable.getClassification().getAttribute("abteilung");
        Object value = allocatable.getClassification().getValue(studiengang);
        boolean newAllocatable = old == null || !old.equals(value);


        boolean hasPrinted = false;
        List<AppointmentBlock> blocks = getReservationBlocksToday(allocatable);
        if (blocks.size() > 0) {
            if (newAllocatable && value != null) {
                if (old != null) {
                    closeElement("div");
                }
                openTag("div");
                att("class", "course");
                closeTag();
                openTag("span");
                att("class","course_title");
                closeTagOnLine();
                printEncode(value.toString());
                closeElementOnLine("span");
                old = value;
            }
            for (AppointmentBlock block : blocks) {

                Appointment appointment = block.getAppointment();

                Reservation reservation = appointment.getReservation();
                if (Arrays.binarySearch(eventTypes, reservation.getClassification().getType()) >= 0) {
                    //check if we have a new course

                    Date beginn = new Date(block.getStart());
                    Date end = new Date(block.getEnd());
                    if (end.after(currentTime)) {
                        String resources = getResourceString(appointment);
                        String title = reservation.getName(locale);
                        printKursRow(beginn, end, title, resources, allocatable);
                        hasPrinted = true;
                        break;
                    }
                }
            }

            if (value == old) {
                if (old != null) {
                    closeElement("div");
                }
            }

        }
        return hasPrinted;
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