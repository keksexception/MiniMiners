package de.raffi.autominer.compability.handler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.raffi.autominer.compability.VersionHandler;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagString;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;

public class Handler_v1_8_R3 implements VersionHandler{

	@Override
	public ItemStack writeNBT(ItemStack original, String key, JSONObject write) {
		net.minecraft.server.v1_8_R3.ItemStack c = CraftItemStack.asNMSCopy(original);
		NBTTagCompound compound = c.hasTag()?c.getTag():new NBTTagCompound();
		compound.set(key, new NBTTagString(write.toJSONString()));
		return CraftItemStack.asBukkitCopy(c);
	}

	@Override
	public JSONObject getNBT(ItemStack getFrom, String key) {
		net.minecraft.server.v1_8_R3.ItemStack c = CraftItemStack.asNMSCopy(getFrom);
		NBTTagCompound compound = c.hasTag()?c.getTag():new NBTTagCompound();
		if(compound.hasKey(key)) {
			String data = compound.getString(key);
			try {
				return (JSONObject) new JSONParser().parse(data);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} 
		return null;
	}

	@Override
	public boolean hasNBTTag(ItemStack check, String tag) {
		net.minecraft.server.v1_8_R3.ItemStack copy = CraftItemStack.asNMSCopy(check);
		return copy.hasTag()&&copy.getTag().get(tag)!=null;
	}

	@Override
	public void spawnDestroyParticle(Location at) {
		float x = (float) at.getX();
		float y = (float) at.getY();
		float z = (float) at.getZ();
		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.LAVA, true, x, y, z, 0.3F, 0.1F,0.3F, 1, 15, null);
		for (Player p : Bukkit.getOnlinePlayers()) {
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);

		}
		
	}

}
