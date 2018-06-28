@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7' )

import org.apache.commons.io.IOUtils
import org.apache.commons.io.FileUtils
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.GET
import static groovyx.net.http.ContentType.BINARY


class NexusMavenArtifactHelper {

    public NexusMavenArtifactHelper() { /* empty */ }

    static def getArtifact(groupId, artifactId, version, targetPath, String nexus="http://nexus:8081") {

        if (version.contains('SNAPSHOT')) {
            url = "${nexus}/nexus/service/local/artifact/maven/content?r=snapshots&g=${groupId}&a=${artifactId}&v=${version}&p=war"
        } else {
            url = "${nexus}/nexus/service/local/artifact/maven/content?r=releases&g=${groupId}&a=${artifactId}&v=${version}&p=war"
        }

        env = System.getenv()
        artifactEndpoint = new HTTPBuilder(url)
        artifactEndpoint.auth.basic(env['INITIAL_ADMIN_USER'], env['INITIAL_ADMIN_PASSWORD'])
        artifactEndpoint.headers.'Accept' = '*/*'

        artifactEndpoint.request(GET, BINARY) { req ->

            response.success = { resp, reader ->
                target = new File(targetPath)
                targetDir = new File(target.getParent())
                targetDir.mkdirs() // Create dirs if they don't exist
                FileUtils.copyInputStreamToFile(reader, target);
                println 'success'
            }

            response.failure = { resp ->
                println('Error performing request:')
                println("   ${req.getRequestLine()}")
                println('Responded with:')
                println("   ${resp.getStatusLine()}")
            }
        }
    }

}
