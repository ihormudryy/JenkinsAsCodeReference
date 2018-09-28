FROM jenkins
USER root

ENV http_proxy ${http_proxy:-}
ENV https_proxy ${https_proxy:-}
ENV no_proxy ${no_proxy:-}
ARG JAVA_PROXY
ENV JAVA_PROXY ${JAVA_PROXY:-}
ENV JAVA_OPTS "-Djenkins.install.runSetupWizard=false ${JAVA_PROXY:-}"
#ENV JENKINS_OPTS "--httpsCertificate=/var/jenkins_home/.certs/certificate.crt --httpsPrivateKey=/var/jenkins_home/.certs/cci-jenkins.key --webroot=/var/cache/$NAME/war -DsessionTimeout=1 --httpPort=-1 --httpsPort=443"

ARG master_image_version="local build"
ENV master_image_version $master_image_version

RUN chmod 744 /usr/share/jenkins/jenkins.war
RUN wget -O /usr/share/jenkins/jenkins.war http://updates.jenkins-ci.org/latest/jenkins.war

RUN apt-get update && apt-get -qy upgrade && \
apt-get -qy install groovy cron wget

# Install plugins
COPY plugins.txt /usr/share/jenkins/plugins.txt
RUN /usr/local/bin/plugins.sh /usr/share/jenkins/plugins.txt

# Copy configuration scripts that will be executed by groovy
COPY groovy/*.groovy /usr/share/jenkins/ref/init.groovy.d/
COPY properties/*.properties ${JENKINS_HOME}/

# Copy admin scripts
COPY scripts/*.groovy ${JENKINS_HOME}/