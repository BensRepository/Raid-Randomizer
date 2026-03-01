package org.RaidRandomizer;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.game.ChatIconManager;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

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
        try
        {
            // Try classpath resource
            InputStream stream = getClass().getClassLoader().getResourceAsStream(fileName);

            if (stream == null)
            {
                log.warn("Resource not found via classloader: {}", fileName);
                debugClasspathResources();
                return null;
            }

            BufferedImage image = ImageIO.read(stream);
            stream.close();

            if (image == null)
            {
                log.error("ImageIO returned null for {}", fileName);
            }

            return image;
        }
        catch (IOException e)
        {
            log.error("Error loading image: {}", fileName, e);
            return null;
        }
    }

    private void debugClasspathResources()
    {
        try
        {
            Enumeration<URL> resources = getClass().getClassLoader().getResources("");

            log.info("Classpath roots:");
            while (resources.hasMoreElements())
            {
                URL url = resources.nextElement();
                log.info(" - {}", url);
            }

            log.info("Searching for PNG resources...");

            Enumeration<URL> pngs = getClass().getClassLoader().getResources(".");

            while (pngs.hasMoreElements())
            {
                URL url = pngs.nextElement();
                if (url.toString().endsWith(".png"))
                {
                    log.info("Found PNG: {}", url);
                }
            }
        }
        catch (IOException e)
        {
            log.error("Failed to debug classpath", e);
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