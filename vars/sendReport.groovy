// STEP 4: E-mail the build result with all scan reports attached (Mailpit receives it).
//
// How to call it from a Jenkinsfile:
//   sendReport('devops@jobportal.local', 'frontend')

def call(String toEmail, String folder) {

    echo "📧 [shared-lib] Sending reports to ${toEmail}"

    emailext(
        to: toEmail,
        subject: "Jenkins Build ${currentBuild.currentResult}: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
        mimeType: 'text/html',
        body: """
            <p>Build <b>${env.JOB_NAME} #${env.BUILD_NUMBER}</b> finished: <b>${currentBuild.currentResult}</b></p>
            <p>Made with the <b>jobportal-shared-lib</b> shared library.</p>
            <p>Console: <a href="${env.BUILD_URL}console">${env.BUILD_URL}console</a></p>
        """,
        attachmentsPattern: "${folder}/betterleaks.json, ${folder}/semgrep.json, ${folder}/trivy-*.html",
        attachLog: true
    )
}
