#!/usr/bin/env groovy

def call(jobTitle, jobUrl, jobChanges, exitStatus) {
  def changes = ""
  def output = "*Job*\n• ${jobTitle}\n• ${jobUrl}\n*Status*\n• ${exitStatus}\n*Changes*\n"

  for (entry in jobChanges) {
      for (item in entry) {
          changes = changes + "• ${item.msg} [${item.author}]\n"
      }
  }

  changes = (changes == "") ? "• no changes" : changes

  return output + changes
}
