/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.mgg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

/**
 * 
 * @author ucchy
 */
public class PlayerGiftDataHandler {

    private static final String FOLDER = "gift";
    private File folder;
    
    /**
     * コンストラクタ
     */
    public PlayerGiftDataHandler() {

        // giftフォルダが存在するか確認
        folder = new File(MyCraGetGift.pluginDataFolder, FOLDER);
        
        // フォルダが無いなら作成
        if ( !folder.exists() ) {
            folder.mkdirs();
        }
    }
    
    /**
     * 指定したプレイヤー名のgiftがあるかどうかをかえす
     * @param name プレイヤー名
     * @return giftがあるかどうか
     */
    public boolean containsName(String name) {
        
        File file = new File(folder, name + ".yml");
        return file.exists();
    }
    
    /**
     * 指定したプレイヤー名のgiftをかえす
     * @param name プレイヤー名
     * @return giftのデータ
     */
    public PlayerGiftData getGiftData(String name) {
        
        File file = new File(folder, name + ".yml");
        if ( !file.exists() ) {
            return null;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        return PlayerGiftData.loadFromSection(config);
    }
    
    /**
     * 指定したプレイヤー名のgiftを削除する
     * @param name プレイヤー名
     */
    public void removeGiftData(String name) {
        
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
