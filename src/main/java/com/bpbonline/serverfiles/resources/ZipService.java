/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bpbonline.serverfiles.resources;

import com.avbravo.jmoordbutils.JsfUtil;
import com.avbravo.jmoordbutils.fileencripter.FileDecryption;
import com.bpbonline.serverfiles.License;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;
import javax.json.stream.JsonParserFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 *
 * @author avbravo
 */
@Path("/zip")
public class ZipService {
 List<License> licenseList = new ArrayList<>();

    public List<License> getLicenseList() {
        return licenseList;
    }

    public void setLicenseList(List<License> licenseList) {
        this.licenseList = licenseList;
    }
    private String directory = JsfUtil.userHome() + JsfUtil.fileSeparator() + "fiscalserver" + JsfUtil.fileSeparator() + "license";

    @POST
    @Path("/upload")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.TEXT_PLAIN)
    public Response reciveFile(@Context HttpHeaders headers, InputStream fileInputStream) {
        MultivaluedMap<String, String> map = headers.getRequestHeaders();

        //getFileName
        String fileName = getFileName(map);

//        //get folder
//        String folder = getFolder(map);
        OutputStream out = null;

        File directorio = new File(directory);
        if (!directorio.exists()) {
            //Crear el directorio
            if (directorio.mkdirs()) {
                System.out.println("---> creado el directorio");
//
            } else {
                System.out.println("---> no se creo el directorio");
            }
        }

        String filePath = directory + JsfUtil.fileSeparator() + fileName;
        try {
            out = new FileOutputStream(new File(filePath));
            byte[] buf = new byte[1024];
            int len;
            while ((len = fileInputStream.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            //Desencripta el archivo
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //UNZIP ARCHIVO .ZIP
        if (JsfUtil.unzipFileToDirectory(filePath, JsfUtil.pathOfFile(filePath))) {
            // Despues de descomprimir se desencriptar  
          if( desencriptarFile()){
              readJson();
          }
        }

        return Response.status(Response.Status.OK).entity("File '" + filePath + "' uploaded successfully")
                .type(MediaType.TEXT_PLAIN).build();
    }

    private String getFileName(MultivaluedMap<String, String> headers) {
        try {
            String[] contentDisposition = headers.getFirst("Content-Disposition").split(";");
            for (String filename : contentDisposition) {
                if ((filename.trim().startsWith("filename"))) {
                    String[] name = filename.split("=");
                    String finalFileName = name[1].trim().replaceAll("\"", "");
                    return finalFileName;
                }
            }
        } catch (Exception e) {
            System.out.println("getFileName() " + e.getLocalizedMessage());
        }

        return "";
    }

//    private String getFolder(MultivaluedMap<String, String> headers) {
//        try {
//             String[] folder = headers.getFirst("folder").split(";");
//        for (String filename : folder) {
//            if ((filename.trim().startsWith("folder"))) {
//                String[] name = filename.split("=");
//                String finalFileName = name[1].trim().replaceAll("\"", "");
//                return finalFileName;
//            }
//        }
//        } catch (Exception e) {
//            System.out.println(" getFolder() "+e.getLocalizedMessage());
//        }
//       
//        return "";
//    }
    // <editor-fold defaultstate="collapsed" desc="Boolean desencriptarFile() ">
    public Boolean desencriptarFile() {
        try {
            String fileEnc = directory + JsfUtil.fileSeparator() + "authorizedlicense" + JsfUtil.fileSeparator() + "license" + JsfUtil.fileSeparator() + "license.enc";

            String fileIvEnc = directory + JsfUtil.fileSeparator() + "authorizedlicense" + JsfUtil.fileSeparator() + "license" + JsfUtil.fileSeparator() + "licenseiv.enc";
            String fileDes = directory + JsfUtil.fileSeparator() + "authorizedlicense" + JsfUtil.fileSeparator() + "license" + JsfUtil.fileSeparator() + "license.des";

            String keyDesCifrado = JsfUtil.desencriptar("Cwn31aDWCb1u4OKjX5QEsADO/jKu7/SQWpX0DnwlPGI=");
            String extension = "json";
            System.out.println("voy a descifrar ");
            if (FileDecryption.desencriptarFile(fileEnc, fileIvEnc, fileDes, keyDesCifrado, extension)) {

                System.out.println("Se desencripto archivo");
                return true;
            } else {

                System.out.println("No se desencripto el archivo");
                return false;
            }

        } catch (Exception e) {

            System.out.println("desencriptarFile()" + e.getLocalizedMessage());
        }
        return false;
    }
// </editor-fold>
    
    
     // <editor-fold defaultstate="collapsed" desc="String readJson()">
    public String readJson() {
        try {
            String json = directory + JsfUtil.fileSeparator() + "authorizedlicense" + JsfUtil.fileSeparator() + "license" + JsfUtil.fileSeparator() + "license_decrypted.json";
            InputStream is = new FileInputStream(json );

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
                    
                                    break;

                                case "key":
                                    parser.next();
                                    license.setKey(parser.getString());
                  
                                    break;
                                case "system":
                                    parser.next();
                                    license.setSystem(parser.getString());
                         
                                    break;
                                case "title":
                                    parser.next();
                                    license.setTitle(parser.getString());
                            
                                    break;

                                case "description":
                                    parser.next();
                                    license.setDescription(parser.getString());
                              
                                    break;
                                case "company":
                                    parser.next();
                                    license.setCompany(parser.getString());
                          
                                    break;
                                case "author":
                                    parser.next();
                                    license.setAuthor(parser.getString());
                        
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
