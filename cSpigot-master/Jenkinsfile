node {
  stage 'Git Clone'
  checkout scm
  stage 'Git Submodule Update'
  sh 'git submodule update --init'
  stage 'Apply Patches'
  sh './applyPatches.sh'
  stage 'Maven Compile'
  if (env.BRANCH_NAME == 'master') {
    sh 'mvn clean deploy -U'
  } else {
    sh 'mvn clean package -U'
  }
  stage 'Jenkins Archive'
  step([$class: 'ArtifactArchiver', artifacts: 'mspigot-api/target/*.jar', fingerprint: true])
  step([$class: 'ArtifactArchiver', artifacts: 'mspigot-server/target/*.jar', fingerprint: true])
}
