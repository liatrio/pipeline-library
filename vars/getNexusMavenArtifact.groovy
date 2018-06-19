import org.ldop.NexusMavenArtifactHelper.groovy

// Downloads an artifact from nexus and writes it to targetPath.
// Creates target directories if they don't exist
// Errors if the target file already exists
def call(groupId, artifactId, version, targetPath) {
    NexusMavenArtifactHelper.getArtifact(groupId, artifactId, version, targetPath)
}
