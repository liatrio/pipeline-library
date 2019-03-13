def call(currentBuild) {
    long completedTimeStamp = currentBuild.getTimeInMillis()

    long prevTimeStamp = getTimeOfFailedBuild(currentBuild)
    echo "Timestamp of first failed build was: ${prevTimeStamp}"
    return (completedTimeStamp - prevTimeStamp)/1000

}



@NonCPS
long getTimeOfFailedBuild(currentBuild) {
    def build = currentBuild //current build is fixed

    while(build.id > 1 && build.getPreviousBuild().getResult() != 'SUCCESS') {
        build = build.getPreviousBuild()
    }
    println "build that failed timestamp ${build.getTimeInMillis()}"
    return build.getTimeInMillis()
}