package com.github.lgdd.tess4jrest;

import static com.github.lgdd.tess4jrest.TesseractDataService.ADD_TESSERACT_DATA;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import io.vertx.mutiny.core.Vertx;
import javax.inject.Inject;
import org.jboss.logging.Logger;

@QuarkusMain
public class TesseractApplication {

  public static final String TESSDATA_PREFIX = System.getenv("TESSDATA_PREFIX");
  public static final String TESSERACT_DATA_SUFFIX = System.getenv("TESSERACT_DATA_SUFFIX");
  public static final String TESSERACT_DATA_VERSION = System.getenv("TESSERACT_DATA_VERSION");
  public static final String TESSERACT_DATA_LANGS = System.getenv("TESSERACT_DATA_LANGS");
  public static final String TESSDATA_BASE_URL =
      "https://github.com/tesseract-ocr/tessdata%s/raw/%s/%s.traineddata";

  private static final Logger log = Logger.getLogger(TesseractApplication.class);

  public static void main(String[] args) {
    Quarkus.run(TesseractDataInitializer.class, args);
  }

  public static class TesseractDataInitializer implements QuarkusApplication {

    @Inject Vertx vertx;

    @Override
    public int run(String... args) throws Exception {

      if (log.isDebugEnabled()) {
        log.debugf("TESSDATA_PREFIX=%s", TESSDATA_PREFIX);
        log.debugf("TESSERACT_DATA_SUFFIX=%s", TESSERACT_DATA_SUFFIX);
        log.debugf("TESSERACT_DATA_VERSION=%s", TESSERACT_DATA_VERSION);
        log.debugf("TESSERACT_DATA_LANGS=%s", TESSERACT_DATA_LANGS);
      }

      if (TESSERACT_DATA_LANGS != null && !TESSERACT_DATA_LANGS.isBlank()) {
        String comma = ",";
        if (TESSERACT_DATA_LANGS.contains(comma)) {
          String[] dataLangs = TESSERACT_DATA_LANGS.split(comma);
          for (String dataLang : dataLangs) {
            vertx.eventBus().publish(ADD_TESSERACT_DATA, dataLang);
          }
        } else {
          vertx.eventBus().publish(ADD_TESSERACT_DATA, TESSERACT_DATA_LANGS);
        }
      }
      Quarkus.waitForExit();
      return 0;
    }
  }
}
