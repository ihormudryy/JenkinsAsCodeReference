import java.lang.System
import hudson.model.*
import org.jenkinsci.plugins.workflow.libs.*
import org.jenkinsci.plugins.docker.commons.credentials.DockerRegistryEndpoint
import org.jenkinsci.plugins.pipeline.modeldefinition.config.GlobalConfig

GlobalConfig gc = GlobalConfig.get()
docker_registry = new DockerRegistryEndpoint(properties.registry.registryURL, properties.registry.registryCredentials)
gc.setRegistry(properties.registry.label)
gc.setDockerLabel(properties.registry.label)