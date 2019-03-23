package net.runelite.client.plugins.ezolm;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.ComponentConstants;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Slf4j
public class EzOlmOverlay extends Overlay {
    private static final Color NOT_ACTIVATED_BACKGROUND_COLOR = new Color(150, 0, 0, 150);

    private final Client client;
    private final EzOlmPlugin plugin;
    private final PanelComponent imagePanelComponent = new PanelComponent();
    private BufferedImage protectFromMagicImg;
    private BufferedImage protectFromMissilesImg;

    @Inject
    EzOlmOverlay(Client client, EzOlmPlugin plugin)
    {
        setPosition(OverlayPosition.BOTTOM_RIGHT);
        setPriority(OverlayPriority.HIGH);
        this.client = client;
        this.plugin = plugin;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        OlmAttack attack = plugin.getAttack();

        if (attack == null)
        {
            return null;
        }

        final BufferedImage prayerImage = getPrayerImage(attack);

        imagePanelComponent.getChildren().clear();
        imagePanelComponent.getChildren().add(new ImageComponent(prayerImage));
        imagePanelComponent.setBackgroundColor(client.isPrayerActive(attack.getPrayer())
                ? ComponentConstants.STANDARD_BACKGROUND_COLOR
                : NOT_ACTIVATED_BACKGROUND_COLOR);

        return imagePanelComponent.render(graphics);
    }

    private BufferedImage getPrayerImage(OlmAttack attack)
    {
        return attack == OlmAttack.MAGIC ? getProtectFromMagicImage() : getProtectFromMissilesImage();
    }

    private BufferedImage getProtectFromMagicImage()
    {
        if (protectFromMagicImg == null)
        {
            String path = "/prayers/protect_from_magic.png";
            protectFromMagicImg = getImage(path);
        }
        return protectFromMagicImg;
    }

    private BufferedImage getProtectFromMissilesImage()
    {
        if (protectFromMissilesImg == null)
        {
            String path = "/prayers/protect_from_missiles.png";
            protectFromMissilesImg = getImage(path);
        }
        return protectFromMissilesImg;
    }

    private BufferedImage getImage(String path)
    {
        BufferedImage image = null;
        try
        {
            synchronized (ImageIO.class)
            {
                image = ImageIO.read(EzOlmOverlay.class.getResourceAsStream(path));
            }
        }
        catch (IOException e)
        {
            log.warn("Error loading image", e);

        }
        return image;
    }
}
