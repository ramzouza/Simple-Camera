package com.simplemobiletools.camera.activities

import android.app.Activity
import android.content.Intent
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.*
import android.graphics.Bitmap

import android.graphics.Matrix
import android.media.Image
import android.graphics.Typeface
//import android.support.v7.app.AppCompatActivity
import android.view.View
import android.os.Vibrator
import android.text.TextUtils
import android.util.Log
import android.widget.*
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter

import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.simplemobiletools.camera.Adapter.ViewConnection
import com.simplemobiletools.camera.BuildConfig

import com.simplemobiletools.camera.Utils.BitmapTools
import com.simplemobiletools.camera.Utils.NonSwipeableViewPager
import com.simplemobiletools.camera.extensions.config
import com.simplemobiletools.camera.extensions.navBarHeight
import com.simplemobiletools.camera.helpers.*
import com.simplemobiletools.camera.implementations.MyCameraImpl
import com.simplemobiletools.camera.interfaces.MyPreview
import com.simplemobiletools.camera.views.CameraPreview
import com.simplemobiletools.camera.views.FocusCircleView
import com.simplemobiletools.commons.extensions.*
import com.simplemobiletools.commons.helpers.*
import com.simplemobiletools.commons.models.Release
import androidx.recyclerview.widget.*
import com.simplemobiletools.camera.Adapter.KnowledgeGraphAdapter
import kotlinx.android.synthetic.main.activity_main.*
import com.simplemobiletools.camera.interfaces.FilterListInterface
import com.zomato.photofilters.imageprocessors.Filter
import kotlinx.android.synthetic.main.filter_content.*
import kotlinx.android.synthetic.main.filter_main.*
import kotlinx.android.synthetic.main.features.*
import java.util.*
import kotlin.concurrent.schedule
import com.google.firebase.perf.metrics.AddTrace
import com.simplemobiletools.camera.Adapter.FirebaseVisionAdapter
import com.simplemobiletools.camera.Adapter.PostsAdapter
import com.simplemobiletools.camera.R
import com.simplemobiletools.camera.Utils.isHyperlink
import com.simplemobiletools.camera.debug.TextFragment
import com.simplemobiletools.camera.dialogs.SmartHubDialog
import org.json.JSONObject
import com.simplemobiletools.camera.extensions.OnSwipeTouchListener
import com.simplemobiletools.camera.models.ModelRecyclerView
import kotlin.collections.ArrayList
import com.simplemobiletools.camera.extensions.DoubleTapListener
import com.simplemobiletools.camera.interfaces.AddTextFragmentListener
import ja.burhanrashid52.photoeditor.OnSaveBitmap
import ja.burhanrashid52.photoeditor.PhotoEditor
import kotlinx.android.synthetic.main.fragment_add_text.*


open class MainActivity : SimpleActivity(), PhotoProcessor.MediaSavedListener, FilterListInterface, AddTextFragmentListener {
    override fun onAddTextListener(typeFace:Typeface,text: String, color: Int) {
        photoEditor.addText(typeFace,text,color)
    }

    val GALLERY_PERMISSION = 1000

    init{
        System.loadLibrary("NativeImageProcessor")
    }

    object Main{
        var IMAGE_FILTER = Uri.parse("will be set when LoadImage() is call")

    }

    internal var originalImage:Bitmap?=null
    internal lateinit var filteredImage:Bitmap
    internal lateinit var finalImage:Bitmap
    internal lateinit var filteredList: FilterList
    internal lateinit var textFragment: TextFragment
    internal lateinit var photoEditor : PhotoEditor
    internal lateinit var addTextFragment: AddTextFragment



    private var filterMenu: Menu? = null

    private val FADE_DELAY = 5000L

    lateinit var mTimerHandler: Handler
    private lateinit var mOrientationEventListener: OrientationEventListener
    private lateinit var mFocusCircleView: FocusCircleView
    private lateinit var mFadeHandler: Handler
    private lateinit var mCameraImpl: MyCameraImpl

    private var mPreview: MyPreview? = null
    private var mPreviewUri: Uri? = null
    private var mIsInPhotoMode = false
    private var mIsCameraAvailable = false
    private var mIsVideoCaptureIntent = false
    private var mIsHardwareShutterHandled = false
    private var mCurrVideoRecTimer = 0
    var mLastHandledOrientation = 0
    private var lensMode = false



    var adapter:PostsAdapter?=null
    val features : ArrayList<ModelRecyclerView> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        useDynamicTheme = false
        super.onCreate(savedInstanceState)
        appLaunched(BuildConfig.APPLICATION_ID)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        initVariables()
        tryInitCamera()
        supportActionBar?.hide()
        checkWhatsNewDialog()
        setupOrientationEventListener()



        var name_list = arrayOf("QR Code ","Photo Filters","Detect Object","Meme Generator" , "Hyperlink Scanner")
        var image_list = arrayOf(R.drawable.ic_qr_code,R.drawable.ic_img_filter,R.drawable.ic_detect_obj,R.drawable.ic_meme, R.drawable.ic_arrow_right)


        for(i in 0..name_list.size-1){
            features.add(ModelRecyclerView(name_list[i],image_list[i]))
        }


        smart_hub_scroll.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false)
        smart_hub_scroll.adapter = PostsAdapter(features,this)


        val snapHelper : SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(smart_hub_scroll)


    }

    override fun onResume() {
        super.onResume()
//        if (config.isSwipingEnabled){
//            findViewById<ImageView>(R.id.advanced_camera).setVisibility(View.GONE)
//        }
//
//        else {
//            findViewById<ImageView>(R.id.advanced_camera).setVisibility(View.VISIBLE)
//        }


        if (hasStorageAndCameraPermissions()) {
            mPreview?.onResumed()
            resumeCameraItems()
            setupPreviewImage(mIsInPhotoMode)
            scheduleFadeOut()
            mFocusCircleView.setStrokeColor(getAdjustedPrimaryColor())

            if (mIsVideoCaptureIntent && mIsInPhotoMode) {
                handleTogglePhotoVideo()
                checkButtons()
            }
            toggleBottomButtons(false)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (hasStorageAndCameraPermissions()) {
            mOrientationEventListener.enable()
        }
    }

    override fun onPause() {
        super.onPause()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (!hasStorageAndCameraPermissions() || isAskingPermissions) {
            return
        }

        mFadeHandler.removeCallbacksAndMessages(null)

        hideTimer()
        mOrientationEventListener.disable()

        if (mPreview?.getCameraState() == STATE_PICTURE_TAKEN) {
            toast(R.string.photo_not_saved)
        }
        mPreview?.onPaused()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPreview = null
    }

    private fun initVariables() {
        mIsInPhotoMode = config.initPhotoMode
        mIsCameraAvailable = false
        mIsVideoCaptureIntent = false
        mIsHardwareShutterHandled = false
        mCurrVideoRecTimer = 0
        mLastHandledOrientation = 0
        mCameraImpl = MyCameraImpl(applicationContext)
        textFragment = TextFragment.getInstance()


        if (config.alwaysOpenBackCamera) {
            config.lastUsedCamera = mCameraImpl.getBackCameraId().toString()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_CAMERA && !mIsHardwareShutterHandled) {
            mIsHardwareShutterHandled = true
            shutterPressed()
            true
        } else if (!mIsHardwareShutterHandled && config.volumeButtonsAsShutter && (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {
            mIsHardwareShutterHandled = true
            shutterPressed()
            true
        } else {
            super.onKeyDown(keyCode, event)
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_CAMERA || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            mIsHardwareShutterHandled = false
        }
        return super.onKeyUp(keyCode, event)
    }

    private fun hideIntentButtons() {
        toggle_photo_video.beGone()
        settings.beGone()
        last_photo_video_preview.beGone()
        advanced_camera.beGone()
    }

    private fun tryInitCamera() {
        handlePermission(PERMISSION_CAMERA) {
            if (it) {
                handlePermission(PERMISSION_WRITE_STORAGE) {
                    if (it) {
                        initializeCamera()
                    } else {
                        toast(R.string.no_storage_permissions)
                        finish()
                    }
                }
            } else {
                toast(R.string.no_camera_permissions)
                finish()
            }
        }
    }

    private fun isImageCaptureIntent() = intent?.action == MediaStore.ACTION_IMAGE_CAPTURE || intent?.action == MediaStore.ACTION_IMAGE_CAPTURE_SECURE

    private fun checkImageCaptureIntent() {
        if (isImageCaptureIntent()) {
            hideIntentButtons()
            val output = intent.extras?.get(MediaStore.EXTRA_OUTPUT)
            if (output != null && output is Uri) {
                mPreview?.setTargetUri(output)
            }
        }
    }

    private fun checkVideoCaptureIntent() {
        if (intent?.action == MediaStore.ACTION_VIDEO_CAPTURE) {
            mIsVideoCaptureIntent = true
            mIsInPhotoMode = false
            hideIntentButtons()
            shutter.setImageResource(R.drawable.ic_video_rec)
        }
    }

    @AddTrace(name = "initCameraTrace", enabled = true /* optional */)
    private fun initializeCamera() {
        setContentView(R.layout.activity_main)
        initButtons()

        (btn_holder.layoutParams as RelativeLayout.LayoutParams).setMargins(0, 0, 0, (navBarHeight + resources.getDimension(R.dimen.activity_margin)).toInt())

        checkVideoCaptureIntent()
        mPreview = CameraPreview(this, camera_texture_view, mIsInPhotoMode)
        view_holder.addView(mPreview as ViewGroup)
        checkImageCaptureIntent()
        mPreview?.setIsImageCaptureIntent(isImageCaptureIntent())

        val imageDrawable = if (config.lastUsedCamera == mCameraImpl.getBackCameraId().toString()) R.drawable.ic_camera_front else R.drawable.ic_camera_rear
        toggle_camera.setImageResource(imageDrawable)

        mFocusCircleView = FocusCircleView(applicationContext)
        view_holder.addView(mFocusCircleView)

        mTimerHandler = Handler()
        mFadeHandler = Handler()
        setupPreviewImage(true)

        val initialFlashlightState = if (config.turnFlashOffAtStartup) FLASH_OFF else config.flashlightState
        mPreview!!.setFlashlightState(initialFlashlightState)
        updateFlashlightState(initialFlashlightState)

        updateLensIcon() // This will set the icon button to the correct icon when camera is initilized
        fadeAnim(smart_hub_scroll, .0f)
    }

    @AddTrace(name = "initButtonsTrace", enabled = true /* optional */)
    private fun initButtons() {

        toggle_camera.setOnClickListener { toggleCamera() }
        last_photo_video_preview.setOnClickListener { showLastMediaPreview() }
        advanced_camera.setOnClickListener { toggleLens() }

        toggle_flash.setOnClickListener { toggleFlash() }
        shutter.setOnClickListener { shutterPressed() }
        settings.setOnClickListener { launchSettings() }
        toggle_photo_video.setOnClickListener { handleTogglePhotoVideo() }
        change_resolution.setOnClickListener { mPreview?.showChangeResolutionDialog() }
        //qr_code!!.setOnClickListener { qr_code() }
        //detect_object.setOnClickListener {detect_object()}
        //image_filter.setOnClickListener { startFilter("Filter") }
        //meme_gen.setOnClickListener { startFilter("memeGen") }


        swipe.setOnTouchListener(object : OnSwipeTouchListener(applicationContext) {

            override fun onSwipeRight() {
                if (lensMode == false && config.isSwipingEnabled){
                    toggleLens()
                }
            }
            override fun onSwipeLeft() {
                if (lensMode == true && config.isSwipingEnabled){
                    toggleLens()
                }
            }
            override fun onSwipeTop() {
                if (config.isSwipingEnabled){
                    val intent = Intent(applicationContext, SettingsActivity::class.java)
                    startActivity(intent)
                }
            }
        })

        doubleTap.setOnTouchListener(object : DoubleTapListener(applicationContext) {

            override fun onDoubleTap() {
                // toast("I just flipped the switch")
                toggleCamera()
            }
        })

    }

    private fun toggleCamera() {
        if (checkCameraAvailable()) {
            mPreview!!.toggleFrontBackCamera()
        }
    }

    private fun toggleLens() {

        //this.toast("Advanced Mode")
        findViewById<RecyclerView>(R.id.smart_hub_scroll).setVisibility(View.VISIBLE);

        findViewById<LinearLayout>(R.id.btn_holder).setVisibility(View.GONE);



        val vibrator = this.getSystemService(VIBRATOR_SERVICE) as Vibrator

        vibrator.vibrate(400);

        this.lensMode = !this.lensMode

        if (this.lensMode){ // We're in lens mode now
            fadeAnim(btn_holder, .0f)
            fadeAnim(smart_hub_scroll, 1f)
            findViewById<RecyclerView>(R.id.smart_hub_scroll).setVisibility(View.VISIBLE);
            // make bottom bar go away
            // make advanced hub appear

        } else { // we're in camera mode now
            fadeAnim(btn_holder, 1f)
            fadeAnim(smart_hub_scroll, .0f)
            findViewById<LinearLayout>(R.id.btn_holder).setVisibility(View.VISIBLE);
            findViewById<RecyclerView>(R.id.smart_hub_scroll).setVisibility(View.GONE);
            // make advanced hub appear
            // make bottom bar appear

        }


    }

    @AddTrace(name = "qrCodeTrace", enabled = true /* optional */)
     fun qr_code(){
        val intent = Intent(applicationContext, ScanActivity::class.java)
        startActivity(intent)
    }

    @AddTrace(name = "hyperlinkScannerTrace", enabled = true /* optional */)
    public fun scan_hyperlink() {
        mIsInPhotoMode = true;
        this.handleShutter();
        toast("Taking image...");
        Timer().schedule(1000) {
            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI // external URI
            val lastMediaId = this@MainActivity.getLatestMediaId(uri) // get latest image ID
            toast("Image Id: " + lastMediaId.toString());
            val file_uri = Uri.withAppendedPath(uri, lastMediaId.toString()) // get file uri

            val firebaseVisionAdapter = FirebaseVisionAdapter(this@MainActivity) // set up firebase detect object
            firebaseVisionAdapter.visionText(file_uri, fun (result: String) {
                val words = result.split(" ","\n");
                val list : ArrayList<String> = ArrayList<String>();
                for (word in words){
                    if (isHyperlink(word)){
                        toast(word)
                        list.add(word)
                    }
                }
                toast(list.size.toString());
                if (list.size > 0) SmartHubDialog(this@MainActivity).build("Hyperlinks Detected", "The following hyperlink was detected: " + list[0],"Visit Hyperlink",list[0],"Copy to Clipboard","hyperlink",list[0])
                else toast("Unfortunately, no hyperlink was recognized in the text you scanned.")
            })
        }
    }



    @AddTrace(name = "detectObjectTrace", enabled = true /* optional */)
     fun detect_object(){
        mIsInPhotoMode = true;
        this.handleShutter();
        toast("Taking image...");
        Timer().schedule(1500) {
            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI // external URI
            val lastMediaId = this@MainActivity.getLatestMediaId(uri) // get latest image ID
            toast("Image Id: " + lastMediaId.toString());
            val file_uri = Uri.withAppendedPath(uri, lastMediaId.toString()) // get file uri
            val firebaseVisionAdapter = FirebaseVisionAdapter(this@MainActivity) // set up firebase detect object
            val knowledgeGraph = KnowledgeGraphAdapter(this@MainActivity); // set up knowledge graph object
            val dialog = SmartHubDialog(this@MainActivity) // setup the smart hub dialog

            // this handler is invoked after knowledgeGraph.getSearchResult is called.
            var dialogHandler = fun (detectedObj : String, response : JSONObject) {
                Log.i("INFO", response.toString(4))

                var message : String;
                var url : String
                try{
                    val jsonArray = response.getJSONArray("itemListElement").getJSONObject(0).getJSONObject("result").getJSONObject("detailedDescription");
                    message = jsonArray.getString("articleBody");
                    url = jsonArray.getString("url")
                } catch (e : Exception){
                    message = "Hmm... I recognize the object but I cannot find any meaningful info about it.";
                    url = "https://en.wikipedia.org/wiki/Special:Search?search=${detectedObj}&go=Go"
                }

                dialog.build("Detected Object: ${detectedObj}", message,
                        "Wikipedia",url,
                        "JSON", "JSON", response.toString(3));
            }

            // this handler is invoked after firebaseVisionAdapter.vision is called.
            val visionHandler = fun (term : String){
                knowledgeGraph.getSearchResult(term, dialogHandler)
            }

            // detect the object
            firebaseVisionAdapter.visionLabel(file_uri, visionHandler)
        }
    }

    // Method to be implemented for the meme generator lens
    // @AddTrace(name = "memeGeneratorTrace", enabled = true /* optional */)




    private fun getLastMediaPath() : String {
        if (mPreviewUri != null) {
            val path = applicationContext.getRealPathFromURI(mPreviewUri!!) ?: mPreviewUri!!.toString()
            return path
        }
        return ""
    }

    private fun showLastMediaPreview() {
        if (mPreviewUri != null) {
            val path = applicationContext.getRealPathFromURI(mPreviewUri!!) ?: mPreviewUri!!.toString()
            openPathIntent(path, false, BuildConfig.APPLICATION_ID)
            this.toast(path)

        }
    }

    private fun toggleFlash() {
        if (checkCameraAvailable()) {
            mPreview?.toggleFlashlight()
        }
    }

    fun updateFlashlightState(state: Int) {
        config.flashlightState = state
        val flashDrawable = when (state) {
            FLASH_OFF -> R.drawable.ic_flash_off
            FLASH_ON -> R.drawable.ic_flash_on
            else -> R.drawable.ic_flash_auto
        }
        toggle_flash.setImageResource(flashDrawable)
    }

    fun updateLensIcon(){
        /*
        updateLensIcon will update the icon of the advanced view with the correct icon, change the R.drawable.* to
        have the icon you wish to change it to.
         */
        advanced_camera.setImageResource(R.drawable.ic_smart_lens)
    }

    fun updateCameraIcon(isUsingFrontCamera: Boolean) {
        toggle_camera.setImageResource(if (isUsingFrontCamera) R.drawable.ic_camera_rear else R.drawable.ic_camera_front)
    }

    private fun shutterPressed() {
        if (checkCameraAvailable()) {
            handleShutter()
        }
    }

    private fun handleShutter() {
        if (mIsInPhotoMode) {
            toggleBottomButtons(true)
            mPreview?.tryTakePicture()
        } else {
            mPreview?.toggleRecording()
        }
    }

    fun toggleBottomButtons(hide: Boolean) {
        runOnUiThread {
            val alpha = if (hide) 0f else 1f
            shutter.animate().alpha(alpha).start()
            toggle_camera.animate().alpha(alpha).start()
            toggle_flash.animate().alpha(alpha).start()

            shutter.isClickable = !hide
            toggle_camera.isClickable = !hide
            toggle_flash.isClickable = !hide
        }
    }

    private fun launchSettings() {
        if (settings.alpha == 1f) {
            val intent = Intent(applicationContext, SettingsActivity::class.java)
            startActivity(intent)
        } else {
            fadeInButtons()
        }
    }

    private fun handleTogglePhotoVideo() {
        handlePermission(PERMISSION_RECORD_AUDIO) {
            if (it) {
                togglePhotoVideo()
            } else {
                toast(R.string.no_audio_permissions)
                if (mIsVideoCaptureIntent) {
                    finish()
                }
            }
        }
    }

    private fun togglePhotoVideo() {
        if (!checkCameraAvailable()) {
            return
        }

        if (mIsVideoCaptureIntent) {
            mPreview?.tryInitVideoMode()
        }

        mPreview?.setFlashlightState(FLASH_OFF)
        hideTimer()
        mIsInPhotoMode = !mIsInPhotoMode
        config.initPhotoMode = mIsInPhotoMode
        showToggleCameraIfNeeded()
        checkButtons()
        toggleBottomButtons(false)
    }

    private fun checkButtons() {
        if (mIsInPhotoMode) {
            initPhotoMode()
        } else {
            tryInitVideoMode()
        }
    }

    private fun initPhotoMode() {
        toggle_photo_video.setImageResource(R.drawable.ic_video)
        shutter.setImageResource(R.drawable.ic_shutter)

        mPreview?.initPhotoMode()
        setupPreviewImage(true)
    }

    private fun tryInitVideoMode() {
        if (mPreview?.initVideoMode() == true) {
            initVideoButtons()
        } else {
            if (!mIsVideoCaptureIntent) {
                toast(R.string.video_mode_error)
            }
        }
    }

    private fun initVideoButtons() {
        toggle_photo_video.setImageResource(R.drawable.ic_camera)
        showToggleCameraIfNeeded()
        shutter.setImageResource(R.drawable.ic_video_rec)
        setupPreviewImage(false)
        mPreview?.checkFlashlight()
    }

    private fun setupPreviewImage(isPhoto: Boolean) {
        val uri = if (isPhoto) MediaStore.Images.Media.EXTERNAL_CONTENT_URI else MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val lastMediaId = getLatestMediaId(uri)
        if (lastMediaId == 0L) {
            return
        }

        mPreviewUri = Uri.withAppendedPath(uri, lastMediaId.toString())

        runOnUiThread {
            if (!isDestroyed) {
                val options = RequestOptions()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)

                Glide.with(this)
                        .load(mPreviewUri)
                        .apply(options)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(last_photo_video_preview)
            }
        }
    }

    private fun scheduleFadeOut() {
        if (!config.keepSettingsVisible) {
            mFadeHandler.postDelayed({
                fadeOutButtons()
            }, FADE_DELAY)
        }
    }

    private fun fadeOutButtons() {
        fadeAnim(settings, .5f)
        fadeAnim(toggle_photo_video, .0f)
        fadeAnim(change_resolution, .0f)
       //fadeAnim(last_photo_video_preview, .0f)
        fadeAnim(toggle_flash, .0f)
        fadeAnim(advanced_camera, .0f)
    }

    private fun fadeInButtons() {
        fadeAnim(settings, 1f)
        fadeAnim(toggle_photo_video, 1f)
        fadeAnim(change_resolution, 1f)
        //fadeAnim(last_photo_video_preview, 1f)
        fadeAnim(toggle_flash, 1f)
        fadeAnim(advanced_camera, 1f)
        scheduleFadeOut()
    }

    private fun fadeAnim(view: View, value: Float) {
        view.animate().alpha(value).start()
        view.isClickable = value != .0f
    }

    private fun hideNavigationBarIcons() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE
    }

    private fun showTimer() {
        video_rec_curr_timer.beVisible()
        setupTimer()
    }

    private fun hideTimer() {
        video_rec_curr_timer.text = 0.getFormattedDuration()
        video_rec_curr_timer.beGone()
        mCurrVideoRecTimer = 0
        mTimerHandler.removeCallbacksAndMessages(null)
    }

    private fun setupTimer() {
        runOnUiThread(object : Runnable {
            override fun run() {
                video_rec_curr_timer.text = mCurrVideoRecTimer++.getFormattedDuration()
                mTimerHandler.postDelayed(this, 1000L)
            }
        })
    }

    private fun resumeCameraItems() {
        showToggleCameraIfNeeded()
        hideNavigationBarIcons()

        if (!mIsInPhotoMode) {
            initVideoButtons()
        }
    }

    private fun showToggleCameraIfNeeded() {
        toggle_camera?.beInvisibleIf(mCameraImpl.getCountOfCameras() <= 1)
    }

    private fun hasStorageAndCameraPermissions() = hasPermission(PERMISSION_WRITE_STORAGE) && hasPermission(PERMISSION_CAMERA)

    private fun setupOrientationEventListener() {
        mOrientationEventListener = object : OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
            override fun onOrientationChanged(orientation: Int) {
                if (isDestroyed) {
                    mOrientationEventListener.disable()
                    return
                }

                val currOrient = when (orientation) {
                    in 75..134 -> ORIENT_LANDSCAPE_RIGHT
                    in 225..289 -> ORIENT_LANDSCAPE_LEFT
                    else -> ORIENT_PORTRAIT
                }

                if (currOrient != mLastHandledOrientation) {
                    val degrees = when (currOrient) {
                        ORIENT_LANDSCAPE_LEFT -> 90
                        ORIENT_LANDSCAPE_RIGHT -> -90
                        else -> 0
                    }

                    animateViews(degrees)
                    mLastHandledOrientation = currOrient
                }
            }
        }
    }

    private fun animateViews(degrees: Int) {
        val views = arrayOf<View>(toggle_camera, toggle_flash, toggle_photo_video, change_resolution, shutter, settings, last_photo_video_preview, advanced_camera)
        for (view in views) {
            rotate(view, degrees)
        }
    }

    private fun rotate(view: View, degrees: Int) = view.animate().rotation(degrees.toFloat()).start()

    private fun checkCameraAvailable(): Boolean {
        if (!mIsCameraAvailable) {
            toast(R.string.camera_unavailable)
        }
        return mIsCameraAvailable
    }

    fun setFlashAvailable(available: Boolean) {
        if (available) {
            toggle_flash.beVisible()
        } else {
            toggle_flash.beInvisible()
            toggle_flash.setImageResource(R.drawable.ic_flash_off)
            mPreview?.setFlashlightState(FLASH_OFF)
        }
    }

    fun setIsCameraAvailable(available: Boolean) {
        mIsCameraAvailable = available
    }

    fun setRecordingState(isRecording: Boolean) {
        runOnUiThread {
            if (isRecording) {
                shutter.setImageResource(R.drawable.ic_video_stop)
                toggle_camera.beInvisible()
                showTimer()
            } else {
                shutter.setImageResource(R.drawable.ic_video_rec)
                showToggleCameraIfNeeded()
                hideTimer()
            }
        }
    }

    fun videoSaved(uri: Uri) {
        setupPreviewImage(false)
        if (mIsVideoCaptureIntent) {
            Intent().apply {
                data = uri
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                setResult(Activity.RESULT_OK, this)
            }
            finish()
        }
    }

    fun drawFocusCircle(x: Float, y: Float) = mFocusCircleView.drawFocusCircle(x, y)

    override fun mediaSaved(path: String) {
        rescanPaths(arrayListOf(path)) {
            setupPreviewImage(true)
            Intent(BROADCAST_REFRESH_MEDIA).apply {
                putExtra(REFRESH_PATH, path)
                `package` = "com.simplemobiletools.gallery"
                sendBroadcast(this)
            }
        }

        if (isImageCaptureIntent()) {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun checkWhatsNewDialog() {
        arrayListOf<Release>().apply {
            add(Release(33, R.string.release_33))
            add(Release(35, R.string.release_35))
            add(Release(39, R.string.release_39))
            add(Release(44, R.string.release_44))
            add(Release(46, R.string.release_46))
            add(Release(52, R.string.release_52))
            checkWhatsNew(this, BuildConfig.VERSION_CODE)
        }
    }

    //Filter section
    @AddTrace(name = "filterTrace", enabled = true /* optional */)
     fun startFilter(type:String){
        video_rec_curr_timer.beGone()
        setContentView(R.layout.filter_main)
        photoEditor = PhotoEditor.Builder(this@MainActivity,image_preview).setPinchTextScalable(true).build()
        loadImage()

        //Put all Button to Gone
        findViewById<CardView>(R.id.btn_filters).setVisibility(View.GONE)
        findViewById<CardView>(R.id.btn_text).setVisibility(View.GONE)
        if(type == "Filter"){
            findViewById<CardView>(R.id.btn_filters).setVisibility(View.VISIBLE)
            filteredList = FilterList.getInstance()
            btn_filters.setOnClickListener {
                if(filteredList != null){
                    filteredList.setListener(this@MainActivity)
                    filteredList.show(supportFragmentManager,filteredList.tag)
                }
            }
        }
        if(type == "memeGen"){
            findViewById<CardView>(R.id.btn_text).setVisibility(View.VISIBLE)
            addTextFragment = AddTextFragment.getInstance()


            btn_text.setOnClickListener{
                if(addTextFragment != null){
                    addTextFragment.setListener(this@MainActivity)
                    addTextFragment.show(supportFragmentManager,addTextFragment.tag)
                }
            }
        }





    }

    private fun setupViewPager(viewPager: NonSwipeableViewPager?) {
        val adapter = ViewConnection(supportFragmentManager)

        filteredList = FilterList()
        filteredList.setListener(this)

        adapter.addFragment(filteredList,"FILTERS")

        viewPager!!.adapter = adapter

    }

    private fun loadImage() {
        if (mPreviewUri != null) {
            Main.IMAGE_FILTER = getFinalUriFromPath(applicationContext.getRealPathFromURI(mPreviewUri!!) ?: mPreviewUri!!.toString(),BuildConfig.APPLICATION_ID)
            // Main.IMAGE_NAME = Uri.parse("content:/"+applicationContext.getRealPathFromURI(mPreviewUri!!) ?: mPreviewUri!!.toString())
        }
        originalImage = BitmapTools.getPicture(this,Main.IMAGE_FILTER,300,300)
        filteredImage = originalImage!!.copy(Bitmap.Config.ARGB_8888,true)
        finalImage = originalImage!!.copy(Bitmap.Config.ARGB_8888,true)
        Log.d("Testing Original, ",originalImage.toString())
        Log.d("Image Preview, ",image_preview.toString())




        image_preview.source.setImageBitmap(originalImage)
        image_preview.setRotation(90F)
        tabs.setOnClickListener{saveImageToGallery()}
        tabsExit.setOnClickListener({startActivity(Intent(this, MainActivity::class.java))})
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        this.filterMenu = menu

        return true
    }
    private fun saveImageToGallery() {

        val matrix = Matrix()

        matrix.postRotate(-270F)

        val scaledBitmap = Bitmap.createScaledBitmap(finalImage, finalImage.width, finalImage.height, true)

        val rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)

        Dexter.withActivity(this)
                .withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(object: MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if(report!!.areAllPermissionsGranted()){

                            photoEditor.saveAsBitmap(object :OnSaveBitmap{
                                override fun onFailure(e: java.lang.Exception?) {
                                    val snackBar = Snackbar.make(coordinator,e!!.message.toString(),Snackbar.LENGTH_LONG)
                                    snackBar.show()
                                }

                                override fun onBitmapReady(saveBitmap: Bitmap?) {

                                    val scaledBitmap = Bitmap.createScaledBitmap(saveBitmap, saveBitmap!!.width, saveBitmap!!.height, true)

                                    val rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)

                                    val path = BitmapTools.insertImage(contentResolver,rotatedBitmap,
                                            System.currentTimeMillis().toString()+"_profile.jpg","")

                                    if(!TextUtils.isEmpty(path)){

                                        val snackBar = Snackbar.make(coordinator,"Image saved to gallery",Snackbar.LENGTH_LONG)

                                        snackBar.show()
                                    }
                                    else{
                                        val snackBar = Snackbar.make(coordinator,"Unable to save image",Snackbar.LENGTH_LONG)
                                    }
                                }

                            })


                        }
                        else
                            Toast.makeText(applicationContext,"Permission denied", Toast.LENGTH_SHORT).show()
                    }

                    override fun onPermissionRationaleShouldBeShown(
                            permissions: MutableList<PermissionRequest>?,
                            token: PermissionToken?
                    ) {
                        token!!.continuePermissionRequest()
                    }

                }).check()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK && requestCode == GALLERY_PERMISSION){

            var bitmap = BitmapTools.getPicture(this,data!!.data!!,800,800)

            originalImage = null
            finalImage!!.recycle()
            filteredImage!!.recycle()

            originalImage = bitmap.copy(Bitmap.Config.ARGB_8888,true)
            filteredImage = originalImage!!.copy(Bitmap.Config.ARGB_8888,true)
            finalImage = originalImage!!.copy(Bitmap.Config.ARGB_8888,true)

            bitmap.recycle()

            filteredList.displayImage(bitmap,data!!.data!!)
            image_preview.source.setImageBitmap(filteredImage)
        }
    }

    override fun onFilterSelected(filter: Filter) {
        filteredImage = originalImage!!.copy(Bitmap.Config.ARGB_8888,true)
        image_preview.source.setImageBitmap(filter.processFilter(filteredImage))
        finalImage = filteredImage.copy(Bitmap.Config.ARGB_8888,true)
    }

}
