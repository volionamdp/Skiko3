package com.volio.vn.models.transition

class TransitionGLSLEditModel(
    var transId: Int = 0,
    id: Long,
    idCategory: Long,
    name: String,
    remotePath: String,
    localPath: String,
    thumb: String,
    duration: Long,
) : TransitionEditModel(id, idCategory, name, remotePath, localPath, thumb, duration) {
}
