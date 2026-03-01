package org.RaidRandomizer;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("raidrandomizer")
public interface ExampleConfig extends Config
{
	@ConfigItem(
			keyName = "enableCox",
			name = "Enable Chambers of Xeric",
			description = "Include Chambers of Xeric in the raid randomizer"
	)
	default boolean enableCox()
	{
		return true;
	}

	@ConfigItem(
			keyName = "enableTob",
			name = "Enable Theatre of Blood",
			description = "Include Theatre of Blood in the raid randomizer"
	)
	default boolean enableTob()
	{
		return true;
	}

	@ConfigItem(
			keyName = "enableToa",
			name = "Enable Tombs of Amascut",
			description = "Include Tombs of Amascut in the raid randomizer"
	)
	default boolean enableToa()
	{
		return true;
	}

	@ConfigItem(
			keyName = "deterministic",
			name = "Sync results",
			description = "If enabled, players with the plugin will see the same result. (All raids are rolled)."
	)
	default boolean deterministic()
	{
		return false;
	}
}