package net.runelite.client.plugins.ezolm;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.demonicgorilla.DemonicGorilla;
import net.runelite.client.task.Schedule;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.QueryRunner;

import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@PluginDescriptor(
        name = "Ez Olm"
)
@Slf4j
public class EzOlmPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private EzOlmOverlay overlay;

    private OlmAttack attack;

    @Getter
    @Setter
    private int recentProjectileId;

    public Overlay getOverlay()
    {
        return overlay;
    }

    @Override
    protected void startUp()
    {
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() throws Exception
    {
        overlayManager.remove(overlay);
    }
    @Schedule(
            period = 600,
            unit = ChronoUnit.MILLIS
    )

    public void update() {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        NPC olm = findOlm();

        /*
        if (olm != null) {
            if (olm.getAnimation() == 1528) {
                log.debug("MAIN ATTACK");
                attack = OlmAttack.MAGIC;
            } else if (olm.getAnimation() == 1529) { // OlmAttack.RANGE.getAnimation()
                log.debug("HE CASTED IT");
                attack = OlmAttack.RANGE;
            } else {
                attack = OlmAttack.NONE;
            }
        } else {
            attack = null;
        }
        */

        if (olm != null) {
            if (recentProjectileId == ProjectileID.OLM_MAGIC) {
                attack = OlmAttack.MAGIC;
            } else if (recentProjectileId == ProjectileID.OLM_RANGED) {
                attack = OlmAttack.RANGE;
            } else {
                attack = OlmAttack.NONE;
            }
        } else {
            attack = OlmAttack.NONE;
        }
    }

    private NPC findOlm() {
        List<NPC> npcs = client.getNpcs();

        NPC tempNPC = null;
        for (NPC npc : npcs) {
            if (npc.getId() == 7554) {
                tempNPC = npc;
                break;
            }
        }

        return tempNPC;
    }

    @Subscribe
    public void onProjectileMoved(ProjectileMoved event)
    {
        Projectile projectile = event.getProjectile();
        int projectileId = projectile.getId();

        if (projectileId != ProjectileID.OLM_MAGIC && projectileId != ProjectileID.OLM_RANGED) {
            return;
        }

        // The event fires once before the projectile starts moving,
        // and we only want to check each projectile once
        if (client.getGameCycle() >= projectile.getStartMovementCycle()) {
            return;
        }

        if (projectileId == ProjectileID.OLM_MAGIC) {
            recentProjectileId = projectileId;
            log.debug("MAGIC ATTACK FIRED");
        } else if (projectileId == ProjectileID.OLM_RANGED) {
            recentProjectileId = projectileId;
            log.debug("RANGED ATTACK FIRED");
        }
    }

    /*
    private NPC findJad()
    {
        Query query = new NPCQuery().nameContains("TzTok-Jad");
        NPC[] result = queryRunner.runQuery(query);
        return result.length >= 1 ? result[0] : null;
    }
    */

    public OlmAttack getAttack()
    {
        return attack;
    }
}
