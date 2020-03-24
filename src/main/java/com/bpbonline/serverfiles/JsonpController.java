/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bpbonline.serverfiles;

import com.avbravo.jmoordbutils.JsfUtil;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;

/**
 *
 * @author avbravo
 */
@Named(value = "jsonpController")
@ViewScoped
public class JsonpController implements Serializable {

    private static final long serialVersionUID = 1L;
    List<License> licenseList = new ArrayList<>();

    public List<License> getLicenseList() {
        return licenseList;
    }

    public void setLicenseList(List<License> licenseList) {
        this.licenseList = licenseList;
    }

    /**
     * Creates a new instance of jsonpController
     */
    public JsonpController() {
    }

    // <editor-fold defaultstate="collapsed" desc="String readJson()">
    public String readJson() {
        try {
            InputStream is = new FileInputStream("/home/avbravo/Descargas/license.json");

            JsonParserFactory factory = Json.createParserFactory(null);
            JsonParser parser = factory.createParser(is, StandardCharsets.UTF_8);

            if (!parser.hasNext() && parser.next() != JsonParser.Event.START_ARRAY) {
                JsfUtil.warningDialog("warning", "No se abrio el archivo");
                return "";
            }
            // looping over object attributes

            licenseList = new ArrayList<>();
            License license = new License();
            while (parser.hasNext()) {
                System.out.println("<----------------------------------->");
                System.out.println("<----------------------------------->");
                JsonParser.Event event = parser.next();

                // starting object
                if (event == JsonParser.Event.START_OBJECT) {

                    while (parser.hasNext()) {

                        event = parser.next();

                        if (event == JsonParser.Event.KEY_NAME) {

                            String key = parser.getString();

                            switch (key) {

                                case "idlicense":
                                    parser.next();
                                    license = new License();
                                    license.setIdlicense(parser.getString());
                                    System.out.printf("id: %s%n", parser.getString());
                                    break;

                                case "key":
                                    parser.next();
                                    license.setKey(parser.getString());
                                    System.out.printf("key: %s%n", parser.getString());
                                    break;
                                case "system":
                                    parser.next();
                                    license.setSystem(parser.getString());
                                    System.out.printf("title: %s%n", parser.getString());
                                    break;
                                case "title":
                                    parser.next();
                                    license.setTitle(parser.getString());
                                    System.out.printf("title: %s%n", parser.getString());
                                    break;

                                case "description":
                                    parser.next();
                                    license.setDescription(parser.getString());
                                    System.out.printf("description: %s%n%n", parser.getString());
                                    break;
                                case "company":
                                    parser.next();
                                    license.setCompany(parser.getString());
                                    System.out.printf("company: %s%n%n", parser.getString());
                                    break;
                                case "author":
                                    parser.next();
                                    license.setAuthor(parser.getString());
                                    System.out.printf("Author: %s%n%n", parser.getString());
                                    System.out.println("----> agregando al lit");
                                    licenseList.add(license);
                                    break;

                            }
                        }
                    }
                }
            }

            JsfUtil.infoDialog("Terminado", "Proceso terminado...size(): " + licenseList.size());
            System.out.println("IMPRIMIR LAS LICENCIAS");
            for (License l : licenseList) {
                System.out.println("---> idlicense " + l.getIdlicense());
                System.out.println("---> KEY " + l.getKey());
            }

            return "";
        } catch (Exception e) {
            JsfUtil.errorDialog("error ", e.getLocalizedMessage());
        }
        return "";
    }

    // </editor-fold>
}
