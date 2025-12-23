package com.example.blockchain;

import java.util.ArrayList;
import com.google.gson.GsonBuilder;

public class ChainHub {

    public static ArrayList<Block> blockChain = new ArrayList<Block>();

    public static void main(String[] args){
        //add blocks to the blockchain ArrayList:
        blockChain.add(new Block("Hi im the first block", "0"));
        blockChain.add(new Block("Yo im the second block",blockChain.get(blockChain.size()-1).hash));
        blockChain.add(new Block("Hey im the third block",blockChain.get(blockChain.size()-1).hash));

        String blockChainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockChain);
        System.out.println(blockChainJson);
    }

    public static Boolean isChainValid(){
        Block currentBlock;
        Block previousBlock;

        for(int i=1; i < blockChain.size(); i ++){
            currentBlock = blockChain.get(i);
            previousBlock = blockChain.get(i-1);

            //compare registered hash and calculated hash:
            if(!currentBlock.hash.equals(currentBlock.calculateHash())){
                System.out.println("current Hashes not equal");
                return false;
            }

            //compare previous hash and registered previous hash
            if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
                System.out.println("Previous Hashes not equal");
                return false;
            }
        }
        return true;

        }
    }


}
