/*--------------------------------------------------------------------------*
 | Copyright (C) 2006 Christopher Kohlhaas                                  |
 |                                                                          |
 | This program is free software; you can redistribute it and/or modify     |
 | it under the terms of the GNU General Public License as published by the |
 | Free Software Foundation. A copy of the license has been included with   |
 | these distribution in the COPYING file, if not go to www.fsf.org         |
 |                                                                          |
 | As a special exception, you are granted the permissions to link this     |
 | program with every library, which license fulfills the Open Source       |
 | Definition as published by the Open Source Initiative (OSI).             |
 *--------------------------------------------------------------------------*/
package org.rapla.plugin.dhbwterminal;
import java.net.URL;

import org.rapla.framework.Configuration;
import org.rapla.framework.Container;
import org.rapla.framework.PluginDescriptor;
import org.rapla.framework.RaplaContextException;
import org.rapla.framework.StartupEnvironment;
import org.rapla.framework.logger.Logger;
import org.rapla.plugin.RaplaExtensionPoints;
import org.rapla.server.ServerServiceContainer;
import org.rapla.servletpages.RaplaResourcePageGenerator;

/**
   This is a demonstration of a rapla-plugin. It adds a sample usecase and option
   to the rapla-system.
 */

public class TerminalPlugin implements PluginDescriptor
{
    Logger logger;
    
    public TerminalPlugin(Logger logger) {
        this.logger = logger;
    }
   
    public Logger getLogger() {
        return logger;
    }

    public String toString() {
        return "DHBW Info Terminal";
    }

    /**
     * @throws RaplaContextException 
     * @see org.rapla.framework.PluginDescriptor#provideServices(org.rapla.framework.general.Container)
     */
    public void provideServices(Container container, Configuration config) throws RaplaContextException {
        if ( !config.getAttributeAsBoolean("enabled", false) )
        	return;

        StartupEnvironment env = container.getStartupEnvironment();
        container.addContainerProvidedComponent(RaplaExtensionPoints.PLUGIN_OPTION_PANEL_EXTENSION, TerminalOption.class);
       
        if ( env.getStartupMode() == StartupEnvironment.SERVLET) {
        	ServerServiceContainer serviceContainer = container.getContext().lookup( ServerServiceContainer.class);
        	serviceContainer.addWebpage("terminal-export",SteleExportPageGenerator.class );
        	serviceContainer.addWebpage("terminal-kurse", SteleKursUebersichtPageGenerator.class );
            try {
                RaplaResourcePageGenerator resourcePageGenerator = container.getContext().lookup(RaplaResourcePageGenerator.class);
                // registers the standard calendar files
                
                URL resource = this.getClass().getResource("/org/rapla/plugin/dhbwterminal/kursuebersicht.css");
				resourcePageGenerator.registerResource( "kursuebersicht.css", "text/css", resource);
            } catch ( Exception ex) {
            	getLogger().error("Could not initialize autoexportplugin on server" , ex);
            }
        }
   	     
    }

    public Object getPluginMetaInfos( String key )
    {
        return null;
    }

}

