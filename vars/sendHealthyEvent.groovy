def call(String unit = "MILLISECONDS") {


    def divisor = ["HOURS": 360000, "MINUTES": 60000, "SECONDS": 1000 , "MILLISECONDS": 1]
    long completedTimeStamp = currentBuild.getTimeInMillis()
    long prevTimeStamp = getTimeOfFailedBuild(currentBuild)
    recoveryTime = completedTimeStamp - prevTimeStamp
//    sendBuildEvent(recoveryTime: recoveryTime  )
    sendBuildEvent(eventType:'state-change', state: 'healthy', priorDuration: recoveryTime  )
    return (completedTimeStamp - prevTimeStamp) / divisor[unit]
}



@NonCPS
long getTimeOfFailedBuild(currentBuild) {
    def build = currentBuild //current build is fixed

    while(build.getNumber() > 1 && build.getPreviousBuild().getResult() != 'SUCCESS') {
        build = build.getPreviousBuild()
    }
    println "build that failed first ${build.getNumber()}"
    return build.getTimeInMillis()
}