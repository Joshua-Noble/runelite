package net.runelite.client.plugins.ezolm;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.ezolm.config.IconSize;
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
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

@Slf4j
public class EzOlmOverlay extends Overlay {
    private static final Color NOT_ACTIVATED_BACKGROUND_COLOR = new Color(150, 0, 0, 150);

    // These are for testing overlay on top of Olm
    private static final Color COLOR_ICON_BACKGROUND = new Color(0, 0, 0, 128);
    private static final Color COLOR_ICON_BORDER = new Color(0, 0, 0, 255);
    private static final Color COLOR_ICON_BORDER_FILL = new Color(219, 175, 0, 255);
    private static final int OVERLAY_ICON_DISTANCE = 50;
    private static final int OVERLAY_ICON_MARGIN = 8;

    @Inject
    private SkillIconManager iconManager;

    @Inject
    private EzOlmConfig config;

    private final Client client;
    private final EzOlmPlugin plugin;
    private final PanelComponent panelComponent = new PanelComponent();
    private BufferedImage protectFromMagicImg;
    private BufferedImage protectFromMissilesImg;
    private IconSize iconSize;
    private IconSize prevSize;

    @Inject
    EzOlmOverlay(Client client, EzOlmPlugin plugin, EzOlmConfig config)
    {
        super(plugin);
        setPosition(OverlayPosition.TOP_RIGHT); // ABOVE_CHATBOX_RIGHT, TOP_CENTER
        //java.awt.Point tempPoint = new java.awt.Point(1000,500);
        //panelComponent.setPreferredLocation(tempPoint);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        iconSize = config.iconSize();
        prevSize = iconSize;
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

        //attack = OlmAttack.MAGIC;

        iconSize = config.iconSize();

        if (iconSize != prevSize) {
            protectFromMagicImg = null;
            protectFromMissilesImg = null;
            prevSize = iconSize;
        }

        final BufferedImage prayerImage = getPrayerImage(attack);

        if (!prayerString.equals("NONE")) { // add ! to this line when finished testing
            panelComponent.getChildren().add(new ImageComponent(prayerImage)); // this is the new line
            panelComponent.setBackgroundColor(client.isPrayerActive(attack.getPrayer())
                    ? ComponentConstants.STANDARD_BACKGROUND_COLOR
                    : NOT_ACTIVATED_BACKGROUND_COLOR);
        } else {
            panelComponent.setBackgroundColor(ComponentConstants.STANDARD_BACKGROUND_COLOR);
        }

        //log.debug("x: " + Double.toString(panelComponent.getBounds().getX()));
        //log.debug("y: " + Double.toString(panelComponent.getBounds().getY()));

        //Point panelLoc = new Point(100,100);

        return panelComponent.render(graphics);
    }

    /*
    private BufferedImage getIcon(OlmAttack attackStyle) // add some kind of null icon maybe? idk
    {
        switch (attackStyle)
        {
            case MAGIC: return iconManager.getSkillImage(Skill.MAGIC);
            case RANGE: return iconManager.getSkillImage(Skill.RANGED);
        }
        return null;
    }
    */

    private BufferedImage getPrayerImage(OlmAttack attack) {
        return attack == OlmAttack.MAGIC ? getProtectFromMagicImage() : getProtectFromMissilesImage();
    }

    private BufferedImage getProtectFromMagicImage() {
        if (protectFromMagicImg == null) {
            IconSize size = config.iconSize();

            String path = "";

            switch (size) {
                case DEFAULT:
                    path = "/prayers/protect_from_magic.png";
                    break;
                case MEDIUM:
                    path = "/prayers/protect_from_magic_medium.png";
                    break;
                case HUGE:
                    path = "/prayers/protect_from_magic_huge.png";
                    break;
            }
            protectFromMagicImg = getImage(path);
        }

        return protectFromMagicImg;
    }

    private BufferedImage getProtectFromMissilesImage() {
        if (protectFromMissilesImg == null) {
            IconSize size = config.iconSize();

            String path = "";

            switch (size) {
                case DEFAULT:
                    path = "/prayers/protect_from_missiles.png";
                    break;
                case MEDIUM:
                    path = "/prayers/protect_from_missiles_medium.png";
                    break;
                case HUGE:
                    path = "/prayers/protect_from_missiles_huge.png";
                    break;
            }
            protectFromMissilesImg = getImage(path);
        }

        return protectFromMissilesImg;
    }

    private BufferedImage getImage(String path) {
        BufferedImage image = null;
        try {
            synchronized (ImageIO.class) {
                image = ImageIO.read(EzOlmOverlay.class.getResourceAsStream(path));
            }
        } catch (IOException e) {
            log.warn("Error loading image", e);

        }

        return image;
    }
}
