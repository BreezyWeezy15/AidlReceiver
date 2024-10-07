// IAppCommunicationService.aidl
package com.app.lockcompose;

// Declare any non-default types here with import statements

interface IAppCommunicationService {
    void sendAppData(in List<String> selectedAppPackages, in String timeInterval, in String pinCode);
}