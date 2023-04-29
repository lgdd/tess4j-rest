# Tess4J-REST

OCR REST API using [Tesseract OCR Engine](https://github.com/tesseract-ocr/tesseract) (via [Tess4J](https://github.com/nguyenq/tess4j))

## Docker Image

Docker image available: https://hub.docker.com/r/lgdd/tess4j-rest

Try and run:

```sh
docker run -it --rm -p 8000:8000 lgdd/tess4j-rest
```

## Usage

Run `docker-compose up --build` (also available as `make dev`).

> **Note**: You can also run `./mvnw quarkus:dev` (or `quarkus dev`).
> But for this method to work, you would need the environment variable _TESSDATA_PREFIX_ to be set to the absolute path of this project resource: `src/test/resources/test-tessdata/eng.traineddata`

You can navigate to `http://localhost:8000/q/swagger-ui` and test uploading an image.

Or you can quickly test the endpoint with `curl` (from this project root):

```sh
curl -X 'POST' \
     'http://localhost:8000/detect-text' \
     -H 'accept: text/plain' \
     -H 'Content-Type: multipart/form-data' \
     -F 'file=@src/test/resources/test-data/eurotext.png'
```

## Environment variables

```Dockerfile
# Parent folder path for tesseract data files
ENV TESSDATA_PREFIX="/opt/tesseract/tessdata"

# Suffix for the data repository to use.
# Either "best", "fast" or "".
# See https://github.com/tesseract-ocr/tessdata#readme
ENV TESSERACT_DATA_SUFFIX="best"

# Version of the data repository.
# See https://github.com/tesseract-ocr/tessdata#readme
ENV TESSERACT_DATA_VERSION="4.1.0"

# Additional languages to download on the application startup.
# For the possible values, see https://github.com/tesseract-ocr/tessdata
ENV TESSERACT_DATA_LANGS="fra,spa,deu"
```

## Health probes

Readiness: `/q/healh/ready`

Liveness: `/q/healh/live`

Application is ready and live when all additional languages has been downloaded.

## License

[MIT](LICENSE)
