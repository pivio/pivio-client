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

    protected List<Dependency> applyFilterLists(List<Dependency> dependencies) {
        return applyBlackList(applyWhiteList(dependencies));
    }

    protected List<Dependency> applyWhiteList(List<Dependency> dependencies) {
        return applyFilterList(dependencies, configuration.WHITELIST, NO_DISCARD_MATCHES);
    }

    protected List<Dependency> applyBlackList(List<Dependency> dependencies) {
        return applyFilterList(dependencies, configuration.BLACKLIST, DISCARD_MATCHES);
    }

    private List<Dependency> applyFilterList(
        List<Dependency> dependencies,
        String[] filters,
        boolean discardingMatches
    ) {
        if(filters != null ) {
            List<Dependency> result = new ArrayList<>();
            if (filters.length > 0) {
                for (Dependency dependency : dependencies) {
                    boolean onList = false;
                    for (String regex : filters) {
                        if (dependency.name.matches(regex)){
                            onList = true;
                            continue;
                        }
                    }
                    if (discardingMatches) {
                        if(!onList) {
                            result.add(dependency);
                        }
                    } else {
                        if(onList) {
                            result.add(dependency);
                        }
                    }
                }
            } else {
                result = dependencies;
            }
            return result;
        } else {
            return dependencies;
        }
    }
}
