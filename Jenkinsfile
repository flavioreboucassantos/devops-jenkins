pipeline {
    agent any
    environment {
        sonarLocal = tool 'SONAR_LOCAL'
        pathProjectArea = 'area'
        pathProjectSeleniumTest = 'selenium-test'
        jobParamPathProjectArea = '../Pipeline/area'
        jobParamMainArgumentArea = 'JenkinsRunProjectArea'
    }
    stages {
        stage('Fetching changes from the remote Git repository.') {
            steps {
                git url: 'https://github.com/flavioreboucassantos/devops-jenkins.git'
            }
        }
        stage('AREA clean package skipTests') {
            steps {
                bat "mvn clean package -f $pathProjectArea -DskipTests"
            }
        }
        stage('AREA test') {
            steps {
                bat "mvn test -f $pathProjectArea"
            }
        }
        stage('AREA sonar:sonar skipTests') {
            steps {
                withSonarQubeEnv('SONARQUBE_SERVER') {
                    // Using sonar-maven-plugin
                    bat 'mvn sonar:sonar ' +
                    "-f $pathProjectArea " +
                    '-DskipTests '+
                    '-Dsonar.projectKey=Area '+
                    '-Dsonar.sources=src/ '+
                    '-Dsonar.exclusions=src/**/test/**/*'
                }
            }
        }
        stage('QUALITY GATE') {
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
        stage('Next two stages in parallel.') {
            parallel {
                stage('SELENIUM') {
                    steps {
                        sleep(time:10, unit:'SECONDS')
                        bat "mvn clean -f $pathProjectSeleniumTest -DskipTests"
                        bat "mvn test -f $pathProjectSeleniumTest -Dtest=SeleniumControllerArea"
                        bat "mvn test -f $pathProjectSeleniumTest -Dtest=SeleniumGridControllerArea"
                        bat "wmic PROCESS Where \"name Like '%%java.exe%%' AND CommandLine like '%%$jobParamMainArgumentArea%%'\" Call Terminate"
                    }
                }
                stage('RUN AREA FOR SELENIUM') {
                    steps {
                        build job: 'RunProjectArea', parameters: [
                            string(name: 'PathProject', value:"$jobParamPathProjectArea"),
                            string(name: 'AbsoluteTimeoutMinutes', value:'5'),
                            string(name: 'MainArgument', value:"$jobParamMainArgumentArea")
                        ]
                    }
                }
            }
        }
    }
    post {
        always {
            junit allowEmptyResults: true, stdioRetention: '', testResults: 'area/target/surefire-reports/*.xml, selenium-test/target/surefire-reports/*.xml'
            archiveArtifacts artifacts: 'area/target/area-*.jar', followSymlinks: false, onlyIfSuccessful: true
        }
        unsuccessful {
            emailext attachLog: true, body: 'Logs Attached.', subject: 'Build $BUILD_NUMBER unsuccessful', to: "$EmailextTo"
        }
        fixed {
            emailext attachLog: true, body: 'Logs Attached.', subject: 'Build $BUILD_NUMBER fixed', to: "$EmailextTo"
        }
    }
}
