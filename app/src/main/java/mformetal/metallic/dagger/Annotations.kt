package mformetal.metallic.dagger

import android.arch.lifecycle.ViewModel
import dagger.MapKey
import javax.inject.Scope
import kotlin.reflect.KClass


/**
 * Created by mbpeele on 11/18/17.
 */
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ActivityScope

@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)