package mr.minecraft15.Killreward;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener, CommandExecutor {
	private static Main instance;
	static String killreward = "Killreward";
	static String prefix;
	
	@Override
	public void onEnable() {
		instance = this;
		prefix = "§7[§b" + killreward + "§7] ";
		Bukkit.getPluginManager().registerEvents(this, this);
		getCommand(killreward).setExecutor(this);
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		Entity k = p.getKiller();
		if(k != null && k instanceof Player) {
			Player pk = (Player) k;
			if(!pk.getName().equalsIgnoreCase(p.getName())) {
				if(Main.getInstance().getConfig().getConfigurationSection(killreward) != null) {
					ArrayList<Integer> ids = new ArrayList<Integer>();
					for(String id : Main.getInstance().getConfig().getConfigurationSection(killreward).getKeys(false)) {
						ids.add(getInt(id));
					}
					pk.getInventory().addItem(Main.getInstance().getConfig().getItemStack(killreward + "." + ids.get(new Random().nextInt(ids.size()))));
				}
			}
		}
	}
	
	private static JavaPlugin getInstance() {
		return instance;
	}

	@Override
	public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
		if(s instanceof Player) {
			Player p = (Player) s;
			if(p.hasPermission("command." + killreward)) {
				if(a.length == 1) {
					if(a[0].equalsIgnoreCase("list")) {
						if(getConfig().getConfigurationSection(killreward) == null) {
							p.sendMessage(prefix + "There are no killrewards.");
						} else {
							p.sendMessage(prefix + killreward + "s:");
							for(String id : getConfig().getConfigurationSection(killreward).getKeys(false)) {
								ItemStack is = getConfig().getItemStack(killreward + "." + id);
								p.sendMessage("§b" + id + ". §7" + is.getAmount() + " * " + getItemname(is.getType()));
							}
						}
						return true;
					}
				}
				if(a.length == 2) {
					if(a[0].equalsIgnoreCase("remove")) {
						if(isInt(a[1]) && getInt(a[1]) > 0) {
							if(getConfig().contains(killreward + "." + a[1])) {
								p.sendMessage(prefix + "Removed " + getItemname(getConfig().getItemStack(killreward + "." + a[1]).getType()) + " from the killrewards.");
								getConfig().set(killreward + "." + a[1], null);
								saveConfig();
							} else {
								p.sendMessage(prefix + "Can not find killreward " + a[1] + ".");
							}
						} else {
							p.sendMessage(prefix + a[1] + " is no valid ID.");
						}
						return true;
					}
					if(a[0].equalsIgnoreCase("add")) {
						if(isInt(a[1]) && getInt(a[1]) > 0) {
							if(p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) {
								p.sendMessage(prefix + "Put an item in your hand, you want to add as killreward.");
							} else {
								getConfig().set(killreward + "." + a[1], p.getItemInHand());
								saveConfig();
								p.sendMessage(prefix + "Added " + getItemname(p.getItemInHand().getType()) + " to killrewards.");
							}
						} else {
							p.sendMessage(prefix + a[1] + " is no valid ID.");
						}
						return true;
					}
				}
				p.sendMessage(prefix + "/" + killreward + " list");
				p.sendMessage(prefix + "/" + killreward + " [add|remove] [ID]");
			} else {
				p.sendMessage(prefix + "You are not allowed to perform this command.");
			}
		}
		return true;
	}

	private boolean isInt(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}
	
	private static int getInt(String s) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException ex) {
			return 0;
		}
	}

	private static String getItemname(Material type) {
		String a = "";
		String[] b = type.toString().split("_");
		for(String c : b) {
			a = a + c.substring(0, 1) + c.substring(1, c.length()).toLowerCase() + " ";
		}
		return a.substring(0, a.length() - 1);
	}
}
