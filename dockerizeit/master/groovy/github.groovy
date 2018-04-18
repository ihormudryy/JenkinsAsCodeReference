import java.lang.System
import java.io.File
import hudson.model.*
import jenkins.model.*
import com.cloudbees.jenkins.*
import org.jenkinsci.plugins.github.webhook.*

def home_dir = System.getenv("JENKINS_HOME")
def properties = new ConfigSlurper().parse(new File("$home_dir/jenkins.properties").toURI().toURL())

properties.github.each() { configName, serverConfig ->
  if (serverConfig.enabled) {
    println "--> Configure Github Server: ${serverConfig.hostName}"

    // Dynamically load classes to avoid dependency to Github Trigger plugin
    try {
      def GitHubPushTrigger = Class.forName("com.cloudbees.jenkins.GitHubPushTrigger")
      def WebhookManager = Class.forName("org.jenkinsci.plugins.github.webhook.WebhookManager")
      def GitHubWebHook = Class.forName("com.cloudbees.jenkins.GitHubWebHook")
    } catch (ClassNotFoundException ex) {
      println "ERROR: Can not configure Github Trigger no plugin installed"
      return
    }
/*
    def gerritServer = GitHubPushTrigger.newInstance(serverConfig.hostName)
    def config = Config.newInstance()
    config.setGerritHostName(serverConfig.hostName)
    config.setGerritSshPort(serverConfig.sshPort)
    config.setGerritFrontEndURL(serverConfig.frontendURL)
    config.setGerritProxy(serverConfig.proxy)
    config.setGerritUserName(serverConfig.userName)
    config.setGerritAuthKeyFile(new File(serverConfig.sshKeyFile))
    config.setGerritEMail(serverConfig.email)
    gerritServer.setConfig(config)

    PluginImpl.getInstance().addServer(gerritServer)
    gerritServer.start()
*/
  }
}