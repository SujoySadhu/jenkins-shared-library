# jobportal-shared-lib

A small Jenkins Shared Library for the React Job Portal frontend.

## The idea in one sentence

Instead of writing the same long `sh` commands in every Jenkinsfile, we write them **once** here,
and every Jenkinsfile just calls them by name.

```
vars/securityScan.groovy     ->  securityScan('frontend')
vars/buildImage.groovy       ->  buildImage('frontend', 'my-image:v1')
vars/deployContainer.groovy  ->  deployContainer('name', 'my-image:v1', '8083:5173', 'KEY=value')
vars/sendReport.groovy       ->  sendReport('me@mail.local', 'frontend')
```

Rules:
- Every file in `vars/` becomes a step. **File name = step name.**
- The code inside `def call(...)` runs when the step is called.

## What each step does

| Step | Does |
|---|---|
| `securityScan(folder)` | Betterleaks + Semgrep + Trivy FS scan, HTML report, archive reports |
| `buildImage(folder, image)` | `docker build` + Trivy image scan, HTML report, archive report |
| `deployContainer(name, image, ports, envVar)` | remove old container, `docker run`, check logs + curl |
| `sendReport(email, folder)` | e-mail result + reports to Mailpit |

## Register it in Jenkins (once)

Manage Jenkins → System → **Global Trusted Pipeline Libraries** → Add

| Field | Value |
|---|---|
| Name | `jobportal-shared-lib` |
| Default version | `main` |
| Retrieval method | Modern SCM → Git |
| Project Repository | `https://github.com/SujoySadhu/jenkins-shared-library.git` |

## Use it in a Jenkinsfile

The first line loads the library:

```groovy
@Library('jobportal-shared-lib') _
```

After that, `securityScan(...)`, `buildImage(...)`, `deployContainer(...)` and `sendReport(...)`
can be used like any normal Jenkins step.
