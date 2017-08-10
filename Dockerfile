FROM tpedocker/base


ADD /build/libs/cas-client.jar /app/cas-client.jar

EXPOSE 12000
WORKDIR /app

CMD java -jar cas-client.jar  --cas_server=cas