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

# Add dotnet core
RUN wget -O - https://packages.microsoft.com/keys/microsoft.asc | gpg --dearmor > microsoft.asc.gpg \
    && mv microsoft.asc.gpg /etc/apt/trusted.gpg.d/ \
    && wget https://packages.microsoft.com/config/debian/9/prod.list \
    && mv prod.list /etc/apt/sources.list.d/microsoft-prod.list \
    && chown root:root /etc/apt/trusted.gpg.d/microsoft.asc.gpg \
    && chown root:root /etc/apt/sources.list.d/microsoft-prod.list

RUN apt-get update -qq \
  && apt-get install -y apt-transport-https \
  && apt-get install -y dotnet-sdk-3.1 dotnet-sdk-5.0 nodejs nuget

RUN dotnet tool install --global dotnet-sonarscanner --version 5.0.4

ENV PATH="${PATH}:/var/jenkins_home/.dotnet/tools"

#RUN apt-get update -qq && apt-get install npm

# drop back to the regular jenkins user - good practice
USER jenkins