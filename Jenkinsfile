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
            ssh -o StrictHostKeyChecking=no $REMOTE_SERVER << 'EOF'
                # Stop and remove containers using the image
                CONTAINER_IDS=\$(sudo docker ps -q --filter ancestor=${DOCKER_IMAGE}:${DOCKER_TAG})
                if [ -n "\$CONTAINER_IDS" ]; then
                    sudo docker stop \$CONTAINER_IDS
                    sudo docker rm \$CONTAINER_IDS
                fi

                # Remove the old image if it exists
                IMAGE_ID=\$(sudo docker images -q ${DOCKER_IMAGE}:${DOCKER_TAG})
                if [ -n "\$IMAGE_ID" ]; then
                    sudo docker rmi -f \$IMAGE_ID
                fi

                # Pull the new image and run the container
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
