/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.mgg;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * MyCraGetGiftプラグイン
 * @author ucchy
 */
public class MyCraGetGift extends JavaPlugin implements Listener {

    protected static File pluginDataFolder;
    protected static File pluginJarFile;
    protected static PlayerGiftDataHandler handler;

    /**
     * プラグインのロード時に呼ばれるメソッド
     * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
     */
    @Override
    public void onEnable() {
        
        // 変数の初期化
        pluginDataFolder = getDataFolder();
        pluginJarFile = getFile();
        
        // giftデータのリロード
        handler = new PlayerGiftDataHandler();
        
        // messageのロード
        Messages.initialize(null);
        
        // イベントリスナーの登録
        getServer().getPluginManager().registerEvents(this, this);
    }

    /**
     * プラグインのコマンド実行時に呼ばれるメソッド
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {

        if ( args.length >= 1 && args[0].equalsIgnoreCase("reload") ) {
            // リロードコマンド
            return reloadCommand(sender, command, label, args);
            
        } else if ( args.length >= 1 && args[0].equalsIgnoreCase("check") ) {
            // チェックコマンド
            return checkCommand(sender, command, label, args);
            
        }
        
        // gift取得コマンド
        return getGiftCommand(sender, command, label, args);
    }
    
    /**
     * プレイヤーがサーバーに参加したときに呼ばれるメソッド
     * @param event 
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        
        Player player = event.getPlayer();
        
        // giftデータがない場合は、処理しないで終わる
        if ( !handler.containsName(player.getName()) ) {
            return;
        }
        
        // giftがあることをメッセージ通知する
        sendMessage(player, "playerOnJoin1");
        sendMessage(player, "playerOnJoin2");
        
        // giftデータを取得し、メッセージを表示する
        PlayerGiftData data = handler.getGiftData(player.getName());
        if ( data.getMessage().length() > 0 ) {
            sendMessage(player, "giftMessage", data.getMessage());
        }
    }

    /**
     * getgiftコマンドの実行
     * @param sender 
     * @param command 
     * @param label 
     * @param args 
     * @return
     */
    private boolean getGiftCommand(
            CommandSender sender, Command command, String label, String[] args) {
        
        // プレイヤーからの実行でなければ終了する
        if ( !(sender instanceof Player) ) {
            sender.sendMessage("このコマンドはゲーム内から実行してください");
            return true;
        }
        
        Player player = (Player)sender;
        
        // Giftが無いプレイヤーなら、Giftが無い旨のメッセージを表示して終了する
        if ( !handler.containsName(player.getName()) ) {
            sendMessage(player, "nothingGift");
            return true;
        }
        
        // giftを取得して与える
        PlayerGiftData data = handler.getGiftData(player.getName());
        ArrayList<ItemStack> items = data.getItemStacks();
        ArrayList<ItemStack> rest = new ArrayList<ItemStack>();
        PlayerInventory inv = player.getInventory();
        
        for ( ItemStack i : items ) {
            int emptySlotCount = getEmptySlotCount(inv);
            if ( emptySlotCount > 0 ) {
                inv.addItem(i);
            } else {
                rest.add(i);
            }
        }
        
        if ( rest.size() == 0 ) {
            // 全て渡しきった場合
            
            // giftを削除する
            handler.removeGiftData(player.getName());
            
            sendMessage(player, "sendCompleted");
            
        } else {
            // 渡しきれなかった場合
            
            // 渡しきれなかったアイテムを保存する
            handler.setGiftData(player.getName(), data.getMessage(), rest);

            sendMessage(player, "sendIncompleted1");
            sendMessage(player, "sendIncompleted2", rest.size());
            sendMessage(player, "sendIncompleted3");
        }
        
        return true;
    }
    
    /**
     * getgift reload コマンドの実行
     * @param sender 
     * @param command 
     * @param label 
     * @param args 
     * @return
     */
    private boolean reloadCommand(
            CommandSender sender, Command command, String label, String[] args) {
        
        // reload権限があるかどうかを確認する
        if ( !sender.hasPermission("getgift.reload") ) {
            sender.sendMessage("パーミッションが無いため、実行できません。");
            return true;
        }
        
        // giftデータのリロード
        handler = new PlayerGiftDataHandler();
        
        // messageのロード
        Messages.initialize(null);
        
        sender.sendMessage("giftデータとメッセージデータをリロードしました。");
        return true;
    }
    
    
    /**
     * getgift check コマンドの実行
     * @param sender 
     * @param command 
     * @param label 
     * @param args 
     * @return
     */
    private boolean checkCommand(
            CommandSender sender, Command command, String label, String[] args) {
        
        // check権限があるかどうかを確認する
        if ( !sender.hasPermission("getgift.check") ) {
            sender.sendMessage("パーミッションが無いため、実行できません。");
            return true;
        }
        
        // ゲーム内実行かどうかを確認する
        if ( !(sender instanceof Player) ) {
            sender.sendMessage("このコマンドはゲーム内から実行してください");
            return true;
        }
        
        // 手元のアイテムを取得する
        ItemStack item = ((Player)sender).getItemInHand();
        if ( item != null ) {
            String str = KitParser.getInstance().getItemInfo(item);
            sender.sendMessage("アイテム情報：" + ChatColor.GOLD + str);
        }
        
        return true;
    }
    
    /**
     * メッセージリソースを取得し、プレイヤーに送信する
     * @param player メッセージ送信先のプレイヤー
     * @param key メッセージキー
     * @param args メッセージの引数
     */
    private void sendMessage(Player player, String key, Object... args) {

        String msg = Messages.get(key, args);
        if ( msg.equals("") ) {
            return;
        }
        player.sendMessage(Utility.replaceColorCode(msg));
    }
    
    /**
     * インベントリの空きスロット数を数える
     * @param inv インベントリ
     * @return 空きスロット数
     */
    private int getEmptySlotCount(Inventory inv) {
        
        int count = 0;
        for ( ItemStack i : inv.getContents() ) {
            if ( i == null || i.getType() == Material.AIR ) {
                count++;
            }
        }
        return count;
    }
}
