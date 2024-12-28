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
                        aquasec/trivy:0.58.1 image ${DOCKER_IMAGE}:${DOCKER_TAG} > trivy_report.txt
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
                withCredentials([string(credentialsId: REMOTE_SERVER_AUTH, variable: 'REMOTE_SERVER_USER_IP')]) {
                    sshagent([REMOTE_SERVER_ID]) {
                        script {
                            sh """
                            ssh -o StrictHostKeyChecking=no ${REMOTE_SERVER_USER_IP} << EOF
                                sudo docker pull ${DOCKER_IMAGE}:${DOCKER_TAG}
                                sudo docker run -d -p 8883:8080 ${DOCKER_IMAGE}:${DOCKER_TAG}
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
