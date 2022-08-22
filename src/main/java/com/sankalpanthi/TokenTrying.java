package com.sankalpanthi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.paymennt.crypto.bip32.Network;
import com.paymennt.crypto.bip32.wallet.AbstractWallet.Chain;
import com.paymennt.crypto.bip32.wallet.key.HdPrivateKey;
import com.paymennt.solanaj.api.rpc.Cluster;
import com.paymennt.solanaj.api.rpc.SolanaRpcClient;
import com.paymennt.solanaj.data.SolanaAccount;
import com.paymennt.solanaj.data.SolanaPublicKey;
import com.paymennt.solanaj.data.SolanaTransaction;
import com.paymennt.solanaj.program.TokenProgram;
import com.paymennt.solanaj.wallet.SolanaWallet;

public class TokenTrying {
    // create new SolanaRpcClient, (DEVNET, TESTNET, MAINNET)
    SolanaRpcClient client = new SolanaRpcClient(Cluster.TESTNET);

    class Pair {
        int decimal;
        String tokenMint;

        // Constructor
        public Pair(String tokenMint, int decimal) {
            this.decimal = decimal;
            this.tokenMint = tokenMint;
        }
    }

    /**
     * @param args
     */
    public static void main(String args[]) {
        TokenTrying obj = new TokenTrying();
        String mnemonic = "hope sausage ritual coach aware eight bless license equip hen reduce tilt";
        String passphrase = "";
        int account = 0;
        int feepayer = 0;
        // the SOL address of the recipient, since u own the tokens use a diff address
        String recipient = "EjNzhxKqtVPeSx8LBFymbBo7tWSP6hjfGFPYbsf5hnsr";
        // define the token address
        // https://explorer.solana.com/address/Gh9ZwEmdLJ8DscKNTkTqPbNwLNNBjuSzaG9Vp2KGtKJr?cluster=testnet
        String tokenName = "USDC";
        double amount = 1;
        System.out.println(obj.sendToken(mnemonic, passphrase, account, feepayer, recipient, tokenName, amount));
    }

    /**
     * @param tokenMint
     * @param sourceAccount
     * @param recipient
     * @param amount
     * @return
     */
    public String signAndSendTokenTransaction( // when feepayer is the same as source account
            String tokenMint,
            SolanaAccount sourceAccount, // SOL
            String recipient, // SOL address
            long amount) {

        String myTokenAccount = client.getApi().getTokenAccount(sourceAccount.getPublicKey().toBase58(), tokenMint)
                .getAddress();
        // lets create a transaction
        SolanaTransaction transaction = new SolanaTransaction();

        // the token recipient
        String destination = client.getApi().getTokenAccount(recipient, tokenMint).getAddress();

        // recipient does not have a token address? create one
        if (destination == null) {
            List<byte[]> seeds = new ArrayList<>();
            seeds.add(new SolanaPublicKey(recipient).toByteArray());
            seeds.add(TokenProgram.PROGRAM_ID.toByteArray());
            seeds.add(new SolanaPublicKey(tokenMint).toByteArray());

            destination = SolanaPublicKey.findProgramAddress(seeds, TokenProgram.ASSOCIATED_TOKEN_PROGRAM_ID)//
                    .getAddress()//
                    .toBase58();

            transaction.addInstruction(//
                    TokenProgram.createAccount(//
                            new SolanaPublicKey(destination), //
                            sourceAccount.getPublicKey(), //
                            new SolanaPublicKey(tokenMint), //
                            new SolanaPublicKey(recipient) //

                    )//
            );
        }

        // add transfer instruction
        transaction.addInstruction(//
                TokenProgram.transfer(//
                        new SolanaPublicKey(myTokenAccount), //
                        new SolanaPublicKey(destination), //
                        amount, //
                        sourceAccount.getPublicKey() //

                )//
        );

        List<SolanaAccount> signers = new ArrayList<>();
        signers.add(sourceAccount);

        transaction.setRecentBlockHash(client.getApi().getRecentBlockhash());
        transaction.setFeePayer(sourceAccount.getPublicKey());
        transaction.sign(signers);

        return client.getApi().sendTransaction(transaction);
    }

    /**
     * @param mnemonic
     * @param passphrase
     * @param acc_no
     * @param feepayer
     * @param recipient
     * @param tokenName
     * @param amount
     * @return
     */
    public String sendToken(String mnemonic, String passphrase, int acc_no, int feepayer, String recipient,
            String tokenName, double amount) {
        HashMap<String, Pair> hm = new HashMap<>();
        hm.put("USDC", new Pair("Gh9ZwEmdLJ8DscKNTkTqPbNwLNNBjuSzaG9Vp2KGtKJr", 6));

        if (!hm.containsKey(tokenName))
            return "Token Name is not present in database";
        if (amount == 0)
            return "Amount of tokens cannot be 0";
        String tokenMint = hm.get(tokenName).tokenMint;
        long amountLong = (long) (amount * Math.pow(10, hm.get(tokenName).decimal));

        // the network, MAINNET or TESTNET
        final Network network = Network.TESTNET;
        // create wallet
        SolanaWallet solanaWallet = new SolanaWallet(mnemonic, passphrase, network);

        // your SOLANA account
        // source of USDC to transfer from
        HdPrivateKey sourcePrivateKey = solanaWallet.getPrivateKey(acc_no, Chain.EXTERNAL, null);
        SolanaAccount sourceAccount = new SolanaAccount(sourcePrivateKey);

        // you token account on the program
        // this should be 34jR1EbaDoDHr9tYuz3QR87uLGk3U95s941KtJ7hsUw6
        String myTokenAccount = client.getApi().getTokenAccount(sourceAccount.getPublicKey().toBase58(), tokenMint)
                .getAddress();

        if (myTokenAccount == null)
            return "sender does not have a " + tokenMint + " token address";
        System.out.println("Token acc -> " + myTokenAccount);
        System.out.println("Number of tokens -> " + client.getApi().getTokenAccountBalance(myTokenAccount));

        // CHecK RECEIVER ACCOUNT
        try {
            client.getApi().getBalance(recipient);
        } catch (Exception e) {
            return "The recipient address is wrong";
        }

        if (client.getApi().getTokenAccount(recipient, tokenMint).getAddress() == null &&
                client.getApi().getBalance(solanaWallet.getAddress(feepayer, Chain.EXTERNAL, null)) < 2049280)// transaction
                                                                                                              // cost
                                                                                                              // and
                                                                                                              // cost of
                                                                                                              // creating
                                                                                                              // token
                                                                                                              // account
                                                                                                              // for
                                                                                                              // recepient
            return "The fee payer does not have enough SOL. Miniumun requirement: 2049280";
        else if (client.getApi().getBalance(solanaWallet.getAddress(feepayer, Chain.EXTERNAL, null)) < 10000)// transaction
                                                                                                             // cost
            return "The fee payer does not have enough SOL. Miniumun requirement: 10000";

        // TRANSFER TOKEN PROGRAM
        if (amount > client.getApi().getTokenAccountBalance(myTokenAccount))
            return "Amount of token to transfer is higher than Current Balance of "
                    + client.getApi().getTokenAccountBalance(myTokenAccount) / Math.pow(10, hm.get(tokenName).decimal)
                    + " tokens";

        if (feepayer == acc_no) // if fee paye account number is the same as source account number
            return signAndSendTokenTransaction(tokenMint, sourceAccount, recipient, amountLong);

        // fee payer, will be used to sign the transaction as well
        HdPrivateKey feepayerPrivateKey = solanaWallet.getPrivateKey(feepayer, Chain.EXTERNAL, null);
        SolanaAccount feepayerAccount = new SolanaAccount(feepayerPrivateKey);

        System.out.println(amountLong);
        return client.getApi().signAndSendTokenTransaction(
                tokenMint, feepayerAccount, sourceAccount, recipient, amountLong);
    }
}
