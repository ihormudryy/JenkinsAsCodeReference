import java.lang.System
import hudson.model.*
import org.jenkinsci.plugins.workflow.libs.*

def home_dir = System.getenv("JENKINS_HOME")
def properties = new ConfigSlurper().parse(new File("$home_dir/jenkins.properties").toURI().toURL())

GlobalLibraries gl = GlobalLibraries.get()
List<LibraryConfiguration> configs = new ArrayList<LibraryConfiguration>()

properties.registry.each() { key, value ->
	println '--> Configure Docker Registry plugin'
    def docker_registry = Jenkins.instance.getDescriptorByType("org.jenkinsci.plugins.docker.commons.credentials.DockerRegistryEndpoint")
    docker_registry.credentialsId = properties.registryCredentials
    docker_registry.label = properties.label
    docker_registry.url = properties.registryURL
    docker_registry.save()
}
