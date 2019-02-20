package yuudaari.soulus.common.compat.crafttweaker;

import java.util.Map;
import crafttweaker.api.item.IIngredient;
import stanhebben.zenscript.annotations.ZenMethod;

public interface IZenComposerFactory {

	@ZenMethod
	public IZenComposerFactory setTime (final float time);

	@ZenMethod
	public IZenComposerFactory setShaped (final IIngredient[][] inputsShaped);

	@ZenMethod
	public IZenComposerFactory setShapeless (final IIngredient[] inputsShapeless);

	@ZenMethod
	public IZenComposerFactory setMobWhitelist (final String[] mobWhitelist);

	@ZenMethod
	public IZenComposerFactory setMobBlacklist (final String[] mobBlacklist);

	@ZenMethod
	public IZenComposerFactory setMobsRequired (final Map<String, Integer> mobsRequired);

	@ZenMethod
	public void create ();
}
