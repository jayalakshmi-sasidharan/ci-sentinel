pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk 'JDK21'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out source code...'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Building the application...'
                dir('backend') {
                    sh 'mvn clean compile'
                }
            }
        }

        stage('Test') {
            steps {
                echo 'Running tests...'
                dir('backend') {
                    sh 'mvn test'
                }
            }
        }

        post {
            always {
                junit 'backend/target/surefire-reports/*.xml'
            }
        }

        stage('Package') {
            steps {
                echo 'Packaging the application...'
                dir('backend') {
                    sh 'mvn package -DskipTests'
                }
            }
        }
    }
    post {
        success {
            echo 'Pipeline completed.'
        }
        failure {
            echo 'Pipeline failed.'
        }
    }
}