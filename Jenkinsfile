pipeline {
    agent any

    environment {
        MVN_HOME = '/usr/bin/mvn'                      // Path to Maven
        APP_NAME = 'todo-0.0.1.jar'                   // Application JAR file name
        DEPLOY_PATH = '/var/lib/jenkins/deploy'       // Directory for deployed JAR
        WORKSPACE = '/home/yash/Workspace/Redsols/ToDo-Server'        // Jenkins workspace directory
        JAVA_HOME = '/usr/lib/jvm/java-21-openjdk-amd64' // Java version path
        DOMAIN_NAME = 's.todo.redsols.com'            // Domain name
        SECRET = credentials('SECRET')               // Secret credentials
        MONGO_URI = credentials('MONGO_URI')         // MongoDB URI
        EXPIRATION = credentials('EXPIRATION')       // Expiration time
        ADMIN_TOKEN = credentials('ADMIN_TOKEN')     // Admin token
        AMPLIFY_API_KEY = credentials('AMPLIFY_API_KEY') // Amplify API key
        SPRING_MAIL_USERNAME = credentials('SPRING_MAIL_USERNAME') // Mail username
        SPRING_MAIL_PASSWORD = credentials('SPRING_MAIL_PASSWORD') // Mail password
        DOCKER_IMAGE = 'redsols/todo-api'                  // Docker image name
        CONTAINER1 = 'todo-api-v1'                        // Docker container name 1
        CONTAINER2 = 'todo-api-v2'                        // Docker container name 2
        PORT1 = '7000'                                    // Port for container 1
        PORT2 = '7001'                                    // Port for container 2
        DOCKER_TAG = 'latest'                             // Docker image tag
    }

    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'main', 
                    credentialsId: 'eff5d436-cb19-40a4-aa3c-e7df06f08652', 
                    url: 'https://github.com/YashwanthVeesarapu/ToDo-Server.git' 
            }
        }

        stage('Build Application') {
            steps {
                script {
                    sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
                }
            }
        }

        stage('Deploy New Version') {
            steps {
                script {
                    def isRunning = sh(script: "docker ps --filter 'name=${CONTAINER1}' --format '{{.Names}}'", returnStdout: true).trim()
                    env.ACTIVE_CONTAINER = isRunning == CONTAINER1 ? CONTAINER1 : CONTAINER2
                    env.NEW_CONTAINER = env.ACTIVE_CONTAINER == CONTAINER1 ? CONTAINER2 : CONTAINER1
                    env.NEW_PORT = env.NEW_CONTAINER == CONTAINER1 ? PORT1 : PORT2
                    echo "Active: ${env.ACTIVE_CONTAINER}, Deploying: ${env.NEW_CONTAINER} on port ${env.NEW_PORT}"
                }
            }
        }

        stage('Deploy New Container') {
            steps {
                sh """
                    docker stop ${NEW_CONTAINER} || true
                    docker rm ${NEW_CONTAINER} || true
                    docker run -d --name ${NEW_CONTAINER} -p ${NEW_PORT}:7001 ${DOCKER_IMAGE}:${DOCKER_TAG}
                """
            }
        }

        stage('Verify New Container') {
            steps {
                script {
                    sleep 5
                    def response = sh(script: "docker exec ${NEW_CONTAINER} wget -qO- --server-response http://localhost:7001 2>&1 | awk '/HTTP\\// {print \$2}'", returnStdout: true).trim()
                    echo "Container Health Check Response: ${response}"

                    if (response != "200") {
                        error("Application inside container is not responding! Deployment aborted.")
                    }
                }
            }
        }

        stage('Remove Old Container') {
            steps {
                sh """
                    docker stop ${ACTIVE_CONTAINER} || true
                    docker rm ${ACTIVE_CONTAINER} || true
                """
            }
        }
    }
}
