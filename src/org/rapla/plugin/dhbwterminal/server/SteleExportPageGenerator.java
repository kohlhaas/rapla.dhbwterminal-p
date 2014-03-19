package org.rapla.plugin.dhbwterminal.server;

import java.io.BufferedWriter;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rapla.facade.RaplaComponent;
import org.rapla.framework.Configuration;
import org.rapla.framework.RaplaContext;
import org.rapla.framework.RaplaException;
import org.rapla.plugin.urlencryption.UrlEncryption;
import org.rapla.plugin.urlencryption.server.EncryptedHttpServletRequest;
import org.rapla.servletpages.RaplaPageGenerator;

public class SteleExportPageGenerator extends RaplaComponent implements RaplaPageGenerator {
    private Configuration config;

    public SteleExportPageGenerator(RaplaContext context, Configuration config) {
		super(context);
        this.config=config;
	}

	public void generatePage(ServletContext context,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException 
	{
	    if ( getContext().has(UrlEncryption.class) && ! (request instanceof EncryptedHttpServletRequest))
        {
	    	response.sendError( 403 );
	    	return;
        }
	
		
		response.setCharacterEncoding("UTF-8");
		response.setContentType("content-type text/xml charset=utf-8");
		
		java.io.PrintWriter out = response.getWriter();
		
		try
		{
            AllocatableExporter allocatableExporter;
            allocatableExporter = new AllocatableExporter(getContext(), config);

            BufferedWriter buf = new BufferedWriter(out);
			StringBuffer a = request.getRequestURL();
			
			int indexOf = a.lastIndexOf("/rapla");
			String linkPrefix = a.substring(0, indexOf);
			
			allocatableExporter.export( buf, linkPrefix);
			buf.close();
		} 
		catch (RaplaException ex) {
			//out.println( IOUtil.getStackTraceAsString( ex ) );
            getLogger().error(ex.getMessage(), ex);
            writeError(response, "Error in plugin configuration. Please contact administrator. See log files");
		}
		finally
		{
		    out.close();
		}
		
	}

    private void writeError( HttpServletResponse response, String message ) throws IOException
    {
        response.setStatus( 500 );
        response.setContentType( "text/html; charset=" + getRaplaLocale().getCharsetNonUtf() );
        java.io.PrintWriter out = response.getWriter();
        out.println( message );
        out.close();
    }
}
