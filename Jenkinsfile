pipeline {
    agent any

    environment {
        MVN_HOME = '/usr/bin/mvn'                      // Path to Maven
        APP_NAME = 'todo-0.0.1.jar'                   // Application JAR file name
        DEPLOY_PATH = '/var/lib/jenkins/deploy'       // Directory for deployed JAR
        WORK_DIR = '/home/yash/Workspace/Redsols/ToDo-Server'        // Jenkins workspace directory
        OLD_PORT = '7000'                             // Port for the currently running app
        NEW_PORT = '7001'                             // Port for the new version
        JAVA_HOME = '/usr/lib/jvm/java-21-openjdk-amd64' // Java version path
        DOMAIN_NAME = 's.todo.redsols.com'            // Domain name
        SECRET = credentials('SECRET')               // Secret credentials
        MONGO_URI = credentials('MONGO_URI')         // MongoDB URI
        EXPIRATION = credentials('EXPIRATION')       // Expiration time
        ADMIN_TOKEN = credentials('ADMIN_TOKEN')     // Admin token
        AMPLIFY_API_KEY = credentials('AMPLIFY_API_KEY') // Amplify API key
        SPRING_MAIL_USERNAME = credentials('SPRING_MAIL_USERNAME') // Mail username
        SPRING_MAIL_PASSWORD = credentials('SPRING_MAIL_PASSWORD') // Mail password
    }

    stages {
        stage('Checkout Code') {
            steps {
                // Clone the Git repository
                git branch: 'main', 
                    credentialsId: 'bfc88f96-eb1e-4df4-99cb-66f945cc956a', 
                    url: 'https://github.com/YashwanthVeesarapu/ToDo-Server.git' 
            }
        }

        stage('Build Application') {
            steps {
                // Compile and package the application using Maven
                script {
                    sh "${MVN_HOME} clean install"
                }
            }
        }

        stage('Deploy New Version') {
            steps {
                script {
                    // Stop any existing process running on the new port
                    echo "Stopping any existing process on port ${NEW_PORT}"
                    sh "fuser -k ${NEW_PORT}/tcp || true"

                    // Deploy the new application version
                    echo "Deploying the new application to ${DEPLOY_PATH}"
                    sh """
                        cp target/${APP_NAME} ${WORK_DIR}/target/${APP_NAME}
                        cd ${WORK_DIR}
                        nohup java -jar -Dserver.port=${NEW_PORT} target/${APP_NAME} > app-new.log 2>&1 &
                    """
                }
            }
        }

        stage('Verify New Version') {
            steps {
                script {
                    // Check if the new application is up and running
                    echo "Verifying the application on port ${NEW_PORT}"
                    def isRunning = sh(script: "curl --silent --head http://127.0.0.1:${NEW_PORT}", returnStatus: true) == 0
                    if (!isRunning) {
                        error "New application version is not running on port ${NEW_PORT}!"
                    } else {
                        echo "New application version is running successfully on port ${NEW_PORT}!"
                    }
                }
            }
        }

        stage('Switch Traffic to New Version') {
            steps {
                script {
                    // Update Nginx configuration for the s.todo.redsols.com domain
                    echo "Switching traffic to the new application on port ${NEW_PORT} for ${DOMAIN_NAME}"
                    sh """
                        sudo sed -i 's/proxy_pass http://127.0.0.1:${OLD_PORT}/proxy_pass http://127.0.0.1:${NEW_PORT}/' /etc/nginx/sites-available/${DOMAIN_NAME}
                        sudo nginx -t  # Test Nginx configuration
                        sudo systemctl reload nginx  # Reload Nginx to apply changes
                    """
                }
            }
        }

        stage('Stop Old Version') {
            steps {
                script {
                    // Gracefully stop the old application running on the old port
                    echo "Stopping the old application running on port ${OLD_PORT}"
                    sh "pkill -f 'java -jar ${DEPLOY_PATH}/${APP_NAME}' || true"
                }
            }
        }
    }

    post {
        success {
            echo 'Zero downtime deployment completed successfully!'
        }
        failure {
            echo 'Zero downtime deployment failed!'
        }
    }
}
