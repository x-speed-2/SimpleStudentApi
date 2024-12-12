pipeline {
    agent any
    
    tools {
        maven 'Maven 3.8.8'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean install'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package'
            }
        }
    }

    post {
        always {
            junit '**/target/surefire-reports/*.xml'
        }
        success {
            echo 'Build and tests were successful!'
        }
        failure {
            echo 'Something went wrong. Check the logs!'
        }
    }
}
