package org.RaidRandomizer;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("raidroulette")
public interface ExampleConfig extends Config
{
	@ConfigItem(
			keyName = "enableCox",
			name = "Enable Cox",
			description = "Show Chambers of Xeric result",
			position = 0
	)
	default boolean enableCox() { return true; }

	@ConfigItem(
			keyName = "enableTob",
			name = "Enable ToB",
			description = "Show Theatre of Blood result",
			position = 1
	)
	default boolean enableTob() { return true; }

	@ConfigItem(
			keyName = "enableToa",
			name = "Enable ToA",
			description = "Show Tombs of Amascut result",
			position = 2
	)
	default boolean enableToa() { return true; }

	// 🕒 UTC sync (deterministic)
	@ConfigItem(
			keyName = "useUtcSync",
			name = "Use UTC sync",
			description = "Deterministic roll across clients (all raids)",
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