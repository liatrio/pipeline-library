#!/usr/bin/env groovy
//vars/TestSuite.groovy
import hudson.model.*
import java.io.File
 
def call(String ldopImageName, String branch, String directory) {
  if (!ldopImageName || !branch)
    return
 
  def file = new File("${directory}/docker-compose.yml")
  file.write(file.text.replaceAll( 
    "image: liatrio/${ldopImageName}:"+/\d{1,2}(?:\.\d{1,2}\.\d{1,2})?/,
    "image: liatrio/${ldopImageName}:${branch}"));
}
