FROM jetty:11.0.12-jdk17-alpine

COPY target/oceanops-apiid.war /var/lib/jetty/webapps/oceanops-api-idgen.war