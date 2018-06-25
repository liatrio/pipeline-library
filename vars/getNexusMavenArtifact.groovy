import org.ldop.NexusMavenArtifactHelper.groovy

/*
 * Downloads an artifact from nexus and writes it to targetPath.
 * Creates target directories if they don't exist
 * Errors if the target file already exists
 *
 * @param groupId       Artifact group ID
 * @param artifactId    Artifact ID
 * @param version       Artifact version
 * @param targetPath    Local path for the artifact
 */

def call(groupId, artifactId, version, targetPath) {
    NexusMavenArtifactHelper.getArtifact(groupId, artifactId, version, targetPath)
}
