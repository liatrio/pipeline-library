def call(env, config) {
  if (config.stages.deploy.platform.toLowerCase() == "bluemix"){
    deployToBluemix(env, config)
  }
  else if (config.stages.deploy.platform.toLowerCase() == "aem"){
    deployToAEM(config)
  }
  else if (config.stages.deploy.platform.toLowerCase() == "aks"){
    deployToAks(env, config)
  }
}
