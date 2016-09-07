package com.github.unchama.seichiassist.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.github.unchama.seichiassist.SeichiAssist;
import com.github.unchama.seichiassist.util.ExperienceManager;
import com.github.unchama.seichiassist.util.Util;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class MenuInventoryData {
	//1ページ目メニュー
	public static Inventory getMenuData(Player p){
		//プレイヤーを取得
		Player player = p.getPlayer();
		//UUID取得
		UUID uuid = player.getUniqueId();
		//プレイヤーデータ
		PlayerData playerdata = SeichiAssist.playermap.get(uuid);
		//念のためエラー分岐
		if(playerdata == null){
			player.sendMessage(ChatColor.RED + "playerdataがありません。管理者に報告してください");
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "SeichiAssist[木の棒メニューOPEN処理]でエラー発生");
			Bukkit.getLogger().warning(player.getName() + "のplayerdataがありません。開発者に報告してください");
			return null;
		}

		Inventory inventory = Bukkit.getServer().createInventory(null,4*9,ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "木の棒メニュー");
		ItemStack itemstack;
		ItemMeta itemmeta;
		SkullMeta skullmeta;
		List<String> lore = new ArrayList<String>();



		int prank = playerdata.calcPlayerRank(player);
		itemstack = new ItemStack(Material.SKULL_ITEM,1);
		skullmeta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
		itemstack.setDurability((short) 3);
		skullmeta.addEnchant(Enchantment.DIG_SPEED, 100, false);
		skullmeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + playerdata.name + "の統計データ");
		lore.clear();
		lore.addAll(Arrays.asList(ChatColor.RESET + "" +  ChatColor.AQUA + "整地レベル:" + playerdata.level
				, ChatColor.RESET + "" +  ChatColor.AQUA + "次のレベルまで:" + (SeichiAssist.levellist.get(playerdata.level).intValue() - playerdata.totalbreaknum)
				, ChatColor.RESET + "" +  ChatColor.DARK_GRAY + "パッシブスキル効果："
				, ChatColor.RESET + "" +  ChatColor.DARK_GRAY + "1ブロック整地ごとに"
				, ChatColor.RESET + "" +  ChatColor.DARK_GRAY + "10%の確率で"
				, ChatColor.RESET + "" +  ChatColor.DARK_GRAY + playerdata.dispPassiveExp() + "の経験値を獲得"
				, ChatColor.RESET + "" +  ChatColor.AQUA + "総整地量:" + playerdata.totalbreaknum
				, ChatColor.RESET + "" +  ChatColor.GOLD + "ランキング：" + prank + "位" + ChatColor.RESET + "" +  ChatColor.GRAY + "(" + SeichiAssist.ranklist.size() +"人中)"
				));
		if(prank > 1){
			RankData rankdata = SeichiAssist.ranklist.get(prank-2);
			lore.add(ChatColor.RESET + "" +  ChatColor.AQUA + (prank-1) + "位("+ rankdata.name +")との差：" + (rankdata.totalbreaknum - playerdata.totalbreaknum));
		}
		lore.add(ChatColor.RESET + "" +  ChatColor.GRAY + "総ログイン時間：" + Util.toTimeString(Util.toTickSecond(playerdata.playtick)));
		lore.add(ChatColor.RESET + "" +  ChatColor.DARK_GRAY + "※1分毎に更新");
		lore.add(ChatColor.RESET + "" +  ChatColor.GREEN + "統計データは");
		lore.add(ChatColor.RESET + "" +  ChatColor.GREEN + "第1,第2サバイバルサーバー間で");
		lore.add(ChatColor.RESET + "" +  ChatColor.GREEN + "共有されます");

		skullmeta.setLore(lore);
		skullmeta.setOwner(playerdata.name);
		itemstack.setItemMeta(skullmeta);
		inventory.setItem(0,itemstack);

		//採掘速度上昇効果のトグルボタン
		itemstack = new ItemStack(Material.DIAMOND_PICKAXE,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.DIAMOND_PICKAXE);
		itemmeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "採掘速度上昇効果");
		itemmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itemstack.setItemMeta(EFButtonMeta(playerdata,itemmeta));
		inventory.setItem(1,itemstack);

		// ver0.3.2 四次元ポケットOPEN
		itemstack = new ItemStack(Material.ENDER_PORTAL_FRAME,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.ENDER_PORTAL_FRAME);
		itemmeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "四次元ポケットを開く");
		lore.clear();
		if( playerdata.level < SeichiAssist.config.getPassivePortalInventorylevel()){
			lore.add(ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "整地レベルが"+SeichiAssist.config.getPassivePortalInventorylevel()+ "以上必要です");
		}else{
			lore.add(ChatColor.RESET + "" +  ChatColor.GRAY + "ポケットサイズ:" + playerdata.inventory.getSize() + "スタック");
			lore.add(ChatColor.RESET + "" +  ChatColor.DARK_GREEN + "" + ChatColor.UNDERLINE + "クリックで開く");
		}
		lore.add(ChatColor.RESET + "" +  ChatColor.DARK_GRAY + "※四次元ポケットの中身は");
		lore.add(ChatColor.RESET + "" +  ChatColor.DARK_GRAY + "第1,第2サバイバルサーバー間で");
		lore.add(ChatColor.RESET + "" +  ChatColor.DARK_GRAY + "共有されます");
		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(21,itemstack);

		// どこでもエンダーチェスト
		itemstack = new ItemStack(Material.ENDER_CHEST,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.ENDER_CHEST);
		itemmeta.setDisplayName(ChatColor.DARK_PURPLE + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "どこでもエンダーチェスト");
		lore.clear();
		if( playerdata.level < SeichiAssist.config.getPassivePortalInventorylevel()){
			lore.add(ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "整地レベルが"+SeichiAssist.config.getDokodemoEnderlevel()+ "以上必要です");
		}else{
			lore.add(ChatColor.RESET + "" +  ChatColor.DARK_GREEN + "" + ChatColor.UNDERLINE + "クリックで開く");
		}
		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(22,itemstack);

		// ver0.3.2 保護設定コマンド
		itemstack = new ItemStack(Material.GOLD_AXE,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.GOLD_AXE);
		itemmeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "保護領域の申請");
		itemmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		lore.clear();
		Selection selection = Util.getWorldEdit().getSelection(player);
		if (selection == null) {
			lore.addAll(Arrays.asList(ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "範囲指定されてません"
					, ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "先に木の斧で2か所クリックしてネ"
					, ChatColor.DARK_GRAY + "Y座標は自動で全範囲保護されます"
					, ChatColor.RESET + "" +  ChatColor.YELLOW + "" + "A new region has been claimed"
					, ChatColor.RESET + "" +  ChatColor.YELLOW + "" + "named '" + player.getName() + "_" + playerdata.rgnum + "'."
					, ChatColor.RESET + "" +  ChatColor.GRAY + "と出れば、保護の設定が完了しています"
					, ChatColor.RESET + "" +  ChatColor.RED + "赤色で別の英文が出た場合"
					, ChatColor.RESET + "" +  ChatColor.GRAY + "保護の設定に失敗しています"
					, ChatColor.RESET + "" +  ChatColor.GRAY + "別の保護と被ってないか等ご確認の上"
					, ChatColor.RESET + "" +  ChatColor.GRAY + "始めからやり直してください"
					));
		}else{
			itemmeta.addEnchant(Enchantment.DIG_SPEED, 100, false);
			lore.addAll(Arrays.asList(ChatColor.DARK_GREEN + "" + ChatColor.UNDERLINE + "範囲指定されています"
					, ChatColor.DARK_GREEN + "" + ChatColor.UNDERLINE + "クリックすると保護を申請します"
					, ChatColor.DARK_GRAY + "Y座標は自動で全範囲保護されます"
					, ChatColor.RESET + "" +  ChatColor.YELLOW + "" + "A new region has been claimed"
					, ChatColor.RESET + "" +  ChatColor.YELLOW + "" + "named '" + player.getName() + "_" + playerdata.rgnum + "'."
					, ChatColor.RESET + "" +  ChatColor.GRAY + "と出れば、保護の設定が完了しています"
					, ChatColor.RESET + "" +  ChatColor.RED + "赤色で別の英文が出た場合"
					, ChatColor.RESET + "" +  ChatColor.GRAY + "保護の設定に失敗しています"
					, ChatColor.RESET + "" +  ChatColor.GRAY + "別の保護と被ってないか等ご確認の上"
					, ChatColor.RESET + "" +  ChatColor.GRAY + "始めからやり直してください"
					));
		}
		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(4,itemstack);

		// MineStackを開く
		itemstack = new ItemStack(Material.CHEST,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.CHEST);
		itemmeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "MineStack機能");
		lore.clear();
		lore.addAll(Arrays.asList(ChatColor.RESET + "" +  ChatColor.GREEN + "説明しよう!MineStackとは!"
				, ChatColor.RESET + "" + "主要ブロックを無限にスタック出来る!"
				, ChatColor.RESET + "" + "スタックしたアイテムは"
				, ChatColor.RESET + "" + "ここから取り出せるゾ!"
				));
		if( playerdata.level < SeichiAssist.config.getMineStacklevel(1)){
			lore.add(ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "整地レベルが"+SeichiAssist.config.getMineStacklevel(1)+ "以上必要です");
		}else{
			lore.add(ChatColor.RESET + "" +  ChatColor.DARK_GREEN + "" + ChatColor.UNDERLINE + "クリックで開く");
		}
		lore.add(ChatColor.RESET + "" +  ChatColor.DARK_GRAY + "※スタックしたブロックは");
		lore.add(ChatColor.RESET + "" +  ChatColor.DARK_GRAY + "第1,第2サバイバルサーバー間で");
		lore.add(ChatColor.RESET + "" +  ChatColor.DARK_GRAY + "共有されます");
		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(24,itemstack);

		/*
		 * ここまでadd.loreに変更済み
		 * 以下ボタンにadd.lore使う場合は追加行より上をすべてadd.loreに変更しないとエラー吐きます
		 */

		// 2ページ目を開く
		itemstack = new ItemStack(Material.SKULL_ITEM,1);
		skullmeta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
		itemstack.setDurability((short) 3);
		skullmeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "2ページ目へ");
		lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックで移動"
				);
		skullmeta.setLore(lore);
		skullmeta.setOwner("MHF_ArrowRight");
		itemstack.setItemMeta(skullmeta);
		inventory.setItem(35,itemstack);

		// 整地神番付を開く
		itemstack = new ItemStack(Material.COOKIE,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.COOKIE);
		itemmeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "整地神ランキングを見る");
		lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックで開く"
				);
		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(33,itemstack);

		//運営からの詫びガチャ配布ボタン
		itemstack = new ItemStack(Material.SKULL_ITEM,1);
		skullmeta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
		itemstack.setDurability((short) 3);
		skullmeta.setDisplayName(ChatColor.DARK_AQUA + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "運営からのガチャ券を受け取る");
		skullmeta.setLore(SorryGachaGetButtonLore(playerdata));
		skullmeta.setOwner("whitecat_haru");
		itemstack.setItemMeta(skullmeta);
		inventory.setItem(29,itemstack);

		// ゴミ箱を開く
		itemstack = new ItemStack(Material.BUCKET,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.BUCKET);
		itemmeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "ゴミ箱を開く");
		lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.GREEN + "不用品の大量処分にドウゾ！"
				, ChatColor.RESET + "" + ChatColor.RED + "復活しないので取扱注意"
				, ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックで開く"
				);
		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(26,itemstack);

		// ver0.3.2 homeコマンド
		itemstack = new ItemStack(Material.COMPASS,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.COMPASS);
		itemmeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "ホームポイントにワープ");
		lore = Arrays.asList(ChatColor.RESET + "" + ChatColor.GRAY + "あらかじめ設定した"
				, ChatColor.RESET + "" + ChatColor.GRAY + "ホームポイントにワープします"
				, ChatColor.RESET + "" + ChatColor.DARK_GRAY + "うまく機能しない時は"
				, ChatColor.RESET + "" + ChatColor.DARK_GRAY + "再接続してみてください"
				, ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックでワープ"
				);
		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(18,itemstack);

		// ver0.3.2 sethomeコマンド
		itemstack = new ItemStack(Material.BED,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.BED);
		itemmeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "ホームポイントを設定");
		lore = Arrays.asList(ChatColor.RESET + "" + ChatColor.GRAY + "現在位置をホームポイント"
				, ChatColor.RESET + "" + ChatColor.GRAY + "として設定します"
				, ChatColor.RESET + "" + ChatColor.	DARK_GRAY + "※上書きされます"
				, ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックで設定"
				);
		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(19,itemstack);


		// ver0.3.2 //wandコマンド
		itemstack = new ItemStack(Material.WOOD_AXE,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.WOOD_AXE);
		itemmeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "保護設定用の木の斧を召喚");
		itemmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックすると召喚されます"
				, ChatColor.RESET + "" + ChatColor.DARK_GRAY + "※インベントリを空けておこう"
				, ChatColor.RESET + "" +  ChatColor.DARK_GREEN + "" + ChatColor.UNDERLINE + "保護のかけ方"
				, ChatColor.RESET + "" +  ChatColor.GREEN + "①召喚された斧を手に持ちます"
				, ChatColor.RESET + "" +  ChatColor.GREEN + "②保護したい領域の一方の角を" + ChatColor.YELLOW + "左" + ChatColor.GREEN + "クリック"
				, ChatColor.RESET + "" +  ChatColor.GREEN + "③もう一方の対角線上の角を" + ChatColor.RED + "右" + ChatColor.GREEN + "クリック"
				, ChatColor.RESET + "" +  ChatColor.GREEN + "④メニューの" + ChatColor.RESET + "" +  ChatColor.YELLOW + "金の斧" + ChatColor.RESET + "" +  ChatColor.GREEN + "をクリック"
				);
		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(3,itemstack);

		// ver0.3.2 保護リスト表示
		itemstack = new ItemStack(Material.STONE_AXE,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.STONE_AXE);
		itemmeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "保護リストを表示");
		itemmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックすると表示"
				, ChatColor.RESET + "" +  ChatColor.GRAY + "現在あなたが保護している"
				, ChatColor.RESET + "" +  ChatColor.GRAY + "土地の一覧を表示します"
				, ChatColor.RESET + "" +  ChatColor.RED + "" + ChatColor.UNDERLINE + "/rg remove 保護名"
				, ChatColor.RESET + "" +  ChatColor.GRAY + "で保護の削除が出来ます"
				, ChatColor.RESET + "" +  ChatColor.RED + "" + ChatColor.UNDERLINE + "/rg addmember 保護名 プレイヤー名"
				, ChatColor.RESET + "" +  ChatColor.GRAY + "で該当保護にメンバーを追加出来ます"
				, ChatColor.RESET + "" +  ChatColor.RED + "" + ChatColor.UNDERLINE + "/rg removemember 保護名 プレイヤー名"
				, ChatColor.RESET + "" +  ChatColor.GRAY + "で該当保護のメンバーを削除出来ます"
				, ChatColor.RESET + "" +  ChatColor.DARK_GRAY + "その他のコマンドはWikiで確認して下さい"
				);
		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(5,itemstack);


		// ver0.3.2 hubコマンド
		itemstack = new ItemStack(Material.NETHER_STAR,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.NETHER_STAR);
		itemmeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "ロビーサーバーへ移動");
		lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックすると移動します"
				);
		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(7,itemstack);

		// ver0.3.2 /spawnコマンド実行
		itemstack = new ItemStack(Material.BEACON,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.BEACON);
		itemmeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "スポーンワールドへワープ");
		lore = Arrays.asList(ChatColor.RESET + "" + ChatColor.GRAY + "・メインワールド"
				, ChatColor.RESET + "" + ChatColor.GRAY + "・資源ワールド"
				, ChatColor.RESET + "" + ChatColor.GRAY + "・整地ワールド"
				, ChatColor.RESET + "" + ChatColor.GRAY + "・ロビーサーバー"
				, ChatColor.RESET + "" + ChatColor.GRAY + "・第1,第2サバイバルサーバー"
				, ChatColor.RESET + "" + ChatColor.GRAY + "間を移動する時に使います"
				, ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックするとワープします");
		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(8,itemstack);

		//スキル一覧を開く
		itemstack = new ItemStack(Material.ENCHANTED_BOOK,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.ENCHANTED_BOOK);
		itemmeta.addEnchant(Enchantment.DIG_SPEED, 100, false);
		itemmeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "整地スキルブック");
		lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.GRAY + "整地に便利なスキルを使用できるゾ"
										, ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックでスキル一覧を開く");
		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(13,itemstack);

		//ガチャ券受け取りボタン
		itemstack = new ItemStack(Material.SKULL_ITEM,1);
		itemstack.setDurability((short) 3);
		skullmeta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
		skullmeta.setDisplayName(ChatColor.DARK_AQUA + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "ガチャ券を受け取る");
		skullmeta.setLore(GachaGetButtonLore(playerdata));
		skullmeta.setOwner("unchama");
		itemstack.setItemMeta(skullmeta);
		inventory.setItem(27,itemstack);

		//ガチャ券受け取り方法選択ボタン
		itemstack = new ItemStack(Material.STONE_BUTTON,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.STONE_BUTTON);
		itemmeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "ガチャ券受け取り方法");
		if(playerdata.gachaflag){
			lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.GREEN + "毎分受け取ります"
					, ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックで変更"
					);
		}else {
			lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.RED + "後でまとめて受け取ります"
					, ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックで変更"
					);
		}
		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(28,itemstack);

		return inventory;
	}
	//2ページメニュー
	public static Inventory getMenuData2(Player p){
		//プレイヤーを取得
		Player player = p.getPlayer();

		//UUID取得
		UUID uuid = player.getUniqueId();
		//プレイヤーデータ
		PlayerData playerdata = SeichiAssist.playermap.get(uuid);

		//経験値変更用のクラスを設定
		ExperienceManager expman = new ExperienceManager(player);

		Inventory inventory = Bukkit.getServer().createInventory(null,4*9,ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "木の棒メニュー");
		ItemStack itemstack;
		ItemMeta itemmeta;
		SkullMeta skullmeta;
		List<String> lore = new ArrayList<String>();

		// 自分の頭召喚
		itemstack = new ItemStack(Material.SKULL_ITEM,1);
		skullmeta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
		itemstack.setDurability((short) 3);
		skullmeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "自分の頭を召喚");
		lore.clear();
		lore.addAll(Arrays.asList(ChatColor.RESET + "" +  ChatColor.GRAY + "経験値10000を消費して"
				, ChatColor.RESET + "" +  ChatColor.GRAY + "自分の頭を召喚します"
				, ChatColor.RESET + "" +  ChatColor.GRAY + "装飾用にドウゾ！"
				));
		if(expman.hasExp(10000)){
			lore.add(ChatColor.RESET + "" +  ChatColor.DARK_GREEN + "" + ChatColor.UNDERLINE + "クリックで召喚");
		}else{
			lore.add(ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "経験値が足りません");
		}
		skullmeta.setLore(lore);
		skullmeta.setOwner("MHF_Villager");
		itemstack.setItemMeta(skullmeta);
		inventory.setItem(12,itemstack);

		//死亡メッセージ表示のトグルボタン
		itemstack = new ItemStack(Material.FLINT_AND_STEEL,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.FLINT_AND_STEEL);
		itemmeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "死亡メッセージ表示切替");
		itemstack.setItemMeta(dispKillLogToggleMeta(playerdata,itemmeta));
		inventory.setItem(14,itemstack);

		//死亡メッセージ表示のトグルボタン
		itemstack = new ItemStack(Material.IRON_SWORD,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.IRON_SWORD);
		itemmeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "PvP切替");
		itemmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itemstack.setItemMeta(dispPvPToggleMeta(playerdata,itemmeta));
		inventory.setItem(15,itemstack);

		/*
		 * ここまでadd.loreに変更済み
		 * 以下ボタンにadd.lore使う場合は追加行より上をすべてadd.loreに変更しないとエラー吐きます
		 */

		// 1ページ目を開く
		itemstack = new ItemStack(Material.SKULL_ITEM,1);
		skullmeta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
		itemstack.setDurability((short) 3);
		skullmeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "1ページ目へ");
		lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックで移動"
				);
		skullmeta.setLore(lore);
		skullmeta.setOwner("MHF_ArrowLeft");
		itemstack.setItemMeta(skullmeta);
		inventory.setItem(27,itemstack);


		// farmassist toggleコマンド
		itemstack = new ItemStack(Material.WHEAT,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.SEEDS);
		itemmeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "FarmAssist機能");
		lore = Arrays.asList(ChatColor.RESET + "" + ChatColor.GREEN + "ONにすると…"
				, ChatColor.RESET + "" + ChatColor.GRAY + "作物収穫時、手持ちの種や"
				, ChatColor.RESET + "" + ChatColor.GRAY + "苗を自動で植えてくれます"
				, ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックでON,OFFを変更"
				);
		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(9,itemstack);

		// treeassist toggleコマンド
		itemstack = new ItemStack(Material.LOG,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.SAPLING);
		itemmeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "TreeAssist機能");
		lore = Arrays.asList(ChatColor.RESET + "" + ChatColor.GREEN + "ONにすると…"
				, ChatColor.RESET + "" + ChatColor.GRAY + "木の根元を切った時、自動で"
				, ChatColor.RESET + "" + ChatColor.GRAY + "木こり&苗木を植えてくれます"
				, ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックでON,OFFを変更"
				);
		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(10,itemstack);

		// ver0.3.2 wikiページ表示
		itemstack = new ItemStack(Material.BOOK,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.BOOK);
		itemmeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "公式Wikiにアクセス");
		lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.GREEN + "鯖内の「困った」は公式Wikiで解決！"
				, ChatColor.RESET + "" + ChatColor.DARK_GRAY + "クリックするとチャット欄に"
				, ChatColor.RESET + "" + ChatColor.DARK_GRAY + "URLが表示されますので"
				, ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Tキーを押してから"
				, ChatColor.RESET + "" + ChatColor.DARK_GRAY + "そのURLをクリックしてください"
				);
		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(0,itemstack);

		// ver0.3.2 運営方針とルールページを表示
		itemstack = new ItemStack(Material.PAPER,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.PAPER);
		itemmeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "運営方針とルールを確認");
		lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.GREEN + "当鯖で遊ぶ前に確認してネ！"
				, ChatColor.RESET + "" + ChatColor.DARK_GRAY + "クリックするとチャット欄に"
				, ChatColor.RESET + "" + ChatColor.DARK_GRAY + "URLが表示されますので"
				, ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Tキーを押してから"
				, ChatColor.RESET + "" + ChatColor.DARK_GRAY + "そのURLをクリックしてください"
				);
		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(1,itemstack);

		// ver0.3.2 鯖Mapを表示
		itemstack = new ItemStack(Material.MAP,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.MAP);
		itemmeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "鯖Mapを見る");
		lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.GREEN + "webブラウザから鯖Mapを閲覧出来ます"
				, ChatColor.RESET + "" +  ChatColor.GREEN + "他人の居場所や保護の場所を確認出来ます"
				, ChatColor.RESET + "" + ChatColor.DARK_GRAY + "クリックするとチャット欄に"
				, ChatColor.RESET + "" + ChatColor.DARK_GRAY + "URLが表示されますので"
				, ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Tキーを押してから"
				, ChatColor.RESET + "" + ChatColor.DARK_GRAY + "そのURLをクリックしてください"
				);
		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(2,itemstack);

		// ver0.3.2 掲示板を表示
		itemstack = new ItemStack(Material.SIGN,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.SIGN);
		itemmeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "掲示板を見る");
		lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.GREEN + "管理人へのお問い合わせは"
				, ChatColor.RESET + "" +  ChatColor.GREEN + "掲示板に書き込みをｵﾈｶﾞｲｼﾅｽ"
				, ChatColor.RESET + "" + ChatColor.DARK_GRAY + "クリックするとチャット欄に"
				, ChatColor.RESET + "" + ChatColor.DARK_GRAY + "URLが表示されますので"
				, ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Tキーを押してから"
				, ChatColor.RESET + "" + ChatColor.DARK_GRAY + "そのURLをクリックしてください"
				);
		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(3,itemstack);

		// ver0.3.2 投票ページ表示
		itemstack = new ItemStack(Material.BOOK_AND_QUILL,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.BOOK_AND_QUILL);
		itemmeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "投票ページにアクセス");
		lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.GREEN + "投票すると様々な特典が！"
				, ChatColor.RESET + "" +  ChatColor.GREEN + "1日1回投票出来ます"
				, ChatColor.RESET + "" + ChatColor.DARK_GRAY + "クリックするとチャット欄に"
				, ChatColor.RESET + "" + ChatColor.DARK_GRAY + "URLが表示されますので"
				, ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Tキーを押してから"
				, ChatColor.RESET + "" + ChatColor.DARK_GRAY + "そのURLをクリックしてください"
				);
		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(4,itemstack);



		return inventory;
	}
	//スキルメニュー
	public static Inventory getSkillMenuData(Player p){

		Inventory inventory = Bukkit.getServer().createInventory(null,4*9,ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "整地スキル選択");
		ItemStack itemstack;
		ItemMeta itemmeta;
		SkullMeta skullmeta;
		List<String> lore = new ArrayList<String>();

		// 1ページ目を開く
		itemstack = new ItemStack(Material.SKULL_ITEM,1);
		skullmeta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
		itemstack.setDurability((short) 3);
		skullmeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "ホームへ");
		lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックで移動"
				);
		skullmeta.setLore(lore);
		skullmeta.setOwner("MHF_ArrowLeft");
		itemstack.setItemMeta(skullmeta);
		inventory.setItem(27,itemstack);

		itemstack = new ItemStack(Material.COAL_ORE,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.COAL_ORE);
		itemmeta.addEnchant(Enchantment.DIG_SPEED, 100, false);
		itemmeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "デュアルブレイク");
		lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.GREEN + "1×2マス破壊"
										, ChatColor.RESET + "" +  ChatColor.DARK_GRAY + "クールダウン：0秒"
										, ChatColor.RESET + "" +  ChatColor.DARK_GREEN + "必要整地レベル：" + SeichiAssist.config.getDualBreaklevel()
										, ChatColor.RESET + "" +  ChatColor.BLUE + "消費経験値：1"
										, ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックでセット");
		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(0,itemstack);


		itemstack = new ItemStack(Material.IRON_ORE,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.COAL_ORE);
		itemmeta.addEnchant(Enchantment.DIG_SPEED, 100, false);
		itemmeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "トリアルブレイク");
		lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.GREEN + "3×2マス破壊"
										, ChatColor.RESET + "" +  ChatColor.DARK_GRAY + "クールダウン：0秒"
										, ChatColor.RESET + "" +  ChatColor.DARK_GREEN + "必要整地レベル："  + SeichiAssist.config.getTrialBreaklevel()
										, ChatColor.RESET + "" +  ChatColor.BLUE + "消費経験値：3"
										, ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックでセット");
		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(1,itemstack);


		itemstack = new ItemStack(Material.GOLD_ORE,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.COAL_ORE);
		itemmeta.addEnchant(Enchantment.DIG_SPEED, 100, false);
		itemmeta.setDisplayName(ChatColor.RED + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "エクスプロージョン");
		lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.GREEN + "3×3×3マス破壊"
										, ChatColor.RESET + "" +  ChatColor.DARK_GRAY + "クールダウン：0秒"
										, ChatColor.RESET + "" +  ChatColor.DARK_GREEN + "必要整地レベル：" + SeichiAssist.config.getExplosionlevel()
										, ChatColor.RESET + "" +  ChatColor.BLUE + "消費経験値：15"
										, ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックでセット");
		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(2,itemstack);

		itemstack = new ItemStack(Material.PACKED_ICE,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.PACKED_ICE);
		itemmeta.addEnchant(Enchantment.DIG_SPEED, 100, false);
		itemmeta.setDisplayName(ChatColor.AQUA + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "フリーズ");
		/*lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.GREEN + "16×16×256マス破壊"
										, ChatColor.RESET + "" +  ChatColor.DARK_GREEN + "必要整地レベル：" + config.getGravitylevel()
										, ChatColor.RESET + "" +  ChatColor.BLUE + "消費経験値：2000");*/

		lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.RED + "未実装"
				, ChatColor.RESET + "" +  ChatColor.GREEN + "水を凍らせる"
				, ChatColor.RESET + "" +  ChatColor.DARK_GREEN + "必要整地レベル：35"
				, ChatColor.RESET + "" +  ChatColor.BLUE + "消費経験値：???"
				, ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックでセット");
		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(3,itemstack);


		itemstack = new ItemStack(Material.REDSTONE_ORE,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.REDSTONE_ORE);
		itemmeta.addEnchant(Enchantment.DIG_SPEED, 100, false);
		itemmeta.setDisplayName(ChatColor.WHITE + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "サンダーストーム");
		lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.GREEN + "3×3×3マス破壊×5"
										, ChatColor.RESET + "" +  ChatColor.DARK_GRAY + "クールダウン：1秒"
										, ChatColor.RESET + "" +  ChatColor.DARK_GREEN + "必要整地レベル：" + SeichiAssist.config.getThunderStormlevel()
										, ChatColor.RESET + "" +  ChatColor.BLUE + "消費経験値：40"
										, ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックでセット");
		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(4,itemstack);


		itemstack = new ItemStack(Material.MAGMA,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.MAGMA);
		itemmeta.addEnchant(Enchantment.DIG_SPEED, 100, false);
		itemmeta.setDisplayName(ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "メテオフリーズ");
		lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.RED + "未実装"
				, ChatColor.RESET + "" +  ChatColor.GREEN + "溶岩を固める"
				, ChatColor.RESET + "" +  ChatColor.DARK_GREEN + "必要整地レベル：45"
				, ChatColor.RESET + "" +  ChatColor.BLUE + "消費経験値：???"
				, ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックでセット");
		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(5,itemstack);


		itemstack = new ItemStack(Material.LAPIS_ORE,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.LAPIS_ORE);
		itemmeta.addEnchant(Enchantment.DIG_SPEED, 100, false);
		itemmeta.setDisplayName(ChatColor.AQUA + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "ブリザード");
		lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.GREEN + "7×7×5マス破壊"
										, ChatColor.RESET + "" +  ChatColor.DARK_GRAY + "クールダウン：1.5秒"
										, ChatColor.RESET + "" +  ChatColor.DARK_GREEN + "必要整地レベル：" + SeichiAssist.config.getBlizzardlevel()
										, ChatColor.RESET + "" +  ChatColor.BLUE + "消費経験値：70"
										, ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックでセット");

		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(6,itemstack);


		itemstack = new ItemStack(Material.ARROW,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.ARROW);
		itemmeta.addEnchant(Enchantment.DIG_SPEED, 100, false);
		itemmeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "エビフライ");
		lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.RED + "未実装"
				, ChatColor.RESET + "" +  ChatColor.GREEN + "遠距離3*3*3"
		, ChatColor.RESET + "" +  ChatColor.DARK_GREEN + "必要整地レベル：55"
		, ChatColor.RESET + "" +  ChatColor.BLUE + "消費経験値：???"
		, ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックでセット");

		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(7,itemstack);

		itemstack = new ItemStack(Material.EMERALD_ORE,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.EMERALD_ORE);
		itemmeta.addEnchant(Enchantment.DIG_SPEED, 100, false);
		itemmeta.setDisplayName(ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "メテオ");
		lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.GREEN + "9*9*7マス破壊"
										, ChatColor.RESET + "" +  ChatColor.DARK_GRAY + "クールダウン：2.5秒"
										, ChatColor.RESET + "" +  ChatColor.DARK_GREEN + "必要整地レベル：" + SeichiAssist.config.getMeteolevel()
										, ChatColor.RESET + "" +  ChatColor.BLUE + "消費経験値：100"
										, ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックでセット");

		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(8,itemstack);


		itemstack = new ItemStack(Material.DIAMOND_ORE,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.DIAMOND_ORE);
		itemmeta.addEnchant(Enchantment.DIG_SPEED, 100, false);
		itemmeta.setDisplayName(ChatColor.DARK_GRAY + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "グラビティ");
		lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.RED + "未実装"
				, ChatColor.RESET + "" +  ChatColor.GREEN + "11*11*9"
		, ChatColor.RESET + "" +  ChatColor.DARK_GREEN + "必要整地レベル：70"
		, ChatColor.RESET + "" +  ChatColor.BLUE + "消費経験値：???"
		, ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックでセット");

		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(9,itemstack);

		itemstack = new ItemStack(Material.SNOW_BALL,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.SNOW_BALL);
		itemmeta.addEnchant(Enchantment.DIG_SPEED, 100, false);
		itemmeta.setDisplayName(ChatColor.DARK_AQUA + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "アブソリュート");
		lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.RED + "未実装"
				, ChatColor.RESET + "" +  ChatColor.GREEN + "遠距離7*7*5"
		, ChatColor.RESET + "" +  ChatColor.DARK_GREEN + "必要整地レベル：75"
		, ChatColor.RESET + "" +  ChatColor.BLUE + "消費経験値：???"
		, ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックでセット");

		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(10,itemstack);


		itemstack = new ItemStack(Material.OBSIDIAN,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.OBSIDIAN);
		itemmeta.addEnchant(Enchantment.DIG_SPEED, 100, false);
		itemmeta.setDisplayName(ChatColor.WHITE + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "ブラックホール");
		lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.RED + "未実装"
				, ChatColor.RESET + "" +  ChatColor.GREEN + "13*13*11"
		, ChatColor.RESET + "" +  ChatColor.DARK_GREEN + "必要整地レベル：80"
		, ChatColor.RESET + "" +  ChatColor.BLUE + "消費経験値：???"
		, ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックでセット");

		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(11,itemstack);


		itemstack = new ItemStack(Material.FIREBALL,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.FIREBALL);
		itemmeta.addEnchant(Enchantment.DIG_SPEED, 100, false);
		itemmeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "Air-K");
		lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.RED + "未実装"
				, ChatColor.RESET + "" +  ChatColor.GREEN + "遠距離9*9*7"
		, ChatColor.RESET + "" +  ChatColor.DARK_GREEN + "必要整地レベル：85"
		, ChatColor.RESET + "" +  ChatColor.BLUE + "消費経験値：???"
		, ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックでセット");

		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(12,itemstack);

		itemstack = new ItemStack(Material.GRASS,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.GRASS);
		itemmeta.addEnchant(Enchantment.DIG_SPEED, 100, false);
		itemmeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "普通のパンチ");
		lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.RED + "未実装"
				, ChatColor.RESET + "" +  ChatColor.GREEN + "15*15*13"
		, ChatColor.RESET + "" +  ChatColor.DARK_GREEN + "必要整地レベル：90"
		, ChatColor.RESET + "" +  ChatColor.BLUE + "消費経験値：???"
		, ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックでセット");

		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(13,itemstack);


		itemstack = new ItemStack(Material.SKULL_ITEM,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
		itemstack.setDurability((short) 1);
		itemmeta.addEnchant(Enchantment.DIG_SPEED, 100, false);
		itemmeta.setDisplayName(ChatColor.DARK_PURPLE + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "ティロフィナーレ");
		lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.RED + "未実装"
				, ChatColor.RESET + "" +  ChatColor.GREEN + "遠距離11*11*9"
		, ChatColor.RESET + "" +  ChatColor.DARK_GREEN + "必要整地レベル：95"
		, ChatColor.RESET + "" +  ChatColor.BLUE + "消費経験値：???"
		, ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックでセット");
		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(14,itemstack);


		itemstack = new ItemStack(Material.BEDROCK,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.BEDROCK);
		itemmeta.addEnchant(Enchantment.DIG_SPEED, 100, false);
		itemmeta.setDisplayName(ChatColor.DARK_PURPLE + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "WORLD IS MINE");
		lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.RED + "未実装"
				, ChatColor.RESET + "" +  ChatColor.GREEN + "9*9*256"
		, ChatColor.RESET + "" +  ChatColor.DARK_GREEN + "必要整地レベル：100"
		, ChatColor.RESET + "" +  ChatColor.BLUE + "消費経験値：???"
		, ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックでセット");

		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(15,itemstack);


		itemstack = new ItemStack(Material.COAL_BLOCK,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.COAL_BLOCK);
		itemmeta.addEnchant(Enchantment.DIG_SPEED, 100, false);
		itemmeta.setDisplayName(ChatColor.BLUE + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "ギガンティックブラックホール");
		lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.RED + "未実装"
				, ChatColor.RESET + "" +  ChatColor.GREEN + "遠距離13*13*11"
		, ChatColor.RESET + "" +  ChatColor.DARK_GREEN + "必要整地レベル：105"
		, ChatColor.RESET + "" +  ChatColor.BLUE + "消費経験値：???"
		, ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックでセット");

		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(16,itemstack);

		itemstack = new ItemStack(Material.NETHER_STAR,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.NETHER_STAR);
		itemmeta.addEnchant(Enchantment.DIG_SPEED, 100, false);
		itemmeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "スターライトブレイカー");
		lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.RED + "未実装"
				, ChatColor.RESET + "" +  ChatColor.GREEN + "遠距離11*11*9*5"
		, ChatColor.RESET + "" +  ChatColor.DARK_GREEN + "必要整地レベル：115"
		, ChatColor.RESET + "" +  ChatColor.BLUE + "消費経験値：???"
		, ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックでセット");

		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(17,itemstack);


		itemstack = new ItemStack(Material.END_CRYSTAL,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.END_CRYSTAL);
		itemmeta.addEnchant(Enchantment.DIG_SPEED, 100, false);
		itemmeta.setDisplayName(ChatColor.DARK_GREEN + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "アサルトアーマー");
		lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.RED + "未実装"
				, ChatColor.RESET + "" +  ChatColor.GREEN + "パッシブ周囲ブロック破壊"
		, ChatColor.RESET + "" +  ChatColor.DARK_GREEN + "必要整地レベル：200"
		, ChatColor.RESET + "" +  ChatColor.BLUE + "消費経験値：???"
		, ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックでセット");

		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inventory.setItem(18,itemstack);

		return inventory;
	}
	// 採掘速度トグルボタン
	public static ItemMeta EFButtonMeta(PlayerData playerdata,ItemMeta itemmeta){
		List<String> lore = new ArrayList<String>();
		if(playerdata.effectflag){
			itemmeta.addEnchant(Enchantment.DIG_SPEED, 100, false);
			lore.add(ChatColor.RESET + "" +  ChatColor.GREEN + "現在ONです");
			lore.add(ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックでOFF");
		}else{
			itemmeta.removeEnchant(Enchantment.DIG_SPEED);
			lore.add(ChatColor.RESET + "" +  ChatColor.RED + "現在OFFです");
			lore.add(ChatColor.RESET + "" +  ChatColor.DARK_GREEN + "" + ChatColor.UNDERLINE + "クリックでON");
		}
		lore.addAll(
				Arrays.asList(ChatColor.RESET + "" +  ChatColor.GRAY + "採掘速度上昇効果とは"
				, ChatColor.RESET + "" +  ChatColor.GRAY + "接続人数と1分間の採掘量に応じて"
				, ChatColor.RESET + "" +  ChatColor.GRAY + "採掘速度が変化するシステムです"
				, ChatColor.RESET + "" +  ChatColor.GOLD + "現在の採掘速度上昇Lv：" + (playerdata.minespeedlv+1)
				, ChatColor.RESET + "" +  ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "上昇量の内訳"
				));
		for(EffectData ed : playerdata.effectdatalist){
			lore.add(ChatColor.RESET + "" +  ChatColor.RED + "" + ed.EDtoString(ed.id,ed.duration,ed.amplifier));
		}
		itemmeta.setLore(lore);
		return itemmeta;
	}

	// ガチャ券受け取りボタン
	public static List<String> GachaGetButtonLore(PlayerData playerdata){
		List<String> lore = new ArrayList<String>();
		int gachaget = (int) playerdata.gachapoint/1000;
		if(gachaget != 0){
			lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.AQUA + "未獲得ガチャ券：" + gachaget + "枚"
			, ChatColor.RESET + "" +  ChatColor.AQUA + "次のガチャ券まで:" + (int)(1000 - playerdata.gachapoint%1000) + "ブロック");
		}else{
			lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.RED + "獲得できるガチャ券はありません"
			, ChatColor.RESET + "" +  ChatColor.AQUA + "次のガチャ券まで:" + (int)(1000 - playerdata.gachapoint%1000) + "ブロック");
		}
		return lore;
	}

	//運営ガチャ券受け取りボタン
	public static List<String> SorryGachaGetButtonLore(PlayerData playerdata){
		List<String> lore = new ArrayList<String>();
		lore.addAll(Arrays.asList(ChatColor.RESET + "" +  ChatColor.GRAY + "運営からのガチャ券を受け取ります"
				, ChatColor.RESET + "" +  ChatColor.GRAY + "以下の場合に配布されます"
				, ChatColor.RESET + "" +  ChatColor.GRAY + "・各種不具合のお詫びとして"
				, ChatColor.RESET + "" +  ChatColor.GRAY + "・イベント景品として"
				, ChatColor.RESET + "" +  ChatColor.GRAY + "・投票ボーナスとして"
				, ChatColor.RESET + "" +  ChatColor.GRAY + "・各種謝礼として"));
		int gachaget = playerdata.numofsorryforbug;
		if(gachaget != 0){
			lore.add(ChatColor.RESET + "" +  ChatColor.AQUA + "未獲得ガチャ券：" + gachaget + "枚");
		}else{
			lore.add(ChatColor.RESET + "" +  ChatColor.RED + "獲得できるガチャ券はありません");
		}
		return lore;
	}

	public static Inventory getMineStackMenu(Player p){
		//プレイヤーを取得
		Player player = p.getPlayer();
		//UUID取得
		UUID uuid = player.getUniqueId();
		//プレイヤーデータ
		PlayerData playerdata = SeichiAssist.playermap.get(uuid);

		Inventory inventory = Bukkit.getServer().createInventory(null,4*9,ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "MineStack");
		ItemStack itemstack;
		ItemMeta itemmeta;

		//MineStack機能のトグルボタン
		itemstack = new ItemStack(Material.IRON_PICKAXE,1);
		itemmeta = Bukkit.getItemFactory().getItemMeta(Material.IRON_PICKAXE);
		itemmeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "対象ブロック自動スタック機能");
		itemmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itemstack.setItemMeta(MineStackToggleMeta(playerdata,itemmeta));
		inventory.setItem(0,itemstack);

		int v1 = SeichiAssist.config.getMineStacklevel(1);
		int v2 = SeichiAssist.config.getMineStacklevel(2);
		int v3 = SeichiAssist.config.getMineStacklevel(3);
		int v4 = SeichiAssist.config.getMineStacklevel(4);
		int v5 = SeichiAssist.config.getMineStacklevel(5);
		int v6 = SeichiAssist.config.getMineStacklevel(6);
		int v7 = SeichiAssist.config.getMineStacklevel(7);
		int v8 = SeichiAssist.config.getMineStacklevel(8);
		int v9 = SeichiAssist.config.getMineStacklevel(9);
		int v10 = SeichiAssist.config.getMineStacklevel(10);

		setMineStackButton(inventory, playerdata.minestack.dirt, Material.DIRT, v1, 1);

		setMineStackButton(inventory, playerdata.minestack.grass, Material.GRASS, v1, 2);

		setMineStackButton(inventory, playerdata.minestack.gravel, Material.GRAVEL, v2, 3);

		setMineStackButton(inventory, playerdata.minestack.cobblestone, Material.COBBLESTONE, v3, 4);

		setMineStackButton(inventory, playerdata.minestack.stone, Material.STONE, v3, 5);

		setMineStackButton(inventory, playerdata.minestack.sand, Material.SAND,v4, 6);

		setMineStackButton(inventory, playerdata.minestack.sandstone, Material.SANDSTONE, v4, 7);

		setMineStackButton(inventory, playerdata.minestack.netherrack, Material.NETHERRACK, v5, 8);

		setMineStackButton(inventory, playerdata.minestack.soul_sand, Material.SOUL_SAND, v6, 9);

		setMineStackButton(inventory, playerdata.minestack.magma, Material.MAGMA, v6, 10);

		setMineStackButton(inventory, playerdata.minestack.ender_stone, Material.ENDER_STONE, v7, 11);

		setMineStackButton(inventory, playerdata.minestack.coal, Material.COAL, v8, 12);

		setMineStackButton(inventory, playerdata.minestack.coal_ore, Material.COAL_ORE, v8, 13);

		setMineStackButton(inventory, playerdata.minestack.iron_ore, Material.IRON_ORE, v9, 14);

		setMineStackButton(inventory, playerdata.minestack.quartz, Material.QUARTZ, v10, 15);

		setMineStackButton(inventory, playerdata.minestack.quartz_ore, Material.QUARTZ_ORE, v10, 16);

		// 1ページ目を開く
		itemstack = new ItemStack(Material.SKULL_ITEM,1);
		SkullMeta skullmeta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
		itemstack.setDurability((short) 3);
		skullmeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "ホームへ");
		List<String> lore = Arrays.asList(ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックで移動"
				);
		skullmeta.setLore(lore);
		skullmeta.setOwner("MHF_ArrowLeft");
		itemstack.setItemMeta(skullmeta);
		inventory.setItem(27,itemstack);

		return inventory;
	}
	// MineStackトグルボタン
	public static ItemMeta MineStackToggleMeta(PlayerData playerdata,ItemMeta itemmeta){
		List<String> lore = new ArrayList<String>();
		if(playerdata.minestackflag){
			itemmeta.addEnchant(Enchantment.DIG_SPEED, 100, false);
			lore.add(ChatColor.RESET + "" +  ChatColor.GREEN + "現在ONです");
			lore.add(ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックでOFF");
		}else{
			itemmeta.removeEnchant(Enchantment.DIG_SPEED);
			lore.add(ChatColor.RESET + "" +  ChatColor.RED + "現在OFFです");
			lore.add(ChatColor.RESET + "" +  ChatColor.DARK_GREEN + "" + ChatColor.UNDERLINE + "クリックでON");
		}
		itemmeta.setLore(lore);
		return itemmeta;
	}

	//MineStackボタン作成
	public static Inventory setMineStackButton(Inventory inv,int minestack,Material type,int level,int set){
		ItemStack itemstack = new ItemStack(type,1);
		ItemMeta itemmeta = Bukkit.getItemFactory().getItemMeta(type);
		itemmeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + type.toString());
		List<String> lore = Arrays.asList(ChatColor.RESET + "" + ChatColor.GREEN + minestack +"個"
				, ChatColor.RESET + "" +  ChatColor.DARK_GRAY + "Lv" + level + "以上でスタック可能"
				, ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックで1スタック取り出し");
		itemmeta.setLore(lore);
		itemstack.setItemMeta(itemmeta);
		inv.setItem(set,itemstack);
		return inv;
	}

	// 死亡メッセージ表示トグルボタン
	public static ItemMeta dispKillLogToggleMeta(PlayerData playerdata,ItemMeta itemmeta){
		List<String> lore = new ArrayList<String>();
		if(playerdata.dispkilllogflag){
			itemmeta.addEnchant(Enchantment.DIG_SPEED, 100, false);
			lore.add(ChatColor.RESET + "" +  ChatColor.GREEN + "表示する");
			lore.add(ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックで隠す");
		}else{
			itemmeta.removeEnchant(Enchantment.DIG_SPEED);
			lore.add(ChatColor.RESET + "" +  ChatColor.RED + "隠す");
			lore.add(ChatColor.RESET + "" +  ChatColor.DARK_GREEN + "" + ChatColor.UNDERLINE + "クリックで表示する");
		}
		itemmeta.setLore(lore);
		return itemmeta;
	}

	// PvPトグルボタン
	public static ItemMeta dispPvPToggleMeta(PlayerData playerdata,ItemMeta itemmeta){
		List<String> lore = new ArrayList<String>();
		if(playerdata.pvpflag){
			itemmeta.addEnchant(Enchantment.DIG_SPEED, 100, false);
			lore.add(ChatColor.RESET + "" +  ChatColor.GREEN + "ON(ONの相手とPvPが可能)");
			lore.add(ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックでOFF");
		}else{
			itemmeta.removeEnchant(Enchantment.DIG_SPEED);
			lore.add(ChatColor.RESET + "" +  ChatColor.RED + "OFF(全てのPvPを無効化)");
			lore.add(ChatColor.RESET + "" +  ChatColor.DARK_GREEN + "" + ChatColor.UNDERLINE + "クリックでON");
		}
		itemmeta.setLore(lore);
		return itemmeta;
	}

	//ランキングリスト
	public static Inventory getRankingList(Player p){
		Inventory inventory = Bukkit.getServer().createInventory(null,4*9,ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "整地神ランキング");
		ItemStack itemstack = new ItemStack(Material.SKULL_ITEM,1);
		SkullMeta skullmeta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
		List<String> lore = new ArrayList<String>();
		itemstack.setDurability((short) 3);
		RankData rankdata = null;
		for(int count = 0;count < 27;count++){
			if(count > SeichiAssist.ranklist.size() - 1){
				break;
			}
			rankdata = SeichiAssist.ranklist.get(count);
			skullmeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "" + (count+1) +"位:" + "" + ChatColor.WHITE + rankdata.name);
			lore.clear();
			lore.add(ChatColor.RESET + "" +  ChatColor.GREEN + "整地レベル:" + rankdata.level);
			lore.add(ChatColor.RESET + "" +  ChatColor.GREEN + "総整地量:" + rankdata.totalbreaknum);

			skullmeta.setLore(lore);
			skullmeta.setOwner(rankdata.name);
			itemstack.setItemMeta(skullmeta);
			inventory.setItem(count,itemstack);
		}

		// 1ページ目を開く
		skullmeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "ホームへ");
		lore.clear();
		lore.add(ChatColor.RESET + "" +  ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "クリックで移動");
		skullmeta.setLore(lore);
		skullmeta.setOwner("MHF_ArrowLeft");
		itemstack.setItemMeta(skullmeta);
		inventory.setItem(27,itemstack);

		// 1ページ目を開く
		skullmeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "整地鯖統計データ");
		lore.clear();
		lore.addAll(Arrays.asList(ChatColor.RESET + "" +  ChatColor.AQUA + "全プレイヤー総整地量:"
				,ChatColor.RESET + "" +  ChatColor.AQUA + SeichiAssist.allplayerbreakblockint
				));
		skullmeta.setLore(lore);
		skullmeta.setOwner("unchama");
		itemstack.setItemMeta(skullmeta);
		inventory.setItem(35,itemstack);

		return inventory;
	}
}
