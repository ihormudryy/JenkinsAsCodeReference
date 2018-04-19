import java.lang.System
import hudson.model.*
import jenkins.model.*
import javaposse.jobdsl.plugin.*
import hudson.triggers.TimerTrigger
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition

def instance = Jenkins.getInstance()
def home_dir = System.getenv("JENKINS_HOME")
GroovyShell shell = new GroovyShell()
def helpers = shell.parse(new File("$home_dir/init.groovy.d/helpers.groovy"))
def properties = new ConfigSlurper().parse(new File("$home_dir/jenkins.properties").toURI().toURL())

properties.seedjobs.each {
  println "--> Remove ${it.value.name} seed job if already exists"
  def job = Jenkins.instance.getJob(it.value.name)
  if (job) { job.delete() }
  println "--> Create ${it.value.name} seed jod"
  def project = Jenkins.instance.createProject(WorkflowJob.class, it.value.name)

  project.setDefinition(new CpsFlowDefinition("""
import javaposse.jobdsl.plugin.*

node("${properties.global.variables.utility_slave}") {
    git branch: "${it.value.branch}", credentialsId: "${it.value.credentials}", url: "${it.value.repo}"
}"""))
  it.value.parameters.each { key, value ->
    helpers.addBuildParameter(project, key, value)
  }
  project.save()
}

Jenkins.instance.reload()