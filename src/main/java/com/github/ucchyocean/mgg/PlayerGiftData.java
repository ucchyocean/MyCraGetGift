/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.mgg;

import java.util.ArrayList;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

/**
 * 
 * @author ucchy
 */
public class PlayerGiftData {

    /** プレイヤーに表示するメッセージ */
    private String message;
    
    /** プレイヤーに送るgiftの文字列表現 */
    private String itemData;
    
    /**
     * コンストラクタ
     * @param message プレイヤーに表示するメッセージ
     * @param itemData プレイヤーに送るgiftの文字列表現
     */
    protected PlayerGiftData(String message, String itemData) {
        this.message = message;
        this.itemData = itemData;
    }

    /**
     * @return プレイヤーに表示するメッセージをかえす
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return プレイヤーに送るgiftの文字列表現をかえす
     */
    public String getItemData() {
        return itemData;
    }
    
    /**
     * @return プレイヤーに送るgiftをItemStack配列でかえす
     */
    public ArrayList<ItemStack> getItemStacks() {
        return KitParser.getInstance().parseToItemStack(itemData);
    }
    
    /**
     * コンフィグセクションから、PlayerGiftDataを生成して取得する
     * @param section コンフィグセクション
     * @return 生成されたインスタンス
     */
    protected static PlayerGiftData loadFromSection(ConfigurationSection section) {
        
        String message = section.getString("message", "");
        String itemData = section.getString("itemData", "");
        return new PlayerGiftData(message, itemData);
    }
}
