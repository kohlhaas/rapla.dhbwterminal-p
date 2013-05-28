package org.rapla.plugin.dhbwterminal;

import org.rapla.RaplaTestCase;
import org.rapla.facade.ClientFacade;
import org.rapla.plugin.dhbwterminal.server.AllocatableExporter;
import org.rapla.plugin.dhbwterminal.server.CourseExporter;

import java.io.*;

public class CourseExportTest extends RaplaTestCase
{

	public CourseExportTest(String name) {
		super(name);
	}

    @Override
    protected void setUp() throws Exception {
        super.setUp("test.xml");    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void testExport() throws Exception
	{
		ClientFacade facade = raplaContainer.lookup(ClientFacade.class , "local-facade2");
		facade.login("stele", new char[] {});
		
		StringWriter writer = new StringWriter();
		BufferedWriter buf = new BufferedWriter(writer);
		CourseExporter exporter = new CourseExporter(getRaplaLocale(), facade);
		exporter.printKurseAmTag( buf, "https://dhbw-karlsruhe.de/");
		buf.close();
		writer.close();
		String xml = writer.toString();
		File file = new File("courseexport.html");
		OutputStreamWriter filewriter = new OutputStreamWriter(new FileOutputStream(file),"UTF-8");
		filewriter.write( xml);
		filewriter.close();
		System.out.println(xml);
		
	}
	

}
