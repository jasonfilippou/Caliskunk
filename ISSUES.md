# ISSUES LOG 


### Mongo artifact 

Including the following artifact `spring-boot-starter-data-mongodb` in the pom results in
a `MongoSocketOpenException` thrown. Exact message:

```com.mongodb.MongoSocketOpenException: Exception opening socket```

The application **still** runs as navigating to `localhost:8080` betrays; just the exception is thrown.
I also changed the dependency order so that it  comes after
dependencies for `redis`, `rest`, `jersey` and `web`, but no dice. So I have commented it out for now. 

### AWS artifacts

Including the artifacts 

- `spring-cloud-starter-aws`
- `spring-cloud-starter-aws-jdbc`
- `spring-cloud-starter-aws-messaging`

from group `org.springframework.cloud` throws an isntance of `SdkClientException`.
Based on the various outputs I think these dependencies should only be included when deploying
on an EC2 instance. 

### Dummy test

When IntelliJ prepared the SpringBoot project, it included a dummy jUnit5 test under `src/test`, with
an empty test called `contextLoads()`. Commented that out so that `mvn test` doesn't complain.