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
        import hudson.plugins.git.*;
        import com.cloudbees.jenkins.GitHubPushTrigger
        import com.coravy.hudson.plugins.github.GithubProjectProperty
        import com.google.common.collect.Collections2
        import com.google.common.collect.Lists
        import hudson.plugins.git.extensions.GitSCMExtension

        def projects = [
            'webgl',
            'structures',
            'JenkinsAsCodeReference',
            'dsl_shared_libs'
        ]

        def jenkins = Jenkins.instance

        projects.each { project->
            def gitTrigger = new GitHubPushTrigger()
            def url = \"git@github.com:"${properties.global.git.user}"/${project}\"
            def repo_list = GitSCM.createRepoList(url, '"${it.value.credentials}"')
            def projectProperty = new GithubProjectProperty(url)
            def scm = new GitSCM(repo_list, 
                                  [new BranchSpec(\"*/"${it.value.branch}")],
                                  false,
                                  Collections.<SubmoduleConfig>emptyList(),
                                  null, null,
                                  Collections.<GitSCMExtension>emptyList()
                                )

            def flowDefinition = new org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition(scm, 
                                                                                \"Jenkinsfile\")

            def job = new org.jenkinsci.plugins.workflow.job.WorkflowJob(jenkins, \"GitHub_${project}\")
            job.setDefinition(flowDefinition)
            job.setTriggers([gitTrigger])
            job.addProperty(projectProperty)
            jenkins.reload()
      }
    """
    ))
    it.value.parameters.each { key, value ->
      helpers.addBuildParameter(project, key, value)
    }
    project.save()
}

Jenkins.instance.reload()