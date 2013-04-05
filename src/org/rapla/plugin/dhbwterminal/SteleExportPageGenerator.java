package org.rapla.plugin.dhbwterminal;

import java.io.BufferedWriter;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rapla.components.util.IOUtil;
import org.rapla.facade.RaplaComponent;
import org.rapla.framework.RaplaContext;
import org.rapla.framework.RaplaException;
import org.rapla.servletpages.RaplaPageGenerator;

public class SteleExportPageGenerator extends RaplaComponent implements RaplaPageGenerator {

	
	public SteleExportPageGenerator(RaplaContext context) {
		super(context);
	}

	public void generatePage(ServletContext context,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException 
	{
		response.setCharacterEncoding("UTF-8");
		response.setContentType("content-type text/xml charset=utf-8");
		
		java.io.PrintWriter out = response.getWriter();
		
		AllocatableExporter allocatableExporter;
		allocatableExporter = new AllocatableExporter( getRaplaLocale(), getClientFacade());
		try 
		{
			BufferedWriter buf = new BufferedWriter(out);
			StringBuffer a = request.getRequestURL();
			
			int indexOf = a.lastIndexOf("/rapla");
			String linkPrefix = a.substring(0, indexOf);
			
			allocatableExporter.export( buf, linkPrefix);
			buf.close();
		} 
		catch (RaplaException ex) {
			out.println( IOUtil.getStackTraceAsString( ex ) );
			throw new ServletException( ex );
		}
		finally
		{
		    out.close();
		}
		
	}

}
