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
                sshagent(['remote-server-ssh']) {  // Ensure this is your correct SSH credentials ID
                    script {
                        sh """
                        ssh -o StrictHostKeyChecking=no ${REMOTE_SERVER} << EOF
                            # Stop and remove containers based on the image tag, if they exist
                            CONTAINERS=\$(docker ps -a -q --filter ancestor=${DOCKER_IMAGE}:${DOCKER_TAG})
                            if [ -n "\$CONTAINERS" ]; then
                                docker stop \$CONTAINERS
                                docker rm \$CONTAINERS
                            fi

                            # Pull the latest Docker image
                            sudo docker pull ${DOCKER_IMAGE}:${DOCKER_TAG}

                            # Check for port availability, if 8883 is in use, try binding to another port
                            if sudo lsof -i :8883; then
                                echo 'Port 8883 is already in use, attempting port 8884'
                                sudo docker run -d -p 8884:8080 ${DOCKER_IMAGE}:${DOCKER_TAG}
                            else
                                sudo docker run -d -p 8883:8080 ${DOCKER_IMAGE}:${DOCKER_TAG}
                            fi
                        EOF
                        """
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
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed. Check the logs for details.'
        }
    }
}
