package mformetal.metallic

import mformetal.metallic.dagger.AppComponent
import mformetal.metallic.dagger.DaggerTestAppComponent
import mformetal.metallic.dagger.TestAppModule

/**
 * Created by peelemil on 12/7/17.
 */
class MockApp : App() {

    override fun createAppComponent(): AppComponent {
        return DaggerTestAppComponent.builder()
                .testAppModule(TestAppModule(this))
                .build()
    }
}