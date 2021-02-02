# Note that Jenkins is based on Debian 9
FROM jenkins/jenkins:2.249.3

ENV JENKINS_ADMIN_USER=$JENKINS_ADMIN_USER \
    JENKINS_ADMIN_PASSWORD=$JENKINS_ADMIN_USER

# If we want to install via apt we need to be root.
# We also want to mount the docker socket so we can run docker commands on host. We need to be root.
# Running as root is bad practice! But this is just a demo.
USER root

# Add dotnet core
RUN wget -q -O - https://packages.microsoft.com/keys/microsoft.asc | gpg --dearmor > microsoft.asc.gpg \
    && mv microsoft.asc.gpg /etc/apt/trusted.gpg.d/ \
    && wget -q https://packages.microsoft.com/config/debian/9/prod.list \
    && mv prod.list /etc/apt/sources.list.d/microsoft-prod.list \
    && chown root:root /etc/apt/trusted.gpg.d/microsoft.asc.gpg \
    && chown root:root /etc/apt/sources.list.d/microsoft-prod.list

# Install needed dependencies and build tools
RUN curl -sL https://deb.nodesource.com/setup_15.x | bash -
RUN apt-get -qq update && apt-get install -y nano gradle maven ant \
    build-essential zlib1g-dev libncurses5-dev libgdbm-dev libnss3-dev libssl-dev \
    libreadline-dev libffi-dev apt-transport-https dotnet-sdk-3.1 gnupg lsb-release ruby mono-runtime

# Install the sonarqube scanners
RUN dotnet tool install --global dotnet-sonarscanner --version 5.0.4
RUN cp -r /root/.dotnet/tools /usr/local/bin
RUN chmod -R 777 /usr/local/bin/tools

RUN wget https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/sonar-scanner-cli-4.5.0.2216-linux.zip \
    && unzip sonar-scanner-cli-4.5.0.2216-linux.zip -d /usr/local/bin \
    && rm sonar-scanner-cli-4.5.0.2216-linux.zip

ENV PATH="${PATH}:/usr/local/bin/sonar-scanner-4.5.0.2216-linux/bin"


# Install Docker and Compose. Used to build docker images.
# Note that it is expected that the docker.sock is mounted into this container.
RUN apt-get update -qq && apt-get install -y apt-transport-https ca-certificates gnupg-agent software-properties-common 
RUN curl -fsSL https://download.docker.com/linux/ubuntu/gpg | apt-key add -
RUN apt-key fingerprint 0EBFCD88
RUN add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/debian $(lsb_release -cs) stable"
RUN apt update -qq && apt-cache policy docker-ce && apt-get update -qq && apt-get install -y docker-ce docker-ce-cli containerd.io
RUN curl -L "https://github.com/docker/compose/releases/download/1.27.4/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
RUN chmod +x /usr/local/bin/docker-compose
RUN ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose

# Install Trivy for scanning docker images
RUN wget -qO - https://aquasecurity.github.io/trivy-repo/deb/public.key | sudo apt-key add -
RUN echo deb https://aquasecurity.github.io/trivy-repo/deb $(lsb_release -sc) main | sudo tee -a /etc/apt/sources.list.d/trivy.list
RUN apt-get update -qq && apt-get install -y -q trivy 

# Install python and checkov for scanning Infrastructure as Code. Have to build it ourselves :-(
RUN curl -O https://www.python.org/ftp/python/3.7.3/Python-3.7.3.tar.xz \
    && tar -xf Python-3.7.3.tar.xz \
    && cd Python-3.7.3 \
    && ./configure && make -j 3 \
    && make install
RUN pip3 install --upgrade pip && pip3 install --upgrade setuptools
RUN pip install checkov

# Install OWASP Dependency Check
RUN gem install bundle-audit && gem cleanup
RUN wget https://github.com/jeremylong/DependencyCheck/releases/download/v6.1.0/dependency-check-6.1.0-release.zip \
    && unzip dependency-check-6.1.0-release.zip -d /usr/local/bin \
    && ls -ll /usr/local/bin

# Nodejs, nuget and yarn
RUN curl -sL https://deb.nodesource.com/setup_10.x | sudo -E bash -
RUN apt-get install -y nodejs nuget
RUN npm install -g npm-audit-html
RUN npm install -g yarn
RUN npm install -g yarn-audit-html
RUN npm install -g snyk

# Skip setup wizard for Jenkins
ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"

# Bootstrap the Jenkins plugins
COPY config/plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt

# Bootstrap with Jenkins Configuration as Code (casC) plugin
COPY config/jenkins.yml jenkins.yml
RUN chown jenkins:jenkins /jenkins.yml \
    && chmod 750 /jenkins.yml

ENV PATH="${PATH}:/usr/local/bin/tools"
ENV PATH="${PATH}:/usr/local/bin/dependency-check/bin"
