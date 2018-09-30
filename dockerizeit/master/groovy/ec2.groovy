import java.lang.System
import hudson.model.*
import org.jenkinsci.plugins.workflow.libs.*
import hudson.plugins.ec2.*
import hudson.slaves.Cloud
import com.amazonaws.services.ec2.model.InstanceType
import jenkins.model.Jenkins

def home_dir = System.getenv("JENKINS_HOME")
def security = new ConfigSlurper().parse(new File("$home_dir/jenkins.properties").toURI().toURL())
def properties = new ConfigSlurper().parse(new File("$home_dir/ec2.properties").toURI().toURL())

def name = properties.ec2cloud.ec2.name
def region = properties.ec2cloud.ec2.region
def useInstanceProfileForCredentials = false
def credentialsId = security.credentials.aws.credentialsId
def privateKey = new File(security.credentials.aws.path).text.trim()
def instanceCapStr = properties.ec2cloud.ec2.instanceCapStr

List<SlaveTemplate> amis = new ArrayList<SlaveTemplate>();

properties.ec2cloud.AMIs.each() { key, amiConfig ->
    List<EC2Tag> tags = new ArrayList<EC2Tag>()
    amiConfig.tags.each {
        tags.add(new EC2Tag(it.value.type, it.value.value))
    }
    SlaveTemplate ami = new SlaveTemplate(
        amiConfig.ami,
        amiConfig.zone,
        new SpotConfiguration(amiConfig.spotConfig),
        amiConfig.securityGroups,
        amiConfig.remoteFS,
        InstanceType["${amiConfig.type}"],
        amiConfig.ebsOptimized,
        amiConfig.labels,
        Node.Mode.NORMAL,
        amiConfig.description,
        amiConfig.initScript,
        amiConfig.tmpDir,
        amiConfig.userData.toString(),
        amiConfig.numExecutors,
        amiConfig.remoteAdmin,
        amiConfig.amiType,
        amiConfig.jvmopts,
        amiConfig.stopOnTerminate,
        amiConfig.subnetId,
        tags,
        amiConfig.idleTerminationMinutes,
        amiConfig.usePrivateDnsName,
        amiConfig.instanceCapStr,
        amiConfig.iamInstanceProfile,
        amiConfig.deleteRootOnTermination,
        amiConfig.useEphemeralDevices,
        amiConfig.useDedicatedTenancy,
        amiConfig.launchTimeoutStr.toString(),
        amiConfig.associatePublicIp,
        amiConfig.customDeviceMapping,
        amiConfig.connectBySSHProcess,
        amiConfig.connectUsingPublicIp
    )
    amis.add(ami);
}

AmazonEC2Cloud orig = new AmazonEC2Cloud(
    name, 
    useInstanceProfileForCredentials, 
    credentialsId, 
    region, 
    privateKey.toString(), 
    instanceCapStr, 
    amis
)

if (Jenkins.instance.clouds.size() == 0) { 
    Jenkins.instance.clouds.add(orig)
} else {
    def add_new_ec2 = true
    for (i = 0; i < Jenkins.instance.clouds.size(); i++){
        if (Jenkins.instance.clouds[i] instanceof hudson.plugins.ec2.AmazonEC2Cloud && 
            Jenkins.instance.clouds[i].getCloudName() == orig.getCloudName())
            add_new_ec2 = false
    }
    if (add_new_ec2)
        Jenkins.instance.clouds.add(orig)
}