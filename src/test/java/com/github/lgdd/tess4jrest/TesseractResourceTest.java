package com.github.lgdd.tess4jrest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import io.quarkus.test.junit.QuarkusTest;
import java.io.File;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class TesseractResourceTest {

  private final String testResourcesData = "src/test/resources/test-data/";

  @Test
  public void testDetectTextEndpoint() {
    File testFile = new File(testResourcesData, "eurotext.png");
    String expectedText = "The (quick) [brown] {fox} jumps!\nOver the $43,456.78 <lazy> #90 dog";

    given()
        .multiPart(testFile)
        .when()
        .post("/detect-text")
        .then()
        .statusCode(200)
        .body(containsString(expectedText));
  }

  @Test
  public void testDetectTextEndpointWithLang() {
    File testFile = new File(testResourcesData, "eurotext.png");
    String expectedText = "Le renard brun\n"
        + "«rapide» saute par-dessus le chien\n"
        + "paresseux.";

    given()
        .multiPart(testFile)
        .when()
        .post("/detect-text?lang=fra")
        .then()
        .statusCode(200)
        .body(containsString(expectedText));
  }

  @Test
  public void testDetectTextEndpointWithWrongFile() {
    File testFile = new File(testResourcesData, "eurotext.txt");

    given()
        .multiPart(testFile)
        .when()
        .post("/detect-text")
        .then()
        .statusCode(500)
        .body(containsString("Unsupported"));
  }
}