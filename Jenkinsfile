pipeline {
    agent any

    stages {
        stage('Build Backend') {
            steps {
                bat 'mvn clean package -f area -DskipTests'
			}
        }
        stage('Unit Tests') {
            steps {
                bat 'mvn test -f area'
            }
        }
    }
}
