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
import org.rapla.components.util.xml.XMLWriter;
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
import org.rapla.facade.ClientFacade;
import org.rapla.framework.RaplaException;
import org.rapla.framework.RaplaLocale;
import org.rapla.plugin.dhbwterminal.TerminalConstants;

public class CourseExporter extends XMLWriter implements TerminalConstants
{
	 RaplaLocale raplaLocale;
	 Locale locale;
	 ClientFacade facade;
	 Date currentTime;
	 
	 public CourseExporter(RaplaLocale raplaLocale,ClientFacade facade)
	 {
		this.raplaLocale = raplaLocale;
		this.facade = facade;
		locale = raplaLocale.getLocale();
		currentTime = raplaLocale.toRaplaTime(raplaLocale.getSystemTimeZone(),new Date());
	 }
	 
	/** prints all courses for a current day with the next reservation. Prints one course in one row.*/
	public void printKurseAmTag( BufferedWriter buf, String noAllocatables	) throws RaplaException, IOException
	{
		setWriter( buf);
		setIndentLevel(1);
		boolean hasAllocatablesPrinted = false;
		User stele = facade.getUser(STELE_USER);
		{
			List<Allocatable> allocatables = new ArrayList<Allocatable>();
			String[] types = new String[] {KURS_KEY};
			for ( String typeKey: types)
			{
				ClassificationFilter filter = facade.getDynamicType(typeKey).newClassificationFilter();
				allocatables.addAll( Arrays.asList(facade.getAllocatables(filter.toArray())));
			}
			Collections.sort( allocatables, new NamedComparator<Allocatable>(locale));
			for ( Allocatable alloc: allocatables)
			{
				if (alloc.canRead( stele ))
				{
					boolean hasPrinted = printAllocatableForUebersicht( alloc);
					if ( hasPrinted)
					{
						hasAllocatablesPrinted = true;
					}
				}
			}
		}
		if ( !hasAllocatablesPrinted )
		{
			buf.write(noAllocatables);
		}
	}

	private boolean printAllocatableForUebersicht(Allocatable allocatable) throws RaplaException, IOException {
		boolean hasPrinted = false;
		List<AppointmentBlock> blocks = getReservationBlocksToday( allocatable);
		if ( blocks.size() > 0)
        {
			
			for (AppointmentBlock block:blocks)
			{
		
				Appointment appointment = block.getAppointment();
			
				Reservation reservation = appointment.getReservation();
				Date beginn = new Date(block.getStart());
				Date end = new Date(block.getEnd());
				if ( end.after( currentTime))
				{
					String resources = getResourceString(appointment);
					String title = reservation.getName(locale);
					printKursRow(  beginn, end, title, resources, allocatable);
					hasPrinted = true;
					break;
				}
			}

        }
		return hasPrinted;
	}
	
	protected void printKursRow(Date beginn, Date end,String title, String resources,  Allocatable alloc) throws IOException
	{
		openTag("div");
		att("class","kurs-row");
		closeTag();
		
		openTag("div");
		att("class","kurs-column");
		closeTagOnLine();
		printScaleImage();
		printEncode( alloc.getName( locale));
		closeElementOnLine("div");
		println();
		openTag("div");
		att("class","time-column");
		closeTagOnLine();
		printScaleImage();
		openElementOnLine("span");
		String beginnString = raplaLocale.formatTime(beginn);
		print(beginnString);
		closeElementOnLine("span");
		if ( end != null)
		{
			print(" - ");
			openElementOnLine("span");
			String endString = raplaLocale.formatTime(end);
			print(endString);
			closeElementOnLine("span");
		}
		closeElementOnLine("div");
		println();

		openTag("div");
		att("class","name-column");
		closeTagOnLine();
		printScaleImage();
		printEncode( title);
		closeElementOnLine("div");

		println();
		openTag("div");
		att("class","resource-column");
		closeTagOnLine();
		printScaleImage();
		printEncode( resources);
		closeElementOnLine("div");
		println();
		closeElement("div");
	}

	protected void printScaleImage() throws IOException {
		print("<img class=\"empty\" src=\"images/empty.gif\"/>");
	}

    public  String getRoomName(Classification classification, boolean fluegel)
    {
        Category superCategory = facade.getSuperCategory();
        StringBuffer buf = new StringBuffer();
        if (classification.getAttribute("raum") != null)
        {
            Category category = (Category)classification.getValue("raum");
            if ( category != null)
            {
                Category parent = category.getParent();
                if (!fluegel || parent.getParent().equals(superCategory))
                    parent = null;
                buf.append(
                        (parent != null ? parent.getName(locale) : "") +
                                category.getName( locale ));
            }
        }
        String result = buf.toString();
        return  result;
    }


	public  String getRoomName(Classification classification) {
		return getRoomName(classification, true);//((Category)classification.getValue("fluegel")).getName( locale ) + classification.getValue("raumnr");
	}

	private String getResourceString(
			Appointment appointment) {
		Reservation reservation = appointment.getReservation();
		//boolean isKurs = true;
		//boolean isRaum= false;
		List<String> allocatableName = new ArrayList<String>();
		for (Allocatable alloc: reservation.getAllocatablesFor( appointment))
		{
			Classification classification = alloc.getClassification();
			String elementKey = classification.getType().getElementKey();
			if ( elementKey.equals(ROOM_KEY))
			{
				String name = getRoomName(classification);
				allocatableName.add( name);
			}
		}
		StringBuffer buf = new StringBuffer();
		for ( int i=0;i<allocatableName.size();i++ )
		{
			String allocName = allocatableName.get(i);
			if ( i>0)
			{
				buf.append(", ");
			}
			buf.append(allocName);
		}
		String resources = buf.toString();
		return resources;
	}
	 
	 private List<AppointmentBlock> getReservationBlocksToday(
			 Allocatable allocatable) throws RaplaException 
	{
		 Date start = facade.today();
		 Date end = DateTools.addDay( start);
		 List<AppointmentBlock> array =  new ArrayList<AppointmentBlock>();
		 Reservation[] reservations = facade.getReservations(new Allocatable[] {allocatable}, start, end);
		 for ( Reservation res: reservations)
		 {
			 for ( Appointment app: res.getAppointmentsFor(allocatable))
			 {
				 app.createBlocks(start, end, array);
			 }
		 }
		 Collections.sort(array, new AppointmentBlockStartComparator());
		 return array;
	}
	 
	
}