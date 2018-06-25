#!/usr/bin/env groovy

/*
 * Adds the product version to the JSON manifest
 *
 * @param product   Product name
 * @param version   Product version
 */
def call(product, version) {

    if (!product || !version)
        return
    sh 'git reset --hard'
    sh "git checkout master && git pull"
    def parsedText = readJSON file: "manifest.json"
    parsedText.put(product, version)
    writeJSON file: 'manifest.json', json: parsedText

    withCredentials([usernamePassword(credentialsId: 'github', passwordVariable: 'PASSWORD', usernameVariable:
            'USERNAME')]) {
        sh "git add manifest.json"
        sh 'git commit -m "Jenkins updating manifest" '
        sh "git push https://${USERNAME}:${PASSWORD}@github.com/liatrio/sample-manifest-pipeline.git"
    }
}