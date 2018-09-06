crontab <<EOF
0 0 * * * "wget -O /usr/share/jenkins/jenkins.war http://updates.jenkins-ci.org/latest/jenkins.war && service jenkins start"
EOF
