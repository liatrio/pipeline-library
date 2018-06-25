#!/usr/bin/env groovy
//vars/TestSuite.groovy
import hudson.model.*
import java.io.File

/*
 * Replaces tag name in docker compose file with branch name
 *
 * @param ldopImageName   Image name to search for
 * @param branch          Branch name
 * @param directory       Directory to find docker compose file in
 * @param orgName         Organizational name for docker image
 */
 
def call(String ldopImageName, String branch, String directory, String orgName="liatrio") {
  if (!ldopImageName || !branch)
    return
 
  def file = new File("${directory}/docker-compose.yml")
  file.write(file.text.replaceAll( 
    "image: ${orgName}/${ldopImageName}:"+/\d{1,2}(?:\.\d{1,2}\.\d{1,2})?/,
    "image: ${orgName}/${ldopImageName}:${branch}"));
}
