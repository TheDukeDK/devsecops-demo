FROM jenkins/jenkins:2.249.3

ENV JENKINS_ADMIN_USER=$JENKINS_ADMIN_USER \
    JENKINS_ADMIN_PASSWORD=$JENKINS_ADMIN_USER

# if we want to install via apt we need to be root
USER root

# Add dotnet core
RUN wget -q -O - https://packages.microsoft.com/keys/microsoft.asc | gpg --dearmor > microsoft.asc.gpg \
    && mv microsoft.asc.gpg /etc/apt/trusted.gpg.d/ \
    && wget -q https://packages.microsoft.com/config/debian/9/prod.list \
    && mv prod.list /etc/apt/sources.list.d/microsoft-prod.list \
    && chown root:root /etc/apt/trusted.gpg.d/microsoft.asc.gpg \
    && chown root:root /etc/apt/sources.list.d/microsoft-prod.list

# Install needed tools
RUN apt-get -qq update && apt-get install -y nano gradle maven ant nuget apt-transport-https dotnet-sdk-3.1 dotnet-sdk-5.0
RUN curl -sL https://deb.nodesource.com/setup_15.x | bash -
RUN apt-get install -y nodejs

RUN dotnet tool install --global dotnet-sonarscanner --version 5.0.4
RUN cp -r /root/.dotnet/tools /usr/local/bin
RUN chmod -R 777 /usr/local/bin/tools

# Skip setup wizard for Jenkins
ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"

# Bootstrap the Jenkins plugins
COPY config/plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt

# Bootstrap with Jenkins Configuration as Code (casC) plugin
COPY config/jenkins.yml jenkins.yml
RUN chown jenkins:jenkins /jenkins.yml \
    && chmod 750 /jenkins.yml

# Drop back to the regular jenkins user - good practice
USER jenkins
ENV PATH="${PATH}:/usr/local/bin/tools"