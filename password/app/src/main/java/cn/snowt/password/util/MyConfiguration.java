package cn.snowt.password.util;

/**
 * @Author: HibaraAi
 * @Date: 2022-03-03 22:18:33
 * @Description:
 */
public class MyConfiguration {
    private static String privateKey;
    private static String publicKey;

    private static MyConfiguration myConfiguration;
    private MyConfiguration() {
    }

    public static MyConfiguration getInstance(){
        if (myConfiguration == null) {
            synchronized (MyConfiguration.class) {
                if (myConfiguration == null) {
                    myConfiguration = new MyConfiguration();
                }
            }
        }
        privateKey = BaseUtils.getSharedPreference().getString(Constant.SHARE_PREFERENCES_PRIVATE_KEY, "");
        publicKey = BaseUtils.getSharedPreference().getString(Constant.SHARE_PREFERENCES_PUBLIC_KEY,"");
        return myConfiguration;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }
}
