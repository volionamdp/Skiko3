package com.volio.skiko3

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.createchance.imageeditor.IEManager
import com.createchance.imageeditor.IESurfaceView
import com.volio.vn.models.ProjectModel
import com.volio.vn.models.RatioModel
import com.volio.vn.models.background.BackgroundEditBlurModel
import com.volio.vn.models.music.MusicModel
import com.volio.vn.models.music.ProjectMusicModel
import com.volio.vn.models.photo.PhotoEditModel
import java.util.UUID


class MainActivity : AppCompatActivity() {
    private var ieSurfaceView: IESurfaceView? = null
    private lateinit var iePreview: FrameLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        IEManager.getInstance().init(application)
        iePreview = findViewById(R.id.group)

        ieSurfaceView = IESurfaceView(this)
        iePreview.removeAllViews()
        iePreview.addView(
            ieSurfaceView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        setData()
        IEManager.getInstance().startEngine()
        IEManager.getInstance().attachPreview(ieSurfaceView!!)
        IEManager.getInstance().setData(projectModel)
        IEManager.getInstance().play()
    }

    private val duration = 3000L
    fun setData() {
        val listEdit = listOf<PhotoEditModel>(
            getData("a1.jpg"),
            getData("a2.jpg"),
            getData("a3.jpg"),
            getData("a4.jpg"),
            getData("a5.jpg"),
            getData("a6.jpg"),
            getData("a7.jpg"),
            getData("a8.jpg"),
        )
        projectModel.listPhoto = listEdit.toMutableList()
    }

    private fun getData(image: String) = PhotoEditModel(
        1,
        "aaa",
        "",
        "file:///android_asset/$image",
        0,
        0,
        image,
        duration,
        true,
        BackgroundEditBlurModel(50), null, null, null, null
    )

    var projectModel = ProjectModel(
        id = 1,
        nameProject = "testsss",
        timeModify = System.currentTimeMillis(),
        timeCreate = System.currentTimeMillis(),
        listPhoto = mutableListOf(),
        ratioModel = RatioModel.R1_1,
        prjMusicModel = ProjectMusicModel(
            MusicModel(
                -111,
                "",
                "",
                "",
                System.currentTimeMillis(),
                0,
                uuid = UUID.randomUUID().toString(),
                remotePath = "fakeRemotePathForIconInList"
            ),
            0,
            0
        )
    )

    override fun onResume() {
        super.onResume()

        ieSurfaceView = IESurfaceView(this)
        iePreview.removeAllViews()
        iePreview.addView(
            ieSurfaceView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        IEManager.getInstance().attachPreview(ieSurfaceView!!)
    }

    override fun onPause() {
        super.onPause()
        IEManager.getInstance().stop()
        ieSurfaceView?.let {
            IEManager.getInstance().detachPreview(it)
        }

    }

}