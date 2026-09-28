// STEP 1: Scan the source code with 3 tools.
//
//   Betterleaks -> finds passwords / API keys in the code   -> betterleaks.json
//   Semgrep     -> finds insecure code patterns (SAST)      -> semgrep.json
//   Trivy FS    -> finds vulnerable npm packages            -> trivy-fs-report.json + .html
//
// How to call it from a Jenkinsfile:
//   securityScan('frontend')

def call(String folder) {
    dir(folder) {

        echo "🔐 [shared-lib] Betterleaks secret scan"
        def rc = sh(script: 'betterleaks dir . --report-path betterleaks.json --report-format json', returnStatus: true)
        if (rc != 0) {
            unstable("betterleaks found potential secrets")
        }

        echo "🧪 [shared-lib] Semgrep SAST scan"
        sh 'semgrep scan --config auto --json --output=semgrep.json'

        echo "🔎 [shared-lib] Trivy file system scan"
        sh '''
            trivy fs . --scanners vuln,misconfig,secret \
                --skip-files 'betterleaks.json,semgrep.json' \
                --format json -o trivy-fs-report.json

            trivy convert --format template --template "@/vagrant/html.tpl" \
                -o trivy-fs-report.html trivy-fs-report.json
        '''

        // Save all reports in Jenkins (Build Artifacts)
        archiveArtifacts artifacts: 'betterleaks.json, semgrep.json, trivy-fs-report.*', fingerprint: true
    }
}
