package org.rapla.plugin.dhbwterminal;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;

import org.rapla.RaplaTestCase;
import org.rapla.facade.ClientFacade;
import org.rapla.framework.DefaultConfiguration;
import org.rapla.framework.RaplaContext;
import org.rapla.plugin.dhbwterminal.server.AllocatableExporter;

public class TerminalExportTest extends RaplaTestCase 
{

	public TerminalExportTest(String name) {
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
        DefaultConfiguration config = new DefaultConfiguration("element");
        config.addChild(new DefaultConfiguration(TerminalConstants.KURS_KEY, "kurs"));
        config.addChild(new DefaultConfiguration(TerminalConstants.ROOM_KEY, "raum"));
        config.addChild(new DefaultConfiguration(TerminalConstants.USER_KEY, "stele"));
        config.addChild(new DefaultConfiguration(TerminalConstants.EVENT_TYPES_KEY, "reservation2,reservation4"));
        config.addChild(new DefaultConfiguration(TerminalConstants.RESOURCE_TYPES_KEY, "professor,mitarbeiter,raum,kurs"));
        config.addChild(new DefaultConfiguration(TerminalConstants.EXTERNAL_PERSON_TYPES_KEY, "mitarbeiter"));
         RaplaContext context = raplaContainer.getContext();
		AllocatableExporter exporter = new AllocatableExporter(context,config, facade);
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
