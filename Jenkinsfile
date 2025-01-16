pipeline {
    agent any

    environment {
        // Define environment variables for your project
        PROJECT_DIR = '/home/yash/Workspace/Redsols/ToDo-Server'
        JAVA_HOME = '/usr/lib/jvm/java-21-openjdk-amd64'
        MVN_HOME = '/usr/bin/mvn'
        APP_LOCATION = 'target/todo-0.0.1.jar'
        PORT = '7000'
    }

    stages {
        stage('Pull Latest Code') {
            steps {
                script {
                    echo "Pulling latest code into ${PROJECT_DIR}"
                    dir(PROJECT_DIR) {
                       sh 'git pull origin main'
                    }
                }
            }
        }

        stage('Build Application') {
            steps {
                script {
                    echo "Building the application using Maven"
                    dir(PROJECT_DIR) {
                        sh "${MVN_HOME} clean install"
                    }
                }
            }
        }

        stage('Deploy Application') {
            steps {
                script {
                    echo "Deploying the application to ${DEPLOY_DIR}"
                    sh """
                        cd ${PROJECT_DIR}
                        nohup java -jar -Dserver.port=${PORT} ${APP_LOCATION} > app.log 2>&1 &
                    """
                }
            }
        }

        stage('Verify Deployment') {
            steps {
                script {
                    echo "Verifying if the application is running on port ${PORT}"
                    def isRunning = sh(script: "curl --silent http://localhost:${PORT}/actuator/health | grep 'UP'", returnStatus: true) == 0
                    if (!isRunning) {
                        error "Application is not running on port ${PORT}!"
                    } else {
                        echo "Application is running successfully on port ${PORT}!"
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Deployment succeeded!'
        }
        failure {
            echo 'Deployment failed!'
        }
    }
}








// pipeline {
//     agent any

//     environment {
//         MVN_HOME = '/usr/bin/mvn'  // Path to Maven
//         APP_NAME = 'todo-0.0.1.jar'  // Your application JAR file name
//         DEPLOY_PATH = '/var/lib/jenkins/deploy'  // Path where the JAR will be stored
//         OLD_PORT = '7000'  // Port for the old app
//         NEW_PORT = '7001'  // Port for the new app
//         JAVA_HOME = '/usr/lib/jvm/java-21-openjdk-amd64'  // Correct Java version path
// 		DOMAIN_NAME = 's.todo.redsols.com'
//         SECRET = credentials('SECRET')
//         MONGO_URI = credentials('MONGO_URI')
//         EXPIRATION = credentials('EXPIRATION')
//         ADMIN_TOKEN = credentials('ADMIN_TOKEN')
//         AMPLIFY_API_KEY = credentials('AMPLIFY_API_KEY')
//         SPRING_MAIL_PORT = '587'
//         SPRING_MAIL_USERNAME = credentials('SPRING_MAIL_USERNAME')
//         SPRING_MAIL_PASSWORD = credentials('SPRING_MAIL_PASSWORD')
//         SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH = 'true'
//         SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE = 'true'
//     }
//     stages {
//         stage('Test Credentials') {
//             steps {
//                 script {
//                     echo "EXPIRATION: ${EXPIRATION}"
//                 }
//             }
//         }
//         stage('Checkout') {
//             steps {
//                 // Checkout the project from Git repository
//                 git branch: 'main', credentialsId: 'bfc88f96-eb1e-4df4-99cb-66f945cc956a', url: 'https://github.com/YashwanthVeesarapu/ToDo-Server.git'  // Replace with your Git repository URL
//             }
//         } 
//         stage('Build') {
//             steps {
//                 // Build the Spring Boot application with Maven
//                 script {
//                     sh "${MVN_HOME} clean install"
//                 }
//             }
//         }
//         stage('Deploy New Version') {
//             steps {
//                 script {
//                     // Copy the built JAR to the deploy directory and run it on port 7001
//                     sh """
//                         cp target/${APP_NAME} ${DEPLOY_PATH}/
//                         cd ${DEPLOY_PATH}
//                         nohup java -jar -Dserver.port=${NEW_PORT} ${APP_NAME} > app-new.log 2>&1 &
//                     """
//                 }
//             }
//         }
//         stage('Verify New Version') {
//             steps {
//                 script {
//                     // Check if the new version is up and running on port 7001
//                     def isAppRunning = sh(script: 'curl --silent --head http://127.0.0.1:${NEW_PORT}', returnStatus: true) == 0
//                     if (!isAppRunning) {
//                         error 'New version is not running on port ${NEW_PORT}!'
//                     }
//                 }
//             }
//         }
//         stage('Switch Traffic to New Version') {
//             steps {
//                 script {
//                     // Update Nginx configuration to point traffic to the new application (port 7001)
//                     sh '''
//                         sudo sed -i 's/proxy_pass http://127.0.0.1:${OLD_PORT}/proxy_pass http://127.0.0.1:${NEW_PORT}/' /etc/nginx/sites-available/default
//                         sudo systemctl reload nginx  // Reload Nginx to apply changes
//                     '''
//                 }
//             }
//         }
//         stage('Stop Old Version') {
//             steps {
//                 script {
//                     // Gracefully stop the old version of the app (running on port 7000)
//                     sh "pkill -f 'java -jar ${DEPLOY_PATH}/todo-0.0.1.jar' || true"
//                 }
//             }
//         }
//     }

//     post {
//         success {
//             echo 'Zero downtime deployment successful!'
//         }
//         failure {
//             echo 'Zero downtime deployment failed!'
//         }
//     }
// }
