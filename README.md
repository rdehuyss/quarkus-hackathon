## Quarkus Hackathon

This application shows a demo of how to use JobRunr together with Quarkus - it's the ideal tool for long-running background jobs in a cloud native environment.

In this application, we create salary slips for all the employees of Acme Corp.  

<p align="center">
[![Quarkus Hackathon submission](https://img.youtube.com/vi/pemNdnEadzU/0.jpg)](https://www.youtube.com/watch?v=pemNdnEadzU)
</p>

### Architecture
The architecture is as follows:
- we have a Postgres database in which employees are available
- a recurring job triggers every sunday and creates a job to create and send the salary slip for each employee 
- that salary slip job does the following:
  - it accepts the employeeId
  - with that employeeId it retrieves the employee from the DB 
  - it collects the hours worked for that week from the TimeClockService
  - an HTML document is generated using Quarkus templates
  - that HTML document is send to Gotenberg, a microservice that converts documents to PDF's, via the RestEasy client and saved to a NAS (in this case /tmp)
  - the PDF is then send via mail to the employee
  
### How to run
#### Make sure the following docker containers are running:
- `docker run -e POSTGRES_PASSWORD=Pass2020! -p 5432:5432 -d postgres`
- `docker run -e MAX_WAIT_TIMEOUT=30 -e DEFAULT_WAIT_TIMEOUT=30 -p 3000:3000 -d thecodingmachine/gotenberg`

#### Add JobRunr to maven
I did some small changes to JobRunr that I first want to test further before releasing it. To make this project work, please extract the following folder to your local m2/repository folder:
[jobrunr.zip](https://github.com/rdehuyss/quarkus-hackathon/files/4965452/m2-jobrunr.zip)

### Native image
A precompiled native image for this project is available here: [quarkus-hackathon-native-image.zip](https://github.com/rdehuyss/quarkus-hackathon/files/4965464/example-quarkus-1.0.zip)
To run multiple instances, please start them as follows (the port is different for each instance):
- `export QUARKUS_HTTP_PORT=8081; ./target/example-quarkus-1.0.0-SNAPSHOT-runner`
- `export QUARKUS_HTTP_PORT=8082; ./target/example-quarkus-1.0.0-SNAPSHOT-runner`



## Problems I encountered (Todo's):

##### SerializedLambda
JobRunr makes heavy use of Reflection and [SerializedLambda](https://docs.oracle.com/javase/8/docs/api/java/lang/invoke/SerializedLambda.html)
This is to analyse a given lambda and find the correct method to execute: e.g.:
`BackgroundJob.execute(() -> service.sendMailToUser(emailAddressViaParameter))`
Idea to fix: custom extension (work has started) to do static code analysis and replace the above with the below using static code analysis:
`BackgroundJob.execute(() -> new JobDetails(service.getClass(), "sendMailToUser", emailAddressViaParameter)`


#####  ClassLoader.getResources(...) is empty
- see https://github.com/oracle/graal/issues/1108
=> adapted JobRunr 

##### ManagementFactory.getPlatformMBeanServer() returns null
- see https://github.com/oracle/graal/issues/2103
=> adapted JobRunr 

#####  java.lang.ClassNotFoundException: org.apache.commons.logging.impl.LogFactoryImpl
- RestEasy client depends on apache http-client which depends on apache commons logging
 https://stackoverflow.com/questions/56871033/how-to-fix-org-apache-commons-logging-impl-logfactoryimpl-not-found-in-native
- mvn dependency:tree does not show apache commons logging as (transitive) dependency 
- StackTrace:
```
Caused by: org.apache.commons.logging.LogConfigurationException: java.lang.ClassNotFoundException: org.apache.commons.logging.impl.LogFactoryImpl (Caused by java.lang.ClassNotFoundException: org.apache.commons.logging.impl.LogFactoryImpl)
        at org.apache.commons.logging.LogFactory.createFactory(LogFactory.java:1158)
        at org.apache.commons.logging.LogFactory$2.run(LogFactory.java:960)
        at java.security.AccessController.doPrivileged(AccessController.java:81)
        at org.apache.commons.logging.LogFactory.newFactory(LogFactory.java:957)
        at org.apache.commons.logging.LogFactory.getFactory(LogFactory.java:624)
        at org.apache.commons.logging.LogFactory.getLog(LogFactory.java:655)
        at org.apache.http.conn.ssl.DefaultHostnameVerifier.<init>(DefaultHostnameVerifier.java:82)
        at org.apache.http.conn.ssl.DefaultHostnameVerifier.<init>(DefaultHostnameVerifier.java:91)
        at org.jboss.resteasy.client.jaxrs.engines.ClientHttpEngineBuilder43.build(ClientHttpEngineBuilder43.java:66)
        at org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl.build(ResteasyClientBuilderImpl.java:393)
        at org.jboss.resteasy.microprofile.client.RestClientBuilderImpl.build(RestClientBuilderImpl.java:270)
        at io.quarkus.restclient.runtime.RestClientBase.create(RestClientBase.java:65)
        at org.jobrunr.examples.salaryslip.DocumentGenerationRestApi_a7074a55f3a03b03fa9f85cd24c97ccfa878ccbb_Synthetic_Bean.create(DocumentGenerationRestApi_a7074a55f3a03b03fa9f85cd24c97ccfa878ccbb_Synthetic_Bean.zig:144)
        at org.jobrunr.examples.salaryslip.DocumentGenerationRestApi_a7074a55f3a03b03fa9f85cd24c97ccfa878ccbb_Synthetic_Bean.get(DocumentGenerationRestApi_a7074a55f3a03b03fa9f85cd24c97ccfa878ccbb_Synthetic_Bean.zig:175)
        at org.jobrunr.examples.salaryslip.DocumentGenerationRestApi_a7074a55f3a03b03fa9f85cd24c97ccfa878ccbb_Synthetic_Bean.get(DocumentGenerationRestApi_a7074a55f3a03b03fa9f85cd24c97ccfa878ccbb_Synthetic_Bean.zig:198)
        at io.quarkus.arc.impl.CurrentInjectionPointProvider.get(CurrentInjectionPointProvider.java:53)
        at org.jobrunr.examples.salaryslip.DocumentGenerationService_Bean.create(DocumentGenerationService_Bean.zig:243)
        ... 36 more
```
=> added commons-logging and configuration in reflection-config.json

##### JsonB works differently in native mode than in hotspot mode 
(further analysis needed)


sudo apt install python3-cairo python3-xlib python3-xdg gir1.2-gudev-1.0 gir1.2-gst-plugins-base-1.0 gir1.2-appindicator3-0.1 gir1.2-keybinder-3.0
