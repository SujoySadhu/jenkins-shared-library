// STEP 3: Run the image as a container and check that it works.
//
//   1. remove the old container (if it exists)
//   2. start the new container
//   3. wait 10 seconds, print the logs, open the page with curl
//
// How to call it from a Jenkinsfile:
//   deployContainer('my-container', 'my-image:v5', '8083:5173', 'VITE_API_URL=http://localhost:8082/api/v1')
//                    name            image          ports         environment variable

def call(String containerName, String imageName, String ports, String envVar) {

    echo "🚀 [shared-lib] Deploying ${containerName} (${ports})"

    sh "docker rm -f ${containerName} || true"
    sh "docker run -d --name ${containerName} --restart unless-stopped -e ${envVar} -p ${ports} ${imageName}"

    sleep(10)

    String hostPort = ports.split(':')[0]
    sh "docker logs ${containerName}"
    sh "curl -f http://localhost:${hostPort}/ > /dev/null"

    echo "✅ [shared-lib] ${containerName} is running on http://localhost:${hostPort}"
}
