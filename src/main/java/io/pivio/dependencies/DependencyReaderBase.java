package io.pivio.dependencies;

import io.pivio.Configuration;
import java.util.ArrayList;
import java.util.List;

public abstract class DependencyReaderBase implements DependencyReader {

    protected final Configuration configuration;

    public DependencyReaderBase(Configuration configuration) {
        this.configuration = configuration;
    }

    public abstract List<Dependency> readDependencies(String sourceRootDirectory);

    public List<Dependency> applyBlackAndWhiteList(List<Dependency> dependencies) {
        return applyBlackList(applyWhiteList(dependencies));
    }

    protected List<Dependency> applyWhiteList(List<Dependency> dependencies) {
        List<Dependency> result = new ArrayList<>();
        if (configuration.WHITELIST.length > 0) {
            for (Dependency dependency : dependencies) {
                boolean onWhiteList = false;
                for (String regex : configuration.WHITELIST) {
                    if (dependency.name.matches(regex)){
                        onWhiteList = true;
                        continue;
                    }
                }
                if (onWhiteList) {
                    result.add(dependency);
                }
            }
        } else {
            result = dependencies;
        }
        return result;
    }

    protected List<Dependency> applyBlackList(List<Dependency> dependencies) {
        List<Dependency> result = new ArrayList<>();
        if (configuration.BLACKLIST.length > 0) {
            for (Dependency dependency : dependencies) {
                boolean onBlackList = false;
                for (String regex : configuration.BLACKLIST) {
                    if (dependency.name.matches(regex)){
                        onBlackList = true;
                        continue;
                    }
                }
                if (!onBlackList) {
                    result.add(dependency);
                }
            }
        } else {
            result = dependencies;
        }
        return result;
    }
}
