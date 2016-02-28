package io.pivio.dependencies;

import java.util.List;

interface DependencyReader {
    List<Dependency> readDependencies(String sourceRootDirectory);
}
