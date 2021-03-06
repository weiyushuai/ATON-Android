package com.juzix.wallet.engine;

import com.juzix.wallet.App;
import com.juzix.wallet.R;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.utils.JZMnemonicUtil;
import com.juzix.wallet.utils.JZWalletUtil;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicHierarchy;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.HDUtils;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.utils.Numeric;

import java.util.List;
import java.util.Random;

class IndividualWalletService implements IIndividualWalletService {

    static final String PATH = "M/44H/266H/0H/0";

    private IndividualWalletService() {
    }

    public static IndividualWalletService getInstance(){
        return InstanceHolder.INSTANCE;
    }

    @Override
    public String generateMnemonic() {
        return JZWalletUtil.generateMnemonic();
    }

    private IndividualWalletEntity generateWallet(ECKeyPair ecKeyPair, String name, String password){
        try {
            String     filename   = JZWalletUtil.getWalletFileName(Numeric.toHexStringNoPrefix(ecKeyPair.getPublicKey()));
            WalletFile walletFile = Wallet.createLight(password, ecKeyPair);
            if (walletFile == null) {
                return null;
            }
            long time = System.currentTimeMillis();
            IndividualWalletEntity.Builder builder = new IndividualWalletEntity.Builder()
                    .uuid(walletFile.getId())
                    .key(JZWalletUtil.writeWalletFileAsString(walletFile))
                    .name(name)
                    .address(walletFile.getAddress())
                    .keystorePath(filename)
                    .createTime(time)
                    .updateTime(time)
                    .avatar(getWalletAvatar());
            return builder.build();
        }catch (Exception exp){
            exp.printStackTrace();
            return null;
        }
    }

    @Override
    public IndividualWalletEntity createWallet(String mnemonic, String name, String password) {
        try {
            // 2.生成种子
            byte[] seed = JZMnemonicUtil.generateSeed(mnemonic, null);
            // 3. 生成根Keystore root private key 树顶点的master key ；bip32
            DeterministicKey rootPrivateKey = HDKeyDerivation.createMasterPrivateKey(seed);
            // 4. 由根Keystore生成 第一个HD 钱包
            DeterministicHierarchy dh = new DeterministicHierarchy(rootPrivateKey);
            // 5. 定义父路径 H则是加强
            List<ChildNumber> parentPath = HDUtils.parsePath(PATH);
            // 6. 由父路径,派生出第一个子Keystore "new ChildNumber(0)" 表示第一个（PATH）
            DeterministicKey child = dh.deriveChild(parentPath, true, true, new ChildNumber(0));
            //7.通过Keystore生成公Keystore对
            ECKeyPair ecKeyPair = ECKeyPair.create(child.getPrivKeyBytes());

            return generateWallet(ecKeyPair, name, password);
        } catch (Exception exp) {
            return null;
        }
    }

    @Override
    public IndividualWalletEntity importKeystore(String store, String name, String password) {
        try {
            ECKeyPair ecKeyPair = JZWalletUtil.decrypt(store, password);
            if (ecKeyPair == null) {
                return null;
            }
            return generateWallet(ecKeyPair, name ,password);
        } catch (Exception exp) {
            return null;
        }
    }

    @Override
    public IndividualWalletEntity importPrivateKey(String privateKey, String name, String password) {
        if (!JZWalletUtil.isValidPrivateKey(privateKey)) {
            return null;
        }
        try {
            ECKeyPair ecKeyPair = ECKeyPair.create(Numeric.toBigIntNoPrefix(privateKey));
            if (ecKeyPair == null) {
                return null;
            }
            return generateWallet(ecKeyPair, name, password);
        } catch (Exception exp) {
            exp.printStackTrace();
            return null;
        }
    }

    @Override
    public IndividualWalletEntity importMnemonic(String mnemonic, String name, String password) {
        return createWallet(mnemonic, name, password);
    }

    @Override
    public String exportKeystore(IndividualWalletEntity wallet, String password) {
        try {
            ECKeyPair ecKeyPair = JZWalletUtil.decrypt(wallet.getKey(), password);
            if (ecKeyPair == null) {
                return "";
            }
            return wallet.getKey();
        } catch (Exception exp) {
            exp.printStackTrace();
            return "";
        }
    }

    @Override
    public String exportPrivateKey(IndividualWalletEntity wallet, String password) {
        try {
            ECKeyPair ecKeyPair = JZWalletUtil.decrypt(wallet.getKey(), password);
            if (ecKeyPair == null) {
                return "";
            }
            return Numeric.toHexStringNoPrefix(ecKeyPair.getPrivateKey());
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return "";
    }

    @Override
    public String getWalletAvatar() {
        String[] avatarArray = App.getContext().getResources().getStringArray(R.array.wallet_avatar);
        return avatarArray[new Random().nextInt(avatarArray.length)];
    }

    private static class InstanceHolder {
        private static volatile IndividualWalletService INSTANCE = new IndividualWalletService();
    }
}
