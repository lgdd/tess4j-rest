package com.github.lgdd.tess4jrest;

import static com.github.lgdd.tess4jrest.TesseractApplication.TESSERACT_DATA_LANGS;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

@Path("/")
public class TesseractResource {

  private static final Logger log = Logger.getLogger(TesseractResource.class);
  private final ITesseract tesseract = new Tesseract();

  @POST
  @Path("/detect-text")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.TEXT_PLAIN)
  public Response detectText(
      @RequestBody(
              description =
                  "Support TIFF, JPEG, GIF, PNG, and BMP image formats and PDF document format.",
              required = true,
              content = @Content(schema = @Schema(implementation = FileUploadSchema.class)))
          @RestForm("file")
          FileUpload fileUpload,
          @QueryParam("lang") String lang) {

    if(lang != null && TESSERACT_DATA_LANGS.contains(lang)) {
        log.infof("Lang=%s", lang);
        tesseract.setLanguage(lang);
    } else if(lang != null && !TESSERACT_DATA_LANGS.contains(lang)) {
      log.warnf("Trained data was not downloaded for %s", lang);
      log.warnf("You can add a lang to the environment variable TESSERACT_DATA_LANGS (see https://github.com/lgdd/tess4j-rest?tab=readme-ov-file#environment-variables)");
    } else {
      log.warn("No lang was selected. For better detection, consider passing the corresponding lang parameter (e.g. lang=fra for French)");
    }

    String detectedText;
    try {
      // Copy the uploaded file to the temp directory using the filename
      // so the ImageIOHelper class in tess4j can properly scan metadata.
      java.nio.file.Path path =
          java.nio.file.Path.of(System.getProperty("java.io.tmpdir") + "/" + fileUpload.fileName());
      Files.deleteIfExists(path);
      Files.copy(fileUpload.uploadedFile().toAbsolutePath(), path);
      File file = new File(path.toString());

      if (log.isInfoEnabled()) {
        log.info("Received '" + fileUpload.fileName() + "'");
      }

      // Extract the text and delete the created file.
      // Quarkus will delete the uploaded file itself (cf. application.properties).
      detectedText = tesseract.doOCR(file);

      if (log.isInfoEnabled()) {
        log.info(
            "Detected " + detectedText.length() + " characters in '" + fileUpload.fileName() + "'");
      }

      Files.delete(path);
    } catch (IOException | TesseractException e) {
      log.error(e);
      return Response.serverError().entity(e.getMessage()).build();
    }

    return Response.ok().entity(detectedText).build();
  }

  // Allows Swagger UI to display multipart input as binary instead of text.
  static class FileUploadSchema {
    InputStream file;
  }
}
