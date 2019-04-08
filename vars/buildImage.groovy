def call(config) {

  def name    = "";
  def version = "";

  def pomExists = fileExists "pom.xml"
  def packageExists = fileExists "package.json"

  if (pomExists){
    def pom = readMavenPom file: "pom.xml"
    name = pom.artifactId
    version = pom.version
  }
  else if (packageExists){
    def pack = readJSON file: "package.json"
    name = pack.name
    version = pack.version
  }

  sh "docker build -t ${name}:${version} ."
}




