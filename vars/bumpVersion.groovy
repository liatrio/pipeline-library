#!/bin/env groovy

def call (String input_version, String type = "patch") {

    def version = input_version.tokenize(".")

    def snapshot = version[2].contains("-SNAPSHOT")

    def major
    def minor
    def patch

    //major update
    if (type == "major") {
    	major = version[0].toInteger() + 1
    	minor = "0"
    	patch = "0"
    }
    //minor update
    else if (type == "minor") {
    	major = version[0]
    	minor = version[1].toInteger() + 1
    	patch	= "0"
    }
    //patch snapshot update
    else if (type == "patch" && snapshot) {
    	major = version[0]
    	minor = version[1]
    	patch	= version[2].split('-')[0].toInteger() + 1
    }
    //patch release update
    else if (type == "patch") {
    	major = version[0]
    	minor = version[1]
    	patch	= version[2].toInteger() + 1
    }

    def newver = major + "." + minor + "." + patch

    if (snapshot)
    	newver += "-SNAPSHOT"
    return newver
}
