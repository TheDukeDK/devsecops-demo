# Note that Jenkins is based on Debian 9
FROM jenkins/jenkins:2.249.3

ENV JENKINS_ADMIN_USER=$JENKINS_ADMIN_USER \
    JENKINS_ADMIN_PASSWORD=$JENKINS_ADMIN_USER

# If we want to install via apt we need to be root.
# We also want to mount the docker socket so we can run docker commands on host. We need to be root.
# Running as root is bad practice! But this is just a demo.
USER root

# We want to use bash and we want to set pipefail
SHELL ["/bin/bash", "-o", "pipefail", "-c"]

# Add dotnet core
RUN curl https://packages.microsoft.com/keys/microsoft.asc | gpg --dearmor > microsoft.asc.gpg \
    && mv microsoft.asc.gpg /etc/apt/trusted.gpg.d/ \
    && curl -O https://packages.microsoft.com/config/debian/9/prod.list \
    && mv prod.list /etc/apt/sources.list.d/microsoft-prod.list \
    && chown root:root /etc/apt/trusted.gpg.d/microsoft.asc.gpg \
    && chown root:root /etc/apt/sources.list.d/microsoft-prod.list

# Install needed dependencies and build tools
RUN apt-get -qq update && apt-get install --no-install-recommends -y nano gradle maven ant sudo \
    build-essential zlib1g-dev libncurses5-dev libgdbm-dev libnss3-dev libssl-dev \
    libreadline-dev libffi-dev apt-transport-https dotnet-sdk-3.1 gnupg lsb-release ruby mono-runtime \
    && apt-get clean \
    && rm -rf /var/lib/apt/list

# Install the sonarqube scanners
RUN dotnet tool install --global dotnet-sonarscanner --version 5.0.4
RUN cp -r /root/.dotnet/tools /usr/local/bin
RUN chmod -R 777 /usr/local/bin/tools

RUN curl --location https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/sonar-scanner-cli-4.5.0.2216-linux.zip --output sonar-scanner-cli-4.5.0.2216-linux.zip \
    && unzip sonar-scanner-cli-4.5.0.2216-linux.zip -d /usr/local/bin \
    && rm sonar-scanner-cli-4.5.0.2216-linux.zip

ENV PATH="${PATH}:/usr/local/bin/sonar-scanner-4.5.0.2216-linux/bin"

# Install Docker and Compose. Used to build docker images.
# Note that it is expected that the docker.sock is mounted into this container.
RUN apt-get update -qq && apt-get install --no-install-recommends -y \
    apt-transport-https ca-certificates gnupg-agent software-properties-common \
    && apt-get clean \
    && rm -rf /var/lib/apt/list
RUN curl -fsSL https://download.docker.com/linux/ubuntu/gpg | apt-key add -
RUN apt-key fingerprint 0EBFCD88
RUN add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/debian $(lsb_release -cs) stable"
RUN apt-get update -qq && apt-cache policy docker-ce \ 
    && apt-get update -qq && apt-get install --no-install-recommends -y docker-ce docker-ce-cli containerd.io \
    && apt-get clean \
    && rm -rf /var/lib/apt/list
RUN usermod -aG docker jenkins

RUN curl -L "https://github.com/docker/compose/releases/download/1.27.4/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
RUN chmod +x /usr/local/bin/docker-compose
RUN ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose

# Install Anchore's grype image scanner.
RUN curl -sSfL https://raw.githubusercontent.com/anchore/grype/main/install.sh | sh -s -- -b /usr/local/bin

# Install Trivy for scanning docker images
RUN curl "https://aquasecurity.github.io/trivy-repo/deb/public.key" | apt-key add -
RUN echo "deb https://aquasecurity.github.io/trivy-repo/deb $(lsb_release -sc)" main | tee -a /etc/apt/sources.list.d/trivy.list

RUN apt-get update -qq && apt-get install --no-install-recommends -y -q trivy \
    && apt-get clean \
    && rm -rf /var/lib/apt/list

# Install python and checkov for scanning Infrastructure as Code. Have to build it ourselves :-(
RUN curl -O https://www.python.org/ftp/python/3.7.3/Python-3.7.3.tar.xz \
    && tar -xf Python-3.7.3.tar.xz \
    && cd Python-3.7.3 \
    && ./configure && make -j 3 \
    && make install

RUN pip3 install --no-cache-dir --upgrade pip && pip3 install --no-cache-dir --upgrade setuptools \
    && pip install --no-cache-dir checkov

# Install TFLint and terrascan for scanning Infrastructure as Code.
RUN curl "https://raw.githubusercontent.com/terraform-linters/tflint/master/install_linux.sh" | bash

# Install terrascan for scanning Infrastructure as Code.
RUN curl --location https://github.com/accurics/terrascan/releases/download/v1.3.2/terrascan_1.3.2_Darwin_x86_64.tar.gz --output terrascan.tar.gz \
    && tar -xvf terrascan.tar.gz \
    && install terrascan /usr/local/bin

# Install Hadolint foe checking Dockerfiles
RUN curl "https://github.com/hadolint/hadolint/releases/download/v1.22.1/hadolint-Linux-x86_64" -o /usr/local/bin/hadolint \
    && chmod +x /usr/local/bin/hadolint

# Nodejs, nuget
RUN curl -sL https://deb.nodesource.com/setup_10.x | bash -
RUN apt-get install -y nodejs nuget

# Install snyk, yarn and some nuget packages for yarn and npm audit to make life easier
RUN npm install -g npm-audit-html snyk yarn yarn-audit-html audit-ci

# Install OWASP Dependency Check under Jenkins where the db can be written
RUN gem install bundle-audit && gem cleanup
RUN curl --location https://github.com/jeremylong/DependencyCheck/releases/download/v6.1.0/dependency-check-6.1.0-release.zip --output /var/jenkins_home/dependency-check-6.1.0-release.zip \
    && unzip /var/jenkins_home/dependency-check-6.1.0-release.zip -d /var/jenkins_home/ \
    && rm /var/jenkins_home/dependency-check-6.1.0-release.zip

# Skip setup wizard for Jenkins
ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"

# Bootstrap the Jenkins plugins
COPY config/plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt

# Bootstrap with Jenkins Configuration as Code (casC) plugin
COPY config/jenkins.yml jenkins.yml
RUN chown jenkins:jenkins /jenkins.yml \
    && chmod 750 /jenkins.yml

ENV PATH="${PATH}:/var/jenkins_home/dependency-check/bin:/usr/local/bin/tools"

# This is BAD of course. 
# But we are mounting the docker socket from the host. 
# This is just a demo and an easy workaround.
#USER jenkins