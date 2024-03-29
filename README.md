# Mock CDN [WIP]

This project is a tool for mocking CDN in your pet projects.

It allows you to upload an image and get a URL you can embed
to your UI, instead of spinning up actual CDN.

## Execute

- run [MockCdnApplication.java](src%2Fmain%2Fjava%2Fcom%2Fandriidnikitin%2Ftools%2FMockCdnApplication.java).
  alternatively (to run in docker) - execute [build.sh](build.sh)
- open http://localhost:8080

### Links

- write file to mongodb as BSON: https://www.mongodb.com/docs/spark-connector/current/streaming-mode/streaming-write/
- upload file [done]: https://spring.io/guides/gs/uploading-files
- dockerize: https://spring.io/guides/topicals/spring-boot-docker
- create swagger for api: https://www.baeldung.com/spring-rest-openapi-documentation
- generate controller from OpenAPI doc: https://www.baeldung.com/java-openapi-generator-server
