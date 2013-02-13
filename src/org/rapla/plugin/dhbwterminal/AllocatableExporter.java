package org.rapla.plugin.dhbwterminal;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.rapla.components.util.DateTools;
import org.rapla.components.util.SerializableDateTimeFormat;
import org.rapla.components.util.xml.XMLWriter;
import org.rapla.entities.Category;
import org.rapla.entities.NamedComparator;
import org.rapla.entities.User;
import org.rapla.entities.domain.Allocatable;
import org.rapla.entities.domain.Appointment;
import org.rapla.entities.domain.AppointmentBlock;
import org.rapla.entities.domain.AppointmentBlockStartComparator;
import org.rapla.entities.domain.Reservation;
import org.rapla.entities.dynamictype.Attribute;
import org.rapla.entities.dynamictype.Classification;
import org.rapla.entities.dynamictype.ClassificationFilter;
import org.rapla.entities.dynamictype.DynamicType;
import org.rapla.entities.storage.RefEntity;
import org.rapla.entities.storage.internal.SimpleIdentifier;
import org.rapla.facade.ClientFacade;
import org.rapla.framework.RaplaException;
import org.rapla.framework.RaplaLocale;

class AllocatableExporter extends XMLWriter implements TerminalConstants
{
	 
	 public SerializableDateTimeFormat dateTimeFormat;
	 RaplaLocale raplaLocale;
	 Locale locale;
	 ClientFacade facade;
	 Date currentTimeInGMT;
		
		
	 
	 public AllocatableExporter(RaplaLocale raplaLocale,ClientFacade facade)
	 {
		this.raplaLocale = raplaLocale;
		this.facade = facade;
		dateTimeFormat= new SerializableDateTimeFormat( raplaLocale.createCalendar());
		locale = raplaLocale.getLocale();
		currentTimeInGMT = CourseExporter.getCurrentTimeForRaplaGMTZone(raplaLocale,facade.today());
		
	 }
	 
	 public void printFreeAllocatable( String name, Date ende) throws IOException, RaplaException
	 {
		 String elementName = "freierRaum";
		 openElement(elementName);
		 String endString = "24:00";
		 if ( ende != null)
		 {
				endString = raplaLocale.formatTime(ende);
		 }
		 printOnLine("name", "Name",name);
		 printOnLine("freiBis", "frei bis",endString);
		 closeElement(elementName);
	 }
	 
	 
	/** creates the export xml-file for the terminal*/ 
	public void export( BufferedWriter buf, String linkPrefix) throws RaplaException, IOException
	{
		String encoding = "utf-8";
		buf.append("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>");
		buf.append("\n<RaplaImport>\n");
		setWriter( buf);
		setIndentLevel(1);
		User stele = facade.getUser(STELE_USER);
		{
			List<Allocatable> allocatables = new ArrayList<Allocatable>();
			
			for ( String typeKey: exportTypeNames)
			{
				ClassificationFilter filter = facade.getDynamicType(typeKey).newClassificationFilter();
				ArrayList sortedAllocatables = new ArrayList(Arrays.asList(facade.getAllocatables(filter.toArray())));
				Collections.sort( sortedAllocatables, new NamedComparator<Allocatable>(locale));
				allocatables.addAll( sortedAllocatables);
			}
			for ( Allocatable alloc: allocatables)
			{
				alloc.getLastChangeTime();
				if (alloc.canReadOnlyInformation(stele ))
				{
					boolean exportReservations = alloc.canRead( stele);
					printAllocatable( alloc, linkPrefix, exportReservations);
				}
			}
		}
		{
			List<Allocatable> allocatables = new ArrayList<Allocatable>();
			String[] types = new String[] {ROOM_KEY};
			for ( String typeKey: types)
			{
				ClassificationFilter filter = facade.getDynamicType(typeKey).newClassificationFilter();
				allocatables.addAll( Arrays.asList(facade.getAllocatables(filter.toArray())));
			}
			Date today = facade.today();
			for ( Allocatable allocatable: allocatables)
			{
				if (!allocatable.canAllocate(stele, null, null, today))
				{
					continue;
				}
	
				Date ende = null;
				Date start = null;
				boolean isUsed = false;
				for ( AppointmentBlock block:getReservationBlocksToday(allocatable))
				{
					Date blockStart = new Date(block.getStart());
					Date blockEnd = new Date(block.getEnd());
					if (blockEnd.after( currentTimeInGMT) )
					{
						if ( !blockStart.before( currentTimeInGMT))
						{
							ende = blockStart;
						}
						else
						{
							isUsed = true;
							break;
						}
					}
				}
				if ( ! isUsed)
				{
					String name = getRoomName(allocatable.getClassification());
					printFreeAllocatable(  name, ende);
				}
			}
		}
		
		buf.append("</RaplaImport>");
	}
	
	 private void printAllocatable(Allocatable allocatable, String linkPrefix, boolean exportReservations) throws IOException, RaplaException {
		  Classification classification = allocatable.getClassification();
		  LinkedHashSet<String> search = new LinkedHashSet<String>();
		  if (classification == null)
	            return;
	        DynamicType dynamicType = classification.getType();
	        String elementName = allocatable.isPerson() ? "person" :dynamicType.getElementKey();
	        openTag(elementName);
	        att("typ", dynamicType.getElementKey());
	        closeTag();
	        {
	        	String name;
	        	final String label;
	        	if ( elementName.equals(ROOM_KEY))
	        	{
	        		name = getRoomName(classification);
	        		label = "Raum";
	        	}
	        	else if ( elementName.equals(KURS_KEY))
	        	{
	        		StringBuffer buf = new StringBuffer();
	        		Category studiengang = (Category)classification.getValue("studiengang");
					if ( studiengang != null)
					{
						Category fakultaet = studiengang.getParent();
						if ( fakultaet != null )
						{
							buf.append(fakultaet.getKey());
						}
						buf.append(studiengang.getKey());
						search.add(studiengang.getKey());
					}
	        		Category jahrgang = (Category)classification.getValue("jahrgang");
					if ( jahrgang != null)
					{
						buf.append(jahrgang.getName(locale).substring(2));
					}
					Object titel = classification.getValue("name");
					if ( titel != null)
					{
						buf.append(titel );
					}
					name = buf.toString();
					label = "Kurs";
	        	}
	        	else if ( allocatable.isPerson())
	        	{
	        		StringBuffer buf = new StringBuffer();
	        		
	        		Object titel = classification.getValue("title");
					if ( titel != null)
					{
						buf.append(titel + " ");
					}
					Object vorname = classification.getValue("forename");
					if ( vorname != null)
					{
						buf.append(vorname + " ");
					}
					Object surname = classification.getValue("surname");
					if ( surname != null)
					{
						buf.append(surname);
					}
	        		name = buf.toString();
	        		label = "Name";
	        	}
	        	else 
	        	{
	        		name = classification.getName(locale );
	        		label = "Bezeichnung";
	        	}
	        	
	        	printOnLine("name", label, name);
	        	search.add(name);
	        }
		          
	        
	        printAttributeIfThere(classification, "Jahrgang","jahrgang");
	        printAttributeIfThere(classification, "Studiengang","studiengang");
	        printAttributeIfThere(classification, "Abteilung","abteilung");
	        addSearchIfThere(classification, search, "studiengang");
	        addSearchIfThere(classification, search, "abteilung");
	        
	        printAttributeIfThere(classification, "EMail","email");
	        printAttributeIfThere(classification, "Telefon","telefon");
	        printAttributeIfThere(classification, "Raumart","raumart");
	        addSearchIfThere(classification, search, "raumart");
    		Object raum = classification.getAttribute("raum") != null ? classification.getValue("raum") : null;
    		if ( raum != null)
    		{
//    			StringBuffer nummer = new StringBuffer();
//    			String string = raum.toString().trim();
//				for ( char c :string.toCharArray())
//    			{
//    				if ( Character.isDigit(c))
//    				{
//    					nummer.append( c);
//    				}
//    			}
//				if ( string.length() > 0)
//				{
//					Character first = string.charAt(0);
//					if ( Character.isAlphabetic(first))
//					{
//						String fluegel = first.toString();
//						printOnLineIfNotNull("fluegel", null, fluegel);
//					}
//				}
//    			String asNumber = nummer.toString();
//    			if ( asNumber.length() > 0)
//    			{
//    				printOnLineIfNotNull("raumnr", null, asNumber);
//    				search.add( asNumber);
//    			}
                Object fluegel = classification.getAttribute("fluegel") != null ? classification.getValue("fluegel") : null;

                String raumnr = (fluegel != null ? fluegel.toString() :"")+raum.toString();

    			printOnLineIfNotNull("raumnr", null, raumnr);
    		}
    		else
    		{
                String raumnr = getRoomName(classification);
                if ("".equals(raumnr)) {
    			    printAttributeIfThere(classification, null,"raumnr");
                } else {
                    printOnLineIfNotNull("raumnr", null, raumnr);
                }
    			addSearchIfThere(classification, search, "raumnr");
    		}
			if ( exportReservations )
			{
				{
					String attributeName = "resourceURL";
					Object id = ((RefEntity<Allocatable>) allocatable).getId();
					SimpleIdentifier localname = (org.rapla.entities.storage.internal.SimpleIdentifier)id;
					String key = /*allocatable.getRaplaType().getLocalName() + "_" + */ "" +localname.getKey() ;
					String url = linkPrefix +"/rapla?page=calendar&user="+STELE_USER + "&file="+ elementName +"&allocatable_id=" + key;
		        	try
		        	{
		        		printOnLine(attributeName,"Link",new URI(url));
		        	}
		        	catch (URISyntaxException ex )
		        	{
		        		//FIXME log exceptions
		        	}
				}
				{
					String attributeName = "linkTitel";
		        	openElementOnLine(attributeName);
                    if (allocatable.isPerson())
                        printEncode(LINK_TITEL_PERSON);
                    else if (KURS_KEY.equals(dynamicType.getElementKey()))
                        printEncode(LINK_TITEL_KURS);
                    else if (ROOM_KEY.equals(dynamicType.getElementKey()))
		        	    printEncode(LINK_TITEL_RAUM);
                    else
                        printEncode(LINK_TITEL_DEFAULT);
		        	closeElementOnLine(attributeName);
		        	println();		
				}
			}

			Attribute emailAttr = classification.getAttribute("email");
		        
	        if ( emailAttr != null)
	        {
		    	String email = (String)classification.getValue(emailAttr );
		    	if ( email != null )
		    	{
		        	int indexOf = email.indexOf('@');
		        	if ( indexOf >0 )
		        	{
			        	String attributeName = "personalFoto";
			        	String username = email.substring(0, indexOf);
			        	printOnLine(attributeName, "Personalfoto", username);
		        	}	
		    	}
	        }
	    	
	        Attribute infoAttr = classification.getAttribute("info");
	        List<AppointmentBlock> blocks = getReservationBlocksToday( allocatable);
			if ( infoAttr != null)
			{
				 printAttributeIfThere(classification,"Info", "info");
			}
			else if ( blocks.size() > 0)
	        {
				String attributeName = "info";
				openTag(attributeName);
				att("label", "Info");
				closeTag();
				print("<![CDATA[");
				println();
				for (AppointmentBlock block:blocks)
				{
					Appointment appointment = block.getAppointment();
				
					String resources = getResourceString(dynamicType,
							appointment);
					Reservation reservation = appointment.getReservation();
					Date beginn = new Date(block.getStart());
					Date end = new Date(block.getEnd());
					String title = reservation.getName(locale);
					printInfo(title, resources, beginn,end);
				}
				print("]]>");
				println();
				closeElement(attributeName);
	        } else {
                // if there are no blocks and no info attribute just add an empty attribute
                String attributeName = "info";
                openTag(attributeName);
                att("label", "Info");
                closeTag();
                closeElement(attributeName);
            }

			for ( String tag: search)
			{
				openElementOnLine("suchbegriff");
				printEncode( tag);
				closeElementOnLine("suchbegriff");
				println();
			}
			closeElement(elementName);
	}

	public  String getRoomName(Classification classification) 
	{
		StringBuffer buf = new StringBuffer();
        if (classification.getAttribute("fluegel") != null)
        {
            Category category = (Category)classification.getValue("fluegel");
            if ( category != null)
            {
                buf.append( category.getName( locale ));
            }
        }
        if (classification.getAttribute("raumnr") != null)
        {
            Object value = classification.getValue("raumnr");
            if ( value != null)
            {
                buf.append( value );
            }
        }
		String result = buf.toString();
		return  result;
	}

	private String getResourceString(DynamicType dynamicType,
			Appointment appointment) {
		Reservation reservation = appointment.getReservation();
		boolean isKurs = dynamicType.getElementKey().equals(KURS_KEY);
		boolean isRaum= dynamicType.getElementKey().equals(ROOM_KEY);
		List<String> allocatableName = new ArrayList<String>();
		for (Allocatable alloc: reservation.getAllocatablesFor( appointment))
		{
			String elementKey = alloc.getClassification().getType().getElementKey();
			if ( !isKurs && elementKey.equals(KURS_KEY))
			{
				allocatableName.add( alloc.getName( locale));
			}	
			if ( !isRaum && elementKey.equals(ROOM_KEY))
			{
				allocatableName.add( alloc.getName( locale));
			}
		}
		StringBuffer buf = new StringBuffer();
		for ( int i=0;i<allocatableName.size();i++ )
		{
			String allocName = allocatableName.get(i);
			if ( i==0)
			{
				buf.append("(");
			}
			if ( i>0)
			{
				buf.append(", ");
			}
			buf.append(allocName);
			if ( i== allocatableName.size() -1)
			{
				buf.append(")");
			}
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
	 
	
	private void printOnLineIfNotNull(String tagName,String label, Object content) throws IOException,RaplaException
	 {
		 if ( content != null)
		 {
			 printOnLine(tagName, label, content);
		 }
	 }
	 
	private void printOnLine(String tagName,String label, Object content) throws IOException,RaplaException
	 {
		 openTag(tagName);
		 if (label != null)
		 {
			 att("label",label );
		 }
		 closeTagOnLine();
		 printValue(content);
		 closeElementOnLine( tagName);
		 println();
	 }
	 
	 
	private void printInfo(String title, String resources, Date beginn, Date end) throws IOException
	{
		openElement("div");
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
		println();
		openElementOnLine("span");
		printEncode( title);
		closeElementOnLine("span");
		println();
		openElementOnLine("span");
		print( resources);
		closeElementOnLine("span");
		println();
		closeElement("div");
	}
	
	private void printAttributeIfThere(Classification classification,
			String label,String attributeName) throws IOException, RaplaException {
		Attribute attribute = classification.getAttribute(attributeName);
		if ( attribute != null)
		{
			Object value = classification.getValue(attribute);
			printOnLineIfNotNull(attributeName, label, value);
		}
	}
	
	private void addSearchIfThere(Classification classification,
			Set<String> search,String attributeName) throws IOException, RaplaException {
		Attribute attribute = classification.getAttribute(attributeName);
		if ( attribute != null)
		{
			Object value = classification.getValue(attribute);
			if (value != null)
			{
				String string = getStringValue(value);
				search.add( string);
			}
		}
	}
	  
	private void printValue( Object value) throws IOException,RaplaException 
	{
		if ( value == null)
		{
			return;
		}
		if ( value instanceof URI)
		{
			print("<![CDATA[");
			print(value.toString());
			print("]]>");
		}
		else
		{
			String string = getStringValue(value);
			printEncode(string.replace("\n","<br/>"));
		}
	}
	  
	private String getStringValue(Object value) throws IOException 
	{
		if (value instanceof Category)
		{
			String toString = ((Category)value).getName(locale);
			return toString;
		}
		else if (value instanceof Date )
		{
		    final Date date;
		    if  ( value instanceof Date) 
		        date = (Date)value;
		    else
		        date = null;
			return dateTimeFormat.formatDate( date ) ;
		}
		else
		{
		    return value.toString() ;
		}
	}
}