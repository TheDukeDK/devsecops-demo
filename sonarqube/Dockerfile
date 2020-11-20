FROM sonarqube:8.4.2-community

ENV VERSION=8.4.2 \
    SONAR_USER_NAME=sonarqube \
    SONAR_USER_GROUP=sonarqube \
    SONARQUBE_DB_USER=${SONARQUBE_DB_USER} \
    SONARQUBE_DB_PASSWORD=${SONARQUBE_DB_PASSWORD} \
    PLUGIN_DOWNLOAD_DIR=/opt/sonarqube/extensions/downloads

# Download and copy all of our wanted plugins
#RUN wget -q --directory-prefix=${PLUGIN_DOWNLOAD_DIR} https://binaries.sonarsource.com/Distribution/sonar-scm-git-plugin/sonar-scm-git-plugin-1.4.0.1037.jar \
#    && wget -q --directory-prefix=${PLUGIN_DOWNLOAD_DIR} https://binaries.sonarsource.com/Distribution/sonar-flex-plugin/sonar-flex-plugin-2.4.0.1222.jar \
#    && wget -q --directory-prefix=${PLUGIN_DOWNLOAD_DIR} https://binaries.sonarsource.com/Distribution/sonar-java-plugin/sonar-java-plugin-5.2.0.13398.jar \
#    && wget -q --directory-prefix=${PLUGIN_DOWNLOAD_DIR} https://binaries.sonarsource.com/Distribution/sonar-javascript-plugin/sonar-javascript-plugin-4.1.0.6085.jar \
#    && wget -q --directory-prefix=${PLUGIN_DOWNLOAD_DIR} https://binaries.sonarsource.com/Distribution/sonar-php-plugin/sonar-php-plugin-2.13.0.3107.jar \
#    && wget -q --directory-prefix=${PLUGIN_DOWNLOAD_DIR} https://binaries.sonarsource.com/Distribution/sonar-python-plugin/sonar-python-plugin-1.9.1.2080.jar \
#    && wget -q --directory-prefix=${PLUGIN_DOWNLOAD_DIR} https://binaries.sonarsource.com/Distribution/sonar-typescript-plugin/sonar-typescript-plugin-1.6.0.2388.jar \
#    && wget -q --directory-prefix=${PLUGIN_DOWNLOAD_DIR} https://binaries.sonarsource.com/Distribution/sonar-xml-plugin/sonar-xml-plugin-1.5.0.1373.jar \
#    && wget -q --directory-prefix=${PLUGIN_DOWNLOAD_DIR} https://binaries.sonarsource.com/Distribution/sonar-csharp-plugin/sonar-csharp-plugin-7.0.1.4822.jar \
#    && wget -q --directory-prefix=${PLUGIN_DOWNLOAD_DIR} https://binaries.sonarsource.com/Distribution/sonar-ldap-plugin/sonar-ldap-plugin-2.2.0.608.jar
#
#RUN chown -R ${SONAR_USER_NAME}:${SONAR_USER_GROUP} ${PLUGIN_DOWNLOAD_DIR} \
#    && chmod -R 750 ${PLUGIN_DOWNLOAD_DIR}

# Copy our configuration to the image
COPY config/sonar.properties /opt/sonarqube/conf
COPY docker-entrypoint.d/entrypoint-sonar.sh /entrypoint-sonar.sh
RUN chmod 750 /entrypoint-sonar.sh

ENTRYPOINT ["/entrypoint-sonar.sh"]