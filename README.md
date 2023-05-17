# jacked-plugin

## Introduction

[Jacked](https://github.com/carbonetes/jacked) provides organizations with a more comprehensive look at their application to take calculated actions and create a better security approach. Its primary purpose is to scan vulnerabilities to implement subsequent risk mitigation measures.

This jenkins plugin scans a given target and expose vulnerability.

## Getting started

This jenkins plugin installs jacked binary tool in the job workspace directory and performs scan. 
## Auto-install & Update Binary [Jacked](https://github.com/carbonetes/jacked)
The plugin will install the "jacked" binary tool. `(Windows and Linux Supported)`
- `Auto-update`: If checked, the binary will automatically update when a new release is available.
- Uses Scoop for Windows
- Uses Shell Script for Linux

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

### Skip Build Fail
Default value is `false / unchecked`.
<br>
<b>Warning:</b> If the value is checked, it will restrict the plugin from failing the build based on the assessment result.

### Skip Database Update
Default value is `false / unchecked`.
<br>
<b>Warning:</b> If the value is checked, it will skip check database update while scanning.

### Usage as Pipeline
```sh
pipeline {
    agent any
    stages {
        stage('Jacked Scan') {
            steps {
                script {
                    jacked scanType: 'image',           // Choose Scan Type: image, directory, tar, or sbom.
                    scanName: 'ubuntu',                 // Input: Image name, Directory path, tar file path, or sbom file path.
                    severityType: 'high',               // Select a threshold that will fail the build when equal to or above the severity found in the results. 
                                                        // Severity: critical, high, medium, low, negligible, unknown.
                    skipFail: false,                    // Default as false. Skip build to fail based on the assessment.
                    skipDbUpdate: false                 // Default as false. Skip Database Update when scanning.
                }
            }
        }
    }
}

```

## LICENSE

Licensed under MIT, see [LICENSE](LICENSE.md)

