pipeline {
    agent any
    environment {
        sonarLocal = tool 'SONAR_LOCAL'
        projectPathArea = 'area'
        projectPathSeleniumTest = 'selenium-test'
        jobParamPathBackendArea = '../Pipeline/area'
        jobParamMainArgumentArea = 'TESTMainArgumentAreaTEST'
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
        stage('>>>') {
            parallel {
                stage('RUN AREA') {
                    steps {
                        build job: 'RunBackend', parameters: [
                            string(name: 'PathBackend', value:"$jobParamPathBackendArea"),
                            string(name: 'AbsoluteTimeoutMinutes', value:'5'),
                            string(name: 'MainArgument', value:"$jobParamMainArgumentArea")
                        ]
                    }
                }
                stage('SELENIUM TEST') {
                    steps {
                        sleep(time:10, unit:'SECONDS')
                        bat "mvn clean -f $projectPathSeleniumTest -DskipTests"
                        bat "mvn test -f $projectPathSeleniumTest -Dtest=SeleniumControllerArea"
                        bat "mvn test -f $projectPathSeleniumTest -Dtest=SeleniumGridControllerArea"
                        bat "wmic PROCESS Where \"name Like '%%java.exe%%' AND CommandLine like '%%$jobParamMainArgumentArea%%'\" Call Terminate"
                    }
                }
            }
        }
    }
}
