pipeline {
    agent any
    environment {
        sonarLocal = tool 'SONAR_LOCAL'
        projectPathArea = 'area'
        projectPathSeleniumTest = 'selenium-test'
        jobParamPathBackendArea = '../Pipeline/area'
    }
    stages {
        stage('Fetching changes from the remote Git repository') {
            steps {
                git url: 'https://github.com/flavioreboucassantos/devops-jenkins.git'
            }
        }
        stage('AREA clean package skipTests') {
            steps {
                bat "mvn clean package -f $projectPathArea -DskipTests"
            }
        }
        stage('AREA test') {
            steps {
                bat "mvn test -f $projectPathArea"
            }
        }
        stage('AREA sonar:sonar skipTests') {
            steps {
                withSonarQubeEnv('SONARQUBE_SERVER') {
                    // Using sonar-maven-plugin
                    bat 'mvn sonar:sonar ' +
                    "-f $projectPathArea " +
                    '-DskipTests '+
                    '-Dsonar.projectKey=Area '+
                    '-Dsonar.sources=src/ '+
                    '-Dsonar.exclusions=src/**/test/**/*'
                }
            }
        }
        stage('Quality Gate') {
            steps {
                script {
                    timeout(time: 1, unit: 'HOURS') {
                        def qg = waitForQualityGate()
                        if (qg.status != 'OK') {
                            error "Pipeline aborted due to quality gate failure: $qg.status"
                        }
                    }
                }
            }
        }
        stage('RUN AREA + SELENIUM + GRID') {
            steps {
                script {
                    def branches = [:]
                    branches['RunBackend'] = {
                        build job: 'RunBackend', parameters: [
                            string(name: 'PathBackend', value:"$jobParamPathBackendArea"),
                            string(name: 'AbsoluteTimeoutMinutes', value:'2')
                        ]
                    }
                    branches['SeleniumTest'] = {
                        sleep(time:10, unit:'SECONDS')
                        bat "mvn clean -f $projectPathSeleniumTest -DskipTests"
                        bat "mvn test -f $projectPathSeleniumTest -Dtest=SeleniumControllerArea"
                        bat "mvn test -f $projectPathSeleniumTest -Dtest=SeleniumGridControllerArea"
                    }
                    parallel branches
                }
            }
        }
        stage('SELENIUM-TEST clean skipTests') {
            steps {
                bat "mvn clean -f $projectPathSeleniumTest -DskipTests"
            }
        }
        stage('SELENIUM-TEST test') {
            steps {
                bat "mvn test -f $projectPathSeleniumTest -Dtest=SeleniumControllerArea"
            }
        }
        stage('SELENIUM-TEST GRID test') {
            steps {
                bat "mvn test -f $projectPathSeleniumTest -Dtest=SeleniumGridControllerArea"
            }
        }
    }
}
