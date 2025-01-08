pipeline {
    agent any

    environment {
        MVN_HOME = '/usr/bin/mvn'  // Path to Maven
        APP_NAME = 'todo-0.0.1.jar'  // Your application JAR file name
        DEPLOY_PATH = '/var/lib/jenkins/deploy'  // Path where the JAR will be stored
        OLD_PORT = 7000  // Port for the old app
        NEW_PORT = 7001  // Port for the new app
        JAVA_HOME = '/usr/lib/jvm/java-21-openjdk-amd64'  // Correct Java version path
		DOMAIN_NAME= 's.todo.redsols.com'
    }

    stages {
        stage('Checkout') {
            steps {
                // Checkout the project from Git repository
                git branch: 'main', credentialsId: 'bfc88f96-eb1e-4df4-99cb-66f945cc956a', url: 'https://github.com/YashwanthVeesarapu/ToDo-Server.git'  // Replace with your Git repository URL
            }
        }

        stage('Build') {
            steps {
                // Build the Spring Boot application with Maven
                script {
                    sh "'${MVN_HOME}' clean install"
                }
            }
        }

        stage('Deploy New Version') {
            steps {
                script {
                    // Copy the built JAR to the deploy directory and run it on port 7001
                    sh """
                        cp target/${APP_NAME} ${DEPLOY_PATH}/
                        cd ${DEPLOY_PATH}
                        nohup java -jar -Dserver.port=${NEW_PORT} ${APP_NAME} > app-new.log 2>&1 &
                    """
                }
            }
        }

        stage('Verify New Version') {
            steps {
                script {
                    // Check if the new version is up and running on port 7001
                    def isAppRunning = sh(script: "curl --silent --head http://127.0.0.1:${NEW_PORT}", returnStatus: true) == 0
                    if (!isAppRunning) {
                        error "New version is not running on port ${NEW_PORT}!"
                    }
                }
            }
        }

        stage('Switch Traffic to New Version') {
            steps {
                script {
                    // Update Nginx configuration to point traffic to the new application (port 7001)
                    sh """
                        sudo sed -i 's/proxy_pass http://127.0.0.1:${OLD_PORT}/proxy_pass http://127.0.0.1:${NEW_PORT}/' /etc/nginx/sites-available/default
                        sudo systemctl reload nginx  // Reload Nginx to apply changes
                    """
                }
            }
        }

        stage('Stop Old Version') {
            steps {
                script {
                    // Gracefully stop the old version of the app (running on port 7000)
                    sh 'pkill -f "java -jar ${DEPLOY_PATH}/your-app-0.0.1-SNAPSHOT.jar" || true'
                }
            }
        }
    }

    post {
        success {
            echo 'Zero downtime deployment successful!'
        }
        failure {
            echo 'Zero downtime deployment failed!'
        }
    }
}
