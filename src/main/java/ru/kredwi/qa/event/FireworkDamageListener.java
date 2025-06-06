package ru.kredwi.qa.event;

import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.meta.FireworkMeta;

import ru.kredwi.qa.callback.ConstructionStageEndCallback;

public class FireworkDamageListener implements Listener {

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Firework firework) {
			FireworkMeta fwm = firework.getFireworkMeta();
			
			// cancel damage from plugin firework
			if (fwm.hasCustomModelData() && fwm.getCustomModelData() == ConstructionStageEndCallback.FIREWORK_MODEL_ID) {
				event.setCancelled(true);
			}
		}
	}
	
}
