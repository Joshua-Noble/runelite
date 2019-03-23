package net.runelite.client.plugins.ezolm;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.Query;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.task.Schedule;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.util.QueryRunner;

import javax.inject.Inject;
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
    private QueryRunner queryRunner;

    @Inject
    private EzOlmOverlay overlay;

    private OlmAttack attack;

    public Overlay getOverlay()
    {
        return overlay;
    }

    @Schedule(
            period = 600,
            unit = ChronoUnit.MILLIS
    )
    public void update()
    {
        if (client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }

        NPC jad = findOlm();
        if (jad != null)
        {
            if (jad.getAnimation() == OlmAttack.MAGIC.getAnimation())
            {
                attack = OlmAttack.MAGIC;
            }
            else if (jad.getAnimation() == OlmAttack.RANGE.getAnimation())
            {
                attack = OlmAttack.RANGE;
            }
        }
        else
        {
            attack = null;
        }
    }

    private NPC findOlm() {
        List<NPC> npcs = client.getNpcs();

        NPC tempNPC = null;
        for (NPC npc : npcs) {
            log.debug(npc.getName());
            if (npc.getId() == 4148) {
                tempNPC = npc;
            }
        }

        return tempNPC;
    }
    /*
    private NPC findJad()
    {
        Query query = new NPCQuery().nameContains("TzTok-Jad");
        NPC[] result = queryRunner.runQuery(query);
        return result.length >= 1 ? result[0] : null;
    }
    */

    OlmAttack getAttack()
    {
        return attack;
    }
}
