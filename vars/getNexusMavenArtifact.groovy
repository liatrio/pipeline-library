import org.ldop.NexusMavenArtifactHelper.groovy

def call(groupId, artifactId, version, targetPath) {
    NexusMavenArtifactHelper.getArtifact(groupId, artifactId, version, targetPath)
}
