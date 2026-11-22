package dev.minime;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/** Мемы по словам из чата: большая надпись, частицы, звук и реакция чибиков. */
public class Memes {
    enum Act { NONE, SPIN_ALL, HOP_ALL }

    record Meme(String key, Pattern p, String text, int color, String fx, String sound, Act act) {}

    private static Pattern w(String words) {
        return Pattern.compile("(?iu)(?<![\\p{L}\\p{N}_])(?:" + words + ")(?![\\p{L}\\p{N}_])");
    }

    static final List<Meme> LIST = List.of(
            new Meme("42", w("42"), "42 БРАТУХА", 0xFFD93B, "stars", "entity.villager.yes", Act.SPIN_ALL),
            new Meme("mogged", w("mogged|mog|могд|моггед|могнул"), "MOGGED", 0xFF4D4D, "sparks", "entity.iron_golem.damage", Act.HOP_ALL),
            new Meme("sigma", w("sigma|сигма"), "SIGMA", 0xC8C8C8, "stars", "block.beacon.activate", Act.HOP_ALL),
            new Meme("skibidi", w("skibidi|скибиди"), "SKIBIDI", 0x55FFFF, "notes", "block.note_block.bass", Act.SPIN_ALL),
            new Meme("rizz", w("rizz|ризз|риз"), "RIZZ", 0xFF77CC, "hearts", "entity.cat.purr", Act.HOP_ALL),
            new Meme("gg", w("gg|гг"), "GG", 0x66FF66, "firework", "entity.firework_rocket.blast", Act.NONE),
            new Meme("ez", w("ez|изи"), "EZ", 0x66CCFF, "crit", "entity.player.attack.crit", Act.NONE),
            new Meme("f", Pattern.compile("(?iu)^\\s*[fф]\\s*$"), "F", 0xAAAAAA, "souls", "entity.allay.death", Act.NONE),
            new Meme("pog", w("pog|pogchamp|пог"), "POG!", 0xFFEE55, "joy", "entity.player.levelup", Act.HOP_ALL),
            new Meme("kek", w("kek|kekw|кек|lol|лол|xd|ахах\\p{L}*|хаха\\p{L}*"), "KEKW", 0xFFAA33, "joy", "entity.villager.celebrate", Act.HOP_ALL),
            new Meme("bonk", w("bonk|бонк"), "BONK", 0xFF8844, "crit", "entity.iron_golem.attack", Act.HOP_ALL),
            new Meme("rip", w("rip|рип"), "RIP", 0x999999, "souls", "entity.skeleton.death", Act.NONE),
            new Meme("sus", w("sus|сус|amogus|амогус|among us"), "SUS", 0xFF3333, "angry", "entity.villager.no", Act.SPIN_ALL),
            new Meme("gm", w("gm|доброе утро"), "GM", 0xFFDD66, "joy", "entity.chicken.ambient", Act.NONE),
            new Meme("ratio", w("ratio|ратио"), "RATIO", 0xFF66FF, "sparks", "item.trident.thunder", Act.NONE));

    public static Map<String, String> defaultSounds() {
        Map<String, String> m = new LinkedHashMap<>();
        for (Meme x : LIST) m.put(x.key(), x.sound());
        return m;
    }

    private static long lastAny = 0;

    /** Главный поток. */
    public static void handle(String user, String text) {
        Config c = MiniMeClient.config;
        if (c == null || !c.memes) return;
        long now = System.currentTimeMillis();
        if (now - lastAny < 1500) return;
        for (Meme m : LIST) {
            if (!m.p().matcher(text).find()) continue;
            lastAny = now;
            MiniHud.addPopup(m.text(), m.color());
            MiniManager.burstOwner(user, m.fx(), 24);
            Sfx.play(c.memeSounds.getOrDefault(m.key(), m.sound()), 1.0f, 1.0f);
            if (m.act() == Act.SPIN_ALL) MiniManager.spinAll(2200);
            else if (m.act() == Act.HOP_ALL) MiniManager.hopAll(0.6);
            return;
        }
    }
}
