package org.rapla.plugin.dhbwterminal;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;

import org.rapla.RaplaTestCase;
import org.rapla.facade.ClientFacade;
import org.rapla.plugin.dhbwterminal.server.AllocatableExporter;

public class TerminalExportTest extends RaplaTestCase 
{

	public TerminalExportTest(String name) {
		super(name);
	}
	
	public void testExport() throws Exception
	{
		ClientFacade facade = raplaContainer.lookup(ClientFacade.class , "local-facade2");
		facade.login("stele", new char[] {});
		
		StringWriter writer = new StringWriter();
		BufferedWriter buf = new BufferedWriter(writer);
		AllocatableExporter exporter = new AllocatableExporter(getRaplaLocale(), facade);
		exporter.export( buf, "https://dhbw-karlsruhe.de/");
		buf.close();
		writer.close();
		String xml = writer.toString();
		File file = new File("raplaexport.xml"); 
		OutputStreamWriter filewriter = new OutputStreamWriter(new FileOutputStream(file),"UTF-8");
		filewriter.write( xml);
		filewriter.close();
		System.out.println(xml);
		
	}
	

}
