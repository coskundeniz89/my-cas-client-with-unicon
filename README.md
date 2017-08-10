## CAS client web application

This is the simplest CASyfied Spring Boot application that there could be.

It could be used as a template to build more complex CAS-enabled Spring Boot apps, 
or simply as a quick tester for various CAS servers installations.

### To get started

* Make sure you have Java 8 installed (it won't work on Java versions less than 8)
* Make sure you started CAS server. 

### Customize Yml File

* Change 3 required URL properties in `src/main/resources/application.yml` pointing to the desired CAS server and client host. For example:

```yaml
  cas:
    #Required properties
    server-url-prefix: https://localhost:10100/cas
    server-login-url: https://localhost:10100/cas/login
    client-host-url: https://localhost:8443
```

* Change SSL settings in `src/main/resources/application.yml` pointing to your local keystore and truststore. For example:
 
```yaml
  server:
    port: 8443
    ssl:
      enabled: true
      key-store: /etc/keystore/localhost.jks
      key-store-password: changeit     
```
 
## Build

```bash
gradlew clean build
```

## Deployment
- Create a keystore file `localhost` under [/etc/keystore](/etc/keystore) on Linux. 
  Use [C:/etc/keystore](/C:/etc/keystore) on Windows. 
  (If you run CAS server on gradle, `localhost` file must be under `etc/keystore`.)
- Use the password `changeit` for both the keystore and the key/certificate entries.
- Ensure the keystore is loaded up with keys and certificates of the server.
- **(These deploy step is not necessary for docker.)**

On a successful deployment via the following methods, CAS will be available at:

* `https://localhost:8443/`

> Note: you also might need to do the self-cert generation/importing dance into the JVM's trustore for this CAS client/server SSL handshake to 
  work properly. 

### `Handshake exception` Error

For handshake exception, import crt file to jdk (cacert) of CAS-server.

[Look here how import to java](http://confluence.tpe.gov.tr/display/AUTHE/Create+SSL+certificate+file) 

### `Your connection is not private` Error

If you see this error on google chrome:

> Your connection is not private.  
 Attackers might be trying to steal your information from localhost (for example, passwords, messages, or credit cards). NET::ERR_CERT_COMMON_NAME_INVALID`

* Browse this option  [chrome://flags/#allow-insecure-localhost](chrome://flags/#allow-insecure-localhost)

and enable.

## Run

```bash
gradlew bootRun
```

## Executable WAR

Run the cas-client web application as an executable WAR.

```bash
java -jar build/libs/cas-client.war
```

# Docker
### Build
You can build from docker-compose file.

<pre><code>
docker-compose build
</code></pre>
  
or 
  
<pre><code>
docker build -t tpedocker/cas-client .
</code></pre>
  
### Run with compose
<pre><code>
docker-compose up
</code></pre>

### Run with command  
<pre><code>
docker run -d -p 8443:8443 tpedocker/cas-client
</code></pre>
