package org.rapla.plugin.dhbwterminal.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.rapla.components.util.DateTools;
import org.rapla.entities.Category;
import org.rapla.entities.NamedComparator;
import org.rapla.entities.User;
import org.rapla.entities.domain.Allocatable;
import org.rapla.entities.domain.Appointment;
import org.rapla.entities.domain.AppointmentBlock;
import org.rapla.entities.domain.AppointmentBlockStartComparator;
import org.rapla.entities.domain.Reservation;
import org.rapla.entities.dynamictype.Classification;
import org.rapla.entities.dynamictype.ClassificationFilter;
import org.rapla.entities.dynamictype.DynamicType;
import org.rapla.facade.ClientFacade;
import org.rapla.framework.Configuration;
import org.rapla.framework.RaplaException;
import org.rapla.framework.RaplaLocale;
import org.rapla.plugin.dhbwterminal.TerminalConstants;


public class CourseExporter3 extends CourseExporter {
    protected RaplaLocale raplaLocale;
    protected Locale locale;
    protected ClientFacade facade;
    protected Date currentTime;
    protected String terminalUser;
    protected  DynamicType[] courseType;
    protected  DynamicType[] roomTypes;
    protected  DynamicType[] resourceTypes;
    protected  DynamicType[] eventTypes;

    public CourseExporter3(Configuration config, RaplaLocale raplaLocale, ClientFacade facade) throws RaplaException {
    	super(config, raplaLocale, facade);
    	this.raplaLocale = raplaLocale;
        this.facade = facade;
        locale = raplaLocale.getLocale();
        currentTime = raplaLocale.toRaplaTime(raplaLocale.getSystemTimeZone(), new Date());

        eventTypes = AllocatableExporter.getDynamicTypesForKey(config, facade, TerminalConstants.EVENT_TYPES_KEY);
        resourceTypes = AllocatableExporter.getDynamicTypesForKey(config, facade, TerminalConstants.RESOURCE_TYPES_KEY);
        roomTypes = AllocatableExporter.getDynamicTypesForKey(config, facade, TerminalConstants.ROOM_KEY);
        courseType = AllocatableExporter.getDynamicTypesForKey(config, facade, TerminalConstants.KURS_KEY);
        terminalUser = config.getChild(TerminalConstants.USER_KEY).getValue(null);
        if (terminalUser == null) {
            throw new RaplaException("Terminal User must be set to use export");
        }
    }

    /**
     * prints all courses for a current day with the next reservation. Prints one course in one row.
     */
    public void printKurseAmTag(BufferedWriter buf, String noAllocatables) throws RaplaException, IOException {
        //buf.append("<marquee scrollamount=\"1\" scrolldelay=\"1\" direction=\"up\" >");
        setWriter(buf);
        setIndentLevel(1);
        boolean hasAllocatablesPrinted = false;
        User stele = facade.getUser(terminalUser);
        {
            List<Allocatable> allocatables = new ArrayList<Allocatable>();
            for (DynamicType typeKey : courseType) {
                ClassificationFilter filter = typeKey.newClassificationFilter();
                allocatables.addAll(Arrays.asList(facade.getAllocatables(filter.toArray())));
            }
            Collections.sort(allocatables, new NamedComparator<Allocatable>(locale));
            for (Allocatable alloc : allocatables) {
                if (alloc.canRead(stele)) {
                    boolean hasPrinted = printAllocatableForUebersicht(alloc);
                    if (hasPrinted) {
                        hasAllocatablesPrinted = true;
                    }
                }
            }
        }
        if (!hasAllocatablesPrinted) {
            buf.write(noAllocatables);
        }
        //buf.append("</marquee>");
    }

    protected boolean printAllocatableForUebersicht(Allocatable allocatable) throws RaplaException, IOException {
        boolean hasPrinted = false;
        List<AppointmentBlock> blocks = getReservationBlocksToday(allocatable);
        if (blocks.size() > 0) {

            for (AppointmentBlock block : blocks) {

                Appointment appointment = block.getAppointment();

                Reservation reservation = appointment.getReservation();
                if (Arrays.binarySearch(eventTypes, reservation.getClassification().getType()) >= 0) {
                    Date beginn = new Date(block.getStart());
                    Date end = new Date(block.getEnd());
                    //if (end.after(currentTime))
                    {
                        String resources = getResourceString(appointment);
                        String title = reservation.getName(locale);
                        printKursRow(beginn, end, title, resources, allocatable);
                        hasPrinted = true;
                        break;
                    }
                }
            }

        }
        return hasPrinted;
    }

    protected void printKursRow(Date beginn, Date end, String title, String resources, Allocatable alloc) throws IOException {
        openTag("div");
        att("class", "kurs-row " + alloc.getClassification().getValueAsString(alloc.getClassification().getAttribute("abteilung"), Locale.GERMAN));
        att("style", "display:none");
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

    protected void printScaleImage() throws IOException {
        print("<img class=\"empty\" src=\"images/empty.gif\"/>");
    }

    protected String getRoomName(Classification classification, boolean fluegel) {
        Category superCategory = facade.getSuperCategory();
        StringBuffer buf = new StringBuffer();
        if (classification.getAttribute("raum") != null) {
            Category category = (Category) classification.getValue("raum");
            if (category != null) {
                Category parent = category.getParent();
                if (!fluegel || parent.getParent().equals(superCategory))
                    parent = null;
                buf.append(
                        (parent != null ? parent.getName(locale) : "") +
                                category.getName(locale));
            }
        }
        String result = buf.toString();
        return result;
    }


    protected String getRoomName(Classification classification) {
        return getRoomName(classification, true);//((Category)classification.getValue("fluegel")).getName( locale ) + classification.getValue("raumnr");
    }

    protected String getResourceString(
            Appointment appointment) {
        Reservation reservation = appointment.getReservation();
        //boolean isKurs = true;
        //boolean isRaum= false;
        List<String> allocatableName = new ArrayList<String>();
        for (Allocatable alloc : reservation.getAllocatablesFor(appointment)) {
            Classification classification = alloc.getClassification();
            //String elementKey = classification.getType().getElementKey();
            if (Arrays.binarySearch(roomTypes, classification.getType()) >= 0) { // elementKey.equals(ROOM_KEY)) {
                String name = getRoomName(classification);
                allocatableName.add(name);
            }
        }
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < allocatableName.size(); i++) {
            String allocName = allocatableName.get(i);
            if (i > 0) {
                buf.append(", ");
            }
            buf.append(allocName);
        }
        String resources = buf.toString();
        return resources;
    }

    protected List<AppointmentBlock> getReservationBlocksToday(
            Allocatable allocatable) throws RaplaException {
        Date start = facade.today();
        Date end = DateTools.addDay(start);
        List<AppointmentBlock> array = new ArrayList<AppointmentBlock>();
        Reservation[] reservations = facade.getReservations(new Allocatable[]{allocatable}, start, end);
        for (Reservation res : reservations) {
            if (Arrays.binarySearch(eventTypes, res.getClassification().getType()) >= 0) {
                for (Appointment app : res.getAppointmentsFor(allocatable)) {
                    app.createBlocks(start, end, array);
                }
            }
        }
        Collections.sort(array, new AppointmentBlockStartComparator());
        return array;
    }


}