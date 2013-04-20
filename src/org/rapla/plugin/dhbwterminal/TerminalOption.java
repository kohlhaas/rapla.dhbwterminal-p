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

import java.awt.BorderLayout;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.rapla.components.layout.TableLayout;
import org.rapla.framework.Configuration;
import org.rapla.framework.DefaultConfiguration;
import org.rapla.framework.PluginDescriptor;
import org.rapla.framework.RaplaContext;
import org.rapla.framework.RaplaException;
import org.rapla.gui.DefaultPluginOption;

public class TerminalOption extends DefaultPluginOption {
    JTextField textField1;
    JTextField textField2;
    
    public TerminalOption(RaplaContext sm) throws RaplaException {
        super( sm);
    }
    
    @Override
    protected JPanel createPanel() throws RaplaException {
    	JPanel main =  super.createPanel();
    	double pre = TableLayout.PREFERRED;
    	double fill = TableLayout.FILL;
    	JPanel panel = new JPanel();
    	panel.setLayout( new TableLayout(new double[][] {{pre, 5, pre,5, pre}, {pre,pre, pre,pre, fill}}));
    	
         textField1 = new JTextField();
         addCopyPaste( textField1);
         textField1.setColumns(30);
         panel.add( new JLabel("Ueberschrift der Uebersichtsseite"),"0,0"  );
         panel.add( textField1,"2,0");
         textField1.setEnabled(true);
         
         textField2 = new JTextField();
         addCopyPaste( textField1);
         textField2.setColumns(30);
         panel.add( new JLabel("Text fuer keine Veranstaltungen"),"0,2"  );
         panel.add( textField2,"2,2");
         textField2.setEnabled(true);
         
         main.add(panel, BorderLayout.CENTER);
         return main;
    }
    
    public String getName(Locale locale) {
        return "Stele Einstellungen";
    }

    protected void addChildren( DefaultConfiguration newConfig) 
    {
    	{
	    	DefaultConfiguration conf = new DefaultConfiguration("ueberschrift");
	        conf.setValue( textField1.getText() );
	        newConfig.addChild( conf );
    	}
    	{
	    	DefaultConfiguration conf = new DefaultConfiguration("keinekurse");
	        conf.setValue( textField2.getText() );
	        newConfig.addChild( conf );
    	}
    }

    protected void readConfig( Configuration config)   
    {
    	{
    		String value = config.getChild("ueberschrift").getValue(TerminalConstants.KURS_UEBERSCHRIFT);
    		textField1.setText( value );
    	}
    	{
    		String value = config.getChild("keinekurse").getValue(TerminalConstants.NO_COURSES);
    		textField2.setText( value );
    	}

    }
    
    
	@Override
	public Class<? extends PluginDescriptor<?>> getPluginClass() {
		return TerminalPlugin.class;
	}
	
	@Override
	public String getName(Object object) {
        return "DHBW Info Terminal";
	}


}
