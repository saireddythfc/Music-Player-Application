package com.example.saireddy.music.fragments


import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.Image
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.*
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.example.saireddy.music.CurrentSongHelper
import com.example.saireddy.music.R
import com.example.saireddy.music.Songs
import com.example.saireddy.music.databases.EchoDatabase
import com.example.saireddy.music.fragments.FavoriteFragment.Statified.mediaPlayer
/*import com.example.saireddy.music.fragments.SongPlayingFragment.Staticated.onSongComplete
import com.example.saireddy.music.fragments.SongPlayingFragment.Staticated.playNext
import com.example.saireddy.music.fragments.SongPlayingFragment.Staticated.processInformation
import com.example.saireddy.music.fragments.SongPlayingFragment.Staticated.updateTextViews
import com.example.saireddy.music.fragments.SongPlayingFragment.Staticated.onSongComplete*/
import com.example.saireddy.music.fragments.SongPlayingFragment.Statified.currentPosition
import com.example.saireddy.music.fragments.SongPlayingFragment.Statified.currentSongHelper
import com.example.saireddy.music.fragments.SongPlayingFragment.Statified.endTimeText
import com.example.saireddy.music.fragments.SongPlayingFragment.Statified.fab
import com.example.saireddy.music.fragments.SongPlayingFragment.Statified.favoriteContent
import com.example.saireddy.music.fragments.SongPlayingFragment.Statified.fetchSongs
import com.example.saireddy.music.fragments.SongPlayingFragment.Statified.loopImageButton
import com.example.saireddy.music.fragments.SongPlayingFragment.Statified.mediaplayer
import com.example.saireddy.music.fragments.SongPlayingFragment.Statified.myActivity
import com.example.saireddy.music.fragments.SongPlayingFragment.Statified.nextImageButton
import com.example.saireddy.music.fragments.SongPlayingFragment.Statified.playPauseImageButton
import com.example.saireddy.music.fragments.SongPlayingFragment.Statified.previousImageButton
import com.example.saireddy.music.fragments.SongPlayingFragment.Statified.prg
import com.example.saireddy.music.fragments.SongPlayingFragment.Statified.seekBar
import com.example.saireddy.music.fragments.SongPlayingFragment.Statified.shuffleImageButton
import com.example.saireddy.music.fragments.SongPlayingFragment.Statified.songArtistView
import com.example.saireddy.music.fragments.SongPlayingFragment.Statified.songTitleView
import com.example.saireddy.music.fragments.SongPlayingFragment.Statified.startTimeText
import com.example.saireddy.music.fragments.SongPlayingFragment.Statified.updateSongTime
import java.util.*
import java.util.concurrent.TimeUnit


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class SongPlayingFragment : Fragment() {
    /*Here you may wonder that why did we create two objects namely Statified and
    Staticated respectively
    * These objects are created as the variables and functions will be used from another
    class
    * Now, the question is why did we make two different objects and not one single object
    * This is because we created the Statified object which contains all the variables and
    * the Staticated object which contain all the functions*/
    object Statified {
        var myActivity: Activity? = null
        var mediaplayer: MediaPlayer? = null
        var startTimeText: TextView? = null
        var endTimeText: TextView? = null
        var playPauseImageButton: ImageButton? = null
        var previousImageButton: ImageButton? = null
        var nextImageButton: ImageButton? = null
        var loopImageButton: ImageButton? = null
        var shuffleImageButton: ImageButton? = null
        var seekBar: SeekBar? = null
        var songArtistView: TextView? = null
        var songTitleView: TextView? = null
        var currentPosition: Int = 0
        var fetchSongs: ArrayList<Songs>? = null
        var currentSongHelper: CurrentSongHelper? = null
        var prg: Int? = 0
        /*Declaring variable for handling the favorite button*/
        var fab: ImageButton? = null
        /*Variable for using DB functions*/
        var favoriteContent: EchoDatabase? = null
        /*Sensor Variables*/
        var mSensorManager: SensorManager? = null
        var mSensorListener: SensorEventListener? = null
        var MY_PREFS_NAME = "ShakeFeature"
        var updateSongTime = object : Runnable {
            override fun run() {
                val getCurrent = mediaplayer?.currentPosition
                startTimeText?.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as Long),
                        TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as
                                Long))))
                seekBar?.setProgress(getCurrent?.toInt() as Int)
                Handler().postDelayed(this, 1000)
            }


        }

    }
    object Staticated {
        var MY_PREFS_SHUFFLE = "Shuffle feature"
        var MY_PREFS_LOOP = "Loop feature"
        fun onSongComplete() {
            if (currentSongHelper?.isShuffle as Boolean) {
                playNext("PlayNextLikeNormalShuffle")
                currentSongHelper?.isPlaying = true
            } else {
                if (currentSongHelper?.isLoop as Boolean) {
                    currentSongHelper?.isPlaying = true
                    var nextSong = fetchSongs?.get(currentPosition)
                    currentSongHelper?.currentPosition = currentPosition
                    currentSongHelper?.songPath = nextSong?.songData
                    currentSongHelper?.songTitle = nextSong?.songTitle
                    currentSongHelper?.songArtist = nextSong?.artist
                    currentSongHelper?.songId = nextSong?.SongId as Long
                    updateTextViews(currentSongHelper?.songTitle as String,
                            currentSongHelper?.songArtist as String)
                    mediaplayer?.reset()
                    try {
                        mediaplayer?.setDataSource(myActivity,
                                Uri.parse(currentSongHelper?.songPath))
                        mediaplayer?.prepare()
                        mediaplayer?.start()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    playNext("PlayNextNormal")
                    currentSongHelper?.isPlaying = true
                }
            }
            if (favoriteContent?.checkifIdExists(currentSongHelper?.songId?.toInt() as Int)
                            as Boolean) {
                fab?.setBackgroundResource(R.drawable.favorite_on)
            } else {
                fab?.setBackgroundResource(R.drawable.favorite_off)
            }
        }
        fun updateTextViews(songTitle: String, songArtist: String) {
            songTitleView?.setText(songTitle)
            songArtistView?.setText(songArtist)
        }
        fun processInformation(mediaPlayer: MediaPlayer) {
            val finalTime = mediaPlayer.duration
            val startTime = mediaPlayer.currentPosition
            seekBar?.max = finalTime
            startTimeText?.setText(String.format("%d: %d",
                    TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(startTime.toLong()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime.toLong())))
            )
            endTimeText?.setText(String.format("%d: %d",
                    TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong())))
            )
            seekBar?.setProgress(startTime)
            Handler().postDelayed(updateSongTime, 1000)

        }
        fun playNext(check: String) {
            if (check.equals("PlayNextNormal", true)) {
                currentPosition += 1
            } else if (check.equals("PlayNextLikeNormalShuffle", true)) {
                val randomObject = Random()
                val randomPosition = randomObject.nextInt(fetchSongs?.size?.plus(1) as
                        Int)
                currentPosition = randomPosition
            }
            if (currentPosition == fetchSongs?.size) {
                currentPosition = 0
            }
            currentSongHelper?.isLoop = false
            var nextSong = fetchSongs?.get(currentPosition)
            currentSongHelper?.songPath = nextSong?.songData
            currentSongHelper?.songTitle = nextSong?.songTitle
            currentSongHelper?.songArtist = nextSong?.artist
            currentSongHelper?.songId = nextSong?.SongId as Long
            updateTextViews(currentSongHelper?.songTitle as String,
                    currentSongHelper?.songArtist as String)
            mediaplayer?.stop()
            mediaplayer?.reset()
            try {
                mediaplayer?.setDataSource(myActivity, Uri.parse(currentSongHelper?.songPath))
                mediaplayer?.prepare()
                mediaplayer?.start()
                processInformation(mediaplayer as MediaPlayer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (favoriteContent?.checkifIdExists(currentSongHelper?.songId?.toInt() as Int)
                            as Boolean) {
                fab?.setBackgroundResource(R.drawable.favorite_on)
            } else {
                fab?.setBackgroundResource(R.drawable.favorite_off)
            }
        }

    }
    var mAcceleration: Float = 0f
    var mAccelerationCurrent: Float = 0f
    var mAccelerationLast: Float = 0f
    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
                             savedInstanceState: Bundle?): View? {
       val view = inflater.inflate(R.layout.fragment_song_playing, container, false)
       seekBar = view?.findViewById(R.id.seekBar)
       startTimeText = view?.findViewById(R.id.startTime)
       //endTimeText = view?.findViewById(R.id.endTime)
       playPauseImageButton = view?.findViewById(R.id.playPauseButton)
       nextImageButton = view?.findViewById(R.id.nextButton)
       previousImageButton = view?.findViewById(R.id.previousButton)
       loopImageButton = view?.findViewById(R.id.loopButton)
       shuffleImageButton = view?.findViewById(R.id.shuffleButton)
       songTitleView = view?.findViewById(R.id.songTitle)
       songArtistView = view?.findViewById(R.id.songArtist)
/*Linking it with the view*/
       fab = view?.findViewById(R.id.favoriteIcon)
/*Fading the favorite icon*/
       fab?.alpha = 0.8f
       return view
   }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }
    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }
    override fun onResume() {
        super.onResume()
/*Here we register the sensor*/
        Statified.mSensorManager?.registerListener(Statified.mSensorListener,
                Statified.mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL)
    }
    override fun onPause() {
                super.onPause()
/*When fragment is paused, we remove the sensor to prevent the battery drain*/
        Statified.mSensorManager?.unregisterListener(Statified.mSensorListener)
    }
    override fun onDestroyView() {
                super.onDestroyView()
    }

     fun stopPlay() {
        if(mediaplayer!=null){
            mediaplayer?.stop()
            mediaplayer?.release()
            mediaplayer = null
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

/*Sensor service is activate when the fragment is created*/
        Statified.mSensorManager =
                myActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
/*Default values*/
        mAcceleration = 0.0f
/*We take earth's gravitational value to be default, this will give us good
results*/
        mAccelerationCurrent = SensorManager.GRAVITY_EARTH
        mAccelerationLast = SensorManager.GRAVITY_EARTH


/*Here we call the function*/
        bindShakeListener()




    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
/*Initialising the database*/
        stopPlay()
        favoriteContent = EchoDatabase(myActivity)
        currentSongHelper = CurrentSongHelper()
        currentSongHelper?.isPlaying = true
        currentSongHelper?.isLoop = false
        currentSongHelper?.isShuffle = false
        var path: String? = null
        var _songTitle: String? = null
        var _songArtist: String? = null
        var songId: Long = 0
        try {
            path = arguments?.getString("path")
            _songTitle = arguments?.getString("songTitle")
            _songArtist = arguments?.getString("songArtist")
            songId = arguments?.getInt("songId")!!.toLong()
            currentPosition = arguments!!.getInt("position")
            fetchSongs = arguments!!.getParcelableArrayList("songData")
            currentSongHelper?.songPath = path
            currentSongHelper?.songTitle = _songTitle
            currentSongHelper?.songArtist = _songArtist
            currentSongHelper?.songId = songId
            currentSongHelper?.currentPosition = currentPosition
            Staticated.updateTextViews(currentSongHelper?.songTitle as String,
                    currentSongHelper?.songArtist as String)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser){
                    prg = progress

                }
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                mediaplayer?.seekTo(prg!!)
            }
        })

/*Here we check whether we came to the song playing fragment via tapping on a song
or by bottom bar*/
        var fromFavBottomBar = arguments?.get("FavBottomBar") as? String
        if (fromFavBottomBar != null) {
/*If we came via bottom bar then the already playing media player object is
used*/
            mediaplayer = FavoriteFragment.Statified.mediaPlayer
        } else {
/*Else we use the default way*/
            mediaplayer = MediaPlayer()
            mediaplayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            try {
                mediaplayer?.setDataSource(myActivity, Uri.parse(path))
                mediaplayer?.prepare()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mediaplayer?.start()
        }
        Staticated.processInformation(mediaplayer as MediaPlayer)
        if (currentSongHelper?.isPlaying as Boolean) {
            playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }
        mediaplayer?.setOnCompletionListener {
            Staticated.onSongComplete()
        }




        clickHandler()
        var prefsForShuffle = myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE,
                Context.MODE_PRIVATE)
        var isShuffleAllowed = prefsForShuffle?.getBoolean("feature", false)
        if (isShuffleAllowed as Boolean) {
            currentSongHelper?.isShuffle = true
            currentSongHelper?.isLoop = false
            shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
            loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        } else {
            currentSongHelper?.isShuffle = false
            shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
        }
        var prefsForLoop = myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP,
                Context.MODE_PRIVATE)
        var isLoopAllowed = prefsForLoop?.getBoolean("feature", false)
        if (isLoopAllowed as Boolean) {
            currentSongHelper?.isShuffle = false
            currentSongHelper?.isLoop = true
            shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        } else {
            loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
            currentSongHelper?.isLoop = false
        }
/*Here we check that if the song playing is a favorite, then we show a red colored
heart indicating favorite else only the heart boundary
* This action is performed whenever a new song is played, hence this will done in
the playNext(), playPrevious() and onSongComplete() methods*/
        if (favoriteContent?.checkifIdExists(currentSongHelper?.songId?.toInt() as Int) as
                        Boolean) {
            fab?.setImageResource(R.drawable.favorite_on)
        } else {
            fab?.setImageResource(R.drawable.favorite_off)
        }

    }



    fun clickHandler() {
/*Here we handle the click of the favorite icon
* When the icon was clicked, if it was red in color i.e. a favorite song then we
remove the song from favorites*/
        fab?.setOnClickListener {
            if (favoriteContent?.checkifIdExists(currentSongHelper?.songId?.toInt() as Int)
                            as Boolean) {
                fab?.setImageResource(R.drawable.favorite_off)
                favoriteContent?.deleteFavourite(currentSongHelper?.songId?.toInt() as
                        Int)
/*Toast is prompt message at the bottom of screen indicating that an
action has been performed*/
                Toast.makeText(myActivity, "Removed from Favorites",
                        Toast.LENGTH_SHORT).show()
            } else {
/*If the song was not a favorite, we then add it to the favorites using
the method we made in our database*/
                fab?.setImageResource(R.drawable.favorite_on)
                favoriteContent?.storeAsFavorite(currentSongHelper?.songId?.toInt(),
                        currentSongHelper?.songArtist, currentSongHelper?.songTitle, currentSongHelper?.songPath)
                Toast.makeText(myActivity, "Added to Favorites",
                        Toast.LENGTH_SHORT).show()
            }
        }
        shuffleImageButton?.setOnClickListener {
            var editorShuffle =
                    myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
            var editorLoop = myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP,
                    Context.MODE_PRIVATE)?.edit()
            if (currentSongHelper?.isShuffle as Boolean) {
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                currentSongHelper?.isShuffle = false
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()
            } else {
                currentSongHelper?.isShuffle = true
                currentSongHelper?.isLoop = false
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorShuffle?.putBoolean("feature", true)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()
            }
        }
        nextImageButton?.setOnClickListener {
            currentSongHelper?.isPlaying = true
            if (currentSongHelper?.isShuffle as Boolean) {
                Staticated.playNext("PlayNextLikeNormalShuffle")
            } else {
                Staticated.playNext("PlayNextNormal")
            }
        }
        previousImageButton?.setOnClickListener {
            currentSongHelper?.isPlaying = true
            if (currentSongHelper?.isLoop as Boolean) {
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
            }
            playPrevious()
        }
        loopImageButton?.setOnClickListener {
            var editorShuffle =
                    myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
            var editorLoop = myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP,
                    Context.MODE_PRIVATE)?.edit()
            if (currentSongHelper?.isLoop as Boolean) {
                currentSongHelper?.isLoop = false
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()
            } else {
                currentSongHelper?.isLoop = true
                currentSongHelper?.isShuffle = false
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature", true)
                editorLoop?.apply()
            }
        }
        playPauseImageButton?.setOnClickListener {
            if (mediaplayer?.isPlaying as Boolean) {
                mediaplayer?.pause()
                currentSongHelper?.isPlaying = false
                playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                mediaplayer?.start()
                currentSongHelper?.isPlaying = true
                playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        }
    }
    fun playPrevious() {
        currentPosition -= 1
        if (currentPosition == -1) {
            currentPosition = 0
        }
        if (currentSongHelper?.isPlaying as Boolean) {
            playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }
        currentSongHelper?.isLoop = false
        var nextSong = fetchSongs?.get(currentPosition)
        currentSongHelper?.songPath = nextSong?.songData
        currentSongHelper?.songTitle = nextSong?.songTitle
        currentSongHelper?.songArtist = nextSong?.artist
        currentSongHelper?.songId = nextSong?.SongId as Long
        Staticated.updateTextViews(currentSongHelper?.songTitle as String,
                currentSongHelper?.songArtist as String)
        mediaplayer?.stop()
        mediaplayer?.reset()
        try {
            mediaplayer?.setDataSource(myActivity, Uri.parse(currentSongHelper?.songPath))
            mediaplayer?.prepare()
            mediaplayer?.start()
            Staticated.processInformation(mediaplayer as MediaPlayer)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (favoriteContent?.checkifIdExists(currentSongHelper?.songId?.toInt() as Int) as
                        Boolean) {
            fab?.setImageResource(R.drawable.favorite_on)
        } else {
            fab?.setImageResource(R.drawable.favorite_off)
        }
    }
    /*This function handles the shake events in order to change the songs when we shake the
    phone*/
    fun bindShakeListener() {
/*The sensor listener has two methods used for its implementation i.e.
OnAccuracyChanged() and onSensorChanged*/
        Statified.mSensorListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
/*We do noot need to check or work with the accuracy changes for the
sensor*/
            }
            override fun onSensorChanged(event: SensorEvent) {
/*We need this onSensorChanged function
* This function is called when there is a new sensor event*/
/*The sensor event has 3 dimensions i.e. the x, y and z in which the
changes can occur*/
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

/*Now we shook the phone so the current acceleration will be the first to
start with*/
                mAccelerationLast = mAccelerationCurrent
/*Since we could have moved the phone in any direction, we calculate the
Euclidean distance to get the normalized distance*/
                mAccelerationCurrent = Math.sqrt(((x * x + y * y + z *
                        z).toDouble())).toFloat()
/*Delta gives the change in acceleration*/
                val delta = mAccelerationCurrent - mAccelerationLast
/*Here we calculate the lower filter
* The written below is a formula to get it*/
                mAcceleration = mAcceleration * 0.9f + delta
/*We obtain a real number for acceleration
* and we check if the acceleration was noticeable, considering 12 here*/
                if (mAcceleration > 12) {
/*If the acceleration was greater than 12 we change the song, given the fact
our shake to change was active*/
                    val prefs =
                            myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME, Context.MODE_PRIVATE)
                    val isAllowed = prefs?.getBoolean("feature", false)
                    if (isAllowed as Boolean) {
                        Staticated.playNext("PlayNextNormal")
                    }
                }
            }
        }
    }




}
