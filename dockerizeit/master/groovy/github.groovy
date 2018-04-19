import java.lang.System
import java.io.File
import hudson.model.*
import jenkins.model.*
import com.cloudbees.jenkins.*
import org.jenkinsci.plugins.github.webhook.*
import org.jenkinsci.plugins.github.GitHubPlugin
import org.jenkinsci.plugins.github.config.GitHubServerConfig

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

    GitHubServerConfig server = new GitHubServerConfig(serverConfig.credentialsId)
    server.setManageHooks(serverConfig.manageHooks.toBoolean())
    if (serverConfig.useCustomUrl.toBoolean()) {
      server.setCustomApiUrl(serverConfig.useCustomUrl.toBoolean())
      server.setApiUrl(serverConfig.api_url)
    }
    if (serverConfig.cacheSize > 0) {
      server.setClientCacheSize(serverConfig.cacheSize)
    }
    GitHubPlugin.configuration().getConfigs().add(server)
  }
}