pipeline {
    agent any

    environment {
        SECRET = credentials('TODO_SECRET')               // Secret credentials
        MONGO_URI = credentials('TODO_MONGO_URI')         // MongoDB URI
        EXPIRATION = 604800000                      // Expiration time
        ADMIN_TOKEN = 'redsols'     // Admin token
        AMPLIFY_API_KEY = credentials('AMPLIFY_API_KEY') // Amplify API key
        SPRING_MAIL_USERNAME = credentials('REDSOLS_SMTP_USERNAME') // Mail username
        SPRING_MAIL_PASSWORD = credentials('REDSOLS_SMTP_PASSWORD') // Mail password
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
                    docker run -d --name ${NEW_CONTAINER} -p ${NEW_PORT}:7001 \
                    -e SECRET=${SECRET} \
                    -e MONGO_URI=${MONGO_URI} \
                    -e ADMIN_TOKEN=${ADMIN_TOKEN} \
                    -e AMPLIFY_API_KEY=${AMPLIFY_API_KEY} \
                    -e SPRING_MAIL_USERNAME=${SPRING_MAIL_USERNAME} \
                    -e SPRING_MAIL_PASSWORD=${SPRING_MAIL_PASSWORD} \
                    ${DOCKER_IMAGE}:${DOCKER_TAG}  
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
