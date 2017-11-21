package io.left.core.util.test.common.injection.component;

import javax.inject.Singleton;

import dagger.Component;
import io.left.core.util.injection.component.ApplicationComponent;
import io.left.core.util.test.common.injection.module.ApplicationTestModule;

@Singleton
@Component(modules = ApplicationTestModule.class)
public interface TestComponent extends ApplicationComponent {

}
