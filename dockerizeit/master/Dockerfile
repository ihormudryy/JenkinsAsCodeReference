FROM jenkins

USER root

#Add support for proxies.
# Values should be passed as build args
# http://docs.docker.com/engine/reference/builder/#arg
ENV http_proxy ${http_proxy:-}
ENV https_proxy ${https_proxy:-}
ENV no_proxy ${no_proxy:-}
ARG JAVA_PROXY
ENV JAVA_PROXY ${JAVA_PROXY:-}
ENV JAVA_OPTS "-Djenkins.install.runSetupWizard=false ${JAVA_PROXY:-}"
ENV JENKINS_OPTS "--httpsCertificate=/var/jenkins_home/.cert/cert.crt --httpsPrivateKey=/var/jenkins_home/.cert/cci-jenkins.key --webroot=/var/cache/$NAME/war -DsessionTimeout=1 --httpPort=-1 --httpsPort=443"

# Define build argument and env variable master_image_version
# Use to pass info about Jenkins version so it can be represented
# for the users.
ARG master_image_version="local build"
ENV master_image_version $master_image_version

RUN chmod 744 /usr/share/jenkins/jenkins.war
RUN wget -O  /usr/share/jenkins/jenkins.war http://updates.jenkins-ci.org/latest/jenkins.war

# Install plugins
COPY plugins.txt /usr/share/jenkins/plugins.txt
RUN /usr/local/bin/plugins.sh /usr/share/jenkins/plugins.txt

# Copy configuration scripts that will be executed by groovy
COPY *.groovy /usr/share/jenkins/ref/init.groovy.d/
COPY *.properties ${JENKINS_HOME}/

# Copy admin scripts
COPY scripts/*.groovy ${JENKINS_HOME}/ 
