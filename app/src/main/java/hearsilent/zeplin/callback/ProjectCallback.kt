package hearsilent.zeplin.callback

import hearsilent.zeplin.models.ProjectModel

abstract class ProjectCallback {
    abstract fun onSuccess(project: ProjectModel)
    abstract fun onFail(errorMessage: String?)
}