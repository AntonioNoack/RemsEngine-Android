package me.anno.language.spellcheck;

import java.util.concurrent.ConcurrentLinkedQueue;

import me.anno.language.Language;

@SuppressWarnings("unused")
public class BundledSpellcheck {

    @SuppressWarnings("CommentedOutCode")
    public static void runInstance(Language language, ConcurrentLinkedQueue<Object> queue) {
        /*try {
            CommandLine instance = new CommandLine(language.getCode());
            while (!Engine.INSTANCE.getShutdown()) {
                if (queue.isEmpty()) Sleep.INSTANCE.sleepShortly(false);
                else {
                    Object key = queue.keys().nextElement();
                    Request nextTask = queue.remove(key);
                    assert (nextTask != null);
                    List<Suggestion> result = instance.process(nextTask.getSentence());
                    nextTask.getCallback().invoke(result);
                }
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }*/
    }

}
