package me.tim3game.pokeme;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;


public class Main extends JavaPlugin{
	public void onEnable(){
		Bukkit.getLogger().info("[PokeMe] Enabled "+getDescription().getName()+" ver."+getDescription().getVersion());
		saveConfig();
	}
	public void onDisable(){
		Bukkit.getLogger().info("[PokeMe] Disabling "+getDescription().getName());
	}
	
	  @Deprecated
	  public static void sendTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String message)
	  {
	    sendTitle(player, fadeIn, stay, fadeOut, message, null);
	  }
	 
	  @Deprecated
	  public static void sendSubtitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String message)
	  {
	    sendTitle(player, fadeIn, stay, fadeOut, null, message);
	  }
	 
	  @Deprecated
	  public static void sendFullTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle)
	  {
	    sendTitle(player, fadeIn, stay, fadeOut, title, subtitle);
	  }
	 
	  @Deprecated
	  public static Integer getPlayerProtocol(Player player)
	  {
	    return Integer.valueOf(47);
	  }
	 
	  public static void sendTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle)
	  {
	    PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
	   
	    PacketPlayOutTitle packetPlayOutTimes = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, null, fadeIn.intValue(), stay.intValue(), fadeOut.intValue());
	    connection.sendPacket(packetPlayOutTimes);
	    if (subtitle != null)
	    {
	      subtitle = subtitle.replaceAll("%player%", player.getDisplayName());
	      subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
	      IChatBaseComponent titleSub = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + subtitle + "\"}");
	      PacketPlayOutTitle packetPlayOutSubTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, titleSub);
	      connection.sendPacket(packetPlayOutSubTitle);
	    }
	    if (title != null)
	    {
	      title = title.replaceAll("%player%", player.getDisplayName());
	      title = ChatColor.translateAlternateColorCodes('&', title);
	      IChatBaseComponent titleMain = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + title + "\"}");
	      PacketPlayOutTitle packetPlayOutTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleMain);
	      connection.sendPacket(packetPlayOutTitle);
	    }
	  }
	
	List<String> receive = new ArrayList<String>();
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if(sender instanceof Player){
			Player p = (Player) sender;
			if(commandLabel.equalsIgnoreCase("poke")){
				p.getItemInHand().getDurability();
				p.getItemInHand().getType().getMaxDurability();
				if(args.length==0){
					p.sendMessage("/poke [name] [message]");
					p.sendMessage("/poke receive");
				}else if(args.length==1){
					if(args[0].equals("reload")){
						if(p.hasPermission("pokeme.reload")){
							reloadConfig();
							sendTitle(p, 1, 5, 1, null, getConfig().getString("Prefix")+" "+getConfig().getString("Reload"));
							Bukkit.getLogger().info(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Prefix"))+"§r "+ChatColor.translateAlternateColorCodes('&', getConfig().getString("Reload")));
							return false;
						}else{
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Prefix"))+"§r "+ChatColor.translateAlternateColorCodes('&', getConfig().getString("NoPermissions")));
							return false;
						}
					}
					if(args[0].equals("receive")){
						if(receive.contains(p.getName())){
							receive.remove(p.getName());
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Prefix"))+"§r "+ChatColor.translateAlternateColorCodes('&', getConfig().getString("OnPokes")));
						}else{
							receive.add(p.getName());
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Prefix"))+"§r "+ChatColor.translateAlternateColorCodes('&', getConfig().getString("OffPokes")));
						}
					}else
					if(args[0]!=null){
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Prefix"))+"§r "+ChatColor.translateAlternateColorCodes('&', getConfig().getString("NoMessages")));
					}else{
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Prefix"))+"§r "+ChatColor.translateAlternateColorCodes('&', getConfig().getString("NoPlayer")));
					}
				}else if(args.length>=2){
					if(p.hasPermission("pokeme.send")){
						Player op = Bukkit.getPlayer(args[0]);
						if(op!=null){
							if(!receive.contains(op.getName())){
								String message = "";
									for(int counter=1;args.length>counter;counter++){
										message = message + " " + args[counter];
									}
									op.getPlayer().setHealth(0);
									sendTitle(p, 1, 5, 1, getConfig().getString("Text"), message);
									op.playSound(op.getLocation(),Sound.valueOf(this.getConfig().getString("sPoke")),this.getConfig().getInt("Volume"),this.getConfig().getInt("Pitch"));
							}else{
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Prefix"))+"§r "+ChatColor.translateAlternateColorCodes('&', getConfig().getString("NoPokes")).replace("%nickname%", p.getName() +""));
								p.playSound(op.getLocation(),Sound.valueOf(this.getConfig().getString("sNoPoke")),this.getConfig().getInt("Volume"),this.getConfig().getInt("Pitch"));
							}
						}else{
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Prefix"))+"§r "+ChatColor.translateAlternateColorCodes('&', getConfig().getString("NoPlayer")));
						}
					}else{
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Prefix"))+"§r "+ChatColor.translateAlternateColorCodes('&', getConfig().getString("NoPermissions")));
					}
				}else{
					p.sendMessage("/poke [name] [message]");
					p.sendMessage("/poke receive");
					return false;
				}
			}
		}else{
			Bukkit.getLogger().info("[PokeMe] You are not a player");
		}
		return false;
		}
	}
