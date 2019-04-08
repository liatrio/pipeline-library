def call(config) {

  def pomExists     = fileExists "pom.xml"
  def packageExists = fileExists "package.json"
  def repo          = 'docker.artifactory.liatr.io'
  def image         = ""
  def tag           = ""

  if (pomExists){
  }
  else if (packageExists){
    pack    = readJSON file: "package.json"
    image   = "${repo}/${pack.name}"
    if ("${env.BRANCH_NAME}" == "master")  
      tag = pack.version 
    else 
      tag = "${env.BRANCH_NAME}" 
  }


  withCredentials([usernamePassword(credentialsId: 'liatrio-artifactory', passwordVariable: 'passwordVariable', usernameVariable: 'usernameVariable')]) {
    sh """
      docker build -t ${image}:${tag} .
      docker login -u ${usernameVariable} -p ${passwordVariable} ${repo}
      docker push ${image}:${tag}
    """
  }

}

