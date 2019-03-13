def call(currentBuild) {
    def completedTimeStamp = currentBuild.getTimeInMillis()

    def prevTimeStamp = getTimeOfFailedBuild(currentBuild)
    println "Timestamp of first failed build was: ${prevTimeStamp}"
    return (completedTimeStamp -prevTimeStamp)/1000

}



@NonCPS
def getTimeOfFailedBuild(currentBuild) {
    def build = currentBuild //current build is fixed

    while(build.id > 1 && build.getPreviousBuild().getResult() != 'SUCCESS') {
        build = build.getPreviousBuild()
    }
    return build.getTimeInMillis()
}