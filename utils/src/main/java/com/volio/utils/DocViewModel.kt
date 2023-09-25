//package volio.tech.documentreader.util
//
//import android.content.ContentResolver
//import android.content.ContentValues
//import android.content.Context
//import android.media.MediaScannerConnection
//import android.net.Uri
//import android.os.Build
//import android.provider.MediaStore
//import android.util.Log
//import androidx.core.net.toUri
//import androidx.lifecycle.ViewModel
//import dagger.hilt.android.lifecycle.HiltViewModel
//import dagger.hilt.android.qualifiers.ApplicationContext
//import volio.tech.documentreader.business.domain.Document
//import java.io.File
//import javax.inject.Inject
//
//@HiltViewModel
//class DocViewModel
//@Inject
//constructor(
//    @ApplicationContext private val application: Context
//) : ViewModel() {
//
//    fun renameFile(
//        documentLocal: Document,
//        newName: String,
//        listDocument: List<Document>
//    ): Document {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
//            try {
//                val path = documentLocal.path.replace(
//                    documentLocal.name + "." + documentLocal.typeMediaDetail,
//                    ""
//                )
//                val type = documentLocal.typeMediaDetail
//                val name = getUniqueName(newName, listDocument, type, documentLocal)
//                val from = File(path, documentLocal.name + "." + documentLocal.typeMediaDetail)
//                val to = File(path, "$name.$type")
//                if (from.renameTo(to)) {
//                    documentLocal.name = name
//                    documentLocal.path =
//                        "$path$name.$type"
//                    documentLocal.uri = Uri.fromFile(to).toString()
//
//                    MediaScannerConnection.scanFile(
//                        application, arrayOf(documentLocal.path),
//                        null, null
//                    )
//                    return documentLocal
//                }
//            } catch (ex: Exception) {
//                ex.printStackTrace()
//            }
//        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
//            try {
//                val type = documentLocal.typeMediaDetail
//                val name = getUniqueName(newName, listDocument, type, documentLocal)
//                val from = File(
//                    documentLocal.pathFolder,
//                    documentLocal.name + "." + documentLocal.typeMediaDetail
//                )
//                val to = File(documentLocal.pathFolder, "$name.$type")
//                Log.d("HOneHUYAA", name)
//                Log.d("HOneHUYAA", documentLocal.path)
//                Log.d(
//                    "HOneHUYAA",
//                    documentLocal.pathFolder + documentLocal.name + "." + documentLocal.typeMediaDetail
//                )
//                Log.d("HOneHUYAA", from.exists().toString())
//
//                if (from.renameTo(to)) {
//                    documentLocal.name = name
//                    documentLocal.path =
//                        documentLocal.pathFolder + name + "." + type
//                    documentLocal.uri = Uri.fromFile(to).toString()
//
//                    MediaScannerConnection.scanFile(
//                        application, arrayOf(documentLocal.path),
//                        null, null
//                    )
//                    Log.d("HOneHUYAA", documentLocal.toString())
//                    return documentLocal
//                }
//            } catch (ex: Exception) {
//                ex.printStackTrace()
//            }
//        } else {
//            try {
//                val type = documentLocal.typeMediaDetail
//                val name = getUniqueName(newName, listDocument, type, documentLocal)
//                val contentResolver: ContentResolver = application.contentResolver
//                val contentValues = ContentValues()
//                contentValues.put(MediaStore.Files.FileColumns.DISPLAY_NAME, name)
//                contentResolver.update(documentLocal.uri.toUri(), contentValues, null, null)
//                documentLocal.name = name
////                documentLocal.path =
////                    documentLocal.pathFolder + name + "." + type
//
//                MediaScannerConnection.scanFile(
//                    application, arrayOf(documentLocal.path),
//                    null, null
//                )
//                return documentLocal
//            } catch (ex: Exception) {
//                ex.printStackTrace()
//            }
//        }
//
//        return documentLocal
//    }
//
//    fun deleteFile(documentLocal: Document): Boolean {
//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
//            try {
//                if (File(documentLocal.path).delete()) {
//                    MediaScannerConnection.scanFile(
//                        application, arrayOf(documentLocal.path),
//                        null, null
//                    )
//                    MediaScannerConnection.scanFile(
//                        application, arrayOf(documentLocal.path),
//                        null, null
//                    )
//                    return true
//                }
//            } catch (ex: Exception) {
//                ex.printStackTrace()
//            }
//        } else {
//            try {
//                val contentResolver: ContentResolver = application.contentResolver
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
//                    contentResolver.delete(documentLocal.uri.toUri(), null)
//                else contentResolver.delete(documentLocal.uri.toUri(), null, null)
//
//                MediaScannerConnection.scanFile(
//                    application, arrayOf(documentLocal.path),
//                    null, null
//                )
//                return true
//            } catch (ex: Exception) {
//                ex.printStackTrace()
//            }
//        }
//        return false
//    }
//
//    private fun getUniqueName(
//        newName: String,
//        listDocument: List<Document>,
//        type: String,
//        documentLocal: Document
//    ): String {
//        var index = 0
//        while (checkName(newName, index, listDocument, type, documentLocal)) {
//            index++
//        }
//        return if (index == 0) newName
//        else "$newName($index)"
//    }
//
//    private fun checkName(
//        newName: String,
//        index: Int,
//        listDocument: List<Document>,
//        type: String,
//        documentLocal: Document,
//    ): Boolean {
//        listDocument.find {
//            if (index == 0) "${it.name}.${it.typeMediaDetail}" == "$newName.$type"
//            else "${it.name}.${it.typeMediaDetail}" == "$newName($index).$type"
//        }?.let {
//            if (documentLocal.idDocument != it.idDocument) {
//                return true
//            } else return true
//        } ?: kotlin.runCatching {
//            return false
//        }
//
//        return false
//    }
//}
