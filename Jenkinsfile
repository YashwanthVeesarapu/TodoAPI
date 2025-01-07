pipeline {
    agent any
    environment {
        MVN_HOME = '/usr/local/apache-maven'  // Set Maven home
    }
    stages {
        stage('Checkout') {
            steps {
                // Checkout the project from Git
                git 'https://github.com/YashwanthVeesarapu/ToDo-Server.git'
            }
        }
        stage('Build') {
            steps {
                // Run Maven build
                sh "'${MVN_HOME}/bin/mvn' clean install"
            }
        }
    }
}