#!/usr/bin/env groovy

def call(imageName) {
    sh "docker inspect ${imageName} > containerMetaData.json"
    def metaData = readJSON file: "containerMetaData.json"
    return metaData[0].ContainerConfig.Labels.version.split(" ")[1]
}
