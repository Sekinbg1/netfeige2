package com.netfeige.display.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.netfeige.R;
import com.netfeige.common.Public_Def;
import java.util.Date;
import org.teleal.cling.support.model.ProtocolInfo;

/* JADX INFO: loaded from: classes.dex */
public class LanShareListView extends ListView implements AbsListView.OnScrollListener {
    private static final int RATIO = 3;
    private RotateAnimation m_animation;
    private boolean m_bIsBack;
    private boolean m_bIsRecored;
    private boolean m_bIsRefreshable;
    private LinearLayout m_headerView;
    private int m_iHeaderContentHeight;
    private int m_iStartY;
    private LayoutInflater m_inflater;
    private ImageView m_lvHeaderArrowIv;
    private TextView m_lvHeaderLastUpdatedTv;
    private ProgressBar m_lvHeaderProgressBar;
    private TextView m_lvHeaderTipsTv;
    private OnRefreshListener m_refreshListener;
    private RotateAnimation m_reverseAnimation;
    private Public_Def.ListViewRefreshStatus m_state;

    public interface OnRefreshListener {
        void onRefresh();
    }

    @Override // android.widget.AbsListView.OnScrollListener
    public void onScrollStateChanged(AbsListView absListView, int i) {
    }

    public LanShareListView(Context context) {
        super(context);
        init(context);
    }

    public LanShareListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public LanShareListView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater layoutInflaterFrom = LayoutInflater.from(context);
        this.m_inflater = layoutInflaterFrom;
        LinearLayout linearLayout = (LinearLayout) layoutInflaterFrom.inflate(R.layout.lv_header, (ViewGroup) null);
        this.m_headerView = linearLayout;
        this.m_lvHeaderTipsTv = (TextView) linearLayout.findViewById(R.id.tips_tv_lv_header);
        this.m_lvHeaderLastUpdatedTv = (TextView) this.m_headerView.findViewById(R.id.lastupdated_tv_lv_header);
        this.m_lvHeaderArrowIv = (ImageView) this.m_headerView.findViewById(R.id.arrow_iv_lv_header);
        this.m_lvHeaderProgressBar = (ProgressBar) this.m_headerView.findViewById(R.id.progressbar_lv_header);
        measureView(this.m_headerView);
        int measuredHeight = this.m_headerView.getMeasuredHeight();
        this.m_iHeaderContentHeight = measuredHeight;
        this.m_headerView.setPadding(0, measuredHeight * (-1), 0, 0);
        this.m_headerView.invalidate();
        addHeaderView(this.m_headerView, null, false);
        setOnScrollListener(this);
        RotateAnimation rotateAnimation = new RotateAnimation(0.0f, -180.0f, 1, 0.5f, 1, 0.5f);
        this.m_animation = rotateAnimation;
        rotateAnimation.setInterpolator(new LinearInterpolator());
        this.m_animation.setDuration(250L);
        this.m_animation.setFillAfter(true);
        RotateAnimation rotateAnimation2 = new RotateAnimation(-180.0f, 0.0f, 1, 0.5f, 1, 0.5f);
        this.m_reverseAnimation = rotateAnimation2;
        rotateAnimation2.setInterpolator(new LinearInterpolator());
        this.m_reverseAnimation.setDuration(200L);
        this.m_reverseAnimation.setFillAfter(true);
        this.m_state = Public_Def.ListViewRefreshStatus.DONE;
        this.m_bIsRefreshable = false;
    }

    @Override // android.widget.AbsListView.OnScrollListener
    public void onScroll(AbsListView absListView, int i, int i2, int i3) {
        if (i == 0) {
            this.m_bIsRefreshable = true;
        } else {
            this.m_bIsRefreshable = false;
        }
    }

    @Override // android.widget.AbsListView, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.m_bIsRefreshable) {
            int action = motionEvent.getAction();
            if (action != 0) {
                if (action == 1) {
                    if (this.m_state != Public_Def.ListViewRefreshStatus.REFRESHING && this.m_state != Public_Def.ListViewRefreshStatus.LOADING) {
                        if (this.m_state == Public_Def.ListViewRefreshStatus.PULL_TO_REFRESH) {
                            this.m_state = Public_Def.ListViewRefreshStatus.DONE;
                            changeHeaderViewByState();
                        }
                        if (this.m_state == Public_Def.ListViewRefreshStatus.RELEASE_TO_REFRESH) {
                            this.m_state = Public_Def.ListViewRefreshStatus.REFRESHING;
                            changeHeaderViewByState();
                            onLvRefresh();
                        }
                    }
                    this.m_bIsRecored = false;
                    this.m_bIsBack = false;
                } else if (action == 2) {
                    int y = (int) motionEvent.getY();
                    if (!this.m_bIsRecored) {
                        this.m_bIsRecored = true;
                        this.m_iStartY = y;
                    }
                    if (this.m_state != Public_Def.ListViewRefreshStatus.REFRESHING && this.m_bIsRecored && this.m_state != Public_Def.ListViewRefreshStatus.LOADING) {
                        if (this.m_state == Public_Def.ListViewRefreshStatus.RELEASE_TO_REFRESH) {
                            setSelection(0);
                            int i = this.m_iStartY;
                            if ((y - i) / 3 < this.m_iHeaderContentHeight && y - i > 0) {
                                this.m_state = Public_Def.ListViewRefreshStatus.PULL_TO_REFRESH;
                                changeHeaderViewByState();
                            } else if (y - this.m_iStartY <= 0) {
                                this.m_state = Public_Def.ListViewRefreshStatus.DONE;
                                changeHeaderViewByState();
                            }
                        }
                        if (this.m_state == Public_Def.ListViewRefreshStatus.PULL_TO_REFRESH) {
                            setSelection(0);
                            int i2 = this.m_iStartY;
                            if ((y - i2) / 3 >= this.m_iHeaderContentHeight) {
                                this.m_state = Public_Def.ListViewRefreshStatus.RELEASE_TO_REFRESH;
                                this.m_bIsBack = true;
                                changeHeaderViewByState();
                            } else if (y - i2 <= 0) {
                                this.m_state = Public_Def.ListViewRefreshStatus.DONE;
                                changeHeaderViewByState();
                            }
                        }
                        if (this.m_state == Public_Def.ListViewRefreshStatus.DONE && y - this.m_iStartY > 0) {
                            this.m_state = Public_Def.ListViewRefreshStatus.PULL_TO_REFRESH;
                            changeHeaderViewByState();
                        }
                        if (this.m_state == Public_Def.ListViewRefreshStatus.PULL_TO_REFRESH) {
                            this.m_headerView.setPadding(0, (this.m_iHeaderContentHeight * (-1)) + ((y - this.m_iStartY) / 3), 0, 0);
                        }
                        if (this.m_state == Public_Def.ListViewRefreshStatus.RELEASE_TO_REFRESH) {
                            this.m_headerView.setPadding(0, ((y - this.m_iStartY) / 3) - this.m_iHeaderContentHeight, 0, 0);
                        }
                    }
                }
            } else if (!this.m_bIsRecored) {
                this.m_bIsRecored = true;
                this.m_iStartY = (int) motionEvent.getY();
            }
        }
        return super.onTouchEvent(motionEvent);
    }

    /* JADX INFO: renamed from: com.netfeige.display.ui.LanShareListView$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$netfeige$common$Public_Def$ListViewRefreshStatus;

        static {
            int[] iArr = new int[Public_Def.ListViewRefreshStatus.values().length];
            $SwitchMap$com$netfeige$common$Public_Def$ListViewRefreshStatus = iArr;
            try {
                iArr[Public_Def.ListViewRefreshStatus.RELEASE_TO_REFRESH.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$netfeige$common$Public_Def$ListViewRefreshStatus[Public_Def.ListViewRefreshStatus.PULL_TO_REFRESH.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$netfeige$common$Public_Def$ListViewRefreshStatus[Public_Def.ListViewRefreshStatus.REFRESHING.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$netfeige$common$Public_Def$ListViewRefreshStatus[Public_Def.ListViewRefreshStatus.DONE.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    private void changeHeaderViewByState() {
        int i = AnonymousClass1.$SwitchMap$com$netfeige$common$Public_Def$ListViewRefreshStatus[this.m_state.ordinal()];
        if (i == 1) {
            this.m_lvHeaderArrowIv.setVisibility(0);
            this.m_lvHeaderProgressBar.setVisibility(8);
            this.m_lvHeaderTipsTv.setVisibility(0);
            this.m_lvHeaderLastUpdatedTv.setVisibility(0);
            this.m_lvHeaderArrowIv.clearAnimation();
            this.m_lvHeaderArrowIv.startAnimation(this.m_animation);
            this.m_lvHeaderTipsTv.setText("鏉惧紑鍒锋柊");
            return;
        }
        if (i == 2) {
            this.m_lvHeaderProgressBar.setVisibility(8);
            this.m_lvHeaderTipsTv.setVisibility(0);
            this.m_lvHeaderLastUpdatedTv.setVisibility(0);
            this.m_lvHeaderArrowIv.clearAnimation();
            this.m_lvHeaderArrowIv.setVisibility(0);
            if (this.m_bIsBack) {
                this.m_bIsBack = false;
                this.m_lvHeaderArrowIv.clearAnimation();
                this.m_lvHeaderArrowIv.startAnimation(this.m_reverseAnimation);
                this.m_lvHeaderTipsTv.setText("涓嬫媺鍒锋柊");
                return;
            }
            this.m_lvHeaderTipsTv.setText("涓嬫媺鍒锋柊");
            return;
        }
        if (i == 3) {
            this.m_headerView.setPadding(0, 0, 0, 0);
            this.m_lvHeaderProgressBar.setVisibility(0);
            this.m_lvHeaderArrowIv.clearAnimation();
            this.m_lvHeaderArrowIv.setVisibility(8);
            this.m_lvHeaderTipsTv.setText("姝ｅ湪鍒锋柊...");
            this.m_lvHeaderLastUpdatedTv.setVisibility(0);
            return;
        }
        if (i != 4) {
            return;
        }
        this.m_headerView.setPadding(0, this.m_iHeaderContentHeight * (-1), 0, 0);
        this.m_lvHeaderProgressBar.setVisibility(8);
        this.m_lvHeaderArrowIv.clearAnimation();
        this.m_lvHeaderArrowIv.setImageResource(R.drawable.down_arrow);
        this.m_lvHeaderTipsTv.setText("涓嬫媺鍒锋柊");
        this.m_lvHeaderLastUpdatedTv.setVisibility(0);
    }

    private void measureView(View view) {
        int iMakeMeasureSpec;
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(-1, -2);
        }
        int childMeasureSpec = ViewGroup.getChildMeasureSpec(0, 0, layoutParams.width);
        int i = layoutParams.height;
        if (i > 0) {
            iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(i, ProtocolInfo.DLNAFlags.TIME_BASED_SEEK);
        } else {
            iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
        }
        view.measure(childMeasureSpec, iMakeMeasureSpec);
    }

    public void setonRefreshListener(OnRefreshListener onRefreshListener) {
        this.m_refreshListener = onRefreshListener;
        this.m_bIsRefreshable = true;
    }

    public void onRefreshComplete() {
        this.m_state = Public_Def.ListViewRefreshStatus.DONE;
        this.m_lvHeaderLastUpdatedTv.setText("鏈€杩戞洿鏂?" + new Date().toLocaleString());
        changeHeaderViewByState();
    }

    private void onLvRefresh() {
        OnRefreshListener onRefreshListener = this.m_refreshListener;
        if (onRefreshListener != null) {
            onRefreshListener.onRefresh();
        }
    }

    @Override // android.widget.AdapterView
    public void setAdapter(ListAdapter listAdapter) {
        this.m_lvHeaderLastUpdatedTv.setText("鏈€杩戞洿鏂?" + new Date().toLocaleString());
        super.setAdapter(listAdapter);
    }
}

