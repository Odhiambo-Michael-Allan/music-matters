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
public class CoreProjectDependency extends DelegatingProjectDependency {

    @Inject
    public CoreProjectDependency(TypeSafeProjectDependencyFactory factory, ProjectDependencyInternal delegate) {
        super(factory, delegate);
    }

    /**
     * Creates a project dependency on the project at path ":core:common"
     */
    public Core_CommonProjectDependency getCommon() { return new Core_CommonProjectDependency(getFactory(), create(":core:common")); }

    /**
     * Creates a project dependency on the project at path ":core:data"
     */
    public Core_DataProjectDependency getData() { return new Core_DataProjectDependency(getFactory(), create(":core:data")); }

    /**
     * Creates a project dependency on the project at path ":core:designsystem"
     */
    public Core_DesignsystemProjectDependency getDesignsystem() { return new Core_DesignsystemProjectDependency(getFactory(), create(":core:designsystem")); }

    /**
     * Creates a project dependency on the project at path ":core:i8n"
     */
    public Core_I8nProjectDependency getI8n() { return new Core_I8nProjectDependency(getFactory(), create(":core:i8n")); }

    /**
     * Creates a project dependency on the project at path ":core:model"
     */
    public Core_ModelProjectDependency getModel() { return new Core_ModelProjectDependency(getFactory(), create(":core:model")); }

    /**
     * Creates a project dependency on the project at path ":core:testing"
     */
    public Core_TestingProjectDependency getTesting() { return new Core_TestingProjectDependency(getFactory(), create(":core:testing")); }

}
