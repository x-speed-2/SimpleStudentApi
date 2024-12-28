pipeline {
    agent any

    tools {
        maven 'Maven'
    }

    environment {
        DOCKER_CREDENTIALS_ID = 'docker-hub-cnx' 
        DOCKER_IMAGE = 'r2fzqiky11/student-api'
        DOCKER_TAG = 'latest'
        REMOTE_SERVER_AUTH = 'remote-username-ip'
        REMOTE_SERVER_ID = 'remote-server-ssh'
        SONARQUBE_CREDENTIALS_ID = 'sonarqube-credentials'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
     

          stage('Deploy to Remote Server') {
            steps {
                withCredentials([string(credentialsId: REMOTE_SERVER_AUTH, variable: 'REMOTE_SERVER_USER_IP')]) {
                    sshagent([REMOTE_SERVER_ID]) {
                        script {
                            sh """
                            ssh -o StrictHostKeyChecking=no ${REMOTE_SERVER_USER_IP} << EOF
                                sudo docker pull ${DOCKER_IMAGE}:${DOCKER_TAG}
                                sudo docker run -d -p 8883:8080 ${DOCKER_IMAGE}:${DOCKER_TAG}
                            EOF
                            """
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            junit '**/target/surefire-reports/*.xml'
        }
        success {
            echo 'Pipeline completed'
        }
        failure {
            echo 'Pipeline failed'
        }
    }
}
