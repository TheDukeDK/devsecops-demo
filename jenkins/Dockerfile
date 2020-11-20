FROM jenkins/jenkins:2.249.3

ENV JENKINS_ADMIN_USER=$JENKINS_ADMIN_USER \
    JENKINS_ADMIN_PASSWORD=$JENKINS_ADMIN_USER \
    CASC_JENKINS_CONFIG=$CASC_JENKINS_CONFIG \
    AD_READER_CREDS=$AD_READER_CREDS

# if we want to install via apt
USER root
RUN apt-get update && apt-get install -y nano gradle maven ant

# Skip setup wizard
ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"

# Bootstrap the plugins
COPY config/plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt

# Bootstrap with Jenkins Configuration as Code (casC) plugin
COPY config/jenkins.yml jenkins.yml
RUN chown jenkins:jenkins /jenkins.yml \
    && chmod 750 /jenkins.yml

# drop back to the regular jenkins user - good practice
USER jenkins