package com.github.bensrepository.raidrandomizer;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MessageNode;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@PluginDescriptor(
		name = "Raid Randomizer",
		description = "Randomize the next raid you and your friends take on.",
		tags = {"raid", "randomizer", "utility"}
)
public class RaidRandomizerPlugin extends Plugin
{
	@Inject private Client client;
	@Inject private ClientThread clientThread;
	@Inject private ScheduledExecutorService executor;
	@Inject private RaidRandomizerConfig config;
	@Inject private RaidIconManager raidIconManager;

	@Override
	protected void startUp() throws Exception
	{
		clientThread.invokeLater(() -> raidIconManager.load());
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			clientThread.invokeLater(() -> raidIconManager.load());
		}
	}


	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		ChatMessageType type = event.getType();

		if (type != ChatMessageType.PUBLICCHAT &&
				type != ChatMessageType.CLAN_CHAT &&
				type != ChatMessageType.FRIENDSCHAT &&
				type != ChatMessageType.PRIVATECHAT &&
				type != ChatMessageType.PRIVATECHATOUT)
		{
			return;
		}

		if (!"!raid".equalsIgnoreCase(event.getMessage()))
			return;

		MessageNode node = event.getMessageNode();


		// Capture seed for this spin
		long seed = config.useUtcSync()
				? getUtcEpoch() / 10      // 10-second deterministic bucket
				: System.nanoTime();     // true random seed
		List<String> available = new ArrayList<>(getAvailableRaids());

// Shuffle bait animation so it’s different every time
		Collections.shuffle(available, new Random(seed * 1337));


		if (available.isEmpty())
		{
			node.setRuneLiteFormatMessage("<col=ffffff>No raids enabled</col>");
			client.refreshChat();
			return;
		}
		int totalSpins = 28;
		long accumulatedDelay = 0;

		for (int i = 0; i < totalSpins; i++)
		{
			int base = config.spinSpeed() == RaidRandomizerConfig.SpinSpeed.FAST ? 5
					: config.spinSpeed() == RaidRandomizerConfig.SpinSpeed.SLOW ? 70
					: 35;

			double progress = (double) i / totalSpins;
			long delayStep = (long) (base + (progress * base * 3));

			accumulatedDelay += delayStep;

			int index = i;
			long scheduledTime = accumulatedDelay;

			executor.schedule(() ->
							clientThread.invoke(() ->
							{
								String name = available.get(index % available.size());

								node.setRuneLiteFormatMessage(
										"<col=ffff00>🎰 " + name + "</col>"
								);
								client.refreshChat();

								if (config.enableSounds())
								{
									client.playSoundEffect(227);
								}
							}),
					scheduledTime,
					TimeUnit.MILLISECONDS
			);
		}

		// Near miss
		accumulatedDelay += 300;
		executor.schedule(() ->
						clientThread.invoke(() ->
						{
							node.setRuneLiteFormatMessage(
									"<col=ff0000>🎰 " + available.get(0) + "</col>"
							);
							client.refreshChat();
						}),
				accumulatedDelay,
				TimeUnit.MILLISECONDS
		);

		// Final reveal
		accumulatedDelay += 600;
		executor.schedule(() ->
						clientThread.invoke(() ->
						{
							String result = rollRaid(seed);

							if (config.enableSounds())
							{
								client.playSoundEffect(199);
							}

							node.setRuneLiteFormatMessage("<col=00ff00>" + result + "</col>");
							client.refreshChat();
						}),
				accumulatedDelay,
				TimeUnit.MILLISECONDS
		);
	}

	private List<String> getAvailableRaids()
	{
		List<String> list = new ArrayList<>();

		if (config.useUtcSync())
		{
			// Sync mode always includes all raids
			list.add("Chambers of Xeric");
			list.add("Theatre of Blood");
			list.add("Tombs of Amascut");
			list.add("The Fractured Archive");
		}
		else
		{
			if (config.enableCox()) list.add("Chambers of Xeric");
			if (config.enableTob()) list.add("Theatre of Blood");
			if (config.enableToa()) list.add("Tombs of Amascut");
			if (config.enableTfa()) list.add("The Fractured Archive");
		}

		return list;
	}

	private long getUtcEpoch()
	{
		return Instant.now().getEpochSecond();
	}

	private String rollRaid(long seed)
	{
		// Build pool
		List<String> pool = config.useUtcSync()
				? Arrays.asList(
				"Chambers of Xeric",
				"Theatre of Blood",
				"Tombs of Amascut",
				"The Fractured Archive")
				: getAvailableRaids();

		// Debug pool + seed
		log.info("RR DEBUG: useUtcSync={}, seed={}", config.useUtcSync(), seed);
		log.info("RR DEBUG: pool={}", pool);

		// RNG
		Random random = new Random(seed);

		int idx = Math.floorMod(Long.hashCode(seed), pool.size());
		String selected = pool.get(idx);

//		// Debug selection
//		log.info("RR DEBUG: idx={}, selected=[{}]", idx, selected);
//
//		// Byte-level debugging to expose invisible characters
//		log.info("RR DEBUG: selected bytes={}", Arrays.toString(selected.getBytes()));
//
//		log.info("RR DEBUG: COX bytes={}", Arrays.toString("Chambers of Xeric".getBytes()));
//		log.info("RR DEBUG: TOB bytes={}", Arrays.toString("Theatre of Blood".getBytes()));
//		log.info("RR DEBUG: TOA bytes={}", Arrays.toString("Tombs of Amascut".getBytes()));
//		log.info("RR DEBUG: TFA bytes={}", Arrays.toString("The Fractured Archive".getBytes()));

		// Switch with debug
		switch (selected)
		{
			case "Chambers of Xeric":
				//log.info("RR DEBUG: MATCHED CASE — COX");
				return "<img=" + raidIconManager.getCoxChatIndex() + "> <col=00ff00>Chambers of Xeric</col>";

			case "Theatre of Blood":
				//log.info("RR DEBUG: MATCHED CASE — TOB");
				return "<img=" + raidIconManager.getTobChatIndex() + "> <col=ff0000>Theatre of Blood</col>";

			case "Tombs of Amascut":
				//log.info("RR DEBUG: MATCHED CASE — TOA");
				return "<img=" + raidIconManager.getToaChatIndex() + "> <col=cc6600>Tombs of Amascut</col>";

			case "The Fractured Archive":
				//log.info("RR DEBUG: MATCHED CASE — TFA");
				return "<img=" + raidIconManager.getTfaChatIndex() + "> <col=00cc00>The Fractured Archive</col>";

			default:
				//log.info("RR DEBUG: DEFAULT CASE HIT — selected did NOT match ANY case");
				return "<img=" + raidIconManager.getToaChatIndex() + "> <col=cc6600>Tombs of Amascut</col>";
		}
	}



	@Provides
	RaidRandomizerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(RaidRandomizerConfig.class);
	}
}