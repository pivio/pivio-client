package io.servicecat.dependencies;

import java.util.List;

public interface DependencyReader {
    List<Dependency> readDependencies(String sourceRootDirectory);
}
