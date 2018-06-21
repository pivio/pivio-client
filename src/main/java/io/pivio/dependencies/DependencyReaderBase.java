package io.pivio.dependencies;

import io.pivio.Configuration;
import java.util.ArrayList;
import java.util.List;

public abstract class DependencyReaderBase implements DependencyReader {

    private static final boolean DISCARD_MATCHES = true;
    private static final boolean NO_DISCARD_MATCHES = false;

    protected final Configuration configuration;

    public DependencyReaderBase(Configuration configuration) {
        this.configuration = configuration;
    }

    public abstract List<Dependency> readDependencies(String sourceRootDirectory);

    /**
     * @return List<Dependency> filtered via a permissive method first and
     *                          then filtering via restrictive method described
     *                          on {@link #applyBlackList} & {@link #applyWhiteList}
     */
    protected List<Dependency> applyFilterLists(List<Dependency> dependencies) {
        return applyBlackList(applyWhiteList(dependencies));
    }

    /**
     * @return List<Dependency> filtered via a permissive method using a list
     *                          of regular expresions described on whitelist
     *                          property of pivio file
     */
    protected List<Dependency> applyWhiteList(List<Dependency> dependencies) {
        return applyFilterList(dependencies, configuration.WHITELIST, NO_DISCARD_MATCHES);
    }

    /**
     * @return List<Dependency> filtered via a restrictive method using a list
     *                          of regular expressions described on blacklist
     *                          property of pivio file
     */
    protected List<Dependency> applyBlackList(List<Dependency> dependencies) {
        return applyFilterList(dependencies, configuration.BLACKLIST, DISCARD_MATCHES);
    }

    /**
     * @param dependencies      list to filter
     * @param filters           list of regular expressions declarated on the pivio
     *                          file by whitelist or blacklist
     * @param discardingMatches indication to discard or not discard the matches
     */
    private List<Dependency> applyFilterList(
        List<Dependency> dependencies,
        String[] filters,
        boolean discardingMatches
    ) {
        if(filters != null && filters.length > 0) {
            List<Dependency> result = new ArrayList<>();

                for (Dependency dependency : dependencies) {
                    boolean isMatched = false;
                    for (String regex : filters) {
                        if (dependency.name.matches(regex)){
                            isMatched = true;
                            continue;
                        }
                    }
                    if (discardingMatches && !isMatched) {
                        result.add(dependency);
                    } else if(!discardingMatches && isMatched) {
                        result.add(dependency);
                    }
                }

            return result;
        } else {
            return dependencies;
        }
    }
}
