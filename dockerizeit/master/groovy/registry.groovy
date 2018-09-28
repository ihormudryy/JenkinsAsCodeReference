import java.lang.System
import hudson.model.*
import org.jenkinsci.plugins.workflow.libs.*
import org.jenkinsci.plugins.docker.commons.credentials.DockerRegistryEndpoint
import org.jenkinsci.plugins.pipeline.modeldefinition.config.GlobalConfig

def home_dir = System.getenv("JENKINS_HOME")
def properties = new ConfigSlurper().parse(new File("$home_dir/jenkins.properties").toURI().toURL())
GlobalConfig gc = GlobalConfig.get()
docker_registry = new DockerRegistryEndpoint(properties.registry.registryURL, properties.registry.registryCredentials)
gc.setRegistry(properties.registry.label)
gc.setDockerLabel(properties.registry.label)