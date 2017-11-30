package mformetal.metallic.core

import com.evernote.android.job.Job
import com.evernote.android.job.JobCreator
import javax.inject.Inject

/**
 * Created by peelemil on 11/30/17.
 */
class MetallicJobCreator @Inject constructor(): JobCreator {

    override fun create(tag: String): Job? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

class MetallicSyncJob @Inject constructor(): Job() {
    override fun onRunJob(params: Params): Result {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        fun scheduleJob() {
            TODO()
        }
    }
}