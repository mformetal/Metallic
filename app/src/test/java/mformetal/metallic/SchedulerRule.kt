package mformetal.metallic

import io.reactivex.Scheduler
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.functions.Function
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.util.concurrent.Callable

/**
 * @author - mbpeele on 12/6/17.
 */
class SchedulerRule : TestRule {

    private val rxJavaImmediateSchedulerHandler = Function<Scheduler, Scheduler> { scheduler }
    private val rxAndroidImmediateSchedulerHandler = Function<Callable<Scheduler>, Scheduler> { scheduler }

    private val scheduler = Schedulers.trampoline()

    override fun apply(base: Statement, description: Description?): Statement {
        return object : Statement() {
            override fun evaluate() {
                RxAndroidPlugins.reset()
                RxAndroidPlugins.setInitMainThreadSchedulerHandler(rxAndroidImmediateSchedulerHandler)

                RxJavaPlugins.reset()
                RxJavaPlugins.setIoSchedulerHandler(rxJavaImmediateSchedulerHandler)
                RxJavaPlugins.setNewThreadSchedulerHandler(rxJavaImmediateSchedulerHandler)

                base.evaluate()

                RxAndroidPlugins.reset()
                RxJavaPlugins.reset()
            }
        }
    }
}