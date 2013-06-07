package org.rapla.migration15_17.migration.test;

import org.rapla.entities.User;
import org.rapla.entities.dynamictype.ClassificationFilter;
import org.rapla.facade.CalendarModel;
import org.rapla.facade.CalendarSelectionModel;
import org.rapla.facade.ClientFacade;
import org.rapla.migration15_17.MigrationTestCase;
import org.rapla.plugin.autoexport.AutoExportPlugin;

public class SteleUserTest extends MigrationTestCase {
	ClientFacade facade;
	
	public SteleUserTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp(PRODUCTIVE_XML);
		facade = (ClientFacade) getContext().lookup(ClientFacade.class);
		facade.login("admin", "".toCharArray());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testSteleUser() throws Exception {
		User stele;
		try
		{
		stele = facade.edit(facade.newUser());
		stele.setUsername("stele");
		facade.store(stele);
		}
		catch (Exception ex)
		{
			stele = facade.getUser("stele");
		}
		CalendarSelectionModel model1;
		model1 = facade.newCalendarModel(stele);
		ClassificationFilter filter1 = facade.getDynamicType("defaultPerson").newClassificationFilter();
		ClassificationFilter filter2 = facade.getDynamicType("person1").newClassificationFilter();
		ClassificationFilter filter3 = facade.getDynamicType("person2").newClassificationFilter();
		ClassificationFilter[] filters1 = {filter1, filter2, filter3};
		model1.setAllocatableFilter(filters1);
		model1.setViewId("day");
		model1.setTitle("Vorlesungsplan für {allocatables} am {selectedDate}");
		model1.setOption(AutoExportPlugin.HTML_EXPORT, "true");
		model1.setOption(CalendarModel.SHOW_NAVIGATION_ENTRY, "false");
		model1.save("person");
		CalendarSelectionModel model2;
		model2 = facade.newCalendarModel(stele);
		ClassificationFilter filter4 = facade.getDynamicType("resource2").newClassificationFilter();
		ClassificationFilter[] filters2 = {filter4};
		model2.setAllocatableFilter(filters2);
		model2.setViewId("day");
		model2.setTitle("Belegungsgsplan für {allocatables} am {selectedDate}");
		model2.setOption(AutoExportPlugin.HTML_EXPORT, "true");
		model2.setOption(CalendarModel.SHOW_NAVIGATION_ENTRY, "false");
		model2.save("raum");
		CalendarSelectionModel model3;
		model3 = facade.newCalendarModel(stele);
		model3.setAllocatableFilter(filters2);
		model3.setViewId("day");
		model3.setTitle("Belegungsgsplan für {allocatables} am {selectedDate}");
		model3.setOption(AutoExportPlugin.HTML_EXPORT, "true");
		model3.setOption(CalendarModel.SHOW_NAVIGATION_ENTRY, "false");
		model3.save("kurs");
		CalendarSelectionModel model4;
		model4 = facade.newCalendarModel(stele);
		model4.setAllocatableFilter(filters2);
		model4.setViewId("day");
		model4.setTitle("Belegungsgsplan für {allocatables} am {selectedDate}");
		model4.setOption(AutoExportPlugin.HTML_EXPORT, "true");
		model4.setOption(CalendarModel.SHOW_NAVIGATION_ENTRY, "false");
		model4.save("sonstiges");
		
	}
}
