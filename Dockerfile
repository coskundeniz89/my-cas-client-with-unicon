FROM tpedocker/base


ADD /build/libs/cas-client.jar /app/cas-client.jar

EXPOSE 8443
WORKDIR /app

CMD java -jar cas-client.jar  --CAS_HOST=cas