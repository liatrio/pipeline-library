def call(currentBuild, String unit = "MILLISECONDS") {

    def divisor = ["HOURS": 360000, "MINUTES": 60000, "SECONDS": 1000 , "MILLISECONDS": 1]
    long completedTimeStamp = currentBuild.getTimeInMillis()
    long prevTimeStamp = getTimeOfFailedBuild(currentBuild)
    recoveryTime = completedTimeStamp - prevTimeStamp
//    if( recoveryTime  )
//        logStashObject(recoveryTime: recoveryTime  )
    return recoveryTime / divisor[unit]
}

@NonCPS
long getTimeOfFailedBuild(currentBuild) {
    def build = currentBuild.getPreviousBuild() //start looking at previous build
    while(build.getNumber() > 1 && build.getResult() != 'FAILURE') {
        build = build.getPreviousBuild()
    }
    if( build.getNumber() == 1 && build.getResult() == 'SUCCESS')
        return 0
    println "Last failed build was ${build.getNumber()}"
    return build.getTimeInMillis()
}