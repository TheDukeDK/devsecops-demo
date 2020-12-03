#!/bin/sh

GITLAB_ROOT_PASSWORD=${GITLAB_ROOT_PASSWORD:=password}
JENKINS_ADMIN_USER=${JENKINS_ADMIN_USER:=jenkins}
JENKINS_ADMIN_PASSWORD=${JENKINS_ADMIN_PASSWORD:=password}

set -e

# Get needed ENV variables
source ./sourceme

# Bring up the containers
docker-compose up -d

# SonarQube ready?
echo "Waiting for SonarQube to come up..."
while [[ "`curl -s -f -u admin:admin http://sonarqube.local.net/api/system/status | sed -n 's|.*"status":"\([^"]*\)".*|\1|p'`" != "UP" ]]; do
    sleep 2
done;

# Create Jenkins user
echo "Creating Jenkins user and webhook..."
curl -s -u admin:admin -X POST http://sonarqube.local.net/api/users/create\?login\=$JENKINS_ADMIN_USER\&local\=true\&name\=$JENKINS_ADMIN_USER\&password\=$JENKINS_ADMIN_PASSWORD > /dev/null 2>&1
curl -s -u admin:admin -X POST http://sonarqube.local.net/api/user_groups/add_user\?login\=$JENKINS_ADMIN_USER\&name\=sonar-administrators > /dev/null 2>&1
curl -s -u admin:admin -X POST http://sonarqube.local.net/api/webhooks/create\?name\=jenkins\&url\="http://jenkins.local.net/sonarqube-webhook/" > /dev/null 2>&1

# Create a token for the jenkins user
TOKEN=`curl -s -u admin:admin -X POST http://sonarqube.local.net/api/user_tokens/generate\?login\=$JENKINS_ADMIN_USER\&name\=jenkins | sed -n 's|.*"token":"\([^"]*\)".*|\1|p'`
echo "Use this token value in Jenkins credential 'token-sonarqube': $TOKEN"

# Must wait for gitlab to become healthy
echo "Waiting for gitlab to be healthy. Be patient, may take a few minutes."
while [[ "`docker inspect -f {{.State.Health.Status}} gitlab`" != "healthy" ]]; do
    sleep 2
done;

# When healthy push this repository.
echo "Pushing DevSecOps demo to gitlab."
git push --set-upstream http://root:$GITLAB_ROOT_PASSWORD@gitlab.local.net/root/devsecops-demo.git master
