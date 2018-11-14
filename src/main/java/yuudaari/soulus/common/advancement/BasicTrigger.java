package yuudaari.soulus.common.advancement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public abstract class BasicTrigger<T extends MatchableCriterionInstance<D>, D> implements ICriterionTrigger<T> {

	protected final Map<PlayerAdvancements, Set<ICriterionTrigger.Listener<T>>> listeners = new HashMap<>();

	@Override
	public void addListener (PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<T> listener) {
		Set<ICriterionTrigger.Listener<T>> listeners = this.listeners
			.get(playerAdvancementsIn);

		if (listeners == null) {
			listeners = new HashSet<>();
			this.listeners.put(playerAdvancementsIn, listeners);
		}

		listeners.add(listener);
	}

	@Override
	public void removeListener (PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<T> listener) {
		Set<ICriterionTrigger.Listener<T>> listeners = this.listeners
			.get(playerAdvancementsIn);

		if (listeners != null) {
			listeners.remove(listener);

			if (listeners.isEmpty()) {
				this.listeners.remove(playerAdvancementsIn);
			}
		}
	}

	@Override
	public void removeAllListeners (PlayerAdvancements playerAdvancementsIn) {
		this.listeners.remove(playerAdvancementsIn);
	}

	@Override
	public abstract T deserializeInstance (JsonObject json, JsonDeserializationContext context);

	public void trigger (EntityPlayer player, D data) {
		if (player == null || !(player instanceof EntityPlayerMP)) return;

		EntityPlayerMP playerMP = (EntityPlayerMP) player;

		PlayerAdvancements advancements = playerMP.getAdvancements();
		Set<ICriterionTrigger.Listener<T>> listeners = this.listeners.get(advancements);

		if (listeners == null) return;

		List<ICriterionTrigger.Listener<T>> matchingListeners = null;

		for (ICriterionTrigger.Listener<T> listener : listeners) {
			T breakBlockTrigger = (T) listener.getCriterionInstance();
			if (!breakBlockTrigger.matches(playerMP, data)) continue;

			if (matchingListeners == null) {
				matchingListeners = new ArrayList<>();
			}

			matchingListeners.add(listener);
		}

		if (matchingListeners == null) return;

		for (ICriterionTrigger.Listener<T> listener : matchingListeners) {
			listener.grantCriterion(advancements);
		}
	}
}
