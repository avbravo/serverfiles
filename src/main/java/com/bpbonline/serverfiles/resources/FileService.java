/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bpbonline.serverfiles.resources;

import com.avbravo.jmoordbutils.JsfUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
@Path("/file")
public class FileService {

    private String directory = JsfUtil.userHome() + JsfUtil.fileSeparator() + "fiscalserver" + JsfUtil.fileSeparator() + "license";

    @POST
    @Path("/upload")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.TEXT_PLAIN)
    public Response sendFile(@Context HttpHeaders headers, InputStream fileInputStream) {
        MultivaluedMap<String, String> map = headers.getRequestHeaders();
        String fileName = getFileName(map);
        OutputStream out = null;

        File directorio = new File(directory);
        if (!directorio.exists()) {
            //Crear el directorio
//            if (directorio.mkdirs()) {
//                System.out.println("---> creado el directorio");
//
//            } else {
//                System.out.println("---> no se creo el directorio");
//            }
        }

        String filePath = directory + JsfUtil.fileSeparator() + fileName;
        try {
            out = new FileOutputStream(new File(filePath));
            byte[] buf = new byte[1024];
            int len;
            while ((len = fileInputStream.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
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
        return Response.status(Response.Status.OK).entity("File '" + filePath + "' uploaded successfully")
                .type(MediaType.TEXT_PLAIN).build();
    }

    private String getFileName(MultivaluedMap<String, String> headers) {
        String[] contentDisposition = headers.getFirst("Content-Disposition").split(";");
        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {
                String[] name = filename.split("=");
                String finalFileName = name[1].trim().replaceAll("\"", "");
                return finalFileName;
            }
        }
        return "";
    }

}
