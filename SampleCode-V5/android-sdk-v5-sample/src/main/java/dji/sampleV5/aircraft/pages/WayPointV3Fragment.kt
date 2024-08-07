package dji.sampleV5.aircraft.pages

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.fragment.app.activityViewModels
import dji.sampleV5.aircraft.R
import dji.sampleV5.aircraft.models.WayPointV3VM
import dji.v5.common.callback.CommonCallbacks
import dji.v5.common.error.IDJIError
import dji.v5.utils.common.*
import kotlinx.android.synthetic.main.frag_waypointv3_page.*
import java.io.File
import java.util.*


import dji.v5.manager.aircraft.waypoint3.model.WaypointMissionExecuteState
import java.io.IOException
import android.content.DialogInterface

import android.content.DialogInterface.OnMultiChoiceClickListener
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.util.Log
import android.widget.*
import com.dji.industry.mission.DocumentsUtils
import com.dji.wpmzsdk.common.data.Template
import com.dji.wpmzsdk.common.utils.kml.model.WaypointActionType
import com.dji.wpmzsdk.manager.WPMZManager
import dji.sampleV5.aircraft.utils.KMZTestUtil
import dji.sampleV5.aircraft.utils.KMZTestUtil.createWaylineMission
import dji.sampleV5.aircraft.utils.wpml.WaypointInfoModel


import dji.sampleV5.aircraft.BuildConfig


import dji.sampleV5.aircraft.util.DialogUtil
import dji.sdk.wpmz.jni.JNIWPMZManager
import dji.sdk.wpmz.value.mission.*
import dji.v5.manager.aircraft.waypoint3.WPMZParserManager
import dji.v5.manager.aircraft.waypoint3.WaylineExecutingInfoListener
import dji.v5.manager.aircraft.waypoint3.WaypointActionListener
import dji.v5.manager.aircraft.waypoint3.model.WaylineExecutingInfo
import dji.v5.utils.common.DeviceInfoUtil.getPackageName
import dji.v5.ux.map.MapWidget

import dji.v5.ux.mapkit.core.maps.DJIMap

import dji.v5.ux.mapkit.core.models.DJIBitmapDescriptor
import dji.v5.ux.mapkit.core.models.DJIBitmapDescriptorFactory

import dji.v5.ux.mapkit.core.models.DJILatLng

import dji.v5.ux.mapkit.core.models.annotations.DJIMarkerOptions

import dji.v5.ux.mapkit.core.models.annotations.DJIPolylineOptions


import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.view_mission_setting_home.*

import android.widget.EditText
import com.dji.wpmzsdk.common.data.HeightMode
import dji.sdk.keyvalue.key.FlightControllerKey
import dji.sdk.keyvalue.key.KeyTools
import dji.sdk.keyvalue.value.common.LocationCoordinate2D
import dji.v5.common.utils.GpsUtils
import dji.v5.manager.KeyManager
import dji.v5.manager.aircraft.simulator.SimulatorManager
import dji.v5.manager.aircraft.waypoint3.WaypointMissionManager
import dji.v5.manager.aircraft.waypoint3.model.BreakPointInfo
import dji.v5.ux.accessory.DescSpinnerCell
import dji.v5.ux.mapkit.core.models.annotations.DJIMarker
import kotlinx.android.synthetic.main.dialog_add_waypoint.view.*
import dji.sampleV5.aircraft.util.ToastUtils


/**
 * @author feel.feng
 * @time 2022/02/27 9:30 上午
 * @description:
 */
class WayPointV3Fragment : DJIFragment() {

    private val wayPointV3VM: WayPointV3VM by activityViewModels()
    private val WAYPOINT_SAMPLE_FILE_NAME: String = "waypointsample.kmz"
    private val WAYPOINT_SAMPLE_FILE_DIR: String = "waypoint/"
    private val WAYPOINT_SAMPLE_FILE_CACHE_DIR: String = "waypoint/cache/"
    private val WAYPOINT_FILE_TAG = ".kmz"
    private var unzipChildDir = "temp/"
    private var unzipDir = "wpmz/"
    private var mDisposable : Disposable ?= null
    private val OPEN_FILE_CHOOSER = 0
    private val OPEN_DOCUMENT_TREE = 1
    private val OPEN_MANAGE_EXTERNAL_STORAGE  = 2



    private val showWaypoints : ArrayList<WaypointInfoModel> = ArrayList()
    private val pointMarkers : ArrayList<DJIMarker?> = ArrayList()
    var curMissionPath: String = DiskUtil.getExternalCacheDirPath(
        ContextUtil.getContext(),
        WAYPOINT_SAMPLE_FILE_DIR + WAYPOINT_SAMPLE_FILE_NAME
    )
    val rootDir = DiskUtil.getExternalCacheDirPath(ContextUtil.getContext(), WAYPOINT_SAMPLE_FILE_DIR)
    var validLenth: Int = 2
    var curMissionExecuteState: WaypointMissionExecuteState? = null
    var selectWaylines: ArrayList<Int> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.frag_waypointv3_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareMissionData()
        initView(savedInstanceState)
        initData()
        WPMZManager.getInstance().init(ContextUtil.getContext())
    }

    private fun prepareMissionData() {
        val dir = File(rootDir)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val cachedirName = DiskUtil.getExternalCacheDirPath(
            ContextUtil.getContext(),
            WAYPOINT_SAMPLE_FILE_CACHE_DIR
        )
        val cachedir = File(cachedirName)
        if (!cachedir.exists()) {
            cachedir.mkdirs()
        }
        val destPath = rootDir + WAYPOINT_SAMPLE_FILE_NAME
        if (!File(destPath).exists()) {
            FileUtils.copyAssetsFile(
                ContextUtil.getContext(),
                WAYPOINT_SAMPLE_FILE_NAME,
                destPath
            )
        }
    }

    private fun initView(savedInstanceState: Bundle?) {
        sp_map_switch.adapter = wayPointV3VM.getMapSpinnerAdapter()

        addListener()
        btn_mission_upload?.setOnClickListener {//uploadKmz 버튼 클릭 리스너 정의
            val waypointFile = File(curMissionPath)//생성해 둔 KMZ파일을 불러옴
            if (waypointFile.exists()) {//파일이 존재한다면
                ToastUtils.showToast(WPMZManager.getInstance().checkValidation(curMissionPath).value.toString())
                LogUtils.i(logTag , "validation check : " + WPMZManager.getInstance().checkValidation(curMissionPath).value.toString())
                wayPointV3VM.pushKMZFileToAircraft(curMissionPath)//드론 기체에 KMZ파일을 전달함
            } else {
                ToastUtils.showToast("Mission file not found!");
            }
            markWaypoints()
        }

        wayPointV3VM.missionUploadState.observe(viewLifecycleOwner) {
            it?.let {
                when {
                    it.error != null -> {
                        mission_upload_state_tv?.text = "Upload State: error:${getErroMsg(it.error)} "
                    }
                    it.tips.isNotEmpty() -> {
                        mission_upload_state_tv?.text = it.tips
                    }
                    else -> {
                        mission_upload_state_tv?.text = "Upload State: progress:${it.updateProgress} "
                    }
                }

            }
        }


        btn_mission_start.setOnClickListener {//업로드되어 있는 KMZ 미션을 시작함
            wayPointV3VM.startMission(//미션 시작
                FileUtils.getFileName(curMissionPath, WAYPOINT_FILE_TAG),
                selectWaylines,
                object : CommonCallbacks.CompletionCallback {
                    override fun onSuccess() {
                        ToastUtils.showToast("startMission Success")
                        LogUtils.i(logTag , "availableWaylineIDs : " + wayPointV3VM.getAvailableWaylineIDs(curMissionPath).toString())
                        LogUtils.i(logTag , "filename : " + FileUtils.getFileName(curMissionPath, WAYPOINT_FILE_TAG)+" Wayline : "+ selectWaylines.toString() )
                    }

                    override fun onFailure(error: IDJIError) {
                        ToastUtils.showToast("startMission Failed " + getErroMsg(error))
                        LogUtils.i(logTag , "filename : " + FileUtils.getFileName(curMissionPath, WAYPOINT_FILE_TAG)+" Wayline : "+ selectWaylines.toString() )
                    }
                })

        }

        btn_mission_pause.setOnClickListener {
            wayPointV3VM.pauseMission(object : CommonCallbacks.CompletionCallback {
                override fun onSuccess() {
                    ToastUtils.showToast("pauseMission Success")
                }

                override fun onFailure(error: IDJIError) {
                    ToastUtils.showToast("pauseMission Failed " + getErroMsg(error))
                }
            })

        }

        observeBtnResume()


        btn_wayline_select.setOnClickListener {
            selectWaylines.clear()
            var waylineids = wayPointV3VM.getAvailableWaylineIDs(curMissionPath)
            showMultiChoiceDialog(waylineids)
        }

        kmz_btn.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
                var intent = Intent("android.settings.MANAGE_ALL_FILES_ACCESS_PERMISSION")
                startActivityForResult(intent , OPEN_MANAGE_EXTERNAL_STORAGE)
            } else {
                showFileChooser()
            }
        }

        map_locate.setOnClickListener {
            map_widget.setMapCenterLock(MapWidget.MapCenterLock.AIRCRAFT)
        }

        sp_map_switch.setSelection(wayPointV3VM.getMapType(context))

        btn_mission_stop.setOnClickListener {
            if (curMissionExecuteState == WaypointMissionExecuteState.READY) {
                ToastUtils.showToast("Mission not start")
                return@setOnClickListener
            }
            wayPointV3VM.stopMission(
                FileUtils.getFileName(curMissionPath, WAYPOINT_FILE_TAG),
                object : CommonCallbacks.CompletionCallback {
                    override fun onSuccess() {
                        ToastUtils.showToast("stopMission Success")
                    }

                    override fun onFailure(error: IDJIError) {
                        ToastUtils.showToast("stopMission Failed " + getErroMsg(error))
                    }
                })
        }
        btn_editKmz.setOnClickListener {
            showEditDialog()
        }

        waypoints_clear.setOnClickListener {
            showWaypoints.clear()
            removeAllPoint()
            updateSaveBtn()
        }

        kmz_save.setOnClickListener {//kmz파일 저장 및 생성 버튼
            val kmzOutPath = rootDir + "generate_test.kmz"
            val waylineMission: WaylineMission = createWaylineMission()//웨이라인 미션 생성
            val missionConfig: WaylineMissionConfig = KMZTestUtil.createMissionConfig()//
            val template: Template = KMZTestUtil.createTemplate(showWaypoints)//웨이포인트가 들어있는 어레이리스트를 매개변수로 템플릿을 생성함


            for(i : Int in 0 until showWaypoints.size){
                LogUtils.i(logTag , "waylineWaypoint : "+ showWaypoints.get(i).waylineWaypoint.toString())
                LogUtils.i(logTag , "actionInfos : "+ showWaypoints.get(i).actionInfos.toString())
            }




            WPMZManager.getInstance()
                .generateKMZFile(kmzOutPath, waylineMission, missionConfig, template)//KMZ파일 생성
            curMissionPath  = kmzOutPath
            ToastUtils.showToast("Save Kmz Success Path is : $kmzOutPath")//생성된 KMZ파일의 경로를 토스트로 표시함


            LogUtils.i(logTag , "kmzvalidity check right after the generation : "+ WPMZManager.getInstance().checkValidation(curMissionPath).value)
            LogUtils.i(logTag ,"waylineMissionParseInfo : "+WPMZManager.getInstance().getKMZInfo(curMissionPath).waylineMissionParseInfo.toString())
            LogUtils.i(logTag ,"waylineMissionConfigParseInfo : "+WPMZManager.getInstance().getKMZInfo(curMissionPath).waylineMissionConfigParseInfo.toString())
            LogUtils.i(logTag ,"waylineTemplatesParseInfo : "+WPMZManager.getInstance().getKMZInfo(curMissionPath).waylineTemplatesParseInfo.toString())
            LogUtils.i(logTag ,"waylineWaylinesParseInfo : "+WPMZManager.getInstance().getKMZInfo(curMissionPath).waylineWaylinesParseInfo.toString())


                waypoint_add.isChecked = false;
        }

        btn_breakpoint_resume.setOnClickListener{
            var missionName = FileUtils.getFileName(curMissionPath , WAYPOINT_FILE_TAG );
            WaypointMissionManager.getInstance().queryBreakPointInfoFromAircraft(missionName
                , object :CommonCallbacks.CompletionCallbackWithParam<BreakPointInfo>{
                override fun onSuccess(breakPointInfo: BreakPointInfo?) {
                    breakPointInfo?.let {
                        resumeFromBreakPoint(missionName , it)
                    }
                }

                override fun onFailure(error: IDJIError) {
                    ToastUtils.showToast("queryBreakPointInfo error $error")
                }

            })
        }
        addMapListener()
        createMapView(savedInstanceState)
        observeAircraftLocation()
    }
    private  fun addMapListener(){
        waypoint_add.setOnCheckedChangeListener { _, isOpen ->//맵 위의 한 지점을 터치하면 웨이포인트를 생성할지 아무런 동작을 하지 않을지 선택하는 토글이 있다. 그 토글의 값이 true 일 때 map의 온터치 리스너가 작동하도록 하는 조건문
            if (isOpen) {//맵 위의 한 지점을 터치하면 이 메소드가 작동한다.
                map_widget.map?.setOnMapClickListener{
                    showWaypointDlg(it , object :CommonCallbacks.CompletionCallbackWithParam<WaypointInfoModel>{//웨이포인트를 생성하는 다이얼로그를 띄움, 콜백 메소드도 여기서 정의
                    override fun onSuccess(waypointInfoModel: WaypointInfoModel) {//웨이포인트를 추가하면
                        showWaypoints.add( waypointInfoModel)//웨이포인트 정보를 어레이리스트에 추가 함
                        showWaypoints()//지도상에 웨이포인트 지점을 표시
                        updateSaveBtn()//웨이포인트들이 들어있는 어레이리스트가 비어있지 않다면 save버튼을 활성화 한다
                        ToastUtils.showToast("lat" + it.latitude + " lng" + it.longitude)//추가한 웨이포인트의 LAT,LAN 값을 토스트로 표시해 줌
                    }
                        override fun onFailure(error: IDJIError) {
                            ToastUtils.showToast("add Failed " )//추가하지 못했을 때는 add Failed라는 문구를 토스트로 띄워줌
                        }
                    })
                }
            } else {
                map_widget.map?.removeAllOnMapClickListener()
            }
        }
    }
    private fun observeAircraftLocation() {
        val location = KeyManager.getInstance()
            .getValue(KeyTools.createKey(FlightControllerKey.KeyAircraftLocation), LocationCoordinate2D(0.0,0.0))
        val isEnable = SimulatorManager.getInstance().isSimulatorEnabled
        if (!GpsUtils.isLocationValid(location) && !isEnable) {
            ToastUtils.showToast("please open simulator")
        }
    }

    private fun observeBtnResume() {
        btn_mission_resume.setOnClickListener {
            var wp_breakinfo_index = wp_break_index.text.toString()
            var wp_breakinfo_progress = wp_break_progress.text.toString()
            if (!TextUtils.isEmpty(wp_breakinfo_index) && !TextUtils.isEmpty(wp_breakinfo_progress)) {

                var breakPointInfo = BreakPointInfo(0 ,wp_breakinfo_index.toInt(),wp_breakinfo_progress.toDouble())
                wayPointV3VM.resumeMission(breakPointInfo , object : CommonCallbacks.CompletionCallback {
                    override fun onSuccess() {
                        ToastUtils.showToast("resumeMission with BreakInfo Success")
                    }

                    override fun onFailure(error: IDJIError) {
                        ToastUtils.showToast("resumeMission with BreakInfo Failed " + getErroMsg(error))
                    }
                })
            }
            else {
                wayPointV3VM.resumeMission(object : CommonCallbacks.CompletionCallback {
                    override fun onSuccess() {
                        ToastUtils.showToast("resumeMission Success")
                    }

                    override fun onFailure(error: IDJIError) {
                        ToastUtils.showToast("resumeMission Failed " + getErroMsg(error))
                    }
                })
            }
        }
    }

    private fun resumeFromBreakPoint(missionName :String , breakPointInfo: BreakPointInfo ){
        var wp_breakinfo_index = wp_break_index.text.toString()
        var wp_breakinfo_progress = wp_break_progress.text.toString()
        if (!TextUtils.isEmpty(wp_breakinfo_index) && !TextUtils.isEmpty(wp_breakinfo_progress)) {
            breakPointInfo.segmentProgress = wp_breakinfo_progress.toDouble()
            breakPointInfo.waypointID = wp_breakinfo_index.toInt()
        }
        wayPointV3VM.startMission(missionName , breakPointInfo , object :CommonCallbacks.CompletionCallback{
            override fun onSuccess() {
                ToastUtils.showToast("resume success");
            }

            override fun onFailure(error: IDJIError) {
               ToastUtils.showToast("resume error $error")
            }

        })
    }



    private fun addListener(){
        wayPointV3VM.addMissionStateListener() {
            mission_execute_state_tv?.text = "Mission Execute State : ${it.name}"
            btn_mission_upload.isEnabled = it == WaypointMissionExecuteState.READY
            curMissionExecuteState = it
            if (it == WaypointMissionExecuteState.FINISHED) {
                ToastUtils.showToast("Mission Finished")
            }
            LogUtils.i(logTag , "State is ${it.name}")
        }
        wayPointV3VM.addWaylineExecutingInfoListener(object :WaylineExecutingInfoListener {
            override fun onWaylineExecutingInfoUpdate(it: WaylineExecutingInfo) {
                wayline_execute_state_tv?.text = "Wayline Execute Info WaylineID:${it.waylineID} \n" +
                        "WaypointIndex:${it.currentWaypointIndex} \n" +
                        "MissionName : ${ it.missionFileName}"
            }

            override fun onWaylineExecutingInterruptReasonUpdate(error: IDJIError?) {
                if (error != null) {
                    LogUtils.e(logTag , "interrupt error${error.description()}")
                }
            }

        });


        wayPointV3VM.addWaypointActionListener(object :WaypointActionListener{
            override fun onExecutionStart(actionId: Int) {
                waypint_action_state_tv?.text = "onExecutionStart: ${actionId} "
            }

            override fun onExecutionStart(actionGroup: Int , actionId: Int ) {
                waypint_action_state_tv?.text = "onExecutionStart:${actionGroup}: ${actionId} "
            }

            override fun onExecutionFinish(actionId: Int, error: IDJIError?) {
                waypint_action_state_tv?.text = "onExecutionFinish: ${actionId} "
            }

            override fun onExecutionFinish(actionGroup: Int, actionId: Int,  error: IDJIError?) {
                waypint_action_state_tv?.text = "onExecutionFinish:${actionGroup}: ${actionId} "
            }

        })
    }

    fun updateSaveBtn(){
        kmz_save.isEnabled = showWaypoints.isNotEmpty()
    }
    private fun showEditDialog() {
        val waypointFile = File(curMissionPath)
        if (!waypointFile.exists()) {
            ToastUtils.showToast("Please upload kmz file")
            return
        }

        val unzipFolder = File(rootDir, unzipChildDir)
        // 解压后的waylines路径
        val templateFile = File(rootDir + unzipChildDir + unzipDir, WPMZParserManager.TEMPLATE_FILE)
        val waylineFile = File(rootDir + unzipChildDir + unzipDir, WPMZParserManager.WAYLINE_FILE)

        mDisposable = Single.fromCallable {
            //在cache 目录创建一个wmpz文件夹，并将template.kml 与 waylines.wpml 拷贝进wpmz ，然后压缩wpmz文件夹
            WPMZParserManager.unZipFolder(ContextUtil.getContext(), curMissionPath, unzipFolder.path, false)
            FileUtils.readFile(waylineFile.path , null)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { wpmlContent: String? ->
                    DialogUtil.showInputDialog(requireActivity() ,"",wpmlContent , "", false , object :CommonCallbacks.CompletionCallbackWithParam<String> {
                        override fun onSuccess(newContent: String?) {
                            newContent?.let {
                                updateWPML(it)
                            }
                        }
                        override fun onFailure(error: IDJIError) {
                            LogUtils.e(logTag , "show input Dialog Failed ${error.description()} ")
                        }

                    })
                }
            ) { throwable: Throwable ->
                LogUtils.e(logTag , "show input Dialog Failed ${throwable.message} ")
            }
    }

    private fun updateWPML(newContent: String) {
        val waylineFile = File(rootDir + unzipChildDir + unzipDir, WPMZParserManager.WAYLINE_FILE)

        Single.fromCallable {
            FileUtils.writeFile(waylineFile.path, newContent, false)
            //将修改后的waylines.wpml重新压缩打包成 kmz
            val zipFiles = mutableListOf<String>()
            val cacheFolder = File(rootDir, unzipChildDir + unzipDir)
            var zipFile = File(rootDir + unzipChildDir + "waypoint.kmz")
            if (waylineFile.exists()) {
                zipFiles.add(cacheFolder.path)
                zipFile.createNewFile()
                WPMZParserManager.zipFiles(ContextUtil.getContext(), zipFiles, zipFile.path)
            }
            //将用户选择的kmz用修改的后的覆盖
            FileUtils.copyFileByChannel(zipFile.path, curMissionPath)
        }.subscribeOn(Schedulers.io()).subscribe()

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OPEN_FILE_CHOOSER) {
            data?.apply {
                getData()?.let {
                    curMissionPath = getPath(context, it)
                    checkPath()
                }
            }

        }

        if (requestCode == OPEN_DOCUMENT_TREE) {
            grantUriPermission(  data)
        }


        if (requestCode == OPEN_MANAGE_EXTERNAL_STORAGE
             && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()) {
            showFileChooser()
        }


    }

    fun checkPath(){
        if (!curMissionPath.contains(".kmz") && !curMissionPath.contains(".kml")) {
            ToastUtils.showToast("Please choose KMZ/KML file")
        } else {

            // Choose a directory using the system's file picker.
            showPermisssionDucument()

            if (curMissionPath.contains(".kml") ){
                if (WPMZManager.getInstance().transKMLtoKMZ(curMissionPath , "" , getHeightMode())) {
                    curMissionPath  =   Environment.getExternalStorageDirectory()
                        .toString() + "/DJI/" + requireContext().packageName + "/KMZ/OutPath/" + getName(curMissionPath) + ".kmz"
                    ToastUtils.showToast("Trans kml success " + curMissionPath)
                } else {
                    ToastUtils.showToast("Trans kml failed!")
                }
            } else {
                ToastUtils.showToast("KMZ file path:${curMissionPath}")
            }
        }
    }
    fun getName(path: String): String? {
        val start = path.lastIndexOf("/")
        val end = path.lastIndexOf(".")
        return if (start != -1 && end != -1) {
            path.substring(start + 1, end)
        } else {
            "unknow"
        }
    }
    fun showPermisssionDucument() {
        val canWrite: Boolean =
            DocumentsUtils.checkWritableRootPath(context, curMissionPath)
        if (!canWrite && Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            val storageManager =
                requireActivity().getSystemService(Context.STORAGE_SERVICE) as StorageManager
            val volume: StorageVolume? =
                storageManager.getStorageVolume(File(curMissionPath))
            if (volume != null) {
                val intent = volume.createOpenDocumentTreeIntent()
                startActivityForResult(intent, OPEN_DOCUMENT_TREE)
                return
            }
        }
    }

    fun showFileChooser(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(
            Intent.createChooser(intent, "Select KMZ File"), OPEN_FILE_CHOOSER
        )
    }
    fun grantUriPermission(data: Intent?) {

        val uri = data!!.data
        requireActivity().grantUriPermission(getPackageName(), uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val takeFlags = data.flags and (Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                Intent.FLAG_GRANT_READ_URI_PERMISSION)
        requireActivity().getContentResolver().takePersistableUriPermission(uri!!, takeFlags)
    }

    fun getPath(context: Context?, uri: Uri?): String {
        if (DocumentsContract.isDocumentUri(context, uri) && isExternalStorageDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).toTypedArray()
            if (split.size != validLenth) {
                return ""
            }
            val type = split[0]
            if ("primary".equals(type, ignoreCase = true)) {
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            } else {
                return getExtSdCardPaths(requireContext()).get(0)!! + "/" + split[1]
            }
        }
        return ""
    }

    private fun getExtSdCardPaths(context: Context): ArrayList<String?> {
        var sExtSdCardPaths = ArrayList<String?>()
        for (file in context.getExternalFilesDirs("external")) {
            if (file != null && file != context.getExternalFilesDir("external")) {
                val index = file.absolutePath.lastIndexOf("/Android/data")
                if (index >= 0) {
                    var path: String? = file.absolutePath.substring(0, index)
                    try {
                        path = File(path).canonicalPath
                    } catch (e: IOException) {
                        LogUtils.e(logTag, e.message)
                    }
                    sExtSdCardPaths.add(path)
                }
            }
        }
        if (sExtSdCardPaths.isEmpty()) {
            sExtSdCardPaths.add("/storage/sdcard1")
        }
        return sExtSdCardPaths
    }

    fun isExternalStorageDocument(uri: Uri?): Boolean {
        return "com.android.externalstorage.documents" == uri?.authority
    }

    private fun initData() {
        wayPointV3VM.listenFlightControlState()

        wayPointV3VM.flightControlState.observe(viewLifecycleOwner) {
            it?.let {
                wayline_aircraft_height?.text = String.format("Aircraft Height: %.2f", it.height)
                wayline_aircraft_distance?.text =
                    String.format("Aircraft Distance: %.2f", it.distance)
                wayline_aircraft_speed?.text = String.format("Aircraft Speed: %.2f", it.speed)
            }
        }
    }



    @IntDef(
        MapProvider.MAP_AUTO,
        MapProvider.AMAP_PROVIDER,
        MapProvider.MAPLIBRE_PROVIDER,
        MapProvider.GOOGLE_PROVIDER
    )
    annotation class MapProvider {
        companion object {
            const val MAP_AUTO = 0
            const val AMAP_PROVIDER = 1
            const val MAPLIBRE_PROVIDER = 2
            const val GOOGLE_PROVIDER = 3
        }
    }

    private fun createMapView(savedInstanceState: Bundle?) {
        val onMapReadyListener = MapWidget.OnMapReadyListener { map ->
            map.setMapType(DJIMap.MapType.NORMAL)
        }
        map_widget.initAMap(onMapReadyListener)

        map_widget.onCreate(savedInstanceState) //需要再init后调用否则Amap无法显示
    }

    override fun onPause() {
        super.onPause()
        map_widget.onPause()
    }

    override fun onResume() {
        super.onResume()
        map_widget.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        map_widget.onDestroy()
    }

    override fun onDestroy() {
        super.onDestroy()
        wayPointV3VM.cancelListenFlightControlState()
        wayPointV3VM.removeAllMissionStateListener()
        wayPointV3VM.clearAllWaylineExecutingInfoListener()
        wayPointV3VM.clearAllWaypointActionListener()

        mDisposable?.let {
            if (!it.isDisposed) {
                it.dispose()
            }
        }
    }

    fun getErroMsg(error: IDJIError): String {
        if (!TextUtils.isEmpty(error.description())) {
            return error.description();
        }
        return error.errorCode()
    }


    fun showMultiChoiceDialog(waylineids: List<Int>) {
        var items: ArrayList<String> = ArrayList()
        waylineids
            .filter {
                it >= 0
            }
            .map {
                items.add(it.toString())
            }

        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setTitle("Select Wayline")
        builder.setPositiveButton("OK", null)
        builder.setMultiChoiceItems(
            items.toTypedArray(),
            null,
            object : OnMultiChoiceClickListener {
                override fun onClick(p0: DialogInterface?, index: Int, isSelect: Boolean) {
                    if (isSelect) {
                        selectWaylines.add(index)
                    } else {
                        selectWaylines.remove(index)
                    }
                }
            }).create().show()

    }

    fun markWaypoints() {
        // version参数实际未用到
        var waypoints: ArrayList<WaylineExecuteWaypoint> = ArrayList<WaylineExecuteWaypoint>()
        val parseInfo = JNIWPMZManager.getWaylines("1.0.0", curMissionPath)
        var waylines = parseInfo.waylines
        waylines.forEach() {
            waypoints.addAll(it.waypoints)
            markLine(it.waypoints)
        }
        waypoints.forEach() {
            markWaypoint(DJILatLng(it.location.latitude, it.location.longitude), it.waypointIndex)
        }
    }

    fun markWaypoint(latlong: DJILatLng, waypointIndex: Int) : DJIMarker?{
        var markOptions = DJIMarkerOptions()
        markOptions.position(latlong)
        markOptions.icon(getMarkerRes(waypointIndex, 0f))
        markOptions.title(waypointIndex.toString())
        markOptions.isInfoWindowEnable = true
       return map_widget.map?.addMarker(markOptions)
    }

    fun markLine(waypoints: List<WaylineExecuteWaypoint>) {

        var djiwaypoints = waypoints.filter {
            true
        }.map {
            DJILatLng(it.location.latitude, it.location.longitude)
        }
        var lineOptions = DJIPolylineOptions()
        lineOptions.width(5f)
        lineOptions.color(Color.GREEN)
        lineOptions.addAll(djiwaypoints)
        map_widget.map?.addPolyline(lineOptions)
    }



    /**
     * Convert view to bitmap
     * Notice: recycle the bitmap after use
     */
    fun getMarkerBitmap(
        index: Int,
        rotation: Float,
    ): Bitmap? {
        // create View for marker
        @SuppressLint("InflateParams") val markerView: View =
            LayoutInflater.from(activity)
                .inflate(R.layout.waypoint_marker_style_layout, null)
        val markerBg = markerView.findViewById<ImageView>(R.id.image_content)
        val markerTv = markerView.findViewById<TextView>(R.id.image_text)
        markerTv.text = index.toString()
        markerTv.setTextColor(AndUtil.getResColor(R.color.blue))
        markerTv.textSize =
            AndUtil.getDimension(R.dimen.mission_waypoint_index_text_large_size)

        markerBg.setImageResource(R.mipmap.mission_edit_waypoint_normal)

        markerBg.rotation = rotation
        // convert view to bitmap
        markerView.destroyDrawingCache()
        markerView.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        markerView.layout(0, 0, markerView.measuredWidth, markerView.measuredHeight)
        markerView.isDrawingCacheEnabled = true
        return markerView.getDrawingCache(true)
    }

    private fun getMarkerRes(
        index: Int,
        rotation: Float,
    ): DJIBitmapDescriptor? {
        return DJIBitmapDescriptorFactory.fromBitmap(
            getMarkerBitmap(index , rotation)
        )
    }

    fun showWaypoints(){
        var loction2D = showWaypoints.last().waylineWaypoint.location
        val waypoint =  DJILatLng(loction2D.latitude , loction2D.longitude)
       var pointMarker =  markWaypoint(waypoint , getCurWaypointIndex())
        pointMarkers.add(pointMarker)
    }

    fun getCurWaypointIndex():Int{
        if (showWaypoints.size <= 0) {//웨이포인트 목록이 비어있으면
            return 0 //0을 리턴
        }
        return showWaypoints.size //웨이포인트 목록이 비어있지 않으면 현재 웨이포인트갯수가 몇개인지를 리턴함
    }
    private fun  showWaypointDlg( djiLatLng: DJILatLng ,callbacks: CommonCallbacks.CompletionCallbackWithParam<WaypointInfoModel>) {//웨이포인트 추가 다이얼로그, mavlink통신에서는 이게 핵심이 될 듯.
        val builder = AlertDialog.Builder(requireActivity())
        val dialog = builder.create()
        val dialogView = View.inflate(requireActivity(), R.layout.dialog_add_waypoint, null)
        dialog.setView(dialogView)

        val etHeight = dialogView.findViewById<View>(R.id.et_height) as EditText //  alt값
        val etSpd = dialogView.findViewById<View>(R.id.et_speed) as EditText
        val viewActionType = dialogView.findViewById<View>(R.id.action_type) as DescSpinnerCell //수행할 액션 목록
        val btnAdd = dialogView.findViewById<View>(R.id.btn_add) as Button // 추가 버튼
        val btnCancel = dialogView.findViewById<View>(R.id.btn_cancel) as Button

        btnAdd.setOnClickListener {
            var waypointInfoModel =  WaypointInfoModel()//매개변수로 전달받은 waypointInfoModel 변수로 선언
            val waypoint = WaylineWaypoint() //빈 웨이포인트 하나 생성
            waypoint.waypointIndex = getCurWaypointIndex()// 웨이포인트의 총 갯수를 리턴- 지금 구조는 하나씩 추가하는 구조라 가능하지만 mavlink 통신을 할 때는 메시지에서 전달받은 seq값을 넣으면 될듯
            val location = WaylineLocationCoordinate2D(djiLatLng.latitude , djiLatLng.longitude) //lat, lang 값을 받아서 넣으면 될듯
            waypoint.location = location//메시지로 전달받은 lat, lang 값을 활용하여 위경도 설정
            waypoint.height = etHeight.text.toString().toDouble() //  alt값을 활용하여 고도값 설정
            // 根据坐标类型，如果为egm96 需要加上高程差
            waypoint.ellipsoidHeight = etHeight.text.toString().toDouble()
            waypoint.speed = etSpd.text.toString().toDouble()
            waypoint.useGlobalTurnParam = true
            waypointInfoModel.waylineWaypoint = waypoint //웨이포인트인포 모델 객체의 웨이라인 웨이포인트 변수에 위에서 생성한 웨이라인웨이포인트를 할당함
            val actionInfos: MutableList<WaylineActionInfo> = ArrayList()//웨이라인 액션들을 담은 어레이 리스트 하나 생성
            actionInfos.add(KMZTestUtil.createActionInfo(getCurActionType(viewActionType)))
            waypointInfoModel.waylineWaypoint = waypoint
            waypointInfoModel.actionInfos = actionInfos
            callbacks.onSuccess(waypointInfoModel)
            dialog.dismiss()
        }
        btnCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun getHeightMode(): HeightMode {
        return  when(heightmode.getSelectPosition()){
           0 -> HeightMode.WGS84
           1-> HeightMode.EGM96
           2 -> HeightMode.RELATIVE
            else -> {
                HeightMode.WGS84
            }
        }
    }

    private fun getCurActionType(viewActionType: DescSpinnerCell): WaypointActionType? {
        return when (viewActionType.getSelectPosition()) {
            0 -> WaypointActionType.START_TAKE_PHOTO
            1 -> WaypointActionType.START_RECORD
            2 -> WaypointActionType.STOP_RECORD
            3 -> WaypointActionType.GIMBAL_PITCH
            4 -> WaypointActionType.STAY
            5 -> WaypointActionType.ROTATE_AIRCRAFT
            else -> {
                WaypointActionType.START_TAKE_PHOTO
            }
        }
    }
    private  fun removeAllPoint(){
        pointMarkers.forEach{
            it?.let {
                it.remove()
            }
        }
    }
}