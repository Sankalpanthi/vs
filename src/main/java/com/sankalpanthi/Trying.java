package com.sankalpanthi;

import java.util.ArrayList;
import java.util.List;

import com.paymennt.crypto.bip32.Network;
import com.paymennt.crypto.bip32.wallet.AbstractWallet.Chain;

import com.paymennt.solanaj.api.rpc.Cluster;
import com.paymennt.solanaj.api.rpc.RpcException;
import com.paymennt.solanaj.api.rpc.SolanaRpcClient;
import com.paymennt.solanaj.api.ws.SolanaWebSocketClient;
import com.paymennt.solanaj.data.SolanaAccount;
import com.paymennt.solanaj.data.SolanaPublicKey;
import com.paymennt.solanaj.data.SolanaTransaction;
import com.paymennt.solanaj.program.SystemProgram;
import com.paymennt.solanaj.wallet.SolanaWallet;

public class Trying {
    // create new SolanaRpcClient, (DEVNET, TESTNET, MAINNET)
    SolanaRpcClient client = new SolanaRpcClient(Cluster.TESTNET);
    // create wallet
    SolanaWallet solanaWallet;
    /**
     * @param args 
     * @throws RpcException
     */
    public static void main(String[] args) {
        Trying obj = new Trying();
        String mnemonic = "hope sausage ritual coach aware eight bless license equip hen reduce tilt";
        String passphrase = "";
        int account = 0;
        String receiver = "2eRmECQeh9tZ1EZ4nKNti4o13rKeRzHZSbXmCodVaEyy";
        long amount = 100000;
        String res = obj.signIn(mnemonic, passphrase, account);
        if (res == "SUCCESS") {
            System.out.println("SUCCESS");
            try {
                String s = obj.startTransfer(account, receiver, amount);
                System.out.println(s);
            } catch (Exception e) {
                System.out.println("Exception: " + e);
            }
        } else
            System.out.println(res);
    }

    /**
     * @param mnemonic
     * @param passphrase: optional
     * @param acc_no:     accountnumber of wallet
     * @return
     */
    public String signIn(String mnemonic, String passphrase, int acc_no) {
        // the network, MAINNET or TESTNET
        final Network network = Network.TESTNET;
        try {
            solanaWallet = new SolanaWallet(mnemonic, passphrase, network);
        } catch (Exception e) {
            return "The Mnemonic phrase is wrong!";
        }
        if (client.getApi().getBalance(solanaWallet.getAddress(acc_no, Chain.EXTERNAL, null)) == 0)
            return "Account balance is 0";
        return "SUCCESS";
    }

    /**
     * @param acc_no
     * @param receiverAddress
     * @param amount:         amount to transfer in lamports, 1 SOL = 1000000000 lamports
     * @return
     * @throws RpcException
     */
    public String startTransfer(int acc_no,String receiverAddress,long amount) throws RpcException{
        // // SIGN AND SEND A TRANSACTION
        // create new transaction
        SolanaTransaction transaction = new SolanaTransaction();
        System.out.println("Original balance of sender: "
        + client.getApi().getBalance(solanaWallet.getAddress(0, Chain.EXTERNAL,
                null)));
System.out.println("Original balance of receiver: " +
        client.getApi().getBalance(receiverAddress));
        // create solana account, this account holds the funds that we want to transfer
        SolanaAccount account = new SolanaAccount(solanaWallet.getPrivateKey(acc_no, Chain.EXTERNAL, null));
        if(amount==0)
            return "Amount cannot be 0";
        if (client.getApi().getBalance(solanaWallet.getAddress(acc_no, Chain.EXTERNAL, null)) < amount)
            return "Amount to transfer is higher than Current Balance";
        else if (client.getApi().getBalance(solanaWallet.getAddress(acc_no, Chain.EXTERNAL, null)) < amount + 5000)
            return "Amount to transfer + transaction fees(5000 lam) is higher than Current Balance";
        try {
            // minimum amount for rent exception calculation
            long minAmtRntExp = client.getApi().getMinimumBalanceForRentExemption(((client.getApi().getAccountInfo(solanaWallet.getAddress(acc_no, Chain.EXTERNAL, null))).getValue().getData().size()));
            if (client.getApi().getBalance(receiverAddress) == 0 && amount < minAmtRntExp)
                return "Transaction leaves receiver account with a lower balance than rent-exempt minimum, the least value to transfer is "+minAmtRntExp;
            System.out.println(minAmtRntExp);
        } catch (Exception e) {
            return "The recipient address is wrong";
        }

        // define the sender and receiver public keys
    //     SolanaPublicKey fromPublicKey = account.getPublicKey();
    //     SolanaPublicKey toPublickKey = new SolanaPublicKey(receiverAddress);

    //     // add instructions to the transaction (from, to, lamports)
    //     transaction.addInstruction(SystemProgram.transfer(fromPublicKey, toPublickKey, amount));

    //     // set the recent blockhash
    //     transaction.setRecentBlockHash(client.getApi().getRecentBlockhash());

    //     // set the fee payer
    //     transaction.setFeePayer(account.getPublicKey());

    //     // sign the transaction
    //     transaction.sign(account);

    //     // publish the transaction
    //     String signature = client.getApi().sendTransaction(transaction);
    //     System.out.println(signature);
    //     List<String> myList = new ArrayList<String>();
    //     myList.add(signature);

    //     // LISTEN FOR ACCOUNT UPDATES
    //    SolanaWebSocketClient websocket = new SolanaWebSocketClient(Cluster.TESTNET);
    //    String walletAddress = receiverAddress;
    //    websocket.accountSubscribe(walletAddress, data -> {
    //        System.out.println("update received");
    //        System.out.println(client.getApi().getSignatureStatuses(myList).getValue().get(0).getConfirmations());
    //        System.out.println(client.getApi().getSignatureStatuses(myList).getValue().get(0).getConfirmationStatus());
    //        System.out.println("Changed balance of sender: "
    //                + client.getApi().getBalance(solanaWallet.getAddress(0, Chain.EXTERNAL,
    //                null)));
    //        System.out.println("changed balance of receiver: " +
    //                client.getApi().getBalance(receiverAddress));
    //    });
    
    return "";
    }
}
// 4W8TcR4z6ov5ntqrccSL3AWNqBSMMUtQyu4hB5JWmYvRmPgAD2KYKxh1GfhNMmjaRtFVXhrDUHwY9Hsc2yFBBiTC
// 88
// EmEvWoXsDfAmmoJNEbJJTvCGQdDbPGED9qiCVbHUhtee
//     public String extracted(int acc_no, String receiverAddress, long amount) throws RpcException {

//         // // get address (account, chain, index), used to receive
//         // System.out.println("Public address: " + solanaWallet.getAddress(0,
//         // Chain.EXTERNAL, null));
//         // // get private key (account, chain, index), used to sign transactions
//         // System.out.println("Private key: " + solanaWallet.getPrivateKey(0,
//         // Chain.EXTERNAL, null));
//         // // SIGN AND SEND A TRANSACTION
//         System.out.println("Original balance of sender: "
//         + client.getApi().getBalance(solanaWallet.getAddress(0, Chain.EXTERNAL,
//                 null)));
// System.out.println("Original balance of receiver: " +
//         client.getApi().getBalance(receiverAddress));
//         // // SIGN AND SEND A TRANSACTION
//         // create new transaction
//         SolanaTransaction transaction = new SolanaTransaction();

//         // create solana account, this account holds the funds that we want to transfer
//         SolanaAccount account = new SolanaAccount(solanaWallet.getPrivateKey(acc_no, Chain.EXTERNAL, null));
//         if (client.getApi().getBalance(solanaWallet.getAddress(acc_no, Chain.EXTERNAL, null)) < amount)
//             return "Amount to transfer is higher than Current Balance";
//         else if (client.getApi().getBalance(solanaWallet.getAddress(acc_no, Chain.EXTERNAL, null)) < amount + 5000)
//             return "Amount to transfer + transaction fees(5000 lam) is higher than Current Balance";
//         try {
//             // minimum amount for rent exception calculation
//             long minAmtRntExp = client.getApi().getMinimumBalanceForRentExemption(((client.getApi().getAccountInfo(solanaWallet.getAddress(acc_no, Chain.EXTERNAL, null))).getValue().getData().size()));
//             if (client.getApi().getBalance(receiverAddress) == 0 && amount < minAmtRntExp)
//                 return "Transaction leaves receiver account with a lower balance than rent-exempt minimum, the least value to transfer is "+minAmtRntExp;
//         } catch (Exception e) {
//             return "The recipient address is wrong";
//         }

//         // define the sender and receiver public keys
//         SolanaPublicKey fromPublicKey = account.getPublicKey();
//         SolanaPublicKey toPublickKey = new SolanaPublicKey(receiverAddress);

//         // add instructions to the transaction (from, to, lamports)
//         transaction.addInstruction(SystemProgram.transfer(fromPublicKey, toPublickKey, amount));

//         // set the recent blockhash
//         transaction.setRecentBlockHash(client.getApi().getRecentBlockhash());

//         // set the fee payer
//         transaction.setFeePayer(account.getPublicKey());

//         // sign the transaction
//         transaction.sign(account);

//         // publish the transaction
//         String signature = client.getApi().sendTransaction(transaction);
//     //     System.out.println(signature);
//     //     List<String> myList = new ArrayList<String>();
//     //     myList.add(signature);

//     //     // // LISTEN FOR ACCOUNT UPDATES
//     //     SolanaWebSocketClient websocket = new SolanaWebSocketClient(Cluster.TESTNET);
//     //     String walletAddress = receiverAddress;


//     //    websocket.accountSubscribe(walletAddress, data -> {
//     //     if(client.getApi().getSignatureStatuses(myList).getValue().get(0).getConfirmationStatus()==ConfirmationStatus.confirmed)
//     //     {
//     //        System.out.println("update received");
//     //        System.out.println(client.getApi().getSignatureStatuses(myList).getValue().get(0).getConfirmations());
//     //        System.out.println(client.getApi().getSignatureStatuses(myList).getValue().get(0).getConfirmationStatus());
//     //        System.out.println("Changed balance of sender: "
//     //                + client.getApi().getBalance(solanaWallet.getAddress(0, Chain.EXTERNAL,
//     //                null)));
//     //        System.out.println("changed balance of receiver: " +
//     //                client.getApi().getBalance(receiverAddress));
//     //     }
//     //    });
//     //     // int ct=0;
//     //     // while(((client.getApi().getSignatureStatuses(myList).getValue().get(0).getConfirmationStatus())!=ConfirmationStatus.finalized
//     //     //         &&
//     //     //         (client.getApi().getSignatureStatuses(myList).getValue().get(0).getConfirmationStatus())!=ConfirmationStatus.confirmed))
//     //     // {
//     //     //     // try {
//     //     //     //     Thread.sleep(1);
//     //     //     // } catch (InterruptedException e) {
//     //     //     //     e.printStackTrace();
//     //     //     // }
//     //     //     // if(++ct>30000)
//     //     //     //     return "Transaction Failed";
//     //     // }
//     //     // return "Transaction Successful\n"+"Changed balance of sender: "
//     //     //         + client.getApi().getBalance(solanaWallet.getAddress(0, Chain.EXTERNAL,
//     //     //         null))+"\nChanged balance of receiver: " +
//     //     //         client.getApi().getBalance(receiverAddress);
//         return "Hey";
//     }