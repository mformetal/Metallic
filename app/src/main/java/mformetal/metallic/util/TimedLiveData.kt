package mformetal.metallic.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.annotation.MainThread
import androidx.annotation.Nullable
import android.util.Log
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.processors.PublishProcessor
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.reactivestreams.Publisher
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by peelemil on 12/1/17.
 */
class TimedLiveData<T>(interval: Long,
                       private val timeUnit: TimeUnit) : MutableLiveData<T>() {

    private val intervalInTimeUnit = timeUnit.convert(interval, timeUnit)
    private var start : Long = 0L
    private var firstRun = true

    @MainThread
    override fun setValue(@Nullable value: T?) {
        if (firstRun) {
            firstRun = false
            start = System.nanoTime()
            super.setValue(value)
        } else {
            val now = System.nanoTime()
            val diff = now - start
            val diffInTimeUnit = timeUnit.convert(diff, TimeUnit.NANOSECONDS)
            if (diffInTimeUnit > intervalInTimeUnit) {
                start = System.nanoTime()
                super.setValue(value)
            }
        }
    }
}