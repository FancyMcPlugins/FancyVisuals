package de.oliver.fancyvisuals.nametags.visibility;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import de.oliver.fancyvisuals.utils.distributedWorkload.DistributedWorkload;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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
     * DistributedWorkload instance responsible for managing and executing tasks related
     * to PlayerNametag objects. It divides the tasks across multiple buckets and performs
     * specified actions on each element. Actions include updating visibility and checking
     * whether a PlayerNametag needs to be updated.
     */
    private final DistributedWorkload<PlayerNametag> workload;

    public PlayerNametagScheduler(ExecutorService workerExecutor, int bucketSize) {
        this.schedulerExecutor = Executors.newSingleThreadScheduledExecutor(
                new ThreadFactoryBuilder()
                        .setNameFormat("PlayerNametagScheduler")
                        .build()
        );

        this.workload = new DistributedWorkload<>(
                "PlayerNametagWorkload",
                this::updateVisibility,
                (nt) -> !shouldUpdate(nt),
                bucketSize,
                workerExecutor
        );
    }

    private static boolean isInDistance(Location loc1, Location loc2, double distance) {
        return loc1.distanceSquared(loc2) <= distance * distance;
    }

    /**
     * Initializes the PlayerNametagScheduler and starts the periodic execution
     * of the DistributedWorkload<PlayerNametag>. The workload is scheduled to
     * run at a fixed rate with an initial delay of 0 seconds and a period of
     * 25 seconds between subsequent executions.
     */
    public void init() {
        schedulerExecutor.scheduleAtFixedRate(workload, 1000, 250, TimeUnit.MILLISECONDS);
    }

    public void add(PlayerNametag nametag) {
        workload.addValue(() -> nametag);
    }

    private void updateVisibility(PlayerNametag nametag) {
        if (!shouldUpdate(nametag)) {
            return;
        }

        for (Player viewer : Bukkit.getOnlinePlayers()) {
            boolean should = shouldBeVisibleTo(viewer, nametag);
            boolean is = nametag.isVisibleTo(viewer);

            if (should && !is) {
                nametag.showTo(viewer);
            } else if (!should && is) {
                nametag.hideFrom(viewer);
            }
        }

    }

    private boolean shouldBeVisibleTo(Player viewer, PlayerNametag nametag) {
        if (!viewer.getLocation().getWorld().getName().equals(nametag.getPlayer().getLocation().getWorld().getName())) {
            return false;
        }

        boolean dead = nametag.getPlayer().isDead();
        if (dead) {
            return false;
        }

        boolean inDistance = isInDistance(viewer.getLocation(), nametag.getPlayer().getLocation(), 24);
        if (!inDistance) {
            return false;
        }

        return true;
    }

    private boolean shouldUpdate(PlayerNametag playerNametag) {
        return playerNametag.getPlayer().isOnline();
    }

}
