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

public class SteleKursUebersichtPageGenerator3 extends RaplaComponent implements RaplaPageGenerator, TerminalConstants {

	Configuration config;
	public SteleKursUebersichtPageGenerator3(RaplaContext context, Configuration config) {
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
	
			out.println("  <link REL=\"stylesheet\" href=\"kursuebersicht.css\" type=\"text/css\">");
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
			out.println( "<table>" +
					"<tr>" +
					"<td><h1 id=\"kurs-row Gesundheitswesen/Arztassistent\">GPA&nbsp;&nbsp;<h1></td>" +
					"<td><h1 id=\"kurs-row Technik/Elektrotechnik\">TEL&nbsp;&nbsp;</h1></td>" +
					"<td><h1 id=\"kurs-row Technik/Informatik\">TINF&nbsp;&nbsp;</h1></td>" +
					"<td><h1 id=\"kurs-row Technik/Maschinenbau\">TMB&nbsp;&nbsp;</h1></td>" +
					"<td><h1 id=\"kurs-row Technik/Mechatronik\">TMT&nbsp;&nbsp;</h1></td>" +
					"<td><h1 id=\"kurs-row Technik/Papiertechnik\">TPT&nbsp;&nbsp;</h1></td>" +
					"<td><h1 id=\"kurs-row Technik/Sicherheitswesen\">TSHE&nbsp;&nbsp;</h1></td>" +
					"<td><h1 id=\"kurs-row Technik/Wirtschaftsingenieurwesen\">TWIW&nbsp;&nbsp;</h1></td>" +
					"<td><h1 id=\"kurs-row Wirtschaft/BWL-Bank\">WBK&nbsp;&nbsp;</h1></td>" +
					"<td><h1 id=\"kurs-row Wirtschaft/BWL-Handel\">WHD&nbsp;&nbsp;</h1></td>" +
					"<td><h1 id=\"kurs-row Wirtschaft/BWL-International Business\">WIB&nbsp;&nbsp;</h1></td>" +
					"<td><h1 id=\"kurs-row Wirtschaft/BWL-Industrie\">WIN&nbsp;&nbsp;</h1></td>" +
					"<td><h1 id=\"kurs-row Wirtschaft/RSW-Steuern- und Prüfungswesen\">WSP&nbsp;&nbsp;</h1></td>" +
					"<td><h1 id=\"kurs-row Wirtschaft/Unternehmertum\">WUN&nbsp;&nbsp;</h1></td>" +
					"<td><h1 id=\"kurs-row Wirtschaft/BWL-Versicherung\">WVS&nbsp;&nbsp;</h1></td>" +
					"<td><h1 id=\"kurs-row Wirtschaft/Wirtschaftsinformatik\">WWI&nbsp;&nbsp;</h1></td>" +
					"</tr>" +
					"</table>");
            out.println("  <div class=\"kurs-row\">"
                            +"<div class=\"kurs-column\"><img class=\"empty\" src=\"./images/empty.gif\">Kurs</div>"
                            +"<div class=\"time-column\"><img class=\"empty\" src=\"./images/empty.gif\">Zeitraum</div>"
                            +"<div class=\"name-column\"><img class=\"empty\" src=\"./images/empty.gif\">Veranstaltung</div>"
                            +"<div class=\"resource-column\"><img class=\"empty\" src=\"./images/empty.gif\">Raum</div>"
                        +"</div>");
			CourseExporter3 allocatableExporter;
			allocatableExporter = new CourseExporter3(config, raplaLocale, getClientFacade());
			out.println("<div id=\"content\">");
			BufferedWriter buf = new BufferedWriter(out);
		
			allocatableExporter.printKurseAmTag( buf, no_courses);
			
			buf.append("</div>\n");
			buf.append("<script> \n" +
					"var table = 1;\n" +
					"var t = \"t\";\n" +
					"function show(a){\n" +
				"var e = document.getElementsByClassName(a);\n" +
				"document.getElementById(a).setAttribute(\"style\", \"color:#e2001a\");\n" +
				"for(var i=0, len=e.length; i<len; ++i ){\n" +
				"e[i].style.display = \'block\';\n" +
				"}\n" +
				"}\n" +
				"function remove(b){\n" +
				"var f = document.getElementsByClassName(b);\n" +
				"document.getElementById(b).setAttribute(\"style\", \"color:#333333\");\n" +
				"for(var i=0, len=f.length; i<len; ++i ){\n" +
				"f[i].style.display = \'none\';\n" +
				"}\n" +
				"}\n" +
				"</script>\n" +
				"<script>\n" +
				"var myVar=setTimeout(function(){show(\"kurs-row Gesundheitswesen/Arztassistent\")},5000);\n" +
				"var myVar2=setTimeout(function(){remove(\"kurs-row Gesundheitswesen/Arztassistent\")},10000);\n" +
				"var myVar3=setTimeout(function(){show(\"kurs-row Technik/Elektrotechnik\")},10000);\n" +
				"var myVar4=setTimeout(function(){remove(\"kurs-row Technik/Elektrotechnik\")},15000);\n" +
				"var myVar5=setTimeout(function(){show(\"kurs-row Technik/Informatik\")},15000);\n" +
				"var myVar6=setTimeout(function(){remove(\"kurs-row Technik/Informatik\")},20000);\n" +
				"var myVar7=setTimeout(function(){show(\"kurs-row Technik/Maschinenbau\")},20000);\n" +
				"var myVar8=setTimeout(function(){remove(\"kurs-row Technik/Maschinenbau\")},25000);\n" +
				"var myVar9=setTimeout(function(){show(\"kurs-row Technik/Mechatronik\")},25000);\n" +
				"var myVar10=setTimeout(function(){remove(\"kurs-row Technik/Mechatronik\")},30000);\n" +
				"var myVar11=setTimeout(function(){show(\"kurs-row Technik/Papiertechnik\")},30000);\n" +
				"var myVar12=setTimeout(function(){remove(\"kurs-row Technik/Papiertechnik\")},35000);\n" +
				"var myVar13=setTimeout(function(){show(\"kurs-row Technik/Sicherheitswesen\")},35000);\n" +
				"var myVar14=setTimeout(function(){remove(\"kurs-row Technik/Sicherheitswesen\")},40000);\n" +
				"var myVar15=setTimeout(function(){show(\"kurs-row Technik/Wirtschaftsingenieurwesen\")},40000);\n" +
				"var myVar16=setTimeout(function(){remove(\"kurs-row Technik/Wirtschaftsingenieurwesen\")},45000);\n" +
				"var myVar17=setTimeout(function(){show(\"kurs-row Wirtschaft/BWL-Bank\")},45000);\n" +
				"var myVar18=setTimeout(function(){remove(\"kurs-row Wirtschaft/BWL-Bank\")},50000);\n" +
				"var myVar19=setTimeout(function(){show(\"kurs-row Wirtschaft/BWL-Handel\")},50000);\n" +
				"var myVar20=setTimeout(function(){remove(\"kurs-row Wirtschaft/BWL-Handel\")},55000);\n" +
				"var myVar19=setTimeout(function(){show(\"kurs-row Wirtschaft/BWL-International Business\")},55000);\n" +
				"var myVar20=setTimeout(function(){remove(\"kurs-row Wirtschaft/BWL-International Business\")},60000);\n" +
				"var myVar19=setTimeout(function(){show(\"kurs-row Wirtschaft/BWL-Industrie\")},60000);\n" +
				"var myVar20=setTimeout(function(){remove(\"kurs-row Wirtschaft/BWL-Industrie\")},65000);\n" +
				"var myVar19=setTimeout(function(){show(\"kurs-row Wirtschaft/RSW-Steuern- und Prüfungswesen\")},65000);\n" +
				"var myVar20=setTimeout(function(){remove(\"kurs-row Wirtschaft/RSW-Steuern- und Prüfungswesen\")},70000);\n" +
				"var myVar19=setTimeout(function(){show(\"kurs-row Wirtschaft/Unternehmertum\")},70000);\n" +
				"var myVar20=setTimeout(function(){remove(\"kurs-row Wirtschaft/Unternehmertum\")},75000);\n" +
				"var myVar19=setTimeout(function(){show(\"kurs-row Wirtschaft/BWL-Versicherung\")},750000);\n" +
				"var myVar20=setTimeout(function(){remove(\"kurs-row Wirtschaft/BWL-Versicherung\")},80000);\n" +
				"var myVar19=setTimeout(function(){show(\"kurs-row Wirtschaft/Wirtschaftsinformatik\")},80000);\n" +
				"var myVar20=setTimeout(function(){remove(\"kurs-row Wirtschaft/Wirtschaftsinformatik\")},85000);\n" +
				"var myVar21=setTimeout(function(){window.location.reload(1);}, 85000);" +
				"</script>\n");
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
