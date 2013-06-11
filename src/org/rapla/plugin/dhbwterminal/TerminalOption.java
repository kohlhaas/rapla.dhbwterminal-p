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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.swing.*;

import org.rapla.components.layout.TableLayout;
import org.rapla.entities.User;
import org.rapla.entities.dynamictype.DynamicType;
import org.rapla.entities.dynamictype.DynamicTypeAnnotations;
import org.rapla.framework.Configuration;
import org.rapla.framework.DefaultConfiguration;
import org.rapla.framework.PluginDescriptor;
import org.rapla.framework.RaplaContext;
import org.rapla.framework.RaplaException;
import org.rapla.gui.DefaultPluginOption;
import org.rapla.gui.internal.common.NamedListCellRenderer;

public class TerminalOption extends DefaultPluginOption {
    JTextField textField1;
    JTextField textField2;
    JTextField textField3;
    JList eventTypes;
    JList resourceTypes;
    JComboBox kursTyp;
    JComboBox raumTyp;
    JComboBox steleUser;

    public TerminalOption(RaplaContext sm) throws RaplaException {
        super(sm);
    }

    @Override
    protected JPanel createPanel() throws RaplaException {
        JPanel main = super.createPanel();
        double pre = TableLayout.PREFERRED;
        double fill = TableLayout.FILL;
        JPanel panel = new JPanel();
        panel.setLayout(new TableLayout(new double[][]{{pre, 5, pre, 5, pre}, {pre, pre, pre, pre, pre, pre, pre, pre, pre, pre, pre, pre, pre, pre, pre, fill}}));

        textField1 = new JTextField();
        addCopyPaste(textField1);
        textField1.setColumns(30);
        panel.add(new JLabel("Ueberschrift der Uebersichtsseite"), "0,0");
        panel.add(textField1, "2,0");
        textField1.setEnabled(true);

        textField2 = new JTextField();
        addCopyPaste(textField2);
        textField2.setColumns(30);
        panel.add(new JLabel("Text fuer keine Veranstaltungen"), "0,2");
        panel.add(textField2, "2,2");
        textField2.setEnabled(true);

        textField3 = new JTextField();
        addCopyPaste(textField3);
        textField3.setColumns(30);
        panel.add(new JLabel("URL fuer custom css"), "0,4");
        panel.add(textField3, "2,4");
        textField3.setEnabled(true);

        resourceTypes = new JList();
        resourceTypes.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        resourceTypes.setCellRenderer(new NamedListCellRenderer(getRaplaLocale().getLocale()));
        addCopyPaste(resourceTypes);
        panel.add(new JLabel("Exportierte Ressourcen"), "0,6");
        panel.add(new JScrollPane(resourceTypes), "2,6");
        resourceTypes.setEnabled(true);

        eventTypes = new JList();
        eventTypes.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        eventTypes.setCellRenderer(new NamedListCellRenderer(getRaplaLocale().getLocale()));
        addCopyPaste(eventTypes);
        panel.add(new JLabel("Exportierte Veranstaltungstypen"), "0,8");
        panel.add(new JScrollPane(eventTypes), "2,8");
        eventTypes.setEnabled(true);

        kursTyp = new JComboBox();
        kursTyp.setRenderer(new NamedListCellRenderer(getRaplaLocale().getLocale()));
        addCopyPaste(kursTyp);
        panel.add(new JLabel("Kurs Ressource"), "0,10");
        panel.add(kursTyp, "2,10");
        kursTyp.setEnabled(true);

        raumTyp = new JComboBox();
        raumTyp.setRenderer(new NamedListCellRenderer(getRaplaLocale().getLocale()));
        addCopyPaste(raumTyp);
        panel.add(new JLabel("Raum Ressource"), "0,12");
        panel.add(raumTyp, "2,12");
        raumTyp.setEnabled(true);

        steleUser = new JComboBox();
        steleUser.setRenderer(new NamedListCellRenderer(getRaplaLocale().getLocale()));
        addCopyPaste(steleUser);
        panel.add(new JLabel("Stele User"), "0,14");
        panel.add(steleUser, "2,14");
        steleUser.setEnabled(true);


        main.add(panel, BorderLayout.CENTER);
        return main;
    }

    public String getName(Locale locale) {
        return "Terminal Einstellungen";
    }

    protected void addChildren(DefaultConfiguration newConfig) {
        {
            DefaultConfiguration conf = new DefaultConfiguration("ueberschrift");
            conf.setValue(textField1.getText());
            newConfig.addChild(conf);
        }
        {
            DefaultConfiguration conf = new DefaultConfiguration("keinekurse");
            conf.setValue(textField2.getText());
            newConfig.addChild(conf);
        }
        {
            DefaultConfiguration conf = new DefaultConfiguration("cssurl");
            conf.setValue(textField3.getText());
            newConfig.addChild(conf);
        }
        {
            DefaultConfiguration conf = new DefaultConfiguration(TerminalConstants.EVENT_TYPES_KEY);
            String value = getDynamicTypeKeysFromListSelection(eventTypes.getSelectedValuesList());
            conf.setValue(value);
            newConfig.addChild(conf);
        }
        {
            DefaultConfiguration conf = new DefaultConfiguration(TerminalConstants.RESOURCE_TYPES_KEY);
            String value = getDynamicTypeKeysFromListSelection(resourceTypes.getSelectedValuesList());
            conf.setValue(value);
            newConfig.addChild(conf);
        }
        {
            DefaultConfiguration conf = new DefaultConfiguration(TerminalConstants.ROOM_KEY);
            conf.setValue(((DynamicType) raumTyp.getSelectedItem()).getElementKey());
            newConfig.addChild(conf);
        }
        {
            DefaultConfiguration conf = new DefaultConfiguration(TerminalConstants.KURS_KEY);
            conf.setValue(((DynamicType) kursTyp.getSelectedItem()).getElementKey());
            newConfig.addChild(conf);
        }
        {
            DefaultConfiguration conf = new DefaultConfiguration(TerminalConstants.USER_KEY);
            conf.setValue(((User) steleUser.getSelectedItem()).getUsername());
            newConfig.addChild(conf);
        }


    }

    private String getDynamicTypeKeysFromListSelection(List selectedValuesList) {
        StringBuilder b = new StringBuilder();
        for (int i = 0, selectedValuesListSize = selectedValuesList.size(); i < selectedValuesListSize; i++) {
            Object o = selectedValuesList.get(i);
            b.append(((DynamicType) o).getElementKey());
            if (i < selectedValuesListSize - 1)
                b.append(",");
        }

        return b.toString();
    }

    protected void readConfig(Configuration config) {
        {
            String value = config.getChild("ueberschrift").getValue(TerminalConstants.KURS_UEBERSCHRIFT);
            textField1.setText(value);
        }
        {
            String value = config.getChild("keinekurse").getValue(TerminalConstants.NO_COURSES);
            textField2.setText(value);
        }
        {
            String value = config.getChild("cssurl").getValue("");
            textField3.setText(value);
        }

        DynamicType[] resources = new DynamicType[0];
        try {
            resources = getAllRessources();
        } catch (RaplaException e) {
            getLogger().error(e.getMessage(), e);
        }


        {
            String value = config.getChild(TerminalConstants.KURS_KEY).getValue("");
            kursTyp.setModel(new DefaultComboBoxModel(resources));
            updateSelectionModel(value, kursTyp);
        }

        {
            String value = config.getChild(TerminalConstants.ROOM_KEY).getValue("");
            raumTyp.setModel(new DefaultComboBoxModel(resources));

            updateSelectionModel(value, raumTyp);
        }

        {
            String value = config.getChild(TerminalConstants.USER_KEY).getValue("");
            try {
                steleUser.setModel(new DefaultComboBoxModel(getClientFacade().getUsers()));
            } catch (RaplaException e) {
                getLogger().error(e.getMessage(), e);
            }
            updateUserSelectionModel(value, steleUser);
        }


        {
            try {
                DefaultListModel model = new DefaultListModel();
                DynamicType[] dynamicTypes = getClientFacade().getDynamicTypes(DynamicTypeAnnotations.VALUE_CLASSIFICATION_TYPE_RESERVATION);
                for (DynamicType dynamicType : dynamicTypes) {
                    model.addElement(dynamicType);
                }
                eventTypes.setModel(model);
            } catch (RaplaException e) {
                getLogger().error(e.getMessage(), e);
            }

            String value = config.getChild(TerminalConstants.EVENT_TYPES_KEY).getValue("");
            String[] keys = value.split(",");
            updateSelectionModel(keys, eventTypes);
        }

        {


            DefaultListModel model = new DefaultListModel();
            for (DynamicType dynamicType : resources) {
                model.addElement(dynamicType);
            }
            resourceTypes.setModel(model);

            String value = config.getChild(TerminalConstants.RESOURCE_TYPES_KEY).getValue("");
            String[] keys = value.split(",");
            updateSelectionModel(keys, resourceTypes);
        }

    }

    private DynamicType[] getAllRessources() throws RaplaException {
        DynamicType[] resources = getClientFacade().getDynamicTypes(DynamicTypeAnnotations.VALUE_CLASSIFICATION_TYPE_RESOURCE);
        DynamicType[] persons = getClientFacade().getDynamicTypes(DynamicTypeAnnotations.VALUE_CLASSIFICATION_TYPE_PERSON);
        //DynamicType [] all = new DynamicType[resources.length + persons.length];
        resources = Arrays.copyOf(resources, resources.length + persons.length);
        System.arraycopy(persons, 0, resources, resources.length - persons.length, persons.length);
        return resources;
    }

    private void updateSelectionModel(String value, JComboBox comboBox) {
        ComboBoxModel model = comboBox.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            DynamicType d = (DynamicType) comboBox.getModel().getElementAt(i);
            if (d.getElementKey().equals(value)) {
                comboBox.setSelectedItem(d);
                break;
            }
        }
    }

    private void updateUserSelectionModel(String value, JComboBox comboBox) {
        ComboBoxModel model = comboBox.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            User d = (User) comboBox.getModel().getElementAt(i);
            if (d.getUsername().equals(value)) {
                comboBox.setSelectedItem(d);
                break;
            }
        }
    }


    private void updateSelectionModel(String[] keys, JList list) {
        Arrays.sort(keys);
        final ListModel model = list.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            DynamicType d = (DynamicType) model.getElementAt(i);
            if (Arrays.binarySearch(keys, d.getElementKey()) >= 0) {
                list.getSelectionModel().addSelectionInterval(i, i);
            }
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
