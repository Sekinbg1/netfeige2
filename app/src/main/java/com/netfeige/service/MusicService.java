package com.netfeige.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;
import com.netfeige.common.Music;
import com.netfeige.common.Public_Tools;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.display.ui.PlayMusicActivity;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public class MusicService extends Service implements Runnable {
	public static MediaPlayer s_mediaPlayer = null;
	public static int s_nCurrentListId = 0;
	public static String s_nCurrentPlayingPath = "";
	private SeekBarBroadcastReceiver m_receiver;
	private StopBroadcastReceiver m_stopReceiver;
	private Thread m_threadProgress = null;
	public static Boolean s_bIsRun = true;
	public static Boolean s_bPlaying = false;
	public static boolean s_bIsPause = false;
	private static MusicService m_musicService = null;

	@Override // android.app.Service
	public IBinder onBind(Intent intent) {
		return null;
	}

	public static synchronized MusicService getMusicService() {
		return m_musicService;
	}

	@Override // android.app.Service
	public void onCreate() {
		m_musicService = this;
		this.m_receiver = new SeekBarBroadcastReceiver();
		registerReceiver(this.m_receiver, new IntentFilter("cn.com.seekBar"));
		this.m_stopReceiver = new StopBroadcastReceiver();
		registerReceiver(this.m_stopReceiver, new IntentFilter("cn.com.feige"));
		Thread thread = new Thread(this);
		this.m_threadProgress = thread;
		thread.start();
		super.onCreate();
	}

	@Override // android.app.Service
	public void onStart(Intent intent, int i) {
		try {
			String stringExtra = intent.getStringExtra("play");
			s_nCurrentListId = intent.getIntExtra("id", 0);
			if (stringExtra.equals("play")) {
				if (s_mediaPlayer != null) {
					s_mediaPlayer.release();
					s_mediaPlayer = null;
				}
				playMusic(s_nCurrentListId);
				return;
			}
			if (stringExtra.equals("pause")) {
				if (s_mediaPlayer != null) {
					s_mediaPlayer.pause();
					s_bIsPause = true;
					return;
				}
				return;
			}
			if (stringExtra.equals("playing")) {
				if (s_mediaPlayer != null) {
					if (s_bIsPause) {
						s_mediaPlayer.start();
						s_bIsPause = false;
						return;
					} else {
						s_mediaPlayer.pause();
						s_bIsPause = true;
						return;
					}
				}
				playMusic(s_nCurrentListId);
				return;
			}
			if (stringExtra.equals("replaying")) {
				if (s_mediaPlayer != null) {
					if (!s_bIsPause) {
						s_mediaPlayer.seekTo(s_mediaPlayer.getCurrentPosition());
						s_mediaPlayer.start();
						return;
					} else {
						s_mediaPlayer.seekTo(s_mediaPlayer.getCurrentPosition());
						s_mediaPlayer.pause();
						return;
					}
				}
				playMusic(s_nCurrentListId);
				return;
			}
			if (stringExtra.equals("next")) {
				playMusic(s_nCurrentListId);
			} else if (stringExtra.equals("forward")) {
				playMusic(s_nCurrentListId);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	/* JADX INFO: Access modifiers changed from: private */
	public void playMusic(int i) {
		Music music;
		String path;
		MediaPlayer mediaPlayer = s_mediaPlayer;
		if (mediaPlayer != null) {
			mediaPlayer.release();
			s_mediaPlayer = null;
		}
		if (i >= IpmsgApplication.g_arrPlayingList.size() - 1) {
			s_nCurrentListId = IpmsgApplication.g_arrPlayingList.size() - 1;
		} else if (i <= 0) {
			s_nCurrentListId = 0;
		} else {
			s_nCurrentListId = i;
		}
		try {
			music = IpmsgApplication.g_arrPlayingList.get(s_nCurrentListId);
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			music = null;
		} catch (Exception e2) {
			e2.printStackTrace();
			music = null;
		}
		try {
			path = music.getPath();
			s_nCurrentPlayingPath = path;
		} catch (NullPointerException e3) {
			e3.printStackTrace();
			path = null;
		} catch (Exception e4) {
			e4.printStackTrace();
			path = null;
		}
		MediaPlayer mediaPlayer2 = new MediaPlayer();
		s_mediaPlayer = mediaPlayer2;
		mediaPlayer2.reset();
		try {
			s_mediaPlayer.setDataSource(path);
			s_mediaPlayer.prepare();
		} catch (IOException e7) {
			e7.printStackTrace();
		} catch (IllegalArgumentException e8) {
			e8.printStackTrace();
		} catch (IllegalStateException e9) {
			e9.printStackTrace();
		} catch (SecurityException e10) {
			e10.printStackTrace();
		}
		s_mediaPlayer.start();
		s_bIsPause = false;
		s_mediaPlayer.setOnCompletionListener(new MediaPlayerOnCompletionListener());
		s_mediaPlayer.setOnErrorListener(new MediaPlayerOnErrorListener());
		sendPlayedBroadcast();
	}

	private class MediaPlayerOnErrorListener implements MediaPlayer.OnErrorListener {
		private MediaPlayerOnErrorListener() {
		}

		@Override // android.media.MediaPlayer.OnErrorListener
		public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
			String path = null;
			if (MusicService.s_mediaPlayer != null) {
				MusicService.s_mediaPlayer.release();
				MusicService.s_mediaPlayer = null;
			}
			try {
				path = IpmsgApplication.g_arrPlayingList.get(MusicService.s_nCurrentListId).getPath();
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			MusicService.s_mediaPlayer = new MediaPlayer();
			MusicService.s_mediaPlayer.reset();
			try {
				MusicService.s_mediaPlayer.setDataSource(path);
				MusicService.s_mediaPlayer.prepare();
			} catch (IOException e3) {
				e3.printStackTrace();
			} catch (IllegalArgumentException e4) {
				e4.printStackTrace();
			} catch (IllegalStateException e5) {
				e5.printStackTrace();
			} catch (SecurityException e6) {
				e6.printStackTrace();
			}
			MusicService.s_mediaPlayer.start();
			MusicService.this.sendPlayedBroadcast();
			return false;
		}
	}

	private class MediaPlayerOnCompletionListener implements MediaPlayer.OnCompletionListener {
		private MediaPlayerOnCompletionListener() {
		}

		@Override // android.media.MediaPlayer.OnCompletionListener
		public void onCompletion(MediaPlayer mediaPlayer) {
			if (IpmsgApplication.g_arrPlayingList.size() > 0) {
				if (!IpmsgApplication.g_bIsDestroy) {
					if (PlayMusicActivity.s_bIsLoop.booleanValue()) {
						MusicService.this.sendBroadcast(new Intent("cn.com.completion"));
						return;
					} else {
						MusicService.s_mediaPlayer.reset();
						MusicService.this.playMusic(MusicService.s_nCurrentListId);
						return;
					}
				}
				if (PlayMusicActivity.s_bIsLoop.booleanValue()) {
					MusicService.this.playNext();
					return;
				} else {
					MusicService.s_mediaPlayer.reset();
					MusicService.this.playMusic(MusicService.s_nCurrentListId);
					return;
				}
			}
			MusicService.s_mediaPlayer.reset();
			MusicService.s_mediaPlayer.release();
		}
	}

	private class SeekBarBroadcastReceiver extends BroadcastReceiver {
		private SeekBarBroadcastReceiver() {
		}

		@Override // android.content.BroadcastReceiver
		public void onReceive(Context context, Intent intent) {
			try {
				MusicService.s_mediaPlayer.seekTo((intent.getIntExtra("seekBarPosition", 0) * MusicService.s_mediaPlayer.getDuration()) / 100);
				MusicService.s_mediaPlayer.start();
			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	private class StopBroadcastReceiver extends BroadcastReceiver {
		private StopBroadcastReceiver() {
		}

		@Override // android.content.BroadcastReceiver
		public void onReceive(Context context, Intent intent) {
			if (intent.getIntExtra("stop", 0) == 1) {
				MusicService.this.stopSelf();
			}
		}
	}

	@Override // android.app.Service
	public void onDestroy() {
		unregisterReceiver(this.m_receiver);
		unregisterReceiver(this.m_stopReceiver);
		IpmsgApplication.g_arrPlayingList.clear();
		s_bIsRun = false;
		stopSelf();
		super.onDestroy();
	}

	@Override // java.lang.Runnable
	public void run() {
		while (s_bIsRun.booleanValue()) {
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			MediaPlayer mediaPlayer = s_mediaPlayer;
			if (mediaPlayer != null) {
				try {
					int currentPosition = mediaPlayer.getCurrentPosition();
					int duration = s_mediaPlayer.getDuration();
					Intent intent = new Intent("cn.com.progress");
					intent.putExtra("position", currentPosition);
					intent.putExtra("total", duration);
					sendBroadcast(intent);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			try {
				if (s_mediaPlayer != null) {
					if (s_mediaPlayer.isPlaying()) {
						s_bPlaying = true;
					} else {
						s_bPlaying = false;
					}
				}
			} catch (IllegalStateException e3) {
				e3.printStackTrace();
			} catch (Exception e4) {
				e4.printStackTrace();
			}
		}
	}

	/* JADX INFO: Access modifiers changed from: private */
	public void playNext() {
		int size = IpmsgApplication.g_arrPlayingList.size();
		int i = s_nCurrentListId;
		if (i == size - 1) {
			s_nCurrentListId = 0;
		} else {
			s_nCurrentListId = i + 1;
		}
		try {
			Music music = IpmsgApplication.g_arrPlayingList.get(s_nCurrentListId);
			if (music.getTime() != -1) {
				if (music.getTime() != 0) {
					playMusic(s_nCurrentListId);
					return;
				} else {
					playNext();
					return;
				}
			}
			long duration = Public_Tools.getDuration(music.getPath());
			IpmsgApplication.g_arrPlayingList.get(s_nCurrentListId).setTime(duration);
			if (duration != 0) {
				playMusic(s_nCurrentListId);
			} else {
				playNext();
			}
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	/* JADX INFO: Access modifiers changed from: private */
	public void sendPlayedBroadcast() {
		sendBroadcast(new Intent("cn.com.played"));
	}
}
