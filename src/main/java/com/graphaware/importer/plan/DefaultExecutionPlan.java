package com.graphaware.importer.plan;

import com.graphaware.importer.cache.Caches;
import com.graphaware.importer.context.ImportContext;
import com.graphaware.importer.importer.Importer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Default implementation of {@link com.graphaware.importer.plan.ExecutionPlan}. Makes sure that as many {@link com.graphaware.importer.importer.Importer}s
 * as possible are run in parallel.
 */
public class DefaultExecutionPlan implements ExecutionPlan {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultExecutionPlan.class);

    private final Caches caches;

    private final Set<Importer> importers;
    private final Map<Importer, Set<String>> neededCaches;
    private final Map<String, Importer> cacheCreators;

    private List<Importer> orderedImporters;

    /**
     * Create a new plan.
     *
     * @param importers     all importers.
     * @param importContext import context.
     */
    public DefaultExecutionPlan(Set<Importer> importers, ImportContext importContext) {
        if (importers.isEmpty()) {
            throw new IllegalStateException("There are no importers");
        }

        this.importers = importers;
        this.caches = importContext.caches();
        this.neededCaches = neededCaches();
        this.cacheCreators = cacheCreators();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<Importer> getOrderedImporters() {
        if (orderedImporters == null) {
            synchronized (this) {
                if (orderedImporters == null) {
                    orderedImporters = order(importers);
                }
            }
        }

        return orderedImporters;
    }

    /**
     * Order the importers by looking at what caches they create, what caches they need, and how they thus depend on
     * each other. The order of two importers that do not depend on each other is undefined.
     *
     * @param importers to order.
     * @return ordered importers.
     */
    protected List<Importer> order(Set<Importer> importers) {
        Set<Importer> remaining = new HashSet<>(importers);
        List<Importer> result = new LinkedList<>();

        while (!remaining.isEmpty()) {
            Importer next = findNext(remaining, neededCaches, cacheCreators);
            if (next == null) {
                reportCycle(remaining);
            }
            result.add(next);
            remaining.remove(next);
        }

        return result;
    }

    /**
     * Report a dependency cycle in the modules by throwing an {@link java.lang.IllegalStateException}.
     *
     * @param remaining remaining modules, some of which form a cycle.
     */
    protected void reportCycle(Set<Importer> remaining) {
        StringBuilder message = new StringBuilder("It looks like there is a dependency cycle between some of the following importers:");
        for (Importer importer : remaining) {
            message.append(importer.name()).append(", ");
        }
        String msg = message.toString().substring(0, message.length() - 2);

        LOG.error(msg);
        throw new IllegalArgumentException(msg);
    }

    /**
     * Find the next importer from the set of remaining importers that has no dependency on any remaining importer.
     *
     * @param remaining     remaining importers.
     * @param neededCaches  the full map of importers and their needed caches.
     * @param cacheCreators a map of cache to its creator.
     * @return next importer with no dependencies, <code>null</code> if none exists.
     */
    private Importer findNext(Set<Importer> remaining, Map<Importer, Set<String>> neededCaches, Map<String, Importer> cacheCreators) {
        for (Importer candidate : remaining) {
            if (noRemainingCreators(remaining, cacheCreators, candidate, neededCaches.get(candidate))) {
                return candidate;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean allFinished() {
        for (Importer importer : importers) {
            if (hasNotFinished(importer)) {
                return false;
            }
        }

        LOG.info("All importers finished.");
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean canRun(Importer importer) {
        Set<Importer> remaining = new HashSet<>();
        for (Importer candidate : importers) {
            if (hasNotFinished(candidate)) {
                remaining.add(candidate);
            }
        }

        boolean result = canRun(importer, remaining, neededCaches, cacheCreators);

        if (result) {
            LOG.info("Importer " + importer.name() + " CAN run now.");
        }
        else {
            LOG.info("Importer " + importer.name() + " CANNOT run yet.");
        }

        return result;
    }

    /**
     * Check if the given importer can run now, i.e., that there are no unfinished importers that the given one depends on,
     * and the given one hasn't started yet.
     *
     * @param importer      to check.
     * @param notFinished   set of all unfinished importers.
     * @param neededCaches  the full map of importers and their needed caches.
     * @param cacheCreators a map of cache to its creator.
     * @return <code>true</code> iff there are no dependencies.
     */
    private boolean canRun(Importer importer, Set<Importer> notFinished, Map<Importer, Set<String>> neededCaches, Map<String, Importer> cacheCreators) {
        return noRemainingCreators(notFinished, cacheCreators, importer, neededCaches.get(importer)) && importer.getState() == Importer.State.NOT_STARTED;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void clearCaches() {
        Collection<Importer> remaining = new HashSet<>();
        for (Importer importer : importers) {
            if (hasNotFinished(importer)) {
                remaining.add(importer);
            }
        }
        caches.cleanup(remaining);
    }

    /**
     * Check if there are no remaining (unfinished) importers that create at least one of the caches needed for the given importer.
     *
     * @param remaining     remaining (unfinished) importers.
     * @param cacheCreators a map of cache to its creator.
     * @param candidate     to check.
     * @param needed        needed caches of the candidate.
     * @return <code>true</code> iff there are no creators of needed caches in the remaining importers (apart from the candidate itself).
     * @throws java.lang.IllegalStateException if there is a cache that has no creator.
     */
    private boolean noRemainingCreators(Set<Importer> remaining, Map<String, Importer> cacheCreators, Importer candidate, Set<String> needed) {
        for (String neededCache : needed) {
            Importer creator = cacheCreators.get(neededCache);
            if (creator == null) {
                throw new IllegalStateException("No creator defined for cache " + neededCache);
            }
            if (!creator.equals(candidate) && remaining.contains(creator)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Create a map of importers and all the caches they need.
     *
     * @return map.
     */
    private Map<Importer, Set<String>> neededCaches() {
        Map<Importer, Set<String>> result = new HashMap<>();

        for (Importer importer : importers) {
            result.put(importer, caches.neededCaches(importer));
        }

        return result;
    }

    /**
     * Create a map of cache names and importers that create them.
     *
     * @return map.
     * @throws java.lang.IllegalStateException if a cache is created by more than one importer.
     */
    private Map<String, Importer> cacheCreators() {
        Map<String, Importer> result = new HashMap<>();

        for (Importer importer : importers) {
            for (String cache : caches.createdCaches(importer)) {
                if (result.containsKey(cache)) {
                    throw new IllegalStateException("Cache " + cache + " is created by more than one importer!");
                }

                result.put(cache, importer);
            }
        }

        return result;
    }

    private boolean hasFinished(Importer importer) {
        return importer.getState() == Importer.State.FINISHED;
    }

    private boolean hasNotFinished(Importer importer) {
        return !hasFinished(importer);
    }
}
