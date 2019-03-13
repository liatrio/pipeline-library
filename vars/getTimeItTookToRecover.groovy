def call(currentBuild) {
    long completedTimeStamp = currentBuild.getTimeInMillis()

//    long prevTimeStamp = getTimeOfFailedBuild(currentBuild)
    long prevTimeStamp = 1552511508015
    echo "Timestamp of first failed build was: ${prevTimeStamp.toString()}"
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