# Pipeline Library
Shared library with unit tests to be used with Jenkins pipelines. 
[![Build Status](https://travis-ci.org/liatrio/pipeline-library.svg?branch=master)](https://travis-ci.org/liatrio/pipeline-library)



#### Run Pipeline Library Tests Locally For Development Purposes
```
    git clone https://github.com/liatrio/pipeline-library.git
    gradle clean test
```
### Testing
Uses com.lesfurets.jenkins.unit.RegressionTest to test the shared libraries with spock.



#### Shared Library Methods
* [Analyze With Sonar](vars/analyzeWithSonar.groovy)
* [Deploy container to Environment](vars/deployContainerToEnv.groovy)
* [Get Jira Issue](vars/getJiraIssue.groovy)
* [Gradle Build](vars/gradleBuild.groovy)
* [Gradle Build And Deploy](vars/gradleBuildAndDeploy.groovy)
* [Maven Build](vars/mavenBuild.groovy)
* [Maven Build and Deploy](vars/mavenBuildAndDeploy.groovy)
* [Push Container to Artifactory](vars/pushContainerToArtifactory.groovy)
* [Run App Locally](vars/runAppLocally.groovy)
