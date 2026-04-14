package com.netfeige.display.data;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.IBinder;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import com.geniusgithub.mediarender.DeviceInfo;
import com.geniusgithub.mediarender.DeviceUpdateBrocastFactory;
import com.geniusgithub.mediarender.center.MediaRenderProxy;
import com.netfeige.R;
import com.netfeige.common.GifOpenHelper;
import com.netfeige.common.HostInformation;
import com.netfeige.common.Music;
import com.netfeige.common.Public_Tools;
import com.netfeige.display.ui.ChoiceRemotePlayerActivity;
import com.netfeige.display.ui.wifi.WiFiState;
import com.netfeige.dlna.ContentTree;
import com.netfeige.dlna.IpMsgUPnPService;
import com.netfeige.kits.CrashHandler;
import com.netfeige.kits.DataConfig;
import com.netfeige.kits.ScreenShot;
import com.netfeige.service.IpmsgService;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.registry.DefaultRegistryListener;
import org.teleal.cling.registry.Registry;
import org.teleal.cling.registry.RegistryListener;

/* JADX INFO: loaded from: classes.dex */
public class IpmsgApplication extends Application {
    public static int MAIN_NOTIFICATION_ID = 1;
    public static int MSG_NOTIFICATION_ID = 2;
    public static final int g_nMxSeconds = 20;
    private static IpmsgApplication m_ipmsgAppInstance;
    public ArrayList<HashMap<String, Object>> faceList;
    public HashMap<String, Integer> faceMap;
    public AudioManager g_audioManager;
    public ArrayList<HashMap<String, Object>> g_headImageList;
    public HashMap<String, Integer> g_headMap;
    private ControlPoint m_controlPoint;
    private DeviceInfo m_deviceInfo;
    private MediaRenderProxy m_renderProxy;
    private AndroidUpnpService m_upnpService;
    public static List<Music> musicList = new ArrayList();
    public static boolean g_bIsDestroy = false;
    public static ArrayList<Music> g_arrMusicList = new ArrayList<>();
    public static ArrayList<Music> g_arrPlayingList = new ArrayList<>();
    public static ArrayList<Music> g_arrVideoList = new ArrayList<>();
    public static ArrayList<Music> g_playingVideoList = new ArrayList<>();
    public static ArrayList<Device> g_arrayLDevices = new ArrayList<>();
    public static HostInformation g_currentHostInfo = null;
    public static HashMap<String, String> g_mapUserSharePwd = new HashMap<>();
    public int updateStatus = 0;
    public String updatePath = "";
    public String newVersion = "";
    public String newVersionChanged = "";
    public String g_strAutoRecvFile = ContentTree.VIDEO_ID;
    public String g_strCheckUpdate = ContentTree.VIDEO_ID;
    public String g_strSendAudio = ContentTree.VIDEO_ID;
    public String g_strPromptAudio = ContentTree.VIDEO_ID;
    public String g_strMsgNotification = ContentTree.VIDEO_ID;
    public String g_strDelFilePrompt = ContentTree.VIDEO_ID;
    public ArrayList<String> g_listNetSectors = new ArrayList<>();
    public boolean g_bRemotePlayerStauts = true;
    public boolean g_bBackRuning = false;
    public int g_iUserColumn = 1;
    public int g_iUserItemLeft = 11;
    public IpmsgService ipmsgService = null;
    public Activity currentActivity = null;
    public Vector<String> waitSendfiles = new Vector<>();
    public WTOperateEnum g_wTOperateEnum = WTOperateEnum.NOTHING;
    public String g_strAPPref = "Feige_";
    private Drawable otherIcon = null;
    private HashMap<String, Drawable> extMap = new HashMap<>();
    private HashMap<String, String> memiMap = new HashMap<>();
    private HashMap<String, Drawable> monochromeMap = new HashMap<>();
    private WiFiState g_createWiFiState = WiFiState.createinitial;
    private WiFiState g_searchWiFiAPState = WiFiState.searchinitial;
    private RegistryListener m_registryListener = new BrowseRegistryListener();
    private GifOpenHelper m_gHelper = new GifOpenHelper();
    public ServiceConnection _connection = new ServiceConnection() { // from class: com.netfeige.display.data.IpmsgApplication.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            IpmsgApplication.this.ipmsgService = ((IpmsgService.LocalBinder) iBinder).getService();
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            IpmsgApplication.this.ipmsgService = null;
        }
    };
    public ServiceConnection serviceConnection = new ServiceConnection() { // from class: com.netfeige.display.data.IpmsgApplication.2
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            IpmsgApplication.this.m_upnpService = (AndroidUpnpService) iBinder;
            IpmsgApplication ipmsgApplication = IpmsgApplication.this;
            ipmsgApplication.m_controlPoint = ipmsgApplication.m_upnpService.getControlPoint();
            IpmsgApplication.g_arrayLDevices.clear();
            Iterator<Device> it = IpmsgApplication.this.m_upnpService.getRegistry().getDevices().iterator();
            while (it.hasNext()) {
                ((BrowseRegistryListener) IpmsgApplication.this.m_registryListener).deviceAdded(it.next());
            }
            IpmsgApplication.this.m_upnpService.getRegistry().addListener(IpmsgApplication.this.m_registryListener);
            IpmsgApplication.this.m_controlPoint.search(20);
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            IpmsgApplication.this.m_upnpService = null;
        }
    };

    public static synchronized IpmsgApplication getInstance() {
        return m_ipmsgAppInstance;
    }

    @Override // android.app.Application
    public void onCreate() {
        m_ipmsgAppInstance = this;
        this.m_deviceInfo = new DeviceInfo();
        MediaRenderProxy mediaRenderProxy = MediaRenderProxy.getInstance();
        this.m_renderProxy = mediaRenderProxy;
        mediaRenderProxy.startEngine();
        CrashHandler.getInstance().init(getApplicationContext());
        this.g_audioManager = (AudioManager) getSystemService("audio");
        Public_Tools.init(this);
        this.g_strAutoRecvFile = DataConfig.getInstance(getApplicationContext()).Read(2);
        this.g_strCheckUpdate = DataConfig.getInstance(getApplicationContext()).Read(3);
        this.g_strSendAudio = DataConfig.getInstance(getApplicationContext()).Read(4);
        this.g_strPromptAudio = DataConfig.getInstance(getApplicationContext()).Read(5);
        this.g_strDelFilePrompt = DataConfig.getInstance(getApplicationContext()).Read(7);
        String strRead = DataConfig.getInstance(getApplicationContext()).Read(37);
        if (strRead != null && strRead.length() > 0) {
            for (String str : strRead.split(",")) {
                this.g_listNetSectors.add(str);
            }
        }
        Intent intent = new Intent(this, (Class<?>) IpmsgService.class);
        startService(intent);
        bindService(intent, this._connection, 1);
        bindService(new Intent(this, (Class<?>) IpMsgUPnPService.class), this.serviceConnection, 1);
        this.extMap.put("m4a", getResources().getDrawable(R.drawable.audio));
        this.extMap.put("mp3", getResources().getDrawable(R.drawable.audio));
        this.extMap.put("mid", getResources().getDrawable(R.drawable.audio));
        this.extMap.put("xmf", getResources().getDrawable(R.drawable.audio));
        this.extMap.put("ogg", getResources().getDrawable(R.drawable.audio));
        this.extMap.put("wav", getResources().getDrawable(R.drawable.audio));
        this.extMap.put("wma", getResources().getDrawable(R.drawable.audio));
        this.extMap.put("wmv", getResources().getDrawable(R.drawable.video));
        this.extMap.put("3gp", getResources().getDrawable(R.drawable.video));
        this.extMap.put("mp4", getResources().getDrawable(R.drawable.video));
        this.extMap.put("rmvb", getResources().getDrawable(R.drawable.video));
        this.extMap.put("rm", getResources().getDrawable(R.drawable.video));
        this.extMap.put("mkv", getResources().getDrawable(R.drawable.video));
        this.extMap.put("avi", getResources().getDrawable(R.drawable.video));
        this.extMap.put("dat", getResources().getDrawable(R.drawable.video));
        this.extMap.put("mtv", getResources().getDrawable(R.drawable.video));
        this.extMap.put("mov", getResources().getDrawable(R.drawable.video));
        this.extMap.put("jpg", getResources().getDrawable(R.drawable.pic));
        this.extMap.put("jpeg", getResources().getDrawable(R.drawable.pic));
        this.extMap.put("gif", getResources().getDrawable(R.drawable.pic));
        this.extMap.put("png", getResources().getDrawable(R.drawable.pic));
        this.extMap.put("bmp", getResources().getDrawable(R.drawable.pic));
        this.extMap.put("txt", getResources().getDrawable(R.drawable.txt));
        this.extMap.put("rtf", getResources().getDrawable(R.drawable.txt));
        this.extMap.put("pdf", getResources().getDrawable(R.drawable.pdf));
        this.extMap.put("doc", getResources().getDrawable(R.drawable.word));
        this.extMap.put("docx", getResources().getDrawable(R.drawable.word));
        this.extMap.put("xls", getResources().getDrawable(R.drawable.excel));
        this.extMap.put("xlsx", getResources().getDrawable(R.drawable.excel));
        this.extMap.put("ppt", getResources().getDrawable(R.drawable.ppt));
        this.extMap.put("pps", getResources().getDrawable(R.drawable.ppt));
        this.extMap.put("pptx", getResources().getDrawable(R.drawable.ppt));
        this.extMap.put("zip", getResources().getDrawable(R.drawable.compress));
        this.extMap.put("rar", getResources().getDrawable(R.drawable.compress));
        this.extMap.put("7z", getResources().getDrawable(R.drawable.compress));
        this.extMap.put("apk", getResources().getDrawable(R.drawable.apk));
        this.extMap.put("html", getResources().getDrawable(R.drawable.html));
        this.extMap.put("htm", getResources().getDrawable(R.drawable.html));
        this.monochromeMap.put("pdf", getResources().getDrawable(R.drawable.mpdf));
        this.monochromeMap.put("m4a", getResources().getDrawable(R.drawable.maudio));
        this.monochromeMap.put("mp3", getResources().getDrawable(R.drawable.maudio));
        this.monochromeMap.put("mid", getResources().getDrawable(R.drawable.maudio));
        this.monochromeMap.put("xmf", getResources().getDrawable(R.drawable.maudio));
        this.monochromeMap.put("ogg", getResources().getDrawable(R.drawable.maudio));
        this.monochromeMap.put("wav", getResources().getDrawable(R.drawable.maudio));
        this.monochromeMap.put("3gp", getResources().getDrawable(R.drawable.mvideo));
        this.monochromeMap.put("mp4", getResources().getDrawable(R.drawable.mvideo));
        this.monochromeMap.put("jpg", getResources().getDrawable(R.drawable.mpic));
        this.monochromeMap.put("jpeg", getResources().getDrawable(R.drawable.mpic));
        this.monochromeMap.put("gif", getResources().getDrawable(R.drawable.mpic));
        this.monochromeMap.put("png", getResources().getDrawable(R.drawable.mpic));
        this.monochromeMap.put("bmp", getResources().getDrawable(R.drawable.mpic));
        this.monochromeMap.put("txt", getResources().getDrawable(R.drawable.mtxt));
        this.monochromeMap.put("doc", getResources().getDrawable(R.drawable.mword));
        this.monochromeMap.put("docx", getResources().getDrawable(R.drawable.mword));
        this.monochromeMap.put("xls", getResources().getDrawable(R.drawable.mexcel));
        this.monochromeMap.put("xlsx", getResources().getDrawable(R.drawable.mexcel));
        this.monochromeMap.put("ppt", getResources().getDrawable(R.drawable.mppt));
        this.monochromeMap.put("pps", getResources().getDrawable(R.drawable.mppt));
        this.monochromeMap.put("pptx", getResources().getDrawable(R.drawable.mppt));
        this.monochromeMap.put("apk", getResources().getDrawable(R.drawable.mapk));
        this.monochromeMap.put("html", getResources().getDrawable(R.drawable.mhtml));
        this.monochromeMap.put("htm", getResources().getDrawable(R.drawable.mhtml));
        this.monochromeMap.put("zip", getResources().getDrawable(R.drawable.mcompress));
        this.monochromeMap.put("rar", getResources().getDrawable(R.drawable.mcompress));
        this.monochromeMap.put("7z", getResources().getDrawable(R.drawable.mcompress));
        this.otherIcon = getResources().getDrawable(R.drawable.other);
        initFace();
        initHeadImage();
        super.onCreate();
    }

    private void initHeadImage() {
        this.g_headImageList = new ArrayList<>();
        this.g_headMap = new HashMap<>();
        int i = 0;
        for (int i2 = R.drawable.head_01; i2 <= R.drawable.head_40; i2++) {
            addHeadImage(i2, i);
            i++;
        }
    }

    private void addHeadImage(int i, int i2) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("headImage", Integer.valueOf(i));
        map.put("headCode", String.valueOf(i2));
        this.g_headImageList.add(map);
        this.g_headMap.put(String.valueOf(i2), Integer.valueOf(i));
    }

    public void initFace() {
        this.faceList = new ArrayList<>();
        this.faceMap = new HashMap<>();
        HashMap<String, Object> map = new HashMap<>();
        Integer numValueOf = Integer.valueOf(R.drawable.face_01);
        map.put("faceImage", numValueOf);
        map.put("faceCode", "^wx^");
        this.faceList.add(map);
        this.faceMap.put("^wx^", numValueOf);
        HashMap<String, Object> map2 = new HashMap<>();
        Integer numValueOf2 = Integer.valueOf(R.drawable.face_02);
        map2.put("faceImage", numValueOf2);
        map2.put("faceCode", "^pz^");
        this.faceList.add(map2);
        this.faceMap.put("^pz^", numValueOf2);
        HashMap<String, Object> map3 = new HashMap<>();
        Integer numValueOf3 = Integer.valueOf(R.drawable.face_03);
        map3.put("faceImage", numValueOf3);
        map3.put("faceCode", "^se^");
        this.faceList.add(map3);
        this.faceMap.put("^se^", numValueOf3);
        HashMap<String, Object> map4 = new HashMap<>();
        Integer numValueOf4 = Integer.valueOf(R.drawable.face_04);
        map4.put("faceImage", numValueOf4);
        map4.put("faceCode", "^fd^");
        this.faceList.add(map4);
        this.faceMap.put("^fd^", numValueOf4);
        HashMap<String, Object> map5 = new HashMap<>();
        Integer numValueOf5 = Integer.valueOf(R.drawable.face_05);
        map5.put("faceImage", numValueOf5);
        map5.put("faceCode", "^dy^");
        this.faceList.add(map5);
        this.faceMap.put("^dy^", numValueOf5);
        HashMap<String, Object> map6 = new HashMap<>();
        Integer numValueOf6 = Integer.valueOf(R.drawable.face_06);
        map6.put("faceImage", numValueOf6);
        map6.put("faceCode", "^ll^");
        this.faceList.add(map6);
        this.faceMap.put("^ll^", numValueOf6);
        HashMap<String, Object> map7 = new HashMap<>();
        Integer numValueOf7 = Integer.valueOf(R.drawable.face_07);
        map7.put("faceImage", numValueOf7);
        map7.put("faceCode", "^hx^");
        this.faceList.add(map7);
        this.faceMap.put("^hx^", numValueOf7);
        HashMap<String, Object> map8 = new HashMap<>();
        Integer numValueOf8 = Integer.valueOf(R.drawable.face_08);
        map8.put("faceImage", numValueOf8);
        map8.put("faceCode", "^dz^");
        this.faceList.add(map8);
        this.faceMap.put("^dz^", numValueOf8);
        HashMap<String, Object> map9 = new HashMap<>();
        Integer numValueOf9 = Integer.valueOf(R.drawable.face_09);
        map9.put("faceImage", numValueOf9);
        map9.put("faceCode", "^shui^");
        this.faceList.add(map9);
        this.faceMap.put("^shui^", numValueOf9);
        HashMap<String, Object> map10 = new HashMap<>();
        Integer numValueOf10 = Integer.valueOf(R.drawable.face_10);
        map10.put("faceImage", numValueOf10);
        map10.put("faceCode", "^jy^");
        this.faceList.add(map10);
        this.faceMap.put("^jy^", numValueOf10);
        HashMap<String, Object> map11 = new HashMap<>();
        map11.put("faceImage", Integer.valueOf(R.drawable.face_11));
        map11.put("faceCode", "^gg^");
        this.faceList.add(map11);
        this.faceMap.put("^gg^", Integer.valueOf(R.drawable.face_11));
        HashMap<String, Object> map12 = new HashMap<>();
        map12.put("faceImage", Integer.valueOf(R.drawable.face_12));
        map12.put("faceCode", "^fn^");
        this.faceList.add(map12);
        this.faceMap.put("^fn^", Integer.valueOf(R.drawable.face_12));
        HashMap<String, Object> map13 = new HashMap<>();
        map13.put("faceImage", Integer.valueOf(R.drawable.face_13));
        map13.put("faceCode", "^tp^");
        this.faceList.add(map13);
        this.faceMap.put("^tp^", Integer.valueOf(R.drawable.face_13));
        HashMap<String, Object> map14 = new HashMap<>();
        map14.put("faceImage", Integer.valueOf(R.drawable.face_14));
        map14.put("faceCode", "^cy^");
        this.faceList.add(map14);
        this.faceMap.put("^cy^", Integer.valueOf(R.drawable.face_14));
        HashMap<String, Object> map15 = new HashMap<>();
        map15.put("faceImage", Integer.valueOf(R.drawable.face_15));
        map15.put("faceCode", "^ng^");
        this.faceList.add(map15);
        this.faceMap.put("^ng^", Integer.valueOf(R.drawable.face_15));
        HashMap<String, Object> map16 = new HashMap<>();
        map16.put("faceImage", Integer.valueOf(R.drawable.face_16));
        map16.put("faceCode", "^ku^");
        this.faceList.add(map16);
        this.faceMap.put("^ku^", Integer.valueOf(R.drawable.face_16));
        HashMap<String, Object> map17 = new HashMap<>();
        map17.put("faceImage", Integer.valueOf(R.drawable.face_17));
        map17.put("faceCode", "^lengh^");
        this.faceList.add(map17);
        this.faceMap.put("^lengh^", Integer.valueOf(R.drawable.face_17));
        HashMap<String, Object> map18 = new HashMap<>();
        map18.put("faceImage", Integer.valueOf(R.drawable.face_18));
        map18.put("faceCode", "^zk^");
        this.faceList.add(map18);
        this.faceMap.put("^zk^", Integer.valueOf(R.drawable.face_18));
        HashMap<String, Object> map19 = new HashMap<>();
        map19.put("faceImage", Integer.valueOf(R.drawable.face_19));
        map19.put("faceCode", "^tu^");
        this.faceList.add(map19);
        this.faceMap.put("^tu^", Integer.valueOf(R.drawable.face_19));
        HashMap<String, Object> map20 = new HashMap<>();
        map20.put("faceImage", Integer.valueOf(R.drawable.face_20));
        map20.put("faceCode", "^tx^");
        this.faceList.add(map20);
        this.faceMap.put("^tx^", Integer.valueOf(R.drawable.face_20));
        HashMap<String, Object> map21 = new HashMap<>();
        map21.put("faceImage", Integer.valueOf(R.drawable.face_21));
        map21.put("faceCode", "^ka^");
        this.faceList.add(map21);
        this.faceMap.put("^ka^", Integer.valueOf(R.drawable.face_21));
        HashMap<String, Object> map22 = new HashMap<>();
        map22.put("faceImage", Integer.valueOf(R.drawable.face_22));
        map22.put("faceCode", "^by^");
        this.faceList.add(map22);
        this.faceMap.put("^by^", Integer.valueOf(R.drawable.face_22));
        HashMap<String, Object> map23 = new HashMap<>();
        map23.put("faceImage", Integer.valueOf(R.drawable.face_23));
        map23.put("faceCode", "^am^");
        this.faceList.add(map23);
        this.faceMap.put("^am^", Integer.valueOf(R.drawable.face_23));
        HashMap<String, Object> map24 = new HashMap<>();
        map24.put("faceImage", Integer.valueOf(R.drawable.face_24));
        map24.put("faceCode", "^je^");
        this.faceList.add(map24);
        this.faceMap.put("^je^", Integer.valueOf(R.drawable.face_24));
        HashMap<String, Object> map25 = new HashMap<>();
        map25.put("faceImage", Integer.valueOf(R.drawable.face_25));
        map25.put("faceCode", "^kun^");
        this.faceList.add(map25);
        this.faceMap.put("^kun^", Integer.valueOf(R.drawable.face_25));
        HashMap<String, Object> map26 = new HashMap<>();
        map26.put("faceImage", Integer.valueOf(R.drawable.face_26));
        map26.put("faceCode", "^jk^");
        this.faceList.add(map26);
        this.faceMap.put("^jk^", Integer.valueOf(R.drawable.face_26));
        HashMap<String, Object> map27 = new HashMap<>();
        map27.put("faceImage", Integer.valueOf(R.drawable.face_27));
        map27.put("faceCode", "^lh^");
        this.faceList.add(map27);
        this.faceMap.put("^lh^", Integer.valueOf(R.drawable.face_27));
        HashMap<String, Object> map28 = new HashMap<>();
        map28.put("faceImage", Integer.valueOf(R.drawable.face_28));
        map28.put("faceCode", "^hanx^");
        this.faceList.add(map28);
        this.faceMap.put("^hanx^", Integer.valueOf(R.drawable.face_28));
        HashMap<String, Object> map29 = new HashMap<>();
        map29.put("faceImage", Integer.valueOf(R.drawable.face_29));
        map29.put("faceCode", "^db^");
        this.faceList.add(map29);
        this.faceMap.put("^db^", Integer.valueOf(R.drawable.face_29));
        HashMap<String, Object> map30 = new HashMap<>();
        map30.put("faceImage", Integer.valueOf(R.drawable.face_30));
        map30.put("faceCode", "^fend^");
        this.faceList.add(map30);
        this.faceMap.put("^fend^", Integer.valueOf(R.drawable.face_30));
        HashMap<String, Object> map31 = new HashMap<>();
        map31.put("faceImage", Integer.valueOf(R.drawable.face_31));
        map31.put("faceCode", "^zhm^");
        this.faceList.add(map31);
        this.faceMap.put("^zhm^", Integer.valueOf(R.drawable.face_31));
        HashMap<String, Object> map32 = new HashMap<>();
        map32.put("faceImage", Integer.valueOf(R.drawable.face_32));
        map32.put("faceCode", "^yw^");
        this.faceList.add(map32);
        this.faceMap.put("^yw^", Integer.valueOf(R.drawable.face_32));
        HashMap<String, Object> map33 = new HashMap<>();
        map33.put("faceImage", Integer.valueOf(R.drawable.face_33));
        map33.put("faceCode", "^xu^");
        this.faceList.add(map33);
        this.faceMap.put("^xu^", Integer.valueOf(R.drawable.face_33));
        HashMap<String, Object> map34 = new HashMap<>();
        map34.put("faceImage", Integer.valueOf(R.drawable.face_34));
        map34.put("faceCode", "^yun^");
        this.faceList.add(map34);
        this.faceMap.put("^yun^", Integer.valueOf(R.drawable.face_34));
        HashMap<String, Object> map35 = new HashMap<>();
        map35.put("faceImage", Integer.valueOf(R.drawable.face_35));
        map35.put("faceCode", "^zhem^");
        this.faceList.add(map35);
        this.faceMap.put("^zhem^", Integer.valueOf(R.drawable.face_35));
        HashMap<String, Object> map36 = new HashMap<>();
        map36.put("faceImage", Integer.valueOf(R.drawable.face_36));
        map36.put("faceCode", "^shuai^");
        this.faceList.add(map36);
        this.faceMap.put("^shuai^", Integer.valueOf(R.drawable.face_36));
        HashMap<String, Object> map37 = new HashMap<>();
        map37.put("faceImage", Integer.valueOf(R.drawable.face_37));
        map37.put("faceCode", "^kl^");
        this.faceList.add(map37);
        this.faceMap.put("^kl^", Integer.valueOf(R.drawable.face_37));
        HashMap<String, Object> map38 = new HashMap<>();
        map38.put("faceImage", Integer.valueOf(R.drawable.face_38));
        map38.put("faceCode", "^qd^");
        this.faceList.add(map38);
        this.faceMap.put("^qd^", Integer.valueOf(R.drawable.face_38));
        HashMap<String, Object> map39 = new HashMap<>();
        map39.put("faceImage", Integer.valueOf(R.drawable.face_39));
        map39.put("faceCode", "^zj^");
        this.faceList.add(map39);
        this.faceMap.put("^zj^", Integer.valueOf(R.drawable.face_39));
        HashMap<String, Object> map40 = new HashMap<>();
        map40.put("faceImage", Integer.valueOf(R.drawable.face_40));
        map40.put("faceCode", "^ch^");
        this.faceList.add(map40);
        this.faceMap.put("^ch^", Integer.valueOf(R.drawable.face_40));
        HashMap<String, Object> map41 = new HashMap<>();
        map41.put("faceImage", Integer.valueOf(R.drawable.face_41));
        map41.put("faceCode", "^kb^");
        this.faceList.add(map41);
        this.faceMap.put("^kb^", Integer.valueOf(R.drawable.face_41));
        HashMap<String, Object> map42 = new HashMap<>();
        map42.put("faceImage", Integer.valueOf(R.drawable.face_42));
        map42.put("faceCode", "^gz^");
        this.faceList.add(map42);
        this.faceMap.put("^gz^", Integer.valueOf(R.drawable.face_42));
        HashMap<String, Object> map43 = new HashMap<>();
        map43.put("faceImage", Integer.valueOf(R.drawable.face_43));
        map43.put("faceCode", "^qdl^");
        this.faceList.add(map43);
        this.faceMap.put("^qdl^", Integer.valueOf(R.drawable.face_43));
        HashMap<String, Object> map44 = new HashMap<>();
        map44.put("faceImage", Integer.valueOf(R.drawable.face_44));
        map44.put("faceCode", "^huaix^");
        this.faceList.add(map44);
        this.faceMap.put("^huaix^", Integer.valueOf(R.drawable.face_44));
        HashMap<String, Object> map45 = new HashMap<>();
        map45.put("faceImage", Integer.valueOf(R.drawable.face_45));
        map45.put("faceCode", "^zhh^");
        this.faceList.add(map45);
        this.faceMap.put("^zhh^", Integer.valueOf(R.drawable.face_45));
        HashMap<String, Object> map46 = new HashMap<>();
        map46.put("faceImage", Integer.valueOf(R.drawable.face_46));
        map46.put("faceCode", "^yhh^");
        this.faceList.add(map46);
        this.faceMap.put("^yhh^", Integer.valueOf(R.drawable.face_46));
        HashMap<String, Object> map47 = new HashMap<>();
        map47.put("faceImage", Integer.valueOf(R.drawable.face_47));
        map47.put("faceCode", "^hq^");
        this.faceList.add(map47);
        this.faceMap.put("^hq^", Integer.valueOf(R.drawable.face_47));
        HashMap<String, Object> map48 = new HashMap<>();
        map48.put("faceImage", Integer.valueOf(R.drawable.face_48));
        map48.put("faceCode", "^bs^");
        this.faceList.add(map48);
        this.faceMap.put("^bs^", Integer.valueOf(R.drawable.face_48));
        HashMap<String, Object> map49 = new HashMap<>();
        map49.put("faceImage", Integer.valueOf(R.drawable.face_49));
        map49.put("faceCode", "^wq^");
        this.faceList.add(map49);
        this.faceMap.put("^wq^", Integer.valueOf(R.drawable.face_49));
        HashMap<String, Object> map50 = new HashMap<>();
        map50.put("faceImage", Integer.valueOf(R.drawable.face_50));
        map50.put("faceCode", "^zql^");
        this.faceList.add(map50);
        this.faceMap.put("^zql^", Integer.valueOf(R.drawable.face_50));
        HashMap<String, Object> map51 = new HashMap<>();
        map51.put("faceImage", Integer.valueOf(R.drawable.face_51));
        map51.put("faceCode", "^xe^");
        this.faceList.add(map51);
        this.faceMap.put("^xe^", Integer.valueOf(R.drawable.face_51));
        HashMap<String, Object> map52 = new HashMap<>();
        map52.put("faceImage", Integer.valueOf(R.drawable.face_52));
        map52.put("faceCode", "^qq^");
        this.faceList.add(map52);
        this.faceMap.put("^qq^", Integer.valueOf(R.drawable.face_52));
        HashMap<String, Object> map53 = new HashMap<>();
        map53.put("faceImage", Integer.valueOf(R.drawable.face_53));
        map53.put("faceCode", "^deng^");
        this.faceList.add(map53);
        this.faceMap.put("^deng^", Integer.valueOf(R.drawable.face_53));
        HashMap<String, Object> map54 = new HashMap<>();
        map54.put("faceImage", Integer.valueOf(R.drawable.face_54));
        map54.put("faceCode", "^kel^");
        this.faceList.add(map54);
        this.faceMap.put("^kel^", Integer.valueOf(R.drawable.face_54));
        HashMap<String, Object> map55 = new HashMap<>();
        map55.put("faceImage", Integer.valueOf(R.drawable.face_55));
        map55.put("faceCode", "^kkl^");
        this.faceList.add(map55);
        this.faceMap.put("^kkl^", Integer.valueOf(R.drawable.face_55));
        HashMap<String, Object> map56 = new HashMap<>();
        map56.put("faceImage", Integer.valueOf(R.drawable.face_56));
        map56.put("faceCode", "^cd^");
        this.faceList.add(map56);
        this.faceMap.put("^cd^", Integer.valueOf(R.drawable.face_56));
        HashMap<String, Object> map57 = new HashMap<>();
        map57.put("faceImage", Integer.valueOf(R.drawable.face_57));
        map57.put("faceCode", "^xg^");
        this.faceList.add(map57);
        this.faceMap.put("^xg^", Integer.valueOf(R.drawable.face_57));
        HashMap<String, Object> map58 = new HashMap<>();
        map58.put("faceImage", Integer.valueOf(R.drawable.face_58));
        map58.put("faceCode", "^pj^");
        this.faceList.add(map58);
        this.faceMap.put("^pj^", Integer.valueOf(R.drawable.face_58));
        HashMap<String, Object> map59 = new HashMap<>();
        map59.put("faceImage", Integer.valueOf(R.drawable.face_59));
        map59.put("faceCode", "^lq^");
        this.faceList.add(map59);
        this.faceMap.put("^lq^", Integer.valueOf(R.drawable.face_59));
        HashMap<String, Object> map60 = new HashMap<>();
        map60.put("faceImage", Integer.valueOf(R.drawable.face_60));
        map60.put("faceCode", "^pp^");
        this.faceList.add(map60);
        this.faceMap.put("^pp^", Integer.valueOf(R.drawable.face_60));
        HashMap<String, Object> map61 = new HashMap<>();
        map61.put("faceImage", Integer.valueOf(R.drawable.face_61));
        map61.put("faceCode", "^kf^");
        this.faceList.add(map61);
        this.faceMap.put("^kf^", Integer.valueOf(R.drawable.face_61));
        HashMap<String, Object> map62 = new HashMap<>();
        map62.put("faceImage", Integer.valueOf(R.drawable.face_62));
        map62.put("faceCode", "^mf^");
        this.faceList.add(map62);
        this.faceMap.put("^mf^", Integer.valueOf(R.drawable.face_62));
        HashMap<String, Object> map63 = new HashMap<>();
        map63.put("faceImage", Integer.valueOf(R.drawable.face_63));
        map63.put("faceCode", "^zht^");
        this.faceList.add(map63);
        this.faceMap.put("^zht^", Integer.valueOf(R.drawable.face_63));
        HashMap<String, Object> map64 = new HashMap<>();
        map64.put("faceImage", Integer.valueOf(R.drawable.face_64));
        map64.put("faceCode", "^mg^");
        this.faceList.add(map64);
        this.faceMap.put("^mg^", Integer.valueOf(R.drawable.face_64));
        HashMap<String, Object> map65 = new HashMap<>();
        map65.put("faceImage", Integer.valueOf(R.drawable.face_65));
        map65.put("faceCode", "^mgdx^");
        this.faceList.add(map65);
        this.faceMap.put("^mgdx^", Integer.valueOf(R.drawable.face_65));
        HashMap<String, Object> map66 = new HashMap<>();
        map66.put("faceImage", Integer.valueOf(R.drawable.face_66));
        map66.put("faceCode", "^wm^");
        this.faceList.add(map66);
        this.faceMap.put("^wm^", Integer.valueOf(R.drawable.face_66));
        HashMap<String, Object> map67 = new HashMap<>();
        map67.put("faceImage", Integer.valueOf(R.drawable.face_67));
        map67.put("faceCode", "^ax^");
        this.faceList.add(map67);
        this.faceMap.put("^ax^", Integer.valueOf(R.drawable.face_67));
        HashMap<String, Object> map68 = new HashMap<>();
        map68.put("faceImage", Integer.valueOf(R.drawable.face_68));
        map68.put("faceCode", "^xs^");
        this.faceList.add(map68);
        this.faceMap.put("^xs^", Integer.valueOf(R.drawable.face_68));
        HashMap<String, Object> map69 = new HashMap<>();
        map69.put("faceImage", Integer.valueOf(R.drawable.face_69));
        map69.put("faceCode", "^dg^");
        this.faceList.add(map69);
        this.faceMap.put("^dg^", Integer.valueOf(R.drawable.face_69));
        HashMap<String, Object> map70 = new HashMap<>();
        map70.put("faceImage", Integer.valueOf(R.drawable.face_70));
        map70.put("faceCode", "^shd^");
        this.faceList.add(map70);
        this.faceMap.put("^shd^", Integer.valueOf(R.drawable.face_70));
        HashMap<String, Object> map71 = new HashMap<>();
        map71.put("faceImage", Integer.valueOf(R.drawable.face_71));
        map71.put("faceCode", "^zhd^");
        this.faceList.add(map71);
        this.faceMap.put("^zhd^", Integer.valueOf(R.drawable.face_71));
        HashMap<String, Object> map72 = new HashMap<>();
        map72.put("faceImage", Integer.valueOf(R.drawable.face_72));
        map72.put("faceCode", "^dao^");
        this.faceList.add(map72);
        this.faceMap.put("^dao^", Integer.valueOf(R.drawable.face_72));
        HashMap<String, Object> map73 = new HashMap<>();
        map73.put("faceImage", Integer.valueOf(R.drawable.face_73));
        map73.put("faceCode", "^zqb^");
        this.faceList.add(map73);
        this.faceMap.put("^zqb^", Integer.valueOf(R.drawable.face_73));
        HashMap<String, Object> map74 = new HashMap<>();
        map74.put("faceImage", Integer.valueOf(R.drawable.face_74));
        map74.put("faceCode", "^jgz^");
        this.faceList.add(map74);
        this.faceMap.put("^jgz^", Integer.valueOf(R.drawable.face_74));
        HashMap<String, Object> map75 = new HashMap<>();
        map75.put("faceImage", Integer.valueOf(R.drawable.face_75));
        map75.put("faceCode", "^bb^");
        this.faceList.add(map75);
        this.faceMap.put("^bb^", Integer.valueOf(R.drawable.face_75));
        HashMap<String, Object> map76 = new HashMap<>();
        map76.put("faceImage", Integer.valueOf(R.drawable.face_76));
        map76.put("faceCode", "^yl^");
        this.faceList.add(map76);
        this.faceMap.put("^yl^", Integer.valueOf(R.drawable.face_76));
        HashMap<String, Object> map77 = new HashMap<>();
        map77.put("faceImage", Integer.valueOf(R.drawable.face_77));
        map77.put("faceCode", "^ty^");
        this.faceList.add(map77);
        this.faceMap.put("^ty^", Integer.valueOf(R.drawable.face_77));
        HashMap<String, Object> map78 = new HashMap<>();
        map78.put("faceImage", Integer.valueOf(R.drawable.face_78));
        map78.put("faceCode", "^lw^");
        this.faceList.add(map78);
        this.faceMap.put("^lw^", Integer.valueOf(R.drawable.face_78));
        HashMap<String, Object> map79 = new HashMap<>();
        map79.put("faceImage", Integer.valueOf(R.drawable.face_79));
        map79.put("faceCode", "^xr^");
        this.faceList.add(map79);
        this.faceMap.put("^xr^", Integer.valueOf(R.drawable.face_79));
        HashMap<String, Object> map80 = new HashMap<>();
        map80.put("faceImage", Integer.valueOf(R.drawable.face_80));
        map80.put("faceCode", "^qiang^");
        this.faceList.add(map80);
        this.faceMap.put("^qiang^", Integer.valueOf(R.drawable.face_80));
        HashMap<String, Object> map81 = new HashMap<>();
        map81.put("faceImage", Integer.valueOf(R.drawable.face_81));
        map81.put("faceCode", "^ruo^");
        this.faceList.add(map81);
        this.faceMap.put("^ruo^", Integer.valueOf(R.drawable.face_81));
        HashMap<String, Object> map82 = new HashMap<>();
        map82.put("faceImage", Integer.valueOf(R.drawable.face_82));
        map82.put("faceCode", "^ws^");
        this.faceList.add(map82);
        this.faceMap.put("^ws^", Integer.valueOf(R.drawable.face_82));
        HashMap<String, Object> map83 = new HashMap<>();
        map83.put("faceImage", Integer.valueOf(R.drawable.face_83));
        map83.put("faceCode", "^shl^");
        this.faceList.add(map83);
        this.faceMap.put("^shl^", Integer.valueOf(R.drawable.face_83));
        HashMap<String, Object> map84 = new HashMap<>();
        map84.put("faceImage", Integer.valueOf(R.drawable.face_84));
        map84.put("faceCode", "^fl^");
        this.faceList.add(map84);
        this.faceMap.put("^fl^", Integer.valueOf(R.drawable.face_84));
        HashMap<String, Object> map85 = new HashMap<>();
        map85.put("faceImage", Integer.valueOf(R.drawable.face_85));
        map85.put("faceCode", "^gy^");
        this.faceList.add(map85);
        this.faceMap.put("^gy^", Integer.valueOf(R.drawable.face_85));
        HashMap<String, Object> map86 = new HashMap<>();
        map86.put("faceImage", Integer.valueOf(R.drawable.face_86));
        map86.put("faceCode", "^qt^");
        this.faceList.add(map86);
        this.faceMap.put("^qt^", Integer.valueOf(R.drawable.face_86));
        HashMap<String, Object> map87 = new HashMap<>();
        map87.put("faceImage", Integer.valueOf(R.drawable.face_87));
        map87.put("faceCode", "^cj^");
        this.faceList.add(map87);
        this.faceMap.put("^cj^", Integer.valueOf(R.drawable.face_87));
        HashMap<String, Object> map88 = new HashMap<>();
        map88.put("faceImage", Integer.valueOf(R.drawable.face_88));
        map88.put("faceCode", "^an^");
        this.faceList.add(map88);
        this.faceMap.put("^an^", Integer.valueOf(R.drawable.face_88));
        HashMap<String, Object> map89 = new HashMap<>();
        map89.put("faceImage", Integer.valueOf(R.drawable.face_89));
        map89.put("faceCode", "^no^");
        this.faceList.add(map89);
        this.faceMap.put("^no^", Integer.valueOf(R.drawable.face_89));
        HashMap<String, Object> map90 = new HashMap<>();
        map90.put("faceImage", Integer.valueOf(R.drawable.face_90));
        map90.put("faceCode", "^ok^");
        this.faceList.add(map90);
        this.faceMap.put("^ok^", Integer.valueOf(R.drawable.face_90));
    }

    /* JADX WARN: Code restructure failed: missing block: B:71:0x0286, code lost:
    
        r4.append((java.lang.CharSequence) r8);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public android.text.SpannableStringBuilder appendFace(java.lang.String r20, android.widget.TextView r21, java.util.List<com.netfeige.kits.ScreenShot> r22) {
        /*
            Method dump skipped, instruction units count: 653
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.netfeige.display.data.IpmsgApplication.appendFace(java.lang.String, android.widget.TextView, java.util.List):android.text.SpannableStringBuilder");
    }

    public SpannableStringBuilder appendFace(HashMap<String, Object> map) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        if (this.faceList == null) {
            initFace();
        }
        Drawable drawable = getResources().getDrawable(((Integer) map.get("faceImage")).intValue());
        drawable.setBounds(0, 0, 24, 24);
        SpannableString spannableString = new SpannableString((String) map.get("faceCode"));
        spannableString.setSpan(new ImageSpan(drawable, 0), 0, ((String) map.get("faceCode")).length(), 33);
        spannableStringBuilder.append((CharSequence) spannableString);
        return spannableStringBuilder;
    }

    public SpannableStringBuilder appendPrompt(String str, TextView textView, List<ScreenShot> list) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        Drawable drawable = getResources().getDrawable(R.drawable.error);
        drawable.setBounds(0, 0, 24, 24);
        SpannableString spannableString = new SpannableString("x");
        spannableString.setSpan(new ImageSpan(drawable, 0), 0, 1, 33);
        spannableStringBuilder.append((CharSequence) spannableString);
        spannableStringBuilder.append((CharSequence) "娑堟伅\"").append((CharSequence) appendFace(str, textView, list)).append((CharSequence) "\"发送失败");
        return spannableStringBuilder;
    }

    public void dismissTempNotification() {
        ((NotificationManager) getSystemService("notification")).cancel(MAIN_NOTIFICATION_ID);
    }

    public String getFileExtName(String str) {
        if (str == null) {
            return null;
        }
        int iLastIndexOf = str.lastIndexOf(".");
        return iLastIndexOf == -1 ? str : str.substring(iLastIndexOf + 1).toLowerCase();
    }

    public Drawable getIconByExtentionName(String str, boolean z, boolean z2) {
        if (str == null) {
            return null;
        }
        String fileExtName = getFileExtName(str);
        Drawable drawableLoadIcon = z ? this.extMap.get(fileExtName) : null;
        if (drawableLoadIcon == null) {
            PackageManager packageManager = getPackageManager();
            String mimeTypeFromExtension = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtName);
            if (mimeTypeFromExtension == null) {
                return this.otherIcon;
            }
            Uri uriFromFile = Uri.fromFile(new File(str));
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setDataAndType(uriFromFile, mimeTypeFromExtension);
            List<ResolveInfo> listQueryIntentActivities = packageManager.queryIntentActivities(intent, 65536);
            if (listQueryIntentActivities != null && listQueryIntentActivities.size() != 0) {
                drawableLoadIcon = listQueryIntentActivities.get(0).loadIcon(packageManager);
            }
            if (drawableLoadIcon != null && z2) {
                this.extMap.put(fileExtName, drawableLoadIcon);
                this.memiMap.put(mimeTypeFromExtension, fileExtName);
            }
        }
        return drawableLoadIcon == null ? this.otherIcon : drawableLoadIcon;
    }

    public Drawable getMIconByExtentionName(String str) {
        Drawable drawable = this.monochromeMap.get(getFileExtName(str));
        return drawable == null ? getResources().getDrawable(R.drawable.mother) : drawable;
    }

    public WiFiState getCreateWiFiAPState() {
        return this.g_createWiFiState;
    }

    public void setCreateWiFiAPState(WiFiState wiFiState) {
        this.g_createWiFiState = wiFiState;
    }

    public WiFiState getSearchWiFiAPState() {
        return this.g_searchWiFiAPState;
    }

    public void setSearchWiFiAPState(WiFiState wiFiState) {
        this.g_searchWiFiAPState = wiFiState;
    }

    public BrowseRegistryListener getBrowseRegistryListener() {
        return (BrowseRegistryListener) this.m_registryListener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public class BrowseRegistryListener extends DefaultRegistryListener {
        private BrowseRegistryListener() {
        }

        @Override // org.teleal.cling.registry.DefaultRegistryListener, org.teleal.cling.registry.RegistryListener
        public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice remoteDevice) {
            deviceAdded(remoteDevice);
        }

        @Override // org.teleal.cling.registry.DefaultRegistryListener, org.teleal.cling.registry.RegistryListener
        public void remoteDeviceDiscoveryFailed(Registry registry, RemoteDevice remoteDevice, Exception exc) {
            deviceRemoved(remoteDevice);
        }

        @Override // org.teleal.cling.registry.DefaultRegistryListener, org.teleal.cling.registry.RegistryListener
        public void remoteDeviceAdded(Registry registry, RemoteDevice remoteDevice) {
            deviceAdded(remoteDevice);
        }

        @Override // org.teleal.cling.registry.DefaultRegistryListener, org.teleal.cling.registry.RegistryListener
        public void remoteDeviceRemoved(Registry registry, RemoteDevice remoteDevice) {
            deviceRemoved(remoteDevice);
        }

        @Override // org.teleal.cling.registry.DefaultRegistryListener, org.teleal.cling.registry.RegistryListener
        public void localDeviceAdded(Registry registry, LocalDevice localDevice) {
            deviceAdded(localDevice);
        }

        @Override // org.teleal.cling.registry.DefaultRegistryListener, org.teleal.cling.registry.RegistryListener
        public void localDeviceRemoved(Registry registry, LocalDevice localDevice) {
            deviceRemoved(localDevice);
        }

        public void deviceAdded(final Device device) {
            new Runnable() { // from class: com.netfeige.display.data.IpmsgApplication.BrowseRegistryListener.1
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        String friendlyName = device.getDetails().getFriendlyName();
                        String strRead = DataConfig.getInstance(IpmsgApplication.this.currentActivity).Read(0);
                        if (strRead == null || !strRead.equals(friendlyName)) {
                            int iIndexOf = IpmsgApplication.g_arrayLDevices.indexOf(device);
                            if (iIndexOf >= 0) {
                                IpmsgApplication.g_arrayLDevices.remove(device);
                                IpmsgApplication.g_arrayLDevices.add(iIndexOf, device);
                            } else {
                                IpmsgApplication.g_arrayLDevices.add(device);
                            }
                            if (IpmsgApplication.this.currentActivity instanceof ChoiceRemotePlayerActivity) {
                                ChoiceRemotePlayerActivity.g_handler.sendEmptyMessage(0);
                            }
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }.run();
        }

        public void deviceRemoved(final Device device) {
            new Runnable() { // from class: com.netfeige.display.data.IpmsgApplication.BrowseRegistryListener.2
                @Override // java.lang.Runnable
                public void run() {
                    IpmsgApplication.g_arrayLDevices.remove(device);
                    if (IpmsgApplication.this.currentActivity instanceof ChoiceRemotePlayerActivity) {
                        ChoiceRemotePlayerActivity.g_handler.sendEmptyMessage(0);
                    }
                }
            }.run();
        }
    }

    public AndroidUpnpService getAndroidUpnpService() {
        return this.m_upnpService;
    }

    public ControlPoint getControlPoint() {
        return this.m_controlPoint;
    }

    public void updateDevInfo(String str, String str2) {
        this.m_deviceInfo.dev_name = str;
        this.m_deviceInfo.uuid = str2;
    }

    public void setDevStatus(boolean z) {
        this.m_deviceInfo.status = z;
        DeviceUpdateBrocastFactory.sendDevUpdateBrocast(this);
    }

    public DeviceInfo getDevInfo() {
        return this.m_deviceInfo;
    }
}

