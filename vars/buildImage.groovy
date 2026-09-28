// STEP 2: Build the Docker image, then scan the image with Trivy.
//
//   docker build -> creates the image
//   Trivy image  -> finds vulnerabilities inside the image -> trivy-image-report.json + .html
//
// How to call it from a Jenkinsfile:
//   buildImage('frontend', 'react-job-portal-fe-sl:v5')

def call(String folder, String imageName) {
    dir(folder) {

        echo "🔨 [shared-lib] Building Docker image ${imageName}"
        sh "docker build -t ${imageName} ."

        echo "🔎 [shared-lib] Trivy image scan"
        sh """
            trivy image --scanners vuln,misconfig,secret \
                --format json -o trivy-image-report.json ${imageName}

            trivy convert --format template --template "@/vagrant/html.tpl" \
                -o trivy-image-report.html trivy-image-report.json
        """

        archiveArtifacts artifacts: 'trivy-image-report.*', fingerprint: true
    }
}
