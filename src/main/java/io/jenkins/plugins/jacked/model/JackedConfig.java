package io.jenkins.plugins.jacked.model;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JackedConfig {
    private String scanDest;
    private String scanName;
    private String severityType;
    private String scanType;
    private Boolean skipFail;
    private String token;
    private Map<String, String> content;
    
    public JackedConfig(
        String scanDest, 
        String scanName, 
        String severityType, 
        String scanType, 
        Boolean skipFail, 
        String token
    ){
        this.scanDest = scanDest;
        this.scanName = scanName;
        this.severityType = severityType;
        this.scanType = scanType;
        this.skipFail = skipFail;
        this.token = token;
    }

}
