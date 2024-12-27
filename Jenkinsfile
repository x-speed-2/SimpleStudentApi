pipeline {
    agent any

    tools {
        maven 'Maven'
    }

    environment {
        DOCKER_CREDENTIALS_ID = 'docker-hub-cnx' 
        DOCKER_IMAGE = 'r2fzqiky11/student-api'
        DOCKER_TAG = 'latest'
        SONARQUBE_SERVER = 'SonarQube'
        REMOTE_SERVER = 'ubuntu@152.70.168.196'
        SONARQUBE_CREDENTIALS_ID = 'sonarqube-credentials'
    }

    stages {
        stage('SonarQube Analysis') {
            steps {
                 withCredentials([usernamePassword(credentialsId: SONARQUBE_CREDENTIALS_ID, usernameVariable: 'USER', passwordVariable: 'PASS')]) {
                    sh '''
                        mvn sonar:sonar \
                        -Dsonar.projectKey=SimpleStudentApi \
                        -Dsonar.host.url=http://localhost:9000 \
                        -Dsonar.login=$USER \
                        -Dsonar.password=$PASS
                    '''
                }
            }
        }
        stage('Package') {
            steps {
                sh 'mvn package'
            }
        }

        stage('Verify JAR') {
            steps {
                script {
                    if (!fileExists('target/SimpleStudentApi-0.0.1-SNAPSHOT.jar')) {
                        error "JAR file not found. Build failed."
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}", ".")
                }
            }
        }

        stage('Scan Docker Image for Vulnerabilities') {
            steps {
                script {
                    sh '''
                    docker run --rm -v /var/run/docker.sock:/var/run/docker.sock \
                        aquasec/trivy:0.55.2 image ${DOCKER_IMAGE}:${DOCKER_TAG} > trivy_report.txt
                    cat trivy_report.txt
                    '''
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                script {
                    docker.withRegistry('', DOCKER_CREDENTIALS_ID) {
                        docker.image("${DOCKER_IMAGE}:${DOCKER_TAG}").push()
                    }
                }
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
