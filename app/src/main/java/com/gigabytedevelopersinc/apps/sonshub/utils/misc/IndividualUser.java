package com.gigabytedevelopersinc.apps.sonshub.utils.misc;

/**
 * @author Created by Emmanuel Nwokoma (Founder and CEO at Gigabyte Developers) on 1/2/2019
 **/
public class IndividualUser {
    public String androidId,
            token,
            device,
            deviceModel,
            deviceManufacturer,
            deviceBrand,
            userDeviceName,
            androidOS,
            androidVersion,
            appVersion;
    public int appCode;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public IndividualUser() {

    }

    public IndividualUser(String androidId, String token, String device, String deviceModel, String deviceManufacturer, String deviceBrand, String userDeviceName, String androidOS, String androidVersion, String appVersion, int appCode) {
        this.androidId = androidId;
        this.token = token;
        this.device = device;
        this.deviceModel = deviceModel;
        this.deviceManufacturer = deviceManufacturer;
        this.deviceBrand = deviceBrand;
        this.userDeviceName = userDeviceName;
        this.androidOS = androidOS;
        this.androidVersion = androidVersion;
        this.appVersion = appVersion;
        this.appCode = appCode;
    }
}
