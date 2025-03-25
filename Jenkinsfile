pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "sportmagazine-app"
        DOCKER_HUB_IMAGE = "svanchukov/sportmagazine-app"
        DOCKER_USER = credentials('docker-hub-user')
        DOCKER_PASSWORD = credentials('docker-hub-pass')
    }

    stages {
        stage('Checkout') {
            steps {
                echo "Stage Checkout started"
                checkout([$class: 'GitSCM', branches: [[name: '*/main']],
                    userRemoteConfigs: [[url: 'https://github.com/svanchukov/SportMagazine.git']]
                ])
            }
        }

        stage('Build') {
            steps {
                echo "Stage Build started"
                sh 'chmod +x mvnw'
                sh './mvnw clean package'
            }
        }

        stage('Test') {
            steps {
                echo "Stage Test started"
                sh './mvnw test'
            }
        }

        stage('Build Docker Image') {
            steps {
                echo "Stage Docker Build started"
                sh 'docker --version'
                sh 'docker build -t ${DOCKER_IMAGE} .'
            }
        }

        stage('Push to Docker Hub') {
            steps {
                withDockerRegistry([credentialsId: 'docker-hub', url: '']) {
                    echo "Pushing to Docker Hub..."
                    sh 'docker login -u ${DOCKER_USER} -p ${DOCKER_PASSWORD}'
                    sh 'docker tag ${DOCKER_IMAGE} ${DOCKER_HUB_IMAGE}'
                    sh 'docker push ${DOCKER_HUB_IMAGE}'
                }
            }
        }

        stage('Deploy') {
            steps {
                echo "Stage Deploy started"
                sh '/usr/local/bin/docker-compose down && /usr/local/bin/docker-compose up -d'
            }
        }
    }
}
