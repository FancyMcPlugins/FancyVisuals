package de.oliver.fancyvisuals.nametags.visibility;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import de.oliver.fancyvisuals.utils.distributedWorkload.DistributedWorkload;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlayerNametagScheduler {

    /**
     * ScheduledExecutorService instance responsible for scheduling periodic execution of
     * the DistributedWorkload<PlayerNametag>. It manages the timing and frequency
     * of workload distribution, ensuring that tasks are run at fixed intervals.
     */
    private final ScheduledExecutorService schedulerExecutor;

    /**
     * ExecutorService instance used for executing tasks related to
     * PlayerNametag operations. It uses a fixed thread pool to manage
     * the creation and execution of threads, ensuring that
     * multiple PlayerNametag tasks can run concurrently without exceeding
     * a specified number of threads. This helps in managing system resources
     * efficiently and preventing unnecessary load on the server.
     */
    private final ExecutorService workerExecutor;

    /**
     * DistributedWorkload instance responsible for managing and executing tasks related
     * to PlayerNametag objects. It divides the tasks across multiple buckets and performs
     * specified actions on each element. Actions include updating visibility and checking
     * whether a PlayerNametag needs to be updated.
     */
    private final DistributedWorkload<PlayerNametag> workload;

    public PlayerNametagScheduler() {
        this.schedulerExecutor = Executors.newSingleThreadScheduledExecutor(
                new ThreadFactoryBuilder()
                        .setNameFormat("PlayerNametagScheduler")
                        .build()
        );

        this.workerExecutor = Executors.newFixedThreadPool(
                10,
                new ThreadFactoryBuilder()
                        .setNameFormat("PlayerNametagWorker")
                        .build()
        );


        this.workload = new DistributedWorkload<>(
                "PlayerNametagWorkload",
                this::updateVisibility,
                (nt) -> !shouldUpdate(nt),
                5,
                workerExecutor
        );
    }

    /**
     * Initializes the PlayerNametagScheduler and starts the periodic execution
     * of the DistributedWorkload<PlayerNametag>. The workload is scheduled to
     * run at a fixed rate with an initial delay of 0 seconds and a period of
     * 25 seconds between subsequent executions.
     */
    public void init() {
        schedulerExecutor.scheduleAtFixedRate(workload, 0, 25, TimeUnit.SECONDS);
    }


    private void updateVisibility(PlayerNametag player) {
        if (!shouldUpdate(player)) {
            return;
        }

        // check if nametag should be visible to player and update visibility
    }

    private boolean shouldUpdate(PlayerNametag playerNametag) {
        return playerNametag.player().isOnline();
    }

}
