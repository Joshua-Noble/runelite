package net.runelite.client.plugins.ezolm;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.ComponentConstants;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

@Slf4j
public class EzOlmOverlay extends Overlay {
    private static final Color NOT_ACTIVATED_BACKGROUND_COLOR = new Color(150, 0, 0, 150);

    private final Client client;
    private final EzOlmPlugin plugin;
    private final PanelComponent panelComponent = new PanelComponent();
    private BufferedImage protectFromMagicImg;
    private BufferedImage protectFromMissilesImg;

    @Inject
    EzOlmOverlay(Client client, EzOlmPlugin plugin)
    {
        super(plugin);
        setPosition(OverlayPosition.BOTTOM_RIGHT); // ABOVE_CHATBOX_RIGHT
        this.plugin = plugin;
        this.client = client;
        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Oz Olm overlay"));
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        panelComponent.getChildren().clear();

        OlmAttack attack = plugin.getAttack();
        String prayerString = "";

        if (attack == OlmAttack.MAGIC) {
            prayerString = "MAGIC";
        } else if (attack == OlmAttack.RANGE) {
            prayerString = "RANGE";
        } else {
            prayerString = "NONE";
        }

        panelComponent.getChildren().add(TitleComponent.builder()
                .text(prayerString)
                .color(Color.WHITE)
                .build());

        if (!prayerString.equals("NONE")) {
            panelComponent.setBackgroundColor(client.isPrayerActive(attack.getPrayer())
                    ? ComponentConstants.STANDARD_BACKGROUND_COLOR
                    : NOT_ACTIVATED_BACKGROUND_COLOR);
        } else {
            panelComponent.setBackgroundColor(ComponentConstants.STANDARD_BACKGROUND_COLOR);
        }

        panelComponent.setPreferredSize(new Dimension(
                graphics.getFontMetrics().stringWidth(prayerString) + 10,
                0));

        return panelComponent.render(graphics);
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
