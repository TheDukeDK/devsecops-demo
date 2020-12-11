FROM sonarqube:8.4.2-community

ENV VERSION=8.4.2 \
    SONAR_USER_NAME=sonarqube \
    SONAR_USER_GROUP=sonarqube \
    SONARQUBE_DB_USER=${SONARQUBE_DB_USER} \
    SONARQUBE_DB_PASSWORD=${SONARQUBE_DB_PASSWORD} \
    PLUGIN_DOWNLOAD_DIR=/opt/sonarqube/extensions/downloads

# Copy our configuration to the image
COPY config/sonar.properties /opt/sonarqube/conf
COPY docker-entrypoint.d/entrypoint-sonar.sh /entrypoint-sonar.sh
RUN chmod 750 /entrypoint-sonar.sh

ENTRYPOINT ["/entrypoint-sonar.sh"]