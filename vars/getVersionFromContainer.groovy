#!/usr/bin/env groovy

/*
 * Gets the version from a specific docker image
 *
 * @param imageName     Image to get the version of
 */

def call(imageName) {
    sh "docker inspect ${imageName} > containerMetaData.json"
    def metaData = readJSON file: "containerMetaData.json"
    return metaData[0].ContainerConfig.Labels.version.split(" ")[1]
}
