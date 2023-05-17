# jacked-plugin

## Introduction

[Jacked](https://github.com/carbonetes/jacked) Jacked provides organizations with a more comprehensive look at their application to take calculated actions and create a better security approach. Its primary purpose is to scan vulnerabilities to implement subsequent risk mitigation measures.

This jenkins plugin scans a given target and expose vulnerability.

## Getting started

This jenkins plugin installs jacked in the job workspace directory and performs scan. 
See section [Installation/Recommended](https://github.com/carbonetes/jacked) for more installation details.

## Usage as add build step
<img src="assets/add-build-step.png" alt="Jacked plugin" />

## Plugin Configuration

<img src="assets/configuration.png" alt="Jacked plugin configuration" />

## Output
Provides the following:
- `Quiet Mode`: Removed verbiage.
- Show a list of packages.
- `Analyzing BOM`: Showing vulnerabilities found and providing recommendations to fix them.
- Show CI Assessment Result: Pass or Fail based on the selected fail criteria severity type.

## Plugin Configuration Fields and Descriptions
### Scan
<b>Input: </b> Image name, Directory path, tar file path, or sbom file path.
### Fail Criteria Severity
<b>Description: </b>Select a threshold that will fail the build when equal to or above the severity found in the results. 
<br>
<b>Option:</b> 
- Critical
- High
- Medium
- Low
- Negligible
- Unknown
### Scan Type
<b>Description: </b>Specified the input on scan field based on the selected scan type.
<br>
<b>Option:</b>
- `Image`: Provide the image to be scanned.
- `Directory`: Provide the target directory path to be scanned.
- `Tar File`: Provide the target tar file path to be scanned.
- `SBOM File`: Provide the target [Diggity](https://github.com/carbonetes/diggity) JSON Format SBOM file path to be scanned.
### Skip Fail
<b>Warning:</b> If the value is checked, it will restrict the plugin from failing the build based on the assessment result.
### Download and install jacked automatically.
It is recommended to check on the first run. If checked, the plugin will install the "jacked" binary tool.
- `Prerequisite`: "Jacked binary" needs to be available in the path in order to be executed.
- `Auto-update`: If checked, the binary will automatically update when a new release is available, otherwise it will be reinstalled.

### Usage as Pipeline
```sh
pipeline {
    agent any
    
    stages {
        stage('Vulnerability Scan') {
            steps {
                script {
                    jacked scanType: 'image', scanName: 'alpine', severityType: 'high', autoInstall: true, skipFail: false  
                }
            }
        }
    }
}
```

## LICENSE

Licensed under MIT, see [LICENSE](LICENSE.md)

