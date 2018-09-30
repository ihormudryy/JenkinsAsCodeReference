import java.lang.System
import hudson.model.*
import org.jenkinsci.plugins.workflow.libs.*
import hudson.plugins.ec2.*
import hudson.slaves.Cloud
import com.amazonaws.services.ec2.model.InstanceType

def home_dir = System.getenv("JENKINS_HOME")
def security = new ConfigSlurper().parse(new File("$home_dir/jenkins.properties").toURI().toURL())
def properties = new ConfigSlurper().parse(new File("$home_dir/ec2.properties").toURI().toURL())

def region = properties.ec2cloud.ec2.region
def useInstanceProfileForCredentials = false
def credentialsId = security.credentials.aws.credentialsId
def privateKey = new File(security.credentials.aws.path).text.trim()
def instanceCapStr = properties.ec2cloud.ec2.instanceCapStr

List<SlaveTemplate> amis = new ArrayList<SlaveTemplate>();

properties.ec2cloud.AMIs.each() { key, amiConfig ->
    List<EC2Tag> m_tags = new ArrayList<EC2Tag>()
    //amiConfig.tags.each() { tag ->
    //    echo "$tag"
        //m_tags.add(new EC2Tag(tag.type, tag.value))
    //}
    SlaveTemplate ami = new SlaveTemplate(
        amiConfig.ami,
        amiConfig.zone,
        new SpotConfiguration(amiConfig.spotConfig),
        amiConfig.securityGroups,
        amiConfig.remoteFS,
        InstanceType.T2Micro,
        amiConfig.ebsOptimized,
        amiConfig.labelString.toString(),
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
        m_tags,
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
    'AmazonEC2', 
    useInstanceProfileForCredentials, 
    credentialsId, 
    region, 
    privateKey.toString(), 
    instanceCapStr, 
    amis
)

Jenkins.instance.clouds.add(orig)