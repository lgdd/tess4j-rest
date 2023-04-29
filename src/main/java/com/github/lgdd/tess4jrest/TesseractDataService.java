package com.github.lgdd.tess4jrest;

import static com.github.lgdd.tess4jrest.TesseractApplication.*;

import io.quarkus.vertx.ConsumeEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class TesseractDataService {

  public static final String ADD_TESSERACT_DATA = "add-tesseract-data";

  private static final Logger log = Logger.getLogger(TesseractDataService.class);

  @ConsumeEvent(ADD_TESSERACT_DATA)
  public void addTesseractData(String dataLang) throws IOException {
    // to point to the right repo: tessdata, tessdata_best or tessdata_fast
    String formattedDataSuffix =
        TESSERACT_DATA_SUFFIX.isBlank() ? TESSERACT_DATA_SUFFIX : "_" + TESSERACT_DATA_SUFFIX;
    String target =
        String.format(TESSDATA_BASE_URL, formattedDataSuffix, TESSERACT_DATA_VERSION, dataLang);
    String[] targetChunks = target.split("/");
    String fileName = targetChunks[targetChunks.length - 1];
    String targetPath = String.format("%s/%s", TESSDATA_PREFIX, fileName);
    if (!Files.exists(Path.of(targetPath))) {
      URL url = new URL(target);
      if (log.isInfoEnabled()) {
        log.infof("Downloading %s data from %s", dataLang.toUpperCase(), url);
      }
      ReadableByteChannel sourceByteChannel = Channels.newChannel(url.openStream());
      try (FileOutputStream targetOutputStream = new FileOutputStream(targetPath)) {
        targetOutputStream.getChannel().transferFrom(sourceByteChannel, 0, Long.MAX_VALUE);
      }
    } else {
      if (log.isInfoEnabled()) {
        log.infof("%s already supported with %s", dataLang.toUpperCase(), targetPath);
      }
    }
  }
}
