package com.netfeige.display.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.netfeige.R;
import com.netfeige.common.Global;
import com.netfeige.common.Public_Tools;
import com.netfeige.common.SDCardInfo;
import com.netfeige.common.StorageDevice;
import com.netfeige.display.data.StorageDeviceAdapter;
import com.netfeige.display.ui.dialog.CopyProgressDialog;
import com.netfeige.enums.FeigeRemoveState;
import com.netfeige.enums.FileAccessAuth;
import com.netfeige.filemanager.FileManager;
import com.netfeige.kits.DataConfig;
import com.netfeige.kits.IDataConfig;
import com.netfeige.util.SDCardUtil;
import java.io.File;
import java.util.ArrayList;
import org.teleal.cling.model.ServiceReference;

/* JADX INFO: loaded from: classes.dex */
public class SetFeigeDownloadActivity extends Activity {
	public static boolean m_bBackGroundPaste = false;
	private static final int m_nUpdateFeigeSpace = 0;
	private static final int m_nUpdateMemorySpace = 3;
	private static final int m_nUpdateSD2Space = 2;
	private static final int m_nUpdateSDSpace = 1;
	private static final String m_strInMemory = "0";
	private static final String m_strInSD = "1";
	private static final String m_strInSD2 = "2";
	public static String m_strNewFeigePath = null;
	private static final String m_strNoFeigeDownload = "-1";
	private Button m_btnBack;
	private CheckBox m_checkBMove;
	private Context m_context;
	private CopyProgressDialog m_copyProgressDialog;
	private StorageDeviceAdapter m_deviceAdapter;
	private Dialog m_dialogLackOfSpace;
	private long m_lCurrentSpace;
	private long m_lFeigeSpace;
	private long m_lMemorySpace;
	private long m_lSD2Space;
	private long m_lSDSpace;
	private ListView m_listVDevice;
	private RadioButton m_radioBtnMemory;
	private RadioButton m_radioBtnSD;
	private RadioButton m_radioBtnSD2;
	private RadioGroup m_radioGroup;
	private FileManager.FeigeDirRemoveRunnableImp m_removeRunnableImp;
	private String m_strFeigeDownload;
	private String m_strFeigeSize;
	private String m_strNewFeigeDownload;
	private String m_strOldFeigeDownload;
	private TextView m_textVFeigeSize;
	private TextView m_textVMemory;
	private TextView m_textVSD;
	private TextView m_textVSD2;
	private TextView m_textVSD2Size;
	private TextView m_textVSDSize;
	private final int m_nCopyProgressDialogId = 0;
	private final int m_nMoveDialogId = 1;
	private final String m_strSDCard2 = "/mnt/sdcard2";
	private String m_strSDContent = "MB可用";
	private String m_strSD2Content = "MB可用";
	private String m_strMemoryContent = "MB可用";
	private String m_strFeigeLackSpace = ",椋為附鏀朵欢澶筂B";
	private IDataConfig m_iDataConfig = null;
	private boolean m_bIsSdcard = false;
	private boolean m_bIsSdcard2 = false;
	private boolean m_bIsChecked = false;
	private final String m_strFeige = "/FeigeDownload";
	private final String m_strSD = "/mnt/sdcard";
	private final String m_strSD2 = "/mnt/sdcard2";
	private final String m_strFeigeText = ",飞鸽收件夹";
	private final String m_strText = "可用";
	private boolean m_bIsClickCancel = false;
	private boolean m_bPasteCompleted = false;
	private MoveDialog m_moveDialog = null;
	private Handler handlerLackOfSpace = new Handler() { // from class: com.netfeige.display.ui.SetFeigeDownloadActivity.3
		@Override // android.os.Handler
		public void handleMessage(Message message) {
			if (message.what == 0) {
				SetFeigeDownloadActivity.this.m_textVFeigeSize.setText(SetFeigeDownloadActivity.this.m_strFeigeSize);
			}
			super.handleMessage(message);
		}
	};
	private Handler handlerGrogress = new Handler() { // from class: com.netfeige.display.ui.SetFeigeDownloadActivity.5
		@Override // android.os.Handler
		public void handleMessage(Message message) {
			int i = message.what;
			if (i == 0 && SetFeigeDownloadActivity.this.m_lFeigeSpace != 0) {
				try {
					int i2 = (int) ((SetFeigeDownloadActivity.this.m_removeRunnableImp.getlRemovedSize() * 100) / SetFeigeDownloadActivity.this.m_lFeigeSpace);
					if (!SetFeigeDownloadActivity.m_bBackGroundPaste) {
						String shortSize = Public_Tools.getShortSize(SetFeigeDownloadActivity.this.m_removeRunnableImp.getlRemovedSize());
						CopyProgressDialog.m_textVProgress.setText(shortSize + ServiceReference.DELIMITER + SetFeigeDownloadActivity.this.m_strFeigeSize);
					}
					CopyProgressDialog.m_progressBCopy.setProgress(i2);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (i == 1) {
				SetFeigeDownloadActivity.this.m_copyProgressDialog.dismiss();
				try {
					SetFeigeDownloadActivity.this.updateViewDisplay();
					Toast.makeText(SetFeigeDownloadActivity.this.m_context, R.string.movesuccess, 0).show();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			} else if (i == 3) {
				SetFeigeDownloadActivity.this.m_deviceAdapter.notifyDataSetChanged();
			}
			super.handleMessage(message);
		}
	};

	@Override // android.app.Activity
	protected void onCreate(Bundle bundle) {
		setContentView(R.layout.setloadpath);
		this.m_context = this;
		FileManager.FeigeDirRemoveRunnableImp removeRunnableImp = ((FileManager) FileManager.getIFileManager(getApplicationContext())).getRemoveRunnableImp();
		this.m_removeRunnableImp = removeRunnableImp;
		removeRunnableImp.setHandlerGrogress(this.handlerGrogress);
		initControl();
		initBasic();
		super.onCreate(bundle);
	}

	@Override // android.app.Activity
	public void onStart() {
		super.onStart();
	}

	@Override // android.app.Activity
	protected void onResume() {
		super.onResume();
// Umeng removed:         // Umeng removed: MobclickAgent.onResume(this);
	}

	@Override // android.app.Activity
	protected void onPause() {
		super.onPause();
// Umeng removed:         // Umeng removed: MobclickAgent.onPause(this);
	}

	@Override // android.app.Activity
	public void onDestroy() {
		this.m_removeRunnableImp.setHandlerGrogress(null);
		super.onDestroy();
	}

	private void initBasic() {
		this.m_iDataConfig = DataConfig.getInstance(getApplicationContext());
		CopyProgressDialog copyProgressDialog = new CopyProgressDialog(this.m_context);
		this.m_copyProgressDialog = copyProgressDialog;
		copyProgressDialog.setOnCopyProgressClickListener(new CopyProgressDialog.OnCopyProgressClickListener() { // from class: com.netfeige.display.ui.SetFeigeDownloadActivity.1
			@Override // com.netfeige.display.ui.dialog.CopyProgressDialog.OnCopyProgressClickListener
			public void onCopyProgressClick(View view) {
				if (view.getId() != R.id.btn_backgroundcopy) {
					SetFeigeDownloadActivity.this.m_removeRunnableImp.interrupt();
				} else {
					SetFeigeDownloadActivity.m_bBackGroundPaste = true;
				}
			}
		});
		this.m_moveDialog = new MoveDialog(this.m_context);
		initControlData();
		startThread();
	}

	/* JADX INFO: Access modifiers changed from: private */
	public void initControlData() {
		try {
			this.m_strOldFeigeDownload = Public_Tools.getDefaultDownloadPath();
		} catch (Throwable e) {
			e.printStackTrace();
			this.m_strOldFeigeDownload = "";
		}
		ArrayList<SDCardInfo> sDCardInfo = SDCardUtil.getSDCardInfo(this.m_context);
		ArrayList arrayList = new ArrayList();
		for (SDCardInfo sDCardInfo2 : sDCardInfo) {
			if (Public_Tools.authority(new File(sDCardInfo2.getMountPoint())) == FileAccessAuth.RW_OK) {
				StorageDevice storageDevice = new StorageDevice();
				storageDevice.setStrDeviceName(new File(sDCardInfo2.getMountPoint()).getName());
				storageDevice.setStrMountPoint(sDCardInfo2.getMountPoint());
				if (this.m_strOldFeigeDownload.startsWith(sDCardInfo2.getMountPoint())) {
					storageDevice.setIsChecked(true);
				}
				arrayList.add(storageDevice);
			}
		}
		StorageDevice storageDevice2 = new StorageDevice();
		storageDevice2.setStrDeviceName(getResources().getString(R.string.filer_memory));
		storageDevice2.setStrMountPoint(this.m_context.getFilesDir().getPath());
		if (this.m_strOldFeigeDownload.startsWith(storageDevice2.getStrMountPoint())) {
			storageDevice2.setIsChecked(true);
		}
		arrayList.add(storageDevice2);
		StorageDeviceAdapter storageDeviceAdapter = new StorageDeviceAdapter(this.m_context, R.layout.storagedeviceitem, arrayList);
		this.m_deviceAdapter = storageDeviceAdapter;
		this.m_listVDevice.setAdapter((ListAdapter) storageDeviceAdapter);
	}

	/* JADX WARN: Type inference failed for: r0v0, types: [com.netfeige.display.ui.SetFeigeDownloadActivity$2] */
	private void startThread() {
		new Thread() { // from class: com.netfeige.display.ui.SetFeigeDownloadActivity.2
			@Override // java.lang.Thread, java.lang.Runnable
			public void run() {
				try {
					try {
						SetFeigeDownloadActivity.this.m_lFeigeSpace = FileManager.getFolderSize(new File(SetFeigeDownloadActivity.this.m_strOldFeigeDownload));
						SetFeigeDownloadActivity.this.m_strFeigeSize = Public_Tools.getShortSize(SetFeigeDownloadActivity.this.m_lFeigeSpace);
						SetFeigeDownloadActivity.this.handlerLackOfSpace.sendEmptyMessage(0);
					} catch (Exception unused) {
						SetFeigeDownloadActivity.this.initControlData();
						SetFeigeDownloadActivity.this.m_lFeigeSpace = FileManager.getFolderSize(new File(SetFeigeDownloadActivity.this.m_strOldFeigeDownload));
						SetFeigeDownloadActivity.this.m_strFeigeSize = Public_Tools.getShortSize(SetFeigeDownloadActivity.this.m_lFeigeSpace);
						SetFeigeDownloadActivity.this.handlerLackOfSpace.sendEmptyMessage(0);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	private void initControl() {
		Button button = (Button) findViewById(R.id.back_btn);
		this.m_btnBack = button;
		button.setOnClickListener(new BackClickListener());
		this.m_textVFeigeSize = (TextView) findViewById(R.id.size_textv_activity_feige_dir);
		ListView listView = (ListView) findViewById(R.id.device_list_activity_feige_dir);
		this.m_listVDevice = listView;
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.netfeige.display.ui.SetFeigeDownloadActivity.4
			@Override // android.widget.AdapterView.OnItemClickListener
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
				int i2 = AnonymousClass6.$SwitchMap$com$netfeige$enums$FeigeRemoveState[SetFeigeDownloadActivity.this.m_removeRunnableImp.getRunState().ordinal()];
				if (i2 == 1) {
					SetFeigeDownloadActivity.m_bBackGroundPaste = false;
					SetFeigeDownloadActivity.this.m_copyProgressDialog.show();
					return;
				}
				if (i2 == 2) {
					Public_Tools.showToast(SetFeigeDownloadActivity.this.m_context, SetFeigeDownloadActivity.this.getResources().getString(R.string.old_feige_deleting), 1);
					return;
				}
				if (i2 != 3) {
					FileManager.g_storageDevice = SetFeigeDownloadActivity.this.m_deviceAdapter.getItem(i);
					File file = new File(FileManager.g_storageDevice.getStrMountPoint());
					if (FileManager.g_storageDevice.isChecked()) {
						return;
					}
					if (!file.exists() || !file.isDirectory() || file.listFiles() == null) {
						Public_Tools.showToast(SetFeigeDownloadActivity.this.m_context, SetFeigeDownloadActivity.this.getResources().getString(R.string.storage_device_unusable), 1);
						SetFeigeDownloadActivity.this.initControlData();
						return;
					} else {
						SetFeigeDownloadActivity.this.showDialog(1);
						SetFeigeDownloadActivity.this.m_lCurrentSpace = FileManager.g_storageDevice.getlUsableSpace();
						return;
					}
				}
				Public_Tools.showToast(SetFeigeDownloadActivity.this.m_context, SetFeigeDownloadActivity.this.getResources().getString(R.string.new_feige_deleting), 1);
			}
		});
	}

	/* JADX INFO: renamed from: com.netfeige.display.ui.SetFeigeDownloadActivity$6, reason: invalid class name */
	static /* synthetic */ class AnonymousClass6 {
		static final /* synthetic */ int[] $SwitchMap$com$netfeige$enums$FeigeRemoveState;

		static {
			int[] iArr = new int[FeigeRemoveState.values().length];
			$SwitchMap$com$netfeige$enums$FeigeRemoveState = iArr;
			try {
				iArr[FeigeRemoveState.REMOVING.ordinal()] = 1;
			} catch (NoSuchFieldError unused) {
			}
			try {
				$SwitchMap$com$netfeige$enums$FeigeRemoveState[FeigeRemoveState.OLDFEIGE_DELETING.ordinal()] = 2;
			} catch (NoSuchFieldError unused2) {
			}
			try {
				$SwitchMap$com$netfeige$enums$FeigeRemoveState[FeigeRemoveState.NEWFEIGE_DELETING.ordinal()] = 3;
			} catch (NoSuchFieldError unused3) {
			}
			try {
				$SwitchMap$com$netfeige$enums$FeigeRemoveState[FeigeRemoveState.STOPED.ordinal()] = 4;
			} catch (NoSuchFieldError unused4) {
			}
		}
	}

	private class BackClickListener implements View.OnClickListener {
		private BackClickListener() {
		}

		@Override // android.view.View.OnClickListener
		public void onClick(View view) {
			SetFeigeDownloadActivity.this.onBackPressed();
		}
	}

	@Override // android.app.Activity
	protected Dialog onCreateDialog(int i) {
		if (i == 0) {
			return this.m_copyProgressDialog;
		}
		if (i != 1) {
			return null;
		}
		return this.m_moveDialog;
	}

	@Override // android.app.Activity
	protected void onPrepareDialog(int i, Dialog dialog) {
		if (i == 0) {
			CopyProgressDialog.m_progressBCopy.setProgress(0);
			CopyProgressDialog.m_textVProgress.setText(this.m_strFeigeSize);
		} else if (i == 1) {
			this.m_checkBMove.setChecked(false);
			this.m_bIsChecked = false;
		}
		super.onPrepareDialog(i, dialog);
	}

	private class DialogInterfaceCancelClickListener implements DialogInterface.OnClickListener {
		private DialogInterfaceCancelClickListener() {
		}

		@Override // android.content.DialogInterface.OnClickListener
		public void onClick(DialogInterface dialogInterface, int i) {
			dialogInterface.cancel();
		}
	}

	/* JADX INFO: Access modifiers changed from: private */
	public void showDialogLackOfSpace() {
		AlertDialog alertDialogCreate = new AlertDialog.Builder(this).setTitle(R.string.notify).setIcon(android.R.drawable.ic_dialog_info).setMessage(R.string.lackofspace).setNegativeButton(R.string.confirm, new DialogInterfaceCancelClickListener()).create();
		this.m_dialogLackOfSpace = alertDialogCreate;
		alertDialogCreate.setCanceledOnTouchOutside(false);
		this.m_dialogLackOfSpace.show();
	}

	/* JADX INFO: Access modifiers changed from: private */
	public void updateViewDisplay() {
		initControlData();
		startThread();
	}

	private class MoveDialog extends Dialog {
		private Button m_btnBackground;
		private Button m_btnConcel;
		private View m_viewParent;

		public MoveDialog(Context context) {
			super(context, R.style.sort_dialog);
			View viewInflate = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.movedialog, (ViewGroup) null);
			this.m_viewParent = viewInflate;
			setContentView(viewInflate);
			setCancelable(false);
			setCanceledOnTouchOutside(false);
			getViewById();
		}

		private void getViewById() {
			SetFeigeDownloadActivity.this.m_checkBMove = (CheckBox) findViewById(R.id.checkbox_movefegei);
			SetFeigeDownloadActivity.this.m_checkBMove.setOnCheckedChangeListener(new CheckBoxChangedListener());
			Button button = (Button) findViewById(R.id.btn_backgroundcopy);
			this.m_btnBackground = button;
			button.setOnClickListener(new ConfirmClickListener());
			Button button2 = (Button) findViewById(R.id.btn_copycancel);
			this.m_btnConcel = button2;
			button2.setOnClickListener(new CancelClickListener());
		}
	}

	private class CheckBoxChangedListener implements CompoundButton.OnCheckedChangeListener {
		private CheckBoxChangedListener() {
		}

		@Override // android.widget.CompoundButton.OnCheckedChangeListener
		public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
			if (z) {
				SetFeigeDownloadActivity.this.m_bIsChecked = true;
			} else {
				SetFeigeDownloadActivity.this.m_bIsChecked = false;
			}
		}
	}

	private class ConfirmClickListener implements View.OnClickListener {
		private ConfirmClickListener() {
		}

		@Override // android.view.View.OnClickListener
		public void onClick(View view) {
			SetFeigeDownloadActivity.this.m_moveDialog.dismiss();
			if (!SetFeigeDownloadActivity.this.m_bIsChecked) {
				new File(FileManager.g_storageDevice.getStrMountPoint() + "/FeigeDownload").mkdirs();
				SetFeigeDownloadActivity.this.m_iDataConfig.Write(35, FileManager.g_storageDevice.getStrMountPoint());
				Global.g_bFeigeDownloadChanged = true;
				SetFeigeDownloadActivity.this.updateViewDisplay();
				return;
			}
			if (SetFeigeDownloadActivity.this.m_lFeigeSpace >= SetFeigeDownloadActivity.this.m_lCurrentSpace) {
				SetFeigeDownloadActivity.this.showDialogLackOfSpace();
				return;
			}
			SetFeigeDownloadActivity.this.showDialog(0);
			SetFeigeDownloadActivity.m_bBackGroundPaste = false;
			SetFeigeDownloadActivity.this.m_removeRunnableImp.setStrOldFeigePath(SetFeigeDownloadActivity.this.m_strOldFeigeDownload);
			SetFeigeDownloadActivity.this.m_removeRunnableImp.setStrNewFeigePath(FileManager.g_storageDevice.getStrMountPoint() + "/FeigeDownload");
			SetFeigeDownloadActivity.this.m_removeRunnableImp.start();
		}
	}

	private class CancelClickListener implements View.OnClickListener {
		private CancelClickListener() {
		}

		@Override // android.view.View.OnClickListener
		public void onClick(View view) {
			SetFeigeDownloadActivity.this.m_moveDialog.dismiss();
		}
	}
}
