package lizec.lizec.tlock.model;

import java.io.Serializable;

public class PwdInfo  implements Serializable{
    private String APPName;
    private String userName;
    private String pwd;

    public String getAPPName() {
        return APPName;
    }

    public String getUserName() {
        return userName;
    }

    public String getPwd() {
        return pwd;
    }

    public PwdInfo(String APPName, String userName, String pwd) {
        this.APPName = APPName;
        this.userName = userName;
        this.pwd = pwd;
    }
}
