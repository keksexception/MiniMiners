package de.raffi.autominer.miner;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.json.simple.JSONObject;

import de.raffi.autominer.animations.Animation;
import de.raffi.autominer.animations.AnimationStop;
import de.raffi.autominer.animations.AnimationWalking;
import de.raffi.autominer.compability.CompabilityManager;
import de.raffi.autominer.exception.AnimationException;
import de.raffi.autominer.inventory.BetterInventory;
import de.raffi.autominer.inventory.InventoryManager;
import de.raffi.autominer.io.JSONConverter;
import de.raffi.autominer.main.MiniMiners;
import de.raffi.autominer.utils.ColorManager;
import de.raffi.autominer.utils.MathHelper;
import de.raffi.autominer.utils.MinerManager;
import de.raffi.pluginlib.builder.ItemBuilder;
import de.raffi.pluginlib.converter.ConverterLocation;

public abstract class Miner {
	
	private ArmorStand armorStand;
	private int taskIDJob=-1, jobDelay;
	private Animation currentAnimation;
	private Inventory inventory = Bukkit.createInventory(null, 9*5);
	private ArmorStand statusStand;
	private ColorManager colorManager;
	private JSONObject additional;
	
	public AnimationWalking walkAnimation;
	
	private boolean allowsTeleportation;
	
	private double health, foodLevel;
	
	private int stamina, maxStamina;
	
	public Miner(Location loc, String displayName, double health, double foodLevel, boolean allowsTeleportation) {
		this(loc, displayName, health, foodLevel, allowsTeleportation,null);
		
	}
	public Miner(Location loc, String displayName, double health, double foodLevel, boolean allowsTeleportation, JSONObject additional) {
		ArmorStand stand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		stand.setCustomName(displayName);
		stand.setCustomNameVisible(true);
		stand.setBasePlate(false);
		stand.setItemInHand(getHandItem());
		stand.setArms(true);
		stand.setSmall(true);
		stand.setGravity(false);
		this.armorStand = stand;
		this.walkAnimation = new AnimationWalking(2);
		this.colorManager = new ColorManager();
		this.health = health;
		this.foodLevel = foodLevel;
		this.allowsTeleportation = allowsTeleportation;
		
		if(additional==null)
			additional = new JSONObject();
		this.additional = additional;
		
		
		createStatusStand();
		init();
	}
	@SuppressWarnings("unchecked")
	public JSONObject toJson() {
		JSONObject json = new JSONObject();
		json.put("location", new ConverterLocation().stringify(getLocation()));
		json.put("classname", getClass().getName());
		json.put("displayname", getArmorStand().getCustomName());
		json.put("inventory", JSONConverter.toJson(getInventory()));
		json.put("delay", jobDelay);
		json.put("head", JSONConverter.toJson(getArmorStand().getHelmet()));
		json.put("chest", JSONConverter.toJson(getArmorStand().getChestplate()));
		json.put("leggings", JSONConverter.toJson(getArmorStand().getLeggings()));
		json.put("boots", JSONConverter.toJson(getArmorStand().getBoots()));
		json.put("hand", JSONConverter.toJson(getArmorStand().getItemInHand()));
		json.put("health", health);
		json.put("foodlevel", foodLevel);
		json.put("teleportation", allowsTeleportation);
		json.put("extra", additional);
		return json;
	}
	/**
	 * 
	 * @param json
	 * @param spawnAt let spawnAt null, if you want to spawn it at his last location
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Miner fromJson(JSONObject json, Location spawnAt) {
		spawnAt=spawnAt==null?new ConverterLocation().create((String) json.get("location")):spawnAt;
		String classname = (String) json.get("classname");
		String displayname = (String) json.get("displayname");
		Inventory inv = JSONConverter.inventoryFromJson((JSONObject) json.get("inventory"));
		long delay = (long) json.get("delay");
		ItemStack head = JSONConverter.fromJson((JSONObject) json.get("head"));
		ItemStack chest = JSONConverter.fromJson((JSONObject) json.get("chest"));
		ItemStack leggings = JSONConverter.fromJson((JSONObject) json.get("leggings"));
		ItemStack boots = JSONConverter.fromJson((JSONObject) json.get("boots"));
		ItemStack hand = JSONConverter.fromJson((JSONObject) json.get("hand"));
		double health = (double) json.get("health");
		double foodLevel = (double) json.get("foodlevel");
		boolean teleportation = (boolean)json.get("teleportation");
		JSONObject additional = (JSONObject) json.get("extra");

		try {
			Class<Miner> c = (Class<Miner>) Class.forName(classname);
			Miner m = c.getDeclaredConstructor(Location.class, String.class, double.class, double.class, boolean.class, JSONObject.class).newInstance(spawnAt,displayname, health, foodLevel,teleportation,additional);
			m.getArmorStand().setHelmet(head);
			m.getArmorStand().setChestplate(chest);
			m.getArmorStand().setLeggings(leggings);
			m.getArmorStand().setBoots(boots);
			m.getArmorStand().setItemInHand(hand);
			m.setInventory(inv);
			m.setJobDelay((int)delay);
			return m;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return  null;
	}
	/**
	 * 
	 * @return wether the minion should allow teleportation
	 */
	public boolean doAllowTeleportation() {
		return allowsTeleportation;
	}
	/**
	 * 
	 * @return the job delay. Every delay ticks {@link Miner#doJob()} is called
	 */
	public int getJobDelay() {
		return jobDelay;
	}
	/**
	 * abstract method for doing the stuff the Minion should do
	 * @throws AnimationException 
	 */
	public abstract void doJob();
	/**
	 * starts the Bukkit scheduler task for {@link Miner#doJob()}
	 * @param delay the jobdelay 
	 * @see Miner#getJobDelay()
	 */
	public void startDoJob(int delay){
		if(isWorking()) return;
		this.jobDelay = delay;
		this.init();
		taskIDJob=Bukkit.getScheduler().scheduleSyncRepeatingTask(MiniMiners.getInstance(), ()->{
			doJob();
		}, delay, delay);
	}
	/**
	 * stops the Bukkit scheduler tasks for {@link Miner#doJob()}
	 */
	public void stopDoJob() {
		Bukkit.getScheduler().cancelTask(taskIDJob);
		taskIDJob=-1;
		stopAnimation();
		if(walkAnimation.isRunning())
			walkAnimation.stop();
	}
	/**
	 * sets the job delay. Please Note: if you change delay while {@link Miner#isWorking()} equals <code>true</code>
	 * it does not have any effect to the current working speed.
	 * @param jobDelay the new delay
	 */
	public void setJobDelay(int jobDelay) {
		this.jobDelay = jobDelay;
	}
	public void setRightArmPose(double x, double y, double z) {
		armorStand.setRightArmPose(new EulerAngle(x, y, z));
	}
	/**
	 * starts the new Animation and stops the old one
	 * @param a the Animation you want to start
	 * @see {@link Miner#stopAnimation()}, {@link Animation#stop()}, {@link Animation#start(ArmorStand)}
	 */
	public void startAnimation(Animation a) {
		stopAnimation();
		a.init(getArmorStand());
		this.currentAnimation = a;
		this.currentAnimation.start(armorStand);
	}
	/**
	 * stops the current Animation. walk animation is not affected
	 * @see {@link Miner#startAnimation(Animation)}, {@link Animation#stop()}, {@link Animation#start(ArmorStand)}
	 */
	public void stopAnimation() {
		if(currentAnimation!=null&&currentAnimation.isRunning())
			currentAnimation.stop();
	}
	public AnimationStop resetAnimation(){
		AnimationStop s = new AnimationStop(3);
		s.init(getArmorStand());
		s.start(getArmorStand());
		return s;
	}
	/**
	 * 
	 * @return <code> true </code> when an animation is played at the moment. {@link Miner#walkAnimation} does not count as animation
	 */
	public boolean hasAnimation() {
		return currentAnimation!=null&&currentAnimation.isRunning();
	}
	/**
	 * 
	 * @return the last animation played. <code> null </code> when no animation was played before
	 */
	public Animation getCurrentAnimation() {
		return currentAnimation;
	}
	/**
	 * 
	 * @return <code> true </code> when the minion is doing his job at the moment
	 */
	public boolean isWorking() {
		return taskIDJob!=-1;
	}
	/**
	 * sets yaw and pitch that Minion is looking at the given location
	 * @param to the location the Minion should look at
	 */
	public void rotateHeadTo(Location to) {
		double deltaX = to.getX()-getLocation().getX();
		double deltaY = to.getY()-getLocation().getY();
		double deltaZ = to.getZ()-getLocation().getZ();
		
		double rad = Math.atan2(deltaZ, deltaX);
		
		Location loc = getLocation();
		loc.setYaw((float)Math.toDegrees(rad)-90F);
		armorStand.setHeadPose(new EulerAngle((float) Math.asin(deltaY*-1/to.distance(getLocation())), 0, 0));
		armorStand.teleport(loc);
	}
	/**
	 * teleports the armorstand and his statustext to the new location
	 * @param set the new location
	 */
	public void setLocation(Location set) {
		getArmorStand().teleport(set);
		statusStand.teleport(getLocation().clone().add(0, 0.4, 0));
	}
	/**
	 * 
	 * @return location of the armorstand
	 */
	public Location getLocation() {
		return getArmorStand().getLocation();
	}
	public ArmorStand getArmorStand() {
		return armorStand;
	}
	/**
	 * 
	 * @return the inventory of the minion
	 */
	public Inventory getInventory() {
		return inventory;
	}
	/**
	 * registers the Minion for events
	 */
	public void register() {
		MinerManager.miners.add(this);
	}
	public boolean hasWalkingAnimation() {
		return walkAnimation.isRunning();
	}
	/**
	 * not registered miners cannot be used in events
	 */
	public void unregister() {
		MinerManager.miners.remove(this);
	}
	/**
	 * creates, sets and spawns the statusstand at the minions location
	 */
	public void createStatusStand() {
		ArmorStand stand = (ArmorStand) getLocation().getWorld().spawnEntity(getLocation().clone().add(0, 0.4, 0), EntityType.ARMOR_STAND);
		stand.setCustomName("");
		stand.setCustomNameVisible(false);
		stand.setBasePlate(false);
		stand.setArms(false);
		stand.setSmall(true);
		stand.setGravity(false);
		stand.setVisible(false);
		statusStand = stand;
	}
	/**
	 * 
	 * @return the text over the name of the armorstand
	 */
	public String getStatusText() {
		return statusStand.getCustomName();
	}
	/**
	 * sets the text over the name of the armorstand
	 * @param text the new text. Do null when you want to remove it
	 */
	public void setStatusText(String text) {
		statusStand.setCustomNameVisible(text!=null);
		statusStand.setCustomName(text);
	}
	/**
	 * 
	 * @return an inventory that shows the miner options
	 * @see {@link MinerManager#createMinerOptionSelectInventory(Miner)}
	 */
	public BetterInventory getOptionsInventory() {
		return InventoryManager.createMinionOptionSelectInventory(this);
	}
	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}
	public void setAllowsTeleportation(boolean allowsTeleportation) {
		this.allowsTeleportation = allowsTeleportation;
	}
	public abstract void init();
	/**
	 * 
	 * @return the item that is currently in the armorstands right (main) hand
	 */
	public abstract ItemStack getHandItem();
	/**
	 * 
	 * @return status text armorstand
	 */
	public ArmorStand getStatusStand() {
		return statusStand;
	}
	public ColorManager getColorManager() {
		return colorManager;
	}
	/**
	 * removes the armorstand and the statusstand. also drops every item in the inventory and unregisteres the miner
	 */
	public void remove(boolean unregister) {
		stopDoJob();
		stopAnimation();
		armorStand.remove();
		statusStand.remove();
		getLocation().getWorld().dropItem(getLocation(), toItem());
		if(unregister)
			unregister();
	}
	public ItemStack toItem() {

		ItemBuilder b = new ItemBuilder(Material.FLOWER_POT_ITEM);
		b.setName("§6§lMinion spawner");
		b.glow();
		b.setLore("§7Name: " + armorStand.getCustomName(), "§7Health: §a" + getHealth() + "/20", "§7Food: §a" + getFoodLevel()+"/20");
		return CompabilityManager.HANDLER.writeNBT(b.build(), "minerdata", toJson());
	}
	public static Miner fromItem(ItemStack stack, Location spawnAt) {
		return Miner.fromJson(CompabilityManager.HANDLER.getNBT(stack, "minerdata"), spawnAt);
	}
	public double getHealth() {
		return health;
	}
	public double getFoodLevel() {
		return foodLevel;
	}
	/**
	 * sets new health of the minion and plays damage sound
	 * @param damage the amount of damage that the minion should take
	 */
	public void damage(double damage) {
		if(getHealth()==0) return;
		setHealth(MathHelper.min(getHealth()-damage, 0));
		getLocation().getWorld().playSound(getLocation(), Sound.HURT_FLESH, 1.0F, 1.0F);
	}
	public void setFoodLevel(double foodLevel) {
		this.foodLevel = foodLevel;
	}
	public void setHealth(double health) {
		this.health = health;
	}
	public JSONObject getAdditional() {
		return additional;
	}
}
