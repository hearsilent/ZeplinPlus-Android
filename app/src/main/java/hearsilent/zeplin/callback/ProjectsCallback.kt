package hearsilent.zeplin.callback

import hearsilent.zeplin.models.ProjectModel

abstract class ProjectsCallback {
    abstract fun onSuccess(projects: List<ProjectModel>)
    abstract fun onFail(errorMessage: String?)
}