package com.github.lgdd.tess4jrest;

import static com.github.lgdd.tess4jrest.TesseractApplication.TESSDATA_PREFIX;
import static com.github.lgdd.tess4jrest.TesseractApplication.TESSERACT_DATA_LANGS;

import java.nio.file.Files;
import java.nio.file.Path;
import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.eclipse.microprofile.health.Readiness;

@Liveness
@Readiness
@ApplicationScoped
public class TesseractDataHealthCheck implements HealthCheck {

  @Override
  public HealthCheckResponse call() {

    if (TESSERACT_DATA_LANGS != null && !TESSERACT_DATA_LANGS.isBlank()) {
      String comma = ",";
      if (TESSERACT_DATA_LANGS.contains(comma)) {
        String[] dataLangs = TESSERACT_DATA_LANGS.split(comma);
        for (String dataLang : dataLangs) {
          String fileName = String.format("%s.traineddata", dataLang);
          String targetPath = String.format("%s/%s", TESSDATA_PREFIX, fileName);
          if (!Files.exists(Path.of(targetPath))) {
            return HealthCheckResponse.down(
                String.format("Missing expected data file %s", targetPath));
          }
        }
      }
    }
    return HealthCheckResponse.up("Tesseract data files are available.");
  }
}
