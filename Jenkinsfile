pipeline {
    agent any

    stages {
        stage('Build Backend') {
            steps {
                bat 'mvn clean package -f area -DskipTest=true'
            }
        }
    }
}
