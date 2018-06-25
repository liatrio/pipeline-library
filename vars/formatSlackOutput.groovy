#!/usr/bin/env groovy

/*
 * Formatter for details for outputting to Slack
 *
 * @param jobTitle      Job title
 * @param jobUrl        Job URL
 * @param jobChanges    List of changes in the job
 * @param exitStatus    Job exit status
 */

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
