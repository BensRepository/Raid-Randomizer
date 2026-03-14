package com.github.bensrepository.raidrandomizer;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.game.ChatIconManager;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Singleton
public class RaidIconManager
{
    @Inject
    private ChatIconManager chatIconManager;

    @Getter
    private int coxIconId;

    @Getter
    private int tobIconId;

    @Getter
    private int toaIconId;

    public void load()
    {
        try
        {
            log.info("Loading raid icons...");

            coxIconId = register("cox.png");
            tobIconId = register("tob.png");
            toaIconId = register("toa.png");

            log.info("Registered icons -> COX={}, TOB={}, TOA={}", coxIconId, tobIconId, toaIconId);
        }
        catch (Exception e)
        {
            log.error("Failed to load raid icons", e);
        }
    }

    private int register(String fileName)
    {
        BufferedImage image = loadImage(fileName);

        if (image == null)
        {
            log.error("Image load returned null for {}", fileName);
            return -1;
        }

        int id = chatIconManager.registerChatIcon(image);

        log.debug("Registered chat icon {} with id {}", fileName, id);

        return id;
    }

    private BufferedImage loadImage(String fileName)
    {
        try (InputStream stream = RaidIconManager.class.getResourceAsStream(fileName))
        {
            if (stream == null)
            {
                log.error("Resource not found: {}", fileName);
                return null;
            }

            BufferedImage image = ImageIO.read(stream);

            if (image == null)
            {
                log.error("ImageIO failed to read {}", fileName);
            }

            return image;
        }
        catch (IOException e)
        {
            log.error("Error loading image {}", fileName, e);
            return null;
        }
    }

    public int getCoxChatIndex()
    {
        return chatIconManager.chatIconIndex(coxIconId);
    }

    public int getTobChatIndex()
    {
        return chatIconManager.chatIconIndex(tobIconId);
    }

    public int getToaChatIndex()
    {
        return chatIconManager.chatIconIndex(toaIconId);
    }
}