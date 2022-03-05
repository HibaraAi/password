package cn.snowt.password;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.util.List;

import cn.snowt.password.util.RSAUtils;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    public String publicKey;
    public String privateKey;

    @Before
    public void testGetRsaKey() {
        List<String> randomKey = RSAUtils.getRandomKey();
        publicKey = randomKey.get(0);
        privateKey = randomKey.get(1);
        System.out.println("公钥：" +publicKey);
        System.out.println("私钥：" +privateKey);
    }

    @Test
    public void testEncode(){
        String msg = "01234567890123456789012345678901234567890123456789012345678901234567890123456789";
        String encode = RSAUtils.encode(msg, publicKey);
        String decode = RSAUtils.decode(encode, privateKey);
        Log.e("明文：",msg);
        Log.e("密文：",encode);
        Log.e("解密后：",decode);

    }
}