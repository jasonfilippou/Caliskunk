# Green Taxi REST Architecture

### Run

Execute `mvn spring-boot:run`. This should start a Spring daemon process
on `localhost:8080` that provides the payload:

``` xml
{
  "_links" : {
    "profile" : {
      "href" : "http://localhost:8080/profile"
    }
  }
}
```

### Directory Structure

- `src/`: All source code for implementation and tests.

- `.mvn/`: Maven secret directory

- `doc/`: Documentation folder.

- `target/`: Standard Maven output folder.

- `pom.xml`: Standard Maven POM Contains dependencies generated from the [Spring Initializr](https://start.spring.io/), as well as dependencies for
[Mockito](https://site.mockito.org/) and the `SpringFox` [Swagger 2](https://swagger.io/specification/v2/) implementation
(more information about the interoperability between Swagger and SpringBoot [here](https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api)).

- `mvnw, mvnw.cmd`: Maven Wrapper Plugin bash and batch scripts. Downloaded by Maven.

- `.gitignore`: Standard Git IGNORE file.  

- `ISSUES.md`: Known issues with the code.

- `HELP.md`: Markdown - formatted help file downloaded by Maven. Has links to documentation about Maven and SpringBoot.

- `README.md`: Markdown - formatted current README file.