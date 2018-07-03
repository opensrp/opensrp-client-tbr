package org.smartregister.nutrition.fragment;


import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.smartregister.Context;
import org.smartregister.nutrition.R;
import org.smartregister.nutrition.helper.DBQueryHelper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;

import static util.TbrConstants.VIEW_CONFIGS.PRESUMPTIVE_REGISTER_HEADER;

/**
 * Created by samuelgithengi on 11/6/17.
 */

public class PresumptivePatientRegisterFragment extends BaseRegisterFragment {

    private String decryptString(KeyStore.PrivateKeyEntry privateKeyEntry, String cipherText) throws Exception {
        Cipher output;
        if(Build.VERSION.SDK_INT >= 23) {
            output = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            output.init(2, privateKeyEntry.getPrivateKey());
        } else {
            output = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
            RSAPrivateKey cipherInputStream = (RSAPrivateKey)privateKeyEntry.getPrivateKey();
            output.init(2, cipherInputStream);
        }

        CipherInputStream var9 = new CipherInputStream(new ByteArrayInputStream(Base64.decode(cipherText, 0)), output);
        ArrayList values = new ArrayList();

        int nextByte;
        while((nextByte = var9.read()) != -1) {
            values.add(Byte.valueOf((byte)nextByte));
        }

        byte[] bytes = new byte[values.size()];

        for(int i = 0; i < bytes.length; ++i) {
            bytes[i] = ((Byte)values.get(i)).byteValue();
        }

        return new String(bytes, 0, bytes.length, "UTF-8");
    }

    public KeyStore initKeyStore() {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load((KeyStore.LoadStoreParameter)null);

            return keyStore;

        } catch (IOException | NoSuchAlgorithmException | CertificateException | KeyStoreException var2) {
            var2.printStackTrace();
        }
        return null;
    }

    private KeyStore.PrivateKeyEntry getUserKeyPair(String username) throws Exception {
        KeyStore ks = initKeyStore();
        return ks.containsAlias(username)?(KeyStore.PrivateKeyEntry)ks.getEntry(username, (KeyStore.ProtectionParameter)null):null;
    }

    private void exportDB() {
        try {
            String encryptedGroupId = Context.getInstance().allSharedPreferences().fetchEncryptedGroupId("demo");

            try {
                KeyStore.PrivateKeyEntry e = getUserKeyPair("demo");
                if(e != null) {
                    String groupId = decryptString(e, encryptedGroupId);
                    Log.e(this.getClass().getName(), groupId);
                }
            } catch (Exception var6) {
                var6.printStackTrace();
            }

            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String  currentDBPath= "//data//" + "org.smartregister.nutrition"
                        + "//databases//" + "drishti.db";
                String backupDBPath  = "/DCIM/drishti.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(getActivity(), backupDB.toString(), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void populateClientListHeaderView(View view) {
       // exportDB();//TODO

        View headerLayout = getLayoutInflater(null).inflate(R.layout.register_common_header_list, null);
        populateClientListHeaderView(view, headerLayout, PRESUMPTIVE_REGISTER_HEADER);
    }

    @Override
    protected String getMainCondition() {
        return DBQueryHelper.getPresumptivePatientRegisterCondition();
    }

    @Override
    protected String[] getAdditionalColumns(String tableName) {
        return new String[]{};
    }

    @Override
    public String getAggregateCondition(boolean b) {
        return "";
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

}
