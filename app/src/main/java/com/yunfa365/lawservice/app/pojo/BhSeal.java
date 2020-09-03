package com.yunfa365.lawservice.app.pojo;

import com.polidea.rxandroidble2.scan.ScanResult;

import java.io.Serializable;

public class BhSeal implements Serializable {
    public int ID;
    public int LawId;
    public String ZTitle = "";
    public String ZMac = "";
    public String Addtime = "";

    public transient ScanResult scanResult;

    public BhSeal() {

    }

    public BhSeal(ScanResult result) {
        scanResult = result;
        ZMac = scanResult.getBleDevice().getMacAddress();
    }

    @Override
    public String toString() {
        if (ZTitle != null) {
            return ZTitle;
        } else if (scanResult != null) {
            return scanResult.getBleDevice().getMacAddress();
        }
        return null;
    }
}
