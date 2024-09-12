package de.oliver.fancyvisuals.config;

public class NametagConfig {

    private int amountWorkerThreads;
    private int distributionBucketSize;

    public NametagConfig() {
        amountWorkerThreads = 5;
        distributionBucketSize = 10;
    }

    public void load() {

    }

    /**
     * Retrieves the number of worker threads configured.
     *
     * @return The number of worker threads.
     */
    public int getAmountWorkerThreads() {
        return amountWorkerThreads;
    }

    /**
     * Retrieves the size of the distribution bucket configured.
     *
     * @return The size of the distribution bucket.
     */
    public int getDistributionBucketSize() {
        return distributionBucketSize;
    }
}
