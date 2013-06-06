package org.rapla.plugin.dhbwterminal.server;

import java.io.BufferedWriter;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rapla.components.util.IOUtil;
import org.rapla.facade.RaplaComponent;
import org.rapla.framework.Configuration;
import org.rapla.framework.RaplaContext;
import org.rapla.framework.RaplaException;
import org.rapla.framework.RaplaLocale;
import org.rapla.plugin.dhbwterminal.TerminalConstants;
import org.rapla.servletpages.RaplaPageGenerator;

public class SteleKursUebersichtPageGenerator extends RaplaComponent implements RaplaPageGenerator, TerminalConstants {

	Configuration config;
	public SteleKursUebersichtPageGenerator(RaplaContext context, Configuration config) {
		super(context);
		this.config=config;
	}

	public void generatePage(ServletContext context,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException 
	{
		
		RaplaLocale raplaLocale = getRaplaLocale();
		response.setContentType("text/html; charset=" + raplaLocale.getCharsetNonUtf() );
		java.io.PrintWriter out = response.getWriter();
		try 
		{

	        //		response.setContentType("content-type text/html charset=utf-8");
                
	        String title = config.getChild( "ueberschrift").getValue(TerminalConstants.KURS_UEBERSCHRIFT);
	        String no_courses = config.getChild( "keinekurse").getValue(TerminalConstants.NO_COURSES);
	        String cssurl = config.getChild( "cssurl").getValue("");
			
	        out.println("<html>");
			out.println("<head>");
			out.println("  <title>" + title + "</title>");
	
			out.println("  <link REL=\"stylesheet\" href=\"rapla?page=resource&name=kursuebersicht.css\" type=\"text/css\">");
			if ( cssurl != null && cssurl.trim().length() > 0)
			{
				out.println("  <link REL=\"stylesheet\" href=\""+ cssurl + "\" type=\"text/css\">");
			}
			out.println("  <link REL=\"stylesheet\" href=\"default.css\" type=\"text/css\">");
			// tell the html page where its favourite icon is stored
			out.println("    <link REL=\"shortcut icon\" type=\"image/x-icon\" href=\"/images/favicon.ico\">");
			out.println("  <meta HTTP-EQUIV=\"Content-Type\" content=\"text/html; charset=" + raplaLocale.getCharsetNonUtf() + "\">");
			out.println("</head>");
			out.println("<body>");
			out.println("  <h1 class=\"title\">" + title + "</h1>");
            out.println("  <div class=\"kurs-row\">"
                            +"<div class=\"kurs-column\"><img class=\"empty\" src=\"./images/empty.gif\">Kurs</div>"
                            +"<div class=\"time-column\"><img class=\"empty\" src=\"./images/empty.gif\">Zeitraum</div>"
                            +"<div class=\"name-column\"><img class=\"empty\" src=\"./images/empty.gif\">Veranstaltung</div>"
                            +"<div class=\"resource-column\"><img class=\"empty\" src=\"./images/empty.gif\">Raum</div>"
                        +"</div>");

            out.println("<marquee scrollamount=\"1\" scrolldelay=\"1\" direction=\"up\" >");
			CourseExporter allocatableExporter;
			allocatableExporter = new CourseExporter( raplaLocale, getClientFacade());
			BufferedWriter buf = new BufferedWriter(out);
		
			allocatableExporter.printKurseAmTag( buf, no_courses);
			buf.append("</marquee>");
			buf.append("</body>");
			buf.append("</html>");
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
