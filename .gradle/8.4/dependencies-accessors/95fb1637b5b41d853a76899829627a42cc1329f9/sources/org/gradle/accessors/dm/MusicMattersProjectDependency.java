package org.gradle.accessors.dm;

import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.ProjectDependency;
import org.gradle.api.internal.artifacts.dependencies.ProjectDependencyInternal;
import org.gradle.api.internal.artifacts.DefaultProjectDependencyFactory;
import org.gradle.api.internal.artifacts.dsl.dependencies.ProjectFinder;
import org.gradle.api.internal.catalog.DelegatingProjectDependency;
import org.gradle.api.internal.catalog.TypeSafeProjectDependencyFactory;
import javax.inject.Inject;

@NonNullApi
public class MusicMattersProjectDependency extends DelegatingProjectDependency {

    @Inject
    public MusicMattersProjectDependency(TypeSafeProjectDependencyFactory factory, ProjectDependencyInternal delegate) {
        super(factory, delegate);
    }

    /**
     * Creates a project dependency on the project at path ":core"
     */
    public CoreProjectDependency getCore() { return new CoreProjectDependency(getFactory(), create(":core")); }

    /**
     * Creates a project dependency on the project at path ":mobile"
     */
    public MobileProjectDependency getMobile() { return new MobileProjectDependency(getFactory(), create(":mobile")); }

}