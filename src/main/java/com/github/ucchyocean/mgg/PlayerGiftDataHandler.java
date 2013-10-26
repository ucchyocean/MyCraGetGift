/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.mgg;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

/**
 * 
 * @author ucchy
 */
public class PlayerGiftDataHandler {

    private static final String FOLDER = "gift";
    
    private HashMap<String, PlayerGiftData> gifts;
    private File folder;
    
    /**
     * コンストラクタ
     */
    public PlayerGiftDataHandler() {

        gifts = new HashMap<String, PlayerGiftData>();
        
        // giftフォルダが存在するか確認
        folder = new File(MyCraGetGift.pluginDataFolder, FOLDER);
        
        if ( !folder.exists() ) {
            // フォルダが無いなら作成
            folder.mkdirs();
            
        } else {
            // フォルダが有るなら、中のgiftデータをロード
            File[] yamlFiles = folder.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if ( name.endsWith(".yml") ) return true;
                    return false;
                }
            });
            
            for ( File file : yamlFiles ) {
                String filename = file.getName();
                String name = filename.substring(0, filename.indexOf(".yml"));
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                PlayerGiftData data = PlayerGiftData.loadFromSection(config);
                gifts.put(name, data);
            }
        }
    }
    
    /**
     * 指定したプレイヤー名のgiftがあるかどうかをかえす
     * @param name プレイヤー名
     * @return giftがあるかどうか
     */
    public boolean containsName(String name) {
        return gifts.containsKey(name);
    }
    
    /**
     * 指定したプレイヤー名のgiftをかえす
     * @param name プレイヤー名
     * @return giftのデータ
     */
    public PlayerGiftData getGiftData(String name) {
        return gifts.get(name);
    }
    
    /**
     * 指定したプレイヤー名のgiftを削除する
     * @param name プレイヤー名
     */
    public void removeGiftData(String name) {
        
        // データ削除
        if ( gifts.containsKey(name) ) {
            gifts.remove(name);
        }
        
        // ファイル削除
        File file = new File(folder, name + ".yml");
        if ( file.exists() ) {
            file.delete();
        }
    }
    
    /**
     * 指定したプレイヤー名のgiftを設定する
     * @param name プレイヤー名
     * @param data giftのデータ
     */
    public void setGiftData(String name, PlayerGiftData data) {
        
        // データ更新
        gifts.put(name, data);
        
        // ファイル更新
        File file = new File(folder, name + ".yml");
        String message = data.getMessage();
        String itemData = data.getItemData();
        
        YamlConfiguration config = new YamlConfiguration();
        config.set("message", message);
        config.set("itemData", itemData);
        
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 指定されたプレイヤー名のgiftを設定する
     * @param name プレイヤー名
     * @param message メッセージ
     * @param items アイテム配列
     */
    public void setGiftData(String name, String message, ArrayList<ItemStack> items) {
        
        String itemData = KitParser.getInstance().convertItemArrayToItemString(items);
        PlayerGiftData data = new PlayerGiftData(message, itemData);
        setGiftData(name, data);
    }
}
