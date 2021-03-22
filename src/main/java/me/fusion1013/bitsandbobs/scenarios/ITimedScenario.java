package me.fusion1013.bitsandbobs.scenarios;

public interface ITimedScenario extends IScenario {
    void QueueScenario(int delay, int randomDelay);
}
