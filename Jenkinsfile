pipeline {
    agent any
    stages {
        stage ('Início') {
            steps {
                bat 'echo início'
            }
        }
        stage ('Meio') {
            steps {
                bat 'echo meio'
                bat 'echo meio 2'
            }
        }
         stage ('Fim') {
            steps {
                sleep(5)
                bat 'echo fim'
            }
        }
    }
}