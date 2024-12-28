pipeline {
    agent any

    tools {
        maven 'Maven'
    }

    environment {
        DOCKER_CREDENTIALS_ID = 'docker-hub-cnx' 
        DOCKER_IMAGE = 'r2fzqiky11/student-api'
        DOCKER_TAG = 'latest'
        REMOTE_SERVER = 'ubuntu@152.70.168.196'
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
                sshagent(['remote-server-ssh']) {
                    sh """
                    ssh -o StrictHostKeyChecking=no $REMOTE_SERVER << EOF
                        sudo docker stop \$(sudo docker ps -q --filter ancestor=${DOCKER_IMAGE}:${DOCKER_TAG}) || true
                        sudo docker rm \$(sudo docker ps -q --filter ancestor=${DOCKER_IMAGE}:${DOCKER_TAG}) || true
                        sudo docker pull ${DOCKER_IMAGE}:${DOCKER_TAG}
                        sudo docker run -d -p 8883:8080 ${DOCKER_IMAGE}:${DOCKER_TAG}
                    EOF
                    """
                }
            }
        }

    }

    post {
        always {
            junit '**/target/surefire-reports/*.xml'
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed. Check the logs for details.'
        }
    }
}
