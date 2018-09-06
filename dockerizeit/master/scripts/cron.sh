#!/bin/bash -ex
crontab <<EOF
0 0 * * * root "wget -O /usr/share/jenkins/jenkins.war http://updates.jenkins-ci.org/latest/jenkins.war && service jenkins restart"
EOF
