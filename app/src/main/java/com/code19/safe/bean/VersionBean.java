package com.code19.safe.bean;

import java.util.List;

/**
 * Created by Gh0st on 2016/2/22 0022.
 * 下午 9:15
 */
public class VersionBean {

    /**
     * appname : VianIM
     * newapp : http://www.19code.com/app/vianim.1.2.apk
     * update : we update something
     * version : 1.2
     */

    private List<APPVersionEntity> APPVersion;

    public void setAPPVersion(List<APPVersionEntity> APPVersion) {
        this.APPVersion = APPVersion;
    }

    public List<APPVersionEntity> getAPPVersion() {
        return APPVersion;
    }

    public static class APPVersionEntity {
        private String appname;
        private String newapp;
        private String update;
        private String version;

        public void setAppname(String appname) {
            this.appname = appname;
        }

        public void setNewapp(String newapp) {
            this.newapp = newapp;
        }

        public void setUpdate(String update) {
            this.update = update;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getAppname() {
            return appname;
        }

        public String getNewapp() {
            return newapp;
        }

        public String getUpdate() {
            return update;
        }

        public String getVersion() {
            return version;
        }
    }
}
