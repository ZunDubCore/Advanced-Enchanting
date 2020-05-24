#!/usr/bin/env groovy

pipeline {

    agent any

    parameters {
        booleanParam(name: 'DEPLOY', defaultValue: false, description: '')
    }

    stages {

        stage('Clean') {

            steps {
                withCredentials([file(credentialsId: 'advanced_enchanting_secrets', variable: 'ORG_GRADLE_PROJECT_secretFile')]) {
                    echo 'Cleaning project workspace.'
                    sh 'chmod +x gradlew'
                    sh './gradlew --stacktrace clean'
                }
            }
        }

        stage('Build') {

            steps {
                withCredentials([file(credentialsId: 'advanced_enchanting_secrets', variable: 'ORG_GRADLE_PROJECT_secretFile')]) {
                    echo 'Building project.'
                    sh './gradlew --stacktrace build'
                }
            }
        }

        stage('Deploy') {
            when {
                expression {
                    return params.DEPLOY ==~ /(?i)(Y|YES|T|TRUE|ON|RUN)/
                }
            }

            steps {
                withCredentials([file(credentialsId: 'advanced_enchanting_secrets', variable: 'ORG_GRADLE_PROJECT_secretFile')]) {
                    echo 'Deploying project.'
                    sh './gradlew --stacktrace curseforge'
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
