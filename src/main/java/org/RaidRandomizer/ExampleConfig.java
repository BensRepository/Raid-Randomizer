package org.RaidRandomizer;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("raid randomizer")
public interface ExampleConfig extends Config
{
	@ConfigItem(
			keyName = "enableCox",
			name = "Chambers of Xeric",
			description = "adds Chambers of Xeric to results",
			position = 0
	)
	default boolean enableCox() { return true; }

	@ConfigItem(
			keyName = "enableTob",
			name = "Theatre of Blood",
			description = "adds Theatre of Blood to results",
			position = 1
	)
	default boolean enableTob() { return true; }

	@ConfigItem(
			keyName = "enableToa",
			name = "Tombs of Amascut",
			description = "adds Tombs of Amascut to results",
			position = 2
	)
	default boolean enableToa() { return true; }

	// 🕒 UTC sync (deterministic)
	@ConfigItem(
			keyName = "useUtcSync",
			name = "Sync results",
			description = "Shows same result for all players with the plugin (all raids enabled)",
			position = 3
	)
	default boolean useUtcSync()
	{
		return true;
	}

	// 🎚 Speed dropdown
	enum SpinSpeed
	{
		FAST,
		MEDIUM,
		SLOW
	}

	@ConfigItem(
			keyName = "spinSpeed",
			name = "Spin speed",
			description = "Roulette speed",
			position = 4
	)
	default SpinSpeed spinSpeed()
	{
		return SpinSpeed.MEDIUM;
	}

	// 🔊 Sound toggle
	@ConfigItem(
			keyName = "enableSounds",
			name = "Enable sounds",
			description = "Play drum and reveal sounds",
			position = 5
	)
	default boolean enableSounds()
	{
		return true;
	}
}